package cloudbase.core.util;

import java.net.InetSocketAddress;

import org.apache.hadoop.io.Text;
import com.facebook.thrift.transport.TSocket;

public class AddressUtil {
	static public InetSocketAddress parseAddress(String address, int defaultPort) throws NumberFormatException {
		address = address.replace('+', ':');
		String[] parts = address.split(":", 2);
		if (parts.length == 2)
			if (parts[1].isEmpty()) {
				return new InetSocketAddress(parts[0], defaultPort);
			} else {
				return new InetSocketAddress(parts[0], Integer.parseInt(parts[1]));
			}
		return new InetSocketAddress(address, defaultPort);
	}
	static public InetSocketAddress parseAddress(Text address, int defaultPort) {
		return parseAddress(address.toString(), defaultPort);
	}
	static public TSocket createTSocket(String address, int defaultPort) {
		InetSocketAddress addr = parseAddress(address, defaultPort);
		return new TSocket(addr.getHostName(), addr.getPort());
	}
	static public String toString(InetSocketAddress addr) {
		return addr.getAddress().getHostAddress() + "+" + addr.getPort();
	}
}
