package cloudbase.core.security;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.Map.Entry;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.WritableComparator;
import org.apache.hadoop.io.WritableUtils;
import org.apache.log4j.Logger;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;

import cloudbase.core.CBConstants;
import cloudbase.core.client.CBSecurityException;
import cloudbase.core.security.thrift.AuthInfo;
import cloudbase.core.zookeeper.ZooCache;
import cloudbase.core.zookeeper.ZooConstants;
import cloudbase.core.zookeeper.ZooSession;

// Utility class for adding all security info into ZK
public class ZKAuthenticator implements Authenticator
{
	private static Logger log = Logger.getLogger(ZKAuthenticator.class.getName());
	private static ZooCache zooCache = null;
	private static ZooKeeper zk = null;
	private static String rootUserName = null;

	private static class CBUser
	{
		public String user;
		public byte[] pass;
		public Set<SystemPermission> sysPerms;
		public Map<String, Set<TablePermission>> tabPerms;
		public Set<Short> auths;

		private CBUser() { }

		private CBUser(String user, byte[] data)
		{
			this.user = user;
			ByteArrayInputStream is = new ByteArrayInputStream(data);
			DataInputStream in = new DataInputStream(is);
			try {
				pass = WritableUtils.readCompressedByteArray(in);
				sysPerms = convertSystemPermissions(WritableUtils.readCompressedByteArray(in));
				int i = WritableUtils.readVInt(in);
				Text text = new Text();
				tabPerms = new HashMap<String, Set<TablePermission>>();
				for (; i > 0; i--)
				{
					text.readFields(in);
					tabPerms.put(text.toString(), convertTablePermissions(WritableUtils.readCompressedByteArray(in)));
				}
				i = WritableUtils.readVInt(in);
				auths = new HashSet<Short>(i);
				for (int j = 0; j < i; j++)
					auths.add(in.readShort());
			} catch (IOException e) {
				// I shouldn't happen
			}
		}

		private byte[] getBytes()
		{
			return userBytes(pass, sysPerms, tabPerms, auths);
		}

		public String toString()
		{
			StringBuilder builder = new StringBuilder();
			builder.append(user);
			builder.append(" ");
			for (int i = 0; i < pass.length; i++)
				builder.append(pass[i]);
			builder.append(" ");
			for (SystemPermission sys : sysPerms)
			{
				builder.append(sys);
				builder.append(" ");
			}
			for (Entry<String,Set<TablePermission>> entry : tabPerms.entrySet())
			{
				builder.append(entry.getKey());
				builder.append(" ");
				for (TablePermission tab : entry.getValue())
				{
					builder.append(tab);
					builder.append(" ");
				}
			}
			for (short s : auths)
				builder.append(s+",");
			return builder.toString();  
		}
	}

	public ZKAuthenticator()
	{
		getZooCache();
		if(zk == null)
			zk = ZooSession.getSession();
	}
	
//	private static byte[] getHash(byte b[])
//	{
//		try {
//			MessageDigest md = MessageDigest.getInstance(CBConstants.PW_HASH_ALGORITHM);
//			md.update(b);
//			return md.digest();
//		} catch (NoSuchAlgorithmException e) {
//			log.error("Invalid message digest algorithm: " + CBConstants.PW_HASH_ALGORITHM);
//			log.error(e.toString());
//			return null;
//		}	
//	}

	private static byte[] userBytes(byte[] hashedPass, Set<SystemPermission> systemPerms, Map<String, Set<TablePermission>> tablePerms, Set<Short> authorizations)
	{
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(os);
		try {
			WritableUtils.writeCompressedByteArray(out, hashedPass);
			byte[] systempermissions = convertSystemPermissions(systemPerms);
			WritableUtils.writeCompressedByteArray(out, systempermissions);
			Text text = new Text();
			WritableUtils.writeVInt(out, tablePerms.size());
			for (Entry<String, Set<TablePermission>> entry: tablePerms.entrySet())
				if (entry.getValue()!=null && entry.getValue().size()!=0)
				{
					text.set(entry.getKey());
					text.write(out);
					byte[] tablepermissions = convertTablePermissions(entry.getValue());
					WritableUtils.writeCompressedByteArray(out, tablepermissions);
				}
			WritableUtils.writeVInt(out, authorizations.size());
			for (short s : authorizations)
				out.writeShort(s);
		} catch (IOException e) {
			// I shouldn't happen
		}
		return os.toByteArray();
	}


	private static byte[] convertSystemPermissions(Set<SystemPermission> systempermissions)
	{
		byte[] toReturn = new byte[(int) Math.ceil(SystemPermission.values().length/8.0)];
		for (int i = 0; i < toReturn.length; i++)
			toReturn[i] = 0;
		for (SystemPermission sp : systempermissions)
		{
			int toOr = 1 << (sp.getId() % 8);
			toReturn[sp.getId()/8]|=toOr;
		}
		return toReturn;
	}

	private static Set<SystemPermission> convertSystemPermissions(byte[] systempermissions)
	{
		Set<SystemPermission> sysPerms = new HashSet<SystemPermission>();
		SystemPermission[] options = SystemPermission.values();
		for (int i = 0; i < systempermissions.length; i++)
		{
			int perm = systempermissions[i];
			for (int shift = 0; shift < 8; shift++)
			{
				if (perm%2==1)
					sysPerms.add(options[shift+i*8]);
				perm = perm >>> 1;
			}	
		}
		return sysPerms;
	}

	private static byte[] convertTablePermissions(Set<TablePermission> tablepermissions)
	{
		byte[] toReturn = new byte[(int) Math.ceil(TablePermission.values().length/8.0)];
		for (int i = 0; i < toReturn.length; i++)
			toReturn[i] = 0;
		for (TablePermission sp : tablepermissions)
		{
			int toOr = 1 << (sp.getId() % 8);
			toReturn[sp.getId()/8]|=toOr;
		}
		return toReturn;
	}

	private static Set<TablePermission> convertTablePermissions(byte[] tablepermissions)
	{
		Set<TablePermission> tabPerms = new HashSet<TablePermission>();
		TablePermission[] options = TablePermission.values();
		for (int i = 0; i < tablepermissions.length; i++)
		{
			int perm = tablepermissions[i];
			for (int shift = 0; shift < 8; shift++)
			{
				if (perm%2==1)
					tabPerms.add(options[shift+i*8]);
				perm = perm >>> 1;
			}	
		}
		return tabPerms;
	}

	private static ZooCache getZooCache()
	{
		if (zooCache == null)
			zooCache = new ZooCache();
		return zooCache;
	}

	/**
	 *  Returns null if user doesn't exist
	 */
	private CBUser getCachedCBUser(String user)
	{
		byte[] userData = zooCache.get(ZooConstants.ZKUserPath+"/"+user);
		return (userData == null) ? null : new CBUser(user, userData);
	}
	
	private CBUser getCBUser(String user)
	{
		byte[] userData = null;
		try {
			userData = zk.getData(ZooConstants.ZKUserPath+"/"+user, true, null);
		} catch (KeeperException e) {
			log.error("Exception getting user data from ZooKeeper when getting data from "+ZooConstants.ZKUserPath+"/"+user+": "+e.code(),e);
		} catch (InterruptedException e) {
			log.error(e);
		}
		if (userData == null)
			log.warn("User "+user+" is null in zookeeper");
		
		return (userData == null) ? null : new CBUser(user, userData);
	}
	
	private void updateUser(CBUser user)
	throws CBSecurityException
	{
		try {
			zk.setData(ZooConstants.ZKUserPath+"/"+user.user, user.getBytes(), -1);
		} catch (Exception e) {
			throw new CBSecurityException(e.getMessage(), false);
		}
	}

	/**
	 * Authenticate a user's credentials
	 * 
	 * @param credentials
	 * @return true if username/password combination match existing user; false otherwise
	 */
	private boolean authenticate(AuthInfo credentials)
	{
		if (credentials.user.equals(ZooConstants.SYSTEM_USERNAME))
			return (0 == WritableComparator.compareBytes(credentials.password, 0, credentials.password.length, ZooConstants.SYSTEM_PASSWORD, 0, ZooConstants.SYSTEM_PASSWORD.length));

		CBUser user = getCachedCBUser(credentials.user);
		if (user == null) return false;
		return (0 == WritableComparator.compareBytes(credentials.password, 0, credentials.password.length, user.pass, 0, user.pass.length));
	}

	/**
	 * Only SYSTEM user can call this method
	 */
	public void initializeRootUser(AuthInfo credentials, String user, byte[] pass)
	throws CBSecurityException
	{
		if (!credentials.user.equals(ZooConstants.SYSTEM_USERNAME) || !authenticate(credentials))
			throw new CBSecurityException(credentials.user, false);

		while (true)
		{
			try {
				// prep parent node of users with root username
				zk.create(ZooConstants.ZKUserPath, user.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
				
				// create the root user with all system privileges, no table privileges, and no record-level authorizations
				Set<SystemPermission> rootPerms = new HashSet<SystemPermission>();
				for (SystemPermission p : SystemPermission.values())
					rootPerms.add(p);
				zk.create(ZooConstants.ZKUserPath+"/"+user, userBytes(pass, rootPerms, new HashMap<String, Set<TablePermission>>(), CBConstants.NO_AUTHS), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT); 
				log.info("Initialized root user with username: "+user+" at the request of user "+(credentials != null ? credentials.user : "null"));
			} catch (KeeperException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return;
		}
	}

	public String getRootUsername()
	{
		if (rootUserName == null)
			rootUserName = new String(getZooCache().get(ZooConstants.ZKUserPath));
		return rootUserName;
	}

	public boolean authenticateUser(AuthInfo credentials, String user, byte[] pass)
	throws CBSecurityException
	{
		//log.info("Authenticating user "+user+" at the request of user "+(credentials != null ? credentials.user : "null"));
		if (!authenticate(credentials))
			throw new CBSecurityException(credentials.user, true);
		if (!credentials.user.equals(user) && !hasSystemPermission(credentials, credentials.user, SystemPermission.SYSTEM))
			throw new CBSecurityException(credentials.user, false);

		return authenticate(new AuthInfo(user, pass));
	}
	
	@Override
	public Set<String> listUsers(AuthInfo credentials)
	throws CBSecurityException
	{
		if (!authenticate(credentials))
			throw new CBSecurityException(credentials.user, true);
		
		try {
			return new HashSet<String>(zk.getChildren(ZooConstants.ZKUserPath, true));
		} catch (KeeperException e) {
		} catch (InterruptedException e) {
		}
		return new HashSet<String>();
	}

	/**
	 * Creates a user with no permissions whatsoever
	 */
	public void createUser(AuthInfo credentials, String user, byte[] pass, Set<Short> authorizations)
	throws CBSecurityException
	{
		if (!hasSystemPermission(credentials, credentials.user, SystemPermission.CREATE_USER))
			throw new CBSecurityException(credentials.user, false);

		// don't allow creating a user with the same name as system user
		if (user.equals(ZooConstants.SYSTEM_USERNAME))
			throw new CBSecurityException(user, false);
		
		try {
			zk.create(ZooConstants.ZKUserPath+"/"+user, userBytes(pass, new TreeSet<SystemPermission>(), new HashMap<String, Set<TablePermission>>(), authorizations), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
			log.info("Created user "+user+" at the request of user "+(credentials != null ? credentials.user : "null"));
		} catch (KeeperException e) {
			throw new CBSecurityException(user, false);
		} catch (InterruptedException e) {
			throw new CBSecurityException(user, false);
		}
	}

	public void dropUser(AuthInfo credentials, String user)
	throws CBSecurityException
	{
		if (!hasSystemPermission(credentials, credentials.user, SystemPermission.DROP_USER))
			throw new CBSecurityException(credentials.user, false);
			
		// can't delete root or system users
		if (user.equals(getRootUsername()) || user.equals(ZooConstants.SYSTEM_USERNAME))
			throw new CBSecurityException(user, false);
		
		try {
			zk.delete(ZooConstants.ZKUserPath+"/"+user, -1);
			log.info("Deleted user "+user+" at the request of user "+(credentials != null ? credentials.user : "null"));
		} catch (InterruptedException e) {
			throw new CBSecurityException(user, false);
		} catch (KeeperException e) {
			throw new CBSecurityException(user, false);
		}
	}

	public void changePassword(AuthInfo credentials, String user, byte[] pass)
	throws CBSecurityException
	{
		synchronized(zk)
		{
			if (!hasSystemPermission(credentials, credentials.user, SystemPermission.ALTER_USER) && !credentials.user.equals(user))
				throw new CBSecurityException(credentials.user, false);
			
			// can't modify system user
			if (user.equals(ZooConstants.SYSTEM_USERNAME))
				throw new CBSecurityException(user, false);
				
			CBUser cbuser = getCBUser(user);
			if (cbuser != null)
			{
				cbuser.pass = pass;
				updateUser(cbuser);
				log.info("Changed password for user "+user+" at the request of user "+(credentials != null ? credentials.user : "null"));
			}
			else
				throw new CBSecurityException(user, false);
		}
	}

	public void changeAuthorizations(AuthInfo credentials, String user, Set<Short> authorizations)
	throws CBSecurityException
	{
		synchronized(zk)
		{
			if (!hasSystemPermission(credentials, credentials.user, SystemPermission.ALTER_USER))
				throw new CBSecurityException(credentials.user, false);
	
			// can't modify system user
			if (user.equals(ZooConstants.SYSTEM_USERNAME))
				throw new CBSecurityException(user, false);
				
			CBUser cbuser = getCBUser(user);
			if (cbuser != null)
			{
				cbuser.auths = authorizations;
				updateUser(cbuser);
				log.info("Changed authorizations for user "+user+" at the request of user "+(credentials != null ? credentials.user : "null"));
			}
			else
				throw new CBSecurityException(user, false);
		}
	}

	public Set<Short> getUserAuthorizations(AuthInfo credentials, String user)
	throws CBSecurityException
	{
		if (!hasSystemPermission(credentials, credentials.user, SystemPermission.SYSTEM) && !credentials.user.equals(user))
			throw new CBSecurityException(credentials.user, false);
		
		// system user doesn't need record-level authorizations for the tables it reads (for now)
		if (user.equals(ZooConstants.SYSTEM_USERNAME))
			return CBConstants.NO_AUTHS;
		
		CBUser cbuser = getCachedCBUser(user);
		if (cbuser != null)
		{
			Set<Short> toReturn = new HashSet<Short>();
			for (short s : cbuser.auths)
				toReturn.add(s);
			return toReturn;
		}
		else
			throw new CBSecurityException(user, false);
	}

	/**
	 * Checks if a user has a system permission
	 * 
	 * @return true if a user exists and has permission; false otherwise
	 */
	@Override
	public boolean hasSystemPermission(AuthInfo credentials, String user, SystemPermission permission)
	throws CBSecurityException
	{
		if (!authenticate(credentials))
			throw new CBSecurityException(credentials.user, true);
		
		if (user.equals(getRootUsername()) || user.equals(ZooConstants.SYSTEM_USERNAME))
			return true;
		
		CBUser cbuser = getCachedCBUser(user);
		return (cbuser != null && cbuser.sysPerms.contains(permission));
	}

	@Override
	public boolean hasTablePermission(AuthInfo credentials, String user, String table, TablePermission permission)
	throws CBSecurityException
	{
		if (!authenticate(credentials))
			throw new CBSecurityException(credentials.user, true);
		
		// always allow system user
		if (user.equals(ZooConstants.SYSTEM_USERNAME))
			return true;
		
		// allow anybody to scan the METADATA table, as long as that user exists
		if (table.equals(CBConstants.METADATA_TABLE_NAME) && exists(user))
			return true;

		CBUser cbuser = getCachedCBUser(user);
		return (cbuser != null && cbuser.tabPerms.get(table) != null && cbuser.tabPerms.get(table).contains(permission));
	}

	@Override
	public synchronized void grantSystemPermission(AuthInfo credentials, String user, SystemPermission permission)
	throws CBSecurityException
	{
		// don't grant permission to non-existant user
		if (!exists(user))
			throw new CBSecurityException(user, true); 

		synchronized(zk)
		{
			if (!hasSystemPermission(credentials, credentials.user, SystemPermission.GRANT))
				throw new CBSecurityException(credentials.user, false);
			
			// can't modify system user
			if (user.equals(ZooConstants.SYSTEM_USERNAME))
				throw new CBSecurityException(user, false);
				
			CBUser cbuser = getCBUser(user);
			if (cbuser != null && !permission.equals(SystemPermission.GRANT))
			{
				cbuser.sysPerms.add(permission);
				updateUser(cbuser);
				log.info("Granted system permission "+permission+" for user "+user+" at the request of user "+(credentials != null ? credentials.user : "null"));
			}
			else
				throw new CBSecurityException(user, false);
		}
	}

	@Override
	public void grantTablePermission(AuthInfo credentials, String user, String table, TablePermission permission)
	throws CBSecurityException
	{
		synchronized(zk)
		{
			if (!hasSystemPermission(credentials, credentials.user, SystemPermission.GRANT))
				throw new CBSecurityException(credentials.user, false);
			
			// can't modify system user
			if (user.equals(ZooConstants.SYSTEM_USERNAME))
				throw new CBSecurityException(user, false);
				
			CBUser cbuser = getCBUser(user);
			if (cbuser != null)
			{
				Set<TablePermission> tablePerms = cbuser.tabPerms.get(table);
				if (tablePerms == null)
				{
					tablePerms = new HashSet<TablePermission>();
					cbuser.tabPerms.put(table, tablePerms);
				}
				tablePerms.add(permission);
				updateUser(cbuser);
				log.info("Granted table permission "+permission+" for user "+user+" on the table "+table+" at the request of user "+(credentials != null ? credentials.user : "null"));
			}
			else
				throw new CBSecurityException(user, false);			
		}
	}

	@Override
	public void revokeSystemPermission(AuthInfo credentials, String user, SystemPermission permission)
	throws CBSecurityException
	{
		synchronized(zk)
		{
			if (!hasSystemPermission(credentials, credentials.user, SystemPermission.GRANT))
				throw new CBSecurityException(credentials.user, false);
			
			// can't modify system user or revoke permissions from root user
			if (user.equals(ZooConstants.SYSTEM_USERNAME) || user.equals(getRootUsername()))
				throw new CBSecurityException(user, false);
				
			CBUser cbuser = getCBUser(user);
			if (cbuser != null)
			{
				cbuser.sysPerms.remove(permission);
				updateUser(cbuser);
				log.info("Revoked system permission "+permission+" for user "+user+" at the request of user "+(credentials != null ? credentials.user : "null"));
			}
			else
				throw new CBSecurityException(user, false);
		}
	}

	@Override
	public void revokeTablePermission(AuthInfo credentials, String user, String table, TablePermission permission)
	throws CBSecurityException
	{
		synchronized(zk)
		{
			if (!hasSystemPermission(credentials, credentials.user, SystemPermission.GRANT))
				throw new CBSecurityException(credentials.user, false);
			
			// can't modify system user
			if (user.equals(ZooConstants.SYSTEM_USERNAME))
				throw new CBSecurityException(user, false);

			CBUser cbuser = getCBUser(user);
			if (cbuser != null)
			{
				Set<TablePermission> tablePerms = cbuser.tabPerms.get(table);
				tablePerms.remove(permission);
				if (tablePerms.size()==0)
					cbuser.tabPerms.remove(table);
				updateUser(cbuser);
				log.info("Revoked table permission "+permission+" for user "+user+" on the table "+table+" at the request of user "+(credentials != null ? credentials.user : "null"));
			}
			else
				throw new CBSecurityException(user, false);
		}
	}
	
	@Override
	public void deleteTable(AuthInfo credentials, String table)
	throws CBSecurityException
	{
		if (!hasSystemPermission(credentials, credentials.user, SystemPermission.DROP_TABLE) && !hasTablePermission(credentials, credentials.user, table, TablePermission.WRITE))
			throw new CBSecurityException(credentials.user, false);
		
		try {
			for (String user : zk.getChildren(ZooConstants.ZKUserPath, true))
			{
				CBUser cbuser = getCBUser(user);
				if (cbuser != null)
					cbuser.tabPerms.remove(table);
			}
		} catch (KeeperException e) {
			log.error(e.getMessage(), e);
			throw new RuntimeException(e);
		} catch (InterruptedException e) {
			log.error(e.getMessage(), e);
			throw new RuntimeException(e);
		}
	}

	private boolean exists(String user)
	{
		return getCachedCBUser(user) != null;		
	}
}
