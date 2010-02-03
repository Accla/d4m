package cloudbase.core.constraints;

import java.io.IOException;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import cloudbase.core.conf.CBConfiguration;
import cloudbase.core.conf.CBConfigurationException;

public class ConstraintLoader {
	
	
	public static final String CONSTRAIN_PROPERTY = "cloudbase.table.constraint.";
	private static Logger log = Logger.getLogger(ConstraintLoader.class.getName());
	
    public static ConstraintChecker load(String table) throws IOException{
		try {
			
			CBConfiguration conf = CBConfiguration.getInstance(table.toString());
			
			ConstraintChecker cc = new ConstraintChecker();
			
			for (Entry<String, String> entry : conf) {
				if(entry.getKey().startsWith(CONSTRAIN_PROPERTY)){
					String className = entry.getValue();
                    Class<? extends Constraint> clazz = (Class<? extends Constraint>) Class.forName(className);
                    log.debug("Loaded constraint "+clazz.getName()+" for "+table);
                    cc.addConstraint(clazz.newInstance());
				}
			}
			
			return cc;
		} catch (ClassNotFoundException e) {
            log.error(e.toString());
            throw new IOException(e);
        } catch (InstantiationException e) {
        	log.error(e.toString());
			throw new IOException(e);
		} catch (IllegalAccessException e) {
			log.error(e.toString());
			throw new IOException(e);
		}
	}
}
