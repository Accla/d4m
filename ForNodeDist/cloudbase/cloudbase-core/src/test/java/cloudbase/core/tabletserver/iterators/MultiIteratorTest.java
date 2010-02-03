package cloudbase.core.tabletserver.iterators;

import java.io.IOException;
import java.util.TreeMap;

import junit.framework.TestCase;

import org.apache.hadoop.io.Text;

import cloudbase.core.data.Value;
import cloudbase.core.data.Key;

public class MultiIteratorTest extends TestCase {
	static Key nk(int row, long ts){
		return new Key(nr(row),ts);
	}
	
	static void nkv(TreeMap<Key, Value> tm, int row, long ts, boolean deleted, String val) {
		Key k = nk(row, ts);
		k.setDeleted(deleted);
		tm.put(k, new Value(val.getBytes()));
	}
	
	static Text nr(int row){
		return new Text(String.format("r%03d", row));
	}
	
	void verify(int start, int end, Key seekKey, Text endRow, Text prevEndRow, boolean init, boolean incrRow, TreeMap<Key, Value>... maps) throws IOException{
		SortedKeyValueIterator iters[] = new SortedKeyValueIterator[maps.length];
		
		for (int i = 0; i < iters.length; i++) {
			iters[i] = new SortedMapIterator(maps[i]);
		}
		
		MultiIterator mi = new MultiIterator(iters, endRow, prevEndRow, init);
		
		if(seekKey != null){
			mi.seek(seekKey);
		}
		
		int i = start;
		while(mi.hasTop()){
			if(incrRow)
				assertEquals(nk(i, 0), mi.getTopKey());
			else
				assertEquals(nk(0, i), mi.getTopKey());
			
			assertEquals("v"+i, mi.getTopValue().toString());
			
			mi.next();
			if(incrRow) 
				i++;
			else
				i--;
		}
		
		assertEquals(end, i);
	}
	
	void verify(int start, Key seekKey, TreeMap<Key, Value>... maps) throws IOException{
		if(seekKey != null){
			verify(start, -1, seekKey, null, null, false, false, maps);
		}
		
		verify(start, -1, seekKey, null, null, true, false, maps);
	}
	
	void verify(int start, int end, Key seekKey, Text endRow, Text prevEndRow, TreeMap<Key, Value>... maps) throws IOException{
		if(seekKey != null){
			verify(start, end, seekKey, endRow, prevEndRow, false, false, maps);
		}
		
		verify(start, end, seekKey, endRow, prevEndRow, true, false, maps);
	}
	
	public void test1() throws IOException{
		//TEST non overlapping inputs
		
		TreeMap<Key, Value> tm1 = new TreeMap<Key, Value>();
		TreeMap<Key, Value> tm2 = new TreeMap<Key, Value>();
		
		for(int i = 0; i < 4; i++){
			nkv(tm1, 0, i, false, "v"+i);
		}
		
		for(int i = 4; i < 8; i++){
			nkv(tm2, 0, i, false, "v"+i);
		}
		
		for(int seek = -1; seek < 8; seek++){
			if(seek == 7){
				verify(seek, null, tm1, tm2);
			}
			verify(seek, nk(0, seek), tm1, tm2);
		}
	}
	
	public void test2() throws IOException{
		//TEST overlapping inputs
		
		TreeMap<Key, Value> tm1 = new TreeMap<Key, Value>();
		TreeMap<Key, Value> tm2 = new TreeMap<Key, Value>();
		
		for(int i = 0; i < 8; i++){
			if(i % 2 == 0)
				nkv(tm1, 0, i, false, "v"+i);
			else
				nkv(tm2, 0, i, false, "v"+i);
		}
		
		for(int seek = -1; seek < 8; seek++){
			if(seek == 7){
				verify(seek, null, tm1, tm2);
			}
			verify(seek, nk(0, seek), tm1, tm2);
		}
	}
	
	public void test3() throws IOException{
		//TEST single input
		
		TreeMap<Key, Value> tm1 = new TreeMap<Key, Value>();

		for(int i = 0; i < 8; i++){	
			nkv(tm1, 0, i, false, "v"+i);
		}
		
		for(int seek = -1; seek < 8; seek++){
			if(seek == 7){
				verify(seek, null, tm1);
			}
			verify(seek, nk(0, seek), tm1);
		}
	}
	
	public void test4() throws IOException{
		//TEST empty input
		
		TreeMap<Key, Value> tm1 = new TreeMap<Key, Value>();

		
		MultiIterator mi = new MultiIterator(new SortedKeyValueIterator[]{new SortedMapIterator(tm1)}, true);
		
		assertFalse(mi.hasTop());
		
		mi.seek(nk(0, 6));
		assertFalse(mi.hasTop());
	}
	
	public void test5() throws IOException{
		//TEST overlapping inputs AND prevRow AND endRow AND seek
		
		TreeMap<Key, Value> tm1 = new TreeMap<Key, Value>();
		TreeMap<Key, Value> tm2 = new TreeMap<Key, Value>();
		
		for(int i = 0; i < 8; i++){
			if(i % 2 == 0)
				nkv(tm1, i, 0, false, "v"+i);
			else
				nkv(tm2, i, 0, false, "v"+i);
		}
		
		for(int seek = -1; seek < 9; seek++){
			verify(Math.max(0,seek), 8, nk(seek,0), null, null, true, true, tm1, tm2);
			verify(Math.max(0,seek), 8, nk(seek,0), null, null, false, true, tm1, tm2);
			
			for(int er = -1; er < 10; er++){
				
				int end = seek > er ? seek : Math.min(er + 1, 8);
				
				//System.out.println("seek = "+seek+"  er = "+er+"  end = "+end);
				
				int noSeekEnd = Math.min(er+1, 8);;
				if(er < 0){
					noSeekEnd = 0;
				}
				
				verify(0, noSeekEnd, null, nr(er), null, true, true, tm1, tm2);
				verify(Math.max(0,seek), end, nk(seek,0), nr(er), null, true, true, tm1, tm2);
				verify(Math.max(0,seek), end, nk(seek,0), nr(er), null, false, true, tm1, tm2);
				
				for(int per = -2; per < er; per++){
					
					int start = Math.max(per + 1, seek);
					
					if(start > er)
						end = start;
					
					if(per >= 8)
						end = start;
					
					
					//System.out.println("seek = "+seek+"  er = "+er+"  per = "+per+"  start = "+start+"  end = "+end);
					
					int noSeekStart = Math.max(0,per + 1);
					
					if(er < 0 || per >= 7){
						noSeekEnd = noSeekStart;
					}
					
					//System.out.println("noSeekStart = "+noSeekStart+"  noSeekEnd = "+noSeekEnd);
					
					verify(noSeekStart, noSeekEnd, null, nr(er), nr(per), true, true, tm1, tm2);
					verify(Math.max(0,start), end, nk(seek,0), nr(er), nr(per), true, true, tm1, tm2);
					verify(Math.max(0,start), end, nk(seek,0), nr(er), nr(per), false, true, tm1, tm2);
				}
			}
		}	
	}
	
	public void test6() throws IOException{
		//TEst setting an endKey
		TreeMap<Key, Value> tm1 = new TreeMap<Key, Value>();
		nkv(tm1, 3, 0, false, "1");
		nkv(tm1, 4, 0, false, "2");
		nkv(tm1, 6, 0, false, "3");
		
		MultiIterator mi = new MultiIterator(new SortedKeyValueIterator[]{new SortedMapIterator(tm1)}, null, null, nk(5,9), true);
		
		assertTrue(mi.hasTop());
		assertTrue(mi.getTopKey().equals(nk(3,0)));
		assertTrue(mi.getTopValue().toString().equals("1"));
		mi.next();
		
		assertTrue(mi.hasTop());
		assertTrue(mi.getTopKey().equals(nk(4,0)));
		assertTrue(mi.getTopValue().toString().equals("2"));
		mi.next();
		
		assertFalse(mi.hasTop());
		
		mi.seek(nk(5, 10));
		assertFalse(mi.hasTop());
		
		mi.seek(nk(4, 10));
		assertTrue(mi.hasTop());
		assertTrue(mi.getTopKey().equals(nk(4,0)));
		assertTrue(mi.getTopValue().toString().equals("2"));
		mi.next();
		
		assertFalse(mi.hasTop());
	}
}
