package cloudbase.core.tabletserver.iterators;

import java.io.IOException;
import java.util.TreeMap;
import java.util.Map.Entry;

import junit.framework.TestCase;

import org.apache.hadoop.io.Text;

import cloudbase.core.aggregation.LongSummation;
import cloudbase.core.data.Value;
import cloudbase.core.data.Key;

public class VersioningIteratorTest extends TestCase {
	// TODO add test for seek function
	
	void createTestData(TreeMap<Key, Value> tm, Text colf, Text colq){
		for (int i = 0; i < 2; i++) {
			for (long j = 0; j < 20; j++) {
				Key k = new Key(new Text(String.format("%03d", i)), colf , colq, j);
				tm.put(k, new Value(LongSummation.longToBytes(j)));
			}
		}
		
		assertTrue("Initial size was "+tm.size(),tm.size()==40);
	}
	
	TreeMap<Key, Value> iteratorOverTestData(VersioningIterator it) throws IOException{
		TreeMap<Key, Value> tmOut = new TreeMap<Key, Value>();
		while (it.hasTop()) {
			tmOut.put(it.getTopKey(), it.getTopValue());
			it.next();
		}
		
		return tmOut;
	}
	
	public void test1() {
		Text colf = new Text("a");
		Text colq = new Text("b");
		
		TreeMap<Key, Value> tm = new TreeMap<Key, Value>();
		
		createTestData(tm, colf, colq);
		
		try {
			VersioningIterator it = new VersioningIterator(new SortedMapIterator<Key, Value>(tm), 3);
			TreeMap<Key, Value> tmOut = iteratorOverTestData(it);
			
			for (Entry<Key, Value> e : tmOut.entrySet()) {
//				System.out.println(e.getKey().toString()+" "+LongSummation.bytesToLong(e.getValue().get()));
				assertTrue(e.getValue().get().length==8);
				assertTrue(16<LongSummation.bytesToLong(e.getValue().get()));
			}
			assertTrue("size after keeping 3 versions was "+tmOut.size(),tmOut.size()==6);
		}
		catch (IOException e) {
			assertFalse(true);
		} catch (Exception e) {
			e.printStackTrace();
			assertFalse(true);
		}
	}
	
	public void test2(){
		Text colf = new Text("a");
		Text colq = new Text("b");
		
		TreeMap<Key, Value> tm = new TreeMap<Key, Value>();
		
		createTestData(tm, colf, colq);
		
		try {
			VersioningIterator it = new VersioningIterator(new SortedMapIterator<Key, Value>(tm), 3);
			
			//after doing this seek, should only get two keys for row 1
			//since we are seeking to the middle of the most recent
			//three keys
			Key seekKey = new Key(new Text(String.format("%03d", 1)),colf, colq, 18);
			it.seek(seekKey);

//			System.out.println("seekKey: "+seekKey.toString());

			TreeMap<Key, Value> tmOut = iteratorOverTestData(it);
			
			for (Entry<Key, Value> e : tmOut.entrySet()) {
//				System.out.println(e.getKey().toString()+" "+LongSummation.bytesToLong(e.getValue().get()));
				assertTrue(e.getValue().get().length==8);
				assertTrue(16<LongSummation.bytesToLong(e.getValue().get()));
			}
			assertTrue("size after keeping 2 versions was "+tmOut.size(),tmOut.size()==2);
		}
		catch (IOException e) {
			assertFalse(true);
		} catch (Exception e) {
			e.printStackTrace();
			assertFalse(true);
		}
	}
	
	public void test3(){
		Text colf = new Text("a");
		Text colq = new Text("b");
		
		TreeMap<Key, Value> tm = new TreeMap<Key, Value>();
		
		createTestData(tm, colf, colq);
		
		try {
			VersioningIterator it = new VersioningIterator(new SortedMapIterator<Key, Value>(tm), 3);

			//after doing this seek, should get zero keys for row 1
			Key seekKey = new Key(new Text(String.format("%03d", 1)),colf, colq, 15);
			it.seek(seekKey);

//			System.out.println("seekKey: "+seekKey.toString());

			TreeMap<Key, Value> tmOut = iteratorOverTestData(it);
			
			for (Entry<Key, Value> e : tmOut.entrySet()) {
//				System.out.println(e.getKey().toString()+" "+LongSummation.bytesToLong(e.getValue().get()));
				assertTrue(e.getValue().get().length==8);
				assertTrue(16<LongSummation.bytesToLong(e.getValue().get()));
			}

			assertTrue("size after seeking past versions was "+tmOut.size(),tmOut.size()==0);
			
			
			//after doing this seek, should get zero keys for row 0 and 3 keys for row 1
			seekKey = new Key(new Text(String.format("%03d", 0)),colf, colq, 15);
			it.seek(seekKey);

//			System.out.println("seekKey: "+seekKey.toString());

			tmOut = iteratorOverTestData(it);
			
			for (Entry<Key, Value> e : tmOut.entrySet()) {
//				System.out.println(e.getKey().toString()+" "+LongSummation.bytesToLong(e.getValue().get()));
				assertTrue(e.getValue().get().length==8);
				assertTrue(16<LongSummation.bytesToLong(e.getValue().get()));
			}

			assertTrue("size after seeking past versions was "+tmOut.size(),tmOut.size()==3);
			
		}
		catch (IOException e) {
			assertFalse(true);
		} catch (Exception e) {
			e.printStackTrace();
			assertFalse(true);
		}
	}
	
	public void test4() {
		Text colf = new Text("a");
		Text colq = new Text("b");
		
		TreeMap<Key, Value> tm = new TreeMap<Key, Value>();
		
		createTestData(tm, colf, colq);
		
		try {
			VersioningIterator it = new VersioningIterator(new SortedMapIterator<Key, Value>(tm), 30);
			TreeMap<Key, Value> tmOut = iteratorOverTestData(it);
			
			assertTrue("size after keeping 30 versions was "+tmOut.size(),tmOut.size()==40);
		}
		catch (IOException e) {
			assertFalse(true);
		} catch (Exception e) {
			e.printStackTrace();
			assertFalse(true);
		}
	}

}
