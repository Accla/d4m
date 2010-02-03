package cloudbase.core.tabletserver.iterators;

import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

import org.apache.hadoop.io.Text;

import junit.framework.TestCase;

import cloudbase.core.conf.CBConfiguration;
import cloudbase.core.data.Value;
import cloudbase.core.data.Key;
import cloudbase.core.data.KeyExtent;
import cloudbase.core.tabletserver.iterators.IteratorUtil.IteratorScope;

public class IterUtilTest extends TestCase {
	
	static class WrappedIter implements SortedKeyValueIterator<Key, Value>{

		protected SortedKeyValueIterator<Key, Value> source;

		public Key getTopKey() {
			return source.getTopKey();
		}

		public Value getTopValue() {
			return source.getTopValue();
		}

		public boolean hasTop() {
			return source.hasTop();
		}

		public void init(SortedKeyValueIterator<Key, Value> source,
				Map<String, String> options) throws IOException {
			this.source = source;
		}

		public void next() throws IOException {
			source.next();
		}

		public void seek(Key key) throws IOException {
			source.seek(key);
		}

		@Override
		public void setEndKey(Key key) {
			source.setEndKey(key);
		}
	}
	
	static class AddingIter extends WrappedIter
	{
		
		int amount = 1;
		
		public Value getTopValue() {
			Value val = super.getTopValue();
			
			int orig = Integer.parseInt(val.toString());
			
			return new Value(((orig + amount)+"").getBytes());
		}
		
		public void init(SortedKeyValueIterator<Key, Value> source,
				Map<String, String> options) throws IOException {
			super.init(source, options);
			
			String amount = options.get("amount");
			
			if(amount != null){
				this.amount = Integer.parseInt(amount);
			}
		}
	}
	
	static class SquaringIter extends WrappedIter
	{
		public Value getTopValue() {
			Value val = super.getTopValue();
			
			int orig = Integer.parseInt(val.toString());
			
			return new Value(((orig * orig)+"").getBytes());
		}
	}
	
    public void test1() throws IOException{
		CBConfiguration conf = CBConfiguration.getInstance();
		conf.clear();
		
		//create an iterator that adds 1 and then squares
		conf.set(IteratorUtil.ITERATOR_PROPERTY+IteratorScope.minc.name()+".addIter", "1,"+AddingIter.class.getName());
		conf.set(IteratorUtil.ITERATOR_PROPERTY+IteratorScope.minc.name()+".sqIter", "2,"+SquaringIter.class.getName());
		
		TreeMap<Key, Value> tm = new TreeMap<Key, Value>();
		
		MultiIteratorTest.nkv(tm, 1, 0, false, "1");
		MultiIteratorTest.nkv(tm, 2, 0, false, "2");
		
		SortedMapIterator source = new SortedMapIterator(tm);
		
		SortedKeyValueIterator iter = IteratorUtil.loadIterators(IteratorScope.minc, source, new KeyExtent(new Text("tab"), null, null), conf);
		
		assertTrue(iter.hasTop());
		assertTrue(iter.getTopKey().equals(MultiIteratorTest.nk(1,0)));
		assertTrue(iter.getTopValue().toString().equals("4"));
		
		iter.next();
		
		assertTrue(iter.hasTop());
		assertTrue(iter.getTopKey().equals(MultiIteratorTest.nk(2,0)));
		assertTrue(iter.getTopValue().toString().equals("9"));
		
		iter.next();
		
		assertFalse(iter.hasTop());
	}
	
    public void test4() throws IOException{
		
		//try loading for a different scope
		
		CBConfiguration conf = CBConfiguration.getInstance();
		conf.clear();
		
		TreeMap<Key, Value> tm = new TreeMap<Key, Value>();
		
		MultiIteratorTest.nkv(tm, 1, 0, false, "1");
		MultiIteratorTest.nkv(tm, 2, 0, false, "2");
		
		SortedMapIterator source = new SortedMapIterator(tm);
		
		SortedKeyValueIterator iter = IteratorUtil.loadIterators(IteratorScope.majc, source, new KeyExtent(new Text("tab"), null, null), conf);
		
		assertTrue(iter.hasTop());
		assertTrue(iter.getTopKey().equals(MultiIteratorTest.nk(1,0)));
		assertTrue(iter.getTopValue().toString().equals("1"));
		
		iter.next();
		
		assertTrue(iter.hasTop());
		assertTrue(iter.getTopKey().equals(MultiIteratorTest.nk(2,0)));
		assertTrue(iter.getTopValue().toString().equals("2"));
		
		iter.next();
		
		assertFalse(iter.hasTop());
		
	}
	
    public void test3() throws IOException{
		//change the load order, so it squares and then adds
		
		CBConfiguration conf = CBConfiguration.getInstance();
		conf.clear();
		
		TreeMap<Key, Value> tm = new TreeMap<Key, Value>();
		
		MultiIteratorTest.nkv(tm, 1, 0, false, "1");
		MultiIteratorTest.nkv(tm, 2, 0, false, "2");
		
		SortedMapIterator source = new SortedMapIterator(tm);
		
		conf.set(IteratorUtil.ITERATOR_PROPERTY+IteratorScope.minc.name()+".addIter", "2,"+AddingIter.class.getName());
		conf.set(IteratorUtil.ITERATOR_PROPERTY+IteratorScope.minc.name()+".sqIter", "1,"+SquaringIter.class.getName());
		
		SortedKeyValueIterator iter = IteratorUtil.loadIterators(IteratorScope.minc, source, new KeyExtent(new Text("tab"), null, null), conf);
		
		assertTrue(iter.hasTop());
		assertTrue(iter.getTopKey().equals(MultiIteratorTest.nk(1,0)));
		assertTrue(iter.getTopValue().toString().equals("2"));
		
		iter.next();
		
		assertTrue(iter.hasTop());
		assertTrue(iter.getTopKey().equals(MultiIteratorTest.nk(2,0)));
		assertTrue(iter.getTopValue().toString().equals("5"));
		
		iter.next();
		
		assertFalse(iter.hasTop());
	}
	
    public void test2() throws IOException{
		CBConfiguration conf = CBConfiguration.getInstance();
		
		conf.clear();
		//create an iterator that adds 1 and then squares
		conf.set(IteratorUtil.ITERATOR_PROPERTY+IteratorScope.minc.name()+".addIter", "1,"+AddingIter.class.getName());
		conf.set(IteratorUtil.ITERATOR_PROPERTY+IteratorScope.minc.name()+".addIter.opt.amount", "7");
		conf.set(IteratorUtil.ITERATOR_PROPERTY+IteratorScope.minc.name()+".sqIter", "2,"+SquaringIter.class.getName());
		
		TreeMap<Key, Value> tm = new TreeMap<Key, Value>();
		
		MultiIteratorTest.nkv(tm, 1, 0, false, "1");
		MultiIteratorTest.nkv(tm, 2, 0, false, "2");
		
		SortedMapIterator source = new SortedMapIterator(tm);
		
		SortedKeyValueIterator iter = IteratorUtil.loadIterators(IteratorScope.minc, source, new KeyExtent(new Text("tab"), null, null), conf);
		
		assertTrue(iter.hasTop());
		assertTrue(iter.getTopKey().equals(MultiIteratorTest.nk(1,0)));
		assertTrue(iter.getTopValue().toString().equals("64"));
		
		iter.next();
		
		assertTrue(iter.hasTop());
		assertTrue(iter.getTopKey().equals(MultiIteratorTest.nk(2,0)));
		assertTrue(iter.getTopValue().toString().equals("81"));
		
		iter.next();
		
		assertFalse(iter.hasTop());
		
	}
	
	public void test5() throws IOException {
		CBConfiguration conf = CBConfiguration.getInstance();
		
		conf.clear();
		//create an iterator that ages off
		conf.set(IteratorUtil.ITERATOR_PROPERTY+IteratorScope.minc.name()+".filter", "1,"+FilteringIterator.class.getName());
		conf.set(IteratorUtil.ITERATOR_PROPERTY+IteratorScope.minc.name()+".filter.opt.0", "cloudbase.core.filter.AgeOffFilter");
		conf.set(IteratorUtil.ITERATOR_PROPERTY+IteratorScope.minc.name()+".filter.opt.0.ttl", "100");
		conf.set(IteratorUtil.ITERATOR_PROPERTY+IteratorScope.minc.name()+".filter.opt.0.currentTime", "1000");
		
		TreeMap<Key, Value> tm = new TreeMap<Key, Value>();
		
		MultiIteratorTest.nkv(tm, 1, 850, false, "1");
		MultiIteratorTest.nkv(tm, 2, 950, false, "2");
		
		SortedMapIterator source = new SortedMapIterator(tm);
		
		SortedKeyValueIterator iter = IteratorUtil.loadIterators(IteratorScope.minc, source, new KeyExtent(new Text("tab"), null, null), conf);
		
		assertTrue(iter.hasTop());
		assertTrue(iter.getTopKey().equals(MultiIteratorTest.nk(2,950)));
		iter.next();
		
		assertFalse(iter.hasTop());

	}
}
