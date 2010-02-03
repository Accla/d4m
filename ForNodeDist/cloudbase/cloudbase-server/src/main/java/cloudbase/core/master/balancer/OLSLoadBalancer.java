package cloudbase.core.master.balancer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cloudbase.core.CBConstants;
import cloudbase.core.data.KeyExtent;
import cloudbase.core.master.thrift.TabletRates;
import cloudbase.core.util.TabletFunctions;

public class OLSLoadBalancer implements LoadBalancer {
	
	private static Random random;
	private HashMap<TabletServerStatsProvider, TabletRates> receivingServers;
	private HashMap<TabletServerStatsProvider, TabletRates> releasingServers;


	@SuppressWarnings("unused")
    private static Log log = LogFactory.getLog(OLSLoadBalancer.class);
	
	// breaks ties in favor of moving small tablets
	private static final double LOAD_DIFFERENTIATOR = 1.01;
		
	private static final double MIGRATE_COST = 2.5;
	private static final double METADATA_MIGRATE_COST = 10;
	
	public OLSLoadBalancer() {
		this(-1);
	}
	
	public OLSLoadBalancer(int seed) {
		receivingServers = new HashMap<TabletServerStatsProvider, TabletRates>();
		releasingServers = new HashMap<TabletServerStatsProvider, TabletRates>();
		if (seed==-1)
			random = new Random();
		else
			random = new Random(seed);
	}
	
	private TabletServerStatsProvider getRandomServer(List<? extends TabletServerStatsProvider> servers) {
		int numServers = servers.size();
		if(numServers ==0)
			return null;
		
		return servers.remove(random.nextInt(numServers));
	}
	
	// Difference between the future load and previous load
	// Smallest negative = better
	// Load Differentiator favors moving smaller tablets for some reason
	private static double loadChange(double tabLoad, double srcLoad, double destLoad) {
		double previous = Math.pow(srcLoad*LOAD_DIFFERENTIATOR, 2) + Math.pow(destLoad*LOAD_DIFFERENTIATOR, 2);
		double future = Math.pow((srcLoad-tabLoad)*LOAD_DIFFERENTIATOR, 2) + Math.pow((destLoad+tabLoad)*LOAD_DIFFERENTIATOR, 2);
		return future - previous;
	}
	
	//Penalty for moving tablet
	private static double impact(TabletRates tablet) {
		if (tablet.key.getTableName().toString().equals(CBConstants.METADATA_TABLE_NAME))
			return TabletFunctions.getLoad(tablet)*METADATA_MIGRATE_COST;
		return TabletFunctions.getLoad(tablet)*MIGRATE_COST;
	}
	
	private static double migScore(TabletRates tablet, double src, double dest) {
		double tLoad = TabletFunctions.getLoad(tablet);
		return loadChange(tLoad, src, dest) + impact(tablet);
	}
	
	public Set<TabletMigration> getMigrations(Map<KeyExtent, ? extends TabletInfo> tablets,
			Collection<? extends TabletServerStatsProvider> servers) {
		return getMigrations(tablets, servers, null, null);
	}

	
	public Set<TabletMigration> getMigrations(Map<KeyExtent, ? extends TabletInfo> tablets,
			Collection<? extends TabletServerStatsProvider> servers, TabletServerStatsProvider first,
			TabletServerStatsProvider second) {
		
		// need at least two servers with updated status
		if(servers.size() - receivingServers.size() - releasingServers.size()< 2) {
			return null;
		}
		
		// find two random servers
		TabletServerStatsProvider one;
		TabletServerStatsProvider two;
		
		List<TabletServerStatsProvider> serverArray = new ArrayList<TabletServerStatsProvider>(servers);
		// Clear out affected servers
		for (TabletServerStatsProvider aServer : receivingServers.keySet()) {
			serverArray.remove(aServer);
		}
		for (TabletServerStatsProvider aServer : releasingServers.keySet()) {
			serverArray.remove(aServer);
		}
		
		// gets a server off the list and removes it.
		// This allows us to do more with less housekeeping.
		if (first==null)
			one = getRandomServer(serverArray);
		else
			one = first;
		if (second==null)
			two = getRandomServer(serverArray);
		else
			two = second;
		
		// we couldn't find a server
		if(one==null || two==null) {
			return null;
		}
		
		if(one.getLoad() < two.getLoad()) {
			TabletServerStatsProvider tmp = one;
			one = two;
			two = tmp;
		}
		
		TabletRates best = getBest(one, two);
		
		if(best == null) {
			return null;
		}
	
		releasingServers.put(one, best);
		receivingServers.put(two, best);		
		
		Set<TabletMigration> set = new TreeSet<TabletMigration>();
		set.add(new TabletMigration(best.key, one, two));
		return set;
	}

	public static TabletRates getBest(TabletServerStatsProvider one,
			TabletServerStatsProvider two) {
		// use 0 so we don't do unneeded migration
		double minScore = 0;	
		TabletRates best = null;

		for(TabletRates rates : one.getTabletsStats()) {
			double score = migScore(rates, one.getLoad(), two.getLoad());
			if(score < minScore) {
				minScore = score;
				best = rates;
			}
		}
		return best;
	}

	public TabletServerStatsProvider getServerForTablet(KeyExtent tablet,
			Map<KeyExtent, ? extends TabletInfo> tablets,
			Collection<? extends TabletServerStatsProvider> servers) {
		TabletInfo ti = tablets.get(tablet);
		if (ti!=null) {
			String serverName = ti.getTabletServerName();
			for (TabletServerStatsProvider server : servers) {
				if (server.getName().equals(serverName))
					return server;
			}
		}
		return getRandomServer(new ArrayList<TabletServerStatsProvider>(servers));
	}

	public void tabletDeleted(KeyExtent ke) {
		// no op
	}

	/**
	 * clear server from list of servers with stale status
	 */
	public void tabletServerStatusUpdated(TabletServerStatsProvider server) {
		// if the updated server now reports having the tablet, we're good
		// and if the old server no longer has it
		if(receivingServers.containsKey(server) && server.getTabletsStats().contains(receivingServers.get(server)))
			receivingServers.remove(server);
		if (releasingServers.containsKey(server) && !server.getTabletsStats().contains(releasingServers.get(server)))
			releasingServers.remove(server);
	}
}
