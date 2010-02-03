package cloudbase.core.client.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;

import org.apache.hadoop.io.Text;
import org.apache.log4j.Logger;

import cloudbase.core.CBConstants;
import cloudbase.core.client.CBException;
import cloudbase.core.client.CBSecurityException;
import cloudbase.core.client.Instance;
import cloudbase.core.client.TableNotFoundException;
import cloudbase.core.client.impl.TabletLocator.TabletLocation;
import cloudbase.core.conf.CBConfiguration;
import cloudbase.core.data.Column;
import cloudbase.core.data.InitialScan;
import cloudbase.core.data.IterInfo;
import cloudbase.core.data.Key;
import cloudbase.core.data.KeyExtent;
import cloudbase.core.data.KeyValue;
import cloudbase.core.data.Range;
import cloudbase.core.data.ScanResult;
import cloudbase.core.data.Value;
import cloudbase.core.security.thrift.AuthInfo;
import cloudbase.core.security.thrift.ThriftSecurityException;
import cloudbase.core.tabletserver.thrift.NoSuchScanIDException;
import cloudbase.core.tabletserver.thrift.NotServingTabletException;
import cloudbase.core.tabletserver.thrift.TabletClientService;
import cloudbase.core.util.TextUtil;
import cloudbase.core.util.UtilWaitThread;

import com.facebook.thrift.TException;
import com.facebook.thrift.protocol.TBinaryProtocol;
import com.facebook.thrift.protocol.TProtocol;
import com.facebook.thrift.transport.TTransport;

public class ThriftScanner
{
	
	static Logger log = Logger.getLogger(ThriftScanner.class.getName());
	static private int retries = 0;
	
	public static boolean getBatchFromServer(AuthInfo credentials, Text startRow, KeyExtent extent, String server, SortedMap<Key, Value> results, SortedSet<Column> fetchedColumns, boolean skipStartKey, int size, Set<Short> authorizations)
	throws CBException, CBSecurityException, NotServingTabletException
	{
		Key startKey;
		
		if (fetchedColumns.size() > 0)
			startKey = new Key(TextUtil.getBytes(startRow), fetchedColumns.first().columnFamily, fetchedColumns.first().columnQualifier, fetchedColumns.first().columnVisibility, Long.MAX_VALUE);
		else
			startKey = new Key(startRow);
		
		if (skipStartKey)
			startKey = startKey.followingKey(1);
		else
			startKey.setTimestamp(Long.MAX_VALUE);
			
		return getBatchFromServer(credentials, startKey, (Key)null, extent, server, results, fetchedColumns, size, authorizations);
	}
	
	static boolean getBatchFromServer(AuthInfo credentials, Key key, Key endKey, KeyExtent extent, String server, SortedMap<Key, Value> results, SortedSet<Column> fetchedColumns, int size, Set<Short> authorizations)
	throws CBException, CBSecurityException, NotServingTabletException
	{
		if (server == null)
			throw new CBException(new IOException());
		
		if (retries < 1)
		    retries = Math.max(CBConfiguration.getInstance().getInt("cloudbase.client.retries", CBConstants.MAX_CLIENT_RETRIES), CBConstants.MIN_CLIENT_RETRIES);

		for (int numTries = 0; numTries < retries; numTries++)
		{
			TTransport transport = null;

			try {
				transport = ThriftTansportPool.getInstance().getTransport(server, CBConfiguration.getInstance().getInt("cloudbase.tabletserver.clientPort", CBConstants.TABLET_CLIENT_PORT_DEFAULT));
				TProtocol protocol = new TBinaryProtocol(transport);
				TabletClientService.Client client = new TabletClientService.Client(protocol);

				List<IterInfo> emptyList = Collections.emptyList();
                Map<String, Map<String, String>> emptyMap = Collections.emptyMap();
                ScanState scanState = new ScanState(credentials, extent.getTableName(), authorizations, new Range(key, true, endKey, true), fetchedColumns, size, emptyList, emptyMap );
                
				InitialScan isr = client.startScan(scanState.credentials, extent, scanState.range, scanState.columns, scanState.size, scanState.serverSideIteratorList, scanState.serverSideIteratorOptions, scanState.authorizations);

				for (KeyValue kv : isr.result.data)
					results.put(kv.key, new Value(kv.value));

				if (isr.result.more)
					client.closeScan(isr.scanID);
				
				return isr.result.more;
			
			} catch (TException e) {
				log.warn("Error getting transport to " + server + ": " + e);
			} catch (ThriftSecurityException e) {
				log.warn("Security Violation in scan request to " + server + ": " + e);
				throw new CBSecurityException(e.user, e.baduserpass, e);
			} finally {
				ThriftTansportPool.getInstance().returnTransport(transport);
			}
		}
		
		throw new CBException("getBatchFromServer: failed");
	}
	
	
	public static class ScanState {
		
		Text tableName;
		Text startRow;
		boolean skipStartRow;
		
		Range range;

		int size;
		
		AuthInfo credentials;
		Set<Short> authorizations;
		List<Column> columns;
		
		TabletLocation prevLoc;
		Long scanID;
		KeyExtent lastExtent;
		
		boolean finished = false;
		
		List<IterInfo> serverSideIteratorList;
		
		Map<String, Map<String, String>> serverSideIteratorOptions;
		
		public ScanState(AuthInfo credentials, Text tableName, Set<Short> authorizations, Range range, SortedSet<Column> fetchedColumns, int size, List<IterInfo> serverSideIteratorList, Map<String, Map<String, String>> serverSideIteratorOptions)
		{
			this.credentials = credentials;
			this.authorizations = authorizations;
			
			columns = new ArrayList<Column>(fetchedColumns.size());
			for (Column column : fetchedColumns) {
				columns.add(column);
			}
			
			this.tableName = tableName;
			this.range = range;
			
			Key startKey = range.getStartKey();
			if(startKey == null){
				startKey = new Key();
			}
			this.startRow = startKey.getRow();
			
			this.skipStartRow = false;
			
			this.size = size;
			
			this.serverSideIteratorList = serverSideIteratorList;
			this.serverSideIteratorOptions = serverSideIteratorOptions;
			
		}
	}

    public static class ScanTimedOutException extends IOException {

		private static final long serialVersionUID = 1L;
		
	}
	
	public static SortedMap<Key, Value> scan(Instance instance, AuthInfo credentials, ScanState scanState, int timeOut)
	throws ScanTimedOutException, CBException, CBSecurityException, TableNotFoundException
	{
		TabletLocation loc = null;
		
		long startTime = System.currentTimeMillis();
		String lastError = null;
		String error = null;
				
		SortedMap<Key, Value> results = null;
		
		while(results == null && !scanState.finished){
			
			if ((System.currentTimeMillis() - startTime) / 1000.0 > timeOut)
				throw new ScanTimedOutException();
			
			while(loc == null)
			{
				if((System.currentTimeMillis() - startTime) / 1000.0 > timeOut)
					throw new ScanTimedOutException();
				
				try {
					loc = TabletLocator.getInstance(instance, credentials, scanState.tableName).locateTablet(scanState.startRow, scanState.skipStartRow);
					if(loc == null)
					{
						error = "Failed to locate tablet for table : "+scanState.tableName+" row : "+ scanState.startRow;
						if(!error.equals(lastError))
							log.error(error);
						lastError = error;
						UtilWaitThread.sleep(100);
					}
				} catch (CBException e) {
					error = "exception from tablet loc "+e.getMessage();
					if(!error.equals(lastError))
						log.warn(error);
					lastError = error;
					UtilWaitThread.sleep(100);
				}
			}
			
			
			try {
				results = scan(loc, scanState);
			} catch (CBSecurityException e) {
				throw e;
			}catch (NotServingTabletException e) {
				TabletLocator.getInstance(instance, credentials, scanState.tableName).invalidateCache(loc.tablet_extent);
				loc = null;
				UtilWaitThread.sleep(100);
			} catch (TException e) {
				TabletLocator.getInstance(instance, credentials, scanState.tableName).invalidateCache(loc.tablet_location);
				loc = null;
				error = "Some thrift error "+e.getClass().getName()+"  "+e.getMessage();
				if (!error.equals(lastError))
					log.warn(error);
				lastError = error;
				UtilWaitThread.sleep(100);
			} catch (NoSuchScanIDException e) {
				scanState.scanID = null;
			}
		}
		
		if(results != null && results.size() == 0 && scanState.finished){
			results = null;
		}
		
		return results;
	}
	
	private static SortedMap<Key, Value> scan(TabletLocation loc, ScanState scanState) throws CBSecurityException, NotServingTabletException, TException, NoSuchScanIDException
	{
		if (scanState.finished)
			return null;
		
		TTransport transport = ThriftTansportPool.getInstance().getTransport(loc.tablet_location, CBConfiguration.getInstance().getInt("cloudbase.tabletserver.clientPort", CBConstants.TABLET_CLIENT_PORT_DEFAULT));
		TProtocol protocol = new TBinaryProtocol(transport);
		TabletClientService.Client client = new TabletClientService.Client(protocol);
		
		try {
			ScanResult sr;
			
			if(scanState.prevLoc != null && !scanState.prevLoc.equals(loc))
				scanState.scanID = null;
			
			scanState.prevLoc = loc;
			
			if(scanState.scanID == null) {
			    // log.debug ("Calling start scan : "+scanState.range+"  loc = "+loc);
				InitialScan is = client.startScan(scanState.credentials, loc.tablet_extent, scanState.range, scanState.columns, scanState.size, scanState.serverSideIteratorList, scanState.serverSideIteratorOptions, scanState.authorizations);
				sr = is.result;
				
				if (sr.more)
					scanState.scanID = is.scanID;
				else
					client.closeScan(is.scanID);

			} else {
			    // log.debug("Calling continue scan : "+scanState.range+"  loc = "+loc);
				sr = client.continueScan(scanState.scanID);
				if(!sr.more)
				{
					client.closeScan(scanState.scanID);
					scanState.scanID = null;
				}
			}
			
			if (!sr.more)
			{
			    // log.debug("No more : tab end row = "+loc.tablet_extent.getEndRow()+" range = "+scanState.range);
				
				if(loc.tablet_extent.getEndRow() == null)
					scanState.finished = true;
				else if (scanState.range.getEndKey() == null || scanState.range.getEndKey().getRow().compareTo(loc.tablet_extent.getEndRow()) > 0)
				{
					scanState.startRow = loc.tablet_extent.getEndRow();
					scanState.skipStartRow = true;
				}
				else
					scanState.finished = true;
			}
			
			//transport.close();	
			
			SortedMap<Key, Value> results = new TreeMap<Key, Value>();
			
			for(KeyValue kv : sr.data)
				results.put(kv.key, new Value(kv.value));
			
			if(results.size() > 0 && !scanState.finished)
				scanState.range = new Range(results.lastKey(), false, scanState.range.getEndKey(), scanState.range.isEndKeyInclusive());
			
			return results;

		} catch (ThriftSecurityException e) {
			throw new CBSecurityException(e.user, e.baduserpass, e);
		} finally {
			ThriftTansportPool.getInstance().returnTransport(transport);
		}
	}
}
