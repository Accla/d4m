package edu.mit.ll.cloud.connection;

import java.io.File;
import java.net.URL;
import java.util.Properties;

public class CloudbaseProperties {

	private static Properties props;

	public static void init(String filename) {
		props = new Properties();

		// load data from the properties file
		try {
			URL url = ClassLoader.getSystemResource(filename);
			if(url == null) {
				String pwd = new File(".").getAbsolutePath();
				File file = new File(pwd + "/conf/" + filename);
								
				if(file != null && file.exists())
					url = file.toURL();
				else {
					file = new File(pwd + "/../conf/" + filename);
				}
				
				if(file != null && file.exists())
					url = file.toURL();
				else {
					file = new File(pwd + "/../../conf/" + filename);
				}
				
				if(file != null && file.exists())
					url = file.toURL();
				else {
					file = new File(pwd + "/../../../conf/" + filename);
				}
				
				if(file != null && file.exists())
					url = file.toURL();
				else {
					System.err.println("cant find the properties file!");
					return;
				}
			}
			System.out.println("trying to load: " + url);
			props.load(url.openStream());
			System.out.println("just loaded: " + url);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static Object get(Object key) {
		// load the properties file if not already loaded
		if (props == null) {
			init("cloudbase.properties");
		}

		// return the value asked for
		return props.get(key);
	}
}

/*
 * %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% 
 * % D4M: Dynamic Distributed Dimensional Data Model 
 * % MIT Lincoln Laboratory
 * %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% 
 * % (c) <2010> Massachusetts Institute of Technology
 * %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
 */
