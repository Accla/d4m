package cloudbase.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.apache.hadoop.io.Text;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Test;

import cloudbase.core.constraints.MetadataConstraints;
import cloudbase.core.data.Mutation;
import cloudbase.core.data.Value;


public class MetadataConstraintsTest {

	@Test
	public void testCheck() {
	    Logger.getLogger("cloudbase.core.conf.CBConfiguration").setLevel(Level.ERROR);
		Mutation m = new Mutation(new Text("test_table;foo"));
		m.put(CBConstants.METADATA_TABLE_TABLET_COLUMN_FAMILY, CBConstants.METADATA_TABLE_TABLET_PREV_ROW_COLUMN_NAME, new Value("1foo".getBytes()));
		
		MetadataConstraints mc = new MetadataConstraints();
		 
		List<Short> violations = mc.check(m);
		
		assertNotNull(violations);
		assertEquals(1, violations.size());
		assertEquals(Short.valueOf((short)3), violations.get(0));
		
		
		m = new Mutation(new Text("test_table:foo"));
		m.put(CBConstants.METADATA_TABLE_TABLET_COLUMN_FAMILY, CBConstants.METADATA_TABLE_TABLET_PREV_ROW_COLUMN_NAME, new Value("1poo".getBytes()));
			 
		violations = mc.check(m);
		
		assertNotNull(violations);
		assertEquals(1, violations.size());
		assertEquals(Short.valueOf((short)4), violations.get(0));
		
		
		m = new Mutation(new Text("test_table;foo"));
		m.put(new Text("bad_column_name"), new Text(""), new Value("e".getBytes()));
			 
		violations = mc.check(m);
		
		assertNotNull(violations);
		assertEquals(1, violations.size());
		assertEquals(Short.valueOf((short)2), violations.get(0));
		
		
		m = new Mutation(new Text("!A<"));
		m.put(CBConstants.METADATA_TABLE_TABLET_COLUMN_FAMILY, CBConstants.METADATA_TABLE_TABLET_PREV_ROW_COLUMN_NAME, new Value("1poo".getBytes()));
			 
		violations = mc.check(m);
		
		assertNotNull(violations);
		assertEquals(1, violations.size());
		assertEquals(Short.valueOf((short)5),violations.get(0));
		
		
		
		m = new Mutation(new Text("test_table;foo"));
		m.put(CBConstants.METADATA_TABLE_TABLET_COLUMN_FAMILY, CBConstants.METADATA_TABLE_TABLET_PREV_ROW_COLUMN_NAME, new Value("".getBytes()));
			 
		violations = mc.check(m);
		
		assertNotNull(violations);
		assertEquals(1, violations.size());
		assertEquals(Short.valueOf((short)6), violations.get(0));
	}

}
