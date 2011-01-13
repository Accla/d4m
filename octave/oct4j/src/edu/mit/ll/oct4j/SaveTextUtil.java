/**
 * 
 */
package edu.mit.ll.oct4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.io.StringReader;
import org.apache.log4j.Logger;
/**
 * In Octave, the save -text - [VAR] command spits out the following
 *  # Created by Octave ....
 *  # name: 
 *  # type:
 *  # elements:
 *  # length:
 *  'some value'
 *  The SaveTextUtil
 * @author cyee
 *
 */
public class SaveTextUtil {
	private static Logger log = Logger.getLogger(SaveTextUtil.class);
	public static String CREATED_OCTAVE="# Created by Octave";
	public static String NAME="# name: ";
	public static String TYPE="# type: ";
	public static String ELEMENTS="# elements: ";
	public static String LENGTH="# length: ";
	public static String ROWS="# rows: ";
	public static String COLS="# columns: ";

	public static String TYPE_MATRIX="matrix";
	public static String TYPE_SQ_STRING="sq_string";
	public static String TYPE_STRING="string";
	public static String TYPE_SCALAR="scalar";
	public static String VALUE_KEY="VALUE";
	public static Map<String,String>  parse(BufferedReader reader) throws IOException {
		HashMap<String, String> map = new HashMap<String,String> ();

		//type: matrix
		//type: scalar
		//type: sq_string

		String str = reader.readLine();
		if(str == null || !str.startsWith(CREATED_OCTAVE)) {
			throw new ParseErrorException("BAD ::Not created by Octave -->  [ " + str + " ]");
		}
		log.debug("1. ****CREATE_BY_OCTAVE="+str);
		//Name field
		str = reader.readLine();
		log.debug("2.  .... "+str);
		if(str == null)
			str = reader.readLine();
		if(str.startsWith(NAME)) {
			String val = str.substring(NAME.length());
			map.put(NAME, val);
		}
		//Type field
		str = reader.readLine();
		String type=null;
		if(str.startsWith(TYPE)) {
			type = str.substring(TYPE.length());
			log.info("3 !!!!TYPE == "+type);
			map.put(TYPE, type);
		}

		if(type.equals(TYPE_SQ_STRING)) {
			do_sq_string(reader,map);
		} else if(type.equals(TYPE_SCALAR)) {
			do_scalar(reader,map);
		} else if(type.equals(TYPE_STRING)) {
			do_string(reader,map);
		}


		return map;
	}



	private static void do_string(BufferedReader reader, Map<String,String> map) throws IOException {
		do_sq_string(reader,map);
	}
	private static void do_sq_string(BufferedReader reader, Map<String,String> map) throws IOException {
		String str = reader.readLine();
		log.debug("4...."+str);
		int numElem=0;
		if(str.startsWith(ELEMENTS)) {

			String val = str.substring(ELEMENTS.length());
			numElem = Integer.parseInt(val);
			map.put(ELEMENTS, val);
		}

		if(numElem > 0) {
			//length field
			StringBuilder SB = new StringBuilder();
			for(int i =0; i < numElem; i++) {
				str = reader.readLine();
				log.debug("5...."+str);
				StringBuilder builder = new StringBuilder();
				if(str.startsWith(LENGTH)) {

					String val = str.substring(LENGTH.length());
					map.put(LENGTH, val);

					//Finally ... get the value
					int len = Integer.parseInt(val);

					boolean first = true;
					while (builder.length() < len) {
						if (!first) {
							builder.append('\n');
						}
						builder.append(reader.readLine());
						first = false;
					}
					log.debug("builder->val={"+builder.toString()+"}");
					SB.append(builder.toString());
					SB.append("\n");
				}
			
			}
			log.debug("6.... FINAL-->VALUE=["+ SB.toString()+"]");
			map.put(VALUE_KEY, SB.toString());
			
		}


	}

	private static void do_scalar(BufferedReader reader, Map<String,String> map) throws IOException {
		String str = reader.readLine();
		map.put(VALUE_KEY, str);
	}

	//doMatrixType
	//doStringType
	//doScalarType

	public static void main(String[] args) throws InterruptedException, IOException {

		String str = "# name: BLAH";
		StringBuffer sb = new StringBuffer();
		sb.append("# Created by Octave 3.2.4,");
		sb.append("\n");
		sb.append(str);
		sb.append("\n");
		sb.append(TYPE+"sq_string");
		sb.append("\n");
		sb.append(ELEMENTS+"1");
		sb.append("\n");
		sb.append(LENGTH+"10");
		sb.append("\n");
		sb.append("Hello World");
		sb.append("\n");
		System.out.println(sb.toString());

		BufferedReader reader = new BufferedReader(new StringReader(sb.toString()));
		Map<String,String> map = SaveTextUtil.parse(reader);

		for(Entry<String,String> entry : map.entrySet()) {
			System.out.println("KEY="+entry.getKey()+", VAL="+entry.getValue());
		}
		System.out.println("===================++++++++++++++=================");
		if(str.startsWith(NAME)) {
			System.out.println("Got name");
		}
		StringBuffer sb1 = new StringBuffer();
		sb1.append("# Created by Octave 3.2.4,");
		sb1.append("\n");
		sb1.append(str);
		sb1.append("\n");
		sb1.append(TYPE+"matrix");
		sb1.append("\n");
		sb1.append(ROWS+"3");
		sb1.append("\n");
		sb1.append(COLS+"3");
		sb1.append("\n");
		sb1.append("1 2 3");
		sb1.append("\n");
		sb1.append("1 2 4");
		sb1.append("\n");
		sb1.append("1 2 5");
		sb1.append("\n");
		System.out.println(sb1.toString());


	}
}
