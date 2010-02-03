package cloudbase.core.tabletserver.iterators;

import java.io.IOException;
import java.util.Map;

import org.apache.hadoop.io.Text;

import cloudbase.core.data.Key;
import cloudbase.core.data.Value;
import junit.framework.TestCase;

class InfiniteIterator implements SortedKeyValueIterator<Key, Value> {

	long top = 0;
	
	@Override
	public Key getTopKey() {
		return new Key(new Text(String.format("%016d", top)));
	}

	@Override
	public Value getTopValue() {
		return new Value(String.format("%016d", top).getBytes());
	}

	@Override
	public boolean hasTop() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void init(SortedKeyValueIterator<Key, Value> source, Map<String, String> options) throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void next() throws IOException {
		top++;
	}

	@Override
	public void seek(Key key) throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setEndKey(Key key) {
		throw new UnsupportedOperationException();
	}
	
}

class InfiniteIterator2 implements SortedKeyValueIterator<Key, Value> {

	long toprow = 0;
	long ts = Long.MAX_VALUE;
	
	private long start;
	private long switchTime;
	
	InfiniteIterator2(long switchTime){
		this.start = System.currentTimeMillis();
		this.switchTime = switchTime;
	}
	
	@Override
	public Key getTopKey() {
		Key key = new Key(new Text(String.format("%016d", toprow)));
		key.setTimestamp(ts);
		return key;
	}

	@Override
	public Value getTopValue() {
		return new Value("val".getBytes());
	}

	@Override
	public boolean hasTop() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void init(SortedKeyValueIterator<Key, Value> source, Map<String, String> options) throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void next() throws IOException {
		if(System.currentTimeMillis() - start > switchTime){
			toprow++;
			ts = Long.MAX_VALUE;
		}else{
			ts--;
		}
		
		
	}

	@Override
	public void seek(Key key) throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setEndKey(Key key) {
		throw new UnsupportedOperationException();
	}
	
}

public class TimeLimitIteratorTest extends TestCase{
	
	public void test1() throws IOException{
		
		for(int i = 0; i< 10; i++){
			InfiniteIterator ii = new InfiniteIterator();
			TimeLimitIterator tli = new TimeLimitIterator(100,1);

			long start = System.currentTimeMillis();

			tli.init(ii, null);

			int count = 0;

			long row = -7;

			while(tli.hasTop()){
				count++;

				row = Long.parseLong(tli.getTopKey().getRow().toString());

				tli.next();
			}
			
			long finish = System.currentTimeMillis();
			assertTrue(finish - start >= 100);

			assertTrue(count > 0);
			assertTrue(tli.getSource().hasTop());
			assertTrue(row + 1 == Long.parseLong(tli.getSource().getTopKey().getRow().toString()));
		}
	}
	
	public void test2() throws IOException{
		
		//this test verifies that limit iterator always returns at least one
	
	
		InfiniteIterator ii = new InfiniteIterator();
		TimeLimitIterator tli = new TimeLimitIterator(0,1);

		tli.init(ii, null);

		int count = 0;

		long row = -7;

		while(tli.hasTop()){
			count++;
			row = Long.parseLong(tli.getTopKey().getRow().toString());
			tli.next();
		}

		assertTrue(count == 1);
		assertTrue(tli.getSource().hasTop());
		assertTrue(row + 1 == Long.parseLong(tli.getSource().getTopKey().getRow().toString()));
	}
	
	
	public void test3() throws IOException{
	
		InfiniteIterator2 ii = new InfiniteIterator2(300);
		TimeLimitIterator tli = new TimeLimitIterator(100,1);

		tli.init(ii, null);

		int count = 0;

		long row = -7;

		while(tli.hasTop()){
			count++;
			row = Long.parseLong(tli.getTopKey().getRow().toString());
			tli.next();
		}

		//System.out.println(count);
		
		assertTrue(count > 0);
		assertTrue(tli.getSource().hasTop());
		assertTrue(Long.parseLong(tli.getSource().getTopKey().getRow().toString()) == 1);
		assertTrue(row == 0);
	}
	
	public void test4() throws IOException{
		
		for(int i = 1; i<=10 ; i++){
	
			InfiniteIterator2 ii = new InfiniteIterator2(50);
			TimeLimitIterator tli = new TimeLimitIterator(0,i);

			tli.init(ii, null);

			int count = 0;

			long row = -7;

			while(tli.hasTop()){
				count++;
				row = Long.parseLong(tli.getTopKey().getRow().toString());
				tli.next();
			}

			//System.out.println(count);

			assertTrue(count > 0);
			assertTrue(tli.getSource().hasTop());
			assertTrue(Long.parseLong(tli.getSource().getTopKey().getRow().toString()) == i);
			assertTrue(row == i-1);
		}
	}
	
}
