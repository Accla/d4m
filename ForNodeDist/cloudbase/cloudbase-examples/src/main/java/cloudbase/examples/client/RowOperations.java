package cloudbase.examples.client;

import java.io.IOException;
import java.util.Map.Entry;

import org.apache.hadoop.io.Text;

import cloudbase.core.CBConstants;
import cloudbase.core.client.BatchWriter;
import cloudbase.core.client.CBException;
import cloudbase.core.client.CBSecurityException;
import cloudbase.core.client.Connector;
import cloudbase.core.client.MasterInstance;
import cloudbase.core.client.MutationsRejectedException;
import cloudbase.core.client.Scanner;
import cloudbase.core.client.TableExistsException;
import cloudbase.core.client.TableNotFoundException;
import cloudbase.core.data.Key;
import cloudbase.core.data.Mutation;
import cloudbase.core.data.Range;
import cloudbase.core.data.Value;


public class RowOperations {

	private static Connector connector;
	private static String table = "example";
	private static BatchWriter bw;
	
	public static void main(String[] args) throws CBException, CBSecurityException, TableExistsException, TableNotFoundException, MutationsRejectedException
	{
		if (args.length != 3)
		{
			System.out.println("Usage: <masterAddress> <username> <password>");
			return;
		}

		// First the setup work
		connector = new Connector(new MasterInstance(args[0]), args[1], args[2].getBytes());
		
		//lets create an example table
		connector.tableOperations().create(table);
		
		// lets create 3 rows of information
		Text row1 = new Text("row1");
		Text row2 = new Text("row2");
		Text row3 = new Text("row3");
		
		// Which means 3 different mutations
		Mutation mut1 = new Mutation(row1);
		Mutation mut2 = new Mutation(row2);
		Mutation mut3 = new Mutation(row3);
		
		// And we'll put 4 columns in each row
		Text col1 = new Text("1");
		Text col2 = new Text("2");
		Text col3 = new Text("3");
		Text col4 = new Text("4");

		// Now we'll add them to the mutations
		mut1.put(new Text("column") , col1, System.currentTimeMillis(), new Value("This is the value for this key".getBytes()));
		mut1.put(new Text("column") , col2, System.currentTimeMillis(), new Value("This is the value for this key".getBytes()));
		mut1.put(new Text("column") , col3, System.currentTimeMillis(), new Value("This is the value for this key".getBytes()));
		mut1.put(new Text("column") , col4, System.currentTimeMillis(), new Value("This is the value for this key".getBytes()));

		mut2.put(new Text("column") , col1, System.currentTimeMillis(), new Value("This is the value for this key".getBytes()));
		mut2.put(new Text("column") , col2, System.currentTimeMillis(), new Value("This is the value for this key".getBytes()));
		mut2.put(new Text("column") , col3, System.currentTimeMillis(), new Value("This is the value for this key".getBytes()));
		mut2.put(new Text("column") , col4, System.currentTimeMillis(), new Value("This is the value for this key".getBytes()));

		mut3.put(new Text("column") , col1, System.currentTimeMillis(), new Value("This is the value for this key".getBytes()));
		mut3.put(new Text("column") , col2, System.currentTimeMillis(), new Value("This is the value for this key".getBytes()));
		mut3.put(new Text("column") , col3, System.currentTimeMillis(), new Value("This is the value for this key".getBytes()));
		mut3.put(new Text("column") , col4, System.currentTimeMillis(), new Value("This is the value for this key".getBytes()));
		
		// Now we'll make a Batch Writer
		bw = connector.createBatchWriter(table, 100000, 30, 1);

		// And add the mutations
		bw.addMutation(mut1);
		bw.addMutation(mut2);
		bw.addMutation(mut3);
		
		// Force a send
		bw.flush();
		
		// Now lets look at the rows
		Scanner rowThree = getRow(new Text("row3"));
		Scanner rowTwo = getRow(new Text("row2"));
		Scanner rowOne = getRow(new Text("row1"));
		
		// And print them
		System.out.println("This is everything");
		printRow(rowOne);
		printRow(rowTwo);
		printRow(rowThree);
		System.out.flush();
		
		// Now lets delete rowTwo with the iterator
		rowTwo = getRow(new Text("row2"));
		deleteRow(rowTwo);
		
		// Now lets look at the rows again
		rowThree = getRow(new Text("row3"));
		rowTwo = getRow(new Text("row2"));
		rowOne = getRow(new Text("row1"));
		
		// And print them
		System.out.println("This is row1 and row3");
		printRow(rowOne);
		printRow(rowTwo);
		printRow(rowThree);
		System.out.flush();
		
		// Should only see the two rows
		// Now lets delete rowOne without passing in the iterator
		
		deleteRow(row1);
		
		// Now lets look at the rows one last time
		rowThree = getRow(new Text("row3"));
		rowTwo = getRow(new Text("row2"));
		rowOne = getRow(new Text("row1"));
		
		// And print them
		System.out.println("This is just row3");
		printRow(rowOne);
		printRow(rowTwo);
		printRow(rowThree);
		System.out.flush();
		
		// Should only see rowThree
		
		// Always close your batchwriter

		bw.close();

		// and lets clean up our mess
		connector.tableOperations().delete(table);
		
		// fin~

	}

	/**
	 * Deletes a row given a text object
	 * 
	 * @param row
	 * @throws TableNotFoundException 
	 * @throws CBSecurityException 
	 * @throws CBException 
	 */
	private static void deleteRow(Text row) throws CBException, CBSecurityException, TableNotFoundException
	{
		deleteRow(getRow(row));
	}

	/**
	 * Deletes a row, given a Scanner of JUST that row
	 * 
	 * @param scanner
	 */
	private static void deleteRow(Scanner scanner) {
		Mutation deleter = null;
		// iterate through the keys
		for (Entry<Key,Value> entry : scanner) {
			// create a mutation for the row
			if (deleter == null)
				deleter = new Mutation(entry.getKey().getRow());
			// the remove function adds the key with the delete flag set to true
			deleter.remove(entry.getKey().getColumnFamily(), entry.getKey().getColumnQualifier());
		}
		bw.addMutation(deleter);
		bw.flush();
	}

	/**
	 * Just a generic print function given an iterator.  Not necessarily just for printing a single row
	 * 
	 * @param scanner
	 */
	private static void printRow(Scanner scanner)
	{
		// iterates through and prints
		for (Entry<Key, Value> entry : scanner)
			System.out.println("Key: " + entry.getKey().toString() + " Value: " + entry.getValue().toString());
	}

	/**
	 * Gets a scanner from CB over one row
	 * 
	 * @param row
	 * @return
	 * @throws TableNotFoundException 
	 * @throws CBSecurityException 
	 * @throws CBException 
	 * @throws IOException
	 */
	private static Scanner getRow(Text row) throws CBException, CBSecurityException, TableNotFoundException
	{
		// Create a scanner
		Scanner scanner = connector.createScanner(table, CBConstants.NO_AUTHS);
		Key key = new Key(row);
		// Say start key is the one with key of row
		// and end key is the one that immediately follows the row
		scanner.setRange(new Range(key, true, key.followingKey(1), false));
		return scanner;
	}

}
