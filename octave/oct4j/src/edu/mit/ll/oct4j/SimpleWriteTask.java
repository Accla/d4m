/**
 * 
 */
package edu.mit.ll.oct4j;

import java.io.IOException;
import java.io.Writer;

/**
 * @author cyee
 *
 */
public class SimpleWriteTask implements WriteTaskIF {
	String cmd=null;

	/**
	 * 
	 */
	public SimpleWriteTask(String cmd) {
		this.cmd=cmd;
	}

	/* (non-Javadoc)
	 * @see edu.mit.ll.oct4j.TaskIF#doIt()
	 */
	@Override
	public void doIt() throws IOException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see edu.mit.ll.oct4j.WriteTaskIF#doIt(java.io.Writer)
	 */
	@Override
	public void doIt(Writer writer) throws IOException {
		writer.write(this.cmd);

	}

	/* (non-Javadoc)
	 * @see edu.mit.ll.oct4j.WriteTaskIF#getWriter()
	 */
	@Override
	public Writer getWriter() {
		// TODO Auto-generated method stub
		return null;
	}

}
