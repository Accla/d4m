package cloudbase.core.master.balancer;

import cloudbase.core.data.KeyExtent;
import cloudbase.core.master.mgmt.TabletState;

public interface TabletInfo {
	KeyExtent getExtent();
	String getTabletServerName();
	String getDestTabletServerName();
	TabletState getState();
}
