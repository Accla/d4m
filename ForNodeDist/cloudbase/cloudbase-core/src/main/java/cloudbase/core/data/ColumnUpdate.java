package cloudbase.core.data;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.io.WritableUtils;

import cloudbase.core.util.TextUtil;

import com.facebook.thrift.TBase;
import com.facebook.thrift.TException;
import com.facebook.thrift.protocol.TField;
import com.facebook.thrift.protocol.TProtocol;
import com.facebook.thrift.protocol.TProtocolUtil;
import com.facebook.thrift.protocol.TStruct;
import com.facebook.thrift.protocol.TType;

/**
 * A single column and value pair within a mutation
 * 
 */
public class ColumnUpdate implements TBase, Writable {	
	private Column column = new Column();
	private long timestamp;
	private boolean hasTimestamp;
	private byte[] value;
	private boolean deleted;
	
	private static final byte EMPTY_BYTES[] = new byte[]{};
	
	/* BEGIN THRIFT Generated Code*/
	//Any modifications to this generated code should be noted
	//here so that when a new generated version is copied in
	//the modifications can be made again
	 public final Isset __isset = new Isset();
	  @SuppressWarnings("serial")
    public static final class Isset implements java.io.Serializable {
	    public boolean column = false;
	    public boolean timestamp = false;
	    public boolean hasTimestamp = false;
	    public boolean value = false;
	    public boolean deleted = false;
	  }

	  public ColumnUpdate(
	    Column column,
	    long timestamp,
	    boolean hasTimestamp,
	    byte[] value,
	    boolean deleted)
	  {
	    this();
	    this.column = column;
	    this.__isset.column = true;
	    this.timestamp = timestamp;
	    this.__isset.timestamp = true;
	    this.hasTimestamp = hasTimestamp;
	    this.__isset.hasTimestamp = true;
	    this.value = value;
	    this.__isset.value = true;
	    this.deleted = deleted;
	    this.__isset.deleted = true;
	  }

	  public boolean equals(Object that) {
	    if (that == null)
	      return false;
	    if (that instanceof ColumnUpdate)
	      return this.equals((ColumnUpdate)that);
	    return false;
	  }

	  public boolean equals(ColumnUpdate that) {
	    if (that == null)
	      return false;

	    boolean this_present_column = true && (this.column != null);
	    boolean that_present_column = true && (that.column != null);
	    if (this_present_column || that_present_column) {
	      if (!(this_present_column && that_present_column))
	        return false;
	      if (!this.column.equals(that.column))
	        return false;
	    }

	    boolean this_present_timestamp = true;
	    boolean that_present_timestamp = true;
	    if (this_present_timestamp || that_present_timestamp) {
	      if (!(this_present_timestamp && that_present_timestamp))
	        return false;
	      if (this.timestamp != that.timestamp)
	        return false;
	    }

	    boolean this_present_hasTimestamp = true;
	    boolean that_present_hasTimestamp = true;
	    if (this_present_hasTimestamp || that_present_hasTimestamp) {
	      if (!(this_present_hasTimestamp && that_present_hasTimestamp))
	        return false;
	      if (this.hasTimestamp != that.hasTimestamp)
	        return false;
	    }

	    boolean this_present_value = true && (this.value != null);
	    boolean that_present_value = true && (that.value != null);
	    if (this_present_value || that_present_value) {
	      if (!(this_present_value && that_present_value))
	        return false;
	      if (!java.util.Arrays.equals(this.value, that.value))
	        return false;
	    }

	    boolean this_present_deleted = true;
	    boolean that_present_deleted = true;
	    if (this_present_deleted || that_present_deleted) {
	      if (!(this_present_deleted && that_present_deleted))
	        return false;
	      if (this.deleted != that.deleted)
	        return false;
	    }

	    return true;
	  }

	  public int hashCode() {
	    return 0;
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
	          if (field.type == TType.STRUCT) {
	            this.column = new Column();
	            this.column.read(iprot);
	            this.__isset.column = true;
	          } else { 
	            TProtocolUtil.skip(iprot, field.type);
	          }
	          break;
	        case 2:
	          if (field.type == TType.I64) {
	            this.timestamp = iprot.readI64();
	            this.__isset.timestamp = true;
	          } else { 
	            TProtocolUtil.skip(iprot, field.type);
	          }
	          break;
	        case 3:
	          if (field.type == TType.BOOL) {
	            this.hasTimestamp = iprot.readBool();
	            this.__isset.hasTimestamp = true;
	          } else { 
	            TProtocolUtil.skip(iprot, field.type);
	          }
	          break;
	        case 4:
	          if (field.type == TType.STRING) {
	            this.value = iprot.readBinary();
	            this.__isset.value = true;
	          } else { 
	            TProtocolUtil.skip(iprot, field.type);
	          }
	          break;
	        case 5:
	          if (field.type == TType.BOOL) {
	            this.deleted = iprot.readBool();
	            this.__isset.deleted = true;
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
	  }

	  public void write(TProtocol oprot) throws TException {
	    TStruct struct = new TStruct("ColumnUpdate");
	    oprot.writeStructBegin(struct);
	    TField field = new TField();
	    if (this.column != null) {
	      field.name = "column";
	      field.type = TType.STRUCT;
	      field.id = 1;
	      oprot.writeFieldBegin(field);
	      this.column.write(oprot);
	      oprot.writeFieldEnd();
	    }
	    field.name = "timestamp";
	    field.type = TType.I64;
	    field.id = 2;
	    oprot.writeFieldBegin(field);
	    oprot.writeI64(this.timestamp);
	    oprot.writeFieldEnd();
	    field.name = "hasTimestamp";
	    field.type = TType.BOOL;
	    field.id = 3;
	    oprot.writeFieldBegin(field);
	    oprot.writeBool(this.hasTimestamp);
	    oprot.writeFieldEnd();
	    if (this.value != null) {
	      field.name = "value";
	      field.type = TType.STRING;
	      field.id = 4;
	      oprot.writeFieldBegin(field);
	      oprot.writeBinary(this.value);
	      oprot.writeFieldEnd();
	    }
	    field.name = "deleted";
	    field.type = TType.BOOL;
	    field.id = 5;
	    oprot.writeFieldBegin(field);
	    oprot.writeBool(this.deleted);
	    oprot.writeFieldEnd();
	    oprot.writeFieldStop();
	    oprot.writeStructEnd();
	  }
 
	/* END THRIFT Generated Code*/
	
	ColumnUpdate(Column reference, Value value) {
		this.column = reference;
		this.value = value.get();
		this.deleted = false;
	}
	
	ColumnUpdate(Column reference) {
		this.column = reference;
		this.value = EMPTY_BYTES;
		this.deleted = true;
	}
	
	ColumnUpdate(Text columnFamily, Text columnQualifier, Value value, byte[] labels){
		this.column.columnFamily = TextUtil.getBytes(columnFamily);
		this.column.columnQualifier = TextUtil.getBytes(columnQualifier);
		this.column.columnVisibility = labels;
		this.value = value.get();
		this.deleted = false;
	}
	
	ColumnUpdate(Text columnFamily, Text columnQualifier, Value value){
		this.column.columnFamily = TextUtil.getBytes(columnFamily);
		this.column.columnQualifier = TextUtil.getBytes(columnQualifier);
		this.column.columnVisibility = EMPTY_BYTES;
		this.value =value.get();
		this.deleted = false;
	}
	
	ColumnUpdate(Text columnFamily, Text columnQualifier) {
		this.column.columnFamily = TextUtil.getBytes(columnFamily);
		this.column.columnQualifier = TextUtil.getBytes(columnQualifier);
		this.value = EMPTY_BYTES;
		this.deleted = true;
		hasTimestamp = false;
		this.column.columnVisibility = EMPTY_BYTES;
	}
	
	ColumnUpdate() {
		
	}

	public ColumnUpdate(Text columnFamily, Text columnQualifier, byte[] columnVisibility) {
		this.column.columnFamily = TextUtil.getBytes(columnFamily);
		this.column.columnQualifier = TextUtil.getBytes(columnQualifier);
		this.column.columnVisibility = columnVisibility;
		this.value = EMPTY_BYTES;
		this.deleted = true;
		hasTimestamp = false;
	}

	public void setTimestamp(long ts){
		hasTimestamp = true;
		this.timestamp = ts;
	}
	
	public boolean hasTimestamp(){
		return hasTimestamp;
	}
	
	public long getTimestamp(){
		return timestamp;
	}
	
	/**
	 * Returns the column
	 * 
	 */
	public byte[] getColumnFamily(){
		return column.columnFamily;
	}
	
	public byte[] getColumnQualifier(){
		return column.columnQualifier;
	}
	
	public byte[] getColumnVisibility(){
		return column.columnVisibility;
	}
	
	/**
	 * Returns the value
	 * 
	 */		
	public byte[] getValue() {
		return value;
	}

	public boolean isDeleted(){
		return deleted;
	}
	
	public void setLabels(Value labels) {
		this.column.columnVisibility = labels.get();
	}
	
	
	public byte[] getLabels() {
		return column.columnVisibility;
	}
	
	/**
	 * Populates the data fields from a DataInput object
	 * 
	 */
	public void readFields(DataInput in) throws IOException {
		
		int cfLen = in.readInt();
		column.columnFamily = new byte[cfLen];
		in.readFully(column.columnFamily);
		
		int cqLen = in.readInt();
		column.columnQualifier = new byte[cqLen];
		in.readFully(column.columnQualifier);
		
		int cvLen = in.readInt();
		column.columnVisibility = new byte[cvLen];
		in.readFully(column.columnVisibility);
		
		this.deleted = in.readBoolean();
		
		if(!deleted){
			this.value = new byte[in.readInt()];
			in.readFully(this.value, 0, this.value.length);
		}else{
			this.value = EMPTY_BYTES;
		}
		
		hasTimestamp = in.readBoolean();
		if(hasTimestamp){
			timestamp = WritableUtils.readVLong(in);
		}
		
	}

	/**
	 * Writes the data fields to a DataOutput object
	 * 
	 */
	public void write(DataOutput out) throws IOException {
		out.writeInt(column.columnFamily.length);
		out.write(column.columnFamily);
		
		out.writeInt(column.columnQualifier.length);
		out.write(column.columnQualifier);
		
		out.writeInt(column.columnVisibility.length);
		out.write(column.columnVisibility);
		
		out.writeBoolean(deleted);
		
		if(!deleted){
			out.writeInt(this.value.length);
			out.write(this.value, 0, this.value.length);
		}
		
		out.writeBoolean(hasTimestamp);
		if(hasTimestamp){
			WritableUtils.writeVLong(out, timestamp);
		}
	}
	
	public String toString(){
		return new String(column.columnFamily)+":"+new String(column.columnQualifier)+" "+(hasTimestamp ? timestamp: "NO_TIME_STAMP")+" "+value+" "+deleted;
	}
}