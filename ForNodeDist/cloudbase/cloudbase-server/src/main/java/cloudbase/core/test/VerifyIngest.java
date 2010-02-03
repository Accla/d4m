package cloudbase.core.test;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.hadoop.io.Text;
import org.apache.log4j.Logger;

import cloudbase.core.client.CBException;
import cloudbase.core.client.CBSecurityException;
import cloudbase.core.client.Connector;
import cloudbase.core.client.Scanner;
import cloudbase.core.client.TableNotFoundException;
import cloudbase.core.client.impl.HdfsZooInstance;
import cloudbase.core.data.Key;
import cloudbase.core.data.Range;
import cloudbase.core.data.Value;
import cloudbase.core.security.thrift.AuthInfo;
import cloudbase.core.test.TestIngest.IngestArgs;

public class VerifyIngest
{
	private static AuthInfo rootCredentials = new AuthInfo("root", "secret".getBytes());
	
	/**
	 * @param args
	 * @throws IOException 
	 * @throws EndOfTableException 
	 * @throws TableNotFoundException 
	 */
	
	private static Logger log = Logger.getLogger(VerifyIngest.class.getName());
	
	public static void main(String[] args) throws CBException, CBSecurityException, TableNotFoundException
	{
		Connector connector = null;
		while (connector == null)
		{
			try {
				connector = new Connector(new HdfsZooInstance(), rootCredentials.user, rootCredentials.password);
			} catch (CBException e) {
				log.warn("Could not connect to cloudbase; will retry: "+e);
			}
		}

		IngestArgs ingestArgs = TestIngest.parseArgs(args);
		
		byte[][] bytevals = TestIngest.generateValues(ingestArgs);
		
		short[] myauths = new short[]{1,2,3,4};
		Set<Short> labels = new HashSet<Short>();
		for (short s : myauths)
			labels.add(s);
		
		int expectedRow = ingestArgs.startRow;
		int expectedCol = 0;
		int recsRead = 0;
		
		long bytesRead = 0;
		long t1 = System.currentTimeMillis();
		
		byte randomValue[] = new byte[ingestArgs.dataSize];
		Random random = new Random();
		
		
		Key endKey = new Key(new Text("row_" + String.format("%010d", ingestArgs.rows + ingestArgs.startRow)));
		
	
		int errors = 0;
		
		
		while(expectedRow < (ingestArgs.rows + ingestArgs.startRow)){
		
			if(ingestArgs.useGet){
				Text rowKey = new Text("row_" + String.format("%010d", expectedRow + ingestArgs.startRow));
				Text colf = new Text("colf");
				Text colq = new Text("col_" + String.format("%05d", expectedCol));
				
				Scanner scanner = connector.createScanner("test_ingest", labels);
				//ScannerImpl scanner = new ScannerImpl(new HdfsZooInstance(), "test_ingest", new SecurityTuple(new short[]{1,2,3,4}));
				scanner.setBatchSize(1);
				Key startKey = new Key(rowKey, colf, colq);
				Range range = new Range(startKey, startKey.followingKey(3));
				scanner.setRange(range);
				
				
				
				byte[] val = null; //t.get(rowKey, column);
				
				Iterator<Entry<Key, Value>> si = scanner.iterator();
				
				if(si.hasNext()){
					val = si.next().getValue().get();
				}
				
				byte ev[];
				if(ingestArgs.random){
					ev = TestIngest.genRandomValue(random, randomValue, ingestArgs.seed, expectedRow, expectedCol);
				}else{
					ev = bytevals[expectedCol % bytevals.length];
				}
				
				if(val == null){
					log.error("Did not find "+rowKey+" "+colf+" "+colq);
					errors++;
				}else{
					recsRead++;
					bytesRead += val.length;
					Value value = new Value(val);
					if(value.compareTo(ev) != 0){
						log.error("unexpected value  ("+rowKey+" "+colf+" "+colq+" : saw "+value+" expected "+new Value(ev));
						errors++;
					}
				}
				
				expectedCol++;
				if(expectedCol >= ingestArgs.cols){
					expectedCol = 0;
					expectedRow++;
				}
				
			} else {
				
				int batchSize = 10000;
				
				Key startKey = new Key(new Text("row_" + String.format("%010d", expectedRow)));
				
                Scanner scanner = connector.createScanner("test_ingest", labels);
				scanner.setBatchSize(batchSize);
				scanner.setRange(new Range(startKey, endKey));
				for(int j =0; j < ingestArgs.cols; j++) {
					scanner.fetchColumn(new Text("colf"), new Text("col_" + String.format("%05d", j)));
				}

				int recsReadBefore = recsRead;
				
				for (Entry<Key, Value> entry : scanner) {
					
					recsRead++;
					
					bytesRead+=entry.getKey().getLength();
					bytesRead+=entry.getValue().getSize();

					int rowNum= Integer.parseInt(entry.getKey().getRow().toString().split("_")[1]);
					int colNum = Integer.parseInt(entry.getKey().getColumnQualifier().toString().split("_")[1]);

					if(rowNum != expectedRow){
						log.error("rowNum != expectedRow   "+rowNum+" != "+expectedRow);
						errors++;
						expectedRow = rowNum;
					}

					if(colNum != expectedCol){
						log.error("colNum != expectedCol  "+colNum +" != "+ expectedCol+"  rowNum : "+rowNum);
						errors++;
					}

					if(expectedRow >= (ingestArgs.rows + ingestArgs.startRow)){
						log.error("expectedRow ("+expectedRow+") >= (ingestArgs.rows + ingestArgs.startRow)  ("+(ingestArgs.rows + ingestArgs.startRow)+"), get batch returned data passed end key");
						errors++;
						break;
					}
					
					byte value[];
					if(ingestArgs.random){
						value = TestIngest.genRandomValue(random, randomValue, ingestArgs.seed, expectedRow, colNum);
					}else{
						value = bytevals[colNum % bytevals.length];
					}
					
					if(entry.getValue().compareTo(value) != 0){
						log.error("unexpected value, rowNum : "+rowNum+" colNum : "+colNum);
						log.error(" saw = "+new String(entry.getValue().get())+" expected = "+new String(value));
						errors++;
					}

					if(ingestArgs.hasTimestamp && entry.getKey().getTimestamp() != ingestArgs.timestamp){
						log.error("unexpected timestamp "+entry.getKey().getTimestamp()+", rowNum : "+rowNum+" colNum : "+colNum);
						errors++;
					}

					expectedCol++;
					if(expectedCol >= ingestArgs.cols){
						expectedCol = 0;
						expectedRow++;
					}

				}
				
				if(recsRead == recsReadBefore){
					log.warn("Scan returned nothing, breaking...");
					break;
				}
				
			}
		}
		
		long t2 = System.currentTimeMillis();
		
		if(errors > 0){
			log.error("saw "+errors+" errors ");
			System.exit(-1);
		}
		
		if(expectedRow != (ingestArgs.rows + ingestArgs.startRow)){
			log.error("Did not read expected number of rows. Saw "+(expectedRow - ingestArgs.startRow)+" expected "+ ingestArgs.rows);
			System.exit(-1);
		}else{
			System.out.printf("%,12d records read | %,8d records/sec | %,12d bytes read | %,8d bytes/sec | %6.3f secs   \n",recsRead, (int)((recsRead)/((t2 - t1)/1000.0)),bytesRead, (int)(bytesRead/((t2 - t1)/1000.0)), (t2 - t1)/1000.0 );
		}
		
		
	}

}
