package cloudbase.core.client.proxy;

import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.apache.hadoop.io.Text;
import org.apache.log4j.Logger;

import cloudbase.core.CBConstants;
import cloudbase.core.Cloudbase;
import cloudbase.core.client.BatchScanner;
import cloudbase.core.client.BatchWriter;
import cloudbase.core.client.Connector;
import cloudbase.core.client.MutationsRejectedException;
import cloudbase.core.client.impl.HdfsZooInstance;
import cloudbase.core.client.impl.MasterClient;
import cloudbase.core.client.impl.ScannerImpl;
import cloudbase.core.client.proxy.thrift.ClientProxy.Iface;
import cloudbase.core.client.proxy.thrift.ClientProxy.Processor;
import cloudbase.core.conf.CBConfiguration;
import cloudbase.core.data.Column;
import cloudbase.core.data.ConstraintViolationSummary;
import cloudbase.core.data.Key;
import cloudbase.core.data.KeyExtent;
import cloudbase.core.data.KeyValue;
import cloudbase.core.data.Mutation;
import cloudbase.core.data.Range;
import cloudbase.core.data.ScanResult;
import cloudbase.core.data.UpdateErrors;
import cloudbase.core.data.Value;
import cloudbase.core.master.thrift.MasterClientService;
import cloudbase.core.security.LabelConversions;
import cloudbase.core.security.LabelExpression;
import cloudbase.core.security.thrift.AuthInfo;
import cloudbase.core.security.thrift.ThriftSecurityException;
import cloudbase.core.tabletserver.thrift.NoSuchScanIDException;

import com.facebook.thrift.TException;
import com.facebook.thrift.TProcessor;
import com.facebook.thrift.protocol.TBinaryProtocol;
import com.facebook.thrift.server.TThreadPoolServer;
import com.facebook.thrift.transport.TServerSocket;
import com.facebook.thrift.transport.TSocket;

public class ClientProxy {
    private static Logger log = Logger.getLogger(ClientProxy.class.getName());
        
    // These values are completely arbitrary
    static int maxMemory = 1 << 12;
    static int maxLatency = 1000;
    static int maxWriteThreads = 10;
        
    static int batchCapacity = 10000;
    static int maxReadThreads = 10;
    static final KeyValue marker = new KeyValue();
    static String username = null;
    static byte[] password = null;
        
    /**
     * Batch: the target of a BatchReader thread.
     *
     * Queues results for pick-up by the client.  When the scanning
     * thread finishes, it will call close on the batch which will
     * push the marker object into the queue.  We must take care to
     * make the Batch target unusable by the scanner thread if the
     * user gives up and posts a close before we are done.  Otherwise,
     * the scanning thread will block and never release the Batch.
     */
    private static class Batch
    {
        private BlockingQueue<KeyValue> results = 
            new ArrayBlockingQueue<KeyValue>(batchCapacity, true);
        private boolean finished = false;

        public void receive(Key key, Value value) {
            try {
                results.put(new KeyValue(key, value.get()));
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
        }
        ScanResult fetch() {
            ScanResult result = new ScanResult();
            result.data = new ArrayList<KeyValue>();
            if (!finished) {
                // wait for at least one element
                try {
                    result.data.add(results.take());
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }

                // they will come in batches, so get
                // any remaining
                results.drainTo(result.data);
                int last = result.data.size() - 1;
                if (result.data.get(last) == marker) {
                    result.data.remove(last);
                    finished = true;
                }
            }
            result.more = !finished;
            return result;
        }
        void close() {
            if (results != null) {
                try {
                    results.put(marker);
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
        void cancel() {
            BlockingQueue<KeyValue> tmp = results;

            // force the posting thread to fail with a
            // null pointer exception
            results = null;
            tmp.drainTo(new ArrayList<KeyValue>());
        }
    }

    private static List<KeyValue> loadBatch(Iterator<Entry<Key, Value>> iter, int batchSize)  {
        List<KeyValue> result = new ArrayList<KeyValue>(batchSize);
        for (int i = 0; iter.hasNext() && i < batchSize; i++) {
            Entry<Key, Value> kv = iter.next();
            result.add(new KeyValue(kv.getKey(), kv.getValue().get()));
        }

        return result;
    }
        
    public static class ClientProxyHandler implements Iface {
        private Map<Long, Iterator<Entry<Key, Value>>> scanners = new HashMap<Long, Iterator<Entry<Key, Value>>>();
        private Map<Long, Batch> batches = new HashMap<Long, Batch>();
        private int batchSize;
        private static Random scannerIdGenerator = new Random();

        private MasterClientService.Client getMasterConnection() {
            try {
                String hostname = MasterClient.lookupMaster();
                int port = CBConfiguration.getInstance().getInt("cloudbase.master.clientPort", CBConstants.MASTER_CLIENT_PORT_DEFAULT);
                TSocket transport = new TSocket(hostname, port);
                TBinaryProtocol protocol = new TBinaryProtocol(transport);
                MasterClientService.Client client = 
                    new MasterClientService.Client(protocol);
                transport.open();
                log.info(String.format("Connected to %s:%d", hostname, port));
                return client;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        
        private static AuthInfo getAuthInfo() {
        	return new AuthInfo(username, password);
        }
                
        public void createTable(String table, List<byte[]> splitPoints)
        {
        	MasterClientService.Client client = getMasterConnection();
            try {
                client.createTable(getAuthInfo(), table, splitPoints);
            } catch (Exception e) {
				log.warn(e);
				e.printStackTrace();
				throw new RuntimeException(e);
			} finally {
                client.getOutputProtocol().getTransport().close();
            }
        }

                
        public void deleteTable(String table)
        {
            MasterClientService.Client client = getMasterConnection();
            try {
                client.deleteTable(getAuthInfo(), table);
            } catch (Exception e) {
				log.warn(e);
				e.printStackTrace();
				throw new RuntimeException(e);
			} finally {
                client.getOutputProtocol().getTransport().close();
            }
        }

                
        public void ping()
        {
        	MasterClientService.Client client = getMasterConnection();
            try {
                client.ping(getAuthInfo());
            } catch (Exception e) {
				log.warn(e);
				e.printStackTrace();
				throw new RuntimeException(e);
			} finally {
                client.getOutputProtocol().getTransport().close();
            }
        }

                
        public void shutdown(boolean stopTabletServers)
        {
            MasterClientService.Client client = getMasterConnection();
            try {
                client.shutdown(getAuthInfo(), stopTabletServers);
            } catch (Exception e) {
				log.warn(e);
				e.printStackTrace();
				throw new RuntimeException(e);
			} finally {
                client.getOutputProtocol().getTransport().close();
            }
        }
        
        private static Key makeKey(cloudbase.core.client.proxy.thrift.Key key) {
        	Key result = null;
        	if (key != null && key.row != null && key.row.length > 0) {
        		if (key.column != null) {
        		    byte[] visibility = 
        		        LabelConversions.formatAuthorizations(key.column.columnVisibility);
        			result = new Key(key.row,
        							  key.column.columnFamily,
        							  key.column.columnQualifier,
        							  visibility,
        							  key.timestamp);
        			
        		}
        		if (key.timestamp != 0)        		
        			result = new Key(new Text(key.row), key.timestamp);
        		else
        			result = new Key(new Text(key.row));
        	}
        	return result;
        }
        
        private static Range convertRange(cloudbase.core.client.proxy.thrift.Range simpleRange) {
            System.err.println("simpleRange: " + simpleRange);
        	boolean startInclusive = simpleRange.start != null && simpleRange.startInclusive;
        	boolean stopInclusive = simpleRange.stop != null && simpleRange.stopInclusive;
        	return new Range(makeKey(simpleRange.start), startInclusive,
							 makeKey(simpleRange.stop), stopInclusive);
        }
        
        public cloudbase.core.client.proxy.thrift.InitialScan startScan(String table,
                                     cloudbase.core.client.proxy.thrift.Range simpleRange, 
                                     List<cloudbase.core.client.proxy.thrift.Column> columns, 
                                     int batchSize)
        {            
            log.error("Simple Range" + simpleRange);
        	KeyExtent extent = new KeyExtent();
         	extent.setTableName(new Text(table));
         	
         	this.batchSize = batchSize;
         	
            try {
                ScannerImpl scanner = new ScannerImpl(new HdfsZooInstance(), new AuthInfo(username, password), extent.getTableName().toString(), CBConstants.NO_AUTHS);
                scanner.setBatchSize(batchSize);
                for (cloudbase.core.client.proxy.thrift.Column column : columns) {
                    Column copy = new Column(column.columnFamily,
                                             column.columnQualifier,
                                             LabelConversions.formatAuthorizations(column.columnVisibility));
                    scanner.fetchColumn(copy);
                }
                scanner.setRange(convertRange(simpleRange));
                cloudbase.core.client.proxy.thrift.InitialScan result = 
                	new cloudbase.core.client.proxy.thrift.InitialScan();
                result.scanID = scannerIdGenerator.nextLong();
                while (scanners.containsKey(result.scanID)) {
                    result.scanID = scannerIdGenerator.nextLong();
                }
                
                Iterator<Entry<Key, Value>> iter = scanner.iterator();
                
                scanners.put(result.scanID, iter);
                ScanResult sr = new ScanResult();
                sr.data = loadBatch(iter, batchSize);
                sr.more = iter.hasNext();
                result.result = convert(sr); 
                return result;
            } catch (Exception e) {
				log.warn(e);
				e.printStackTrace();
				throw new RuntimeException(e);
            }
        }

        public cloudbase.core.client.proxy.thrift.ScanResult continueScan(long scanID) throws NoSuchScanIDException
        {
            Iterator<Entry<Key, Value>> iter = scanners.get(scanID);
            if (iter == null)
                throw new NoSuchScanIDException();
            ScanResult result = new ScanResult();
            result.data = loadBatch(iter, batchSize);
            result.more = iter.hasNext();
            return convert(result);
        }

        public void closeScan(long scanID) throws TException {
            scanners.remove(scanID);            
        }

    	private void mutate(Mutation m, cloudbase.core.client.proxy.thrift.ColumnUpdate update) {
    		
    		Text colf = new Text(update.column.columnFamily);
    		Text colq = new Text(update.column.columnQualifier);
    		
    		LabelExpression le;
    		if(update.column.columnVisibility == null || update.column.columnVisibility.size() == 0){
    			le = new LabelExpression(new short[0]);
    		}else{
    			le = new LabelExpression(update.column.columnVisibility);
    		}
    		
    		if (update.deleted) {
    			if (update.__isset.timestamp && update.timestamp != 0)
    				m.remove(colf, colq ,le , update.timestamp);
    			else
    				m.remove(colf, colq ,le);
    			return;
    		}
    		if (update.__isset.timestamp && update.timestamp != 0)
    			m.put(colf, colq ,le, update.timestamp, new Value(update.value));
    		else
    			m.put(colf, colq ,le, new Value(update.value));
    	}

        public UpdateErrors update(String table, List<cloudbase.core.client.proxy.thrift.Mutation> updates)
        {                        
            try {
                Connector connector = new Connector(new HdfsZooInstance(), username, password);
                BatchWriter writer = connector.createBatchWriter(table, maxMemory, maxLatency, maxWriteThreads);
                
                for (cloudbase.core.client.proxy.thrift.Mutation mutation : updates) {
                        Mutation m = new Mutation(new Text(mutation.row));
                        for (cloudbase.core.client.proxy.thrift.ColumnUpdate update : mutation.updates) {
                        	mutate(m,update);
                        }
                        writer.addMutation(m);
                }
                
                try {
					writer.close();
				} catch (MutationsRejectedException e) {
				    Map<KeyExtent,Long> empty = Collections.emptyMap();
					return new UpdateErrors(empty, e.getConstraintViolationSummaries(), e.getAuthorizationFailures());
				}
				Map<KeyExtent,Long> empty = Collections.emptyMap();
				List<ConstraintViolationSummary> emptyViolations = Collections.emptyList();
				List<KeyExtent> emptyExtents = Collections.emptyList();
				return new UpdateErrors(empty, emptyViolations, emptyExtents);
            } catch (Exception e) {
				log.warn(e);
				e.printStackTrace();
				throw new RuntimeException(e);
			}
        }
        
        cloudbase.core.client.proxy.thrift.ScanResult convert(ScanResult internal) {
            cloudbase.core.client.proxy.thrift.ScanResult result = 
            	new cloudbase.core.client.proxy.thrift.ScanResult();
            result.more = internal.more;
            result.data = new ArrayList<cloudbase.core.client.proxy.thrift.KeyValue>(internal.data.size());
            for (KeyValue kv : internal.data) {
            	cloudbase.core.client.proxy.thrift.KeyValue rkv =
            		new cloudbase.core.client.proxy.thrift.KeyValue();
            	rkv.key = getKey(kv.key);
            	rkv.value = kv.value;
            	result.data.add(rkv);
            }
            return result;
        	
        }
        

        public cloudbase.core.client.proxy.thrift.ScanResult fetch(long scanID) 
        	throws NoSuchScanIDException, TException {
            Batch batch = batches.get(scanID);
            if (batch == null) {
                throw new NoSuchScanIDException();
            }
            return convert(batch.fetch());
        }
        
         public long lookup(final String table, 
                           final List<cloudbase.core.client.proxy.thrift.Range> simpleRanges, 
                           final List<cloudbase.core.client.proxy.thrift.Column> columns,
                           List<Integer> authorizations) 
            throws TException {
            final Batch batch = new Batch();
            long scanID = scannerIdGenerator.nextLong();
            while (batches.containsKey(scanID)) {
                scanID = scannerIdGenerator.nextLong();
            }
            batches.put(scanID, batch);
            final short[] shortAuthorizations = new short[authorizations.size()];
            final ArrayList<Range> ranges = new ArrayList<Range>(simpleRanges.size());
            for (int i = 0; i < authorizations.size(); i++) {
                shortAuthorizations[i] = authorizations.get(i).shortValue();
            }
            for (cloudbase.core.client.proxy.thrift.Range simpleRange : simpleRanges) {
            	ranges.add(convertRange(simpleRange));
            }
            Thread thread = new Thread(
             new Runnable() {
                 public void run() {
                     Connector connector;
                     BatchScanner reader = null;
                     
                     
                     try {
                         connector = new Connector(new HdfsZooInstance(), username, password);
                    	 reader = connector.createBatchScanner(table, CBConstants.NO_AUTHS, maxReadThreads);
                    	 
                    	 reader.setRanges(ranges);
                    	 
                         for (Entry<Key,Value> entry : reader)
                             batch.receive(entry.getKey(), entry.getValue());

                     } catch (Exception ex) {
                         log.error("Exception", ex);
                     } finally {
                    	 if(reader != null)
                    		 reader.close();
                         batch.close();
                     }
                                                
                 }      
             }, "Batch Reader");
            thread.start();
            return scanID;
        }
        public void closeBatch(long scanID) throws TException {
            Batch batch = batches.remove(scanID);
            if (batch != null) {
                batch.cancel();
            }
        }

        @Override
        public Set<String> getTables()
        {
            MasterClientService.Client client = getMasterConnection();
            try {
                return client.getTables(getAuthInfo());
            } catch (Exception e) {
				log.warn(e);
				e.printStackTrace();
				throw new RuntimeException(e);
			} finally {
                client.getOutputProtocol().getTransport().close();
            }  
        }

		@Override
		public boolean setTableProperty(String table, String property, String value)
		{
			MasterClientService.Client client = getMasterConnection();
            try {
                return client.setTableProperty(getAuthInfo(), table, property, value);
            } catch (ThriftSecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (TException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
                client.getOutputProtocol().getTransport().close();
            }
			return false;  
		}

		@Override
		public boolean removeTableProperty(String table, String property)
		{
			MasterClientService.Client client = getMasterConnection();
            try {
                return client.removeTableProperty(getAuthInfo(), table, property);
            } catch (ThriftSecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (TException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
                client.getOutputProtocol().getTransport().close();
            }
			return false;  
		}

    }
        
    public static void main(String args[]) {
        TProcessor processor = new Processor(new ClientProxyHandler());
        
        try {
            Cloudbase.init("//conf//client_proxy.ini");
            CBConfiguration conf = CBConfiguration.getInstance();
            maxMemory = conf.getInt("cloudbase.proxy.batchWriter.maxMemory", maxMemory);
            maxLatency =  conf.getInt("cloudbase.prox.batchWriter.maxLatency", maxLatency);
            maxWriteThreads = conf.getInt("cloudbase.proxy.batchWriter.maxThreads", maxWriteThreads);
            batchCapacity = conf.getInt("cloudbase.proxy.batchReader.readCapacity", batchCapacity);
            maxReadThreads = conf.getInt("cloudbase.proxy.batchReader.maxThreads", maxReadThreads);
            username = conf.get("cloudbase.proxy.username", "root");
            password = conf.get("cloudbase.proxy.password", "secret").getBytes();

            int port = conf.getInt("cloudbase.proxy.port", 0);
            if (args.length > 0) {
                try {
                    port = Integer.parseInt(args[0]);
                } catch (NumberFormatException ex) {
                    throw new RuntimeException(ex);
                }
            }
            log.info("Client Proxy server starting on " + port);
                        
            ServerSocket socket = new ServerSocket();
            socket.setReuseAddress(true);
            socket.bind(new InetSocketAddress(port));
            TServerSocket serverTransport = new TServerSocket(socket);
            System.out.println(String.format("Proxy Serving on port [%d]", 
                                             socket.getLocalPort()));
            TThreadPoolServer server = new TThreadPoolServer(processor, 
                                                             serverTransport);
            server.serve();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static byte[] extract(Key ikey, int sliceStart, int sliceLen) {
		byte[] result = new byte[sliceLen];
		System.arraycopy(ikey.getKeyData(), sliceStart, result, 0, sliceLen);
		return result;
	}

	public static cloudbase.core.client.proxy.thrift.Key getKey(Key key) {
		cloudbase.core.client.proxy.thrift.Key tKey = new cloudbase.core.client.proxy.thrift.Key();
		byte[] columnBytes = extract(key, key.getColumnVisibilityOffset(), key.getColumnVisibilityLen());
		LabelExpression expr = new LabelExpression(columnBytes);
        List<List<Short>> visibility = new ArrayList<List<Short>>();
        while (expr.hasNext()) {
            List<Short> shorts = new ArrayList<Short>();
            for (short s : expr.next())
                shorts.add(s);
            visibility.add(shorts);
        }
		tKey.column = new cloudbase.core.client.proxy.thrift.Column
		(extract(key, key.getColumnFamilyOffset(), key.getColumnFamilyLen()),
		 extract(key, key.getColumnQualifierOffset(), key.getColumnQualifierLen()),
		 visibility);
		tKey.row = extract(key, key.getRowOffset(), key.getRowLen());
		tKey.timestamp = key.getTimestamp();
		return tKey;
	}
}
