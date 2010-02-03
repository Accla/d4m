package cloudbase.core.util;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import cloudbase.core.data.MyMapFile;
import org.apache.log4j.Logger;

import cloudbase.core.CBConstants;
import cloudbase.core.data.Value;
import cloudbase.core.data.Key;

public class DumpTable {
	private static Logger log = Logger.getLogger(DumpTable.class.getName());
	
	public static void main(String[] args) throws IOException {
		
		if(args.length < 1) {
			log.error("usage: DumpTable [-s] <table name>");
			return;
		}
		
		boolean summarize = false;
		int tablenameIndex= 0;
		
		if(args[0].equals("-s")){
			summarize = true;
			tablenameIndex++;
			//System.out.println();
		}
		
		Configuration conf = new Configuration();
		FileSystem fs = FileSystem.get(conf);

		long totalCount = 0;
		Key min = null;
		Key max = null;
		
		Key key = new Key();
		Value value = new Value();
		
		FileStatus[] tablets = fs.listStatus(new Path(CBConstants.TABLES_DIR + "/" +  args[tablenameIndex]));
		for(FileStatus tablet : tablets) {
			FileStatus[] mapfiles = fs.listStatus(tablet.getPath());
			
			Key tabletMin = null;
			Key tabletMax = null;
			long tabletCount = 0;
			
			for(FileStatus mapfile : mapfiles) {
				MyMapFile.Reader mfr = new MyMapFile.Reader(fs, mapfile.getPath().toString(), conf);
				
				long count = 0;
				
				if(summarize){
					if(mfr.next(key, value)){
						if(min == null || min.compareTo(key) > 0){
							min = new Key(key);
						}
						if(tabletMin == null || tabletMin.compareTo(key) > 0){
							tabletMin = new Key(key);
						}
						count++;
						System.out.printf("(%20s, ",""+key);
						
					}
				}
				
				while(mfr.next(key, value)) {
					if(!summarize)
						log.info(key.getRow());
					else
						count++;
				}
				
				if(summarize){
					if(count > 0){
						if(max == null || max.compareTo(key) < 0){
							max = new Key(key);
						}
						if(tabletMax == null || tabletMax.compareTo(key) < 0){
							tabletMax = new Key(key);
						}
						System.out.printf("%20s) ",""+key);
					}
					
					totalCount += count;
					tabletCount += count;
					System.out.printf("%,10d ",count);
					log.info(args[tablenameIndex]+"/"+tablet.getPath().getName()+"/"+mapfile.getPath().getName());
				}
			}
			if(summarize){
				System.out.printf("(%20s, %20s) %,10d %s\n\n",""+tabletMin,""+tabletMax, tabletCount, args[tablenameIndex]+"/"+tablet.getPath().getName());
			}
		}
		
		System.out.printf("(%20s, %20s) %,10d %s\n",""+min,""+max, totalCount, args[tablenameIndex]);
	}
}
