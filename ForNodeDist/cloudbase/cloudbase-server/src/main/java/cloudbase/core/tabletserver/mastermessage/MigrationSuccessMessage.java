package cloudbase.core.tabletserver.mastermessage;

import org.apache.log4j.Logger;

import cloudbase.core.CBConstants;
import cloudbase.core.security.thrift.AuthInfo;
import cloudbase.core.data.KeyExtent;
import cloudbase.core.master.thrift.MasterTabletService;

import com.facebook.thrift.TException;

public class MigrationSuccessMessage implements MasterMessage {
	@SuppressWarnings("unused")
    private static Logger log = Logger.getLogger(MigrationSuccessMessage.class.getName());	

	private KeyExtent extent;
	
	public MigrationSuccessMessage(KeyExtent extent)
	{
		this.extent = extent;
	}
	
	public void send(AuthInfo auth, String serverName, MasterTabletService.Client client) throws TException {
		client.reportTabletStatus(auth, serverName, extent, CBConstants.MSG_TS_REPORT_TABLET_LOADED);
	}

}
