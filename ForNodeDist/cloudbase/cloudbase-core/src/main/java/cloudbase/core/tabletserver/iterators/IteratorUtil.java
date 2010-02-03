package cloudbase.core.tabletserver.iterators;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.io.WritableComparable;
import org.apache.log4j.Logger;

import cloudbase.core.aggregation.Aggregator;
import cloudbase.core.client.CBException;
import cloudbase.core.client.CBSecurityException;
import cloudbase.core.client.Instance;
import cloudbase.core.client.impl.MasterClient;
import cloudbase.core.conf.CBConfiguration;
import cloudbase.core.data.IterInfo;
import cloudbase.core.data.KeyExtent;
import cloudbase.core.master.thrift.MasterClientService;
import cloudbase.core.security.thrift.AuthInfo;
import cloudbase.core.security.thrift.ThriftSecurityException;

public class IteratorUtil {
	
	private static Logger log = Logger.getLogger(IteratorUtil.class.getName());
	
	public static final String ITERATOR_PROPERTY = "cloudbase.table.iterator.";
	
	public static enum IteratorScope {
		majc,
		minc,
		scan;		
	};
	
	public static class IterInfoComparator implements Comparator<IterInfo> {

		@Override
		public int compare(IterInfo o1, IterInfo o2) {
			return (o1.priority<o2.priority ? -1 : (o1.priority==o2.priority ? 0 : 1));
		}
		
	}
	
	
	
	public static Map<String, String> generateInitialTableProperties(Map<Text, String> aggregators){
		
		TreeMap<String, String> props = new TreeMap<String, String>();
		
		for (IteratorScope iterScope : IteratorScope.values()) {
			if(aggregators.size() > 0){
				props.put(ITERATOR_PROPERTY+iterScope.name()+".agg", "10,"+AggregatingIterator.class.getName());
			}
			
			props.put(ITERATOR_PROPERTY+iterScope.name()+".vers", "20,"+VersioningIterator.class.getName());
			props.put(ITERATOR_PROPERTY+iterScope.name()+".vers.opt.maxVersions", "1");
		}
		
		for (Entry<Text, String> entry : aggregators.entrySet()) {
			for (IteratorScope iterScope : IteratorScope.values()) {
				props.put(ITERATOR_PROPERTY+iterScope.name()+".agg.opt."+entry.getKey(), entry.getValue());
			}
		}
		
		return props;
	}
	
	public static boolean setupInitialIterators(Instance instance, AuthInfo credentials, String table, Map<Text, Class<? extends Aggregator>> aggregators)
	throws CBException, CBSecurityException
	{
		boolean bool = true;
		try {
			MasterClientService.Client client = MasterClient.master_connect(instance);
			
			Map<Text, String> aggs = new TreeMap<Text, String>();
			for (Entry<Text, Class<? extends Aggregator>> entry : aggregators.entrySet()) {
				aggs.put(entry.getKey(), entry.getValue().getName());
			}
			
			Map<String, String> props = generateInitialTableProperties(aggs);
			
			for (Entry<String, String> entry : props.entrySet())
				if(!client.setTableProperty(credentials, table, entry.getKey(), entry.getValue()))
					bool = false;

		} catch (ThriftSecurityException e) {
			throw new CBSecurityException(e.user, e.baduserpass, e);
		} catch (Throwable t) {
			throw new CBException(t);
		}
		return bool;
	}
	
	private static void parseIterConf(IteratorScope scope, List<IterInfo> iters, Map<String, Map<String, String>> allOptions, CBConfiguration conf)
	{
		for (Entry<String, String> entry : conf) {
			if(entry.getKey().startsWith(ITERATOR_PROPERTY)){
				
				String suffix = entry.getKey().substring(ITERATOR_PROPERTY.length());
				String suffixSplit[] = suffix.split("\\.",4);

				if(!suffixSplit[0].equals(scope.name())){
			        
				    //do a sanity check to see if this is a valid scope
		            boolean found = false;
		            IteratorScope[] scopes = IteratorScope.values();
		            for (IteratorScope s : scopes) {
		                found = found || suffixSplit[0].equals(s.name());
		            }
		            
		            if(!found){
		                log.warn("Option contains unknown scope: "+entry.getKey());
		            }
		            
		            continue;
		        }
				
				if(suffixSplit.length == 2){
					String sa[] = entry.getValue().split(",");
					int prio = Integer.parseInt(sa[0]);
					String className = sa[1];
					iters.add(new IterInfo(prio, className, suffixSplit[1]));
				}else if(suffixSplit.length == 4 && suffixSplit[2].equals("opt")){
					String iterName = suffixSplit[1];
					String optName = suffixSplit[3];
					
					Map<String, String> options = allOptions.get(iterName);
					if(options == null){
						options = new HashMap<String, String>();
						allOptions.put(iterName, options);
					}
					
					options.put(optName, entry.getValue());
					
				}else{
					log.warn("Unrecognizable option: "+entry.getKey());
				}
			}
		}
		
		Collections.sort(iters, new IterInfoComparator());
	}
	
	public static String findIterator(IteratorScope scope, String className, CBConfiguration conf, Map<String, String> opts)
	{
		ArrayList<IterInfo> iters = new ArrayList<IterInfo>();
		Map<String, Map<String, String>> allOptions = new HashMap<String, Map<String,String>>();
		
		parseIterConf(scope, iters, allOptions, conf);
		
		for (IterInfo iterInfo : iters)
			if(iterInfo.className.equals(className))
			{
				opts.putAll(allOptions.get(iterInfo.iterName));
				return iterInfo.iterName;
			}
		
		return null;
	}
	
	public static <K extends WritableComparable<?>, V extends Writable> SortedKeyValueIterator<K, V> loadIterators(IteratorScope scope, SortedKeyValueIterator<K, V> source, KeyExtent extent, CBConfiguration conf)
	throws IOException
	{
	    List<IterInfo> emptyList = Collections.emptyList();
	    Map<String,Map<String,String>> emptyMap = Collections.emptyMap();
	    return loadIterators(scope, source, extent, conf, emptyList, emptyMap);
	}
	
	@SuppressWarnings("unchecked")
	public static <K extends WritableComparable<?>, V extends Writable> SortedKeyValueIterator<K, V> loadIterators(IteratorScope scope, SortedKeyValueIterator<K, V> source, KeyExtent extent, CBConfiguration conf, List<IterInfo> ssiList, Map<String, Map<String, String>> ssio)
	throws IOException
	{
		try {
			
			List<IterInfo> iters = new ArrayList<IterInfo>(ssiList);
			Map<String, Map<String, String>> allOptions = new HashMap<String, Map<String, String>>();
			
			parseIterConf(scope, iters, allOptions, conf);
			
			SortedKeyValueIterator<K, V> prev = source;
			
			for (IterInfo iterInfo : iters)
			{
				Class<? extends SortedKeyValueIterator<K,V>> clazz = (Class<? extends SortedKeyValueIterator<K,V>>) Class.forName(iterInfo.className);
				
				SortedKeyValueIterator<K, V> skvi = clazz.newInstance();
				
                Map<String, String> options = allOptions.get(iterInfo.iterName);
                Map<String, String> userOptions = ssio.get(iterInfo.iterName);
                
                if(options == null && userOptions == null) 
                	options = Collections.emptyMap();
                else if(options == null && userOptions != null)
                	options = userOptions;
                else if(options != null && userOptions != null)
                	options.putAll(userOptions);
                
                
                skvi.init(prev, options);
                prev = skvi;
			}
			
			return prev;
			
		} catch (ClassNotFoundException e) {
            log.error(e.toString());
            throw new IOException(e);
        } catch (InstantiationException e) {
        	log.error(e.toString());
			throw new IOException(e);
		} catch (IllegalAccessException e) {
			log.error(e.toString());
			throw new IOException(e);
		}
	}
}
