package cloudbase.core.zookeeper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

public class ZooShell {
    public static void main(String[] args) throws Exception {
        ZooKeeper zooKeeper = new ZooKeeper("localhost:2181", 5000, new Watcher(){

            @Override
            public void process(WatchedEvent event) {
                System.out.println("event "+event.getPath()+" "+event.getType()+" "+event.getState());
            }
            
        });
        
        
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        
        String line;
        
        Watcher w = new Watcher(){

            @Override
            public void process(WatchedEvent event) {
                System.out.println("gc watcher event "+event.getPath()+" "+event.getType()+" "+event.getState());
            }};
        
        ZooCache zc = new ZooCache();
            
        while((line = in.readLine()) != null){
            
            String ca[] = line.split("\\s+");
            
            if(ca[0].equals("gc")){
                List<String> children = zooKeeper.getChildren(ca[1], w);
                
                System.out.println("children : ");
                for (String child : children) {
                    System.out.println("\t"+child);
                }
                System.out.println();
            }else if(ca[0].equals("e")){
                Stat exists = zooKeeper.exists(ca[1], w);
                System.out.println(exists != null);
            }else if(ca[0].equals("get")){
                byte[] val = zc.get(ca[1]);
                System.out.println(ca[1]+" =  "+(val == null ? null : new String(val)));
            }else if(ca[0].equals("set")){
                ZooSession.getSession().setData(ca[1], ca[2].getBytes(), -1);
            }else if(ca[0].equals("create")){
                ZooSession.getSession().create(ca[1], ca[2].getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            }
        }
    }
}
