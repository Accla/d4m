package cloudbase.core.client.mapreduce;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.OutputFormat;
import org.apache.hadoop.mapred.RecordWriter;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.util.Progressable;
import org.apache.log4j.Logger;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;
import cloudbase.core.aggregation.Aggregator;
import cloudbase.core.client.BatchWriter;
import cloudbase.core.client.CBException;
import cloudbase.core.client.CBSecurityException;
import cloudbase.core.client.Connector;
import cloudbase.core.client.Instance;
import cloudbase.core.client.MasterInstance;
import cloudbase.core.client.MultiTableBatchWriter;
import cloudbase.core.client.MutationsRejectedException;
import cloudbase.core.client.TableExistsException;
import cloudbase.core.client.TableNotFoundException;
import cloudbase.core.client.impl.HdfsZooInstance;
import cloudbase.core.data.ColumnUpdate;
import cloudbase.core.data.KeyExtent;
import cloudbase.core.data.Mutation;
import cloudbase.core.security.LabelExpression;

/**
 * This class allows MapReduce jobs to use Cloudbase as the output of data
 * 
 * The user must specify the following via static method calls to this class:
 *  
 *  CBOutputFormat.setUsername(job, username) - sets the username for cloudbase from a string
 *  CBOutputFormat.setPassword(job, password) - sets the password for cloudbase from a byte array
 *  CBOutputFormat.setInstance(job, instance) - sets the location of the cloudbase master, as a string
 *                                              containing a comma separated list of host[:port]
 * 
 * The user may specify these additional options via static method calls:
 * 
 *  CBOutputFormat.setMutationBufferSize(job, numberOfBytes) - an optional batchwriter setting
 *  CBOutputFormat.setMaxLatency(job, numberOfSeconds) - an optional batchwriter setting
 *  CBOutputFormat.setMaxSendThreads(job, numberOfThreads) - an optional batchwriter setting
 *  
 *  CBOutputFormat.enableDebug(job) - enabled debugging output in the RecordWriter (off by default)
 *  CBOutputFormat.enableVerbose(job) - enable verbose output in the RecordWriter (off by default)
 *  CBOutputFormat.enableHdfsZooInstance(job) - enable the use of HdfsZooInstance in lieu of CBOutputFormat.setInstance()
 *                                              This is advanced and requires a lot of configuration to be correct. It
 *                                              is included only for debugging purposes.
 *                                              
 *  CBOutputFormat.enableCreateTables(job) - enable Table creation if they don't exist (off by default)
 *  
 *  CBOutputFormat.setDefaultTableName(job, name) - table used if the map output table is null or empty
 *  
 *  IMPORTANT: Using CBOutputFormat.setDefaultTableName(job, name) requires an understanding of its behavior.
 *             the table specified in CBRecordWriter.write() is used unless it is empty or null
 *             if this is the case, then the default table name is used if it is set
 *             if both are empty or null, then output goes to System.out if verbose is enabled
 *  
 *             Currently nothing happens if neither a default table or a table is specified and verbose
 *             is disabled; I'm not sure that's the best behavior. I think an error should occur, but will have
 *             to revisit this later. TODO
 *             
 *             Setting the default table name allows a user to deliberately omit the table name at each
 *             write operation (collect/emit/output) and still be able to write to a table. This is useful
 *             if the user only plans on writing to one table, or mostly writes to the same table except for
 *             a few cases, where the table name is overridden for a particular write.
 *             
 *  IMPORTANT: You MUST specify the instance of the master with CBOutputFormat.setInstance() or explicitly
 *             specify to use HdfsZooInstance with CBOutputFormat.enableHdfsZooInstance(). There will be NO
 *             automatic failover to HdfsZooInstance.
 * 
 *  Typical Example:
 *   CBOutputFormat.setUsername(job, "webuser");
 *   CBOutputFormat.setPassword(job, "my_very_insecure_password".getBytes());
 *   CBOutputFormat.setInstance(job, "cloud1:9999,cloud2,cloud3:9911");
 * 
 */
public class CBOutputFormat implements OutputFormat<Text, Mutation>
{
    // Required options
    private static final String USERNAME = "cloudbase.output.username";
    private static final String PASSWORD = "cloudbase.output.password";

    public static String getUsername(JobConf job) { return job.get(USERNAME); }
    public static void setUsername(JobConf job, String user) { job.set(USERNAME, user); }

    // WARNING: The password is stored in the JobConf and shared with all MapReduce tasks
    // The BASE64 encoding is not intended to be secure. It's only used for a Charset safe conversion to String
    public static byte[] getPassword(JobConf job) throws IOException { return new BASE64Decoder().decodeBuffer(job.get(PASSWORD)); }
    public static void setPassword(JobConf job, byte[] pwd) { job.set(PASSWORD, new BASE64Encoder().encode(pwd)); }
    
    // BatchWriter options
    // TODO: these static methods should be in the BatchWriter, and exposed here; the defaults should be specified there, not here
    private static final String MAX_MUTATION_BUFFER_SIZE = "cloudbase.batchwriter.maxmemory";
    private static final String MAX_LATENCY = "cloudbase.batchwriter.maxlatency";
    private static final String NUM_WRITE_THREADS = "cloudbase.batchwriter.writethreads";
    private static final long DEFAULT_MAX_MUTATION_BUFFER_SIZE = 50 * 1024 * 1024; // 50 MB
    private static final int DEFAULT_MAX_LATENCY = 60 * 5; // 5 minutes
    private static final int DEFAULT_NUM_WRITE_THREADS = 2;

    public static long getMaxMutationBufferSize(JobConf job) { return job.getLong(MAX_MUTATION_BUFFER_SIZE, DEFAULT_MAX_MUTATION_BUFFER_SIZE); }
    public static void setMaxMutationBufferSize(JobConf job, long numberOfBytes) { job.setLong(MAX_MUTATION_BUFFER_SIZE, numberOfBytes); }

    public static int getMaxLatency(JobConf job) { return job.getInt(MAX_LATENCY, DEFAULT_MAX_LATENCY); }
    public static void setMaxLatency(JobConf job, int numberOfSeconds) { job.setInt(MAX_LATENCY, numberOfSeconds); }

    public static int getMaxWriteThreads(JobConf job) { return job.getInt(NUM_WRITE_THREADS, DEFAULT_NUM_WRITE_THREADS); }
    public static void setMaxWriteThreads(JobConf job, int numberOfThreads) { job.setInt(NUM_WRITE_THREADS, numberOfThreads); }

    // Additional options
    private static final String DEBUG = "cloudbase.output.debug";
    private static final String VERBOSE = "cloudbase.output.verbose";
    private static final String CREATETABLES = "cloudbase.output.createtables";
    private static final String INSTANCE = "cloudbase.output.instance";
    private static final String USE_HDFS_ZOO_INSTANCE = "cloudbase.output.hdfszooinstance";
    private static final String DEFAULT_TABLE_NAME = "cloudbase.output.defaulttable";

    public static void enableDebug(JobConf job) { job.setBoolean(DEBUG, true); }
    public static void disableDebug(JobConf job) { job.setBoolean(DEBUG, false); }
    public static boolean isDebug(JobConf job) { return job.getBoolean(DEBUG, false); }
    
    public static void enableVerbose(JobConf job) { job.setBoolean(VERBOSE, true); }
    public static void disableVerbose(JobConf job) { job.setBoolean(VERBOSE, false); }
    public static boolean isVerbose(JobConf job) { return job.getBoolean(VERBOSE, false); }
    
    public static void enableCreateTables(JobConf job) { job.setBoolean(CREATETABLES, true); }
    public static void disableCreateTables(JobConf job) { job.setBoolean(CREATETABLES, false); }
    public static boolean canCreateTables(JobConf job) { return job.getBoolean(CREATETABLES, false); }

    public static String getInstance(JobConf job) { return job.get(INSTANCE); }
    public static void setInstance(JobConf job, String masters) { job.set(INSTANCE, masters); }
    
    public static void enableHdfsZooInstance(JobConf job) { job.setBoolean(USE_HDFS_ZOO_INSTANCE, true); }
    public static void disableHdfsZooInstance(JobConf job) { job.setBoolean(USE_HDFS_ZOO_INSTANCE, false); }
    public static boolean useHdfsZooInstance(JobConf job) { return job.getBoolean(USE_HDFS_ZOO_INSTANCE, false); }
    
    public static String getDefaultTableName(JobConf job) { return job.get(DEFAULT_TABLE_NAME); }
    public static void setDefaultTableName(JobConf job, String name) { job.set(DEFAULT_TABLE_NAME, name); }

    // Return the record writer
    @Override
    public RecordWriter<Text, Mutation> getRecordWriter(FileSystem ignored, JobConf job, String jobName, Progressable progress)
    throws IOException
    {
    	try {
			return new CBRecordWriter(job);
		} catch (Exception e) {
			throw new IOException(e);
		}
	}
    
    @Override
    public void checkOutputSpecs(FileSystem ignored, JobConf job)
    throws IOException
    {
    	String username = job.get(USERNAME);
    	String password = job.get(PASSWORD);
    	String instance = job.get(INSTANCE);
    	boolean hdfszoo = job.getBoolean(USE_HDFS_ZOO_INSTANCE, false);
    	
        if (username == null || username.isEmpty())
            throw new IOException("Missing username");
        if (password == null || password.isEmpty())
            throw new IOException("Missing password");
		if ((instance == null || instance.isEmpty()) && !hdfszoo)
			throw new IOException("Missing instance");
    }

    public class CBRecordWriter implements RecordWriter<Text, Mutation>
    {
        // TODO This is the log4j logger, which uses Hadoop's log4j settings. We may want to avoid this for now, and use our own logging.
        private final Logger log = Logger.getLogger(CBRecordWriter.class.getName());
        
        private MultiTableBatchWriter mtbw = null;
        private HashMap<Text, BatchWriter> bws = null;
        private Instance instance = null;
        private String username = null;
        private byte[] password = null;
        private Text defaultTableName = null;
        
        private boolean debug = false;
        private boolean verbose = false;
        private boolean createTables = false;
        private boolean useHdfsZooInstance = false;
        
        private long mutCount = 0;
        private long valCount = 0;

        public CBRecordWriter(JobConf job)
        throws IOException, CBException, CBSecurityException
        {
            this.debug = isDebug(job);
            this.verbose = isVerbose(job);
            this.createTables = canCreateTables(job);
            this.useHdfsZooInstance = useHdfsZooInstance(job);

            if (debug)
                return;
            
            String inst = getInstance(job);
            this.instance = ((inst == null || inst.isEmpty()) && useHdfsZooInstance) ? new HdfsZooInstance() : new MasterInstance(inst);

            this.bws = new HashMap<Text, BatchWriter>();
            
            this.username = getUsername(job);
            this.password = getPassword(job);
            
            String tname = getDefaultTableName(job);
            this.defaultTableName = (tname == null) ? null : new Text(tname);
            
            Connector connector = new Connector(instance, username, password);
            mtbw = connector.createMultiTableBatchWriter(getMaxMutationBufferSize(job), getMaxLatency(job), getMaxWriteThreads(job));
        }

        public void write(Text table, Mutation mutation)
        throws IOException
        {
            if (table == null || table.toString().isEmpty())
                table = this.defaultTableName;
            
            if (debug || table==null) {
                if (verbose && table != null) {
                    ++mutCount;
                    valCount += mutation.getUpdates().size();
                    printMutation(table, mutation);
                }
                // TODO if verbose is disabled and no table name is specified, do we really want to fail at input, without a peep?
                // I understand not inputting for debugging reasons, but to ignore data silently because verbose isn't enabled...
                // I'm not clear on the reason for this.
                return;
            }
            if (!bws.containsKey(table))
				try {
					addTable(table, null);
				} catch (Exception e) {
					e.printStackTrace();
					throw new IOException(e);
				}
            
            bws.get(table).addMutation(mutation);
        }

        public void close(Reporter reporter)
        {
            if (debug) {
                if (verbose)
                    System.out.println("mutations written: "+mutCount+", values written: "+valCount);
                return;
            }
            
            try {
                mtbw.close();
            } catch (MutationsRejectedException e) {
                if(e.getAuthorizationFailures().size() >= 0){
                    HashSet<String> tables = new HashSet<String>();
                    for(KeyExtent ke : e.getAuthorizationFailures()){
                        tables.add(ke.getTableName().toString());
                    }
                    
                    log.error("Not authorized to write to tables : "+tables);
                }
                
                if(e.getConstraintViolationSummaries().size() > 0){
                    log.error("Constraint violations : "+e.getConstraintViolationSummaries().size());
                }
            }
        }

        /*
         * This method should not be used to create tables outside of this class.
         * Instead, a new Connector object should be created to do that.
         */
        public void addTable(Text key, Map<Text, Class<? extends Aggregator>> aggregators)
        throws CBException, CBSecurityException
        {
            if (debug)
                return;
            
            BatchWriter bw = null;
            String table = key.toString();
            Connector conn = new Connector(this.instance, this.username, this.password);
            
            if (createTables && !conn.tableOperations().exists(table))
            {
                try {
                    if (aggregators == null)
                        conn.tableOperations().create(table);
                    else
                        conn.tableOperations().create(table, aggregators);
                } catch (CBSecurityException e) {
                    log.error("Cloudbase security violation creating " + table, e);
                    throw e;
                } catch (TableExistsException e) {
					// Shouldn't happen
				}
            }

            try {
                bw = mtbw.getBatchWriter(table);
            } catch (TableNotFoundException e) {
                log.error("CLOUDBASE Table " + table +" doesn't exist and cannot be created.", e);
                throw new CBException(e);
            } catch (CBException e) {
            	throw e;
			} catch (CBSecurityException e) {
				throw e;
			}

            if (bw != null)
                bws.put(key, bw);
        }
        
        private int printMutation(Text table, Mutation m)
        throws UnsupportedEncodingException
        {
            System.out.println();
            System.out.printf("DEBUG %s row key: ", table);
            System.out.println(hexDump(m.getRow()));
            for (ColumnUpdate cu : m.getUpdates()) {
                System.out.printf("DEBUG %s column: %s:%s\n", table, hexDump(cu.getColumnFamily()), hexDump(cu.getColumnQualifier()));
                System.out.printf("DEBUG %s value: %s\n", table, hexDump(cu.getValue()));
                System.out.printf("DEBUG %s security: %s\n",table, new LabelExpression(cu.getLabels()).toString());
            }
            return m.getUpdates().size();
        }
        
        private String hexDump(byte[] ba)
        {
            StringBuilder sb = new StringBuilder();
            for (byte b : ba) {
                if ((b > 0x20) && (b < 0x7e))
                    sb.append((char)b);
                else
                    sb.append(String.format("x%02x", b));
            }
            return sb.toString();
        }

    }
}
