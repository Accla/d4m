package cloudbase.core.client.mapreduce.bulk;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.MapFile;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.SequenceFile.Writer;

import cloudbase.core.CBConstants;
import cloudbase.core.client.BatchWriter;
import cloudbase.core.client.CBException;
import cloudbase.core.client.CBSecurityException;
import cloudbase.core.client.Connector;
import cloudbase.core.client.Instance;
import cloudbase.core.client.MasterInstance;
import cloudbase.core.client.MutationsRejectedException;
import cloudbase.core.client.Scanner;
import cloudbase.core.client.TableNotFoundException;
import cloudbase.core.client.impl.HdfsZooInstance;
import cloudbase.core.client.impl.MasterClient;
import cloudbase.core.client.impl.ThriftTansportPool;
import cloudbase.core.conf.CBConfiguration;
import cloudbase.core.data.Key;
import cloudbase.core.data.KeyExtent;
import cloudbase.core.data.Mutation;
import cloudbase.core.data.MyMapFile;
import cloudbase.core.data.Range;
import cloudbase.core.data.Value;
import cloudbase.core.master.thrift.MasterClientService;
import cloudbase.core.security.TablePermission;
import cloudbase.core.security.thrift.AuthInfo;
import cloudbase.core.security.thrift.ThriftSecurityException;
import cloudbase.core.tabletserver.thrift.TabletClientService;
import cloudbase.core.util.MapFileUtil;
import cloudbase.core.util.StopWatch;
import cloudbase.core.util.UtilWaitThread;
import cloudbase.core.util.MetadataTable.SSTableValue;

import com.facebook.thrift.protocol.TBinaryProtocol;
import com.facebook.thrift.protocol.TProtocol;
import com.facebook.thrift.transport.TTransport;


public class BulkOperations {
	
	public static void main(String[] args) throws IOException, CBException, CBSecurityException, TableNotFoundException
	{
		
		if (args.length == 0)
		{
			printUsage();
			return;
		}
		
		if (args[0].equals("import"))
		{
			int numMapThreads = 4;
			int numAssignThreads = 20;
			boolean safeMode = false;
			boolean disableGC = false;
			
			ArrayList<String> otherArgs = new ArrayList<String>();
			
			for (int i = 1; i < args.length; i++)
			{
				if(args[i].equals("-numThreads") || args[i].equals("-numMapThreads"))
				{
					if(args[i].equals("-numThreads"))
						System.err.println("WARN : Use of -numThreads is deprecated, use -numMapThreads and -numAssignThreads instead.");
					numMapThreads = Integer.parseInt(args[++i]);
				}
				else if(args[i].equals("-numAssignThreads"))
					numAssignThreads = Integer.parseInt(args[++i]);
				else if(args[i].equals("-safeMode"))
					safeMode = true;
				else if(args[i].equals("-disableGC"))
					disableGC = true;
				else if(args[i].equals("-batchAssignments"))
					System.err.println("WARN : Use of -batchAssignments is deprecated, this is the default mode of operations now.");
				else
					otherArgs.add(args[i]);
			}
			
			if (otherArgs.size() == 6)
			{
				System.out.println("INFO : Using "+numMapThreads+" threads to read map files information.");
				System.out.println("INFO : Using "+numAssignThreads+" threads to assign map files to tablet servers.");
				AuthInfo credentials = new AuthInfo();
				credentials.user = otherArgs.get(1);
				credentials.password = otherArgs.get(2).getBytes();
				bringMapFilesOnline(new MasterInstance(otherArgs.get(0)), credentials, otherArgs.get(3), new Path(otherArgs.get(4)), new Path(otherArgs.get(5)), numMapThreads, numAssignThreads, safeMode, disableGC);
			}
			else
				printUsage();
		}
		else if (args[0].equals("splits"))
		{
			Collection<Text> splits = null;
			
			if(args.length == 6)
			{
				AuthInfo credentials = new AuthInfo();
				credentials.user = args[3];
				credentials.password = args[4].getBytes();
				splits = getSplits(new MasterInstance(args[2]), credentials, args[5], Integer.parseInt(args[1]));
			}
			else if(args.length == 5)
			{
				AuthInfo credentials = new AuthInfo();
				credentials.user = args[2];
				credentials.password = args[3].getBytes();
				splits = getSplits(new MasterInstance(args[1]), credentials, args[2]);
			}
			else
				printUsage();
			
			for (Text split : splits)
				System.out.println(split);
		}
		else
			printUsage();
	}
	
	private static void printUsage()
	{
		System.out.println("BulkOperations import [-numMapThreads <num>] [-numAssignThreads <num>] [-safeMode] [-disableGC] <master> <username> <password> <table name> <dir> <failure dir>");
		System.out.println("               splits [<maxSplits>] <master> <username> <password> <table name>");
		System.exit(-1);
	}

	public static Collection<Text> getSplits(Instance instance, AuthInfo credentials, String tablename)
	throws CBException, CBSecurityException, TableNotFoundException
	{
		SortedSet<KeyExtent> tablets = new TreeSet<KeyExtent>();
		Map<KeyExtent, String> locations = new TreeMap<KeyExtent, String>();
		
		getMetadataEntries(instance, credentials, tablename, locations, tablets);
		
		ArrayList<Text> endRows = new ArrayList<Text>(tablets.size());
		
		for(KeyExtent ke : tablets)
			if(ke.getEndRow() != null)
				endRows.add(ke.getEndRow());
		
		return endRows;
	}
	
	public static Collection<Text> getSplits(Instance instance, AuthInfo credentials, String tablename, int maxSplits)
	throws CBException, CBSecurityException, TableNotFoundException
	{
		Collection<Text> endRows = getSplits(instance, credentials, tablename);
		
		if(endRows.size() <= maxSplits)
			return endRows;
		
		double r = (maxSplits + 1) / (double)(endRows.size());
		double pos = 0;;
		
		ArrayList<Text> subset = new ArrayList<Text>(maxSplits);
		
		int j = 0;
		for(int i = 0; i < endRows.size() && j < maxSplits; i++)
		{
			pos+=r;
			while(pos > 1)
			{
				subset.add(((ArrayList<Text>)endRows).get(i));
				j++;
				pos-=1;
			}
		}
			
		return subset;
		
	}
	
	private static class MapFileInfo
	{
		Key firstKey = new Key();
		Key lastKey = new Key();
	}
	
	private static Path moveMapFiles(Configuration conf, FileSystem fs, String tablename, Path dir, Path bulkDir, double errprec)
	throws IOException
	{
		FileStatus[] mapFiles = fs.listStatus(dir);

		int seq = 0;
		int errorCount = 0;
		
		//acquire first and last key in each map files
		try {
			for (FileStatus fileStatus : mapFiles)
			{
				if((double)errorCount/(double)mapFiles.length > errprec)
					throw new IOException(String.format("More than %6.2f%s of map files failed to move",errprec*100.0,"%"));
				
				if(!fileStatus.isDir())
				{
					System.out.println("INFO : "+fileStatus.getPath()+" is not a map file, ignoring");
					continue;
				}
				
				try {
					FileStatus dataStatus = fs.getFileStatus(new Path(fileStatus.getPath() , MyMapFile.DATA_FILE_NAME));
					if(dataStatus.isDir())
					{
						System.out.println("INFO : "+fileStatus.getPath()+" is not a map file, ignoring");
						continue;	
					}
				} catch(FileNotFoundException fnfe) {
					System.out.println("INFO : "+fileStatus.getPath()+" is not a map file, ignoring");
					continue;
				}
				
				String newName = "map_" + String.format("%05d", 0) + "_" + String.format("%05d", seq++);
				Path newPath = new Path(bulkDir, newName);
				try {
					fs.rename(fileStatus.getPath(), newPath);
					System.out.println("INFO : moved "+fileStatus.getPath()+" to "+newPath);
				} catch(IOException E1) {
					System.err.println("ERROR Could not move: "+fileStatus.getPath().toString()+" "+E1.getMessage());
					errorCount+=1;
				}
			}
		}
		catch(IOException DataProcError){
			throw DataProcError;
		}
		
		return bulkDir;
	}

	private static Map<Path, KeyExtent> getMapFileInfo(final Configuration conf, final FileSystem fs, final String tablename, Path bulkDir, final Path failureDir, int numThreads)
	throws IOException
	{
		Map<Path, KeyExtent> mapFilesInfo = new TreeMap<Path, KeyExtent>();
		final Map<Path, KeyExtent> synchronizedMapFilesInfo = Collections.synchronizedMap(mapFilesInfo);

		FileStatus[] files = fs.globStatus(new Path(bulkDir.toString()+"/map*"));

		ExecutorService tp = Executors.newFixedThreadPool(numThreads);

		for(final FileStatus file : files)
		{
			Runnable r = new Runnable(){
				public void run()
				{
					boolean successful;
					int failures = 0;
					
					do {
						successful = true;
						try {
							MapFileInfo mfi = new MapFileInfo();
							MyMapFile.Reader reader = MapFileUtil.openMapFile(fs, file.getPath().toString(), conf); //  MyMapFile.Reader(fs, file.getPath().toString(), conf);
							Value firstValue = new Value();

							if(reader.next(mfi.firstKey, firstValue))
							{
								reader.finalKey(mfi.lastKey);
								KeyExtent ke = new KeyExtent(new Text(tablename), mfi.lastKey.getRow(), mfi.firstKey.getRow());
								synchronizedMapFilesInfo.put(file.getPath(), ke);
							}
							else
								System.err.println("WARN : "+file.getPath()+" is an empty map file");

							reader.close();
							
						} catch(IOException ioe) {
							successful = false;
							failures++;
							System.err.println("WARN : Failed to read map file "+file.getPath()+" ["+ioe.getMessage()+"] failures = "+failures);
							if(failures < 3)
								try {
									Thread.sleep(500);
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
						} catch(Throwable t) {
							successful = false;
							failures = 3;
							System.err.println("WARN : Failed to read map file "+file.getPath()+" ["+t.getMessage()+"]");
						}
					} while(!successful && failures < 3);
					
					if(!successful)
					{
						Path dest = new Path(failureDir, file.getPath().getName());
						System.err.println("ERROR : Failed to read map file "+file.getPath()+", moving map file to "+dest);
						try {
							fs.rename(file.getPath(), dest);
						} catch (IOException e) {
							System.err.println("ERROR : Failed to move map file from "+file.getPath()+" to "+dest+" ["+e.getMessage()+"] it could be garbage collected");
						}
					}
				}

			};
			
			tp.submit(r);
		}

		tp.shutdown();
		
		while(!tp.isTerminated())
			try {
				tp.awaitTermination(10, TimeUnit.MINUTES);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}


		return mapFilesInfo;
	}
	
	@Deprecated
    public static void getMetadataEntries(AuthInfo credentials, String tablename, Map<KeyExtent, String> locations, SortedSet<KeyExtent> tablets)
    throws CBException, CBSecurityException, TableNotFoundException
    {
        getMetadataEntries (new HdfsZooInstance(), credentials, tablename, locations, tablets);
    }
    
    public static void getMetadataEntries(Instance instance, AuthInfo credentials, String tablename, Map<KeyExtent, String> locations, SortedSet<KeyExtent> tablets)
    throws CBException, CBSecurityException, TableNotFoundException
    {
		Scanner scanner = new Connector(instance, credentials.user, credentials.password).createScanner(CBConstants.METADATA_TABLE_NAME, CBConstants.NO_AUTHS);
		scanner.fetchColumn(CBConstants.METADATA_TABLE_TABLET_COLUMN_FAMILY, CBConstants.METADATA_TABLE_TABLET_PREV_ROW_COLUMN_NAME);
		scanner.fetchColumn(CBConstants.METADATA_TABLE_TABLET_COLUMN_FAMILY, CBConstants.METADATA_TABLE_TABLET_LOCATION_COLUMN_NAME);

		//position at first entry in metadata table for given table
		KeyExtent ke = new KeyExtent(new Text(tablename), new Text(), null);
		Key startKey = new Key(ke.getMetadataEntry());
		ke = new KeyExtent(new Text(tablename), null, null);
		Key endKey = new Key(ke.getMetadataEntry()).followingKey(1);
		scanner.setRange(new Range(startKey, endKey));

		Text colq = new Text();

		KeyExtent currentKeyExtent = null;
		String location = null;
		Text row = null;
		//acquire this tables METADATA table entries
		boolean haveExtent = false;
		boolean haveLocation = false;
		for (Entry<Key, Value> entry : scanner)
		{
			if(row != null)
			{
				if (!row.equals(entry.getKey().getRow()))
				{
					currentKeyExtent = null;
					haveExtent = false;
					haveLocation = false;
					row = entry.getKey().getRow();
				}
			}
			else
				row = entry.getKey().getRow();

			colq = entry.getKey().getColumnQualifier(colq);
			
			//stop scanning metadata table when another table is reached
			if (!(new KeyExtent(entry.getKey().getRow(), (Text)null)).getTableName().toString().equals(tablename))
				break;
			
			if (colq.equals(CBConstants.METADATA_TABLE_TABLET_PREV_ROW_COLUMN_NAME))
			{
				currentKeyExtent = new KeyExtent(entry.getKey().getRow(), entry.getValue());
				tablets.add(currentKeyExtent);
				haveExtent = true;
			}
			else if (colq.equals(CBConstants.METADATA_TABLE_TABLET_LOCATION_COLUMN_NAME))
			{
				location = entry.getValue().toString();
				haveLocation = true;
			}
			
			if (haveExtent && haveLocation)
			{
				locations.put(currentKeyExtent, location);
				haveExtent = false;
				haveLocation = false;
				currentKeyExtent = null;
			}
		}

		validateMetadataEntries(tablename, tablets);
	}

	public static void validateMetadataEntries(String tablename, SortedSet<KeyExtent> tablets)
	throws CBException
	{
		//sanity check of metadata table entries
		//make sure tablets has no holes, and that it starts and ends w/ null
		if (tablets.size() == 0)
			throw new CBException("No entries found in metadata table for table "+tablename);
		
		if (tablets.first().getPrevEndRow() != null)
			throw new CBException("Problem with metadata table, first entry for table "+tablename+"- " + tablets.first() + " - has non null prev end row");
		
		if (tablets.last().getEndRow() != null)
			throw new CBException("Problem with metadata table, last entry for table "+tablename+"- " + tablets.first() + " - has non null end row");
		
		Iterator<KeyExtent> tabIter = tablets.iterator();
		Text lastEndRow = tabIter.next().getEndRow();
		while(tabIter.hasNext())
		{
			KeyExtent tabke = tabIter.next();
			
			if(tabke.getPrevEndRow() == null)
				throw new CBException("Problem with metadata table, it has null prev end row in middle of table "+tabke);
			
			if(!tabke.getPrevEndRow().equals(lastEndRow))
				throw new CBException("Problem with metadata table, it has a hole "+tabke.getPrevEndRow()+" != "+lastEndRow);
			
			lastEndRow = tabke.getEndRow();
		}
		
		//end METADATA table sanity check
	}
	
	public static void bringMapFilesOnline(Instance instance, AuthInfo credentials, String tablename, Path dir, Path failureDir, int numThreads, int numAssignThreads, boolean safeMode, boolean disableGC)
	throws IOException, CBException, CBSecurityException, TableNotFoundException
	{
		(new BulkOperations())._bringMapFilesOnline(instance, credentials, tablename, dir, failureDir, numThreads, numAssignThreads, safeMode, disableGC);		
	}

	public static void bringMapFilesOnline(Instance instance, AuthInfo credentials, String tablename, Path dir, Path failureDir)
	throws IOException, CBException, CBSecurityException, TableNotFoundException
	{
		bringMapFilesOnline(instance, credentials, tablename, dir, failureDir, 4, 20);
	}
	
	public static void bringMapFilesOnline(Instance instance, AuthInfo credentials, String tablename, Path dir, Path failureDir, int numThreads, int numAssignThreads)
	throws IOException, CBException, CBSecurityException, TableNotFoundException
	{
		bringMapFilesOnline(instance, credentials, tablename, dir, failureDir, numThreads, numAssignThreads, false, false);
	}
	
	public static void bringMapFilesOnline(Instance instance, AuthInfo credentials, String tablename, Path dir, Path failureDir, int numThreads, int numAssignThreads, boolean safeMode)
	throws IOException, CBException, CBSecurityException, TableNotFoundException
	{
		bringMapFilesOnline(instance, credentials, tablename, dir, failureDir, numThreads, numAssignThreads, safeMode, false);
	}
	
	private enum Timers
	{
		MOVE_MAP_FILES,
		EXAMINE_MAP_FILES,
		QUERY_METADATA,
		IMPORT_MAP_FILES,
		SLEEP,
		TOTAL
	}
	
	StopWatch<Timers> timer;
	
	private class AssignmentStats
	{
		private Map<KeyExtent, Integer> counts;
		private int numUniqueMapFiles;
		private Map<Path, List<KeyExtent>> completeFailures;
		private Set<Path> failedFailures;
		
		AssignmentStats(SortedSet<KeyExtent> tablets, Map<Path, KeyExtent> mapFilesInfo)
		{
			counts = new HashMap<KeyExtent, Integer>();
			for (KeyExtent keyExtent : tablets)
				counts.put(keyExtent, 0);
			
			numUniqueMapFiles = mapFilesInfo.size();
		}
		
		void attemptingAssignments(Map<Path, List<KeyExtent>> assignments)
		{
			for (Entry<Path, List<KeyExtent>> entry : assignments.entrySet())
				for(KeyExtent ke : entry.getValue())
					counts.put(ke, counts.get(ke)+1);
		}
		
		void assignmentsFailed(Map<Path, List<KeyExtent>> assignmentFailures)
		{
			for (Entry<Path, List<KeyExtent>> entry : assignmentFailures.entrySet())
				for(KeyExtent ke : entry.getValue())
					counts.put(ke, counts.get(ke)-1);
		}
		
		public void assignmentsAbandoned(Map<Path, List<KeyExtent>> completeFailures)
		{
			this.completeFailures = completeFailures;
		}
		
		void tabletSplit(KeyExtent parent, Collection<KeyExtent> children)
		{
			int count = counts.get(parent);
			
			counts.remove(parent);
			
			for (KeyExtent keyExtent : children)
				counts.put(keyExtent, count);
		}
		
		public void unrecoveredMapFiles(Set<Path> failedFailures)
		{
			this.failedFailures = failedFailures;
		}
		
		void printReport()
		{
			int totalAssignments = 0;
			int tabletsImportedTo = 0;
			
			int min = Integer.MAX_VALUE, max = Integer.MIN_VALUE;
			
			for (Entry<KeyExtent, Integer> entry : counts.entrySet())
			{
				totalAssignments+=entry.getValue();
				if(entry.getValue() > 0)
					tabletsImportedTo++;
				
				if(entry.getValue() < min)
					min = entry.getValue();
				
				if(entry.getValue() > max)
					max = entry.getValue();
			}
			
			double stddev = 0;
			
			for (Entry<KeyExtent, Integer> entry : counts.entrySet())
				stddev += Math.pow(entry.getValue() - totalAssignments/(double)counts.size(), 2);
			
			stddev = stddev / counts.size();
			stddev = Math.sqrt(stddev);
			
			Set<KeyExtent> failedTablets = new HashSet<KeyExtent>();
			for(List<KeyExtent> ft : completeFailures.values())
				failedTablets.addAll(ft);
			
			System.out.println();
			System.out.println("INFO : BULK IMPORT ASSIGNMENT STATISTICS");
			System.out.printf("INFO : # of map files            : %,10d \n", numUniqueMapFiles);
			System.out.printf("INFO : # map files with failures : %,10d %6.2f%s\n", completeFailures.size(), 100.0 * completeFailures.size()/(double)numUniqueMapFiles, "%");
			System.out.printf("INFO : # failed failed map files : %,10d %s\n", failedFailures.size(), failedFailures.size() > 0 ? " <-- THIS IS BAD" : "");
			System.out.printf("INFO : # of tablets              : %,10d \n", counts.size());
			System.out.printf("INFO : # tablets imported to     : %,10d %6.2f%s\n", tabletsImportedTo, 100.0 * tabletsImportedTo/(double)counts.size(),"%");
			System.out.printf("INFO : # tablets with failures   : %,10d %6.2f%s\n", failedTablets.size(), 100.0 * failedTablets.size()/(double)counts.size(), "%");
			System.out.printf("INFO : min map files per tablet  : %,10d \n", min);
			System.out.printf("INFO : max map files per tablet  : %,10d \n", max);
			System.out.printf("INFO : avg map files per tablet  : %,10.2f (std dev = %.2f)\n", totalAssignments/(double)counts.size(), stddev);
		}	
	}
	
	private void _bringMapFilesOnline(Instance instance, AuthInfo credentials, String tablename, Path dir, Path failureDir, int numThreads, int numAssignThreads, boolean safeMode, boolean disableGC)
	throws IOException, CBException, CBSecurityException, TableNotFoundException
	{
		MasterClientService.Client client = null;
		try {
			client = MasterClient.master_connect(instance);
			if (!client.hasTablePermission(credentials, credentials.user, tablename, TablePermission.WRITE.getId()))
			{
				System.out.println("Your username/password is incorrect.");
				return;
			}
		} catch (ThriftSecurityException e) {
			System.out.println("You do not have permission to write to this table");
			return;
		} catch (Throwable e) {
			System.out.println("Cannot cannot master");
			return;
		}  finally {
			MasterClient.close(client);
		}
		timer = new StopWatch<Timers>(Timers.class);
		
		timer.start(Timers.TOTAL);
		
		Configuration conf = new Configuration();
		FileSystem fs = FileSystem.get(conf);

		Map<Path, KeyExtent> mapFilesInfo = null;
		Map<KeyExtent, String> locations = null;
		SortedSet<KeyExtent> tablets = null;
		AssignmentStats assignmentStats = null;
		
		Map<Path, List<KeyExtent>> completeFailures = new TreeMap<Path, List<KeyExtent>>();
		
		if(fs.exists(failureDir))
		{
			System.err.println("ERROR "+failureDir+" already exist");
			System.exit(-1);
		}
		
		fs.mkdirs(failureDir);
		
		Path bulkDir = createNewBulkDir(fs, tablename);;
		
		Path procFile = new Path(bulkDir.toString()+"/processing_proc_"+System.currentTimeMillis());
		FSDataOutputStream fsOut = fs.create(procFile);
		
		fsOut.write(String.valueOf(System.currentTimeMillis()).getBytes());
		fsOut.close();

		try {
			timer.start(Timers.MOVE_MAP_FILES);
			moveMapFiles(conf,fs,tablename,dir, bulkDir, 0.15);
			timer.stop(Timers.MOVE_MAP_FILES);
		} catch (IOException ioe) {
			// TODO Auto-generated catch block
			System.err.println("ERROR Could not move files : "+ioe.getMessage());
			System.exit(-1);
		}
		System.out.println("INFO Succeeded in Moving files.");
		
		do {
			
			try {
				if(mapFilesInfo == null){
					timer.start(Timers.EXAMINE_MAP_FILES);
					mapFilesInfo = getMapFileInfo(conf, fs, tablename, bulkDir, failureDir, numThreads);
					timer.stop(Timers.EXAMINE_MAP_FILES);
				}
				
				locations = new TreeMap<KeyExtent, String>();
				tablets = new TreeSet<KeyExtent>();
		
				timer.start(Timers.QUERY_METADATA);
				getMetadataEntries(instance, credentials, tablename, locations, tablets);
				timer.stop(Timers.QUERY_METADATA);
			
				assignmentStats = new AssignmentStats(tablets, mapFilesInfo);
				
			} catch(IOException ioe) {
				
				timer.stopIfActive(Timers.EXAMINE_MAP_FILES);
				timer.stopIfActive(Timers.QUERY_METADATA);
				
				System.err.println("WARN : "+ioe.getMessage()+" ... retrying ...");
				ioe.printStackTrace();
				UtilWaitThread.sleep(3000);
				
				locations = null;
				tablets = null;
			}
			
		} while(mapFilesInfo == null || locations == null || tablets == null);
		
		Set<Entry<Path, KeyExtent>> mapFileIter = mapFilesInfo.entrySet();
		
		Map<Path, List<KeyExtent>> assignments = new TreeMap<Path, List<KeyExtent>>();
		
		for (Entry<Path, KeyExtent> mapFile : mapFileIter)
		{
			ArrayList<KeyExtent> tabletsToAssignMapFileTo = findOverlappingTablets(tablename, tablets, mapFile.getValue().getPrevEndRow(), mapFile.getValue().getEndRow());
			
			if (tabletsToAssignMapFileTo.size() == 0)
				completeFailures.put(mapFile.getKey(), tabletsToAssignMapFileTo);
			else
				assignments.put(mapFile.getKey(), tabletsToAssignMapFileTo);
		}
		
		assignmentStats.attemptingAssignments(assignments);
		Map<Path, List<KeyExtent>> assignmentFailures = assignMapFiles(conf, credentials, fs, tablename, bulkDir, assignments, locations, mapFilesInfo, numAssignThreads, numThreads, safeMode);
		assignmentStats.assignmentsFailed(assignmentFailures);
		
		Map<Path, Integer> failureCount = new TreeMap<Path, Integer>();
		
		for(Entry<Path, List<KeyExtent>> entry : assignmentFailures.entrySet())
			failureCount.put(entry.getKey(), 1);
		
		while(assignmentFailures.size() > 0)
		{
			//assumption about assignment failures is that it caused by a split happening
			//or a missing location
			//
			//for splits we need to find children key extents that cover the same 
			//key range and are contiguous (no holes, no overlap)
			
			timer.start(Timers.SLEEP);
			UtilWaitThread.sleep(4000);
			timer.stop(Timers.SLEEP);
			
			//reacquire metadata table entries
			locations = null;
			tablets = null;
			
			System.out.println("INFO : Trying to assign "+assignmentFailures.size()+" map files that previously failed on some key extents");
			
			do {
				
				try {
			
					locations = new TreeMap<KeyExtent, String>();
					tablets = new TreeSet<KeyExtent>();
			
					timer.start(Timers.QUERY_METADATA);
					getMetadataEntries(instance, credentials, tablename, locations, tablets);
					timer.stop(Timers.QUERY_METADATA);
				
				} catch(Throwable ioe) {
					
					timer.stopIfActive(Timers.QUERY_METADATA);
					
					System.err.println("WARN : "+ioe.getMessage()+" ... retrying ...");
					UtilWaitThread.sleep(3000);
					
					locations = null;
					tablets = null;
				}
				
			} while(locations == null || tablets == null);
			
			assignments.clear();
			
			//for failed key extents, try to find children key extents to assign to
			for (Entry<Path, List<KeyExtent>> entry : assignmentFailures.entrySet())
			{
				Iterator<KeyExtent> keListIter = entry.getValue().iterator();
			
				ArrayList<KeyExtent> tabletsToAssignMapFileTo = new ArrayList<KeyExtent>();
				
				while(keListIter.hasNext())
				{
					KeyExtent ke = keListIter.next();
					
					SortedSet<KeyExtent> children = KeyExtent.findChildren(ke, tablets);
					
					boolean contiguous = isContiguousRange(ke, children);
					
					if (contiguous)
					{
						if (children.size() == 1)
						{
							tabletsToAssignMapFileTo.add(ke);
							keListIter.remove();
						}
						else
						{
							assignmentStats.tabletSplit(ke, children);
							
							KeyExtent mapFileRange = mapFilesInfo.get(entry.getKey());
							tabletsToAssignMapFileTo.addAll(findOverlappingTablets(tablename, children, mapFileRange.getPrevEndRow(), mapFileRange.getEndRow()));
							keListIter.remove();
						}
					}
					else
						System.err.println("WARN : will retry tablet "+ke+" later it does not have contiguous children "+children);
				}
				
				if(tabletsToAssignMapFileTo.size() > 0)
					assignments.put(entry.getKey(), tabletsToAssignMapFileTo);
			}
			
			assignmentStats.attemptingAssignments(assignments);
			Map<Path, List<KeyExtent>> assignmentFailures2 = assignMapFiles(conf, credentials, fs, tablename, bulkDir, assignments, locations, mapFilesInfo, numAssignThreads, numThreads, safeMode);
			assignmentStats.assignmentsFailed(assignmentFailures2);
			
			//merge assignmentFailures2 into assignmentFailures
			for(Entry<Path, List<KeyExtent>> entry : assignmentFailures2.entrySet())
			{
				assignmentFailures.get(entry.getKey()).addAll(entry.getValue());
				
				Integer fc = failureCount.get(entry.getKey());
				if (fc == null)
					fc = 0;
				
				failureCount.put(entry.getKey(), fc + 1);
			}
			
			//remove map files that have no more key extents to assign
			Iterator<Entry<Path, List<KeyExtent>>> afIter = assignmentFailures.entrySet().iterator();
			while(afIter.hasNext())
			{
				Entry<Path, List<KeyExtent>> entry = afIter.next();
				if(entry.getValue().size() == 0)
					afIter.remove();
			}
			
			Set<Entry<Path, Integer>> failureIter = failureCount.entrySet();
			for (Entry<Path, Integer> entry : failureIter)
			{
				if (entry.getValue() > 3 && assignmentFailures.get(entry.getKey()) != null)
				{
					System.err.println("ERROR : Map file "+entry.getKey()+" failed more than three times, giving up.");
					completeFailures.put(entry.getKey(), assignmentFailures.get(entry.getKey()));
					assignmentFailures.remove(entry.getKey());
				}
			}
			
		}
		
		assignmentStats.assignmentsAbandoned(completeFailures);
		Set<Path> failedFailures = processFailures(conf, fs, failureDir, completeFailures);
		assignmentStats.unrecoveredMapFiles(failedFailures);
		
		if (disableGC)
		{
			Path procFile2 = new Path(bulkDir.toString()+"/processing_proc_disableGC");
			System.out.println("INFO : Creating file "+procFile2);
			FSDataOutputStream fsOut2 = fs.create(procFile2);
			
			fsOut2.write(String.valueOf(System.currentTimeMillis()).getBytes());
			fsOut2.close();
		}
		
		fs.delete(procFile, false);
		
		timer.stop(Timers.TOTAL);
		printReport();
		assignmentStats.printReport();
		
	}

	private void printReport()
	{
		long totalTime = 0;
		for (Timers t : Timers.values())
		{
			if(t == Timers.TOTAL)
				continue;
			
			totalTime += timer.get(t);
		}
		
		System.out.println();
		System.out.println("INFO : BULK IMPORT TIMING STATISTICS");
		System.out.printf("INFO : Move map files       : %,10.2f secs %6.2f%s\n", timer.getSecs(Timers.MOVE_MAP_FILES), 100.0 * timer.get(Timers.MOVE_MAP_FILES)/timer.get(Timers.TOTAL), "%");
		System.out.printf("INFO : Examine map files    : %,10.2f secs %6.2f%s\n", timer.getSecs(Timers.EXAMINE_MAP_FILES), 100.0 * timer.get(Timers.EXAMINE_MAP_FILES)/timer.get(Timers.TOTAL), "%");
		System.out.printf("INFO : Query !METADATA      : %,10.2f secs %6.2f%s\n", timer.getSecs(Timers.QUERY_METADATA), 100.0 * timer.get(Timers.QUERY_METADATA)/timer.get(Timers.TOTAL), "%");
		System.out.printf("INFO : Import Map Files     : %,10.2f secs %6.2f%s\n", timer.getSecs(Timers.IMPORT_MAP_FILES), 100.0 * timer.get(Timers.IMPORT_MAP_FILES)/timer.get(Timers.TOTAL), "%");
		System.out.printf("INFO : Sleep                : %,10.2f secs %6.2f%s\n", timer.getSecs(Timers.SLEEP), 100.0 * timer.get(Timers.SLEEP)/timer.get(Timers.TOTAL), "%");
		System.out.printf("INFO : Misc                 : %,10.2f secs %6.2f%s\n", (timer.get(Timers.TOTAL) - totalTime)/1000.0, 100.0 * (timer.get(Timers.TOTAL) - totalTime)/timer.get(Timers.TOTAL), "%");
		System.out.printf("INFO : Total                : %,10.2f secs \n", timer.getSecs(Timers.TOTAL));
		
		
		System.out.println();
	}

	private Set<Path> processFailures(Configuration conf, FileSystem fs, Path failureDir, Map<Path, List<KeyExtent>> completeFailures)
	{
		//TODO if map file was not assigned to any tablets, just move it
		
		Set<Entry<Path, List<KeyExtent>>> es = completeFailures.entrySet();
		
		if(completeFailures.size() == 0)
			return Collections.emptySet();
		
		System.out.println("INFO : The following map files failed completely, saving this info to : "+new Path(failureDir, "failures.seq"));
		
		for (Entry<Path, List<KeyExtent>> entry : es)
		{
			List<KeyExtent> extents = entry.getValue();
			
			for (KeyExtent keyExtent : extents)
				System.out.println("\t"+entry.getKey()+" -> "+keyExtent);
		}
		
		try {
		
			Writer outSeq = SequenceFile.createWriter(fs, conf, new Path(failureDir, "failures.seq"), Text.class, KeyExtent.class);
		
			for (Entry<Path, List<KeyExtent>> entry : es)
			{
				List<KeyExtent> extents = entry.getValue();
			
				for (KeyExtent keyExtent : extents)
					outSeq.append(new Text(entry.getKey().toString()), keyExtent);
			}
		
			outSeq.close();
		} catch(IOException ioe) {
			System.err.println("ERROR : Failed to create "+new Path(failureDir, "failures.seq")+" : "+ioe.getMessage());
		}
		
		//TODO make copying multithreaded
		
		Set<Path> failedCopies = new HashSet<Path>();
		
		for (Entry<Path, List<KeyExtent>> entry : es)
		{
			Path dest = new Path(failureDir, entry.getKey().getName());
		
			System.out.println("INFO : Copying "+entry.getKey()+" to "+dest);
		
			try {
				fs.mkdirs(dest);
				copy(fs, new Path(entry.getKey(), MapFile.INDEX_FILE_NAME), new Path(dest, MapFile.INDEX_FILE_NAME));
			} catch(IOException ioe) {
				System.err.println("ERROR : Failed to copy "+new Path(entry.getKey(), MapFile.INDEX_FILE_NAME)+" : "+ioe.getMessage());
				failedCopies.add(entry.getKey());
			}
			
			try {
				copy(fs, new Path(entry.getKey(), MapFile.DATA_FILE_NAME), new Path(dest, MapFile.DATA_FILE_NAME));
			} catch(IOException ioe) {
				System.err.println("ERROR : Failed to copy "+new Path(entry.getKey(), MapFile.DATA_FILE_NAME)+" : "+ioe.getMessage());
				failedCopies.add(entry.getKey());
			}
		}
		
		return failedCopies;
	}

	private void copy(FileSystem fs, Path src, Path dst)
	throws IOException
	{
		FSDataInputStream in = fs.open(src);
		FSDataOutputStream out = fs.create(dst);
		
		byte buf[] = new byte[1<<16];
		int numRead;
		
		while((numRead = in.read(buf)) > 0)
			out.write(buf, 0, numRead);
		
		in.close();
		out.close();
	}

	public static boolean isContiguousRange(KeyExtent ke, SortedSet<KeyExtent> children)
	{
		if (children.size() == 0)
			return false;
		
		if (children.size() == 1)
			return children.first().equals(ke);
		
		Text per = children.first().getPrevEndRow();
		Text er = children.last().getEndRow();
		
		boolean perEqual = (per == ke.getPrevEndRow() ||
							per != null && ke.getPrevEndRow() != null && 
							ke.getPrevEndRow().compareTo(per) == 0);
		
		boolean erEqual = (er == ke.getEndRow() ||
						   er != null && ke.getEndRow() != null && 
						   ke.getEndRow().compareTo(er) == 0);
		
		if(!perEqual || !erEqual)
			return false;
		
		Iterator<KeyExtent> iter = children.iterator();
		
		Text lastEndRow = iter.next().getEndRow();
		
		while(iter.hasNext())
		{
			KeyExtent cke = iter.next();
			
			per = cke.getPrevEndRow();
			
			//something in the middle should not be null
			
			if (per == null || lastEndRow == null)
				return false;
			
			if (per.compareTo(lastEndRow) != 0)
				return false;
			
			lastEndRow = cke.getEndRow();
		}
		
		return true;
	}

	private static ArrayList<KeyExtent> findOverlappingTablets(String tablename, SortedSet<KeyExtent> tablets, Text prevEndRow, Text endRow)
	{
		SortedSet<KeyExtent> tailMap = tablets.tailSet(new KeyExtent(new Text(tablename), prevEndRow, null));
		Iterator<KeyExtent> esIter = tailMap.iterator();
		
		Text mapFileLastRow = endRow;
		
		ArrayList<KeyExtent> tabletsToAssignMapFileTo = new ArrayList<KeyExtent>();
		
		//find all tablets a map file should be assigned to
		while(esIter.hasNext())
		{
			KeyExtent ke = esIter.next();
			
			Text tabletPrevEndRow = ke.getPrevEndRow();
			
			if(tabletPrevEndRow != null && tabletPrevEndRow.compareTo(mapFileLastRow) >= 0)
				break;
			
			tabletsToAssignMapFileTo.add(ke);
		}
		
		return tabletsToAssignMapFileTo;
	}

	private Path createNewBulkDir(FileSystem fs, String tablename)
	throws IOException
	{
		FileStatus[] files  = fs.listStatus(new Path(CBConstants.TABLES_DIR+"/"+tablename));
		
		HashSet<String> dirNames = new HashSet<String>();
		
		for (FileStatus fileStatus : files)
			dirNames.add(fileStatus.getPath().getName());
		
		int bulkCount = 0;
		
		while(dirNames.contains(String.format("bulk_%05d", bulkCount)))
			bulkCount++;
		
		Path newBulkDir = new Path(CBConstants.TABLES_DIR+"/"+tablename+"/"+String.format("bulk_%05d", bulkCount)); 
		
		fs.mkdirs(newBulkDir);
		
		return newBulkDir;
	}
	
	private static class AssignmentInfo
	{
		public AssignmentInfo(KeyExtent keyExtent, Long estSize)
		{
			this.ke = keyExtent;
			this.estSize = estSize;
		}
		
		KeyExtent ke;
		long estSize;
	}
	
	private Map<Path, List<AssignmentInfo>> estimateSizes(final Configuration conf, final FileSystem fs, Path bulkDir, Map<Path, List<KeyExtent>> assignments, Map<Path, KeyExtent> mapFilesInfo, int numThreads )
	{
		FileStatus[] files = null;
		
		long t1 = System.currentTimeMillis();
		
		try {
			files = fs.globStatus(new Path(bulkDir.toString()+"/map*/"+MapFile.DATA_FILE_NAME));
		} catch (IOException e) {
			System.err.println("ERROR Failed to list map files in "+bulkDir+" "+e.getMessage());
			e.printStackTrace();
			//TODO retry
			System.exit(-1);
		}
		
		final Map<Path, Long> mapFileSizes = new TreeMap<Path, Long>();
		for (FileStatus fileStatus : files)
			mapFileSizes.put(fileStatus.getPath().getParent(), fileStatus.getLen());
		
		final Map<Path, List<AssignmentInfo>> ais = Collections.synchronizedMap(new TreeMap<Path, List<AssignmentInfo>>());
		
		ExecutorService threadPool = Executors.newFixedThreadPool(numThreads);
		
		for (final Entry<Path, List<KeyExtent>> entry : assignments.entrySet())
		{
			if (entry.getValue().size() == 1)
			{
				KeyExtent mapFileRange = mapFilesInfo.get(entry.getKey());
				KeyExtent tabletExtent = entry.getValue().get(0);
				
				//if the tablet completely contains the map file, there is no need to estimate its
				//size
				
				if(tabletExtent.contains(mapFileRange.getPrevEndRow()) && tabletExtent.contains(mapFileRange.getEndRow()))
				{
					//System.out.println("DEBUG : "+tabletExtent+" completely contains "+entry.getKey()+" "+mapFileSizes.get(entry.getKey()));
					ais.put(entry.getKey(), Collections.singletonList(new AssignmentInfo(tabletExtent, mapFileSizes.get(entry.getKey()))));
					continue;
				}
			}

			Runnable estimationTask = new Runnable(){
				public void run()
				{					
					Map<KeyExtent, Long> estimatedSizes = null;

					try {
						estimatedSizes = MapFileUtil.estimateSizes(entry.getKey(), mapFileSizes.get(entry.getKey()), entry.getValue(), conf, fs);
					} catch (IOException e) {
						System.err.println("WARN : Failed to estimate map file sizes "+e.getMessage());
					}
					
					if(estimatedSizes == null)
					{
						//estimation failed, do a simple estimation
						estimatedSizes = new TreeMap<KeyExtent, Long>();
						long estSize = (long)(mapFileSizes.get(entry.getKey()) / (double)entry.getValue().size());
						for(KeyExtent ke : entry.getValue())
							estimatedSizes.put(ke, estSize);
					}
					
					List<AssignmentInfo> assignmentInfoList = new ArrayList<AssignmentInfo>(estimatedSizes.size());

					for (Entry<KeyExtent,Long> entry2 : estimatedSizes.entrySet())
						assignmentInfoList.add(new AssignmentInfo(entry2.getKey(), entry2.getValue()));
					
					ais.put(entry.getKey(), assignmentInfoList);
				}
			};
			
			threadPool.submit(estimationTask);
		}
		
		threadPool.shutdown();
		
		while(!threadPool.isTerminated())
		{
			try {
				threadPool.awaitTermination(60, TimeUnit.SECONDS);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		long t2 = System.currentTimeMillis();
		
		System.out.printf("INFO Estimated map files sizes in %6.2f secs\n", (t2 - t1)/1000.0 );
		
		return ais;
	}
	
	private Map<Path, List<KeyExtent>> assignMapFiles(Configuration conf, AuthInfo credentials,
			FileSystem fs, String tablename, Path bulkDir, Map<Path, List<KeyExtent>> assignments,
			Map<KeyExtent, String> locations, Map<Path, KeyExtent> mapFilesInfo, int numThreads, 
			int numMapThreads, boolean safeMode)
	{
		timer.start(Timers.EXAMINE_MAP_FILES);
		Map<Path, List<AssignmentInfo>> assignInfo = estimateSizes(conf, fs, bulkDir, assignments, mapFilesInfo, numMapThreads);
		timer.stop(Timers.EXAMINE_MAP_FILES);
		
		FileStatus[] files = null;
		
		try {
			files = fs.globStatus(new Path(bulkDir.toString()+"/map*/"+MapFile.DATA_FILE_NAME));
		} catch (IOException e) {
			System.err.println("ERROR Failed to list map files in "+bulkDir+" "+e.getMessage());
			e.printStackTrace();
			//TODO retry
			System.exit(-1);
		}
		
		Map<Path, Long> mapFileSizes = new TreeMap<Path, Long>();
		for (FileStatus fileStatus : files)
			mapFileSizes.put(fileStatus.getPath().getParent(), fileStatus.getLen());
		
		Map<Path, List<KeyExtent>> ret;
		
		timer.start(Timers.IMPORT_MAP_FILES);
		if (!safeMode)
			//online assignments
			ret = assignMapFiles(credentials, tablename, assignInfo, locations, numThreads);
		else
		{
			//offline assignments
			try {
				writeMapFileAssignmentsToMetadataTable(credentials, tablename, assignInfo);
			} catch (Exception e) {
				System.err.println("ERROR Failed to make updates to metadata table "+e.getMessage());
				e.printStackTrace();
				//TODO retry
				System.exit(-1);
			} 
			ret = Collections.emptyMap();
		}
		timer.stop(Timers.IMPORT_MAP_FILES);
		
		return ret;
	}
	
	private void writeMapFileAssignmentsToMetadataTable(AuthInfo credentials, String tablename, Map<Path, List<AssignmentInfo>> assignments)
	throws CBException, CBSecurityException, TableNotFoundException
	{
	    Connector connector = new Connector(new HdfsZooInstance(), credentials.user, credentials.password);
		BatchWriter batchWriter = connector.createBatchWriter(CBConstants.METADATA_TABLE_NAME, 10000000, 120, 10);
				
		for (Entry<Path, List<AssignmentInfo>> entry : assignments.entrySet())
		{
			Path mapFile = entry.getKey();
			List<AssignmentInfo> tabletsToAssignMapFileTo = entry.getValue();
			
			for (AssignmentInfo ai : tabletsToAssignMapFileTo)
			{
				Mutation m = new Mutation(ai.ke.getMetadataEntry()); 
				String relativePath = "/"+mapFile.getParent().getName()+"/"+mapFile.getName();
				m.put(CBConstants.METADATA_TABLE_TABLET_SSTABLES_COLUMN_FAMILY, new Text(relativePath), new Value(new SSTableValue(ai.estSize, 0l).encode()));
				
				batchWriter.addMutation(m);
				
				System.out.println("INFO : Adding metadata table entry for "+relativePath+"("+ai.estSize+") to tablet "+ai.ke);
			}
		}
		
		try {
			batchWriter.close();
		} catch (MutationsRejectedException e) {
			throw new CBException("Auth failures or constraint violations", e);
		}
	}

	private static class AssignmentTask implements Runnable
	{
		Map<Path, List<KeyExtent>> assignmentFailures;
		String tablename;
		String location;
		AuthInfo credentials;
		private Map<KeyExtent, List<PathSize>> assignmentsPerTablet;
		
		public AssignmentTask(AuthInfo credentials, Map<Path, List<KeyExtent>> assignmentFailures,
							   String tablename, 
							   String location,
							   Map<KeyExtent, List<PathSize>> assignmentsPerTablet)
		{
			this.assignmentFailures = assignmentFailures;
			this.tablename = tablename;
			this.location = location;
			this.assignmentsPerTablet = assignmentsPerTablet;
			this.credentials = credentials;
		}

		private void handleFailures(Collection<KeyExtent> failures, String message)
		{
			for (KeyExtent ke : failures)
			{
				List<PathSize> mapFiles = assignmentsPerTablet.get(ke);
				synchronized (assignmentFailures)
				{
					for (PathSize pathSize : mapFiles)
					{
						List<KeyExtent> existingFailures = assignmentFailures.get(pathSize.path);
						if(existingFailures == null)
						{
							existingFailures = new ArrayList<KeyExtent>();
							assignmentFailures.put(pathSize.path, existingFailures);
						}

						existingFailures.add(ke);
					}
				}
				
				System.err.println("WARN : Could not assign  "+mapFiles.size()+" map files to tablet "+ke+" because : "+message+".  Will retry ...");
			}
		}
		
		public void run()
		{
			HashSet<Path> uniqMapFiles = new HashSet<Path>();
			for(List<PathSize> mapFiles : assignmentsPerTablet.values())
				for (PathSize ps : mapFiles)
					uniqMapFiles.add(ps.path);
			
			System.out.println("INFO : Assigning "+uniqMapFiles.size()+" map files to "+assignmentsPerTablet.size()+" tablets at "+location);

			try {
				List<KeyExtent> failures = assignMapFiles(credentials, tablename, location, assignmentsPerTablet);
				handleFailures(failures, "Not Serving Tablet");
			} catch (CBException e) {
				handleFailures(assignmentsPerTablet.keySet(), e.getMessage());
			} catch (CBSecurityException e) {
				handleFailures(assignmentsPerTablet.keySet(), e.getMessage());
			}
		}
		
	}
	
	private static class PathSize
	{
		public PathSize(Path mapFile, long estSize)
		{
			this.path = mapFile;
			this.estSize = estSize;
		}
		Path path;
		long estSize;
		
		public String toString()
		{
			return path+" "+estSize;
		}
	}
	
	private Map<Path, List<KeyExtent>> assignMapFiles(AuthInfo credentials,
			String tablename, Map<Path, List<AssignmentInfo>> assignments,
			Map<KeyExtent, String> locations, int numThreads)
	{
		
		//group assignments by tablet 
		Map<KeyExtent, List<PathSize>> assignmentsPerTablet = new TreeMap<KeyExtent, List<PathSize>>();
		for (Entry<Path, List<AssignmentInfo>> entry : assignments.entrySet())
		{
			Path mapFile = entry.getKey();
			List<AssignmentInfo> tabletsToAssignMapFileTo = entry.getValue();
			
			for (AssignmentInfo ai : tabletsToAssignMapFileTo)
			{
				List<PathSize> mapFiles = assignmentsPerTablet.get(ai.ke);
				if(mapFiles == null)
				{
					mapFiles = new ArrayList<PathSize>();
					assignmentsPerTablet.put(ai.ke, mapFiles);
				}
				
				mapFiles.add(new PathSize(mapFile, ai.estSize));
			}
		}
		
		//group assignments by tabletserver

		Map<Path, List<KeyExtent>> assignmentFailures = Collections.synchronizedMap(new TreeMap<Path, List<KeyExtent>>());
		
		TreeMap<String, Map<KeyExtent, List<PathSize>>> assignmentsPerTabletServer = new TreeMap<String, Map<KeyExtent,List<PathSize>>>();
		
		for (Entry<KeyExtent, List<PathSize>> entry : assignmentsPerTablet.entrySet())
		{
			KeyExtent ke = entry.getKey();
			String location = locations.get(ke);
			
			if (location == null)
			{
				for (PathSize pathSize : entry.getValue())
				{
					synchronized (assignmentFailures)
					{
						List<KeyExtent> failures = assignmentFailures.get(pathSize.path);
						if (failures == null)
						{
							failures = new ArrayList<KeyExtent>();
							assignmentFailures.put(pathSize.path, failures);
						}
						
						failures.add(ke);
					}	
				}
				
				System.err.println("WARN : Could not assign "+entry.getValue().size()+" map files to tablet "+ke+" because it had no location, will retry ...");
				
				continue;
			}
			
			Map<KeyExtent, List<PathSize>> apt = assignmentsPerTabletServer.get(location);
			if(apt == null)
			{
				apt = new TreeMap<KeyExtent, List<PathSize>>();
				assignmentsPerTabletServer.put(location, apt);
			}
			
			apt.put(entry.getKey(), entry.getValue());
		}
		
		ExecutorService threadPool = Executors.newFixedThreadPool(numThreads);
		
		for (Entry<String, Map<KeyExtent, List<PathSize>>> entry : assignmentsPerTabletServer.entrySet())
		{
			String location = entry.getKey();
			threadPool.submit(new AssignmentTask(credentials, assignmentFailures, tablename, location, entry.getValue()));
		}
		
		threadPool.shutdown();
		
		while (!threadPool.isTerminated())
		{
			try {
				threadPool.awaitTermination(60, TimeUnit.SECONDS);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		}
		
		return assignmentFailures;
	}
	
	private static List<KeyExtent> assignMapFiles(AuthInfo credentials, String tablename, String location, Map<KeyExtent, List<PathSize>> assignmentsPerTablet)
	throws CBException, CBSecurityException
	{
		TTransport transport = null;

		try {
			transport = ThriftTansportPool.getInstance().getTransport(location, CBConfiguration.getInstance().getInt("cloudbase.tabletserver.clientPort", CBConstants.TABLET_CLIENT_PORT_DEFAULT));
			TProtocol protocol = new TBinaryProtocol(transport);
			TabletClientService.Client client = new TabletClientService.Client(protocol);
			
			HashMap<KeyExtent, Map<String, Long>> files = new HashMap<KeyExtent, Map<String, Long>>();
			for (Entry<KeyExtent, List<PathSize>> entry : assignmentsPerTablet.entrySet())
			{
				HashMap<String, Long> tabletFiles = new HashMap<String, Long>();
				files.put(entry.getKey(), tabletFiles);
				
				for(PathSize pathSize : entry.getValue())
					tabletFiles.put(pathSize.path.toUri().getPath().toString(), pathSize.estSize);
			}
			
			List<KeyExtent> failures = client.bulkImport(credentials, files);

			return failures;
		} catch (ThriftSecurityException e) {
			throw new CBSecurityException(e.user, e.baduserpass, e);
		} catch (Throwable t) {
			t.printStackTrace();
			throw new CBException(t);
		} finally {
			ThriftTansportPool.getInstance().returnTransport(transport);
		}
	}
}
