package cloudbase.core.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.Map.Entry;

import org.apache.hadoop.io.Text;

import cloudbase.core.CBConstants;
import cloudbase.core.client.CBException;
import cloudbase.core.client.CBSecurityException;
import cloudbase.core.client.TableNotFoundException;
import cloudbase.core.client.impl.HdfsZooInstance;
import cloudbase.core.client.impl.ScannerImpl;
import cloudbase.core.data.Key;
import cloudbase.core.data.KeyExtent;
import cloudbase.core.data.Value;
import cloudbase.core.security.thrift.AuthInfo;

public class CheckForMetadataProblems {
	private static String user;
	private static byte[] pass;
	public static void checkTable(String tablename, TreeSet<KeyExtent> tablets, boolean patch){
		//sanity check of metadata table entries
		//make sure tablets has no holes, and that it starts and ends w/ null
		if(tablets.size() == 0){
			System.out.println("No entries found in metadata table for table "+tablename);
		}
		
		if(tablets.first().getPrevEndRow() != null){
			System.out.println("First entry for table "+tablename+"- " + tablets.first() + " - has non null prev end row");
		}
		
		if(tablets.last().getEndRow() != null){
			System.out.println("Last entry for table "+tablename+"- " + tablets.last() + " - has non null end row");
		}
		
		Iterator<KeyExtent> tabIter = tablets.iterator();
		Text lastEndRow = tabIter.next().getEndRow();
		while(tabIter.hasNext()){
			KeyExtent tabke = tabIter.next();
			boolean broke = false;
			if(tabke.getPrevEndRow() == null){
				System.out.println("Table "+tablename+" has null prev end row in middle of table "+tabke);
				broke = true;
			} else if(!tabke.getPrevEndRow().equals(lastEndRow)){
				System.out.println("Table "+tablename+" has a hole "+tabke.getPrevEndRow()+" != "+lastEndRow);
				broke = true;
			}
			if (broke && patch) {
			    KeyExtent ke = new KeyExtent(tabke);
			    ke.setPrevEndRow(lastEndRow);
			    MetadataTable.updateTabletPrevEndRow(ke, new AuthInfo(user, pass));
			    System.out.println("KE " + tabke + " has been repaired to " + ke);
			}
			
			lastEndRow = tabke.getEndRow();
			if (!broke)
				System.out.println("All is well for table " + tablename);
		}
	}
	
	public static void checkMetadataTableEntries(boolean patch) throws CBException, CBSecurityException, TableNotFoundException
	{
		Map<String, TreeSet<KeyExtent>> tables = new HashMap<String, TreeSet<KeyExtent>>(); 
		
		ScannerImpl scanner = new ScannerImpl(new HdfsZooInstance(), new AuthInfo(user, pass), CBConstants.METADATA_TABLE_NAME, CBConstants.NO_AUTHS);
		scanner.fetchColumn(CBConstants.METADATA_TABLE_TABLET_COLUMN_FAMILY, CBConstants.METADATA_TABLE_TABLET_PREV_ROW_COLUMN_NAME);
		scanner.fetchColumn(CBConstants.METADATA_TABLE_TABLET_COLUMN_FAMILY, CBConstants.METADATA_TABLE_TABLET_LOCATION_COLUMN_NAME);

		Text colf = new Text();
		Text colq = new Text();

		for (Entry<Key, Value> entry : scanner) {
			colf = entry.getKey().getColumnFamily(colf);
			colq = entry.getKey().getColumnQualifier(colq);
			
			String tableName = (new KeyExtent(entry.getKey().getRow(), (Text)null)).getTableName().toString();
			
			TreeSet<KeyExtent> tablets = tables.get(tableName);
			if(tablets == null){
				tablets = new TreeSet<KeyExtent>();
				tables.put(tableName, tablets);
			}
						
			if(colf.equals(CBConstants.METADATA_TABLE_TABLET_COLUMN_FAMILY) &&
					colq.equals(CBConstants.METADATA_TABLE_TABLET_PREV_ROW_COLUMN_NAME)){
				KeyExtent tabletKe = new KeyExtent(entry.getKey().getRow(), entry.getValue());
				tablets.add(tabletKe);
			}
		}
		
		Set<Entry<String, TreeSet<KeyExtent>>> es = tables.entrySet();
		
		for (Entry<String, TreeSet<KeyExtent>> entry : es) {
			checkTable(entry.getKey(), entry.getValue(), patch);
		}
		

		//end METADATA table sanity check
	}
	
	public static void main(String[] args)
	throws CBException, CBSecurityException, TableNotFoundException
	{
		if (args.length==3) {
			user = args[1];
			pass = args[2].getBytes();
			checkMetadataTableEntries(Boolean.parseBoolean(args[0]));
		}
		else if (args.length==2) {
			user = args[0];
			pass = args[1].getBytes();
			checkMetadataTableEntries(false);
		}
		else
			System.out.println("Insufficient paratmers, needs username and password");
	}

}
