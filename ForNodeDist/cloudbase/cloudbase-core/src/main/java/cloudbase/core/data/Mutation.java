package cloudbase.core.data;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.io.WritableComparator;
import org.apache.log4j.Logger;

import cloudbase.core.CBConstants;
import cloudbase.core.data.ColumnUpdate;
import cloudbase.core.security.LabelExpression;

import com.facebook.thrift.TBase;
import com.facebook.thrift.TException;
import com.facebook.thrift.protocol.TField;
import com.facebook.thrift.protocol.TList;
import com.facebook.thrift.protocol.TProtocol;
import com.facebook.thrift.protocol.TProtocolUtil;
import com.facebook.thrift.protocol.TStruct;
import com.facebook.thrift.protocol.TType;

/**
 * Mutation represents an action that manipulates a row in a table
 * 
 */
public class Mutation implements TBase, Writable, Comparable<Mutation>  {
	private static Logger log = Logger.getLogger(Mutation.class.getName());
	private List<ColumnUpdate> updates = new ArrayList<ColumnUpdate>();
	
	private byte[] row;

	private long size;
	
	/* BEGIN THRIFT Generated Code*/
	//Any modifications to this generated code should be noted
	//here so that when a new generated version is copied in
	//the modifications can be made again
	
	//NOTE added code to end of read() 
	
	  public final Isset __isset = new Isset();
	  public static final class Isset implements java.io.Serializable {
	    /**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		public boolean row = false;
	    public boolean updates = false;
	  }


	  public Mutation(
	    byte[] row,
	    List<ColumnUpdate> updates)
	  {
	    this();
	    this.row = row;
	    this.__isset.row = true;
	    this.updates = updates;
	    this.__isset.updates = true;
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
		            this.row = iprot.readBinary();
		            this.__isset.row = true;
		          } else { 
		            TProtocolUtil.skip(iprot, field.type);
		          }
		          break;
		        case 2:
		          if (field.type == TType.LIST) {
		            {
		              TList _list0 = iprot.readListBegin();
		              this.updates = new ArrayList<ColumnUpdate>(_list0.size);
		              for (int _i1 = 0; _i1 < _list0.size; ++_i1)
		              {
		                ColumnUpdate _elem2 = new ColumnUpdate();
		                _elem2 = new ColumnUpdate();
		                _elem2.read(iprot);
		                this.updates.add(_elem2);
		              }
		              iprot.readListEnd();
		            }
		            this.__isset.updates = true;
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
	    computeSize();
	  }

	  public void write(TProtocol oprot) throws TException {
		    TStruct struct = new TStruct("Mutation");
		    oprot.writeStructBegin(struct);
		    TField field = new TField();
		    if (this.row != null) {
		      field.name = "row";
		      field.type = TType.STRING;
		      field.id = 1;
		      oprot.writeFieldBegin(field);
		      oprot.writeBinary(this.row);
		      oprot.writeFieldEnd();
		    }
		    if (this.updates != null) {
		      field.name = "updates";
		      field.type = TType.LIST;
		      field.id = 2;
		      oprot.writeFieldBegin(field);
		      {
		        oprot.writeListBegin(new TList(TType.STRUCT, this.updates.size()));
		        for (ColumnUpdate _iter3 : this.updates)        {
		          _iter3.write(oprot);
		        }
		        oprot.writeListEnd();
		      }
		      oprot.writeFieldEnd();
		    }
		    oprot.writeFieldStop();
		    oprot.writeStructEnd();
	  }
	
	
	/* END THRIFT Generated Code*/
	
	public Mutation() {
		size = 0;
	}
	
	public Mutation(Text row) {
		this.row = new byte[row.getLength()];
		System.arraycopy(row.getBytes(), 0, this.row, 0, row.getLength());
		size = 0;
	}
	
	/**
	 * Adds or updates a column value pair to the row mutation
	 * 
	 */
	public void put(Text columnFamily, Text columnQualifier, Value value) {
		size += columnFamily.getLength() + columnQualifier.getLength() + value.getSize();
		updates.add(new ColumnUpdate(columnFamily, columnQualifier, value));
	}
	
	public void put(Text columnFamily, Text columnQualifier, LabelExpression columnVisibility, Value value) {
		size += columnFamily.getLength() + columnQualifier.getLength() + value.getSize();
		if (columnVisibility != null) {
			updates.add(new ColumnUpdate(columnFamily, columnQualifier, value, columnVisibility.toByteArray()));
		}
		else {
			updates.add(new ColumnUpdate(columnFamily, columnQualifier, value));
		}
	}


	/**
	 * Adds or updates a column value pair to the row mutation
	 */
	public void put(Text columnFamily, Text columnQualifier, long timestamp, Value value) {
		size += columnFamily.getLength() + columnQualifier.getLength() + value.getSize();
		ColumnUpdate cvp = new ColumnUpdate(columnFamily, columnQualifier, value);
		cvp.setTimestamp(timestamp);
		updates.add(cvp);
	}
	
	public void put(Text columnFamily, Text columnQualifier, LabelExpression columnVisibility, long timestamp, Value value) {
		size += columnFamily.getLength() + columnQualifier.getLength() + value.getSize();
		ColumnUpdate cvp = new ColumnUpdate(columnFamily, columnQualifier, value, columnVisibility.toByteArray());
		cvp.setTimestamp(timestamp);
		updates.add(cvp);
	}
	
	
	/**
	 * Removes a given column from the mutation
	 * 
	 */
	public void remove(Text columnFamily, Text columnQualifier) {	
		size += columnFamily.getLength() + columnQualifier.getLength();
		updates.add(new ColumnUpdate(columnFamily, columnQualifier));
	}

	public void remove(Text columnFamily, Text columnQualifier, LabelExpression columnVisibility) {	
		size += columnFamily.getLength() + columnQualifier.getLength();
		updates.add(new ColumnUpdate(columnFamily, columnQualifier, columnVisibility.toByteArray()));
	}
	
	/**
	 * Removes a given column from the mutation
	 * 
	 */
	public void remove(Text columnFamily, Text columnQualifier, long timestamp) {	
		size += columnFamily.getLength() + columnQualifier.getLength();
		ColumnUpdate cvp = new ColumnUpdate(columnFamily, columnQualifier);
		cvp.setTimestamp(timestamp);
		updates.add(cvp);
	}
	
	public void remove(Text columnFamily, Text columnQualifier, LabelExpression columnVisibility, long timestamp) {	
		size += columnFamily.getLength() + columnQualifier.getLength();
		ColumnUpdate cvp = new ColumnUpdate(columnFamily, columnQualifier, columnVisibility.toByteArray());
		cvp.setTimestamp(timestamp);
		updates.add(cvp);
	}
	
	/**
	 * Returns the row associated with this mutation
	 * 
	 */
	public byte[] getRow() {
		return row;
	}
	
	
	public Collection<ColumnUpdate> getUpdates() {
        return updates;
    }
    
	/**
	 * @deprecated renamed to getUpdates
	 */
	@Deprecated
    public Collection<ColumnUpdate> getMutations() {
		return updates;
	}
	
	private static int _length(byte[] bytes) {
		if (bytes == null) {
			return 0;
		}
		return bytes.length;
	}
	
	private void computeSize(){
		size = row.length;
		
		for (ColumnUpdate cvp : updates) {
			size += _length(cvp.getColumnFamily()) + _length(cvp.getColumnQualifier()) + _length(cvp.getColumnVisibility()) + cvp.getValue().length + 8; //add 8 bytes for timestamp
		}
	}
	
	/**
	 * Populates the mutations data fields from a DataInput object
	 * 
	 */
	public void readFields(DataInput in) throws IOException {
		updates.clear();
		
		int rowLen = in.readInt();
		row = new byte[rowLen];
		in.readFully(row);
		
		size = row.length;
		
		int numMutations = in.readInt();
		
		for(int i = 0; i < numMutations; i++) {
			ColumnUpdate cvp = new ColumnUpdate();
			cvp.readFields(in);
			size += cvp.getColumnFamily().length + cvp.getColumnQualifier().length + cvp.getColumnVisibility().length + cvp.getValue().length + 8; //add 8 bytes for timestamp
			if(size > CBConstants.MAX_MUTATION_BATCH_SIZE) {
				log.error("A single mutation reached the max mutation batch size. bailing ...");
				// throw away rest of the mutations
				for(int j=i+1; j < numMutations; j++)
					cvp.readFields(in); // overwrite
				return;
			}
			updates.add(cvp);
		}
		
		computeSize();
	}

	/**
	 * Writes the mutations data fields to a DataOutput object
	 * 
	 */
	public void write(DataOutput out) throws IOException {
		
		out.writeInt(row.length);
		out.write(row);
		
		out.writeInt(updates.size());
		
		for(int i = 0; i < updates.size(); i++){
			updates.get(i).write(out);
		}
	}

	
	public int compareTo(Mutation  o) {
		byte[] otherRow = o.row;
		return WritableComparator.compareBytes(row, 0, row.length, otherRow, 0, otherRow.length);
	}

	public long numBytes() {
		return size;
	}
	
	public String toString(){
		return new String(row)+" "+updates;
	}
}
