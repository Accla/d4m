package cloudbase.core.zookeeper;

import sun.misc.BASE64Encoder;
import cloudbase.core.CBConstants;
import cloudbase.core.Cloudbase;

public class ZooConstants {
	// User paths in ZK
	public static String ZKUserPath = CBConstants.ZROOT_PATH+"/"+Cloudbase.getInstanceID()+"/users";
	
	public static final String SYSTEM_USERNAME = "!SYSTEM";
	public static final byte[] SYSTEM_PASSWORD = new BASE64Encoder().encode(Cloudbase.getInstanceID().getBytes()).getBytes();
}
