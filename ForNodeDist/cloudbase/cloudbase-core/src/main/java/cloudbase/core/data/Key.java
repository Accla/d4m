package cloudbase.core.data;

/**
 * This class is used to group together all the info that goes into a key in 
 * the mapfiles
 * 
 */

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.WritableComparator;
import org.apache.hadoop.io.WritableUtils;

import com.facebook.thrift.TBase;
import com.facebook.thrift.TException;
import com.facebook.thrift.protocol.TField;
import com.facebook.thrift.protocol.TProtocol;
import com.facebook.thrift.protocol.TProtocolUtil;
import com.facebook.thrift.protocol.TStruct;
import com.facebook.thrift.protocol.TType;

import cloudbase.core.security.LabelExpression;


public class Key implements TBase, WritableComparable<Object> {
	private int colFamilyOffset;
	private int colQualifierOffset;
	private int colVisibilityOffset;
	private int totalLen;
	
	private byte keyData[];	
	
	private long timestamp;
	private boolean deleted = false;

	private static final byte EMPTY_BYTES[] = new byte[0];

	public final Isset __isset = new Isset();
	  @SuppressWarnings("serial")
    public static final class Isset implements java.io.Serializable {
	    public boolean colFamilyOffset = false;
	    public boolean colQualifierOffset = false;
	    public boolean colVisibilityOffset = false;
	    public boolean totalLen = false;
	    public boolean keyData = false;
	    public boolean timestamp = false;
	  }

	  public Key(
	    int colFamilyOffset,
	    int colQualifierOffset,
	    int colVisibilityOffset,
	    int totalLen,
	    byte[] keyData,
	    long timestamp)
	  {
	    this();
	    this.colFamilyOffset = colFamilyOffset;
	    this.__isset.colFamilyOffset = true;
	    this.colQualifierOffset = colQualifierOffset;
	    this.__isset.colQualifierOffset = true;
	    this.colVisibilityOffset = colVisibilityOffset;
	    this.__isset.colVisibilityOffset = true;
	    this.totalLen = totalLen;
	    this.__isset.totalLen = true;
	    this.keyData = keyData;
	    this.__isset.keyData = true;
	    this.timestamp = timestamp;
	    this.__isset.timestamp = true;
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
	          if (field.type == TType.I32) {
	            this.colFamilyOffset = iprot.readI32();
	            this.__isset.colFamilyOffset = true;
	          } else { 
	            TProtocolUtil.skip(iprot, field.type);
	          }
	          break;
	        case 2:
	          if (field.type == TType.I32) {
	            this.colQualifierOffset = iprot.readI32();
	            this.__isset.colQualifierOffset = true;
	          } else { 
	            TProtocolUtil.skip(iprot, field.type);
	          }
	          break;
	        case 3:
	          if (field.type == TType.I32) {
	            this.colVisibilityOffset = iprot.readI32();
	            this.__isset.colVisibilityOffset = true;
	          } else { 
	            TProtocolUtil.skip(iprot, field.type);
	          }
	          break;
	        case 4:
	          if (field.type == TType.I32) {
	            this.totalLen = iprot.readI32();
	            this.__isset.totalLen = true;
	          } else { 
	            TProtocolUtil.skip(iprot, field.type);
	          }
	          break;
	        case 5:
	          if (field.type == TType.STRING) {
	            this.keyData = iprot.readBinary();
	            this.__isset.keyData = true;
	          } else { 
	            TProtocolUtil.skip(iprot, field.type);
	          }
	          break;
	        case 6:
	          if (field.type == TType.I64) {
	            this.timestamp = iprot.readI64();
	            this.__isset.timestamp = true;
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
	    TStruct struct = new TStruct("Key");
	    oprot.writeStructBegin(struct);
	    TField field = new TField();
	    field.name = "colFamilyOffset";
	    field.type = TType.I32;
	    field.id = 1;
	    oprot.writeFieldBegin(field);
	    oprot.writeI32(this.colFamilyOffset);
	    oprot.writeFieldEnd();
	    field.name = "colQualifierOffset";
	    field.type = TType.I32;
	    field.id = 2;
	    oprot.writeFieldBegin(field);
	    oprot.writeI32(this.colQualifierOffset);
	    oprot.writeFieldEnd();
	    field.name = "colVisibilityOffset";
	    field.type = TType.I32;
	    field.id = 3;
	    oprot.writeFieldBegin(field);
	    oprot.writeI32(this.colVisibilityOffset);
	    oprot.writeFieldEnd();
	    field.name = "totalLen";
	    field.type = TType.I32;
	    field.id = 4;
	    oprot.writeFieldBegin(field);
	    oprot.writeI32(this.totalLen);
	    oprot.writeFieldEnd();
	    if (this.keyData != null) {
	      field.name = "keyData";
	      field.type = TType.STRING;
	      field.id = 5;
	      oprot.writeFieldBegin(field);
	      oprot.writeBinary(this.keyData);
	      oprot.writeFieldEnd();
	    }
	    field.name = "timestamp";
	    field.type = TType.I64;
	    field.id = 6;
	    oprot.writeFieldBegin(field);
	    oprot.writeI64(this.timestamp);
	    oprot.writeFieldEnd();
	    oprot.writeFieldStop();
	    oprot.writeStructEnd();
	  }

	
	/* END THRIFT Generated Code*/
	
	private final void init(int rLen, int cfLen, int cqLen, int cvLen)
	{
		colFamilyOffset = rLen;
		colQualifierOffset = colFamilyOffset + cfLen;
		colVisibilityOffset = colQualifierOffset + cqLen;
		totalLen = colVisibilityOffset + cvLen;
		keyData = new byte[totalLen];
	}
	
	private final void init(byte r[], int rOff, int rLen,
			byte cf[], int cfOff, int cfLen,
			byte cq[], int cqOff, int cqLen,
			byte cv[], int cvOff, int cvLen)
	{
		init(rLen, cfLen, cqLen, cvLen);
		
		if (rLen > 0)
			System.arraycopy(r, rOff, keyData, 0, rLen);
		if (cfLen > 0)
			System.arraycopy(cf, cfOff, keyData, colFamilyOffset, cfLen);
		if (cqLen > 0)
			System.arraycopy(cq, cqOff, keyData, colQualifierOffset, cqLen);
		if (cvLen > 0)
			System.arraycopy(cv, cvOff, keyData, colVisibilityOffset, cvLen);

	}
	
	
	public final int getRowOffset(){
		return 0;
	}
	
	public final int getRowLen(){
		return colFamilyOffset;
	}
	
	public final int getColumnFamilyOffset(){
		return colFamilyOffset;
	}
	
	public final int getColumnFamilyLen(){
		return colQualifierOffset - colFamilyOffset;
	}
	
	public final int getColumnQualifierOffset(){
		return colQualifierOffset;
	}

	public final int getColumnQualifierLen(){
		return colVisibilityOffset - colQualifierOffset;
	}

	public final int getColumnVisibilityOffset() {
		return colVisibilityOffset;
	}
	
	/** Use getColumnVisibilityOffset() */
	@Deprecated
	public final int getLabelOffset() {
		return colVisibilityOffset;
	}
	
	public final int getColumnVisibilityLen() {
		return totalLen - colVisibilityOffset;
	}
	
	/** Use getColumnVisibility */
	@Deprecated
	public final int getLabelLen() {
		return totalLen - colVisibilityOffset;
	}
	
	
	public final byte[] getKeyData(){
		return keyData;
	}
	
	public Key() {
		keyData = EMPTY_BYTES;
	}

	public Key(Text r) {
		init(r.getBytes(), 0, r.getLength(),
				EMPTY_BYTES,0,0,
				EMPTY_BYTES,0,0,
				EMPTY_BYTES,0,0);
		
		timestamp = Long.MAX_VALUE;
	}

	public Key(Text r, long ts) {
		this(r);
		timestamp = ts;
	}
	
	public Key(byte r[], int rOff, int rLen,
			byte cf[], int cfOff, int cfLen,
			byte cq[], int cqOff, int cqLen,
			byte cv[], int cvOff, int cvLen,
			long ts)
	{
		init(r, rOff, rLen,
				cf, cfOff, cfLen,
				cq, cqOff, cqLen,
				cv, cvOff, cvLen);
		
		this.timestamp = ts;
	}
	
	
	public Key(byte[] r, byte[] cf, byte[] cq, byte[] cv, long ts) {
		init(r, 0, r == null ? 0 :r.length,
			 cf, 0, cf == null ? 0 :cf.length,
			 cq, 0, cq == null ? 0 :cq.length,
			 cv, 0, cv == null ? 0 :cv.length);
		
		timestamp = ts;
	}
	
	
	public Key(byte[] r, byte[] cf, byte[] cq, byte[] cv, long ts, boolean deleted) {
		this(r, cf, cq, cv, ts);
		this.deleted = deleted;
	}
	
	public Key(Text r, Text cf) {
		init(r.getBytes(), 0, r.getLength(),
				cf.getBytes(), 0, cf.getLength(),
				EMPTY_BYTES,0,0,
				EMPTY_BYTES,0,0);
		
		timestamp = Long.MAX_VALUE;
	}
	
	public Key(Text r, Text cf, Text cq) {
		init(r.getBytes(), 0, r.getLength(),
				cf.getBytes(), 0, cf.getLength(),
				cq.getBytes(), 0, cq.getLength(),
				EMPTY_BYTES,0,0);
		
		timestamp = Long.MAX_VALUE;
	}
	
	public Key(Text r, Text cf, Text cq, byte[] cv) {
		init(r.getBytes(), 0, r.getLength(),
				cf.getBytes(), 0, cf.getLength(),
				cq.getBytes(), 0, cq.getLength(),
				cv,0,cv.length);
		
		timestamp = Long.MAX_VALUE;
	}
	
	public Key(Text r, Text cf, Text cq, long ts) {
		init(r.getBytes(), 0, r.getLength(),
				cf.getBytes(), 0, cf.getLength(),
				cq.getBytes(), 0, cq.getLength(),
				EMPTY_BYTES,0,0);
		timestamp = ts;
	}
	
	public Key(Text r, Text cf, Text cq, byte[] cv, long ts) {
		init(r.getBytes(), 0, r.getLength(),
			cf.getBytes(), 0, cf.getLength(),
			cq.getBytes(), 0, cq.getLength(),
			cv,0,cv.length);

		timestamp = ts;
	}
	
	
	
	
	private Key depth(int depth, byte appendByte, long ts){
		Key returnKey = new Key();
		switch (depth){
		case 1: 
			returnKey.init(getRowLen()+1, 0, 0 , getColumnVisibilityLen());
			System.arraycopy(keyData, getRowOffset(), returnKey.keyData, returnKey.getRowOffset(), getRowLen());
			returnKey.keyData[returnKey.getRowOffset() + getRowLen()] = appendByte;
			System.arraycopy(keyData, getColumnVisibilityOffset(), returnKey.keyData, returnKey.getColumnVisibilityOffset(), getColumnVisibilityLen());
			returnKey.setTimestamp(Long.MAX_VALUE);
			break;
		case 2:
			returnKey.init(getRowLen(), getColumnFamilyLen()+1, 0 , getColumnVisibilityLen());
			System.arraycopy(keyData, getRowOffset(), returnKey.keyData, returnKey.getRowOffset(), getRowLen());
			System.arraycopy(keyData, getColumnFamilyOffset(), returnKey.keyData, returnKey.getColumnFamilyOffset(), getColumnFamilyLen());
			returnKey.keyData[returnKey.getColumnFamilyOffset() + getColumnFamilyLen()] = appendByte;
			System.arraycopy(keyData, getColumnVisibilityOffset(), returnKey.keyData, returnKey.getColumnVisibilityOffset(), getColumnVisibilityLen());
			returnKey.setTimestamp(Long.MAX_VALUE);
			break;
		case 3: 
			returnKey.init(getRowLen(), getColumnFamilyLen(), getColumnQualifierLen()+1 , getColumnVisibilityLen());
			System.arraycopy(keyData, getRowOffset(), returnKey.keyData, returnKey.getRowOffset(), getRowLen());
			System.arraycopy(keyData, getColumnFamilyOffset(), returnKey.keyData, returnKey.getColumnFamilyOffset(), getColumnFamilyLen());
			System.arraycopy(keyData, getColumnQualifierOffset(), returnKey.keyData, returnKey.getColumnQualifierOffset(), getColumnQualifierLen());
			returnKey.keyData[returnKey.getColumnQualifierOffset() + getColumnQualifierLen()] = appendByte;
			System.arraycopy(keyData, getColumnVisibilityOffset(), returnKey.keyData, returnKey.getColumnVisibilityOffset(), getColumnVisibilityLen());
			returnKey.setTimestamp(Long.MAX_VALUE);
			break;
		case 4:
			// not sure if this should be implemented.  This could break Cell Level Authenticator or something else, possibly, if parent functions not used correctly
			// I currently throw an out of bounds exception, TODO
			returnKey.init(getRowLen(), getColumnFamilyLen(), getColumnQualifierLen() , getColumnVisibilityLen()+1);
			System.arraycopy(keyData, getRowOffset(), returnKey.keyData, returnKey.getRowOffset(), getRowLen());
			System.arraycopy(keyData, getColumnFamilyOffset(), returnKey.keyData, returnKey.getColumnFamilyOffset(), getColumnFamilyLen());
			System.arraycopy(keyData, getColumnQualifierOffset(), returnKey.keyData, returnKey.getColumnQualifierOffset(), getColumnQualifierLen());
			System.arraycopy(keyData, getColumnVisibilityOffset(), returnKey.keyData, returnKey.getColumnVisibilityOffset(), getColumnVisibilityLen());
			returnKey.keyData[returnKey.keyData.length-1] = appendByte;
			returnKey.setTimestamp(Long.MAX_VALUE);
			break;
		case 5:
		case 6:
			returnKey = new Key(this);
			returnKey.setTimestamp(ts);
			break;
		default : 
			throw new RuntimeException("Depth "+depth+" out of range, depth must be 1, 2, 3, or 4");
		}
		return returnKey;
	}
	
	/**
	 * Returns a key that will sort immediately after
	 * this key.  Depth 1 is for row, 2 is for column
	 * family, 3 is for column qualifier, and 4 is
	 * for timestamp.
	 * 
	 */
	
	public Key followingKey(int depth){
		return depth(depth, (byte)0, timestamp - 1);
	}
	
	/**
	 * 
	 * creates a new Key designed for endRow to end at 
	 * a specific row, colf, column, or timestamp,
	 * based on the inclusive boolean
	 * 
	 */
	public Key endKey(int depth){
		return depth(depth, (byte)0xff, Long.MAX_VALUE);
	}
	
	public Key(Key k) {
		this();
		set(k);
	}

	public Text getRow(Text r){
		r.set(keyData, 0, getRowLen());
		return r;
	}
	
	public Text getRow() {
		return getRow(new Text());
	}
	
	public int compareRow(Text r){
		return WritableComparator.compareBytes(keyData, 0, getRowLen(), r.getBytes(), 0, r.getLength());
	}
	
	public Text getColumnFamily(Text cf) {
		cf.set(keyData, colFamilyOffset, getColumnFamilyLen());
		return cf;
	}
	
	public Text getColumnFamily() {
		return getColumnFamily(new Text());
	}

	public int compareColumnFamily(Text cf){
		return WritableComparator.compareBytes(keyData, colFamilyOffset, getColumnFamilyLen(), cf.getBytes(), 0, cf.getLength());
	}
	
	public Text getColumnQualifier(Text cq) {
		cq.set(keyData, colQualifierOffset, getColumnQualifierLen());
		return cq;
	}
	
	public Text getColumnQualifier() {
		return getColumnQualifier(new Text());
	}

	public int compareColumnQualifier(Text cq){
		return WritableComparator.compareBytes(keyData, colQualifierOffset, getColumnQualifierLen(), cq.getBytes(), 0, cq.getLength());
	}
	
	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public boolean isDeleted() {
		return deleted;
	}
	
	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}
	
	public final byte[] getColumnVisibility() {
		int labelLen = totalLen - colVisibilityOffset;
		byte[] ret = new byte[labelLen];
		System.arraycopy(keyData,colVisibilityOffset,ret,0,labelLen);
		return ret;
	}
	
	public void set(Key k) {
		if(keyData.length < k.totalLen){
			keyData = new byte[k.totalLen];
		}
		
		System.arraycopy(k.keyData, 0, keyData, 0, k.totalLen);
		
		colFamilyOffset = k.colFamilyOffset;
		colQualifierOffset = k.colQualifierOffset;
		colVisibilityOffset = k.colVisibilityOffset;
		totalLen = k.totalLen;
		timestamp = k.timestamp;
		deleted = k.deleted;
		
	}
	
	public void readFields(DataInput in) throws IOException {
		colFamilyOffset = WritableUtils.readVInt(in);
		colQualifierOffset = WritableUtils.readVInt(in);
		colVisibilityOffset = WritableUtils.readVInt(in);
		totalLen = WritableUtils.readVInt(in);
		
		if(keyData.length < totalLen){
			keyData = new byte[totalLen];
		}
		
		in.readFully(keyData, 0, totalLen);
		
		timestamp = WritableUtils.readVLong(in);
		deleted = in.readBoolean();
	}

	public void write(DataOutput out) throws IOException {
		
		WritableUtils.writeVInt(out, colFamilyOffset);
		WritableUtils.writeVInt(out, colQualifierOffset);
		WritableUtils.writeVInt(out, colVisibilityOffset);

		WritableUtils.writeVInt(out, totalLen);
		
		out.write(keyData, 0, totalLen);
		
		WritableUtils.writeVLong(out, timestamp);
		out.writeBoolean(deleted);
	}

	/**
	 * compare to another Key, depth indicates which fields to compare
	 * 
	 * when depth is 1 only compare row
	 * when depth is 2 compare row and columnFamily
	 * when depth is 3 compare row, columnFamily, and columnQualifier
	 * when depth is 4 compare row, columnFamily, columnQualifier, and timestamp
	 * 
	 */
	
	public int compareTo(Object o, int depth) {
		Key other = (Key)o;
		
		if(depth < 1 || depth > 6){
			throw new RuntimeException("depth out of range");
		}
		int result = WritableComparator.compareBytes(keyData, 0, getRowLen(), other.keyData, 0, other.getRowLen());
		if(result != 0 || depth == 1) return result;

		result = WritableComparator.compareBytes(keyData, colFamilyOffset, getColumnFamilyLen(),
				other.keyData, other.colFamilyOffset, other.getColumnFamilyLen());
		if(result != 0 || depth == 2) return result;

		result = WritableComparator.compareBytes(keyData, colQualifierOffset, getColumnQualifierLen(),
				other.keyData, other.colQualifierOffset, other.getColumnQualifierLen());
		if(result != 0 || depth == 3) return result;
		
		result = WritableComparator.compareBytes(keyData, colVisibilityOffset, getColumnVisibilityLen(), 
				other.keyData, other.colVisibilityOffset, other.getColumnVisibilityLen());
		if(result != 0 || depth == 4) return result;
		
		if(timestamp < other.timestamp){
			result = 1;
		}else if(timestamp > other.timestamp){
			result = -1;
		}else{
			result = 0;
		}

		if(result != 0 || depth == 5) return result;
		
		if(deleted){
			if(other.deleted)
				result = 0;
			else
				result = -1;
		}else{
			if(other.deleted)
				result = 1;
			else
				result = 0;
		}
		
		return result;
	}
	
	/**
	 *  determines the order of keys in the MapFiles
	 *  we must then just make sure that *'s are not ever stored
	 */

	public int compareTo(Object o) {
		return compareTo(o, 6);
	}
	
	public boolean equals(Object o)
	{
		return compareTo(o) == 0;
	}
	
	// for convenience, not used in collections for sorting
	/*public int compareWithTimestampTo(Object o) {
		int result = compareTo(o);
		if(result != 0) return result;
		Key ok = (Key)o;
		if(this.timestamp > ok.timestamp) return -1;
		if(this.timestamp < ok.timestamp) return 1;
		return 0;
	}*/

	public int hashCode() {
		return WritableComparator.hashBytes(keyData, totalLen);
	}

	public String toString() {
		int labelLen = totalLen - colVisibilityOffset;
		byte[] ba = new byte[labelLen];
		System.arraycopy(keyData,colVisibilityOffset,ba,0,labelLen);
		String labelString = (new LabelExpression(ba)).toString();

		String s = new String(keyData,0, getRowLen()) + " " +
					new String(keyData, colFamilyOffset, getColumnFamilyLen()) + ":" +
					new String(keyData, colQualifierOffset, getColumnQualifierLen()) + " " +
					labelString + " " +
					Long.toString(timestamp)+" "+
					deleted;
		return s;
	}
	
	public String toStringNoTime() {
		
		int labelLen = totalLen - colVisibilityOffset;
		byte[] ba = new byte[labelLen];
		System.arraycopy(keyData,colVisibilityOffset,ba,0,labelLen);
		String labelString = (new LabelExpression(ba)).toString();

		String s = new String(keyData,0, getRowLen()) + " " +
					new String(keyData, colFamilyOffset, getColumnFamilyLen()) + ":" +
					new String(keyData, colQualifierOffset, getColumnQualifierLen()) + " " +
					labelString;
		return s;
	}
	
	public int getLength() {
		return totalLen;
	}
	
	public int getSize() {
		return keyData.length;
	}
}


