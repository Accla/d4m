package cloudbase.core.test;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import cloudbase.core.data.MyMapFile;
import org.apache.log4j.Logger;

import cloudbase.core.data.Value;
import cloudbase.core.data.Key;



public class DumpMapFile {
	private static Logger log = Logger.getLogger(DumpMapFile.class.getName());
	
	public static void main(String[] args) throws IOException {
		Configuration conf = new Configuration();
		FileSystem fs = FileSystem.get(conf);
		
		MyMapFile.Reader mr = new MyMapFile.Reader(fs, args[0], conf);
		Key key = new Key();
		Value value = new Value();
		
		int i = 0;
		long start = System.currentTimeMillis();
		while(mr.next(key, value)) {
			log.info(key + " -> " + value);
			i++;
		}
		long stop = System.currentTimeMillis();
		log.info(stop - start);
	}
}
