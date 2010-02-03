namespace java cloudbase.core.monitor.thrift

include "master.thrift"


struct TableInfo {
        1:i64 recs;
        2:i64 recsInMemory;
        3:i32 tablets;
        4:i32 onlineTablets;
        5:double ingestRate;
        6:double queryRate;
}


struct MasterMonitorInfo {
        1:map<string, TableInfo > tableMap,
        2:list<master.TabletServerStatus > tServerInfo,
        3:map<string, i16> badTServers,
}

service MasterMonitorService { 
	 
	 MasterMonitorInfo getMasterStats()
	 
}
