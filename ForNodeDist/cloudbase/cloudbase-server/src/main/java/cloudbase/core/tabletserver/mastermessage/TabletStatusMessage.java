package cloudbase.core.tabletserver.mastermessage;



import com.facebook.thrift.TException;

import cloudbase.core.security.thrift.AuthInfo;
import cloudbase.core.data.KeyExtent;
import cloudbase.core.master.thrift.MasterTabletService.Client;

public class TabletStatusMessage implements MasterMessage {
	
	private KeyExtent extent;
	private int status;

	public TabletStatusMessage(int status, KeyExtent extent)
	{
		this.extent = extent;
		this.status = status;
	}


	public void send(AuthInfo auth, String serverName, Client client) throws TException {
		client.reportTabletStatus(auth, serverName, extent, status);
	}
	
	
}
