package cloudbase.core.test;

import java.util.Iterator;
import java.util.Random;
import java.util.TreeSet;
import java.util.Map.Entry;

import org.apache.hadoop.io.Text;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import cloudbase.core.CBConstants;
import cloudbase.core.client.BatchWriter;
import cloudbase.core.client.CBException;
import cloudbase.core.client.CBSecurityException;
import cloudbase.core.client.Connector;
import cloudbase.core.client.MutationsRejectedException;
import cloudbase.core.client.Scanner;
import cloudbase.core.client.TableExistsException;
import cloudbase.core.client.TableNotFoundException;
import cloudbase.core.client.impl.HdfsZooInstance;
import cloudbase.core.data.Key;
import cloudbase.core.data.Mutation;
import cloudbase.core.data.Range;
import cloudbase.core.data.Value;
import cloudbase.core.security.thrift.AuthInfo;

public class TestBinaryRows
{
	private static AuthInfo rootCredentials = new AuthInfo("root", "secret".getBytes());
	
	static byte[] encodeLong(long l){
		byte[] ba = new byte[8];
		
		ba[0] = (byte)(l >>> 56);
        ba[1] = (byte)(l >>> 48);
        ba[2] = (byte)(l >>> 40);
        ba[3] = (byte)(l >>> 32);
        ba[4] = (byte)(l >>> 24);
        ba[5] = (byte)(l >>> 16);
        ba[6] = (byte)(l >>>  8);
        ba[7] = (byte)(l >>>  0);
        
        return ba;
	}
	
	static long decodeLong(byte ba[]){
		return (((long)ba[0] << 56) +
                ((long)(ba[1] & 255) << 48) +
                ((long)(ba[2] & 255) << 40) +
                ((long)(ba[3] & 255) << 32) +
                ((long)(ba[4] & 255) << 24) +
                ((ba[5] & 255) << 16) +
                ((ba[6] & 255) <<  8) +
                ((ba[7] & 255) <<  0));
	}
	
	public static void main(String[] args)
	throws CBException, CBSecurityException, TableNotFoundException, MutationsRejectedException, TableExistsException
	{
		String mode = args[0];
		
		Connector connector = new Connector(new HdfsZooInstance(), rootCredentials.user, rootCredentials.password);

		ConsoleAppender ca = new ConsoleAppender();
		ca.setThreshold(Level.DEBUG);
		Logger.getLogger("cloudbase.core").addAppender(ca);
		Logger.getLogger("cloudbase.core").setLevel(Level.DEBUG);
		
		if(mode.equals("ingest") || mode.equals("delete")){
			String table = args[1];
			long start = Long.parseLong(args[2]);
			long num = Long.parseLong(args[3]);
			
			BatchWriter bw = connector.createBatchWriter(table, 20000000, 60, 8);
			boolean delete = mode.equals("delete");
			
			for(long i = 0; i < num; i++){
				byte[] row = encodeLong(i + start);
				String value = ""+(i+start);
			
				Mutation m = new Mutation(new Text(row));
				if(delete){
					m.remove(new Text("cf"), new Text("cq"));
				}else{
					m.put(new Text("cf"), new Text("cq"), new Value(value.getBytes()));
				}
				bw.addMutation(m);
			}
		
			bw.close();
		}else if(mode.equals("verifyDeleted")){
			String table = args[1];
			long start = Long.parseLong(args[2]);
			long num = Long.parseLong(args[3]);
			
			Scanner s = connector.createScanner(table, CBConstants.NO_AUTHS);
			Key startKey = new Key(encodeLong(start), "cf".getBytes(), "cq".getBytes(), new byte[0], Long.MAX_VALUE);
			Key stopKey = new Key(encodeLong(start+num -  1), "cf".getBytes(), "cq".getBytes(), new byte[0], 0);
			s.setBatchSize(50000);
			s.setRange(new Range(startKey, stopKey));
			
			for (Entry<Key, Value> entry : s) {
				System.err.println("ERROR : saw entries in range that should be deleted ( first value : "+entry.getValue().toString()+")");
				System.err.println("exiting...");
				System.exit(-1);
			}
			
		}else if(mode.equals("verify")){
			
			String table = args[1];
			long start = Long.parseLong(args[2]);
			long num = Long.parseLong(args[3]);
			
			long t1 = System.currentTimeMillis();
			
			Scanner s = connector.createScanner(table, CBConstants.NO_AUTHS);
			Key startKey = new Key(encodeLong(start), "cf".getBytes(), "cq".getBytes(), new byte[0], Long.MAX_VALUE);
			Key stopKey = new Key(encodeLong(start+num -  1), "cf".getBytes(), "cq".getBytes(), new byte[0], 0);
			s.setBatchSize(50000);
			s.setRange(new Range(startKey, stopKey));
			
			long i = start;
			
			for (Entry<Key, Value> e : s) {
				Key k = e.getKey();
				Value v = e.getValue();
				
				//System.out.println("v = "+v);
				
				checkKeyValue(i, k, v);
				
				i++;
			}
			
			if(i != start + num){
				System.err.println("ERROR : did not see expected number of rows, saw "+(i-start)+" expected "+num);
				System.err.println("exiting... ARGHHHHHH");
				System.exit(-1);
			}
			
			long t2 = System.currentTimeMillis();
			
			System.out.printf("time : %9.2f secs\n", ((t2 - t1) / 1000.0) );
			System.out.printf("rate : %9.2f entries/sec\n", num / ((t2 - t1) / 1000.0) );
			
		}else if(mode.equals("randomLookups")){
			
			String table = args[1];
			long start = Long.parseLong(args[2]);
			long num = Long.parseLong(args[3]);
			
			int numLookups = 1000;
			
			Random r = new Random();
			
			long t1 = System.currentTimeMillis();
			
			for(int i = 0; i < numLookups; i++){
				long row = (Math.abs(r.nextLong()) % num) + start;
				
				Scanner s = connector.createScanner(table, CBConstants.NO_AUTHS);
				Key startKey = new Key(encodeLong(row), "cf".getBytes(), "cq".getBytes(), new byte[0], Long.MAX_VALUE);
				Key stopKey = new Key(encodeLong(row), "cf".getBytes(), "cq".getBytes(), new byte[0], 0);
				s.setRange(new Range(startKey, stopKey));
				
				Iterator<Entry<Key, Value>> si = s.iterator();
				
				if(si.hasNext()){
					Entry<Key, Value> e = si.next();
					Key k = e.getKey();
					Value v = e.getValue();
					
					checkKeyValue(row, k, v);
					
					if(si.hasNext()){
						System.err.println("ERROR : lookup on "+row+" returned more than one result ");
						System.err.println("exiting...");
						System.exit(-1);
					}
					
				}else{
					System.err.println("ERROR : lookup on "+row+" failed ");
					System.err.println("exiting...");
					System.exit(-1);
				}
			}
			
			long t2 = System.currentTimeMillis();
			
			System.out.printf("time    : %9.2f secs\n", ((t2 - t1) / 1000.0) );
			System.out.printf("lookups : %9d keys\n", numLookups );
			System.out.printf("rate    : %9.2f lookups/sec\n", numLookups / ((t2 - t1) / 1000.0) );
			
		}else if(mode.equals("split")){
			
			String table = args[1];
			int shift = Integer.parseInt(args[2]);
			int count = Integer.parseInt(args[3]);
			
			TreeSet<Text> splits = new TreeSet<Text>();
			
			for(long i = 0; i < count; i++){
				long splitPoint = i << shift;
				
				splits.add(new Text(encodeLong(splitPoint)));
				System.out.printf("added split point 0x%016x  %,12d\n", splitPoint, splitPoint);
			}
			
			connector.tableOperations().create(table, splits);
			
		}else{
			System.err.println("ERROR : "+mode+" is not a valid operation.");
			System.exit(-1);
		}
	}

	private static void checkKeyValue(long expected, Key k, Value v) {
		if(expected != decodeLong(k.getRow().getBytes())){
			System.err.println("ERROR : expected row "+expected+" saw "+decodeLong(k.getRow().getBytes()));
			System.err.println("exiting...");
			System.exit(-1);
		}
		
		if(!v.toString().equals(""+expected)){
			System.err.println("ERROR : expected value "+expected+" saw "+v.toString());
			System.err.println("exiting...");
			System.exit(-1);
		}
	}
}
