package cloudbase.examples.mapreduce.bulk;

import java.util.TreeSet;

import org.apache.hadoop.io.Text;

import cloudbase.core.client.CBException;
import cloudbase.core.client.CBSecurityException;
import cloudbase.core.client.Connector;
import cloudbase.core.client.MasterInstance;
import cloudbase.core.client.TableExistsException;

public class SetupTable {
	
	public static void main(String[] args) throws CBException, CBSecurityException, TableExistsException
	{
		if (args.length == 4)
		{
			// create a basic table
			new Connector(new MasterInstance(args[0]), args[1], args[2].getBytes()).tableOperations().create(args[3]);
		}
		else if (args.length > 4)
		{
			// create a table with initial partitions
			Connector conn = new Connector(new MasterInstance(args[0]), args[1], args[2].getBytes());
			
			TreeSet<Text> intialPartitions = new TreeSet<Text>();
			for (int i = 4; i < args.length; ++i)
				intialPartitions.add(new Text(args[i]));
		
			conn.tableOperations().create(args[3], intialPartitions);
		}
		else
		{
			System.err.println("Usage : SetupTable <master> <username> <password> <table name> [<split point>{ <split point}]");
		}
	}
}
