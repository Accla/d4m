/**
 * 
 */
package edu.mit.ll.oct4j;

import java.io.IOException;
import java.io.Writer;
import java.util.concurrent.Callable;

/**
 * @author cyee
 *
 */
public class CallableWriteData implements Callable<Void> {

	private WriteTaskIF writeTask = null;
	private Writer writer=null;
	private String ending=null;

	/**
	 * 
	 *
	public CallableWriteData(Writer writer, String ending) {
		this.writer = writer;
		this.ending = ending;
	}
	*/

	public CallableWriteData(WriteTaskIF writeTask,Writer writer,String ending) {
		this.writer = writer;
		this.writeTask = writeTask;
		this.ending = ending;
	}
	/* (non-Javadoc)
	 * @see java.util.concurrent.Callable#call()
	 */
	@Override
	public Void call() throws Exception {
		try {
			writeTask.doIt(this.writer);
		} catch (final IOException e) {

			throw new IOException("WriteTask problem ... ", e);
		}
		try {
			this.writer.write("\nprintf(\"\\n%s\\n\", \"" + this.ending + "\");\n");
			this.writer.flush();
		} catch (final IOException e) {

			throw new IOException("Problem writing the ending ... ", e);
		}
		return null;
	}

}
