package cloudbase.core.tabletserver.iterators;

import java.io.IOException;
import java.util.Map;

import org.apache.commons.collections.buffer.PriorityBuffer;
import org.apache.hadoop.io.Text;

import cloudbase.core.data.Value;
import cloudbase.core.data.Key;
import cloudbase.core.data.KeyExtent;

/**
 * An iterator capable of iterating over other iterators in sorted order.
 * 
 *
 */

public class MultiIterator implements SortedKeyValueIterator<Key, Value> {

	private Text endRow;
	private Key endKey;
	private Text prevEndRow;
	private PriorityBuffer heap;
	private SortedKeyValueIterator<Key, Value>[] iters;
	
	private static class Index implements Comparable<Index>{
		SortedKeyValueIterator<Key, Value> iter;

		public Index(SortedKeyValueIterator<Key, Value> iter) {
			this.iter = iter;
		}

		public int compareTo(Index o) {
			return iter.getTopKey().compareTo(o.iter.getTopKey());
		}
	}
	
	private boolean lteEndRow(Key k){
		return (endKey == null || k.compareTo(endKey, 4) <= 0) && 
			(endRow == null || k.compareRow(endRow) <= 0);
	}
	
	private void init() throws IOException {
		for(int i = 0; i < iters.length; i++)
		{
			if(iters[i].hasTop() && lteEndRow(iters[i].getTopKey())){
				heap.add(new Index(iters[i]));
			}
		}
		
		while(hasTop() && getTopKey().compareRow(prevEndRow) < 1) {
			//System.out.println("skipping "+getTopKey());
			next();
		}
	}
	
	public MultiIterator(SortedKeyValueIterator<Key, Value>[] iters, boolean init) throws IOException{
		this(iters, null, null, null, init);
	}
	
	public MultiIterator(SortedKeyValueIterator<Key, Value>[] iters, KeyExtent extent, boolean init) throws IOException{
		this(iters, extent.getEndRow(), extent.getPrevEndRow(), null, init);
	}
	
	public MultiIterator(SortedKeyValueIterator<Key, Value>[] iters, KeyExtent extent, Key endKey, boolean init) throws IOException{
		this(iters, extent.getEndRow(), extent.getPrevEndRow(), endKey, init);
	}

	public MultiIterator(SortedKeyValueIterator<Key, Value>[] iters, Text endRow, boolean init) throws IOException{
		this(iters, endRow, null, null, init);
	}
	
	public MultiIterator(SortedKeyValueIterator<Key, Value>[] iters, Text endRow, Text prevEndRow, boolean init) throws IOException{
		this(iters, endRow, prevEndRow, null, init);
	}
	
	public MultiIterator(SortedKeyValueIterator<Key, Value>[] iters, Text endRow, Text prevEndRow, Key endKey, boolean init) throws IOException{
		this.prevEndRow = prevEndRow;
		this.endRow = endRow;
		this.endKey = endKey;
		this.iters = iters;
		
		if(prevEndRow == null)
			this.prevEndRow = new Text();
		else
			this.prevEndRow = new Text(prevEndRow);
		
		if (endRow==null)
			this.endRow = null;
		else
			this.endRow = new Text(endRow);
		
		heap = new PriorityBuffer(iters.length == 0 ? 1 : iters.length);
		
		if(init){
			init();
		}
	}
	
	public Key getTopKey() {
		return ((Index)heap.get()).iter.getTopKey();
	}

	public Value getTopValue() {
		return ((Index)heap.get()).iter.getTopValue();
	}

	public boolean hasTop() {
		return heap.size() > 0;
	}

	public void next() throws IOException {
		Index idx = (Index) heap.remove();
		
		idx.iter.next();
		
		if(idx.iter.hasTop() && lteEndRow(idx.iter.getTopKey())){
			heap.add(idx);
		}
	}

	public void seek(Key seekKey) throws IOException {
		heap.clear();

		if(seekKey.compareRow(prevEndRow) < 0){
			//System.out.println("seekKey lt PER ");
			seekKey = new Key(prevEndRow);
		}
		
		for(int i = 0; i < iters.length; i++)
		{
			iters[i].seek(seekKey);
			
			if(iters[i].hasTop() && lteEndRow(iters[i].getTopKey())){
				heap.add(new Index(iters[i]));
			}
		}

		// always skip the previous tablet's end row
		while(hasTop() && getTopKey().compareRow(prevEndRow) < 1) {
			//System.out.println("skipping "+getTopKey());
			next();
		}
	}
	
	public void init(SortedKeyValueIterator<Key, Value> source, Map<String, String> options) throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setEndKey(Key key) {
		this.endKey = new Key(key);
		for(SortedKeyValueIterator<Key,Value> iter:iters)
		{
			iter.setEndKey(key);
		}
	}

}
