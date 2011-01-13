/**
 * 
 */
package edu.mit.ll.oct4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.CharBuffer;
import java.util.Map;

/**
 * Read data task
 * @author cyee
 *
 */
public class DataReadTask implements ReadTaskIF {

	String key=null;
	BufferedReader reader=null;
	BaseOctDataObject data;
	/**
	 * 
	 */
	public DataReadTask(String key) {
		// TODO Auto-generated constructor stub
		this.key =key;
	}



	/* (non-Javadoc)
	 * @see edu.mit.ll.oct4j.ReadProcessIF#getReader()
	 */
	@Override
	public Reader getReader() {

		return this.reader;
	}

	/* (non-Javadoc)
	 * @see edu.mit.ll.oct4j.ReadProcessIF#getData()
	 */
	@Override
	public BaseOctDataObject getData() {
		return this.data;
	}


	@Override
	public void doIt() throws IOException {
		// 
		
	}





	@Override
	public void doIt(Reader reader) throws IOException {
		
		BufferedReader bufferedReader = new BufferedReader(reader);
		Map<String,String> map = SaveTextUtil.parse(bufferedReader);
		//Get the data
		ReadIO interpreter = new ReadIO();
		interpreter.read(map);
		
		this.data=interpreter.getData();
		
	}
/*
	@Override
	public void doIt(Reader reader) throws IOException {
		
		BufferedReader bufferedReader = new BufferedReader(reader);
		//Read the first line
		String s = bufferedReader.readLine();
		//Check for Octave preamble 
		if (s == null || !s.startsWith(SaveTextUtil.CREATED_OCTAVE)) {
            throw new ParseErrorException("BAD ::Not created by Octave -->  [ " + s + " ]");
        }
		
		//2. Grab the NAME field
		s = bufferedReader.readLine();
		String name_field=SaveTextUtil.NAME;
		if(!s.startsWith(name_field)) {
			throw new ParseErrorException("BAD::Not NAME field, but [ " + s +" ]");
		}
		String name = s.substring(name_field.length());
		if(key.equals(name)) {
			//Extract the value
			//Check the type
			
		}
		
	}
	*/
	
}
