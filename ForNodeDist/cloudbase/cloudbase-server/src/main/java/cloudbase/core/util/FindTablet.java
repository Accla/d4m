package cloudbase.core.util;

import java.io.IOException;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.io.Text;

import cloudbase.core.CBConstants;
import cloudbase.core.client.CBException;
import cloudbase.core.client.CBSecurityException;
import cloudbase.core.client.impl.ThriftScanner;
import cloudbase.core.data.Column;
import cloudbase.core.data.Key;
import cloudbase.core.data.KeyExtent;
import cloudbase.core.data.Value;
import cloudbase.core.security.thrift.AuthInfo;
import cloudbase.core.tabletserver.thrift.NotServingTabletException;
import cloudbase.core.zookeeper.ZooConstants;

public class FindTablet {
	private static AuthInfo systemCredentials_ = null;
	
	private static AuthInfo systemCredentials()
	{
		if (systemCredentials_ == null)
			systemCredentials_ = new AuthInfo(ZooConstants.SYSTEM_USERNAME, ZooConstants.SYSTEM_PASSWORD);
		return systemCredentials_;
	}
	
	public static void main(String[] args) throws IOException, CBException, CBSecurityException
	{
		
	    if(true) throw new RuntimeException("This code is broken, fix it to use ZooKeeper to obtain tablet server list");
	    
	    if((args.length != 3)) {
			System.out.println("Usage: FindTablet table endRow prevEndRow");
			return;
		}
			
		Text tableName = new Text(args[0]);
		Text endRow = new Text(args[1]);
		Text prevEndRow = new Text(args[2]);
		
		Configuration conf = new Configuration();
		
		KeyExtent extent = new KeyExtent(tableName, endRow, prevEndRow);
		
		// find all tablet servers
		FileSystem fs = FileSystem.get(conf);
		FileStatus[] servers = new FileStatus[0];
		
		SortedMap<Key, Value> results = new TreeMap<Key, Value>();
		SortedSet<Column> noCols = new TreeSet<Column>();
		
		// ask each one whether they have the tablet
		for(FileStatus server : servers) {
			results.clear();
			
			boolean serving = true;
			try {
				ThriftScanner.getBatchFromServer(systemCredentials(), endRow, extent, server.getPath().getName().toString(), results, noCols, false, 1, CBConstants.NO_AUTHS);
			} catch (NotServingTabletException e) {
				serving = false;
			}
			
			if(serving)
				System.out.println(server.getPath().getName().toString() + " reports hosting " + extent);
		}
	}
}
