package cloudbase.core.client;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import junit.framework.TestCase;

import org.apache.hadoop.io.Text;

import cloudbase.core.data.Key;
import cloudbase.core.data.Range;


public class RangeTest  extends TestCase {
	private Range nr(String k1, String k2){
		Key ik1 = null;
		if(k1 != null)
			ik1 = new Key(new Text(k1),0l);
		
		Key ik2 = null;
		if(k2 != null)
			ik2 = new Key(new Text(k2),0l);
		
		return new Range(ik1, ik2);
	}
	
	private List<Range> nrl(Range... ranges){
		return Arrays.asList(ranges);
	}
	
	private void check(List<Range> rl, List<Range> expected) {
		HashSet<Range> s1 = new HashSet<Range>(rl); 
		HashSet<Range> s2 = new HashSet<Range>(expected);
		
		//System.out.println(rl+" "+expected);
		
		assertTrue("got : "+rl+" expected : "+expected, s1.equals(s2));
	}
	
	public void testMergeOverlapping1(){
		List<Range> rl = nrl(nr("a","c"), nr("a","b"));
		List<Range> expected = nrl(nr("a","c"));
		check(Range.mergeOverlapping(rl), expected);
	}

	public void testMergeOverlapping2(){
		List<Range> rl = nrl(nr("a","c"), nr("d","f"));
		List<Range> expected = nrl(nr("a","c"), nr("d","f"));
		check(Range.mergeOverlapping(rl), expected);
	}
	
	public void testMergeOverlapping3(){
		List<Range> rl = nrl(nr("a","e"), nr("b","f"), nr("c","r"),nr("g","j"),nr("t","x"));
		List<Range> expected = nrl(nr("a","r"), nr("t","x"));
		check(Range.mergeOverlapping(rl), expected);
	}
	
	public void testMergeOverlapping4(){
		List<Range> rl = nrl(nr("a","e"), nr("b","f"), nr("c","r"),nr("g","j"));
		List<Range> expected = nrl(nr("a","r"));
		check(Range.mergeOverlapping(rl), expected);
	}
	
	public void testMergeOverlapping5(){
		List<Range> rl = nrl(nr("a","e"));
		List<Range> expected = nrl(nr("a","e"));
		check(Range.mergeOverlapping(rl), expected);
	}
	
	public void testMergeOverlapping6(){
		List<Range> rl = nrl();
		List<Range> expected = nrl();
		check(Range.mergeOverlapping(rl), expected);
	}
	
	public void testMergeOverlapping7(){
		List<Range> rl = nrl(nr("a","e"), nr("g","q"), nr("r","z"));
		List<Range> expected = nrl(nr("a","e"), nr("g","q"), nr("r","z"));
		check(Range.mergeOverlapping(rl), expected);
	}
	
	public void testMergeOverlapping8(){
		List<Range> rl = nrl(nr("a","c"), nr("a","c"));
		List<Range> expected = nrl(nr("a","c"));
		check(Range.mergeOverlapping(rl), expected);
	}
	
	public void testMergeOverlapping9(){
		List<Range> rl = nrl(nr(null,null));
		List<Range> expected = nrl(nr(null,null));
		check(Range.mergeOverlapping(rl), expected);
	}
	
	public void testMergeOverlapping10(){
		List<Range> rl = nrl(nr(null,null), nr("a","c"));
		List<Range> expected = nrl(nr(null,null));
		check(Range.mergeOverlapping(rl), expected);
	}
	
	public void testMergeOverlapping11(){
		List<Range> rl = nrl(nr("a","c"), nr(null,null));
		List<Range> expected = nrl(nr(null,null));
		check(Range.mergeOverlapping(rl), expected);
	}
	
	public void testMergeOverlapping12(){
		List<Range> rl = nrl(nr("b","d"), nr("c",null));
		List<Range> expected = nrl(nr("b",null));
		check(Range.mergeOverlapping(rl), expected);
	}
	
	public void testMergeOverlapping13(){
		List<Range> rl = nrl(nr("b","d"), nr("a",null));
		List<Range> expected = nrl(nr("a",null));
		check(Range.mergeOverlapping(rl), expected);
	}
	
	public void testMergeOverlapping14(){
		List<Range> rl = nrl(nr("b","d"), nr("e",null));
		List<Range> expected = nrl(nr("b","d"), nr("e",null));
		check(Range.mergeOverlapping(rl), expected);
	}
	
	public void testMergeOverlapping15(){
		List<Range> rl = nrl(nr("b","d"), nr("e",null), nr("c","f"));
		List<Range> expected = nrl(nr("b",null));
		check(Range.mergeOverlapping(rl), expected);
	}
	
	public void testMergeOverlapping16(){
		List<Range> rl = nrl(nr("b","d"), nr("f",null), nr("c","e"));
		List<Range> expected = nrl(nr("b","e"), nr("f",null));
		check(Range.mergeOverlapping(rl), expected);
	}
	
	public void testMergeOverlapping17(){
		List<Range> rl = nrl(nr("b","d"), nr("r",null), nr("c","e"), nr("g","t"));
		List<Range> expected = nrl(nr("b","e"), nr("g",null));
		check(Range.mergeOverlapping(rl), expected);
	}
	
	
	public void testMergeOverlapping18(){
		List<Range> rl = nrl(nr(null,"d"), nr("r",null), nr("c","e"), nr("g","t"));
		List<Range> expected = nrl(nr(null,"e"), nr("g", null));
		check(Range.mergeOverlapping(rl), expected);
	}
	
	public void testMergeOverlapping19(){
		List<Range> rl = nrl(nr(null,"d"), nr("r",null), nr("c","e"), nr("g","t"), nr("d","h"));
		List<Range> expected = nrl(nr(null,null));
		check(Range.mergeOverlapping(rl), expected);
	}
}
