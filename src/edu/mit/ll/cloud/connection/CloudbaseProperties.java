package edu.mit.ll.cloud.connection;

import java.io.File;
import java.net.URL;
import java.util.Properties;

import org.apache.log4j.Logger;

public class CloudbaseProperties {

	private static final Logger logger = Logger.getLogger(CloudbaseProperties.class);
	private static Properties props;

	public static void init(String filename) {
		props = new Properties();

		// load data from the properties file
		try {
			URL url = ClassLoader.getSystemResource(filename);
			if(url == null) {
				String pwd = new File(".").getAbsolutePath();
				File file = new File(pwd + "/" + filename);
				
				if(file != null && file.exists())
					url = file.toURL();
				else {
					file = new File(pwd + "/conf/" + filename);
				}
				
				if(file != null && file.exists())
					url = file.toURL();
				else {
					file = new File(pwd + "/../conf/" + filename);
				}
				
				if(file != null && file.exists())
					url = file.toURL();
				else {
					logger.error("cant find the properties file!");
					return;
				}
			}
			logger.info("trying to load: " + url);
			props.load(url.openStream());
			logger.info("just loaded: " + url);
		}
		catch (Exception e) {
			logger.error("", e);
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
 * %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% % D4M: Dynamic
 * Distributed Dimensional Data Model % MIT Lincoln Laboratory
 * %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% % (c) <2010>
 * Massachusetts Institute of Technology
 * %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
 */
