package cloudbase.core.master.balancer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;

import org.apache.hadoop.io.Text;

import cloudbase.core.data.KeyExtent;
import cloudbase.core.master.mgmt.TabletServerState;
import cloudbase.core.master.mgmt.TabletState;
import cloudbase.core.master.thrift.Compacting;
import cloudbase.core.master.thrift.TabletRates;

public class SimpleLoadBalancerTest extends TestCase {
    
    static final Compacting FAKE = new Compacting(); 
    

	public void testBalanceOneHigh() {
		ArrayList<String> tables = new ArrayList<String>();
		Map<KeyExtent, TabletInfo> extentMap = new HashMap<KeyExtent, TabletInfo>();
		tables.add("table");
		List<TabletServerStatsProvider> tservers = new ArrayList<TabletServerStatsProvider>();
		for(int i = 0; i < 30; i++) {
			tservers.add(new fakeTabletServerStatsProvider(new String("tserver"+i)));
		}
		int tservercnt = 0;
		int server = 0;
		for(int i=0; i < 200; i++) {
			KeyExtent ke = new KeyExtent(new Text("table"), new Text(new String(""+i)), null);
			extentMap.put(ke, new fakeTabletInfo(ke, tservers.get(server).getName()));
			tservers.get(server).getTabletsStats().add(new TabletRates(ke, 0, 0, 0, 0, FAKE, FAKE));
			tservercnt++;
			if (tservercnt % 6 == 0)
				if (server!=29)
					server++;
		}
		Balance(tservers, extentMap);
	}
	
	public void testBalanceOneLow() {
		ArrayList<String> tables = new ArrayList<String>();
		Map<KeyExtent, TabletInfo> extentMap = new HashMap<KeyExtent, TabletInfo>();
		tables.add("table");
		List<TabletServerStatsProvider> tservers = new ArrayList<TabletServerStatsProvider>();
		for(int i = 0; i < 30; i++) {
			tservers.add(new fakeTabletServerStatsProvider(new String("tserver"+i)));
		}
		int tservercnt = 0;
		int server = 0;
		for(int i=0; i < 280; i++) {
			KeyExtent ke = new KeyExtent(new Text("table"), new Text(new String(""+i)), null);
			extentMap.put(ke, new fakeTabletInfo(ke, tservers.get(server).getName()));
			tservers.get(server).getTabletsStats().add(new TabletRates(ke, 0, 0, 0, 0, FAKE, FAKE));
			tservercnt++;
			if (tservercnt % 10 == 0)
				if (server!=28)
					server++;
		}
		Balance(tservers, extentMap);
	}
	
	public void testBalanceBalanced() {
		ArrayList<String> tables = new ArrayList<String>();
		Map<KeyExtent, TabletInfo> extentMap = new HashMap<KeyExtent, TabletInfo>();
		tables.add("table");
		List<TabletServerStatsProvider> tservers = new ArrayList<TabletServerStatsProvider>();
		for(int i = 0; i < 30; i++) {
			tservers.add(new fakeTabletServerStatsProvider(new String("tserver"+i)));
		}
		int tservercnt = 0;
		int server = 0;
		for(int i=0; i < 300; i++) {
			KeyExtent ke = new KeyExtent(new Text("table"), new Text(new String(""+i)), null);
			extentMap.put(ke, new fakeTabletInfo(ke, tservers.get(server).getName()));
			tservers.get(server).getTabletsStats().add(new TabletRates(ke, 0, 0, 0, 0, FAKE, FAKE));
			tservercnt++;
			if (tservercnt % 10 == 0)
				server++;
		}
		Balance(tservers, extentMap);
	}
	
	public void testBalanceLowTabletCount() {
		ArrayList<String> tables = new ArrayList<String>();
		Map<KeyExtent, TabletInfo> extentMap = new HashMap<KeyExtent, TabletInfo>();
		tables.add("table");
		List<TabletServerStatsProvider> tservers = new ArrayList<TabletServerStatsProvider>();
		for(int i = 0; i < 30; i++) {
			tservers.add(new fakeTabletServerStatsProvider(new String("tserver"+i)));
		}
		int tservercnt = 0;
		int server = 0;
		for(int i=0; i < 5; i++) {
			KeyExtent ke = new KeyExtent(new Text("table"), new Text(new String(""+i)), null);
			extentMap.put(ke, new fakeTabletInfo(ke, tservers.get(server).getName()));
			tservers.get(server).getTabletsStats().add(new TabletRates(ke, 0, 0, 0, 0, FAKE, FAKE));
			tservercnt++;
			if (tservercnt % 10 == 0)
				server++;
		}
		Balance(tservers, extentMap);
	}
	
	public void testBalanceEvenHighLow() {
		ArrayList<String> tables = new ArrayList<String>();
		Map<KeyExtent, TabletInfo> extentMap = new HashMap<KeyExtent, TabletInfo>();
		tables.add("table");
		List<TabletServerStatsProvider> tservers = new ArrayList<TabletServerStatsProvider>();
		for(int i = 0; i < 30; i++) {
			tservers.add(new fakeTabletServerStatsProvider(new String("tserver"+i)));
		}
		int tservercnt = 0;
		int server = 0;
		for(int i=0; i < 300; i++) {
			KeyExtent ke = new KeyExtent(new Text("table"), new Text(new String(""+i)), null);
			extentMap.put(ke, new fakeTabletInfo(ke, tservers.get(server).getName()));
			tservers.get(server).getTabletsStats().add(new TabletRates(ke, 0, 0, 0, 0, FAKE, FAKE));
			tservercnt++;
			if ((server < 15 && tservercnt % 20 == 0) || (server >= 15 && tservercnt %10 == 0))
				server++;
		}
		Balance(tservers, extentMap);
	}

	
	// provide extents and lists of tservers with pertinent info and it will do the rest
	public void Balance(List<TabletServerStatsProvider> tservers, Map<KeyExtent, ? extends TabletInfo> extentMap) {
	    boolean debug = false;
	    if (debug) dumpTserverLoads(tservers);
		LoadBalancer lb = new SimpleLoadBalancer(0);
		Set<TabletMigration> migrations = lb.getMigrations(extentMap, tservers);
		for (TabletMigration mig : migrations) {
			mig.newServer.getTabletsStats().add(new TabletRates(mig.tablet,0,0,0,0,FAKE,FAKE));
			mig.oldServer.getTabletsStats().remove(new TabletRates(mig.tablet,0,0,0,0,FAKE,FAKE));
		}
		if (debug) dumpTserverLoads(tservers);
		assertTrue(verifyBalance(tservers));
	}

	private boolean verifyBalance(List<TabletServerStatsProvider> tservers) {
		int max, min;
		if (tservers == null || tservers.size()<=1)
			return true;
		max = tservers.get(0).getTabletsStats().size();
		min = max;
		for (int i = 1; i < tservers.size(); i++) {
			int numTablets = tservers.get(i).getTabletsStats().size();
			max = Math.max(max, numTablets);
			min = Math.min(min, numTablets);
		}
		// if two tablets have more than a 1 tablet difference, we failed
		if (max-min>1)
			return false;
		return true;
	}

	private void dumpTserverLoads(List<TabletServerStatsProvider> tservers) {
		for (TabletServerStatsProvider ts : tservers) {
			System.out.println("Tserver " + ts.getName() + " has load of " + ts.getTabletsStats().size() + " tablets");
		}		
	}

	private class fakeTabletInfo implements TabletInfo {
		KeyExtent ke;
		String tserver;

		public fakeTabletInfo(KeyExtent ke, String server) {
			this.ke = ke;
			this.tserver = server;
		}

		@Override
		public String getDestTabletServerName() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public KeyExtent getExtent() {
			return ke;
		}

		@Override
		public TabletState getState() {
			return TabletState.ASSIGNED;
		}

		@Override
		public String getTabletServerName() {
			return tserver;
		}

		public void setTabletServerName(String tserver) {
			this.tserver = tserver;
		}
	}

	public class fakeTabletServerStatsProvider implements TabletServerStatsProvider {
		String name;
		List<TabletRates> rates = new ArrayList<TabletRates>();

		public fakeTabletServerStatsProvider(String name) {
			this.name = name;
		}
		@Override
		public double getLoad() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public String getName() {
			return name;
		}

		@Override
		public TabletServerState getState() {
			return TabletServerState.ONLINE;
		}

		@Override
		public List<TabletRates> getTabletsStats() {
			return rates;
		}

		@Override
		public boolean statsUpToDate() {
			return true;
		}

	}
}
