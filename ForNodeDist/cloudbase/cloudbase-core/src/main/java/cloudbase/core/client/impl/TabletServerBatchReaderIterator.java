package cloudbase.core.client.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Map.Entry;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Semaphore;

import org.apache.hadoop.io.Text;
import org.apache.log4j.Logger;

import com.facebook.thrift.TException;
import com.facebook.thrift.protocol.TBinaryProtocol;
import com.facebook.thrift.protocol.TProtocol;
import com.facebook.thrift.transport.TTransport;
import com.facebook.thrift.transport.TTransportException;

import cloudbase.core.CBConstants;
import cloudbase.core.client.CBException;
import cloudbase.core.client.CBSecurityException;
import cloudbase.core.client.Instance;
import cloudbase.core.client.TableNotFoundException;
import cloudbase.core.client.impl.TabletLocator.TabletLocation;
import cloudbase.core.conf.CBConfiguration;
import cloudbase.core.security.thrift.AuthInfo;
import cloudbase.core.security.thrift.ThriftSecurityException;
import cloudbase.core.data.Column;
import cloudbase.core.data.Key;
import cloudbase.core.data.InitialScan;
import cloudbase.core.data.KeyExtent;
import cloudbase.core.data.KeyValue;
import cloudbase.core.data.Range;
import cloudbase.core.data.ScanResult;
import cloudbase.core.data.Value;
import cloudbase.core.tabletserver.thrift.NoSuchScanIDException;
import cloudbase.core.tabletserver.thrift.TabletClientService;

public class TabletServerBatchReaderIterator implements Iterator<Entry<Key, Value>>{
    
	private static Logger log = Logger.getLogger(TabletServerBatchReaderIterator.class.getName());
	
    private String table;
    private ExecutorService queryThreadPool;
    private int numThreads;
    
    private Instance instance;
    
    private ScannerOptions options;
    
    private ArrayBlockingQueue<Entry<Key, Value>> resultsQueue = new ArrayBlockingQueue<Entry<Key,Value>>(1000);
    private Entry<Key, Value> next;

	private AuthInfo credentials;
    
    public interface ResultReceiver {
        void receive(Key key, Value value);
    }
    
    private static class MyEntry implements Entry<Key, Value>{

        private Key key;
        private Value value;

        MyEntry(Key key, Value value){
            this.key = key;
            this.value = value;
        }
        
        @Override
        public Key getKey() {
            return key;
        }

        @Override
        public Value getValue() {
            return value;
        }

        @Override
        public Value setValue(Value value) {
            throw new UnsupportedOperationException();
        }
        
    }
    
    public TabletServerBatchReaderIterator(Instance instance, AuthInfo credentials,
    		String table, ArrayList<Range> ranges, int numThreads,
            ExecutorService queryThreadPool, ScannerOptions scannerOptions) {
        
        this.instance = instance;
        this.credentials = credentials;
        this.table = table;
        this.numThreads = numThreads;
        this.queryThreadPool = queryThreadPool;
        this.options = new ScannerOptions(scannerOptions);
        
        ResultReceiver rr = new ResultReceiver(){

            @Override
            public void receive(Key key, Value value) {
                try {
					resultsQueue.put(new MyEntry(key, value));
				} catch (InterruptedException e) {
					log.error("Failed to add Batch Scan result for key "+key, e);
				}
            }
            
        };
        
        try{
            lookup(ranges, rr);
        }catch(Exception e){
        	throw new RuntimeException("Failed to create iterator", e);
        }
    }

    @Override
    public boolean hasNext() {
        if(next != null && next.getKey() == null && next.getValue() == null){
            return false;
        }
        
        try {
            next = resultsQueue.take();
            return next.getKey() != null && next.getValue() != null;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Entry<Key, Value> next() {
        if(next == null || (next.getKey() == null && next.getValue() == null)){
            throw new IllegalStateException("Called next() when there is not a next");
        }
        
        return next;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
    
    public synchronized void lookup(List<Range> ranges, ResultReceiver receiver) throws CBException, CBSecurityException, TableNotFoundException {
        List<Column> columns = new ArrayList<Column>(options.fetchedColumns);
        ranges = Range.mergeOverlapping(ranges);
        
        Map<String, Map<KeyExtent, List<Range>>> binnedRanges = 
            new HashMap<String, Map<KeyExtent,List<Range>>>();
        
        TabletLocator.getInstance(instance, credentials, new Text(table)).binRanges(ranges, binnedRanges);
        
        doLookups(binnedRanges, receiver, columns, options.authorizations);
    }

    private void processFailures(Map<KeyExtent, List<Range>> failures, ResultReceiver receiver, List<Column> columns)
    throws CBException, CBSecurityException, TableNotFoundException
    {
            for (KeyExtent failedExtent : failures.keySet())
                TabletLocator.getInstance(instance, credentials, new Text(table)).invalidateCache(failedExtent);
            
            Map<String, Map<KeyExtent, List<Range>>> binnedRanges = 
                new HashMap<String, Map<KeyExtent,List<Range>>>();
            
            SortedMap<KeyExtent, TabletLocation> metaCache = TabletLocator.getInstance(instance, credentials, new Text(table)).getTablets();
            SortedSet<KeyExtent> tablets = new TreeSet<KeyExtent>(metaCache.keySet());
            for (Entry<KeyExtent, List<Range>> entry : failures.entrySet())
            {
                SortedSet<KeyExtent> children = KeyExtent.findChildren(entry.getKey(), tablets);
                TreeMap<KeyExtent, TabletLocation> metadataSubSet = new TreeMap<KeyExtent, TabletLocation>();
                for (KeyExtent keyExtent : children)
                    metadataSubSet.put(keyExtent, metaCache.get(keyExtent));
                
                // a range can cover multiple tablets... 
                //when failure occurs only want to reprocess
                //tablets that failed, not all tablets a range
                //covers... that is why only a subset of the
                //metadata table is used for binning
                
                TabletLocator.binRanges(new Text(table), entry.getValue(), binnedRanges, metadataSubSet);
            }
            
            doLookups(binnedRanges, receiver, columns, options.authorizations);
    }
    
    private class QueryTask implements Runnable
    {
        private String tsLocation;
        private Map<KeyExtent, List<Range>> tabletsRanges;
        private ResultReceiver receiver;
        private byte[] auths;
        private Semaphore semaphore;
        private Map<KeyExtent, List<Range>> failures;
        private List<Column> columns;
        private int semaphoreSize;

        QueryTask(String tsLocation, Map<KeyExtent, List<Range>> tabletsRanges, Map<KeyExtent, List<Range>> failures, ResultReceiver receiver, List<Column> columns, byte auths[])
        {
            this.tsLocation = tsLocation;
            this.tabletsRanges = tabletsRanges;
            this.receiver = receiver;
            this.columns = columns;
            this.auths = auths;
            this.failures = failures;
        }
        
        void setSemaphore(Semaphore semaphore, int semaphoreSize)
        {
            this.semaphore = semaphore;
            this.semaphoreSize = semaphoreSize;
        }
        
        public void run()
        {
            try {
                Map<KeyExtent, List<Range>> tsFailures = doLookup(tsLocation, tabletsRanges, receiver, columns, auths);
                if(tsFailures.size() > 0)
                    synchronized (failures) { failures.putAll(tsFailures); }
                
            } catch(IOException e) {
                synchronized (failures) { failures.putAll(tabletsRanges); }
                
                TabletLocator.getInstance(instance, credentials, new Text(table)).invalidateCache(tsLocation);
                
            } catch (CBSecurityException e) {
                throw new RuntimeException(e);
            }catch (Throwable t){
                System.err.println("Uncaught Exception : "+t.getMessage());
                t.printStackTrace(System.err);
            } finally {
                semaphore.release();
                
                if(semaphore.tryAcquire(semaphoreSize)){
                    //finished processing all queries
                    if(failures.size() > 0){
                        System.out.println("processing failures");
                        //there were some failures
                        try {
                            processFailures(failures, receiver, columns);
                        } catch (TableNotFoundException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        } catch (CBException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (CBSecurityException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
                    }else{
                        //we are finished with this batch query
                        resultsQueue.add(new MyEntry(null, null));
                    }
                }
                //System.out.println("DEBUG : released semaphore");
            }
        }
        
    }
    
    private void doLookups(Map<String, Map<KeyExtent, List<Range>>> binnedRanges, final ResultReceiver receiver, List<Column> columns, byte authorizations[]) { 
        //when there are lots of threads and a few tablet servers
        //it is good to break request to tablet servers up, the 
        //following code determines if this is the case
        int maxTabletsPerRequest = Integer.MAX_VALUE;
        if(numThreads / binnedRanges.size() > 1){
            int totalNumberOfTablets = 0;
            for (Entry<String, Map<KeyExtent, List<Range>>> entry : binnedRanges.entrySet()) {
                totalNumberOfTablets += entry.getValue().size();
            }
            
            maxTabletsPerRequest = totalNumberOfTablets / numThreads;
            if(maxTabletsPerRequest == 0){
                maxTabletsPerRequest = 1;
            }
            
            //System.out.println("DEBUG : maxTabletsPerRequest = "+maxTabletsPerRequest);
        }
        
        Map<KeyExtent, List<Range>> failures = new HashMap<KeyExtent, List<Range>>();
        
        //randomize tabletserver order... this will help when there are multiple
        //batch readers and writers running against cloudbase
        List<String> locations = new ArrayList<String>(binnedRanges.keySet());
        Collections.shuffle(locations);
        
        List<QueryTask> queryTasks = new ArrayList<QueryTask>();
        
        for(final String tsLocation : locations){
            
            final Map<KeyExtent, List<Range>> tabletsRanges = binnedRanges.get(tsLocation);
            if(maxTabletsPerRequest == Integer.MAX_VALUE || tabletsRanges.size() == 1){
                //System.out.println("DEBUG : tabletsRanges.size() = "+tabletsRanges.size()+"  tsLocation = "+tsLocation);
                QueryTask queryTask = new QueryTask(tsLocation, tabletsRanges, failures, receiver, columns, authorizations);
                queryTasks.add(queryTask);
            }else{
                HashMap<KeyExtent, List<Range>> tabletSubset = new HashMap<KeyExtent, List<Range>>();
                for (Entry<KeyExtent, List<Range>> entry : tabletsRanges.entrySet()) {
                    tabletSubset.put(entry.getKey(), entry.getValue());
                    //System.out.println("DEBUG : tabletSubset.size() = "+tabletSubset.size()+"  tsLocation = "+tsLocation);
                    if(tabletSubset.size() >= maxTabletsPerRequest){
                        QueryTask queryTask = new QueryTask(tsLocation, tabletSubset, failures, receiver, columns, authorizations);
                        queryTasks.add(queryTask);
                        tabletSubset = new HashMap<KeyExtent, List<Range>>();
                    }
                }
                
                if(tabletSubset.size() > 0){
                    QueryTask queryTask = new QueryTask(tsLocation, tabletSubset, failures, receiver, columns, authorizations);
                    queryTasks.add(queryTask);
                }
            }
        }
        
        final Semaphore semaphore = new Semaphore(queryTasks.size());
        semaphore.acquireUninterruptibly(queryTasks.size());
        
        for (QueryTask queryTask : queryTasks) {
            queryTask.setSemaphore(semaphore, queryTasks.size());
            queryThreadPool.execute(queryTask);
        }
    }
    
    private Map<KeyExtent, List<Range>> doLookup(String server, Map<KeyExtent, List<Range>> tabletsRanges, ResultReceiver receiver, List<Column> columns, byte authorizations[])
    throws IOException, CBSecurityException {
        //TODO maybe only send 1000 or so ranges at a time
        
        if(tabletsRanges.size() == 0){
            return Collections.emptyMap();
        }

        TTransport transport = null; 
        
        try {

            transport = ThriftTansportPool.getInstance().getTransport(server, CBConfiguration.getInstance().getInt("cloudbase.tabletserver.clientPort", CBConstants.TABLET_CLIENT_PORT_DEFAULT));
            TProtocol protocol = new TBinaryProtocol(transport);
            TabletClientService.Client client = new TabletClientService.Client(protocol);
            
            HashSet<Short> auths = new HashSet<Short>(options.authorizations.length);
            for (short s : options.authorizations)
            	auths.add(s);

            InitialScan imsr = client.startMultiScan(credentials, tabletsRanges, columns, options.serverSideIteratorList, options.serverSideIteratorOptions, auths);
            
            ScanResult scanResult = imsr.result;
            
            for (KeyValue kv : scanResult.data) {
                receiver.receive(kv.key, new Value(kv.value));
            }
            
            while(scanResult.more){
                scanResult = client.continueMultiScan(imsr.scanID);
                for (KeyValue kv : scanResult.data) {
                    receiver.receive(kv.key, new Value(kv.value));
                }
            }
            
            Map<KeyExtent, List<Range>> failures = client.closeMultiScan(imsr.scanID);
            
            return failures;
        } catch (TTransportException e) {
            e.printStackTrace();
            throw new IOException(e);
        } catch (ThriftSecurityException e) {
            e.printStackTrace();
            throw new CBSecurityException(e.user, e.baduserpass, e);
		}catch (TException e) {
            e.printStackTrace();
            throw new IOException(e);
        } catch (NoSuchScanIDException e) {
            e.printStackTrace();
            throw new IOException(e);
		}finally{
            ThriftTansportPool.getInstance().returnTransport(transport);
        }
    }
}
