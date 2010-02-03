package cloudbase.core.master.balancer;

import java.util.List;

import cloudbase.core.master.mgmt.TabletServerState;
import cloudbase.core.master.thrift.TabletRates;

public interface TabletServerStatsProvider {
	String getName();
	double getLoad();
	TabletServerState getState();
	List<TabletRates> getTabletsStats();
	boolean statsUpToDate();
}
