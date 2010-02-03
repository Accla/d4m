package cloudbase.core.client;

import java.util.List;


/**
 * This class represents the information a client
 * needs to know to connect to an instance of
 * cloudbase. 
 * 
 */
public interface Instance {
    /**
     * Get the location of the tablet server that is serving the root tablet.
     * 
     * @return location in "hostname:port" form. 
     */
    public abstract String getRootTabletLocation();
    
    /**
     * Get the location(s) of the cloudbase master and any redundant servers.
     * 
     * @return a list of locations in "hostname:port" form.
     */
    public abstract List<String> getMasterLocations();
    
    /**
     * Get a unique string that identifies this instance of cloudbase.
     * @return a UUID
     */
    public abstract String getInstanceID();
}
