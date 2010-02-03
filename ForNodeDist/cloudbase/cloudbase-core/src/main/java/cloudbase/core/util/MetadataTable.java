/**
 * provides a reference to the metadata table for updates by tablet servers
 */
package cloudbase.core.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Map.Entry;

import org.apache.hadoop.io.Text;
import org.apache.log4j.Logger;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;

import cloudbase.core.CBConstants;
import cloudbase.core.Cloudbase;
import cloudbase.core.client.CBException;
import cloudbase.core.client.CBSecurityException;
import cloudbase.core.client.Connector;
import cloudbase.core.client.Instance;
import cloudbase.core.client.Scanner;
import cloudbase.core.client.TableNotFoundException;
import cloudbase.core.client.impl.HdfsZooInstance;
import cloudbase.core.client.impl.ScannerImpl;
import cloudbase.core.client.impl.ThriftScanner;
import cloudbase.core.client.impl.Writer;
import cloudbase.core.client.mapreduce.bulk.BulkOperations;
import cloudbase.core.data.Column;
import cloudbase.core.data.Key;
import cloudbase.core.data.KeyExtent;
import cloudbase.core.data.Mutation;
import cloudbase.core.data.Range;
import cloudbase.core.data.Value;
import cloudbase.core.security.thrift.AuthInfo;
import cloudbase.core.tabletserver.thrift.ConstraintViolationException;
import cloudbase.core.tabletserver.thrift.NotServingTabletException;
import cloudbase.core.zookeeper.ZooSession;

import com.facebook.thrift.TException;


public class MetadataTable {
	
	private static Map<AuthInfo,Writer> metadata_tables = new HashMap<AuthInfo, Writer>();
	private static Logger log = Logger.getLogger(MetadataTable.class.getName());
    private static HdfsZooInstance hdfsInstance;
	
	private static final int SAVE_ROOT_TABLET_RETRIES = 3;

	
	private MetadataTable() {
			
	}
	
	public static class SSTableValue {
		private long size;
		private long numEntries;
		private boolean firstAndLastRowKnown;
		private Text firstRow;
		private Text lastRow;
		
		
		public SSTableValue(long size, long numEntries){
			this.size = size;
			this.numEntries = numEntries;
			firstAndLastRowKnown = false;
		}
		
		public SSTableValue(long size, long numEntries, Text firstRow, Text lastRow){
			this.size = size;
			this.numEntries = numEntries;
			
			if(firstRow == null || lastRow == null){
				
				if(numEntries != 0){
					throw new IllegalArgumentException("can not have non zero entries with no first and last row");
				}
				
				this.firstRow = null;
				this.lastRow = null;
				firstAndLastRowKnown = false;
			}else{
				this.firstRow = firstRow;
				this.lastRow = lastRow;
				firstAndLastRowKnown = true;
			}
		}
		
		public SSTableValue(long size, long numEntries, SSTableValue value) {
			this.size = size;
			this.numEntries = numEntries;
			
			firstAndLastRowKnown = value.firstAndLastRowKnown;
			if(firstAndLastRowKnown){
				this.firstRow = new Text(value.firstRow);
				this.lastRow = new Text(value.lastRow);
			}
		}
		
		public SSTableValue(byte[] encodedSSTV){
			ByteArrayInputStream bais = new ByteArrayInputStream(encodedSSTV);
			DataInputStream dis = new DataInputStream(bais);
			
			try{
				size = Long.parseLong(dis.readUTF());
				numEntries = Long.parseLong(dis.readUTF());
			
				firstAndLastRowKnown = dis.readBoolean();
				if(firstAndLastRowKnown){
					firstRow = new Text();
					firstRow.readFields(dis);
					lastRow = new Text();
					lastRow.readFields(dis);
				}	
			}catch(IOException e){
				log.error(e);
				throw new RuntimeException(e);
			}
		}
		
		public long getSize(){
			return size;
		}
		
		public long getNumEntries(){
			return numEntries;
		}
		
		public byte[] encode(){
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			DataOutputStream dos = new DataOutputStream(baos);
			
			try {
				dos.writeUTF(""+size);
				dos.writeUTF(""+numEntries);
				dos.writeBoolean(firstAndLastRowKnown);
				if(firstAndLastRowKnown){
					firstRow.write(dos);
					lastRow.write(dos);
				}
				
				dos.close();
			} catch (IOException e) {
				log.error(e);
				throw new RuntimeException(e);
			}
			
			return baos.toByteArray();
		}

		public Text getFirstRow() {
			return firstRow;
		}

		public Text getLastRow() {
			return lastRow;
		}
		
		public boolean isFirstAndLastRowKnown(){
			return firstAndLastRowKnown;
		}
	}
	
	public synchronized static Instance getInstance() {
	    if(hdfsInstance == null){
	        hdfsInstance = new HdfsZooInstance();
	    }
	    
	    return hdfsInstance;
	}
	
	public synchronized static Writer getMetadataTable(AuthInfo credentials)
	{
		Writer metadataTable = metadata_tables.get(credentials);
		if(metadataTable == null)
		{
			metadataTable = new Writer(getInstance(), credentials, CBConstants.METADATA_TABLE_NAME);
			metadata_tables.put(credentials, metadataTable);
		}
		return metadataTable;
	}

	
	/**
	 * new sstable update function
	 * adds one sstable to a tablet's list
	 * 
	 * path should be relative to the table directory
	 * 
	 * @param extent
	 * @param path
	 */
	public static boolean updateTabletSSTable(KeyExtent extent, String path, SSTableValue sstv, AuthInfo credentials)
	{
		Writer t;
		try {
			t = getMetadataTable(credentials);

			Mutation m = new Mutation(extent.getMetadataEntry()); 
			
			m.put(CBConstants.METADATA_TABLE_TABLET_SSTABLES_COLUMN_FAMILY, new Text(path), new Value(sstv.encode()));
			//System.out.println("DEBUG : updating "+new Text(extent.getMetadataEntry())+" "+new Text(CBConstants.METADATA_TABLE_TABLET_SSTABLES_COLUMN_FAMILY.toString() + ":" + path)+" "+fileSize);
			
			if(t.update(m)) {
				// System.out.println("commited metadata sstable update: " + row_key + " " + path);
				return true;
			}
		} catch (CBException e) {
			log.error(e.toString());
		} catch (CBSecurityException e) {
			log.error(e.toString());
		} catch (ConstraintViolationException e) {
			log.error(e.toString());
		} catch (TableNotFoundException e) {
			log.error(e.toString());
		}
		return false;
	}
	
	public static boolean updateTabletSSTable(KeyExtent extent, Map<String, SSTableValue> estSizes, AuthInfo credentials)
	{
	    Writer t;
		try {
			t = getMetadataTable(credentials);

			Mutation m = new Mutation(extent.getMetadataEntry()); 
			
			for(Entry<String, SSTableValue> entry : estSizes.entrySet()){
				m.put(CBConstants.METADATA_TABLE_TABLET_SSTABLES_COLUMN_FAMILY, new Text(entry.getKey()), new Value(entry.getValue().encode()));
				//System.out.println("DEBUG : updating "+new Text(extent.getMetadataEntry())+" "+new Text(CBConstants.METADATA_TABLE_TABLET_SSTABLES_COLUMN_FAMILY.toString() + ":" + path)+" "+fileSize);
			}
			
			if(t.update(m)) {
				// System.out.println("commited metadata sstable update: " + row_key + " " + path);
				return true;
			}
		} catch (CBException e) {
			log.error(e.toString());
		} catch (CBSecurityException e) {
			log.error(e.toString());
		}catch (ConstraintViolationException e) {
			log.error(e.toString());
		} catch (TableNotFoundException e) {
			log.error(e.toString());
		}
		return false;
	}
	
	
	/**
	 * 
	 * @param extent
	 * @param path
	 */
	public static boolean updateTabletDirectory(KeyExtent extent, String path, AuthInfo credentials) 
	{
	    Writer t;
		try {
			t = getMetadataTable(credentials);

			Mutation m = new Mutation(extent.getMetadataEntry()); 
			
			m.put(CBConstants.METADATA_TABLE_TABLET_COLUMN_FAMILY, CBConstants.METADATA_TABLE_TABLET_DIRECTORY_COLUMN_NAME, new Value(path.getBytes()));
			if(t.update(m)) {
				// System.out.println("commited metadata sstable update: " + row_key + " " + path);
				return true;
			}
			else {
				log.warn("unable to commit directory update");
			}
		} catch (CBException e) {
			log.error(e.toString());
		} catch (CBSecurityException e) {
			log.error(e.toString());
		}catch (ConstraintViolationException e) {
			log.error(e.toString());
		} catch (TableNotFoundException e) {
			log.error(e.toString());
		}
		return false;
	}
	
	public static boolean updateTabletLocation(KeyExtent extent, String location, AuthInfo credentials)  {
	    Writer t;
		try {
			t = getMetadataTable(credentials);

			Text row_key = extent.getMetadataEntry();
			Mutation m = new Mutation(row_key);
			m.put(CBConstants.METADATA_TABLE_TABLET_COLUMN_FAMILY, CBConstants.METADATA_TABLE_TABLET_LOCATION_COLUMN_NAME, new Value(location.getBytes()));
			if(t.update(m)) {
				//System.out.println("commited metadata location update " + row_key + " " + location);
				return true;
			}
			else {
				log.warn("unable to commit location update");
			}
		} catch (CBException e) {
			log.error(e.toString());
		} catch (CBSecurityException e) {
			log.error(e.toString());
		}catch (ConstraintViolationException e) {
			log.error(e.toString());
		} catch (TableNotFoundException e) {
			log.error(e.toString());
		}
		return false;
	}

	public static boolean updateTabletPrevEndRow(KeyExtent extent, AuthInfo credentials) {
	    Writer t;
		try {
			t = getMetadataTable(credentials);

			Mutation m = extent.getPrevRowUpdateMutation(); // 
			
			if(t.update(m))
				return true;
			else
				log.warn("unable to commit prev end row update");
			
		} catch (CBException e) {
			log.error(e.toString());
		} catch (CBSecurityException e) {
			log.error(e.toString());
		}catch (ConstraintViolationException e) {
			log.error(e.toString());
		} catch (TableNotFoundException e) {
			log.error(e.toString());
		}
		return false;
	}
	
	/**
	 * convenience method for reading entries from the metadata table
	 */
	public static SortedMap<KeyExtent, Text> getMetadataDirectoryEntries(SortedMap<Key, Value> entries) {
		Key key;
		Value val;
		Text sstables = null;
		Value prevRow = null;
		KeyExtent ke;
		
		SortedMap<KeyExtent, Text> results = new TreeMap<KeyExtent, Text>();
		
		Text lastRowFromKey = new Text();
		
		//text obj below is meant to be reused in loop for efficiency
		Text colf = new Text();
		Text colq = new Text();
		
		for(Entry<Key, Value> entry : entries.entrySet()) {
			//System.out.println("convert md entry: " + entry.getKey() + " " + entry.getValue());
			key = entry.getKey();
			val = entry.getValue();
			
			if(key.compareRow(lastRowFromKey) != 0){
				prevRow = null;
				sstables = null;
				key.getRow(lastRowFromKey);
			}
			
			// interpret the row id as a key extent
			if(key.getColumnFamily(colf).equals(CBConstants.METADATA_TABLE_TABLET_COLUMN_FAMILY) &&
			   key.getColumnQualifier(colq).equals(CBConstants.METADATA_TABLE_TABLET_DIRECTORY_COLUMN_NAME))
				sstables = new Text(val.toString());
			
			else if(key.getColumnFamily(colf).equals(CBConstants.METADATA_TABLE_TABLET_COLUMN_FAMILY) && 
					key.getColumnQualifier(colq).equals(CBConstants.METADATA_TABLE_TABLET_PREV_ROW_COLUMN_NAME))
				prevRow = new Value(val);
			
			if(sstables != null && prevRow != null) {
				ke = new KeyExtent(key.getRow(), prevRow);
				results.put(ke, sstables);
				//System.out.println("converted to " + ke + " " + sstables);
				
				sstables = null;
				prevRow = null;
			}	
		}
		return results;
	}
	
	private static boolean getBatchFromRootTablet(AuthInfo credentials, Text startRow, SortedMap<Key, Value> results, SortedSet<Column> columns, boolean skipStartRow, int size)
	throws CBSecurityException
	{
		while (true){
			try {
				return ThriftScanner.getBatchFromServer(credentials, startRow, CBConstants.ROOT_TABLET_EXTENT, getInstance().getRootTabletLocation(), results, columns, skipStartRow, size, CBConstants.NO_AUTHS);
			} catch (NotServingTabletException e) {
				UtilWaitThread.sleep(100);
				continue;
			} catch (CBException e) {
				UtilWaitThread.sleep(100);
				continue;
			}
		}
		
		
	}
	
	/**
	 * convenience method for reading all metadata directory entries from the root tablet
	 * 
	 * @throws IOException 
	 * @throws TException 
	 */
	public static SortedMap<KeyExtent, Text[]> getRootMetadataEntries(AuthInfo credentials) throws IOException, TException {
		Key key;
		Value val;
		Value prevRow = null;
		KeyExtent ke;
		
		Text startRow = new Text("");
		
		TreeSet<Column> columns = new TreeSet<Column>();
		columns.add(new Column(TextUtil.getBytes(CBConstants.METADATA_TABLE_TABLET_COLUMN_FAMILY), TextUtil.getBytes(CBConstants.METADATA_TABLE_TABLET_DIRECTORY_COLUMN_NAME), null));
		columns.add(new Column(TextUtil.getBytes(CBConstants.METADATA_TABLE_TABLET_COLUMN_FAMILY), TextUtil.getBytes(CBConstants.METADATA_TABLE_TABLET_PREV_ROW_COLUMN_NAME), null));
		columns.add(new Column(TextUtil.getBytes(CBConstants.METADATA_TABLE_TABLET_COLUMN_FAMILY), TextUtil.getBytes(CBConstants.METADATA_TABLE_TABLET_LOCATION_COLUMN_NAME), null));
		
		SortedMap<KeyExtent, Text[]> results = new TreeMap<KeyExtent, Text[]>();

		try {
			
			log.debug("About to create TabletServerConnection");
		
			SortedMap<Key, Value> entries = new TreeMap<Key, Value>();
			
			boolean skipStartRow = false;
			if(startRow.toString().equals(""))
				skipStartRow = true;
			
			log.debug("About to call getBatchFromServer");
			
			// Loop until root tserver is assigned.
			// TODO: Try to remedy this to not use a busy wait in case of a real tablet assignment issue
			boolean more = getBatchFromRootTablet(credentials, startRow, entries, columns, skipStartRow, CBConstants.SCAN_BATCH_SIZE);
			
			log.debug("Finished calling getBatchFromServer more = "+more+"  entries.size() = "+entries.size());

			while(more){
				startRow = entries.lastKey().getRow(); // set end row
				more = getBatchFromRootTablet(credentials, startRow, entries, columns, true, CBConstants.SCAN_BATCH_SIZE);	
				log.debug("Finished calling getBatchFromServer again more = "+more+"  entries.size() = "+entries.size());
			}
			
		
			Text directory = null;
			Text location = null;

			Text lastRowFromKey = new Text();

			//text obj below is meant to be reused in loop for efficiency
			Text colf = new Text();
			Text colq = new Text();

			for(Entry<Key, Value> entry : entries.entrySet()) {
				//System.out.println("convert md entry: " + entry.getKey() + " " + entry.getValue());
				key = entry.getKey();
				val = entry.getValue();

				if(key.compareRow(lastRowFromKey) != 0){
					prevRow = null;
					directory = null;
					key.getRow(lastRowFromKey);
				}

				// interpret the row id as a key extent
				if(key.getColumnFamily(colf).equals(CBConstants.METADATA_TABLE_TABLET_COLUMN_FAMILY) &&
				   key.getColumnQualifier(colq).equals(CBConstants.METADATA_TABLE_TABLET_DIRECTORY_COLUMN_NAME))
					directory = new Text(val.toString());

				else if(key.getColumnFamily(colf).equals(CBConstants.METADATA_TABLE_TABLET_COLUMN_FAMILY) &&
						key.getColumnQualifier(colq).equals(CBConstants.METADATA_TABLE_TABLET_PREV_ROW_COLUMN_NAME))
					prevRow = new Value(val);
				
				else if(key.getColumnFamily(colf).equals(CBConstants.METADATA_TABLE_TABLET_COLUMN_FAMILY) &&
						key.getColumnQualifier(colq).equals(CBConstants.METADATA_TABLE_TABLET_LOCATION_COLUMN_NAME))
					location = new Text(val.toString());

				if(directory != null && prevRow != null) {
					ke = new KeyExtent(key.getRow(), prevRow);
					results.put(ke, new Text[]{directory, location});
					//System.out.println("converted to " + ke + " " + directory);

					directory = null;
					prevRow = null;
					location = null;
				}	
			}
			
			try{
				BulkOperations.validateMetadataEntries(CBConstants.METADATA_TABLE_NAME, new TreeSet<KeyExtent>(results.keySet()));
			}catch(Exception e){
				log.warn("Failed to validate root tablet entries, retrying ....", e);
				UtilWaitThread.sleep(100);
				return getRootMetadataEntries(credentials);
			}
			
			return results;
		} catch (CBSecurityException e) {
			log.warn("Unauthorized access...");
			return results;
		}
	}
	
	/**
	 * convenience method for reading a metadata tablet's sstable entries from the root tablet
	 * 
	 * @throws IOException 
	 */
	public static SortedMap<Key, Value> getRootMetadataSSTableEntries(KeyExtent extent, AuthInfo credentials) throws IOException, TException {
		SortedSet<Column> columns = new TreeSet<Column>();
		columns.add(new Column(CBConstants.METADATA_TABLE_TABLET_SSTABLES_COLUMN_FAMILY.getBytes(), null, null));
		
		try {
			SortedMap<Key, Value> entries = new TreeMap<Key, Value>();
			
			Text metadataEntry = extent.getMetadataEntry();
			Text startRow;
            boolean more = getBatchFromRootTablet(credentials, metadataEntry, entries, columns, false, CBConstants.SCAN_BATCH_SIZE);
			
			while(more){
				startRow = entries.lastKey().getRow(); // set end row
				more = getBatchFromRootTablet(credentials, startRow, entries, columns, true, CBConstants.SCAN_BATCH_SIZE);	
			}
			
			Iterator<Key> iter = entries.keySet().iterator();
			while(iter.hasNext()){
				Key key = iter.next();
				if(key.compareRow(metadataEntry) != 0){
					iter.remove();
				}
			}
			
			return entries;
			
		} catch (CBSecurityException e) {
			log.warn("Unauthorized access...");
			return new TreeMap<Key, Value>();
		}
	
	}
	
	public static SortedMap<KeyExtent, Text> getMetadataLocationEntries(SortedMap<Key, Value> entries) {
		Key key;
		Value val;
		Text location = null;
		Value prevRow = null;
		KeyExtent ke;
		
		SortedMap<KeyExtent, Text> results = new TreeMap<KeyExtent, Text>();
		
		Text lastRowFromKey = new Text();
		
		//text obj below is meant to be reused in loop for efficiency
		Text colf = new Text();
		Text colq = new Text();
		
		for(Entry<Key, Value> entry : entries.entrySet()) {
			//System.out.println("convert md entry: " + entry.getKey() + " " + entry.getValue());
			key = entry.getKey();
			val = entry.getValue();
			
			if(key.compareRow(lastRowFromKey) != 0){
				prevRow = null;
				location = null;
				key.getRow(lastRowFromKey);
			}
			
			// interpret the row id as a key extent
			if(key.getColumnFamily(colf).equals(CBConstants.METADATA_TABLE_TABLET_COLUMN_FAMILY) &&
					key.getColumnQualifier(colq).equals(CBConstants.METADATA_TABLE_TABLET_LOCATION_COLUMN_NAME))
				location = new Text(val.toString());
			
			else if(key.getColumnFamily(colf).equals(CBConstants.METADATA_TABLE_TABLET_COLUMN_FAMILY) &&
					key.getColumnQualifier(colq).equals(CBConstants.METADATA_TABLE_TABLET_PREV_ROW_COLUMN_NAME))
				prevRow = new Value(val);
			
			if(location != null && prevRow != null) {
				ke = new KeyExtent(key.getRow(), prevRow);
				results.put(ke, location);
				//System.out.println("converted to " + ke + " " + location);
				
				location = null;
				prevRow = null;
			}	
		}
		return results;
	}

	public static boolean recordRootTabletLocation(String address) {
	    for (int i = 0; i < SAVE_ROOT_TABLET_RETRIES; i++) {
	        try {
	            log.info("trying to write root tablet location to ZooKeeper as " + address);
	            
	            ZooKeeper session = ZooSession.getSession();
	            String zRootLocPath = CBConstants.ZROOT_PATH+"/"+Cloudbase.getInstanceID()+CBConstants.ZROOT_TABLET_LOCATION;
	            
	            if(session.exists(zRootLocPath, false) == null){
	                session.create(zRootLocPath, address.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
	            }else{
	                session.setData(zRootLocPath, address.getBytes(), -1);
	            }
	            
	            return true;
	        } catch (KeeperException e) {
	            log.error("Master: unable to save root tablet location in zookeeper. exception: " + e.getMessage());
            } catch (InterruptedException e) {
                log.error("Master: unable to save root tablet location in zookeeper. exception: " + e.getMessage());
            }
	    }
	    log.error("Giving up after " + SAVE_ROOT_TABLET_RETRIES + " retries");
	    return false;
	}
	
	public static SortedMap<String, SSTableValue> getSSTableSizes(KeyExtent extent, AuthInfo credentials)
	throws CBException, CBSecurityException, TableNotFoundException
	{
		TreeMap<String, SSTableValue> sizes = new TreeMap<String, SSTableValue>();
		
		Scanner mdScanner = new Connector(getInstance(), credentials.user, credentials.password).createScanner(CBConstants.METADATA_TABLE_NAME, CBConstants.NO_AUTHS);
		mdScanner.fetchColumnFamily(CBConstants.METADATA_TABLE_TABLET_SSTABLES_COLUMN_FAMILY);
		Text row = extent.getMetadataEntry();
		
		Key endKey = new Key(row, CBConstants.METADATA_TABLE_TABLET_SSTABLES_COLUMN_FAMILY, new Text(""));
		endKey = endKey.followingKey(2);
		
		mdScanner.setRange(new Range(new Key(row), endKey));
		for (Entry<Key, Value> entry : mdScanner) {			
			//System.out.println(entry.getKey()+".compareTo("+endKey+", 3) = "+entry.getKey().compareTo(endKey, 3));
			
			if(!entry.getKey().getRow().equals(row))
				break;
			SSTableValue sstv = new SSTableValue(entry.getValue().get());
			sizes.put(entry.getKey().getColumnQualifier().toString(), sstv);
		}
		
		return sizes;
	}

	public static boolean addNewTablet(KeyExtent extent, String path, String location, Map<String, SSTableValue> sstableSizes, AuthInfo credentials)
	{
	    Writer t;
		try {
			t = getMetadataTable(credentials);

			Mutation m = extent.getPrevRowUpdateMutation(); // 
			
			m.put(CBConstants.METADATA_TABLE_TABLET_COLUMN_FAMILY, CBConstants.METADATA_TABLE_TABLET_DIRECTORY_COLUMN_NAME, new Value(path.getBytes()));
			if(location != null)
				m.put(CBConstants.METADATA_TABLE_TABLET_COLUMN_FAMILY, CBConstants.METADATA_TABLE_TABLET_LOCATION_COLUMN_NAME, new Value(location.getBytes()));
			
			for (Entry<String, SSTableValue> entry : sstableSizes.entrySet()) {
				m.put(CBConstants.METADATA_TABLE_TABLET_SSTABLES_COLUMN_FAMILY, new Text(entry.getKey()), 
						new Value(entry.getValue().encode()));
			}
			
			
			if(t.update(m)) { 
				//System.out.println("committed metadata prev row update " + extent + " " + extent.getPrevEndRow());
				return true;
			}
			else
				log.warn("unable to commit prev end row update");
		} catch (CBException e) {
			log.error(e.toString());
		} catch (CBSecurityException e) {
			log.error(e.toString());
		} catch (ConstraintViolationException e) {
			log.error(e.toString());
		} catch (TableNotFoundException e) {
			log.error(e.toString());
		}
		return false;
		
	}

	public static boolean splitTablet(KeyExtent extent, Map<String, SSTableValue> sstableSizes, Text oldPrevEndRow, double splitRatio, AuthInfo credentials)
	{
	    Writer t;
		try {
			t = getMetadataTable(credentials);

			Mutation m = extent.getPrevRowUpdateMutation(); // 
			
			for (Entry<String, SSTableValue> entry : sstableSizes.entrySet()) {
				m.put(CBConstants.METADATA_TABLE_TABLET_SSTABLES_COLUMN_FAMILY, new Text(entry.getKey()), 
						new Value(entry.getValue().encode()));
			}
			
			
			m.put(CBConstants.METADATA_TABLE_TABLET_COLUMN_FAMILY, CBConstants.METADATA_TABLE_TABLET_SPLIT_RATIO_COLUMN_NAME, 
					new Value(Double.toString(splitRatio).getBytes()));
			
			m.put(CBConstants.METADATA_TABLE_TABLET_COLUMN_FAMILY, CBConstants.METADATA_TABLE_TABLET_OLD_PREV_ROW_COLUMN_NAME, 
					KeyExtent.encodePrevEndRow(oldPrevEndRow));
			
			if(t.update(m)) { 
				//System.out.println("committed metadata prev row update " + extent + " " + extent.getPrevEndRow());
				return true;
			}
			else
				log.warn("unable to commit prev end row update");
		} catch (CBException e) {
			log.error(e.toString());
		} catch (CBSecurityException e) {
			log.error(e.toString());
		} catch (ConstraintViolationException e) {
			log.error(e.toString());
		} catch (TableNotFoundException e) {
			log.error(e.toString());
		}
		return false;
	}

	public static boolean finishSplit(Text metadataEntry, List<String> highSstablesToRemove, AuthInfo credentials) {
	    Writer t;
		try {
			t = getMetadataTable(credentials);

			Mutation m = new Mutation(metadataEntry);; // 
			
			m.remove(CBConstants.METADATA_TABLE_TABLET_COLUMN_FAMILY, CBConstants.METADATA_TABLE_TABLET_SPLIT_RATIO_COLUMN_NAME);
			m.remove(CBConstants.METADATA_TABLE_TABLET_COLUMN_FAMILY, CBConstants.METADATA_TABLE_TABLET_OLD_PREV_ROW_COLUMN_NAME);
			
			for (String pathToRemove : highSstablesToRemove) {
				m.remove(CBConstants.METADATA_TABLE_TABLET_SSTABLES_COLUMN_FAMILY, new Text(pathToRemove));
			}
			
			if(t.update(m)) { 
				//System.out.println("committed metadata prev row update " + extent + " " + extent.getPrevEndRow());
				return true;
			}
			else
				log.warn("unable to commit prev end row update");
		} catch (CBException e) {
			log.error(e.toString());
		} catch (CBSecurityException e) {
			log.error(e.toString());
		} catch (ConstraintViolationException e) {
			log.error(e.toString());
		} catch (TableNotFoundException e) {
			log.error(e.toString());
		}
		return false;
	}
	
	public static boolean finishSplit(KeyExtent extent, List<String> highSstablesToRemove, AuthInfo credentials) {
		return finishSplit(extent.getMetadataEntry(), highSstablesToRemove, credentials);
	}
	
	public static boolean replaceSSTables(KeyExtent extent, Collection<String> sstablesToDelete, String path, SSTableValue size, AuthInfo credentials) {
	    Writer t;
		try {
			

			Mutation m = new Mutation(extent.getMetadataEntry()); 
			
			for (String pathToRemove : sstablesToDelete)
				m.remove(CBConstants.METADATA_TABLE_TABLET_SSTABLES_COLUMN_FAMILY, new Text(pathToRemove));
			
			if (size.getNumEntries() > 0)
				m.put(CBConstants.METADATA_TABLE_TABLET_SSTABLES_COLUMN_FAMILY, new Text(path), new Value(size.encode()));
			
			t = getMetadataTable(credentials);
			if(t.update(m)) {
				// System.out.println("commited metadata sstable update: " + row_key + " " + path);
				return true;
			}
			
		} catch (CBException e) {
			log.error(e.toString());
		} catch (CBSecurityException e) {
			log.error(e.toString());
		} catch (ConstraintViolationException e) {
			log.error(e.toString());
		} catch (TableNotFoundException e) {
			log.error(e.toString());
		}
		return false;
		
	}

	public static SortedMap<Text, SortedMap<Text, Value>> getTabletEntries(KeyExtent ke, List<Text> columns, AuthInfo credentials)
	throws CBException, CBSecurityException, TableNotFoundException
	{
		Text startRow;
		Text endRow = ke.getMetadataEntry();
		
		if(ke.getPrevEndRow() == null){
			startRow = new Text(KeyExtent.getMetadataEntry(ke.getTableName(), new Text()));
		}else{
			startRow = new Text(KeyExtent.getMetadataEntry(ke.getTableName(), ke.getPrevEndRow()));
		}
		
		Scanner scanner = new Connector(getInstance(), credentials.user, credentials.password).createScanner(CBConstants.METADATA_TABLE_NAME, CBConstants.NO_AUTHS);
		
		for (Text column : columns)
			scanner.fetchColumn(CBConstants.METADATA_TABLE_TABLET_COLUMN_FAMILY, column);

		scanner.setRange(new Range(new Key(startRow), new Key(endRow).followingKey(1)));
		
		TreeMap<Text, SortedMap<Text, Value>> tabletEntries = new TreeMap<Text, SortedMap<Text,Value>>();
		
		for (Entry<Key, Value> entry : scanner)
		{
			Text row = entry.getKey().getRow();
			
			SortedMap<Text, Value> colVals = tabletEntries.get(row);
			if(colVals == null)
			{
				colVals = new TreeMap<Text, Value>();
				tabletEntries.put(row, colVals);
			}
			
			colVals.put(entry.getKey().getColumnQualifier(), entry.getValue());
		}
		
		return tabletEntries;
	}
	
	private static KeyExtent fixSplit(Text table, Text metadataEntry, Text metadataPrevEndRow, Value oper, double splitRatio, String hostAddress, AuthInfo credentials)
	throws CBException, CBSecurityException, TableNotFoundException
	{
		if(metadataPrevEndRow == null)
			//something is wrong, this should not happen... if a tablet is split, it will always have a
			//prev end row.... 
			throw new CBException("Split tablet does not have prev end row, something is amiss, extent = "+metadataEntry);
		
		KeyExtent low = null;
		
		List<String> highSstablesToRemove = new ArrayList<String>();
	
		String lowDirectory;
		try {
			lowDirectory = Encoding.encodeDirectoryName(metadataPrevEndRow);
		} catch (UnsupportedEncodingException e) {
			throw new CBException(e);
		}
		//TODO create dir?

		Text prevPrevEndRow = KeyExtent.decodePrevEndRow(oper);

		low = new KeyExtent(table, metadataPrevEndRow, prevPrevEndRow);

		Scanner scanner3 = new Connector(getInstance(), credentials.user, credentials.password).createScanner(CBConstants.METADATA_TABLE_NAME, CBConstants.NO_AUTHS);
		Key rowKey = new Key(metadataEntry);

		Map<String, SSTableValue> lowSstableSizes = new TreeMap<String, SSTableValue>();
		scanner3.fetchColumnFamily(CBConstants.METADATA_TABLE_TABLET_SSTABLES_COLUMN_FAMILY);
		scanner3.setRange(new Range(rowKey, rowKey.followingKey(1)));

		for(Entry<Key, Value> entry : scanner3) {
			if(entry.getKey().compareColumnFamily(CBConstants.METADATA_TABLE_TABLET_SSTABLES_COLUMN_FAMILY) == 0){
				SSTableValue old_sstv = new SSTableValue(entry.getValue().get());

				Text firstRow = old_sstv.getFirstRow();
				Text lastRow = old_sstv.getLastRow();

				boolean rowsKnown = old_sstv.isFirstAndLastRowKnown();

				if(rowsKnown && firstRow.compareTo(metadataPrevEndRow) > 0){
					//only in high

				}else if(rowsKnown && lastRow.compareTo(metadataPrevEndRow) <= 0){
					//only in low
					long lowSize = old_sstv.getSize();
					long lowEntries = old_sstv.getNumEntries();

					SSTableValue new_sstv = new SSTableValue(lowSize, lowEntries, old_sstv);

					lowSstableSizes.put(entry.getKey().getColumnQualifier().toString(), new_sstv);

					highSstablesToRemove.add(entry.getKey().getColumnQualifier().toString());
				}else{
					long lowSize = (long)(old_sstv.getSize()/ (1.0 - splitRatio) * splitRatio);
					long lowEntries = (long)(old_sstv.getNumEntries()/ (1.0 - splitRatio) * splitRatio);

					SSTableValue new_sstv = new SSTableValue(lowSize, lowEntries, old_sstv);

					lowSstableSizes.put(entry.getKey().getColumnQualifier().toString(), new_sstv);
				}
			}
		}
		
		
		//check to see if prev tablet exist in metadata tablet
		Key prevRowKey = new Key(new Text(KeyExtent.getMetadataEntry(table, metadataPrevEndRow)));
		
		ScannerImpl scanner2 = new ScannerImpl(getInstance(), credentials, CBConstants.METADATA_TABLE_NAME, CBConstants.NO_AUTHS);
		scanner2.setRange(new Range(prevRowKey, prevRowKey.followingKey(1)));
		
		if(!scanner2.iterator().hasNext()){
			log.debug("Prev tablet "+prevRowKey+" does not exist, need to create it "+metadataPrevEndRow+" "+prevPrevEndRow+" "+splitRatio);
			while(!MetadataTable.addNewTablet(low, lowDirectory,  hostAddress, lowSstableSizes, credentials)){
				log.warn("Metadata table split update failed, will retry...");
				UtilWaitThread.sleep(1000);
			}
		}else{
			log.debug("Prev tablet "+prevRowKey+" exist, do not need to add it");
		}
		
		while(!MetadataTable.finishSplit(metadataEntry, highSstablesToRemove, credentials)){
			log.warn("Metadata table split update failed, will retry...");
			UtilWaitThread.sleep(1000);
		}
		
		return low;
	}
	
	public static KeyExtent fixSplit(Text metadataEntry, SortedMap<Text, Value> columns, String hostAddress, AuthInfo credentials)
	throws CBException, CBSecurityException, TableNotFoundException
	{
		log.warn("Incomplete split "+metadataEntry+" attempting to fix");
		
		Value oper = columns.get(CBConstants.METADATA_TABLE_TABLET_OLD_PREV_ROW_COLUMN_NAME);
		
		if(columns.get(CBConstants.METADATA_TABLE_TABLET_SPLIT_RATIO_COLUMN_NAME) == null){
			log.warn("Metadata entry does not have split ratio ("+metadataEntry+")");
			return null;
		}
		
		double splitRatio = Double.parseDouble(new String(columns.get(CBConstants.METADATA_TABLE_TABLET_SPLIT_RATIO_COLUMN_NAME).get()));
		
		Value prevEndRowIBW = columns.get(CBConstants.METADATA_TABLE_TABLET_PREV_ROW_COLUMN_NAME);
		
		if(prevEndRowIBW == null){
			log.warn("Metadata entry does not have prev row ("+metadataEntry+")");
			return null;
		}
		 
		Text metadataPrevEndRow = KeyExtent.decodePrevEndRow(prevEndRowIBW);
		
		Text table = (new KeyExtent(metadataEntry, (Text)null)).getTableName();
		
		return fixSplit(table, metadataEntry, metadataPrevEndRow, oper, splitRatio, hostAddress, credentials);
	}
	
	public static KeyExtent fixSplit(Text metadataEntry, String hostAddress, AuthInfo credentials)
	throws CBException, CBSecurityException, TableNotFoundException
	{
		
		List<Text> columnsToFetch = Arrays.asList(new Text[]{
				CBConstants.METADATA_TABLE_TABLET_PREV_ROW_COLUMN_NAME,
				CBConstants.METADATA_TABLE_TABLET_SPLIT_RATIO_COLUMN_NAME,
				CBConstants.METADATA_TABLE_TABLET_OLD_PREV_ROW_COLUMN_NAME});
		
		KeyExtent ke = new KeyExtent(metadataEntry, (Text)null);
		ke.setPrevEndRow(ke.getEndRow());
		
		SortedMap<Text, SortedMap<Text, Value>> tabletEntries = 
			getTabletEntries(ke, columnsToFetch, credentials);
		
		return fixSplit(metadataEntry, tabletEntries.get(metadataEntry), hostAddress, credentials);
	}
	
	
	public static void deleteTable(String table, AuthInfo credentials)
	throws CBException, CBSecurityException, TableNotFoundException, ConstraintViolationException
	{
		Scanner ms = new Connector(getInstance(), credentials.user, credentials.password).createScanner(CBConstants.METADATA_TABLE_NAME, CBConstants.NO_AUTHS);
		Text tableText = new Text(table);
		Writer mt = getMetadataTable(credentials);
		
		// scan metadata for our table and delete everything we find
		Mutation m = null;
		ms.setRange(new Range(new Text(KeyExtent.getMetadataEntry(tableText, new Text())), null));
		
		for(Entry<Key, Value> cell : ms) {
		    Key key = cell.getKey();
            String rowTable = new String(KeyExtent.tableOfMetadataRow(key.getRow()));
            
			if(!rowTable.equals(table))
				break;
			
			if(m == null)
				m = new Mutation(key.getRow());
			
			if(key.getRow().compareTo(m.getRow(), 0, key.getRowLen()) != 0)
			{
				mt.update(m);
				m = new Mutation(key.getRow());
			}
			m.remove(key.getColumnFamily(), key.getColumnQualifier());
		}
		
		if(m != null)
			mt.update(m);
	}
}
