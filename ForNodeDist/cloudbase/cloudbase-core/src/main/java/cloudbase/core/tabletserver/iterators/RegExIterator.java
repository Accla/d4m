package cloudbase.core.tabletserver.iterators;

import java.io.IOException;
import java.util.Map;

import cloudbase.core.data.Key;
import cloudbase.core.data.Value;
import cloudbase.core.filter.RegExFilter;

public class RegExIterator implements SortedKeyValueIterator<Key, Value>, OptionDescriber {
	
	private SortedKeyValueIterator<Key, Value> source;
	
	private RegExFilter ref = new RegExFilter();
	
	private boolean matches(Key key, Value value){
		return ref.accept(key, value);			
	}
	
	private void consumeNonMatches() throws IOException{
		while(source.hasTop() && !matches(source.getTopKey(), source.getTopValue())){
			source.next();
		}
	}
	
	@Override
	public Key getTopKey() {
		return source.getTopKey();
	}
	
	@Override
	public Value getTopValue() {
		return source.getTopValue();
	}
	
	@Override
	public boolean hasTop() {
		return source.hasTop();
	}
	
	@Override
	public void init(SortedKeyValueIterator<Key, Value> source, Map<String, String> options) throws IOException {
		this.source = source;
		ref.init(options);		
		consumeNonMatches();
	}
	
	@Override
	public void next() throws IOException {
		source.next();
		consumeNonMatches();
	}
	
	@Override
	public void seek(Key key) throws IOException {
		source.seek(key);
		consumeNonMatches();
	}
	
	@Override
	public IteratorOptions describeOptions() {
		return ref.describeOptions();
	}
	
	@Override
	public boolean validateOptions(Map<String, String> options) {
		return ref.validateOptions(options);
	}

	@Override
	public void setEndKey(Key key) {
		source.setEndKey(key);
	}
	
}
