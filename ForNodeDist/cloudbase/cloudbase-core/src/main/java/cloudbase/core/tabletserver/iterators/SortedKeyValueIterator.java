package cloudbase.core.tabletserver.iterators;

import java.io.IOException;
import java.util.Map;

import org.apache.hadoop.io.Writable;
import org.apache.hadoop.io.WritableComparable;

/**
 * An iterator that support iterating over key and value pairs.  Anything
 * implementing this interface should return keys in sorted order.
 * 
 */

public interface SortedKeyValueIterator <K extends WritableComparable<?>, V extends Writable>{
	
	void init(SortedKeyValueIterator<K, V> source, Map<String, String> options) throws IOException;
	
	boolean hasTop();
	void next() throws IOException;
	void seek(K key) throws IOException;
	/**
	 * setEndKey is just a suggestion -- iterators may return keys that are past the given key
	 * @param key
	 */
	void setEndKey(K key);
	K getTopKey();
	V getTopValue();
}
