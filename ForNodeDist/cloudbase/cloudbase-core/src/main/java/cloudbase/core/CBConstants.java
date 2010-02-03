package cloudbase.core;

import java.util.Collections;
import java.util.Set;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;

import cloudbase.core.conf.CBConfiguration;
import cloudbase.core.data.KeyExtent;

public class CBConstants {
	public static final String VERSION = "1.0.0-RC2";
	public static final int DATA_VERSION = 1;

	// Table report status codes
	public static final int MSG_TS_REPORT_TABLET_LOADED = 15;
	public static final int MSG_TS_ASSIGNMENT_FAILURE = 16;
	public static final int MSG_TS_REPORT_METADATA_TABLET = 22;
	public static final int MSG_TS_REPORT_TABLET_UNLOAD_FAILURE_NOT_SERVING = 25;
	public static final int MSG_TS_REPORT_TABLET_UNLOAD_FAILURE_MAJC = 26;
	public static final int MSG_TS_REPORT_TABLET_UNLOAD_FAILURE_CLOSED = 28;
	public static final int MSG_TS_REPORT_TABLET_UNLOAD_ERROR = 27;
	
	// May be overridden in a configuration file.
	public static final int MASTER_CLIENT_PORT_DEFAULT = 9999;
	public static final int TABLET_MASTER_PORT_DEFAULT = 9998;
	public static final int TABLET_CLIENT_PORT_DEFAULT = 9997;
	public static final int MASTER_MONITOR_PORT_DEFAULT = 9996;


	public static final int SHUTDOWN_STAGE_1 = 0;
	public static final int SHUTDOWN_STAGE_2 = 1;
	public static final int SHUTDOWN_STAGE_3 = 2;
	public static final int SHUTDOWN_STAGE_4 = 3;


	public final static String ZROOT_PATH = "/cloudbase";
	public static final String ZCONF_PATH = "/conf";
	public static final String ZTABLE_CONF_PATH = ZCONF_PATH+"/tables";
	public static final String ZROOT_TABLET_LOCATION = "/root_tablet_loc";
    public static final String ZMASTER_LOCK_NODE = "/master_lock";
    public final static String ZTSERVERS_PATH = "/tservers";
    
	public final static String BASE_DIR = CBConfiguration.getInstance().get("cloudbase.directory","/cloudbase");
	public final static String TABLES_DIR = BASE_DIR + "/tables";

	public static final String METADATA_TABLE_NAME = "!METADATA";
	public static final String DEFAULT_TABLET_LOCATION = "/default_tablet";

	public static final Text METADATA_TABLE_TABLET_COLUMN_FAMILY = new Text("metadata");
	public static final Text METADATA_TABLE_TABLET_LOCATION_COLUMN_NAME = new Text("location");
	public static final Text METADATA_TABLE_TABLET_PREV_ROW_COLUMN_NAME = new Text("prevrow");
	public static final Text METADATA_TABLE_TABLET_OLD_PREV_ROW_COLUMN_NAME = new Text("oldprevrow");
	public static final Text METADATA_TABLE_TABLET_DIRECTORY_COLUMN_NAME = new Text("directory");
	public static final Text METADATA_TABLE_TABLET_SPLIT_RATIO_COLUMN_NAME = new Text("splitRatio");
	public static final Text METADATA_TABLE_TABLET_SSTABLES_COLUMN_FAMILY = new Text("sstables");


	public static final Path INSTANCE_ID_LOCATION = new Path(BASE_DIR + "/instance_id");
	public static final Path DATA_VERSION_LOCATION = new Path(BASE_DIR + "/version");

	public static final KeyExtent ROOT_TABLET_EXTENT =
		new KeyExtent(new Text(METADATA_TABLE_NAME), KeyExtent.getMetadataEntry(new Text(METADATA_TABLE_NAME), null), null);

	public final static String METADATA_TABLE_DIR = TABLES_DIR + "/" + METADATA_TABLE_NAME;
	public static final String ROOT_TABLET_DIR = METADATA_TABLE_DIR+"/root_tablet";

	public static final String UTF8_ENCODING = "UTF-8";

	// note: all times are in milliseconds

	public static final int SCAN_BATCH_SIZE = 1000; // this affects the table client caching of metadata

	public static final int STD_CLIENT_TIMEOUT = 0; 
	public static final Integer TABLET_REASSIGNMENT_TIMEOUT = 100; // cycles not seconds
	public static final long TABLETSERVER_CONTACT_TIMEOUT = 180000; 
	public static final long MAX_MUTATION_BATCH_SIZE = 8000000/2;
	public static final int MIN_CLIENT_RETRIES = 1;
	public static final int MAX_CLIENT_RETRIES = 5;
	public static final long MIN_MASTER_LOOP_TIME = 1000;
	public static final int MASTER_TABLETSERVER_CONNECTION_TIMEOUT = 3000; 
	public static final int MAX_CONSECUTIVE_PONG_FAILURES = 100;
	public static final long CLIENT_SLEEP_BEFORE_RECONNECT = 1000;
	public static final long TABLET_MAX_ATOMIC_OPERATION_TIME_MILLIS_DEFAULT = 100;
	public static final int TABLET_MIN_SCAN_DEFAULT = 1000;


	// Security configuration
	public static final String PW_HASH_ALGORITHM = "SHA-1";
	public static final int PW_HASH_LENGTH = 20;  // in bytes, e.g. 20 for 160-bit SHA-1

	//defaults to use when config file was not found
	public static final long DEFAULT_TABLET_SPLIT_THRESHOLD = 1073741824;
	
	// MapFile defaults
	public static final int DEFAULT_MAPFILE_COMPRESSION_BLOCK_SIZE = 100000;
	public static final int DEFAULT_MAPFILE_REPLICATION = 3;
	
	// Representation of an empty set of authorizations
	// (used throughout the code, because scans of metadata table and many tests do not set record-level visibility)
	public static final Set<Short> NO_AUTHS = Collections.emptySet();
}
