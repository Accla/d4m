package cloudbase.core.conf;


public interface ConfigurationObserver {
	void propertyChanged(String key);
	void propertiesChanged();
	void sessionExpired();
}
