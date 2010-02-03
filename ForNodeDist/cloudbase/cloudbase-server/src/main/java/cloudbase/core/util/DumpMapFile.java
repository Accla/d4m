package cloudbase.core.util;

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
		
		if(args.length < 1) {
			log.error("usage: DumpMapFile [-s] <map file>");
			return;
		}
		
		boolean summarize = false;
		int filenameIndex = 0;
		
		if(args[0].equals("-s")){
			summarize = true;
			filenameIndex++;
		}
		
		Configuration conf = new Configuration();
		FileSystem fs = FileSystem.get(conf);
		
		MyMapFile.Reader mr = new MyMapFile.Reader(fs, args[filenameIndex], conf);
		Key key = new Key();
		Value value = new Value();
		
		long count = 0;
		
		if(summarize){
			if(mr.next(key, value)){
				count++;
				log.info("first key : "+key);
			}
		}
		
		
		
		long start = System.currentTimeMillis();
		while(mr.next(key, value)) {
			if(!summarize){
				log.info(key + " -> " + value);
			}
			
			count++;
		}
		
		if(summarize && count > 0){
			log.info("last  key : "+key);
		}
		
		long stop = System.currentTimeMillis();
		
		if(summarize){
			System.out.printf("count     : %,d\n",count);
		}
		
		log.info("\nsecs      : "+(stop - start)/1000);
	}
}
