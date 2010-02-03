package cloudbase.core.tabletserver;


/*
 * We need to be able to have the master tell a tabletServer to
 * close this file, and the tablet server to handle all pending client reads
 * before closing
 * 
 */

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Map.Entry;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.log4j.Logger;

import cloudbase.core.CBConstants;
import cloudbase.core.aggregation.conf.AggregatorSet;
import cloudbase.core.client.CBException;
import cloudbase.core.client.CBSecurityException;
import cloudbase.core.client.TableNotFoundException;
import cloudbase.core.client.impl.HdfsZooInstance;
import cloudbase.core.client.impl.ScannerImpl;
import cloudbase.core.conf.CBConfiguration;
import cloudbase.core.conf.ConfigurationObserver;
import cloudbase.core.constraints.ConstraintChecker;
import cloudbase.core.constraints.ConstraintLoader;
import cloudbase.core.constraints.UnsatisfiableConstraint;
import cloudbase.core.constraints.Violations;
import cloudbase.core.data.Column;
import cloudbase.core.data.ColumnUpdate;
import cloudbase.core.data.IterInfo;
import cloudbase.core.data.Key;
import cloudbase.core.data.KeyExtent;
import cloudbase.core.data.KeyValue;
import cloudbase.core.data.Mutation;
import cloudbase.core.data.MyMapFile;
import cloudbase.core.data.MySequenceFile;
import cloudbase.core.data.Range;
import cloudbase.core.data.Value;
import cloudbase.core.security.LabelConversions;
import cloudbase.core.security.LabelExpressionFormatException;
import cloudbase.core.security.thrift.AuthInfo;
import cloudbase.core.tabletserver.TabletServerResourceManager.TabletResourceManager;
import cloudbase.core.tabletserver.iterators.AggregatingIterator;
import cloudbase.core.tabletserver.iterators.CountingIterator;
import cloudbase.core.tabletserver.iterators.DeletingIterator;
import cloudbase.core.tabletserver.iterators.IteratorUtil;
import cloudbase.core.tabletserver.iterators.MultiIterator;
import cloudbase.core.tabletserver.iterators.ScanCache;
import cloudbase.core.tabletserver.iterators.SequenceFileIterator;
import cloudbase.core.tabletserver.iterators.SortedKeyValueIterator;
import cloudbase.core.tabletserver.iterators.SystemScanIterator;
import cloudbase.core.tabletserver.iterators.TimeLimitIterator;
import cloudbase.core.tabletserver.iterators.IteratorUtil.IteratorScope;
import cloudbase.core.tabletserver.log.MutationReceiver;
import cloudbase.core.tabletserver.log.TabletLog;
import cloudbase.core.util.Encoding;
import cloudbase.core.util.MapFileUtil;
import cloudbase.core.util.MetadataTable;
import cloudbase.core.util.Stat;
import cloudbase.core.util.UtilWaitThread;
import cloudbase.core.util.MetadataTable.SSTableValue;
import cloudbase.core.zookeeper.ZooConstants;

import com.facebook.thrift.TException;

/**
 * 
 *
 * this class just provides an interface to read from a MapFile
 * mostly takes care of reporting start and end keys
 * 
 * need this because a single row extent can have multiple columns
 * this manages all the columns (each handled by a store) for a single row-extent
 * 
 *
 */

// TODO support loading mapfiles into memory
public class Tablet {
    
	private class TabletMemory {
	    private InMemoryMap memTable;
	    private InMemoryMap otherMemTable;
        private AggregatorSet ingestAggregators;
        private long lastMutateTime;
        
        TabletMemory(){
            memTable = new InMemoryMap();
        }
        
        InMemoryMap getMemTable(){
            return memTable;
        }
	    
        InMemoryMap getMinCMemTable(){
            return otherMemTable;
        }
        
	    void prepareForMinC(){
	        if(otherMemTable != null){
	            throw new IllegalStateException();
	        }
	        
	        otherMemTable = memTable;
	        if(ingestAggregators != null){
	            memTable = new InMemoryMap(ingestAggregators);
	        }else{
	            memTable = new InMemoryMap();
	        }
	        
	        tabletResources.updateMemoryUsageStats(memTable.size(), lastMutateTime, otherMemTable == null ? 0L : otherMemTable.size());
	    }
	    
	    void finishedMinC(){
	        
	        if(otherMemTable == null){
	            throw new IllegalStateException();
	        }

	        otherMemTable = null;
	        Tablet.this.notifyAll();
	        
	        tabletResources.updateMemoryUsageStats(memTable.size(), lastMutateTime, otherMemTable == null ? 0L : otherMemTable.size());
	    }
	    
	    boolean memoryReservedForMinC(){
	        return otherMemTable != null;
	    }
	    
	    void waitForMinC(){
	        while(otherMemTable != null){
	            try {
	                Tablet.this.wait();
	            } catch (InterruptedException e) {
	                log.warn(e);
	            }
	        }
	    }
	    
	    void mutate(Mutation m){
	        memTable.mutate(m);
	        lastMutateTime = System.currentTimeMillis();
	        tabletResources.updateMemoryUsageStats(memTable.size(), lastMutateTime, otherMemTable == null ? 0L : otherMemTable.size());
	    }
	    
	    public void setIngestAggregator(AggregatorSet ingestAggregators) {
	        this.ingestAggregators = ingestAggregators;
	        memTable.setIngestAggregator(ingestAggregators);
	    }
	    
	    SortedKeyValueIterator[] getIterators() {
	        if (otherMemTable == null)
	            return new SortedKeyValueIterator[]{memTable.skvIterator()};
	        else
	            return new SortedKeyValueIterator[]{memTable.skvIterator(), otherMemTable.skvIterator()};
	    }

        public long getNumEntries() {
            if (otherMemTable != null)
                return memTable.getNumEntries() + otherMemTable.getNumEntries();
            else
                return memTable.getNumEntries();
        }
	}

	private TabletMemory tabletMemory;
	
	private Path location; //absolute path of this tablets dir

	private Configuration conf;
	private FileSystem fs;

	private CBConfiguration cbConf;

	private static AuthInfo systemCredentials_ = null;
	
	private static AuthInfo systemCredentials()
	{
		if (systemCredentials_ == null)
			systemCredentials_ = new AuthInfo(ZooConstants.SYSTEM_USERNAME, ZooConstants.SYSTEM_PASSWORD);
		return systemCredentials_;
	}
	
	private int mapfileSequence = 0;
	private int generation = 0;
	private boolean closed = false;
	
	private KeyExtent extent;
	private int splitHoldRequests = 0;
	
	
	private TabletResourceManager tabletResources;
	private SSTableManager sstableManager;
    private boolean majorCompactionInProgress = false;
	private boolean majorCompactionWaitingToStart = false;
	private boolean minorCompactionInProgress = false;
	private boolean minorCompactionWaitingToStart = false;
	
	private ConstraintChecker constraintChecker;
	
	private String tabletDirectory;
	
	private TabletLog walog;
	
	static Logger log = Logger.getLogger(Tablet.class.getName());
	public TabletTimer timer;

	
	private double prevQueryRate;
	private long queryCount;
	private double prevIngestRate;
	private long ingestCount;
	
	private byte[] defaultSecurityLabel = new byte[0];
	
	private ScanCache scanCache;
	private boolean useScanCache;
	private long lastMinorCompactionFinishTime;
	private long lastMapFileImportTime;
	
	private volatile long numEntries;
	private volatile long numEntriesInMemory;
	private ConfigurationObserver configObserver;
	
	// TODO implement this thing for quick lookups?

	@SuppressWarnings("serial")
    public static class TabletClosedException extends Exception{
		
	}
	
	private void incrementGeneration(){
		generation++;
		mapfileSequence = 0;
	}
	
	private String getMapFilename(int gen, int seq){
		return location.toString() + "/map_" + String.format("%05d", gen)+"_"+String.format("%05d", seq);
	}
	
	private String getNextMapFilename(){
		return getMapFilename(generation, mapfileSequence++);
	}
	
	private void initGeneration(Path tabletDir, Set<String> mapFileNames) throws IOException {

		//create a copy of the set since this method adds to it
		mapFileNames = new TreeSet<String>(mapFileNames);
		
		//add all files in tablet dir, as they may not be listed in 
		//!METADATA table... do not want to try and create a map file
		//using an existing file name
		FileStatus[] files = fs.listStatus(tabletDir);
		for (FileStatus fileStatus : files) {
			//log.debug("Adding file "+fileStatus.getPath().getName());
			mapFileNames.add(fileStatus.getPath().getName());
		}
		
		int maxGen = 0;

		//find largest gen num
		for (String file : mapFileNames) {
			int gen = Integer.parseInt((new Path(file)).getName().split("_")[1]);
			if(gen > maxGen){
				maxGen = gen;
			}
		}
		
		generation = maxGen;
		
		int maxSeq = -1;
		
		//find largest Seq
		for (String file : mapFileNames) {
			String split[] = (new Path(file)).getName().split("_");
			
			int gen = Integer.parseInt(split[1]);
			int seq = Integer.parseInt(split[2]);
			
			if(gen == maxGen && seq > maxSeq){
				maxSeq = seq;
			}
		}
		
		mapfileSequence = maxSeq + 1;
	}
	
	public boolean isMetadataTablet()
	{
		return extent.getTableName().toString().equals(CBConstants.METADATA_TABLE_NAME);
	}
	
	class SSTableManager {
		private TreeMap<String, SSTableValue> sstableSizes;
		private SortedMap<String, SSTableValue> unModMap;
		SSTableManager(SortedMap<String, SSTableValue> sstableSizes){
			this.sstableSizes = new TreeMap<String, SSTableValue>(sstableSizes);
			unModMap = Collections.unmodifiableSortedMap(this.sstableSizes);
			
			for(Entry<String, SSTableValue> sstable : sstableSizes.entrySet()) {
				try {
					tabletResources.addMapFile(CBConstants.TABLES_DIR + "/" + extent.getTableName() + sstable.getKey());
				}
				catch (IOException e) {
					log.error("ioexception trying to open " + sstable);
					continue;
				}
			}
		}
		
		public void importMapFiles(Map<String, SSTableValue> paths) throws IOException {
			
			Map<String, String> relPaths = new HashMap<String, String>(paths.size());
			Map<String, SSTableValue> relSizes = new HashMap<String, SSTableValue>(paths.size());
			
			for (String tpath : paths.keySet()) {
				Path tmpPath = new Path(tpath);
				
				if(!tmpPath.getParent().getParent().equals(new Path(CBConstants.TABLES_DIR + "/" + extent.getTableName()))){
					throw new IOException("Map file "+tpath+" not in table dir "+CBConstants.TABLES_DIR + "/" + extent.getTableName());
				}
				
				String path = "/"+ tmpPath.getParent().getName() + "/" + tmpPath.getName();
				relPaths.put(tpath, path);
				relSizes.put(path, paths.get(tpath));
			}
			
			if(!extent.equals(CBConstants.ROOT_TABLET_EXTENT)) {
				if(!MetadataTable.updateTabletSSTable(extent, relSizes, systemCredentials())){
					throw new IOException("Failed to make metdata table update for map file import");
				}
			}
			
			synchronized(Tablet.this) {
				for (String tpath : paths.keySet()) {
					String path = relPaths.get(tpath);
					SSTableValue estSize = paths.get(tpath);
					
					if(sstableSizes.containsKey(path)){
						log.error("Adding sstable that is already in set "+path);
					}
					sstableSizes.put(path, estSize);
					tabletResources.addMapFile(tpath);
				}
				
				computeNumEntries();
			}
			for (String tpath : paths.keySet()) {
				String path = relPaths.get(tpath);
				log.log(TLevel.TABLET_HIST, extent+" import "+path+" "+paths.get(tpath));
			}
		}
		
		void bringMinorCompactionOnline(String tmpSST, String newSST, SSTableValue sstv){
			String path = tabletDirectory + "/" + new Path(newSST).getName();
			if(!extent.equals(CBConstants.ROOT_TABLET_EXTENT) && sstv.getNumEntries() > 0) {
				while(!MetadataTable.updateTabletSSTable(extent, path, sstv, systemCredentials())){
					log.warn("Tablet "+extent+" failed to add "+path+" to metadata table after MinC, will retry in 60 secs...");
					UtilWaitThread.sleep(60 * 1000);
				}
			}
			//put in metadata table before renaming so it is not garbage collected
			boolean renameFailed;
			do{
				renameFailed = false;
				try{
					if(fs.exists(new Path(newSST))){
						log.error("Target map file already exist "+newSST, new Exception());
					}
					
					fs.rename(new Path(tmpSST), new Path(newSST));
				}catch(IOException ioe){
					log.warn("Tablet "+extent+" failed to rename "+path+" after MinC, will retry in 60 secs...", ioe);
					UtilWaitThread.sleep(60 * 1000);
					renameFailed = true;
				}
			}while(renameFailed);
			
			try {
				walog.minorCompactionFinished(newSST);
			} catch (IOException e) {
				log.error("Failed to write to write ahead log "+e.getMessage(), e);
				throw new RuntimeException(e);
			}
			
			long t1, t2;
			
			synchronized(Tablet.this) {
				t1 = System.currentTimeMillis();
				if(sstableSizes.containsKey(path)){
					log.error("Adding sstable that is already in set "+path);
				}
				
				if(sstv.getNumEntries() > 0){
					sstableSizes.put(path, sstv);
				}
				
				boolean addMFFailed;
				do{
					addMFFailed = false;
					try {
						if(sstv.getNumEntries() > 0){
							tabletResources.addMapFile(newSST);
						}
					} catch (IOException e) {
						log.warn("Tablet "+extent+" failed to add map file "+path+" after MinC, will retry in 60 secs...", e);
						try {
							Tablet.this.wait(60 * 1000);
						} catch (InterruptedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						addMFFailed = true;
					}
				}while(addMFFailed);
				
				tabletMemory.finishedMinC();
				
				computeNumEntries();
				t2 = System.currentTimeMillis();
			}
			
			log.log(TLevel.TABLET_HIST, extent+" MinC "+path);
			log.debug(String.format("MinC finish lock %.2f secs", (t2 - t1)/1000.0));
			if(sstv.getSize() > cbConf.getLong("cloudbase.tablet.split.threshold", CBConstants.DEFAULT_TABLET_SPLIT_THRESHOLD)){
				log.warn(String.format("Minor Compaction wrote out file larger than split threshold.  split threshold = %,d  file size = %,d",
						cbConf.getLong("cloudbase.tablet.split.threshold", CBConstants.DEFAULT_TABLET_SPLIT_THRESHOLD),
						sstv.getSize()));
			}
			
		}
		
		synchronized void bringMajorCompactionOnline(Set<String> oldSSTs, String tmpSST, String newSST, SSTableValue sstv) throws IOException{
			
			Collection<String> sstablesToDelete = new ArrayList<String>(oldSSTs.size()); 
			for (String oldSSTable : oldSSTs) {
				Path path = new Path(oldSSTable);
				sstablesToDelete.add("/"+path.getParent().getName()+"/"+path.getName());
			}
			
			String shortPath = tabletDirectory + "/" + new Path(newSST).getName();
			
			if(extent.equals(CBConstants.ROOT_TABLET_EXTENT)){
				//mark files as ready for deletion, but
				//do not delete them until we successfully
				//rename the compacted map file, in case
				//the system goes down
				
				String compactName = new Path(newSST).getName();
				
				for(String file : oldSSTs) {
					Path path = new Path(file);
					fs.rename(path, new Path(location+"/delete."+compactName+"."+path.getName()));
				}
				
				if(fs.exists(new Path(newSST))){
					log.error("Target map file already exist "+newSST, new Exception());
				}
				
				fs.rename(new Path(tmpSST), new Path(newSST));
				
				//start deleting files, if we do not finish they will be cleaned
				//up later
				for(String file : oldSSTs) {
					Path path = new Path(file);
					fs.delete(new Path(location+"/delete."+compactName+"."+path.getName()), true);
				}
			}else{
				MetadataTable.replaceSSTables(extent,sstablesToDelete, shortPath, sstv, systemCredentials());
				
				if(fs.exists(new Path(newSST))){
					log.error("Target map file already exist "+newSST, new Exception());
				}
				
				//put in metadata table before renaming so it is not garbage collected
				fs.rename(new Path(tmpSST), new Path(newSST));
			}
			
			long t1,t2;
			
			synchronized (Tablet.this) {
				t1 = System.currentTimeMillis();
				//atomically remove old files and add new file	
				for (String oldSSTable : sstablesToDelete) {
					if(!sstableSizes.containsKey(oldSSTable)){
						log.error("ss table does not exist in set "+oldSSTable);
					}
					sstableSizes.remove(oldSSTable);
				}
				
				tabletResources.removeMapFiles(oldSSTs);
			
				if(sstv.getNumEntries() > 0){
					tabletResources.addMapFile(newSST);
				}
				
				if(sstableSizes.containsKey(shortPath)){
					log.error("Adding sstable that is already in set "+shortPath);
				}
				
				if(sstv.getNumEntries() > 0){
					sstableSizes.put(shortPath, sstv);
				}
				
				checkConsistency();
				
				computeNumEntries();
				t2 = System.currentTimeMillis();
			}
			
			log.debug(String.format("MajC finish lock %.2f secs", (t2 - t1)/1000.0));
			log.log(TLevel.TABLET_HIST, extent+" MajC "+sstablesToDelete+" --> "+shortPath);
		}

		private void checkConsistency() throws IOException{
			synchronized (Tablet.this) {

				//do a consistency check
				SortedSet<String> paths = tabletResources.getCopyOfMapFilePaths();


				boolean inconsistent = false;

				for(String sstp : sstableSizes.keySet()){
					String longPath = CBConstants.TABLES_DIR+"/"+extent.getTableName()+sstp;
					if(!paths.contains(longPath)){
						log.error(sstp+" not in tabletResources");
						inconsistent = true;
					}
				}

				for(String path : paths){
					Path p = new Path(path);
					String sstp = "/"+p.getParent().getName()+"/"+p.getName();
					if(!sstableSizes.containsKey(sstp)){
						log.error(sstp+" not in sstableSizes");
						inconsistent = true;
					}
				}

				if(inconsistent)
					log.error("sstable sets inconsistent "+paths+" "+sstableSizes.keySet());

				
			}

		}
		
		public SortedMap<String, SSTableValue> getSSTableSizes() {
			return unModMap;
		}
	}
	
	
	/**
	 * 
	 * 
	 * @param location
	 * @throws IOException 
	 * @throws IOException 
	 */
	
	public Tablet(Text location, KeyExtent extent, TabletResourceManager trm) throws IOException, TException {
		this(location, extent, new Configuration(), trm);
	}
	
	public Tablet(Text location, KeyExtent extent, TabletResourceManager trm, SortedMap<String, SSTableValue> sstables) throws IOException {
		this(location, extent, new Configuration(), trm, sstables);
	}

	public Tablet(Text location, KeyExtent extent, Configuration conf, TabletResourceManager trm, SortedMap<String, SSTableValue> sstables) throws IOException {
		this(location, extent, conf, FileSystem.get(conf), trm, sstables);
	}
	
	public Tablet(Text location, KeyExtent extent, Configuration conf, TabletResourceManager trm) throws IOException, TException {
		this(location, extent, conf, FileSystem.get(conf), trm);
	}
	
	
	private static SortedMap<String, SSTableValue> lookupSSTables(Text locText, FileSystem fs, KeyExtent extent) throws IOException, TException {
		Path location = new Path(CBConstants.TABLES_DIR + "/" + extent.getTableName().toString() + locText.toString());
		
		FileStatus[] files = fs.listStatus(location);
		TreeMap<String, SSTableValue> sstables = new TreeMap<String, SSTableValue>();

		// create the tablet directory if it doesn't already exist
		if(files == null) {
			fs.mkdirs(location);
		}
		else {
			if(extent.equals(CBConstants.ROOT_TABLET_EXTENT)) { // the meta0 tablet
				//cleanUpFiles() has special handling for delete. files
				log.warn("fs " + fs + " files: " + files + " location: " + location);
				Collection<String> goodPaths = cleanUpFiles(fs, files, location, true);
				for(String path : goodPaths) {
					String filename = new Path(path).getName();
					sstables.put(locText.toString() + "/" + filename, new SSTableValue(0,0));
				}
			}
			else {
				try { // regular tablets
					

					SortedMap<Key, Value> sstablesMetadata;
					
					if(extent.getTableName().toString().equals(CBConstants.METADATA_TABLE_NAME)) {
						sstablesMetadata = MetadataTable.getRootMetadataSSTableEntries(extent, systemCredentials());
					}else{
					    ScannerImpl mdScanner = new ScannerImpl(new HdfsZooInstance(), systemCredentials(), CBConstants.METADATA_TABLE_NAME, CBConstants.NO_AUTHS);

						// Commented out because when no SSTable present, each tablet will scan through metadata table and return nothing
						// reduced batch size to improve performance
						// changed here after endKeys were implemented from 10 to 1000
						mdScanner.setBatchSize(1000);
						
						// leave these in, again, now using endKey for safety
						mdScanner.fetchColumnFamily(CBConstants.METADATA_TABLE_TABLET_SSTABLES_COLUMN_FAMILY);
						
						Text rowName = extent.getMetadataEntry();
						
						// there could be several extents between the metadata entry and the metadata entry' ' but not many
						Text closeNextExtent = new Text(extent.getMetadataEntry());
						closeNextExtent.append(new byte[]{0}, 0, 1);
						
						mdScanner.setRange(new Range(rowName, closeNextExtent));
						
						sstablesMetadata = new TreeMap<Key, Value>();
						
						for (Entry<Key, Value> entry : mdScanner) {
							
							if(entry.getKey().compareRow(rowName) != 0){
								break;
							}
							
							//if(entry.getKey().compareColumnFamily(CBConstants.METADATA_TABLE_TABLET_SSTABLES_COLUMN_FAMILY)==0 || entry.getKey().compareColumnFamily(CBConstants.METADATA_TABLE_TABLET_MFD_SSTABLES_COLUMN_FAMILY)==0)
							sstablesMetadata.put(new Key(entry.getKey()), new Value(entry.getValue()));
						}
					}
					
					Iterator<Entry<Key, Value>> sstmdIter = sstablesMetadata.entrySet().iterator();
					
					while(sstmdIter.hasNext()) {
						Entry<Key, Value> entry = sstmdIter.next();
						
						sstables.put(entry.getKey().getColumnQualifier().toString(), new SSTableValue(entry.getValue().get()));
						
						Path sstPath = new Path(CBConstants.TABLES_DIR + "/" + extent.getTableName().toString()+entry.getKey().getColumnQualifier());
						try{
							fs.getFileStatus(sstPath);
						}catch (FileNotFoundException fne){
							try{
								Path sstTmpPath = new Path(CBConstants.TABLES_DIR + "/" + extent.getTableName().toString()+entry.getKey().getColumnQualifier()+"_tmp");
								log.debug("SSTable "+extent+" checking if "+sstTmpPath+" exist because "+sstTmpPath+" does not exist");
								fs.getFileStatus(sstTmpPath);
								log.warn("SSTable "+extent+" renamed "+sstTmpPath+" to "+sstPath);
								fs.rename(sstTmpPath, sstPath);
							}catch (FileNotFoundException fne2){
								log.error("Tablet "+extent+" references "+entry.getKey().getColumnQualifier()+" which does not exist. skipping.");
								// note: there's not much we can do here, we've lost data, logged it, so move on
								// TODO remove entries from the metadata table safely (i.e. handle the case where we are the metadata table)
								// at least forget about it for the near future
								sstables.remove(entry.getKey().getColumnQualifier().toString());
								//throw new IOException("Tablet "+extent+" references "+entry.getKey().getColumnQualifier()+" which does not exist");
							}
						}
						//System.out.println("DEBUG : "+entry.getKey().toString()+" "+entry.getValue());
					}
				} catch (TableNotFoundException e) {
					throw new IOException(e);
				} catch (CBException e) {
					throw new IOException(e);
				} catch (CBSecurityException e) {
					throw new IOException(e);
				}
			}
		}
		return sstables;
	}
	
	/**
	 * 
	 * @param location
	 * @param extent
	 * @param conf
	 * @param fs
	 * @param trm
	 * @throws IOException
	 */
	public Tablet(Text location, KeyExtent extent, Configuration conf, FileSystem fs, TabletResourceManager trm) throws IOException, TException {
		this(location, extent, conf, fs, trm, lookupSSTables(location, fs, extent));
	}
	
	/**
	 * yet another constructor - this one allows us to avoid costly lookups into 
	 * the Metadata table if we already know the sstables we need - as at split time
	 * 
	 * @param location
	 * @param extent
	 * @param conf
	 * @param fs
	 * @param trm
	 * @param sstables
	 * @throws IOException
	 */
	public Tablet(Text location, final KeyExtent extent, Configuration conf, FileSystem fs, TabletResourceManager trm, SortedMap<String, SSTableValue> sstables) throws IOException {
		this.location = new Path(CBConstants.TABLES_DIR + "/" + extent.getTableName().toString() + location.toString());
		this.tabletDirectory = location.toString();
		this.conf = conf;
		this.cbConf = CBConfiguration.getInstance(extent.getTableName().toString());

		this.fs = fs;
		this.extent = extent;
		this.tabletResources = trm;
		this.useScanCache = cbConf.getBoolean("cloudbase.tablet.cache.enable", true);
		
		if(useScanCache){
			log.warn("Not honoring request to enable scan cache "+extent+", it is not safe to use with new iterators (see #395 and #287)");
			useScanCache = false;
		}
		
		if(useScanCache){
			this.scanCache = new ScanCache(cbConf.getLong("cloudbase.tablet.cache.size", 1l<<22),extent);
			log.debug("Scan cache enabled for "+extent);
		}
		else
			log.debug("Scan cache disabled for "+extent);
		
		this.timer = new TabletTimer();
		
		setupDefaultSecurityLabels(extent);
		
		tabletMemory = new TabletMemory();
	
		prevQueryRate = 0.0;
		queryCount = 0L;
		prevIngestRate = 0.0;
		ingestCount = 0L;
		
		if(sstables.size() > 0) {
			initGeneration(this.location, sstables.keySet());
		}
		
		constraintChecker = ConstraintLoader.load(extent.getTableName().toString());
		
		initMemmapAgg();
		
		cbConf.addObserver(configObserver = new ConfigurationObserver(){
			
			private void reloadConstraints(){
				try {
					log.debug("Reloading constraints");
					constraintChecker = ConstraintLoader.load(extent.getTableName().toString());
				} catch (IOException e) {
					log.error("Failed to reload constraints for "+extent, e);
					constraintChecker = new ConstraintChecker();
					constraintChecker.addConstraint(new UnsatisfiableConstraint((short)-1, "Failed to reload constraints, not accepting mutations."));
				}
			}
			
			private void reloadMMAggs(){
				log.debug("Reloading memap aggregrators");
				try {
					initMemmapAgg();
				} catch (IOException e) {
					e.printStackTrace();
					log.error("Failed to reload memap aggregrators "+extent, e);
				}
			}

			public void propertiesChanged() {
				reloadConstraints();
				reloadMMAggs();
				
				try {
					setupDefaultSecurityLabels(extent);
				} catch (Exception e) {
					log.error("Failed to reload default security labels for extent: " + extent.toString());
				}
			}

			public void propertyChanged(String prop) {
				if(prop.startsWith(ConstraintLoader.CONSTRAIN_PROPERTY))
					reloadConstraints();
				else if(prop.startsWith(IteratorUtil.ITERATOR_PROPERTY+IteratorScope.minc))
					reloadMMAggs();
				else if(prop.equals("cloudbase.security.cellLevel.defaultLabelExpression")){
					try {
						log.info("Default security labels changed for extent: " + extent.toString());
						setupDefaultSecurityLabels(extent);
					} catch (Exception e) {
						log.error("Failed to reload default security labels for extent: " + extent.toString());
					}
				}
				
			}

			public void sessionExpired() {
				log.debug("Session expired, no longer updating per table props...");
			}
			
		});
		
		tabletResources.setTablet(this, cbConf);
		
        log.info("Starting Write-Ahead Log recovery for " + this.extent);
		walog = TabletLog.getInstance(extent);
		walog.open(new MutationReceiver(){
			public void receive(Mutation m) {
				tabletMemory.mutate(m);
			}}
		);
		log.info("Write-Ahead Log recovery complete for " + this.extent);
		
		//do this last after tablet is completely setup because it
		//could cause major compaction to start
		sstableManager = new SSTableManager(sstables);
		
		computeNumEntries();
		
		log.log(TLevel.TABLET_HIST, extent+" opened ");
	}
	
	private void initMemmapAgg() throws IOException {
		HashMap<String, String> opts = new HashMap<String, String>();
		String name = IteratorUtil.findIterator(IteratorScope.minc, AggregatingIterator.class.getName(), cbConf, opts);
		
		AggregatorSet ingestAggregators;
		
		if(name != null && opts.size() > 0){
			try {
				ingestAggregators = new AggregatorSet(opts);
			} catch (InstantiationException e) {
				throw new IOException(e);
			} catch (IllegalAccessException e) {
				throw new IOException(e);
			} catch (ClassNotFoundException e) {
				throw new IOException(e);
			}
		}else{
			ingestAggregators = new AggregatorSet();
		}
		
		tabletMemory.setIngestAggregator(ingestAggregators);
	}
	
	private void setupDefaultSecurityLabels(KeyExtent extent) {
		if ( extent.getTableName().toString().equals(CBConstants.METADATA_TABLE_NAME)) {
			defaultSecurityLabel = new byte[0];
		}
		else {
			for (Entry<String, String> entry : this.cbConf) {
				if(entry.getKey().equals("cloudbase.security.cellLevel.defaultLabelExpression")){
					try {
						this.defaultSecurityLabel = LabelConversions.expressionStringToBytes(entry.getValue());
					} catch (LabelExpressionFormatException e) {
						log.error(e);
						this.defaultSecurityLabel = new byte[0];
					}
				}
			}
		}
	}
	
	private static Collection<String> cleanUpFiles(FileSystem fs, FileStatus[] files, Path location, boolean deleteTmp) throws IOException {
		/*
		 * called in constructor and before major compactions
		 */	
		Collection<String> goodFiles = new ArrayList<String>(files.length);
		
		for(FileStatus file : files) {
			
			String path = file.getPath().toString();
			String filename = file.getPath().getName();
			
			//check for incomplete major compaction, this should only occur
			//for root tablet
			if(filename.startsWith("delete.")){
				String expectedCompactedFile = location.toString() + "/" + filename.split(".")[1];
				if(fs.exists(new Path(expectedCompactedFile))){
					//compaction finished, but did not finish deleting compacted files.. so delete it
					fs.delete(file.getPath(), true);
					continue;
				}else{
					//compaction did not finish, so put files back
					
					//reset path and filename for rest of loop
					filename = filename.split(".",3)[2];
					path = location+"/"+filename;
					
					fs.rename(file.getPath(), new Path(path));
				}
			}
			
			if(filename.endsWith("_tmp")) {
				if(deleteTmp){
					log.warn("cleaning up old tmp file" + path);
					fs.delete(file.getPath(), true);
				}
				continue;
			}
			
			if(!filename.toString().startsWith("map_")){
				log.error("unknown file in tablet" + path);
				continue;
			}

			if(!fs.exists(new Path(path+"/"+MyMapFile.DATA_FILE_NAME))){
				String corruptName = file.getPath().getParent()+"/corrupt_"+file.getPath().getName();
				log.error("mapfile missing data file" + path+" renaming to "+corruptName);
				//TODO remove from mapfiles map?
				fs.rename(file.getPath(), new Path(corruptName));
			}

			goodFiles.add(path);
		}
		
		return goodFiles;
	}
	
	@SuppressWarnings("serial")
    public static class KVEntry extends KeyValue{
		private Value valueObj;
		
		public KVEntry(Key k, Value v) {
			key = new Key(k);
			valueObj = new Value(v);
			value = valueObj.get();
		}

		public Key getKey() {
			return key;
		}

		public void setKey(Key key) {
			this.key = key;
		}

		public Value getValue() {
			return valueObj;
		}

		public void setValue(Value value) {
			this.valueObj = value;
		}
		
		public String toString(){
			return key.toString()+"="+valueObj;
		}
		
		int estimateMemoryUsed(){
			return key.getSize() + valueObj.get().length + (9*32); // overhead is 32 per object
		}
	}
	
	private synchronized LookupResult lookup(MyMapFile.Reader[] mapfiles, List<Range> ranges, HashSet<Column> columnSet, byte[] authorizations, ArrayList<KVEntry> results, long maxResultsSize, List<IterInfo> ssiList, Map<String, Map<String, String>> ssio, LookupResult lookupResult) throws IOException{

		// the MyMapFile.seek function is smart enough that there is no point in scanning instead of individual lookups between ranges
		// so we can always seek between ranges instead of scanning
		MyMapFile.mapFileSeekScanCompareTime.clear();
		MyMapFile.mapFileSeekScans.clear();
		MyMapFile.mapFileSeekScanTime.clear();
		MyMapFile.mapFileSeekTimeStat.clear();
		
		if(lookupResult == null)
			lookupResult = new LookupResult();
		if(lookupResult.unfinishedRanges == null)
			lookupResult.unfinishedRanges = new ArrayList<Range>();
		else
			lookupResult.unfinishedRanges.clear();
		lookupResult.bytesAdded = 0;
		lookupResult.closed = false;
		
		boolean exceededMemoryUsage = false;
		boolean exceededTimeLimit = false;
		
		long scanStartTime = System.currentTimeMillis();
		long mark = scanStartTime;
		int rangeCount = 0;
		Stat seekTime = new Stat();
		Stat scanTime = new Stat();
		Stat overheadTime = new Stat();

		long maxAtomicSectionTimeMillis = cbConf.getLong("cloudbase.tablet.scan.max.time", CBConstants.TABLET_MAX_ATOMIC_OPERATION_TIME_MILLIS_DEFAULT);
		int minScan = cbConf.getInt("cloudbase.tablet.scan.min.entries", CBConstants.TABLET_MIN_SCAN_DEFAULT);
		
		TimeLimitIterator tli = new TimeLimitIterator(maxAtomicSectionTimeMillis, minScan);
		
		SortedKeyValueIterator<Key, Value> mmfi = createScanIterator(mapfiles, authorizations, this.defaultSecurityLabel, tli, null, columnSet, ssiList, ssio, false);
		
		for (Range range : ranges) {
			// setting the end key to the tablet end would mean that the lower level iterators can scan over much more of the key space than the range specifies
			// we set the end key to the range end to avoid scanning extra keys that are filtered by lower level iterators
			mmfi.setEndKey(range.getEndKey());
			{
				long tempTime = System.currentTimeMillis();
				overheadTime.addStat(tempTime - mark);
				mark = tempTime;
			}
			
			long timeLeft = maxAtomicSectionTimeMillis - (mark - scanStartTime);
			
			//want to always process at least one range so progress is made, that is
			//why rangeCount is checked below
			if(timeLeft <= 0 && rangeCount > 0){
				exceededTimeLimit = true;
			}
			
			if(exceededMemoryUsage || exceededTimeLimit){
				lookupResult.unfinishedRanges.add(range);
				continue;
			}
			
			rangeCount++;

			tli.setLimit(timeLeft < 0 ? 0 : timeLeft);
			mmfi.seek(range.getStartKey());
			
			{
				long tempTime = System.currentTimeMillis();
				seekTime.addStat(tempTime - mark);
				mark = tempTime;
			}

			while(mmfi.hasTop()){
				Key key = mmfi.getTopKey();

				if(range.afterEndKey(key)){
					break;
				}

				if(range.contains(key)){
					KVEntry kve = new KVEntry(key, mmfi.getTopValue());
					results.add(kve);
					lookupResult.bytesAdded += kve.estimateMemoryUsed();
					
					exceededMemoryUsage = lookupResult.bytesAdded > maxResultsSize;
					
					if(exceededMemoryUsage)
					{
						addUnfinishedRange(lookupResult, range, key, false);
						break;
					}
				}

				mmfi.next();
			}
			
			if(tli.hitLimit() && tli.getSource().hasTop()){
				//hit time limit
				if(exceededMemoryUsage){
					throw new RuntimeException("Should not happen");
				}
				
				exceededTimeLimit = true;
				
				addUnfinishedRange(lookupResult, range, tli.getSource().getTopKey(), true);
			}
			
			{
				long tempTime = System.currentTimeMillis();
				scanTime.addStat(tempTime - mark);
				mark = tempTime;
			}
		}
		{
			long tempTime = System.currentTimeMillis();
			overheadTime.addStat(tempTime - mark);
			mark = tempTime;
		}
		log.debug("Atomic lookup time "+(mark-scanStartTime)+"ms: overhead time "+overheadTime+", scan time "+scanTime+", seek time "+seekTime+", range count "+rangeCount);
		log.debug("mapfile seeks "+MyMapFile.mapFileSeekTimeStat+" mapfile seekscans "+MyMapFile.mapFileSeekScans
				+" mapfileSeekScanTime "+MyMapFile.mapFileSeekScanTime+" mapfileSeekScanCompareTime "+MyMapFile.mapFileSeekScanCompareTime);
		return lookupResult;
	}

	private void addUnfinishedRange(LookupResult lookupResult, Range range, Key key, boolean inclusiveStartKey) {
		if(range.getEndKey() == null || key.compareTo(range.getEndKey()) < 0){
			Range nlur = new Range(new Key(key), inclusiveStartKey, range.getEndKey(), range.isEndKeyInclusive());
			lookupResult.unfinishedRanges.add(nlur);
		}
	}
	
	public static interface KVReceiver {
		void receive(List<KVEntry> matches) throws IOException;
	}
	
	class LookupResult {
		List<Range> unfinishedRanges;
		
		long bytesAdded;
		boolean closed;
	}
	
	public LookupResult lookup(List<Range> ranges, HashSet<Column> columns, byte[] authorizations, ArrayList<KVEntry> results, long maxResultSize, List<IterInfo> ssiList, Map<String, Map<String, String>> ssio) throws IOException{
		
		long lookupStartTime = System.currentTimeMillis();
		
		LookupResult lookupResult = new LookupResult();
		
		lookupResult.unfinishedRanges = Collections.emptyList();
		lookupResult.closed = false;
		lookupResult.bytesAdded = 0;
		
		if(ranges.size() == 0){
			return lookupResult;
		}
		
		MyMapFile.Reader mapfileReaders[] = null; 
		
		ranges = Range.mergeOverlapping(ranges);
		Collections.sort(ranges);
		
		long totalLookupTime = 0;
		int numLookups = 0;
		
		int rangesSize = ranges.size();
		
		while(ranges != null){
			LookupResult subResult = null;
			
			//TODO only need to sync scan, but when doing lookups could sync
			//each one to allow more concurrency
			synchronized(this){
				
				try{
					if(closed){
						lookupResult.unfinishedRanges = ranges;
						lookupResult.closed = true;
						//return a list of the ranges we have not processed yet
						log.debug(String.format("Tablet closed during lookup.  Looked up %,d ranges in %6.2f secs  (avg syncLookup %6.2f secs, avg ranges/syncLookup %,6.2f, overhead %6.2f secs) %s",rangesSize,totalLookupTime/1000.0 , ((totalLookupTime)/1000.0)/numLookups, rangesSize/(double)numLookups, (System.currentTimeMillis() - lookupStartTime - totalLookupTime)/1000.0, extent));
						return lookupResult;
					}
					
					if(lookupResult.bytesAdded >= maxResultSize){
						lookupResult.unfinishedRanges = ranges;
						lookupResult.closed = false;
						log.debug(String.format("Filled buffer during lookup.  Looked up %,d ranges in %6.2f secs  (avg syncLookup %6.2f secs, avg ranges/syncLookup %,6.2f, overhead %6.2f secs) %s",rangesSize,totalLookupTime/1000.0 , ((totalLookupTime)/1000.0)/numLookups, rangesSize/(double)numLookups, (System.currentTimeMillis() - lookupStartTime - totalLookupTime)/1000.0, extent));
						return lookupResult;
					}
					
					
					mapfileReaders = tabletResources.reserveMapFileReaders();
			
					long t1 = System.currentTimeMillis();
					subResult = lookup(mapfileReaders, ranges, columns, authorizations, results, maxResultSize - lookupResult.bytesAdded, ssiList, ssio, subResult);
					long t2 = System.currentTimeMillis();
					
					
					lookupResult.bytesAdded += subResult.bytesAdded;
					
					if(subResult.unfinishedRanges.size() > 0){
						//not everything was looked up, reset things so that these ranges are looked up
						//on the next round
//						ArrayList<Range> newRanges = new ArrayList<Range>(subResult.unfinishedRanges);
//						ranges = newRanges;
						List<Range> tempRanges = ranges;
						ranges = subResult.unfinishedRanges;
						subResult.unfinishedRanges = tempRanges;
					}
					else
						ranges = null;
					
					//collect some stats
					totalLookupTime += (t2 - t1);
					numLookups++;

				}finally{
					//code in finally block because always want
					//to return mapfiles, even when exception is thrown
					if(mapfileReaders != null){
						tabletResources.returnMapFileReaders();
						mapfileReaders = null;
					}
				}
			}
			if(ranges != null)
			{
				try {
					// allow other threads (possibly on other cores) a chance to read
					Thread.sleep(1);
				} catch (InterruptedException e) {
				}
			}
		}
		if(ranges != null)
		{
			lookupResult.unfinishedRanges = ranges;
			lookupResult.closed = false;
		}
		
		log.debug(String.format("Looked up %,d ranges in %6.2f secs  (avg syncLookup %6.2f secs, avg ranges/syncLookup %,6.2f, overhead %6.2f secs) %s",rangesSize,totalLookupTime/1000.0 , ((totalLookupTime)/1000.0)/numLookups, rangesSize/(double)numLookups, (System.currentTimeMillis() - lookupStartTime - totalLookupTime)/1000.0, extent));
		return lookupResult;
	}
		
    private SortedKeyValueIterator<Key, Value> createScanIterator(MyMapFile.Reader[] mapfiles, byte[] authorizations, byte[] defaultLabels, TimeLimitIterator tli, Key endKey, HashSet<Column> columnSet, List<IterInfo> ssiList, Map<String, Map<String, String>> ssio, boolean useScanCache) throws IOException {
	    SortedKeyValueIterator memIters[] = tabletMemory.getIterators();
	    
	    SortedKeyValueIterator iters[] = new SortedKeyValueIterator[mapfiles.length + memIters.length];
		
		for (int i = 0; i < mapfiles.length; i++) {
			iters[i] = mapfiles[i];
		}
		
		for(int i = 0; i < memIters.length; i++){
		    iters[mapfiles.length + i] = memIters[i];
		}
		
		MultiIterator multiIter = new MultiIterator(iters, extent, endKey, false);
		tli.init(multiIter, null);
		DeletingIterator delIter = new DeletingIterator(tli, false);
		
		SystemScanIterator systermIter;
		
		if(useScanCache){
			log.warn("Using the scan cache is not safe");
			scanCache.setAuthorityIterator(delIter);
			systermIter = new SystemScanIterator(scanCache, authorizations, defaultLabels, columnSet);
		}else{
			systermIter = new SystemScanIterator(delIter, authorizations, defaultLabels, columnSet);
		}
		
		return IteratorUtil.loadIterators(IteratorScope.scan, systermIter, extent, CBConfiguration.getInstance(extent.getTableName().toString()), ssiList, ssio);
//		return new FilteringAggregatingIterator(new MultiIterator(iters, extent, false), queryAggregators, false);
		
	}
	private Batch nextBatch(MyMapFile.Reader[] mapfiles, Range range, int num, HashSet<Column> columns, byte[] authorizations, List<IterInfo> ssiList, Map<String, Map<String, String>> ssio) throws IOException  {
		
		//log.info("In nextBatch..");

		
		List<KVEntry> results = new ArrayList<KVEntry>();
		Key key = null;

		Value value;
		long resultSize = 0L;
		
		int maxResultsSize = cbConf.getInt("cloudbase.tablet.scan.max.memory", 50000000);
		long maxScanTime = cbConf.getLong("cloudbase.tablet.scan.max.time", CBConstants.TABLET_MAX_ATOMIC_OPERATION_TIME_MILLIS_DEFAULT);
		int minScan = cbConf.getInt("cloudbase.tablet.scan.min.entries", CBConstants.TABLET_MIN_SCAN_DEFAULT);
		
		TimeLimitIterator tli = new TimeLimitIterator(maxScanTime, minScan);
		
		SortedKeyValueIterator<Key, ?> iter;
		
		iter = createScanIterator(mapfiles, authorizations, this.defaultSecurityLabel, tli, range.getEndKey(), columns, ssiList, ssio, useScanCache);
		
		Key startKey = range.getStartKey();
		if(startKey == null){
			startKey = new Key();
		}
		
		iter.seek(startKey);
		
		while(iter.hasTop() && range.beforeStartKey(iter.getTopKey())){
			iter.next();
		}
		
		int viewedCount = 0;
		
		Key continueKey = null;
		boolean skipContinueKey = false;
		
		boolean endOfTabletReached = false;
		while(iter.hasTop()) {

			value = (Value)iter.getTopValue();
			key = iter.getTopKey();
			
			viewedCount++;
			
			if(range.afterEndKey(key)){
				endOfTabletReached = true;
				break;
			}
			
			KVEntry kvEntry = new KVEntry(key, value); // copies key and value
			results.add(kvEntry); 	
			resultSize += kvEntry.estimateMemoryUsed();
			
			if(resultSize >= maxResultsSize || results.size() >= num) {
				continueKey = new Key(key);
				skipContinueKey = true;
				break;
			}
			
			iter.next();
		}
		
		if(tli.hitLimit() && tli.getSource().hasTop()){
			//hit the time limit
			if(continueKey != null){
				throw new RuntimeException("Should not happen 1");
			}
			
			if(endOfTabletReached == true){
				throw new RuntimeException("Should not happen 2");
			}
			
			continueKey = new Key(tli.getSource().getTopKey());
			skipContinueKey = false;
			log.debug("Hit time limit, viewedCount = "+viewedCount+" continue key "+continueKey);
		}else if(iter.hasTop() == false){
			endOfTabletReached = true;
		}
		
		Batch retBatch = new Batch();
		
		
		if(!endOfTabletReached){
			retBatch.continueKey = continueKey;
			retBatch.skipContinueKey = skipContinueKey;
		}else{
			retBatch.continueKey = null;
		}
		
		if(useScanCache)
			scanCache.finishScan();
		
		if(endOfTabletReached && results.size() == 0)
			retBatch.results = null;
		else
			retBatch.results = results;
		
		return retBatch;
	}
	
	public class Batch {
		public boolean skipContinueKey;
		public List<KVEntry> results;
		public Key continueKey;
	}
	
	public synchronized Batch nextBatch(Range range, int num, HashSet<Column> columns, byte[] authorizations, List<IterInfo> ssiList, Map<String, Map<String, String>> ssio) throws IOException, TabletClosedException  {
		MyMapFile.Reader mapfileReaders[] = null; 
		Batch results = null;
		
		if(closed){
			throw new TabletClosedException();
		}
		
		try{
			mapfileReaders = tabletResources.reserveMapFileReaders();
			
			results = nextBatch(mapfileReaders, range, num, columns, authorizations, ssiList, ssio);

		}finally{
			//code in finally block because always want
			//to return mapfiles, even when exception is thrown
			if(mapfileReaders != null){
				tabletResources.returnMapFileReaders();
			}
		}
		if(results != null && results.results != null)
			queryCount += results.results.size();
		return results;
	}
		
	private void minorCompact(Configuration conf, FileSystem fs, InMemoryMap memTable, String tmpSST, String newSST){
		minorCompact(conf, fs, memTable, tmpSST, newSST, false, 0);
	}
	
	private void minorCompact(Configuration conf, FileSystem fs, InMemoryMap memTable, String tmpSST, String newSST, boolean hasQueueTime, long queued){
		boolean failed = false;
		long start = System.currentTimeMillis();
		timer.statusMinor++;
		
		long count = 0;
		
		try{
			count = memTable.getNumEntries();
			SSTableValue stats = memTable.minorCompact(conf, fs, tmpSST, extent);				
			sstableManager.bringMinorCompactionOnline(tmpSST, newSST, stats);
		}
		catch(RuntimeException E) {
			failed = true;
			throw E;
		}
		finally{
			if(hasQueueTime)
				timer.updateTime(TabletTimer.MINOR_FINISHED, queued, start, count, failed);
			else
				timer.updateTime(TabletTimer.MINOR_FINISHED, start, count, failed);
			
			if(!failed){
				lastMinorCompactionFinishTime = System.currentTimeMillis();
			}
		}
	}

	synchronized boolean initiateMinorCompaction(){
	    
		long t1 = System.currentTimeMillis();
		
		if(closed || majorCompactionWaitingToStart || tabletMemory.memoryReservedForMinC() || tabletMemory.getMemTable().size() == 0){
			return false;
		}
		minorCompactionWaitingToStart = true;
		
		tabletMemory.prepareForMinC();

		// generate the filename
		final String newMapfileLocation = getNextMapFilename();

		// get a reference to this
		//final Tablet thisTablet = this;
		final long queued = System.currentTimeMillis();
		Runnable r = new Runnable() {
			public void run() {
			    minorCompactionWaitingToStart = false;
			    minorCompactionInProgress = true;
			    try {
			        minorCompact(conf, fs, tabletMemory.getMinCMemTable(), newMapfileLocation+"_tmp", newMapfileLocation, true, queued);
			    } finally {
			        minorCompactionInProgress = false;
			    }
			}
		};

		try {
			walog.minorCompactionStarted(newMapfileLocation);
		} catch (IOException e) {
			log.error("Failed to write to write ahead log "+e.getMessage(), e);
			throw new RuntimeException(e);
		}
		// start the minor compaction in thread t
		tabletResources.executeMinorCompaction(r);
		
		long t2 = System.currentTimeMillis();
		
		log.debug(String.format("MinC initiate lock %.2f secs", (t2 - t1)/1000.0));
		
		return true;
	}
	
	@SuppressWarnings("serial")
    static class TConstraintViolationException extends Exception{
		private Violations violations;

		TConstraintViolationException(Violations violations){
			this.violations = violations;
		}
		
		Violations getViolations(){
			return violations;
		}
	}
	
	/**
	 * Apply the given mutation to our data
	 * 
	 */
	public boolean commit(Mutation mutation) throws TConstraintViolationException {
		tabletResources.waitUntilCommitsAreEnabled();
		return _commit(mutation);
	}
	
	// now returns false only if the tablet is closed - during a split ...
	public synchronized boolean _commit(Mutation mutation) throws TConstraintViolationException { 

		if(closed || tabletMemory == null) {
			//log.debug("tablet closed, can't commit");
			return false;
		}
		
		numEntries++;
		numEntriesInMemory++;
		
		Violations violations = constraintChecker.check(extent, mutation);
		if(violations != null){
			throw new TConstraintViolationException(violations);
		}
		
		long lastCommitTime = System.currentTimeMillis();
		
		// remove keys from the scan cache
		for (ColumnUpdate cvp : mutation.getUpdates()) {
			//TODO reuse this Key? it is create twice.. once here and once in mutate
			Key newKey = new Key(mutation.getRow(), cvp.getColumnFamily(), cvp.getColumnQualifier(), cvp.getColumnVisibility(), cvp.getTimestamp());
			if(useScanCache)
				scanCache.invalidate(newKey);
			
			if(!cvp.hasTimestamp()) {
				cvp.setTimestamp(lastCommitTime);
			}
		}
				
		try {
			walog.log(mutation);
		} catch (IOException e) {
			log.error("Failed to log mutation "+e.getMessage(), e);
			return false;
		}
		
		// write the mutation to the in memory table
		tabletMemory.mutate(mutation);
		
		ingestCount++;

		return true;
	}

	

	/**
	 * Closes the mapfiles associated with a Tablet.  If saveState is true,
	 * a minor compaction is performed.
	 * 
	 * @param saveState
	 * @throws IOException 
	 */
	public synchronized void close(boolean saveState) throws IOException {

		initiateClose(saveState);

		completeClose();
	}
	
	synchronized void initiateClose(boolean saveState) throws IOException {
		// do a minor compaction to save the data that's in memory
		// prevent future writes 
		
		if(saveState)
		{
			// check to see if this table is already being compacted
			if(tabletMemory != null){
			    tabletMemory.waitForMinC();
			}
			
			if(tabletMemory != null && tabletMemory.getMemTable().size() > 0) {
				// compact the table
				// generate the filename
				final String newMapfileLocation = getNextMapFilename();

				tabletMemory.prepareForMinC();
				
				// call the minor compaction function in the mem table
				walog.minorCompactionStarted(newMapfileLocation);
				SSTableValue mincStats = tabletMemory.getMinCMemTable().minorCompact(conf, fs, newMapfileLocation+"_tmp", extent);
				sstableManager.bringMinorCompactionOnline(newMapfileLocation+"_tmp", newMapfileLocation, mincStats);
				
				tabletMemory = null;
			}
		}
		
		closed = true;
	}

	synchronized void completeClose() throws IOException {
		
		if(closed != true){
			throw new IllegalStateException();
		}
		
		//	wait for major compactions to finish
		while(majorCompactionInProgress){
			try {
				this.wait(100);
			} catch (InterruptedException e) {
			
				log.error(e.toString());
			}
		}
		
		// release optional bloom filter memory

		// close map files
		tabletResources.close();
		
		walog.close();
		
		log.log(TLevel.TABLET_HIST, extent+" closed");
		
		cbConf.removeObserver(configObserver);
	}
	
	/**
	 * Returns a Path object representing the tablet's location on the DFS.
	 * 
	 * @return location
	 */
	public Path getLocation() {
		return location;
	}

	/**
	 * Returns true if a major compaction should be performed
	 * on the tablet.
	 *  
	 */
	public boolean needsMajorCompaction() {
		if(majorCompactionInProgress){
			return false;
		}
		try{
			return tabletResources.needsMajorCompaction();
		}catch(IOException ioe){
			log.error(ioe.toString());
			return false;
		}
	}

	/**
	 * Returns list of files that need to be compacted by major compactor
	 * @throws IOException 
	 *
	 */
	
	public Object[] getFilesToCompact() throws IOException{
		/*
		 * This function tries to determine which files to compact.
		 * It has a goal number of files.  If the goal number of
		 * files is three, then it will keep the 2 largest files
		 * around and compact everything else.  
		 * 
		 */
		
		//TODO when differential between largest map file and avg map file size
		//is not that large, compact all files.
		
		int numToKeep = tabletResources.getMaxMapFilesMajorCompactionCanLeaveUncompacted();
		
		Set<Entry<String,SSTableValue>> es = sstableManager.getSSTableSizes().entrySet();
		ArrayList<Entry<String,SSTableValue>> filesInDescSizeOrder = new ArrayList<Entry<String,SSTableValue>>(es);
		
		Collections.sort(filesInDescSizeOrder, new Comparator<Entry<String,SSTableValue>>(){
			public int compare(Entry<String, SSTableValue> o1, Entry<String, SSTableValue> o2) {
				return -1 * new Long(o1.getValue().getSize()).compareTo(o2.getValue().getSize());
			}});
		
		if(filesInDescSizeOrder.size() == 0 || numToKeep >= filesInDescSizeOrder.size()){
			return null;
		}
		
		boolean compactAll = numToKeep == 0;
		
		TreeSet<String> filesToCompact = new TreeSet<String>();
		
		for(int i = numToKeep; i < filesInDescSizeOrder.size(); i++){
			String path = location.getParent().toString() + filesInDescSizeOrder.get(i).getKey();
			filesToCompact.add(path);
		}
		
		/*if(!tabletResources.containsAllMapFiles(filesToCompact)){
			TreeSet<String> notPresent = new TreeSet<String>(filesToCompact);
			notPresent.removeAll(tabletResources.getCopyOfMapFilePaths());
			System.err.println("ERROR : files in HDFS not in mapfiles TreeSet : "+notPresent);
		}*/
		
		Object ret[] = new Object[2];
		ret[0] = filesToCompact;
		ret[1] = new Boolean(compactAll);
		
		return ret;
	}
	
	/**
	 * Returns an int representing the total block size
	 * of the mapfiles served by this tablet.
	 * 
	 * @return size 
	 */
	// this is the size of just the mapfiles 
	public long estimateTabletSize() {
		long size = 0L;
		
		for(SSTableValue sz : sstableManager.getSSTableSizes().values())
			size += sz.getSize();
		
		return size;
	}

	private boolean sawBigRow = false;
	private long timeOfLastMinCWhenBigFreakinRowWasSeen = 0;
	private long timeOfLastImportWhenBigFreakinRowWasSeen = 0;
	
	private Map<String, Object> findSplitRow(){
		
		// never split the root tablet
		// check if we already decided that we can never split
		// check to see if we're big enough to split
			
		long splitThreshold = cbConf.getLong("cloudbase.tablet.split.threshold", CBConstants.DEFAULT_TABLET_SPLIT_THRESHOLD);
		if(location.toString().equals(CBConstants.ROOT_TABLET_DIR) ||
				estimateTabletSize() <= splitThreshold){
			return null;
		}
		
		//have seen a big row before, do not bother checking unless a minor compaction or map file import has occurred.
		if(sawBigRow){
			if(timeOfLastMinCWhenBigFreakinRowWasSeen != lastMinorCompactionFinishTime ||
					timeOfLastImportWhenBigFreakinRowWasSeen != lastMapFileImportTime){
				//a minor compaction or map file import has occurred... check again
				sawBigRow = false;
			}else{
				//nothing changed, do not split
				return null;
			}
		}
		
		SortedMap<Double, Key> keys = null;
		
		try {
			//TODO make .25 below configurable
			keys = MapFileUtil.findMidPoint(extent.getPrevEndRow(), extent.getEndRow(), tabletResources.getCopyOfMapFilePaths(), .25);
		} catch (IOException e) {
			log.error("Failed to find midpoint "+e.getMessage());
			return null;
		}
		
		// check to see if one row takes up most of the tablet, in which case we can not split
		try {
			
			Text lastRow;
			if(extent.getEndRow() == null){
				Key lastKey = (Key)MapFileUtil.findLastKey(tabletResources.getCopyOfMapFilePaths());
				lastRow = lastKey.getRow();
			}else{
				lastRow = extent.getEndRow();
			}
			
			// check to see that the midPoint is not equal to the end key
			if(keys.get(.5).compareRow(lastRow) == 0 ){
				if(keys.firstKey() < .5){
					Key candidate = keys.get(keys.firstKey());
					if(candidate.compareRow(lastRow) != 0){
						//TODO use this ratio in split size estimations
						//System.out.printf("DEBUG : Splitting at %6.2f instead of .5, row at .5 is same as end row\n", keys.firstKey());
						Map<String, Object> ret = new HashMap<String, Object>();
						ret.put("splitRatio", keys.firstKey());
						ret.put("key", candidate);
						
						return ret;
					}
					
				}
				
				log.warn("Cannot split tablet "+extent+" it contains a big row : "+lastRow);
				
				sawBigRow = true;
				timeOfLastMinCWhenBigFreakinRowWasSeen = lastMinorCompactionFinishTime;
				timeOfLastImportWhenBigFreakinRowWasSeen = lastMapFileImportTime;
				
				return null; 
			}else{
				Map<String, Object> ret = new HashMap<String, Object>();
				ret.put("splitRatio", new Double(.5));
				ret.put("key", keys.get(.5));
				return ret;
			}
		} catch (IOException e) {
			// don't split now, but check again later
			log.error("Failed to find lastkey "+e.getMessage());
			return null;
		}
	}
	
	/**
	 * Returns true if this tablet needs to be split
	 * 
	 */
	public synchronized boolean needsSplit() {
		boolean ret;
		
		if(closed)
			ret = false;
		else
			ret = findSplitRow() != null;
	
		return ret;
	}
	
	public static class MajorCompactionStats {
		private long entriesRead;
		private long entriesWritten;
		Text firstRow;
		Text lastRow;
		
		MajorCompactionStats(long er, long ew, Text firstRow, Text lastRow){
			this.setEntriesRead(er);
			this.setEntriesWritten(ew);
			this.firstRow = firstRow;
			this.lastRow = lastRow;
		}

		public MajorCompactionStats() {
			// TODO Auto-generated constructor stub
		}

		private void setEntriesRead(long entriesRead) {
			this.entriesRead = entriesRead;
		}

		public long getEntriesRead() {
			return entriesRead;
		}

		private void setEntriesWritten(long entriesWritten) {
			this.entriesWritten = entriesWritten;
		}

		public long getEntriesWritten() {
			return entriesWritten;
		}

		public void add(MajorCompactionStats mcs) {
			this.entriesRead += mcs.entriesRead;
			this.entriesWritten += mcs.entriesWritten;
		}
		
		
	}
	
	//BEGIN PRIVATE METHODS RELATED TO MAJOR COMPACTION
	
	private static MajorCompactionStats compactFiles(Text table, Configuration conf, FileSystem fs, String newMFName, CountingIterator citr, SortedKeyValueIterator<?, ?> itr) throws IOException{
		long t1 = System.currentTimeMillis();
		
		MyMapFile.Writer mfw = MapFileUtil.openMapFileWriter(table, conf, fs, newMFName);

		long entriesCompacted = 0;
		
		Text firstRow = null;
		Text lastRow = new Text();
		
		while(itr.hasTop()) {
			
			if(firstRow == null){
				firstRow = ((Key)itr.getTopKey()).getRow();
			}
			
			//TODO avoid copy 
			((Key)itr.getTopKey()).getRow(lastRow);
			
			mfw.append(itr.getTopKey(), itr.getTopValue());
			itr.next();
			entriesCompacted++;
		}

		mfw.close();

		if(firstRow == null){
			lastRow = null;
		}
		
		long t2 = System.currentTimeMillis();
		
		log.debug(String.format("MajC %,d read | %,d written | %,6d entries/sec | %6.3f secs",citr.getCount(), entriesCompacted, (int)(citr.getCount()/((t2 - t1)/1000.0)), (t2 - t1)/1000.0,(t2 - t1)/1000.0));
		return new MajorCompactionStats(citr.getCount(), entriesCompacted, firstRow, lastRow);
	}

	private Map<String, Set<String>> batchFilesToCompact(Collection<String> filesToCompact){
	
		/*
		 * It is assumed this method is called from a synchronized block.
		 * 
		 * This method will plan which groups of files to compact, given the following goals
		 * and constrains :
		 * 
		 *   1) There is maximum number of files that can be compacted at once
		 *   2) Files must be compacted in age order.Assume we do not want to compact more than
		 *   3) Want one resulting compaction file
		 *    
		 * As an example assume only 5 files are compacted at a time and we have 19 files to compact.  
		 * This method will produce the following compaction plan : 
		 *  
		 *   compact files 1 to 5 into compact_file_1 
		 *   compact files 6 to 10 into compact_file_2
		 *   compact files 11 to 15 into compact_file_3
		 *   compact compact_file_1, compact_file_2, and compact_file_3 into compact_file_4
		 * 
		 */
		
		LinkedList<String> listOfFiles = new LinkedList<String>(filesToCompact);
		TreeMap<String, Set<String>> compactBatches = new TreeMap<String, Set<String>>();
		
		if(listOfFiles.size() ==  1){
			compactBatches.put(getNextMapFilename(), new TreeSet<String>(listOfFiles));
			return compactBatches;
		}
		
		int maxFilesToCompact = cbConf.getInt("cloudbase.tabletserver.majorCompaction.maxOpen", 30)
								/ cbConf.getInt("cloudbase.tabletserver.majorCompaction.maxConcurrent", 1);
		
		while(listOfFiles.size() > maxFilesToCompact){
			String compactName = getNextMapFilename();
			TreeSet<String> compactBatch = new TreeSet<String>();
			for(int i = 0; i < maxFilesToCompact; i++){
				compactBatch.add(listOfFiles.removeFirst());
			}
			
			compactBatches.put(compactName, compactBatch);	
		}
		
		if(listOfFiles.size() == 1){
			//only one left, add it to the end of the last batch
			compactBatches.get(compactBatches.lastKey()).add(listOfFiles.removeFirst());
		}else if(listOfFiles.size() > 1){
			String compactName = getNextMapFilename();
			TreeSet<String> compactBatch = new TreeSet<String>();
			while(listOfFiles.size() > 0){
				compactBatch.add(listOfFiles.removeFirst());
			}
		
			compactBatches.put(compactName, compactBatch);
		}
		
		if(compactBatches.size() > 1){
			compactBatches.putAll(batchFilesToCompact(compactBatches.keySet()));
		}
		
		return compactBatches;
	}
	
	private static MySequenceFile.Reader[] openMapDataFiles(Configuration conf, FileSystem fs, Set<String> mapFiles, Text prevEndRow, int depth) throws IOException{
		ArrayList<MySequenceFile.Reader> mapfileArrayList = new ArrayList<MySequenceFile .Reader>();
		try{
			for(String map_file_location : mapFiles ) {
				//System.out.println("trying to open a new reader for " + map_file_location);
				if(prevEndRow != null){
					Key seekKey = new Key(prevEndRow);
					mapfileArrayList.add(MapFileUtil.seek(seekKey, depth, true, new Path(map_file_location), conf, fs));
				}else{
					MySequenceFile .Reader reader = new MySequenceFile .Reader(fs, new Path(map_file_location+"/"+MyMapFile.DATA_FILE_NAME), conf);
					mapfileArrayList.add(reader);
				}
			}
		}catch(Throwable e){
			log.warn("Some problem opening map files", e);
			//failed to open some map file... close the ones that were opened
			for (MySequenceFile.Reader reader : mapfileArrayList) {
				try{
					reader.close();
				}catch(Throwable e2){
					log.warn("Failed to close map file", e2);
				}
			}
			
			
			if(e instanceof IOException){
				throw (IOException)e;
			}else{
				throw new IOException("Failed to open map data files", e);
			}
		}
		
		return mapfileArrayList.toArray(new MySequenceFile .Reader[mapfileArrayList.size()]);
	}
	
    private MajorCompactionStats compactFiles(Configuration conf, FileSystem fs, 
			Path location, Set <String> filesToCompact, boolean propogateDeletes,
			KeyExtent extent, String compactTmpName) throws IOException{

		MySequenceFile.Reader[] mrs = null;
		CountingIterator citr = null;
		SortedKeyValueIterator<?, ?> itr = null;

		try{
			mrs = openMapDataFiles(conf, fs, filesToCompact, extent.getPrevEndRow(), 1);

			SortedKeyValueIterator iters[] = new SortedKeyValueIterator[mrs.length];

			for (int i = 0; i < mrs.length; i++) {
				iters[i] = new SequenceFileIterator(mrs[i], true);
			}

			citr = new CountingIterator(new MultiIterator(iters, extent.getEndRow(), true));
			DeletingIterator delIter = new DeletingIterator(citr, propogateDeletes);
			itr = IteratorUtil.loadIterators(IteratorScope.majc, delIter, extent, CBConfiguration.getInstance(extent.getTableName().toString()));

			MajorCompactionStats majCStats = compactFiles(extent.getTableName(), conf, fs, compactTmpName, citr, itr);
			return majCStats;
		}finally{
			//close sequence files opened
			if(mrs != null){
				for (int i = 0; i < mrs.length; i++) {
					try{
						mrs[i].close();
					}catch(Throwable e){
						log.warn("Failed to close map file", e);
					}
				}
			}
		}
		
		
	}

	private MajorCompactionStats _majorCompact() throws IOException {
		//TODO handle majorCompaction failure
	
		boolean propogateDeletes;
		//boolean needSplit;
		String minorCompactFileName = null;
		boolean doMinorCompaction = false;

		//String splitTmpName = null;
		Map<String, Set<String>> compactionBatches = null;
		
		long t1, t2, t3;
		
		synchronized(this) {
			//plan all that work that needs to be done in the sync block... then do the actual work
			//outside the sync block

			t1 = System.currentTimeMillis();
			
			Set<String> filesToCompact;

			
			majorCompactionWaitingToStart = true;
			
			tabletMemory.waitForMinC();

			t2 = System.currentTimeMillis();
			
			majorCompactionWaitingToStart = false;
			notifyAll();

			//System.out.println("DEBUG : MajC starting "+getExtent() + " : ");
			
			if(extent.equals(CBConstants.ROOT_TABLET_EXTENT)){
				//very important that we call this before doing major compaction,
				//otherwise deleted compacted files could possible be brought back
				//at some point if the file they were compacted to was legitimately
				//removed by a major compaction
				cleanUpFiles(fs, fs.listStatus(this.location), this.location, false);
			}

			//TODO getFilesToCompact() and cleanUpFiles() both 
			//do dir listings, which means two calls to the namenode
			//should refactor so that there is only one call
			Object ret[] = getFilesToCompact();
			if(ret == null){
				//nothing to compact
				return null;
			}
			filesToCompact = (Set<String>) ret[0];

			if(!((Boolean)ret[1]).booleanValue()){
				//since not all files are being compacted, we want to propagate delete entries
				propogateDeletes = true;
			}else{
				propogateDeletes = false;
			}
			
			if(tabletMemory.getMemTable().getNumEntries() > 0){
			    
			    tabletMemory.prepareForMinC();
				
				minorCompactFileName = getNextMapFilename();
				//set doMinorCompaction to true while in sync so we know we need to 
				//do minor compact... do not want to check if otherMemTable != null
				//outside of sync
				doMinorCompaction = true;
				
				filesToCompact.add(minorCompactFileName);
				
				//must log fact of minor compaction start here in sync block
				walog.minorCompactionStarted(minorCompactFileName);
			}
			
			// increment generation after choosing minor compact filename
			incrementGeneration();
			
			//organize compactions so that not too many files are
			//opened at once
			compactionBatches = batchFilesToCompact(filesToCompact);
			
			t3 = System.currentTimeMillis();
		}
		
		log.debug(String.format("MajC initiate lock %.2f secs, wait %.2f secs", (t3 - t2)/1000.0, (t2 - t1)/1000.0));
		
		if(doMinorCompaction){
			//numberOfEntries += otherMemTable.getNumEntries();
			//long size = otherMemTable.minorCompact(conf, fs, minorCompactFileName+"_tmp", minorCompactionAggregators);
			//sstableManager.bringMinorCompactionOnline(minorCompactFileName+"_tmp", minorCompactFileName, size);
			minorCompact(conf, fs, tabletMemory.getMinCMemTable(), minorCompactFileName+"_tmp", minorCompactFileName);
		}
		
		
		Iterator<Entry<String, Set<String>>> cbIter = compactionBatches.entrySet().iterator();
		
		MajorCompactionStats majCStats = new MajorCompactionStats();
		
		while(cbIter.hasNext()) {
			Entry<String, Set<String>> cbEntry = cbIter.next();
		
			boolean lastEntry = !cbIter.hasNext();
			String compactTmpName = cbEntry.getKey()+"_tmp";
		
			MajorCompactionStats mcs =
			 compactFiles(conf, 
					fs, 
					location, 
					cbEntry.getValue(), 
					lastEntry ? propogateDeletes : true, //alway propogate deletes, unless last batch  
					extent, 
					compactTmpName); 
			
			majCStats.add(mcs);
			
			 long size = fs.getFileStatus(new Path(compactTmpName + "/" + MyMapFile.DATA_FILE_NAME)).getLen();

			 sstableManager.bringMajorCompactionOnline(cbEntry.getValue(), compactTmpName, cbEntry.getKey(), new SSTableValue(size, mcs.entriesWritten, mcs.firstRow, mcs.lastRow));
		}
		
		return majCStats;
	}

	
	//END PRIVATE METHODS RELATED TO MAJOR COMPACTION
	
	/**
	 * Performs a major compaction on the tablet.  If needsSplit() returns true,
	 * the tablet is split and a reference to the new tablet is returned.
	 * 
	 * @return splitTablet
	 * @throws IOException
	 */
	
	//public Tablet majorCompact() throws IOException {
	public MajorCompactionStats majorCompact() throws IOException {
		
		MajorCompactionStats majCStats = null;
		
		try{
			synchronized(this) {
				if(closed || majorCompactionInProgress || splitWaiting)
					return null;
				majorCompactionInProgress = true;
				//System.out.println("DEBUG : "+extent+" major compaction starting");
			}
			
			majCStats = _majorCompact();
		} finally {
			//ensure we always reset boolean, even
			//when an exception is thrown
			synchronized (this) {
				majorCompactionInProgress = false;
				//System.out.println("DEBUG : "+extent+" major compaction finished");
			}
		}
		
		return majCStats;
	}
	

	/**
	 * Returns a KeyExtent object representing this tablet's key range.
	 * 
	 * @return extent
	 */
	public KeyExtent getExtent()  {
		return extent;
		//KeyExtent k = new KeyExtent(new Text(location.getParent().getName()),endKey,previousEndKey);
		//return k;
	}

	private synchronized void computeNumEntries(){
		Collection<SSTableValue> vals = sstableManager.getSSTableSizes().values();
		
		long numEntries = 0;
		
		for (SSTableValue tableValue : vals) {
			numEntries += tableValue.getNumEntries();
		}
		
		this.numEntriesInMemory = tabletMemory.getNumEntries();
		numEntries += tabletMemory.getNumEntries();
		
		this.numEntries = numEntries;
	}
	
	public long getNumEntries(){
		return numEntries;
	}
	
	public long getNumEntriesInMemory() {
		return numEntriesInMemory;
	}

	/*public void dump() throws IOException {
		
		MyMapFile.Reader[] a = null;
		
		try{
			a = tabletResources.reserveMapFileReaders();
			
			MMFAndMemoryIterator mmfi = new MMFAndMemoryIterator(new MultipleMapFileIterator(a,extent.getPrevEndRow(), null,extent.getEndRow(), false, 1), memTable, otherMemTable);
			
			while(mmfi.hasTop()) {
				log.info("TABLET DUMP ("+location+"): "+mmfi.getTopKey() + " " + mmfi.getTopValue());
				mmfi.next();
			}
			
		}finally{
			if(a != null){
				tabletResources.returnMapFileReaders();
			}
		}
		
		
	}*/
	
	public synchronized boolean isClosed() {
		return closed;
	}
	
	public synchronized void enableSplitting() {
		splitHoldRequests--;
		if(splitHoldRequests < 0) {
			throw new RuntimeException("splitHoldRequests is " + splitHoldRequests);
		}
		
		if(splitHoldRequests == 0){
			notifyAll();
		}
	}
	
	public synchronized boolean disableSplitting() {
		if(splitWaiting)
			return false;
		splitHoldRequests++;
		return true;
	}

	public boolean majorCompactionRunning() {
		return this.majorCompactionInProgress;
	}
	
    public boolean minorCompactionQueued() {
        return minorCompactionWaitingToStart;
    }

    public boolean minorCompactionRunning() {
        return minorCompactionInProgress;
   }

    public boolean majorCompactionQueued() {
        return needsMajorCompaction();
    }


	
	/**
	 * operations are disallowed while we split
	 * which is ok since splitting is fast
	 * 
	 * a minor compaction should have taken place before calling this
	 * so there should be relatively little left to compact
	 * 
	 * we just need to make sure major compactions aren't occurring
	 * if we have the major compactor thread decide who needs splitting
	 * we can avoid synchronization issues with major compactions
	 * 
	 * @throws IOException 
	 */
	
	private double splitRatio = -1;
	private boolean splitWaiting = false;
	
	static class SplitInfo {
		String dir;
		SortedMap<String, SSTableValue> ssTables;
		
		SplitInfo(String d, SortedMap<String, SSTableValue> sst){
			this.dir = d;
			this.ssTables = sst;
		}
		
	}
	
	public synchronized TreeMap<KeyExtent, SplitInfo> split() throws IOException {

	    if(extent.equals(CBConstants.ROOT_TABLET_EXTENT)){
	        log.warn("Cannot split root tablet");
	        return null;
	    }
	    
		if(majorCompactionInProgress || closed){
			log.debug("SSTable "+extent+" not splitting : majorCompactionInProgress = "+majorCompactionInProgress+"  closed = "+closed);
			return null;
		}
		
		if(splitHoldRequests > 0)
		{
			log.debug("SSTable "+extent+" waiting for splitHoldRequests to go to zero");
			
			//disable additional split hold request, this will also 
			//disable major compactions from starting, because lock is not
			//held while waiting a maj comp could start if not disabled
			splitWaiting  = true;
			
			while(splitHoldRequests > 0)
			{
				try {
					this.wait();
				} catch (InterruptedException e) {
				}
			}
			
			if(closed){
				//tablet was closed while in wait()
				log.debug("SSTable "+extent+" not splitting, tablet was closed while waiting for split hold request to go to zero");
				return null;
			}else{
				log.debug("SSTable "+extent+" splitHoldRequests went to zero");
			}
		}
		
		
		
		// java needs tuples ...
		TreeMap<KeyExtent, SplitInfo> newTablets = new TreeMap<KeyExtent, SplitInfo>();		
		
		//disable further updates and major compactions
		closed = true;
		
		tabletMemory.waitForMinC();
		
		// minor compact anything left
		if(tabletMemory.getMemTable().size() > 0) {
			String newMapfileLocation = getNextMapFilename();
			walog.minorCompactionStarted(newMapfileLocation);
			//long size = memTable.minorCompact(conf, fs, newMapfileLocation+"_tmp", minorCompactionAggregators);
			//sstableManager.bringMinorCompactionOnline(newMapfileLocation+"_tmp", newMapfileLocation, size);
			tabletMemory.prepareForMinC();
			minorCompact(conf, fs, tabletMemory.getMinCMemTable(), newMapfileLocation+"_tmp", newMapfileLocation);
		}
		
		// choose a split point
		Map<String, Object> splitPoint = findSplitRow();
		if(splitPoint == null || splitPoint.get("key") == null) {
			log.warn("had to abort split because splitRow was null");
			closed = false;
			return null;
		}
		
		tabletMemory = null;
		
		Text midRow = ((Key)splitPoint.get("key")).getRow();
		splitRatio = (Double)splitPoint.get("splitRatio");
		
		KeyExtent high = new KeyExtent(extent.getTableName(), extent.getEndRow(), midRow);
		KeyExtent low = new KeyExtent(extent.getTableName(), midRow, extent.getPrevEndRow());
		
		// TODO don't use the keys here
		String lowDirectory = Encoding.encodeDirectoryName(midRow);
		Path lowDirectoryPath = new Path(location.getParent().toString() + lowDirectory);
		
		try {
			fs.mkdirs(lowDirectoryPath);
		}
		catch (UnsupportedEncodingException uee) {
			log.error("couldn't encode key to safe filename");
			lowDirectory = "/key_encoding_error_" + System.currentTimeMillis();
			lowDirectoryPath = new Path(location.getParent().toString() + lowDirectory);
			fs.mkdirs(lowDirectoryPath); 
		}
		
		// write new tablet information to MetadataTable
		
		SortedMap<String, SSTableValue> sizes;
		try {
			sizes = MetadataTable.getSSTableSizes(high, systemCredentials());
		} catch (TableNotFoundException e) {
			throw new IOException(e);
		} catch (CBException e) {
			throw new IOException(e);
		} catch (CBSecurityException e) {
			throw new IOException(e);
		}
		
		SortedMap<String, SSTableValue> lowSstableSizes = new TreeMap<String, SSTableValue>();
		SortedMap<String, SSTableValue> highSstableSizes = new TreeMap<String, SSTableValue>();;
		
		// ---] LOW
		// sstables
		
		List<String> highSstablesToRemove = new ArrayList<String>();
		
		for(Entry<String, SSTableValue> entry : sizes.entrySet()) {
			
			Text firstRow = entry.getValue().getFirstRow();
			Text lastRow = entry.getValue().getLastRow();
			
			boolean rowsKnown = entry.getValue().isFirstAndLastRowKnown();
			
			if(rowsKnown && firstRow.compareTo(midRow) > 0){
				//only in high
				long highSize = entry.getValue().getSize();
				long highEntries = entry.getValue().getNumEntries();
				highSstableSizes.put(entry.getKey(), new SSTableValue(highSize, highEntries, entry.getValue()));
			}else if(rowsKnown && lastRow.compareTo(midRow) <= 0){
				//only in low
				long lowSize = entry.getValue().getSize();
				long lowEntries = entry.getValue().getNumEntries();
				lowSstableSizes.put(entry.getKey(), new SSTableValue(lowSize, lowEntries, entry.getValue()));
				
				highSstablesToRemove.add(entry.getKey());
			}else{
				long lowSize = (long)(entry.getValue().getSize() * splitRatio);
				long lowEntries = (long)(entry.getValue().getNumEntries() * splitRatio);
				lowSstableSizes.put(entry.getKey(), new SSTableValue(lowSize, lowEntries, entry.getValue()));
			
				long highSize = (long)(entry.getValue().getSize() * (1.0 - splitRatio));
				long highEntries = (long)(entry.getValue().getNumEntries() * (1.0 - splitRatio));
				highSstableSizes.put(entry.getKey(), new SSTableValue(highSize, highEntries, entry.getValue()));
			}
		}
		
		while(!MetadataTable.splitTablet(high, highSstableSizes, extent.getPrevEndRow(), splitRatio, systemCredentials())){
			log.warn("Metadata table split update failed, will retry...");
			UtilWaitThread.sleep(1000);
		}
		
		while(!MetadataTable.addNewTablet(low, lowDirectory,  TabletServer.getClientAddressString(), lowSstableSizes, systemCredentials())){
			log.warn("Metadata table split update failed, will retry...");
			UtilWaitThread.sleep(1000);
		}

		while(!MetadataTable.finishSplit(high, highSstablesToRemove, systemCredentials())){
			log.warn("Metadata table split update failed, will retry...");
			UtilWaitThread.sleep(1000);
		}
		
		log.log(TLevel.TABLET_HIST, extent+" split "+low+" "+high);
		
		newTablets.put(high, new SplitInfo(tabletDirectory, highSstableSizes));
		newTablets.put(low, new SplitInfo(lowDirectory, lowSstableSizes));
		
		// don't close() the tablet yet
		return newTablets;
	}
	
	public SortedMap<String, SSTableValue> getSSTables() throws IOException {
		return sstableManager.getSSTableSizes();
	}
	
	public double getSplitRatio() {
		// TODO Auto-generated method stub
		return splitRatio;
	}

	public double queryRate(long delta, double decay) {
		if(delta < 1000)
			return prevQueryRate;
		if(prevQueryRate < 0.1)
			prevQueryRate = queryCount / (delta / 1000.0) * decay;
		else 
			prevQueryRate = queryCount / (delta / 1000.0) * decay + (prevQueryRate * (1-decay));
		queryCount = 0L;
//		log.info("Query rate updated, queries " + queryCount + " new rate: " + prevQueryRate + " time: " + delta + " decay: " + decay);
		return prevQueryRate;
	}

	public double ingestRate(long delta, double decay) {
		if(delta < 1000)
			return prevIngestRate;
		if(prevIngestRate < 0.1)
			prevIngestRate = ingestCount / (delta / 1000.0) * decay;
		else
			prevIngestRate = ingestCount / (delta / 1000.0) * decay + (prevIngestRate * (1-decay));
		ingestCount = 0L;
//		log.info("Ingest rate updated, ingests: " + ingestCount + " new rate: " + prevIngestRate + " time: " + delta + " decay: " + decay);
		return prevIngestRate;
	}

	public void importMapFiles(Map<String, Long> fileMap) throws IOException {
		Map<String, SSTableValue> entries = new HashMap<String, SSTableValue>(fileMap.size());
		
		if(cbConf.getBoolean("cloudbase.tablet.bulkImport.estimateEntries", false)){
			for(String path : fileMap.keySet()){
				long estEntries = MapFileUtil.estimateEntriesInMapFile(fs, conf, new Path(path), extent);
				entries.put(path, new SSTableValue(fileMap.get(path), estEntries));
			}
		}else{
			for(String path : fileMap.keySet()){
				entries.put(path, new SSTableValue(fileMap.get(path), 0l));
			}
		}
		
		synchronized (this) {
			if(closed){
				throw new IOException("tablet "+extent+" is closed");
			}
			
			sstableManager.importMapFiles(entries);
			lastMapFileImportTime = System.currentTimeMillis();
		}
	}

}
