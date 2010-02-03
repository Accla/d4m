package cloudbase.core.data;

/**
 * keeps track of information needed to identify a tablet
 * apparently, we only need the endKey and not the start as well
 * 
 */


import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.hadoop.io.BinaryComparable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.WritableComparable;

import cloudbase.core.CBConstants;

import com.facebook.thrift.TBase;
import com.facebook.thrift.TException;
import com.facebook.thrift.protocol.TField;
import com.facebook.thrift.protocol.TProtocol;
import com.facebook.thrift.protocol.TProtocolUtil;
import com.facebook.thrift.protocol.TStruct;
import com.facebook.thrift.protocol.TType;


public class KeyExtent implements TBase, WritableComparable<Object> {
	
	//private static Log log = LogFactory.getLog(KeyExtent.class);
	
	private Text textTableName;
	private Text textEndRow;
	private Text textPrevEndRow;

	/* BEGIN THRIFT Generated Code*/
	//Any modifications to this generated code should be noted
	//here so that when a new generated version is copied in
	//the modifications can be made again
	
	//NOTE Code added to the beginning of write() and end of read() methods
	
	private byte[] table;
	  private byte[] endRow;
	  private byte[] prevEndRow;

	  public final Isset __isset = new Isset();
	  @SuppressWarnings("serial")
    public static final class Isset implements java.io.Serializable {
	    public boolean table = false;
	    public boolean endRow = false;
	    public boolean prevEndRow = false;
	  }

	  public KeyExtent(
	    byte[] table,
	    byte[] endRow,
	    byte[] prevEndRow)
	  {
	    this();
	    this.table = table;
	    this.__isset.table = true;
	    this.endRow = endRow;
	    this.__isset.endRow = true;
	    this.prevEndRow = prevEndRow;
	    this.__isset.prevEndRow = true;
	  }

	  public void read(TProtocol iprot) throws TException {
	    TField field;
	    iprot.readStructBegin();
	    while (true)
	    {
	      field = iprot.readFieldBegin();
	      if (field.type == TType.STOP) { 
	        break;
	      }
	      switch (field.id)
	      {
	        case 1:
	          if (field.type == TType.STRING) {
	            this.table = iprot.readBinary();
	            this.__isset.table = true;
	          } else { 
	            TProtocolUtil.skip(iprot, field.type);
	          }
	          break;
	        case 2:
	          if (field.type == TType.STRING) {
	            this.endRow = iprot.readBinary();
	            this.__isset.endRow = true;
	          } else { 
	            TProtocolUtil.skip(iprot, field.type);
	          }
	          break;
	        case 3:
	          if (field.type == TType.STRING) {
	            this.prevEndRow = iprot.readBinary();
	            this.__isset.prevEndRow = true;
	          } else { 
	            TProtocolUtil.skip(iprot, field.type);
	          }
	          break;
	        default:
	          TProtocolUtil.skip(iprot, field.type);
	          break;
	      }
	      iprot.readFieldEnd();
	    }
	    iprot.readStructEnd();
	    
	    //ADDED CODE
	    thriftToHistorical();
	  }

	

	public void write(TProtocol oprot) throws TException {
		
		//ADDED code
		historicalToThrift();
		  
	    TStruct struct = new TStruct("KeyExtent");
	    oprot.writeStructBegin(struct);
	    TField field = new TField();
	    if (this.table != null) {
	      field.name = "table";
	      field.type = TType.STRING;
	      field.id = 1;
	      oprot.writeFieldBegin(field);
	      oprot.writeBinary(this.table);
	      oprot.writeFieldEnd();
	    }
	    if (this.endRow != null) {
	      field.name = "endRow";
	      field.type = TType.STRING;
	      field.id = 2;
	      oprot.writeFieldBegin(field);
	      oprot.writeBinary(this.endRow);
	      oprot.writeFieldEnd();
	    }
	    if (this.prevEndRow != null) {
	      field.name = "prevEndRow";
	      field.type = TType.STRING;
	      field.id = 3;
	      oprot.writeFieldBegin(field);
	      oprot.writeBinary(this.prevEndRow);
	      oprot.writeFieldEnd();// TODO Auto-generated method stub
	    }

	    oprot.writeFieldStop();
	    oprot.writeStructEnd();
	  }
	
	/* END THRIFT Generated Code*/
	
	private void thriftToHistorical() {
		//TODO avoid copy that occurs in Text constructor
		textTableName = new Text(table);
		
		if(endRow == null || endRow.length == 0){
			textEndRow = null;
		}else{
			textEndRow = new Text(endRow);
		}
		
		if(prevEndRow == null || prevEndRow.length == 0){
			textPrevEndRow = null;
		}else{
			textPrevEndRow = new Text(prevEndRow);
		}

	}

	private static byte[] getExactLengthByteArray(Text t){
		if(t.getLength() == t.getBytes().length){
			  return t.getBytes();
		  }else{
			  byte[] ba = new byte[t.getLength()];
			  System.arraycopy(t.getBytes(), 0, ba, 0, t.getLength());
			  return ba;
		  }
	}
	
	private void historicalToThrift() {
		  table = getExactLengthByteArray(textTableName);
		  
		  if(textEndRow == null){
			  endRow = null;
		  }else{
			  endRow = getExactLengthByteArray(textEndRow);
		  }
		  
		  if(textPrevEndRow == null){
			  prevEndRow = null;
		  }else{
			  prevEndRow = getExactLengthByteArray(textPrevEndRow);
		  }
	}

	private void check(){
		
		if(getTableName() == null)
			throw new IllegalArgumentException("null table name not allowed");
		
		if(getEndRow() == null || getPrevEndRow() == null)
			return;
		
		if(getPrevEndRow().compareTo(getEndRow()) >= 0){
			throw new IllegalArgumentException("prevEndRow ("+getPrevEndRow()+") >= endRow ("+getEndRow()+")");
		}
	}
	
	/**
	 * Default constructor
	 * 
	 */
	public KeyExtent() {
		this.setTableName(new Text(), false);
		this.setEndRow(new Text(), false, false);
		this.setPrevEndRow(new Text(), false, false);
	}
	
	
	public KeyExtent(Text table, Text endRow, Text prevEndRow) {
		this.setTableName(table, true);
		this.setEndRow(endRow, false, true);
		this.setPrevEndRow(prevEndRow, false, true);
		
		check();
	}
	
	

	public KeyExtent(KeyExtent extent) {
		this.setTableName(extent.getTableName(), true);
		this.setEndRow(extent.getEndRow(), false, true);
		this.setPrevEndRow(extent.getPrevEndRow(), false, true);
		
		check();
	}

	/**
	 * Returns a String representing this extent's entry in the
	 * Metadata table
	 * 
	 */
	public Text getMetadataEntry() {
		return getMetadataEntry(getTableName(), getEndRow());
	}
	
	public static Text getMetadataEntry(Text table, Text row) {
		Text entry = new Text(table);
		
		if(row == null){
			entry.append(new byte[]{'<'}, 0, 1);
		}else{
			entry.append(new byte[]{';'}, 0, 1);
			entry.append(row.getBytes(), 0, row.getLength());
		}
		
		return entry;
		
	}
	
	// constructor for loading extents from metadata rows
	public KeyExtent(Text flattenedExtent, Value prevEndRow) {
		decodeMetadataRow(flattenedExtent);
		
		// decode the prev row
		this.setPrevEndRow(decodePrevEndRow(prevEndRow), false, true);
		
		check();
	}
	
	
	
	// recreates an encoded extent from a string representation
	// this encoding is what is stored as the row id of the metadata table
	public KeyExtent(Text flattenedExtent, Text prevEndRow) {
		//System.out.println("Creating KeyExtent from string '"+flattenedExtent+"'");

		decodeMetadataRow(flattenedExtent);
		
		this.setPrevEndRow(null, false, false);
		if(prevEndRow != null)
			this.setPrevEndRow(prevEndRow, false, true);
		
		check();
	}

	private void setTableName(Text tName, boolean copy){
		
		if(tName == null)
			throw new IllegalArgumentException("null table name not allowed");
		
		if(copy)
			this.textTableName = new Text(tName);
		else
			this.textTableName = tName;
	}
	
	/**
	 * Sets the extents table name
	 * 
	 */
	public void setTableName(Text table) {
		setTableName(table, true);
	}

	/**
	 * Returns the extent's table name
	 * 
	 */
	public Text getTableName() {
		return textTableName;
	}

	private void setEndRow(Text endRow, boolean check, boolean copy) {
		if(endRow != null)
			if(copy)
				this.textEndRow = new Text(endRow);
			else
				this.textEndRow = endRow;
		else
			this.textEndRow = null;
		
		if(check) 
			check();
	}
	
	/**
	 * Sets this extent's end row
	 * 
	 * @param endRow
	 */
	public void setEndRow(Text endRow) {
		setEndRow(endRow, true, true);
	}

	/**
	 * Returns this extent's end row
	 * 
	 */
	public Text getEndRow() {
		return textEndRow;
	}

	/**
	 * Return the previous extent's end row
	 * 
	 */
	public Text getPrevEndRow() {
		return textPrevEndRow;
	}

	
	private void setPrevEndRow(Text prevEndRow, boolean check, boolean copy) {
		if(prevEndRow != null)
			if(copy)
				this.textPrevEndRow = new Text(prevEndRow);
			else
				this.textPrevEndRow = prevEndRow;
		else
			this.textPrevEndRow = null;
		
		if(check)
			check();
	}
	
	/**
	 * Sets the previous extent's end row
	 * 
	 */
	public void setPrevEndRow(Text prevEndRow) {
		setPrevEndRow(prevEndRow, true, true);
	}

	/**
	 * Populates the extents data fields from a DataInput object
	 * 
	 */
	public void readFields(DataInput in) throws IOException {
		getTableName().readFields(in);
		boolean hasRow = in.readBoolean();
		if(hasRow) {
			Text er = new Text();
			er.readFields(in);
			setEndRow(er, false, false);
		}
		else {
			setEndRow(null, false, false);
		}
		boolean hasPrevRow = in.readBoolean();
		if(hasPrevRow) {
			Text per = new Text();
			per.readFields(in);
			setPrevEndRow(per, false, true);
		}
		else {
			setPrevEndRow(null);
		}
		
		check();
	}

	/**
	 * Writes this extent's data fields to a DataOutput object
	 * 
	 */
	public void write(DataOutput out) throws IOException {
		getTableName().write(out);
		if(getEndRow() != null) {
			out.writeBoolean(true);
			getEndRow().write(out);
		}
		else {
			out.writeBoolean(false);
		}
		if(getPrevEndRow() != null) {
			out.writeBoolean(true);
			getPrevEndRow().write(out);
		}
		else {
			out.writeBoolean(false);
		}
	}

	/**
	 * Returns a String representing the previous extent's entry in the
	 * Metadata table
	 * 
	 */
	public Mutation getPrevRowUpdateMutation() {
		return getPrevRowUpdateMutation(this);
	}
	
	/**
	 * Returns each key extent that the rows cover
	 * Empty start or end rows tell the method there are no start or end rows,
	 * and to use all the keyextents that are before the end row if no start row etc.
	 * 
	 * @param startRow
	 * @param endRow
	 * @param kes
	 * @return
	 */
	
	public static Collection<KeyExtent> getKeyExtentsForRange(Text startRow, Text endRow, Set<KeyExtent> kes) {
		Collection<KeyExtent> keys = new ArrayList<KeyExtent>();
		Iterator<KeyExtent> kesiter = kes.iterator();
		while (kesiter.hasNext()) {
			KeyExtent ckes = (KeyExtent) kesiter.next();
			
			//System.out.println("prevrow \'" + startRow + "\' endrow \'" + endRow + "\' ckes.prevrow\'" + ckes.getPrevEndRow() + "\' ckes.endrow\'" + ckes.getEndRow() + "\'");
			

			if (ckes.getPrevEndRow() == null) {
				if (ckes.getEndRow() == null) {
					// only tablet
					keys.add(ckes);
				}
				else {
					// first tablet
					// if start row = '' then we want everything up to the endRow which will always include the first tablet
					if (startRow.getLength() == 0)
					{
						keys.add(ckes);
					}
					else if (ckes.getEndRow().compareTo(startRow) >= 0)
					{
						keys.add(ckes);
					}
				}
			}
			else {
				if (ckes.getEndRow() == null) {
					// last tablet
					// if endRow = '' and we're at the last tablet, add it
					if (endRow.getLength() == 0)
					{
						keys.add(ckes);
					}					
					if (ckes.getPrevEndRow().compareTo(endRow) < 0)
					{
						keys.add(ckes);	
					}
				}
				else {
					// tablet in the middle
					if (startRow.getLength() == 0) {
						// no start row
						
						if (endRow.getLength() == 0) {
							// no start & end row
							keys.add(ckes);
						}
						else {
							// just no start row
							if (ckes.getPrevEndRow().compareTo(endRow) <= 0) {
								keys.add(ckes);
							}							
						}
					}
					else if (endRow.getLength() == 0) {
						// no end row
						if (ckes.getEndRow().compareTo(startRow) > 0)
						{
							keys.add(ckes);
						}
					}
					else {
						// no null prevend or endrows and no empty string start or end rows
						if ((ckes.getPrevEndRow().compareTo(endRow) <= 0 && ckes.getEndRow().compareTo(startRow) > 0))
						{
							keys.add(ckes);
						}
					}
					
				}
			}
		}		
		return keys;//.toArray(new KeyExtent[keys.size()]);
	}
	
	public static Text decodePrevEndRow(Value ibw){
		Text per = null;
		
		if(ibw.get()[0] != 0)
		{
			per = new Text();
			per.set(ibw.get(), 1, ibw.get().length - 1);
		}
		
		return per;
	}
	
	public static Value encodePrevEndRow(Text per){
		if(per == null){
			return new Value(new byte[]{0});
		}else{
			byte [] b = new byte[per.getLength() + 1];
			b[0] = 1;
			System.arraycopy(per.getBytes(), 0, b, 1, per.getLength());
			return new Value(b);
		}
	}
	
	public static Mutation getPrevRowUpdateMutation(KeyExtent ke)
	{
		Mutation m = new Mutation(ke.getMetadataEntry());
		m.put(CBConstants.METADATA_TABLE_TABLET_COLUMN_FAMILY, CBConstants.METADATA_TABLE_TABLET_PREV_ROW_COLUMN_NAME, encodePrevEndRow(ke.getPrevEndRow()));
		return m;
	}

	/**
	 * Compares extents based on rows
	 * 
	 */
	public int compareTo(Object o) {
		KeyExtent other = (KeyExtent) o;
		
		int result = getTableName().compareTo(other.getTableName());
		if(result != 0) return result;
		
		if(this.getEndRow() == null) {
			if(other.getEndRow() != null)
				return 1;
		}
		else { 
			if(other.getEndRow() == null) return -1;
		
			result = getEndRow().compareTo(other.getEndRow());
			if(result != 0) return result;
		}
		if(this.getPrevEndRow() == null) {
			if(other.getPrevEndRow() == null) return 0;
			return -1;
		}
		if(other.getPrevEndRow() == null) return 1;
		return this.getPrevEndRow().compareTo(other.getPrevEndRow());
	}

	public int hashCode(){
		int prevEndRowHash = 0;
		int endRowHash = 0;
		if(this.getEndRow() != null) {
			endRowHash = this.getEndRow().hashCode();
		}
		
		if(this.getPrevEndRow() != null) {
			prevEndRowHash = this.getPrevEndRow().hashCode();
		}
		
		return getTableName().hashCode() +  endRowHash + prevEndRowHash;
	}
	
	public boolean equals(Object o){
		return compareTo(o) == 0;
	}

	public String toString() { 
		String endRowString;
		String prevEndRowString;
		String tableNameString = getTableName().toString().replaceAll(";", "\\\\;").replaceAll("\\\\", "\\\\\\\\");
		
		if(getEndRow() == null)
			endRowString = "<";
		else
			endRowString = ";" + getEndRow().toString().replaceAll(";", "\\\\;").replaceAll("\\\\", "\\\\\\\\");

		if(getPrevEndRow() == null)
			prevEndRowString = "<";
		else
			prevEndRowString = ";" + getPrevEndRow().toString().replaceAll(";", "\\\\;").replaceAll("\\\\", "\\\\\\\\");
		
		return tableNameString + endRowString + prevEndRowString;
	}
	
	// note: this is only the encoding of the table name and the last row, not the prev row
	/**
	 * Populates the extent's fields based on a flatted extent
	 * 
	 */
	private void decodeMetadataRow(Text flattenedExtent) {
		int semiPos = -1;
		int ltPos = -1;
		
		for(int i = 0 ; i < flattenedExtent.getLength(); i++){
			if(flattenedExtent.getBytes()[i] == ';' && semiPos < 0){
				//want the position of the first semicolon
				semiPos = i;
			}
			
			if(flattenedExtent.getBytes()[i] == '<'){
				ltPos = i;
			}
		}
		
		if(semiPos < 0 && ltPos < 0){
			throw new IllegalArgumentException("Metadata row does not contain ; or <  "+flattenedExtent);
		}
		
		if(semiPos < 0) {
			
			if(ltPos != flattenedExtent.getLength() - 1){
				throw new IllegalArgumentException("< must come at end of Metadata row  "+flattenedExtent);
			}
			
			Text tableName = new Text();
			tableName.set(flattenedExtent.getBytes(), 0, flattenedExtent.getLength() - 1);
			this.setTableName(tableName, false);
			this.setEndRow(null, false, false);
		}
		else {
			
			Text tableName = new Text();
			tableName.set(flattenedExtent.getBytes(), 0, semiPos);
			
			Text endRow = new Text();
			endRow.set(flattenedExtent.getBytes(), semiPos + 1, flattenedExtent.getLength() - (semiPos + 1));
			
			this.setTableName(tableName, false);
			
			this.setEndRow(endRow, false, false);
		}
	}
	
	public static byte[] tableOfMetadataRow(Text row) {
		KeyExtent ke = new KeyExtent();
		ke.decodeMetadataRow(row);
		if (ke.table != null)
		    return ke.table;
		return ke.getTableName().getBytes();
	}
	

	public boolean contains(BinaryComparable row)
	{
		if(row == null) {
			//System.out.println("extent can contain null row?");
			return getPrevEndRow() == null;
		}
			
		if((this.getPrevEndRow() == null || this.getPrevEndRow().compareTo(row) < 0) &&
			(this.getEndRow() == null || this.getEndRow().compareTo(row) >= 0)) {
			//System.out.println(this.toString() + " contains " + row);
			return true;
		}
		//System.out.println("row outside the key extent " + row + " " + this.toString());
		return false;
	}
	
	public static SortedSet<KeyExtent> findChildren(KeyExtent ke, SortedSet<KeyExtent> tablets) {
		
		
		SortedSet<KeyExtent> children = null;
		
		//TODO use a tail map
		
		for(KeyExtent tabletKe : tablets){
			
			
			if(ke.getPrevEndRow() == tabletKe.getPrevEndRow() ||
				ke.getPrevEndRow() != null && tabletKe.getPrevEndRow() != null && 
				tabletKe.getPrevEndRow().compareTo(ke.getPrevEndRow()) == 0)
			{
				children = new TreeSet<KeyExtent>();
			}
			
			if(children != null){
				children.add(tabletKe);
			}
			
			if(ke.getEndRow() == tabletKe.getEndRow() ||
				ke.getEndRow() != null && tabletKe.getEndRow() != null && 
				tabletKe.getEndRow().compareTo(ke.getEndRow()) == 0)
			{
				return children;	
			}
		}
		
		return new TreeSet<KeyExtent>();
	}

	public static KeyExtent findContainingExtent(KeyExtent extent, SortedSet<KeyExtent> extents){
		
		KeyExtent lookupExtent = new KeyExtent(extent);
		lookupExtent.setPrevEndRow(null);
		
		SortedSet<KeyExtent> tailSet = extents.tailSet(lookupExtent);
		
		if(tailSet.size() == 0){
			return null;
		}
		
		KeyExtent first = tailSet.first();
		
		if(first.getTableName().compareTo(extent.getTableName()) != 0){
			return null;
		}
		
		if(first.getPrevEndRow() == null){
			return first;
		}
		
		if(extent.getPrevEndRow() == null){
			return null;
		}
		
		if(extent.getPrevEndRow().compareTo(first.getPrevEndRow()) >= 0)
			return first;
		else
			return null;
		
	}

	public static Text getMetadataEntry(KeyExtent extent) {
		return getMetadataEntry(extent.getTableName(), extent.getEndRow());
	}
	

	// test key extent comparator
	public static void main(String[] args) {
		
	}
}
