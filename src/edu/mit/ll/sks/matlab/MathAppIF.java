/**
 * 
 */
package edu.mit.ll.sks.matlab;

/**
 * @author cyee
 *
 */
public interface MathAppIF {

	public String getResult();
	public void process(String host, String request);
	public void process(String script, String [] params);
	public void stop();
}
