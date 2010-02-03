package cloudbase.core.client.impl;

import java.io.IOException;
import java.lang.management.CompilationMXBean;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

import org.apache.hadoop.io.Text;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import cloudbase.core.CBConstants;
import cloudbase.core.client.CBException;
import cloudbase.core.client.CBSecurityException;
import cloudbase.core.client.Instance;
import cloudbase.core.client.MutationsRejectedException;
import cloudbase.core.client.TableNotFoundException;
import cloudbase.core.client.impl.TabletLocator.TabletServerMutations;
import cloudbase.core.conf.CBConfiguration;
import cloudbase.core.constraints.Violations;
import cloudbase.core.security.thrift.AuthInfo;
import cloudbase.core.security.thrift.ThriftSecurityException;
import cloudbase.core.data.ConstraintViolationSummary;
import cloudbase.core.data.KeyExtent;
import cloudbase.core.data.Mutation;
import cloudbase.core.data.UpdateErrors;
import cloudbase.core.tabletserver.thrift.NoSuchScanIDException;
import cloudbase.core.tabletserver.thrift.TabletClientService;

import com.facebook.thrift.TException;
import com.facebook.thrift.protocol.TBinaryProtocol;
import com.facebook.thrift.protocol.TProtocol;
import com.facebook.thrift.transport.TTransport;
import com.facebook.thrift.transport.TTransportException;

public class TabletServerBatchWriter {

	private static class MutationSet {
		
		private HashMap<String, List<Mutation>> mutations;
		private int size = 0;
		private int memoryUsed = 0;
		
		MutationSet(){
			mutations = new HashMap<String, List<Mutation>>();
		}
		
		void addMutation(String table, Mutation mutation){
			List<Mutation> tabMutList = mutations.get(table);
			if(tabMutList == null){
				tabMutList = new ArrayList<Mutation>();
				mutations.put(table, tabMutList);
			}
			
			tabMutList.add(mutation);
			size++;
			
			memoryUsed += mutation.numBytes();
		}
		
		Map<String, List<Mutation>> getMutations(){
			return mutations;
		}
		
		void clear(){
			mutations.clear();
			size = 0;
			memoryUsed = 0;
		}
		
		int size(){
			return size;
		}

		public void addAll(MutationSet failures) {
			Set<Entry<String, List<Mutation>>> es = failures.getMutations().entrySet();
			
			for (Entry<String, List<Mutation>> entry : es) {
				String table = entry.getKey();
				
				for(Mutation mutation : entry.getValue()){
					addMutation(table, mutation);
				}
			}
		}

		public void addAll(String table, List<Mutation> mutations) {
			for(Mutation mutation : mutations){
				addMutation(table, mutation);
			}
		}
		
		public int getMemoryUsed(){
			return memoryUsed;
		}
		
	}
	
	
	private MutationSet processedMutations;
	private MutationSet processingMutations;
	private MutationSet mutations;
	
	//private String table;
	private boolean isProcessing;
	
	private ExecutorService sendThreadPool;
	private ExecutorService processThreadPool;
	
	private long processingMemory = 0;
	private long maxMemory = 0;
	private long totalMutations = 0;
	private long totalAdded = 0;
	private long totalFailed = 0;
	private long totalSent = 0;
	private long totalBinned = 0;
	private long totalBinTime = 0;
	private long totalSendTime = 0;
	private long startTime = 0;
	private long initialGCTimes;
	private long initialCompileTimes;
	private double initialSystemLoad;
	private long lastProcessingFinishTime = 0;
	private long idleTime = 0;
	
	private int tabletServersBatchSum = 0;
	private int tabletBatchSum = 0;
	private int numBatches = 0;
	private int maxTabletBatch = Integer.MIN_VALUE;
	private int minTabletBatch = Integer.MAX_VALUE;
	private int minTabletServersBatch = Integer.MAX_VALUE;
	private int maxTabletServersBatch = Integer.MIN_VALUE;
	private Violations violations;
	private HashSet<KeyExtent> authorizationFailures;
    private Instance instance;
    private AuthInfo credentials;
	
	private static Logger log = Logger.getLogger(TabletServerBatchWriter.class.getName());
	
	public TabletServerBatchWriter(Instance instance, AuthInfo credentials, long maxMemory2, int numSendThreads) {
	    this.instance = instance;
		this.maxMemory = maxMemory2;
		this.credentials = credentials;
		mutations = new MutationSet();
		processedMutations = new MutationSet();
		isProcessing = false;
	
		sendThreadPool = Executors.newFixedThreadPool(numSendThreads);
		processThreadPool = Executors.newFixedThreadPool(1);
		
		violations = new Violations();
		
		authorizationFailures = new HashSet<KeyExtent>();
	}
	
	public synchronized void addMutation(String table, Mutation m) {
		
		startProcessingMutations(maxMemory);
		
		if(startTime == 0){
			startTime  = System.currentTimeMillis();
			lastProcessingFinishTime = startTime;
			
			List<GarbageCollectorMXBean> gcmBeans = ManagementFactory.getGarbageCollectorMXBeans();
			for (GarbageCollectorMXBean garbageCollectorMXBean : gcmBeans) {
				initialGCTimes += garbageCollectorMXBean.getCollectionTime();
			}
			
			CompilationMXBean compMxBean = ManagementFactory.getCompilationMXBean();
			if(compMxBean.isCompilationTimeMonitoringSupported()){
				initialCompileTimes = compMxBean.getTotalCompilationTime();
			}
			
			initialSystemLoad = ManagementFactory.getOperatingSystemMXBean().getSystemLoadAverage();
		}
		
		mutations.addMutation(table, m);
		totalMutations++;
		totalAdded++;
	}

	public synchronized void flush() {
		startProcessingMutations(0);
	}
	
	public synchronized void close() throws MutationsRejectedException {
		
		log.debug("close() called, "+String.format("%,d", totalMutations)+" mutations queued");
		
		while(getTotalMemoryUsed() > 0){
			startProcessingMutations(0);
			try {
				this.wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	
		long finishTime = System.currentTimeMillis();
				
		idleTime += (finishTime - lastProcessingFinishTime);
		
		long finalGCTimes = 0;
		List<GarbageCollectorMXBean> gcmBeans = ManagementFactory.getGarbageCollectorMXBeans();
		for (GarbageCollectorMXBean garbageCollectorMXBean : gcmBeans) {
			finalGCTimes += garbageCollectorMXBean.getCollectionTime();
		}
		
		CompilationMXBean compMxBean = ManagementFactory.getCompilationMXBean();
		long finalCompileTimes = 0;
		if(compMxBean.isCompilationTimeMonitoringSupported()){
			finalCompileTimes = compMxBean.getTotalCompilationTime();
		}
		
		double averageRate = totalSent / (totalSendTime / 1000.0);
		double overallRate = totalAdded / ((finishTime - startTime) / 1000.0);
		
		double finalSystemLoad = ManagementFactory.getOperatingSystemMXBean().getSystemLoadAverage();

		log.debug("");
		log.debug("TABLET SERVER BATCH WRITER STATISTICS");
		log.debug(String.format("Added                : %,10d mutations", totalAdded));
		log.debug(String.format("Sent                 : %,10d mutations", totalSent));
		log.debug(String.format("Failure percentage   : %10.2f%s", totalFailed / (double)totalAdded * 100.0, "%"));
		log.debug(String.format("Overall time         : %,10.2f secs", (finishTime - startTime) / 1000.0));
		log.debug(String.format("Overall send rate    : %,10.2f mutations/sec", overallRate));
		log.debug(String.format("Send efficiency      : %10.2f%s", overallRate / averageRate * 100.0,"%"));
		log.debug("");
		log.debug("BACKGROUND WRITER PROCESS STATISTICS");
		log.debug(String.format("Total send time      : %,10.2f secs %6.2f%s", totalSendTime/1000.0, 100.0 * totalSendTime/(finishTime - startTime), "%"));
		log.debug(String.format("Average send rate    : %,10.2f mutations/sec", averageRate));
		log.debug(String.format("Total bin time       : %,10.2f secs %6.2f%s", totalBinTime/1000.0, 100.0 * totalBinTime/(finishTime - startTime), "%"));
		log.debug(String.format("Average bin rate     : %,10.2f mutations/sec", totalBinned / (totalBinTime / 1000.0)));
		//log.debug(String.format("Metadata update time : %,10.2f secs %6.2f%s",(totalMetadataUpdateTime/1000.0), 100.0 * totalMetadataUpdateTime/(finishTime - startTime), "%"));
		//log.debug(String.format("Metadata update rate : %,10.2f entries/sec",totalMetadataUpdates / (totalMetadataUpdateTime/1000.0)));
		log.debug(String.format("Idle time            : %,10.2f secs %6.2f%s", idleTime/1000.0, 100.0 * idleTime/(finishTime - startTime), "%"));
		log.debug(String.format("tservers per batch   : %,8.2f avg  %,6d min %,6d max", (tabletServersBatchSum/(double)numBatches), minTabletServersBatch, maxTabletServersBatch));
		log.debug(String.format("tablets per batch    : %,8.2f avg  %,6d min %,6d max", (tabletBatchSum/(double)numBatches), minTabletBatch, maxTabletBatch));
		log.debug("");
		log.debug("SYSTEM STATISTICS");
		log.debug(String.format("JVM GC Time          : %,10.2f secs",((finalGCTimes - initialGCTimes)/1000.0)));
		if(compMxBean.isCompilationTimeMonitoringSupported()){
			log.debug(String.format("JVM Compile Time     : %,10.2f secs",((finalCompileTimes - initialCompileTimes)/1000.0)));
		}
		log.debug(String.format("System load average : initial=%6.2f final=%6.2f", initialSystemLoad, finalSystemLoad));
		
		sendThreadPool.shutdown();
		processThreadPool.shutdown();
		
		processingMutations = null;
		processedMutations = null;
		mutations = null;
		
		
		
		List<ConstraintViolationSummary> cvsList = violations.asList();
		violations = null;
		
        if(cvsList.size() > 0 || authorizationFailures.size() > 0){
        	throw new MutationsRejectedException(cvsList, new ArrayList<KeyExtent>(authorizationFailures));
        }
	}

	public synchronized void addMutation(String table, Iterator<Mutation> list) {
		while (list.hasNext()) {
			addMutation(table, list.next());
		}
	}
	
	public void enableDebugMessages(){
		ConsoleAppender ca = new ConsoleAppender();
		ca.setThreshold(Level.DEBUG);
		log.addAppender(ca);
		log.setLevel(Level.DEBUG);
	}
	
	private synchronized long getTotalMemoryUsed(){
		return processingMemory + mutations.getMemoryUsed();
	}
	
	private synchronized void startProcessingMutations(long maxMemory2) {
		
		if(getTotalMemoryUsed() < maxMemory2 && mutations.getMemoryUsed() < maxMemory2/2){
			return;
		}
		
		while(isProcessing){
			try {
				this.wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if(getTotalMemoryUsed() < maxMemory2 && mutations.getMemoryUsed() < maxMemory2/2){
				return;
			}
		}
		
		log.debug("Starting to process "+mutations.size()+" mutations, memory used = "+mutations.getMemoryUsed());
		
		isProcessing = true;
		
		processingMutations = mutations;
		mutations = processedMutations;
		mutations.clear();
		
		processingMemory = processingMutations.getMemoryUsed();
		
		Runnable mutTask = new Runnable(){
			public void run() {
				try{
					MutationSet failures = null;
					int numMuts = processingMutations.size();
					try {
						//TODO wait if had previous failures? probably best to put failed mutations aside and try them a few seconds later... but keep going if possible
						failures = processMutations(processingMutations);
					} catch (TableNotFoundException e) {
						//TODO exit?
						e.printStackTrace();
					}
					
					synchronized (TabletServerBatchWriter.this) {
						processingMutations.clear();
						processedMutations = processingMutations;
						processingMutations = null;
						isProcessing = false;
						
						mutations.addAll(failures);
						totalMutations -= numMuts;
						totalMutations += failures.size();
						
						processingMemory = 0;
						
						TabletServerBatchWriter.this.notifyAll();
					}
				}catch(Throwable t){
					log.error("Failed while processing batch : "+t.getMessage(), t);
					t.printStackTrace();
				}
			}
		};
		
		processThreadPool.submit(mutTask);
	}

	private synchronized void updatedConstraintViolations(List<ConstraintViolationSummary> cvsList){
		violations.add(cvsList);
	}
	
	private synchronized void updateAuthorizationFailures(List<KeyExtent> authorizationFailures) {
		this.authorizationFailures.addAll(authorizationFailures);
	}
	
	private MutationSet sendMutationsToTabletServer(String location, Map<KeyExtent, List<Mutation>> tabMuts) throws IOException, CBSecurityException {
		if(tabMuts.size() == 0){
			return new MutationSet();
		}


		TTransport transport = null; 
			
		try {

			transport = ThriftTansportPool.getInstance().getTransport(location, CBConfiguration.getInstance().getInt("cloudbase.tabletserver.clientPort", CBConstants.TABLET_CLIENT_PORT_DEFAULT));
			TProtocol protocol = new TBinaryProtocol(transport);
			TabletClientService.Client client = new TabletClientService.Client(protocol);
			
			MutationSet allFailures = new MutationSet();

			long usid = client.startUpdate(credentials);

			for(Entry<KeyExtent, List<Mutation>> entry :  tabMuts.entrySet()){
				client.setUpdateTablet(usid, entry.getKey());
				for(Mutation mutation : entry.getValue()){
					client.applyUpdate(usid, mutation);
				}
			}

			UpdateErrors updateErrors = client.closeUpdate(usid);
			Map<KeyExtent, Long> failures =  updateErrors.failedExtents;
			updatedConstraintViolations(updateErrors.violationSummaries);
			updateAuthorizationFailures(updateErrors.authorizationFailures);

			for (Entry<KeyExtent, Long> entry: failures.entrySet()) {
				KeyExtent failedExtent = entry.getKey();
				int numCommited = (int)(long)entry.getValue();

				String table = failedExtent.getTableName().toString();
				
				TabletLocator.getInstance(instance, credentials, new Text(table)).invalidateCache(failedExtent);
				
				ArrayList<Mutation> mutations = (ArrayList<Mutation>) tabMuts.get(failedExtent);
				allFailures.addAll(table, mutations.subList(numCommited, mutations.size()));
			}

			return allFailures;
		} catch (TTransportException e) {
			e.printStackTrace();
			throw new IOException(e);
		} catch (ThriftSecurityException e) {
			e.printStackTrace();
			throw new CBSecurityException(e.user, e.baduserpass, e);
		} catch (TException e) {
			e.printStackTrace();
			throw new IOException(e);
		} catch (NoSuchScanIDException e) {
			e.printStackTrace();
			throw new IOException(e);
		}finally{
			ThriftTansportPool.getInstance().returnTransport(transport);
		}
	}

	private MutationSet processMutations(final MutationSet mutationsToProcess)
	throws CBException, CBSecurityException, TableNotFoundException
	{
		Map<String, TabletServerMutations> binnedMutations = new HashMap<String, TabletServerMutations>();
		
		long t1,t2;

		t1 = System.currentTimeMillis();
		idleTime += (t1 - lastProcessingFinishTime);
				
		final MutationSet allFailures = new MutationSet();
		
		t1 = System.currentTimeMillis();
		binMutations(mutationsToProcess, binnedMutations, allFailures);
		t2 = System.currentTimeMillis();
		
		log.debug("sending "+String.format("%,d", mutationsToProcess.size())+" mutations to "+binnedMutations.size()+" tablet servers");
		
		//collect some stats
		updateBatchStats(mutationsToProcess, binnedMutations);
		
		//get copy of tables before clearing mutationsToProcess
		final List<String> tables = new ArrayList<String>(mutationsToProcess.getMutations().keySet());
		
		//clear mutationsToProcess so muts can be GCed after threads process them
		int numMutations = mutationsToProcess.size();
		mutationsToProcess.clear();
		
		int numTabletServers = binnedMutations.size();
		
		totalBinned += numMutations;
		totalBinTime += (t2 - t1);
		
		//System.out.printf("DEBUG : Binned %d mutations in %6.2f seconds (%,6.2f mutations/second) into %d tserver bins.\n", numMutations, (t2 - t1)/1000.0, numMutations/((t2 - t1)/1000.0), binnedMutations.size());
		
		
		t1 = System.currentTimeMillis();
		
		//semaphore used to determine when all threads are complete
		final Semaphore semaphore = new Semaphore(numTabletServers);
		
		//initially acquire all permits... permits will be released as data
		//is sent for each tablet server
		semaphore.acquireUninterruptibly(numTabletServers);
		
		//randomize order in which tablet servers are placed on thread pool queue
		ArrayList<String> tabServers = new ArrayList<String>(binnedMutations.keySet());
		Collections.shuffle(tabServers);
		
		//synchronize access to binnedMutations so that multiple threads can access it
		final Map<String, TabletServerMutations> syncBinnedMutations = Collections.synchronizedMap(binnedMutations);
		
		for (final String location : tabServers) {
			Runnable sendTask = new Runnable(){
				public void run() {
					
					MutationSet failures = null;
					
					try {
						TabletServerMutations tsm = syncBinnedMutations.get(location);
						
						Map<KeyExtent, List<Mutation>> mutationBatch = tsm.getMutations();
						try{
							
							long count = 0;
							for (List<Mutation> list : mutationBatch.values()) {
								count += list.size();
							}
							log.debug("sending "+String.format("%,d", count)+" mutations to "+String.format("%,d", mutationBatch.size())+" tablets at "+location+" ");
							
							long st1 = System.currentTimeMillis();
							failures = sendMutationsToTabletServer(location, mutationBatch);
							long st2 = System.currentTimeMillis();

							log.debug("sent "+String.format("%,d", count)+" mutations to "+location+" in "+String.format("%.2f secs (%,.2f mutations/sec) with %,d failures", (st2 - st1)/1000.0, count/((st2 - st1)/1000.0), failures.size()));
							
							if(failures.size() > 0){
								synchronized (allFailures) {
									//add mutations back into queue
									allFailures.addAll(failures);
								}
							}
						} catch (IOException e) {
							log.debug("failed to send mutations to "+location+" : "+e.getMessage());
							synchronized (allFailures)
							{
								for(Entry<KeyExtent, List<Mutation>> entry : mutationBatch.entrySet())
									allFailures.addAll(entry.getKey().getTableName().toString(), entry.getValue());
							}
							e.printStackTrace();

							for (String table : tables)
								TabletLocator.getInstance(instance, credentials, new Text(table)).invalidateCache(location);
							
						} catch (CBSecurityException e) {
							// TODO exit?
							e.printStackTrace();
						}	
						
						//allow mutations to be garbage collected after being processed
						syncBinnedMutations.remove(location);
						
					} catch (Throwable t) {
						log.error("Failed to send tablet server "+location+" its batch : "+t.getMessage(), t);
						t.printStackTrace();
					} finally {
						semaphore.release();
					}
				}
			};
			
			sendThreadPool.execute(sendTask);
		}
		
		//wait for threads to finish sending data to all tablet servers
		semaphore.acquireUninterruptibly(numTabletServers);
		
		t2 = System.currentTimeMillis();
		
		totalSent += numMutations;
		totalSendTime += (t2 - t1);
		totalFailed += allFailures.size();
		
		lastProcessingFinishTime = t2;
			
		return allFailures;
	}

	private void updateBatchStats(MutationSet mutationsToProcess, Map<String, TabletServerMutations> binnedMutations)
	{
		tabletServersBatchSum += binnedMutations.size();
		
		minTabletServersBatch = Math.min(minTabletServersBatch, binnedMutations.size());
		maxTabletServersBatch = Math.max(maxTabletServersBatch, binnedMutations.size());
		
		int numTablets = 0;
		
		for(Entry<String, TabletServerMutations> entry : binnedMutations.entrySet())
		{
			TabletServerMutations tsm = entry.getValue();
			numTablets += tsm.getMutations().size();
		}
		
		tabletBatchSum += numTablets;
		
		minTabletBatch = Math.min(minTabletBatch, numTablets);
		maxTabletBatch = Math.max(maxTabletBatch, numTablets);
		
		numBatches++;
	}

	
	private void binMutations(MutationSet mutationsToProcess, Map<String, TabletServerMutations> binnedMutations, MutationSet failures)
	throws CBException, CBSecurityException, TableNotFoundException 
	{
		
		Set<Entry<String, List<Mutation>>> es = mutationsToProcess.getMutations().entrySet();
		for (Entry<String, List<Mutation>> entry : es) {
			TabletLocator locator = TabletLocator.getInstance(instance, credentials, new Text(entry.getKey()));
			
			ArrayList<Mutation> tableFailures = new ArrayList<Mutation>();
			
			locator.binMutations(entry.getValue(), binnedMutations, tableFailures);
			
			failures.addAll(entry.getKey(), tableFailures);
		}
	}
	
	/* 
	   
	   I wrote the code below thinking it would be faster, and it turned out
	   not to be.   The sort took too long.  After the mutations were sorted
	   binning was blazing fast.
	   
	   It may be that this method will be faster when the number of metadata
	   entries is large and the lookups in the TreeMap take a long time.  May
	   want to automatically switch to use this code when the # of metadata entries
	   are large.
	 
	private void binMutations(ArrayList<Mutation> mutationsToProcess, KeyExtent lookupKey, 
			Map<String, Map<KeyExtent, List<Mutation>>> binnedMutations) 
	{
		
		long t1 = System.currentTimeMillis();
		Collections.sort(mutationsToProcess, new Comparator<Mutation>(){
			public int compare(Mutation o1, Mutation o2) {
				return o1.getRow().compareTo(o2.getRow());
			}}
		);
		
		long t2 = System.currentTimeMillis();
		
		Iterator<CacheEntry> iter = metaCache.values().iterator();
		CacheEntry currentCacheEntry = iter.next();
		ArrayList<Mutation> currentMutList = new ArrayList<Mutation>();
		
		for (Mutation mutation : mutationsToProcess) {
			//binMutation(lookupKey, mutation, binnedMutations);
			
			if(!currentCacheEntry.ke.contains(mutation.getRow())){
				if(currentMutList.size() > 0){
					Map<KeyExtent, List<Mutation>> mutsPerExtent = binnedMutations.get(currentCacheEntry.location);
					if(mutsPerExtent == null){
						mutsPerExtent = new TreeMap<KeyExtent, List<Mutation>>();
						binnedMutations.put(currentCacheEntry.location, mutsPerExtent);
					}
					
					mutsPerExtent.put(currentCacheEntry.ke, currentMutList);
					
					currentMutList = new ArrayList<Mutation>();	
				}
				
				do{
					currentCacheEntry = iter.next();
				}while(!currentCacheEntry.ke.contains(mutation.getRow()));
			}
			
			currentMutList.add(mutation);
		}
		
		if(currentMutList.size() > 0){
			Map<KeyExtent, List<Mutation>> mutsPerExtent = binnedMutations.get(currentCacheEntry.location);
			if(mutsPerExtent == null){
				mutsPerExtent = new TreeMap<KeyExtent, List<Mutation>>();
				binnedMutations.put(currentCacheEntry.location, mutsPerExtent);
			}
			
			mutsPerExtent.put(currentCacheEntry.ke, currentMutList);
		}
		
		long t3 = System.currentTimeMillis();
		System.out.printf("DEBUG : Sort time %6.2f  Bin time %6.2f\n", (t2 - t1)/1000.0, (t3 - t2)/1000.0);
	}*/
}
