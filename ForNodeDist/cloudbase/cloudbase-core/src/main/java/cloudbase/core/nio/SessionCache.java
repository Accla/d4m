package cloudbase.core.nio;

public interface SessionCache {
	public void putInCache(Object key, Object value);
	public void removeFromCache(Object key);
	public Object getFromCache(Object key);
	public void clearCache();
}
