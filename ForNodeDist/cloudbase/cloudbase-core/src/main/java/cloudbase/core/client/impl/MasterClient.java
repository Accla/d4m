package cloudbase.core.client.impl;

import java.io.IOException;

import cloudbase.core.CBConstants;
import cloudbase.core.Cloudbase;
import cloudbase.core.client.Instance;
import cloudbase.core.conf.CBConfiguration;
import cloudbase.core.master.MasterNotRunningException;
import cloudbase.core.master.thrift.MasterClientService;
import cloudbase.core.util.AddressUtil;
import cloudbase.core.zookeeper.ZooLock;

import com.facebook.thrift.TException;
import com.facebook.thrift.protocol.TBinaryProtocol;
import com.facebook.thrift.transport.TSocket;

public class MasterClient
{
	
	private static CBConfiguration cbConf = null;
	
	public static synchronized MasterClientService.Client master_connect(Instance instance)
	throws IOException, TException, MasterNotRunningException
	{
		if(cbConf == null)
			cbConf = CBConfiguration.getInstance();
		
		int portHint = cbConf.getInt("cloudbase.master.clientPort", CBConstants.MASTER_CLIENT_PORT_DEFAULT);
		TSocket socket = AddressUtil.createTSocket(instance.getMasterLocations().get(0), portHint);
		TBinaryProtocol protocol = new TBinaryProtocol(socket);
		MasterClientService.Client client = new MasterClientService.Client(protocol);
		socket.open();
		return client;
	}
	
	public static String lookupMaster() throws IOException, MasterNotRunningException
	{
	    String masterLocPath = CBConstants.ZROOT_PATH+"/"+Cloudbase.getInstanceID()+CBConstants.ZMASTER_LOCK_NODE;
	    
	    byte[] loc = ZooLock.getLockData(masterLocPath);
	    
	    if(loc == null)
	        throw new MasterNotRunningException("");
	    
	    return new String(loc);
	}
	
	public static void close(MasterClientService.Client client)
	{
		if (client != null
				&& client.getInputProtocol() != null
				&& client.getInputProtocol().getTransport() != null
				&& client.getInputProtocol().getTransport().isOpen())
			client.getInputProtocol().getTransport().close();
	}
}
