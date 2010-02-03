package cloudbase.core.data;

import junit.framework.TestCase;

public class KeyTest extends TestCase {
	public void testDeletedCompare(){
		Key k1 = new Key("r1".getBytes(), "cf".getBytes(), "cq".getBytes(), new byte[0], 0, false);
		Key k2 = new Key("r1".getBytes(), "cf".getBytes(), "cq".getBytes(), new byte[0], 0, false);
		Key k3 = new Key("r1".getBytes(), "cf".getBytes(), "cq".getBytes(), new byte[0], 0, true);
		Key k4 = new Key("r1".getBytes(), "cf".getBytes(), "cq".getBytes(), new byte[0], 0, true);
		
		assertTrue(k1.compareTo(k2) == 0);
		assertTrue(k3.compareTo(k4) == 0);
		assertTrue(k1.compareTo(k3) > 0);
		assertTrue(k3.compareTo(k1) < 0);
	}
}
