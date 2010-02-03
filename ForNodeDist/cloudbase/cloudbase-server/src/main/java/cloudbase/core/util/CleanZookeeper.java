package cloudbase.core.util;

import java.io.IOException;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import cloudbase.core.CBConstants;
import cloudbase.core.Cloudbase;

public class CleanZookeeper {

    static void recursivelyDelete(ZooKeeper zk, String path) throws KeeperException, InterruptedException {
        for (String child : zk.getChildren(path, false) ) {
            recursivelyDelete(zk, path + "/" + child);
        }
        zk.delete(path, -1);
    }
    
    /**
     * @param args should contain one element: the address of a zookeeper node
     * @throws IOException error connecting to cloudbase or zookeeper
     */
    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            System.err.println("Usage: " + CleanZookeeper.class.getName() + " hostname[:port]");
            System.exit(1);
        }
        String root = CBConstants.ZROOT_PATH;
        ZooKeeper zk = new ZooKeeper(args[0], 10000, new Watcher() {
            public void process(WatchedEvent event) {}
        });
        try {
            for (String child : zk.getChildren(root, false) ) {
                if (!child.equals(Cloudbase.getInstanceID())) {
                    recursivelyDelete(zk, root + "/" + child);
                }
            }
        } catch (Exception ex) {
            System.out.println("Error Occurred: " + ex);
        }
    }

}
