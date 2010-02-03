namespace java cloudbase.core.tabletserver.thrift
namespace rb Cloudbase.Proxy

include "data.thrift"
include "security.thrift"

exception NotServingTabletException {
	1:data.KeyExtent extent
}

exception NoSuchScanIDException {
}

exception ConstraintViolationException {
	1:list<data.ConstraintViolationSummary> violationSummaries
}

service TabletMasterService {

	async void sendTabletList(1:security.AuthInfo credentials),
	
	async void loadTablet(1:security.AuthInfo credentials, 2:data.KeyExtent extent, 3:binary location),
	
	async void unloadTablet(1:security.AuthInfo credentials, 2:data.KeyExtent extent, 3:bool save),
	
	async void shutdown(1:security.AuthInfo credentials, 2:string serverName, 3:i32 stage),
	
	async void ping(1:security.AuthInfo credentials),
	
	async void flush(1:security.AuthInfo credentials, set<string> tables)
}

service TabletClientService {
	
	// scan a range of keys
	data.InitialScan startScan(1:security.AuthInfo credentials,
	                           2:data.KeyExtent extent,
	                           3:data.Range range,
	                           4:list<data.Column> columns,
	                           5:i32 batchSize,
	                           6:list<data.IterInfo> ssiList,
	                           7:map<string, map<string, string> > ssio,
	                           8:set<i16> authorizations)  throws (1:security.ThriftSecurityException sec, 2:NotServingTabletException nste),

	data.ScanResult continueScan(1:data.ScanID scanID)  throws (1:NoSuchScanIDException nssi, 2:NotServingTabletException nste),

	async void closeScan(1:data.ScanID scanID),
	
	// scan over a series of ranges
	data.InitialScan startMultiScan(1:security.AuthInfo credentials,
	                                2:data.ScanBatch batch,
	                                3:list<data.Column> columns,
	                                4:list<data.IterInfo> ssiList,
	                                5:map<string, map<string, string> > ssio,
                                    6:set<i16> authorizations)  throws (1:security.ThriftSecurityException sec),

	data.ScanResult continueMultiScan(1:data.ScanID scanID) throws (1:NoSuchScanIDException nssi),
	data.ScanBatch closeMultiScan(1:data.ScanID scanID) throws (1:NoSuchScanIDException nssi),
	
	//the following calls support a batch update to multiple tablets on a tablet server
	data.UpdateID startUpdate(1:security.AuthInfo credentials) throws (1:security.ThriftSecurityException sec),
	async void setUpdateTablet(1:data.UpdateID updateID, 2:data.KeyExtent keyExtent),
	async void applyUpdate(1:data.UpdateID updateID, 2:data.Mutation mutation),
	data.UpdateErrors closeUpdate(1:data.UpdateID updateID) throws (1:NoSuchScanIDException nssi),
	
	//the following call supports making a single update to a tablet
	void update(1:security.AuthInfo credentials, 2:data.KeyExtent keyExtent, 3:data.Mutation mutation)
	  throws (1:security.ThriftSecurityException sec, 2:NotServingTabletException nste, 3:ConstraintViolationException cve),
	
	// on success, returns an empty list
	list<data.KeyExtent> bulkImport(1:security.AuthInfo credentials, 2:data.TabletFiles files) throws (1:security.ThriftSecurityException sec),
}
