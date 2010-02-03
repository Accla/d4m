package cloudbase.core.util;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;

import cloudbase.core.CBConstants;
import cloudbase.core.Cloudbase;
import cloudbase.core.zookeeper.ZooSession;

public class TablePropUtil {
	public static void setTableProperty(String tablename, String property, String value) throws KeeperException, InterruptedException {
		ZooKeeper zk = ZooSession.getSession();
		if(zk.exists(CBConstants.ZROOT_PATH+"/"+Cloudbase.getInstanceID()+CBConstants.ZTABLE_CONF_PATH+"/"+tablename, false) == null){
			zk.create(CBConstants.ZROOT_PATH+"/"+Cloudbase.getInstanceID()+CBConstants.ZTABLE_CONF_PATH+"/"+tablename, new byte[]{}, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
		}

		String zPath = CBConstants.ZROOT_PATH+"/"+Cloudbase.getInstanceID()+CBConstants.ZTABLE_CONF_PATH+"/"+tablename+"/"+property;

		if(zk.exists(zPath, false) == null){
			zk.create(zPath, value.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
		}else{
			zk.setData(zPath, value.getBytes(), -1);
		}

	}
	
	public static void removeTableProperty(String tablename, String property) throws InterruptedException, KeeperException {
		String zPath = CBConstants.ZROOT_PATH+"/"+Cloudbase.getInstanceID()+CBConstants.ZTABLE_CONF_PATH+"/"+tablename+"/"+property;
		ZooSession.getSession().delete(zPath, -1);
	}
}