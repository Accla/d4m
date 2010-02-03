package cloudbase.core.tabletserver.iterators;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.apache.hadoop.io.Text;

import cloudbase.core.aggregation.Aggregator;
import cloudbase.core.data.Value;
import cloudbase.core.data.Key;
import junit.framework.TestCase;

public class AggregatingIteratorTest extends TestCase {
	
	public static class SummationAggregator implements Aggregator {

		int sum;
		
		public Value aggregate() {
			return new Value((sum+"").getBytes());
		}

		public void collect(Value value) {
			int val = Integer.parseInt(value.toString());
			
			sum += val;
		}

		public void reset() {
			sum = 0;
			
		}
		
	}
	
	
	static Key nk(int row, int colf, int colq, long ts){
		return new Key(nr(row),new Text(String.format("cf%03d", colf)),new Text(String.format("cq%03d", colq)),ts);
	}
	
	static void nkv(TreeMap<Key, Value> tm, int row, int colf, int colq, long ts, boolean deleted, String val) {
		Key k = nk(row, colf, colq, ts);
		k.setDeleted(deleted);
		tm.put(k, new Value(val.getBytes()));
	}
	
	static Text nr(int row){
		return new Text(String.format("r%03d", row));
	}
	
	public void test1() throws IOException{
		
		TreeMap<Key, Value> tm1 = new TreeMap<Key, Value>();
		
		//keys that do not aggregate
		nkv(tm1, 1, 1, 1, 1, false, "2");
		nkv(tm1, 1, 1, 1, 2, false, "3");
		nkv(tm1, 1, 1, 1, 3, false, "4");
		
		AggregatingIterator ai = new AggregatingIterator();
		
		
		Map<String, String> emptyMap = Collections.emptyMap();
	    ai.init(new SortedMapIterator(tm1), emptyMap);
		
		assertTrue(ai.hasTop());
		assertEquals(nk(1,1,1,3), ai.getTopKey());
		assertEquals("4", ai.getTopValue().toString());
		
		ai.next();
		
		assertTrue(ai.hasTop());
		assertEquals(nk(1,1,1,2), ai.getTopKey());
		assertEquals("3", ai.getTopValue().toString());
		
		ai.next();
		
		assertTrue(ai.hasTop());
		assertEquals(nk(1,1,1,1), ai.getTopKey());
		assertEquals("2", ai.getTopValue().toString());
		
		ai.next();
		
		assertFalse(ai.hasTop());
		
		//try seeking
		
		ai.seek(nk(1,1,1,2));
		
		assertTrue(ai.hasTop());
		assertEquals(nk(1,1,1,2), ai.getTopKey());
		assertEquals("3", ai.getTopValue().toString());
		
		ai.next();
		
		assertTrue(ai.hasTop());
		assertEquals(nk(1,1,1,1), ai.getTopKey());
		assertEquals("2", ai.getTopValue().toString());
		
		ai.next();
		
		assertFalse(ai.hasTop());
		
		//seek after everything
		ai.seek(nk(1,1,1,0));
		
		assertFalse(ai.hasTop());
		
	}
	
	public void test2() throws IOException{
		
		TreeMap<Key, Value> tm1 = new TreeMap<Key, Value>();
		
		//keys that aggregate
		nkv(tm1, 1, 1, 1, 1, false, "2");
		nkv(tm1, 1, 1, 1, 2, false, "3");
		nkv(tm1, 1, 1, 1, 3, false, "4");
		
		AggregatingIterator ai = new AggregatingIterator();
		
		Map<String, String> opts = new HashMap<String, String>();
		
		opts.put("cf001", SummationAggregator.class.getName());
		
		ai.init(new SortedMapIterator(tm1), opts);
		
		assertTrue(ai.hasTop());
		assertEquals(nk(1,1,1,3), ai.getTopKey());
		assertEquals("9", ai.getTopValue().toString());
		
		ai.next();
		
		assertFalse(ai.hasTop());
		
		//try seeking to the beginning of a key that aggregates
		
		ai.seek(nk(1,1,1,3));
		
		assertTrue(ai.hasTop());
		assertEquals(nk(1,1,1,3), ai.getTopKey());
		assertEquals("9", ai.getTopValue().toString());
		
		ai.next();
		
		assertFalse(ai.hasTop());
		
		//try seeking the middle of a key the aggregates
		ai.seek(nk(1,1,1,2));
		
		assertFalse(ai.hasTop());
		
		
		//try seeking to the end of a key the aggregates
		ai.seek(nk(1,1,1,1));
		
		assertFalse(ai.hasTop());
		
		//try seeking before a key the aggregates
		ai.seek(nk(1,1,1,4));
		
		assertTrue(ai.hasTop());
		assertEquals(nk(1,1,1,3), ai.getTopKey());
		assertEquals("9", ai.getTopValue().toString());
		
		ai.next();
		
		assertFalse(ai.hasTop());
	}
	
	public void test3() throws IOException{
		
		TreeMap<Key, Value> tm1 = new TreeMap<Key, Value>();
		
		//keys that aggregate
		nkv(tm1, 1, 1, 1, 1, false, "2");
		nkv(tm1, 1, 1, 1, 2, false, "3");
		nkv(tm1, 1, 1, 1, 3, false, "4");
		
		//keys that do not aggregate
		nkv(tm1, 2, 2, 1, 1, false, "2");
		nkv(tm1, 2, 2, 1, 2, false, "3");
		
		AggregatingIterator ai = new AggregatingIterator();
		
		Map<String, String> opts = new HashMap<String, String>();
		
		opts.put("cf001", SummationAggregator.class.getName());
		
		ai.init(new SortedMapIterator(tm1), opts);
		
		assertTrue(ai.hasTop());
		assertEquals(nk(1,1,1,3), ai.getTopKey());
		assertEquals("9", ai.getTopValue().toString());
		
		ai.next();
		
		assertTrue(ai.hasTop());
		assertEquals(nk(2,2,1,2), ai.getTopKey());
		assertEquals("3", ai.getTopValue().toString());
		
		ai.next();
		
		assertTrue(ai.hasTop());
		assertEquals(nk(2,2,1,1), ai.getTopKey());
		assertEquals("2", ai.getTopValue().toString());
		
		ai.next();
		
		assertFalse(ai.hasTop());
		
		//seek after key that aggregates
		ai.seek(nk(1,1,1,2));
		
		assertTrue(ai.hasTop());
		assertEquals(nk(2,2,1,2), ai.getTopKey());
		assertEquals("3", ai.getTopValue().toString());
		
		//seek before key that aggregates
		ai.seek(nk(1,1,1,4));
		
		assertTrue(ai.hasTop());
		assertEquals(nk(1,1,1,3), ai.getTopKey());
		assertEquals("9", ai.getTopValue().toString());
		
		ai.next();
		
		assertTrue(ai.hasTop());
		assertEquals(nk(2,2,1,2), ai.getTopKey());
		assertEquals("3", ai.getTopValue().toString());
		
	}
	
	public void test4() throws IOException{
		
		TreeMap<Key, Value> tm1 = new TreeMap<Key, Value>();
		
		//keys that do not aggregate
		nkv(tm1, 0, 0, 1, 1, false, "7");
		
		//keys that aggregate
		nkv(tm1, 1, 1, 1, 1, false, "2");
		nkv(tm1, 1, 1, 1, 2, false, "3");
		nkv(tm1, 1, 1, 1, 3, false, "4");
		
		//keys that do not aggregate
		nkv(tm1, 2, 2, 1, 1, false, "2");
		nkv(tm1, 2, 2, 1, 2, false, "3");
		
		AggregatingIterator ai = new AggregatingIterator();
		
		Map<String, String> opts = new HashMap<String, String>();
		
		opts.put("cf001", SummationAggregator.class.getName());
		
		ai.init(new SortedMapIterator(tm1), opts);
		
		assertTrue(ai.hasTop());
		assertEquals(nk(0,0,1,1), ai.getTopKey());
		assertEquals("7", ai.getTopValue().toString());
		
		ai.next();
		
		assertTrue(ai.hasTop());
		assertEquals(nk(1,1,1,3), ai.getTopKey());
		assertEquals("9", ai.getTopValue().toString());
		
		ai.next();
		
		assertTrue(ai.hasTop());
		assertEquals(nk(2,2,1,2), ai.getTopKey());
		assertEquals("3", ai.getTopValue().toString());
		
		ai.next();
		
		assertTrue(ai.hasTop());
		assertEquals(nk(2,2,1,1), ai.getTopKey());
		assertEquals("2", ai.getTopValue().toString());
		
		ai.next();
		
		assertFalse(ai.hasTop());
		
		//seek test
		ai.seek(nk(0,0,1,0));
		
		assertTrue(ai.hasTop());
		assertEquals(nk(1,1,1,3), ai.getTopKey());
		assertEquals("9", ai.getTopValue().toString());
		
		ai.next();
		
		assertTrue(ai.hasTop());
		assertEquals(nk(2,2,1,2), ai.getTopKey());
		assertEquals("3", ai.getTopValue().toString());
		
		//seek after key that aggregates
		ai.seek(nk(1, 1, 1, 2));
		
		assertTrue(ai.hasTop());
		assertEquals(nk(2,2,1,2), ai.getTopKey());
		assertEquals("3", ai.getTopValue().toString());
		
	}
	
	public void test5() throws IOException{
		//try aggregating across multiple data sets that contain 
		//the exact same keys w/ different values
		
		TreeMap<Key, Value> tm1 = new TreeMap<Key, Value>();
		nkv(tm1, 1, 1, 1, 1, false, "2");
		
		TreeMap<Key, Value> tm2 = new TreeMap<Key, Value>();
		nkv(tm2, 1, 1, 1, 1, false, "3");
		
		TreeMap<Key, Value> tm3 = new TreeMap<Key, Value>();
		nkv(tm3, 1, 1, 1, 1, false, "4");
		
		
		AggregatingIterator ai = new AggregatingIterator();
		Map<String, String> opts = new HashMap<String, String>();
		opts.put("cf001", SummationAggregator.class.getName());
		
		SortedKeyValueIterator<Key, Value>[] sources = new SortedKeyValueIterator[3];
		sources[0] = new SortedMapIterator<Key, Value>(tm1);
		sources[1] = new SortedMapIterator<Key, Value>(tm2);
		sources[2] = new SortedMapIterator<Key, Value>(tm3);
		
		MultiIterator mi = new MultiIterator(sources, (Text)null, true); 
		ai.init(mi, opts);
		
		assertTrue(ai.hasTop());
		assertEquals(nk(1, 1, 1, 1), ai.getTopKey());
		assertEquals("9", ai.getTopValue().toString());
	}
}
