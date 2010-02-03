package cloudbase.core.master.mgmt;

public enum TabletState {
	UNASSIGNED,
	ASSIGNED,
	ASSIGNED_BAD_SERVER,
	MIGRATING,
	SPLIT,
	DELETED
}
