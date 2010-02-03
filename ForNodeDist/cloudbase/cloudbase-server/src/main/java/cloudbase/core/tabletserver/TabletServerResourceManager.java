package cloudbase.core.tabletserver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.log4j.Logger;

import cloudbase.core.CBConstants;
import cloudbase.core.conf.CBConfiguration;
import cloudbase.core.data.KeyExtent;
import cloudbase.core.data.MyMapFile;
import cloudbase.core.data.MyMapFile.Reader;
import cloudbase.core.util.Daemon;
import cloudbase.core.util.LoggingRunnable;
import cloudbase.core.util.MapFileUtil;
import cloudbase.core.util.UtilWaitThread;

/**
 * ResourceManager is responsible for managing the resources
 * of all tablets within a tablet server.  
 * 
 *
 */
public class TabletServerResourceManager {
	
	/*
	 * DEADLOCK WARNING
	 * 
	 * Threads call methods in TabletResourceManager that call methods
	 * in TabletServerResourceManager that need to access data in 
	 * other TabletResourceManager objects.  Each TabletResourceManager
	 * object has its own lock seperate from TabletServerResourceManager.
	 * Must ensure these method call graphs do not cause deadlock.
	 * 
	 * The surest way to avoid deadlock is to always obtain the locks
	 * in the same order.  Obtain the TabletServerResourceManager lock
	 * first, then the TabletResourceManager lock.
	 * 
	 * The reason each TabletResourceManager has its own lock is to
	 * allow for concurrency in tablet server operations.
	 * 
	 */
	
	private ExecutorService minorCompactionThreadPool;
	private ExecutorService majorCompactionThreadPool;
	private ExecutorService rootMajorCompactionThreadPool;
	private ExecutorService defaultMajorCompactionThreadPool;
	private ExecutorService splitThreadPool;
	private ExecutorService defaultSplitThreadPool;
	private ExecutorService migrationPool;
	private ExecutorService assignmentPool;
	private ExecutorService assignMetaDataPool;
	private Map<String, ExecutorService> threadPools = new TreeMap<String, ExecutorService>(); 
	
	private Configuration conf;
	private FileSystem fs;
	
	private CBConfiguration cbConf;
	
	private HashSet<TabletResourceManager> tabletResources;
	
	private int maxOpenFiles;

	@SuppressWarnings("unused")
    private TabletServer tabletServer;
	
	private MemoryManager memoryManger;
	
    @SuppressWarnings("unused")
    private Runnable minCStarter;
    
    private MemoryManagementFramework memMgmt;
	
	private static Logger log = Logger.getLogger(TabletServerResourceManager.class.getName());
	
	private static class NamingThreadFactory implements ThreadFactory {

		private ThreadFactory dtf = Executors.defaultThreadFactory();
		private int threadNum = 1;
		private String name;
		
		NamingThreadFactory(String name){
			this.name = name;
		}
		
		public Thread newThread(Runnable r) {
			Thread thread = dtf.newThread(r);
			thread.setName(name+" "+threadNum++);
			return thread;
		}
		
	}
	
	private ExecutorService addEs(String name, ExecutorService tp){
		if(threadPools.containsKey(name)){
			throw new IllegalArgumentException("Cannot create two executor services with same name "+name);
		}
		
		threadPools.put(name,tp);
		return tp;
	}
	
	private ExecutorService createEs(int max, String name){
	    return addEs(name, Executors.newFixedThreadPool(max, new NamingThreadFactory(name)));
	}
	
	private ExecutorService createEs(int min, int max, int timeout, String name){
		return addEs(name, 
		             new ThreadPoolExecutor(min, max,
		                                    timeout, TimeUnit.SECONDS,
		                                    new LinkedBlockingQueue<Runnable>(),
		                                    new NamingThreadFactory(name)));
	}
	
	public TabletServerResourceManager(Configuration conf, FileSystem fs, TabletServer tabServer){
		
		this.conf = conf;
		this.fs = fs;
		this.tabletServer = tabServer;
		
		this.cbConf = CBConfiguration.getInstance();
		
		long maxMemory = cbConf.getLong("cloudbase.tabletserver.maxMapMemory", -1);
		Runtime runtime = Runtime.getRuntime();
		if (maxMemory > runtime.maxMemory()) {
		    throw new IllegalArgumentException(String.format("Maximum tablet server map memory %,d is too large for this JVM configuration %,d", maxMemory, runtime.maxMemory()));
		}
		runtime.gc();
		if (maxMemory > runtime.freeMemory()) {
		    log.warn("In-memory map may not fit into local memory space.");
		}
		
		minorCompactionThreadPool = createEs(cbConf.getInt("cloudbase.tabletserver.minorCompaction.maxConcurrent", 4), "minor compactor");

		//TODO make this thread pool have a priority queue... and execute tablets with the most
		//files first!
		majorCompactionThreadPool = createEs(cbConf.getInt("cloudbase.tabletserver.majorCompaction.maxConcurrent", 1), "major compactor");
		rootMajorCompactionThreadPool = createEs(0,1,300, "md root major compactor");
		defaultMajorCompactionThreadPool = createEs(0,1,300, "md major compactor");
		
		splitThreadPool = createEs(1, "splitter");
		defaultSplitThreadPool = createEs(0,1,60, "md splitter");
		
		migrationPool = createEs(cbConf.getInt("cloudbase.tabletserver.tabletMigration.maxConcurrent",1),"tablet migration");

		//TODO revriew task put on this thread pool to ensure thread safety, task used to be handled by single thread
		//this thread pool must have only one thread as long as it processes CBConstants.MSG_MS_START_SERVICE
		assignmentPool = createEs(/*cbConf.getInt("cloudbase.tabletserver.tabletAssignment.maxConcurrent",1)*/ 1 ,"tablet assignment");

		assignMetaDataPool = createEs(0,1,60,"metadata tablet assignment");

		tabletResources = new HashSet<TabletResourceManager>();
		
		maxOpenFiles = cbConf.getInt("cloudbase.tabletserver.maxOpen", 90) - 
					cbConf.getInt("cloudbase.tabletserver.majorCompaction.maxOpen", 30);
		
		try {
			memoryManger = (MemoryManager) Class.forName(CBConfiguration.getInstance().get("cloudbase.tabletserver.memoryManager")).newInstance();
			log.debug("Loaded memory manager : "+memoryManger.getClass().getName());
		} catch (Exception e) {
			log.error("Failed to find memory manger in config, using default", e);
		} 
		
		if(memoryManger == null){
			memoryManger = new LargestFirstMemoryManager();
		} 
		
		memMgmt = new MemoryManagementFramework(); 
	}
	
	private static class TabletStateImpl implements TabletState, Cloneable {

		private long lct;
		private Tablet tablet;
		private long mts;
		private long mcmts;

		public TabletStateImpl(Tablet t, long mts, long lct, long mcmts) {
		    this.tablet = t;
		    this.mts = mts;
            this.lct = lct;
            this.mcmts = mcmts;
        }
		
        public KeyExtent getExtent() {
			return tablet.getExtent();
		}

        Tablet getTablet(){
            return tablet;
        }
        
        public long getLastCommitTime() {
			return lct;
		}

        public long getMemTableSize() {
			return mts;
		}

        public long getMinorCompactingMemTableSize() {
			return mcmts;
		}
	}
	
	private class MemoryManagementFramework {
	   private Map<KeyExtent, TabletStateImpl> tabletReports;
	   private LinkedBlockingQueue<TabletStateImpl> memUsageReports;
	   private long lastMemCheckTime = System.currentTimeMillis();
	   private long maxMem;
	   
	   MemoryManagementFramework(){
	       tabletReports = Collections.synchronizedMap(new HashMap<KeyExtent, TabletStateImpl>());
	       memUsageReports = new LinkedBlockingQueue<TabletStateImpl>();
	       maxMem = CBConfiguration.getInstance().getLong("cloudbase.tabletserver.maxMapMemory", 1073741824);
	       
	       Runnable r1 = new Runnable(){
	           public void run() {
	               processTabletMemStats();
	           }
	       };
	       
	       Thread t1 = new Daemon(new LoggingRunnable(log, r1));
	       t1.setPriority(Thread.NORM_PRIORITY+1);
	       t1.setName("CB Memory Guard");
	       t1.start();
	       
	       Runnable r2 = new Runnable(){
               public void run() {
                   manageMemory();
               }
           };
           
           Thread t2 = new Daemon(new LoggingRunnable(log, r2));
           t2.setName("CB Minor Compaction Initiator");
           t2.start();
	       
	   }
	   
	   private long lastMemTotal = 0;
	   
	   private void processTabletMemStats(){
	       while(true){   
               try {
                               
                   TabletStateImpl report = memUsageReports.take();
                               
                   while(report != null){
                       tabletReports.put(report.getExtent(), report);
                       report = memUsageReports.poll();
                   }
                   
                   long delta = System.currentTimeMillis() - lastMemCheckTime; 
                   if(holdCommits || delta > 50 || lastMemTotal > 0.90 * maxMem){
                       lastMemCheckTime = System.currentTimeMillis();
                       
                       long totalMemUsed = 0;

                       synchronized (tabletReports) {
                           for (TabletStateImpl tsi : tabletReports.values()) {
                               totalMemUsed += tsi.getMemTableSize();
                               totalMemUsed += tsi.getMinorCompactingMemTableSize();
                           }
                       }
                       
                       if(totalMemUsed > 0.95*maxMem){
                           holdAllCommits(true);
                       }else{
                           holdAllCommits(false);
                       }
                       
                       lastMemTotal = totalMemUsed;
                   }
                   
               } catch (InterruptedException e) {
                   log.warn(e,e);
               }
	       }
	   }
	   
	   private void manageMemory(){
	       while(true){   
	           MemoryManagementActions mma = null;

	           try{
	               ArrayList<TabletState> tablets;
	               synchronized (tabletReports) {
	                   tablets = new ArrayList<TabletState>(tabletReports.values());
	               }
	               mma = memoryManger.getMemoryManagementActions(tablets);

	           }catch(Throwable t){
	               log.error("Memory manager failed "+t.getMessage(), t);
	           }

	           if(mma != null && mma.tabletsToMinorCompact != null && mma.tabletsToMinorCompact.size() > 0){
	               for (KeyExtent keyExtent : mma.tabletsToMinorCompact) {
	                   TabletStateImpl tabletReport = tabletReports.get(keyExtent);

	                   if(tabletReport == null){
	                       log.warn("Memory manager asked to compact nonexistant tablet "+keyExtent);
	                       continue;
	                   }

	                   tabletReport.getTablet().initiateMinorCompaction();
	               }

	               //log.debug("mma.tabletsToMinorCompact = "+mma.tabletsToMinorCompact);
	           }
	           
	           UtilWaitThread.sleep(250);
	       }
	   }

	   public void updateMemoryUsageStats(Tablet tablet, long size, long lastCommitTime, long mincSize) {
	       memUsageReports.add(new TabletStateImpl(tablet, size, lastCommitTime, mincSize));
	   }

	   public void tabletClosed(KeyExtent extent) {
	       tabletReports.remove(extent);
	   }
	}
	
	private Object commitHold = new String("");
	private volatile boolean holdCommits = false;
	private long holdStartTime;
	
	protected void holdAllCommits(boolean holdAllCommits) {
		synchronized (commitHold) {
			if(holdCommits != holdAllCommits){
				holdCommits = holdAllCommits;
				
				if(holdCommits){
					holdStartTime = System.currentTimeMillis();
				}
				
				if(!holdCommits){
					log.debug(String.format("Commits held for %6.2f secs",(System.currentTimeMillis()- holdStartTime) / 1000.0));
					commitHold.notifyAll();
				}
			}
		}
		
	}

	private void waitUntilCommitsAreEnabled(){
		if(holdCommits){
			synchronized (commitHold) {
				while(holdCommits){
					try {
						commitHold.wait();
					} catch (InterruptedException e) {}
				}
			}
		}
	}
	
	public void close() {
		for (ExecutorService executorService : threadPools.values()) {
		    executorService.shutdown();   
        }

		for (Entry<String, ExecutorService> entry : threadPools.entrySet()) {
		    while(true){
		        try {
                    if(entry.getValue().awaitTermination(60, TimeUnit.SECONDS)){
                        break;
                    }else{
                        log.info("Waiting for thread pool "+entry.getKey()+" to shutdown");   
                    }
                } catch (InterruptedException e) {
                  log.warn(e);
                }          
            }    
        }
	}
	
	public synchronized TabletResourceManager createTabletResourceManager(){
		TabletResourceManager trm = new TabletResourceManager();
		return trm;
	}
	
	private int countFilesOpen(){
		//count how many files are currently open
		int filesOpen = 0;
		for (TabletResourceManager tabletResource : tabletResources) {
			if(!tabletResource.closed){
				//go directly to openMapFiles object inside
				//tabletResource to avoid DEADLOCK
				filesOpen += tabletResource.openMapFiles.size();
			}
		}
		
		return filesOpen;
	}
	
	//BEGIN methods called by class TabletResources
	//if adding a new method, consider DEADLOCK warning
	
	synchronized private void addTabletResource(TabletResourceManager tr) {
		tabletResources.add(tr);
	}
	
	synchronized private void removeTabletResource(TabletResourceManager tr) {
		tabletResources.remove(tr);
	}
	
	synchronized private int openMapFiles(TabletResourceManager requestor, Set<String> mapFiles, Map<String, MyMapFile.Reader> openMapFiles) throws IOException {
		//TODO detect thrashing! ... when we are constantly opening and closing tablets files
		
		int opens = 0;
		
		int numToOpen = 0;
		
		for (String path : mapFiles) {
			if(!openMapFiles.containsKey(path)){
				numToOpen++;
			}
		}
		
		if(countFilesOpen() + numToOpen > maxOpenFiles){
			//oh no, too many files will be opened
			if(mapFiles.size() > maxOpenFiles){
				//uh oh requestor alone would exceed maxOpenFiles if we honored this
				//request... 
				
				//TODO make requestor do a major compaction
				
				log.error("cannot open files for tablet "+requestor.tablet.getExtent()+", #map files = "+mapFiles.size()+" maxOpenFiles = "+maxOpenFiles);
				throw new IOException("cannot open files for tablet "+requestor.tablet.getExtent()+", #map files = "+mapFiles.size()+" maxOpenFiles = "+maxOpenFiles);
			}
			
			log.warn("Too many files open, must close other tablets to open tablet "+requestor.tablet.getExtent()+" countFilesOpen() = "+countFilesOpen()+" numToOpen = "+numToOpen+" maxOpenFiles = "+maxOpenFiles);
			
			//close other tablets files until we free up enough files,
			//consider least recently used tablets first
			
			//sort tablets in LRU order
			TabletResourceManager lruTabletResources[] = tabletResources.toArray(new TabletResourceManager[tabletResources.size()]);
			Arrays.sort(lruTabletResources, new Comparator<TabletResourceManager>(){
				public int compare(TabletResourceManager o1, TabletResourceManager o2) {
					if(o1.lastReserveTime < o2.lastReserveTime){
						return -1;
					}else if(o1.lastReserveTime > o2.lastReserveTime){
						return 1;
					}else{
						return 0;
					}
				}}
			);
			
			int index = 0;
			while(index < lruTabletResources.length && countFilesOpen() + numToOpen > maxOpenFiles){
				if(lruTabletResources[index] == requestor){
					index++;
					continue;
				}

				//calling this function can result in DEADLOCK is locks
				//are not acquired in proper order
				
				int numClosed = lruTabletResources[index].closeAllMapFiles();
				if(numClosed > 0){
					log.debug("Closed files : tablet = "+lruTabletResources[index].tablet.getExtent()+" # closed = "+numClosed);
				}
				
				index++;
			}
			
			if(countFilesOpen() + numToOpen > maxOpenFiles){
				log.error("unable to close enough files in tablet server to open tablet "+requestor.tablet.getExtent()+" files countFilesOpen() = "+countFilesOpen()+" numToOpen = "+numToOpen+" maxOpenFiles = "+maxOpenFiles);
				throw new IOException("unable to close enough files in tablet server to open tablet "+requestor.tablet.getExtent()+" files countFilesOpen() = "+countFilesOpen()+" numToOpen = "+numToOpen+" maxOpenFiles = "+maxOpenFiles);
			}
		}
		
		//TODO this method should open all map files requested or none... if there is a failure part way through opening map files
		//it should close the map files it has already opened and then throw an exception
		
		//open the tablets unopened files
		for (String path : mapFiles) {
			if(!openMapFiles.containsKey(path)){
				MyMapFile.Reader mfr = MapFileUtil.openMapFile(fs, path, conf);
				openMapFiles.put(path, mfr);
				opens++;
			}
		}
		
		return opens;
	}
	
	//END methods called by class TabletResources
	
	public class TabletResourceManager {
		
		private Set<String> mapFiles;
		private Map<String, MyMapFile.Reader> openMapFiles;
		private long lastReserveTime = 0;
		
		private volatile boolean openFilesReserved = false;
		
		private volatile boolean closed = false;
		
		private volatile Reader[] cachedReaderArray;
		private Tablet tablet;
		
		private CBConfiguration tableConf;
		
		TabletResourceManager(){
			
			
			mapFiles = Collections.synchronizedSortedSet(new TreeSet<String>());
			openMapFiles = Collections.synchronizedSortedMap(new TreeMap<String, MyMapFile.Reader>());
			
			cachedReaderArray = null;
		}
		
		void setTablet(Tablet tablet, CBConfiguration tableConf){
			this.tablet = tablet;
			this.tableConf = tableConf;
			//TabletResourceManager is not really initializied until this
			//function is called.... so do not make it publicly available 
			//until now
			addTabletResource(this);
		}
		
		synchronized private void closeMapFiles() throws IOException {
			if(closed) throw new IOException("closed");
			synchronized(openMapFiles){
				Iterator<Entry<String, Reader>> iter = openMapFiles.entrySet().iterator();
				while(iter.hasNext()){
					Entry<String, Reader> entry = iter.next();
					if(!mapFiles.contains(entry.getKey())){
						try {
							entry.getValue().close();
						} catch (IOException e) {
							log.error(e.toString());
						}
						iter.remove();
					}
				}
			}
			cachedReaderArray = null;
		}
		
		synchronized private int closeAllMapFiles() throws IOException{
			if(closed) return -1;
			
			while(openFilesReserved){
				try {
					wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					log.error(e.toString());
				}
			}
			
			int numClosed = 0;
			
			synchronized(openMapFiles){
				Iterator<Entry<String, Reader>> iter = openMapFiles.entrySet().iterator();
			
				while(iter.hasNext()){
					Entry<String, Reader> entry = iter.next();
				
					try {
						entry.getValue().close();
						numClosed++;
					} catch (IOException e) {
						log.error(e.toString());
					}
				}
			
				openMapFiles.clear();
			}
			
			cachedReaderArray = null;
			return numClosed;
		}
		
		//BEGIN methods that Tablets call to manage their set of open map files
		synchronized void addMapFile(String name) throws IOException {
			if(closed) throw new IOException("closed");
			if(openFilesReserved) throw new IOException("tired to add map file while open files reserved");
			
			if(mapFiles.contains(name)){
				log.error("Adding map files that is already in set "+name);
			}
			
			mapFiles.add(name);
			
			cachedReaderArray = null;
		}
		
		synchronized void removeMapFiles(Set<String> filesCompacted) throws IOException {
			if(closed) throw new IOException("closed");
			if(openFilesReserved) throw new IOException("tried to remove map files while open files reserved");
			
			for (String file : filesCompacted) {
				if(!mapFiles.contains(file)){
					log.warn("Requested removal of file not in set "+file);
				}
			}
			
			mapFiles.removeAll(filesCompacted);
			
			closeMapFiles();

			cachedReaderArray = null;
		}
		
		synchronized SortedSet<String> getCopyOfMapFilePaths() throws IOException {
			if(closed) throw new IOException("closed");
			return new TreeSet<String>(mapFiles);
		}
	
		synchronized boolean containsAllMapFiles(TreeSet<String> filesToCompact) throws IOException {
			if(closed) throw new IOException("closed");
			return mapFiles.containsAll(filesToCompact);
		}
		
		Reader[] reserveMapFileReaders() throws IOException {
			boolean needToOpenFiles;

			//this code is structured in a peculiar way to avoid deadlock
			//in a sync block, it is determined if the TabletServerResourceManager.this
			//lock needs to be obtained, if so leave the sync block and then obtain
			//TabletServerResourceManager.this lock and then "this" lock.
			
			//The code is structured this way to allow for concurrency... only
			//obtain the TabletServerResourceManager.this lock (which blocks
			//everything) when files need to be opened... if files are already
			//open, then only need to lock "this"
			
			synchronized(this){
				if(closed) throw new IOException("closed");
				if(openFilesReserved) throw new IOException("tried to reserve open files while open files reserved");
				
				//determine in synch block if need to open files.
				if(cachedReaderArray == null){
					needToOpenFiles = true;
				}else{
					//files are already open, go ahead and reserve them
					needToOpenFiles = false;
					lastReserveTime = System.currentTimeMillis();
					openFilesReserved = true;
				}
			}
			
			if(needToOpenFiles){
				//when opening file, always obtain the TabletServerResourceManager.this
				//lock first before obtaining "this" lock... this will prevent deadlock
				//because all threads obtain locks in the same order!
				synchronized(TabletServerResourceManager.this){
					synchronized(this){
						if(closed) throw new IOException("closed");
						if(openFilesReserved) throw new IOException("tried to reserve open files while open files reserved");
						
						TabletServerResourceManager.this.openMapFiles(this, mapFiles, openMapFiles);
						cachedReaderArray = openMapFiles.values().toArray(new MyMapFile.Reader[openMapFiles.size()]);
						lastReserveTime = System.currentTimeMillis();
						openFilesReserved = true;
					}
				}
			}

			return cachedReaderArray;
		}
		
		synchronized void returnMapFileReaders() throws IOException {
			if(closed) throw new IOException("closed");
			if(!openFilesReserved) throw new IOException("tried to return open files when open files not reserved");
			
			openFilesReserved = false;
			notifyAll();
		}
		
		//END methods that Tablets call to manage their set of open map files
		
		//BEGIN methods that Tablets call to manage memory
		
		private long lastReportedSize;
		private long lastReportedCommitTime;
		private long lastReportedMincSize;
		private volatile int memStatModCount = 0;
		
		public void updateMemoryUsageStats(long size, long lastCommitTime, long mincSize) {
		   
		    //only one thread should call this at a time... add a simple sanity
		    //check to try and detect if multiple threads ever enter this code
		    //... doing this to avoid synchronizing again, since the code that calls
		    //this in Tablet is currently synchronized... however the Tablet code 
		    //could change
		    int expectedModCount = ++memStatModCount;
		    
		    //do not want to update stats for every little change,
		    //so only do it under certain circumstances... the reason
		    //for this is that reporting stats acquires a lock, do
		    //not want all tablets locking on the same lock for every
		    //commit
		    if(lastReportedMincSize != mincSize ||
		            lastCommitTime - lastReportedCommitTime > 1000 ||
		            size - lastReportedSize > 32000)
		    {
		        lastReportedSize = size;
		        lastReportedCommitTime = lastCommitTime;
		        lastReportedMincSize = mincSize;
		        
		        memMgmt.updateMemoryUsageStats(tablet, size, lastCommitTime, mincSize);
		    }
		    
		    if(expectedModCount != memStatModCount){
		        throw new ConcurrentModificationException();
		    }
        }

        void waitUntilCommitsAreEnabled(){
            if(tablet.getExtent().getTableName().toString().equals(CBConstants.METADATA_TABLE_NAME)){
                //commits are never held for the !METADATA table
                return;
            }
            
            TabletServerResourceManager.this.waitUntilCommitsAreEnabled();
        }
		
		//END methods that Tablets call to manage memory
		
		//BEGIN methods that Tablets call to make decisions about major compaction
		//when too many files are open, we may want tablets to compact down
		//to one map file
		synchronized boolean needsMajorCompaction() throws IOException {
			if(closed) return false;//throw new IOException("closed");
			boolean needsMajorCompaction;
			needsMajorCompaction = mapFiles.size() > tableConf.getInt("cloudbase.tablet.majorCompaction.threshold", 5);
			
			return needsMajorCompaction;
		}
		
		int getMaxMapFilesMajorCompactionCanLeaveUncompacted() throws IOException{
			if(closed) throw new IOException("closed");
			
			int keep;
			
			keep = tableConf.getInt("cloudbase.tablet.majorCompaction.keep", 2);
			
			if(keep > 0){
				float prob = tableConf.getFloat("cloudbase.tablet.majorCompaction.compactAllProbability", .05f);
				double rand = Math.random();
				if(rand <= prob){
					keep = 0;
				}
			}
			
			return keep;
		}
		//END methods that Tablets call to make decisions about major compaction
		
		//tablets call this method to run minor compactions,
		//this allows us to control how many minor compactions
		//run concurrently in a tablet server
		void executeMinorCompaction(final Runnable r){
			//TODO hold minor compaction if there are too many map
			//files??? like 50% of maxOpen?  kick off major compaction?
			//
			//could add delays and make the delays larger as num open
			//approaches max open
			
			minorCompactionThreadPool.execute(new LoggingRunnable(log, r));
		}

		void close() throws IOException {
			//always obtain locks in same order to avoid deadlock
			synchronized (TabletServerResourceManager.this) {
				synchronized (this) {
					if(closed) throw new IOException("closed");
					if(openFilesReserved) throw new IOException("tired to close files while open files reserved");
					
					mapFiles.clear();
					closeAllMapFiles();
					
					TabletServerResourceManager.this.removeTabletResource(this);
					
					memMgmt.tabletClosed(tablet.getExtent());
					memoryManger.tabletClosed(tablet.getExtent());
					
					closed = true;
				}
			}
		}

		public TabletServerResourceManager getTabletServerResourceManager() {
			return TabletServerResourceManager.this;
		}
	}

	public void executeSplit(KeyExtent tablet, Runnable splitTask) {
		if(tablet.getTableName().toString().equals(CBConstants.METADATA_TABLE_NAME)){
			if(tablet.equals(CBConstants.ROOT_TABLET_EXTENT)){
				log.warn("Saw request to split root tablet, ignoring");
				return;
			}
			defaultSplitThreadPool.execute(splitTask);
		}else{
			splitThreadPool.execute(splitTask);
		}
	}
	
	public void executeMajorCompaction(KeyExtent tablet, Runnable compactionTask) {
		if(tablet.equals(CBConstants.ROOT_TABLET_EXTENT)){
			rootMajorCompactionThreadPool.execute(compactionTask);
		}else if(tablet.getTableName().toString().equals(CBConstants.METADATA_TABLE_NAME)){
			defaultMajorCompactionThreadPool.execute(compactionTask);
		}else{
			majorCompactionThreadPool.execute(compactionTask);
		}
	}
	
	public void addAssignment(Runnable assignmentHandler)
	{
		assignmentPool.execute(assignmentHandler);
	}
	
	public void addMetaDataAssignment(Runnable assignmentHandler)
	{
		assignMetaDataPool.execute(assignmentHandler);
	}
	
	public void addMigration(Runnable migrationHandler)
	{
		migrationPool.execute(migrationHandler);
	}
}
