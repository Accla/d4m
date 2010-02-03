package cloudbase.core.tabletserver;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;

import org.apache.hadoop.io.Text;

import cloudbase.core.aggregation.StringSummation;
import cloudbase.core.aggregation.conf.AggregatorSet;
import cloudbase.core.data.Key;
import cloudbase.core.data.Mutation;
import cloudbase.core.data.Value;
import junit.framework.TestCase;

public class InMemoryMapTest extends TestCase{
	
	public void mutate(InMemoryMap imm, String row, String column, long ts){
		Mutation m = new Mutation(new Text(row));
		String[] sa = column.split(":");
		m.remove(new Text(sa[0]), new Text(sa[1]), ts);
		
		imm.mutate(m);
	}
	
	public void mutate(InMemoryMap imm, String row, String column, long ts, String value){
		Mutation m = new Mutation(new Text(row));
		String[] sa = column.split(":");
		m.put(new Text(sa[0]), new Text(sa[1]), ts, new Value(value.getBytes()));
		
		imm.mutate(m);
	}
	
	public void verify(InMemoryMap imm, String row, String column, long ts, String value){
		String[] sa = column.split(":");
		Key k = new Key(new Text(row), new Text(sa[0]), new Text(sa[1]), ts);
		SortedMap<Key, Value> tm = imm.tailmap(k);
		
		assertTrue(tm.firstKey().compareTo(k) == 0);
		assertTrue(tm.get(tm.firstKey()).toString().equals(value));
	}
	
	public void verify(InMemoryMap imm, String row, String column, long ts){
		String[] sa = column.split(":");
		Key k = new Key(new Text(row), new Text(sa[0]), new Text(sa[1]), ts);
		k.setDeleted(true);
		SortedMap<Key, Value> tm = imm.tailmap(k);
		
		assertTrue(tm.firstKey().compareTo(k) == 0);
	}
	
	public void verifyDeleted(InMemoryMap imm, String row, String column, long ts, boolean deleted){
		String[] sa = column.split(":");
		Key k = new Key(new Text(row), new Text(sa[0]), new Text(sa[1]), ts);
		k.setDeleted(deleted);
		SortedMap<Key, Value> tm = imm.tailmap(k);
		
		assertTrue(tm.size() ==0 || tm.firstKey().compareTo(k) != 0);
	}
	
	public void testAggregation1() throws InstantiationException, IllegalAccessException, ClassNotFoundException, IOException{
		Map<String, String> aggs = new HashMap<String, String>();
		aggs.put("cf:cq", StringSummation.class.getName());
		AggregatorSet aggSet = new AggregatorSet(aggs);
		InMemoryMap imm = new InMemoryMap(aggSet);
		
		mutate(imm, "r1", "cf:cq", 1, "1");
		verify(imm, "r1", "cf:cq", 1, "1");
		assertTrue(imm.getNumEntries() == 1);
		
		mutate(imm, "r1", "cf:cq", 2, "1");
		verify(imm, "r1", "cf:cq", 2, "2");
		assertTrue(imm.getNumEntries() == 1);
		
		//insert a delete
		mutate(imm, "r1", "cf:cq", 2);
		verify(imm, "r1", "cf:cq", 2);
		assertTrue(imm.getNumEntries() == 1);
		
		//insert data before the delete,
		//should aggregate up to the delete
		mutate(imm, "r1", "cf:cq", 3, "1");
		verify(imm, "r1", "cf:cq", 3, "1");
		assertTrue(imm.getNumEntries() == 2);
		
		mutate(imm, "r1", "cf:cq", 4, "2");
		verify(imm, "r1", "cf:cq", 4, "3");
		assertTrue(imm.getNumEntries() == 2);
	}
	
	
	public void testAggregation2() throws InstantiationException, IllegalAccessException, ClassNotFoundException, IOException{
		Map<String, String> aggs = new HashMap<String, String>();
		aggs.put("cf:cq", StringSummation.class.getName());
		AggregatorSet aggSet = new AggregatorSet(aggs);
		InMemoryMap imm = new InMemoryMap(aggSet);
		
		//insert a delete
		mutate(imm, "r1", "cf:cq", 2);
		verify(imm, "r1", "cf:cq", 2);
		assertTrue(imm.getNumEntries() == 1);
		
		//insert two mutations after the delete, should remove them
		mutate(imm, "r1", "cf:cq", 1, "1");
		verifyDeleted(imm,  "r1", "cf:cq", 1, false);
		verify(imm, "r1", "cf:cq", 2);
		assertTrue(imm.getNumEntries() == 1);
		
		mutate(imm, "r1", "cf:cq", 2, "1");
		verifyDeleted(imm,  "r1", "cf:cq", 2, false);
		verify(imm, "r1", "cf:cq", 2);
		assertTrue(imm.getNumEntries() == 1);
		
		//insert data before the delete,
		//should aggregate up to the delete
		mutate(imm, "r1", "cf:cq", 3, "1");
		verify(imm, "r1", "cf:cq", 3, "1");
		assertTrue(imm.getNumEntries() == 2);
		
		mutate(imm, "r1", "cf:cq", 4, "2");
		verify(imm, "r1", "cf:cq", 4, "3");
		assertTrue(imm.getNumEntries() == 2);
		
		//insert another delete
		mutate(imm, "r1", "cf:cq", 4);
		verify(imm, "r1", "cf:cq", 4);
		assertTrue(imm.getNumEntries() == 1);
		verifyDeleted(imm,  "r1", "cf:cq", 4, false);
		verifyDeleted(imm,  "r1", "cf:cq", 2, true);
	}
	
	
	public void testAggregation3() throws InstantiationException, IllegalAccessException, ClassNotFoundException, IOException{
		Map<String, String> aggs = new HashMap<String, String>();
		aggs.put("cf:cq", StringSummation.class.getName());
		AggregatorSet aggSet = new AggregatorSet(aggs);
		InMemoryMap imm = new InMemoryMap(aggSet);
		
		//insert two mutations leaving space in between for a delete
		mutate(imm, "r1", "cf:cq", 1, "1");
		verify(imm, "r1", "cf:cq", 1, "1");
		assertTrue(imm.getNumEntries() == 1);
		
		mutate(imm, "r1", "cf:cq", 3, "1");
		verify(imm, "r1", "cf:cq", 3, "2");
		assertTrue(imm.getNumEntries() == 1);
		
		//insert a delete between values that have already been aggregated,
		//should have no effect.
		
		mutate(imm, "r1", "cf:cq", 2);
		verify(imm, "r1", "cf:cq", 2);
		verify(imm, "r1", "cf:cq", 3, "2");
		assertTrue(imm.getNumEntries() == 2);
	}
	
	public void testAggregation4() throws InstantiationException, IllegalAccessException, ClassNotFoundException, IOException{
		Map<String, String> aggs = new HashMap<String, String>();
		aggs.put("cf", StringSummation.class.getName());
		AggregatorSet aggSet = new AggregatorSet(aggs);
		InMemoryMap imm = new InMemoryMap(aggSet);
		
		//experiment with aggregating two different keys and deletes
		
		//insert two mutations for cq1
		mutate(imm, "r1", "cf:cq1", 1, "1");
		verify(imm, "r1", "cf:cq1", 1, "1");
		assertTrue(imm.getNumEntries() == 1);
		
		mutate(imm, "r1", "cf:cq1", 3, "1");
		verify(imm, "r1", "cf:cq1", 3, "2");
		assertTrue(imm.getNumEntries() == 1);
		
		//insert two mutations for cq2, should not affect cq1
		
		mutate(imm, "r1", "cf:cq2", 1, "1");
		verify(imm, "r1", "cf:cq2", 1, "1");
		verify(imm, "r1", "cf:cq1", 3, "2");
		assertTrue(imm.getNumEntries() == 2);
		
		mutate(imm, "r1", "cf:cq2", 3, "9");
		verify(imm, "r1", "cf:cq2", 3, "10");
		verify(imm, "r1", "cf:cq1", 3, "2");
		assertTrue(imm.getNumEntries() == 2);
		
		
		//insert a delete between values that have already been aggregated,
		//should have no effect.
		
		mutate(imm, "r1", "cf:cq1", 2);
		verify(imm, "r1", "cf:cq1", 2);
		verify(imm, "r1", "cf:cq1", 3, "2");
		verify(imm, "r1", "cf:cq2", 3, "10");
		assertTrue(imm.getNumEntries() == 3);
		
		//delete cq2
		mutate(imm, "r1", "cf:cq2", 3);
		verify(imm, "r1", "cf:cq2", 3);
		verifyDeleted(imm, "r1", "cf:cq2", 3, false);
		verify(imm, "r1", "cf:cq1", 2);
		verify(imm, "r1", "cf:cq1", 3, "2");
		assertTrue(imm.getNumEntries() == 3);
		
	}
	
	public void testAggregation5() throws InstantiationException, IllegalAccessException, ClassNotFoundException, IOException{
		Map<String, String> aggs = new HashMap<String, String>();
		aggs.put("cf", StringSummation.class.getName());
		AggregatorSet aggSet = new AggregatorSet(aggs);
		InMemoryMap imm = new InMemoryMap(aggSet);
		
		for(int i = 0; i < 100; i++){
			for(int j = 0; j < 100; j++){
				mutate(imm, "r1", "cf:cq"+i, j, "1");
				verify(imm, "r1", "cf:cq"+i, j, ""+(j+1));
			}
			
			for(int k = 0; k <= i; k++){
				verify(imm, "r1", "cf:cq"+k, 99, ""+100);
			}
		}
		
		
		imm = new InMemoryMap(aggSet);
		
		for(int i = 0; i < 100; i++){
			for(int j = 0; j < 100; j++){
				mutate(imm, "r1", "cf:cq"+j, i, "1");
				verify(imm, "r1", "cf:cq"+j, i, ""+(i+1));
			}
			
			for(int k = 0; k < 100; k++){
				verify(imm, "r1", "cf:cq"+k, i, ""+(i+1));
			}
		}
		
	}
	
	public void testAggregation6() throws InstantiationException, IllegalAccessException, ClassNotFoundException, IOException{
		Map<String, String> aggs = new HashMap<String, String>();
		aggs.put("cf", StringSummation.class.getName());
		AggregatorSet aggSet = new AggregatorSet(aggs);
		InMemoryMap imm = new InMemoryMap(aggSet);
		
		//try inserting values with the same timestamp
		mutate(imm, "r1", "cf:cq1", 3, "1");
		verify(imm, "r1", "cf:cq1", 3, "1");
		mutate(imm, "r1", "cf:cq1", 3, "1");
		verify(imm, "r1", "cf:cq1", 3, "2");
		mutate(imm, "r1", "cf:cq1", 3, "1");
		verify(imm, "r1", "cf:cq1", 3, "3");
		assertTrue(imm.getNumEntries() == 1);
		
		
		//delete it and then try inserting w/ same timestamp as delete
		mutate(imm, "r1", "cf:cq1", 3);
		verify(imm, "r1", "cf:cq1", 3);
		verifyDeleted(imm, "r1", "cf:cq1", 3, false);
		assertTrue(imm.getNumEntries() == 1);
		
		mutate(imm, "r1", "cf:cq1", 3, "1");
		verify(imm, "r1", "cf:cq1", 3);
		verifyDeleted(imm, "r1", "cf:cq1", 3, false);
		assertTrue(imm.getNumEntries() == 1);
		
	}
	
	public void testAggregation7() throws InstantiationException, IllegalAccessException, ClassNotFoundException, IOException{
		Map<String, String> aggs = new HashMap<String, String>();
		aggs.put("cf", StringSummation.class.getName());
		AggregatorSet aggSet = new AggregatorSet(aggs);
		InMemoryMap imm = new InMemoryMap(aggSet);
		
		//try two deletes in a row with the same timestamp
		mutate(imm, "r1", "cf:cq1", 3);
		verify(imm, "r1", "cf:cq1", 3);
		assertTrue(imm.getNumEntries() == 1);
		
		mutate(imm, "r1", "cf:cq1", 3);
		verify(imm, "r1", "cf:cq1", 3);
		assertTrue(imm.getNumEntries() == 1);
	}
	
	public void test1() throws InstantiationException, IllegalAccessException, ClassNotFoundException, IOException{
		
		Map<String, String> aggs = new HashMap<String, String>();
		aggs.put("cf", StringSummation.class.getName());
		AggregatorSet aggSet = new AggregatorSet(aggs);
		InMemoryMap imm = new InMemoryMap(aggSet);
		
		//simple test that does not aggregation
		mutate(imm, "r1", "foo:cq1", 3, "bar1");
		verify(imm, "r1", "foo:cq1", 3, "bar1");
		assertTrue(imm.getNumEntries() == 1);
		
		mutate(imm, "r1", "foo:cq1", 4, "bar2");
		verify(imm, "r1", "foo:cq1", 4, "bar2");
		verify(imm, "r1", "foo:cq1", 3, "bar1");
		assertTrue(imm.getNumEntries() == 2);
	}
}
