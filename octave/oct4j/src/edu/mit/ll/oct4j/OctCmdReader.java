/**
 * 
 */
package edu.mit.ll.oct4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

/**
 * octave command reader
 * @author cyee
 *
 */
public class OctCmdReader extends Reader {

	private BufferedReader reader=null;
	private String ending=null;
	boolean EOF = false;
	boolean BEGIN= true;
	StringBuilder sb=null;
	/**
	 * 
	 */
	public OctCmdReader(BufferedReader reader, String ending) {
		this.reader = reader;
		this.ending = ending;
	}

	/**
	 * @param lock
	 */
	public OctCmdReader(Object lock) {
		super(lock);
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see java.io.Reader#read(char[], int, int)
	 */
	@Override
	public int read(char[] cbuf, int off, int len) throws IOException {
		int numChars=-1;
		if(EOF) {
			numChars=-1;
		}
		else {
			if(sb == null) {
				//Read lines into buffer
				//Populate cbuf
				String s = this.reader.readLine();
				if (s == null) {
					throw new IOException("Octave IO reading problem");
				}

				if(ending.equals(s)) {
					EOF=true;
					return -1;
				}
				sb = new StringBuilder(s.length()+1);
				if(BEGIN) {
					BEGIN=false;
				} else {
					sb.append("\n");
				}
				sb.append(s);
			}
			numChars = Math.min(sb.length(), len);
			sb.getChars(0, numChars, cbuf, off);
			if( numChars == sb.length()) {
				sb = null;
			} else {
				sb.delete(0, numChars);
			}
		}
		return numChars;
	}

	/* (non-Javadoc)
	 * @see java.io.Reader#close()
	 */
	@Override
	public void close() throws IOException {
		char [] c = new char[1024*4];
		while(read(c) != -1){}
	}

}
