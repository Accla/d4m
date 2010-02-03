package cloudbase.core.tabletserver.iterators;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.TreeMap;

import junit.framework.TestCase;

import org.apache.hadoop.io.Text;

import cloudbase.core.data.Column;
import cloudbase.core.data.Key;
import cloudbase.core.data.Value;
import cloudbase.core.filter.AgeOffFilter;
import cloudbase.core.filter.ColumnFilter;
import cloudbase.core.filter.Filter;
import cloudbase.core.filter.VisibilityFilter;
import cloudbase.core.security.LabelConversions;
import cloudbase.core.security.LabelExpression;

public class FilteringIteratorTest extends TestCase {
	
	public class SimpleFilter implements Filter {
		public boolean accept(Key k, Value v) {
			if (k.getRow().toString().endsWith("0"))
				return true;
			return false;
		}

		@Override
		public void init(Map<String, String> options) {
			// TODO Auto-generated method stub
			
		}
	}

	public class SimpleFilter2 implements Filter {
		public boolean accept(Key k, Value v) {
			if (k.getColumnFamily().toString().equals("a"))
				return false;
			return true;
		}

		@Override
		public void init(Map<String, String> options) {
			// TODO Auto-generated method stub
			
		}
	}

	public void test1() {
		Text colf = new Text("a");
		Text colq = new Text("b");
		Value dv = new Value();
		TreeMap<Key, Value> tm = new TreeMap<Key, Value>();
		ArrayList<Filter> f = new ArrayList<Filter>();
		f.add(new SimpleFilter());
		
		for (int i = 0; i < 1000; i++) {
			Key k = new Key(new Text(String.format("%03d", i)), colf, colq);
			tm.put(k, dv);
		}
		assertTrue(tm.size()==1000);
		
		try {
			FilteringIterator fi = new FilteringIterator(new SortedMapIterator<Key, Value>(tm), f);
			TreeMap<Key, Value> tmOut = new TreeMap<Key, Value>();
			while (fi.hasTop()) {
				tmOut.put(fi.getTopKey(), fi.getTopValue());
				fi.next();
			}
			assertTrue(tmOut.size()==100);
		}
		catch (IOException e) {
			assertFalse(true);
		}

		try {
			FilteringIterator fi = new FilteringIterator(new SortedMapIterator<Key, Value>(tm), f);
			Key k = new Key(new Text("500"));
			fi.seek(k);
			TreeMap<Key, Value> tmOut = new TreeMap<Key, Value>();
			while (fi.hasTop()) {
				tmOut.put(fi.getTopKey(), fi.getTopValue());
				fi.next();
			}
			assertTrue(tmOut.size()==50);
		}
		catch (IOException e) {
			assertFalse(true);
		}
		
		f.add(new SimpleFilter2());
		try {
			FilteringIterator fi = new FilteringIterator(new SortedMapIterator<Key, Value>(tm), f);
			TreeMap<Key, Value> tmOut = new TreeMap<Key, Value>();
			while (fi.hasTop()) {
				tmOut.put(fi.getTopKey(), fi.getTopValue());
				fi.next();
			}
			assertTrue("tmOut wasn't empty "+tmOut.size(),tmOut.isEmpty());
		}
		catch (IOException e) {
			assertFalse(true);
		}
	}
	
	public void test2() {
		Text colf = new Text("a");
		Text colq = new Text("b");
		Value dv = new Value();
		TreeMap<Key, Value> tm = new TreeMap<Key, Value>();
		ArrayList<Filter> f = new ArrayList<Filter>();
		AgeOffFilter a = new AgeOffFilter();
		HashMap<String,String> options = new HashMap<String,String>();
		options.put("ttl", "101");
		options.put("currentTime", "1001");
		a.init(options);
		f.add(a);
		
		for (int i = 0; i < 1000; i++) {
			Key k = new Key(new Text(String.format("%03d", i)), colf, colq);
			k.setTimestamp(i);
			tm.put(k, dv);
		}
		assertTrue(tm.size()==1000);
		
		try {
			FilteringIterator fi = new FilteringIterator(new SortedMapIterator<Key, Value>(tm), f);
			TreeMap<Key, Value> tmOut = new TreeMap<Key, Value>();
			while (fi.hasTop()) {
				tmOut.put(fi.getTopKey(), fi.getTopValue());
				fi.next();
			}
			assertTrue(tmOut.size()==100);
		}
		catch (IOException e) {
			assertFalse(true);
		}
	}

	public void test3() {
		Value dv = new Value();
		TreeMap<Key, Value> tm = new TreeMap<Key, Value>();
		ArrayList<Filter> f = new ArrayList<Filter>();
		HashSet<Column> hsc = new HashSet<Column>();
		hsc.add(new Column("c".getBytes(),new byte[0],new byte[0]));
		ColumnFilter a = new ColumnFilter(hsc);
		f.add(a);
		
		Text colf1 = new Text("a");
		Text colq1 = new Text("b");
		Text colf2 = new Text("c");
		Text colq2 = new Text("d");
		Text colf;
		Text colq;
		for (int i = 0; i < 1000; i++) {
			if (Math.ceil(i/2.0)==i/2.0){
				colf = colf1;
				colq = colq1;
			}else{
				colf = colf2;
				colq = colq2;
			}
			Key k = new Key(new Text(String.format("%03d", i)), colf ,colq);
			k.setTimestamp(157l);
			tm.put(k, dv);
		}
		assertTrue(tm.size()==1000);
		
		try {
			FilteringIterator fi = new FilteringIterator(new SortedMapIterator<Key, Value>(tm), f);
			TreeMap<Key, Value> tmOut = new TreeMap<Key, Value>();
			while (fi.hasTop()) {
				tmOut.put(fi.getTopKey(), fi.getTopValue());
				fi.next();
			}
			assertTrue(tmOut.size()==500);
		}
		catch (IOException e) {
			assertFalse(true);
		}
		
		f = new ArrayList<Filter>();
		hsc = new HashSet<Column>();
		hsc.add(new Column("a".getBytes(),"b".getBytes(),null));
		a = new ColumnFilter(hsc);
		f.add(a);
		try {
			FilteringIterator fi = new FilteringIterator(new SortedMapIterator<Key, Value>(tm), f);
			TreeMap<Key, Value> tmOut = new TreeMap<Key, Value>();
			while (fi.hasTop()) {
				tmOut.put(fi.getTopKey(), fi.getTopValue());
				fi.next();
			}
			assertTrue("size was "+tmOut.size(),tmOut.size()==500);
		}
		catch (IOException e) {
			assertFalse(true);
		}

		f = new ArrayList<Filter>();
		hsc = new HashSet<Column>();
		a = new ColumnFilter(hsc);
		f.add(a);
		try {
			FilteringIterator fi = new FilteringIterator(new SortedMapIterator<Key, Value>(tm), f);
			TreeMap<Key, Value> tmOut = new TreeMap<Key, Value>();
			while (fi.hasTop()) {
				tmOut.put(fi.getTopKey(), fi.getTopValue());
				fi.next();
			}
			assertTrue("size was "+tmOut.size(),tmOut.size()==1000);
		}
		catch (IOException e) {
			assertFalse(true);
		}
}

	public void test4() {
		Value dv = new Value();
		TreeMap<Key, Value> tm = new TreeMap<Key, Value>();
		ArrayList<Filter> f = new ArrayList<Filter>();
		short[] sa1 = {1,3,5};
		short[] sa2 = {3,5};
		short[] sa3 = {1,6};
		short[] sa4 = new short[0];
		LabelExpression le1 = new LabelExpression(sa1);
		LabelExpression le2 = new LabelExpression(sa2);
		LabelExpression le3 = new LabelExpression(sa3);
		LabelExpression le4 = new LabelExpression(sa4);
		LabelExpression[] lea = {le1, le2, le3, le4};
		VisibilityFilter a = new VisibilityFilter(LabelConversions.formatAuthorizations(sa1), le2.toByteArray());
		f.add(a);
		
		for (int i = 0; i < 1000; i++) {
			Key k = new Key(new Text(String.format("%03d", i)), new Text("a"), new Text("b"), lea[i%4].toByteArray());
			tm.put(k, dv);
		}
		assertTrue(tm.size()==1000);
		
		try {
			FilteringIterator fi = new FilteringIterator(new SortedMapIterator<Key, Value>(tm), f);
			TreeMap<Key, Value> tmOut = new TreeMap<Key, Value>();
			while (fi.hasTop()) {
				tmOut.put(fi.getTopKey(), fi.getTopValue());
				fi.next();
			}
			assertTrue("size was "+tmOut.size(),tmOut.size()==750);
		}
		catch (IOException e) {
			assertFalse(true);
		}
	}

}
