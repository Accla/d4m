package cloudbase.examples.client;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.Map.Entry;

import org.apache.hadoop.io.Text;

import cloudbase.core.CBConstants;
import cloudbase.core.client.BatchScanner;
import cloudbase.core.client.CBException;
import cloudbase.core.client.CBSecurityException;
import cloudbase.core.client.Connector;
import cloudbase.core.client.TableNotFoundException;
import cloudbase.core.data.Key;
import cloudbase.core.data.Range;
import cloudbase.core.data.Value;

class CountingVerifyingReceiver {

	long count = 0;
	int expectedValueSize = 0;
	HashMap<Text, Boolean> expectedRows;
	
	CountingVerifyingReceiver(HashMap<Text, Boolean> expectedRows, int expectedValueSize){
		this.expectedRows = expectedRows;
		this.expectedValueSize = expectedValueSize;
	}
	
	public void receive(Key key, Value value) {
		
		String row = key.getRow().toString();
		long rowid = Integer.parseInt(row.split("_")[1]);
		
		byte expectedValue[] = RandomBatchWriter.createValue(rowid, expectedValueSize);
		
		if(!Arrays.equals(expectedValue, value.get())){
			System.out.println("ERROR : Got unexpected value for "+key+" expected : "+new String(expectedValue)+" got : "+new String(value.get()));
		}
		
		if(!expectedRows.containsKey(key.getRow())){
			System.out.println("ERROR : Got unexpected key "+key);
		}else{
			expectedRows.put(key.getRow(), true);
		}
		
		count++;
	}
}

public class RandomBatchScanner {
	
	static void generateRandomQueries(int num, long min, long max, Random r, HashSet<Range> ranges, HashMap<Text, Boolean> expectedRows){
		System.out.printf("Generating %,d random queries...",num);
		while(ranges.size() < num){
			long rowid = (Math.abs(r.nextLong()) % (max - min)) + min;
			
			Text row1 = new Text(String.format("row_%010d", rowid));
			Text row2 = new Text(String.format("row_%010d", rowid+1));
			
			Range range = new Range(new Key(row1), new Key(row2));
			ranges.add(range);
			expectedRows.put(row1, false);
		}
		
		System.out.println("finished");
	}
	
	private static void printRowsNotFound(HashMap<Text, Boolean> expectedRows) {
		int count = 0;
		for (Entry<Text, Boolean> entry : expectedRows.entrySet()) {
			if(!entry.getValue()){
				count++;
			}
		}
		
		if(count > 0){
			System.out.println("Did not find "+count+" rows");
			for (Entry<Text, Boolean> entry : expectedRows.entrySet()) {
				if(!entry.getValue()){
					System.out.println("Did not find row "+entry.getKey());
				}
			}
		}
		
	}
	
	static void doRandomQueries(int num, long min, long max, int evs, Random r, BatchScanner tsbr)
	{
		
		HashSet<Range> ranges = new HashSet<Range>(num);
		HashMap<Text, Boolean> expectedRows = new java.util.HashMap<Text, Boolean>();
		
		generateRandomQueries(num, min, max, r, ranges, expectedRows);
		
		tsbr.setRanges(ranges);
		
		CountingVerifyingReceiver receiver = new CountingVerifyingReceiver(expectedRows, evs);
		
		long t1 = System.currentTimeMillis();
		
		for (Entry<Key,Value> entry : tsbr) {
            receiver.receive(entry.getKey(), entry.getValue());
        }
		
		long t2 = System.currentTimeMillis();
		
		System.out.printf("%6.2f lookups/sec %6.2f secs\n", num / ((t2 - t1)/1000.0), ((t2 - t1)/1000.0));
		System.out.printf("num results : %,d\n", receiver.count);
		
		printRowsNotFound(expectedRows);
	}

	public static void main(String[] args) throws CBException, CBSecurityException, TableNotFoundException
	{
		
		if(args.length != 9)
		{
			System.out.println("Usage : RandomBatchReader <master> <username> <password> <table> <num> <min> <max> <expected value size> <num threads>");
			return;
		}
		
		String master = args[0];
		String user = args[1];
		byte[] pass = args[2].getBytes();
		String table = args[3];
		int num = Integer.parseInt(args[4]);
		long min = Long.parseLong(args[5]);
		long max = Long.parseLong(args[6]);
		int expectedValueSize = Integer.parseInt(args[7]);
		int numThreads = Integer.parseInt(args[8]);
		
		Connector connector = new Connector(master, user, pass);
		BatchScanner tsbr = connector.createBatchScanner(table, CBConstants.NO_AUTHS, numThreads);
		
		Random r = new Random();

		//do one cold
		doRandomQueries(num, min, max, expectedValueSize, r, tsbr);

		System.gc();
		System.gc();
		System.gc();
		
		//do one hot (connections already established, metadata table cached)
		doRandomQueries(num, min, max, expectedValueSize, r, tsbr);
		
		tsbr.close();
	}
}
