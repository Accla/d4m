package cloudbase.core.master;

import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.hadoop.io.Text;
import org.apache.log4j.Logger;

import cloudbase.core.client.TableNotFoundException;
import cloudbase.core.data.KeyExtent;
import cloudbase.core.master.TabletServerManager.TabletServerConnection;
import cloudbase.core.master.balancer.TabletInfo;
import cloudbase.core.master.mgmt.TabletState;

public class TabletsManager {

	static Logger log = Logger.getLogger(TabletsManager.class.getName());
	
	private SortedMap<KeyExtent, TabletStatus> allTablets;
	private Map<TabletState, SortedMap<KeyExtent, TabletStatus>> index;
	
	private Map<TabletServerConnection, SortedMap<KeyExtent, TabletStatus>> tabletServerTablets;
	
	private Map<Text, EnumMap<TabletState, SortedMap<KeyExtent, TabletStatus>>> tableIndexes;
	
	private Map<Text, SortedMap<KeyExtent, TabletStatus>> tableTablets;
	
	synchronized private void updateStateIndex(Map<TabletState, SortedMap<KeyExtent, TabletStatus>> idx, TabletState state, TabletState newState, KeyExtent extent, TabletStatus tabletStatus){
		TabletStatus ts = idx.get(state).remove(extent);
		
		if(ts != tabletStatus){
			TabletState actualState = null;
			TabletStatus tabStat = allTablets.get(extent);
			if(tabStat != null){
				actualState = tabStat.getState();
			}
			
			log.error("Tablet index corrupted! Tablet status not at expected location in index (state = "+state+", newState = "+newState+", extent = "+extent+", actualState = "+actualState);
			throw new RuntimeException("The sh*t hit the fan");
		}
		
		idx.get(newState).put(extent, tabletStatus);
	}
	
	public class TabletStatus implements TabletInfo { // implements TabletStats {
		private KeyExtent extent;
		private TabletState state;
		private TabletServerConnection tsc;
		private Text tabletDirectory;
		private String tabletLocation;
		private TabletServerConnection destServer;
		private boolean online;
		
		//private TabletRates tabletRates;
		
		private TabletStatus(KeyExtent ke, TabletState state, Text tabletDirectory, String tabletLocation) {
			this.extent = ke;
			this.state = state;
			this.tabletDirectory = tabletDirectory;
			this.tabletLocation = tabletLocation;
		}

		void setState(TabletState newState){
			synchronized (TabletsManager.this) {
				if(state == newState){
					return;
				}
				
				if(state == TabletState.ASSIGNED)
					online = false;
				
				updateStateIndex(index, state, newState, extent, this);
				
				EnumMap<TabletState, SortedMap<KeyExtent, TabletStatus>> tableIndex = tableIndexes.get(extent.getTableName());
				if(tableIndex != null){
					updateStateIndex(tableIndex, state, newState, extent, this);
				}
				
				log.debug(String.format("Tablet state transition %10s -> %10s : %s", state, newState, extent));
				
				state = newState;
		
				if(state != TabletState.ASSIGNED){
					removeFromTabletServerIndex();
				}
			}
		}

		TabletServerConnection getTabletServer() {
			synchronized (TabletsManager.this) {
				return tsc;
			}
		}
		
		TabletServerConnection getDestTabletServer() {
			synchronized (TabletsManager.this) {
				return destServer;
			}
		}

		private void removeFromTabletServerIndex(){
			synchronized (TabletsManager.this) {
				if(tsc != null){
					SortedMap<KeyExtent, TabletStatus> oldTS = tabletServerTablets.get(tsc);
					if(oldTS != null){
						oldTS.remove(extent);
					}
				}
			}
		}
		
		private void setTabletServer(TabletServerConnection server){
			synchronized (TabletsManager.this) {
				removeFromTabletServerIndex();
				
				this.tsc = server;
				this.tabletLocation = server.getMasterAddress();
				SortedMap<KeyExtent, TabletStatus> newTS = tabletServerTablets.get(tsc);
				if(newTS == null){
					newTS = new TreeMap<KeyExtent, TabletStatus>();
					tabletServerTablets.put(tsc, newTS);
				}
				
				newTS.put(extent, this);
			}
		}
		
		void setAssigned(TabletServerConnection server) {
			synchronized (TabletsManager.this) {
				setTabletServer(server);
				setState(TabletState.ASSIGNED);
			}
		}
		
		Text getDirectory(){
			synchronized (TabletsManager.this) {
				return tabletDirectory;
			}
		}
		
		String getLocation() {
			synchronized (TabletsManager.this) {
				return tabletLocation;
			}
		}

		void setMigrating(TabletServerConnection destServer) {
			synchronized (TabletsManager.this) {
				this.destServer = destServer;
				setState(TabletState.MIGRATING);
			}
		}
		
		public TabletState getState() {
			synchronized (TabletsManager.this) {
				return state;
			}
		}

		public KeyExtent getExtent() {
			synchronized (TabletsManager.this) {
				return extent;
			}
		}

		void setDirectory(Text dir) {
			synchronized (TabletsManager.this) {
				this.tabletDirectory = dir;
			}
		}

		void setLocation(String loc) {
			synchronized (TabletsManager.this) {
				this.tabletLocation = loc;
			}
		}
		
		public String getTabletServerName() {
			synchronized (TabletsManager.this) {
				if(getTabletServer() == null){
					return this.tabletLocation;
				}else{
					return getTabletServer().getMasterAddress();
				}		
			}
		}

		public String getDestTabletServerName() {
			synchronized (TabletsManager.this) {
				if(getDestTabletServer() == null){
					return null;
				}else{
					return getDestTabletServer().getMasterAddress();
				}		
			}
		}
		
		public void setOnline(boolean b) {
			synchronized (TabletsManager.this) {
				this.online = b;
			}
		}
		
		public boolean isOnline() {
			synchronized (TabletsManager.this) {
				return this.online;
			}
		}
	}
	
	TabletsManager(List<String> tablesToIndex){
		tableIndexes = Collections.synchronizedMap(new HashMap<Text, EnumMap<TabletState, SortedMap<KeyExtent, TabletStatus>>>());
		index = Collections.synchronizedMap(new EnumMap<TabletState, SortedMap<KeyExtent, TabletStatus>>(TabletState.class));
		allTablets = Collections.synchronizedSortedMap(new TreeMap<KeyExtent, TabletStatus>());
		tabletServerTablets = Collections.synchronizedMap(new HashMap<TabletServerConnection, SortedMap<KeyExtent, TabletStatus>>());
		
		tableTablets = Collections.synchronizedMap(new HashMap<Text, SortedMap<KeyExtent,TabletStatus>>());
		
		for(TabletState ts : TabletState.values()){
			index.put(ts, Collections.synchronizedSortedMap(new TreeMap<KeyExtent, TabletStatus>()));
		}
		
		for (String table : tablesToIndex) {
			indexTable(table);
		}
	}
	
	synchronized SortedMap<KeyExtent, TabletStatus> getIndex(TabletState state){
		return Collections.unmodifiableSortedMap(index.get(state));
	}
	
	synchronized SortedMap<KeyExtent, TabletStatus> getIndex(String table, TabletState state){
		return getIndex(new Text(table), state);
	}
	
	synchronized SortedMap<KeyExtent, TabletStatus> getIndex(Text table, TabletState state){
		EnumMap<TabletState, SortedMap<KeyExtent, TabletStatus>> tindex = tableIndexes.get(table);
		
		if(tindex == null){
			throw new IllegalArgumentException("table "+table+" is not indexed");
		}
		
		return Collections.unmodifiableSortedMap(tindex.get(state));
	}
	
	synchronized TabletServerConnection getAssignedServer(KeyExtent ke){
		TabletStatus ts = getIndex(TabletState.ASSIGNED).get(ke);
		if(ts != null){
			return ts.getTabletServer();
		}
		return null;
	}
	
	synchronized void addTablet(KeyExtent extent, TabletState initialState, TabletServerConnection server) {
		addTablet(extent, null, initialState, server);
	}
	
	synchronized void addTablet(KeyExtent newExtent, Text text, TabletState initialState) {
		addTablet(newExtent, text, initialState, null);
	}
	
	synchronized void addTablet(KeyExtent ke, Text tabletLocation, TabletState initialState, TabletServerConnection server){
		if(allTablets.containsKey(ke)){
			log.warn("Not adding "+ke+" already have it");
		}else{
			TabletStatus tabStatus = new TabletStatus(ke, initialState, tabletLocation, server == null ? null : server.getMasterAddress());
			index.get(initialState).put(ke, tabStatus);
			allTablets.put(ke, tabStatus);
			
			SortedMap<KeyExtent, TabletStatus> tt = tableTablets.get(ke.getTableName());
			if (tt == null) {
			    tt = new TreeMap<KeyExtent, TabletStatus>();
			    tableTablets.put(ke.getTableName(), tt);
			}
			tt.put(ke, tabStatus);
			
			if(server != null){
				tabStatus.setTabletServer(server);
			}
			
			EnumMap<TabletState, SortedMap<KeyExtent, TabletStatus>> ti = tableIndexes.get(ke.getTableName());
			if(ti != null){
				ti.get(initialState).put(ke, tabStatus);
			}
		}
	}

	synchronized void addOrUpdateTablet(KeyExtent ke, Text tabletDirectory, TabletState initialStateIfNonExistant, String tabletLocation) {
		TabletStatus ts = allTablets.get(ke);
		if(ts != null){
			ts.setDirectory(tabletDirectory);
		}else{
			TabletStatus tabStatus = new TabletStatus(ke, initialStateIfNonExistant, tabletDirectory, tabletLocation);
			index.get(initialStateIfNonExistant).put(ke, tabStatus);
			allTablets.put(ke, tabStatus);
			
			SortedMap<KeyExtent, TabletStatus> tt = tableTablets.get(ke.getTableName());
	         if (tt == null) {
	             tt = new TreeMap<KeyExtent, TabletStatus>();
	             tableTablets.put(ke.getTableName(), tt);
	         }
	         tt.put(ke, tabStatus);
			
			EnumMap<TabletState, SortedMap<KeyExtent, TabletStatus>> ti = tableIndexes.get(ke.getTableName());
			if(ti != null){
				ti.get(initialStateIfNonExistant).put(ke, tabStatus);
			}
		}
	}
	
	synchronized TabletStatus getTablet(KeyExtent extent) {
		return allTablets.get(extent);
	}

	synchronized SortedMap<KeyExtent, TabletStatus> getTablets() {
		return Collections.unmodifiableSortedMap(allTablets);
	}
	
	public synchronized SortedMap<KeyExtent, TabletStatus> getTablets(TabletServerConnection server) {
		SortedMap<KeyExtent, TabletStatus> newTS = tabletServerTablets.get(server);
		if(newTS == null){
			return Collections.unmodifiableSortedMap(new TreeMap<KeyExtent, TabletStatus>());
		}
		
		return Collections.unmodifiableSortedMap(newTS);
	}
	
	synchronized SortedMap<KeyExtent, TabletStatus> getTablets(String table) throws TableNotFoundException {
		SortedMap<KeyExtent, TabletStatus> tt = tableTablets.get(new Text(table));
		
		if(tt == null){
			throw new TableNotFoundException(table);
		}
		
		return Collections.unmodifiableSortedMap(tt);
	}
	
	synchronized private void indexTable(String table){
		Text t = new Text(table);
	
		if(!tableIndexes.containsKey(t)){
			EnumMap<TabletState, SortedMap<KeyExtent, TabletStatus>> tindex = new EnumMap<TabletState, SortedMap<KeyExtent, TabletStatus>>(TabletState.class);
			
			for(TabletState ts : TabletState.values()){
				tindex.put(ts, Collections.synchronizedSortedMap(new TreeMap<KeyExtent, TabletStatus>()));
			}
			
			tableIndexes.put(t, tindex);
		}
		
		tableTablets.put(new Text(table), new TreeMap<KeyExtent, TabletStatus>());
	}

	synchronized void removeTablet(KeyExtent tablet) {
		
		TabletStatus tabStat = allTablets.remove(tablet);
		
		if(tabStat == null){
			log.warn("Tried to remove non-existant tablet "+tablet);
			return;
		}
		
		tabStat.removeFromTabletServerIndex();
		
		SortedMap<KeyExtent, TabletStatus> tt = tableTablets.get(tablet.getTableName());
		
		if(tt != null){
			if(tt.remove(tablet) == null){
				log.error("Tablet "+tablet+" not in index");
				throw new RuntimeException("The shit hit the fan");
			}
			if (tt.size() == 0) {
			    tableTablets.remove(tablet.getTableName());
			}
		}
		
		index.get(tabStat.getState()).remove(tablet);
		
		EnumMap<TabletState, SortedMap<KeyExtent, TabletStatus>> ti = tableIndexes.get(tablet.getTableName());
		
		if(ti != null){
			if(ti.get(tabStat.getState()).remove(tablet) == null){
				log.error("Tablet "+tablet+" not in index");
				throw new RuntimeException("The shit hit the fan");
			}
		}
	}
}

