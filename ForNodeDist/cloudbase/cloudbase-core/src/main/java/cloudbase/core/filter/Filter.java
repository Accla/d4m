package cloudbase.core.filter;

import java.util.Map;

import cloudbase.core.data.Key;
import cloudbase.core.data.Value;

public interface Filter {
	public void init(Map<String, String> options);
	public boolean accept(Key k, Value v);
}
