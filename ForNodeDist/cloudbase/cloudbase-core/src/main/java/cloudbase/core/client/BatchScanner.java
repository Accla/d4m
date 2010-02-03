package cloudbase.core.client;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map.Entry;

import cloudbase.core.data.Key;
import cloudbase.core.data.Range;
import cloudbase.core.data.Value;

/**
 * Implementations of BatchScanner support efficient lookups 
 * of many ranges in cloudbase.  
 * 
 */

public interface BatchScanner extends ScannerBase, Iterable<Entry<Key, Value>> {
	
	void setRanges(Collection<Range> ranges);
	
	/**
     * Returns an iterator over a cloudbase table.  This iterator uses the options
     * that are currently set for its lifetime.  So setting options will have no effect 
     * on existing iterators.
     */
    public Iterator<Entry<Key, Value>> iterator();
	
	void close();
}
