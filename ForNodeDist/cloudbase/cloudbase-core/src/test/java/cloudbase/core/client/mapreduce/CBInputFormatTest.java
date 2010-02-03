package cloudbase.core.client.mapreduce;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;

import junit.framework.TestCase;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobConf;

import cloudbase.core.data.KeyExtent;

public class CBInputFormatTest extends TestCase {
	JobConf job = null;
	CBInputFormat cbif = null;
	
	TreeSet<KeyExtent> kes = new TreeSet<KeyExtent>();
	SortedMap<KeyExtent, String> locations = new TreeMap<KeyExtent, String>();
	List<CBInputSplit> splits = new ArrayList<CBInputSplit>();

	public void init(String startRow, String endRow) {
		job = new JobConf();
		cbif = new CBInputFormat();
		CBInputFormat.setTableName(job, "test");
		CBInputFormat.setStartRow(job, startRow);
		CBInputFormat.setStopRow(job, endRow);
	}
		
	public void reset() {
		kes.clear();
		locations.clear();
		splits.clear();
	}
	
	public void addExtent(String prevEndRow, String endRow, String id) {
		KeyExtent k = new KeyExtent(new Text("test"), endRow==null ? null : new Text(endRow), prevEndRow==null ? null : new Text(prevEndRow));
		kes.add(k);
		locations.put(k, id);
	}
	
	public boolean checkSplit(CBInputSplit cbis, String startRow, String stopRow, String loc) throws IOException {
		/* removed this testing since empty startRow and endRows are "", not null
		boolean ret = true;
		if (cbis.getStartRow()==null || startRow==null)
			if (cbis.getStartRow()!=null || startRow != null)
				return false;
		
		if (cbis.getStopRow()==null || stopRow==null)
			if (cbis.getStopRow()!=null || stopRow!=null)
				return false;
		*/
		return cbis.getStartRow().compareTo(startRow.getBytes(), 0, startRow.length())==0
			&& cbis.getStopRow().compareTo(stopRow.getBytes(), 0, stopRow.length())==0
			&& cbis.getLocations()[0].equals(loc);
	}
	
	public void printSplits(List<CBInputSplit> splits) throws IOException {
		//System.out.println("splits size "+splits.size());
		for (CBInputSplit s : splits) {
			assertTrue(s.getLocations().length==1);
			//System.out.println("start "+s.getStartRow()+" stop "+s.getStopRow()+" id "+s.getLocations()[0]);
		}
	}
	
	public void test1() throws IOException {
		if (cbif==null) init("b","e");
		reset();
		addExtent("a", "f", "0");

		cbif.createSplits(job, kes, locations, splits);
		printSplits(splits);
		
		assertTrue(splits.size()==1);
		assertTrue(checkSplit(splits.get(0), "b", "e", "0"));
	}

	public void test2() throws IOException {
		if (cbif==null) init("b","e");
		reset();
		addExtent(null, null, "1");

		cbif.createSplits(job, kes, locations, splits);
		printSplits(splits);
		
		assertTrue(splits.size()==1);
		assertTrue(checkSplit(splits.get(0), "b", "e", "1"));
	}
	
	public void test3() throws IOException {
		if (cbif==null) init("b","e");
		reset();
		addExtent(null, "f", "2");
		addExtent("f", null, "3");

		cbif.createSplits(job, kes, locations, splits);
		printSplits(splits);
		
		assertTrue(splits.size()==1);
		assertTrue(checkSplit(splits.get(0), "b", "e", "2"));
	}
	
	public void test4() throws IOException {
		if (cbif==null) init("b","e");
		reset();
		addExtent(null, "a", "4");
		addExtent("f", null, "6");

		cbif.createSplits(job, kes, locations, splits);
		printSplits(splits);
		
		assertTrue(splits.size()==0);
	}
	
	public void test5() throws IOException {
		if (cbif==null) init("b","e");
		reset();
		addExtent("a", "c", "7");
		addExtent("c", "f", "8");
		addExtent("f", null, "9");

		cbif.createSplits(job, kes, locations, splits);
		printSplits(splits);
		
		assertTrue(splits.size()==2);
		assertTrue(checkSplit(splits.get(0), "b", "c", "7"));
		assertTrue(checkSplit(splits.get(1), "c", "e", "8"));
	}
	
	public void test6() throws IOException {
		if (cbif==null) init("b","e");
		reset();
		addExtent(null, "c", "10");
		addExtent("c", null, "11");

		cbif.createSplits(job, kes, locations, splits);
		printSplits(splits);
		
		assertTrue(splits.size()==2);
		assertTrue(checkSplit(splits.get(0), "b", "c", "10"));
		assertTrue(checkSplit(splits.get(1), "c", "e", "11"));
	}
	
	public void test7() throws IOException {
		if (cbif==null) init("b","e");
		reset();
		addExtent(null, "a", "12");
		addExtent("a", null, "13");

		cbif.createSplits(job, kes, locations, splits);
		printSplits(splits);
		
		assertTrue(splits.size()==1);
		assertTrue(checkSplit(splits.get(0), "b", "e", "13"));
	}

	public void test8() throws IOException {
		if (cbif==null) init("b","e");
		reset();
		addExtent("a", "b", "14");
		addExtent("b", "c", "15");
		addExtent("c", "d", "16");
		addExtent("d", "e", "17");
		addExtent("e", "f", "18");
		
		cbif.createSplits(job, kes, locations, splits);
		printSplits(splits);
		
		assertTrue(splits.size()==4);
		assertTrue(checkSplit(splits.get(0), "b", "b", "14"));
		assertTrue(checkSplit(splits.get(1), "b", "c", "15"));
		assertTrue(checkSplit(splits.get(2), "c", "d", "16"));
		assertTrue(checkSplit(splits.get(3), "d", "e", "17"));
	}
	
	public void test9() throws IOException {
		if (cbif==null) init("","e");
		reset();
		
		addExtent(null, "a", "19");
		addExtent("a", "f", "20");
		addExtent("f", null, "21");

		cbif.createSplits(job, kes, locations, splits);
		printSplits(splits);
		
		assertTrue(splits.size()==2);
		assertTrue(checkSplit(splits.get(0), "", "a", "19"));
		assertTrue(checkSplit(splits.get(1), "a", "e", "20"));
	}

	public void test10() throws IOException {
		if (cbif==null) init("b","");
		reset();
		
		addExtent(null, "a", "22");
		addExtent("a", "f", "23");
		addExtent("f", null, "24");

		cbif.createSplits(job, kes, locations, splits);
		printSplits(splits);
		
		assertTrue(splits.size()==2);
		assertTrue(checkSplit(splits.get(0), "b", "f", "23"));
		assertTrue(checkSplit(splits.get(1), "f", "", "24"));
	}
	
	public void test11() throws IOException {
		if (cbif==null) init("","");
		reset();
		
		addExtent(null, "a", "25");
		addExtent("a", "f", "26");
		addExtent("f", null, "27");

		cbif.createSplits(job, kes, locations, splits);
		printSplits(splits);
		
		assertTrue(splits.size()==3);
		assertTrue(checkSplit(splits.get(0), "", "a", "25"));
		assertTrue(checkSplit(splits.get(1), "a", "f", "26"));
		assertTrue(checkSplit(splits.get(2), "f", "", "27"));
	}
	
	public void test12() throws IOException {
		if (cbif==null) init("","");
		reset();
		
		addExtent(null, null, "28");

		cbif.createSplits(job, kes, locations, splits);
		printSplits(splits);
		
		assertTrue(splits.size()==1);
		assertTrue(checkSplit(splits.get(0), "", "", "28"));
	}
	public void test13() throws IOException {
		if (cbif==null) init("","");
		reset();
		
		Collection<Text> splitRows = new ArrayList<Text>();
		String splitrows = "abcdefghijklmnopqrstuvwxyz";//[/]^_`ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		String keyextents = "aabbccddeeffgghhiijjkkllmmnnooppqqrrssttuuvvwwxxyyzz";
		for (int i = 0; i < splitrows.length(); ++i)
			splitRows.add(new Text(keyextents.substring(i, i+2)));
		int j = 0;
		addExtent(null, keyextents.substring(j, j+2), "what");
		//System.out.println("Adding extent from \'" + null + "\' to \'" + keyextents.substring(j, j+2) + "\'");
		for (j = 0; j < keyextents.length() - 2; j+=2)
		{
			addExtent(keyextents.substring(j, j+2), keyextents.substring(j+2, j+4), "anywhere" + keyextents.substring(j, j+2));
			//System.out.println("Adding extent from \'" + keyextents.substring(j, j+2) + "\' to \'" + keyextents.substring(j+2, j+4) + "\'");
		}
		addExtent(keyextents.substring(j, j+2), null, "anywhere");
		//System.out.println("Adding extent from \'" + keyextents.substring(j, j+2) + "\' to \'" + null + "\'");
		cbif.createSplitsForMaxMaps(job, splitRows, kes, locations, splits);
		//System.out.println(splits.size());
		assertTrue(splits.size()==27);			
	}
	public void test14() throws IOException {
		if (cbif==null) init("","");
		reset();
		
		Collection<Text> splitRows = new ArrayList<Text>();
		String[] startRows = new String[]{ null, "AAAA", "CCCC", "EEEE", "GGGG", "IIII", "KKKK"};
		String[] stopRows = new String[]{ "AAAA", "CCCC", "EEEE", "GGGG", "IIII", "KKKK", null};
		String[] locs = new String[]{"1.1.1.1+2705", "2.2.2.2+2703", "3.3.3.3+2703", 
									 "4.4.4.4+2705", "5.5.5.5+2703", "6.6.6.6+2703",
									 "7.7.7.7+1908"};
		
		splitRows.add(new Text("BBBA"));
		splitRows.add(new Text("BBBF"));		
		
		for (int i = 0; i < startRows.length; ++i)
		{
			addExtent(startRows[i], stopRows[i], locs[i]);
		}
		
		cbif.createSplitsForMaxMaps(job, splitRows, kes, locations, splits);
		
		Collection<String> firstlocs = new ArrayList<String>();
		firstlocs.add(locs[0]);
		firstlocs.add(locs[1]);
		Collection<String> secondlocs = new ArrayList<String>();
		secondlocs.add(locs[1]);
		Collection<String> thirdlocs = new ArrayList<String>();
		thirdlocs.add(locs[1]);
		thirdlocs.add(locs[2]);
		thirdlocs.add(locs[3]);
		thirdlocs.add(locs[4]);
		thirdlocs.add(locs[5]);
		thirdlocs.add(locs[6]);
		

//		String[] firstlocs = new String[]{"1.1.1.1+2705", "2.2.2.2+2703"};
//		String[] secondlocs = new String[]{"2.2.2.2+2703"};
//		String[] thirdlocs = new String[]{"2.2.2.2+2703", "3.3.3.3+2703", "4.4.4.4+2705", "5.5.5.5+2703", "6.6.6.6+2703", "7.7.7.7+1908"};
		
		
		int at = 0;
		for (CBInputSplit split : splits) {
			if (at == 0) { 				 
				//System.out.println(split.getStartRow() + ", " + split.getStopRow());			
				for (String sloc : split.getLocations()) {
					assert(firstlocs.contains(sloc));
				}			
			}
			if (at == 1) { 
				for (String sloc : split.getLocations()) {
					assert(secondlocs.contains(sloc));
				}
			}
			if (at == 2) { 
				for (String sloc : split.getLocations()) {
					assert(thirdlocs.contains(sloc));
				}
			}					
			++at;
		}
		
		assertTrue(splits.size()==3);			
	}
	
}
