package cloudbase.core.util;

import org.apache.log4j.Logger;

public class UtilWaitThread {
	private static Logger log = Logger.getLogger(UtilWaitThread.class.getName());
	
	public static void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
		}
	}
	
	public static void main(String[] args) {
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			log.error(e.toString());
		}
		
		System.out.println("done with thread");
		UtilWaitThread.sleep(100);
		
		System.out.println("done with utilwaitthread");
	}
}