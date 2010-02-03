package cloudbase.core.tabletserver.mastermessage;

import com.facebook.thrift.TException;

import cloudbase.core.CBConstants;
import cloudbase.core.security.thrift.AuthInfo;
import cloudbase.core.master.thrift.MasterTabletService.Client;
import cloudbase.core.tabletserver.TabletServer;

public class ShutdownResponseMessage implements MasterMessage {
	
	private int stage;
	private TabletServer.ShutdownHandler shutdownHandler;
	
	public ShutdownResponseMessage(int stage, TabletServer.ShutdownHandler shutdownHandler)
	{
		this.stage = stage;
		this.shutdownHandler = shutdownHandler;
	}

	public void send(AuthInfo auth, String serverName, Client client) throws TException {
		client.reportShutdown(auth, serverName, stage);
		if (stage == CBConstants.SHUTDOWN_STAGE_3) {
			shutdownHandler.setStage(CBConstants.SHUTDOWN_STAGE_4);
		}
	}

}
