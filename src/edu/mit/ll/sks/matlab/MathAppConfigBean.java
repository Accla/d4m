/**
 * 
 */
package edu.mit.ll.sks.matlab;

/**
 * @author cyee
 *
 */
public class MathAppConfigBean {

	private String [] args=null;
	/**
	 * 
	 */
	public MathAppConfigBean(String [] args) {
		
		this.args = args;
	}
	public String[] getArgs() {
		return args;
	}
	public void setArgs(String[] args) {
		this.args = args;
	}

}
