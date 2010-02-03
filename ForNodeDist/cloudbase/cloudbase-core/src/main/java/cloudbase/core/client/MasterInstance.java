package cloudbase.core.client;

import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

import cloudbase.core.client.impl.MasterClient;
import cloudbase.core.master.thrift.MasterClientService.Client;


/**
 * An implementation of instance that is told
 * about the master location and contacts the 
 * master to find other information.
 * 
 */

public class MasterInstance implements Instance {

    private List<String> loc;
    Logger log = Logger.getLogger(MasterInstance.class);
    
    /**
     * 
     * 
     * @param masterLocations A comma separated list of master locations.  Each location can contain an optional port, 
     * of the format host:port.
     */
    
    public MasterInstance(String masterLocations) {
        this.loc = Arrays.asList(masterLocations.split(","));
    }
    
    /**
     * A list of master locations.  Each location can contain an optional port, 
     * of the format host:port.
     * 
     * @param masterLocations A comma separated list of master locations.  Each location can contain an optional port, 
     * of the format host:port.
     */
    
    public MasterInstance(List<String> masterLocations){
        this.loc = masterLocations;
    }
    
    @Override
    public List<String> getMasterLocations() {
        return loc;
    }

    @Override
    public String getRootTabletLocation() {
        
        Client master = null;
        
        try {
            master = MasterClient.master_connect(this);
            return master.getRootTabletLocation();
        } catch (Exception e) {
           log.error(e);
        } finally {
        	MasterClient.close(master);
        }
        
        return null;
    }

    @Override
    public String getInstanceID() {
        Client master = null;
        
        try {
            master = MasterClient.master_connect(this);
            return master.getInstanceId();
        } catch (Exception e) {
           log.error(e);
        } finally {
        	MasterClient.close(master);
        }
        
        return null;
    }
    
}
