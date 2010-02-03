package cloudbase.core.filter;

import java.util.HashSet;

import org.apache.hadoop.io.Text;

import cloudbase.core.data.Column;
import cloudbase.core.data.Value;
import cloudbase.core.data.Key;

import junit.framework.TestCase;

public class ColumnFilterTest extends TestCase{
	
	Key nk(String row, String cf, String cq){
		return new Key(new Text(row), new Text(cf), new Text(cq));
	}
	
	Column nc(String cf){
		return new Column(cf.getBytes(), null, null);
	}
	
	Column nc(String cf, String cq){
		return new Column(cf.getBytes(), cq.getBytes(), null);
	}
	
	public void test1(){
		HashSet<Column> columns = new HashSet<Column>();
		
		columns.add(nc("cf1"));
		
		ColumnFilter cf = new ColumnFilter(columns);
		
		assertTrue(cf.accept(nk("r1","cf1","cq1"), new Value(new byte[0])));
		assertFalse(cf.accept(nk("r1","cf2","cq1"), new Value(new byte[0])));
		
	}
	
	public void test2(){
		HashSet<Column> columns = new HashSet<Column>();
		
		columns.add(nc("cf1"));
		columns.add(nc("cf2", "cq1"));
		
		ColumnFilter cf = new ColumnFilter(columns);
		
		assertTrue(cf.accept(nk("r1","cf1","cq1"), new Value(new byte[0])));
		assertTrue(cf.accept(nk("r1","cf2","cq1"), new Value(new byte[0])));
		assertFalse(cf.accept(nk("r1","cf2","cq2"), new Value(new byte[0])));
	}
	
	
	public void test3(){
		HashSet<Column> columns = new HashSet<Column>();
		
		
		columns.add(nc("cf2", "cq1"));
		
		ColumnFilter cf = new ColumnFilter(columns);
		
		assertFalse(cf.accept(nk("r1","cf1","cq1"), new Value(new byte[0])));
		assertTrue(cf.accept(nk("r1","cf2","cq1"), new Value(new byte[0])));
		assertFalse(cf.accept(nk("r1","cf2","cq2"), new Value(new byte[0])));
	}
}
