package cloudbase.core.util;

import java.io.IOException;

import cloudbase.core.tabletserver.log.BasicLogger;

public class PrintWriteAheadLog {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		BasicLogger.printWriteAheadLog(args[0]);
	}

}
