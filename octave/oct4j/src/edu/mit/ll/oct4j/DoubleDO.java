/**
 * 
 */
package edu.mit.ll.oct4j;

/**
 * @author cyee
 *
 */
public class DoubleDO extends BaseOctDataObject {

	private String type="scalar";
	private Double value=null;
	/**
	 * 
	 */
	public DoubleDO(String d) {
		// TODO Auto-generated constructor stub
		value = Double.valueOf(d);
	}

	/* (non-Javadoc)
	 * @see edu.mit.ll.oct4j.BaseOctDataObject#getType()
	 */
	@Override
	public String getType() {
		// TODO Auto-generated method stub
		return type;
	}

	public Double get() {
		return value;
	}
}
