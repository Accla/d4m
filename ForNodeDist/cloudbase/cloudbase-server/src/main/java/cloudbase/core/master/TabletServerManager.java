package cloudbase.core.master;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.hadoop.io.Text;
import org.apache.log4j.Logger;

import cloudbase.core.CBConstants;
import cloudbase.core.conf.CBConfiguration;
import cloudbase.core.data.KeyExtent;
import cloudbase.core.master.balancer.TabletServerStatsProvider;
import cloudbase.core.master.mgmt.TabletServerState;
import cloudbase.core.master.thrift.TabletRates;
import cloudbase.core.master.thrift.TabletServerStatus;
import cloudbase.core.security.thrift.AuthInfo;
import cloudbase.core.tabletserver.thrift.TabletMasterService;
import cloudbase.core.util.AddressUtil;
import cloudbase.core.util.TabletFunctions;
import cloudbase.core.util.TextUtil;
import cloudbase.core.util.UtilWaitThread;
import cloudbase.core.zookeeper.ZooLock;
import cloudbase.core.zookeeper.ZooLock.AsyncLockWatcher;
import cloudbase.core.zookeeper.ZooLock.LockLossReason;

import com.facebook.thrift.TException;
import com.facebook.thrift.protocol.TBinaryProtocol;
import com.facebook.thrift.transport.TSocket;

public class TabletServerManager {

	private Map<String, TabletServerConnection> allServers;
	private Map<TabletServerState, SortedMap<String, TabletServerConnection>> index;
	
	private static Logger log = Logger.getLogger(TabletServerManager.class.getName());
	
	static class TabletServerLockWatcher implements AsyncLockWatcher {

	    volatile boolean gotLock = false;
	    volatile Exception failureException = null;
	    
        @Override
        public void acquiredLock() {
            gotLock = true;
        }

        @Override
        public void failedToAcquireLock(Exception e) {
            failureException = e;
        }

        @Override
        public void lostLock(LockLossReason reason) {
            // TODO Auto-generated method stub
            
        }
	    
	}
	
	public class TabletServerConnection implements Comparable<TabletServerConnection>, TabletServerStatsProvider {
		
	    public TabletServerLockWatcher lockWatcher;
        public ZooLock zooLock;
	    
	    private InetSocketAddress address;
		
		private AuthInfo credentials = null;
		private Logger log = Logger.getLogger(TabletServerConnection.class.getName());
		private double load;
		private int retries;
		
		public TabletServerStatus status;
		
	    TSocket socket = null;
	    TBinaryProtocol protocol = null;
	    TabletMasterService.Client client = null;

		private InetSocketAddress clientAddress = null;

		private InetSocketAddress monitorAddress = null;

		private TabletServerState state;

	    public TabletServerConnection(InetSocketAddress addr, int timeout, AuthInfo sb) {
	    	address = addr;
	    	credentials = sb;
	    	
			status = new TabletServerStatus();
			load = -1.0;

			retries = CBConfiguration.getInstance().getInt("cloudbase.client.retries", CBConstants.MAX_CLIENT_RETRIES);
			retries = Math.max(retries, 1);
	    }

            /**
             * Connect to the tablet server with retries
             */
            private void connectWithRetry(int timeout) throws TException
            {
            	// Used to bypass throwing iox due to both comparisons being less than.  Now will throw error
                for(int numTries=0; numTries <= retries; numTries++) {
                	try
                    {
                        connect(timeout);
                        break;
                    }
                    catch (TException iox)
                    {
                        if (numTries < retries)
                        {
                            log.error("Could not connect to tablet server " + address + " : " + iox.getMessage() + ", retrying.");
                            UtilWaitThread.sleep(CBConstants.CLIENT_SLEEP_BEFORE_RECONNECT);
                        }
                        else
                        {
                            throw iox;
                        }
                    }
                }
                return;
            }
            
		/**
		 * Invalidates the connection
		 * 
		 * @throws IOException
		 */
	    public void close() throws TException {
	    	if(socket != null && socket.isOpen()) {
	    		socket.close();
	    		socket = null;
	    	}
	    }
	   
		/**
		 * Opens a socket connection with the tablet with a given timeout value
		 * 
		 * @param timeout
		 */	    
	    public void connect(int timeout) throws TException {
	        // TODO: timeout is unused 
	    	close();
    		socket = new TSocket(address.getHostName(), address.getPort());
    		protocol = new TBinaryProtocol(socket);
    		client = new TabletMasterService.Client(protocol);
    		socket.open();
   	    }
	    
	    
		/**
		 * Returns the tablet's Master Service address as a string
		 * 
		 * @return address
		 */	    
	    public String getMasterAddress() {
	    	return AddressUtil.toString(address);
	    }

		public void setClientAddress(String clientServiceLocation) {
			int port = CBConfiguration.getInstance().getInt("cloudbase.tabletserver.clientPort", CBConstants.TABLET_CLIENT_PORT_DEFAULT);
			clientAddress = AddressUtil.parseAddress(clientServiceLocation, port);
		}
		
		public String getClientAddress() {
			return AddressUtil.toString(clientAddress);
		}
		
		public int compareTo(TabletServerConnection other) {
			return this.getMasterAddress().compareTo(other.getMasterAddress());
		}
		
		public boolean equals(TabletServerConnection other) {
			return this.compareTo(other) == 0;
		}
		
	    public void shutdown(int stage) throws TException {
	    	client.shutdown(credentials, getMasterAddress(), stage);
	    }
	    
	    public void ping() throws TException {
	    	client.ping(credentials);
	    }
	    public void assignTablet(KeyExtent extent, Text location) throws TException {
	    	client.loadTablet(credentials, extent, TextUtil.getBytes(location));
	    }
	    public void sendTabletList() throws TException {
	    	client.sendTabletList(credentials);
	    }
	    
	    public void unloadTablet(KeyExtent extent, boolean save) throws TException {
	    	client.unloadTablet(credentials, extent, save);
	    }

		public String getMonitorUrl(String myAddress) {
			return "http://" + monitorAddress.getHostName() + ":" + monitorAddress.getPort() + "/monitor?masterServer=" + myAddress;
		}
		
		public void setMonitorAddress(String address) {
			monitorAddress = AddressUtil.parseAddress(address,
					CBConfiguration.getInstance().getInt("cloudbase.tabletserver.monitor.port", 50096));
		}

		public String getMonitorName() {
			return address.toString();
		}
		
		public void flush(Set<String> tables) throws TException{
			client.flush(credentials, tables);
		}
		
		public double getLoad() {
		    if(load < 0 && statsUpToDate()) {
		        load = 0.0;
		        for(TabletRates tablet : status.tabletRates)
		            load += TabletFunctions.getLoad(tablet);
		    }

		    return load;
		}
		
		public int getHostedTabletCount() {
            int numTablets = 0;
            
            if(status != null && status.tabletRates != null){
                numTablets = status.tabletRates.size();
            }
            return numTablets;
		}
		
		public TabletServerStatus getStatus() {
		    List<TabletRates> rates = new ArrayList<TabletRates>();
		    if (status.tabletRates != null)
		        rates.addAll(status.tabletRates);
		    return new TabletServerStatus(status.queryRate,
		                                  status.ingestRate,
		                                  rates,
		                                  status.totalRecords,
		                                  status.lastContact,
		                                  status.URL,
		                                  status.name,
		                                  getLoad(),
		                                  status.osLoad,
		                                  status.tabletServerTime);
		}
		
		public boolean statsUpToDate() {
			return status != null && status.tabletRates != null;
		}

		public void updateStats(TabletServerStatus in) {
			this.status = in;
			this.status.lastContact = System.currentTimeMillis();
			this.load = -1;
		}


		public String getName() {
			return getMasterAddress();
		}

		@Override
		public List<TabletRates> getTabletsStats() {
			if(!statsUpToDate())
				return Collections.emptyList();
			
			return this.status.tabletRates;
		}
		
		public void setState(TabletServerState state){
			synchronized (TabletServerManager.this) {
				
				TabletServerConnection removedTsc = index.get(this.state).remove(getName());
				
				if(removedTsc != this){
					log.error("Tablet server index corrupted, tablet "+getName()+" not at expected state "+this.state);
					throw new RuntimeException("The sh*t hit the fan");
				}
				
				index.get(state).put(getName(), this);
				
				log.debug(String.format("Tablet server state transition %10s -> %10s : %s", this.state, state, this.getName()));
				
				this.state = state;
			}
		}
		
		public TabletServerState getState(){
			synchronized (TabletServerManager.this) {
				return state;
			}
		}
	}
	
	TabletServerManager(){
		
		allServers = Collections.synchronizedMap(new TreeMap<String, TabletServerConnection>());
		
		index = Collections.synchronizedMap(new EnumMap<TabletServerState, SortedMap<String,TabletServerConnection>>(TabletServerState.class));
		
		for(TabletServerState tss : TabletServerState.values()){
			index.put(tss, Collections.synchronizedSortedMap(new TreeMap<String, TabletServerConnection>()));
		}
		
	}
	
	TabletServerConnection addTabletServer(InetSocketAddress addr, int timeout, AuthInfo sb, ZooLock zooLock, TabletServerLockWatcher lockWatcher) throws TException{
		TabletServerConnection tsc = null;
		synchronized(this){
		
		    tsc = new TabletServerConnection(addr, timeout, sb);
		    
    		tsc.lockWatcher = lockWatcher;
    		tsc.zooLock = zooLock;
    		
    		TabletServerConnection existingTsc = allServers.get(tsc.getName());
    		if(existingTsc != null){
    			existingTsc.close();
    		}
    		
    		allServers.put(tsc.getName(), tsc);
    		
    		for(TabletServerState tss : TabletServerState.values()){
    			if(index.get(tss).remove(tsc.getName()) != null){
    			    log.debug(String.format("Tablet server state transition %10s -> %10s : %s", tss, TabletServerState.NEW, tsc.getName()));
    			}
    		}
    		
    		tsc.state = TabletServerState.NEW;
    		index.get(TabletServerState.NEW).put(tsc.getName(), tsc);
		}
		
		try {
			tsc.connectWithRetry(timeout);
		} catch (TException e) {
			//tsc.setState(TabletServerState.DOWN);
		    log.warn("Failed to connect to tablet server "+tsc.getName(), e);
			throw e;
		}
		
		return tsc;
	}
	
	synchronized Map<String, TabletServerConnection> getIndex(TabletServerState state){
		return index.get(state);
	}
	
	Map<String, TabletServerConnection> getTabletServers(){
		return allServers;
	}
}
