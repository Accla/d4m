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
public interface WriteTaskIF extends TaskIF {

	public void doIt(Writer writer) throws IOException;
	public Writer getWriter();
}
