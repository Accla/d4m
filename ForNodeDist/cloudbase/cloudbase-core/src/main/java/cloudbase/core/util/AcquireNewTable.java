package cloudbase.core.util;

import java.io.IOException;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import cloudbase.core.data.MyMapFile;
import org.apache.hadoop.io.Text;
import org.apache.log4j.Logger;

import cloudbase.core.CBConstants;
import cloudbase.core.data.Value;
import cloudbase.core.data.Key;
import cloudbase.core.data.KeyExtent;

/**
 * create a new default tablet
 * for the metadata table
 * 
 * This assumes that all tables have only one mapfile - not really generally useful
 * 
 *
 */
public class AcquireNewTable {
	private static Logger log = Logger.getLogger(AcquireNewTable.class.getName());
	
	public static void main(String[] args) throws IOException {
		Configuration conf = new Configuration();
		FileSystem fs = FileSystem.get(conf);
		FileStatus[] tables = fs.listStatus(new Path(CBConstants.TABLES_DIR));
		
		Path default_tablet = new Path(CBConstants.METADATA_TABLE_DIR + CBConstants.DEFAULT_TABLET_LOCATION);
		fs.delete(default_tablet, true);
		fs.mkdirs(default_tablet);
		
		MyMapFile.Writer mfw = new MyMapFile.Writer(conf, fs, CBConstants.METADATA_TABLE_DIR + CBConstants.DEFAULT_TABLET_LOCATION + "/map_00000_00000", Key.class, Value.class);
		
		TreeMap<KeyExtent, Value> data = new TreeMap<KeyExtent, Value>();
		
		log.info("scanning filesystem for tables ...");
		for(FileStatus table : tables) {
			log.info("found " + table.getPath().getName());
			if(table.getPath().getName().equals(CBConstants.METADATA_TABLE_NAME))
				continue;
			FileStatus[] tablets = fs.globStatus(new Path(table.getPath().toString() + "/*"));
			
			for(FileStatus tablet : tablets) {
				log.info("found tablet " + tablet.getPath().toUri().getPath().toString());
				Key endKey = new Key();

				FileStatus[] mapfiles = fs.listStatus(tablet.getPath());
				
				// just take the first
				if(mapfiles.length == 0) {
					log.warn("no mapfiles in " + tablet.getPath().toString() + ". skipping ...");
					continue;
				}
				try {
					MyMapFile.Reader mfr = new MyMapFile.Reader(fs, mapfiles[0].getPath().toString(), conf);
					mfr.finalKey(endKey);
					mfr.close();
				}
				catch(IOException ioe) {
					log.error("exception opening mapfile " + mapfiles[0] + ": " + ioe.getMessage() + ". skipping ...");
					continue;
				}
				
				KeyExtent extent;
				
				if(endKey.getRow().toString().equals("tablet")) {
					extent = new KeyExtent(new Text(table.getPath().getName()), null, null);
				}
				else {
					extent = new KeyExtent(new Text(table.getPath().getName()), endKey.getRow(), null);
				}
					
				Value value = new Value(tablet.getPath().toUri().getPath().toString().getBytes());
				
				data.put(extent, value);
			}
		}
		
		log.info("Found " + data.size() + " non-metadata tablets.\n Writing metadata table");
		
		// note: don't use the prev end row from the extent
		Text prevEndRow = null;
		for(Entry<KeyExtent, Value> entry : data.entrySet()) {
			Text endRow = entry.getKey().getEndRow();
			
			Key key = new Key(new Text(KeyExtent.getMetadataEntry(entry.getKey().getTableName(), endRow)), CBConstants.METADATA_TABLE_TABLET_COLUMN_FAMILY, CBConstants.METADATA_TABLE_TABLET_PREV_ROW_COLUMN_NAME);
			
			byte [] b;
			if(prevEndRow == null) {
				b = new byte[1];
				b[0] = 0;
			}
			else {
				b = new byte[prevEndRow.toString().getBytes().length + 1];
				b[0] = 1;
				for(int i = 1; i < b.length; i++) {
					b[i] = prevEndRow.toString().getBytes()[i-1];
				}
			}
			
			mfw.append(key, new Value(b));
			
			// write out the directory info
			key = new Key(new Text(KeyExtent.getMetadataEntry(entry.getKey().getTableName(), endRow)), CBConstants.METADATA_TABLE_TABLET_COLUMN_FAMILY, CBConstants.METADATA_TABLE_TABLET_DIRECTORY_COLUMN_NAME);
			mfw.append(key, entry.getValue());
			
			if(endRow == null)
				prevEndRow = null;
			else
				prevEndRow = new Text(endRow);
		}
		
		mfw.close();
		log.info("done.");
	}
}
