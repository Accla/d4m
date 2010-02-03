package cloudbase.core.client.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cloudbase.core.client.BatchScanner;
import cloudbase.core.client.CBException;
import cloudbase.core.client.CBSecurityException;
import cloudbase.core.client.Connector;
import cloudbase.core.client.Instance;
import cloudbase.core.client.TableNotFoundException;
import cloudbase.core.data.Key;
import cloudbase.core.data.Range;
import cloudbase.core.data.Value;
import cloudbase.core.security.thrift.AuthInfo;

public class TabletServerBatchReader extends ScannerOptions implements BatchScanner {

	private String table;
	private int numThreads;
	private ExecutorService queryThreadPool;
	
    private Instance instance;
    private ArrayList<Range> ranges;
    
    private AuthInfo credentials;
	
	public TabletServerBatchReader(Instance instance, AuthInfo credentials, String table, Set<Short> authorizations, int numQueryThreads)
	throws TableNotFoundException, CBException, CBSecurityException
	{
		if (!new Connector(instance, credentials.user, credentials.password).tableOperations().exists(table))
		    throw new TableNotFoundException(table);
		
		this.instance = instance;
		this.credentials = credentials;
		this.table = table;
		this.numThreads = numQueryThreads;
		queryThreadPool = Executors.newFixedThreadPool(numThreads);
	}

	public void close()
	{
		queryThreadPool.shutdown();
	}

    @Override
    public void setRanges(Collection<Range> ranges)
    {
        this.ranges = new ArrayList<Range>(ranges);
        
    }

    @Override
    public Iterator<Entry<Key, Value>> iterator()
    {
        return new TabletServerBatchReaderIterator(instance, credentials, table, ranges, numThreads, queryThreadPool, new ScannerOptions(this));
    }
}
