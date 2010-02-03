package cloudbase.examples.client;

import java.util.HashSet;
import java.util.Random;

import org.apache.hadoop.io.Text;

import cloudbase.core.client.BatchWriter;
import cloudbase.core.client.CBException;
import cloudbase.core.client.CBSecurityException;
import cloudbase.core.client.Connector;
import cloudbase.core.client.MutationsRejectedException;
import cloudbase.core.client.TableNotFoundException;
import cloudbase.core.data.KeyExtent;
import cloudbase.core.data.Mutation;
import cloudbase.core.data.Value;

public class RandomBatchWriter {
	
	public static byte[] createValue(long rowid, int dataSize){
		Random r = new Random(rowid);
		byte value[] = new byte[dataSize];
		
		r.nextBytes(value);
		
		//transform to printable chars
		for(int j = 0; j < value.length; j++) {
			value[j] = (byte)(( (0xff & value[j]) % 92) + ' ');
		}
		
		return value;
	}
	
	public static Mutation createMutation(long rowid, int dataSize){
		Text row = new Text(String.format("row_%010d", rowid));
		
		//System.out.println("row = "+row);
		
		Mutation m = new Mutation(row);
		
		//create a random value that is a function of the
		//row id for verification purposes
		byte value[] = createValue(rowid, dataSize);
		
		m.put(new Text("foo"), new Text("1"), new Value(value));
		
		return m;
	}
	
	public static void main(String[] args) throws CBException, CBSecurityException, TableNotFoundException
	{
		if(args.length != 10)
		{
			System.out.println("Usage : RandomBatchWriter <master> <username> <password> <table> <num> <min> <max> <value size> <max memory> <num threads>");
			return;
		}
		
		String master = args[0];
		String user = args[1];
		byte[] pass = args[2].getBytes();
		String table = args[3];
		int num = Integer.parseInt(args[4]);
		long min = Long.parseLong(args[5]);
		long max = Long.parseLong(args[6]);
		int valueSize = Integer.parseInt(args[7]);
		int maxMemory = Integer.parseInt(args[8]);
		int numThreads = Integer.parseInt(args[9]);
		
		Random r = new Random();
		
		Connector connector = new Connector(master, user, pass);
		BatchWriter bw = connector.createBatchWriter(table, maxMemory, Integer.MAX_VALUE, numThreads);
		
		for(int i = 0; i < num; i++){
			
			long rowid = (Math.abs(r.nextLong()) % (max - min)) + min;
			
			Mutation m = createMutation(rowid, valueSize);
			
			bw.addMutation(m);
			
		}
		
		try {
			bw.close();
		} catch (MutationsRejectedException e) {
			if(e.getAuthorizationFailures().size() > 0){
				HashSet<String> tables = new HashSet<String>();
				for(KeyExtent ke : e.getAuthorizationFailures()){
					tables.add(ke.getTableName().toString());
				}
				System.err.println("ERROR : Not authorized to write to tables : "+tables);
			}
			
			if(e.getConstraintViolationSummaries().size() > 0){
				System.err.println("ERROR : Constraint violations occurred : "+e.getConstraintViolationSummaries());
			}
		}
	}
}
