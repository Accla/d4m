package cloudbase.core.master.balancer;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import cloudbase.core.data.KeyExtent;

/**
 * Each load balancer returns a migration or null, meaning no migration recommended
 * 
 *
 */
public interface LoadBalancer {
	
	public void tabletServerStatusUpdated(TabletServerStatsProvider server);
	public void tabletDeleted(KeyExtent ke);
	
	public Set<TabletMigration> getMigrations(Map<KeyExtent, ? extends TabletInfo> tablets, Collection<? extends TabletServerStatsProvider> servers);
	public TabletServerStatsProvider getServerForTablet(KeyExtent tablet, Map<KeyExtent, ? extends TabletInfo> tablets, Collection<? extends TabletServerStatsProvider> servers);
}
