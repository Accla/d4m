/**
 * 
 */
package edu.mit.ll.oct4j;

import java.io.Reader;
import java.io.IOException;

/**
 * @author cyee
 *
 */
public interface ReadTaskIF extends TaskIF {

	public void doIt(Reader reader) throws IOException;

	public Reader getReader();
	public BaseOctDataObject getData();
}
