package cloudbase.core.tabletserver.iterators;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import cloudbase.core.aggregation.Aggregator;
import cloudbase.core.aggregation.conf.AggregatorSet;
import cloudbase.core.data.Key;
import cloudbase.core.data.Value;

/**
 * This iterator wraps another iterator.  It automatically aggregates.
 * 
 *
 */

public class AggregatingIterator implements SortedKeyValueIterator<Key, Value>, OptionDescriber {

	private SortedKeyValueIterator<Key, Value> iterator;
	private AggregatorSet aggregators;
	
	private Key workKey = new Key();
	
	private Key aggrKey;
	private Value aggrValue;
	//private boolean propogateDeletes;
	static Logger log = Logger.getLogger(AggregatingIterator.class.getName());

	public AggregatingIterator(){}
	
	private void aggregateRowColumn(Aggregator aggr) throws IOException {
		//this function assumes that first value is not delete
		
		//TODO avoid copy
		workKey.set(iterator.getTopKey());
		
		Key keyToAggregate = workKey;
		
		aggr.reset();
		
		aggr.collect(iterator.getTopValue());
		iterator.next();
		
		while(iterator.hasTop() && iterator.getTopKey().compareTo(keyToAggregate, 4) == 0){	
			aggr.collect(iterator.getTopValue());
			iterator.next();
		}
		
		aggrKey = workKey;
		aggrValue = aggr.aggregate();
		
	}

	private void findTop() throws IOException{
		//check if aggregation is needed
		if(iterator.hasTop()){
			Aggregator aggr = aggregators.getAggregator(iterator.getTopKey());
			if(aggr != null){
				aggregateRowColumn(aggr);
			}
		}
	}
	
	public AggregatingIterator(SortedKeyValueIterator<Key, Value> iterator, AggregatorSet aggregators) throws IOException{
		this.iterator = iterator;
		this.aggregators = aggregators;
		
		findTop();
	}

	public Key getTopKey() {
		if(aggrKey != null){
			return aggrKey;
		}
		return iterator.getTopKey();
	}

	public Value getTopValue() {
		if(aggrKey != null){
			return aggrValue;
		}
		return iterator.getTopValue();
	}

	public boolean hasTop() {
		return aggrKey != null || iterator.hasTop();
	}

	public void next() throws IOException {
		if(aggrKey != null){
			aggrKey = null;
			aggrValue = null;
		}else{
			iterator.next();
		}
		
		findTop();
	}

	public void seek(Key key) throws IOException {
		//do not want to seek to the middle of a value that should be 
		//aggregated...  
		
		Key seekKey = key;
		
		if(seekKey.getTimestamp() != Long.MAX_VALUE){
			seekKey = new Key(key);
			seekKey.setTimestamp(Long.MAX_VALUE);
		}
		
		iterator.seek(seekKey);
		findTop();
		
		while(hasTop() &&
				getTopKey().compareTo(key, 4) == 0 &&
				getTopKey().getTimestamp() > key.getTimestamp())
		{
			//the value has a more recent time stamp, so
			//pass it up
			//log.debug("skipping "+getTopKey());
			next();
		}
		
	}

	public void init(SortedKeyValueIterator<Key, Value> source, Map<String, String> options) throws IOException {
		
		this.iterator = source;
		
		try {
			this.aggregators = new AggregatorSet(options);
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
		
		findTop();
	}

	@Override
	public IteratorOptions describeOptions() {
		return new IteratorOptions("agg","Aggregators apply aggregating functions to values with identical keys",
				null, Collections.singletonList("<columnName> <aggregatorClass>"));
	}

	@Override
	public boolean validateOptions(Map<String, String> options) {
		for (Entry<String,String> entry : options.entrySet()) {
			Class<? extends Aggregator> clazz;
			try {
				clazz = (Class<? extends Aggregator>) Class.forName(entry.getValue());
				clazz.newInstance();
			} catch (ClassNotFoundException e) {
				throw new IllegalArgumentException("class not found: "+entry.getValue());
			} catch (InstantiationException e) {
				throw new IllegalArgumentException("instantiation exception: "+entry.getValue());
			} catch (IllegalAccessException e) {
				throw new IllegalArgumentException("illegal access exception: "+entry.getValue());
			}
		}

		return true;
	}

	@Override
	public void setEndKey(Key key) {
		iterator.setEndKey(key);
	}
	
}
