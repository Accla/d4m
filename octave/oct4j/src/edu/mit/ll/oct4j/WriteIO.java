package edu.mit.ll.oct4j;

import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class WriteIO {
	private Writer writer;
	private BaseOctDataObject data;
	public WriteIO() {
		// TODO Auto-generated constructor stub
	}

	public void write(Writer writer, BaseOctDataObject data) {
		this.writer = writer;
		this.data = data;
		
		//Use reflection to find the method based on the data's type.
		String type = data.getType();
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
				
				theMethod.invoke(this, data);
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
	
	public void sq_string(BaseOctDataObject o) {
		StringDO sdo = (StringDO)o;
		String string = sdo.toString();
        try {
			writer.write("" //
			        + "# type: sq_string\n" //
			        + "# elements: 1\n" //
			        + "# length: " + string.length() + "\n" //
			        + string + "\n" //
			        + "");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	public void matrix(BaseOctDataObject data) {
		//TODO
	}
}
