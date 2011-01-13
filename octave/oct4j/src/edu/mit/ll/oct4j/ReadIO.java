/**
 * 
 */
package edu.mit.ll.oct4j;

import java.io.BufferedReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * @author cyee
 *
 */
public class ReadIO {

	private BufferedReader reader;
	private BaseOctDataObject data;
	/**
	 * 
	 */
	public ReadIO() {
		// TODO Auto-generated constructor stub
	}

	public void read(Map<String,String> datamap) {
		
		String type = datamap.get(SaveTextUtil.TYPE);
		
		Method [] method = this.getClass().getDeclaredMethods();
		Method theMethod=null;
		for(int i = 0; i < method.length; i++) {
			String methodName  = method[i].getName();
			if(methodName.equals(type)) {
				theMethod= method[i];
				break;
			}
		}
		if(theMethod != null) {
			
			try {
				
				theMethod.invoke(this,datamap);
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}
	
	public void string(Map<String,String> datamap) {
		sq_string(datamap);
	}	
	public void sq_string(Map<String,String> datamap) {
		StringDO sdo = new StringDO();
		String value = datamap.get(SaveTextUtil.VALUE_KEY);
		sdo.set(value);
		this.data = sdo;
	}
	
	public void scalar(Map<String,String> datamap) {
		DoubleDO d = new DoubleDO(datamap.get(SaveTextUtil.VALUE_KEY));
		this.data = d;
	}
	public BaseOctDataObject getData() {
		return this.data;
	}
}
