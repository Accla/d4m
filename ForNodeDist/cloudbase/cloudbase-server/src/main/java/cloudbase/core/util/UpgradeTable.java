package cloudbase.core.util;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.log4j.Logger;

import cloudbase.core.CBConstants;

public class UpgradeTable {
	private static Logger log = Logger.getLogger(UpgradeTable.class.getName());
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		if(args.length < 1) {
			log.error("usage: upgradeTable <table name>");
			return;
		}
		
		int tablenameIndex= 0;
		
		Configuration conf = new Configuration();
		FileSystem fs = FileSystem.get(conf);
		
		FileStatus[] tablets = fs.listStatus(new Path(CBConstants.TABLES_DIR + "/" +  args[tablenameIndex]));
		for(FileStatus tablet : tablets) {
			FileStatus[] mapfiles = fs.listStatus(tablet.getPath());
			
			for(FileStatus mapfile : mapfiles) {
				String name = mapfile.getPath().getName();
				String parts[] = name.split("_");
				
				boolean renamed = false;
				
				if(parts.length == 2){
					String prefix = parts[0];
					String seqNum = parts[1];
					boolean seqOk = true;
					try{
						Integer.parseInt(seqNum);
					}catch(NumberFormatException nfe){
						seqOk = false;
					}
					
					if(seqOk){
						String newName = prefix + "_00001_" + seqNum;
						newName = mapfile.getPath().getParent().toString()+"/"+newName;
						log.info("renamed "+mapfile.getPath()+" to "+newName);
						fs.rename(mapfile.getPath(), new Path(newName));
						renamed = true;
					}
				}
				
				if(!renamed){
					log.info("skipped "+mapfile.getPath());
					//fs.delete(mapfile.getPath(), true);
				}
			}
			
		}
	}

}
