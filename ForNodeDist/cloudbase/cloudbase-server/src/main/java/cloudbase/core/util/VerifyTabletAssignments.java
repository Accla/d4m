package cloudbase.core.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.hadoop.io.Text;

import cloudbase.core.CBConstants;
import cloudbase.core.client.CBException;
import cloudbase.core.client.CBSecurityException;
import cloudbase.core.client.Connector;
import cloudbase.core.client.TableNotFoundException;
import cloudbase.core.client.impl.HdfsZooInstance;
import cloudbase.core.client.mapreduce.bulk.BulkOperations;
import cloudbase.core.conf.CBConfiguration;
import cloudbase.core.data.InitialScan;
import cloudbase.core.data.KeyExtent;
import cloudbase.core.data.Range;
import cloudbase.core.data.ScanResult;
import cloudbase.core.security.thrift.AuthInfo;
import cloudbase.core.tabletserver.thrift.TabletClientService;

import com.facebook.thrift.protocol.TBinaryProtocol;
import com.facebook.thrift.protocol.TProtocol;
import com.facebook.thrift.transport.TTransport;

public class VerifyTabletAssignments {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args)
	throws Exception
	{
		if (args.length != 3)
			System.err.println("Usage: VerifyTabletAssignments <master> <username> <password>");
		
		else
		{
			String user = args[1];
			byte[] pass = args[2].getBytes();
		
			Connector conn = new Connector(args[0], user, pass);
	
			for (String table : conn.tableOperations().list())
				checkTable(user, pass, table, null);
		}
		
	}

	private static void checkTable(final String user, final byte[] pass, String table, HashSet<KeyExtent> check)
	throws CBException, CBSecurityException, TableNotFoundException, InterruptedException
	{
		
		if(check == null)
			System.out.println("Checking table "+table);
		else
			System.out.println("Checking table "+table+" again, failures "+check.size());
		
		Map<KeyExtent, String> locations = new TreeMap<KeyExtent, String>();
		SortedSet<KeyExtent> tablets = new TreeSet<KeyExtent>();
		
		BulkOperations.getMetadataEntries(new HdfsZooInstance(), new AuthInfo(user, pass), table, locations, tablets);
		
		final HashSet<KeyExtent> failures = new HashSet<KeyExtent>();
		
		
		
		for (KeyExtent keyExtent : tablets)
			if(!locations.containsKey(keyExtent))
				System.out.println(" Tablet "+keyExtent+" has no location");
		
		Map<String, List<KeyExtent>> extentsPerServer = new TreeMap<String, List<KeyExtent>>();
		
		for (Entry<KeyExtent, String> entry : locations.entrySet())
		{
			List<KeyExtent> extentList = extentsPerServer.get(entry.getValue());
			if(extentList == null)
			{
				extentList = new ArrayList<KeyExtent>();
				extentsPerServer.put(entry.getValue(), extentList);
			}
			
			if(check == null || check.contains(entry.getKey()))
				extentList.add(entry.getKey());
		}
		
		ExecutorService tp = Executors.newFixedThreadPool(20);
		
		for (final Entry <String, List<KeyExtent>> entry : extentsPerServer.entrySet()) {
			Runnable r = new Runnable(){

				@Override
				public void run() {
					try {
						checkTabletServer(user, pass, entry, failures);
					} catch (Exception e) {
						System.err.println("Failure on ts "+entry.getKey()+" "+e.getMessage());
						e.printStackTrace();
						failures.addAll(entry.getValue());
					} 
				}
				
			};
			
			tp.execute(r);
		}
		
		tp.shutdown();
		
		while(!tp.awaitTermination(1, TimeUnit.HOURS)){
			
		}
		
		if(failures.size() > 0)
			checkTable(user, pass, table, failures);
	}

	private static void checkTabletServer(final String user, final byte[] pass, Entry<String, List<KeyExtent>> entry, HashSet<KeyExtent> failures) throws Exception {
		
		TTransport transport = AddressUtil.createTSocket(entry.getKey(), CBConfiguration.getInstance().getInt("cloudbase.tabletserver.clientPort", CBConstants.TABLET_CLIENT_PORT_DEFAULT));
		transport.open();
		TProtocol protocol = new TBinaryProtocol(transport);
		TabletClientService.Client client = new TabletClientService.Client(protocol);
		
		AuthInfo st = new AuthInfo(user,pass);
		
		Map<KeyExtent, List<Range>> batch = new TreeMap<KeyExtent, List<Range>>();
		
		for (KeyExtent keyExtent : entry.getValue()) {
			Text row = keyExtent.getEndRow();
			Text row2 = null;
			
			if(row == null){
				row = keyExtent.getPrevEndRow();
				
				if(row != null){
					row = new Text(row);
					row.append(new byte[]{'a'}, 0, 1);
				}else{
					row = new Text("1234567890");
				}
				
				row2 = new Text(row);
				row2.append(new byte[]{'!'},0,1);
			}else{
				row = new Text(row);
				row2 = new Text(row);
				
				row.getBytes()[row.getLength() - 1] = (byte)(row.getBytes()[row.getLength() - 1] - 1);
			}
			
		
			
			//System.out.println(fullExtent.get(keyExtent)+" "+row+" "+row2);
			
			Range r = new Range(row, row2);
			batch.put(keyExtent, Collections.singletonList(r));
		}
		
		InitialScan is = client.startMultiScan(st, batch, Collections.EMPTY_LIST, Collections.EMPTY_LIST, Collections.EMPTY_MAP, CBConstants.NO_AUTHS);
		if(is.result.more){
			ScanResult result = client.continueMultiScan(is.scanID);
			while(result.more){
				result = client.continueMultiScan(is.scanID);
			}
		}
		
		Map<KeyExtent, List<Range>> scanFailures = client.closeMultiScan(is.scanID);
		
      // System.out.println(failures);
		
		for(KeyExtent ke : scanFailures.keySet()){
			System.out.println(" Tablet "+ke+" failed at "+entry.getKey());
			failures.add(ke);
		}
		
		transport.close();
		
		
	}

}
