package cloudbase.core.security;

import java.util.Set;

import cloudbase.core.client.CBSecurityException;
import cloudbase.core.security.thrift.AuthInfo;

public interface Authenticator
{
	public void initializeRootUser(AuthInfo credentials, String user, byte[] pass) throws CBSecurityException;
	public String getRootUsername();
	
	public boolean authenticateUser(AuthInfo credentials, String user, byte[] pass) throws CBSecurityException;

	public Set<String> listUsers(AuthInfo credentials) throws CBSecurityException;

	public void createUser(AuthInfo credentials, String user, byte[] pass, Set<Short> authorizations) throws CBSecurityException;
	public void dropUser(AuthInfo credentials, String user) throws CBSecurityException;
	public void changePassword(AuthInfo credentials, String user, byte[] pass) throws CBSecurityException;
	public void changeAuthorizations(AuthInfo credentials, String user, Set<Short> authorizations) throws CBSecurityException;
    public Set<Short> getUserAuthorizations(AuthInfo credentials, String user) throws CBSecurityException;

    public boolean hasSystemPermission(AuthInfo credentials, String user, SystemPermission permission) throws CBSecurityException;
    public boolean hasTablePermission(AuthInfo credentials, String user, String table, TablePermission permission) throws CBSecurityException;

    public void grantSystemPermission(AuthInfo credentials, String user, SystemPermission permission) throws CBSecurityException;
    public void revokeSystemPermission(AuthInfo credentials, String user, SystemPermission permission) throws CBSecurityException;
    public void grantTablePermission(AuthInfo credentials, String user, String table, TablePermission permission) throws CBSecurityException;
    public void revokeTablePermission(AuthInfo credentials, String user, String table, TablePermission permission) throws CBSecurityException;
	public void deleteTable(AuthInfo credentials, String table) throws CBSecurityException;
}
