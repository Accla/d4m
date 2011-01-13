/**
 * 
 */
package edu.mit.ll.sks.matlab;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Logger;

/**
 * Factory to generate either MatlabApp or OctaveApp.
 * @author cyee
 *
 */
public class MathAppFactory {
	private static Logger log = Logger.getLogger(MathAppFactory.class.getName());

	//enum
	/**
	 * 
	 */
	public MathAppFactory() {
		// TODO Auto-generated constructor stub
	}
	public static MathAppIF getInstance(String appname, String [] args) throws SecurityException, IllegalArgumentException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
		MathAppIF mathapp = null;
		Class<?> cls = createClass(appname);
		mathapp = (MathAppIF) constructClass(cls,args);
		return mathapp;
	}

	public static MathAppIF getInstance(String appname) {
		MathAppIF mathapp = null;
		ClassLoader loader = ClassLoader.getSystemClassLoader();

		Class<?> cls = null;
		String clsname=null;
		
		for(AppEnum ae : AppEnum.values()) {
			if(ae.getName().equals(appname)) {
				clsname = ae.getClsname();
			}
		}
	
		log.info("Class name = "+clsname);
		try {
			cls = loader.loadClass(clsname);
			mathapp = (MathAppIF)cls.newInstance();
			
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return mathapp;
	}
	
	public static Object constructClass(Class<?> cls, String [] args) throws SecurityException, NoSuchMethodException, IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException {
		Constructor<?> ct = cls.getConstructor(new Class [] {MathAppConfigBean.class});
	
		MathAppConfigBean confBean = new MathAppConfigBean(args);
		
		Object obj = ct.newInstance(confBean);
		return obj;
	}
	public static Class<?> createClass(String appname) {
		ClassLoader loader = ClassLoader.getSystemClassLoader();

		Class<?> cls = null;
		String clsname=null;
		
		for(AppEnum ae : AppEnum.values()) {
			if(ae.getName().equals(appname)) {
				clsname = ae.getClsname();
			}
		}
	
		log.info("Class name = "+clsname);
		try {
			cls = loader.loadClass(clsname);
		}catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return cls;

	}
	public static void main(String[] args)
	{
		for(AppEnum a : AppEnum.values()) {
			
			System.out.println(a.getName()+ ", "+a.getClsname());
		}

		MathAppIF mathapp = MathAppFactory.getInstance("octave");
		System.out.println(mathapp.getClass().getCanonicalName());
		
	}

	public enum AppEnum {
		OCTAVE("octave","edu.mit.ll.sks.matlab.D4mOctaveApp"),
		MATLAB("matlab","edu.mit.ll.sks.matlab.MatlabApp");

		private String name=null;
		private String clsname=null;
		AppEnum(String name,String clsname) {
			this.name = name;
			this.clsname = clsname;
		}
		public String getName(){
			return this.name.trim();
		}
		public String getClsname() {
			return this.clsname.trim();
		}
	}
}
