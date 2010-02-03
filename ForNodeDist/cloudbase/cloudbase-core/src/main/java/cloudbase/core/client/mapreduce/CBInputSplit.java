package cloudbase.core.client.mapreduce;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Set;
import java.util.TreeSet;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.InputSplit;

import cloudbase.core.data.Column;

/**
 * represents a range of a table to be scanned
 * as part of a map reduction
 * 
 *
 */
public class CBInputSplit implements InputSplit {

	private String table;
	private Text startRow;
	private Text stopRow;
	private Set<Column> columns;
	private String[] locations;
	
	// need this
	public CBInputSplit() {
		startRow = new Text();
		stopRow = new Text();
	}
	
	public CBInputSplit(String table, Text startRow, Text stopRow, Set<Column> columns, Set<Short> auths, String[] locations) {
		
		this.table = new String(table);
		
		if(startRow != null)
			this.startRow = new Text(startRow);
		else
			this.startRow = new Text();
		
		if(stopRow != null)
			this.stopRow = new Text(stopRow);
		else
			this.stopRow = new Text();
		
		this.columns = new TreeSet<Column>();
		
		for(Column col : columns)
			this.columns.add(col);
		
		this.locations = new String[locations.length];
		System.arraycopy(locations, 0, this.locations, 0, locations.length);
	}
	
	public long getLength() throws IOException {
		// TODO will having no length be a problem?
		return 10;
	}

	public String[] getLocations() throws IOException {
		return locations;
	}
	
	public void readFields(DataInput in) throws IOException {
		table = in.readUTF();
		startRow.readFields(in);
		stopRow.readFields(in);
		
		int numCols = in.readInt();
		columns = new TreeSet<Column>();
		
		for(int i=0 ; i < numCols; i++) {
			Column col = new Column();
			col.readFields(in);
			columns.add(col);
		}
	}

	public void write(DataOutput out) throws IOException {
		out.writeUTF(table);
		startRow.write(out);
		stopRow.write(out);
		
		out.writeInt(columns.size());
		for(Column col : columns) 
			col.write(out);
	}

	public String getTableName() {
		return table;
	}
	
	public Text getStartRow() {
		return startRow;
	}

	public Text getStopRow() {
		return stopRow;
	}

	public Set<Column> getColumns() {
		return columns;
	}

}
