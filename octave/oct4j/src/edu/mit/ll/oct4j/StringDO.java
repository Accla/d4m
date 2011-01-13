/**
 * 
 */
package edu.mit.ll.oct4j;

/**
 * String class to a octave string
 * @author cyee
 *
 */
public class StringDO extends BaseOctDataObject {

	/**
	 * 
	 */
	public StringDO() {
		super();
	}
	private String string;
	/**
	 * 
	 */
	public StringDO(Object str) {
		this.string = (String)str;
	}

	public String toString() {
		return this.string;
	}
	
	public String get() {
		return this.string;
	}
	
	public void set(String obj) {
		// TODO Auto-generated method stub
		this.string = obj;
	}

	@Override
	public String getType() {
		return "sq_string";
	}
}
