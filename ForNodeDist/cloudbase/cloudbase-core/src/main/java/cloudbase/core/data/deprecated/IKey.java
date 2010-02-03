package cloudbase.core.data.deprecated;

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
import org.apache.log4j.Logger;


public class IKey implements WritableComparable<Object> {
	private int colFamilyOffset;
	private int colQualifierOffset;
	private int labelOffset;
	private int totalLen;
	
	private byte keyData[];	
	
	private long timestamp;


	private static final byte EMPTY_BYTES[] = new byte[0];
	private static final byte[] COLON = new byte[]{':'};
	@SuppressWarnings("unused")
    private static Logger log = Logger.getLogger(IKey.class.getName());
	
	private final void init(int rLen, int cfLen, int cqLen, int lLen)
	{
		colFamilyOffset = rLen;
		colQualifierOffset = colFamilyOffset + cfLen;
		labelOffset = colQualifierOffset + cqLen;
		totalLen = labelOffset + lLen;
		keyData = new byte[totalLen];
	}
	
	private final void init(byte r[], int rOff, int rLen,
			byte cf[], int cfOff, int cfLen,
			byte cq[], int cqOff, int cqLen,
			byte l[], int lOff, int lLen)
	{
		init(rLen, cfLen, cqLen, lLen);
		
		System.arraycopy(r, rOff, keyData, 0, rLen);
		System.arraycopy(cf, cfOff, keyData, colFamilyOffset, cfLen);
		System.arraycopy(cq, cqOff, keyData, colQualifierOffset, cqLen);
		System.arraycopy(l, lOff, keyData, labelOffset, lLen);

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
		return labelOffset - colQualifierOffset;
	}

	public final int getLabelOffset() {
		return labelOffset;
	}
	
	public final int getLabelLen() {
		return totalLen - labelOffset;
	}
	
	
	public final byte[] getKeyData(){
		return keyData;
	}
	
	public IKey() {
		keyData = EMPTY_BYTES;
	}

	public IKey(Text r) {
		init(r.getBytes(), 0, r.getLength(),
				EMPTY_BYTES,0,0,
				EMPTY_BYTES,0,0,
				EMPTY_BYTES,0,0);
		
		timestamp = Long.MAX_VALUE;
	}

	public IKey(Text r, Text cf, Text cq) {
		init(r.getBytes(), 0, r.getLength(),
				cf.getBytes(), 0, cf.getLength(),
				cq.getBytes(), 0, cq.getLength(),
				EMPTY_BYTES,0,0);
		
		timestamp = Long.MAX_VALUE;
	}
	
	public IKey(Text r, Text cf, Text cq, byte[] labels) {
		init(r.getBytes(), 0, r.getLength(),
				cf.getBytes(), 0, cf.getLength(),
				cq.getBytes(), 0, cq.getLength(),
				labels,0,labels.length);
		
		timestamp = Long.MAX_VALUE;
	}
	
	public IKey(Text r, Text cf, Text cq, long ts) {
		init(r.getBytes(), 0, r.getLength(),
				cf.getBytes(), 0, cf.getLength(),
				cq.getBytes(), 0, cq.getLength(),
				EMPTY_BYTES,0,0);
		timestamp = ts;
	}
	
	public IKey(Text r, Text cf, Text cq, long ts, byte[] labels) {
		init(r.getBytes(), 0, r.getLength(),
			cf.getBytes(), 0, cf.getLength(),
			cq.getBytes(), 0, cq.getLength(),
			labels,0,labels.length);

		timestamp = ts;
	}
	
	// not using string split for efficiency
	public IKey(Text r, Text column, long ts) {
		int index = -1;
		
		for(int i = 0; i < column.getLength(); i++){
			if(column.getBytes()[i] == ':'){
				index = i;
				break;
			}
		}
		
		if(index == -1){
			init(r.getBytes(), 0, r.getLength(),
					column.getBytes(), 0, column.getLength(),
					EMPTY_BYTES, 0, 0,
					EMPTY_BYTES, 0, 0);
		}else{
			init(r.getBytes(), 0, r.getLength(),
					column.getBytes(), 0, index,
					column.getBytes(), index+1, column.getLength() - (index + 1),
					EMPTY_BYTES,0,0);
		}
		
		timestamp = ts;
	}
	
	
	public IKey(Text r, Text column) {
		this(r, column, Long.MAX_VALUE);
	}
	
	// not using string split for efficiency
	public IKey(Text r, Text column, byte[] labels) {
		
		int index = -1;
		
		for(int i = 0; i < column.getLength(); i++){
			if(column.getBytes()[i] == ':'){
				index = i;
				break;
			}
		}
		
		if(index == -1){
			init(r.getBytes(), 0, r.getLength(),
					column.getBytes(), 0, column.getLength(),
					EMPTY_BYTES, 0, 0,
					labels,0,labels.length);
		}else{
			init(r.getBytes(), 0, r.getLength(),
					column.getBytes(), 0, index,
					column.getBytes(), index+1, column.getLength() - (index + 1),
					labels,0,labels.length);
		}
		
		timestamp = Long.MAX_VALUE;
	}
	
	
	private IKey depth(int depth, byte appendByte, long ts){
		IKey returnKey = new IKey();
		switch (depth){
		case 1: 
			returnKey.init(getRowLen()+1, 0, 0 , getLabelLen());
			System.arraycopy(keyData, getRowOffset(), returnKey.keyData, returnKey.getRowOffset(), getRowLen());
			returnKey.keyData[returnKey.getRowOffset() + getRowLen()] = appendByte;
			System.arraycopy(keyData, getLabelOffset(), returnKey.keyData, returnKey.getLabelOffset(), getLabelLen());
			returnKey.setTimestamp(Long.MAX_VALUE);
			break;
		case 2:
			returnKey.init(getRowLen(), getColumnFamilyLen()+1, 0 , getLabelLen());
			System.arraycopy(keyData, getRowOffset(), returnKey.keyData, returnKey.getRowOffset(), getRowLen());
			System.arraycopy(keyData, getColumnFamilyOffset(), returnKey.keyData, returnKey.getColumnFamilyOffset(), getColumnFamilyLen());
			returnKey.keyData[returnKey.getColumnFamilyOffset() + getColumnFamilyLen()] = appendByte;
			System.arraycopy(keyData, getLabelOffset(), returnKey.keyData, returnKey.getLabelOffset(), getLabelLen());
			returnKey.setTimestamp(Long.MAX_VALUE);
			break;
		case 3: 
			returnKey.init(getRowLen(), getColumnFamilyLen(), getColumnQualifierLen()+1 , getLabelLen());
			System.arraycopy(keyData, getRowOffset(), returnKey.keyData, returnKey.getRowOffset(), getRowLen());
			System.arraycopy(keyData, getColumnFamilyOffset(), returnKey.keyData, returnKey.getColumnFamilyOffset(), getColumnFamilyLen());
			System.arraycopy(keyData, getColumnQualifierOffset(), returnKey.keyData, returnKey.getColumnQualifierOffset(), getColumnQualifierLen());
			returnKey.keyData[returnKey.getColumnQualifierOffset() + getColumnQualifierLen()] = appendByte;
			System.arraycopy(keyData, getLabelOffset(), returnKey.keyData, returnKey.getLabelOffset(), getLabelLen());
			returnKey.setTimestamp(Long.MAX_VALUE);
			break;
		case 4: 
			returnKey = new IKey(this);
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
	
	public IKey followingKey(int depth){
		return depth(depth, (byte)0, timestamp - 1);
	}
	
	/**
	 * 
	 * creates a new IKey designed for endRow to end at 
	 * a specific row, colf, column, or timestamp,
	 * based on the inclusive boolean
	 * 
	 */
	public IKey endKey(int depth, boolean inclusive){
		IKey returnKey;
		char[] mod;
		if (inclusive){
			switch (depth){
			case 1: 
				mod = this.getRowString().toCharArray();
				mod[mod.length-1]++;
				returnKey = new IKey(new Text(new String(mod)));
				break;
			case 2: 
				mod = this.getColumnFamily().toString().toCharArray();
				mod[mod.length-1]++;
				returnKey = new IKey(this.getRow(),new Text(new String(mod)));
				break;
			case 3: 
				mod = this.getColumn().toString().toCharArray();
				mod[mod.length-1]++;
				returnKey = new IKey(this.getRow(),new Text(new String(mod)));
				break;
			case 4: 
				returnKey = new IKey(this);
				returnKey.setTimestamp(Long.MIN_VALUE);
				break;
			default : 
				returnKey = new IKey(this);
				break;
			}
		}
		else {
			returnKey = depth(depth, (byte)0xff, Long.MAX_VALUE);
		}
		return returnKey;
	}
	
	
	public void setColumn(Text column) {
		int index = -1;
		
		for(int i = 0; i < column.getLength(); i++){
			if(column.getBytes()[i] == ':'){
				index = i;
				break;
			}
		}
		
		int newLen;
		int labelLen = totalLen - labelOffset;
		if(index == -1){
			newLen = colFamilyOffset + column.getLength() + labelLen;
		}else{
			newLen = colFamilyOffset + column.getLength() - 1 + labelLen;
		}
		int newLabelOffset = newLen - labelLen;

		if(keyData.length < newLen){
			//reallocate and copy the row key
			byte newData[] = new byte[newLen];
			System.arraycopy(keyData, 0, newData, 0, colFamilyOffset);
			System.arraycopy(keyData, labelOffset, newData, newLabelOffset, labelLen);
			keyData = newData;
		}
		else {
			/// need to adjust position of labels
			byte tmpData[] = new byte[labelLen];
			System.arraycopy(keyData, labelOffset, tmpData, 0, labelLen);
			System.arraycopy(tmpData, 0, keyData, newLabelOffset, labelLen);
		}
		
		if(index == -1){
			colQualifierOffset = colFamilyOffset + column.getLength();
			totalLen = colQualifierOffset + labelLen;
			labelOffset = newLabelOffset;
			System.arraycopy(column.getBytes(), 0, keyData, colFamilyOffset, column.getLength());
		}else{
			colQualifierOffset = colFamilyOffset + index;
			totalLen = colQualifierOffset + (column.getLength() - (index + 1)) + labelLen;
			labelOffset = newLabelOffset;
			System.arraycopy(column.getBytes(), 0, keyData, colFamilyOffset, index);
			System.arraycopy(column.getBytes(), index+1, keyData, colQualifierOffset, column.getLength() - (index + 1));
		}
	}
	
	// allow specification of the timestamp
	//public IKey(Text r, Text cf, Text cq, long ts) {
	//	row = r;
	//	columnFamily = cf;
	//	columnQualifier = cq;
		//timestamp = ts;
	//}
	
	public IKey(IKey k) {
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
	
	public String getRowString() {
		return new String(keyData,0,getRowLen());
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

	public Text getColumn(Text col) {
		col.set(keyData, colFamilyOffset, getColumnFamilyLen());
		col.append(COLON, 0, 1);
		col.append(keyData, colQualifierOffset, getColumnQualifierLen());
		return col;
	}
	
	public Text getColumn() {
		return getColumn(new Text());
	}
	

	public void setLabels(byte[] labels) {
		int labelLen = totalLen - labelOffset;
		if( labels.length > labelLen) { //need to create new byte array
			byte[] newData = new byte[totalLen + labels.length - labelLen];
			System.arraycopy(keyData,0,newData,0,labelOffset);
			keyData = newData;
		}
		System.arraycopy(labels,0,keyData,labelOffset,labels.length);
		totalLen += labels.length - labelLen;
		labelLen = labels.length;
			
	}
	
	public final byte[] getLabels() {
		int labelLen = totalLen - labelOffset;
		byte[] ret = new byte[labelLen];
		System.arraycopy(keyData,labelOffset,ret,0,labelLen);
		return ret;
	}
	

	
	public void set(IKey k) {
		if(keyData.length < k.totalLen){
			keyData = new byte[k.totalLen];
		}
		
		System.arraycopy(k.keyData, 0, keyData, 0, k.totalLen);
		
		colFamilyOffset = k.colFamilyOffset;
		colQualifierOffset = k.colQualifierOffset;
		labelOffset = k.labelOffset;
		totalLen = k.totalLen;
		timestamp = k.timestamp;
		
	}
	
	public void readFields(DataInput in) throws IOException {
		colFamilyOffset = WritableUtils.readVInt(in);
		colQualifierOffset = WritableUtils.readVInt(in);
		labelOffset = WritableUtils.readVInt(in);
		totalLen = WritableUtils.readVInt(in);
		
		if(keyData.length < totalLen){
			keyData = new byte[totalLen];
		}
		
		in.readFully(keyData, 0, totalLen);
		
		timestamp = WritableUtils.readVLong(in);		
		
	}

	public void write(DataOutput out) throws IOException {
		
		WritableUtils.writeVInt(out, colFamilyOffset);
		WritableUtils.writeVInt(out, colQualifierOffset);
		WritableUtils.writeVInt(out, labelOffset);

		WritableUtils.writeVInt(out, totalLen);
		
		out.write(keyData, 0, totalLen);
		
		WritableUtils.writeVLong(out, timestamp);
		
	}

	/**
	 * compare to another IKey, depth indicates which fields to compare
	 * 
	 * when depth is 1 only compare row
	 * when depth is 2 compare row and columnFamily
	 * when depth is 3 compare row, columnFamily, and columnQualifier
	 * when depth is 4 compare row, columnFamily, columnQualifier, and timestamp
	 * 
	 */
	
	public int compareTo(Object o, int depth) {
		IKey other = (IKey)o;
		
		if(depth < 1 || depth > 4){
			throw new RuntimeException("depth out of range");
		}
		int result = WritableComparator.compareBytes(keyData, 0, getRowLen(), other.keyData, 0, other.getRowLen());
		if(result != 0 || depth == 1) return result;

		if(getColumnFamilyLen() == 1 && keyData[colFamilyOffset] == '*') return 0; // match on wildcard
		result = WritableComparator.compareBytes(keyData, colFamilyOffset, getColumnFamilyLen(),
				other.keyData, other.colFamilyOffset, other.getColumnFamilyLen());
		if(result != 0 || depth == 2) return result;

		if(getColumnQualifierLen() == 1 && keyData[colQualifierOffset] == '*') return 0;
		result = WritableComparator.compareBytes(keyData, colQualifierOffset, getColumnQualifierLen(),
				other.keyData, other.colQualifierOffset, other.getColumnQualifierLen());
		if(result != 0 || depth == 3) return result;
		
		if(timestamp < other.timestamp){
			result = 1;
		}else if(timestamp > other.timestamp){
			result = -1;
		}else{
			result = 0;
		}

		return result;
	}
	
	/**
	 *  determines the order of keys in the MapFiles
	 *  we must then just make sure that *'s are not ever stored
	 */

	public int compareTo(Object o) {
		return compareTo(o, 4);
	}
	
	public boolean equals(Object o)
	{
		boolean comp = compareTo(o) == 0;
		return comp;
	}
	
	// for convenience, not used in collections for sorting
	/*public int compareWithTimestampTo(Object o) {
		int result = compareTo(o);
		if(result != 0) return result;
		IKey ok = (IKey)o;
		if(this.timestamp > ok.timestamp) return -1;
		if(this.timestamp < ok.timestamp) return 1;
		return 0;
	}*/

	public int hashCode() {
		return WritableComparator.hashBytes(keyData, totalLen);
	}

//	public String toString() {
//		int labelLen = totalLen - labelOffset;
//		byte[] ba = new byte[labelLen];
//		System.arraycopy(keyData,labelOffset,ba,0,labelLen);
//		String labelString = (new LabelExpression(ba)).toString();
//
//		String s = new String(keyData,0, getRowLen()) + " " +
//					new String(keyData, colFamilyOffset, getColumnFamilyLen()) + ":" +
//					new String(keyData, colQualifierOffset, getColumnQualifierLen()) + " " +
//					labelString + " " +
//					Long.toString(timestamp);
//		return s;
//	}
//	
//	public String toStringNoTime() {
//		
//		int labelLen = totalLen - labelOffset;
//		byte[] ba = new byte[labelLen];
//		System.arraycopy(keyData,labelOffset,ba,0,labelLen);
//		String labelString = (new LabelExpression(ba)).toString();
//
//		String s = new String(keyData,0, getRowLen()) + " " +
//					new String(keyData, colFamilyOffset, getColumnFamilyLen()) + ":" +
//					new String(keyData, colQualifierOffset, getColumnQualifierLen()) + " " +
//					labelString;
//		return s;
//	}
	
	public int getLength() {
		return totalLen;
	}
	
	public long getSize() {
		return keyData.length;
	}
	
	/*
	public static void main(String[] args) {
		
		Text row = new Text("somerow");
		Text cf = new Text("foo");
		Text cq = new Text("bar");
		short[] sa = new short[]{8,116,194,273,9910};
		LabelSet ls = new LabelSet(sa);
		IKey ik = new IKey(row,cf,cq,ls.toByteArray());
		
		log.info(ik.toString());
		
	}
	*/
	
}


