/**
 * 
 */
package edu.mit.ll.d4m.db.cloud;

/**
 * The ColumnBean holds the properties for a column such as
 *   columnFamily
 *   columnVisibility
 *   columnQualifier
 *  
 * @author cyee
 *
 */
public class ColumnBean {
	private String columnFamily = "vertexfamily";
	private String columnVisibility = "";
	private String columnQualifier = columnFamily+"Value:";
	private String operator="|";
	/**
	 * 
	 */
	public ColumnBean() {

	}
	public String getColumnFamily() {
		return columnFamily;
	}
	public void setColumnFamily(String columnFamily) {
		this.columnFamily = columnFamily;
	}
	public String getColumnVisibility() {
		return columnVisibility;
	}
	public void setColumnVisibility(String columnVisibility) {
		this.columnVisibility = columnVisibility;
	}
	public String getColumnQualifier() {
		String retval=this.columnQualifier;
		if(!this.columnQualifier.endsWith(":")) {
			retval +=":";
		}
		return retval;
	}
	public void setColumnQualifier(String columnQualifier) {
		this.columnQualifier = columnQualifier;
	}

	public void setColumnVisibility(String [] visibility) {
		StringBuffer sb = new StringBuffer();
		String sep="";
		for(int i = 0; i< visibility.length; i++) {
			sb.append(sep);
			sep=operator;
			sb.append(visibility[i]);
		}
		this.setColumnVisibility(sb.toString());
		
	}
}
