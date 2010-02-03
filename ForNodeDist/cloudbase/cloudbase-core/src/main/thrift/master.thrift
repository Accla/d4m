namespace java cloudbase.core.master.thrift

include "data.thrift"
include "security.thrift"

struct Compacting {
  1:bool compacting,
  2:bool queued
}

struct TabletRates {
    1:data.KeyExtent key,
    2:double tableQueryRate,
    3:double tableIngestRate,
    4:i64 records,
    5:i64 recordsInMemory,
    6:Compacting minor,
    7:Compacting major
}

struct TabletServerStatus {
    1:double queryRate,
    2:double ingestRate,
    3:list<TabletRates> tabletRates,
    4:i64 totalRecords,
    5:i64 lastContact
    6:string URL,
    7:string name,
    8:double load,
    9:double osLoad,
    10:i64 tabletServerTime
}

struct KeyExtentLocation {
    1:data.KeyExtent extent,
    2:binary location
}

struct TabletSplit {
    1:data.KeyExtent oldTablet,
    2:list<KeyExtentLocation> keyExtentLocations
}

service MasterTabletService {

    void reportTabletStatus(1:security.AuthInfo credentials, 2:string serverName, 3:data.KeyExtent extent, 4:i32 status),
    
    void reportTabletUnloaded(1:security.AuthInfo credentials, 2:data.KeyExtent extent),
    
    void reportTabletList(1:security.AuthInfo credentials, 
                          2:string serverName, 
                          3:list<data.KeyExtent> extents,
                          4:string clientServiceLocation,
                          5:string monitorServiceLocation,
                          6:i64 tabletServerTime),
    
    void reportSplitExtent(1:security.AuthInfo credentials, 2:string serverName, 3:TabletSplit split),
    
    void pong(1:security.AuthInfo credentials, 2:string serverName, 3:TabletServerStatus status),
    
    void reportShutdown(1:security.AuthInfo credentials, 2:string serverName, 3:i32 stage)
}

exception TableCreationException {
    1:string table,
    2:bool exists
}

exception TableDeletionException {
    1:string table,
    2:bool exists
}

service MasterClientService {
    
    // zero-length split points means create the table with one default tablet
    void createTable(1:security.AuthInfo credentials, 2:string table, 3:list<binary> splitPoints) throws (1:security.ThriftSecurityException sec, 2:TableCreationException tcek)
    
    void deleteTable(1:security.AuthInfo credentials, 2:string table) throws (1:security.ThriftSecurityException sec, 2:TableDeletionException tde)
    
    void shutdown(1:security.AuthInfo credentials, 2:bool stopTabletServers) throws (1:security.ThriftSecurityException sec)
    
    void ping(1:security.AuthInfo credentials) throws (1:security.ThriftSecurityException sec)
    
    void flush(1:security.AuthInfo credentials, 2:string table) throws (1:security.ThriftSecurityException sec)
    
    bool setTableProperty(1:security.AuthInfo credentials, 2:string table, 3:string property, 4:string value) throws (1:security.ThriftSecurityException sec)

    bool removeTableProperty(1:security.AuthInfo credentials, 2:string table, 3:string property) throws (1:security.ThriftSecurityException sec)

	// security methods
    bool authenticateUser(1:security.AuthInfo credentials, 2:string user, 3:binary password) throws (1:security.ThriftSecurityException sec)

    set<string> listUsers(1:security.AuthInfo credentials) throws (1:security.ThriftSecurityException sec)
    
    void createUser(1:security.AuthInfo credentials, 2:string user, 3:binary password, 4:set<i16> authorizations) throws (1:security.ThriftSecurityException sec)
    void dropUser(1:security.AuthInfo credentials, 2:string user) throws (1:security.ThriftSecurityException sec)
    void changePassword(1:security.AuthInfo credentials, 2:string user, 3:binary password) throws (1:security.ThriftSecurityException sec)
    void changeAuthorizations(1:security.AuthInfo credentials, 2:string user, 3:set<i16> authorizations) throws (1:security.ThriftSecurityException sec)
    set<i16> getUserAuthorizations(1:security.AuthInfo credentials, 2:string user) throws (1:security.ThriftSecurityException sec)

    bool hasSystemPermission(1:security.AuthInfo credentials, 2:string user, 3:i32 sysPerm) throws (1:security.ThriftSecurityException sec)
    bool hasTablePermission(1:security.AuthInfo credentials, 2:string user, 3:string table, 4:i32 tblPerm) throws (1:security.ThriftSecurityException sec)

    void grantSystemPermission(1:security.AuthInfo credentials, 2:string user, 3:i32 permission) throws (1:security.ThriftSecurityException sec)
    void revokeSystemPermission(1:security.AuthInfo credentials, 2:string user, 3:i32 permission) throws (1:security.ThriftSecurityException sec)
    void grantTablePermission(1:security.AuthInfo credentials, 2:string user, 3:string table, 4:i32 permission) throws (1:security.ThriftSecurityException sec)
    void revokeTablePermission(1:security.AuthInfo credentials, 2:string user, 3:string table, 4:i32 permission) throws (1:security.ThriftSecurityException sec)
    
    string getRootTabletLocation()
    
    string getInstanceId()
    
    set<string> getTables(1:security.AuthInfo credentials) throws (1:security.ThriftSecurityException sec)
}
