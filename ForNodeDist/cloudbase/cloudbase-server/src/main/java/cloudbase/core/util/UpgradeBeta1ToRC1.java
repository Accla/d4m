package cloudbase.core.util;

import org.apache.hadoop.conf.Configuration;
import org.apache.log4j.Logger;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;

import cloudbase.core.CBConstants;
import cloudbase.core.Cloudbase;
import cloudbase.core.conf.CBConfiguration;
import cloudbase.core.zookeeper.ZooSession;

public class UpgradeBeta1ToRC1 {

	private static final String METADATA_CONSTRAINT_KEY = "cloudbase.table.constraint.1";
	private static final String OLD_METADATA_CONSTRAINT_VALUE = "cloudbase.core.MetadataConstraints";
	private static final String NEW_METADATA_CONSTRAINT_VALUE = "cloudbase.core.constraints.MetadataConstraints";
	
	private static Logger log = Logger.getLogger(UpgradeBeta1ToRC1.class.getName());
	
	/**
	 * @param args
	 * @throws InterruptedException 
	 * @throws KeeperException 
	 */
	public static void main(String[] args) throws Exception {
		
		Configuration conf = new Configuration();
		
		log.info("Hadoop Filesystem is " + conf.get("fs.default.name"));
		log.info("Cloudbase data dir is " + CBConstants.BASE_DIR);
		log.info("Zookeeper server is " + CBConfiguration.getInstance().get("cloudbase.zookeeper.host"));
		
		CBConfiguration cbConf = CBConfiguration.getInstance(CBConstants.METADATA_TABLE_NAME);
		
		if(cbConf.get(METADATA_CONSTRAINT_KEY).equals(OLD_METADATA_CONSTRAINT_VALUE)){
			TablePropUtil.setTableProperty(CBConstants.METADATA_TABLE_NAME, METADATA_CONSTRAINT_KEY, NEW_METADATA_CONSTRAINT_VALUE);
			log.info("Set per table property on !METADATA "+METADATA_CONSTRAINT_KEY+"="+NEW_METADATA_CONSTRAINT_VALUE);
		}else{
			log.fatal("Not upgrading current install does not appear to be BETA1");
			System.exit(-1);
		}
		
		String masterLocPath = CBConstants.ZROOT_PATH+"/"+Cloudbase.getInstanceID()+"/master_loc";
		
		if(ZooSession.getSession().exists(masterLocPath, false) != null){
			ZooSession.getSession().delete(masterLocPath, -1);
			log.info("Removed "+masterLocPath+" from zookeeper");
			
		}
		
		String masterLockPath = CBConstants.ZROOT_PATH+"/"+Cloudbase.getInstanceID()+CBConstants.ZMASTER_LOCK_NODE;
		ZooSession.getSession().create(masterLockPath, new byte[]{}, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
		log.info("Created "+masterLockPath+" in zookeeper");
		
		//delete beta1 user info, and re-init security
		log.info("Initializing security settings in zookeeper");
		Initialize.resetSecurity();
	}

}
