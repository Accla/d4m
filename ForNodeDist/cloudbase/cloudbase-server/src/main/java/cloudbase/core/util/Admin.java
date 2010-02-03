package cloudbase.core.util;

import org.apache.log4j.Logger;

import cloudbase.core.client.CBException;
import cloudbase.core.client.CBSecurityException;
import cloudbase.core.client.impl.HdfsZooInstance;
import cloudbase.core.client.impl.MasterClient;
import cloudbase.core.master.thrift.MasterClientService;
import cloudbase.core.security.thrift.AuthInfo;
import cloudbase.core.security.thrift.ThriftSecurityException;
import cloudbase.core.zookeeper.ZooConstants;

import com.facebook.thrift.TException;

public class Admin {
	private static Logger log = Logger.getLogger(Admin.class.getName());
	
	private static void stop(String user, byte[] password, boolean tabletServersToo)
	throws CBException, CBSecurityException
	{
	    MasterClientService.Client client;
	    try {
	         client = MasterClient.master_connect(new HdfsZooInstance());
	    } catch (Throwable t) {
	        log.error("Unable to connect to the master");
	        return;
	    }
        try {
			client.shutdown(new AuthInfo(user, password), tabletServersToo);
		} catch (ThriftSecurityException e) {
			throw new CBSecurityException(e.user, e.baduserpass, e);
		} catch (TException e) {
			throw new CBException(e);
		}
        MasterClient.close(client);
	}
	
	public static void main(String[] args) throws CBException, CBSecurityException
	{
		if(args.length != 1)
			log.error("Usage: Admin <stopMaster | stopAll>");
		else if(args[0].equals("stopMaster"))
			stop(ZooConstants.SYSTEM_USERNAME,ZooConstants.SYSTEM_PASSWORD, false);
		else if (args[0].equals("stopAll"))
			stop(ZooConstants.SYSTEM_USERNAME,ZooConstants.SYSTEM_PASSWORD, true);
		else
			log.error("Unrecognized command: " + args[0]);
	}
}
