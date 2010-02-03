package cloudbase.core.tabletserver.mastermessage;

import cloudbase.core.security.thrift.AuthInfo;
import cloudbase.core.master.thrift.MasterTabletService;

import com.facebook.thrift.TException;

public interface MasterMessage {

	void send(AuthInfo info, String serverName, MasterTabletService.Client client) throws TException;

}
