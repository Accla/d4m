package cloudbase.core.tabletserver.iterators;

import java.io.IOException;
import java.util.Map;

import org.apache.log4j.Logger;

import cloudbase.core.data.Key;
import cloudbase.core.data.Value;

public class TimeLimitIterator implements SortedKeyValueIterator<Key, Value> {

	static Logger log = Logger.getLogger(TimeLimitIterator.class.getName());
	
	private SortedKeyValueIterator<Key, Value> source;
	private long limit;
	private long start;
	private boolean hitLimit;
	private int count = 0;
	private int minScan;
	
	private Key lastKey = new Key();
	
	private void resetLimitCheck() {
		start = System.currentTimeMillis();
		hitLimit = false;
		count = 0;
		
		if(source.hasTop()){
			lastKey.set(source.getTopKey());
		}
	}
	
	public TimeLimitIterator(long timeLimit, int minScan){
		
		if(minScan < 1){
			throw new IllegalArgumentException();
		}
		
		hitLimit = false;
		this.limit = timeLimit;
		this.minScan = minScan - 1;
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
		return !hitLimit && source.hasTop();
	}

	@Override
	public void init(SortedKeyValueIterator<Key, Value> source, Map<String, String> options) throws IOException {
		this.source = source;
		resetLimitCheck();
	}

	@Override
	public void next() throws IOException {
		
		if(hitLimit){
			throw new IllegalStateException();
		}
		
		source.next();
		
		if(source.hasTop()){
			boolean keyChanged = lastKey.compareTo(source.getTopKey(), 4) != 0;
			
			long ct = System.currentTimeMillis();
			if(ct - start >= limit && count >= minScan && keyChanged){
				hitLimit = true;
			}
			
			if(keyChanged){
				lastKey.set(source.getTopKey());
				count++;
			}	
		}
	}

	@Override
	public void seek(Key key) throws IOException {
		source.seek(key);
		resetLimitCheck();
	}

	@Override
	public void setEndKey(Key key) {
		source.setEndKey(key);
	}

	public SortedKeyValueIterator<Key, Value> getSource(){
		return source;
	}
	
	public boolean hitLimit(){
		return hitLimit;
	}
	
	public void setLimit(long limit){
		this.limit = limit;
	}
}
