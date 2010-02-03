package cloudbase.core.tabletserver.log;

import java.io.IOException;

import org.apache.log4j.Logger;

import cloudbase.core.CBConstants;
import cloudbase.core.conf.CBConfiguration;
import cloudbase.core.data.KeyExtent;
import cloudbase.core.data.Mutation;

public abstract class TabletLog {
	
	static Logger log = Logger.getLogger(BasicLogger.class.getName());
	
	public static TabletLog getInstance(KeyExtent ke){
		
		if(ke.getTableName().toString().equals(CBConstants.METADATA_TABLE_NAME)){
			CBConfiguration conf = CBConfiguration.getInstance();
			String logDir = conf.get("cloudbase.tablet.walog.directory");
			
			log.debug("cloudbase.tablet.walog.directory : "+logDir);
			
			if(logDir != null && !logDir.trim().equals("")){
				return new BasicLogger(ke, logDir);
			}else{
				return new NullLogger();
			}
		}else{
			return new NullLogger();
		}
		
	}

	public abstract void log(Mutation m) throws IOException;
	public abstract void minorCompactionStarted(String fullyQualifiedFileName) throws IOException;
	public abstract void minorCompactionFinished(String fullyQualifiedFileName) throws IOException;
	
	public abstract void open(MutationReceiver mr) throws IOException;
	public abstract void close() throws IOException;
}
