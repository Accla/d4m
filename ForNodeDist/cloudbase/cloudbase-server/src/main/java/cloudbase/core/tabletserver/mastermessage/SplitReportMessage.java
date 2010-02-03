package cloudbase.core.tabletserver.mastermessage;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.apache.hadoop.io.Text;

import com.facebook.thrift.TException;

import cloudbase.core.security.thrift.AuthInfo;
import cloudbase.core.data.KeyExtent;
import cloudbase.core.master.thrift.KeyExtentLocation;
import cloudbase.core.master.thrift.TabletSplit;
import cloudbase.core.master.thrift.MasterTabletService.Client;

public class SplitReportMessage implements MasterMessage {
	Map<KeyExtent, Text> extents;
	KeyExtent old_extent;
	
	public SplitReportMessage(KeyExtent old_extent, Map<KeyExtent, Text> newExtents)
	{
		this.old_extent = old_extent;
		extents = new TreeMap<KeyExtent, Text>(newExtents);
	}

	public SplitReportMessage(KeyExtent old_extent, KeyExtent ne1, Text np1, KeyExtent ne2, Text np2) {
		this.old_extent = old_extent;
		extents = new TreeMap<KeyExtent, Text>();
		extents.put(ne1, np1);
		extents.put(ne2, np2);
	}

	public void send(AuthInfo credentials, String serverName, Client client) throws TException {
		TabletSplit split = new TabletSplit();
		split.oldTablet = old_extent;
		split.keyExtentLocations = new ArrayList<KeyExtentLocation>();
		for (Entry<KeyExtent, Text> entry : extents.entrySet()) {
			split.keyExtentLocations.add(new KeyExtentLocation(entry.getKey(), entry.getValue().getBytes()));
		}
		client.reportSplitExtent(credentials, serverName, split);		
	}

}
