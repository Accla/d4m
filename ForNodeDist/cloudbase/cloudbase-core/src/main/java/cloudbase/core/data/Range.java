package cloudbase.core.data;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.WritableComparable;

import com.facebook.thrift.TException;
import com.facebook.thrift.protocol.TField;
import com.facebook.thrift.protocol.TProtocol;
import com.facebook.thrift.protocol.TProtocolUtil;
import com.facebook.thrift.protocol.TStruct;
import com.facebook.thrift.protocol.TType;


public class Range implements WritableComparable<Range> {
	private Key start;
	private Key stop;
	private boolean startKeyInclusive;
	private boolean stopKeyInclusive;
	private boolean infiniteStartKey;
	private boolean infiniteStopKey;
	
	/* BEGIN THRIFT Generated Code*/
	//Any modifications to this generated code should be noted
	//here so that when a new generated version is copied in
	//the modifications can be made again
	
	

	  public final Isset __isset = new Isset();
	  @SuppressWarnings("serial")
    public static final class Isset implements java.io.Serializable {
	    public boolean start = false;
	    public boolean stop = false;
	    public boolean startKeyInclusive = false;
	    public boolean stopKeyInclusive = false;
	    public boolean infiniteStartKey = false;
	    public boolean infiniteStopKey = false;
	  }

	  public Range(
	    Key start,
	    Key stop,
	    boolean startKeyInclusive,
	    boolean stopKeyInclusive,
	    boolean infiniteStartKey,
	    boolean infiniteStopKey)
	  {
	    this();
	    this.start = start;
	    this.__isset.start = true;
	    this.stop = stop;
	    this.__isset.stop = true;
	    this.startKeyInclusive = startKeyInclusive;
	    this.__isset.startKeyInclusive = true;
	    this.stopKeyInclusive = stopKeyInclusive;
	    this.__isset.stopKeyInclusive = true;
	    this.infiniteStartKey = infiniteStartKey;
	    this.__isset.infiniteStartKey = true;
	    this.infiniteStopKey = infiniteStopKey;
	    this.__isset.infiniteStopKey = true;
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
	            this.start = new Key();
	            this.start.read(iprot);
	            this.__isset.start = true;
	          } else { 
	            TProtocolUtil.skip(iprot, field.type);
	          }
	          break;
	        case 2:
	          if (field.type == TType.STRUCT) {
	            this.stop = new Key();
	            this.stop.read(iprot);
	            this.__isset.stop = true;
	          } else { 
	            TProtocolUtil.skip(iprot, field.type);
	          }
	          break;
	        case 3:
	          if (field.type == TType.BOOL) {
	            this.startKeyInclusive = iprot.readBool();
	            this.__isset.startKeyInclusive = true;
	          } else { 
	            TProtocolUtil.skip(iprot, field.type);
	          }
	          break;
	        case 4:
	          if (field.type == TType.BOOL) {
	            this.stopKeyInclusive = iprot.readBool();
	            this.__isset.stopKeyInclusive = true;
	          } else { 
	            TProtocolUtil.skip(iprot, field.type);
	          }
	          break;
	        case 5:
	          if (field.type == TType.BOOL) {
	            this.infiniteStartKey = iprot.readBool();
	            this.__isset.infiniteStartKey = true;
	          } else { 
	            TProtocolUtil.skip(iprot, field.type);
	          }
	          break;
	        case 6:
	          if (field.type == TType.BOOL) {
	            this.infiniteStopKey = iprot.readBool();
	            this.__isset.infiniteStopKey = true;
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
	    TStruct struct = new TStruct("Range");
	    oprot.writeStructBegin(struct);
	    TField field = new TField();
	    if (this.start != null) {
	      field.name = "start";
	      field.type = TType.STRUCT;
	      field.id = 1;
	      oprot.writeFieldBegin(field);
	      this.start.write(oprot);
	      oprot.writeFieldEnd();
	    }
	    if (this.stop != null) {
	      field.name = "stop";
	      field.type = TType.STRUCT;
	      field.id = 2;
	      oprot.writeFieldBegin(field);
	      this.stop.write(oprot);
	      oprot.writeFieldEnd();
	    }
	    field.name = "startKeyInclusive";
	    field.type = TType.BOOL;
	    field.id = 3;
	    oprot.writeFieldBegin(field);
	    oprot.writeBool(this.startKeyInclusive);
	    oprot.writeFieldEnd();
	    field.name = "stopKeyInclusive";
	    field.type = TType.BOOL;
	    field.id = 4;
	    oprot.writeFieldBegin(field);
	    oprot.writeBool(this.stopKeyInclusive);
	    oprot.writeFieldEnd();
	    field.name = "infiniteStartKey";
	    field.type = TType.BOOL;
	    field.id = 5;
	    oprot.writeFieldBegin(field);
	    oprot.writeBool(this.infiniteStartKey);
	    oprot.writeFieldEnd();
	    field.name = "infiniteStopKey";
	    field.type = TType.BOOL;
	    field.id = 6;
	    oprot.writeFieldBegin(field);
	    oprot.writeBool(this.infiniteStopKey);
	    oprot.writeFieldEnd();
	    oprot.writeFieldStop();
	    oprot.writeStructEnd();
	  }

	
	/* END THRIFT Generated Code*/
	
	/**
	 * Creates a range that goes from negative to positive infinity
	 */
	  
	public Range(){
		this(null, true, null, true);
	}
	
	
	/**
	 * Creates a range from startKey inclusive to endKey inclusive
	 * 
	 * @param startKey set this to null when negative infinity is needed
	 * @param endKey  set this to null when positive infinity is needed
	 */
	public Range(Key startKey, Key endKey){
		this(startKey, true, endKey, true);
	}
	
	/**
	 * Creates a range from startRow inclusive to endRow inclusive
	 * 
	 * @param startRow set this to null when negative infinity is needed
	 * @param endRow  set this to null when positive infinity is needed
	 */
	public Range(Text startRow, Text endRow) {
		this(startRow == null ? null : new Key(startRow), endRow == null ? null : new Key(endRow));		
	}
	
	/**
	 * @param startKey          set this to null when negative infinity is needed
	 * @param startKeyInclusive determines if the ranges includes the start key
	 * @param endKey            set this to null when negative infinity is needed
	 * @param endKeyInclusive   determines if the range includes the end key
	 */
	public Range(Key startKey, boolean startKeyInclusive, Key endKey, boolean endKeyInclusive){
		this.start = startKey;
		this.startKeyInclusive = startKeyInclusive;
		this.infiniteStartKey = startKey == null;
		this.stop = endKey;
		this.stopKeyInclusive = endKeyInclusive;
		this.infiniteStopKey = stop == null;
		
		if(!infiniteStartKey && !infiniteStopKey && beforeStartKey(endKey)){
			throw new IllegalArgumentException("Start key must be less than end key in range ("+startKey+", "+endKey+")");
		}	
	}

	/**
	 * Copy constructor
	 * 
	 * @param range
	 */
	public Range(Range range) {
		this(range.start, range.stop, range.startKeyInclusive, range.stopKeyInclusive, range.infiniteStartKey, range.infiniteStopKey);
	}

	public Key getStartKey(){
		if(infiniteStartKey){
			return null;
		}
		return start;
	}
	
	public boolean beforeStartKey(Key key){
		if(infiniteStartKey){
			return false;
		}
		
		if(startKeyInclusive){
			return key.compareTo(start) < 0;
		}else{
			return key.compareTo(start) <= 0;
		}
	}
	
	public Key getEndKey(){
		if(infiniteStopKey){
			return null;
		}
		return stop;
	}
	
	public boolean afterEndKey(Key key){
		if(infiniteStopKey)
			return false;
		
		if(stopKeyInclusive)
			return stop.compareTo(key) < 0;
		else
			return stop.compareTo(key) <= 0;
	}
	
	public int hashCode(){
		int startHash = infiniteStartKey ? 0 : start.hashCode();
		int stopHash = infiniteStopKey ? 0 : stop.hashCode();
		
		return startHash + stopHash;
	}
	
	public boolean equals(Object o){
		Range otherRange = (Range)o;
		
		boolean startEqual;
		
		if(infiniteStartKey)
			startEqual = otherRange.infiniteStartKey;
		else
			startEqual = start.equals(otherRange.start) && startKeyInclusive == otherRange.startKeyInclusive;
		
		if(!startEqual)
			return false;
		
		boolean stopEqual;
		if(infiniteStopKey)
			stopEqual = otherRange.infiniteStopKey;
		else
			stopEqual = stop.equals(otherRange.stop) && stopKeyInclusive == otherRange.stopKeyInclusive;
		
		return stopEqual;
	}

	public int compareTo(Range o) {
		int comp;
		
		if(infiniteStartKey)
			if(o.infiniteStartKey)
				comp = 0;
			else
				comp = -1;
		else
			if(o.infiniteStartKey)
				comp = 1;
			else
				comp = start.compareTo(o.start);
		
		if(comp == 0)
			if(infiniteStopKey)
				if(o.infiniteStopKey)
					comp = 0;
				else
					comp = 1;
			else
				if(o.infiniteStopKey)
					comp = -1;
				else
					comp = stop.compareTo(o.stop);
		
		return comp;
	}
	
	public boolean contains(Key key){
		return !beforeStartKey(key) && !afterEndKey(key);
	}
	
	public static List<Range> mergeOverlapping(List<Range> ranges)
	{
		
		if(ranges.size() == 0){
			return Collections.emptyList();
		}
		
		ArrayList<Range> ral = null;
		if(ranges instanceof ArrayList)
			ral = (ArrayList)ranges;
		else
			ral = new ArrayList<Range>(ranges);
		
		ArrayList<Range> ret = new ArrayList<Range>();
		
		Collections.sort(ral);
		
		Range currentRange = ral.get(0);
		boolean currentStartKeyInclusive = ral.get(0).startKeyInclusive;
		
		for(int i = 1; i < ral.size(); i++){
			//because of inclusive switch, equal keys may not be seen
			
			if(currentRange.infiniteStopKey){
				//this range has the minimal start key and
				//an infinite end key so it will contain all 
				//other ranges
				break;
			}
			
			boolean startKeysEqual;
			if(ral.get(i).infiniteStartKey){
				//previous start key must be infinite because it is sorted
				assert(currentRange.infiniteStartKey);
				startKeysEqual = true;
			}else if(currentRange.infiniteStartKey){
				startKeysEqual = false;
			}else if(currentRange.start.compareTo(ral.get(i).start) == 0){
				startKeysEqual = true;
			}else{
				startKeysEqual = false;
			}
			
			if(startKeysEqual || currentRange.contains(ral.get(i).start)){
				if(ral.get(i).startKeyInclusive){
					currentStartKeyInclusive = true;
				}
				
				if(ral.get(i).infiniteStopKey || ral.get(i).stop.compareTo(currentRange.stop) > 0){
					currentRange = new Range(currentRange.getStartKey(), currentStartKeyInclusive, 
							ral.get(i).getEndKey(), ral.get(i).stopKeyInclusive);
				}/*else currentRange contains ral.get(i)*/
			}else{
				ret.add(currentRange);
				currentRange = ral.get(i);
				currentStartKeyInclusive = ral.get(i).startKeyInclusive;
			}
		}
		
		ret.add(currentRange);
		
		return ret;
	}
	
	public String toString(){
		return "("+start+","+stop+")";
	}

	public void readFields(DataInput in) throws IOException {
		start = new Key();
		start.readFields(in);
		stop = new Key();
		stop.readFields(in);
		startKeyInclusive = in.readBoolean();
		stopKeyInclusive = in.readBoolean();
	}

	public void write(DataOutput out) throws IOException {
		start.write(out);
		stop.write(out);
		out.writeBoolean(startKeyInclusive);
		out.writeBoolean(stopKeyInclusive);
	}

	public boolean isEndKeyInclusive() {
		return stopKeyInclusive;
	}

	public boolean isStartKeyInclusive() {
		return startKeyInclusive;
	}
}
