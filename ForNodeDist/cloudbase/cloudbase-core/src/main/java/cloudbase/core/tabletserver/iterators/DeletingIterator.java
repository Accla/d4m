package cloudbase.core.tabletserver.iterators;

import java.io.IOException;
import java.util.Map;

import cloudbase.core.data.Value;
import cloudbase.core.data.Key;

public class DeletingIterator implements SortedKeyValueIterator<Key, Value> {
	private SortedKeyValueIterator<Key, Value> iterator;
	private boolean propogateDeletes;
	private Key workKey = new Key();

	public DeletingIterator(){}
	
	public DeletingIterator(SortedKeyValueIterator<Key, Value> iterator, 
			boolean propogateDeletes) throws IOException{
		this.iterator = iterator;
		this.propogateDeletes = propogateDeletes;
		
		findTop();
	}
	
	public Key getTopKey() {
		return iterator.getTopKey();
	}

	public Value getTopValue() {
		return iterator.getTopValue();
	}

	public boolean hasTop() {
		return iterator.hasTop();
	}

	public void next() throws IOException {
		if (iterator.getTopKey().isDeleted())
			skipRowColumn();
		else
			iterator.next();
		findTop();
	}

	public void seek(Key key) throws IOException {
		// do not want to seek to the middle of a row
		Key seekKey;
		if (key.getTimestamp()==Long.MAX_VALUE) {
			seekKey = key;
		}
		else {
			seekKey = new Key(key);
			seekKey.setTimestamp(Long.MAX_VALUE);
		}
		
		iterator.seek(seekKey);
		findTop();
		
		while (iterator.hasTop() && iterator.getTopKey().compareTo(key, 5) < 0) {
			next();
		}
	}
	
	private void findTop() throws IOException {
		if(!propogateDeletes){
			while(iterator.hasTop() && iterator.getTopKey().isDeleted()){
				skipRowColumn();
			}
		}
	}
	
	private void skipRowColumn() throws IOException {
		//TODO avoid copy
		workKey.set(iterator.getTopKey());
		
		Key keyToSkip = workKey;
		iterator.next();
		
		while(iterator.hasTop() && iterator.getTopKey().compareTo(keyToSkip, 4) == 0){
			iterator.next();
		}
	}
	
	public void init(SortedKeyValueIterator<Key, Value> source, Map<String, String> options) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setEndKey(Key key) {
		iterator.setEndKey(key);
	}
}
