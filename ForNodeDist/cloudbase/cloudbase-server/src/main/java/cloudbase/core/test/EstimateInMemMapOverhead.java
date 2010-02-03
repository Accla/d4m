package cloudbase.core.test;

import java.util.TreeMap;

import org.apache.hadoop.io.Text;

import cloudbase.core.aggregation.conf.AggregatorSet;
import cloudbase.core.data.Value;
import cloudbase.core.tabletserver.InMemoryMap;
import cloudbase.core.data.Mutation;

abstract class MemoryUsageTest {
	abstract void addEntry(int i);
	abstract int getEstimatedBytesPerEntry();
	abstract void clear();
	abstract int getNumPasses();
	abstract String getName(); 
	abstract void init();
	
	
	public void run(){
		System.gc();
		//System.out.printf("*** Starting test %,8d %,8d %,8d %,8d %,8d\n",numEntries, keyLen, colFamLen, colQualLen,dataLen);
		long usedMem = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
		int count = 0;
		while(usedMem > 1024*1024 && count < 10){
			//System.out.printf("%,d\n", usedMem);
			System.gc();
			usedMem = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
			count++;
		}
		//System.out.printf("usedMem = %,d count = %d\n", usedMem, count);
		
		init();
		
		for(int i = 0; i < getNumPasses(); i++){
			addEntry(i);
		}
	
		//System.out.printf("*** Finished test \n");
		System.gc();
	
		long memSize = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) - usedMem;
		
		double actualBytesPerEntry = memSize/(double)getNumPasses();
		double expectedBytesPerEntry = getEstimatedBytesPerEntry();
		double diff = actualBytesPerEntry - expectedBytesPerEntry;
		double ratio = actualBytesPerEntry / expectedBytesPerEntry * 100;
		
		System.out.printf("%30s | %,10d | %6.2fGB | %6.2f | %6.2f | %6.2f | %6.2f%s\n",getName(),getNumPasses(), memSize/(1024*1024*1024.0), actualBytesPerEntry, expectedBytesPerEntry, diff, ratio,"%");
		
		clear();
		
	}
	
}

class TextMemoryUsageTest extends MemoryUsageTest {

	private int keyLen;
	private int colFamLen;
	private int colQualLen;
	private int dataLen;
	private TreeMap<Text, Value> map;
	private int passes;

	TextMemoryUsageTest(int passes, int keyLen, int colFamLen, int colQualLen, int dataLen){
		this.keyLen = keyLen;
		this.colFamLen = colFamLen;
		this.colQualLen = colQualLen;
		this.dataLen = dataLen;
		this.passes = passes;

		
		
	}
	
	void init() {		
		map = new TreeMap<Text, Value>();
	}
	
	public void addEntry(int i) {
		Text key = new Text(String.format("%0"+keyLen+"d:%0"+colFamLen+"d:%0"+colQualLen+"d", i,0,0).getBytes());
		//
		byte data[] = new byte[dataLen];
		for (int j = 0; j < data.length; j++) {
			data[j] = (byte) (j%10 + 65);
		}
		Value value = new Value(data);
		
		map.put(key, value);
		
	}

	public void clear() {
		map.clear();
		map = null;
	}

	public int getEstimatedBytesPerEntry() {
		return keyLen + colFamLen + colQualLen + dataLen;
	}

	int getNumPasses() {
		return passes;
	}

	String getName() {
		return "Text "+keyLen+" "+colFamLen+" "+colQualLen+" "+dataLen;
	}

	

	
}

class InMemoryMapMemoryUsageTest extends MemoryUsageTest {

	private int keyLen;
	private int colFamLen;
	private int colQualLen;
	private int dataLen;
	
	private InMemoryMap imm;
	private Text key;
	private Text colf;
	private Text colq;
	private int passes;

	InMemoryMapMemoryUsageTest(int passes, int keyLen, int colFamLen, int colQualLen, int dataLen){
		this.keyLen = keyLen;
		this.colFamLen = colFamLen;
		this.colQualLen = colQualLen;
		this.dataLen = dataLen;
		this.passes = passes;
		
	
	}
	
	void init() {
		imm = new InMemoryMap(new AggregatorSet());
		key = new Text();
		
		colf = new Text(String.format("%0"+colFamLen+"d", 0));
		colq = new Text(String.format("%0"+colQualLen+"d", 0));
	}
	
	public void addEntry(int i) {
		key.set(String.format("%0"+keyLen+"d", i));
		
		Mutation m = new Mutation(key);
		
		byte data[] = new byte[dataLen];
		for (int j = 0; j < data.length; j++) {
			data[j] = (byte) (j%10 + 65);
		}
		Value idata = new Value(data);
		
		m.put(colf, colq, idata);
		
		imm.mutate(m);
		
	}

	public int getEstimatedBytesPerEntry() {
		return keyLen + colFamLen + colQualLen + dataLen;
	}

	public void clear() {
		imm = null;
		key = null;
		colf = null;
		colq = null;
	}

	int getNumPasses() {
		return passes;
	}
	
	String getName() {
		return "IMM "+keyLen+" "+colFamLen+" "+colQualLen+" "+dataLen;
	}
}

class MutationMemoryUsageTest extends MemoryUsageTest {

	private int keyLen;
	private int colFamLen;
	private int colQualLen;
	private int dataLen;
	
	private Mutation[] mutations;
	private Text key;
	private Text colf;
	private Text colq;
	private int passes;

	MutationMemoryUsageTest(int passes, int keyLen, int colFamLen, int colQualLen, int dataLen){
		this.keyLen = keyLen;
		this.colFamLen = colFamLen;
		this.colQualLen = colQualLen;
		this.dataLen = dataLen;
		this.passes = passes;
		mutations = new Mutation[passes];
		
	
	}
	
	void init() {
		key = new Text();
		
		colf = new Text(String.format("%0"+colFamLen+"d", 0));
		colq = new Text(String.format("%0"+colQualLen+"d", 0));
		
		byte data[] = new byte[dataLen];
		for (int i = 0; i < data.length; i++) {
			data[i] = (byte) (i%10 + 65);
		}
	}
	
	public void addEntry(int i) {
		key.set(String.format("%0"+keyLen+"d", i));
		
		Mutation m = new Mutation(key);
		
		byte data[] = new byte[dataLen];
		for (int j = 0; j < data.length; j++) {
			data[j] = (byte) (j%10 + 65);
		}
		Value idata = new Value(data);
		
		m.put(colf, colq, idata);
		
		mutations[i] = m;
	}

	public int getEstimatedBytesPerEntry() {
		return keyLen + colFamLen + colQualLen + dataLen;
	}

	public void clear() {
		key = null;
		colf = null;
		colq = null;
		mutations = null;
	}

	int getNumPasses() {
		return passes;
	}
	
	String getName() {
		return "Mutation "+keyLen+" "+colFamLen+" "+colQualLen+" "+dataLen;
	}
}


class IntObjectMemoryUsageTest extends MemoryUsageTest {

	//private int index;
	private int passes;
	private Object data[];

	static class SimpleObject {
		int d;
		SimpleObject(int d){
			this.d = d;
		}
	}
	
	IntObjectMemoryUsageTest(int numPasses){
		//index = 0;
		this.passes = numPasses;
	}
	
	void init() {
		data = new Object[passes];
	}
	
	void addEntry(int i) {
		data[i] = new SimpleObject(i);
		
	}

	void clear() {
		@SuppressWarnings("unused")
		IntObjectMemoryUsageTest data = null;
	}

	int getEstimatedBytesPerEntry() {
		return 4;
	}

	String getName() {
		return "int obj";
	}

	int getNumPasses() {
		return passes;
	}
	
}

public class EstimateInMemMapOverhead {
	
	

	
	private static void runTest(int numEntries, int keyLen, int colFamLen, int colQualLen, int dataLen) {
		new IntObjectMemoryUsageTest(numEntries).run();
		new InMemoryMapMemoryUsageTest(numEntries, keyLen, colFamLen, colQualLen, dataLen).run();
		new TextMemoryUsageTest(numEntries, keyLen, colFamLen, colQualLen, dataLen).run();
		new MutationMemoryUsageTest(numEntries, keyLen, colFamLen, colQualLen, dataLen).run();
	}
	
	public static void main(String[] args) throws InterruptedException {
		
		//System.getProperties().list(System.out);
		
		
		
		runTest(10000, 10, 4, 4, 20);
		runTest(100000, 10, 4, 4, 20);
		runTest(500000, 10, 4, 4, 20);
		runTest(1000000, 10, 4, 4, 20);
		runTest(2000000, 10, 4, 4, 20);
		
		runTest(10000, 20, 5, 5, 500);
		runTest(100000, 20, 5, 5, 500);
		runTest(500000, 20, 5, 5, 500);
		runTest(1000000, 20, 5, 5, 500);
		runTest(2000000, 20, 5, 5, 500);
		
		runTest(10000, 40, 10, 10, 1000);
		runTest(100000, 40, 10, 10, 1000);
		runTest(500000, 40, 10, 10, 1000);
		runTest(1000000, 40, 10, 10, 1000);
		runTest(2000000, 40, 10, 10, 1000);
	}

	

}
