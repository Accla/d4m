package cloudbase.core.test;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.io.Text;

import cloudbase.core.data.Key;
import cloudbase.core.data.KeyExtent;
import cloudbase.core.data.MyMapFile;
import cloudbase.core.tabletserver.iterators.MultiIterator;
import cloudbase.core.tabletserver.iterators.SortedKeyValueIterator;
import cloudbase.core.util.MapFileUtil;

public class MMFITest {

	/**
	 * @param args
	 * @throws IOException 
	 */
	
    public static void runTest(String[] args) throws IOException {
		MyMapFile.Reader[] mapfiles = new MyMapFile.Reader[args.length - 3];
		
		Configuration conf = new Configuration();
		FileSystem fs = FileSystem.get(conf);
		
		Text startRow = new Text(String.format("row_%010d", Integer.parseInt(args[0])));
		Text prevEndRow = new Text(String.format("row_%010d", Integer.parseInt(args[1])));
		Text endRow = new Text(String.format("row_%010d", Integer.parseInt(args[2])));
		
		for (int i = 3; i < args.length; i++) {
			mapfiles[i - 3] = MapFileUtil.openMapFile(fs, args[i], conf);
		}
		
		SortedKeyValueIterator iters[] = new SortedKeyValueIterator[mapfiles.length];
		for (int i = 0; i < mapfiles.length; i++) {
			iters[i] = mapfiles[i];
		}
		
		MultiIterator mmfi = new MultiIterator(iters, new KeyExtent(new Text(""), endRow, prevEndRow), false);
		mmfi.seek(new Key(startRow));
		
		int count = 0;
		
		long t1 = System.currentTimeMillis();
		
		Key lastKey = new Key();
		
		while(mmfi.hasTop()){
			Key key = mmfi.getTopKey();
			
			if(lastKey.compareTo(key) > 0){
				System.err.println("Not sorted : "+lastKey+" "+key);
				System.exit(-1);
			}
			
			lastKey.set(key);
			
			System.out.println(" "+key);
			
			mmfi.next();
			count++;
		}
		
		long t2 = System.currentTimeMillis();
		
		double time = (t2 - t1) / 1000.0;
		System.out.printf("count : %,d   time : %6.2f  rate : %,6.2f\n", count, time, count/time);
		
		for (int i = 0; i < mapfiles.length; i++) {
			mapfiles[i].close();
		}
	}
	
	public static void main(String[] args) throws IOException, InterruptedException {
		runTest(args);
		
		/*System.gc();
		Thread.sleep(5000);
		
		runTest(args);
		
		System.gc();
		Thread.sleep(5000);
		
		runTest(args);*/
	}

}
