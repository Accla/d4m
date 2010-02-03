package cloudbase.core.aggregation.conf;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.hadoop.io.Text;

import cloudbase.core.aggregation.Aggregator;
import cloudbase.core.data.Key;

public class AggregatorSet {
	
	private static int hash(byte[] bytes, int offset, int len){
		 int hash = 1;
		 int end = offset + len;
		 
		 for (int i = offset; i < end; i++)
			 hash = (31 * hash) + (int)bytes[i];
		 
		 return hash;
	}
	
	static class ColFamHashKey {
		Text columnFamily;
		
		Key key;
		
		private int hashCode;
		
		ColFamHashKey(){
			columnFamily = null;
		}
		
		ColFamHashKey(Text cf){
			columnFamily = cf;
			hashCode = hash(columnFamily.getBytes(), 0, columnFamily.getLength()) ;
		}
		
		void set(Key key){
			this.key = key;
			hashCode = hash(key.getKeyData(), key.getColumnFamilyOffset(), key.getColumnFamilyLen());
		}
		
		public int hashCode(){
			return hashCode;
		}
		
		public boolean equals(Object o){
			ColFamHashKey ohk = (ColFamHashKey) o;
			
			if(columnFamily == null){
				return key.compareColumnFamily(ohk.columnFamily) == 0;
			}else{
				return ohk.key.compareColumnFamily(columnFamily) == 0;
			}
		}
	}
	
	static class ColHashKey {
		Text columnFamily;
		Text columnQualifier;
		
		Key key;
		
		private int hashCode;
		
		ColHashKey(){
			columnFamily = null;
			columnQualifier = null;
		}
		
		ColHashKey(Text cf, Text cq){
			columnFamily = cf;
			columnQualifier = cq;
			hashCode = hash(columnFamily.getBytes(), 0, columnFamily.getLength()) ^ 
				hash(columnQualifier.getBytes(), 0, columnQualifier.getLength());
		}
		
		void set(Key key){
			this.key = key;
			hashCode = hash(key.getKeyData(), key.getColumnFamilyOffset(), key.getColumnFamilyLen()) ^
				hash(key.getKeyData(), key.getColumnQualifierOffset(), key.getColumnQualifierLen());
		}
		
		public int hashCode(){
			return hashCode;
		}
		
		public boolean equals(Object o){
			ColHashKey ohk = (ColHashKey) o;
			
			if(columnFamily == null){
				return key.compareColumnFamily(ohk.columnFamily) == 0 &&
					key.compareColumnQualifier(ohk.columnQualifier) == 0;
			}else{
				return ohk.key.compareColumnFamily(columnFamily) == 0 &&
					ohk.key.compareColumnQualifier(columnQualifier) == 0;
			}
		}
	}
	
	private HashMap<ColFamHashKey, Aggregator> aggregatorsCF;
	private HashMap<ColHashKey, Aggregator> aggregatorsCol;
	
	private ColHashKey lookupCol = new ColHashKey();
	private ColFamHashKey lookupCF = new ColFamHashKey();
	
	public AggregatorSet(){
		aggregatorsCF = new HashMap<ColFamHashKey, Aggregator>();
		aggregatorsCol = new HashMap<ColHashKey, Aggregator>();
	}
	
	@SuppressWarnings("unchecked")
	public AggregatorSet(Map<String, String> aggregatorStrings) throws InstantiationException, IllegalAccessException, ClassNotFoundException{
		this();
		
		Map<Text, Class<? extends Aggregator>> aggregators = new HashMap<Text, Class<? extends Aggregator>>();
			
		for (Entry<String, String> entry : aggregatorStrings.entrySet()) {
			String column = entry.getKey();
			String className = entry.getValue();
            Class<? extends Aggregator> clazz = (Class<? extends Aggregator>) Class.forName(className);
            aggregators.put(new Text(column), clazz);
		}
		
		init(aggregators);
	}
	
	private void init(Map<Text, Class<? extends Aggregator>> aggregators) throws InstantiationException, IllegalAccessException {	
		Set<Entry<Text, Class<? extends Aggregator>>> es = aggregators.entrySet();
		
		for (Entry<Text, Class<? extends Aggregator>> entry : es) {
			Text column = entry.getKey();
			
			int index = -1;
			
			for(int i = 0; i < column.getLength(); i++){
				if(column.getBytes()[i] == ':'){
					index = i;
					break;
				}
			}
			
			if(index == -1){
				Text colf = new Text(column);
				addAggregator(colf, entry.getValue().newInstance());
			}else{
				Text colf = new Text();
				colf.set(column.getBytes(), 0, index);
				Text colq = new Text();
				colq.set(column.getBytes(), index+1, column.getLength() - (index + 1));
				addAggregator(colf, colq, entry.getValue().newInstance());
			}
		}
	}

	void addAggregator(Text colf, Aggregator agg){
		aggregatorsCF.put(new ColFamHashKey(new Text(colf)), agg);
	}
	
	void addAggregator(Text colf, Text colq, Aggregator agg){
		aggregatorsCol.put(new ColHashKey(colf, colq), agg);
	}
	
	public Aggregator getAggregator(Key key) {
		Aggregator agg = null;
		
		//lookup just column family
		if(aggregatorsCF.size() > 0){
			lookupCF.set(key);
			agg = aggregatorsCF.get(lookupCF);
			if(agg != null){
				return agg;
			}
		}
		
		//lookup column family and column qualifier
		if(aggregatorsCol.size() > 0){
			lookupCol.set(key);
			agg = aggregatorsCol.get(lookupCol);
		}
		
		return agg;
	}
}
