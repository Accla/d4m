package cloudbase.core.test;

import org.apache.hadoop.io.Text;

import cloudbase.core.CBConstants;
import cloudbase.core.client.CBException;
import cloudbase.core.client.CBSecurityException;
import cloudbase.core.client.impl.ThriftTansportPool;
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

public class WrongTabletTest {
	private static AuthInfo rootCredentials = new AuthInfo("root", "secret".getBytes());
	
	public static void main(String[] args) throws CBException, CBSecurityException, ConstraintViolationException, NotServingTabletException
	{
		try {
			String location = args[0];
			TTransport transport = ThriftTansportPool.getInstance().getTransport(location, CBConfiguration.getInstance().getInt("cloudbase.tabletserver.clientPort", CBConstants.TABLET_CLIENT_PORT_DEFAULT));
			TProtocol protocol = new TBinaryProtocol(transport);
			TabletClientService.Client client = new TabletClientService.Client(protocol);
			
			Mutation mutation = new Mutation(new Text("row_0003750001"));
			//mutation.set(new Text("colf:colq"), new Value("val".getBytes()));
			mutation.remove(new Text("colf"), new Text("colq"));
			client.update(rootCredentials, new KeyExtent(new Text("test_ingest"), null, new Text("row_0003750000")), mutation);
		} catch (ThriftSecurityException e) {
			throw new CBSecurityException(e.user, e.baduserpass, e);
		} catch (NotServingTabletException e) {
			throw e;
		} catch (ConstraintViolationException e) {
			throw e;
		} catch (TTransportException e) {
			throw new CBException(e);
		} catch (TException e) {
			throw new CBException(e);
		}
	}
}
