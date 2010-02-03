package cloudbase.core.tabletserver.iterators;

import java.io.IOException;
import java.util.Map;

import cloudbase.core.data.Key;
import cloudbase.core.data.Value;

public class CountingIterator implements SortedKeyValueIterator<Key, Value> {

	private SortedKeyValueIterator<Key, Value> source;
	private long count;
	
	public CountingIterator(SortedKeyValueIterator<Key, Value> source){
		this.source = source;
		count = 0;
	}

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
		throw new UnsupportedOperationException();
	}

	public void next() throws IOException {
		source.next();
		count++;
	}

	public void seek(Key key) throws IOException {
		source.seek(key);
	}
	
	public long getCount(){
		return count;
	}

	@Override
	public void setEndKey(Key key) {
		source.setEndKey(key);		
	}
	
}
