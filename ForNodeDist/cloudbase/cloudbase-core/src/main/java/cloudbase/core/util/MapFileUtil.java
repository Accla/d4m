package cloudbase.core.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.io.WritableComparable;
import org.apache.log4j.Logger;

import cloudbase.core.CBConstants;
import cloudbase.core.conf.CBConfiguration;
import cloudbase.core.data.Key;
import cloudbase.core.data.KeyExtent;
import cloudbase.core.data.MyMapFile;
import cloudbase.core.data.MySequenceFile;
import cloudbase.core.data.Value;
import cloudbase.core.data.MySequenceFile.Reader;
import cloudbase.core.tabletserver.iterators.MultiIterator;
import cloudbase.core.tabletserver.iterators.SequenceFileIterator;
import cloudbase.core.tabletserver.iterators.SortedKeyValueIterator;

/**
 * 
 *
 */

public class MapFileUtil {
	private static Logger log = Logger.getLogger(MapFileUtil.class.getName());
	
	//public static final int INDEX_INTERVAL = 128;
	
	public static boolean attemptToFixMapFile(Configuration conf, FileSystem fs, String dirName){
		boolean fixed = true;
		try {
			log.info("Attempting to fix mapfile "+dirName);
			Path indexFile = new Path(dirName+"/"+MyMapFile.INDEX_FILE_NAME);
			if(fs.exists(indexFile) && fs.getFileStatus(indexFile).getLen() == 0){
				log.info("Deleting 0 length index file "+indexFile);
				fs.delete(indexFile, false);
			}
			
			MyMapFile.fix(fs, new Path(dirName), Key.class, Value.class, false, conf);
		} catch (Exception e) {
			log.error("Failed to fix mapfile "+dirName, e);
			fixed = false;
		}
		
		return fixed;
	}
	
	public static MyMapFile.Reader openMapFile(FileSystem fs, String dirName, Configuration conf) throws IOException{
		try {
			MyMapFile.Reader mfr = new MyMapFile.Reader(fs, dirName, conf);
			return mfr;
		}catch (IOException ioe){
			if(attemptToFixMapFile(conf, fs, dirName)){
				log.info("Fixed mapfile "+dirName);
				MyMapFile.Reader mfr = new MyMapFile.Reader(fs, dirName, conf);
				return mfr;
			}else{
				throw ioe;
			}
		}
	}

	public static MySequenceFile.Reader openIndex(Configuration conf, FileSystem fs, Path mapFile) throws IOException{
		Path indexPath = new Path(mapFile, MyMapFile.INDEX_FILE_NAME);
		try {
			MySequenceFile.Reader index = new MySequenceFile.Reader(fs, indexPath, conf);
			return index;
		}catch (IOException ioe){
			if(attemptToFixMapFile(conf, fs, mapFile.toString())){
				log.info("Fixed mapfile "+mapFile);
				MySequenceFile.Reader index = new MySequenceFile.Reader(fs, indexPath, conf);
				return index;
			}else{
				throw ioe;
			}
		}
		
	}
	
	public static MyMapFile.Writer openMapFileWriter(Text tableName, Configuration conf, FileSystem fs, String dirname) throws IOException {
		MyMapFile.Writer mfw;
		int hbs = conf.getInt("io.seqfile.compress.blocksize", CBConstants.DEFAULT_MAPFILE_COMPRESSION_BLOCK_SIZE);
		int hrep = conf.getInt("dfs.replication", CBConstants.DEFAULT_MAPFILE_REPLICATION);
		
		//dfs.replication
		
		Configuration newConf = null;
		
		int cbbs = CBConfiguration.getInstance(tableName.toString()).getInt("cloudbase.tablet.io.seqfile.compress.blocksize", CBConstants.DEFAULT_MAPFILE_COMPRESSION_BLOCK_SIZE);
		int cbrep = CBConfiguration.getInstance(tableName.toString()).getInt("cloudbase.tablet.dfs.replication", -1);
		
		if(hbs != cbbs){
			log.debug("Overriding io.seqfile.compress.blocksize setting "+String.format("%,d with %,d", hbs,cbbs)+" for "+tableName+" map file");
			newConf = new Configuration(conf);
			newConf.setInt("io.seqfile.compress.blocksize", cbbs);
		}
		
		if(fs.exists(new Path(dirname))){
			log.error("Map file "+dirname+" already exists", new Exception());
		}
		
		if(newConf != null) {
			conf = newConf;
		}
		
		mfw = new MyMapFile.Writer(conf, fs, dirname, Key.class, Value.class, MySequenceFile.CompressionType.BLOCK);
		
		if(cbrep > 0 && cbrep != hrep){
			//tried to set dfs.replication property on conf obj, however this was ignored, so have to manually set the prop
			log.debug("Overriding dfs.replication setting "+String.format("%,d with %,d", hrep,cbrep)+" for "+tableName+" map file");
			fs.setReplication(new Path(dirname+"/"+MyMapFile.DATA_FILE_NAME), (short)cbrep);
			fs.setReplication(new Path(dirname+"/"+MyMapFile.INDEX_FILE_NAME), (short)cbrep);
		}
		
		return mfw;
	}
	
	
	public static long estimateEntriesInMapFile(FileSystem fs, Configuration conf, Path mapFile) throws IOException{
		
		MySequenceFile.Reader indexReader = null;
		MyMapFile.Reader dataReader = null;
		

		WritableComparable<?> indexKey;
		//LongWritable offset;
		try {
			
			indexReader = openIndex(conf, fs, mapFile);
			
			indexKey = (WritableComparable<?>) indexReader.getKeyClass().newInstance();
			
			long numIndexEntries=0;
			
			while (indexReader.next(indexKey)) {
				//System.out.println("indexKey = "+indexKey);
				numIndexEntries++;
			}
			
			int numEntriesAfterLastIndexEntry = 0;
			
			//seek to last key and start counting
			if(numIndexEntries > 0){
				//Path dataPath = new Path(mapFile+"/"+MyMapFile.DATA_FILE_NAME);
				dataReader = new MyMapFile.Reader(fs, mapFile.toString(), conf);

				dataReader.seek(indexKey);
				//seek will put pointer at indexKey, when next is called it
				//will return next key... so increment counter or indexKey
				//is not counted
				numEntriesAfterLastIndexEntry++; 
				
				WritableComparable<WritableComparable> dataKey = (WritableComparable<WritableComparable>) dataReader.getKeyClass().newInstance();
				Writable dataValue = (Writable) dataReader.getValueClass().newInstance();
				
				while(dataReader.next(dataKey, dataValue)){
					//System.out.println("dataKey = "+dataKey);
					if(dataKey.compareTo(indexKey) >= 0){
						numEntriesAfterLastIndexEntry++;
					}
					
				}
				
				
			}else{
				return 0;
			}
			
			
			//TODO write function that examines map file to see how many entries are skipped in index
			
			return (numIndexEntries - 1) * MyMapFile.getIndexInterval() + numEntriesAfterLastIndexEntry;
			
			
		} catch (InstantiationException e) {
			log.error(e.toString());
		} catch (IllegalAccessException e) {
			log.error(e.toString());
		}finally{
			
			//make damn sure both files are closed!
			try{
				if(indexReader != null){
					indexReader.close();
				}
			}catch(IOException ioe){
				log.error(ioe.toString());
			}
			
			try{
				if(dataReader != null){
					dataReader.close();
				}
			}catch(IOException ioe){
				log.error(ioe.toString());
			}
		}
		
		return 0;
	}

	public static long estimateEntriesInMapFile(FileSystem fs, Configuration conf, Path mapFile, KeyExtent extent) throws IOException{
		//TODO avoid using a MyMapFile.Reader object because it reads index into memory
		MySequenceFile.Reader indexReader = null;
		MyMapFile.Reader dataReader = null;
		

		WritableComparable<?> indexKey;
		//LongWritable offset;
		try {
			
			indexReader = openIndex(conf, fs, mapFile);
			
			indexKey = (WritableComparable<?>) indexReader.getKeyClass().newInstance();
			
			long numIndexEntries=0;
			
			Key lastIndexedKey = null;
			Key firstIndexKey = null;
			Key glbKey = null;

			// TODO: skip to just before the first row of the extent if possible
			while (indexReader.next(indexKey)) {
				// before the extent
				if(extent.getPrevEndRow() != null && extent.getPrevEndRow().compareTo(((Key)indexKey).getRow()) >= 0)
				{
					glbKey = new Key((Key)indexKey);
				}
				// in the extent
				else if(extent.contains(((Key)indexKey).getRow()))
				{
					lastIndexedKey = new Key((Key)indexKey);
					if(firstIndexKey == null)
					{
						firstIndexKey = new Key((Key)indexKey);
					}
					numIndexEntries++;
				}
				// after the extent
				else
				{
					break;
				}
			}
			
			int numExtraEntries = 0;
			
			dataReader = new MyMapFile.Reader(fs,mapFile.toString(), conf);
			Key dataKey = new Key();
			Writable dataValue = (Writable) dataReader.getValueClass().newInstance();
			// read all of the entries before the indexed range
			if(glbKey != null)
			{
				dataReader.seek(glbKey);
				while(dataReader.next(dataKey,dataValue))
				{
					// stop if we get to the first index key
					if(firstIndexKey != null && firstIndexKey.equals(dataKey))
						break;
					// stop if we get past the extent
					if(extent.getEndRow() != null && extent.getEndRow().compareTo(((Key)dataKey).getRow()) < 0)
						break;
					if(extent.contains(((Key)dataKey).getRow()))
						numExtraEntries++;
				}
			}
			
			// read all of the entries after the indexed range
			if(lastIndexedKey != null)
			{
				dataReader.seek(lastIndexedKey);
				while(dataReader.next(dataKey, dataValue))
				{
					if(extent.contains(dataKey.getRow()))
					{
						numExtraEntries++;
					}
					else
					{
						break;
					}
				}
			}
			
			//TODO write function that examines map file to see how many entries are skipped in index
			
			if(numIndexEntries > 0)
				return (numIndexEntries - 1) * MyMapFile.getIndexInterval() + 1 + numExtraEntries;
			else
				return numExtraEntries;
		} catch (InstantiationException e) {
			log.error(e.toString());
		} catch (IllegalAccessException e) {
			log.error(e.toString());
		}finally{
			//make damn sure both files are closed!
			try{
				if(indexReader != null){
					indexReader.close();
				}
			}catch(IOException ioe){
				log.error(ioe.toString());
			}
			try{
				if(dataReader != null){
					dataReader.close();
				}
			}catch(IOException ioe){
				log.error(ioe.toString());
			}
		}
		
		return 0;
	}

	public static WritableComparable<Object> findLastKey(Collection<String> mapFiles) throws IOException {
		try{
			Configuration conf = new Configuration();
			FileSystem fs = FileSystem.get(conf);

			WritableComparable<Object> lastKey = null;

			for (String path : mapFiles) {
				MyMapFile.Reader reader = openMapFile(fs, path, conf);

				WritableComparable<Object> key = (WritableComparable<Object>) reader.getKeyClass().newInstance();
				Writable value = (Writable) reader.getValueClass().newInstance();
				
				if(!reader.next(key, value)){
					//file is empty, so there is no last key
					continue;
				}
				
				reader.finalKey(key);
				
				if(lastKey == null || key.compareTo(lastKey) > 0){
					lastKey = key;
				}
				
				reader.close();
			}

			return lastKey;

		}catch(InstantiationException e){
			throw new IOException(e.getMessage());
		} catch (IllegalAccessException e) {
			throw new IOException(e.getMessage());
		}
	}
	
	private static String createTmpDir(Configuration conf, FileSystem fs) throws IOException {
		String cbDir = CBConfiguration.getInstance().get("cloudbase.directory", "/cloudbase");
		
		String tmpDir = null;
		while(tmpDir == null){
			tmpDir = cbDir+"/tmp/idxReduce_"+String.format("%09d", (int)(Math.random() * Integer.MAX_VALUE));
		
			boolean caughtFNE = false;
			
			try{
				fs.getFileStatus(new Path(tmpDir));
			}catch(FileNotFoundException fne){
				caughtFNE = true;
			}
			
			if(!caughtFNE){
				tmpDir = null;
				continue;
			}
			
			fs.mkdirs(new Path(tmpDir));
			
			//try to resever the tmp dir
			//TODO determine if createNewFile is atomic on name node
			if(!fs.createNewFile(new Path(tmpDir+"/__reserve"))){
				tmpDir = null;
			}
			
		}
		
		return tmpDir;
	}
	
    private static Collection<String> reduceFiles(Configuration conf, FileSystem fs, Text prevEndRow, Text endRow, Collection<String> mapFiles, int maxFiles, String tmpDir, int pass) throws IOException, InstantiationException, IllegalAccessException {
		
		ArrayList<String> paths = new ArrayList<String>(mapFiles);
		
		if(paths.size() <= maxFiles){
			return paths;
		}
		
		
		String newDir = String.format("%s/pass_%04d", tmpDir, pass); 
		fs.mkdirs(new Path(newDir));
		
		int start = 0;
		
		ArrayList<String> outFiles = new ArrayList<String>();
		
		int count = 0;
		
		while(start < paths.size()){
			int end = Math.min(maxFiles+start, paths.size());
			List<String> inFiles = paths.subList(start, end);
			
			//System.out.println("inFiles : "+inFiles);
			
			start = end;
		
			String newMapFile = String.format("%s/mf_%04d", newDir, count++);
			fs.mkdirs(new Path(newMapFile));
			
			Path outFile = new Path(String.format("%s/index", newMapFile));
			outFiles.add(newMapFile);
			
			//long t1 = System.currentTimeMillis();
			
			MySequenceFile.Writer writer = MySequenceFile.createWriter(fs, conf, outFile, Key.class, LongWritable.class, MySequenceFile.CompressionType.BLOCK);			
			
			MySequenceFile.Reader readers[] = new MySequenceFile.Reader[inFiles.size()];
			SortedKeyValueIterator iters[] = new SortedKeyValueIterator[inFiles.size()];
			
			try{
				//TODO use iterator for following code and in findMidPoint
				for (int i = 0 ; i < readers.length; i++) {
					Reader reader = openIndex(conf, fs, new Path(inFiles.get(i)));
					readers[i] = reader;
					iters[i] = new SequenceFileIterator(reader, false);
				}
				
				MultiIterator mmfi = new MultiIterator(iters, (Text)null, true);
				
				while(mmfi.hasTop()){
					Key key = mmfi.getTopKey();
					
					boolean gtPrevEndRow = prevEndRow == null || key.compareRow(prevEndRow) > 0;
					boolean lteEndRow = endRow == null || key.compareRow(endRow) <= 0;
					
					if(gtPrevEndRow && lteEndRow){
						writer.append(key, new LongWritable(0));
					}
					
					if(!lteEndRow){
						break;
					}
					
					mmfi.next();
				}
			}finally{
				for (int i = 0 ; i < readers.length; i++) {
					if(readers[i] != null)
						readers[i].close();
				}
				
				writer.close();
			}
			
			//long t2 = System.currentTimeMillis();
			//log.debug(String.format("reduced indexes, out : %s  num indexes in : %d   time : %6.2f secs\n", outFile, inFiles.size(), (t2 - t1)/1000.0));
		}
		
		return reduceFiles(conf, fs, prevEndRow, endRow, outFiles, maxFiles, tmpDir, pass+1);
	}

	
	public static SortedMap<Double, Key> findMidPoint(Text prevEndRow, Text endRow, Collection<String> mapFiles, double minSplit) throws IOException {
		return findMidPoint(prevEndRow, endRow, mapFiles, minSplit, true);
	}
	
	/**
	 * 
	 * @param endRow 
	 * @param prevEndRow 
	 * @param mapFiles - list MapFiles to find the mid point key
	 * @throws IOException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * 
	 * ISSUES :  This method used the index files to find the mid point.  If the map files have different index intervals this method will not return an accurate mid point.  Also, it woud be tricky to use this method in conjuction with an in memory map because the indexing interval is unknown.
	 */
	
    private static SortedMap<Double, Key> findMidPoint(Text prevEndRow, Text endRow, Collection<String> mapFiles, double minSplit, boolean useIndex) throws IOException {
		
		Collection<String> origMapFiles = mapFiles;
		
		String tmpDir = null;
		
		Configuration conf = new Configuration();
		FileSystem fs = FileSystem.get(conf);
		
		int maxToOpen = CBConfiguration.getInstance().getInt("cloudbase.tablet.split.findMidPoint.maxOpen", 30);
		
		if(mapFiles.size() > maxToOpen){
			if(!useIndex){
				throw new IOException("Cannot find mid point using data files, too many "+mapFiles.size());
			}else{
				
				boolean deleteTmp = true;
				tmpDir = createTmpDir(conf, fs);
				deleteTmp = false;
				
				log.debug("Too many indexes ("+mapFiles.size()+") to open at once for "+endRow+" "+prevEndRow+", reducing in tmpDir = "+tmpDir);
			
				try {
					long t1 = System.currentTimeMillis();
					mapFiles = reduceFiles(conf, fs, prevEndRow, endRow, mapFiles, maxToOpen, tmpDir, 0);
					long t2 = System.currentTimeMillis();
					
					log.debug("Finished reducing indexes for "+endRow+" "+prevEndRow+" in "+String.format("%6.2f secs", (t2 -t1) / 1000.0));
					
				} catch (InstantiationException e) {
					throw new IOException(e.getMessage());
				} catch (IllegalAccessException e) {
					throw new IOException(e.getMessage());
				}finally{
					if(deleteTmp && tmpDir != null){
						String tmpPrefix = CBConfiguration.getInstance().get("cloudbase.directory", "/cloudbase")+"/tmp";
						if(tmpDir.startsWith(tmpPrefix)){
							fs.delete(new Path(tmpDir), true);
						}else{
							log.error("Did not delete tmp dir because it wasn't a tmp dir "+tmpDir);
						}
					}
				}
			}
		}
		
		MySequenceFile.Reader readers[] = new MySequenceFile.Reader[mapFiles.size()];
		
		try{

			//System.out.println(prevEndRow + ", " + endRow);
			if(prevEndRow == null)
				prevEndRow = new Text();
			
			long t1 = System.currentTimeMillis();
		
			long numKeys = 0;

			int index = 0;
			//count the total number of index entries
			for (String path : mapFiles) {
				MySequenceFile.Reader reader;
				if(useIndex){
					reader = openIndex(conf, fs, new Path(path));
				}else{
					//Path dataPath = new Path(new Path(path), MyMapFile.DATA_FILE_NAME);
					reader = simpleSeek(prevEndRow, new Path(path), conf, fs);//new MySequenceFile.Reader(fs, dataPath, conf); 
				}

				WritableComparable<?> key = (WritableComparable<?>) reader.getKeyClass().newInstance();

				while (reader.next(key)) {
					//System.out.println(path+" "+key);
					if(endRow != null && ((Key)key).compareRow(endRow) > 0)
						break;
					if(prevEndRow == null || ((Key)key).compareRow(prevEndRow) > 0){
						//System.out.println(path+" "+key);
						numKeys++;
					}
				}

				//System.out.println(path + " " + numKeys);

				reader.close();

				if(useIndex){
					readers[index++] = openIndex(conf, fs, new Path(path));
				}else{
					//Path dataPath = new Path(new Path(path), MyMapFile.DATA_FILE_NAME);
					readers[index++] = simpleSeek(prevEndRow, new Path(path), conf, fs); //new MySequenceFile.Reader(fs, dataPath, conf); 
				}
			}

			//System.out.println("num keys : " + numKeys);
			if(numKeys == 0){
				if(useIndex){
					log.warn("Failed to find mid point using indexes, falling back to data files which is slower. No entries between "+prevEndRow+" and "+endRow+" for "+mapFiles);
					//need to pass original map files, not possibly reduced indexes
					return findMidPoint(prevEndRow, endRow, origMapFiles, minSplit, false);
				}else{
					throw new IOException("Failed to find mid point, no entries between "+prevEndRow+" and "+endRow+" for "+mapFiles);
				}
			}

			SortedKeyValueIterator iters[] = new SortedKeyValueIterator[readers.length];
			for (int i = 0; i < readers.length; i++) {
				iters[i] = new SequenceFileIterator(readers[i], false);
			}
			
			MultiIterator mmfi = new MultiIterator(iters, (Text)null, true);
			
			// skip the prevendrow
			while(mmfi.hasTop() && mmfi.getTopKey().compareRow(prevEndRow) <= 0){
				mmfi.next();
			}
			
			//read half of the keys in the index
			TreeMap<Double, Key> ret = new TreeMap<Double, Key>();
			Key lastKey = null;
			long keysRead = 0;
			
			Key keyBeforeMidPoint=null;
			long keyBeforeMidPointPosition = 0;
			
			while(keysRead < numKeys/2){
				if(lastKey != null 
						&& lastKey.compareTo(mmfi.getTopKey(), 1) !=0
						&& (keysRead-1)/(double)numKeys >= minSplit){
					
					keyBeforeMidPoint = new Key(lastKey);
					keyBeforeMidPointPosition = keysRead-1;
				}

				if(lastKey == null){
					lastKey = new Key();
				}
				
				lastKey.set(mmfi.getTopKey());

				//System.err.println("Consuming : "+minIndex+" "+nextKey[minIndex]);
				keysRead++;
				
				// consume minimum
				mmfi.next();
			}

			if(keyBeforeMidPoint != null){
				ret.put(keyBeforeMidPointPosition/(double)numKeys, keyBeforeMidPoint);
			}
			
			//System.out.printf("DEBUG : prevRow %s %6.2f\n ",(currentRow == null ? null : currentRow.toString()),(prevRowPosition/(double)numKeys));
			
			long t2 = System.currentTimeMillis();
			
			log.debug(String.format("Found midPoint from indexes in %6.2f secs.\n", ((t2 - t1)/1000.0)));

			ret.put(.5, mmfi.getTopKey());
			
			//sanity check
			for(Key key : ret.values()){
				boolean inRange = (key.compareRow(prevEndRow) > 0 && (endRow == null || key.compareRow(endRow) < 1));
				if(!inRange){
					log.error("Found mid point is not in range "+key+" "+prevEndRow+" "+endRow+" "+mapFiles);
					throw new IOException("Found mid point is not in range "+key+" "+prevEndRow+" "+endRow+" "+mapFiles);
				}
			}
			
			return ret;
		}catch(InstantiationException e){
			throw new IOException(e.getMessage());
		} catch (IllegalAccessException e) {
			throw new IOException(e.getMessage());
		}finally{
			//close all of the index sequence files
			for(int i =0 ; i < readers.length; i++){
				try{
					if(readers[i] != null){
						readers[i].close();
					}
				}catch(IOException ioe){
					log.warn("Failed to close index file", ioe);
				}
			}
			
			if(tmpDir != null){
				String tmpPrefix = CBConfiguration.getInstance().get("cloudbase.directory", "/cloudbase")+"/tmp";
				if(tmpDir.startsWith(tmpPrefix)){
					fs.delete(new Path(tmpDir), true);
				}else{
					log.error("Did not delete tmp dir because it wasn't a tmp dir "+tmpDir);
				}
			}
		}
	}
		
	public static MySequenceFile.Reader simpleSeek(Text row, Path mapFile, Configuration conf, FileSystem fs) throws IOException{
		MySequenceFile.Reader index = openIndex(conf, fs, mapFile);
		
		Key key = new Key();
		LongWritable position = new LongWritable();
		
		Path dataPath = new Path(mapFile, MyMapFile.DATA_FILE_NAME);
		MySequenceFile.Reader data = new MySequenceFile.Reader(fs, dataPath, conf);
		
		long seekTo = data.getPosition();
		//System.out.println("File: "+mapFile.toString());
		//System.out.println("Initial position: "+seekTo);

		int indexPosition = 0;
		
		while(index.next(key, position)){
			int c = key.compareRow(row);
			if(c > 0 && indexPosition == 0){
				index.close();
				return data;
			}
			
			if(c > 0){
				break;
			}else{
				//System.out.println("index entry key=["+key+"]  pos=["+position.get()+"]");
				seekTo = position.get();
			}
			
			indexPosition++;
		}
		
		index.close();
		
		//System.out.println("Seek position: "+seekTo);
		data.seek(seekTo);
		
		return data;
	}
	
	public static MySequenceFile.Reader seek(Key seekKey, int depth, boolean skip, Path mapFile, Configuration conf, FileSystem fs) throws IOException{
		MySequenceFile.Reader index = openIndex(conf, fs, mapFile);
		
		Key key = new Key();
		Key prevKey = null;
		LongWritable position = new LongWritable();
		
		Path dataPath = new Path(mapFile, MyMapFile.DATA_FILE_NAME);
		MySequenceFile.Reader data = new MySequenceFile.Reader(fs, dataPath, conf);
		
		long seekTo = data.getPosition();

		int indexPosition = 0;
		
		while(index.next(key, position)){
			int c = seekKey.compareTo(key, depth);
			if(c < 0 && indexPosition == 0){
				index.close();
				return data;
			}
			
			if(c <= 0){
				break;
			}else{
				//System.out.println("index entry key=["+key+"]  pos=["+position.get()+"]");
				seekTo = position.get();
			}
			
			indexPosition++;
		}
		
		index.close();
		
		data.seek(seekTo);
		
		Value value = new Value();
		
		boolean sawSeekKey = false;
		
		while(data.next(key, value)){
			
			int c = seekKey.compareTo(key, depth);
			
			//System.out.println("data entry key=["+key+"]  pos=["+data.getPosition()+"]  c = "+c);
			
			
			if(c <= 0){
				sawSeekKey = c == 0;
				break;
			}
			
			
			if(prevKey == null){
				prevKey = new Key();
			}
			
			Key tmp = key;
			key = prevKey;
			prevKey = tmp;
			
		}
		
		if(!skip || !sawSeekKey){
			//System.out.println(" prev "+prevKey);
			data.seek(seekTo);
			
			if(prevKey != null){
				while(data.next(key, value)){
					if(prevKey.compareTo(key, depth) == 0){
						break;
					}
				}
			}
		}
		
		return data;
	}

	private static class MLong{
		public MLong(long i) {
			l = i;
		}

		long l;
	}
	
	public static Map<KeyExtent, Long> estimateSizes(Path mapFile, long fileSize, List<KeyExtent> extents, Configuration conf, FileSystem fs) throws IOException {
		Reader index = openIndex(conf, fs, mapFile);
		
		Key key = new Key();
		LongWritable position = new LongWritable();
		
		long totalIndexEntries = 0;
		
		Map<KeyExtent, MLong> counts = new TreeMap<KeyExtent, MLong>();
		
		for (KeyExtent keyExtent : extents) {
			counts.put(keyExtent, new MLong(0));
		}
		
		Text row = new Text();
		
		while(index.next(key, position)){
			totalIndexEntries++;
			
			key.getRow(row);
			
			for (Entry<KeyExtent, MLong> entry : counts.entrySet()) {
				if(entry.getKey().contains(row)){
					entry.getValue().l++;
				}
			}
		}

		Map<KeyExtent, Long> results = new TreeMap<KeyExtent, Long>();
		
		for (KeyExtent keyExtent : extents) {
			double numEntries = counts.get(keyExtent).l;
			if(numEntries == 0){
				numEntries = 1;
			}
			
			long estSize = (long)((numEntries / totalIndexEntries) * fileSize);
			
			results.put(keyExtent, estSize);
		}
		
		return results;
	}
}
