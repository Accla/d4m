package cloudbase.core.conf;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.util.StringUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;

import cloudbase.core.CBConstants;
import cloudbase.core.Cloudbase;
import cloudbase.core.zookeeper.ZooCache;
import cloudbase.core.zookeeper.ZooSession;

public class CBConfiguration implements Iterable<Entry<String, String>> {
	
	private static Logger log = Logger.getLogger(CBConfiguration.class.getName());
	
	private static CBConfiguration instance = null;
	private static Map<String, CBConfiguration> tableInstances = new HashMap<String, CBConfiguration>();
	
	static {
	    Logger zkLogger = Logger.getLogger("org.apache.zookeeper");
	    zkLogger.setLevel(Level.WARN);
	    Logger compressLogger = Logger.getLogger("org.apache.hadoop.io.compress");
	    compressLogger.setLevel(Level.WARN);
	}
	
	private static Configuration xmlConfig;
	private static ZooCache tablePropCache = new ZooCache(new ZWatcher());
	
	private String table = null;
	
	private static Configuration getXmlConfig(){
		if(xmlConfig == null){
			xmlConfig = new Configuration(false);
			
			if(CBConfiguration.class.getClassLoader().getResource("cloudbase-default.xml") == null){
				log.warn("cloudbase-default.xml not found on classpath");
			}
			
			if(CBConfiguration.class.getClassLoader().getResource("cloudbase-site.xml") == null){
				log.warn("cloudbase-site.xml not found on classpath");
			}
			
			xmlConfig.addResource("cloudbase-default.xml");
			xmlConfig.addResource("cloudbase-site.xml");
		}
		
		return xmlConfig;
	}
	
	private Set<ConfigurationObserver> observers;
	
	public void addObserver(ConfigurationObserver co){
		iterator();
		observers.add(co);
	}
	
	public void removeObserver(ConfigurationObserver configObserver) {
		observers.remove(configObserver);
	}
	
	private static class ZWatcher implements Watcher {

		@Override
		public void process(WatchedEvent event) {
			//System.out.println("WatchEvent : "+event.getPath()+" "+event.getState()+" "+event.getType());
			
			String prefix = CBConstants.ZROOT_PATH+"/"+Cloudbase.getInstanceID()+CBConstants.ZTABLE_CONF_PATH+"/";
			
			String table = null;
			String key = null;
			
			if(event.getPath() != null){
				if(event.getPath().startsWith(prefix)){
					table = event.getPath().substring(prefix.length());
					if(table.contains("/")){
						String[] sa = table.split("/",2);
						table = sa[0];
						key = sa[1];
					}
				}
				
				if(table == null){
					log.warn("Zookeeper told me about a path I was not watching "+event.getPath()+" state="+event.getState()+" type="+event.getType());
					return;
				}
			}
			
			switch(event.getType()){
			case NodeDataChanged:
				{
					CBConfiguration conf = tableInstances.get(table);
					//log.debug("EventNodeDataChanged "+event.getPath());
					if(key != null){
						synchronized (conf.observers) {
							for (ConfigurationObserver cbo : conf.observers) {
								cbo.propertyChanged(key);
							}	
						}
					}
				}
				break;
			case NodeChildrenChanged:
				{
					CBConfiguration conf = tableInstances.get(table);
					//System.out.println("EventNodeChildrenChanged "+event.getPath());
					//log.debug("EventNodeChildrenChanged "+event.getPath());
					synchronized (conf.observers) {
						for (ConfigurationObserver cbo : conf.observers) {
							cbo.propertiesChanged();
						}
					}
				}
				break;
			case NodeCreated:
				break;
			case NodeDeleted:
				break;
			case None:
				switch(event.getState()){
				case Expired:
					synchronized (CBConfiguration.class) {
						Set<Entry<String, CBConfiguration>> es = tableInstances.entrySet();
						for (Entry<String, CBConfiguration> entry : es) {
							CBConfiguration conf = entry.getValue();
							synchronized (conf.observers) {
								for (ConfigurationObserver cbo : conf.observers) {
									cbo.sessionExpired();
								}
							}
						}
					}
					break;
				case SyncConnected:
					break;
				default:
					log.warn("EventNone event not handled path = "+event.getPath()+" state="+event.getState());
				}
				break;
			default:
				log.warn("Event not handled path = "+event.getPath()+" state="+event.getState()+" type = "+event.getType());

			}
		}
	}
	
	public String get(String key, String defaultValue){
		
		String value = get(key);
		
		if(value != null){
			return value;
		}
		
		return defaultValue;
	}
	
	public String get(String key){
		
		String value = null;
		
		if(table != null){
			String zPath= CBConstants.ZROOT_PATH+"/"+Cloudbase.getInstanceID()+CBConstants.ZTABLE_CONF_PATH+"/"+table+"/"+key;
			byte[] v = tablePropCache.get(zPath);
			if(v != null){
				value = new String(v);
			}
		}
		
		if(value == null){
			value = getXmlConfig().get(key);
		}
		
		return value;
	}
	
	public boolean getBoolean(String key, boolean defaultValue){
		String v = get(key);
		if(v == null)
			return defaultValue;
		
		return Boolean.parseBoolean(v);
	}
	
	public short getShort(String key, short defaultValue){
		String v = get(key);
		if(v == null)
			return defaultValue;
		
		try{
			return Short.parseShort(v);
		}catch(NumberFormatException nfe){
			log.warn("key = "+key+"  value = "+v, nfe);
		}
		
		return defaultValue;
	}
	
	public int getInt(String key, int defaultValue){
		String v = get(key);
		if(v == null)
			return defaultValue;
		
		try{
			return Integer.parseInt(v);
		}catch(NumberFormatException nfe){
			log.warn("key = "+key+"  value = "+v, nfe);
		}

		return defaultValue;
	}
	
	public long getLong(String key, long defaultValue){
		String v = get(key);
		if(v == null)
			return defaultValue;
		
		try{
			return Long.parseLong(v);
		}catch(NumberFormatException nfe){
			log.warn("key = "+key+"  value = "+v, nfe);
		}
		
		
		return defaultValue;
	}
	
	
	public float getFloat(String key, float defaultValue){
		String v = get(key);
		if(v == null)
			return defaultValue;
		
		try{
			return Float.parseFloat(v);
		}catch(NumberFormatException nfe){
			log.warn("key = "+key+"  value = "+v, nfe);
		}
		
		
		return defaultValue;
	}
	
	public double getDouble(String key, double defaultValue){
		String v = get(key);
		if(v == null)
			return defaultValue;
		
		try{
			return Double.parseDouble(v);
		}catch(NumberFormatException nfe){
			log.warn("key = "+key+"  value = "+v, nfe);
		}
		
		
		return defaultValue;
	}
	
	public Collection<String> getStringCollection(String key) {
		String valueString = get(key);
		return StringUtils.getStringCollection(valueString);
	}
	
	@Override
	public Iterator<Entry<String, String>> iterator() {
		if(table == null){
			return getXmlConfig().iterator();
		}
		
		List<String> children = tablePropCache.getChildren(CBConstants.ZROOT_PATH+"/"+Cloudbase.getInstanceID()+CBConstants.ZTABLE_CONF_PATH+"/"+table);
		
		if(children == null){
			return getXmlConfig().iterator();
		}
		
		TreeMap<String, String> entries = new TreeMap<String, String>();
		
		for (Entry<String, String> entry : getXmlConfig()) {
			entries.put(entry.getKey(), entry.getValue());
		}
		
		for (String child : children)
			if(child != null)
				entries.put(child, get(child));
		
		return entries.entrySet().iterator();
	}
	
	/**
	 * method here to support testing, do not call
	 */
	public void clear(){
		getXmlConfig().clear();
	}
	
	/**
	 * method here to support testing, do not call
	 */
	public void set(String key, String value){
		getXmlConfig().set(key, value);
	}
	
	CBConfiguration(){
		table = null;
	}
	
	CBConfiguration(String table) {
		this.table = table;
		observers = Collections.synchronizedSet(new HashSet<ConfigurationObserver>());
	}
	
	public static synchronized CBConfiguration getInstance(){
		if(instance == null){
			instance = new CBConfiguration();
		}
		
		return instance;
	}
	
	public static synchronized CBConfiguration getInstance(String table) {
		
		CBConfiguration conf = tableInstances.get(table);
		if(conf == null){
			conf = new CBConfiguration(table);
			tableInstances.put(table, conf);
		}
		
		return conf;
	}
	
	public static void initializePerTableProperties() throws CBConfigurationException{
		try {
			if(ZooSession.getSession().exists(CBConstants.ZROOT_PATH, false) == null){
				ZooSession.getSession().create(CBConstants.ZROOT_PATH, new byte[]{}, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
			}
			
			ZooSession.getSession().create(CBConstants.ZROOT_PATH+"/"+Cloudbase.getInstanceID(), new byte[]{}, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
			ZooSession.getSession().create(CBConstants.ZROOT_PATH+"/"+Cloudbase.getInstanceID()+CBConstants.ZCONF_PATH, new byte[]{}, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
			ZooSession.getSession().create(CBConstants.ZROOT_PATH+"/"+Cloudbase.getInstanceID()+CBConstants.ZTABLE_CONF_PATH, new byte[]{}, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
			
		} catch (KeeperException e) {
			throw new CBConfigurationException("Failed to initialize per table props", e);
		} catch (InterruptedException e) {
			throw new CBConfigurationException("Failed to initialize per table props", e);
		} 
	}

	
}
