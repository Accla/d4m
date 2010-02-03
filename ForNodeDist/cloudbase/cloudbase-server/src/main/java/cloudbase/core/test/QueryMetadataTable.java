package cloudbase.core.test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.hadoop.io.Text;

import cloudbase.core.CBConstants;
import cloudbase.core.client.CBException;
import cloudbase.core.client.CBSecurityException;
import cloudbase.core.client.Connector;
import cloudbase.core.client.Scanner;
import cloudbase.core.client.TableNotFoundException;
import cloudbase.core.client.impl.HdfsZooInstance;
import cloudbase.core.data.Key;
import cloudbase.core.data.KeyExtent;
import cloudbase.core.data.Range;
import cloudbase.core.data.Value;
import cloudbase.core.security.thrift.AuthInfo;

public class QueryMetadataTable
{
	private static AuthInfo rootCredentials = new AuthInfo("root", "secret".getBytes());
	
	static String location;
	
	static class MDTQuery implements Runnable {
		private Text row;

		MDTQuery(Text row){
			this.row = row;
		}

		public void run() {
			
			
			//throw new RuntimeException("this test doesn't currently work");
			
			try {
				//System.out.print("Querying "+row+"...");
				
				
					KeyExtent extent = new KeyExtent(row, (Text)null);
					//SortedMap<String, Long> sizes = MetadataTable.getSSTableSizes(ke);
					
					//SortedMap<String, Long> sizes = new TreeMap<String, Long>();
					
					Connector connector = new Connector(new HdfsZooInstance(), rootCredentials.user, rootCredentials.password);
					Scanner mdScanner = connector.createScanner(CBConstants.METADATA_TABLE_NAME, CBConstants.NO_AUTHS);
					//mdScanner.fetchColumn(new Text(CBConstants.METADATA_TABLE_TABLET_SSTABLES_COLUMN_FAMILY + ":"));
					Text row = extent.getMetadataEntry();
					
					//Key endKey = new Key(row, new Text("zzzzzz"), new Text("zzzzzz"));
					//mdScanner.setEndKey(endKey);
					
					//mdScanner.setBatchSize(20);
					
					Text nextRow = new Text(row);
					nextRow.append(new byte[]{0}, 0, 1);
					mdScanner.setRange(new Range(row, nextRow));
					
					int count = 0;
					
					for (Entry<Key, Value> entry : mdScanner) {
						
						//System.out.println(entry.getKey()+".compareTo("+endKey+", 3) = "+entry.getKey().compareTo(endKey, 3));
						
						if(!entry.getKey().getRow().equals(row))
							break;
						//long size = Long.parseLong(entry.getValue().toString());
						
						//sizes.put(entry.getKey().getColumnQualifier().toString(), new Long(size));
						
						count++;
					}
					
					//System.out.println(sizes);				
					//System.out.println(" count = "+count);
				
				//System.out.println();
				
			} catch (TableNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new RuntimeException(e);
			} catch (CBException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new RuntimeException(e);
			} catch (CBSecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}
	}
	
	
	public static void main(String[] args) throws CBException, CBSecurityException, TableNotFoundException {
		
		if(args.length != 2){
			System.out.println("Usage : queryMetadataTable <numQueries> <numThreads>");
			System.exit(-1);
		}
		
		int numQueries = Integer.parseInt(args[0]);
		int numThreads = Integer.parseInt(args[1]);

		//System.out.println(KeyExtent.getMetadataEntry(new Text(CBConstants.METADATA_TABLE_NAME),null));
		Connector connector = new Connector(new HdfsZooInstance(), rootCredentials.user, rootCredentials.password);
        Scanner scanner = connector.createScanner(CBConstants.METADATA_TABLE_NAME, CBConstants.NO_AUTHS);
		scanner.setBatchSize(20000);
		Text mdrow = new Text(KeyExtent.getMetadataEntry(new Text(CBConstants.METADATA_TABLE_NAME), null));
		//scanner.lookUp(mdrow);
		
		
		HashSet<Text> rowSet = new HashSet<Text>();
		
		int count = 0;
		
		for (Entry<Key,Value> entry : scanner) {
			System.out.print(".");
			if(count % 72 == 0){
				System.out.printf(" %,d\n",count);
			}
			if(entry.getKey().compareRow(mdrow) == 0 &&
					entry.getKey().getColumnFamily().equals(CBConstants.METADATA_TABLE_TABLET_COLUMN_FAMILY) &&
					entry.getKey().getColumnQualifier().equals(CBConstants.METADATA_TABLE_TABLET_LOCATION_COLUMN_NAME)){
				System.out.println(entry.getKey()+" "+entry.getValue());
				location = entry.getValue().toString();
			}
			
			if(!entry.getKey().getRow().toString().startsWith("!METADATA"))
				rowSet.add(entry.getKey().getRow());
			count++;
			
			
		}
		
		System.out.printf(" %,d\n",count);
		
		ArrayList<Text> rows = new ArrayList<Text>(rowSet);
		
		Random r = new Random();
		
		ExecutorService tp = Executors.newFixedThreadPool(numThreads);
		
		long t1 = System.currentTimeMillis();
		
		for(int i = 0; i < numQueries; i++){
			int index = r.nextInt(rows.size());
			MDTQuery mdtq = new MDTQuery(rows.get(index));
			tp.submit(mdtq);
		}
		
		tp.shutdown();
		
		try {
			tp.awaitTermination(1, TimeUnit.HOURS);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		long t2 = System.currentTimeMillis();
		double delta = (t2 - t1) / 1000.0;
		System.out.println("time : "+delta+"  queries per sec : "+(numQueries/delta));
		
	}

}
