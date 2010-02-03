package cloudbase.core.data;

import java.util.TreeSet;

import junit.framework.TestCase;

import org.apache.hadoop.io.Text;

public class KeyExtentTest extends TestCase{
	KeyExtent nke(String t, String er, String per){
		return new KeyExtent(new Text(t), 
				er == null ? null : new Text(er), 
				per == null ? null : new Text(per));
	}
	
	public void testDecodingMetadataRow(){
		Text flattenedExtent = new Text("foo;bar");
		
		KeyExtent ke = new KeyExtent(flattenedExtent, (Text)null);
		
		assertTrue(ke.getEndRow().equals(new Text("bar")));
		assertTrue(ke.getTableName().equals(new Text("foo")));
		assertTrue(ke.getPrevEndRow() == null);
		
		
		flattenedExtent = new Text("foo<");
		
		ke = new KeyExtent(flattenedExtent, (Text)null);
		
		assertTrue(ke.getEndRow() == null);
		assertTrue(ke.getTableName().equals(new Text("foo")));
		assertTrue(ke.getPrevEndRow() == null);
		
		
		flattenedExtent = new Text("foo;bar;");
		
		ke = new KeyExtent(flattenedExtent, (Text)null);
		
		assertTrue(ke.getEndRow().equals(new Text("bar;")));
		assertTrue(ke.getTableName().equals(new Text("foo")));
		assertTrue(ke.getPrevEndRow() == null);
		
	}
	
	public void testFindContainingExtents(){
		TreeSet<KeyExtent> set0 = new TreeSet<KeyExtent>();
		
		assertTrue(KeyExtent.findContainingExtent(nke("t",null,null), set0) == null);
		assertTrue(KeyExtent.findContainingExtent(nke("t","1","0"), set0) == null);
		assertTrue(KeyExtent.findContainingExtent(nke("t","1",null), set0) == null);
		assertTrue(KeyExtent.findContainingExtent(nke("t",null,"0"), set0) == null);
		
		TreeSet<KeyExtent> set1 = new TreeSet<KeyExtent>();
		
		set1.add(nke("t",null,null));
		
		assertTrue(KeyExtent.findContainingExtent(nke("t",null,null), set1).equals(nke("t",null,null)));
		assertTrue(KeyExtent.findContainingExtent(nke("t","1","0"), set1).equals(nke("t",null,null)));
		assertTrue(KeyExtent.findContainingExtent(nke("t","1",null), set1).equals(nke("t",null,null)));
		assertTrue(KeyExtent.findContainingExtent(nke("t",null,"0"), set1).equals(nke("t",null,null)));
		
		TreeSet<KeyExtent> set2 = new TreeSet<KeyExtent>();
		
		set2.add(nke("t","g",null));
		set2.add(nke("t",null,"g"));
		
		assertTrue(KeyExtent.findContainingExtent(nke("t",null,null), set2) == null);
		assertTrue(KeyExtent.findContainingExtent(nke("t","c","a"), set2).equals(nke("t","g",null)));
		assertTrue(KeyExtent.findContainingExtent(nke("t","c",null), set2).equals(nke("t","g",null)));
		
		assertTrue(KeyExtent.findContainingExtent(nke("t","g","a"), set2).equals(nke("t","g",null)));
		assertTrue(KeyExtent.findContainingExtent(nke("t","g",null), set2).equals(nke("t","g",null)));
		
		assertTrue(KeyExtent.findContainingExtent(nke("t","h","a"), set2) == null);
		assertTrue(KeyExtent.findContainingExtent(nke("t","h",null), set2) == null);

		assertTrue(KeyExtent.findContainingExtent(nke("t","z","f"), set2) == null);
		assertTrue(KeyExtent.findContainingExtent(nke("t",null,"f"), set2) == null);
		
		assertTrue(KeyExtent.findContainingExtent(nke("t","z","g"), set2).equals(nke("t",null,"g")));
		assertTrue(KeyExtent.findContainingExtent(nke("t",null,"g"), set2).equals(nke("t",null,"g")));
		
		assertTrue(KeyExtent.findContainingExtent(nke("t","z","h"), set2).equals(nke("t",null,"g")));
		assertTrue(KeyExtent.findContainingExtent(nke("t",null,"h"), set2).equals(nke("t",null,"g")));
		
		
		TreeSet<KeyExtent> set3 = new TreeSet<KeyExtent>();
		
		set3.add(nke("t","g",null));
		set3.add(nke("t","s","g"));
		set3.add(nke("t",null,"s"));
		
		assertTrue(KeyExtent.findContainingExtent(nke("t",null,null), set3) == null);
		
		assertTrue(KeyExtent.findContainingExtent(nke("t","g", null), set3).equals(nke("t","g",null)));
		assertTrue(KeyExtent.findContainingExtent(nke("t","s", "g"), set3).equals(nke("t","s","g")));
		assertTrue(KeyExtent.findContainingExtent(nke("t",null, "s"), set3).equals(nke("t",null,"s")));
		
		assertTrue(KeyExtent.findContainingExtent(nke("t","t", "g"), set3) == null);
		assertTrue(KeyExtent.findContainingExtent(nke("t","t", "f"), set3) == null);
		assertTrue(KeyExtent.findContainingExtent(nke("t","s", "f"), set3) == null);
		
		assertTrue(KeyExtent.findContainingExtent(nke("t","r", "h"), set3).equals(nke("t","s","g")));
		assertTrue(KeyExtent.findContainingExtent(nke("t","s", "h"), set3).equals(nke("t","s","g")));
		assertTrue(KeyExtent.findContainingExtent(nke("t","r", "g"), set3).equals(nke("t","s","g")));
		
		assertTrue(KeyExtent.findContainingExtent(nke("t",null, "t"), set3).equals(nke("t",null,"s")));
		assertTrue(KeyExtent.findContainingExtent(nke("t",null, "r"), set3) == null);
		
		assertTrue(KeyExtent.findContainingExtent(nke("t","f", null), set3).equals(nke("t","g",null)));
		assertTrue(KeyExtent.findContainingExtent(nke("t","h", null), set3) == null);
		
		
		TreeSet<KeyExtent> set4 = new TreeSet<KeyExtent>();
		
		set4.add(nke("t1","d",null));
		set4.add(nke("t1","q","d"));
		set4.add(nke("t1",null,"q"));
		set4.add(nke("t2","g",null));
		set4.add(nke("t2","s","g"));
		set4.add(nke("t2",null,"s"));

		
		assertTrue(KeyExtent.findContainingExtent(nke("t",null,null), set4) == null);
		assertTrue(KeyExtent.findContainingExtent(nke("z",null,null), set4) == null);
		assertTrue(KeyExtent.findContainingExtent(nke("t11",null,null), set4) == null);
		assertTrue(KeyExtent.findContainingExtent(nke("t1",null,null), set4) == null);
		assertTrue(KeyExtent.findContainingExtent(nke("t2",null,null), set4) == null);
		
		assertTrue(KeyExtent.findContainingExtent(nke("t","g", null), set4) == null);
		assertTrue(KeyExtent.findContainingExtent(nke("z","g", null), set4) == null);
		assertTrue(KeyExtent.findContainingExtent(nke("t11","g", null), set4) == null);
		assertTrue(KeyExtent.findContainingExtent(nke("t1","g", null), set4) == null);
		
		assertTrue(KeyExtent.findContainingExtent(nke("t2","g", null), set4).equals(nke("t2","g",null)));
		assertTrue(KeyExtent.findContainingExtent(nke("t2","s", "g"), set4).equals(nke("t2","s","g")));
		assertTrue(KeyExtent.findContainingExtent(nke("t2",null, "s"), set4).equals(nke("t2",null,"s")));
		
		assertTrue(KeyExtent.findContainingExtent(nke("t1","d", null), set4).equals(nke("t1","d",null)));
		assertTrue(KeyExtent.findContainingExtent(nke("t1","q", "d"), set4).equals(nke("t1","q","d")));
		assertTrue(KeyExtent.findContainingExtent(nke("t1",null, "q"), set4).equals(nke("t1",null,"q")));
		
		
		
	}
}
