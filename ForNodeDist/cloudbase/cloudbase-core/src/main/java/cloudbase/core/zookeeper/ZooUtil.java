package cloudbase.core.zookeeper;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;

public class ZooUtil {
	public static void recursiveDelete(String zPath) throws KeeperException, InterruptedException, IOException {
		ZooKeeper zk = ZooSession.getSession();
		if (zk.exists(zPath, false) != null) {
			//System.out.println("Deleting "+zPath);
			List<String> children = zk.getChildren(zPath, false);
			Iterator<String> iter = children.iterator();
			while (iter.hasNext())
				recursiveDelete(zPath+"/"+iter.next());
			zk.delete(zPath, -1);
		}		
	}

}
