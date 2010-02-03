package cloudbase.core.client.impl;

import org.apache.log4j.Logger;

import cloudbase.core.CBConstants;
import cloudbase.core.client.CBSecurityException;
import cloudbase.core.conf.CBConfiguration;
import cloudbase.core.data.KeyExtent;
import cloudbase.core.data.Mutation;
import cloudbase.core.security.thrift.AuthInfo;
import cloudbase.core.security.thrift.ThriftSecurityException;
import cloudbase.core.tabletserver.thrift.ConstraintViolationException;
import cloudbase.core.tabletserver.thrift.NotServingTabletException;
import cloudbase.core.tabletserver.thrift.TabletClientService;

import com.facebook.thrift.TException;
import com.facebook.thrift.protocol.TBinaryProtocol;
import com.facebook.thrift.protocol.TProtocol;
import com.facebook.thrift.transport.TTransport;
import com.facebook.thrift.transport.TTransportException;

public class TabletClient {
    
	static Logger log = Logger.getLogger(TabletClient.class.getName());
	
    static int retries = 0;

	public static void updateServer(Mutation m, KeyExtent extent, String server, AuthInfo ai)
	throws TException, NotServingTabletException, ConstraintViolationException, CBSecurityException {
		
	    if (retries == 0) {
	        retries = CBConfiguration.getInstance().getInt("cloudbase.client.retries", CBConstants.MAX_CLIENT_RETRIES);
	        retries = Math.max(retries, 1);
	    }
	    
	    for(int numTries=0; numTries < retries; numTries++) {
			
			TTransport transport = null;
			try{
				transport = ThriftTansportPool.getInstance().getTransport(server, CBConfiguration.getInstance().getInt("cloudbase.tabletserver.clientPort", CBConstants.TABLET_CLIENT_PORT_DEFAULT));
				TProtocol protocol = new TBinaryProtocol(transport);
				TabletClientService.Client client = new TabletClientService.Client(protocol);

				client.update(ai, extent, m);
				return;
			} catch (TTransportException e) {
				throw new TException(e);
			} catch (ThriftSecurityException e) {
				throw new CBSecurityException(e.user, e.baduserpass, e);
			} finally{
				ThriftTansportPool.getInstance().returnTransport(transport);
			}

		}
		throw new TException("update: max retries reached");
	}

}
