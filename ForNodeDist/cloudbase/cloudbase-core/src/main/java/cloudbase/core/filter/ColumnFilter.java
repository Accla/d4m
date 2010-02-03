package cloudbase.core.filter;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import org.apache.hadoop.io.WritableComparator;

import cloudbase.core.data.Column;
import cloudbase.core.data.Key;
import cloudbase.core.data.Value;

public class ColumnFilter implements Filter {
	private HashSet<HashableByteWrapper> matchFamilies;
	private boolean scanColumns;
	private HashSet<ColFQ> columns;
	private HashableByteWrapper columnFamily = new HashableByteWrapper();
	private ColFQ column = new ColFQ();

	
	private static class HashableByteWrapper {
		
		byte ba[];
		int off;
		int len;
		
		 private static int hashBytes(byte[] bytes, int offset, int length) {
			    int hash = 1;
			    for (int i = 0; i < length; i++)
			      hash = (31 * hash) + (int)bytes[i+offset];
			    return hash;
			  }
		 
		 void set(byte a[], int o, int l){
			 this.ba = a;
			 this.off = o;
			 this.len = l;	 
		 }
		 
		 HashableByteWrapper(byte a[], int o, int l){
			set(a,o,l); 
		 }
		 
		 public HashableByteWrapper() {
			
		}

		@Override
		public int hashCode() {
			return hashBytes(ba, off, len);
		}
		 
		@Override
		public boolean equals(Object obj) {
			HashableByteWrapper other = (HashableByteWrapper) obj;
			if(len != other.len){
				return false;
			}
			
			return WritableComparator.compareBytes(ba, off, len, other.ba, other.off, other.len) == 0;
		}
	}
	
	private static class ColFQ {
		HashableByteWrapper colf = new HashableByteWrapper();
		
		HashableByteWrapper colq = new HashableByteWrapper();
		
		/** Compute hash for binary data. */
		 
		ColFQ(){}
		ColFQ(Column c){
			colf.set(c.columnFamily, 0, c.columnFamily.length);
			colq.set(c.columnQualifier, 0, c.columnQualifier.length);
		}

		void set(Key key){
			colf.set(key.getKeyData(), key.getColumnFamilyOffset(), key.getColumnFamilyLen());
			colq.set(key.getKeyData(), key.getColumnQualifierOffset(), key.getColumnQualifierLen());
		}
		
		@Override
		public int hashCode() {
			return colf.hashCode() + colq.hashCode();
		}
		
		@Override
		public boolean equals(Object obj) {
			ColFQ other = (ColFQ) obj;
			return colf.equals(other.colf) && colq.equals(other.colq);
		}
	}
	
	public ColumnFilter(HashSet<Column> columns) {
		this.init(columns);
	}
	
	public boolean accept(Key key, Value v) {
		if(!scanColumns) 
			return true;
		
		columnFamily.set(key.getKeyData(), key.getColumnFamilyOffset(), key.getColumnFamilyLen());
		column.set(key);
		
		return (matchFamilies.size() > 0 && matchFamilies.contains(columnFamily)) || columns.contains(column);
	}
	
    public void init(HashSet<Column> columns) {
		scanColumns = columns.size() > 0;
		matchFamilies = new HashSet<HashableByteWrapper>();
		this.columns = new HashSet<ColFQ>();
		
		for(Iterator<Column> iter = columns.iterator(); iter.hasNext();) {
			Column col = iter.next();
			if(col.columnQualifier != null)
				this.columns.add(new ColFQ(col));
			if(col.columnQualifier == null || col.columnQualifier.length == 0 || 
			   (col.columnQualifier.length == 1 && col.columnQualifier[0] == '*')) {  // match the whole family
				matchFamilies.add(new HashableByteWrapper(col.columnFamily, 0, col.columnFamily.length));
				//System.out.println("matching on family: " + words[0]);
			}
		}		
	}
	
	@Override
	public void init(Map<String, String> options) {
		// TODO Auto-generated method stub
		
	}

}
