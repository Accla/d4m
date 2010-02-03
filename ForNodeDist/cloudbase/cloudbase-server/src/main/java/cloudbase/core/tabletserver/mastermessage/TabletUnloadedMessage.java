package cloudbase.core.tabletserver.mastermessage;



import com.facebook.thrift.TException;

import cloudbase.core.security.thrift.AuthInfo;
import cloudbase.core.data.KeyExtent;
import cloudbase.core.master.thrift.MasterTabletService.Client;

public class TabletUnloadedMessage implements MasterMessage {
	
	private KeyExtent extent;

	public TabletUnloadedMessage(KeyExtent extent)
	{
		this.extent = extent;
	}


	public void send(AuthInfo auth, String serverName, Client client) throws TException {
		client.reportTabletUnloaded(auth, extent);
	}
	
	
}
