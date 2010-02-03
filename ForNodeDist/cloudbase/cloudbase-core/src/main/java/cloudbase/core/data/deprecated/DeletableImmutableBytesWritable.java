/**
 * The old Value from 0.5 branch: used for converting Map Files
 * 
 */
package cloudbase.core.data.deprecated;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;


public class DeletableImmutableBytesWritable extends ImmutableBytesWritable {
	private boolean deleted;
	
	public DeletableImmutableBytesWritable(byte[] bytes, boolean b) {
		super(bytes);
		this.deleted = b;
	}

	public DeletableImmutableBytesWritable() {
		super();
		deleted = false;
	}

	public DeletableImmutableBytesWritable(ImmutableBytesWritable value, boolean b) {
		super(value);
		this.deleted = b;
	}

	public DeletableImmutableBytesWritable(byte[] bytes, int offset, int length, boolean b) {
		super(bytes, offset, length);
		this.deleted = b;
	}

	public boolean isDeleted() {
		return deleted;
	}
	
	public void delete() {
		deleted = true;
	}
	
	public void readFields(final DataInput in) throws IOException {
		super.readFields(in);
		deleted = in.readBoolean();
	}

	/** {@inheritDoc} */
	public void write(final DataOutput out) throws IOException {
		super.write(out);
		out.writeBoolean(deleted);
	}
	
	public void set(DeletableImmutableBytesWritable o) {
		super.set(o.get());
		this.deleted = o.deleted;
	}
}
