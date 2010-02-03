package cloudbase.core.zookeeper;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.data.Stat;

public class ZooLock implements Watcher {
    
    private static Logger log = Logger.getLogger(ZooLock.class.getName());
    
    public static final String LOCK_PREFIX = "zlock-"; 
    
    public enum LockLossReason {
    	LOCK_DELETED,
    	SESSION_EXPIRED
    }
    
    public interface LockWatcher {
        void lostLock(LockLossReason reason);
    }
    
    public interface AsyncLockWatcher extends LockWatcher {
        void acquiredLock();
        void failedToAcquireLock(Exception e);
    }
    
    private String path;
    private ZooKeeper zooKeeper;
    private String lock;
    private LockWatcher lockWatcher;

    private String asyncLock;
    
    public ZooLock(String path) throws IOException{
        this.path = path;
        zooKeeper = ZooSession.getSession(this);
    }
    
    private static class TryLockAsyncLockWatcher implements AsyncLockWatcher {
        
        boolean acquiredLock = false;
        boolean failedToAcquireLock = false;
        LockWatcher lw;

        public TryLockAsyncLockWatcher(LockWatcher lw2) {
            this.lw = lw2;
        }

        @Override
        public void acquiredLock() {
            acquiredLock = true;
        }

        @Override
        public void failedToAcquireLock(Exception e) {
            failedToAcquireLock = true;
        }

        @Override
        public void lostLock(LockLossReason reason) {
            lw.lostLock(reason);
        }
        
        
    }
    
    public synchronized boolean tryLock(LockWatcher lw, byte data[]) throws KeeperException, InterruptedException{
        
        TryLockAsyncLockWatcher tlalw = new TryLockAsyncLockWatcher(lw);
        
        lockAsync(tlalw, data);
        
        if(tlalw.acquiredLock){
            return true;
        }
        
        if(asyncLock != null){
            zooKeeper.delete(path+"/"+asyncLock, -1);
            asyncLock = null;
        }
        
        return false;
    }
    
    private synchronized void lockAsync(final String myLock, final AsyncLockWatcher lw) throws KeeperException, InterruptedException{
        
        if(asyncLock == null){
            throw new IllegalStateException();
        }
        
        List<String> children = zooKeeper.getChildren(path, false);

        Collections.sort(children);

        if(children.get(0).equals(myLock)){
            this.lockWatcher = lw;
            this.lock = myLock;
            asyncLock = null;
            lw.acquiredLock();
            return;
        }else{
            String prev = null;
            for (String child : children) {
                if(child.equals(myLock)){
                    break;
                }

                prev = child;
            }
            
            //System.out.println("prev "+prev);
            
            final String lockToWatch = path+"/"+prev;
            
            Stat stat = zooKeeper.exists(path+"/"+prev, new Watcher(){

                @Override
                public void process(WatchedEvent event) {
                    
                    //System.out.println("asl event "+event.getPath()+" "+event.getType()+" "+event.getState());
                    
                    if(event.getType() == EventType.NodeDeleted && event.getPath().equals(lockToWatch)){
                        synchronized(ZooLock.this){
                            try {
                                if(asyncLock != null){
                                    lockAsync(myLock, lw);
                                }else{
                                    //System.out.println("While waiting for another lock "+lockToWatch+" "+myLock+" was deleted");
                                }
                            } catch (Exception e) {
                                if(lock == null){
                                    //have not acquired lock yet
                                    lw.failedToAcquireLock(e);
                                }
                            }
                        }
                    }
                    
                    if(event.getState() == KeeperState.Expired){
                        synchronized(ZooLock.this){
                            if(lock == null){
                                lw.failedToAcquireLock(new Exception("Zookeeper Session expired"));
                            }
                        }
                    }
                }
                
            });
            
            //System.out.println("stat "+prev);
            
            if(stat == null){
                lockAsync(myLock, lw);
            }
            
           
            
        }
    }
    
    public synchronized void lockAsync(final AsyncLockWatcher lw, byte data[]){
        
        if(lockWatcher != null || lock != null || asyncLock != null){
            throw new IllegalStateException();
        }
        
        try {
            asyncLock = zooKeeper.create(path+"/"+LOCK_PREFIX, data, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
            
            Stat stat = zooKeeper.exists(asyncLock, new Watcher(){
                public void process(WatchedEvent event) {
                    synchronized(ZooLock.this){
                        if(lock != null && event.getType() == EventType.NodeDeleted && event.getPath().equals(path+"/"+lock)){
                            //System.out.println("Some bastard deleted my lock");
                            LockWatcher localLw = lockWatcher;
                            lock = null;
                            lockWatcher = null;
                            
                            localLw.lostLock(LockLossReason.LOCK_DELETED);
                            
                        }else if(asyncLock != null && event.getType() == EventType.NodeDeleted && event.getPath().equals(path+"/"+asyncLock)){
                            lw.failedToAcquireLock(new Exception("Lock deleted before acquired"));
                            asyncLock = null;
                        }
                    }
                }}
            );
            
            if(stat == null){
                lw.failedToAcquireLock(new Exception("Lock does not exist after create"));
                return;
            }
            
            asyncLock = asyncLock.substring(path.length()+1);
            
            lockAsync(asyncLock, lw);
            
        } catch (KeeperException e) {
            lw.failedToAcquireLock(e);
        } catch (InterruptedException e) {
            lw.failedToAcquireLock(e);
        }
    }

    public synchronized boolean tryToCancelAsyncLockOrUnlock() throws InterruptedException, KeeperException{
    	boolean del = false;
    	
    	if(asyncLock != null){
    		zooKeeper.delete(path+"/"+asyncLock, -1);
    		del = true;
    	}
    	
    	if(lock != null){
    		unlock();
    		del = true;
    	}
    	
    	return del;
    }
    
    public synchronized void unlock() throws InterruptedException, KeeperException{
        if(lock == null){
            throw new IllegalStateException();
        }
   
        LockWatcher localLw = lockWatcher;
        String localLock = lock;
        
        lock = null;
        lockWatcher = null;
        
        zooKeeper.delete(path+"/"+localLock, -1);
        
        localLw.lostLock(LockLossReason.LOCK_DELETED);
    }
    
    public synchronized String getLockPath() {
    	if(lock == null){
    		return null;
    	}
		return path+"/"+lock;
	}
    
    @Override
    public synchronized void process(WatchedEvent event) {
       log.debug("event "+event.getPath()+" "+event.getType()+" "+event.getState());
       
       if(event.getState() == KeeperState.Expired && lock != null){
           LockWatcher localLw = lockWatcher;
           lock = null;
           lockWatcher = null;
           localLw.lostLock(LockLossReason.SESSION_EXPIRED);
       }
    }
    
    private static ZooCache getLockDataZooCache = new ZooCache();
    
    public static byte[] getLockData(String path){
    	
    	List<String> children = getLockDataZooCache.getChildren(path);
    	
    	if(children.size() == 0){
    		return null;
    	}
    	
    	Collections.sort(children);
    	
    	String lockNode =  children.get(0);
    	
    	return getLockDataZooCache.get(path+"/"+lockNode);
    }
    
    public static void main(String[] args) throws Exception {
        String node  = "/test/lock1";
        ZooLock zl = new ZooLock(node);

       /* boolean locked = zl.tryLock(new LockWatcher(){

            @Override
            public void lostLock() {
                System.out.println("Lost my lock");
            }}, "foo".getBytes());
        
        System.out.println("locked "+locked);
        */
        
        zl.lockAsync(new AsyncLockWatcher(){

            @Override
            public void acquiredLock() {
                System.out.println("I got the lock");
            }

            @Override
            public void lostLock(LockLossReason reason) {
                System.out.println("OMG I lost my lock, reason = "+reason);

            }

            @Override
            public void failedToAcquireLock(Exception e) {
                System.out.println("Failed to acquire lock  ");
                e.printStackTrace();
            }

        },
        new byte[0]);
        
        
        while(true){
            Thread.sleep(1000);
        }
    }
}
