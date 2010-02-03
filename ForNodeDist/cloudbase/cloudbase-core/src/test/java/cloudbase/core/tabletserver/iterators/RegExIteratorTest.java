package cloudbase.core.tabletserver.iterators;

import java.io.IOException;
import java.util.HashMap;
import java.util.TreeMap;

import org.apache.hadoop.io.Text;

import cloudbase.core.data.Key;
import cloudbase.core.data.Value;
import cloudbase.core.filter.RegExFilter;
import junit.framework.TestCase;

public class RegExIteratorTest extends TestCase {
	
	private Key nkv(TreeMap<Key, Value> tm, String row, String cf, String cq, String val) {
		Key k = nk(row, cf, cq);
		tm.put(k, new Value(val.getBytes()));
		return k;
	} 
	
	private Key nk(String row, String cf, String cq) {
		return new Key(new Text(row), new Text(cf), new Text(cq));
	}

	public void test1() throws IOException{
		TreeMap<Key, Value> tm = new TreeMap<Key, Value>();
		
		Key k1 = nkv(tm, "boo1","yup", "20080201","dog");
		Key k2 = nkv(tm, "boo1","yap", "20080202","cat");
		Key k3 = nkv(tm, "boo2","yip", "20080203","hamster");
		
		RegExIterator rei = new RegExIterator();
		
		HashMap<String, String> options = new HashMap<String, String>();
		
		options.put(RegExFilter.ROW_REGEX, ".*2");
		rei.init(new SortedMapIterator<Key, Value>(tm), options);
		
		assertTrue(rei.hasTop());
		assertTrue(rei.getTopKey().equals(k3));
		rei.next();
		assertFalse(rei.hasTop());
		
		//-----------------------------------------------------
		options.clear();
		
		options.put(RegExFilter.COLF_REGEX, "ya.*");
		rei.init(new SortedMapIterator<Key, Value>(tm), options);
		
		assertTrue(rei.hasTop());
		assertTrue(rei.getTopKey().equals(k2));
		rei.next();
		assertFalse(rei.hasTop());
		
		//-----------------------------------------------------
		options.clear();
		
		options.put(RegExFilter.COLQ_REGEX, ".*01");
		rei.init(new SortedMapIterator<Key, Value>(tm), options);
		
		assertTrue(rei.hasTop());
		assertTrue(rei.getTopKey().equals(k1));
		rei.next();
		assertFalse(rei.hasTop());
		
		//-----------------------------------------------------
		options.clear();
		
		options.put(RegExFilter.VALUE_REGEX, ".*at");
		rei.init(new SortedMapIterator<Key, Value>(tm), options);
		
		assertTrue(rei.hasTop());
		assertTrue(rei.getTopKey().equals(k2));
		rei.next();
		assertFalse(rei.hasTop());
		
		
		//-----------------------------------------------------
		options.clear();
		
		options.put(RegExFilter.VALUE_REGEX, ".*ap");
		rei.init(new SortedMapIterator<Key, Value>(tm), options);
		
		assertFalse(rei.hasTop());
		
		//-----------------------------------------------------
		options.clear();
		
		options.put(RegExFilter.COLF_REGEX, "ya.*");
		options.put(RegExFilter.VALUE_REGEX, ".*at");
		rei.init(new SortedMapIterator<Key, Value>(tm), options);
		
		assertTrue(rei.hasTop());
		assertTrue(rei.getTopKey().equals(k2));
		rei.next();
		assertFalse(rei.hasTop());
		
		//-----------------------------------------------------
		options.clear();
		
		options.put(RegExFilter.COLF_REGEX, "ya.*");
		options.put(RegExFilter.VALUE_REGEX, ".*ap");
		rei.init(new SortedMapIterator<Key, Value>(tm), options);
		
		assertFalse(rei.hasTop());
		
		//-----------------------------------------------------
		options.clear();
		
		options.put(RegExFilter.ROW_REGEX, "boo1");
		rei.init(new SortedMapIterator<Key, Value>(tm), options);
		
		assertTrue(rei.hasTop());
		assertTrue(rei.getTopKey().equals(k2));
		rei.next();
		assertTrue(rei.hasTop());
		assertTrue(rei.getTopKey().equals(k1));
		rei.next();
		assertFalse(rei.hasTop());
		
		//-----------------------------------------------------
		options.clear();
		
		rei.init(new SortedMapIterator<Key, Value>(tm), options);
		
		assertTrue(rei.hasTop());
		assertTrue(rei.getTopKey().equals(k2));
		rei.next();
		assertTrue(rei.hasTop());
		assertTrue(rei.getTopKey().equals(k1));
		rei.next();
		assertTrue(rei.hasTop());
		assertTrue(rei.getTopKey().equals(k3));
		rei.next();
		assertFalse(rei.hasTop());
	}

	public void test2() throws IOException{
		TreeMap<Key, Value> tm = new TreeMap<Key, Value>();
		
		Key k1 = nkv(tm, "boo1","yup", "20080201","dog");
		Key k2 = nkv(tm, "boo1","yap", "20080202","cat");
		Key k3 = nkv(tm, "boo2","yip", "20080203","hamster");
		
		FilteringIterator rei = new FilteringIterator();
		
		HashMap<String, String> options = new HashMap<String, String>();
		
		options.put("0", RegExFilter.class.getName());
		options.put("0."+RegExFilter.ROW_REGEX, ".*2");
		rei.init(new SortedMapIterator<Key, Value>(tm), options);
		
		assertTrue(rei.hasTop());
		assertTrue(rei.getTopKey().equals(k3));
		rei.next();
		assertFalse(rei.hasTop());
		
		//-----------------------------------------------------
		options.clear();
		
		options.put("0", RegExFilter.class.getName());
		options.put("0."+RegExFilter.COLF_REGEX, "ya.*");
		rei.init(new SortedMapIterator<Key, Value>(tm), options);
		
		assertTrue(rei.hasTop());
		assertTrue(rei.getTopKey().equals(k2));
		rei.next();
		assertFalse(rei.hasTop());
		
		//-----------------------------------------------------
		options.clear();
		
		options.put("0", RegExFilter.class.getName());
		options.put("0."+RegExFilter.COLQ_REGEX, ".*01");
		rei.init(new SortedMapIterator<Key, Value>(tm), options);
		
		assertTrue(rei.hasTop());
		assertTrue(rei.getTopKey().equals(k1));
		rei.next();
		assertFalse(rei.hasTop());
		
		//-----------------------------------------------------
		options.clear();
		
		options.put("0", RegExFilter.class.getName());
		options.put("0."+RegExFilter.VALUE_REGEX, ".*at");
		rei.init(new SortedMapIterator<Key, Value>(tm), options);
		
		assertTrue(rei.hasTop());
		assertTrue(rei.getTopKey().equals(k2));
		rei.next();
		assertFalse(rei.hasTop());
		
		
		//-----------------------------------------------------
		options.clear();
		
		options.put("0", RegExFilter.class.getName());
		options.put("0."+RegExFilter.VALUE_REGEX, ".*ap");
		rei.init(new SortedMapIterator<Key, Value>(tm), options);
		
		assertFalse(rei.hasTop());
		
		//-----------------------------------------------------
		options.clear();
		
		options.put("0", RegExFilter.class.getName());
		options.put("0."+RegExFilter.COLF_REGEX, "ya.*");
		options.put("0."+RegExFilter.VALUE_REGEX, ".*at");
		rei.init(new SortedMapIterator<Key, Value>(tm), options);
		
		assertTrue(rei.hasTop());
		assertTrue(rei.getTopKey().equals(k2));
		rei.next();
		assertFalse(rei.hasTop());
		
		//-----------------------------------------------------
		options.clear();
		
		options.put("0", RegExFilter.class.getName());
		options.put("0."+RegExFilter.COLF_REGEX, "ya.*");
		options.put("0."+RegExFilter.VALUE_REGEX, ".*ap");
		rei.init(new SortedMapIterator<Key, Value>(tm), options);
		
		assertFalse(rei.hasTop());
		
		//-----------------------------------------------------
		options.clear();
		
		options.put("0", RegExFilter.class.getName());
		options.put("0."+RegExFilter.ROW_REGEX, "boo1");
		rei.init(new SortedMapIterator<Key, Value>(tm), options);
		
		assertTrue(rei.hasTop());
		assertTrue(rei.getTopKey().equals(k2));
		rei.next();
		assertTrue(rei.hasTop());
		assertTrue(rei.getTopKey().equals(k1));
		rei.next();
		assertFalse(rei.hasTop());
		
		//-----------------------------------------------------
		options.clear();
		
		rei.init(new SortedMapIterator<Key, Value>(tm), options);
		
		assertTrue(rei.hasTop());
		assertTrue(rei.getTopKey().equals(k2));
		rei.next();
		assertTrue(rei.hasTop());
		assertTrue(rei.getTopKey().equals(k1));
		rei.next();
		assertTrue(rei.hasTop());
		assertTrue(rei.getTopKey().equals(k3));
		rei.next();
		assertFalse(rei.hasTop());
	}

}
