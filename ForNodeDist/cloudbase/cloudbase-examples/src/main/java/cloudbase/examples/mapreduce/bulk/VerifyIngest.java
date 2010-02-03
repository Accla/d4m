package cloudbase.examples.mapreduce.bulk;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map.Entry;

import org.apache.hadoop.io.Text;

import cloudbase.core.CBConstants;
import cloudbase.core.client.CBException;
import cloudbase.core.client.CBSecurityException;
import cloudbase.core.client.Connector;
import cloudbase.core.client.Scanner;
import cloudbase.core.client.TableNotFoundException;
import cloudbase.core.data.Key;
import cloudbase.core.data.Range;
import cloudbase.core.data.Value;

public class VerifyIngest {

	/**
	 * @param args
	 * @throws CBSecurityException 
	 * @throws CBException 
	 * @throws TableNotFoundException 
	 * @throws IOException 
	 */
	public static void main(String[] args) throws CBException, CBSecurityException, TableNotFoundException
	{
		if (args.length != 6)
		{
			System.err.println("VerifyIngest <master> <username> <password> <table> <startRow> <numRows> ");
			return;
		}
		
		String master = args[0];
		String user = args[1];
		byte[] pass = args[2].getBytes();
		String table = args[3];
		
		int startRow = Integer.parseInt(args[4]);
		int numRows = Integer.parseInt(args[5]);
		
		Connector connector = new Connector(master, user, pass);
		Scanner scanner = connector.createScanner(table, CBConstants.NO_AUTHS);
		
		scanner.setRange(new Range(new Text(String.format("row_%08d", startRow)), null));
		
		Iterator<Entry<Key, Value>> si = scanner.iterator();
		
		boolean ok = true;
		
		for(int i = startRow; i < numRows; i++){
			
			if (si.hasNext())
			{
				Entry<Key, Value> entry = si.next();
				
				if (!entry.getKey().getRow().toString().equals(String.format("row_%08d", i)))
				{
					System.out.println("ERROR unexpected row key "+entry.getKey().getRow().toString()+" expected "+String.format("row_%08d", i));
					ok = false;
				}
				
				if (!entry.getValue().toString().equals(String.format("value_%08d", i)))
				{
					System.out.println("ERROR unexpected value "+entry.getValue().toString()+" expected "+String.format("value_%08d", i));
					ok = false;
				}
				
			} else {
				System.out.println("ERROR no more rows, expected "+String.format("row_%08d", i));
				ok = false;
				break;
			}
			
		}

		if(ok)
			System.out.println("OK");

		System.exit(ok ? 0 : 1);
	}

}
