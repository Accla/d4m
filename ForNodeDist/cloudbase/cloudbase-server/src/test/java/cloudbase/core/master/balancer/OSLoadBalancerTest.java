package cloudbase.core.master.balancer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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
import cloudbase.core.util.TabletFunctions;

public class OSLoadBalancerTest extends TestCase {

    static final Compacting FAKE = new Compacting(); 
    
    
	public void testBalanceHalfHiHalfLo() {
		Map<KeyExtent, TabletInfo> extentMap = new HashMap<KeyExtent, TabletInfo>();
		List<TabletServerStatsProvider> tservers = new ArrayList<TabletServerStatsProvider>();
		for(int i = 0; i < 10; i++) {
			tservers.add(new fakeTabletServerStatsProvider(new String("tserver"+i)));
		}
		int tservercnt = 0;
		int server = 0;
		for(int i=0; i < 100; i++) {
			KeyExtent ke = new KeyExtent(new Text("table"), new Text(new String(""+i)), null);
			extentMap.put(ke, new fakeTabletInfo(ke, tservers.get(server).getName()));
			if (server<5)
				tservers.get(server).getTabletsStats().add(new TabletRates(ke, 10, 10, 0, 0, FAKE, FAKE));
			else
				tservers.get(server).getTabletsStats().add(new TabletRates(ke, 10, 1000, 0, 0, FAKE, FAKE));
			tservercnt++;
			if (tservercnt % 10 == 0)
				server++;
		}
		Balance(tservers, extentMap, 500, -1, -2);
	}

	public void testBalanceOneLowTServer() {
		Map<KeyExtent, TabletInfo> extentMap = new HashMap<KeyExtent, TabletInfo>();
		List<TabletServerStatsProvider> tservers = new ArrayList<TabletServerStatsProvider>();
		for(int i = 0; i < 10; i++) {
			tservers.add(new fakeTabletServerStatsProvider(new String("tserver"+i)));
		}
		int tservercnt = 0;
		int server = 0;
		for(int i=0; i < 100; i++) {
			KeyExtent ke = new KeyExtent(new Text("table"), new Text(new String(""+i)), null);
			extentMap.put(ke, new fakeTabletInfo(ke, tservers.get(server).getName()));
			if (server==0)
				tservers.get(server).getTabletsStats().add(new TabletRates(ke, 10, 10, 0, 0, FAKE, FAKE));
			else
				tservers.get(server).getTabletsStats().add(new TabletRates(ke, 10, 1000, 0, 0, FAKE, FAKE));
			tservercnt++;
			if (tservercnt % 10 == 0)
				server++;
		}
		Balance(tservers, extentMap, 500, -1, -2);
	}

	public void testBalanceOneHighTServer() {
		Map<KeyExtent, TabletInfo> extentMap = new HashMap<KeyExtent, TabletInfo>();
		List<TabletServerStatsProvider> tservers = new ArrayList<TabletServerStatsProvider>();
		for(int i = 0; i < 10; i++) {
			tservers.add(new fakeTabletServerStatsProvider(new String("tserver"+i)));
		}
		int tservercnt = 0;
		int server = 0;
		for(int i=0; i < 100; i++) {
			KeyExtent ke = new KeyExtent(new Text("table"), new Text(new String(""+i)), null);
			extentMap.put(ke, new fakeTabletInfo(ke, tservers.get(server).getName()));
			if (server==0)
				tservers.get(server).getTabletsStats().add(new TabletRates(ke, 10, 1000, 0, 0, FAKE, FAKE));
			else
				tservers.get(server).getTabletsStats().add(new TabletRates(ke, 10, 10, 0, 0, FAKE, FAKE));
			tservercnt++;
			if (tservercnt % 10 == 0)
				server++;
		}
		Balance(tservers, extentMap, 500, -1, -2);
	}

	public void testBalanceBalanced() {
		Map<KeyExtent, TabletInfo> extentMap = new HashMap<KeyExtent, TabletInfo>();
		List<TabletServerStatsProvider> tservers = new ArrayList<TabletServerStatsProvider>();
		for(int i = 0; i < 30; i++) {
			tservers.add(new fakeTabletServerStatsProvider(new String("tserver"+i)));
		}
		int tservercnt = 0;
		int server = 0;
		for(int i=0; i < 300; i++) {
			KeyExtent ke = new KeyExtent(new Text("table"), new Text(new String(""+i)), null);
			extentMap.put(ke, new fakeTabletInfo(ke, tservers.get(server).getName()));
			tservers.get(server).getTabletsStats().add(new TabletRates(ke, 1000, 100000, 0, 0, FAKE, FAKE));
			tservercnt++;
			if (tservercnt % 10 == 0)
				server++;
		}
		Balance(tservers, extentMap, 1000, -1, -1);
	}

	public void testLocalityStartup() {
		Map<KeyExtent, TabletInfo> extentMap = new HashMap<KeyExtent, TabletInfo>();
		List<TabletServerStatsProvider> tservers = new ArrayList<TabletServerStatsProvider>();
		for(int i = 0; i < 5; i++) {
			tservers.add(new fakeTabletServerStatsProvider(new String("tserver"+i)));
		}
		int tservercnt = 0;
		int server = 0;
		for(int i=0; i < 10; i++) {
			KeyExtent ke = new KeyExtent(new Text("table"), new Text(new String(""+i)), null);
			extentMap.put(ke, new fakeTabletInfo(ke, tservers.get(server).getName()));
			tservercnt++;
			if (tservercnt % 2 == 0)
				server++;
		}
		for(int i=10; i < 15; i++) {
			KeyExtent ke = new KeyExtent(new Text("table"), new Text(new String(""+i)), null);
			extentMap.put(ke, new fakeTabletInfo(ke, "tserver10"));
		}
		for(int i=15; i < 20; i++) {
			KeyExtent ke = new KeyExtent(new Text("table"), new Text(new String(""+i)), null);
			extentMap.put(ke, new fakeTabletInfo(ke, null));
		}
		OLSLoadBalancer lb = new OLSLoadBalancer(0);
		for (KeyExtent ke : extentMap.keySet()) {
			TabletServerStatsProvider tssp = lb.getServerForTablet(ke, extentMap, tservers);
			tssp.getTabletsStats().add(new TabletRates(ke, 0, 0, 0, 0, FAKE, FAKE));
		}
		int total = 0;
		for (int i = 0; i < 5; i++) {
			List<TabletRates> trs = new ArrayList<TabletRates>(tservers.get(i).getTabletsStats());
			boolean one = false;
			boolean two = false;
			for (TabletRates rates : trs) {
				if (rates.key.equals(new KeyExtent(new Text("table"), new Text(new String(""+(i*2))), null)))
					one=true;
				if (rates.key.equals(new KeyExtent(new Text("table"), new Text(new String(""+(i*2+1))), null)))
					two=true;
			}
			assertTrue(one && two);
			total += trs.size();
		}
		assertTrue(total==20);
		dumpTserverLoads(tservers);
	}

	// provide extents and lists of tservers with pertinent info and it will do the rest
	public void Balance(List<TabletServerStatsProvider> tservers, Map<KeyExtent, ? extends TabletInfo> extentMap, int cycles, int specOne, int specTwo) {
		dumpTserverLoads(tservers);
		TabletServerStatsProvider first = null;
		TabletServerStatsProvider second = null;
		if (specOne > -1)
			first = tservers.get(specOne);
		if (specTwo > -1)
			second = tservers.get(specTwo);
		OLSLoadBalancer lb = new OLSLoadBalancer(0);
		int i;
		for (i = 0; i < cycles; i++) {
			if (specTwo == -2) {
				second = tservers.get(i % tservers.size());
			}
			if (specOne == -2) {
				first = tservers.get(i % tservers.size());
			}
			Set<TabletMigration> migrations = lb.getMigrations(extentMap, tservers, first, second);
			if (migrations==null) {
				if (verifyBalance(tservers))
					break;
				continue;
			}
			Iterator<TabletMigration> iter = migrations.iterator();
			if(!iter.hasNext()) {
				if (verifyBalance(tservers))
					break;
				continue;
			}
			TabletMigration mig = iter.next();
			TabletRates moved = null;
			for (TabletRates rates : mig.oldServer.getTabletsStats())
				if(rates.key.equals(mig.tablet)) {
					moved = rates;
					break;
				}
			mig.oldServer.getTabletsStats().remove(moved);
			mig.newServer.getTabletsStats().add(moved);
			lb.tabletServerStatusUpdated(mig.oldServer);
			lb.tabletServerStatusUpdated(mig.newServer);
			if (verifyBalance(tservers))
				break;
		}
		dumpTserverLoads(tservers);
		assertTrue(verifyBalance(tservers));
	}

	private boolean verifyBalance(List<TabletServerStatsProvider> tservers) {
		// If truly balanced, then we don't want to do any more migrations.
		// If we do, we aren't balanced
		for (int i=0; i < tservers.size()-1; i++) {
			for (int j = i; j < tservers.size(); j++) {
				TabletServerStatsProvider one, two;
				one = tservers.get(i);
				two = tservers.get(j);
				if(one.getLoad() < two.getLoad()) {
					TabletServerStatsProvider tmp = one;
					one = two;
					two = tmp;
				}
				if(OLSLoadBalancer.getBest(one, two)!=null)
					return false;
			}
		}
		return true;
	}

	private void dumpTserverLoads(List<TabletServerStatsProvider> tservers) {
	    boolean debug = false;
	    if (debug) {
	        for (TabletServerStatsProvider ts : tservers) {
	            System.out.println("Tserver " + ts.getName() + " has load of " + ts.getLoad() + " from " + ts.getTabletsStats().size() + " tablets");
	            for (TabletRates rates : ts.getTabletsStats())
	                System.out.print(rates.key.toString() + " ");
	            System.out.println();
	        }
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
	
		private String name;
		List<TabletRates> rates = new ArrayList<TabletRates>();
		
		public fakeTabletServerStatsProvider(String name) {
			this.name = name;
		}

		public double getLoad() {
			double load = 0;
			for (TabletRates rate : rates) {
				load+=TabletFunctions.getLoad(rate);
			}
			return load;
		}

		public String getName() {
			return name;
		}

		public TabletServerState getState() {
			return TabletServerState.ONLINE;
		}


		public List<TabletRates> getTabletsStats() {
			return rates;
		}

		public boolean statsUpToDate() {
			return true;
		}

	}
}
