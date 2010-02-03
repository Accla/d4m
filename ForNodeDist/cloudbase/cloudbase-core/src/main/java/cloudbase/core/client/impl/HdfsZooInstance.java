package cloudbase.core.client.impl;

import java.util.Collections;
import java.util.List;

import cloudbase.core.CBConstants;
import cloudbase.core.Cloudbase;
import cloudbase.core.client.Instance;
import cloudbase.core.zookeeper.ZooCache;
import cloudbase.core.zookeeper.ZooLock;

/**
 * An implementation of Instance that looks in HDFS and ZooKeeper
 * to find the master and root tablet location.
 *
 */
public class HdfsZooInstance implements Instance {
    
    private static ZooCache zooCache = new ZooCache();
    
    @Override
    public String getRootTabletLocation() {
        String zRootLocPath = CBConstants.ZROOT_PATH+"/"+Cloudbase.getInstanceID()+CBConstants.ZROOT_TABLET_LOCATION;
        
        byte[] loc = zooCache.get(zRootLocPath);
        
        if(loc == null){
            return null;
        }
        
        return new String(loc); 
    }
    
    @Override
    public List<String> getMasterLocations() {
        
        String masterLocPath = CBConstants.ZROOT_PATH+"/"+Cloudbase.getInstanceID()+CBConstants.ZMASTER_LOCK_NODE;
        
        byte[] loc = ZooLock.getLockData(masterLocPath);
        
        if(loc == null){
            throw new RuntimeException("No master entry in zookeeper");
        }
        
        return Collections.singletonList(new String(loc));
    }

    @Override
    public String getInstanceID() {
        return Cloudbase.getInstanceID();
    }
}
