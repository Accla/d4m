package edu.mit.ll.d4m.db.cloud;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.Set;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TTransport;

import cloudbase.core.client.impl.MasterClient;
import cloudbase.core.CBConstants;
import cloudbase.core.client.BatchScanner;
import cloudbase.core.client.Connector;
import cloudbase.core.client.TableNotFoundException;
import cloudbase.core.data.Key;
import cloudbase.core.data.Range;
import cloudbase.core.data.Value;
import cloudbase.core.security.Authorizations;
import cloudbase.core.security.thrift.AuthInfo;
import cloudbase.core.client.ZooKeeperInstance;
import cloudbase.core.client.CBException;
import cloudbase.core.client.CBSecurityException;
import cloudbase.core.client.TableNotFoundException;
import cloudbase.core.client.impl.HdfsZooInstance;
import cloudbase.core.client.impl.Tables;
import cloudbase.core.client.impl.ThriftTransportPool;
import cloudbase.core.master.thrift.TabletInfo;
import cloudbase.core.master.thrift.TabletRates;
import cloudbase.core.master.thrift.TabletServerStatus;
import cloudbase.core.master.thrift.MasterClientService;
import cloudbase.core.master.thrift.MasterMonitorInfo;
import cloudbase.core.tabletserver.thrift.TabletClientService;
import cloudbase.core.util.AddressUtil;
import cloudbase.core.util.Pair;
import cloudbase.server.master.mgmt.TabletServerState;
import cloudbase.server.monitor.Monitor;
import cloudbase.server.monitor.Monitor.MajorMinorStats;
import cloudbase.server.monitor.util.Table;
import cloudbase.server.monitor.util.TableRow;
import cloudbase.server.monitor.util.celltypes.CompactionsType;
import cloudbase.server.monitor.util.celltypes.DurationType;
import cloudbase.server.monitor.util.celltypes.NumberType;
import cloudbase.server.monitor.util.celltypes.ProgressChartType;
import cloudbase.server.monitor.util.celltypes.TServerLinkType;
import cloudbase.server.monitor.util.celltypes.TableLinkType;
import cloudbase.server.security.SecurityConstants;
/*
 *  Test code to access the cloud via the MasterClientService and MasterMonitorInfo
 *
 */
public class TestMonitor {

    private static MasterMonitorInfo mmi;

    public static void main(String[] args) throws CBException, CBSecurityException, TableNotFoundException
    {

	String user = args[0];
	String pass = args[1];
	String instanceName = args[2];
	String zooKeepers = args[3];
	String tserverAddress = args[4];
	String tableName = args[5];
	D4mDbTableOperations d4mOps = new D4mDbTableOperations(instanceName, zooKeepers, user,pass);
	//	long numRows = d4mOps.getNumberOfRows(tableName);
	System.out.println("TOTAL ENTRIES = "+d4mOps.getNumberOfEntries());
	System.out.println(tableName+"::Number of Rows = "+d4mOps.getNumberOfRows(tableName));
	System.out.println("####TOTAL(except METADATA)::Number of Rows = "+d4mOps.getNumberOfRows());

	ArrayList<String> splitList = d4mOps.getSplits(tableName);
	for(String s : splitList) {
	    System.out.println("     -->> "+s);
	}

    }

    public static void doTotalNumEntries(String[] args) throws CBException, CBSecurityException, TableNotFoundException
    {

	//args[0] username
	//args[1]  password
	//args[2]  instance name
	//args[3]  zookeeper host (comma separated list)
	//args[4]  host(tserver):port
	String user = args[0];
	byte[] pass = args[1].getBytes();
	String instanceName = args[2];
	String zooKeepers = args[3];
	String tserverAddress = args[4];

	ZooKeeperInstance instance = new ZooKeeperInstance(instanceName, zooKeepers);
		Connector connector = new Connector(instance, user, pass);
	//	Map<String, String> nameToIdMap = Tables.getNameToIdMap(HdfsZooInstance.getInstance());

	Map<String, String> nameToIdMap = Tables.getNameToIdMap(connector.getInstance());
	InetSocketAddress address = AddressUtil.parseAddress(tserverAddress, -1);
	TTransport transport = null;
	ThriftTransportPool transportPool = ThriftTransportPool.getInstance();
	TabletInfo total = new TabletInfo();
	TreeMap<String, TabletInfo> tabletMap =null;
	AuthInfo authInfo = new AuthInfo(user,pass);
	try {
	    System.out.println(" address = "+ address.getAddress().getHostAddress()+ ", port = "+ address.getPort());
	    transport = transportPool.getTransportWithDefaultTimeout(address.getAddress().getHostAddress(), address.getPort());

	    System.out.println("Got a transport obj.");
	    TProtocol protocol = new TBinaryProtocol(transport);
	    System.out.println("Got a TProtocol obj.");
	    TabletClientService.Client client = new TabletClientService.Client(protocol);
	    System.out.println("Got a TabletClientService client.");
	
	    tabletMap = new TreeMap<String, TabletInfo>(client.getTabletMap(authInfo));

	    int size= tabletMap.size();
	    Set<String> keySet = tabletMap.keySet();
	    Iterator<String> itKey = keySet.iterator();
	    while(itKey.hasNext()) {
		String k = itKey.next();
		System.out.println("TabletMap key = "+ k);
	    }

	    for (Entry<String, TabletInfo> te : tabletMap.entrySet()) {
		System.out.println(" Entry key = "+ te.getKey());
		TabletInfo info = te.getValue();
		if (te.getKey().isEmpty()) {
		    continue;
		}
		
		total.numEntries += info.numEntries;
	    }    
	    System.out.println("Total number of entries = " + total.numEntries);
	    //	    tabletMap = new TreeMap<String, TabletInfo>(client.getTabletMap(SecurityConstants.systemCredentials));

	    doMmi(instance, authInfo);
	} catch (Exception e) {
	    //			banner(sb, "error", "No&nbsp;Such&nbsp;Tablet&nbsp;Server&nbsp;Available");
	    System.err.println(e);
	    return;
	} finally {
	    transportPool.returnTransport(transport);
	}
	


    }

    public static void doMmi (ZooKeeperInstance  instance, AuthInfo authInfo) {
	MasterClientService.Client client = null;
	try {
	    client = MasterClient.getConnection(instance);
	    mmi = client.getMasterStats(authInfo);
	    ArrayList<TabletServerStatus> tservers = new ArrayList<TabletServerStatus>();
	    if (mmi != null)
		tservers.addAll(mmi.tServerInfo);
	    for (TabletServerStatus status : tservers) {
		System.out.println("TabletServer status ::  name = "+status.name);
	    }


	} catch (Exception e) {
	    mmi = null;

	} finally {
	    if (client != null)
		MasterClient.close(client);
	}
	
    }
}
