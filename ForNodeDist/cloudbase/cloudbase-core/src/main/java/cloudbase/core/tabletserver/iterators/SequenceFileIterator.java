package cloudbase.core.tabletserver.iterators;

import java.io.IOException;
import java.util.Map;

import cloudbase.core.data.Value;
import cloudbase.core.data.Key;
import cloudbase.core.data.MySequenceFile;
import cloudbase.core.data.MySequenceFile.Reader;

public class SequenceFileIterator implements SortedKeyValueIterator<Key, Value> {

	private Reader reader;
	private Value top_value;
	private Key top_key;
	private boolean readValue;

	public SequenceFileIterator(MySequenceFile.Reader reader, boolean readValue) throws IOException{
		this.reader = reader;
		this.readValue = readValue;
		
		top_key = new Key();
		
		if(readValue)
			top_value = new Value();
		
		next();
	}
	
	public Key getTopKey() {
		return top_key;
	}

	public Value getTopValue() {
		return top_value;
	}

	public boolean hasTop() {
		return top_key != null;
	}

	public void next() throws IOException {
		boolean valid;
		if(readValue)
			valid = reader.next(top_key, top_value);
		else
			valid = reader.next(top_key);
		
		if(!valid){
			top_key = null;
			top_value = null;
		}
		
	}

	public void seek(Key seekKey) throws IOException {
		throw new UnsupportedOperationException("seek() not supported");
	}

	public void init(SortedKeyValueIterator<Key, Value> source,
			Map<String, String> options) throws IOException {
		throw new UnsupportedOperationException();
		
	}

	@Override
	public void setEndKey(Key key) {
		// TODO: support limiting the end key...
		//throw new UnsupportedOperationException("setEndKey() not supported");
	}

}
