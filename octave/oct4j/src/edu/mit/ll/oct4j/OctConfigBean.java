/**
 * 
 */
package edu.mit.ll.oct4j;

/**
 * Configuration object to configure the octave process
 * @author cyee
 *
 */
public class OctConfigBean {

	private String [] params=null;

	public String[] getParams() {
		return params;
	}
	public void setParams(String[] params) {
		this.params = params;
	}
	/**
	 * 
	 */
	public OctConfigBean(String [] params) {
		this.params = params;
	}

}
