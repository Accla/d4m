package cloudbase.core.tabletserver.mastermessage;

import cloudbase.core.security.thrift.AuthInfo;
import cloudbase.core.master.thrift.MasterTabletService;
import cloudbase.core.master.thrift.TabletServerStatus;

import com.facebook.thrift.TException;

public class PongMessage implements MasterMessage {
	
	private TabletServerStatus status;
	
	public PongMessage(TabletServerStatus status)
	{
		this.status = status;
	}
	
	public void send(AuthInfo auth, String serverName, MasterTabletService.Client client) throws TException {
		status.tabletServerTime = System.currentTimeMillis();
		client.pong(auth, serverName, status);
	}

}
