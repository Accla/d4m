package cloudbase.core.constraints;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.apache.hadoop.io.Text;
import org.apache.log4j.Logger;

import cloudbase.core.CBConstants;
import cloudbase.core.data.ColumnUpdate;
import cloudbase.core.data.KeyExtent;
import cloudbase.core.data.Mutation;
import cloudbase.core.data.Value;
import cloudbase.core.util.MetadataTable;
import cloudbase.core.util.MetadataTable.SSTableValue;

public class MetadataConstraints implements Constraint {
	
	private static Logger log = Logger.getLogger(MetadataConstraints.class.getName());
	
	private static final HashSet<Text> validColumnQuals = new HashSet<Text>(Arrays.asList(
			new Text[]{
					CBConstants.METADATA_TABLE_TABLET_LOCATION_COLUMN_NAME,
					CBConstants.METADATA_TABLE_TABLET_PREV_ROW_COLUMN_NAME,
					CBConstants.METADATA_TABLE_TABLET_OLD_PREV_ROW_COLUMN_NAME,
					CBConstants.METADATA_TABLE_TABLET_DIRECTORY_COLUMN_NAME,
					CBConstants.METADATA_TABLE_TABLET_SPLIT_RATIO_COLUMN_NAME
			}));
	
	private static boolean isValidColumn(ColumnUpdate cu){
		if(CBConstants.METADATA_TABLE_TABLET_SSTABLES_COLUMN_FAMILY.equals(new Text(cu.getColumnFamily()))){
			return true;
		}
		
		if(CBConstants.METADATA_TABLE_TABLET_COLUMN_FAMILY.equals(new Text(cu.getColumnFamily())) && validColumnQuals.contains(new Text(cu.getColumnQualifier()))){
			return true;
		}
		
		return false;
	}
	
	public List<Short> check(Mutation mutation) {
		
		ArrayList<Short> violations = null;
		
		Collection<ColumnUpdate> colUpdates = mutation.getUpdates();
		
		//check the row, it should contains at least one ; or end with <
		boolean containsSemiC = false;
		
		byte[] row = mutation.getRow();
		for (byte b : row) {
			if(b == ';'){
				containsSemiC = true;
			}
		}
		
		if(!containsSemiC){
			//see if last row char is <
			if(row.length == 0 || row[row.length - 1] != '<'){
				violations = new ArrayList<Short>();
				violations.add((short)4);
			}
		}
		
		//ensure row is not less than !METADATA
		if(new Text(row).compareTo(new Text(CBConstants.METADATA_TABLE_NAME)) < 0){
			if(violations == null) violations = new ArrayList<Short>();
			violations.add((short)5);
		}
		
		for (ColumnUpdate columnUpdate : colUpdates) {
			Text columnFamily = new Text(columnUpdate.getColumnFamily());
			
			if(columnUpdate.isDeleted()){				
				if(!isValidColumn(columnUpdate)){
					if(violations == null) violations = new ArrayList<Short>();
					violations.add((short)2);
				}
				continue;
			}
			
			if(columnUpdate.getValue().length == 0){
				if(violations == null) violations = new ArrayList<Short>();
				violations.add((short)6);
			}
			
			if(columnFamily.equals(CBConstants.METADATA_TABLE_TABLET_SSTABLES_COLUMN_FAMILY)){
				try{
					SSTableValue sstv = new SSTableValue(columnUpdate.getValue());
					
					if(sstv.getSize() < 0 || sstv.getNumEntries() < 0){
						if(violations == null) violations = new ArrayList<Short>();
						violations.add((short)1);
					}
				}catch(NumberFormatException nfe){
					if(violations == null) violations = new ArrayList<Short>();
					violations.add((short)1);
				}catch(ArrayIndexOutOfBoundsException aiooe){
					if(violations == null) violations = new ArrayList<Short>();
					violations.add((short)1);
				}
			}else{
				if(!isValidColumn(columnUpdate)){
					if(violations == null) violations = new ArrayList<Short>();
					violations.add((short)2);
				}else if(new Text(columnUpdate.getColumnQualifier()).equals(CBConstants.METADATA_TABLE_TABLET_PREV_ROW_COLUMN_NAME) && columnUpdate.getValue().length > 0 && (violations == null || !violations.contains((short)4))){
					KeyExtent ke = new KeyExtent(new Text(mutation.getRow()), (Text)null);
					
					Text per = KeyExtent.decodePrevEndRow(new Value(columnUpdate.getValue()));
					
					boolean prevEndRowLessThanEndRow = per == null || ke.getEndRow() == null || per.compareTo(ke.getEndRow()) < 0;
					
					if(!prevEndRowLessThanEndRow){
						if(violations == null) violations = new ArrayList<Short>();
						violations.add((short)3);
					}
				}
				
			}
		}
		
		if(violations != null){
			log.debug(" violating metadata mutation : "+mutation);
		}
		
		return violations;
	}

	public String getViolationDescription(short violationCode) {
		switch(violationCode){
		case 1:
			return "sstable size must be a non-negative integer";
		case 2:
			return "Invalid column name given.";
		case 3:
			return "Prev end row is greater than or equal to end row.";
		case 4:
			return "Invalid metadata row format";
		case 5:
			return "Row can not be less than !METADATA";
		case 6:
			return "Empty values are not allowed for any !METADATA column";
		}
		return null;
	}
	
}
