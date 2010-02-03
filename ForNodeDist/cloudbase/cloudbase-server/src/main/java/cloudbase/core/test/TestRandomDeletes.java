
package cloudbase.core.test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.Map.Entry;

import org.apache.hadoop.io.Text;
import org.apache.log4j.Logger;

import cloudbase.core.client.BatchWriter;
import cloudbase.core.client.Connector;
import cloudbase.core.client.Scanner;
import cloudbase.core.client.impl.HdfsZooInstance;
import cloudbase.core.client.impl.ScannerImpl;
import cloudbase.core.client.proxy.ClientProxy;
import cloudbase.core.data.Column;
import cloudbase.core.data.Key;
import cloudbase.core.data.Mutation;
import cloudbase.core.data.Value;
import cloudbase.core.security.LabelConversions;
import cloudbase.core.security.LabelExpression;
import cloudbase.core.security.thrift.AuthInfo;


public class TestRandomDeletes {
    private static Logger log = Logger.getLogger(TestRandomDeletes.class.getName());
	private static Set<Short> labels = new HashSet<Short>();
	private static short[] myauths = new short[]{1,2,3,4};
	
	private static AuthInfo rootCredentials = new AuthInfo("root", "secret".getBytes());

	static private class RowColumn implements Comparable<RowColumn> {
		Text row;
		Column column;
		long timestamp;
		
	        public RowColumn(Text row, Column column, long timestamp) {
			this.row = row;
			this.column = column;
			this.timestamp = timestamp;
		}
		public int compareTo(RowColumn other){
			int result = row.compareTo(other.row);
			if (result != 0)
				return result;
			return column.compareTo(other.column);
		}
		public String toString() {
			return row.toString() + ":" + column.toString();
		}
	}
	
	
    private static TreeSet<RowColumn> scanAll(Text t) throws Exception {
    	for (short s : myauths)
    		labels.add(s);
    	
    	TreeSet<RowColumn> result = new TreeSet<RowColumn>();
    	Scanner scanner = new ScannerImpl(new HdfsZooInstance(), rootCredentials, t.toString(), labels);
    	scanner.setBatchSize(20000);
    	for (Entry<Key, Value> entry : scanner) {
    		cloudbase.core.client.proxy.thrift.Key key = ClientProxy.getKey(entry.getKey());
    		Column column = new Column(key.column.columnFamily,
    		                           key.column.columnQualifier,
    		                           LabelConversions.formatAuthorizations(key.column.columnVisibility));
    		result.add(new RowColumn(new Text(key.row), column, entry.getKey().getTimestamp()));
    	}
    	return result;
    }

    
    private static long scrambleDeleteHalfAndCheck(Text t, Set<RowColumn> rows) throws Exception {
    	for (short s : myauths)
    		labels.add(s);
    	int result = 0;
    	ArrayList<RowColumn> entries = new ArrayList<RowColumn>(rows);
    	java.util.Collections.shuffle(entries);
    	
    	Connector connector = new Connector(new HdfsZooInstance(), rootCredentials.user, rootCredentials.password);
    	BatchWriter mutations = connector.createBatchWriter(t.toString(), 10000, 10000, 4);
    	
    	LabelExpression le = new LabelExpression(myauths);
    	
    	for (int i = 0; i < (entries.size() + 1) / 2; i++) {
    		RowColumn rc = entries.get(i);
    		Mutation m = new Mutation(rc.row);
    		m.remove(new Text(rc.column.columnFamily), new Text(rc.column.columnQualifier), le, rc.timestamp + 1);
    		mutations.addMutation(m);
    		rows.remove(rc);
    		result++;
    	}
    	
    	mutations.close();
    	
    	Set<RowColumn> current = scanAll(t);
    	current.removeAll(rows);
    	if (current.size() > 0) {
    		throw new RuntimeException(current.size() + " records not deleted");
    	}
    	return result;
    }
	
    public static void main(String[] args) throws Exception {
    	long deleted = 0;

    	Text t = new Text("test_ingest");

    	TreeSet<RowColumn> doomed = scanAll(t);
    	log.info("Got " + doomed.size() + " rows");

    	long startTime = System.currentTimeMillis();
    	while (true) {
    	    long half = scrambleDeleteHalfAndCheck(t, doomed);
    		deleted += half;
    		if (half == 0) break;
    	}
    	long stopTime = System.currentTimeMillis();

    	long elapsed = (stopTime - startTime) / 1000;
    	log.info("deleted " + deleted + " values in " + elapsed + " seconds");
    	System.exit(0);
    }
}
