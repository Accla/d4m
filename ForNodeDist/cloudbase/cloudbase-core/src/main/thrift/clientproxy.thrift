namespace java cloudbase.core.client.proxy.thrift
namespace rb Cloudbase.Proxy

include "data.thrift"
include "master.thrift"
include "tabletserver.thrift"
include "security.thrift"

exception NoSuchTableException {
}

struct Column {
	1:binary          columnFamily,
	2:binary          columnQualifier,
	3:list<list<i16>> columnVisibility,
}

/* Changes to a table are called Mutations.  Every mutation takes place at a single row.
 * However, there may be changes to multiple columns on that row.  This structure defines
 * the modification to be made to a single column.  If "deleted" is true, then value is
 * ignored, and the item is deleted.
 */
struct ColumnUpdate {
  1:Column column;
  2:i64 timestamp;
  3:binary value;
  4:bool deleted;
}

/*
 * Modify columns at a single row.
 */
struct Mutation {
  1:binary row;
  2:list<ColumnUpdate> updates;
}

/*
 * The location of a value in a table.
 */
struct Key {
  1:binary row;
  2:Column column;
  3:i64 timestamp;              /* zero means that timestamp is not set */
}

/*
 * Represent a the start and stop points of a scan of a table.
 */
struct Range {
  1:Key start;
  2:bool startInclusive;             /* include the key in the result? */
  3:Key stop;
  4:bool stopInclusive;             /* include the key in the result? */
}

/*
 * An element of a scan: a pointer into the table and the value
 */
struct KeyValue {
  1:Key key;
  2:binary value;
}

/*
 * Scan results come back in chunks called a ScanResult.  Keep fetching until more is false.
 */
struct ScanResult {
	1:list<KeyValue> data,
	2:bool more
}

/*
 * After initiating a scan, you get a scan id that identifies this scan in the Proxy which
 * can be used to fetch the rest of the scan.
 */
struct InitialScan {
	1:data.ScanID scanID,
	2:ScanResult result
}

service ClientProxy {

  // Master Service Calls
  // -----------------------------------------------------------------------
  /*
   * Create a new table with the given pre-existing split points.
   */
  void createTable(1:string tableName, 
                   2:list<binary> splitPoints) 
                throws (1:security.ThriftSecurityException sec, 2:master.TableCreationException tce);
        
  /*
   * Delete a table from the database.
   */
  void deleteTable(1:string tableName) 
                throws (1:security.ThriftSecurityException sec, 2:master.TableDeletionException tde);
        
  /*
   * Shutdown the master server, and optionally, all the tablet servers.
   */
  async void shutdown(1:bool stopTabletServers);

  /* Test for master liveness */
  void ping() 
                throws (1:security.ThriftSecurityException sec)


  /*
   * Get a list of all the tables
   */
  set<string> getTables()
                throws (1:security.ThriftSecurityException sec)


   	/*
   	* Sets per table configs
   	*/
   	bool setTableProperty(1:string tablename
   							2:string property
   							3:string value)
                throws (1:security.ThriftSecurityException sec)

   	/*
   	* Removes a per table configs
   	*/
   	bool removeTableProperty(1:string tablename
   							2:string property
   							)
                throws (1:security.ThriftSecurityException sec)

       
       
  // Scanner Calls
  // -----------------------------------------------------------------------
  
  /*
   * Fetch all the values in a table between two points.  Return results in "batchSize" chunks.
   */
  InitialScan startScan(1:string tablename, 
                        2:Range range, 
                        3:list<Column> columns, 
                        4:i32 batchSize)
                throws (1:security.ThriftSecurityException sec, 2:NoSuchTableException nst);
            
  /*
   * Fetch more data from a scan.
   */
  ScanResult continueScan(1:data.ScanID scanID) 
    throws (1:tabletserver.NoSuchScanIDException nssi, 
            2:NoSuchTableException nst);
            
  /*
   * Release the scanner in the proxy.
   */
  async void closeScan(1:data.ScanID scanID);
        
  // Batch Reader
  // -----------------------------------------------------------------------
  
  /*
   * Fetch data from multiple ranges at the same time.
   */
  data.ScanID lookup(1:string tablename, 
                     2:list<Range> ranges, 
                     3:list<Column> columns, 
                     4:list<i32> authorizations);
  /*
   * Get another batch.
   */
  ScanResult fetch(1:data.ScanID scanID) 
    throws (1:tabletserver.NoSuchScanIDException nssi);
  async void closeBatch(1:data.ScanID scanID);
        
  // Mutations
  // -----------------------------------------------------------------------
  /*
   * Modify the table with a set of mutations
   */
  data.UpdateErrors update(1:string tableName,
                           2:list<Mutation> updates) 
                throws (1:security.ThriftSecurityException sec, 2:NoSuchTableException nst);
        
}
