package cloudbase.core.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;
import java.util.Map.Entry;

import jline.ConsoleReader;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.log4j.Logger;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;

import cloudbase.core.CBConstants;
import cloudbase.core.Cloudbase;
import cloudbase.core.client.CBSecurityException;
import cloudbase.core.conf.CBConfiguration;
import cloudbase.core.data.Key;
import cloudbase.core.data.KeyExtent;
import cloudbase.core.data.MyMapFile;
import cloudbase.core.data.Value;
import cloudbase.core.security.Authenticator;
import cloudbase.core.security.ZKAuthenticator;
import cloudbase.core.security.thrift.AuthInfo;
import cloudbase.core.zookeeper.ZooConstants;
import cloudbase.core.zookeeper.ZooSession;
import cloudbase.core.zookeeper.ZooUtil;


/**
 * This class is used to setup the directory structure 
 * and the root tablet to get an instance started
 * 
 *
 */
public class Initialize {
	private static Logger log = Logger.getLogger(Initialize.class.getName());
	
	public static void doInit() throws Exception {
		Configuration conf = new Configuration();
		log.info("Hadoop Filesystem is " + conf.get("fs.default.name"));
		log.info("Cloudbase data dir is " + CBConstants.BASE_DIR);
		log.info("Zookeeper server is " + CBConfiguration.getInstance().get("cloudbase.zookeeper.host"));

		FileSystem fs = FileSystem.get(conf); 

		if(fs.exists(CBConstants.INSTANCE_ID_LOCATION) || fs.exists(CBConstants.DATA_VERSION_LOCATION)){
			log.error("It appears this location was previously initialized, exiting ... ");
			return;
		}
		
		FileStatus fstat;

		// the actual disk location of the root tablet
		final Path rootTablet = new Path(CBConstants.ROOT_TABLET_DIR);

		final Path defaultMetadataTablet = new Path(CBConstants.METADATA_TABLE_DIR + CBConstants.DEFAULT_TABLET_LOCATION);

		final Path metadataTableDir = new Path(CBConstants.METADATA_TABLE_DIR);

		fs.mkdirs(new Path(CBConstants.DATA_VERSION_LOCATION, ""+CBConstants.DATA_VERSION));
		
		//create an instance id
		fs.mkdirs(CBConstants.INSTANCE_ID_LOCATION);
		fs.createNewFile(new Path(CBConstants.INSTANCE_ID_LOCATION, UUID.randomUUID().toString()));

		//initialize initial metadata config in zookeeper
		initMetadataConfig();

		// create !METADATA table 
		try {
			fstat = fs.getFileStatus(metadataTableDir);
			if(!fstat.isDir()) {
				log.error("location " + metadataTableDir.toString() + " exists but is not a directory");
				return;
			}
		}
		catch (FileNotFoundException fnfe) {
			// create btl dir
			if(!fs.mkdirs(metadataTableDir)) {
				log.error("unable to create directory " + metadataTableDir.toString());
				return;
			}
		}

		// create root tablet
		try {
			fstat = fs.getFileStatus(rootTablet);
			if(!fstat.isDir()) {
				log.error("location " + rootTablet.toString() + " exists but is not a directory");
				return;
			}
		}
		catch (FileNotFoundException fnfe) {
			// create btl dir
			if(!fs.mkdirs(rootTablet)) {
				log.error("unable to create directory " + rootTablet.toString());
				return;
			}
			
			// populate the root tablet with info about the default tablet
			// the root tablet contains the key extent and locations of all the metadata tablets
			MyMapFile.Writer mfw = new MyMapFile.Writer(conf, fs, CBConstants.METADATA_TABLE_DIR + "/root_tablet/map_00000_00000", Key.class, Value.class);
			
			// -----------] root tablet info
			Text rootExtent = CBConstants.ROOT_TABLET_EXTENT.getMetadataEntry();
			
			// root's directory
			Key rootDirKey = new Key(rootExtent, CBConstants.METADATA_TABLE_TABLET_COLUMN_FAMILY, CBConstants.METADATA_TABLE_TABLET_DIRECTORY_COLUMN_NAME, 0);
			mfw.append(rootDirKey, new Value("/root_tablet".getBytes()));
			
			// root's prev row
			Key rootPrevRowKey = new Key(rootExtent, CBConstants.METADATA_TABLE_TABLET_COLUMN_FAMILY, CBConstants.METADATA_TABLE_TABLET_PREV_ROW_COLUMN_NAME, 0);
			byte[] none = { 0 };
			Value rootPrevRow = new Value(none);
			mfw.append(rootPrevRowKey, rootPrevRow);
			
			// root's sstables
			//Key rootSSTablesKey = new Key(rootExtent, new Text(CBConstants.METADATA_TABLE_TABLET_SSTABLES_COLUMN_FAMILY.toString() + ":" + "/root_tablet/map_00000_00000"));
			//mfw.append(rootSSTablesKey, new DeletableImmutableBytesWritable("/root_tablet/map_00000_00000".getBytes(), false));
			
			// ----------] default tablet info			
			Text defaultExtent = new Text(KeyExtent.getMetadataEntry(new Text(CBConstants.METADATA_TABLE_NAME), null));
			
			// default's directory
			Key defaultDirKey = new Key(defaultExtent, CBConstants.METADATA_TABLE_TABLET_COLUMN_FAMILY, CBConstants.METADATA_TABLE_TABLET_DIRECTORY_COLUMN_NAME, 0);
			mfw.append(defaultDirKey, new Value(CBConstants.DEFAULT_TABLET_LOCATION.getBytes()));
			
			// default's prevrow
			Key defaultPrevRowKey = new Key(defaultExtent, CBConstants.METADATA_TABLE_TABLET_COLUMN_FAMILY, CBConstants.METADATA_TABLE_TABLET_PREV_ROW_COLUMN_NAME, 0);
			Text prev_key_string = KeyExtent.getMetadataEntry(new Text(CBConstants.METADATA_TABLE_NAME),null);

			// special value for previous keys, has a bit that says whether the prev row exists or not
			// 1 is true
			byte[] prev_key_bytes = new byte[prev_key_string.getBytes().length + 1];
			prev_key_bytes[0] = 1;
			for(int i = 0; i < prev_key_string.getBytes().length; i++)
			{
				prev_key_bytes[i+1] = prev_key_string.getBytes()[i];
			}

			mfw.append(defaultPrevRowKey, new Value(prev_key_bytes));

			mfw.close();
		}


		// create default tablet directory
		try {
			fstat = fs.getFileStatus(defaultMetadataTablet);
			if(!fstat.isDir()) {
				log.error("location " + defaultMetadataTablet.toString() + " exists but is not a directory");
				return;
			}
		}
		catch (FileNotFoundException fnfe) {
			// create btl dir
			if(!fs.mkdirs(defaultMetadataTablet)) {
				log.error("unable to create directory " + defaultMetadataTablet.toString());
				return;
			}
		}
	
		
		/*
		// create default metadata tablet mapfile
		try {
			fstat = fs.getFileStatus(defaultMetadataTabletMapFile);
			if(!fstat.isDir()) {
				log.error("location " + defaultMetadataTabletMyMapFile.toString() + " exists but is not a directory");
				return;
			}
		}
		catch (FileNotFoundException fnfe) {
			// create btl dir
			if(!fs.mkdirs(defaultMetadataTabletMapFile)) {
				log.error("unable to create directory " + defaultMetadataTabletMyMapFile.toString());
				return;
			}
		}
		*/

		// create tserver dir
		String zPath = CBConstants.ZROOT_PATH+"/"+Cloudbase.getInstanceID()+CBConstants.ZTSERVERS_PATH;
		ZooSession.getSession().create(zPath, new byte[]{}, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
		
		//create master lock node
		String masterLockPath = CBConstants.ZROOT_PATH+"/"+Cloudbase.getInstanceID()+CBConstants.ZMASTER_LOCK_NODE;
		ZooSession.getSession().create(masterLockPath, new byte[]{}, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
			
		initSecurity();
	}
	
	private static void initSecurity() throws IOException {
		try {
			initUsers(new ZKAuthenticator());
		} catch (Exception e) {
			log.error("error talking to zookeeper",e);
			throw new IOException(e);		
		}
	}

	private static void initUsers(Authenticator authenticator)
	throws KeeperException, InterruptedException, CBSecurityException, IOException
	{
		String rootUser = "root";
		ConsoleReader reader = new ConsoleReader();
    	byte[] pass = reader.readLine("Enter initial password for "+rootUser+": ", '*').getBytes();
		authenticator.initializeRootUser(new AuthInfo(ZooConstants.SYSTEM_USERNAME, ZooConstants.SYSTEM_PASSWORD), rootUser, pass);
	}

	private static void initMetadataConfig() throws IOException {
		InputStream in;
		Configuration conf;
		try{
			//read in default metadata table config
			in = Initialize.class.getClassLoader().getResourceAsStream("initial-metadata-conf.xml"); 
			
			conf = new Configuration(false);
			conf.addResource(in);
		}
		catch (Exception e) {
			log.error("can't find initial-metadata-conf.xml on classpath");
			throw new IOException(e);
		}
		
		try {
			//setup data in zookeeper
			CBConfiguration.initializePerTableProperties();
			
			for (Entry<String, String> entry : conf) {
				TablePropUtil.setTableProperty(CBConstants.METADATA_TABLE_NAME, entry.getKey(), entry.getValue());
			}
				
			in.close();
		}catch(Exception e){
			log.error("error talking to zookeeper",e);
			throw new IOException(e);
		}
		
	}

	/**
	 * @param args
	 * @throws IOException 
	 * @throws CBSecurityException 
	 */
	public static void main(String[] args) throws Exception {
		if (args.length == 1 && args[0].equals("--reset-security"))
			resetSecurity();
		else
		doInit();
	}

	/**
	 * Wipe existing security in zookeeper (for 1.0) and re-initialize
	 */
	protected static void resetSecurity()
	{
		try {
			ZooUtil.recursiveDelete(ZooConstants.ZKUserPath);
		} catch (Exception e) {
			log.warn("Could not delete zookeeper entry for users", e);
		}
		log.info("Removed "+ZooConstants.ZKUserPath+" from zookeeper");
		
		//change this when new security model is done
		log.info("Initializing security settings in zookeeper");
		try {
			Initialize.initSecurity();
		} catch (IOException e) {
			log.error("Could not initialize zookeeper entry for users", e);
		}
	}
}
