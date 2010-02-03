package cloudbase.core.test;

import org.apache.log4j.Logger;

public class InterruptTest {
	private static Logger log = Logger.getLogger(InterruptTest.class.getName());
	
	public static void main(String[] args) {
		Runtime r = Runtime.getRuntime();
		r.addShutdownHook(new Thread() {
			public void run() {
				log.info("shutdown");
			}
		});
		while(true);
		
	}
	
	protected void finalize() {
		log.info("in finalizer");
	}
}
