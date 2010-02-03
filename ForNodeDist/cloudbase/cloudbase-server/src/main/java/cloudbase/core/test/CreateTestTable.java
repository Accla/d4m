package cloudbase.core.test;

import java.util.TreeSet;
import java.util.Map.Entry;

import org.apache.hadoop.io.Text;

import cloudbase.core.CBConstants;
import cloudbase.core.client.BatchWriter;
import cloudbase.core.client.Connector;
import cloudbase.core.client.Scanner;
import cloudbase.core.client.impl.HdfsZooInstance;
import cloudbase.core.data.Key;
import cloudbase.core.data.Mutation;
import cloudbase.core.data.Value;

public class CreateTestTable
{
	// root user is needed for tests
	private static String rootUser = "root";
	private static String rootPass = "secret";
	
	private static void readBack(Connector conn, int last) throws Exception {
		Scanner scanner = conn.createScanner("mrtest1", CBConstants.NO_AUTHS);
		int count = 0;
		for (Entry<Key, Value> elt : scanner) {
			String expected = String.format("%05d", count);
			assert(elt.getKey().getRow().toString().equals(expected));
			count++;
		}
		assert(last == count);
	}
	
	
	public static void main(String[] args) throws Exception {
		// create the test table within cloudbase
		boolean readOnly = false;
		int count = 10000;
		String table = "mrtest1";
		Connector connector = new Connector(new HdfsZooInstance(), rootUser, rootPass.getBytes());
		for (String arg: args) {
			if (arg.toLowerCase().equals("-readonly")) {
				readOnly = true;
			} else {
				count = Integer.parseInt(arg);
			}
		}
		if (!readOnly) {
			TreeSet<Text> keys = new TreeSet<Text>();
			for(int i=0; i < count / 100; i++) {
				keys.add(new Text(String.format("%05d",i*100)));
			}

			// presplit
			connector.tableOperations().create(table, keys);
			
			BatchWriter b = connector.createBatchWriter(table, 10000000, 1000000, 10);
			// populate
			for(int i=0; i < count; i++) {
				Mutation m = new Mutation(new Text(String.format("%05d", i)));
				m.put(new Text("col" + Integer.toString((i % 3) + 1)), new Text("qual"), new Value("junk".getBytes()));
				b.addMutation(m);
			}
			b.close();
		}
		readBack(connector, count);
	}
}

