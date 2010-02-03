package cloudbase.core.client.impl;


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
import java.util.Set;
import java.util.Map.Entry;

import org.apache.hadoop.io.Text;

import cloudbase.core.CBConstants;
import cloudbase.core.client.CBException;
import cloudbase.core.client.CBSecurityException;
import cloudbase.core.client.Connector;
import cloudbase.core.client.Instance;
import cloudbase.core.client.Scanner;
import cloudbase.core.client.TableNotFoundException;
import cloudbase.core.data.Key;
import cloudbase.core.data.Range;
import cloudbase.core.data.Value;
import cloudbase.core.security.thrift.AuthInfo;

public class ScannerImpl extends ScannerOptions implements Scanner
{
	
	// keep a list of columns over which to scan
	// keep track of the last thing read
	// hopefully, we can track all the state in the scanner on the client
	// and just query for the next highest row from the tablet server
	
    private Instance instance;
    private AuthInfo credentials;
    private Set<Short> authorizations;
    private Text table;

	private int size;
	private int timeOut;

	private Range range;
	
	public ScannerImpl(Instance instance, AuthInfo credentials, String table, Set<Short> authorizations)
	throws CBException, CBSecurityException, TableNotFoundException
	{
	    this.instance = instance;
	    this.credentials = credentials;
		this.table = new Text(table);
   		this.range = new Range((Key)null, (Key)null);
   		this.authorizations = authorizations;
   		

   		this.size = CBConstants.SCAN_BATCH_SIZE;
   		this.timeOut = Integer.MAX_VALUE;

   		Connector conn = new Connector(instance, credentials.user, credentials.password);
   		if (!conn.tableOperations().exists(table))
   			throw new TableNotFoundException(table);
	}
	
	/**
	 * When failure occurs, the scanner automatically retries.  This
	 * setting determines how long a scanner will retry.  By default
	 * a scanner will retry forever. 
	 * 
	 * @param timeOut in milliseconds
	 */
	public void setTimeOut(int timeOut)
	{
		this.timeOut = timeOut;
	}
	
	public int getTimeOut(){
		return timeOut;
	}
	
	public synchronized void setRange(Range range)
	{
		this.range = range;
	}
   	
   	public void setBatchSize(int size)
   	{
   		if (size > 0)
   			this.size = size;
   		else
   			throw new IllegalArgumentException("size must be greater than zero");
   	}
   		
	/**
	 * Returns an iterator over a cloudbase table.  This iterator uses the options
	 * that are currently set on the scanner for its lifetime.  So setting options 
	 * on a Scanner object will have no effect on existing iterators.
	 */
	public Iterator<Entry<Key, Value>> iterator()
	{
		return new ScannerIterator(instance, credentials, table, authorizations, range, size, timeOut, this);
	}
}
