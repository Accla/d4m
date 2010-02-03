package cloudbase.core.zookeeper;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.KeeperException.Code;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.data.Stat;

/**
 * Caches values stored in zookeeper and keeps them up to date
 * as they change in zookeeper.
 * 
 *
 */
public class ZooCache {
	
	private static Logger log = Logger.getLogger(ZooCache.class.getName());
	
	//private ZooKeeper zooKeeper;
	
	private ZCacheWatcher watcher = new ZCacheWatcher();
	private Watcher externalWatcher = null;

	private HashMap<String, byte[]> cache;
	private HashMap<String, List<String>> childrenCache;
	private HashSet<String> pathsToCache;
	
	private class ZCacheWatcher implements Watcher {

		private byte[] data;

		@Override
		public void process(WatchedEvent event) {
		    
		    if(event.getType() != EventType.None && event.getPath() != null && !pathsToCache.contains(event.getPath())){
		        //notified about a path we are not caching in
		        return;
		    }
		        
		    
			try{

				switch (event.getType()) {
				case NodeDataChanged:
					//System.out.println("EventNodeDataChanged "+event.getPath());
					log.debug("EventNodeDataChanged "+event.getPath());
					data = ZooSession.getSession().getData(event.getPath(), watcher, null);
					put(event.getPath(), data);
					break;
				case NodeChildrenChanged:
					log.debug("EventNodeChildrenChanged "+event.getPath());
					try{
						List<String> children = ZooSession.getSession().getChildren(event.getPath(), watcher);
						childrenCache.put(event.getPath(), children);
					}catch(KeeperException ke){
						if(ke.code() == Code.NONODE){
							remove(event.getPath());
						}else{
							throw ke;
						}
					}
                    
					break;
				case NodeCreated:
					log.debug("EventNodeCreated "+event.getPath());
					data = ZooSession.getSession().getData(event.getPath(), watcher, null);
					put(event.getPath(), data);
					break;
				case NodeDeleted:
					log.debug("EventNodeDeleted "+event.getPath());
					remove(event.getPath());
					break;
				case None:
					switch(event.getState()){
					case Disconnected:
						break;
					case Expired:
						log.debug("Zoo keeper connection expired, clearing cache");
						clear();
						break;
					case SyncConnected:
						break;
					default:
						log.warn("EventNone event not handled path = "+event.getPath()+" state="+event.getState());
					}
					break;
				default:
					log.warn("Event not handled path = "+event.getPath()+" state="+event.getState()+" type = "+event.getType());

				}

				if(externalWatcher != null){
					externalWatcher.process(event);
				}
				
			} catch (KeeperException e) {
				log.warn(e.getMessage(), e);
				clear();
			} catch (InterruptedException e) {
				log.error(e.getMessage(), e);
			}finally{

			}
			
		}
	}
	
	public ZooCache(){
		this(null);
	}
	
	public ZooCache(Watcher watcher){
		cache = new HashMap<String, byte[]>();
		childrenCache = new HashMap<String, List<String>>();
		pathsToCache = new HashSet<String>();
		this.externalWatcher = watcher;
	}
	
	private static interface ZooRunnable {
	    void run(ZooKeeper zooKeeper) throws KeeperException, InterruptedException;
	}
	
	private synchronized void retry(ZooRunnable op){
	    boolean tryAgain = true;
        int sleepTime = 100;
      
        
        while(tryAgain){
        
            ZooKeeper zooKeeper = ZooSession.getSession();
            
            try {
                op.run(zooKeeper);
                tryAgain = false;
                
            } catch (KeeperException e) {
                if(e.code() == Code.NONODE){
                    log.error("Looked up non existant node in cache "+e.getPath(), e);
                }
                log.debug("Zookeeper error, will retry",e);
            } catch (InterruptedException e) {
                log.debug("Zookeeper error, will retry",e);
            }
            
            if(tryAgain){
                try {
                    //do not hold lock while sleeping
                    wait(sleepTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(sleepTime < 10000)
                    sleepTime = (int)(sleepTime + sleepTime * Math.random());
            }
        }
	}
	
	public synchronized List<String> getChildren(final String zPath){
	    if(!childrenCache.containsKey(zPath)){
	        pathsToCache.add(zPath);
	        
	        ZooRunnable zr = new ZooRunnable(){

                @Override
                public void run(ZooKeeper zooKeeper) throws KeeperException, InterruptedException {
                	try{
                		List<String> children = zooKeeper.getChildren(zPath, watcher);
                		childrenCache.put(zPath, children);
                	}catch(KeeperException ke){
						if(ke.code() != Code.NONODE){
							throw ke;
						}
					}
                }
                
            };
	        
            retry(zr);
	    }else{
	        //log.debug("Found "+zPath+" in children cache");
	    }
	    
	    return childrenCache.get(zPath);
	}
	
	public synchronized byte[] get(final String zPath) {
		//must call contains, since the value could be null
		if(!cache.containsKey(zPath)){
		    
		    pathsToCache.add(zPath);
		    
		    ZooRunnable zr = new ZooRunnable(){

                @Override
                public void run(ZooKeeper zooKeeper) throws KeeperException, InterruptedException {
                    Stat stat = zooKeeper.exists(zPath, watcher);
                    
                    byte[] data = null;            
                    
                    if(stat == null){
                        data = null;
                        //log.debug("zookeeper did not contain "+zPath);
                    }else{
                        data = zooKeeper.getData(zPath, watcher, stat);
                        //log.debug("zookeeper contained "+zPath+" "+(data == null ? null : new String(data)));
                    }
                    //log.debug("putting "+zPath+" "+(data == null ? null : new String(data))+" in cache");
                    cache.put(zPath, data);
                }
		        
		    };
		    
		    retry(zr);
		}else{
            //log.debug("Found "+zPath+" in data cache");
        }
		
		return cache.get(zPath);
	}
	
	private synchronized void put(String zPath, byte[] data){
		cache.put(zPath, data);
	}
	
	private synchronized void remove(String zPath){
		//log.debug("removing "+zPath+" from cache");
		cache.remove(zPath);
		childrenCache.remove(zPath);
		pathsToCache.remove(zPath);
	}
	
	private synchronized void clear() {
		cache.clear();
		childrenCache.clear();
		pathsToCache.clear();
	}
}
