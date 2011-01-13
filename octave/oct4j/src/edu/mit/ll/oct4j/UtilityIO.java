/**
 * 
 */
package edu.mit.ll.oct4j;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

/**
 * Input/Output utility class contains commonly used methods
 * 
 * @author cyee
 *
 */
public class UtilityIO {
	public static int BUFFER_SIZE = 1024*8;
	
	public static long copy(Reader reader, Writer writer) throws IOException {
		final char[] buffer = new char[BUFFER_SIZE];
		long cnt = 0;
		while (true) {
			int numCharsRead = reader.read(buffer, 0, BUFFER_SIZE);
			if (numCharsRead == -1) {
				break;
			}
			cnt += numCharsRead;
			writer.write(buffer, 0, numCharsRead);
		}
		writer.flush();
		return cnt;
	}


	public static String read() {
		return null;
	}
	

    /**
     * @param writer
     * @param name  variable name
     * @param data  data to be written to octave
     * @throws IOException
     */
    public static void write( Writer writer,  String name,  BaseOctDataObject data) throws IOException {
        writer.write("# name: " + name + "\n");
        WriteIO writeIO = new WriteIO();
        writeIO.write(writer, data);
    }

}
