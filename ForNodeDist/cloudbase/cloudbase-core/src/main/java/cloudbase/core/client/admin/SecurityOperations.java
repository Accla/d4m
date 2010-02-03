package cloudbase.core.client.admin;

import java.util.Set;

import cloudbase.core.client.CBException;
import cloudbase.core.client.CBSecurityException;
import cloudbase.core.client.Instance;
import cloudbase.core.client.impl.MasterClient;
import cloudbase.core.master.thrift.MasterClientService;
import cloudbase.core.security.SystemPermission;
import cloudbase.core.security.TablePermission;
import cloudbase.core.security.thrift.AuthInfo;
import cloudbase.core.security.thrift.ThriftSecurityException;

public class SecurityOperations
{
	private Instance instance;
	private AuthInfo credentials;

	public SecurityOperations(Instance instance, AuthInfo credentials)
	{
		this.instance = instance;
		this.credentials = credentials;
	}

	/**
	 * Create a user
	 * 
	 * @param user
	 * @param password
	 * @param authorizations
	 * @throws CBException
	 * @throws CBSecurityException
	 */
	public void createUser(String user, byte[] password, Set<Short> authorizations)
	throws CBException, CBSecurityException
	{
		MasterClientService.Client client = null;
		try {
			client = MasterClient.master_connect(instance);
			client.createUser(credentials, user, password, authorizations);
		} catch (ThriftSecurityException e) {
			throw new CBSecurityException(e.user, e.baduserpass, e);
		} catch (Exception e) {
			throw new CBException(e);
	    } finally {
			MasterClient.close(client);
		}
	}

	/**
	 * Delete a user
	 * 
	 * @param user
	 * @throws CBException
	 * @throws CBSecurityException
	 */
	public void dropUser(String user)
	throws CBException, CBSecurityException
	{
		MasterClientService.Client client = null;
		try {
			client = MasterClient.master_connect(instance);
			client.dropUser(credentials, user);
		} catch (ThriftSecurityException e) {
			throw new CBSecurityException(e.user, e.baduserpass, e);
		} catch (Exception e) {
			throw new CBException(e);
	    } finally {
			MasterClient.close(client);
		}
	}

	/**
	 * Verify a username/password combination is valid
	 * 
	 * @param user
	 * @param password
	 * @return if the user asking is allowed to know, then true if the specific user/password is valid, false if invalid
	 * @throws CBException
	 * @throws CBSecurityException thrown when the asking user is not authorized
	 */
	public boolean authenticateUser(String user, byte[] password)
	throws CBException, CBSecurityException
	{
		MasterClientService.Client client = null;
		try {
			client = MasterClient.master_connect(instance);
			return client.authenticateUser(credentials, user, password);
		} catch (ThriftSecurityException e) {
			throw new CBSecurityException(e.user, e.baduserpass, e);
		} catch (Exception e) {
			throw new CBException(e);
	    } finally {
			MasterClient.close(client);
		}
	}

	/**
	 * Set the user's password
	 * 
	 * @param user
	 * @param password
	 * @throws CBException
	 * @throws CBSecurityException
	 */
	public void changeUserPassword(String user, byte[] password)
	throws CBException, CBSecurityException
	{
		MasterClientService.Client client = null;
		try {
			client = MasterClient.master_connect(instance);
			client.changePassword(credentials, user, password);
		} catch (ThriftSecurityException e) {
			throw new CBSecurityException(e.user, e.baduserpass, e);
		} catch (Exception e) {
			throw new CBException(e);
	    } finally {
			MasterClient.close(client);
		}
	}

	/**
	 * Set the user's record-level authorizations
	 * 
	 * @param user
	 * @param auths
	 * @throws CBException
	 * @throws CBSecurityException
	 */
	public void changeUserAuthorizations(String user, Set<Short> authorizations)
	throws CBException, CBSecurityException
	{
		MasterClientService.Client client = null;
		try {
			client = MasterClient.master_connect(instance);
			client.changeAuthorizations(credentials, user, authorizations);
		} catch (ThriftSecurityException e) {
			throw new CBSecurityException(e.user, e.baduserpass, e);
		} catch (Exception e) {
			throw new CBException(e);
	    } finally {
			MasterClient.close(client);
		}
	}

	/**
	 * Verify the user has a particular system permission
	 * 
	 * @param user
	 * @return
	 * @throws CBException
	 * @throws CBSecurityException
	 */
	public boolean hasSystemPermission(String user, SystemPermission perm)
	throws CBException, CBSecurityException
	{
		MasterClientService.Client client = null;
		try {
			client = MasterClient.master_connect(instance);
			return client.hasSystemPermission(credentials, user, perm.getId());
		} catch (ThriftSecurityException e) {
			throw new CBSecurityException(e.user, e.baduserpass, e);
		} catch (Exception e) {
			throw new CBException(e);
	    } finally {
			MasterClient.close(client);
		}
	}

	/**
	 * Verify the user has a particular table permission
	 * 
	 * @param user
	 * @param table
	 * @return
	 * @throws CBException
	 * @throws CBSecurityException
	 */
	public boolean hasTablePermission(String user, String table, TablePermission perm)
	throws CBException, CBSecurityException
	{
		MasterClientService.Client client = null;
		try {
			client = MasterClient.master_connect(instance);
			return client.hasTablePermission(credentials, user, table, perm.getId());
		} catch (ThriftSecurityException e) {
			throw new CBSecurityException(e.user, e.baduserpass, e);
		} catch (Exception e) {
			throw new CBException(e);
	    } finally {
			MasterClient.close(client);
		}
	}

	/**
	 * Grant a user a system permission
	 * 
	 * @param user
	 * @param permission
	 * @throws CBException
	 * @throws CBSecurityException
	 */
	public void grantSystemPermission(String user, SystemPermission permission)
	throws CBException, CBSecurityException
	{
		MasterClientService.Client client = null;
		try {
			client = MasterClient.master_connect(instance);
			client.grantSystemPermission(credentials, user, permission.getId());
		} catch (ThriftSecurityException e) {
			throw new CBSecurityException(e.user, e.baduserpass, e);
		} catch (Exception e) {
			throw new CBException(e);
	    } finally {
			MasterClient.close(client);
		}
	}

	/**
	 * Grant a user a specific permission for a specific table
	 * 
	 * @param user
	 * @param table
	 * @param permission
	 * @throws CBException
	 * @throws CBSecurityException
	 */
	public void grantTablePermission(String user, String table, TablePermission permission)
	throws CBException, CBSecurityException
	{
		MasterClientService.Client client = null;
		try {
			client = MasterClient.master_connect(instance);
			client.grantTablePermission(credentials, user, table, permission.getId());
		} catch (ThriftSecurityException e) {
			throw new CBSecurityException(e.user, e.baduserpass, e);
		} catch (Exception e) {
			throw new CBException(e);
	    } finally {
			MasterClient.close(client);
		}
	}

	/**
	 * Revoke a system permission from a user
	 * 
	 * @param user
	 * @param permission
	 * @throws CBException
	 * @throws CBSecurityException
	 */
	public void revokeSystemPermission(String user, SystemPermission permission)
	throws CBException, CBSecurityException
	{
		MasterClientService.Client client = null;
		try {
			client = MasterClient.master_connect(instance);
			client.revokeSystemPermission(credentials, user, permission.getId());
		} catch (ThriftSecurityException e) {
			throw new CBSecurityException(e.user, e.baduserpass, e);
		} catch (Exception e) {
			throw new CBException(e);
	    } finally {
			MasterClient.close(client);
		}
	}

	/**
	 * Revoke a table permission for a specific user on a specific table
	 * 
	 * @param user
	 * @param table
	 * @param permission
	 * @throws CBException
	 * @throws CBSecurityException
	 */
	public void revokeTablePermission(String user, String table, TablePermission permission)
	throws CBException, CBSecurityException
	{
		MasterClientService.Client client = null;
		try {
			client = MasterClient.master_connect(instance);
			client.revokeTablePermission(credentials, user, table, permission.getId());
		} catch (ThriftSecurityException e) {
			throw new CBSecurityException(e.user, e.baduserpass, e);
		} catch (Exception e) {
			throw new CBException(e);
	    } finally {
			MasterClient.close(client);
		}
	}

	/**
	 * Return a list of users in cloudbase
	 * 
	 * @return
	 * @throws CBException 
	 * @throws CBSecurityException 
	 */
	public Set<String> listUsers()
	throws CBException, CBSecurityException
	{
		MasterClientService.Client client = null;
		try {
			client = MasterClient.master_connect(instance);
			return client.listUsers(credentials);
		} catch (ThriftSecurityException e) {
			throw new CBSecurityException(e.user, e.baduserpass, e);
		} catch (Exception e) {
			throw new CBException(e);
	    } finally {
			MasterClient.close(client);
		}
	}

}
