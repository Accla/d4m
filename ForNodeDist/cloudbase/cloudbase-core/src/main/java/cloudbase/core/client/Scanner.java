package cloudbase.core.client;


/**
 * provides scanner functionality
 * 
 * "Clients can iterate over multiple column families, and there are several 
 * mechanisms for limiting the rows, columns, and timestamps traversed by a 
 * scan. For example, we could restrict [a] scan ... to only produce anchors 
 * whose columns match [a] regular expression ..., or to only produce 
 * anchors whose timestamps fall within ten days of the current time."
 * 
 */


import java.util.Iterator;
import java.util.Map.Entry;

import cloudbase.core.data.Key;
import cloudbase.core.data.Range;
import cloudbase.core.data.Value;

/**
 * Walk a table over a given range.
 * 
 */
public interface Scanner extends ScannerBase, Iterable<Entry<Key, Value>> {
		
	/**
	 * When failure occurs, the scanner automatically retries.  This
	 * setting determines how long a scanner will retry.  By default
	 * a scanner will retry forever. 
	 * 
	 * @param timeOut in milliseconds
	 */
	public void setTimeOut(int timeOut);
	
	public int getTimeOut();
	
	public void setRange(Range range);
   	
	/**
	 * @param size the number of Keys/Value pairs to fetch per call to Cloudbase
	 */
   	public void setBatchSize(int size);
	
	/**
	 * Returns an iterator over a cloudbase table.  This iterator uses the options
	 * that are currently set on the scanner for its lifetime.  So setting options 
	 * on a Scanner object will have no effect on existing iterators.
	 */
	public Iterator<Entry<Key, Value>> iterator();
}
