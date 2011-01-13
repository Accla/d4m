/**
 * 
 */
package edu.mit.ll.oct4j;

/**
 * @author cyee
 *
 */
public class OctDataObject {

	private String name=null;
	private String type=null;
	private Object value=null;
	/**
	 *  val    value
	 */
	public OctDataObject(String name, Object val) {
		// TODO Auto-generated constructor stub
		if(val instanceof String) {
			this.type="sq_string";
		} else if ( val instanceof Double || val instanceof Integer) {
			this.type= "scalar";
		} else {
			this.type="unknown";
		}
		this.name = name;
		this.value = val;
	}

	/*
	 *  ToOctave is used to write to the octave process
	 */
	public String toOctave () {
		String s=null;
		StringBuffer sb = new StringBuffer();
		if(this.type.equals("sq_string")) {
			sb.append("");
			sb.append("# type: string");
			sb.append("\n");
			sb.append("# elements: 1").append("\n");
			int len = ((String)this.value).length();
			sb.append("# length: ").append(len).append("\n");
			sb.append((String)this.value).append("\n");
			sb.append("");
			s = sb.toString();
		}
		return s;
	}
	
	public String toString() {
		return this.value.toString();
	}
	
	/*
	 * return the value
	 */
	public Object getValue() {
		return this.value;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setValue(Object value) {
		this.value = value;
	}
	
}
