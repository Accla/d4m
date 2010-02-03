package cloudbase.core.util;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;

import cloudbase.core.CBConstants;
import cloudbase.core.data.Key;
import cloudbase.core.data.KeyExtent;
import cloudbase.core.data.MyMapFile;
import cloudbase.core.data.Value;
import cloudbase.core.data.MySequenceFile.CompressionType;
import cloudbase.core.tabletserver.iterators.IteratorUtil;
import cloudbase.core.util.MetadataTable.SSTableValue;

public class UpgradeMetadataTable {

	static class AggregatorInfo {
		long timestamp;
		byte serializedAggInfo[];
	}
	
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws Exception {
		
		HashSet<String> tables = new HashSet<String>();
		HashMap<String, AggregatorInfo> aggregators = new HashMap<String, AggregatorInfo>();
		
		String inputDir = args[0];
		String outputDir = args[1];
		
		Configuration conf = new Configuration();
		FileSystem fs = FileSystem.get(conf);
		
		conf.setInt("io.seqfile.compress.blocksize", 32000);
		
		FileStatus[] tablets = fs.listStatus(new Path(inputDir));
		
		for (FileStatus tabletStatus : tablets) {
			FileStatus[] mapFiles = fs.listStatus(tabletStatus.getPath());
			
			fs.mkdirs(new Path(outputDir+"/"+tabletStatus.getPath().getName()));
			
			for (FileStatus fileStatus : mapFiles) {
				
				String inDir = fileStatus.getPath().toString();
				String outDir = outputDir+"/"+tabletStatus.getPath().getName()+"/"+fileStatus.getPath().getName();
				
				System.out.println("Converting "+inDir+" to "+outDir);
				
				MyMapFile.Reader in = new MyMapFile.Reader(fs, inDir, conf);
				
				MyMapFile.Writer out = new MyMapFile.Writer(conf, fs, outDir, Key.class, Value.class, CompressionType.BLOCK);
				
				Key key = new Key();
				Value val = new Value();
				
				while(in.next(key, val)){
					
					boolean processed = false;
					
					KeyExtent ke = new KeyExtent(key.getRow(), (Text)null);
					
					if(ke.getTableName().equals(new Text("!USERS"))){
						continue;
					}
					
					tables.add(ke.getTableName().toString());
					
					if(key.getColumnFamily().equals(CBConstants.METADATA_TABLE_TABLET_SSTABLES_COLUMN_FAMILY)){
						if(key.isDeleted()){
							out.append(key, val);
						}else{
							long size = Long.parseLong(val.toString());
							SSTableValue sstv = new SSTableValue(size, 0);
						
							out.append(key, new Value(sstv.encode()));
						}
						
						processed = true;
					}else if(key.getColumnFamily().equals(CBConstants.METADATA_TABLE_TABLET_COLUMN_FAMILY)){
						if(key.getColumnQualifier().equals(CBConstants.METADATA_TABLE_TABLET_DIRECTORY_COLUMN_NAME)){
							out.append(key, val);
							processed = true;
						}else if(key.getColumnQualifier().equals(CBConstants.METADATA_TABLE_TABLET_LOCATION_COLUMN_NAME)){
							processed = true;
						}else if(key.getColumnQualifier().equals(CBConstants.METADATA_TABLE_TABLET_PREV_ROW_COLUMN_NAME)){
							out.append(key, val);
							processed = true;
						}
					}else if(key.getColumnFamily().toString().equals("conf") && 
							 key.getColumnQualifier().toString().equals("aggregators")){
						if(key.isDeleted()){
							out.append(key, val);
						}else{
							AggregatorInfo aggInfo = aggregators.get(ke.getTableName().toString());
							if(aggInfo == null || aggInfo.timestamp < key.getTimestamp()){
								aggInfo = new AggregatorInfo();
								aggInfo.timestamp = key.getTimestamp();

								aggInfo.serializedAggInfo = new byte[val.get().length];
								System.arraycopy(val.get(), 0, aggInfo.serializedAggInfo, 0, val.get().length);

								aggregators.put(ke.getTableName().toString(), aggInfo);

							}
						}
						processed = true;
					}
					
					if(!processed){
						System.err.println("Ignoring !METADATA entry "+key+" -> "+val);
					}
				}
				
				in.close();
				out.close();
			}
		}
		
		setupPerTableProperties(tables, aggregators);
		
	}

	private static void setupPerTableProperties(HashSet<String> tables, HashMap<String, AggregatorInfo> aggregators) throws Exception {
		
		System.out.println("\n Setting up per table properties in zookeeper.");
		
		boolean sawAgg = false;
		
		for (String table : tables) {
			Map<Text, String> aggs = new HashMap<Text, String>();
			AggregatorInfo aggInfo = aggregators.get(table);
			if(aggInfo != null){
				ByteArrayInputStream bais = new ByteArrayInputStream(aggInfo.serializedAggInfo);
				DataInputStream dis = new DataInputStream(bais);
				
				int num = dis.readInt();
				
				for(int i = 0; i< num; i++){
					Text col = new Text();
					col.readFields(dis);
					String className = dis.readUTF();
					
					aggs.put(col, className);
					
					System.out.println("  Aggregator  table="+table+" col="+col+"  class="+className);
				}
				
				sawAgg = true;
			}
			
			Map<String, String> props = IteratorUtil.generateInitialTableProperties(aggs);
			
			for (Entry<String, String> entry : props.entrySet()) {
				TablePropUtil.setTableProperty(table, entry.getKey(), entry.getValue());
			}
			
		}
		
		if(sawAgg){
			System.out.println();
			System.out.println("************* WARN ********* WARN **********");
			System.out.println("Some tables had aggregators. Must recompile");
			System.out.println("custom 0.5 aggregators against 1.0 API and ");
			System.out.println("place jar w/ custom aggs in lib dir. Do this");
			System.out.println("before starting upgraded 1.0 instance.");
			System.out.println("************* WARN ********* WARN **********");
			System.out.println();
		}
	}

}
