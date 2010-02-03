package cloudbase.core.master.balancer;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import cloudbase.core.data.KeyExtent;
import cloudbase.core.master.mgmt.TabletServerState;


public class SimpleLoadBalancer implements LoadBalancer {

	static Logger log = Logger.getLogger(SimpleLoadBalancer.class.getName());

	private Random random = new Random();
	private long lastRunTime;

	public SimpleLoadBalancer() {
		this(System.currentTimeMillis());
	}

	public SimpleLoadBalancer(long time) {
		lastRunTime = time;
	}

	private TabletServerStatsProvider getRandomServer(Collection<? extends TabletServerStatsProvider> servers) {
		int numServers = servers.size();
		if(numServers ==0)
			return null;

		int s = random.nextInt(numServers);

		int i=0;
		for(TabletServerStatsProvider server : servers) {
			if(i == s)
				return server;
			i++;
		}

		return null;
	}

	public void tabletDeleted(KeyExtent ke) {
		// TODO Auto-generated method stub

	}

	public void tabletServerStatusUpdated(TabletServerStatsProvider server) {
		// TODO Auto-generated method stub

	}

	public Set<TabletMigration> getMigrations(Map<KeyExtent, ? extends TabletInfo> tablets,
			Collection<? extends TabletServerStatsProvider> servers) {

		if(System.currentTimeMillis() - lastRunTime < 60000){
			//do not run too often
			return null;
		}

		lastRunTime = System.currentTimeMillis();

		// So the premise of this balancer is that all tservers should have the number of tablets
		// equal to one of two values, lo and hi, which are either equal or within 1 of eachother
		// This is done so we can have an even distribution of tablets to tablet servers

		int lo, hi;


		//build a per server index that takes into account what is on or migrating to a server
		Map<String, List<TabletInfo>> tabletsPerServer = new HashMap<String, List<TabletInfo>>();

		int totalTablets = 0;

		for(TabletInfo ti : tablets.values()){

			String server = null;

			switch(ti.getState()){
			case ASSIGNED:
				totalTablets++;
				server = ti.getTabletServerName();
				break;
			case MIGRATING:
				totalTablets++;
				server = ti.getDestTabletServerName();
				break;
			case UNASSIGNED:
				break;
			}

			if(server != null){
				List<TabletInfo> tabletList = tabletsPerServer.get(server);
				if(tabletList == null){
					tabletList = new ArrayList<TabletInfo>();
					tabletsPerServer.put(server, tabletList);
				}

				tabletList.add(ti);
			}
		}
		for (TabletServerStatsProvider tserver : servers) {
			if (!tabletsPerServer.containsKey(tserver.getName()))
				tabletsPerServer.put(tserver.getName(), new ArrayList<TabletInfo>());
		}

		//create a map of numbers of tablets on a tserver a list of the tservers
		SortedMap<Integer, List<String>> countsToTServers = new TreeMap<Integer, List<String>>();
		for (Map.Entry<String, List<TabletInfo>> entry : tabletsPerServer.entrySet()) {
			String server = entry.getKey();
			// totalTablets - number of tablets so we have a high -> low sort order
			Integer count = new Integer(totalTablets-entry.getValue().size());
			List<String> serverList = countsToTServers.get(count);
			if(serverList==null) {
				serverList = new ArrayList<String>();
				countsToTServers.put(count, serverList);
			}
			serverList.add(server);		
		}

		if (countsToTServers.size()==2) {
			int key1 = -1;
			int key2 = -1;
			for (Integer key : countsToTServers.keySet()) {
				if (key1==-1)
					key1 = key.intValue();
				else
					key2 = key.intValue();
			}
			if (key2-key1 < 2)
				return null;
		}





		//index tablet servers
		int totalServers = 0;

		Map<String, TabletServerStatsProvider> tabletServersByName = new HashMap<String, TabletServerStatsProvider>();
		for (TabletServerStatsProvider tss : servers) {
			if(tss.getState() == TabletServerState.ONLINE){
				tabletServersByName.put(tss.getName(), tss);
				totalServers++;

				if(!tabletsPerServer.containsKey(tss.getName())){
					List<TabletInfo> empty = Collections.emptyList();
					tabletsPerServer.put(tss.getName(), empty);
				}
			}
		}

		if(totalServers < 2){
			return null;
		}

		lo = (int)Math.floor((double)totalTablets/(double)totalServers);
		hi = (int)Math.ceil((double)totalTablets/(double)totalServers);
		int atHi = 0;
		int toHi = totalTablets-lo*totalServers;
		int migCnt = 0;
		List<TabletMigration> migrationCandidates = new ArrayList<TabletMigration>();
		for (Map.Entry<Integer, List<String>> entry : countsToTServers.entrySet()) {
			for(String server : entry.getValue()){
				Random r = new Random();
				int numTablets = totalTablets - entry.getKey().intValue();
				// if numTablets > hi then we need to pull more off or if we've reached hi, pull one more off
				if (numTablets > hi || (numTablets == hi && atHi <= toHi)) {
					// pull enough to bring the value to hi
					int toMove = numTablets - hi;
					// If we already reached the number of servers that need to be at hi, then we need to start getting them to the low val
					if (atHi == toHi)
						toMove = numTablets - lo;
					if (toMove!=0) {
						// get the list of tablets
						List<TabletInfo> tabletList = tabletsPerServer.get(server);
						for (int cnt = 0; cnt < toMove; cnt++) {
							// pull out a random one and add it to the migration list.  We'll update the destination later
							TabletInfo migrating = tabletList.remove(r.nextInt(tabletList.size()));
							migrationCandidates.add(new TabletMigration(migrating.getExtent(), tabletServersByName.get(server), null));
						}
					}
					// One more tServer is at the hi amount
					if (numTablets-toMove == hi)
						atHi++;
				}
				// if we don't have enough hi tablets, start moving these guys up
				else if((numTablets==lo && atHi < (totalTablets - lo*totalServers)) || numTablets < lo) {
					int toTake = lo - numTablets;
					if (atHi < toHi)
						toTake = hi - numTablets;
					if (toTake==0)
						continue;
					for (int cnt = 0; cnt < toTake; cnt++) {
						// grab the first from the migration list and add me to the destination
						migrationCandidates.get(migCnt).newServer = tabletServersByName.get(server);
						migCnt++;
					}
					if (toTake + numTablets == hi)
						atHi++;
				}
			}
		}
		return new HashSet<TabletMigration>(migrationCandidates);
	}

	public TabletServerStatsProvider getServerForTablet(KeyExtent tablet,
			Map<KeyExtent, ? extends TabletInfo> tablets,
			Collection<? extends TabletServerStatsProvider> servers) {


		// Doesn't balance correctly because tss.getTabletStats.size only updates every ping
		// This causes larger systems to have the majority of tablets on a single tserver.
		// So, simple solution is just go random and let the balancer work it out.
		/*if(servers.size() == 0){
			return null;
		}
		int totalTablets = tablets.size();

		int target = totalTablets / servers.size();

		TabletServerStatsProvider minTss = null;
		int min = Integer.MAX_VALUE;

		for (TabletServerStatsProvider tss : servers) {
			if(tss != null && tss.getTabletsStats().size() < min){
				min = tss.getTabletsStats().size();
				minTss = tss;

				if(min == 0)
					break;
			}
		}

		if(min < target){
			return minTss;
		}
		 */

		return getRandomServer(servers);
	}



}
