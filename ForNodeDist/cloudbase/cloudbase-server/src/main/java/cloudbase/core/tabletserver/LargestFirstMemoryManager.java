package cloudbase.core.tabletserver;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import cloudbase.core.conf.CBConfiguration;
import cloudbase.core.data.KeyExtent;

public class LargestFirstMemoryManager implements MemoryManager {
		
	private static Logger log = Logger.getLogger(LargestFirstMemoryManager.class.getName());
	
	private long maxMemory;
	private int maxConcurrentMincs;
	private int numWaitingMultiplier;
	private long prevIngestMemory;
	private double compactionThreshold;
	private long maxObserved;
	
	LargestFirstMemoryManager(long maxMemory, int maxConcurrentMincs, int numWaitingMultiplier){
		this.maxMemory = maxMemory;
		this.maxConcurrentMincs = maxConcurrentMincs;
		this.numWaitingMultiplier = numWaitingMultiplier;
		prevIngestMemory = 0;
		compactionThreshold = 0.5;
		maxObserved = 0;
	}
	
	LargestFirstMemoryManager(){
		this(CBConfiguration.getInstance().getLong("cloudbase.tabletserver.maxMapMemory", 1073741824),
				CBConfiguration.getInstance().getInt("cloudbase.tabletserver.minorCompaction.maxConcurrent", 4),
				CBConfiguration.getInstance().getInt("cloudbase.tabletserver.minorCompaction.maxConcurrent.numWaitingMultiplier", 2));
	}

	
	@Override
	public MemoryManagementActions getMemoryManagementActions(List<TabletState> tablets) {
		long ingestMemory = 0;
		long compactionMemory = 0;
		KeyExtent largestMemTablet = null;
		long largestMemTableLoad = 0;
		long mts;
		long mcmts;
		int numWaitingMincs = 0;
		long idleTime;
		long tml;
		long ct = System.currentTimeMillis();

		long chosenIdleTime = -1, chosenMem = -1;
		
		for (TabletState ts : tablets) {
			mts = ts.getMemTableSize();
			mcmts = ts.getMinorCompactingMemTableSize();
			idleTime = ct - ts.getLastCommitTime();
			ingestMemory += mts;
			tml = timeMemoryLoad(mts, idleTime);
			if (mcmts==0 && mts > 0 && tml > largestMemTableLoad) {
				largestMemTableLoad = tml;
				largestMemTablet = ts.getExtent();
				
				chosenIdleTime = idleTime;
				chosenMem = mts; 
			}
			compactionMemory += mcmts;
			if (mcmts>0)
				numWaitingMincs++;
		}
		
		if (ingestMemory+compactionMemory > maxObserved) {
			maxObserved = ingestMemory+compactionMemory;
		}
		
		long memoryChange = ingestMemory-prevIngestMemory;
		prevIngestMemory = ingestMemory;
		
		MemoryManagementActions mma = new MemoryManagementActions();
		mma.tabletsToMinorCompact = new ArrayList<KeyExtent>();
				
		boolean startMinC = false;
		
		if (numWaitingMincs < maxConcurrentMincs*numWaitingMultiplier && memoryChange >= 0 && ingestMemory+memoryChange > compactionThreshold*maxMemory) {
			// based on previous ingest memory increase, if we think that the next increase will 
			// take us over the threshold for non-compacting memory, then start a minor compaction
			startMinC = true;
		}
		
		if (startMinC && largestMemTablet != null) {
			mma.tabletsToMinorCompact.add(largestMemTablet);
			log.debug(String.format("COMPACTING %s  total = %,d ingestMemory = %,d", largestMemTablet.toString(), (ingestMemory + compactionMemory), ingestMemory));
			log.debug(String.format("chosenMem = %,d chosenIT = %.2f load %,d", chosenMem, chosenIdleTime/1000.0, timeMemoryLoad(chosenMem, chosenIdleTime)));
		}		
		else if (memoryChange < 0) {
			// right now, starting a minor compaction means that memoryChange >= 0.
			// if that changes, we may want to remove the "else".
			
			// memory change < 0 means a minor compaction occurred
			// we want to see how full the memory got during the compaction
			// (the goal is for it to have between 80% and 90% memory utilization)
			// and adjust the compactionThreshold accordingly
			
			log.debug(String.format("BEFORE compactionThreshold = %.3f maxObserved = %,d",compactionThreshold, maxObserved));
			
			if (compactionThreshold < 0.82 && maxObserved < 0.8*maxMemory) { 
				// 0.82 * 1.1 is about 0.9, which is our desired max threshold
				compactionThreshold *= 1.1;
			}
			else if (compactionThreshold > 0.056 && maxObserved > 0.9*maxMemory) {
				// 0.056 * 0.9 is about 0.05, which is our desired min threshold
				compactionThreshold *= 0.9;
			}
			maxObserved = 0;
			
			log.debug(String.format("AFTER compactionThreshold = %.3f",compactionThreshold));
		}

		return mma;
	}
		
	@Override
	public void tabletClosed(KeyExtent extent) {
	}
	
	static long timeMemoryLoad(long mem, long time) {
		double minutesIdle = time / 60000.0;
		
		return (long)(mem * Math.pow(2, minutesIdle / 15.0)); 
	}
	
	public static void main(String[] args) {
		for(int i = 0; i < 62; i++){
			System.out.printf("%d\t%d\n", i, timeMemoryLoad(1, i * 60000));
		}
	}
}
