package cloudbase.core.tabletserver;

/**
 * 
 * Design Notes:
 * 
 * serving a tablet consists in having exclusive (among tablet servers) access 
 * to a range of rows, and the files that store their information
 * 
 * one Tablet server serves several tablets at once
 * 
 * implements a ScanCache containing recently read values for
 * exploitation of temporal locality
 * 
 * we may get exploitation of spatial locality through the mechanism
 * of reading in 64Kb (?) blocks from MapFiles - check on this
 * if not, we need to implement a BlockCache
 * 
 * 
 * Jobs
 * 
 * Load information from tablets into memory (indices ...)
 * Listen for requests from clients
 * Upon receiving a new request, spawn a worker thread (limit?)
 * manage worker threads
 * 
 * Worker thread
 * 
 * handle row requests from clients, parse out columns
 * manage scanner state
 * access row information from HDFS
 * send data to the client
 * receive interrupts from server, close files and die
 * 
 * 
 */

import java.io.IOException;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Map.Entry;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;

import cloudbase.core.CBConstants;
import cloudbase.core.Cloudbase;
import cloudbase.core.client.CBException;
import cloudbase.core.client.CBSecurityException;
import cloudbase.core.client.TableNotFoundException;
import cloudbase.core.client.impl.MasterClient;
import cloudbase.core.client.mapreduce.bulk.BulkOperations;
import cloudbase.core.conf.CBConfiguration;
import cloudbase.core.constraints.Violations;
import cloudbase.core.data.Column;
import cloudbase.core.data.ConstraintViolationSummary;
import cloudbase.core.data.InitialScan;
import cloudbase.core.data.IterInfo;
import cloudbase.core.data.KeyExtent;
import cloudbase.core.data.Mutation;
import cloudbase.core.data.Range;
import cloudbase.core.data.ScanResult;
import cloudbase.core.data.UpdateErrors;
import cloudbase.core.data.Value;
import cloudbase.core.master.thrift.Compacting;
import cloudbase.core.master.thrift.MasterTabletService;
import cloudbase.core.master.thrift.TabletRates;
import cloudbase.core.master.thrift.TabletServerStatus;
import cloudbase.core.security.Authenticator;
import cloudbase.core.security.LabelConversions;
import cloudbase.core.security.SystemPermission;
import cloudbase.core.security.TablePermission;
import cloudbase.core.security.ZKAuthenticator;
import cloudbase.core.security.thrift.AuthInfo;
import cloudbase.core.security.thrift.ThriftSecurityException;
import cloudbase.core.tabletserver.Tablet.Batch;
import cloudbase.core.tabletserver.Tablet.KVEntry;
import cloudbase.core.tabletserver.Tablet.LookupResult;
import cloudbase.core.tabletserver.Tablet.MajorCompactionStats;
import cloudbase.core.tabletserver.Tablet.SplitInfo;
import cloudbase.core.tabletserver.Tablet.TConstraintViolationException;
import cloudbase.core.tabletserver.Tablet.TabletClosedException;
import cloudbase.core.tabletserver.TabletServerResourceManager.TabletResourceManager;
import cloudbase.core.tabletserver.mastermessage.MasterMessage;
import cloudbase.core.tabletserver.mastermessage.PongMessage;
import cloudbase.core.tabletserver.mastermessage.ReportTabletMessage;
import cloudbase.core.tabletserver.mastermessage.ShutdownResponseMessage;
import cloudbase.core.tabletserver.mastermessage.SplitReportMessage;
import cloudbase.core.tabletserver.mastermessage.TabletStatusMessage;
import cloudbase.core.tabletserver.mastermessage.TabletUnloadedMessage;
import cloudbase.core.tabletserver.thrift.ConstraintViolationException;
import cloudbase.core.tabletserver.thrift.NoSuchScanIDException;
import cloudbase.core.tabletserver.thrift.NotServingTabletException;
import cloudbase.core.tabletserver.thrift.TabletClientService;
import cloudbase.core.tabletserver.thrift.TabletMasterService;
import cloudbase.core.util.AddressUtil;
import cloudbase.core.util.Daemon;
import cloudbase.core.util.LoggingRunnable;
import cloudbase.core.util.MetadataTable;
import cloudbase.core.util.Stat;
import cloudbase.core.util.TServerUtils;
import cloudbase.core.util.UtilWaitThread;
import cloudbase.core.zookeeper.ZooConstants;
import cloudbase.core.zookeeper.ZooLock;
import cloudbase.core.zookeeper.ZooSession;
import cloudbase.core.zookeeper.ZooLock.LockLossReason;
import cloudbase.core.zookeeper.ZooLock.LockWatcher;

import com.facebook.thrift.TException;
import com.facebook.thrift.TProcessor;
import com.facebook.thrift.protocol.TBinaryProtocol;
import com.facebook.thrift.server.TServer;
import com.facebook.thrift.server.TThreadPoolServer;
import com.facebook.thrift.transport.TServerTransport;
import com.facebook.thrift.transport.TSocket;


public class TabletServer implements Runnable {
	private static Logger log = Logger.getLogger(TabletServer.class.getName());
	
	private static final String LOG_CONFIG = "//conf//tablet_server_logger.ini";

	private static AuthInfo systemCredentials_ = null; 

	private static synchronized AuthInfo systemCredentials() {
	    if (systemCredentials_ == null) {
	        systemCredentials_ = new AuthInfo(ZooConstants.SYSTEM_USERNAME, ZooConstants.SYSTEM_PASSWORD);
	    }
	    return systemCredentials_;
	}
	
	
	private static HashMap<String, Long> prevGcTime = new HashMap<String, Long>(); 
	private static long lastMemorySize = 0;
	private synchronized static void logGCInfo() {
		List<GarbageCollectorMXBean> gcmBeans = ManagementFactory.getGarbageCollectorMXBeans();
		Runtime rt = Runtime.getRuntime();
		
		StringBuilder sb = new StringBuilder("gc");
		
		boolean sawChange = false;
		
		for (GarbageCollectorMXBean gcBean : gcmBeans) {
			Long prevTime = prevGcTime.get(gcBean.getName());
			long pt = 0;
			if(prevTime != null){
				pt = prevTime;
			}
			
			long time = gcBean.getCollectionTime();
			
			if(time - pt != 0){
				sawChange = true;
			}
			
			sb.append(String.format(" %s=%,.2f(+%,.2f) secs",gcBean.getName(),time/1000.0, (time - pt)/1000.0));
			
			prevGcTime.put(gcBean.getName(), time);
		}
		
		long mem = rt.freeMemory();
		
		if(mem > lastMemorySize){
			sawChange = true;
		}
		
		String sign = "+";
		if(mem - lastMemorySize <= 0){
			sign = "";
		}
		
		sb.append(String.format(" freemem=%,d(%s%,d) totalmem=%,d", mem,sign,(mem-lastMemorySize),rt.totalMemory()));
		
		if(sawChange){
			log.debug(sb.toString());
		}
		
		lastMemorySize = mem;
	}
	
	private TabletTimer timer;

	private static class Session {
		long lastAccessTime;
		long startTime;
	}

	private static class SessionManager {

		Random random;
		Map<Long, Session> sessions;

		SessionManager(){
			random = new Random();
			sessions = new HashMap<Long, Session>();

			final int maxIdle = CBConfiguration.getInstance().getInt("cloudbase.core.tabletserver.session.maxIdleTime", 60*10);
			
			TimerTask r = new TimerTask(){
				public void run() {
					sweep(maxIdle);					
				}
			};

			getJtimer().schedule(r, 0, Math.max(maxIdle * 1000 / 2, 1000));
		}

		synchronized long createSession(Session session){
			long sid = random.nextLong();

			while(sessions.containsKey(sid)){
				sid = random.nextLong();
			}

			sessions.put(sid, session);

			session.startTime = session.lastAccessTime = System.currentTimeMillis();

			return sid;
		}

		synchronized Session getSession(long sessionId){
			Session session = sessions.get(sessionId);
			session.lastAccessTime = System.currentTimeMillis();
			return session;
		}

		synchronized Session removeSession(long sessionId){
			return sessions.remove(sessionId);
		}

		synchronized void sweep(int maxIdle){
			Iterator<Session> iter = sessions.values().iterator();
			while (iter.hasNext()) {
				Session session = iter.next();
				int idleTime = (int)((System.currentTimeMillis() - session.lastAccessTime) / 1000);
				if(idleTime > maxIdle){
					iter.remove();
				}
			}
		}
	}

	private static class UpdateSession extends Session {
		KeyExtent currentExtent;
		public Tablet currentTablet;
		public long successfulCommits;
		Map<KeyExtent, Long> failures = new HashMap<KeyExtent, Long>();
		List<KeyExtent> authFailures = new ArrayList<KeyExtent>();
		public Violations violations;
		public AuthInfo credentials;
		public long totalUpdates = 0;
		Stat commitTimes = new Stat();
		Stat authTimes = new Stat();
	}

	private static class ScanSession  extends Session {
		public KeyExtent extent;
		public AuthInfo credentials;
		public HashSet<Column> columnSet;
		public Range range;
		public int batchSize;
		public List<IterInfo> ssiList;
		public Map<String, Map<String, String>> ssio;
		public long entriesReturned = 0;
		public Stat nbTimes = new Stat();
		public byte[] authorizations;
	}

	private static class MultiScanSession extends Session {
		AuthInfo credentials;
		HashSet<Column> columnSet;
		Map<KeyExtent, List<Range>> queries;
		Map<KeyExtent, List<Range>> failedQueries;
		public List<IterInfo> ssiList;
		public Map<String, Map<String, String>> ssio;
		public byte[] authorizations;
	}

	private class ThriftClientHandler implements TabletClientService.Iface {

		SessionManager sessionManager;

		CBConfiguration cbConf = CBConfiguration.getInstance();

		ThriftClientHandler(){
			log.debug(ThriftClientHandler.class.getName()+" created");
			sessionManager = new SessionManager();
		}

		public List<KeyExtent> bulkImport(AuthInfo credentials, Map<KeyExtent, Map<String, Long>> files) 
		throws TException, ThriftSecurityException
		{
			ArrayList<KeyExtent> failures = new ArrayList<KeyExtent>();

			for(Entry<KeyExtent, Map<String, Long>> entry : files.entrySet())
			{
				try {
					if (!authenticator.hasTablePermission(credentials, credentials.user, entry.getKey().getTableName().toString(), TablePermission.BULK_IMPORT))
						throw new ThriftSecurityException(credentials.user, false);
				} catch (CBSecurityException e) {
					throw new ThriftSecurityException(e.user, e.baduserpass);
				}
			}

			for(Entry<KeyExtent, Map<String, Long>> entry : files.entrySet())
			{
				KeyExtent tke = entry.getKey();
				Map<String, Long> fileMap = entry.getValue();

				Tablet importTablet = onlineTablets.get(tke);

				if(importTablet == null){
					failures.add(tke);
				}else{
					try{
						importTablet.importMapFiles(fileMap);
					}catch(IOException ioe){
						log.warn(ioe.getMessage(), ioe);
						failures.add(tke);
					}
				}

			}

			return failures;
		}

		public InitialScan startScan(AuthInfo credentials, KeyExtent extent, Range range, List<Column> columns, int batchSize, List<IterInfo> ssiList, Map<String, Map<String, String>> ssio, Set<Short> authorizations) throws TException, NotServingTabletException, ThriftSecurityException {

			Set<Short> whitelist = null;
			
			try {
			    if (!authenticator.hasTablePermission(credentials, credentials.user, extent.getTableName().toString(), TablePermission.READ))
					throw new ThriftSecurityException(credentials.user, false);
				whitelist = authenticator.getUserAuthorizations(credentials, credentials.user);
			} catch (CBSecurityException e) {
			    log.error("Security error:", e);
				throw new ThriftSecurityException(e.user, e.baduserpass);
			}

			Set<Short> keepers = new HashSet<Short>();
			for (Short shorts : authorizations)
				if (whitelist.contains(shorts))
					keepers.add(shorts);
			short[] authors = new short[keepers.size()];
			int cnt = 0;
			for (Short shorts : keepers) {
				authors[cnt] = shorts;
				cnt++;
			}
				
			ScanSession scanSession = new ScanSession();
			scanSession.authorizations = LabelConversions.formatAuthorizations(authors);
			scanSession.extent = extent;
			scanSession.credentials = credentials;
			scanSession.columnSet = new HashSet<Column>();
			scanSession.range = range;
			scanSession.batchSize = batchSize;
			scanSession.ssiList = ssiList;
			scanSession.ssio = ssio;

			for (Column column : columns) {
				scanSession.columnSet.add(column);
			}

			long sid = sessionManager.createSession(scanSession);

			ScanResult scanResult;
			try {
				scanResult = continueScan(sid);
			} catch (NoSuchScanIDException e) {
				log.error("The impossible happened", e);
				throw new cloudbase.core.tabletserver.thrift.NotServingTabletException(extent);
			}

			return new InitialScan(sid, scanResult);
		}

        public ScanResult continueScan(long scanID) throws NoSuchScanIDException, TException, NotServingTabletException {

			ScanSession scanSession = (ScanSession) sessionManager.getSession(scanID);
			if(scanSession == null){
				throw new NoSuchScanIDException();
			}

			Tablet tablet = onlineTablets.get(scanSession.extent);

			if(tablet == null){
				throw new cloudbase.core.tabletserver.thrift.NotServingTabletException(scanSession.extent);
				//return new ScanResult(Collections.EMPTY_LIST, ScanReturnCode.TABLET_CLOSED);
			}

			Batch bresult;

			try {
				long t1 = System.currentTimeMillis();
				bresult = tablet.nextBatch(scanSession.range, scanSession.batchSize, scanSession.columnSet, scanSession.authorizations,
						scanSession.ssiList, scanSession.ssio);
				long t2 = System.currentTimeMillis();
				scanSession.nbTimes .addStat(t2 - t1);
			} catch (IOException e) {
				log.warn("Tablet look up failed ", e);
				throw new cloudbase.core.tabletserver.thrift.NotServingTabletException(scanSession.extent);
			} catch (TabletClosedException e) {
				throw new cloudbase.core.tabletserver.thrift.NotServingTabletException(scanSession.extent);
			}

			ScanResult scanResult;

			if(bresult.results == null){
				scanResult = new ScanResult(Collections.EMPTY_LIST,false);
			}else if(bresult.continueKey == null){
				scanResult = new ScanResult((List)bresult.results, false);
			}else{
				scanSession.range = new Range(bresult.continueKey, !bresult.skipContinueKey, scanSession.range.getEndKey(), scanSession.range.isEndKeyInclusive());
				scanResult = new ScanResult((List)bresult.results, true);
			}	

			scanSession.entriesReturned += scanResult.data.size();
			
			return scanResult;
		}

		public void closeScan(long scanID) throws TException {
			ScanSession ss = (ScanSession) sessionManager.removeSession(scanID);
			if(ss != null){
				log.debug(String.format("ScanSess %,d entries in %.2f secs, nbTimes = [%s] ", ss.entriesReturned, (System.currentTimeMillis() - ss.startTime)/1000.0, ss.nbTimes.toString()));
				
			}
		}

		public InitialScan startMultiScan(AuthInfo credentials, Map<KeyExtent, List<Range>> batch, List<Column> columns, List<IterInfo> ssiList, Map<String, Map<String, String>> ssio, Set<Short> authorizations)
		throws TException, ThriftSecurityException
		{
			Set<Short> whitelist = null;
			HashSet<String> tables = new HashSet<String>();
			for (KeyExtent keyExtent : batch.keySet()) {
				tables.add(keyExtent.getTableName().toString());
			}
			
			for (String table : tables)
				try {
					if (!authenticator.hasTablePermission(credentials, credentials.user, table, TablePermission.READ))
						throw new ThriftSecurityException(credentials.user, false);
					whitelist = authenticator.getUserAuthorizations(credentials, credentials.user);
				} catch (CBSecurityException e) {
					throw new ThriftSecurityException(e.user, e.baduserpass);
				}


			Set<Short> keepers = new HashSet<Short>();
			for (Short shorts : authorizations)
				if (whitelist.contains(shorts))
					keepers.add(shorts);
			short[] authors = new short[keepers.size()];
			int cnt = 0;
			for (Short shorts : keepers) {
				authors[cnt] = shorts;
				cnt++;
			}
			
			MultiScanSession mss = new MultiScanSession();
			mss.queries = batch;
			mss.failedQueries = new HashMap<KeyExtent, List<Range>>();
			mss.credentials = credentials;
			mss.columnSet = new HashSet<Column>(columns.size());
			mss.ssiList = ssiList;
			mss.ssio = ssio;
			mss.authorizations = LabelConversions.formatAuthorizations(authors);

			for (Column column : columns) {
				mss.columnSet.add(column);
			}

			long sid = sessionManager.createSession(mss);

			ScanResult result;
			try {
				result = continueMultiScan(sid);
			} catch (NoSuchScanIDException e) {
				log.error("the impossible happened", e);
				throw new TException("the impossible happened", e);
			}

			return new InitialScan(sid, result);
		}

        public ScanResult continueMultiScan(long scanID) throws NoSuchScanIDException, TException {
			MultiScanSession session = (MultiScanSession) sessionManager.getSession(scanID);
			if(session == null){
				throw new NoSuchScanIDException();
			}

			int maxResultsSize = cbConf.getInt("cloudbase.tablet.scan.max.memory", 50000000);
			long bytesAdded = 0;

			ArrayList<KVEntry> results = new ArrayList<KVEntry>();

			Iterator<Entry<KeyExtent, List<Range>>> iter = session.queries.entrySet().iterator();
			while(iter.hasNext() && bytesAdded < maxResultsSize){
				Entry<KeyExtent, List<Range>> entry = iter.next();

				iter.remove();

				Tablet tablet = onlineTablets.get(entry.getKey());
				if(tablet == null){
					session.failedQueries.put(entry.getKey(), entry.getValue());
					continue;
				}

				LookupResult lookupResult;
				int sizeBeforeLookup = results.size();
				try {
					lookupResult = tablet.lookup(entry.getValue(), session.columnSet, session.authorizations, results, maxResultsSize - bytesAdded, session.ssiList, session.ssio);
				} catch (IOException e) {
					log.warn("lookup failed for tablet "+entry.getKey(), e);

					//roll back anything this lookup added
					results = new ArrayList<KVEntry>(results.subList(0, sizeBeforeLookup));

					session.failedQueries.put(entry.getKey(), entry.getValue());
					continue;
				}

				bytesAdded += lookupResult.bytesAdded;

				if(lookupResult.unfinishedRanges.size() > 0){
					if(lookupResult.closed){
						session.failedQueries.put(entry.getKey(), lookupResult.unfinishedRanges);
					}else{
						session.queries.put(entry.getKey(), lookupResult.unfinishedRanges);
					}
				}
			}


			return new ScanResult((List)results, session.queries.size() != 0);
		}

		public Map<KeyExtent, List<Range>> closeMultiScan(long scanID) throws NoSuchScanIDException, TException {
			MultiScanSession session = (MultiScanSession) sessionManager.removeSession(scanID);
			if(session == null){
				throw new NoSuchScanIDException();
			}

			return session.failedQueries;
		}


		public long startUpdate(AuthInfo credentials)
		throws TException, ThriftSecurityException
		{
			// Make sure user is real
			try {
				if (!authenticator.authenticateUser(credentials, credentials.user, credentials.password))
					throw new ThriftSecurityException(credentials.user, true);
			} catch (CBSecurityException e) {
				throw new ThriftSecurityException(e.user, e.baduserpass);
			}

			UpdateSession us = new UpdateSession();
			us.violations = new Violations();
			us.credentials = credentials;

			long sid = sessionManager.createSession(us);
			
			return sid;
		}

		public void setUpdateTablet(long updateID, KeyExtent keyExtent)
		throws TException
		{
			UpdateSession us = (UpdateSession) sessionManager.getSession(updateID);
			if(us != null){
				long t1 = System.currentTimeMillis();
				try {
					// if user has no permission to write to this table, add it to the failures list
					if (authenticator.hasTablePermission(systemCredentials(), us.credentials.user, keyExtent.getTableName().toString(), TablePermission.WRITE))
					{
						long t2 = System.currentTimeMillis();
						us.authTimes.addStat(t2 - t1);
						us.currentExtent = keyExtent;
						us.successfulCommits = 0;
						us.currentTablet = onlineTablets.get(keyExtent);

						if(us.currentTablet == null)
							us.failures.put(us.currentExtent, us.successfulCommits);
					}else{
						log.warn("Denying access to table "+keyExtent.getTableName()+" for user "+us.credentials.user);
						long t2 = System.currentTimeMillis();
						us.authTimes.addStat(t2 - t1);
						us.currentTablet = null;
						us.authFailures.add(keyExtent);
						return;
					}
				} catch (CBSecurityException e) {
					log.error("Denying permission to check user "+us.credentials.user+" with user "+e.user, e);
					long t2 = System.currentTimeMillis();
					us.authTimes.addStat(t2 - t1);
					us.currentTablet = null;
					us.authFailures.add(keyExtent);
					return;
				}
			}else{
				throw new TException("Unknown sessionID");
			}
		}

		public void applyUpdate(long updateID, Mutation mutation) throws TException {
			UpdateSession us = (UpdateSession) sessionManager.getSession(updateID);
			if(us != null){
				us.totalUpdates++;
				if(us.currentTablet != null){
					//TODO check if mutation goes to tablet

					boolean commmitSuccessful = true;
					try {
						long t1 = System.currentTimeMillis();
						commmitSuccessful = us.currentTablet.commit(mutation);
						long t2 = System.currentTimeMillis();
						us.commitTimes.addStat(t2 - t1);
					} catch (TConstraintViolationException e) {
						us.violations.add(e.getViolations());
					}

					if(commmitSuccessful){
						us.successfulCommits++;
					}else{
						//set currentTablet to null so that all subsequent updates
						//to this tablet fail
						us.currentTablet = null;
						us.failures.put(us.currentExtent, us.successfulCommits);
					}
				}
			}else{
                throw new TException("Unknown sessionID");
			}
		}

		public UpdateErrors closeUpdate(long updateID) throws TException, NoSuchScanIDException {
			UpdateSession us = (UpdateSession) sessionManager.removeSession(updateID);
			if(us == null){
				throw new NoSuchScanIDException();
			}

			log.debug(String.format("UpSess %,d ups in %.2f secs, authTimes = [%s] commitTimes = [%s]", us.totalUpdates, (System.currentTimeMillis() - us.startTime)/1000.0, us.authTimes.toString(), us.commitTimes.toString()));
			if (us.failures.size() > 0) {
			    Entry<KeyExtent, Long> first = us.failures.entrySet().iterator().next();
			    log.debug(String.format("Failures: %d, first %s occurs %d", us.failures.size(), first.getKey().toString(), first.getValue()));
			}
			List<ConstraintViolationSummary> violations = us.violations.asList();
			if (violations.size() > 0) {
			    ConstraintViolationSummary first = us.violations.asList().iterator().next();
			    log.debug(String.format("Violations: %d, first %s occurs %d", violations.size(), first.violationDescription, first.numberOfViolatingMutations));
			}
			if (us.authFailures.size() > 0) {
			    KeyExtent first = us.authFailures.iterator().next();
			    log.debug(String.format("Authentication Failures: %d, first %s", us.authFailures.size(), first.toString()));
			}
			return new UpdateErrors(us.failures, violations, us.authFailures);
		}

		public void update(AuthInfo credentials, KeyExtent keyExtent, Mutation mutation) 
		throws NotServingTabletException, TException, ConstraintViolationException, ThriftSecurityException 
		{
			try {
				if (!authenticator.hasTablePermission(credentials, credentials.user, keyExtent.getTableName().toString(), TablePermission.WRITE))
					throw new ThriftSecurityException(credentials.user, false);
			} catch (CBSecurityException e) {
				throw new ThriftSecurityException(e.user, e.baduserpass);
			}

			Tablet tablet = onlineTablets.get(keyExtent);

			//log.debug("update("+keyExtent+","+mutation+")");

			try {
				if(tablet == null || !tablet.commit(mutation)){
					throw new NotServingTabletException(keyExtent);
				}
			} catch (TConstraintViolationException e) {
				throw new ConstraintViolationException(e.getViolations().asList());
			}
		}
	}

	private MajorCompactionStats compactTablet(Tablet tablet){

		MajorCompactionStats majCStats = null;

		synchronized(tablet) {
			//check that compaction is still needed - defer to splitting
			if ( majorCompactorDisabled || tablet.isClosed() || !tablet.needsMajorCompaction() || tablet.needsSplit())
				return null;
		}

		try {
			majCStats = tablet.majorCompact();
		}
		catch (Throwable t) {
			log.error("MajC Failed, extent = "+tablet.getExtent());
			log.error("MajC Failed, message = "+(t.getMessage() == null ? t.getClass().getName() : t.getMessage()),t);
			throw new RuntimeException(t);
		}

		return majCStats;
	}

	//Wraps a major compaction runnable for timing
	private class CompactionRunner implements Runnable {

		private Tablet tablet;		
		long queued;	
		long start;
		boolean failed = false;

		public CompactionRunner(Tablet t) {			
			queued = System.currentTimeMillis();
			tablet = t;
		}
		public void run() {
			MajorCompactionStats majCStats = null;

			if(majorCompactorDisabled){
				//this will make compaction task that were queued when shutdown was
				//initiated exit
				return;
			}
			
			try {						
				tablet.timer.statusMajor++;
				start = System.currentTimeMillis();				
				majCStats = compactTablet(tablet);
			}
			catch (RuntimeException E) {
				failed = true;
			}
			finally {
				long count = 0;
				if(majCStats != null){
					count = majCStats.getEntriesRead();
				}

				tablet.timer.updateTime(TabletTimer.MAJOR_FINISHED, queued, start, count, failed);							
			}
		}
	}

	private class SplitRunner implements Runnable {
		private Tablet tablet;

		public SplitRunner(Tablet tablet) {
			this.tablet = tablet;
		}

		@Override
		public void run() {
			if(majorCompactorDisabled){
				//this will make split task that were queued when shutdown was
				//initiated exit
				return;
			}
			
			splitTablet(tablet);
		}
	}
	
	private class MajorCompactor implements Runnable{
		private CBConfiguration cbConf = CBConfiguration.getInstance();

		public void run(){
			while(!majorCompactorDisabled){
				UtilWaitThread.sleep(cbConf.getInt("cloudbase.tabletserver.compactionDelay",10) * 1000);

				TreeMap<KeyExtent,Tablet> copyOnlineTablets = new TreeMap<KeyExtent,Tablet>();

				synchronized (onlineTablets) {
					copyOnlineTablets.putAll(onlineTablets); // avoid concurrent modification
				}

				// TODO seriously consider synchronizing on onlineTablets to 
				// avoid migrating things away while we're working with them
				Iterator<Entry<KeyExtent, Tablet>> iter = copyOnlineTablets.entrySet().iterator();
				while(iter.hasNext() && !majorCompactorDisabled) { // bail early now if we're shutting down

					Entry<KeyExtent, Tablet> entry = iter.next();

					// TODO what if a tablet is migrated away while it's in the copyOnlineTablets list?
					if(!onlineTablets.containsKey(entry.getKey())) // quick lookup doesn't need synchronization
						continue; // skip it

					Tablet tablet = entry.getValue();

					// if we need to split AND compact, we need a good way to decide what to do
					if(tablet.needsSplit()) {
						resourceManager.executeSplit(tablet.getExtent(), new LoggingRunnable(log, new SplitRunner(tablet)));
						continue;
					}

					if(!tablet.isClosed() && tablet.needsMajorCompaction()){
						resourceManager.executeMajorCompaction(tablet.getExtent(), new LoggingRunnable(log, new CompactionRunner(tablet)));
					}
				}
			}
		}
	}

	private void splitTablet(Tablet tablet) {
		try {
			TreeMap<KeyExtent, SplitInfo> tabletInfo = tablet.split();
			if(tabletInfo == null){
			 // either split or compact not both
				if(!tablet.isClosed() && tablet.needsMajorCompaction()){
					//were not able to split... so see if a major compaction is needed
					resourceManager.executeMajorCompaction(tablet.getExtent(), new LoggingRunnable(log, new CompactionRunner(tablet)));
				}
				return;
			}

			log.info("Starting split: " + tablet.getExtent());
			timer.statusSplit++;						
			long start = System.currentTimeMillis();

			Tablet[] newTablets = new Tablet[2];

			newTablets[0] = new Tablet(new Text(tabletInfo.firstEntry().getValue().dir), tabletInfo.firstKey(), resourceManager.createTabletResourceManager(), tabletInfo.firstEntry().getValue().ssTables);
			newTablets[1] = new Tablet(new Text(tabletInfo.lastEntry().getValue().dir), tabletInfo.lastKey(), resourceManager.createTabletResourceManager(), tabletInfo.lastEntry().getValue().ssTables);

			tablet.close(false);
			
			enqueueMasterMessage(new SplitReportMessage(tablet.getExtent(), 
					newTablets[0].getExtent(), new Text("/"+newTablets[0].getLocation().getName()), 
					newTablets[1].getExtent(), new Text("/"+newTablets[1].getLocation().getName())));

			// roll tablet stats over into tablet server's timer object as historical data
			timer.saveMinorTimes(tablet.timer.minorNum, tablet.timer.minorElapsed, tablet.timer.minorQueueTime, tablet.timer.minorSumDev, tablet.timer.minorQueueSumDev, tablet.timer.minorCount, tablet.timer.minorFail);
			timer.saveMajorTimes(tablet.timer.majorNum, tablet.timer.majorElapsed, tablet.timer.majorQueueTime, tablet.timer.majorSumDev, tablet.timer.majorQueueSumDev, tablet.timer.majorCount, tablet.timer.majorFail);

			// lose the reference to the old tablet and open two new ones 

			synchronized (onlineTablets) {
				onlineTablets.remove(tablet.getExtent());
				onlineTablets.put(newTablets[0].getExtent(), newTablets[0]);
				onlineTablets.put(newTablets[1].getExtent(), newTablets[1]);
			}

			timer.updateTime(TabletTimer.SPLIT_FINISHED, start, 0, false);
			log.info("Tablet split: " + tablet.getExtent() + " size0 " + newTablets[0].estimateTabletSize() + " size1 " + newTablets[1].estimateTabletSize());
		}catch (IOException e) {
			timer.updateTime(TabletTimer.SPLIT_FINISHED, 0, 0, true);
			log.error("split failed: " + e.getMessage());
			log.error(e.toString());
		}catch(Exception e) {
			timer.updateTime(TabletTimer.SPLIT_FINISHED, 0, 0, true);
			log.error("Unknown error on split: " + e,e);
		}
	}
	
	public long lastPingTime = System.currentTimeMillis();
	public Socket currentMaster;

	// a queue to hold messages that are to be sent back to the master
	private BlockingDeque<MasterMessage> masterMessages = new LinkedBlockingDeque<MasterMessage>();

	// add a message for the main thread to send back to the master
	private void enqueueMasterMessage(MasterMessage m)
	{
		masterMessages.addLast(m);
	}

	// return whether there are any messages waiting to be sent to the master
	private boolean masterMessagesPending()
	{
		return masterMessages.size() > 0;
	}

	private class UnloadTabletHandler implements Runnable
	{
		private KeyExtent extent;
		private boolean saveState;

		public UnloadTabletHandler(KeyExtent extent, boolean saveState)
		{
			this.extent = extent;
			this.saveState = saveState;
		}

		public void run()
		{
			
			Tablet t = null;

			synchronized (unopenedTablets) {
				if(unopenedTablets.contains(extent)) {
					unopenedTablets.remove(extent);
					enqueueMasterMessage(new TabletUnloadedMessage(extent));
					return;
				}
			}
			synchronized(openingTablets) {
				while (openingTablets.contains(extent)) {
					try{openingTablets.wait();}
					catch (InterruptedException e) {
						}
				}
			}
			synchronized(onlineTablets) {			
				if(onlineTablets.containsKey(extent)) {
					t = onlineTablets.get(extent);
				}
			}
			
			if(t == null)
			{
				log.warn("told to unload tablet that was not being served " + extent);
				enqueueMasterMessage(new TabletStatusMessage(CBConstants.MSG_TS_REPORT_TABLET_UNLOAD_FAILURE_NOT_SERVING,extent));
				return;
			}

			synchronized(t)
			{
				// TODO: is it really necessary to fail migration if a major compaction is in progress?
				// should just abandon major compaction
				// check to see if we're doing a major compaction now and fail if so
				if(t.majorCompactionRunning()) {
					log.warn("told to unload while doing compaction, failing ... "+t.getExtent());
					enqueueMasterMessage(new TabletStatusMessage(CBConstants.MSG_TS_REPORT_TABLET_UNLOAD_FAILURE_MAJC,extent));
					return;
				}
				
				// check to see if the tablet is splitting or already migrating
				if(t.isClosed()){
					log.warn("told to unload after tablet was closed " + t.getExtent());
					enqueueMasterMessage(new TabletStatusMessage(CBConstants.MSG_TS_REPORT_TABLET_UNLOAD_FAILURE_CLOSED,extent));
					return;
				}else{
					// take the tablet offline
					try {
						t.close(saveState);
					} catch (IOException e) {
						log.error("Failed to close tablet "+extent+"... Aborting migration",e);
						enqueueMasterMessage(new TabletStatusMessage(CBConstants.MSG_TS_REPORT_TABLET_UNLOAD_ERROR,extent));
						return;
					}
				}
			}

			// stop serving tablet - client will get not serving tablet exceptions
			onlineTablets.remove(extent);

			// tell the master how it went
			enqueueMasterMessage(new TabletUnloadedMessage(extent));

			// roll tablet stats over into tablet server's timer object as historical data
			timer.saveMinorTimes(t.timer.minorNum, t.timer.minorElapsed, t.timer.minorQueueTime, t.timer.minorSumDev, t.timer.minorQueueSumDev, t.timer.minorCount, t.timer.minorFail);
			timer.saveMajorTimes(t.timer.majorNum, t.timer.majorElapsed, t.timer.majorQueueTime, t.timer.majorSumDev, t.timer.majorQueueSumDev, t.timer.majorCount, t.timer.majorFail);

			log.info("unloaded " + extent);

		}
	}

	private class AssignmentHandler implements Runnable
	{
		private KeyExtent extent;
		private Text location;
		public AssignmentHandler(KeyExtent extent, Text location)
		{
			this.extent = extent;
			this.location = location;
		}

		public void run() {
			log.info(masterAddress + ": got assignment from master: " + extent + " " + location);

			synchronized(unopenedTablets) {
				synchronized(openingTablets) {
					// don't open if we already have it
					if(onlineTablets.containsKey(extent)) {
						// do report it though
						log.warn("Already have tablet from " + extent.getTableName().toString() + " open. reporting ...");
						if (extent.getTableName().toString().compareTo(CBConstants.METADATA_TABLE_NAME) == 0){
							enqueueMasterMessage(new TabletStatusMessage(CBConstants.MSG_TS_REPORT_METADATA_TABLET, extent));
							log.info("Sent MSG_TS_REPORT_METADATA_TABLET : " + extent + " to master");
						} else {
							log.info("Sent MSG_TS_REPORT_TABLET_LOADED: " + extent + " to master");
							enqueueMasterMessage(new TabletStatusMessage(CBConstants.MSG_TS_REPORT_TABLET_LOADED, extent));
						}
						return;
					}
					if (!unopenedTablets.contains(extent)) {
						log.debug("Tablet Server had extent " + extent + " unassigned.  Not opening");
						return;
					}
					unopenedTablets.remove(extent);
					openingTablets.add(extent);
				}
			}
			

			log.debug("Loading extent: " + extent);

			// check Metadata table before accepting assignment
			SortedMap<KeyExtent, Text> tabletsInRange;

			try {
				tabletsInRange = verifyTabletInformation(extent, location);
			} catch (CBSecurityException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
            if(tabletsInRange == null) {
                log.info("Reporting tablet " + extent + " assignment failure: unable to verify Tablet Information");
                enqueueMasterMessage(new TabletStatusMessage(CBConstants.MSG_TS_ASSIGNMENT_FAILURE, extent));
                return;
            }
			// If extent given is not the one to be opened, update
			if (tabletsInRange.size()!=1 || !tabletsInRange.containsKey(extent)) {
				synchronized(openingTablets) {
					openingTablets.remove(extent);
					openingTablets.notifyAll();
					for (KeyExtent e : tabletsInRange.keySet()) 
						openingTablets.add(e);
				}
			}
			if(tabletsInRange.size() > 1){
				log.debug("Master didn't know "+extent+" was split, letting it know about "+tabletsInRange.keySet());
				enqueueMasterMessage(new SplitReportMessage(extent, tabletsInRange));
			}

			// create the tablet object
			Iterator<KeyExtent> kei = tabletsInRange.keySet().iterator();
			while(kei.hasNext()){
				Tablet tablet = null;
				int failures = 0;
				boolean successful = false;

				KeyExtent extentToOpen = kei.next();

				if(onlineTablets.containsKey(extentToOpen)) {
					//know this was from fixing a split, because initial check would have caught original extent
					log.warn("Something is screwy!  Already serving tablet "+extentToOpen+" derived from fixing split. Original extent = "+extent);
					synchronized(openingTablets) {
						openingTablets.remove(extentToOpen);
						openingTablets.notifyAll();       
					}
					continue;
				}

				while(failures < 3 && !successful){

					Text locationToOpen = tabletsInRange.get(extentToOpen);

					try {
						TabletResourceManager trm = resourceManager.createTabletResourceManager();

						// this opens the tablet file and fills in the endKey in the extent
						tablet = new Tablet(locationToOpen, extentToOpen, trm);
						synchronized(openingTablets) {
							synchronized(onlineTablets) {
								openingTablets.remove(extentToOpen);
								onlineTablets.put(extentToOpen, tablet);
								openingTablets.notifyAll();
							}
						}
						// inform the master of this extent
						if (extent.getTableName().toString().compareTo(CBConstants.METADATA_TABLE_NAME) == 0){
							enqueueMasterMessage(new TabletStatusMessage(CBConstants.MSG_TS_REPORT_METADATA_TABLET,extentToOpen));
							log.info("Sent MSG_TS_REPORT_METADATA_TABLET : "+extentToOpen+" to master");
						}
						tablet = null; // release this reference
						successful = true;
					}
					catch (Exception e) {
						failures++;
						log.warn("exception trying to assign tablet " + extentToOpen + " " + locationToOpen, e);
						if(e.getMessage() != null)
							log.warn(e.getMessage());

						if(failures < 3){
							UtilWaitThread.sleep(1000);
						}

					}

					if(!successful){
						synchronized(openingTablets) {
							openingTablets.remove(extentToOpen);
							openingTablets.notifyAll();
						}
						log.warn("failed to open tablet "+extentToOpen+" reporting failure to master");
						enqueueMasterMessage(new TabletStatusMessage(CBConstants.MSG_TS_ASSIGNMENT_FAILURE,extentToOpen));
					} else {
						enqueueMasterMessage(new TabletStatusMessage(CBConstants.MSG_TS_REPORT_TABLET_LOADED,extentToOpen));	
					}
				}

			}

		}
	}


	//public volatile boolean serviceStopped;
	private static FileSystem fs;
	private static Configuration conf;

	private SortedMap<KeyExtent, Tablet> onlineTablets;
	private SortedSet<KeyExtent> unopenedTablets;
	private SortedSet<KeyExtent> openingTablets;
	
	private Thread majorCompactorThread;

	//	used for stopping the server and MasterListener thread
	private volatile boolean serverStopRequested = false;

	private static InetSocketAddress masterAddress;
	private static InetSocketAddress clientAddress;

	private double decayRate;

	private TabletServerResourceManager resourceManager;
	private Authenticator authenticator;
	private boolean majorCompactorDisabled = false;

	private volatile boolean shutdownComplete = false;
	private Thread monitorThread;

    private Thread shutdownHook = null;
	private static MonitorThread mt;
	
	private ArrayList<TServer> servers = new ArrayList<TServer>();

	private ZooLock tabletServerLock;

	private static Timer jtimer;
	
	private synchronized static Timer getJtimer() {
		if(jtimer == null){
			jtimer = new Timer();
		}
		return jtimer;
	}

	public TabletTimer getTimer() {
		return timer;
	}

	/** Start a server, at the given port, or higher, if that port is not available.
	 * 
	 * @param portHint the port to attempt to open, can be zero, meaning "any available port"
	 * @param processor the service to be started
	 * @param threadName name this service's thread for better debugging
	 * @param threadPoolSize the maximum size of the thread pool for this service
	 * @return the actual address used
	 * @throws UnknownHostException when we don't know our own address
	 */
	private InetSocketAddress startServer(int portHint, 
	                                     TProcessor processor,
	                                     final String threadName,
	                                     int maxThreads) 
	throws UnknownHostException {
	    String addressString = conf.get("tabletserver.hostname", "");
		InetAddress hostaddr = InetAddress.getLocalHost();
		if(addressString != "")
			hostaddr = InetAddress.getByName(addressString);
		if (maxThreads < 0) {
		    maxThreads = new TThreadPoolServer.Options().maxWorkerThreads; 
		}

		// Are we going to slide around, looking for an open port?
		int portsToSearch = 1;
		if (CBConfiguration.getInstance().getBoolean("cloudbase.tabletserver.portSearch", false))
			portsToSearch = 1000;

		for (int i = 0; i < portsToSearch; i++) {
			int port = portHint + i;
			if (port > 65535)
				port = 1024 + port % 65535;
			InetSocketAddress result = new InetSocketAddress(hostaddr, port);
			TServerTransport serverTransport;
			try {
			    serverTransport = TServerUtils.openPort(port);
			} catch (IOException ex) {
				if (portHint == 0) {
					throw new RuntimeException(ex);
				}
				log.warn("Unable to use port " + port + ", retrying. (Thread Name = "+threadName+")");
				continue;
			}
			servers.add(TServerUtils.startTServer(processor, serverTransport, threadName, -1));
			return result;
		}
		throw new UnknownHostException("Unable to find a listen port");
	}

	private InetSocketAddress startTabletMasterService() throws UnknownHostException {
		TabletMasterServiceHandler handler = new TabletMasterServiceHandler();
		CBConfiguration cbConf = CBConfiguration.getInstance();
		int portHint = cbConf.getInt("cloudbase.tabletserver.masterPort", CBConstants.TABLET_MASTER_PORT_DEFAULT);
		TabletMasterService.Processor processor = new TabletMasterService.Processor(handler);
		return startServer(portHint, processor, "Tablet Master Service", -1);
	}

	private String getMasterHost(){
		try{
			return MasterClient.lookupMaster();
		}catch(Exception e){
			log.warn("Failed to obtain master host " + e, e);
		}
		
		return null;
	}
	
	// Connect to the master for posting asynchronous results
	private MasterTabletService.Client masterConnection(String hostname) {
		try {
			if(hostname == null){
				return null;
			}
			
			int port = CBConfiguration.getInstance().getInt("cloudbase.master.tabletPort", 11223);
			TSocket transport = new TSocket(hostname, port);
			TBinaryProtocol protocol = new TBinaryProtocol(transport);
			MasterTabletService.Client client = new MasterTabletService.Client(protocol);
			transport.open();
			//log.info("Callback API to master has been opened");
			return client;
		} catch (Exception e) {
			log.warn("Issue with masterConnection " + e, e);
		}
		return null;
	}

	// Listen for these asychronous Master requests
	private class TabletMasterServiceHandler implements TabletMasterService.Iface {

		public void loadTablet(AuthInfo credentials, KeyExtent extent, byte[] location)
		throws TException
		{
			try {
				if(!authenticator.hasSystemPermission(credentials, credentials.user, SystemPermission.SYSTEM))
				{
					log.warn("Got loadTablet message from user: "+credentials.user);
					throw new TException("User "+credentials.user+" not authorized");
				}
			} catch (CBSecurityException e) {
				log.warn("Got loadTablet message from unauthenticatable user: "+e.user);
				throw new TException("User "+credentials.user+" not authenticated");
			}

			// add the assignment job to the appropriate queue
			log.info("Loading tablet " + extent);
			Text textLocation = new Text(location);
			unopenedTablets.add(extent);
			Runnable ah = new LoggingRunnable(log, new AssignmentHandler(extent, textLocation));
			// Root tablet assignment must take place immediately
			if (extent.compareTo(CBConstants.ROOT_TABLET_EXTENT) == 0) {
				ah.run();
				log.info("Root tablet loaded: " + extent);
			} else {
				if (extent.getTableName().compareTo(new Text(CBConstants.METADATA_TABLE_NAME)) == 0) {
					resourceManager.addMetaDataAssignment(ah);					
				} else {
					resourceManager.addAssignment(ah);
				}
			}
		}

		public void ping(AuthInfo credentials)
		throws TException
		{
			// anybody can do this, for now
			pong();
		}

		public void sendTabletList(AuthInfo credentials)
		throws TException
		{
			try {
				if(!authenticator.hasSystemPermission(credentials, credentials.user, SystemPermission.SYSTEM))
				{
					log.warn("Got sendTabletList message from user: "+credentials.user);
					throw new TException("User "+credentials.user+" not authorized");
				}
			} catch (CBSecurityException e) {
				log.warn("Got sendTabletList message from unauthenticatable user: "+e.user);
				throw new TException("User "+credentials.user+" not authenticated");
			}
			
			// go through the tablets that are online and send their extents to the master
			SortedSet<KeyExtent> report = new TreeSet<KeyExtent>();
			synchronized(unopenedTablets){
				synchronized(openingTablets) {
					synchronized (onlineTablets) {
						report.addAll(onlineTablets.keySet());
						report.addAll(openingTablets);
						report.addAll(unopenedTablets);
					}
				}
			}
			enqueueMasterMessage(new ReportTabletMessage(report, 
			                                             getClientAddressString(),
			                                             getMonitorAddressString()));
		}

		private String getMonitorAddressString() {
			return AddressUtil.toString(mt.getAddress());
		}

		public void shutdown(AuthInfo credentials, String who, int stage)
		throws TException
		{
			try {
				if(!authenticator.hasSystemPermission(credentials, credentials.user, SystemPermission.SYSTEM))
				{
					log.warn("Got shutdown message from user: "+credentials.user);
					throw new TException("User "+credentials.user+" not authorized");
				}
			} catch (CBSecurityException e) {
				log.warn("Got shutdown message from unauthenticatable user: "+e.user);
				throw new TException("User "+credentials.user+" not authenticated");
			}

			log.info("TabletServer: shutdown command received via Thrift call. Shutting down ...");
			shutdownServer(stage);
		}

		public void unloadTablet(AuthInfo credentials, KeyExtent extent, boolean save)
		throws TException
		{
			try {
				if(!authenticator.hasSystemPermission(credentials, credentials.user, SystemPermission.SYSTEM))
				{
					log.warn("Got unloadTablet message from user: "+credentials.user);
					throw new TException("User "+credentials.user+" not authorized");
				}
			} catch (CBSecurityException e) {
				log.warn("Got unloadTablet message from unauthenticatable user: "+e.user);
				throw new TException("User "+credentials.user+" not authenticated");
			}

			resourceManager.addMigration(new LoggingRunnable(log, new UnloadTabletHandler(extent, save)));
		}

		public void flush(AuthInfo credentials, Set<String> tables)
		throws TException
		{
			try {
				if(!authenticator.hasSystemPermission(credentials, credentials.user, SystemPermission.SYSTEM))
				{
					log.warn("Got flush message from user: "+credentials.user);
					throw new TException("User "+credentials.user+" not authorized");
				}
			} catch (CBSecurityException e) {
				log.warn("Got flush message from unauthenticatable user: "+e.user);
				throw new TException("User "+credentials.user+" not authenticated");
			}

			ArrayList<Tablet> tabletsToFlush = new ArrayList<Tablet>();
			
			synchronized(onlineTablets) {
				for(Tablet tablet : onlineTablets.values()) {
					if(tables.contains(tablet.getExtent().getTableName().toString())){
						tabletsToFlush.add(tablet);
					}
				}
			}
			
			for (Tablet tablet : tabletsToFlush) {
				tablet.initiateMinorCompaction();
			}

		}

	}

	public InetSocketAddress startTabletClientService() throws UnknownHostException {
		//start listening for client connection last
		ThriftClientHandler tch = new ThriftClientHandler();
		TabletClientService.Processor processor = new TabletClientService.Processor(tch);
		int maxThreads = CBConfiguration.getInstance().getInt("cloudbase.tabletserver.clientHandlers.maxConcurrent", 16);
		int port = CBConfiguration.getInstance().getInt("cloudbase.tabletserver.clientPort", CBConstants.TABLET_CLIENT_PORT_DEFAULT);
		log.info("port = "+port);
		return startServer(port, processor, "Thrift Client Server", maxThreads);
	}

	/**
	 * listen for connections from the Master on masterPort
	 * wait for tablet assignments from master, open them
	 * tell the master the key extents for our rtablets
	 * 
	 * listen on a socket for requests from clients 
	 */
	public TabletServer(Configuration conf) throws IOException {	

        TabletServer.conf = conf;
        fs = FileSystem.get(conf);
        
        TimerTask gcDebugTask = new TimerTask(){
			@Override
			public void run() {
				logGCInfo();
			}
        };
        
        getJtimer().schedule(gcDebugTask, 0, 1000);
        
        shutdownHook = new Thread() {
            public void start() {
                log.debug("Shutdown hook");
                
                if(!startSignalShutdown()){
                	log.warn("OS Signal initiated shutdown will not run, thrift initiated shutdown in progress");
                	return;
                }
                
                if (!shutdownComplete) { 
                    if (hdfsIsClosed()) {
                        log.error("Sorry, HDFS is already closed, data not saved.");
                        return;
                    }
                    log.warn("Tablet Server " + getClientAddressString() + " shutting down now!");
                    shutdownStage1();
                    shutdownStage2();
                    UtilWaitThread.sleep(30);
                    shutdownStage3();
                    UtilWaitThread.sleep(30);
                    shutdownStage4();
                    log.info("Tablet Server " + getClientAddressString() + " shutdown complete.");
                    try {
                        FileSystem.closeAll();
                    } catch (IOException ex) {
                        log.error("Unable to close HDFS", ex);
                    }
                }
            }
        };
        
        Runtime.getRuntime().addShutdownHook(shutdownHook);
        hackTheHadoopFileSystemShutdownHook();

		this.resourceManager = new TabletServerResourceManager(conf, fs, this); 

		onlineTablets = Collections.synchronizedSortedMap(new TreeMap<KeyExtent, Tablet>());
		unopenedTablets = Collections.synchronizedSortedSet(new TreeSet<KeyExtent>());
		openingTablets = Collections.synchronizedSortedSet(new TreeSet<KeyExtent>());
		lastPingTime = System.currentTimeMillis();
		authenticator = new ZKAuthenticator();
		

		decayRate = 0.2;

		//serviceStopped =true;
		currentMaster = null;

		timer = new TabletTimer();

		// start major compactor
		majorCompactorThread = new Daemon(new LoggingRunnable(log, new MajorCompactor()));
		majorCompactorThread.setName("Split/MajC initiator");
		majorCompactorThread.start();

		//start stats server
		mt = new MonitorThread(this);
		monitorThread = new Daemon(new LoggingRunnable(log, mt), "Monitor Thread");
		monitorThread.start();

	}
    
    private boolean hdfsIsClosed() {
        try {
            fs.exists(new Path("/"));
            return false;
        } catch (Exception ex) {
        }
        return true;
    }

    /*
     * This method could be removed if o.a.h.fs.FileSystem.clientInitializer
     * wasn't private. Or if we didn't need to close files on a shutdown hook
     * of our own. But the two can't play nice together.
     */
    private boolean hackTheHadoopFileSystemShutdownHook()
    {
        try
        {
            Class<FileSystem> clz = org.apache.hadoop.fs.FileSystem.class;
            java.lang.reflect.Field f = clz.getDeclaredField("clientFinalizer");
            f.setAccessible(true); // it is private in o.a.h.dfs.DFSClient
            Object clientFinalizer = f.get(null); // it is a static
            if (clientFinalizer != null)
            {
                if (Runtime.getRuntime().removeShutdownHook((Thread)clientFinalizer))
                {
                    return true;
                }
                else
                {
                    log.error("Could not remove shutdown hook using " +
                        clientFinalizer.getClass().getName());
                }
            }
            else
            {
                log.error("DFSClient.clientFinalizer is null!");
            }
        }
        catch (Throwable t)
        {
            log.error("Could not remove DFSClient shutdown hook",t);
        }
        return false;
    } 
	 

	private void announceExistence() {
	    try {
	        String zPath = CBConstants.ZROOT_PATH+"/"+Cloudbase.getInstanceID()+CBConstants.ZTSERVERS_PATH+ "/" + getMasterAddressString();

	        ZooKeeper zooKeeper = ZooSession.getSession();


	        if(zooKeeper.exists(zPath, false) == null){
	            zooKeeper.create(zPath, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
	        }


	        tabletServerLock = new ZooLock(zPath);

	        LockWatcher lw = new LockWatcher(){

	            @Override
	            public void lostLock(LockLossReason reason) {
	                log.info("Lost tablet server lock (reason = "+reason+"), exiting.");
	                logGCInfo();
	                Runtime.getRuntime().halt(0);
	            }

	        };

	        for (int i = 0; i < 120 / 5; i++) {
	            if(zooKeeper.exists(zPath, false) == null){
	                zooKeeper.create(zPath, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
	            }

	            if(tabletServerLock.tryLock(lw, "tserver".getBytes())){
	            	log.debug("Obtained tablet server lock "+tabletServerLock.getLockPath());
	                return;
	            } else {
	                log.info("Waiting for tablet server lock");
	            }
	            UtilWaitThread.sleep(5000);
	        }
            log.info("Too many retries, exiting.");
            System.exit(0);
	    } catch (Exception e) {
            log.info("Could not obtain tablet server lock, exiting.",e);
            System.exit(0);
	    }	
	}

	static public String getMasterAddressString() {
	    return AddressUtil.toString(masterAddress);
	}


	@SuppressWarnings("unused")
	private synchronized void printOnlineTablets() {
		log.info("\n------ online tablets ---------\n");
		for(KeyExtent ke : onlineTablets.keySet()) {
			log.info(ke);
		}
		//System.out.println();
	}

	//	main loop listens for client requests
	public void run() {

	    // Prevent the master for asking us anything until we've been initialized
	    synchronized (onlineTablets) {
	        for (int i = 0; i < 100; i++) {
	            try {
	                masterAddress = startTabletMasterService();
	                clientAddress = startTabletClientService();
	                System.setProperty("cloudbase.core.tabletserver.port", "." + masterAddress.getPort());
	                PropertyConfigurator.configure(System.getenv("CLOUDBASE_HOME") + LOG_CONFIG);
	                break;
	            } catch (UnknownHostException e1) {
	                log.error("Unable to start tablet client service", e1);
	                UtilWaitThread.sleep(1000);
	            }
	        }
	        if (clientAddress == null) {
	            throw new RuntimeException("Failed to start the tablet client service");
	        }
	    }

	    announceExistence();
	    
	    //cache the master host so it is not continually looked up in HDFS
	    String masterHost = getMasterHost();
	    
        while(!serverStopRequested) {
			// send all of the pending messages
			try {
                MasterMessage mm = null;
                
				MasterTabletService.Client client = null;
				
				try {
					
					//wait until a message is ready to send, or a sever stop
					//was requested
					while(mm == null && !serverStopRequested){
						mm = masterMessages.poll(1000, TimeUnit.MILLISECONDS);
					}
		            
					//have a message to send to the master, so grab a connection
					client = masterConnection(masterHost);
					
					if(client == null){
						masterHost = getMasterHost();
						client = masterConnection(masterHost);
					}
					
					//if while loop does not execute at all and mm != null, then
					//finally block should place mm back on queue
	                while(!serverStopRequested &&
	                		mm != null &&
							client != null &&
							client.getOutputProtocol() != null &&
							client.getOutputProtocol().getTransport() != null &&
							client.getOutputProtocol().getTransport().isOpen())
					{
						if (false) log.debug("Sending message " + mm + " to master.");
						try {
							mm.send(systemCredentials(), getMasterAddressString(), client);
							mm = null;
						} catch (TException ex) {
							log.warn("Error sending message: queuing message again");
							masterMessages.putFirst(mm);
							throw ex;
						}

						//if any messages are immediately available grab em and send them
						mm = masterMessages.poll();
					}
	                
				} finally {
					
					if(mm != null){
						masterMessages.putFirst(mm);
					}
					
					if (client != null &&
							client.getOutputProtocol() != null &&
							client.getOutputProtocol().getTransport() != null){
						client.getOutputProtocol().getTransport().close();
					}
					
					if(client != null && 
							client.getInputProtocol() != null && 
							client.getInputProtocol().getTransport() != null){
						client.getInputProtocol().getTransport().close();
					}
					
					UtilWaitThread.sleep(1000);
				}
			}
			catch (InterruptedException e) {
				log.info("Interrupt Exception received, shutting down");
				serverStopRequested = true;

			}
			catch (Exception e) {
				// may have lost connection with master
				// loop back to the beginning and wait for a new one
				// this way we survive master failures

				masterHost = getMasterHost();
				
				log.error(getMasterAddressString() + ": TabletServer: Exception. Master down?", e);
			} 
		}

		// wait for shutdown
		// if the main thread exits before the master listener, the JVM will kill the 
		//		other threads and finalize objects.  We want the shutdown that is running 
		//		in the master listener thread to complete before this happens.
		// TODO: consider making other threads daemon threads so that objects don't get prematurely finalized
		synchronized(this)
		{
			while(shutdownComplete == false)
			{
				try {
					this.wait(1000);
				} catch (InterruptedException e) {
					log.error(e.toString());
				}
			}
		}
		log.debug("Stopping Thrift Servers");
		for (TServer s : servers) {
		    TServerUtils.stopTServer(s);
		}

		try {
			log.debug("Closing filesystem");
			fs.close();
		} catch (IOException e) {
			log.warn("Failed to close filesystem : "+e.getMessage(), e);
		}
		
		getJtimer().cancel();
		
		logGCInfo();
		
		log.info("TabletServer: stop requested. exiting ... ");
		
		try {
			tabletServerLock.unlock();
		} catch (Exception e) {
			log.warn("Failed to release tablet server lock", e);
		}
	}

	// TODO Handle case when shutdown is started while tablets are waiting to be opened
	public class ShutdownHandler extends Daemon
	{
		private int stage;
		public synchronized void setStage(int stage)
		{
			if(stage > this.stage)
				this.stage = stage;
		}

		ShutdownHandler(int stage)
		{
			super("ShutdownHandler");
			this.stage = stage;
		}

		public void run() {
			int completedStage = this.stage - 1;
			int stage = this.stage;
			int repeatCounter = 0;
			while(true)
			{
				stage = this.stage;
				if(stage <= completedStage)
				{
					if (repeatCounter==0)
						enqueueMasterMessage(new ShutdownResponseMessage(stage, this));
					repeatCounter++;
					if (repeatCounter>600)
						repeatCounter=0;
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
					}
					continue;
				}
				completedStage = stage;

				switch(stage) {
				case CBConstants.SHUTDOWN_STAGE_1: // close all non-metadata tablets
					shutdownStage1();
					enqueueMasterMessage(new ShutdownResponseMessage(stage, this));
					break;

				case CBConstants.SHUTDOWN_STAGE_2: // close all non-root tablets
					shutdownStage2();
					enqueueMasterMessage(new ShutdownResponseMessage(stage, this));
					break;

				case CBConstants.SHUTDOWN_STAGE_3: // close root tablet if necessary
					shutdownStage3();
					enqueueMasterMessage(new ShutdownResponseMessage(stage, this));
					while(masterMessagesPending())
					{
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
						}
					}
					// set this so server comm thread should die off
					// should be after message is sent so socket isn't lost prior to response
					serverStopRequested = true;
					break;

				case CBConstants.SHUTDOWN_STAGE_4:
					shutdownStage4();
					break;
				}
			}
		}

	}

	private ShutdownHandler shutdownHandler = null;
	private boolean signalShutdownInProgress = false;
	
	public synchronized boolean startSignalShutdown(){
		if(shutdownHandler == null){
			signalShutdownInProgress = true;
			return true;
		}
		
		return false;
	}
	
	// initiate a shutdown if one is not already in progress
	// otherwise, set the shutdown stage
	public synchronized void shutdownServer(int stage) {
		
		if(signalShutdownInProgress){
			log.warn("Ignoring thrift requested shutdown, signal initiated shutdown in progress");
			return;
		}
		
		if(shutdownHandler == null)
		{
			shutdownHandler = new ShutdownHandler(stage);
			shutdownHandler.start();
		}
		else
		{
			shutdownHandler.setStage(stage);
		}
	}

	private SortedMap<KeyExtent, Text> verifyTabletInformation(KeyExtent extent, Text location) throws CBSecurityException {
		for(int tries=0; tries < 3; tries++) {
			try {		
				log.debug("verifying extent " + extent);
				//System.out.flush();
				// ignore metadata tablets
				if(extent.getTableName().toString().equals("!METADATA")) {
					log.info("assuming information about the metadata table is correct");
					TreeMap<KeyExtent, Text> set = new TreeMap<KeyExtent, Text>();
					set.put(extent, location);
					return set;
				}

				List<Text> columnsToFetch = Arrays.asList(new Text[]{
						CBConstants.METADATA_TABLE_TABLET_DIRECTORY_COLUMN_NAME,
						CBConstants.METADATA_TABLE_TABLET_LOCATION_COLUMN_NAME,
						CBConstants.METADATA_TABLE_TABLET_PREV_ROW_COLUMN_NAME,
						CBConstants.METADATA_TABLE_TABLET_SPLIT_RATIO_COLUMN_NAME,
						CBConstants.METADATA_TABLE_TABLET_OLD_PREV_ROW_COLUMN_NAME});

				SortedMap<Text, SortedMap<Text, Value>> tabletEntries = 
					MetadataTable.getTabletEntries(extent, columnsToFetch, systemCredentials());

				if(tabletEntries.size() == 0){
					log.warn("Failed to find any metadata entries for "+extent);
					return null;
				}

				//ensure lst key in map is same as extent that was passed in
				if(!tabletEntries.lastKey().equals(extent.getMetadataEntry())){
					log.warn("Failed to find metadata entry for "+extent+" found "+tabletEntries.lastKey());
					return null;
				}

				Text lastKey = tabletEntries.lastKey();

				//check location to make sure we are supposed to be serving this tablet
				// TODO why do we need to do this?
				// this test is failing when it shouldn't be
				Value locIBW = tabletEntries.get(lastKey).get(CBConstants.METADATA_TABLE_TABLET_DIRECTORY_COLUMN_NAME);
				if(locIBW != null){
					Text loc = new Text(locIBW.get());
					if(loc.compareTo(location) != 0){
						log.warn("Location in metadata ("+loc+") differs from master ("+location+") for tablet "+extent);
						//return null;
					}
				}

				//look for incomplete splits
				int splitsFixed = 0;
				for (Entry<Text, SortedMap<Text, Value>> entry : tabletEntries.entrySet()) {

					if(extent.getPrevEndRow() != null){ 
						Text prevRowMetadataEntry = new Text(KeyExtent.getMetadataEntry(extent.getTableName(), extent.getPrevEndRow()));
						if(entry.getKey().compareTo(prevRowMetadataEntry) <= 0){
							continue;
						}
					}

					if(entry.getValue().containsKey(CBConstants.METADATA_TABLE_TABLET_OLD_PREV_ROW_COLUMN_NAME)){
						KeyExtent fixedke = MetadataTable.fixSplit(entry.getKey(), entry.getValue(), TabletServer.getClientAddressString(), systemCredentials());
						if(fixedke != null){
							if(fixedke.getPrevEndRow() == null || fixedke.getPrevEndRow().compareTo(extent.getPrevEndRow()) < 0){
								extent = new KeyExtent(extent);
								extent.setPrevEndRow(fixedke.getPrevEndRow());
							}
							splitsFixed++;
						}
					}
				}

				if(splitsFixed > 0){
					//reread and reverify metadata entries now that metadata entries were fixed
					return verifyTabletInformation(extent, location);
				}

				SortedMap<KeyExtent, Text> children = new TreeMap<KeyExtent, Text>();

				for (Entry<Text, SortedMap<Text, Value>> entry : tabletEntries.entrySet()) {
					if(extent.getPrevEndRow() != null){ 
						Text prevRowMetadataEntry = new Text(KeyExtent.getMetadataEntry(extent.getTableName(), extent.getPrevEndRow()));

						if(entry.getKey().compareTo(prevRowMetadataEntry) <= 0){
							continue;
						}
					}

					Value prevEndRowIBW = entry.getValue().get(CBConstants.METADATA_TABLE_TABLET_PREV_ROW_COLUMN_NAME);
					if(prevEndRowIBW == null){
						log.warn("Metadata entry does not have prev row ("+entry.getKey()+")");
						return null;
					}

					Value dirIBW = entry.getValue().get(CBConstants.METADATA_TABLE_TABLET_DIRECTORY_COLUMN_NAME);
					if(dirIBW == null){
						log.warn("Metadata entry does not have directory ("+entry.getKey()+")");
						return null;
					}

					Text dir = new Text(dirIBW.get());

					KeyExtent child = new KeyExtent(entry.getKey(), prevEndRowIBW);
					children.put(child, dir);
				}

				if(!BulkOperations.isContiguousRange(extent, new TreeSet<KeyExtent>(children.keySet()))){
					log.warn("For extent "+extent+" metadata entries "+children+" do not form a contiguous range.");
					return null;
				}

				return children;
			} catch (TableNotFoundException e) {
				log.error("error verifying metadata information. metadata table missing. ignoring.");
				log.error(e.toString());
				break;
			} catch (CBException e) {
				log.error("error verifying metadata information. retrying ...");
				log.error(e.toString());
				UtilWaitThread.sleep(1000);
			} catch (CBSecurityException e) {
				// if it's a security exception, retrying won't work either.
				log.error(e.toString());
				throw e;
			}
		}
		// default is to accept
		return null;
	}

	static String getClientAddressString() {
	    if (clientAddress == null)
	        return null;
		return AddressUtil.toString(clientAddress);
	}

	public static InetSocketAddress getMasterAddress() {
		return masterAddress;
	}



	/**
	 * we invoke the TabletServer on a drone using the 'hadoop'
	 * script. We could just set the relevent HDFS parameters in
	 * the JobConf object and probably be ok?
	 * 
	 * @param args
	 * @throws IOException 
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {				
		conf = new Configuration();

		Cloudbase.init(LOG_CONFIG);

		try {
			fs = FileSystem.get(conf);
		}
		catch (IOException e) {
			log.error(InetAddress.getLocalHost().getHostAddress() + ": couldn't get a reference to the filesystem. quitting.");
			throw e;
		}

		log.info("Tablet server starting on "+InetAddress.getLocalHost().getHostAddress());

		if(args.length > 0)
			conf.set("tabletserver.hostname", args[0]);

		Runnable ts = new LoggingRunnable(log, new TabletServer(conf));
		ts.run();
	}

	public void getStats(Map<KeyExtent,Tablet> statsSnapshot) {
		synchronized (onlineTablets) {
			statsSnapshot.putAll(onlineTablets);
		}					
	}

	// Update the master with our status
    private void pong() {
        // construct and send a pong message
        // write a moving average of queries per second
        TabletServerStatus info = new TabletServerStatus();
        long delta = System.currentTimeMillis() - lastPingTime;
        double dtSec = delta/1000.0;
        double decay = Math.pow(decayRate, dtSec);

        if(delta >= 1000)
        	lastPingTime += delta;

        // send tablet stats
        HashSet<String> tablesOfInterest = new HashSet<String>(CBConfiguration.getInstance().getStringCollection("cloudbase.stats.tableRates"));
        
        Map<KeyExtent, Tablet> onlineTabletsCopy;
        synchronized(this.onlineTablets) {
            onlineTabletsCopy =  new HashMap<KeyExtent, Tablet>(this.onlineTablets);
        }
        ArrayList<TabletRates> tablets = new ArrayList<TabletRates>();
        long totalRecords = 0;
        long tableRecords = 0;
        for(Entry<KeyExtent, Tablet> entry : onlineTabletsCopy.entrySet()) {
            Tablet tablet = entry.getValue();
            long recs = tablet.getNumEntries();
            totalRecords += recs;
            double tqr = tablet.queryRate(delta, decay);
            double tir = tablet.ingestRate(delta, decay);
            long recsInMemory = tablet.getNumEntriesInMemory();
            Compacting minor = new Compacting(tablet.minorCompactionRunning(), 
                                              tablet.minorCompactionQueued());
            Compacting major = new Compacting(tablet.majorCompactionRunning(),
                                              tablet.majorCompactionQueued());
            info.queryRate += tqr;
            info.ingestRate += tir;
            tablets.add(new TabletRates(entry.getKey(), tqr, tir, recs, recsInMemory, minor, major));
            if(tablesOfInterest.contains(entry.getKey().getTableName().toString()))
                tableRecords += recs;
        }
        info.tabletRates = tablets;
        info.totalRecords = totalRecords;
        info.osLoad = ManagementFactory.getOperatingSystemMXBean().getSystemLoadAverage();
        enqueueMasterMessage(new PongMessage(info));
    }

    private void shutdownStage1() {
        log.debug("Entering shutdown stage 1");

        majorCompactorDisabled = true;

        ArrayList<Tablet> closedTablets = new ArrayList<Tablet>();
        synchronized(onlineTablets) {
        	for(Tablet t : onlineTablets.values()) {
        		// skip metadata tablets
        		if(!t.getExtent().getTableName().toString().equals(CBConstants.METADATA_TABLE_NAME)) 
        			closedTablets.add(t);
        	}
        }

        for(Tablet t: closedTablets) {
        	try {
        		t.initiateClose(true); 
        	} catch (IOException e) {
        		log.error(e.toString());
        	}
        }

        synchronized(onlineTablets) {
        	for(Tablet t: closedTablets) {
        		onlineTablets.remove(t.getExtent());
        	}
        }

        // wait for the compaction thread pool to finished everything
        for(Tablet t : closedTablets) {
        	try {
        		t.completeClose();
        	} catch (IOException e) {
        		log.error(e.toString());
        	}
        }
    }

    private void shutdownStage2() {
        ArrayList<Tablet> closedTablets;
        log.debug("Entering shutdown stage 2");
        closedTablets = new ArrayList<Tablet>();
        synchronized(onlineTablets) {
        	for(Tablet t : onlineTablets.values()) {
        		// skip root tablet
        		if(!t.getExtent().equals(CBConstants.ROOT_TABLET_EXTENT)) 
        			closedTablets.add(t);
        	}
        }

        for(Tablet t: closedTablets) {
        	try {
        		t.close(true); 
        	} catch (IOException e) {
        		log.error(e.toString());
        	}
        }

        synchronized(onlineTablets) {
        	for(Tablet t: closedTablets) {
        		onlineTablets.remove(t.getExtent());
        	}
        }
    }

    private void shutdownStage3() {
        log.debug("Entering shutdown stage 3");

        synchronized(onlineTablets) {
        	for(Tablet t : onlineTablets.values()) {
        		if(t.getExtent().equals(CBConstants.ROOT_TABLET_EXTENT)) {
        			try {
        				log.debug("Closing root tablet");
        				t.close(true);
        			} catch (IOException e) {
        				log.error(e.toString());
        			}
        		}
        	}
        }

        //majorCompactorDisabled  = true; // done above

        // must call this after closing all tablets
        resourceManager.close();
        mt.stopServer();
    }

    private void shutdownStage4() {
        log.debug("Entering shutdown stage 4");
        serverStopRequested = true;

        log.debug("Closing client connection");

        // send a signal to the main thread that it is ok to exit now
        log.debug("Setting shutdownComplete");
        shutdownComplete  = true;
        synchronized(this)
        {
        	this.notifyAll();
        }
        // if not, could have a timeout here and kill them
        log.info("end of shutdown()");
    }

}

