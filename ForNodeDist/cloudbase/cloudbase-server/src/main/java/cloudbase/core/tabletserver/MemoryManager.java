package cloudbase.core.tabletserver;

import java.util.List;

import cloudbase.core.data.KeyExtent;
/**
 * A MemoryManager in cloudbase currently determines when minor compactions
 * should occur and when ingest should be put on hold.  The goal of a memory
 * manager implementation is to maximize ingest throughput and minimize the 
 * number of minor compactions.  
 * 
 *
 */

public interface MemoryManager {
	/**
	 * An implementation of this function will be called periodically
	 * by cloudbase and should return a list of tablets to minor compact.
	 * 
	 * Instructing a tablet that is already minor compacting (this can be 
	 * inferred from the TabletState) to minor compact has no effect.
	 * TODO hold commits for a minor compacting tablet that was asked to 
	 * minor compact?
	 * 
	 * Holding all ingest does not affect !METADATA tablets.
	 * 
	 * @param tablets
	 */
	
	MemoryManagementActions getMemoryManagementActions(List<TabletState> tablets);

	/**
	 * This method is called when a tablet is closed. A memory manger
	 * can clean up any per tablet state it is keeping when this is
	 * called.
	 * 
	 * @param extent
	 */
	void tabletClosed(KeyExtent extent);
}
