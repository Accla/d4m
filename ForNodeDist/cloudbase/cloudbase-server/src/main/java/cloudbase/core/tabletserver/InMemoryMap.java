package cloudbase.core.tabletserver;

import java.io.IOException;
import java.util.Iterator;
import java.util.Random;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.log4j.Logger;

import cloudbase.core.aggregation.Aggregator;
import cloudbase.core.aggregation.conf.AggregatorSet;
import cloudbase.core.conf.CBConfiguration;
import cloudbase.core.data.ColumnUpdate;
import cloudbase.core.data.Key;
import cloudbase.core.data.KeyExtent;
import cloudbase.core.data.Mutation;
import cloudbase.core.data.MyMapFile;
import cloudbase.core.data.Value;
import cloudbase.core.tabletserver.iterators.DeletingIterator;
import cloudbase.core.tabletserver.iterators.IteratorUtil;
import cloudbase.core.tabletserver.iterators.SortedKeyValueIterator;
import cloudbase.core.tabletserver.iterators.SortedMapIterator;
import cloudbase.core.tabletserver.iterators.IteratorUtil.IteratorScope;
import cloudbase.core.util.MapFileUtil;
import cloudbase.core.util.UtilWaitThread;
import cloudbase.core.util.MetadataTable.SSTableValue;


// TODO make this copy-on-write and allow writes to proceed in parallel

public class InMemoryMap {
	// TODO: initialize this to the mutation log for the tablet server write thread
	MutationLog mutationLog;
	
	private TreeMap<Key, Value> map = new TreeMap<Key, Value>();
	private long bytesInMemory;

	private AggregatorSet ingestAggSet;
	private static Logger log = Logger.getLogger(InMemoryMap.class.getName());
	
	public InMemoryMap(){
	}
	
	public InMemoryMap(AggregatorSet aggSet){
		this.ingestAggSet = aggSet;
	}
	
	
	
	/**
	 * Applies changes to a row in the InMemoryMap
	 * 
	 */
	public void mutate(Mutation m) {
	
		for (ColumnUpdate cvp : m.getUpdates()) {
			Key newKey = new Key(m.getRow(), cvp.getColumnFamily(), cvp.getColumnQualifier(), cvp.getColumnVisibility(), cvp.getTimestamp(), cvp.isDeleted());
			
			Value value = new Value(cvp.getValue());
			
			
			Aggregator agg = ingestAggSet.getAggregator(newKey);
			if(agg != null){
				value = aggregateExactKey(newKey, value, agg);
			}
			
			//insert key and value into map and update bookkeeping.
			bytesInMemory += newKey.getLength();
			
			bytesInMemory += value.getSize();
			map.put(newKey, value);
			
			//check if aggregation is needed
			if(agg != null){
				aggregate(newKey, agg);
			}

			//log.debug("IN_MEM_MAP MUTATE : put " + newKey + " and " + value);
		}
	}

	private void aggregate(Key newKey, Aggregator agg) {
		Value value;
		//newKey is already in the map, do not want to modify it,
		//so create a copy... lookupKey can not change while iterating
		//over the tail map
		Key lookupKey = new Key(newKey);
		lookupKey.setTimestamp(Long.MAX_VALUE);
		SortedMap<Key, Value> tm = tailmap(lookupKey);
		Iterator<Entry<Key, Value>> iter = tm.entrySet().iterator();
		
		int count = 0;
		int aggregationThreshold = 2;
		
		//the following while loop checks to see if there is anything to aggregate
		while(iter.hasNext()){
			Entry<Key, Value> entry = iter.next();
			
			if(entry.getKey().isDeleted()){
				//remove everything after the delete, even other deletes
				//only need to keep the most recent delete
				while(iter.hasNext()){
					entry = iter.next();
					if(entry.getKey().compareTo(newKey, 4) == 0){
						bytesInMemory -= entry.getKey().getLength();
						bytesInMemory -= entry.getValue().getSize();
						iter.remove();
					}else{
						break;
					}
				}
				break;
			}
			
			if(entry.getKey().compareTo(newKey, 4) == 0){
				count++;
			}else{
				break;
			}
		}
		
		if(count >= aggregationThreshold){
			//we have entries to aggregate
			long maxTS = Long.MIN_VALUE;
			agg.reset();
			
			iter = tm.entrySet().iterator();
		
			while(iter.hasNext()){
				Entry<Key, Value> entry = iter.next();
			
				if(entry.getKey().isDeleted()){
					break;
				}
			
				if(entry.getKey().compareTo(newKey, 4) == 0){
					if(entry.getKey().getTimestamp() > maxTS){
						maxTS = entry.getKey().getTimestamp();
					}
					
					agg.collect(entry.getValue());
				
					//log.debug("Collecting "+entry.getValue()+" for "+entry.getKey());
				
					bytesInMemory -= entry.getKey().getLength();
					bytesInMemory -= entry.getValue().getSize();
				
					iter.remove();
				}else{
					break;
				}
			}
			
			//newKey object may still be in the map, so do not modify it
			//we no longer need lookupKey since iteration over the tailmap
			//is finished, so modify lookupKey and use it for the insert
			lookupKey.setTimestamp(maxTS);
			value = agg.aggregate();
			
			bytesInMemory += lookupKey.getLength();
			bytesInMemory += value.getSize();
			
			map.put(lookupKey, value);
		}
	}

	private Value aggregateExactKey(Key newKey, Value value, Aggregator agg) {
		
		if(newKey.isDeleted()){
			return value;
		}
		
		//before inserting anything check if the exact same key already exist in the map
		Value v = map.get(newKey);
		
		if(v != null){
			agg.reset();
			agg.collect(value);
			agg.collect(v);
			
			value = agg.aggregate();
		}
		
		return value;
	}

	
	/**
	 * Writes the InMemoryMap to a MapFile
	 * 
	 * @param conf
	 * @param fs
	 * @param dirname
	 */
	public SSTableValue minorCompact(Configuration conf, FileSystem fs, String dirname, KeyExtent extent)
	{
		// TODO output fact of minor compaction to log
		
		// output to new MapFile with a temporary name
		
		boolean failed;
		int sleepTime = 100;
		double exponent = 2;
		int maxSleepTime = 1000 * 5 * 60; //TODO make configurable
		
		
		do{
			failed = false;
			try {
				// TODO: the directory name can possibly confict with other directories: change the append .tmp to prepend tmp_
				//System.out.println("Outputting map file " + dirname + "_tmp");

				// TODO add table or locality group level compression information to the metadata table, use it here
				MyMapFile.Writer mfw;

				long t1 = System.currentTimeMillis();
				
				mfw = MapFileUtil.openMapFileWriter(extent.getTableName(), conf, fs, dirname);

				DeletingIterator sourceItr = new DeletingIterator(new SortedMapIterator<Key, Value>(map) , true);
				SortedKeyValueIterator<Key, Value> fai = IteratorUtil.loadIterators(IteratorScope.minc, sourceItr, extent, CBConfiguration.getInstance(extent.getTableName().toString()));

				long entriesCompacted = 0;

				Text firstRow = null;
				Text lastRow = new Text();
				
				while(fai.hasTop()){
					
					if(firstRow == null){
						firstRow = ((Key)fai.getTopKey()).getRow();
					}
					
					//TODO avoid copy into last row
					((Key)fai.getTopKey()).getRow(lastRow);
					
					mfw.append(fai.getTopKey(), fai.getTopValue());
					fai.next();
					entriesCompacted++;
				}
				
				mfw.close();

				if(firstRow == null){
					lastRow = null;
				}
				
				long t2 = System.currentTimeMillis();
				log.debug(String.format("MinC %,d recs in | %,d recs out | %,d recs/sec | %6.3f secs | %,d bytes | %,d est bytes",map.size(), entriesCompacted, (int)(map.size()/((t2 - t1)/1000.0)), (t2 - t1)/1000.0, bytesInMemory, size()));

				return new SSTableValue(
					fs.getFileStatus(new Path(dirname.toString() + "/" + MyMapFile.DATA_FILE_NAME)).getLen(),
					entriesCompacted,
					firstRow,
					lastRow
				);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				log.warn("MinC failed ("+e.getMessage()+") to create "+dirname+" retrying ...");
				failed = true;
			}
		
			if(failed){
				Random random = new Random();
				random.nextInt(sleepTime);
				
				int sleep = sleepTime + random.nextInt(sleepTime);
				log.debug("MinC failed sleeping "+sleep+" ms before retrying");
				UtilWaitThread.sleep(sleep);
				sleepTime = (int)Math.round(Math.min(maxSleepTime, Math.pow(sleepTime, exponent)));
				
				//clean up
				try {
					if(fs.exists(new Path(dirname))){
						fs.delete(new Path(dirname), true);
					}
				} catch (IOException e) {
					e.printStackTrace();
					log.warn("Failed to delete failed MinC file "+dirname+" "+e.getMessage());
				}
				
			}
			
		}while(failed);
		
		return null;
	}

	/**
	 * Returns a long representing the size of the InMemoryMap
	 * 
	 * @return bytesInMemory
	 */
	public long size()
	{
		//all of the java objects that are used to hold the
		//data and make it searchable have overhead... this
		//overhead is esitimated using test.EstimateInMemMapOverhead
		//and is in bytes.. the estimates were obtained by running
		//java 6_7 in 64 bit server mode
		
		int overheadPerEntry = 203;
		
		return bytesInMemory + (map.size() * overheadPerEntry);
	}

	/**
	 * Returns a view of the portion of this map whose keys are greater than or equal to k
	 * 
	 * @param k
	 * @return tailMap
	 */
	public SortedMap<Key, Value> tailmap(Key k) {
		//return a tail map starting at the key with the largest timestamp
		
		//long origTimestamp = k.getTimestamp();
		
		Key startKey = new Key(k);
		//startKey.setTimestamp(Long.MAX_VALUE);
		
		SortedMap<Key, Value> tm = map.tailMap(startKey);
		
		return tm;
	}

	/**
	 * Returns an iterator of the entrySet
	 * 
	 * @return iterator
	 */
	public Iterator<Entry<Key,Value>> iterator()
	{
		return map.entrySet().iterator();
	}


	public long getNumEntries() {
		return map.size();
	}


	public void setIngestAggregator(AggregatorSet ingestAggregators) {
		this.ingestAggSet = ingestAggregators;
	}

	public SortedKeyValueIterator<Key, Value> skvIterator() {
		return new SortedMapIterator<Key, Value>(map);
	}
}
