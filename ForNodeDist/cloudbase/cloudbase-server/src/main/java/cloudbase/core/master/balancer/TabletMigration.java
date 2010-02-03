package cloudbase.core.master.balancer;

import cloudbase.core.data.KeyExtent;

public class TabletMigration {
	public KeyExtent tablet;
	public TabletServerStatsProvider oldServer;
	public TabletServerStatsProvider newServer;
	
	public TabletMigration(KeyExtent tablet, TabletServerStatsProvider oldServer, TabletServerStatsProvider newServer) {
		this.tablet = tablet;
		this.oldServer = oldServer;
		this.newServer = newServer;
	}
}
