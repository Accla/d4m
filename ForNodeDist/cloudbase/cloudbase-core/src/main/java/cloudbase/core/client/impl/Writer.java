package cloudbase.core.client.impl;

import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.log4j.Logger;

import cloudbase.core.CBConstants;
import cloudbase.core.client.CBException;
import cloudbase.core.client.CBSecurityException;
import cloudbase.core.client.Instance;
import cloudbase.core.client.TableNotFoundException;
import cloudbase.core.client.impl.TabletLocator.TabletLocation;
import cloudbase.core.conf.CBConfiguration;
import cloudbase.core.data.Mutation;
import cloudbase.core.security.thrift.AuthInfo;
import cloudbase.core.tabletserver.thrift.ConstraintViolationException;
import cloudbase.core.tabletserver.thrift.NotServingTabletException;
import cloudbase.core.util.UtilWaitThread;

import com.facebook.thrift.TException;

public class Writer
{
    
    private static Logger log = Logger.getLogger(Writer.class.getName());
    
    private Instance instance;
    private AuthInfo credentials;
    private Text table;
    private int retries = 0;
    
    public Writer(Instance instance, AuthInfo credentials, Text table)
    {
        this.instance = instance;
        this.credentials = credentials;
        this.table = table;

        retries = Math.max(CBConfiguration.getInstance().getInt("cloudbase.client.retries", CBConstants.MAX_CLIENT_RETRIES), CBConstants.MIN_CLIENT_RETRIES);
    }
    
    public Writer(Instance instance, AuthInfo credentials, String table)
    {
    	this(instance, credentials, new Text(table));
    }

    public boolean update(Mutation m)
    throws CBException, CBSecurityException, ConstraintViolationException, TableNotFoundException
    {
        TabletLocation tabLoc;
        tabLoc = TabletLocator.getInstance(instance, credentials, table).locateTablet(new Text(m.getRow()), false);

        if(tabLoc == null)
        {
            log.error("got null loc from metadata table");
            return false;
        }
        
        for(int numTries=0; numTries < retries; numTries++)
        {
            // get results
            try {
                
                boolean servingTablet = true;
                
                try {
                    TabletClient.updateServer(m, tabLoc.tablet_extent, tabLoc.tablet_location, credentials);
                } catch (TException e) {
                    throw new IOException(e);
                } catch (NotServingTabletException e) {
                    servingTablet = false;
                }
                while(!servingTablet) {
                    //System.err.println("got MSG_TS_NOT_SERVING_TABLET");
                    TabletLocator.getInstance(instance, credentials, table).invalidateCache(tabLoc.tablet_extent);
                    try {
                        tabLoc = TabletLocator.getInstance(instance, credentials, table).locateTablet(new Text(m.getRow()), false);
                    } catch (TableNotFoundException e1) {
                        throw new IOException(e1);
                    }
                    if(tabLoc == null)
                        return false;
                    
                    servingTablet = true;
                    
                    try {
                        TabletClient.updateServer(m, tabLoc.tablet_extent, tabLoc.tablet_location, credentials);
                    } catch (TException e) {
                        throw new IOException(e);
                    } catch (NotServingTabletException e) {
                        servingTablet = false;
                        UtilWaitThread.sleep(1000);
                    }
                }
                
                return true;
            } catch(IOException e) {
                log.warn("IOException", e);
                TabletLocator.getInstance(instance, credentials, table).invalidateCache(tabLoc.tablet_extent);
                UtilWaitThread.sleep(1000);
            }
        }
        throw new CBException("update: max retries reached");
    }
}
