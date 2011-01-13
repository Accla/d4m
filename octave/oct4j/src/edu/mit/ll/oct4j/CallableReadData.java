/**
 * 
 */
package edu.mit.ll.oct4j;

import java.io.BufferedReader;
import java.io.Reader;
import java.util.concurrent.Callable;

/**
 * @author cyee
 *
 */
public class CallableReadData implements Callable<Void> {

	private ReadTaskIF readerProc=null;
	private BufferedReader breader=null;
	private String ending = null;
	/**
	 * 
	 */
	public CallableReadData(ReadTaskIF reader, BufferedReader breader,String ending) {
		this.readerProc = reader;
		this.breader = breader;
		this.ending = ending;
	}
	

	/**
	 * @param breader
	 * @param name  variable name to look for
	 * @param ending
	 */
//	public CallableReadData(BufferedReader breader, String name,String ending) {
//		super();
//		this.breader = breader;
//		this.ending = ending;
//	
//	}


	/* (non-Javadoc)
	 * @see java.util.concurrent.Callable#call()
	 */
	@Override
	public Void call() throws Exception {
		// Use OctCmdReader
		
		OctCmdReader reader = new OctCmdReader(this.breader,this.ending);
		this.readerProc.doIt(reader);
		reader.close();
		
		
		return null;
	}




}
