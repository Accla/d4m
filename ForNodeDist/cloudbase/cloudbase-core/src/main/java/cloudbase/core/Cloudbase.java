package cloudbase.core;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import cloudbase.core.conf.CBConfiguration;
import cloudbase.core.util.Version;

public class Cloudbase {

	private static Logger log = Logger.getLogger(Cloudbase.class.getName());
	private static String instance = null;
	private static Integer dataVersion = null;

	public static synchronized String getInstanceID(){

		if(instance != null){
			return instance;
		}

		Configuration conf = new Configuration();
		try{
			FileSystem fs = FileSystem.get(conf);

			FileStatus[] files = fs.listStatus(CBConstants.INSTANCE_ID_LOCATION);
			if(files == null || files.length == 0) {
				log.error("unable obtain instance id");
				throw new RuntimeException("Cloudbase not initialized, there is no instance id");
			}
			else
				instance = files[0].getPath().getName();
			return instance;
		}catch(IOException e){
			throw new RuntimeException("Cloudbase not initialized, there is no instance id", e);
		}
	}

	public static synchronized void setInstance(String instance) {
		Cloudbase.instance = instance;
	}

	public static synchronized int getCloudbasePersistentVersion() {
		if (dataVersion != null) return dataVersion;

		Configuration conf = new Configuration();
		try {
			FileSystem fs = FileSystem.get(conf);

			FileStatus[] files = fs.listStatus(CBConstants.DATA_VERSION_LOCATION);
			if(files == null || files.length == 0) {
				dataVersion = -1; //assume it is 0.5 or earlier
			}
			else {
				dataVersion = Integer.parseInt(files[0].getPath().getName());
			}
			return dataVersion;
		}catch(IOException e){
			throw new RuntimeException("Unable to read cloudbase version: an error occured.", e);
		}

	}

	public static void init(String logConf) throws UnknownHostException {
		//setup logging for the tablet server
		if(System.getenv("CLOUDBASE_HOME") == null){
			System.err.println("CLOUDBASE_HOME not set.... exiting...");
			System.exit(1);
		}

		//Setup logging.
		System.setProperty("cloudbase.core.dir.home", System.getenv("CLOUDBASE_HOME"));

		if(System.getenv("CLOUDBASE_LOG_DIR") != null)
			System.setProperty("cloudbase.core.dir.log", System.getenv("CLOUDBASE_LOG_DIR"));
		else
			System.setProperty("cloudbase.core.dir.log", System.getenv("CLOUDBASE_HOME")+"/logs/");

		System.setProperty("cloudbase.core.ip.localhost.hostname",InetAddress.getLocalHost().getHostName().toString());

		PropertyConfigurator.configure(System.getenv("CLOUDBASE_HOME")+logConf);

		log.info("Instance "+Cloudbase.getInstanceID());
		log.info("Data Version " + Cloudbase.getCloudbasePersistentVersion());

		int dataVersion = Cloudbase.getCloudbasePersistentVersion();
		Version codeVersion = new Version(CBConstants.VERSION);
		if (dataVersion != CBConstants.DATA_VERSION){
			throw new RuntimeException("This version of cloudbase ("+codeVersion+
					") is not compatible with files stored using data version" + 
					dataVersion);
		}

		Iterator<Entry<String, String>> iter = CBConfiguration.getInstance().iterator();
		TreeMap<String, String> sortedProps = new TreeMap<String, String>();
		while(iter.hasNext()){
			Entry<String, String> entry = iter.next();
			if(entry.getKey().startsWith("cloudbase"))
				sortedProps.put(entry.getKey(), entry.getValue());
		}

		iter = sortedProps.entrySet().iterator();
		while(iter.hasNext()){
			Entry<String, String> entry = iter.next();
			log.info(entry.getKey()+" = "+entry.getValue());
		}


	}

}
