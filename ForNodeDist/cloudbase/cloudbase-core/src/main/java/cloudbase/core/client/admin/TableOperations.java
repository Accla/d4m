package cloudbase.core.client.admin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.hadoop.io.Text;

import cloudbase.core.CBConstants;
import cloudbase.core.aggregation.Aggregator;
import cloudbase.core.client.CBException;
import cloudbase.core.client.CBSecurityException;
import cloudbase.core.client.Instance;
import cloudbase.core.client.TableExistsException;
import cloudbase.core.client.TableNotFoundException;
import cloudbase.core.client.impl.MasterClient;
import cloudbase.core.master.thrift.MasterClientService;
import cloudbase.core.master.thrift.TableCreationException;
import cloudbase.core.master.thrift.TableDeletionException;
import cloudbase.core.security.thrift.AuthInfo;
import cloudbase.core.security.thrift.ThriftSecurityException;
import cloudbase.core.tabletserver.iterators.IteratorUtil;
import cloudbase.core.util.TextUtil;

public class TableOperations
{
	private Instance instance;
	private AuthInfo credentials;

	public TableOperations(Instance instance, AuthInfo credentials)
	{
		this.instance = instance;
		this.credentials = credentials;
	}
	
    /**
     * Retrieve a list of tables in Cloudbase.
     * 
     * @return List of tables in cloudbase
     * @throws CBException 
     * @throws CBSecurityException 
     */
    public SortedSet<String> list()
    throws CBException, CBSecurityException
	{
	    MasterClientService.Client client = null;
	    try {
	        client = MasterClient.master_connect(instance);
	        return new TreeSet<String>(client.getTables(credentials));
		} catch (ThriftSecurityException e) {
			throw new CBSecurityException(e.user, e.baduserpass, e);
		} catch (Exception e) {
			throw new CBException(e);
	    } finally {
			MasterClient.close(client);
		}
	}

    /**
     * A method to check if a table exists in Cloudbase.
     * 
     * @param table
     * @return boolean - whether the given table exists
     * @throws CBException
     * @throws CBSecurityException
     */
	public boolean exists(String table)
	throws CBException, CBSecurityException
	{
		return table.equals(CBConstants.METADATA_TABLE_NAME) || list().contains(table);
	}

	/**
	 * Create a table
	 * 
	 * @param table
	 * @throws CBException
	 * @throws CBSecurityException
	 * @throws TableExistsException
	 */
	public void create(String table)
	throws CBException, CBSecurityException, TableExistsException
    {
		create(table, new TreeSet<Text>());
	}

	/**
	 * Create a table
	 * 
	 * @param table
	 * @param partitionKeys
	 * @throws CBException
	 * @throws CBSecurityException
	 * @throws TableExistsException
	 */
	public void create(String table, SortedSet<Text> partitionKeys)
	throws CBException, CBSecurityException, TableExistsException
    {
		Map<Text, Class<? extends Aggregator>> emptyMap = Collections.emptyMap();
		create(table, partitionKeys, emptyMap);
	}

	/**
	 * Create a table
	 * 
	 * @param table
	 * @param aggregators
	 * @throws CBException
	 * @throws CBSecurityException
	 * @throws TableExistsException
	 */
	public void create(String table, Map<Text, Class<? extends Aggregator>> aggregators)
	throws CBException, CBSecurityException, TableExistsException
    {
		create(table, new TreeSet<Text>(), aggregators);
	}
	
	/**
	 * Create a table
	 * 
	 * @param table
	 * @param partitionKeys
	 * @param aggregators
	 * @throws CBException
	 * @throws CBSecurityException
	 * @throws TableExistsException
	 */
	public void create(String table, SortedSet<Text> partitionKeys, Map<Text, Class<? extends Aggregator>> aggregators)
	throws CBException, CBSecurityException, TableExistsException
    {
		MasterClientService.Client client = null;
		
		try {
			if (!IteratorUtil.setupInitialIterators(instance, credentials, table, aggregators))
				throw new IOException("Error in master trying to write properties");
	
			ArrayList<byte[]> pkl = new ArrayList<byte[]>();
			
			for (Text text : partitionKeys)
				pkl.add(TextUtil.getBytes(text));
			
			client = MasterClient.master_connect(instance);
			client.createTable(credentials, table, pkl);
		} catch (ThriftSecurityException e) {
			throw new CBSecurityException(e.user, e.baduserpass, e);
		} catch (TableCreationException e) {
			if (e.exists)
				throw new TableExistsException(e.table, e);
			else
				throw new CBException(e.table, e);
		} catch (Exception e) {
			throw new CBException(e);
	    } finally {
			MasterClient.close(client);
		}
	}
	
	/**
	 * Delete a table
	 * 
	 * @param table
	 * @throws CBException
	 * @throws CBSecurityException
	 * @throws TableNotFoundException
	 */
	public void delete(String table)
	throws CBException, CBSecurityException, TableNotFoundException
	{
		MasterClientService.Client client = null;
		try {
			client = MasterClient.master_connect(instance);
			client.deleteTable(credentials, table);
		} catch (ThriftSecurityException e) {
			throw new CBSecurityException(e.user, e.baduserpass, e);
		} catch (TableDeletionException e) {
			if (!e.exists)
				throw new TableNotFoundException(e.table, e);
			else
				throw new CBException(e.table, e);
		} catch (Exception e) {
			throw new CBException(e);
	    } finally {
			MasterClient.close(client);
		}
	}

	/**
	 * Flush a table
	 * 
	 * @param table
	 * @throws CBException
	 * @throws CBSecurityException
	 */
	public void flush(String table)
	throws CBException, CBSecurityException
    {
		MasterClientService.Client client = null;
		try {
			client = MasterClient.master_connect(instance);
			client.flush(credentials, table);
		} catch (ThriftSecurityException e) {
			throw new CBSecurityException(e.user, e.baduserpass, e);
		} catch (Exception e) {
			throw new CBException(e);
	    } finally {
			MasterClient.close(client);
		}
	}

	/**
	 * Sets a property on a table
	 * 
	 * @param table
	 * @param property
	 * @param value
	 * @throws CBException
	 * @throws CBSecurityException
	 */
	public void setProperty(String table, String property, String value)
	throws CBException, CBSecurityException
	{
		MasterClientService.Client client = null;
		try {
			client = MasterClient.master_connect(instance);
			client.setTableProperty(credentials, table, property, value);
		} catch (ThriftSecurityException e) {
			throw new CBSecurityException(e.user, e.baduserpass, e);
		} catch (Throwable t) {
			throw new CBException(t);
		} finally {
				MasterClient.close(client);
		}
	}

	/**
	 * Removes a property from a table
	 * 
	 * @param table
	 * @param property
	 * @throws CBException
	 * @throws CBSecurityException
	 */
	public void removeProperty(String table, String property)
	throws CBException, CBSecurityException
	{
		MasterClientService.Client client = null;
		try {
			client = MasterClient.master_connect(instance);
			client.removeTableProperty(credentials, table, property);
		} catch (ThriftSecurityException e) {
			throw new CBSecurityException(e.user, e.baduserpass, e);
		} catch (Throwable t) {
			throw new CBException(t);
		} finally {
				MasterClient.close(client);
		}
	}

}
