package cloudbase.core.tabletserver.iterators;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

import cloudbase.core.data.Key;
import cloudbase.core.data.Value;

public class VersioningIterator implements SortedKeyValueIterator<Key, Value>, OptionDescriber {
	private SortedKeyValueIterator<Key, Value> iterator;
	private Key currentKey = new Key();
	private int numVersions;
	private int maxVersions;

	public VersioningIterator(){}
	
	public VersioningIterator(SortedKeyValueIterator<Key, Value> iterator, int maxVersions) throws IOException{
		if (maxVersions < 1)
			throw new IllegalArgumentException("maxVersions for versioning iterator must be >= 1");
		this.iterator = iterator;
		this.maxVersions = maxVersions;
		resetVersionCount();
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
		if (numVersions >= maxVersions) {
			skipRowColumn();
			resetVersionCount();
			return;
		}
		
		iterator.next();
		if (iterator.hasTop()) {
			if (iterator.getTopKey().compareTo(currentKey, 4) == 0) {
				numVersions++;
			}
			else {
				resetVersionCount();
			}
		}
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
		resetVersionCount();
		
		while (iterator.hasTop() && iterator.getTopKey().compareTo(key, 5) < 0)
		{
			//the value has a more recent time stamp, so
			//pass it up
			//log.debug("skipping "+getTopKey());
			next();
		}
/*
		if(iterator.hasTop()) {
			System.out.println("key after seek "+iterator.getTopKey().toString());
		}
		else {
			System.out.println("no key after seek");
		}
*/
	}

	private void resetVersionCount() throws IOException {
		if (iterator.hasTop())
			currentKey.set(iterator.getTopKey());
		numVersions = 1;
	}
	
	private void skipRowColumn() throws IOException {
		Key keyToSkip = currentKey;
		iterator.next();
		
		while(iterator.hasTop() && iterator.getTopKey().compareTo(keyToSkip, 4) == 0){
			iterator.next();
		}
	}
	
	public void init(SortedKeyValueIterator<Key, Value> source, Map<String, String> options) throws IOException {
		this.iterator = source;
		this.numVersions = 0;
	
		String maxVerString = options.get("maxVersions");
		if(maxVerString != null)
			this.maxVersions = Integer.parseInt(maxVerString);
		else
			this.maxVersions = 1;
		
		if (maxVersions < 1)
			throw new IllegalArgumentException("maxVersions for versioning iterator must be >= 1");
		
		resetVersionCount();
		
	}
	
	@Override
	public IteratorOptions describeOptions() {
		return new IteratorOptions("vers",
				"The VersioningIterator keeps a fixed number of versions for each key",
				Collections.singletonMap("maxVersions", "number of versions to keep for a particular key (with differing timestamps)"),
				null);
	}
	
	@Override
	public boolean validateOptions(Map<String, String> options) {
		int i = Integer.parseInt(options.get("maxVersions"));
		if (i < 1)
			throw new IllegalArgumentException("maxVersions for versioning iterator must be >= 1");
		return true;
	}

	@Override
	public void setEndKey(Key key) {
		iterator.setEndKey(key);
	}
}
