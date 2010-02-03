package cloudbase.core.util;

import java.io.IOException;

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


public class CountDiskRows {

	/**
	 * @param args
	 * @throws IOException 
	 */
	private static Logger log = Logger.getLogger(CountDiskRows.class.getName());
	
	public static void main(String[] args) throws IOException {
		if(args.length < 1) {
			log.error("usage: CountDiskRows tablename");
			return;
		}
		
		Configuration conf = new Configuration();
		FileSystem fs = FileSystem.get(conf);

		Key key = new Key();
		Value value = new Value();
		int numrows = 0;
		Text prevRow = new Text("");
		Text row = null;
		
		FileStatus[] tablets = fs.listStatus(new Path(CBConstants.TABLES_DIR + "/" + args[0]));
		for(FileStatus tablet : tablets) {
			FileStatus[] mapfiles = fs.listStatus(tablet.getPath());
			for(FileStatus mapfile : mapfiles) {
				MyMapFile.Reader mfr = new MyMapFile.Reader(fs, mapfile.getPath().toString(), conf);
				while(mfr.next(key, value)) {
					row = key.getRow();
					if(!row.equals(prevRow)) {
						prevRow = new Text(row);
						numrows++;
					}
				}	
			}
		}
		
		log.info("files in directory " + args[0] + " have " + numrows);
	}

}


