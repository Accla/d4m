package cloudbase.core.master.balancer;

import cloudbase.core.data.KeyExtent;
import cloudbase.core.master.mgmt.TabletState;

public interface TabletStats {
	TabletState getState();
	KeyExtent getExtent();
	double getQueryRate();
	double getIngestRate();
}
