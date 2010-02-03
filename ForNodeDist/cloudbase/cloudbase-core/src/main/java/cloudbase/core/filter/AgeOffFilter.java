package cloudbase.core.filter;

import java.util.Collections;
import java.util.Map;

import cloudbase.core.data.Key;
import cloudbase.core.data.Value;
import cloudbase.core.tabletserver.iterators.OptionDescriber;

public class AgeOffFilter implements Filter, OptionDescriber {
	private long threshold;
	private long currentTime;
	
	@Override
	public boolean accept(Key k, Value v) {
		if (currentTime - k.getTimestamp() > threshold)
			return false;
		return true;
	}
	
	@Override
	public void init(Map<String, String> options) {
		threshold = -1;
		if (options == null)
			throw new IllegalArgumentException("ttl must be set for AgeOffFilter");		

		String ttl = options.get("ttl");
		if (ttl == null)
			throw new IllegalArgumentException("ttl must be set for AgeOffFilter");		
		
		threshold = Long.parseLong(ttl);
		
		String time = options.get("currentTime");
		if (time != null)
			currentTime = Long.parseLong(time);
		else
			currentTime = System.currentTimeMillis();
		
		// TODO: add sanity checks for threshold and currentTime?
	}
	
	@Override
	public IteratorOptions describeOptions() {
		return new IteratorOptions("ageoff","AgeOffFilter removes entries with timestamps more than <ttl> milliseconds old",
				Collections.singletonMap("ttl", "time to live (milliseconds)"),null);
	}

	@Override
	public boolean validateOptions(Map<String, String> options) {
		Long.parseLong(options.get("ttl"));
		return true;
	}
}
