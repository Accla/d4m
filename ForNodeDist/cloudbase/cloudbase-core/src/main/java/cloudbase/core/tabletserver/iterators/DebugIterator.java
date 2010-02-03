package cloudbase.core.tabletserver.iterators;

import java.io.IOException;
import java.util.Map;

import org.apache.log4j.Logger;

import cloudbase.core.data.Value;
import cloudbase.core.data.Key;

public class DebugIterator implements SortedKeyValueIterator<Key, Value> {
	
	private SortedKeyValueIterator<Key, Value> source;
	private String prefix;

	static Logger log = Logger.getLogger(DebugIterator.class.getName());
	
	public DebugIterator(String prefix, SortedKeyValueIterator<Key, Value> source){
		this.prefix = prefix;
		this.source = source;
	}
	
	public Key getTopKey() {
		Key wc = source.getTopKey();
		log.debug(prefix+" getTopKey() --> "+wc);
		return wc;
	}

	public Value getTopValue() {
		Value w = source.getTopValue();
		log.debug(prefix+" getTopValue() --> "+w);
		return w;
	}

	public boolean hasTop() {
		boolean b = source.hasTop();
		log.debug(prefix+" hasTop() --> "+b);
		return b;
	}

	public void next() throws IOException {
		source.next();
	}

	public void seek(Key key) throws IOException {
		log.debug(prefix+" seek("+key+")");
		source.seek(key);
	}

	public void init(SortedKeyValueIterator<Key, Value> source, Map<String, String> options) {
		this.source = source;
	}

	@Override
	public void setEndKey(Key key) {
		source.setEndKey(key);
	}

}
