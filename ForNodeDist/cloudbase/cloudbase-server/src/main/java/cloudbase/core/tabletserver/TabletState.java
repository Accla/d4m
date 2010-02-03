package cloudbase.core.tabletserver;

import cloudbase.core.data.KeyExtent;

public interface TabletState {
	KeyExtent getExtent();
	long getLastCommitTime();
	long getMemTableSize();
	long getMinorCompactingMemTableSize();
}
