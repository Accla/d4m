package cloudbase.core.test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.io.Text;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import cloudbase.core.client.BatchWriter;
import cloudbase.core.client.CBException;
import cloudbase.core.client.CBSecurityException;
import cloudbase.core.client.Connector;
import cloudbase.core.client.MutationsRejectedException;
import cloudbase.core.client.TableExistsException;
import cloudbase.core.client.TableNotFoundException;
import cloudbase.core.client.impl.HdfsZooInstance;
import cloudbase.core.data.ConstraintViolationSummary;
import cloudbase.core.data.Key;
import cloudbase.core.data.KeyExtent;
import cloudbase.core.data.Mutation;
import cloudbase.core.data.MyMapFile;
import cloudbase.core.data.Value;
import cloudbase.core.data.MySequenceFile.CompressionType;
import cloudbase.core.security.Authenticator;
import cloudbase.core.security.LabelExpression;
import cloudbase.core.security.ZKAuthenticator;
import cloudbase.core.security.thrift.AuthInfo;



public class TestIngest {
	private static Logger log = Logger.getLogger(TestIngest.class.getName());
	private static AuthInfo rootCredentials = new AuthInfo("root", "secret".getBytes());
	
	public static class CreateTable {
		public static void main(String[] args)
		throws CBException, CBSecurityException, TableExistsException
		{
			long start = Long.parseLong(args[0]);
			long end = Long.parseLong(args[1]);
			long numsplits = Long.parseLong(args[2]);
			
			long splitSize = (end - start)/numsplits;
			
			long pos = start + splitSize;
			
			TreeSet<Text> splits = new TreeSet<Text>();
			
			while(pos < end)
			{
				splits.add(new Text(String.format("row_%010d", pos)));
				pos+=splitSize;
			}
			
			Connector conn = new Connector(new HdfsZooInstance(), rootCredentials.user, rootCredentials.password);
			conn.tableOperations().create("test_ingest", splits);
			System.exit(0);
		}
	}
	
	public static class CreateMapFiles {
        public static void main(String[] args) throws InterruptedException
        {
            String dir = args[0];
            int numThreads = Integer.parseInt(args[1]);
            long start = Long.parseLong(args[2]);
            long end = Long.parseLong(args[3]);
            long numsplits = Long.parseLong(args[4]);
            
            long splitSize = Math.round((end - start)/(double)numsplits);
            
            long currStart = start;
            long currEnd = start + splitSize;
            
            ExecutorService threadPool = Executors.newFixedThreadPool(numThreads);
            
            int count = 0;
            while(currEnd <= end && currStart < currEnd){
                
                final String tia = String.format("-mapFile /%s/mf%05d -timestamp 1 -size 50 -random 56 %d %d 1", dir, count, currEnd - currStart, currStart);
                
                Runnable r = new Runnable(){

                    @Override
                    public void run() {
                        try {
                            TestIngest.main(tia.split(" "));
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }                        
                    }
                    
                };
                
                threadPool.execute(r);
                
                //System.out.println(tia);
                
                count++;
                currStart = currEnd;
                currEnd = Math.min(end, currStart + splitSize);
            }
            
            
            threadPool.shutdown();
            while(!threadPool.isTerminated())
                threadPool.awaitTermination(1, TimeUnit.HOURS);
            
        }
    }
	
	public static class IngestArgs {
		int rows;
		int startRow;
		int cols;
		
		boolean random = false;
		int seed = 0;
		int dataSize = 1000;
		
		boolean delete = false;
		long timestamp = 0;
		boolean hasTimestamp = false;
		boolean useGet = false;
		
		public boolean unique;
		
		boolean outputToMapFile = false;
		String outputFile;
		
		int stride;
		public boolean useTsbw = false;
	}
	
	public static IngestArgs parseArgs(String args[]){
		IngestArgs ia = new IngestArgs();
		
		
		List<String> requiredArgs = new ArrayList<String>();
		
		for (int i = 0; i < args.length; i++) {
			if(args[i].equals("-size")){
				i++;
				ia.dataSize = Integer.parseInt(args[i]);
			}else if(args[i].equals("-timestamp")){
				i++;
				ia.timestamp = Long.parseLong(args[i]);
				ia.hasTimestamp = true;
			}else if(args[i].equals("-delete")){
				ia.delete = true;
			}else if(args[i].equals("-useGet")){
				ia.useGet = true;
			}else if(args[i].equals("-random")){
				ia.random = true;
				i++;
				ia.seed = Integer.parseInt(args[i]);
			}else if(args[i].equals("-mapFile")){
				ia.outputToMapFile = true;
				i++;
				ia.outputFile = args[i];
			}else if(args[i].equals("-stride")){
				i++;
				ia.stride = Integer.parseInt(args[i]);
			}else if(args[i].equals("-tsbw")){
				ia.useTsbw  = true;
			}else{
				requiredArgs.add(args[i]);
			}
		}
		
		if(requiredArgs.size() != 3){
			log.error("usage : test_ingest [-delete] [-size <value size>] [-random <seed>] [-timestamp <ts>] [-stride <size>] <rows> <start row> <# cols> ");
			System.exit(-1);
		}
		
		ia.rows = Integer.parseInt(requiredArgs.get(0));
		ia.startRow = Integer.parseInt(requiredArgs.get(1));
		ia.cols = Integer.parseInt(requiredArgs.get(2));
		
		
		
		return ia;
	}
	
	public static byte[][] generateValues(IngestArgs ingestArgs){
		
		byte[][] bytevals = new byte[10][];
		

		byte[] letters = {'1','2','3','4','5','6','7','8','9','0'};
		
		for(int i = 0; i < 10; i++) {
			bytevals[i] = new byte[ingestArgs.dataSize];
			for(int j=0; j< ingestArgs.dataSize; j++)
				bytevals[i][j] = letters[i]; 
		}
		
		return bytevals;
	}
	
	public static byte[] genRandomValue(Random random, byte dest[], int seed, int row, int col){
		random.setSeed((row ^ seed) ^ col);
		random.nextBytes(dest);
		//transform to printable chars
		for(int i = 0; i < dest.length; i++) {
			dest[i] = (byte)(( (0xff & dest[i]) % 92) + ' ');
		}
		
		return dest;
	}
	
	public static void main(String[] args) throws IOException, CBException, CBSecurityException, TableNotFoundException, MutationsRejectedException
	{
		//Table t = null;

		ConsoleAppender ca = new ConsoleAppender();
		ca.setThreshold(Level.DEBUG);
		Logger.getLogger("cloudbase.core").addAppender(ca);
		Logger.getLogger("cloudbase.core").setLevel(Level.DEBUG);
		
		
		IngestArgs ingestArgs = parseArgs(args);

		// TODO compare updates to different rows verses only one
		// how does performance change
		
		/*
		System.out.println("writing ...");
		for(int i=0; i < 10000; i++) {
			Mutation m = new Mutation(new Text("row1"));
			m.set(new Text("colfam:col1"), new ImmutableBytesWritable(("value1").getBytes()));
			t.update(m);
			if(i % 100 == 0)
				System.out.println(i);
		}
		*/
		
		// test batch update
		
		
		
		long stopTime;
		
		byte[][] bytevals = generateValues(ingestArgs);
		
		byte randomValue[] = new byte[ingestArgs.dataSize];
		Random random = new Random();
		
		long bytesWritten = 0;
		
		BatchWriter bw = null;
		MyMapFile.Writer mfw = null;
		
		if(ingestArgs.outputToMapFile){
			Configuration conf = new Configuration();
			FileSystem fs = FileSystem.get(conf);
			mfw = new MyMapFile.Writer(conf, fs, ingestArgs.outputFile, Key.class, Value.class, CompressionType.BLOCK);
		}else{
		    Connector connector = new Connector(new HdfsZooInstance(), rootCredentials.user, rootCredentials.password);
			bw = connector.createBatchWriter("test_ingest", 20000000, 10 * 60, 10); 
		}
		
		short[] labels = new short[]{1,2,3,4};
		Set<Short> labelsSet = new HashSet<Short>();
		for (short s : labels)
			labelsSet.add(s);
		Authenticator authenticator = new ZKAuthenticator();
		authenticator.changeAuthorizations(rootCredentials, rootCredentials.user, labelsSet);
		LabelExpression le = new LabelExpression(labels);
		byte[] labBA = le.toByteArray();
		
		//int step = 100;
		
		long startTime = System.currentTimeMillis();
		for(int i=0; i < ingestArgs.rows; i++) {
			
			int rowid;
			
			if(ingestArgs.stride > 0){
				rowid = ((i%ingestArgs.stride) * (ingestArgs.rows / ingestArgs.stride)) + (i/ingestArgs.stride);	
			}else{
				rowid = i;
			}
			
			Text row = new Text("row_" + String.format("%010d", rowid + ingestArgs.startRow));
			Mutation m = new Mutation(row);
			for(int j =0; j < ingestArgs.cols; j++) {
				Text colf = new Text("colf");
				Text colq = new Text("col_"+ String.format("%05d", j));
				
				if(m == null){
					m = new Mutation(row);
				}
				
				if(ingestArgs.outputToMapFile){
					Key key = new Key(row, colf, colq, labBA);
					if(ingestArgs.hasTimestamp) {
						key.setTimestamp(ingestArgs.timestamp);
					}else{
						key.setTimestamp(System.currentTimeMillis());
					}
					
					if(ingestArgs.delete){
						key.setDeleted(true);
					}else{
						key.setDeleted(false);
					}
					
					bytesWritten += key.getSize();
					
					if(ingestArgs.delete){
						mfw.append(key, new Value(new byte[0]));
					}else{
						byte value[];
						if(ingestArgs.random){
							value = genRandomValue(random, randomValue, ingestArgs.seed, rowid + ingestArgs.startRow, j);
						}else{
							value = bytevals[j % bytevals.length];
						}
						
						Value v = new Value(value);
						mfw.append(key, v);
						bytesWritten += v.getSize();
					}
					
				}else{
				    Key key = new Key(row, colf, colq, labBA);
				    bytesWritten += key.getSize();

				    if(ingestArgs.delete){
						if(ingestArgs.hasTimestamp)
							m.remove(colf, colq, le, ingestArgs.timestamp);
						else
							m.remove(colf, colq, le);
					}else{
						byte value[];
						if(ingestArgs.random){
							value = genRandomValue(random, randomValue, ingestArgs.seed, rowid + ingestArgs.startRow, j);
						}else{
							value = bytevals[j % bytevals.length];
						}
						bytesWritten += value.length;

						if(ingestArgs.hasTimestamp) {
							m.put(colf, colq, le, ingestArgs.timestamp, new Value(value, true));
						}
						else {
							m.put(colf, colq, le, new Value(value, true));

						}
					}

					if(bw != null){
						bw.addMutation(m);
						//m = new Mutation(new Text("row_" + String.format("%010d", rowid + ingestArgs.startRow)));
						m = null;
					}
				
				}
			}
			
		}
		
		if(ingestArgs.outputToMapFile){
			mfw.close();
		}else if(bw != null){
			//System.out.println("DEBUG : calling batch writer close");
			
			try {
				bw.close();
			} catch (MutationsRejectedException e) {
				if(e.getAuthorizationFailures().size() > 0){
					for (KeyExtent ke : e.getAuthorizationFailures()) {
						System.err.println("ERROR : Not authorized to write to : "+ke);
					}
				}
				
				if(e.getConstraintViolationSummaries().size() > 0){
					for (ConstraintViolationSummary cvs : e.getConstraintViolationSummaries()) {
						System.err.println("ERROR : Constraint violates : "+cvs);
					}
				}
				
				
				throw e;
			}
		}
		
		stopTime = System.currentTimeMillis();
		
		int totalValues = ingestArgs.rows*ingestArgs.cols;
		double elapsed = (stopTime - startTime) / 1000.0;
		
		System.out.printf("%,12d records written | %,8d records/sec | %,12d bytes written | %,8d bytes/sec | %6.3f secs   \n",totalValues, (int)(totalValues/elapsed),bytesWritten, (int)(bytesWritten/elapsed), elapsed );
	}
}
