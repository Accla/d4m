package cloudbase.core.tabletserver.iterators;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedMap;
import java.util.Map.Entry;

import org.apache.hadoop.io.Writable;
import org.apache.hadoop.io.WritableComparable;

/**
 * A simple iterator over a Java SortedMap
 * 
 */

public class SortedMapIterator<K extends WritableComparable<?>, V extends Writable> implements SortedKeyValueIterator<K, V>{
	private Iterator<Entry<K, V>> iter;
	private Entry<K, V> entry;

	private SortedMap<K,V> map;
	
	public SortedMapIterator(SortedMap<K, V> map){
		this.map = map;
		iter = map.entrySet().iterator();
		if(iter.hasNext())
			entry = iter.next();
		else
			entry = null;
	}

	public K getTopKey() {
		return entry.getKey();
	}

	public V getTopValue() {
		return entry.getValue();
	}

	public boolean hasTop() {
		return entry != null;
	}

	public void next() throws IOException {
		if(iter.hasNext())
			entry = iter.next();
		else
			entry = null;
		
	}

	public void seek(K key) throws IOException {
		//if(skipKey)
		//	System.out.println("SortedMapIterator: Seeking after "+key);
		//else
		//	System.out.println("SortedMapIterator: Seeking to "+key);
		iter = map.tailMap(key).entrySet().iterator();
		if(iter.hasNext())
			entry = iter.next();
		else
			entry = null;
		//System.out.println("SortedMapIterator: First entry after seek: "+entry);
	}

	public void init(SortedKeyValueIterator<K, V> source,
			Map<String, String> options) throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setEndKey(K key) {
		// TODO: support an end key limit
		//throw new UnsupportedOperationException("setEndKey() not supported");
	}
}
