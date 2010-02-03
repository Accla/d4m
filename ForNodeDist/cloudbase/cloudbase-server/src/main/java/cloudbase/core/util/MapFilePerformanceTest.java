package cloudbase.core.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.MapFile;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.Text;

import cloudbase.core.data.Value;
import cloudbase.core.data.Key;
import cloudbase.core.data.KeyExtent;
import cloudbase.core.data.MyMapFile;
import cloudbase.core.tabletserver.iterators.MultiIterator;
import cloudbase.core.tabletserver.iterators.SortedKeyValueIterator;

public class MapFilePerformanceTest {

	public static String[] createMapFiles(String input, String output, int blocksize, int mapFiles) throws IOException{
		
		//System.out.println("Creating "+mapFiles+" map files using a compression block size of "+blocksize);
		
		Configuration conf = new Configuration();
		FileSystem fs = FileSystem.get(conf);
		
		//conf.set("io.seqfile.compress.blocksize", blocksize+"");
		
		
		
		SequenceFile.Reader in = new SequenceFile.Reader(fs, new Path(input+"/"+MapFile.DATA_FILE_NAME), conf);
		
		boolean someFilesExist = false;
		
		MapFile.Writer out[] = new MapFile.Writer[mapFiles];
		for (int i = 0; i < out.length; i++) {
			if(!fs.exists(new Path(output+"_"+i+"_"+mapFiles))){
				out[i] = new MapFile.Writer(conf, fs, output+"_"+i+"_"+mapFiles, Key.class, Value.class, SequenceFile.CompressionType.RECORD);
			}else{
				someFilesExist = true;
			}
		}
			
		Key key = new Key();
		Value value = new Value();
		
		Random r = new Random();
		
		if(someFilesExist){
			System.out.println("NOT Creating "+mapFiles+" map files using a compression block size of "+blocksize+" some files exist");
		}else{
			while(in.next(key, value)){
				int i = r.nextInt(mapFiles);
				out[i].append(key, value);
			}
		}
		
		String names[] = new String[mapFiles];
		
		in.close();
		for (int i = 0; i < out.length; i++) {
			if(out[i] != null){
				out[i].close();
			}
			names[i] = output+"_"+i+"_"+mapFiles;
		}
		
		//System.out.println("Finished creating "+mapFiles+" map files using a compression block size of "+blocksize);
		
		return names;
	}
	
	public static void selectRandomKeys(String input, double percentage, ArrayList<Key> keys) throws IOException{
		
		System.out.println("Selecting random keys ...");
		
		Configuration conf = new Configuration();
		FileSystem fs = FileSystem.get(conf);
		
		Random r = new Random();
		
		SequenceFile.Reader in = new SequenceFile.Reader(fs, new Path(input+"/"+MapFile.DATA_FILE_NAME), conf);
		
		Key key = new Key();
		//DeletableImmutableBytesWritable value = new DeletableImmutableBytesWritable();
		
		while(in.next(key)) {
			if(r.nextDouble() < percentage)
				keys.add(new Key(key));
		}
		
		in.close();
		
		Collections.shuffle(keys);
		
		System.out.println("Selected "+keys.size()+" random keys.");
	}
	
    public static void runTest(String testName, String mapFiles[], ArrayList<Key> queries) throws IOException{
		
		Configuration conf = new Configuration();
		FileSystem fs = FileSystem.get(conf);
		
		
		if(mapFiles.length == 1){
			MapFile.Reader mr = new MapFile.Reader(fs, mapFiles[0], conf);

			Value value = new Value();

			long t1 = System.currentTimeMillis();
			int count = 0;
			int misses = 0;
			for (Key key : queries) {
				//key.setTimestamp(Long.MAX_VALUE);
				//System.out.println("lookup key = "+key);

				Key key2 = (Key) mr.getClosest(key, value);
				if(key2.compareTo(key) != 0){
					//System.out.println(key2+" "+key);
					misses++;
				}
				//System.out.println("lookup --> "+key2+" "+value);
				count++;
			}


			long t2 = System.currentTimeMillis();

			mr.close();

			double secs = (t2 -t1)/1000.0;
			double queriesPerSec = count /  (secs);

			System.out.printf("DIRECT %40s q/s = %8.2f s = %8.2f m = %,d\n", testName, queriesPerSec, secs, misses);
		}
		
		MyMapFile.Reader readers[] = new MyMapFile.Reader[mapFiles.length];
		for(int i = 0; i < mapFiles.length; i++){
			readers[i] = new MyMapFile.Reader(fs, mapFiles[i], conf);
		}
		
		SortedKeyValueIterator iters[] = new SortedKeyValueIterator[readers.length];
		for (int i = 0; i < readers.length; i++) {
			iters[i] = readers[i];
		}
		
		MultiIterator mmfi = new MultiIterator(iters, new KeyExtent(new Text(""), null, null), false);
		mmfi.seek(new Key());
		
		
		long t1 = System.currentTimeMillis();
		int count = 0;
		int misses = 0;
		for (Key key : queries) {
			//key.setTimestamp(Long.MAX_VALUE);
			//System.out.println("lookup key = "+key);
			mmfi.seek(key);
			if(mmfi.getTopKey().compareTo(key) != 0){
				//System.out.println(mmfi.getTopKey()+" "+key);
				misses++;
			}
			count++;
		}


		long t2 = System.currentTimeMillis();

		

		double secs = (t2 -t1)/1000.0;
		double queriesPerSec = count /  (secs);

		System.out.printf("MMFI   %40s q/s = %8.2f s = %8.2f m = %,d\n", testName, queriesPerSec, secs, misses);
		
		for(int i = 0; i < mapFiles.length; i++){
			readers[i].close();
		}
	}
	
	/**long t1 = System.currentTimeMillis();
	 * @param args
	 * @throws IOException 
	 * @throws InterruptedException 
	 */
	public static void main(final String[] args) throws IOException, InterruptedException {
		
		Configuration conf = new Configuration();
		FileSystem fs = FileSystem.get(conf);
		
		final ArrayList<Key> keys = new ArrayList<Key>();
		
		int blocksizes[] = new int[]{10000};
		int numMapFiles[] = new int[]{1,2,3,5,7};
		
		ExecutorService tp = Executors.newFixedThreadPool(10);
		
		Runnable selectKeysTask = new Runnable(){

			public void run() {
				try {
					selectRandomKeys(args[0], .002, keys);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		};
		
		tp.submit(selectKeysTask);
		
		final Map<Integer, Map<Integer, String[]>> tests = new HashMap<Integer, Map<Integer, String[]>>();
		
		for (final int num : numMapFiles) {
			for (final int blocksize : blocksizes) {
				
				Runnable r = new Runnable(){
					public void run(){
						System.out.println("Thread "+Thread.currentThread().getName()+" creating map files blocksize = "+blocksize+" num = "+num);
						String[] filenames;
						try {
							filenames = createMapFiles(args[0], args[1]+"/map_"+blocksize, blocksize, num);
							
							synchronized (tests) {
								Map<Integer, String[]> map = tests.get(num);
								if(map == null){
									map = new HashMap<Integer, String[]>();
									tests.put(num, map);
								}
								
								map.put(blocksize, filenames);
							}
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						System.out.println("Thread "+Thread.currentThread().getName()+" finished creating map files");
						
						
					}
				};
				
				tp.execute(r);
			}
		}

		tp.shutdown();
		while(!tp.isTerminated()){
			tp.awaitTermination(1, TimeUnit.DAYS);
		}
		
		for (int num : numMapFiles) {
			for (int blocksize : blocksizes) {
				String[] filenames = tests.get(num).get(blocksize);
				
				long len = 0;
				for (String filename : filenames) {
					len  += fs.getFileStatus(new Path(filename+"/"+MapFile.DATA_FILE_NAME)).getLen();
				}
				runTest(String.format("bs = %,12d fs = %,12d nmf = %d ",blocksize,len, num), filenames, keys);
				runTest(String.format("bs = %,12d fs = %,12d nmf = %d ",blocksize,len, num), filenames, keys);
			}
		}
	}

}
