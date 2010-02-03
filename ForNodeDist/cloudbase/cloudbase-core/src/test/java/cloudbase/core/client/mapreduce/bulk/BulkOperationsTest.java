package cloudbase.core.client.mapreduce.bulk;

import java.util.SortedSet;
import java.util.TreeSet;

import junit.framework.TestCase;

import org.apache.hadoop.io.Text;

import cloudbase.core.data.KeyExtent;

public class BulkOperationsTest extends TestCase {
	
	private KeyExtent createKeyExtent(String tname, String er, String per){
		return new KeyExtent(new Text(tname), er == null ? null : new Text(er), per == null ? null : new Text(per));
	}
	
	private SortedSet<KeyExtent> createKeyExtents(String data [][]){
		
		TreeSet<KeyExtent> extents = new TreeSet<KeyExtent>();
		for (String[] exdata : data) {
			extents.add(createKeyExtent(exdata[0], exdata[1], exdata[2]));
		}
		
		return extents;
	}
	
	
	private void runTest(String beginRange, String endRange){
		KeyExtent ke = createKeyExtent("foo",endRange, beginRange);
		
		SortedSet<KeyExtent> children = createKeyExtents(new String[][]{
				new String[]{"foo", endRange, beginRange}
		});
		
		assertTrue(BulkOperations.isContiguousRange(ke, children));
		
		children = createKeyExtents(new String[][]{
				new String[]{"foo", "r1", beginRange},
				new String[]{"foo", endRange, "r1"}
		});
		
		assertTrue(BulkOperations.isContiguousRange(ke, children));
		
		children = createKeyExtents(new String[][]{
				new String[]{"foo", "r1", beginRange},
				new String[]{"foo", endRange, "r2"}
		});
		
		assertFalse(BulkOperations.isContiguousRange(ke, children));

		children = createKeyExtents(new String[][]{
				new String[]{"foo", "r1", beginRange},
				new String[]{"foo", (endRange == null ? "r2":endRange+"Z"), "r1"}
		});
		
		assertFalse(BulkOperations.isContiguousRange(ke, children));

		children = createKeyExtents(new String[][]{
				new String[]{"foo", "r1", (beginRange == null ? "r0":"a"+beginRange)},
				new String[]{"foo", endRange, "r1"}
		});
		
		assertFalse(BulkOperations.isContiguousRange(ke, children));
		
		children = createKeyExtents(new String[][]{
				new String[]{"foo", "r1", beginRange},
				new String[]{"foo", "r2", "r1"},
				new String[]{"foo", endRange, "r2"}
		});
		
		assertTrue(BulkOperations.isContiguousRange(ke, children));
		
		children = createKeyExtents(new String[][]{
				new String[]{"foo", "r1", beginRange},
				new String[]{"foo", "r2", "r1"},
				new String[]{"foo", endRange, "r1"}
		});
		
		assertFalse(BulkOperations.isContiguousRange(ke, children));
		
		children = createKeyExtents(new String[][]{
				new String[]{"foo", "r1", beginRange},
				new String[]{"foo", "r2", null},
				new String[]{"foo", endRange, "r2"}
		});
		
		assertFalse(BulkOperations.isContiguousRange(ke, children));
		
		if(endRange == null){
			children = createKeyExtents(new String[][]{
					new String[]{"foo", "r1", beginRange},
					new String[]{"foo", null, "r1"},
					new String[]{"foo", endRange, "r2"}
			});
			
			assertFalse(BulkOperations.isContiguousRange(ke, children));
		}
		
		children = createKeyExtents(new String[][]{
				new String[]{"foo", "r1", beginRange},
				new String[]{"foo", "r2", "r1"},
				new String[]{"foo", "r3", "r2"},
				new String[]{"foo", endRange, "r3"}
		});
		
		assertTrue(BulkOperations.isContiguousRange(ke, children));
		
	}
	
	public void testICR1(){
		runTest(null, null);
		runTest(null, "r4");
		runTest("r0", null);
		runTest("r0", "r4");
	}
}
