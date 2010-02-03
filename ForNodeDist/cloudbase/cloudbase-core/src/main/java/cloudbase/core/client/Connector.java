package cloudbase.core.client;

import java.util.Set;

import cloudbase.core.client.admin.SecurityOperations;
import cloudbase.core.client.admin.TableOperations;
import cloudbase.core.client.impl.BatchWriterImpl;
import cloudbase.core.client.impl.MasterClient;
import cloudbase.core.client.impl.MultiTableBatchWriterImpl;
import cloudbase.core.client.impl.ScannerImpl;
import cloudbase.core.client.impl.TabletServerBatchReader;
import cloudbase.core.master.thrift.MasterClientService;
import cloudbase.core.security.thrift.AuthInfo;
import cloudbase.core.security.thrift.ThriftSecurityException;

/**
 * Connector facilitates connecting to a Cloudbase instance. One
 * of the main purposes of this class is to make it easy to connect
 * to multiple Cloudbase instances within the same JVM.
 * 
 * Additionally, the Connector object enforces security on the client
 * side, by forcing all API calls to be accompanied by user credentials.
 * 
 * This Connector requires a master to be running to be instantiated,
 * and for certain calls, such as getTables()
 */
public class Connector
{
    private Instance instance;
    private AuthInfo credentials;

    /**
     * Construct an Connector from an Instance.
     * 
     * @param instance
     * @param user
     * @param password
     * @throws CBException 
     * @throws CBSecurityException 
     */
    public Connector(Instance instance, String user, byte[] password)
    throws CBException, CBSecurityException
    {
        this.instance = instance;
        this.credentials = new AuthInfo(user, password);

	    MasterClientService.Client client = null;
	    try {
	        client = MasterClient.master_connect(instance);
			client.authenticateUser(this.credentials, user, password);
		} catch (ThriftSecurityException e) {
			throw new CBSecurityException(e.user, e.baduserpass, e);
		} catch (Exception e) {
			throw new CBException(e);
	    } finally {
			MasterClient.close(client);
		}
    }
    
    /**
     * Create a Connector from a list of masters.
     * 
     * @param masters - a comma separated list of hostnames. Each host may optionally be followed by a colon and a port number
     * @param user
     * @param password
     * @throws CBException 
     * @throws CBSecurityException 
     */
    public Connector(String masters, String user, byte[] password)
    throws CBException, CBSecurityException
    {
        this(new MasterInstance(masters), user, password);
    }

    /**
     * Factory method to create a BatchScanner connected to Cloudbase.
     *  
     * @param table
     * @param authorizations
     * @param numQueryThreads
     * @return BatchScanner object
     * @throws CBException 
     * @throws CBSecurityException 
     * @throws TableNotFoundException 
     */
    public BatchScanner createBatchScanner(String table, Set<Short> authorizations, int numQueryThreads)
    throws CBException, CBSecurityException, TableNotFoundException
    {
        return new TabletServerBatchReader(instance, credentials, table, authorizations, numQueryThreads);
    }
    
    /**
     * Factory method to create a BatchWriter connected to Cloudbase.
     *  
     * @param table
     * @param maxMemory
     * @param maxLatency
     * @param maxWriteThreads
     * @return BatchWriter
     * @throws CBException 
     * @throws CBSecurityException 
     * @throws TableNotFoundException 
     */
    public BatchWriter createBatchWriter(String table, int maxMemory, int maxLatency, int maxWriteThreads)
    throws CBException, CBSecurityException, TableNotFoundException
    {
		return new BatchWriterImpl(instance, credentials, table, maxMemory, maxLatency, maxWriteThreads);
    }
    
    /**
     * Factory method to create a Multi-Table BatchWriter connected to Cloudbase.
     *  
     * @param maxMemory
     * @param maxLatency
     * @param maxWriteThreads
     * @return MultiTableBatchWriter
     * @throws CBException 
     * @throws CBSecurityException 
     */
    public MultiTableBatchWriter createMultiTableBatchWriter(long maxMemory, int maxLatency, int maxWriteThreads)
    throws CBException, CBSecurityException
    {
        return new MultiTableBatchWriterImpl(instance, credentials, maxMemory, maxLatency, maxWriteThreads);
    }
    
    /**
     * Factory method to create a Scanner connected to Cloudbase.
     * 
     * @param table
     * @param authorizations
     * @return Scanner object
     * @throws CBException 
     * @throws CBSecurityException 
     * @throws TableNotFoundException 
     */
    public Scanner createScanner(String table, Set<Short> authorizations)
    throws CBException, CBSecurityException, TableNotFoundException
    {
		return new ScannerImpl(instance, credentials, table, authorizations);
    }

	/** 
     * Accessor method for internal instance object.
     */
    public Instance getInstance()
    {
        return this.instance;
    }

    /**
     * Get the current user for this connector
     * 
     * @return the user name
     */
	public String whoami()
	{
		return credentials.user;
	}

	/**
	 * Retrieves a TableOperations object to perform table functions, such as create and delete
	 * 
	 * @return
	 */
	public TableOperations tableOperations()
	{
		return new TableOperations(instance, credentials);
	}
	
	/**
	 * Retrieves a SecurityOperations object to perform user security operations, such as creating users
	 * 
	 * @return
	 */
	public SecurityOperations securityOperations()
	{
		return new SecurityOperations(instance, credentials);
	}
}
