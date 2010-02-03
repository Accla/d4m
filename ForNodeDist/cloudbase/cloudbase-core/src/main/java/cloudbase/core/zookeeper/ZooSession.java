package cloudbase.core.zookeeper;

import java.io.IOException;
import java.util.HashSet;

import org.apache.log4j.Logger;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooKeeper.States;

import cloudbase.core.conf.CBConfiguration;
import cloudbase.core.util.UtilWaitThread;

public class ZooSession {
    
    private static Logger log = Logger.getLogger(ZooSession.class.getName());
    
    private static ZooKeeper zooKeeper = null;
    private static CBWatcher watcher = null;
    
    private static class CBWatcher implements Watcher {
        
        private HashSet<Watcher> watchers = new HashSet<Watcher>();
        
        public void process(WatchedEvent event) {
            
            for (Watcher watcher : watchers) {
                watcher.process(event);
            }
            
            if(event.getState() == KeeperState.Expired){
            	log.debug("Session expired, state of current session : "+zooKeeper.getState());
            }
        }

        public void add(Watcher w) {
            watchers.add(w);
        }
    }
    
    public static ZooKeeper connect(String host, int timeout, Watcher watcher){
        boolean tryAgain = true;
        int sleepTime = 100;
        ZooKeeper zooKeeper = null;
        
        while(tryAgain){
            try {
                zooKeeper = new ZooKeeper(host, timeout, watcher);
                tryAgain = false;
            } catch (IOException e) {
                log.warn("Connection to zooKeeper failed, will try again in "+String.format("%.2f secs", sleepTime/1000.0),e);
            }
            
            if(tryAgain){
                UtilWaitThread.sleep(sleepTime);
                if(sleepTime < 10000)
                    sleepTime = (int)(sleepTime + sleepTime * Math.random());
            }
        }
        
        return zooKeeper;
    }
    
    public static synchronized ZooKeeper getSession(){
    	
    	if(zooKeeper != null && zooKeeper.getState() == States.CLOSED){
    		zooKeeper = null;
    	}
    	
        if(zooKeeper == null){
            watcher = new CBWatcher();
            zooKeeper = connect(CBConfiguration.getInstance().get("cloudbase.zookeeper.host", "localhost"), CBConfiguration.getInstance().getInt("cloudbase.zookeeper.sessionTimeout", 30000), watcher);
        }
        
        return zooKeeper;
    }
    
    public static synchronized ZooKeeper getSession(Watcher w){
    	
    	if(zooKeeper != null && zooKeeper.getState() == States.CLOSED){
    		zooKeeper = null;
    	}
    	
        if(zooKeeper == null){
            watcher = new CBWatcher();
            zooKeeper = connect(CBConfiguration.getInstance().get("cloudbase.zookeeper.host","localhost"), CBConfiguration.getInstance().getInt("cloudbase.zookeeper.sessionTimeout", 30000), watcher);
        }
        
        watcher.add(w);
        
        return zooKeeper;
    }   
}
