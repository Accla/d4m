package cloudbase.core.client.impl;

import java.util.Iterator;
import java.util.Set;
import java.util.SortedMap;
import java.util.Map.Entry;

import org.apache.hadoop.io.Text;
import org.apache.log4j.Logger;

import cloudbase.core.client.CBException;
import cloudbase.core.client.CBSecurityException;
import cloudbase.core.client.Instance;
import cloudbase.core.client.TableNotFoundException;
import cloudbase.core.client.impl.ThriftScanner.ScanState;
import cloudbase.core.client.impl.ThriftScanner.ScanTimedOutException;
import cloudbase.core.data.Key;
import cloudbase.core.data.Range;
import cloudbase.core.data.Value;
import cloudbase.core.security.thrift.AuthInfo;

public class ScannerIterator implements Iterator<Entry<Key, Value>> {
	
	private static Logger log = Logger.getLogger(ScannerIterator.class.getName());
	
	//scanner options
	private Range range;
	private Text tableName;
	private int size;
	private int timeOut;
	
	//scanner state
	private SortedMap<Key, Value> currentBatch;
	private Iterator<Entry<Key, Value>> iter;
	private boolean endReached = false;
	private ScanState scanState;
	private AuthInfo credentials;
	private Set<Short> authorizations;

    private Instance instance;

    private ScannerOptions options;
	
	public ScannerIterator(Instance instance, AuthInfo credentials, Text table, Set<Short> authorizations, Range range, int size, int timeOut, ScannerOptions options){
		this.instance = instance;
	    this.tableName = new Text(table);
	    this.range = new Range(range);
		this.size = size;
		this.timeOut = timeOut;
		this.credentials = credentials;
		this.authorizations = authorizations;
	
		this.options = new ScannerOptions(options);
	}
	
	public boolean hasNext() {
		if(endReached == true)
			return false;
		
		if(iter != null && iter.hasNext()) { // && !spent) {
			return true;
		}
		
		else { //  this is done in order to find see if there is another batch to get
			// TODO: check to make sure progress is made in this loop
			while(iter == null || iter.hasNext() == false)
			{
				try {
					
					if (scanState == null)
						scanState = new ScanState(credentials, tableName, authorizations, range, options.fetchedColumns, size, options.serverSideIteratorList, options.serverSideIteratorOptions);
					
					currentBatch = ThriftScanner.scan(instance, credentials, scanState, timeOut);
					if(currentBatch == null) return false;
					iter = currentBatch.entrySet().iterator();
					continue;
				} catch (ScanTimedOutException e) {
					throw new RuntimeException(e);
				} catch (CBException e) {
					throw new RuntimeException(e);
				} catch (CBSecurityException e) {
					log.warn("Authentication failure. Please check your username and password and try again.");
					endReached = true;
					return false;
				} catch (TableNotFoundException e) {
					log.warn(e.getMessage());
					endReached = true;
					return false;
				}
			}
			return true;
		}	
	}

	public Entry<Key, Value> next()
	{
		return iter.hasNext() ? iter.next() : null;
	}
	
	// just here to satisfy the interface
	// could make this actually delete things from the database
	public void remove()
	{
		throw new UnsupportedOperationException("remove is not supported in Scanner");
	}
	
}
