package cloudbase.core.test;

import java.util.TreeSet;
import java.util.Map.Entry;

import org.apache.hadoop.io.Text;

import cloudbase.core.CBConstants;
import cloudbase.core.client.BatchWriter;
import cloudbase.core.client.Connector;
import cloudbase.core.client.Instance;
import cloudbase.core.client.MasterInstance;
import cloudbase.core.client.Scanner;
import cloudbase.core.data.Key;
import cloudbase.core.data.Mutation;
import cloudbase.core.data.Range;
import cloudbase.core.data.Value;

public class ScanRangeTest {

	private static final int TS_LIMIT = 1;
	private static final int CQ_LIMIT = 5;
	private static final int CF_LIMIT = 5;
	private static final int ROW_LIMIT = 100;

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		String master = args[0];
		String tablePrefix = args[1];
		String username = "root";
		String password = "secret";
		
		String table1 = tablePrefix + "1";
		
		setupTable(master, username, password, table1, 0);
		insertData(master, username, password, table1);
		scanTable(master, username, password, table1);
		
		String table2 = tablePrefix + "2";
		
		setupTable(master, username, password, table2, 3);
		insertData(master, username, password, table2);
		scanTable(master, username, password, table2);
	}

	private static void scanTable(String master, String username, String password, String table) throws Exception {
		scanRange(master, username, password, table,new IntKey(0,0,0,0), new IntKey(1,0,0,0));
		
		scanRange(master, username, password, table,
				new IntKey(0,0,0,0), new IntKey(ROW_LIMIT - 1, CF_LIMIT - 1,CQ_LIMIT - 1,0));
		
		scanRange(master, username, password, table, null, null);
		
		for(int i = 0; i < ROW_LIMIT; i += (ROW_LIMIT / 3)){
			for(int j = 0; j < CF_LIMIT; j += (CF_LIMIT / 2)){
				for(int k = 1; k < CQ_LIMIT; k += (CQ_LIMIT / 2)){
					scanRange(master, username, password, table, null, new IntKey(i,j,k,0));
					scanRange(master, username, password, table, new IntKey(0,0,0,0), new IntKey(i,j,k,0));
					
					scanRange(master, username, password, table,
							new IntKey(i,j,k,0),
							new IntKey(ROW_LIMIT - 1,CF_LIMIT - 1,CQ_LIMIT - 1,0));
					
					scanRange(master, username, password, table, new IntKey(i,j,k,0),null);
					
				}
			}
		}
		
		for(int i = 0; i < ROW_LIMIT; i++){
			scanRange(master, username, password, table, new IntKey(i,0,0,0), new IntKey(i,CF_LIMIT - 1,CQ_LIMIT - 1,0));
			
			if(i > 0 && i < ROW_LIMIT - 1){
				scanRange(master, username, password, table, new IntKey(i-1,0,0,0), new IntKey(i+1,CF_LIMIT - 1,CQ_LIMIT - 1,0));
			}
		}
		
	}

	private static class IntKey {
		private int row;
		private int cf;
		private int cq;
		private long ts;

		IntKey(IntKey ik){
			this.row = ik.row;
			this.cf = ik.cf;
			this.cq = ik.cq;
			this.ts = ik.ts;
		}
		
		IntKey(int row, int cf, int cq, long ts){
			this.row = row;
			this.cf = cf;
			this.cq = cq;
			this.ts = ts;
		}
		
		Key createKey() {
			Text trow = createRow(row);
			Text tcf = createCF(cf);
			Text tcq = createCQ(cq);
			
			return new Key(trow, tcf, tcq, ts);
		}
		
		IntKey increment(){
			
			IntKey ik = new IntKey(this);
			
			ik.ts++;
			if(ik.ts >= TS_LIMIT){
				ik.ts = 0;
				ik.cq++;
				if(ik.cq >= CQ_LIMIT){
					ik.cq = 0;
					ik.cf++;
					if(ik.cf >= CF_LIMIT){
						ik.cf = 0;
						ik.row++;
					}
				}
			}
			
			return ik;
		}
		
	}
	
	
	private static void scanRange(String master, String username, String password, String table, IntKey ik1, IntKey ik2) throws Exception
	{
		scanRange(master, username, password, table, ik1, false, ik2, false);
		scanRange(master, username, password, table, ik1, false, ik2, true);
		scanRange(master, username, password, table, ik1, true, ik2, false);
		scanRange(master, username, password, table, ik1, true, ik2, true);
	}
	
	private static void scanRange(String master, String username, String password, String table,
									IntKey ik1, boolean inclusive1,
									IntKey ik2, boolean inclusive2)
	 								throws Exception
	{
		Connector connector = new Connector(master, username, password.getBytes());
		
		Scanner scanner = connector.createScanner(table, CBConstants.NO_AUTHS);
		
		Key key1 = null;
		Key key2 = null;
		
		IntKey expectedIntKey;
		IntKey expectedEndIntKey;
		
		if(ik1 != null){
			key1 = ik1.createKey();
			expectedIntKey = ik1;
			
			if(!inclusive1){
				expectedIntKey = expectedIntKey.increment();
			}
		}else{
			expectedIntKey = new IntKey(0,0,0,0);
		}
		
		if(ik2 != null){
			key2 = ik2.createKey();
			expectedEndIntKey = ik2;
			
			if(inclusive2){
				expectedEndIntKey = expectedEndIntKey.increment();
			}
		}else{
			expectedEndIntKey = new IntKey(ROW_LIMIT, 0, 0, 0);
		}
		
		Range range = new Range(key1, inclusive1, key2, inclusive2);
		
		scanner.setRange(range);
		
		for (Entry<Key, Value> entry : scanner) {
			//System.out.println(" key "+entry.getKey());
			
			Key expectedKey = expectedIntKey.createKey();
			if(!expectedKey.equals(entry.getKey())){
				throw new Exception(" "+expectedKey+" != "+entry.getKey());
			}
			
			expectedIntKey = expectedIntKey.increment();
		}
		
		//System.out.println(expectedIntKey.createKey());
		
		if(!expectedIntKey.createKey().equals(expectedEndIntKey.createKey())){
			throw new Exception(" "+expectedIntKey.createKey()+" != "+expectedEndIntKey.createKey());
		}
	}

	private static Text createCF(int cf) {
		Text tcf = new Text(String.format("cf_%03d", cf));
		return tcf;
	}
	
	private static Text createCQ(int cf) {
		Text tcf = new Text(String.format("cq_%03d", cf));
		return tcf;
	}

	private static Text createRow(int row) {
		Text trow = new Text(String.format("r_%06d", row));
		return trow;
	}

	private static void insertData(String master, String username, String password, String table) throws Exception {
		Connector connector = new Connector(master, username, password.getBytes());
		
		BatchWriter bw = connector.createBatchWriter(table, 10000000, 1000, 4);
		
		for(int i = 0; i < ROW_LIMIT; i++){
			Mutation m = new Mutation(createRow(i));
			
			for(int j = 0; j < CF_LIMIT; j++){
				for(int k = 0; k < CQ_LIMIT; k++){
					for(int t = 0; t < TS_LIMIT; t++){
						m.put(createCF(j), 
								createCQ(k), 
								t, 
								new Value(String.format("%06d_%03d_%03d_03d", i,j,k,t).getBytes()));
					}
				}	
			}
			
			bw.addMutation(m);
		}
		
		bw.close();
	}

	private static void setupTable(String master, String username, String password, String table, int splits) throws Exception {
		Instance instance = new MasterInstance(master);
		Connector conn = new Connector(instance, username, password.getBytes());
		
		if(splits == 0)
			conn.tableOperations().create(table);
		else
		{
			TreeSet<Text> splitRows = new TreeSet<Text>();
			for(int i = (ROW_LIMIT / splits); i < ROW_LIMIT; i += (ROW_LIMIT / splits))
				splitRows.add(createRow(i));
			
			conn.tableOperations().create(table, splitRows);
		}
	}

}
