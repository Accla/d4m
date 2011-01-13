/**
 * 
 */
package edu.mit.ll.oct4j;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author cyee
 *
 */
public class OctThreadFactory implements ThreadFactory {

	private String name=null;
	private static final AtomicInteger poolNumber = new AtomicInteger(1);

	private ThreadGroup group = null;

	private final AtomicInteger threadNumber = new AtomicInteger(1);
	/**
	 * 
	 */
	public OctThreadFactory(String name) {
		final SecurityManager securityManager = System.getSecurityManager();
		group = 
			(securityManager != null) ? securityManager.getThreadGroup() : Thread.currentThread().getThreadGroup();
			this.name = Thread.currentThread().getName() + "-octave4j-" + name + "-" + poolNumber.getAndIncrement()
			+ "-";
	}

	/* (non-Javadoc)
	 * @see java.util.concurrent.ThreadFactory#newThread(java.lang.Runnable)
	 */
	@Override
	public Thread newThread(Runnable r) {
		final Thread thread = 
			new Thread(group, r, this.name + threadNumber.getAndIncrement());
		if (thread.isDaemon()) {
			thread.setDaemon(false);
		}
		if (thread.getPriority() != Thread.NORM_PRIORITY) {
			thread.setPriority(Thread.NORM_PRIORITY);
		}
		return thread;
	}

}
