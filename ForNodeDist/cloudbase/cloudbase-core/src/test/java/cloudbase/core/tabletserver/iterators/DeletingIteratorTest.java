package cloudbase.core.tabletserver.iterators;

import java.io.IOException;
import java.util.TreeMap;
import java.util.Map.Entry;

import junit.framework.TestCase;

import org.apache.hadoop.io.Text;

import cloudbase.core.data.Value;
import cloudbase.core.data.Key;

public class DeletingIteratorTest extends TestCase {
	
	public void test1() {
		Text colf = new Text("a");
		Text colq = new Text("b");
		Value dvOld = new Value("old".getBytes());
		Value dvDel = new Value("old".getBytes());
		Value dvNew = new Value("new".getBytes());
		
		TreeMap<Key, Value> tm = new TreeMap<Key, Value>();
		Key k;
		
		for (int i = 0; i < 2; i++) {
			for (long j = 0; j < 5; j++) {
				k = new Key(new Text(String.format("%03d", i)), colf, colq ,j);
				tm.put(k, dvOld);
			}
		}
		k = new Key(new Text(String.format("%03d", 0)), colf , colq,5);
		k.setDeleted(true);
		tm.put(k, dvDel);
		for (int i = 0; i < 2; i++) {
			for (long j = 6; j < 11; j++) {
				k = new Key(new Text(String.format("%03d", i)), colf, colq, j);
				tm.put(k, dvNew);
			}
		}
		assertTrue("Initial size was "+tm.size(),tm.size()==21);
		
		Text checkRow = new Text("000");
		try {
			DeletingIterator it = new DeletingIterator(new SortedMapIterator<Key, Value>(tm), false);
			TreeMap<Key, Value> tmOut = new TreeMap<Key, Value>();
			while (it.hasTop()) {
				tmOut.put(it.getTopKey(), it.getTopValue());
				it.next();
			}
			assertTrue("size after no propagation was "+tmOut.size(),tmOut.size()==15);
			for (Entry<Key, Value> e : tmOut.entrySet()) {
				if (e.getKey().getRow().equals(checkRow)) {
					byte[] b = e.getValue().get();
					assertTrue(b[0]=='n');
					assertTrue(b[1]=='e');
					assertTrue(b[2]=='w');						
				}
			}
		}
		catch (IOException e) {
			assertFalse(true);
		}

		try {
			DeletingIterator it = new DeletingIterator(new SortedMapIterator<Key, Value>(tm), true);
			TreeMap<Key, Value> tmOut = new TreeMap<Key, Value>();
			while (it.hasTop()) {
				tmOut.put(it.getTopKey(), it.getTopValue());
				it.next();
			}
			assertTrue("size after propagation was "+tmOut.size(),tmOut.size()==16);
			for (Entry<Key, Value> e : tmOut.entrySet()) {
				if (e.getKey().getRow().equals(checkRow)) {
					byte[] b = e.getValue().get();
					if (e.getKey().isDeleted()) {
						assertTrue(b[0]=='o');
						assertTrue(b[1]=='l');
						assertTrue(b[2]=='d');
					}
					else {
						assertTrue(b[0]=='n');
						assertTrue(b[1]=='e');
						assertTrue(b[2]=='w');						
					}
				}
			}
		}
		catch (IOException e) {
			assertFalse(true);
		}
	}
	
	//seek test
	public void test2() throws IOException{
		TreeMap<Key, Value> tm = new TreeMap<Key, Value>();
		
		nkv(tm, "r000", 4, false, "v4");
		nkv(tm, "r000", 3, false, "v3");
		nkv(tm, "r000", 2, true, "v2");
		nkv(tm, "r000", 1, false, "v1");
		
		DeletingIterator it = new DeletingIterator(new SortedMapIterator<Key, Value>(tm), false);
		
		//SEEK two keys before delete
		it.seek(nk("r000", 4));
		
		assertTrue(it.hasTop());
		assertEquals(nk("r000", 4), it.getTopKey());
		assertEquals("v4", it.getTopValue().toString());
		
		it.next();
		
		assertTrue(it.hasTop());
		assertEquals(nk("r000", 3), it.getTopKey());
		assertEquals("v3", it.getTopValue().toString());
		
		it.next();
		
		assertFalse(it.hasTop());
		
		//SEEK passed delete
		it.seek(nk("r000", 1));
		
		assertFalse(it.hasTop());
		
		//SEEK to delete
		it.seek(nk("r000", 2));
		
		assertFalse(it.hasTop());
		
		//SEEK right before delete
		it.seek(nk("r000", 3));
		
		assertTrue(it.hasTop());
		assertEquals(nk("r000", 3), it.getTopKey());
		assertEquals("v3", it.getTopValue().toString());
		
		it.next();
		
		assertFalse(it.hasTop());
	}

	//test delete with same timestamp as existing key
	public void test3() throws IOException{
		TreeMap<Key, Value> tm = new TreeMap<Key, Value>();
		
		nkv(tm, "r000", 3, false, "v3");
		nkv(tm, "r000", 2, false, "v2");
		nkv(tm, "r000", 2, true, "");
		nkv(tm, "r000", 1, false, "v1");
		
		DeletingIterator it = new DeletingIterator(new SortedMapIterator<Key, Value>(tm), false);
		
		assertTrue(it.hasTop());
		assertEquals(nk("r000", 3), it.getTopKey());
		assertEquals("v3", it.getTopValue().toString());
		
		it.next();
		
		assertFalse(it.hasTop());
		
		it.seek(nk("r000", 2));
		
		assertFalse(it.hasTop());
	}
	
	private Key nk(String row, long ts){
		return new Key(new Text(row),ts);
	}
	
	private void nkv(TreeMap<Key, Value> tm, String row, long ts, boolean deleted, String val) {
		Key k = nk(row, ts);
		k.setDeleted(deleted);
		tm.put(k, new Value(val.getBytes()));
	}
}
