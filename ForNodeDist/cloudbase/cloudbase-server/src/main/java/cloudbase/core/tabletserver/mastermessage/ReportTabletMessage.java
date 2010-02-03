package cloudbase.core.tabletserver.mastermessage;



import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.facebook.thrift.TException;

import cloudbase.core.security.thrift.AuthInfo;
import cloudbase.core.data.KeyExtent;
import cloudbase.core.master.thrift.MasterTabletService.Client;

public class ReportTabletMessage implements MasterMessage {
	
	private List<KeyExtent> extents;
	private String clientServiceAddress;
	private String monitorServiceAddress;

	public ReportTabletMessage(Collection<KeyExtent> extents, String clientServiceAddress, String monitorServiceAddress)
	{
		this.extents = new ArrayList<KeyExtent>(extents);
		this.clientServiceAddress = clientServiceAddress;
		this.monitorServiceAddress = monitorServiceAddress;
	}

	public void send(AuthInfo auth, String serverName, Client client) throws TException {
		client.reportTabletList(auth, serverName, extents, clientServiceAddress, monitorServiceAddress, System.currentTimeMillis());
	}
}
