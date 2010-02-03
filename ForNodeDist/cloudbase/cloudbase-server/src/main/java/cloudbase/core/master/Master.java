package cloudbase.core.master;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Map.Entry;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.security.authorize.AuthorizationException;
import org.apache.log4j.Logger;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;

import cloudbase.core.CBConstants;
import cloudbase.core.Cloudbase;
import cloudbase.core.client.CBException;
import cloudbase.core.client.CBSecurityException;
import cloudbase.core.client.TableNotFoundException;
import cloudbase.core.client.impl.HdfsZooInstance;
import cloudbase.core.client.impl.ScannerImpl;
import cloudbase.core.client.impl.TabletClient;
import cloudbase.core.conf.CBConfiguration;
import cloudbase.core.data.Key;
import cloudbase.core.data.KeyExtent;
import cloudbase.core.data.Mutation;
import cloudbase.core.data.Range;
import cloudbase.core.data.Value;
import cloudbase.core.master.TabletServerManager.TabletServerConnection;
import cloudbase.core.master.TabletServerManager.TabletServerLockWatcher;
import cloudbase.core.master.TabletsManager.TabletStatus;
import cloudbase.core.master.balancer.LoadBalancer;
import cloudbase.core.master.balancer.SimpleLoadBalancer;
import cloudbase.core.master.balancer.TabletMigration;
import cloudbase.core.master.mgmt.TabletServerState;
import cloudbase.core.master.mgmt.TabletState;
import cloudbase.core.master.thrift.KeyExtentLocation;
import cloudbase.core.master.thrift.MasterClientService;
import cloudbase.core.master.thrift.MasterTabletService;
import cloudbase.core.master.thrift.TableCreationException;
import cloudbase.core.master.thrift.TableDeletionException;
import cloudbase.core.master.thrift.TabletRates;
import cloudbase.core.master.thrift.TabletServerStatus;
import cloudbase.core.master.thrift.TabletSplit;
import cloudbase.core.monitor.thrift.MasterMonitorInfo;
import cloudbase.core.monitor.thrift.MasterMonitorService;
import cloudbase.core.monitor.thrift.TableInfo;
import cloudbase.core.security.Authenticator;
import cloudbase.core.security.SystemPermission;
import cloudbase.core.security.TablePermission;
import cloudbase.core.security.ZKAuthenticator;
import cloudbase.core.security.thrift.AuthInfo;
import cloudbase.core.security.thrift.ThriftSecurityException;
import cloudbase.core.tabletserver.thrift.ConstraintViolationException;
import cloudbase.core.tabletserver.thrift.NotServingTabletException;
import cloudbase.core.util.AddressUtil;
import cloudbase.core.util.Daemon;
import cloudbase.core.util.Encoding;
import cloudbase.core.util.LoggingRunnable;
import cloudbase.core.util.MetadataTable;
import cloudbase.core.util.TServerUtils;
import cloudbase.core.util.TablePropUtil;
import cloudbase.core.util.UtilWaitThread;
import cloudbase.core.zookeeper.ZooCache;
import cloudbase.core.zookeeper.ZooConstants;
import cloudbase.core.zookeeper.ZooLock;
import cloudbase.core.zookeeper.ZooSession;
import cloudbase.core.zookeeper.ZooUtil;
import cloudbase.core.zookeeper.ZooLock.LockLossReason;
import cloudbase.core.zookeeper.ZooLock.LockWatcher;

import com.facebook.thrift.TException;
import com.facebook.thrift.server.TServer;
import com.facebook.thrift.transport.TServerTransport;



/**
 * 
 * The master has several jobs:
 * 
 * 1 discover tablet servers
 * 2 process messages from tablets - splits n such
 * 4 assign tablets
 * 5 balance the tablet load across the tablet servers
 * x. Garbage collect SSTables no tablet is using
 * 
 * 
 */

public class Master {

	static Logger log = Logger.getLogger(Master.class.getName());

	/**
	 * updates metadata table without relying on the metadata table now just
	 * handles location updates
	 * 
	 * all metadata location updates are sent to the root tablet
	 * 
	 * @throws IOException
	 * @throws NotServingTabletException
	 * @throws AuthorizationException 
	 * @throws ConstraintViolationException 
	 */
	private boolean applyMetadataLocationUpdate(KeyExtent extent, String address) throws NotServingTabletException, TException {
		if(extent.compareTo(CBConstants.ROOT_TABLET_EXTENT) == 0) 
			return true;

		TabletServerConnection rts = tabletsManager.getAssignedServer(CBConstants.ROOT_TABLET_EXTENT);
		if(rts == null) {
			log.error("no assigned server found for " + extent + ". can't write update to metadata");
			log.error("tablet state is " + tabletsManager.getTablet(extent).getState());
			return false;
		}

		// TODO : Ideally, we should only write out new locations, not all of them.
		// Currently, if we restart the master, this could massively delay return time.
		try {
			Mutation m = new Mutation(new Text(KeyExtent.getMetadataEntry(extent.getTableName(), extent.getEndRow())));
			m.put(CBConstants.METADATA_TABLE_TABLET_COLUMN_FAMILY, CBConstants.METADATA_TABLE_TABLET_LOCATION_COLUMN_NAME, new Value(address.getBytes()));

			TabletClient.updateServer(m, CBConstants.ROOT_TABLET_EXTENT, rts.getClientAddress(), systemCredentials());
			return true;
		}catch (CBSecurityException e) {
			log.error("Master authentication failure...");
			return false;
		} catch (ConstraintViolationException e) {
			log.error("!METADATA Constraint violation", e);
			return false;
		}
	}


	/**
	 * This class applies location updates as a separate thread
	 * this way the main tablet server handler thread doesn't
	 * have to wait for metadata tablets to come online
	 * 
	 *
	 */
	private class LocationUpdateManager implements Runnable {


		private LinkedBlockingQueue<KeyExtent> updates;

		public LocationUpdateManager() {
			updates = new LinkedBlockingQueue<KeyExtent>();
		}

		@Override
		public void run() {
			KeyExtent extent;

			while(!allStopRequested) {

				try {
					extent = updates.take();
				} catch (InterruptedException e1) {
					continue;
				}

				TabletStatus status = tabletsManager.getTablet(extent);
				// tablet doesn't exist anymore - was split
				if(status == null) {
					log.warn("tablet " + extent + " no longer exists? aborting location update");
					continue;
				}

				// tablet is in motion or something, try again
				if(status.getState() != TabletState.ASSIGNED) {
					updates.add(extent);

					// avoid spinning on one update
					if(updates.size() == 1) 
						UtilWaitThread.sleep(100);

					continue;
				}

				String address = status.getTabletServer().getClientAddress();
				if(address == null) {
					updates.add(extent);
					continue;
				}

				if(extent.getTableName().toString().equals(CBConstants.METADATA_TABLE_NAME)) {
					try {
						//log.warn("writing metadata tablet location update to root tablet" + ke + address);
						if(!applyMetadataLocationUpdate(extent, address)) {
							updates.add(extent);
							log.warn("could not write pending update to root tablet (false returned)");
							UtilWaitThread.sleep(100);
						}
					} 
					catch (Exception e) {
						log.warn("could not write pending update to root tablet (exception): " + e.getMessage());
						updates.add(extent);
						UtilWaitThread.sleep(100);
					}
				}
				else { // regular tablets

					try {
						if(!MetadataTable.updateTabletLocation(extent, address, systemCredentials())) {
							log.warn("unable to apply metadata location update: " + extent + " : " + address);
							updates.add(extent);
							UtilWaitThread.sleep(100);
						}
					}
					catch(Exception e) {
						log.warn("unable to apply metadata location update: " + extent + " : " + address);
						updates.add(extent);
						UtilWaitThread.sleep(100);
					}
				}
			}
		}

		public void enqueueUpdate(KeyExtent tablet) {
			try {
				updates.put(tablet);
			} catch (InterruptedException e) {
				log.error("interrupted during waiting to put location update on queue - this shouldn't happen");
				log.error("location update queue should be unbounded");
				e.printStackTrace();
			}
		}
	}

	/**
	 * helper function to assign a tablet to a server
	 * 
	 * @param extent
	 * @param server
	 * @throws IOException
	 */
	private void assignTablet(KeyExtent extent, TabletServerConnection server) throws TException {
		if(shutdownStage >= 2)
		{
			// don't rehost any tablets
			log.debug("Skipping assignment of tablet "+extent+" in shutdownStage "+shutdownStage);
			return;
		}
		else if(shutdownStage >= 1 && !extent.equals(CBConstants.ROOT_TABLET_EXTENT))
		{
			// don't rehost data tablets or metadata tablets
			// only rehost the root tablet
			log.debug("Skipping assignment of tablet "+extent+" in shutdownStage "+shutdownStage);
			return;
		}
		else if(shutdownStage >= 0 && !extent.getTableName().toString().equals(CBConstants.METADATA_TABLE_NAME))
		{
			// don't rehost data tablets
			// only rehost metadata tablets
			log.debug("Skipping assignment of tablet "+extent+" in shutdownStage "+shutdownStage);
			return;
		}
		
		log.debug("assignTablet " + (new Text(tabletsManager.getTablet(extent).getDirectory().getBytes())) + " to " + server.getClientAddress());
		server.assignTablet(extent, tabletsManager.getTablet(extent).getDirectory());

		registerTablet(extent, server, true);
	}

	/**
	 * marks a table as being online and updates the metadata table (and the
	 * availability of the entire metadata table)
	 * 
	 * @param extent
	 * @param server
	 * @throws ConstraintViolationException 
	 * @throws TException
	 */
	private void registerTablet(KeyExtent extent, TabletServerConnection server, boolean setAssigned) {

		if (setAssigned) {
			tabletsManager.getTablet(extent).setAssigned(server);
		}

		// record new root tablet location unless we are starting up
		if (extent.equals(CBConstants.ROOT_TABLET_EXTENT)) { // && rootTabletAssigned()) { 
			MetadataTable.recordRootTabletLocation(server.getClientAddress());
		}
		else if(extent.getTableName().toString().compareTo(CBConstants.METADATA_TABLE_NAME) == 0) {
			log.debug("enqueuing metadata location update " + extent);
			metadataLocationUpdateManager.enqueueUpdate(extent);
		}
		else {
			log.debug("enqueuing location update " + extent);
			locationUpdateManager.enqueueUpdate(extent);
		}
	}

	private class MasterTabletServiceHandler implements MasterTabletService.Iface {
		Logger log = Logger.getLogger(MasterTabletServiceHandler.class.getName());

		public void reportShutdown(AuthInfo credentials, String who, int stage)
		throws TException {
			log.info("User "+credentials.user+" on client " + who + " reports shutdown stage: " + (stage + 1));
			try {
				if(!authenticator.hasSystemPermission(credentials, credentials.user, SystemPermission.SYSTEM))
				{
					log.warn("Got tablet server shutdown status message from unauthorized user: "+credentials.user);
					throw new TException("User "+credentials.user+" not authorized");
				}
			} catch (CBSecurityException e) {
				log.warn("Got tablet server shutdown status message from unauthenticatable user: "+e.user);
				throw new TException("User "+credentials.user+" not authenticated");
			}
			
			TabletServerConnection tServer = tabletServers.getIndex(TabletServerState.ONLINE).get(who);
			if (tServer == null) {
				log.warn("Shutdown message for tablet server we don't know: " + who);
				return;
			}
			// make shutdown stage non-decreasing to handle possible out of order messages
			synchronized(shutdownStages)
			{
				if(shutdownStages.containsKey(tServer) == false || shutdownStages.get(tServer) < stage)
					shutdownStages.put(tServer, stage);				
			}
		}

		public void reportTabletList(AuthInfo credentials, 
				String serverName,	
				List<KeyExtent> extents,
				String clientServiceLocation,
				String monitorServiceLocation,
				long tabletServerTime) throws TException {

			try {
				if(!authenticator.hasSystemPermission(credentials, credentials.user, SystemPermission.SYSTEM))
				{
					log.warn("Got tablet list query from user: "+credentials.user);
					throw new TException("User "+credentials.user+" not authorized");
				}
			} catch (CBSecurityException e) {
				log.warn("Got tablet list query from unauthenticatable user: "+e.user);
				throw new TException("User "+credentials.user+" not authenticated");
			}

			
			//get the time that this method was called on the proxy
			long invokeTime = tabletServiceQueuingProxy.getOriginalInvokeTime();

			if(invokeTime < 0){
				//looks like we are not being called through the proxy
				invokeTime = System.currentTimeMillis();
			}

			log.info("TabletServer "+ serverName + " reported in with " + extents.size() + " extents");
			TabletServerConnection tServer = tabletServers.getIndex(TabletServerState.NEW).get(serverName);

			if (tServer == null) {
				log.warn("Report Tablet List message for tablet server we don't know: " + serverName);
				return;
			}

			if(isTabletServerTimeDeltaTooLarge(serverName, extents, tabletServerTime, invokeTime, tServer)){
				return;
			}

			tServer.setClientAddress(clientServiceLocation);
			tServer.setMonitorAddress(monitorServiceLocation);

			for (KeyExtent extent: extents) {

				TabletStatus tabStat = tabletsManager.getTablet(extent);

				if(tabStat == null) { // first we've heard of a tablet
					tabletsManager.addTablet(extent, TabletState.ASSIGNED, tServer);
					registerTablet(extent, tServer, false);
					tabletsManager.getTablet(extent).setOnline(true);
				} else {				
					switch(tabStat.getState()){
					case ASSIGNED:
						TabletServerConnection server = tabStat.getTabletServer();
						if (server != null && server != tServer) {
							log.warn("already have an assignment for tablet " + extent + ": " + server.getMasterAddress());
							tServer.unloadTablet(extent, true);
						} else {
							tabStat.setOnline(true);
						}
						break;
					case UNASSIGNED:
						registerTablet(extent, tServer, true);
						break;
					default:
						log.error("Tablet "+extent+" in unexpected state "+tabStat.getState()+" after recieving tab server tablet report");
					}
				}
			}

			tServer.setState(TabletServerState.ONLINE);
		}

		private boolean isTabletServerTimeDeltaTooLarge(String serverName, Collection<KeyExtent> extents, long tabletServerTime, long invokeTime, TabletServerConnection tServer)
				throws TException {
			
			long delta = invokeTime - tabletServerTime;
			int maxPastDelta = cbConf.getInt("cloudbase.master.tabletserver.maxPastDelta", 30) * 1000;
			int maxFutureDelta = cbConf.getInt("cloudbase.master.tabletserver.maxFutureDelta", 1) * 1000 * -1;

			if((delta > maxPastDelta || delta < maxFutureDelta) && tServer.getState() == TabletServerState.ONLINE){
				log.warn("Tablet server "+tServer.getName()+" time delta too large.  delta = "+(delta/1000.0)+" secs   # extents = "+extents.size()+".");

				ArrayList<KeyExtent> extentsCopy = new ArrayList<KeyExtent>(extents);
				
				for (KeyExtent extent: extentsCopy) {
					TabletStatus tabStat = tabletsManager.getTablet(extent);
					log.warn("Tablet server with bad time has tablet "+extent+" unloading it (state = "+(tabStat == null ? null : tabStat.getState())+").");

					//set the tablets state to ASSIGNED_BAD_SERVER... that way when the 
					//unload completes, the tablet can be marked UNASSIGNED
					
					boolean unload = false;
					
					if(tabStat == null){
						tabletsManager.addTablet(extent, TabletState.ASSIGNED_BAD_SERVER, tServer);
						unload = true;
					}else{
						if(tabStat.getState() == TabletState.ASSIGNED){
							if(tabStat.getTabletServer().getMasterAddress().equals(serverName)){
								tabStat.setState(TabletState.ASSIGNED_BAD_SERVER);
							}else{
								//tablet is multiply assigned, do not want to set it to ASSIGNED_BAD_SERVER
								//just unload and let the other tabletserver continue to host it
								log.warn("already have an assignment for tablet " + extent + ": " + tabStat.getTabletServer().getMasterAddress());
							}
							
							unload = true;
						}else if(tabStat.getState() != TabletState.MIGRATING){
							tabStat.setState(TabletState.ASSIGNED_BAD_SERVER);
							unload = true;
						}
					}

					if(unload){
						tServer.unloadTablet(extent, true);
					}
				}

				tServer.setState(TabletServerState.BAD_TIME);

				// remove from dir so we know when it's back up
				//removeServerLock(tServer.getMasterAddress());

				tServer.close();

				return true;
			}
			
			return false;
		}

		public void reportTabletUnloaded(AuthInfo credentials, KeyExtent extent)
		throws TException {
			
			try {
				if(!authenticator.hasSystemPermission(credentials, credentials.user, SystemPermission.SYSTEM))
				{
					log.warn("Got tabletUnload message from user: "+credentials.user);
					throw new TException("User "+credentials.user+" not authorized");
				}
			} catch (CBSecurityException e) {
				log.warn("Got tabletUnload message from unauthenticatable user: "+e.user);
				throw new TException("User "+credentials.user+" not authenticated");
			}



			TabletStatus tabStat = tabletsManager.getTablet(extent);

			if(tabStat.getState() == TabletState.MIGRATING){
				finishMigration(extent, tabStat);
			}else if(tabStat.getState() == TabletState.DELETED) {
				log.debug("forgetting deleted and unloaded tablet " + tabStat.getExtent());
				tabletsManager.removeTablet(tabStat.getExtent());
			}else if(tabStat.getState() == TabletState.ASSIGNED_BAD_SERVER) {
				tabStat.setState(TabletState.UNASSIGNED);
			} else {
				log.error("Unexpected tablet unload message for extent: " + extent+" state "+tabStat.getState());
				if (tabStat.getState() == TabletState.ASSIGNED) {
					log.error("Tablet " + extent + " presently assigned to " + tabStat.getLocation());
				}
			}
		}

		private void finishMigration(KeyExtent extent, TabletStatus tabStat)
		{
			try {
				assignTablet(extent,tabStat.getDestTabletServer());//.assignTablet(extent, tabStat.getDirectory());
			} catch (TException ex) {
				log.warn("Failed to assign unloaded tablet: " + ex, ex);
				tabStat.setState(TabletState.UNASSIGNED);
				return;
			}
		}


		public void pong(AuthInfo credentials, String tServerName, TabletServerStatus status)
		throws TException {
			try {
				if(!authenticator.hasSystemPermission(credentials, credentials.user, SystemPermission.SYSTEM))
				{
					log.warn("Got pong from user: "+credentials.user);
					throw new TException("User "+credentials.user+" not authorized");
				}
			} catch (CBSecurityException e) {
				log.warn("Got pong from unauthenticatable user: "+e.user);
				throw new TException("User "+credentials.user+" not authenticated");
			}

			//get the time that this method was called on the proxy
			long invokeTime = tabletServiceQueuingProxy.getOriginalInvokeTime();

			if(invokeTime < 0){
				//looks like we are not being called through the proxy
				invokeTime = System.currentTimeMillis();
			}
			
			//the following loop does some sanity checks of what the tablet server is serving and 
			//reports any discrepancies with what the master thinks it is serving
			for (TabletRates tabletRates : status.tabletRates) {
				TabletStatus tablet = tabletsManager.getTablet(tabletRates.key);
				if(tablet == null){
					log.warn("tablet server "+tServerName+" is serving unknown tablet "+tabletRates.key+" (could be a split)");
				}else if(tablet.getState() != TabletState.ASSIGNED){
					if(tablet.getState() == TabletState.MIGRATING){
						if(tablet.getTabletServerName() == null || !tablet.getTabletServerName().equals(tServerName)){
							log.error("tablet server "+tServerName+" is serving tablet "+tabletRates.key+" that is in the "+tablet.getState()+" state from "+tablet.getTabletServerName());
						}
					}else{
						log.error("tablet server "+tServerName+" is serving tablet "+tabletRates.key+" that is in the "+tablet.getState()+" state");
					}
				}else if(tablet.getState() == TabletState.ASSIGNED && (tablet.getTabletServerName() == null || !tablet.getTabletServerName().equals(tServerName))){
					log.error("tablet server "+tServerName+" is serving tablet "+tabletRates.key+" that is assigned to "+tablet.getTabletServerName());
				}
			}

			updateFields(tServerName, status);
	
			TabletServerConnection tServer = tabletServers.getTabletServers().get(tServerName);
			if(tServer != null){
				isTabletServerTimeDeltaTooLarge(tServerName, tabletsManager.getTablets(tServer).keySet(), status.tabletServerTime, invokeTime, tServer);
			}
		}

		public void reportTabletStatus(AuthInfo credentials, String serverName, KeyExtent extent, int status) throws TException {
			try {
				if(!authenticator.hasSystemPermission(credentials, credentials.user, SystemPermission.SYSTEM))
				{
					log.warn("Got reportTabletStatus message from user: "+credentials.user);
					throw new TException("User "+credentials.user+" not authorized");
				}
			} catch (CBSecurityException e) {
				log.warn("Got reportTabletStatus message from unauthenticatable user: "+e.user);
				throw new TException("User "+credentials.user+" not authenticated");
			}

			if(tabletsManager.getTablet(extent) == null){
				log.error("Recieved tablet status about unknown extent "+extent+" from "+serverName+"  status = "+status);
				return;
			}

			log.debug(serverName + " reports extent " + extent);
			switch (status) {
			case CBConstants.MSG_TS_REPORT_TABLET_LOADED:

				log.debug("Got TABLET_LOADED success for  " + extent + " from "+ serverName);

				TabletStatus tabStat = tabletsManager.getTablet(extent);

				switch(tabStat.getState()){
				case ASSIGNED:
					tabStat.setOnline(true);
					break;
				default:
					log.error("Loaded tablet was in unexpected state : "+tabStat.getState()+" "+extent+" from "+serverName);
				}

				break;			
			case CBConstants.MSG_TS_ASSIGNMENT_FAILURE:
				if (tabletsManager.getIndex(TabletState.ASSIGNED).containsKey(extent)) {
					// TODO should we really ignore this or correct the
					// master's information?
					log.warn("online tablet reported as assignment failure: " + extent + ". ignoring."+" (from "+serverName+")");

				} else if (tabletsManager.getTablet(extent).getState() == TabletState.UNASSIGNED) {

					log.warn("assignment failure: marking tablet " + extent + " for reassignment"+" (from "+serverName+")");

					//tabletsManager.getTablet(extent).setCyclesUnassigned(0);
				}
				break;
			case CBConstants.MSG_TS_REPORT_METADATA_TABLET:

				log.debug("MSG_TS_REPORT_METADATA_TABLET : "+extent+" unassignedTablets : "+tabletsManager.getIndex(TabletState.UNASSIGNED).keySet());

				if(extent.getTableName().toString().equals(CBConstants.METADATA_TABLE_NAME)){
					TabletStatus mdTabStat = tabletsManager.getTablet(extent);
					TabletServerConnection sender = tabletServers.getIndex(TabletServerState.ONLINE).get(serverName);

					if (sender == null) {
						log.error("MetaData tablet reported from disconnected TS");
						return;
					}

					switch(mdTabStat.getState()){
					case ASSIGNED:
						TabletServerConnection tServer = mdTabStat.getTabletServer();
						if (tServer != null && tServer != sender) {
							log.error("already have an assignment for tablet " + extent+" from "+serverName);
							tServer.unloadTablet(extent, false); // may lose data
						}
						break;
					default:
						log.error("Loaded tablet was in unexpected state : "+mdTabStat.getState()+" "+extent);
					return;
					}
				}else{
					log.error(" Got MSG_TS_REPORT_METADATA_TABLET for non metadata tablet "+extent+" from "+serverName);
				}

				break;
			case CBConstants.MSG_TS_REPORT_TABLET_UNLOAD_FAILURE_CLOSED :
			case CBConstants.MSG_TS_REPORT_TABLET_UNLOAD_FAILURE_NOT_SERVING :
			{
				log.warn("Recieved unload failure "+status+" for "+extent+"  from "+serverName);

				TabletStatus mdTabStat = tabletsManager.getTablet(extent);
				switch(mdTabStat.getState()){
				case MIGRATING:
					//a split might have occurred, don't migrate it... 
					if(serverName.equals(mdTabStat.getTabletServerName())){
						mdTabStat.setAssigned(mdTabStat.getTabletServer());
						mdTabStat.setOnline(false);
					}else{
						log.error("Tablet server ("+serverName+") who is not serving tablet, reported unload not serving or closed failure for "+extent+", "+mdTabStat.getTabletServerName()+" is serving it");
					}
					break;
				case DELETED:
					//nothing to do
					break;
				default:
					log.error("Recieved unload failure "+status+" for "+extent+"  from "+serverName+" state "+mdTabStat.getState()+" taking no action");
				}
				break;
			}
			case CBConstants.MSG_TS_REPORT_TABLET_UNLOAD_FAILURE_MAJC :
			{
				TabletStatus mdTabStat = tabletsManager.getTablet(extent);

				log.debug("Recieved MSG_TS_REPORT_TABLET_UNLOAD_FAILURE_MAJC for "+extent+" from "+serverName+" state "+mdTabStat.getState());

				switch(mdTabStat.getState()){
				case MIGRATING:
					if(serverName.equals(mdTabStat.getTabletServerName())){
						mdTabStat.setAssigned(mdTabStat.getTabletServer());
						mdTabStat.setOnline(true);
					}else{
						log.error("Tablet server ("+serverName+") who is not serving tablet, reported unload majc failure for "+extent+", "+mdTabStat.getTabletServerName()+" is serving it");
					}
					break;
				case DELETED:
					//try asking it to unload again
					if(!serverName.equals(mdTabStat.getTabletServerName())){
						log.error("Tablet server ("+serverName+") who is not serving tablet, reported unload majc failure for "+extent+", "+mdTabStat.getTabletServerName()+" is serving it");
					}else{
						//try asking tablet server to unload again
						mdTabStat.getTabletServer().unloadTablet(extent, false);
					}
					break;
				default:
					log.error("Recieved MSG_TS_REPORT_TABLET_UNLOAD_FAILURE_MAJC for "+extent+" from "+serverName+" state "+mdTabStat.getState()+" taking no action");
				}

				break;
			}
			case CBConstants.MSG_TS_REPORT_TABLET_UNLOAD_ERROR :
			{
				TabletStatus mdTabStat = tabletsManager.getTablet(extent);

				log.error("Recieved MSG_TS_REPORT_TABLET_UNLOAD_ERROR for "+extent+"  from "+serverName+" state "+mdTabStat.getState());

				switch(mdTabStat.getState()){
				case MIGRATING:
					if(serverName.equals(mdTabStat.getTabletServerName())){
						//set it assigned to original server
						mdTabStat.setAssigned(mdTabStat.getTabletServer());
						mdTabStat.setOnline(false);
					}else{
						log.error("Tablet server ("+serverName+") who is not serving tablet, reported unload error for "+extent+", "+mdTabStat.getTabletServerName()+" is serving it");
					}
					break;
				}

				break;
			}
			default:
				log.error("Recieved unknown tablet status msg "+status+" for "+extent+"  from "+serverName);

			}	

		}

		public void reportSplitExtent(AuthInfo credentials, String who, TabletSplit split)
		throws TException {
			try {
				if(!authenticator.hasSystemPermission(credentials, credentials.user, SystemPermission.SYSTEM))
				{
					log.warn("Got reportSplitExtent message from user: "+credentials.user);
					throw new TException("User "+credentials.user+" not authorized");
				}
			} catch (CBSecurityException e) {
				log.warn("Got reportSplitExtent message from unauthenticatable user: "+e.user);
				throw new TException("User "+credentials.user+" not authenticated");
			}

			TabletServerConnection tServer = tabletsManager.getAssignedServer(split.oldTablet);
			if (tServer == null) {
				// tablet isn't assigned, maybe it's in the middle of a migration:
				TabletStatus status = tabletsManager.getTablet(split.oldTablet);
				if (status == null) {
					log.error("Tablet " + split.oldTablet + " is completely unknown!");
					return;
				}
				if (status.getState() == TabletState.MIGRATING) {
					// Well, I guess it didn't get migrated... so let's fix it 
					status.setState(TabletState.ASSIGNED);
					tServer = tabletServers.getIndex(TabletServerState.ONLINE).get(who);
					if (tServer == null) {
						log.error("Giving up: master does not know tablet server " + who);
						return;
					}
				} else {
					log.error("Split from a tablet that is not assigned or migrating: " + split.oldTablet);
					if (status != null) {
						log.error("Tablet is currently: " + status.getState());
					} 
					return;
				}
			}

			//tabletStats.remove(split.oldTablet);
			loadBalancer.tabletDeleted(split.oldTablet);
			tServer.status = null;

			//unregisterTablet(split.oldTablet, tServer, TabletState.SPLIT);
			tabletsManager.getTablet(split.oldTablet).setState(TabletState.SPLIT);
			tabletsManager.removeTablet(split.oldTablet);

			for (KeyExtentLocation kel : split.keyExtentLocations) {
				tabletsManager.addTablet(kel.extent, new Text(kel.location), TabletState.ASSIGNED, tServer);
				registerTablet(kel.extent, tServer, false);
				tabletsManager.getTablet(kel.extent).setOnline(true);
			}

			log.debug("got new split extent from server " + tServer.getMasterAddress() + ": "+ split.oldTablet.toString() + " "+ split.keyExtentLocations);
		}

	}

	class MasterClientServiceHandler implements MasterClientService.Iface
	{
		public void createTable(AuthInfo credentials, String table, List<byte[]> splitPoints) 
		throws TableCreationException, TException, ThriftSecurityException
		{
            splitPoints = uniqOrderedSplitPoints(splitPoints);

			try {
				// user's username and password is checked inside authenticator
				// make sure user has permission to create tables
				if (!authenticator.hasSystemPermission(credentials, credentials.user, SystemPermission.CREATE_TABLE))
					throw new CBSecurityException(credentials.user, false);
			} catch (CBSecurityException e) {
				throw new ThriftSecurityException(e.user, e.baduserpass);
			}

			if (table.contains(";") || table.contains(">"))
			{
				String why = "Table names cannot contain \";\" or \">\" characters";
				log.warn(why);
					throw new TableCreationException(why, false);
			}

			if(table.compareTo(CBConstants.METADATA_TABLE_NAME) <= 0)
			{
				String why = "Table names cannot be <= "+CBConstants.METADATA_TABLE_NAME;
				log.warn(why);
				throw new TableCreationException(why, false);
			}

			ArrayList<KeyExtent> newly_created_tablets = new ArrayList<KeyExtent>();

			waitForMetadataTableReady();

			synchronized(deletedTables) {
				
				if(deletedTables.contains(table))
					throw new TableCreationException(table+" (pending delete)", true);
				
		        try {
		            if (tabletsManager.getTablets(table) != null)
			            throw new TableCreationException(table, true);
		        } catch (TableNotFoundException e) {
					// this is okay; this is what we want
		        }

                log.info(String.format("creating table %s with %d splitPoints", table, splitPoints.size()));
                
				try {
					if (splitPoints.isEmpty())
					{
						KeyExtent extent = new KeyExtent(new Text(table), null, null);
						MetadataTable.updateTabletDirectory(extent, CBConstants.DEFAULT_TABLET_LOCATION, systemCredentials());
						MetadataTable.updateTabletPrevEndRow(extent, systemCredentials());
						KeyExtent newExtent = new KeyExtent(new Text(table), null, null);

						newly_created_tablets.add(newExtent);
						tabletsManager.addTablet(newExtent, new Text(CBConstants.DEFAULT_TABLET_LOCATION), TabletState.UNASSIGNED);
					}
					else
					{
						Text tableName = new Text(table);
						Text previous = null;
						splitPoints.add(null);
						for (byte[] splitpoint : splitPoints)
						{
							Text splitpointText = null;
							String sstables = CBConstants.DEFAULT_TABLET_LOCATION;
							if (splitpoint != null)
							{
								splitpointText = new Text(splitpoint);
								sstables = Encoding.encodeDirectoryName(splitpointText);
							}
							KeyExtent local_newExtent = new KeyExtent(tableName, splitpointText, previous);
	
							fs.mkdirs(new Path(CBConstants.TABLES_DIR + "/" + table + sstables));
	
							MetadataTable.updateTabletDirectory(local_newExtent, sstables, systemCredentials());
							MetadataTable.updateTabletPrevEndRow(local_newExtent, systemCredentials());
	
							newly_created_tablets.add(local_newExtent);
							tabletsManager.addTablet(local_newExtent, new Text(sstables), TabletState.UNASSIGNED);
	
							previous = splitpointText;
						}
	
					}
					
				} catch (UnsupportedEncodingException e) {
					log.error(e.getMessage(), e);
					throw new TException(e);
				} catch (IOException e) {
					log.error(e.getMessage(), e);
					throw new TException(e);
				}
			}
		    
			// give all table permissions to the creator
			for(TablePermission permission:TablePermission.values())
			{
				try {
					authenticator.grantTablePermission(systemCredentials(), credentials.user, table, permission);
				} catch (CBSecurityException e) {
					log.error(e.getMessage(), e);
					throw new ThriftSecurityException(e.user, e.baduserpass);
				}
			}
			
			// wait for all new tablets to be assigned
			for (KeyExtent local_newExtent : newly_created_tablets)
			{
				while (!tabletsManager.getIndex(TabletState.ASSIGNED).containsKey(local_newExtent))
				{
					log.info("waiting on " + local_newExtent + " to be assigned ...");
					UtilWaitThread.sleep(100);
				}
			}
            log.info("table " + table + " assigned");

		}

		private void waitForMetadataTableReady()
		{
			while (!metadataTableOnline() || !haveScannedMetadataTable)
				UtilWaitThread.sleep(100);
		}

		public void deleteTable(AuthInfo credentials, String table)
		throws TableDeletionException, TException, ThriftSecurityException
		{
			try {
				// user's username and password is checked inside authenticator
				// make sure user has permission to delete tables
				if (!authenticator.hasSystemPermission(credentials, credentials.user, SystemPermission.DROP_TABLE) &&
						!authenticator.hasTablePermission(credentials, credentials.user, table, TablePermission.WRITE))
					throw new CBSecurityException(credentials.user, false);
			} catch (CBSecurityException e) {
				throw new ThriftSecurityException(e.user, e.baduserpass);
			}

			log.info("got delete request for table " + table);

			// unassign tablets
			synchronized(deletedTables) {
				if (table.equals(CBConstants.METADATA_TABLE_NAME))
					throw new TableDeletionException(CBConstants.METADATA_TABLE_NAME, true);
				if (!getTables(systemCredentials()).contains(table))
					throw new TableDeletionException(table, false);
				
				deletedTables.add(table);
				while (deletedTables.contains(table.toString())) {
				    try {
                        deletedTables.wait();
                    } catch (InterruptedException e) {
                    }
				}
			}
		}

		public void ping(AuthInfo credentials) throws TException
		{
			// anybody can call this; no authentication check
			log.info("Master reports: I just got pinged!");
			System.out.println("I just got pinged!");
		}

		public void shutdown(AuthInfo credentials, boolean stopTabletServers)
		throws TException, ThriftSecurityException
		{
			try {
				// user's username and password is checked inside authenticator
				// make sure user has permission to stop the system
				if (!authenticator.hasSystemPermission(credentials, credentials.user, SystemPermission.SYSTEM))
					throw new CBSecurityException(credentials.user, false);
			} catch (CBSecurityException e) {
				throw new ThriftSecurityException(e.user, e.baduserpass);
			}

			if (!stopTabletServers) {
				allStopRequested = true;
				return;
			}

			tServersStopRequested = true;

			for(shutdownStage = 0; shutdownStage < 3; shutdownStage++) {
				// signal that shutdown messages be sent out
				tServersStopRequested = true;

				// wait for stage one shutdown confirmation
				while(!shutdownLevelComplete(shutdownStage)) {
					UtilWaitThread.sleep(1000);
				}
			}

			// signal the stop of our own threads
			allStopRequested = true;
			log.info("finished shutdown");
		}

		public void flush(AuthInfo credentials, String table)
		throws TException, ThriftSecurityException
		{
			try {
				// user's username and password is checked inside authenticator
				// make sure user has permission to stop the system
				if (!authenticator.hasSystemPermission(credentials, credentials.user, SystemPermission.SYSTEM)
						&& !authenticator.hasTablePermission(credentials, credentials.user, table, TablePermission.WRITE))
					throw new CBSecurityException(credentials.user, false);
			} catch (CBSecurityException e) {
				throw new ThriftSecurityException(e.user, e.baduserpass);
			}

			flushRequest.add(table);
		}

		@Override
		public String getInstanceId() throws TException {
			return new HdfsZooInstance().getInstanceID();
		}

		@Override
		public String getRootTabletLocation() throws TException {
			return new HdfsZooInstance().getRootTabletLocation();
		}

		@Override
		public SortedSet<String> getTables(AuthInfo credentials)
		throws TException, ThriftSecurityException
		{
			// just make sure the user asking exists with the correct password
			if (!authenticateUser(systemCredentials(), credentials.user, credentials.password))
				throw new ThriftSecurityException(credentials.user, true);
			
			log.debug("Waiting for metadataTable to come online to list tables");
			waitForMetadataTableReady();

			try {
				SortedSet<String> tables = new TreeSet<String>();
				synchronized (tabletsManager)
				{
					for (KeyExtent extent : tabletsManager.getTablets().keySet())
                                        {
                                                log.debug(extent + " " + tabletsManager.getTablet(extent).getState());
						tables.add(extent.getTableName().toString());
                                        }
					return tables;
				}
			} catch (Throwable t) {
				log.error("Error getting tables", t);
				throw new TException(t);
			}
		}

		@Override
		public boolean setTableProperty(AuthInfo credentials, String table, String property, String value)
		throws TException, ThriftSecurityException
		{
			try {
				// user's username and password is checked inside authenticator
				// make sure user has permission to alter tables
				if (!authenticator.hasSystemPermission(credentials, credentials.user, SystemPermission.ALTER_TABLE))
					throw new CBSecurityException(credentials.user, false);
			} catch (CBSecurityException e) {
				throw new ThriftSecurityException(e.user, e.baduserpass);
			}

			try {
				TablePropUtil.setTableProperty(table, property, value);
				return true;
			} catch (KeeperException e) {
				return false;
			} catch (InterruptedException e) {
				return false;
			}
		}

		@Override
		public boolean removeTableProperty(AuthInfo credentials, String table, String property)
		throws TException, ThriftSecurityException
		{
			try {
				// user's username and password is checked inside authenticator
				// make sure user has permission to alter tables
				if (!authenticator.hasSystemPermission(credentials, credentials.user, SystemPermission.ALTER_TABLE))
					throw new CBSecurityException(credentials.user, false);
			} catch (CBSecurityException e) {
				throw new ThriftSecurityException(e.user, e.baduserpass);
			}

			try {
				TablePropUtil.removeTableProperty(table, property);
				return true;
			} catch (KeeperException e) {
				return false;
			} catch (InterruptedException e) {
				return false;
			}
		}

		@Override
		public boolean authenticateUser(AuthInfo credentials, String user, byte[] password)
		throws ThriftSecurityException
		{
			try {
				return authenticator.authenticateUser(credentials, user, password);
			} catch (CBSecurityException e) {
				throw new ThriftSecurityException(e.user, e.baduserpass);
			}
		}

		@Override
		public void changeAuthorizations(AuthInfo credentials, String user, Set<Short> authorizations)
		throws ThriftSecurityException, TException
		{
			try {
				authenticator.changeAuthorizations(credentials, user, authorizations);
			} catch (CBSecurityException e) {
				throw new ThriftSecurityException(e.user, e.baduserpass);
			}
		}

		@Override
		public void changePassword(AuthInfo credentials, String user, byte[] password)
		throws ThriftSecurityException, TException
		{
			try {
				authenticator.changePassword(credentials, user, password);
			} catch (CBSecurityException e) {
				throw new ThriftSecurityException(e.user, e.baduserpass);
			}
		}

		@Override
		public void createUser(AuthInfo credentials, String user, byte[] password, Set<Short> authorizations)
		throws ThriftSecurityException, TException
		{
			try {
				authenticator.createUser(credentials, user, password, authorizations);
			} catch (CBSecurityException e) {
				throw new ThriftSecurityException(e.user, e.baduserpass);
			}
		}

		@Override
		public void dropUser(AuthInfo credentials, String user)
		throws ThriftSecurityException, TException
		{
			try {
				authenticator.dropUser(credentials, user);
			} catch (CBSecurityException e) {
				throw new ThriftSecurityException(e.user, e.baduserpass);
			}
		}

		@Override
		public Set<Short> getUserAuthorizations(AuthInfo credentials, String user)
		throws ThriftSecurityException, TException
		{
			try {
				return authenticator.getUserAuthorizations(credentials, user);
			} catch (CBSecurityException e) {
				throw new ThriftSecurityException(e.user, e.baduserpass);
			}
		}

		@Override
		public void grantSystemPermission(AuthInfo credentials, String user, int permission)
		throws ThriftSecurityException, TException
		{
			try {
				authenticator.grantSystemPermission(credentials, user, SystemPermission.getPermissionById(permission));
			} catch (CBSecurityException e) {
				throw new ThriftSecurityException(e.user, e.baduserpass);
			}

		}

		@Override
		public void grantTablePermission(AuthInfo credentials, String user, String table, int permission)
		throws ThriftSecurityException, TException
		{
			try {
				authenticator.grantTablePermission(credentials, user, table, TablePermission.getPermissionById(permission));
			} catch (CBSecurityException e) {
				throw new ThriftSecurityException(e.user, e.baduserpass);
			}
		}

		@Override
		public void revokeSystemPermission(AuthInfo credentials, String user, int permission)
		throws ThriftSecurityException, TException
		{
			try {
				authenticator.revokeSystemPermission(credentials, user, SystemPermission.getPermissionById(permission));
			} catch (CBSecurityException e) {
				throw new ThriftSecurityException(e.user, e.baduserpass);
			}
		}

		@Override
		public void revokeTablePermission(AuthInfo credentials, String user, String table, int permission)
		throws ThriftSecurityException, TException
		{
			try {
				authenticator.revokeTablePermission(credentials, user, table, TablePermission.getPermissionById(permission));
			} catch (CBSecurityException e) {
				throw new ThriftSecurityException(e.user, e.baduserpass);
			}
		}

		@Override
		public boolean hasSystemPermission(AuthInfo credentials, String user, int sysPerm)
		throws ThriftSecurityException, TException
		{
			try {
				return authenticator.hasSystemPermission(credentials, user, SystemPermission.getPermissionById(sysPerm));
			} catch (CBSecurityException e) {
				throw new ThriftSecurityException(e.user, e.baduserpass);
			}
		}

		@Override
		public boolean hasTablePermission(AuthInfo credentials, String user, String table, int tblPerm)
		throws ThriftSecurityException, TException
		{
			try {
				return authenticator.hasTablePermission(credentials, user, table, TablePermission.getPermissionById(tblPerm));
			} catch (CBSecurityException e) {
				throw new ThriftSecurityException(e.user, e.baduserpass);
			}
		}

		@Override
		public Set<String> listUsers(AuthInfo credentials)
		throws TException, ThriftSecurityException
		{
			try {
				return authenticator.listUsers(credentials);
			} catch (CBSecurityException e) {
				throw new ThriftSecurityException(e.user, e.baduserpass);
			}
		}	
	};

	private static List<byte[]> uniqOrderedSplitPoints(List<byte[]> splitPoints) {
		Set<Text> points = new TreeSet<Text>();
		for (byte[] point : splitPoints)
			points.add(new Text(point));
		List<byte[]> result = new ArrayList<byte[]>();
		for (Text point : points)
			result.add(point.getBytes());
		return result;
	}


	/**
	 * manages tablet servers
	 * 
	 * tasks: - monitor the tservers directory in HDFS for tabletServers
	 * (instead of in Chubby) - on startup, poll tabletServers for currently
	 * assigned tablets (may be none) - determine if a tabletServer is
	 * available, assign tablets to it
	 * 
	 */

	class MasterMonitorServiceHandler implements MasterMonitorService.Iface
	{

		@Override
		public MasterMonitorInfo getMasterStats() throws TException {
			try {
				MasterMonitorInfo mmi = new MasterMonitorInfo();
				mmi.tServerInfo = new ArrayList<TabletServerStatus>();
				mmi.tableMap = getTableStats();
		        mmi.badTServers = new TreeMap<String, Short>();
				for (TabletServerConnection tsc : tabletServers.getIndex(TabletServerState.ONLINE).values()) {
					TabletServerStatus tss = tsc.getStatus();
					if (tss != null) {
						tss.name = tsc.getName();
						tss.URL = tsc.getMonitorUrl(tsc.getClientAddress());
						mmi.tServerInfo.add(tss);
					}
				}
		        for (TabletServerConnection tsc : tabletServers.getIndex(TabletServerState.BAD_TIME) .values()) {
		            mmi.badTServers.put(tsc.getName(), (short)tsc.getState().ordinal());
		        }
		        log.debug("Bad TServers = " + mmi.badTServers.size());
				return mmi;
			} catch (Throwable t) {
				log.error("Error in getTabletsStats", t);
				throw new TException(t);
			}
		}

	}

	private class TabletServerHandler implements Runnable {
		/**
		 * process messages received from tabletServers
		 * 
		 * @param msg
		 * @param tServer
		 */
		private Logger log = Logger.getLogger(TabletServerHandler.class.getName());


		private void reassignTablet(KeyExtent extent, TabletServerConnection current_server, TabletServerConnection new_server)
		throws IOException, TException {
			if(new_server == null) {
				log.warn("Reassign tablet " + extent + " requested for null tablet server");
				return;
			}
			if(current_server == null) {
				log.debug("Tablet " + extent + " currently unassigned -- simply assigning it");
				assignTablet(extent, new_server);
			} else {
				// tell the current server to migrate the tablet to the new server
				TabletStatus tabletM = tabletsManager.getTablet(extent);
				tabletM.getTabletServer().unloadTablet(extent, true);
				tabletM.setMigrating(new_server);
			}
		}

		private void assignTablets() {
			synchronized(tabletsManager) {
				SortedSet<KeyExtent> unassignedTablets = new TreeSet<KeyExtent>(tabletsManager.getIndex(TabletState.UNASSIGNED).keySet());

				for(KeyExtent ke : unassignedTablets) {
					log.debug(ke + " is unassigned.");

					TabletServerConnection server;
					try {
						server = (TabletServerConnection) loadBalancer.getServerForTablet(ke,tabletsManager.getTablets(), tabletServers.getIndex(TabletServerState.ONLINE).values());
					} catch (Exception e) {
						log.error("Load balancer failed "+e.getMessage(), e);
						return;
					}

					if(server == null) {
						log.info("waiting for servers to come online to assign tablets");
						return;
					}
					try {
						assignTablet(ke, server);
					} catch (Exception e) {
						log.error("Unexpected error assigning tablet: " + ke, e);
					}
				}
			}
		}

		private void balanceLoad() {
			if(!rootTabletScanned || tabletsManager.getIndex(TabletState.UNASSIGNED).size() > 0)
				return;

			Set<TabletMigration> migrations;

			synchronized(tabletsManager) {
				try{
					migrations = loadBalancer.getMigrations(tabletsManager.getTablets(), tabletServers.getIndex(TabletServerState.ONLINE).values());
				}catch(Exception e){
					log.error("Load balancer failed "+e.getMessage(), e);
					return;
				}

				if(migrations == null || migrations.size() == 0)
					return;

				for(TabletMigration migration : migrations) {
					// don't trust what the load balancer recommends

					TabletStatus ts = tabletsManager.getTablet(migration.tablet);
					if (ts == null) {
						log.warn("Load Balancer wants to move a tablet we are no longer managing: " + migration.tablet);
						continue;
					}

					if(ts.getState() != TabletState.ASSIGNED){
						log.debug("skipping migrating tablet in flux " + migration.tablet);
						continue;
					}

					if(ts.getDirectory() == null){
						continue;
					}

					if(ts.getTabletServer() == null){
						continue;
					}

					if(!ts.getTabletServer().getName().equals(migration.oldServer.getName())){
						log.warn("Load balancer wanted to migrate "+migration.tablet+" from "+migration.oldServer.getName()+" but its at "+ts.getTabletServer().getName());
						continue;
					}

					try {
						log.debug("migrating " + migration.tablet+" from "+migration.oldServer.getName()+" to "+migration.newServer.getName());
						reassignTablet(migration.tablet, (TabletServerConnection)migration.oldServer, (TabletServerConnection)migration.newServer);
					} catch (IOException e) {
						e.printStackTrace();
					} catch (TException e) {
						e.printStackTrace();
					}
				}
			}
		}



		/**
		 * finds tablet servers using the tabletservers directory in HDFS
		 * 
		 */
		public Set<String> discoverTabletServers() {

			String zPath = CBConstants.ZROOT_PATH+"/"+Cloudbase.getInstanceID()+CBConstants.ZTSERVERS_PATH;

			return new HashSet<String>(zooCache.getChildren(zPath));
		}

		public void connectToTabletServer(String address, ZooLock zooLock, TabletServerLockWatcher lockWatcher) {
			TabletServerConnection tServer;
			try {
				int port = cbConf.getInt("cloudbase.tabletserver.masterPort", CBConstants.TABLET_MASTER_PORT_DEFAULT);
				int timeout = CBConstants.MASTER_TABLETSERVER_CONNECTION_TIMEOUT;

				tServer = tabletServers.addTabletServer(AddressUtil.parseAddress(address, port), timeout, systemCredentials(), zooLock, lockWatcher);

				tServer.status.lastContact = System.currentTimeMillis();

				log.info("Master: got new tablet server: "+ tServer.getMasterAddress());
			}
			catch (TException e) {
				log.warn("Master: error trying to connect to new tablet server: "	+ address + ": " + e.getMessage());
				return;
			}


			try {
				tServer.sendTabletList();
			} catch (TException e) {
				log.error("Master: error issuing extent report request to server "
						+ tServer.getMasterAddress());
				log.error(e.toString());
			}

		}

		private List<String> connectToTabletServers(Set<String> newServerList) {
			List<String> doomed = new ArrayList<String>();
			for (String address : newServerList) {

				if (tabletServers.getIndex(TabletServerState.BAD_TIME).containsKey(address)){
					continue;
				}


				TabletServerLockWatcher lockWatcher = new TabletServerLockWatcher();
				ZooLock zooLock = null;
				try {
					zooLock = new ZooLock(getTserverZooPath(address));
				} catch (IOException e) {
					log.warn("Failed to create ZooLock for "+address, e);
					continue;
				}

				zooLock.lockAsync(lockWatcher, "master".getBytes());

				if(!lockWatcher.gotLock && lockWatcher.failureException == null){
					connectToTabletServer(address, zooLock, lockWatcher);
				}else if(lockWatcher.gotLock){
					doomed.add(address);
					try {
						zooLock.unlock();
					} catch (Exception e) {
						log.warn("Failed to unlock "+address, e);
					} 
					log.info("Ignoring tablet server "+address+" it does not hold lock.");        
				}else if(lockWatcher.failureException != null){
					log.warn("Attempt to lock "+address+" failed ", lockWatcher.failureException);
				}
			}

			return doomed;
		}

		private void takeServerOffline(TabletServerConnection tServer) {
			log.warn("Master: unable to reach tablet server at "+ tServer.getMasterAddress());
			log.warn("marking tablets as unassigned ...");

			TreeMap<KeyExtent, TabletStatus> indexCopy = new TreeMap<KeyExtent, TabletStatus>();

			indexCopy.putAll(tabletsManager.getIndex(TabletState.ASSIGNED));
			indexCopy.putAll(tabletsManager.getIndex(TabletState.MIGRATING));

			for (Entry<KeyExtent, TabletStatus> entry : indexCopy.entrySet()) {
				// find all tablets assigned to the bad tserver

				TabletServerConnection tabletsTserver = null;

				tabletsTserver = entry.getValue().getTabletServer();

				if(tabletsTserver.compareTo(tServer)==0) {
					entry.getValue().setState(TabletState.UNASSIGNED);

					// TODO could also send a shutdown message in
					// case the problem is in communication from
					// tserver to master
				}
			}

			tServer.setState(TabletServerState.UNRESPONSIVE);
			try {
				tServer.close();
			} catch (TException e) {
				log.warn(e.getMessage(),e);
			}
		}

		/**
		 * main thread does all master's management jobs
		 * 
		 */
		public void run() {
			log.info("starting tabletserver handler ...");

			while (!allStopRequested) {
				long loop_start_time = System.currentTimeMillis();

				try {
					loopOnce();
				} catch (Throwable t) {
					log.error("Uncaught exception in TabletServerHandler", t);
				}

				long loop_remaining_time = CBConstants.MIN_MASTER_LOOP_TIME	- (System.currentTimeMillis() - loop_start_time);
				if (loop_remaining_time > 0) {
					UtilWaitThread.sleep(loop_remaining_time);
				}


			}
		}

		private void loopOnce() {
			maybeTellServersToShutdown();

			Set<String> newServerList = checkServerLocks();

			pingServers();

			handleDownServers();

			tabletServiceQueuingProxy.executeQueuedMethodCalls();

			assignTablets();

			balanceLoad();

			processDeletedTables();

			List<String> doomed = connectToTabletServers(newServerList);

			if (doomed.size() > 0) {
				ZooKeeper session = ZooSession.getSession();
				for (String server : doomed) {
					try {
						session.delete(getTserverZooPath(server), -1);    
					} catch (Exception ex) {
						log.warn("Unexpected error deleting lock directory for " + server, ex);
					}
				}
			}

			initiateTableFlushes();
		}

		private void initiateTableFlushes() {
			if(flushRequest.size() > 0){

				Set<String> tablesToFlush;

				synchronized (flushRequest) {
					tablesToFlush = new HashSet<String>(flushRequest);
					flushRequest.clear();
				}

				for (Entry<String, TabletServerConnection> e : tabletServers.getIndex(TabletServerState.ONLINE).entrySet()) {
					TabletServerConnection server = e.getValue();
					try {
						server.flush(tablesToFlush);
					} catch (TException ex) {
						log.error(ex.toString());
					}
				}
			}
		}

		private void handleDownServers() {
			// System.out.println("removing unresponsive servers ...");
			// handle unresponsive servers

			List<TabletServerConnection> downServers = new ArrayList<TabletServerConnection>(tabletServers.getIndex(TabletServerState.UNRESPONSIVE).values());

			for (TabletServerConnection i : downServers) {
				log.warn("handling down server: "+ i.getMasterAddress());
				i.setState(TabletServerState.DOWN);
			}

			downServers = null;
		}

		private void pingServers() {
			// handle each tablet server
			List<TabletServerConnection> onlineServers = new ArrayList<TabletServerConnection>(tabletServers.getIndex(TabletServerState.ONLINE).values());

			for (TabletServerConnection tServer : onlineServers) {
				try {
					tServer.ping();
				} catch (TException e) {
					// don't reconnect if we're shutting down after stage 1, because that tabletserver has probably already MinC and closed it's tablets
					if(shutdownStage > 1)
						continue;
					log.error("Master: problem pinging server, " + tServer.getMasterAddress() + " trying to reconnect ... ");

					try {
						tServer.connect(CBConstants.MASTER_TABLETSERVER_CONNECTION_TIMEOUT);
					} catch (TException e3) { // server is down
						log.error(e3.toString());
					} 
				}


			}
		}

		private Set<String> checkServerLocks() {
			Set<String> newServerList = discoverTabletServers();

			for (TabletServerConnection tServer : tabletServers.getTabletServers().values()){
				if(tServer.getState() == TabletServerState.DOWN || tServer.getState() == TabletServerState.BAD_TIME){
					continue;
				}
				// Great... master holds the lock on a tServer: take offline and remove the lock
				if (tServer.lockWatcher.gotLock) {
					takeServerOffline(tServer);
					try {
						tServer.zooLock.unlock();
					} catch (Exception e) {
						log.warn("Unexpected error releasing lock for " + tServer.getName(), e);
					}
				} else if(tServer.lockWatcher.failureException != null){
					log.warn("Attempt to lock "+tServer.getName()+" failed, retrying ... ", tServer.lockWatcher.failureException);

					try {
						//delete current master lock attempt if it exist
						tServer.zooLock.unlock();
					} catch (Exception e1) {
						log.warn("Failed to remove current lock attempt on "+tServer.getName()+" ", e1);
					}

					try {
						ZooLock tLock = new ZooLock(getTserverZooPath(tServer.getName()));
						TabletServerLockWatcher lockWatcher = new TabletServerLockWatcher();
						tLock.lockAsync(lockWatcher, "master".getBytes());

						tServer.zooLock = tLock;
						tServer.lockWatcher = lockWatcher;

					} catch (IOException e) {
						log.error("Failed to create new asyn lock for "+tServer.getName()+" ", e);
					}
				}
				newServerList.remove(tServer.getMasterAddress());
			}
			return newServerList;
		}

		private void maybeTellServersToShutdown() {
			if(tServersStopRequested) {
				int[] shutdownMessages = new int[3];
				shutdownMessages[0] = CBConstants.SHUTDOWN_STAGE_1;
				shutdownMessages[1] = CBConstants.SHUTDOWN_STAGE_2;
				shutdownMessages[2] = CBConstants.SHUTDOWN_STAGE_3;

				for (Entry<String, TabletServerConnection> e : tabletServers.getIndex(TabletServerState.ONLINE).entrySet()) {
					if (shutdownStage >= shutdownMessages.length)
						break;
					if(shutdownStages.containsKey(e.getValue())&& shutdownStages.get(e.getValue()) >= shutdownStage)
						continue;
					log.info("sending shutdown message for stage " + (shutdownStage+1) + " to " + e.getKey());
					TabletServerConnection server = e.getValue();
					try {
						// I'm not too familiar with the shutdown code, but we may want to bring back the operation of
						// letting a tserver stay down if it's connection is broken after stage 1.
						server.shutdown(shutdownMessages[shutdownStage]);
					} catch (TException ex) {
						log.error(ex);
					}
				}
			}
		}

		private void processDeletedTables() {

			if(deletedTables.size() == 0)
				return;

			HashSet<String> completelyDeletedTables;
			synchronized(deletedTables) {
				completelyDeletedTables = new HashSet<String>(deletedTables);
			}

			// step one: set state to deleted
			SortedMap<KeyExtent,TabletStatus> tablets = tabletsManager.getTablets();
			for(TabletStatus tablet : tablets.values()) {
				// only unload assigned tablets
				if(deletedTables.contains(tablet.getExtent().getTableName().toString())) {
					if(tablet.getState().equals(TabletState.ASSIGNED)) {
						log.debug("setting tablet " + tablet.getExtent() + " as deleted");
						tablet.setState(TabletState.DELETED);
						//MetadataTable.deleteTablet(tablet.getExtent());
					}
					else { // some tablet of this table is migrating or something
						String table = tablet.getExtent().getTableName().toString();
						if(completelyDeletedTables.contains(table))
							completelyDeletedTables.remove(table);
					}
				}
			}

			// step two: unload any deleted tablets
			for(TabletStatus tablet : tabletsManager.getIndex(TabletState.DELETED).values()) { 
				try {
					log.debug("unloading deleted tablet " + tablet.getExtent());
					tablet.getTabletServer().unloadTablet(tablet.getExtent(), false);
					//deletedTablets.add(tablet.getExtent());
				} catch (TException e) {
					log.error("failed to unload tablet " + tablet.getExtent());
				}	
			}

			for(String table : completelyDeletedTables) {
				try {
					MetadataTable.deleteTable(table, systemCredentials());
				} catch (Exception e) {
					log.error("error deleting " + table + " from metadata table", e);
				}

				try{
					String zPath = CBConstants.ZROOT_PATH+"/"+Cloudbase.getInstanceID()+CBConstants.ZTABLE_CONF_PATH+"/"+table;
					log.debug("Deleting table "+table+"'s info in zookeeper "+zPath);
					ZooUtil.recursiveDelete(zPath);
				}catch (Exception e) {
					log.error("error deleting table config for " + table + " from zookeeper table", e);
				}
				
				try {
					authenticator.deleteTable(systemCredentials(), table);
				} catch (CBSecurityException e) {
					log.error(e.getMessage(), e);
				}
			}

			// step three: forget tablets
			for(Entry<KeyExtent, TabletStatus> entry : tablets.entrySet()) {
				// only unload assigned tablets
				TabletStatus tablet = entry.getValue();
				if(tablet.getState().equals(TabletState.DELETED) && 
						completelyDeletedTables.contains(tablet.getExtent().getTableName())) {
					tabletsManager.removeTablet(entry.getKey());
				}
			}

			// delete stuff from HDFS
			for (String table : completelyDeletedTables) {
				try {
					fs.delete(new Path(CBConstants.TABLES_DIR, table), true);
				} catch (IOException e) {
					log.error("Unable to remove deleted table directory", e);
				}
			}

			// step four: forget tablets that are completely deleted
			synchronized (deletedTables) {
				deletedTables.removeAll(completelyDeletedTables);
				deletedTables.notifyAll();
			}
		}	
	}

	private HashSet<String> deletedTables;

	private TabletsManager tabletsManager;

	// tablet server address -> tabletserverinfo object
	TabletServerManager tabletServers;

	//private LinkedList<TabletServerConnection> downServers;

	private volatile boolean allStopRequested;

	private volatile boolean tServersStopRequested;

	private Map<TabletServerConnection, Integer> shutdownStages = Collections.synchronizedMap(new HashMap<TabletServerConnection,Integer>());

	private static Configuration conf;

	private static FileSystem fs;

	private String hostname;

	private volatile int shutdownStage;

	private static AuthInfo systemCredentials_ = null;
	private static synchronized AuthInfo systemCredentials() {
	    if (systemCredentials_ == null)
	        systemCredentials_ = new AuthInfo(ZooConstants.SYSTEM_USERNAME, ZooConstants.SYSTEM_PASSWORD);
	    return systemCredentials_;
	}

	private boolean safeMode;

	private CBConfiguration cbConf;

	private List<String> flushRequest;

	private LoadBalancer loadBalancer;

	private boolean rootTabletScanned;

	private LocationUpdateManager locationUpdateManager;

	private LocationUpdateManager metadataLocationUpdateManager;

	private QueueingProxy tabletServiceQueuingProxy;

	private ZooCache zooCache;

	private Authenticator authenticator;

	//private QueueingProxy clientServiceQueuingProxy;
	private TServer masterTabletService = null;
	private TServer masterClientService = null;

	private ZooLock masterLock;

	private boolean haveScannedMetadataTable = false;

	public Master() throws IOException {
		// do this to read in configuration information
		// means we have to start master using the Hadoop script
		zooCache = new ZooCache();
		authenticator = new ZKAuthenticator();

		log.info("Version "+CBConstants.VERSION);
		log.info("Instance "+Cloudbase.getInstanceID());

		cbConf = CBConfiguration.getInstance();

		fs = FileSystem.get(Master.conf);
		hostname = InetAddress.getLocalHost().getHostAddress();

		allStopRequested = false;
		tServersStopRequested = false;


		tabletsManager = new TabletsManager(Collections.singletonList(CBConstants.METADATA_TABLE_NAME));

		tabletServiceQueuingProxy = new QueueingProxy(MasterTabletService.Iface.class, new MasterTabletServiceHandler(), false);

		tabletServers = new TabletServerManager();

		deletedTables = new HashSet<String>();

		flushRequest = Collections.synchronizedList(new ArrayList<String>());

		String balancerClazz = cbConf.get("cloudbase.master.balancer", SimpleLoadBalancer.class.getName());
		loadBalancer = null;

		try {
			loadBalancer = (LoadBalancer) Class.forName(balancerClazz).newInstance();
			log.info("Loaded balancer : "+balancerClazz);
		} catch (ClassNotFoundException e) {
			log.warn("Failed to load balancer", e);
		} catch (InstantiationException e) {
			log.warn("Failed to load balancer", e);
		} catch (IllegalAccessException e) {
			log.warn("Failed to load balancer", e);
		} catch (ClassCastException e){
			log.warn("Failed to load balancer", e);
		}

		if(loadBalancer == null){
			loadBalancer = new SimpleLoadBalancer();
		}

		safeMode = false;
		rootTabletScanned = false;
		shutdownStage = -1;
	}

	public void updateFields(String serverName, TabletServerStatus in) {
		TabletServerConnection tServer = tabletServers.getIndex(TabletServerState.ONLINE).get(serverName);
		if (tServer == null) {
			log.warn("Status update for tablet server we don't know: " + serverName);
			return;
		}
		tServer.updateStats(in);

		loadBalancer.tabletServerStatusUpdated(tServer);
	}

	private boolean metadataTableOnline() {
		if(!rootTabletScanned)
			return false;

		int numAssigned = tabletsManager.getIndex(CBConstants.METADATA_TABLE_NAME, TabletState.ASSIGNED).size();
		int total;
		try {
			total = tabletsManager.getTablets(CBConstants.METADATA_TABLE_NAME).size();
		} catch (TableNotFoundException e) {
			// METADATA tablet is not online
			return false;
		}

		return (numAssigned == total);
	}

	private void startMasterTabletService() {
		MasterTabletService.Iface handler = (MasterTabletService.Iface) tabletServiceQueuingProxy.getProxy();
		MasterTabletService.Processor processor = new MasterTabletService.Processor(handler);
		TServerTransport serverTransport = null;
		int port= CBConfiguration.getInstance().getInt("cloudbase.master.tabletPort", 11223);
		try {
			serverTransport = TServerUtils.openPort(port);
		} catch(Exception ex) {
			throw new RuntimeException(ex);
		}
		masterTabletService = TServerUtils.startTServer(processor, serverTransport, "Master Tablet Service", -1);
	}

	private void masterClientServiceHandler() {
		log.info("---- Starting the master client service...");

		MasterClientService.Iface handler = new MasterClientServiceHandler();
		MasterClientService.Processor processor = new MasterClientService.Processor(handler);
		TServerTransport serverTransport = null;
		int port = CBConfiguration.getInstance().getInt("cloudbase.master.clientPort", CBConstants.MASTER_CLIENT_PORT_DEFAULT);
		try {
			serverTransport = TServerUtils.openPort(port);
		} catch(Exception ex) {
			throw new RuntimeException(ex);
		}
		masterClientService = TServerUtils.startTServer(processor, serverTransport, "Master Client Service Handler", -1);
	}

	private void masterMonitorServiceHandler() {
		log.info("---- Starting the monitor service...");

		MasterMonitorService.Iface handler = new MasterMonitorServiceHandler();
		MasterMonitorService.Processor processor = new MasterMonitorService.Processor(handler);
		TServerTransport serverTransport = null;
		int port = CBConfiguration.getInstance().getInt("cloudbase.master.monitor.port", CBConstants.MASTER_MONITOR_PORT_DEFAULT);
		try {
			serverTransport = TServerUtils.openPort(port);
		} catch(Exception ex) {
			throw new RuntimeException(ex);
		}
		TServerUtils.startTServer(processor, serverTransport, "Master Monitor Service Handler", -1);
	}

	private boolean rootTabletAssigned() {
		return tabletsManager.getIndex(TabletState.ASSIGNED).containsKey(CBConstants.ROOT_TABLET_EXTENT);
	}


	public void run() {

		obtainMasterLock();

		startLocationUpdateManagers();

		// Establish the new Thrift API:
		masterClientServiceHandler();

		masterMonitorServiceHandler();
		// Establish asych callback API for TabletServers:
		startMasterTabletService();

		TabletServerHandler tsHandler = new TabletServerHandler();

		// discover tablet servers manually here
		Set<String> servers = tsHandler.discoverTabletServers();
		tsHandler.connectToTabletServers(servers);

		Thread tsHandlerThread = new Daemon(new LoggingRunnable(log, tsHandler));
		tsHandlerThread.setName("Tablet Server Handler Thread");
		tsHandlerThread.start();

		// possibly discover unresponsive servers
		log.info("------] giving existing tablet servers a chance to report ...");

		long startTime = System.currentTimeMillis();

		// loop while timeout is alive
		while (tabletServers.getIndex(TabletServerState.NEW).size() > 0 && System.currentTimeMillis() - startTime < 60000) {
			UtilWaitThread.sleep(500);		
		}

		log.info("----------] " + tabletServers.getIndex(TabletServerState.ONLINE).size() + " tablet servers have checked in ("+tabletServers.getIndex(TabletServerState.NEW).size()+" did not check in)");

		if (tabletServers.getIndex(TabletServerState.NEW).size() > 0) {
			wackUnresponsiveTabletServers();
		}

		scanMetadataTable();

		while (tabletsManager.getIndex(TabletState.UNASSIGNED).size() > 0) {
			//log.debug("All tablets not assigned... Still have " + unassignedTablets.size());
			UtilWaitThread.sleep(1000);
		}
		log.info("All tablets assigned, master server is started");


		while (!allStopRequested) {
			UtilWaitThread.sleep(1000);
			//if (!metadataTableOnline()) {
			//	scanMetadataTable();
			//}
		}

		// wait for other threads to stop
		boolean threadsRunning = true;
		while(threadsRunning) {
			threadsRunning = false;
			if(tsHandlerThread.getState() == Thread.State.RUNNABLE)
				threadsRunning = true;
			UtilWaitThread.sleep(1000);
		}
		TServerUtils.stopTServer(masterTabletService);
		TServerUtils.stopTServer(masterClientService);
		log.info("all threads done.");

		for(TabletServerConnection tsc : tabletServers.getTabletServers().values()){
			
			if(tsc == null){
				continue;
			}
			
			try{
				if(tsc.zooLock != null){
					if(tsc.zooLock.tryToCancelAsyncLockOrUnlock()){
						log.debug("Deleted tablet server lock (or lock attempt) : "+tsc.getName());
					}
				}
			}catch (Exception e) {
				log.debug("Failed to release tablet server lock attmept",e);
			} 
		}
		
		try {
			log.info("Releasing master lock");
			masterLock.unlock();
		} catch (Exception e) {
			log.warn("Failed to release master lock",e);
		} 

	}

	private void wackUnresponsiveTabletServers() {
		/*wack anything in zookeeper that is not in the online state!
		 *this is a very strong check to make sure we kill all tablets
		 *servers that do not report back...
		 *
		 *The purpose of this method is to try to prevent the situation 
		 *where existing tablets servers are serving tablets but for
		 *some reason do not report back about what they are serving. If
		 *nothing is done this could lead to the dreaded multiple assignment
		 *problem.  By deleting the locks of tablet servers that did not
		 *report back, hopefully we can cause those tablet servers to kill 
		 *themselves and avoid disaster.
		 *
		 */ 


		String tsListPath = CBConstants.ZROOT_PATH+"/"+Cloudbase.getInstanceID()+CBConstants.ZTSERVERS_PATH;

		List<String> children = zooCache.getChildren(tsListPath);

		for (String tsName : children) {

			TabletServerConnection tsc = tabletServers.getTabletServers().get(tsName);

			if(tsc == null || tsc.getState() != TabletServerState.ONLINE){
				log.warn("Deleting lock of unresponsive tablet server "+tsName+" (state : "+(tsc == null ? "UKNOWN" : tsc.getState())+")");
			}

			boolean successful = false;

			while(!successful){
				try {
					if(tsc == null || tsc.getState() != TabletServerState.ONLINE){

						ZooKeeper zk = ZooSession.getSession();
						String tsPath = getTserverZooPath(tsName);

						if(zk.exists(tsPath, false) != null){
							List<String> locks = zk.getChildren(tsPath, false);

							Collections.sort(locks, Collections.reverseOrder());

							// Delete locks that aren't our locks
							Text masterData = new Text("master");
							for (String lock : locks) {
								String path = tsPath + "/" + lock;
								Text contents = new Text(zk.getData(path, null, null));
								if (!contents.equals(masterData)) {
									zk.delete(tsPath+"/"+lock, -1);
								}
							}

							// Delete the directory if it is empty
							try {
								zk.delete(tsPath, -1);
							} catch (KeeperException.NotEmptyException ex) {
								// ignore
							}
						}

						if(tsc != null){
							tsc.setState(TabletServerState.DOWN);
						}
					}

					successful = true;

				}catch (KeeperException e) {
					log.warn("Failed to delete lock of unresponsive tablet server "+tsName+" retrying.... ", e);
					UtilWaitThread.sleep(1000);
				} catch (InterruptedException e) {
					log.warn("Failed to delete lock of unresponsive tablet server "+tsName+" retrying.... ", e);
					UtilWaitThread.sleep(1000);
				}
			}
		}
	}

	private void obtainMasterLock() {

		if(masterLock != null){
			throw new IllegalStateException("Tried to obtain master lock while a master lock exists");
		}

		// mark our territory in zookeeper so people can find us
		try {
			log.info("trying to obtain master lock and set it to "+hostname);

			final String zMasterLoc = CBConstants.ZROOT_PATH+"/"+Cloudbase.getInstanceID()+CBConstants.ZMASTER_LOCK_NODE;

			masterLock = new ZooLock(zMasterLoc);
			LockWatcher masterLockWatcher = new LockWatcher(){

				public void lostLock(LockLossReason reason) {
					if(reason == LockLossReason.SESSION_EXPIRED){
						//TODO this is a convience thing, when we have master failover we must exit when
						//this happens... our current goal is not to support master failover but to just
						//ensure only one master is running.... so we do not want our one master to die
						//when the zookeeper session expires...
						log.warn("Master lock in zookeeper expired, trying to obtain it again...");
						masterLock = null;
						obtainMasterLock();
					}else{
						log.fatal("Master lock in zookeeper lost (reason = "+reason+"), exiting!");
						Runtime.getRuntime().halt(-1);
					}

				}
			};

			long current = System.currentTimeMillis();
			long waitTime = conf.getLong("cloudbase.zookeeper.sessionTimeout", 30000);
			while (System.currentTimeMillis() - current < waitTime)
			{
			    if(masterLock.tryLock(masterLockWatcher, hostname.getBytes()))
			        break;
			    log.warn("Failed to obtain master lock.");
			    UtilWaitThread.sleep(1000);
			}
			if (System.currentTimeMillis() - current >= waitTime)
			{
			    log.fatal("Failed to get master lock, even after waiting for session timeout, exiting.");
				System.exit(-1);
			}

			log.info("obtained master lock "+masterLock.getLockPath());

		} catch (Exception e) {
			log.fatal("Error while trying to obtain master lock. " + e.getMessage() + ". exiting",e);
			System.exit(-1);
		}
	}

	private String getTserverZooPath(String server){
		return CBConstants.ZROOT_PATH+"/"+Cloudbase.getInstanceID()+CBConstants.ZTSERVERS_PATH+"/"+server;
	}

	private void startLocationUpdateManagers() {
		locationUpdateManager = new LocationUpdateManager();
		metadataLocationUpdateManager = new LocationUpdateManager();

		Thread lumThread = new Daemon(new LoggingRunnable(log, locationUpdateManager));
		Thread mlumThread = new Daemon(new LoggingRunnable(log, metadataLocationUpdateManager));
		lumThread.setName("Location update manager");
		mlumThread.setName("metadataLocationUpdateManager");

		lumThread.start();
		mlumThread.start();
	}

	private void scanMetadataTable() {
		// scan (possibly assign) root tablet to ensure all metadata tablets are assigned
		scanRootTablet();

		log.info("---------] all metadata tablets assigned.\n");
		log.info(" scanning metadata table ...");

		try {
			scanNonRootMetadataTablets();
		} catch (CBException e) {
			log.error("unable to scan metadata table (CBException)");
			log.error(e.toString());
			return;
		} catch (TableNotFoundException e) {
			log.error("unable to scan metadata table (TableNotFoundException)");
			log.error(e.toString());
			return;
		} catch (CBSecurityException e) {
			log.error("unable to scan metadata table (CBSecurityException)");
			log.error(e.toString());
			return;
		}

		log.info("--------] metadata table scanned.");
		haveScannedMetadataTable  = true;
	}



	private void printMetadata() {
		log.debug("metadata has: ");
		for (Entry<KeyExtent, TabletStatus> e : tabletsManager.getTablets().entrySet()) {
			log.debug(e.getKey() + "\t->\t" + e.getValue().getDirectory());
		}
	}

	/**
	 * assigns the root tablet to a tablet server if necessary and updates the
	 * location information in HDFS
	 */
	private void assignRootTablet() {
		// addresses the last key of the metadata table
		// this marks the one and only tablet containing info on other metadata tablets

		// First check prior location so we can apply shutdown locality
		String loc = new HdfsZooInstance().getRootTabletLocation();

		// assign to tablet server
		log.info("assigning root tablet");

		// record address of root tablet's sstables
		tabletsManager.addOrUpdateTablet(CBConstants.ROOT_TABLET_EXTENT, new Text("/root_tablet"), TabletState.UNASSIGNED, loc);

		printMetadata();

		// wait for a tabletserver thread to pick up on this
		log.info("waiting on tablet server handler thread to pickup");
		while (!tabletsManager.getIndex(TabletState.ASSIGNED).containsKey(CBConstants.ROOT_TABLET_EXTENT)) {
			UtilWaitThread.sleep(1000);
		}
		log.info("assigned root tablet.");

		String address = tabletsManager.getAssignedServer(CBConstants.ROOT_TABLET_EXTENT).getClientAddress();

		// write this information to HDFS
		while (!MetadataTable.recordRootTabletLocation(address)) {
			log.warn("unable to write root tablet location to disk");
		}

	}

	private void scanRootTablet() {
		while (!rootTabletScanned) {
			try {
				if (!rootTabletAssigned())
					assignRootTablet();

				// get the address of the server with the root tablet
				TabletServerConnection tServer = tabletsManager.getAssignedServer(CBConstants.ROOT_TABLET_EXTENT);

				log.info("root tablet on " + tServer.getClientAddress());
				log.info("scanning root tablet ...");

				SortedMap<KeyExtent,Text[]> results = MetadataTable.getRootMetadataEntries(systemCredentials());

				if(results.size() == 0) {
					log.error("could not scan root tablet - 0 results returned");
					System.exit(-1);
				}

				log.debug("root tablet has "+results.size() + " entries");

				KeyExtent ke = new KeyExtent();
				Text sstables = null;
				Text location = null;
				for (Entry<KeyExtent, Text[]> entry : results.entrySet()) {

					ke = entry.getKey();
					sstables = entry.getValue()[0];
					location = entry.getValue()[1];

					// skip root tablet
					if(ke.equals(CBConstants.ROOT_TABLET_EXTENT))
						continue;

					log.debug("adding tablet " + ke);

					String locationString = null;
					if(location != null){
						locationString = location.toString();
					}

					tabletsManager.addOrUpdateTablet(ke, sstables, TabletState.UNASSIGNED, locationString);

				}

				if (false) printMetadata();
				rootTabletScanned = true;

				// wait until all metadata tablets are assigned
				log.info("waiting for metadata tablets to be assigned ...");
				while (!metadataTableOnline()) {
					log.info("Still waiting for metadata tablets to be assigned ...");
					UtilWaitThread.sleep(2000);
				}
				log.info("finished scanning root tablet ...");
			} catch (Exception e) {
				log.error(e.toString(),e);
				UtilWaitThread.sleep(1000);
			}
		}
	}

	/**
	 * metadata tablet should be completely assigned by now
	 * @throws TableNotFoundException 
	 * @throws CBSecurityException 
	 * @throws CBException 
	 * 
	 * @throws IOException
	 * @throws TableNotFoundException 
	 */
	private void scanNonRootMetadataTablets() throws CBException, CBSecurityException, TableNotFoundException
	{
		// scan over metadata table using a regular scanner
		KeyExtent ke;

		ScannerImpl mdScanner = new ScannerImpl(new HdfsZooInstance(), systemCredentials(), CBConstants.METADATA_TABLE_NAME, CBConstants.NO_AUTHS); // start at the beginning
		if (safeMode) {
			Key tmpKey = new Key(new Text("!METADATA<"));
			mdScanner.setRange(new Range(new Key(), tmpKey.followingKey(1)));
		}

		mdScanner.fetchColumn(CBConstants.METADATA_TABLE_TABLET_COLUMN_FAMILY, CBConstants.METADATA_TABLE_TABLET_DIRECTORY_COLUMN_NAME);
		mdScanner.fetchColumn(CBConstants.METADATA_TABLE_TABLET_COLUMN_FAMILY, CBConstants.METADATA_TABLE_TABLET_PREV_ROW_COLUMN_NAME);
		mdScanner.fetchColumn(CBConstants.METADATA_TABLE_TABLET_COLUMN_FAMILY, CBConstants.METADATA_TABLE_TABLET_OLD_PREV_ROW_COLUMN_NAME);
		mdScanner.fetchColumn(CBConstants.METADATA_TABLE_TABLET_COLUMN_FAMILY, CBConstants.METADATA_TABLE_TABLET_LOCATION_COLUMN_NAME);

		SortedMap<Key, Value> entries = new TreeMap<Key, Value>();
		SortedMap<KeyExtent, Text> metadataEntries;
		SortedMap<KeyExtent, Text> metadataLocations;



		// TODO could cause memory issues
		for (Entry<Key, Value> entry : mdScanner) {
			entries.put(entry.getKey(), entry.getValue());
		}

		metadataEntries = MetadataTable.getMetadataDirectoryEntries(entries);
		metadataLocations = MetadataTable.getMetadataLocationEntries(entries);


		for(Entry<Key, Value> e : entries.entrySet()) {
			if(e.getKey().getColumnFamily().equals(CBConstants.METADATA_TABLE_TABLET_COLUMN_FAMILY) &&
					e.getKey().getColumnQualifier().equals(CBConstants.METADATA_TABLE_TABLET_OLD_PREV_ROW_COLUMN_NAME)){
				log.warn("Detected incomplete split "+e.getKey()+".  Could be transient or may be fixed by tablet server."); 
				//do not attempt to fix here... could be race conditions between master and tab server fixing it
				//that could result in multiple tab servers serving fixed tablets....  just let tab server fix it
			}
		}

		TreeSet<KeyExtent> onlineExtents;
		synchronized (tabletsManager.getIndex(TabletState.ASSIGNED)) {
			onlineExtents = new TreeSet<KeyExtent>(tabletsManager.getIndex(TabletState.ASSIGNED).keySet());
		}

		for (Entry<KeyExtent, Text> entry : metadataEntries.entrySet()) {
			ke = entry.getKey();
			//log.info("Found metadataEntry: " + ke.toString() + " , " + entry.getValue().toString());
			String location = null;
			if (metadataLocations.get(ke)!=null)
				location = metadataLocations.get(ke).toString();
			tabletsManager.addOrUpdateTablet(ke, entry.getValue(), TabletState.UNASSIGNED, location);

			KeyExtent containingExtent = KeyExtent.findContainingExtent(ke, onlineExtents);

			if (containingExtent == null) {
				log.debug("added " + ke + " to the list of unassigned tablets");

			}else if(!ke.equals(containingExtent)){
				log.debug("Not adding "+ke+" to unassignedTablets because "+containingExtent+" contains it. Assuming "+entry.getValue()+" split "+containingExtent+" since reporting it.");
				//TODO could do a check later to ensure someone is serving ke
			}
		}

	}

	private boolean shutdownLevelComplete(int level)
	{
		synchronized(shutdownStages)
		{
			synchronized(tabletServers)
			{
				Iterator<Entry<String,TabletServerConnection>> tabletIter = tabletServers.getIndex(TabletServerState.ONLINE).entrySet().iterator();
				while(tabletIter.hasNext())
				{
					Entry<String,TabletServerConnection> entry = tabletIter.next();
					if(!shutdownStages.containsKey(entry.getValue()) || shutdownStages.get(entry.getValue()) < level)
					{
						log.info("Shutdown stage "+(level+1)+" waiting for tserver "+entry.getValue().getMasterAddress());
						// TODO: resend shutdown message?
						return false;
					}
				}
			}
		}
		return true;
	}

	public static void main(String[] args) throws IOException, InterruptedException {
		conf = new Configuration();
		fs = FileSystem.get(Master.conf);

		Cloudbase.init("//conf//master_logger.ini");

		Master master = new Master();
		if(args.length > 0 && args[0].toUpperCase().equals("SAFEMODE"))
			master.safeMode = true;

		master.run();
	}

	Map<String, TableInfo> getTableStats(){

		Map<String, TableInfo> tableStats= new TreeMap<String, TableInfo>();
		Map<String, Map<KeyExtent, TabletRates>> trIndex = new HashMap<String, Map<KeyExtent, TabletRates>>();

		synchronized (tabletsManager) {

			SortedMap<KeyExtent, TabletStatus> allTablets = tabletsManager.getTablets();

			Set<Entry<KeyExtent, TabletStatus>> es = allTablets.entrySet();

			for (Entry<KeyExtent, TabletStatus> entry : es) {
				String tableName = entry.getKey().getTableName().toString();
				TableInfo tableInfo = tableStats.get(tableName);

				if(tableInfo == null){
					tableInfo = new TableInfo();
					tableStats.put(tableName, tableInfo);
				}

				tableInfo.tablets++;
				if(entry.getValue().getState() == TabletState.ASSIGNED){

					if(entry.getValue().isOnline())
						tableInfo.onlineTablets++;

					Map<KeyExtent, TabletRates> tsIndex = trIndex.get(entry.getValue().getTabletServer().getMasterAddress());

					if(tsIndex == null && entry.getValue().getTabletServer().getState() == TabletServerState.ONLINE){
						//build index of a tablet servers tablets

						tsIndex = new HashMap<KeyExtent, TabletRates>();
						trIndex.put(entry.getValue().getTabletServer().getMasterAddress(), tsIndex);

						if(entry.getValue().getTabletServer().status != null && entry.getValue().getTabletServer().status.tabletRates != null){
							for (TabletRates tabletRates : entry.getValue().getTabletServer().status.tabletRates) {
								tsIndex.put(tabletRates.key, tabletRates);
							}
						}
					}

					TabletRates tr = tsIndex.get(entry.getKey());

					if(tr != null){
						tableInfo.recs += tr.records;
						tableInfo.recsInMemory += tr.recordsInMemory;
						tableInfo.ingestRate += tr.tableIngestRate;
						tableInfo.queryRate += tr.tableQueryRate;
					}
				}
			}
		}

		return tableStats;
	}

	public int onlineTabletCount() {
		return tabletsManager.getIndex(TabletState.ASSIGNED).size();
	}

	public boolean stopping() {
		return allStopRequested;
	}
	
}
