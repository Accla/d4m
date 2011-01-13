/**
 * 
 */
package edu.mit.ll.oct4j;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import javax.imageio.spi.ServiceRegistry;
/**
 * A factory to generate OctDataObjects
 * 
 *     map:
 *        sq_string, edu.mit.ll.oct4j.StringDO
 * @author cyee
 *
 */
public class ODataObjFactory {

	/**
	 * 
	 */
	private ODataObjFactory() {
		super();
		init();
	}

	private static ConcurrentHashMap<String,BaseOctDataObject> map=null;
	private static ConcurrentHashMap<String,String> MAP=null;

	public static <T extends BaseOctDataObject> T getObjInstance(String typename, Map<String,String> datamap) {

		//Cast.cast(Class, obj)
		return null;
	}

	public static BaseOctDataObject getValue(String key) {
		init();
		return map.get(key);
	}
	
	public static String getClass(String key) {
		return MAP.get(key);
	}
	/*
	 * read properties file
	 */

	public static void initByConf() {
		if (map== null) {

			Properties prop = new Properties();
			try {
				String name="conf.xml";
				URL url = ClassLoader.getSystemClassLoader().getResource(name); 
				if (url != null) {
					String filename = url.getFile();
					FileInputStream fis = new FileInputStream(filename);

					prop.loadFromXML(fis);
					MAP=new ConcurrentHashMap<String,String>();
					Enumeration<?> e = prop.propertyNames();
					while(e.hasMoreElements()) {
						String key = (String)e.nextElement();
						String value = prop.getProperty(key);
						MAP.put(key, value);
					}
				} else {
					System.out.println("cannot find "+name);
				}

			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(MAP != null) {
			System.out.println(MAP.size());
		}
	}


	public static void init() {
		Iterator<BaseOctDataObject> sp = ServiceRegistry.lookupProviders(BaseOctDataObject.class);
		while (sp.hasNext()) {
			final BaseOctDataObject obj = sp.next();
			System.out.println(obj.getType());
			map.put(obj.getType(), obj);
		}
	}

	public static void main(String[] args) throws InterruptedException {
		ODataObjFactory.initByConf();
		String clzname = ODataObjFactory.getClass("sq_string");
		System.out.println("class name="+clzname);
		//BaseOctDataObject val = ODataObjFactory.getValue("sq_string");
		//System.out.println("++++  "+val+"  ++++");
	}
}
