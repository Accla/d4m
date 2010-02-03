package cloudbase.examples.client;

import cloudbase.core.client.BatchWriter;
import cloudbase.core.client.CBException;
import cloudbase.core.client.CBSecurityException;
import cloudbase.core.client.Connector;
import cloudbase.core.client.MutationsRejectedException;
import cloudbase.core.client.TableNotFoundException;
import cloudbase.core.data.Mutation;

public class SequentialBatchWriter {
	
	public static void main(String[] args) throws CBException, CBSecurityException, TableNotFoundException, MutationsRejectedException
	{
		if(args.length != 9)
		{
			System.out.println("Usage : SequentialBatchWriter <master> <username> <password> <table> <start> <num> <value size> <max memory> <num threads>");
			return;
		}
		
		String master = args[0];
		String user = args[1];
		byte[] pass = args[2].getBytes();
		String table = args[3];
		long start = Long.parseLong(args[4]);
		long num = Long.parseLong(args[5]);
		int valueSize = Integer.parseInt(args[6]);
		int maxMemory = Integer.parseInt(args[7]);
		int numThreads = Integer.parseInt(args[8]);
		
		Connector connector = new Connector(master, user, pass);
		BatchWriter bw = connector.createBatchWriter(table, maxMemory, Integer.MAX_VALUE, numThreads);
		
		long end = start + num;
		
		for(long i = start; i < end; i++)
		{
			Mutation m = RandomBatchWriter.createMutation(i, valueSize);
			bw.addMutation(m);
		}
		
		bw.close();
	}
}
