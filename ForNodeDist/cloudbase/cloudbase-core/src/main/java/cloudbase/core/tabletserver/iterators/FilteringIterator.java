package cloudbase.core.tabletserver.iterators;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import cloudbase.core.data.Key;
import cloudbase.core.data.Value;
import cloudbase.core.filter.Filter;

public class FilteringIterator implements SortedKeyValueIterator<Key, Value>, OptionDescriber {
	private SortedKeyValueIterator<Key, Value> iterator;
	private List<? extends Filter> filters;

	private static Logger log = Logger.getLogger(IteratorUtil.class.getName());
	
	public FilteringIterator(){}
	
	public FilteringIterator(SortedKeyValueIterator<Key, Value> iterator, 
			List<? extends Filter> filters) throws IOException{
		this.iterator = iterator;
		this.filters = filters;
		
		findTop();
	}

	public Key getTopKey() {
		return iterator.getTopKey();
	}

	public Value getTopValue() {
		return iterator.getTopValue();
	}

	public boolean hasTop() {
		return iterator.hasTop();
	}

	public void next() throws IOException {
		iterator.next();
		findTop();
	}

	public void seek(Key key) throws IOException {
		iterator.seek(key);
		findTop();
	}
	
	private void findTop() throws IOException {
		boolean goodKey;
		while (iterator.hasTop()) {
			goodKey = true;
			for (Filter f : filters) {
				if (!f.accept(iterator.getTopKey(), iterator.getTopValue())) {
					goodKey = false;
					break;
				}
			}
			if (goodKey==true)
				return;
			iterator.next();
		}
	}

    public void init(SortedKeyValueIterator<Key, Value> source, Map<String, String> options) throws IOException {
		this.iterator = source;
		
		Map<String, Map<String, String>> classesToOptions = parseOptions(options);
		
		ArrayList<Filter> newFilters = new ArrayList<Filter>(classesToOptions.size());
		
		Collection<String> classes = classesToOptions.keySet();
		try{
			for (String filterClass : classes) {
			    Class<? extends Filter> clazz = (Class<? extends Filter>) Class.forName(filterClass);
			    Filter f = clazz.newInstance();
			    f.init(classesToOptions.get(filterClass));
				newFilters.add(f);
			}
			
			filters = newFilters;
			
		}catch (ClassNotFoundException e) {
            log.error(e.toString());
            throw new IOException(e);
        } catch (InstantiationException e) {
        	log.error(e.toString());
			throw new IOException(e);
		} catch (IllegalAccessException e) {
			log.error(e.toString());
			throw new IOException(e);
		}
		
		findTop();
	}
	
	/* FilteringIterator expects its options in the following form:
	 * 
	 * key			value
	 * 0			classNameA
	 * 0.optname1	optvalue1
	 * 0.optname2	optvalue2
	 * 1			classNameB
	 * 1.optname3	optvalue3
	 * 
	 * The initial digit is used only to distinguish the different filter classes.  
	 * Additional options need not be provided, unless expected by the particular filter.
	 * 
	 */
	
	private static Map<String, Map<String, String>> parseOptions(Map<String, String> options) {
		HashMap<String, String> namesToClasses = new HashMap<String, String>();
		HashMap<String, Map<String, String>> namesToOptions = new HashMap<String, Map<String, String>>();
		HashMap<String, Map<String, String>> classesToOptions = new HashMap<String, Map<String, String>>();
		
		int index;
		String name;
		String subName;
		
		Collection<Entry<String, String>> entries = options.entrySet();
		for (Entry<String, String> e : entries) {
			name = e.getKey();
			if ((index = name.indexOf(".")) < 0)
				namesToClasses.put(name, e.getValue());
			else {
				subName = name.substring(0,index);
				if (!namesToOptions.containsKey(subName))
					namesToOptions.put(subName, new HashMap<String, String>());
				namesToOptions.get(subName).put(name.substring(index+1), e.getValue());
			}
		}
		
		Collection<String> names = namesToClasses.keySet();
		for (String s : names) {
			classesToOptions.put(namesToClasses.get(s), namesToOptions.get(s));
		}
		
		return classesToOptions;
	}
	/*
	@Override
	public IteratorOptions requestOptions(ConsoleReader reader)
			throws IOException {
		String className;
		Map<String, String> options = new HashMap<String, String>();
		
		String name = reader.readLine("The FilteringIterator removes entries using Filter classes\n" +
				"distinguishing name for FilteringIterator: ");

		while (true) {
			className = reader.readLine("<filterClass> (hit enter to skip): ");
			if (className.length()==0)
				break;
			Class<? extends Filter> clazz;
			try {
				clazz = Cloudbase.getClass(className);
				Filter f = clazz.newInstance();
				if (!(f instanceof InteractiveOptionChooser))
					throw new IOException(className+" does not implement an InteractiveOptionChooser, it is not configured to set properties via the shell");
					
				IteratorOptions opt = ((InteractiveOptionChooser)f).requestOptions(reader);
				options.put(opt.name, className);
				for (Entry<String,String> entry : opt.options.entrySet())
					options.put(opt.name+"."+entry.getKey(), entry.getValue());
			} catch (ClassNotFoundException e) {
				throw new IOException("class not found: "+className);
			} catch (InstantiationException e) {
				throw new IOException("instantiation exception: "+className);
			} catch (IllegalAccessException e) {
				throw new IOException("illegal access exception: "+className);
			}
		}
		
		return new IteratorOptions(name, options);
	}*/

	@Override
	public IteratorOptions describeOptions() {
		return new IteratorOptions("filter","FilteringIterator uses Filters to accept or reject key/value pairs",
				null, Collections.singletonList("<filterPriorityNumber> <ageoff|regex|filterClass>"));
	}

	@Override
	public boolean validateOptions(Map<String, String> options) {
		parseOptions(options);
		return true;
	}

	@Override
	public void setEndKey(Key key) {
		iterator.setEndKey(key);
	}
}
