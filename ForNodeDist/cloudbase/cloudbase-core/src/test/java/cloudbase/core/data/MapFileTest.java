package cloudbase.core.data;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import junit.framework.TestCase;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;

import cloudbase.core.data.MySequenceFile.CompressionType;

public class MapFileTest extends TestCase {
	public void testMapFileIndexing()
	{
		try {
			Configuration conf = new Configuration();
			FileSystem fs = FileSystem.get(conf);
			conf.setInt("io.seqfile.compress.blocksize", 2000);

			/*****************************
			 * write out the test map file
			 */
			MyMapFile.Writer mfw = new MyMapFile.Writer(conf,fs,"/tmp/testMapFileIndexingMap",Text.class,BytesWritable.class,CompressionType.BLOCK);
			Text key = new Text();
			BytesWritable value;
			Random r = new Random();
			byte [] bytes = new byte [1024];
			ArrayList<String> keys = new ArrayList<String>();
			ArrayList<String> badKeys = new ArrayList<String>();
			for(int i = 0; i < 1000; i++)
			{
				String keyString = Integer.toString(i+1000000);
				keys.add(keyString);
				badKeys.add(keyString+"_");
				key.set(keyString);
				r.nextBytes(bytes);
				bytes[0] = (byte)keyString.charAt(0);
				bytes[1] = (byte)keyString.charAt(1);
				bytes[2] = (byte)keyString.charAt(2);
				bytes[3] = (byte)keyString.charAt(3);
				bytes[4] = (byte)keyString.charAt(4);
				bytes[5] = (byte)keyString.charAt(5);
				bytes[6] = (byte)keyString.charAt(6);
				value = new BytesWritable(bytes);
				mfw.append(key, value);
			}
			mfw.close();
			
			MyMapFile.Reader mfr = new MyMapFile.Reader(fs,"/tmp/testMapFileIndexingMap",conf);
			value = new BytesWritable();
			// test edge cases
			key.set(keys.get(0));
			assertTrue(mfr.seek(key));
			key.set(keys.get(keys.size() - 1));
			assertTrue(mfr.seek(key));
			key.set("");
			assertFalse(mfr.seek(key));
			key.set(keys.get(keys.size()-1)+"_");
			assertFalse(mfr.seek(key));
			
			// test interaction with nextKey
			key.set(keys.get(17));
			//System.out.println("Seeking to key "+key);
			assertTrue(mfr.seek(key));
			//System.out.println("Scanning...");
			assertTrue(mfr.next(key, value));
			//System.out.println("Got key "+key);
			//System.out.println("Seeking to key "+key);
			assertTrue(mfr.seek(key,key));
			
			
			// test seeking before the beginning of the file
			key.set("");
			Text closestKey = (Text) mfr.getClosest(key, value, false, null);
			assertTrue(closestKey.toString().equals(keys.get(0)));
			assertTrue(value.getBytes()[6] == (byte)(closestKey.toString().charAt(6)));
			closestKey = (Text) mfr.getClosest(key, value, false, closestKey);
			assertTrue(closestKey.toString().equals(keys.get(0)));
			assertTrue(value.getBytes()[6] == (byte)(closestKey.toString().charAt(6)));
			closestKey = (Text) mfr.getClosest(key, value, false, closestKey);
			assertTrue(closestKey.toString().equals(keys.get(0)));
			assertTrue(value.getBytes()[6] == (byte)(closestKey.toString().charAt(6)));
			
			// test seeking after the end of the file
			key.set(keys.get(keys.size() - 1));
			closestKey = (Text) mfr.getClosest(key, value, false, null);
			assertTrue(keys.get(keys.size()-1).equals(closestKey.toString()));
			key.set("z");
			closestKey = (Text) mfr.getClosest(key, value, false, closestKey);
			assertTrue(closestKey == null);
			key.set("zz");
			closestKey = (Text) mfr.getClosest(key, value, false, closestKey);
			assertTrue(closestKey == null);
			
			
			key.set(keys.get(keys.size() - 1));
			closestKey = (Text) mfr.getClosest(key, value, false, null);
			assertFalse(mfr.next(closestKey, value));
			key.set("z");
			closestKey = (Text) mfr.getClosest(key, value, false, closestKey);
			assertTrue(closestKey == null);
			
			// test sequential reads
			for(int sample = 0; sample < 1000; sample++)
			{
				key.set(keys.get(sample));
				assertTrue(mfr.seek(key));
			}
			
			// test sequential misses
			for(int sample = 0; sample < 1000; sample++)
			{
				key.set(badKeys.get(sample));
				assertFalse(mfr.seek(key));
			}
			
			// test backwards reads
			for(int sample = 0; sample < 1000; sample++)
			{
				key.set(keys.get(keys.size() - 1 - sample));
				assertTrue(mfr.seek(key));
			}
			
			// test backwards misses
			for(int sample = 0; sample < 1000; sample++)
			{
				key.set(badKeys.get(badKeys.size() - 1 - sample));
				assertFalse(mfr.seek(key));
			}
			
			// test interleaved reads
			for(int sample = 0; sample < 1000; sample++)
			{
				key.set(keys.get(sample));
				assertTrue(mfr.seek(key));
				key.set(badKeys.get(sample));
				assertFalse(mfr.seek(key));				
			}
			
			
			// test random reads
			Collections.shuffle(keys);
			Collections.shuffle(badKeys);
			for(int sample = 0; sample < 1000; sample++)
			{
				key.set(keys.get(sample));
				boolean seekGood = mfr.seek(key);
				if(!seekGood)
				{
					System.out.println("Key: "+keys.get(sample));
					if(sample != 0)
					{
						System.out.println("Last key: "+keys.get(sample-1));
					}
				}
				assertTrue(seekGood);
				key.set(badKeys.get(sample));
				assertTrue(!mfr.seek(key));
			}
			
			fs.delete(new Path("/tmp/testMapFileIndexingMap"), true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void testMapFileFix()
	{
		try {
			Configuration conf = new Configuration();
			FileSystem fs = FileSystem.get(conf);
			conf.setInt("io.seqfile.compress.blocksize", 4000);

			for(CompressionType compressionType:CompressionType.values())
			{
				System.out.println("Testing fix with compression type "+compressionType);
				/*****************************
				 * write out the test map file
				 */
				MyMapFile.Writer mfw = new MyMapFile.Writer(conf,fs,"/tmp/testMapFileIndexingMap",Text.class,BytesWritable.class,compressionType);
				BytesWritable value;
				Random r = new Random();
				byte [] bytes = new byte [1024];
				for(int i = 0; i < 1000; i++)
				{
					String keyString = Integer.toString(i+1000000);
					Text key = new Text(keyString);
					r.nextBytes(bytes);
					value = new BytesWritable(bytes);
					mfw.append(key, value);
				}
				mfw.close();
	
				/************************************
				 * move the index file
				 */
				fs.rename(new Path("/tmp/testMapFileIndexingMap/index"), new Path("/tmp/testMapFileIndexingMap/oldIndex"));
				
				/************************************
				 * recreate the index
				 */
				MyMapFile.fix(fs, new Path("/tmp/testMapFileIndexingMap"), Text.class, BytesWritable.class, false, conf);
				
	
				/************************************
				 * compare old and new indices
				 */
				MySequenceFile.Reader oldIndexReader = new MySequenceFile.Reader(fs,new Path("/tmp/testMapFileIndexingMap/oldIndex"),conf);
				MySequenceFile.Reader newIndexReader = new MySequenceFile.Reader(fs,new Path("/tmp/testMapFileIndexingMap/index"),conf);
	
				Text oldKey = new Text();
				Text newKey = new Text();
				LongWritable oldValue = new LongWritable();
				LongWritable newValue = new LongWritable();
				while(true)
				{
					boolean moreKeys = false;
					// check for the same number of records
					assertTrue((moreKeys = oldIndexReader.next(oldKey, oldValue)) == newIndexReader.next(newKey,newValue));
					if(!moreKeys)
						break;
	//				System.out.println("Old Key: "+oldKey+"  New Key: "+newKey);
	//				System.out.println("Old Value: "+oldValue+"  New Value: "+newValue);
					assertTrue(oldKey.compareTo(newKey) == 0);
					assertTrue(oldValue.compareTo(newValue) == 0);
				}
				oldIndexReader.close();
				newIndexReader.close();
	
				fs.delete(new Path("/tmp/testMapFileIndexingMap"), true);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void main(String [] args)
	{
		try {
			Configuration conf = new Configuration();
			FileSystem fs = FileSystem.get(conf);
			MyMapFile.Writer mfw = new MyMapFile.Writer(conf,fs,"/tmp/testMapFileIndexingMap",Text.class,BytesWritable.class,CompressionType.BLOCK);
			Text key = new Text();
			BytesWritable value;
			Random r = new Random();
			byte [] bytes = new byte [1024];
			ArrayList<String> keys = new ArrayList<String>();
			ArrayList<String> badKeys = new ArrayList<String>();
			for(int i = 0; i < 100000; i++)
			{
				String keyString = Integer.toString(i+1000000);
				keys.add(keyString);
				badKeys.add(keyString+"_");
				key.set(keyString);
				r.nextBytes(bytes);
				value = new BytesWritable(bytes);
				mfw.append(key, value);
			}
			mfw.close();
			
			MyMapFile.Reader mfr = new MyMapFile.Reader(fs,"/tmp/testMapFileIndexingMap",conf);

			long t1 = System.currentTimeMillis();

			value = new BytesWritable();
			key.set(keys.get(0));
			mfr.seek(key);
			while(mfr.next(key, value));

			long t2 = System.currentTimeMillis();
			
			System.out.println("Scan time: "+(t2-t1));
			
			t1 = System.currentTimeMillis();
			Text key2 = new Text();
			for(int i = 0; i < 100000; i+=1000)
			{
				key.set(keys.get(i));
				key2 = (Text) mfr.getClosest(key, value, false, key2);
			}
			t2 = System.currentTimeMillis();
			
			System.out.println("Seek time: "+(t2-t1));
			
			t1 = System.currentTimeMillis();

			value = new BytesWritable();
			key.set(keys.get(0));
			mfr.seek(key);
			while(mfr.next(key, value));

			t2 = System.currentTimeMillis();
			System.out.println("Scan time: "+(t2-t1));
			t1 = System.currentTimeMillis();
			key2 = new Text();
			for(int i = 0; i < 100000; i+=1000)
			{
				key.set(keys.get(i));
				key2 = (Text) mfr.getClosest(key, value, false, key2);
			}
			t2 = System.currentTimeMillis();
			
			System.out.println("Seek time: "+(t2-t1));

			fs.delete(new Path("/tmp/testMapFileIndexingMap"), true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
