package cloudbase.core.client;


/**
 * This class enables efficient batch writing to multiple tables. When
 * creating a batch writer for each table, each has its own memory and
 * network resources. Using this class these resource may be shared 
 * among multiple tables.  
 * 
 */
public interface MultiTableBatchWriter {

	public BatchWriter getBatchWriter(String table) throws CBException, CBSecurityException, TableNotFoundException;
	
	/**
	 * Send mutations for all tables to cloudbase. 
	 */
	public void flush();
	
	/**
	 * Flush and release all resources.
	 * 
	 */
	public void close() throws MutationsRejectedException;
}
