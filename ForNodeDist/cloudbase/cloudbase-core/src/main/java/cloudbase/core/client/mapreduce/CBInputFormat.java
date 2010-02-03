package cloudbase.core.client.mapreduce;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.InputFormat;
import org.apache.hadoop.mapred.InputSplit;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.RecordReader;
import org.apache.hadoop.mapred.Reporter;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;
import cloudbase.core.CBConstants;
import cloudbase.core.client.CBException;
import cloudbase.core.client.CBSecurityException;
import cloudbase.core.client.Instance;
import cloudbase.core.client.MasterInstance;
import cloudbase.core.client.TableNotFoundException;
import cloudbase.core.client.impl.HdfsZooInstance;
import cloudbase.core.client.mapreduce.bulk.BulkOperations;
import cloudbase.core.data.Column;
import cloudbase.core.data.Key;
import cloudbase.core.data.KeyExtent;
import cloudbase.core.data.Value;
import cloudbase.core.security.thrift.AuthInfo;

/**
 * This class allows MapReduce jobs to use Cloudbase as the source of data
 * 
 * The user must specify the following via static methods:
 * 
 *  CBInputFormat.setUsername(job, username) - sets the username for cloudbase from a string
 *  CBInputFormat.setPassword(job, password) - sets the password for cloudbase from a byte array
 *  CBInputFormat.setInstance(job, instance) - sets the location of the cloudbase master, as a string
 *                                              containing a comma separated list of host[:port]
 *  CBInputFormat.setTableName(job, table) - the name of the table in cloudbase from which to read data  
 *  CBInputFormat.setAuthorizations(job, authorizations) - a comma separated list of record level authorizations
 *  
 *  Optional methods:
 *  
 *  CBInputFormat.setStartRow(job, row) - The row to start scanning on in the table. 
 *  								      Leave blank to start at the beginning of the table.
 *  CBInputFormat.setEndRow(job, row)	- The row to start scanning on in the table.
 *  									  Leave blank to start at the beginning of the table.
 *  CBInputFormat.setColumns(job, columns) - A comma separated list of columns to scan over. 
 *  										 Leave blank for all columns.
 *  CBInputFormat.setMaxMaps(maps)		- Sets the number of maximum maps of this map/reduce job
 * 
 * This input format provides keys and values of type Key and Value to the Map() and Reduce() functions
 * 
 */
public class CBInputFormat implements InputFormat<Key, Value>
{
	public RecordReader<Key, Value> getRecordReader(InputSplit split, JobConf job, Reporter reporter)
	throws IOException
	{
		Instance inst = useHdfsZooInstance(job) ? new HdfsZooInstance() : new MasterInstance(getInstance(job));
		String user = getUsername(job);
		byte[] pass = getPassword(job);
		Set<Short> auths = getAuthorizations(job);
		try {
			return new CBRecordReader((CBInputSplit) split, inst, user, pass, auths);
		} catch (Exception e) {
			e.printStackTrace();
			throw new IOException(e);
		}
	}
	
	/**
	 * read the metadata table to get tablets of interest
	 * these each become a split
	 * 
	 */
	public InputSplit[] getSplits(JobConf job, int numSplits)
	throws IOException
	{

		ArrayList<CBInputSplit> splits = new ArrayList<CBInputSplit>();
		
		SortedSet<KeyExtent> extents = new TreeSet<KeyExtent>();
		Map<KeyExtent, String> locations = new HashMap<KeyExtent, String>();
		
		// extract information from metadata table
		try {
			BulkOperations.getMetadataEntries(new AuthInfo(getUsername(job), getPassword(job)), getTableName(job), locations, extents);
		} catch (TableNotFoundException e) {
			throw new IOException("table does not exist", e);
		} catch (CBException e) {
			throw new IOException(e);
		} catch (CBSecurityException e) {
			throw new IOException(e);
		}
		
		boolean create = false;
		
		// create splits
		if (getMaxMaps(job) > 0)
		{
			Instance instance = new HdfsZooInstance();
			Collection<Text> splitRows;
			try {
				splitRows = BulkOperations.getSplits(instance, new AuthInfo(getUsername(job), getPassword(job)), getTableName(job), getMaxMaps(job) - 1);
			} catch (TableNotFoundException e) {
				throw new IOException(e);
			} catch (CBException e) {
				throw new IOException(e);
			} catch (CBSecurityException e) {
				throw new IOException(e);
			}
			create = createSplitsForMaxMaps(job, splitRows, extents, locations, splits);
		}
		else
			create = createSplits(job, extents, locations, splits);
		
		if (create)
			return splits.toArray(new CBInputSplit[splits.size()]);
		else
			return new ArrayList<CBInputSplit>().toArray(new CBInputSplit[0]); // or just null?
	}	
	
	// This should not be called outside of this class except for testing
	protected boolean createSplitsForMaxMaps(JobConf job, Collection<Text> splitRows, Set<KeyExtent> kes, Map<KeyExtent, String> locations, List<CBInputSplit> splits)
	{
	
		String tableName = getTableName(job);
		Text startRow = getStartRow(job);
		Text stopRow = getStopRow(job);


		SortedSet<Column> columns = getColumns(job);
		Set<Short> authorizations = getAuthorizations(job);
		
		Text prevRow = startRow;

		Collection<KeyExtent> keyextent = null;
		List<String> locs = null;
		
		for (Text row : splitRows)
		{			
			if(row.compareTo(prevRow) <= 0){
				continue;
			}
			
			if(stopRow.getLength() > 0 && row.compareTo(stopRow) >= 0){
				break;
			}
			
			keyextent = KeyExtent.getKeyExtentsForRange(prevRow, row, kes);
			locs = new ArrayList<String>();
			for (KeyExtent k : keyextent) {
				locs.add(locations.get(k));
			}
			splits.add(new CBInputSplit(tableName, prevRow, row, columns, authorizations, locs.toArray(new String[locs.size()])));
			
			//System.out.println("prevEndRow "+prevRow+" endRow "+row+" startRow "+startRow+" stopRow "+stopRow+" thisStartRow "+startRow+" thisStopRow "+stopRow+" loc "+locs.toString());
						

			prevRow = row;				
		}

		keyextent = KeyExtent.getKeyExtentsForRange(prevRow, stopRow, kes);
		locs = new ArrayList<String>();
		for (KeyExtent k : keyextent) {
			locs.add(locations.get(k));
		}
		

		splits.add(new CBInputSplit(tableName, prevRow, stopRow, columns, authorizations, locs.toArray(new String[locs.size()])));
		//System.out.println("prevEndRow "+prevRow+" endRow "+stopRow+" startRow "+startRow+" stopRow "+stopRow+" thisStartRow "+startRow+" thisStopRow "+stopRow+" loc "+locs.toString());

		
		return true;
			
	}

	// This should not be called outside of this class except for testing
	protected boolean createSplits(JobConf job, Set<KeyExtent> kes, Map<KeyExtent, String> locations, List<CBInputSplit> splits)
	{
		for(KeyExtent extent : kes) {				
			String[] locs = new String[1];
			locs[0] = locations.get(extent);
			Text prevEndRow = extent.getPrevEndRow();
			Text endRow = extent.getEndRow();
			Text thisStartRow = prevEndRow;
			Text thisStopRow = endRow;
			Text startRow = getStartRow(job);
			Text stopRow = getStopRow(job);
			
			if (startRow.getLength()>0 && (prevEndRow==null || startRow.compareTo(prevEndRow)>0)) {
				if (endRow!=null && startRow.compareTo(endRow)>0) {
				    //System.out.println("skipping extent "+locs[0]);
					continue;
				}
				thisStartRow = startRow;
			}
			
			if (stopRow.getLength()>0 && (endRow==null || stopRow.compareTo(endRow)<0)) {
				if (prevEndRow!=null && stopRow.compareTo(prevEndRow)<=0) {
				    //System.out.println("skipping extent "+locs[0]);
					continue;
				}
				thisStopRow = stopRow;
			}
			// System.out.println("prevEndRow "+prevEndRow+" endRow "+endRow+" startRow "+startRow+" stopRow "+stopRow+" thisStartRow "+thisStartRow+" thisStopRow "+thisStopRow+" loc "+locs[0]);
			splits.add(new CBInputSplit(getTableName(job), thisStartRow, thisStopRow, getColumns(job), getAuthorizations(job), locs));
		}
		return true;
	}

	private static final String USERNAME = "cloudbase.input.username";
	private static final String PASSWORD = "cloudbase.input.password";
	private static final String INSTANCE = "cloudbase.input.instance";
	private static final String USE_HDFS_ZOO_INSTANCE = "cloudbase.input.hdfszooinstance";
	private static final String TABLE_NAME = "cloudbase.input.tablename";
	private static final String START_ROW = "cloudbase.input.startrow";
	private static final String STOP_ROW = "cloudbase.input.stoprow";
	private static final String MAX_MAPS = "cloudbase.input.maxmaps";
	private static final String COLUMN_NAMES = "cloudbase.input.columnnames";
	private static final String AUTHORIZATIONS = "cloudbase.input.authorizations";
	
	public static void setMaxMaps(Configuration job, int max) { job.setInt(MAX_MAPS, max); }
	public static int getMaxMaps(Configuration job) { return job.getInt(MAX_MAPS, 0); }

    public static String getInstance(JobConf job) { return job.get(INSTANCE); }
    public static void setInstance(Configuration job, String masters) { job.set(INSTANCE, masters); }
    
    public static String getUsername(Configuration job) { return job.get(USERNAME); }
    public static void setUsername(Configuration job, String user) { job.set(USERNAME, user); }

    // WARNING: The password is stored in the Configuration and shared with all MapReduce tasks
    // The BASE64 encoding is not intended to be secure. It's only used for a Charset safe conversion to String
    public static byte[] getPassword(Configuration job) throws IOException { return new BASE64Decoder().decodeBuffer(job.get(PASSWORD)); }
    public static void setPassword(Configuration job, byte[] pwd) { job.set(PASSWORD, new BASE64Encoder().encode(pwd)); }

    public static void enableHdfsZooInstance(Configuration job) { job.setBoolean(USE_HDFS_ZOO_INSTANCE, true); }
    public static void disableHdfsZooInstance(Configuration job) { job.setBoolean(USE_HDFS_ZOO_INSTANCE, false); }
    public static boolean useHdfsZooInstance(Configuration job) { return job.getBoolean(USE_HDFS_ZOO_INSTANCE, false); }

	
	public static String getTableName(Configuration job) { return job.get(TABLE_NAME); }
	public static void setTableName(Configuration job, String table) { job.set(TABLE_NAME, table); }

	public static Text getStartRow(Configuration job) { return new Text(job.get(START_ROW,"")); }
	public static void setStartRow(Configuration job, String startRow) { job.set(START_ROW, startRow); }

	public static Text getStopRow(Configuration job) { return new Text(job.get(STOP_ROW,"")); }
	public static void setStopRow(Configuration job, String stopRow) { job.set(STOP_ROW, stopRow); }

	public static SortedSet<Column> getColumns(Configuration job)
	{
		SortedSet<Column> columns = new TreeSet<Column>();
		String columnString = job.get(COLUMN_NAMES);
		
		if(columnString!=null && columnString.length() > 0)
		{
			for(String col : columnString.split(","))
			{
				String colParts[] = col.split(":",2);
				
				columns.add((colParts.length == 1) ? new Column(col.getBytes(), null, null) : new Column(colParts[0].getBytes(), colParts[1].getBytes(), null));
			}
		}
		return columns;
	}

	public static void setColumns(Configuration job, String columns) { job.set(COLUMN_NAMES, columns); }

	public static Set<Short> getAuthorizations(Configuration job)
	{
		String authString = job.get(AUTHORIZATIONS);
		
		if (authString == null)
			return CBConstants.NO_AUTHS;
		
		Set<Short> authorizations = new HashSet<Short>();
		for (String s : authString.split(","))
			if (!s.isEmpty())
				authorizations.add(Short.parseShort(s));
		return authorizations;
	}

	public static void setAuthorizations(Configuration job, String authorizations) { job.set(AUTHORIZATIONS, authorizations); }
	public static void setAuthorizations(Configuration job, Set<Short> authorizations)
	{
		StringBuilder sb = new StringBuilder();
		for (short s : authorizations)
			sb.append(String.format("%d,", s));
		job.set(AUTHORIZATIONS, sb.substring(0, sb.length()-1)); // chop off trailing comma
	}

}
