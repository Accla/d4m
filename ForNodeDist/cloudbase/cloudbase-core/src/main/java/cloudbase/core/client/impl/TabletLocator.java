package cloudbase.core.client.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Map.Entry;

import org.apache.hadoop.io.Text;
import org.apache.log4j.Logger;

import cloudbase.core.CBConstants;
import cloudbase.core.client.CBException;
import cloudbase.core.client.CBSecurityException;
import cloudbase.core.client.Instance;
import cloudbase.core.client.TableNotFoundException;
import cloudbase.core.client.mapreduce.bulk.BulkOperations;
import cloudbase.core.data.Column;
import cloudbase.core.data.Key;
import cloudbase.core.data.KeyExtent;
import cloudbase.core.data.Mutation;
import cloudbase.core.data.Range;
import cloudbase.core.data.Value;
import cloudbase.core.security.thrift.AuthInfo;
import cloudbase.core.tabletserver.thrift.NotServingTabletException;
import cloudbase.core.util.MetadataTable;
import cloudbase.core.util.TextUtil;
import cloudbase.core.util.UtilWaitThread;

public abstract class TabletLocator {

	public abstract TabletLocation locateTablet(Text row, boolean skipRow) throws CBException, CBSecurityException, TableNotFoundException;
	
	public abstract void binMutations(List<Mutation> mutations, 
			Map<String, TabletServerMutations> binnedMutations,
			List<Mutation> failures) throws CBException, CBSecurityException, TableNotFoundException; 
	
	public abstract void binRanges(List<Range> ranges, Map<String, Map<KeyExtent, List<Range>>> binnedRanges) throws CBException, CBSecurityException, TableNotFoundException;
	
	public abstract void invalidateCache(KeyExtent failedExtent);
	
	public abstract SortedMap<KeyExtent, TabletLocation> getTablets() throws CBException, CBSecurityException, TableNotFoundException; 
	
	/**
	 * Invalidate all metadata entries that point to server s
	 */
	public abstract void invalidateCache(String server);

	private static class LocatorKey
	{
	    String instanceId;
	    Text tableName;
	    
	    LocatorKey(String instanceId, Text table){
	        this.instanceId = instanceId;
	        this.tableName = table;
	    }
	    
	    public int hashCode(){
	        return instanceId.hashCode() + tableName.hashCode();
	    }
	    
	    public boolean equals(Object o){
	        LocatorKey lk = (LocatorKey) o;
	        return instanceId.equals(lk.instanceId) && tableName.equals(lk.tableName);
	    }
	    
	}
	
	private static HashMap<LocatorKey, TabletLocator> locators = new HashMap<LocatorKey, TabletLocator>();
	
	public static synchronized TabletLocator getInstance(Instance instance, AuthInfo credentials, Text tablename)
	{
	    LocatorKey key = new LocatorKey(instance.getInstanceID(), tablename);
	    
	    TabletLocator tl = locators.get(key);
	    
	    if (tl == null)
	    {
	    	tl = (tablename.toString().equals(CBConstants.METADATA_TABLE_NAME)) ? new TabletLocationCache(instance, credentials, tablename) : new MetadataCache(instance, credentials, tablename);
	        locators.put(key, tl);
	    }
	    
	    return tl;
	}

	public static class TabletLocation
	{
		public final KeyExtent tablet_extent;
		public final String tablet_location;

		public TabletLocation(KeyExtent tablet_extent, String tablet_location)
		{
			this.tablet_extent = tablet_extent;
			this.tablet_location = tablet_location;
		}
		
		public boolean equals(Object o)
		{
			if(o instanceof TabletLocation)
			{
				TabletLocation otl = (TabletLocation) o;
				return tablet_extent.equals(otl.tablet_extent) && tablet_location.equals(otl.tablet_location);
			}
			else
				return false;
		}
		
		public String toString()
		{
			return "("+tablet_extent+","+tablet_location+")";
		}
	}
	
	public static class TabletServerMutations
	{
		private Map<KeyExtent, List<Mutation>> mutations;
		
		TabletServerMutations()
		{
			mutations = new HashMap<KeyExtent, List<Mutation>>();
		}
		
		void addMutation(KeyExtent ke, Mutation m)
		{
			List<Mutation> mutList = mutations.get(ke);
			if(mutList == null)
			{
				mutList = new ArrayList<Mutation>();
				mutations.put(ke, mutList);
			}
			
			mutList.add(m);
		}
		
		Map<KeyExtent, List<Mutation>> getMutations()
		{
			return mutations;
		}
	}
	
	//END INTERFACE Begin implementations
	
	
	/**
	 * This cache holds the locations of region servers by key extent
	 * as reported from the master. Only the last key for each
	 * region is used in lookups.
	 * 
	 * These cache classes (ScanCache as well) are designed to sit between the
	 * application interface and the servers and end up handing server communication
	 * which is not really a cache's job description ...
	 * 
	 * I suppose the comms functionality could be offloaded to another class
	 * an instance of which, to which the cache classes have a reference
	 * 
	 */

	private static Logger tlcLog = Logger.getLogger(TabletLocationCache.class.getName());
	
	private static class TabletLocationCache extends TabletLocator
	{
		private TreeMap<KeyExtent, Text> entries;
		private SortedSet<Column> locCols;
		private Text tableName;
        private Instance instance;
        private AuthInfo credentials;
		
		public TabletLocationCache(Instance instance, AuthInfo credentials, Text table)
		{
		    // in the cache, all keys are from the same table, so we only map keys to locations, not extents
            this.tableName = table;
            this.instance = instance;
            this.credentials = credentials;
            entries = new TreeMap<KeyExtent, Text>();
            locCols = new TreeSet<Column>();
            locCols.add(new Column(TextUtil.getBytes(CBConstants.METADATA_TABLE_TABLET_COLUMN_FAMILY), TextUtil.getBytes(CBConstants.METADATA_TABLE_TABLET_LOCATION_COLUMN_NAME), null));
            locCols.add(new Column(TextUtil.getBytes(CBConstants.METADATA_TABLE_TABLET_COLUMN_FAMILY), TextUtil.getBytes(CBConstants.METADATA_TABLE_TABLET_PREV_ROW_COLUMN_NAME), null));
        }

		private synchronized TabletLocation get(KeyExtent extent, boolean skipRow)
		{
			//log.debug("trying to locate: " + extent);
			if(skipRow && extent.getEndRow() == null)
				throw new IllegalArgumentException("skipping a null row ???");

			//dumpEntries();
			SortedMap<KeyExtent,Text> tailmap = entries.tailMap(extent);
			if(tailmap == null || tailmap.size() == 0)  {
				tlcLog.debug("found no cached loc for " + extent);
				return null;
			}
			Iterator<Entry<KeyExtent,Text>> i = tailmap.entrySet().iterator();
			Entry<KeyExtent,Text> value = i.next();
			if(value.getKey().getTableName().equals(extent.getTableName()))
			{
				if(skipRow && value.getKey().getPrevEndRow() != null && value.getKey().getPrevEndRow().equals(extent.getEndRow()))
					return new TabletLocation(value.getKey(), value.getValue().toString());
				if(value.getKey().contains(extent.getEndRow()))
				{
					if(skipRow && value.getKey().getEndRow() != null && value.getKey().getEndRow().equals(extent.getEndRow()))
					{
						if(!i.hasNext())
							return null;
						value = i.next();
						// make sure the next tablet is the one immediately after the previous tablet
						// check to make sure it's the same table
						if(!value.getKey().getTableName().equals(extent.getTableName()))
							return null;
						// check to make sure the end key of the previous tablet matches the previous end key of this tablet
						if(value.getKey().getPrevEndRow() == null || !value.getKey().getPrevEndRow().equals(extent.getEndRow()))
							return null;
						return new TabletLocation(value.getKey(), value.getValue().toString());
					}
					else
						return new TabletLocation(value.getKey(), value.getValue().toString());
				}
			}
			return null;
		}

		private synchronized void cacheResults(SortedMap<Key, Value> results)
		{
			SortedMap<KeyExtent, Text> metadata = MetadataTable.getMetadataLocationEntries(results);

			if (metadata.size() > 0)
			{
			    // Remove any overlapping keys from the current cache
			    Set<KeyExtent> doomed = new TreeSet<KeyExtent>(metadata.subMap(metadata.firstKey(), metadata.lastKey()).keySet());
			    entries.keySet().removeAll(doomed);
			}
			entries.putAll(metadata);
		}

		/**
		 * new recursive tablet location method
		 * @throws CBException 
		 */
		private TabletLocation locateTablet(Text table, Text row, boolean skipRow)
		throws CBException
		{
			int comparison_result = row.compareTo(KeyExtent.getMetadataEntry(new Text(CBConstants.METADATA_TABLE_NAME),null));

			synchronized (this){
				if(table.equals(new Text(CBConstants.METADATA_TABLE_NAME)) &&
						((skipRow && comparison_result < 0) || (!skipRow && comparison_result <= 0))) 
				{
					// might as well clear the cache at this point?
					entries.clear();
					String loc;

					loc = instance.getRootTabletLocation();
					if (loc == null)
					    return null;

					entries.put(CBConstants.ROOT_TABLET_EXTENT, new Text(loc));
					return new TabletLocation(CBConstants.ROOT_TABLET_EXTENT, loc);
				}
			}
						
			KeyExtent extent = new KeyExtent(table, row, null);

			//Following code IS NOT SYNCHRONIZED because it does I/O
			//and does not directly access entries.  All methods it
			//calls that do access entries are syncronized.  
			for(int tries = 0; tries < 1; tries++)
			{
				// lookup in cache
				TabletLocation tablet = get(extent, skipRow);
				// cache hit - return
				if(tablet != null)
					return tablet;

				// cache miss
				Text metadataRow = extent.getMetadataEntry();

				// else recurse up the hierarchy, prepending the metadata table name as we go
				TabletLocation higherTablet = locateTablet(new Text(CBConstants.METADATA_TABLE_NAME), metadataRow, skipRow);
				// propagate nulls down
				if(higherTablet == null)
					return null;

				TreeMap<Key, Value> results = new TreeMap<Key, Value>();
				// read in cache entries
				
				
				try {
					boolean servingTablet = lookupInMetadataTablet(credentials, skipRow, metadataRow, higherTablet, results);
					
					//System.out.println("DEBUG : results = "+results);
					
					while(!servingTablet) {
						// invalidate what we just read and try again
						invalidate(higherTablet.tablet_extent);
						results.clear();
						higherTablet = locateTablet(new Text(CBConstants.METADATA_TABLE_NAME), metadataRow, false);
						if(higherTablet == null)
							return null;
						
						servingTablet = lookupInMetadataTablet(credentials, skipRow, metadataRow, higherTablet, results);
					}
						 
					cacheResults(results);

					// check cache again - might be null
					TabletLocation location = get(extent, skipRow); 
					if (location == null)
					    synchronized (entries) {
					        entries.clear();
					    }
					return location;
				}
				catch (CBSecurityException e) {
					tlcLog.error("Authentication exception in locateTablet()");
					return null;
				}
			}
			return null;
		}

		private boolean lookupInMetadataTablet(AuthInfo credentials, boolean skipRow, Text metadataRow, TabletLocation higherTablet, TreeMap<Key, Value> results)
		throws CBException, CBSecurityException
		{
			try {
				boolean more = ThriftScanner.getBatchFromServer(credentials, metadataRow, higherTablet.tablet_extent, higherTablet.tablet_location, results, locCols, false, CBConstants.SCAN_BATCH_SIZE, CBConstants.NO_AUTHS);
				while(more && results.size() <= (skipRow ? 4 : 2)){
					more = ThriftScanner.getBatchFromServer(credentials, results.lastKey().followingKey(5), null, higherTablet.tablet_extent, higherTablet.tablet_location, results, locCols, CBConstants.SCAN_BATCH_SIZE, CBConstants.NO_AUTHS);
				}
			} catch (NotServingTabletException e) {
				return false;
			} catch (CBException e) {
				return false;
			}
			return true;
		}


		private synchronized void invalidate(KeyExtent tablet_extent) {
			//if(entries.containsKey(tablet_extent)) {
				//System.err.println("invalidating location cache entry " + tablet_extent);
				entries.remove(tablet_extent);
			//}
		}

		@Override
		public void binMutations(List<Mutation> mutations,
				Map<String, TabletServerMutations> binnedMutations,
				List<Mutation> failures) {
			
			Text row = new Text();
			
			for (Mutation mutation : mutations)
			{
				row.set(mutation.getRow());
				try {
					TabletLocation tl = locateTablet(row, false);
					
					if(tl == null || tl.tablet_location == null)
					{
						failures.add(mutation);
						continue;
					}
					
					TabletServerMutations tsm = binnedMutations.get(tl.tablet_location);
					
					if (tsm == null)
					{
						tsm = new TabletServerMutations();
						binnedMutations.put(tl.tablet_location, tsm);
					}
					
					tsm.addMutation(tl.tablet_extent, mutation);
				} catch (Exception e) {
					failures.add(mutation);
				}
			}
		}

		@Override
		public void invalidateCache(KeyExtent failedExtent)
		{
			invalidate(failedExtent);
		}

		@Override
		public synchronized void invalidateCache(String server)
		{
			Iterator<Entry<KeyExtent, Text>> iterator = entries.entrySet().iterator();
			
			while (iterator.hasNext())
				if (iterator.next().getValue().toString().equals(server))
					iterator.remove();
		}

		public TabletLocation locateTablet(Text row, boolean skipRow)
		throws CBException
		{
			return locateTablet(tableName, row, skipRow);
		}

		@Override
		public void binRanges(List<Range> ranges, Map<String, Map<KeyExtent, List<Range>>> binnedRanges)
		{
			throw new UnsupportedOperationException();
		}

		@Override
		public SortedMap<KeyExtent, TabletLocation> getTablets()
		{
			throw new UnsupportedOperationException();
		}

	}
	
	
	/**
	 * A complete cahce of the metadata entries for a particular table. 
	 * This cache is suitable for bulk operations.
	 * 
	 *
	 */
	private static class MetadataCache extends TabletLocator
	{
	
		private static Logger log = Logger.getLogger(MetadataCache.class.getName());
		
		private long totalMetadataQueryTime = 0;
		private int numberOfMetadataQueries = 0;
		private long totalMetadataScanTime = 0;
		private int numberOfMetadataScans = 0;
		private Instance instance = null;
		
		TreeMap<KeyExtent, TabletLocation> metaCache = new TreeMap<KeyExtent, TabletLocation>();
		private TreeSet<KeyExtent> badExtents = new TreeSet<KeyExtent>();

		private Text tableName;

		private AuthInfo credentials;
		
		MetadataCache(Instance instance, AuthInfo credentials, Text tableName)
		{
		    this.instance = instance;
		    this.credentials = credentials;
			this.tableName = tableName;
		}
		
		
		private int updateMetadataCache()
		throws CBException, CBSecurityException, TableNotFoundException
		{
			synchronized (metaCache)
			{
				synchronized (badExtents)
				{
					return _updateMetadataCache();
				}
			}
		}
		
		private int _updateMetadataCache() throws CBException, CBSecurityException, TableNotFoundException
		{
			int updates = 0;

			if(metaCache.size() > 0 && badExtents.size() == 0){
				//nothing to do
				return 0;
			}

			//see if its quicker to scan !METADATA or do individual queries.. this decision is made using historical times

			StringBuilder debugString = new StringBuilder();
			
			if(metaCache.size() > 0 && numberOfMetadataQueries > 0){
				double expectedScanTime = totalMetadataScanTime / (double)numberOfMetadataScans;
				//System.out.printf("DEBUG : Expected scan time  : %6.2f\n", expectedScanTime/1000.0);

				//time to do indvidual metadata lookup
				double expectedLookupTime = totalMetadataQueryTime / (double)numberOfMetadataQueries;

				//expected time to do all queries
				double expectedQueryTime = expectedLookupTime * badExtents.size();
				//System.out.printf("DEBUG : Expected query time : %6.2f badExtents.size() = %d\n", expectedQueryTime/1000.0, badExtents.size());

				if(expectedScanTime < expectedQueryTime){
					//looks like it will be quicker to scan metadata table, so do that
					//metaCache.clear();
					//System.out.printf("DEBUG : Cleared metadata cache\n");
				}

				debugString.append(String.format("exp_scan:%.3f exp_query: %.3f num_query:%d", expectedScanTime/1000.0, expectedQueryTime/1000.0, badExtents.size()));
				
			}

			if(metaCache.size() == 0){
				long t1 = System.currentTimeMillis();
				Map<KeyExtent, String> locations = null;
				SortedSet<KeyExtent> tablets = null;

				do{
					try {
						locations = new HashMap<KeyExtent, String>();
						tablets = new TreeSet<KeyExtent>();
						//TODO reuse table object... this call creates a new table object each time
						BulkOperations.getMetadataEntries(instance, credentials, tableName.toString(), locations, tablets);
					} catch (CBException e) {
					    log.warn("Exception Locating metadata entries: " + e + ", retrying.");
						locations = null;
						tablets = null;

						UtilWaitThread.sleep(1000);
					} catch (CBSecurityException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}while(locations == null);

				if(tablets.size() == 0)
					throw new TableNotFoundException(tableName.toString());

				for (KeyExtent keyExtent : tablets)
				{
					TabletLocation ce = new TabletLocation(keyExtent, locations.get(keyExtent));

					metaCache.put(keyExtent, ce);
					updates++;
				}

				long t2 = System.currentTimeMillis();

				debugString.append(String.format(" act_scan:%.3f", (t2 - t1)/1000.0));
				
				totalMetadataScanTime += (t2 - t1);
				numberOfMetadataScans++;

			}
			else
			{
				for (KeyExtent badExtent : badExtents)
				{
					metaCache.remove(badExtent);

					List<Text> columns = Arrays.asList(CBConstants.METADATA_TABLE_TABLET_PREV_ROW_COLUMN_NAME, CBConstants.METADATA_TABLE_TABLET_LOCATION_COLUMN_NAME);

					SortedSet<KeyExtent> newExtents = new TreeSet<KeyExtent>();
					Map<KeyExtent, String> locations = new HashMap<KeyExtent, String>();

					long t1 = System.currentTimeMillis();

					while(true)
					{
						try {
							SortedMap<Text, SortedMap<Text, Value>> tabletEntries = 
								MetadataTable.getTabletEntries(badExtent, columns , credentials);

							newExtents.clear();
							locations.clear();

							for (Entry<Text, SortedMap<Text, Value>> entry : tabletEntries.entrySet()) {

								if(badExtent.getPrevEndRow() != null)
								{
									Text prevRowMetadataEntry = new Text(KeyExtent.getMetadataEntry(badExtent.getTableName(), badExtent.getPrevEndRow()));
									if(entry.getKey().compareTo(prevRowMetadataEntry) <= 0)
										continue;
								}

								Value prevRowIBW = 
									entry.getValue().get(CBConstants.METADATA_TABLE_TABLET_PREV_ROW_COLUMN_NAME);

								if(prevRowIBW == null)
								{
									log.warn("!METADATA problem, "+entry.getKey()+" has no prev end row!");
									continue;
								}

								KeyExtent ke = new KeyExtent(entry.getKey(), prevRowIBW);

								newExtents.add(ke);

								Value locationIBW = 
									entry.getValue().get(CBConstants.METADATA_TABLE_TABLET_LOCATION_COLUMN_NAME);

								if(locationIBW != null){
									locations.put(ke, locationIBW.toString());
								}
							}

							if(!BulkOperations.isContiguousRange(badExtent, newExtents))
							{
								log.warn("Could not find updates in !METADATA for "+badExtent+", found "+newExtents);
								UtilWaitThread.sleep(1000);
								continue;
							}

						} catch (CBException e) {
							e.printStackTrace();
							UtilWaitThread.sleep(1000);
							continue;
						}

						break;
					}

					long t2 = System.currentTimeMillis();
					
					debugString.append(String.format(" act_query:%.3f", (t2 - t1)/1000.0));

					totalMetadataQueryTime += (t2 - t1);
					numberOfMetadataQueries++;

					for (KeyExtent newExtent : newExtents) {
						TabletLocation ce = new TabletLocation(newExtent, locations.get(newExtent));
						metaCache.put(newExtent, ce);
						updates++;
					}
				}

				badExtents.clear();
			}

			log.debug(debugString.toString());
			
			return updates;
		}

		public void invalidateCache(KeyExtent failedExtent)
		{
			//TODO could update metadata entries in background while sendind data
			//to tablet servers
			synchronized (badExtents)
			{
				badExtents.add(failedExtent);
			}
		}
		
		/**
		 * Invalidate all metadata entries that point to server s
		 */
		public void invalidateCache(String server)
		{
			synchronized (metaCache)
			{
				for (TabletLocation cacheEntry : metaCache.values())
					if(cacheEntry.tablet_location.equals(server))
						invalidateCache(cacheEntry.tablet_extent);
			}
		}

		@Override
		public void binMutations(List<Mutation> mutations, Map<String, TabletServerMutations> binnedMutations, List<Mutation> failures)
		throws CBException, CBSecurityException, TableNotFoundException
		{
			synchronized (metaCache)
			{
				updateMetadataCache();
				
				KeyExtent lookupKey = new KeyExtent();
				lookupKey.setTableName(new Text(tableName));
				lookupKey.setPrevEndRow(null);
				
				Text row = new Text();
				
				for (Mutation mutation : mutations) {
					row.set(mutation.getRow());
					lookupKey.setEndRow(row);
					
					Entry<KeyExtent, TabletLocation> entry = metaCache.ceilingEntry(lookupKey);
						
					if(entry.getValue().tablet_location == null){
						failures.add(mutation);
						continue;
					}
						
					TabletServerMutations tsm = binnedMutations.get(entry.getValue().tablet_location);
						
					if(tsm == null){
						tsm = new TabletServerMutations();
						binnedMutations.put(entry.getValue().tablet_location, tsm);
					}
						
					tsm.addMutation(entry.getValue().tablet_extent, mutation);
				}
			}
		}

		@Override
		public TabletLocation locateTablet(Text row, boolean skipRow)
		throws CBException, CBSecurityException, TableNotFoundException
		{
			synchronized (metaCache) {
				updateMetadataCache();
				
				KeyExtent lookupKey = new KeyExtent(tableName, row, null);
			
				SortedMap<KeyExtent, TabletLocation> tailMap = metaCache.tailMap(lookupKey);
			
				Iterator<Entry<KeyExtent, TabletLocation>> iter = tailMap.entrySet().iterator();
				Entry<KeyExtent, TabletLocation> entry = iter.next();
			
				if(skipRow && entry.getKey().getEndRow() != null && entry.getKey().getEndRow().equals(row)){
					entry = iter.next();
				}
			
				if(entry.getValue().tablet_location == null){
					return null;
				}
				
				return entry.getValue();
			}
		}

		@Override
		public void binRanges(List<Range> ranges, Map<String, Map<KeyExtent, List<Range>>> binnedRanges)
		throws CBException, CBSecurityException, TableNotFoundException
		{
			synchronized (metaCache) {
				updateMetadataCache();
				
				TabletLocator.binRanges(tableName, ranges, binnedRanges, metaCache);
			}
		}


		@Override
		public SortedMap<KeyExtent, TabletLocation> getTablets()
		throws CBException, CBSecurityException, TableNotFoundException
		{
			synchronized (metaCache) {
				updateMetadataCache();
				
				return new TreeMap<KeyExtent, TabletLocation>(metaCache);
			}
		}
	}
	
	static void binRanges(Text tablename, List<Range> ranges, 
			Map<String, Map<KeyExtent, List<Range>>> binnedRanges,
			TreeMap<KeyExtent, TabletLocation> metaCache) {
		
		KeyExtent lookupKey = new KeyExtent();
		lookupKey.setTableName(tablename);
		lookupKey.setPrevEndRow(null);
		
		Text row = new Text();
		
		for (Range range : ranges) {
			//find which tablets this range goes to
			lookupKey.setEndRow(range.getStartKey().getRow(row));
			
			SortedMap<KeyExtent, TabletLocation> tailMap = metaCache.tailMap(lookupKey);
			
			for(Entry<KeyExtent, TabletLocation> entry : tailMap.entrySet()){
				TabletLocation cacheEntry = entry.getValue();
				
				Map<KeyExtent, List<Range>> tablets = binnedRanges.get(cacheEntry.tablet_location);
				if(tablets == null){
					tablets = new HashMap<KeyExtent, List<Range>>();
					binnedRanges.put(cacheEntry.tablet_location, tablets);
				}
				
				List<Range> tabletsRanges = tablets.get(cacheEntry.tablet_extent);
				if(tabletsRanges == null){
					tabletsRanges = new ArrayList<Range>();
					tablets.put(cacheEntry.tablet_extent, tabletsRanges);
				}
				
				tabletsRanges.add(range);
				
				if(entry.getKey().contains(range.getEndKey().getRow(row))){
					break;
				}
			}
		}
	}
	
}
