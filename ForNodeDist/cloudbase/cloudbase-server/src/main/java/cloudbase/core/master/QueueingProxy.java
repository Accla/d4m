package cloudbase.core.master;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.InvocationHandler;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.log4j.Logger;

import cloudbase.core.util.UtilWaitThread;

public class QueueingProxy {
	
	static Logger log = Logger.getLogger(QueueingProxy.class.getName());
	
	private LinkedBlockingQueue<MethodCall> callQueue;
	private Map<Long, ReturnValue> returnQueue;
	private Object proxy;

	private Object handler;
	
	private AtomicLong nextReturnId;

	private boolean waitForVoidMethodsToReturn;

	private long invokeTime;
	
	private static class MethodCall {
		long returnId;
		Method method; 
		Object[] args;
		long timeInvoked;
		
		MethodCall(Method m, Object[] args, long timeInvoked, long returnId){
			this.method = m;
			this.args = args;
			this.timeInvoked = timeInvoked;
			this.returnId = returnId;
		}
	}
	
	private static class ReturnValue {
		Object value;
		Throwable exception;
		
		ReturnValue(Object value, Throwable excpetion){
			this.value = value;
			this.exception = excpetion;
		}
	}
	
	private class MyInvocationHandler implements InvocationHandler {
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			
			long returnId = nextReturnId.getAndIncrement();
			
			if(!callQueue.offer(new MethodCall(method, args, System.currentTimeMillis(), returnId))){
				log.error("Failed to add object to queue ");
				throw new RuntimeException();
			}
			
			if(!waitForVoidMethodsToReturn && method.getReturnType() == Void.TYPE)
				return null;
			else{
				synchronized (returnQueue) {
					while(!returnQueue.containsKey(returnId)){
						returnQueue.wait();
					}
					
					ReturnValue rv = returnQueue.remove(returnId);
					
					if(rv.exception != null){
						throw rv.exception;
					}else{
						return rv.value;
					}
				}
			}
		}
		
	}
	
	public QueueingProxy(Class<?> iface, Object handler, boolean waitForVoidMethodsToReturn){
		callQueue = new LinkedBlockingQueue<MethodCall>();
		returnQueue = Collections.synchronizedMap(new HashMap<Long, ReturnValue>());
		nextReturnId = new AtomicLong();
		this.waitForVoidMethodsToReturn = waitForVoidMethodsToReturn;
		
		this.handler = handler;
		
		InvocationHandler mih = new MyInvocationHandler();
		
		invokeTime = -1;
		
		proxy = Proxy.newProxyInstance(iface.getClassLoader(),
                new Class[] { iface },
                mih);
	}
	
	public long getOriginalInvokeTime(){
		return invokeTime;
	}
	
	public void executeQueuedMethodCalls(){
		MethodCall mc;
		
		while((mc = callQueue.poll()) != null){
			
				Object ret = null;
				Throwable excpetion = null;
				
				//System.out.println("Executing : "+mc.method.getName());
				
				try{
					invokeTime = mc.timeInvoked;
					ret = mc.method.invoke(handler, mc.args);
				}catch (IllegalArgumentException e) {
					log.error("Failed to execute queue method ", e);
				} catch (IllegalAccessException e) {
					log.error("Failed to execute queue method ", e);
				} catch (InvocationTargetException e) {
					excpetion = e.getCause();
				}finally{
					invokeTime = -1;
				}
				
				if(!waitForVoidMethodsToReturn && mc.method.getReturnType() == Void.TYPE){
					if(excpetion != null){
						log.warn("void async method "+mc.method.getName()+" threw exception ", excpetion);
					}
				}else{
					synchronized (returnQueue) {
						returnQueue.put(mc.returnId, new ReturnValue(ret, excpetion));
						returnQueue.notifyAll();
					}
				}
			 
		}
	}
	
	public Object getProxy(){
		return proxy;
	}
	
	public static void main(String[] args) {
		Foo foo = new Foo(){

			public void bar(int i) {
				System.out.println(i);
				
			}

			public int poo(int i) {
				if(i == 4){
					throw new RuntimeException("arghhh");
				}
				return i*i;
			}
			
			public void pooj(){
				throw new RuntimeException("heee");
			}
			
		};
		
		final QueueingProxy qp = new QueueingProxy(Foo.class, foo, true);
		
		Runnable r = new Runnable(){

			public void run() {
				for(int i = 0; i < 5; i++){
					UtilWaitThread.sleep(1000);
					qp.executeQueuedMethodCalls();
				}
				
			}
			
		};
		
		new Thread(r).start();
		
		final Foo fooProxy = (Foo)qp.getProxy();
		
		r = new Runnable(){public void run() {fooProxy.bar(3);}};
		new Thread(r).start();
		r = new Runnable(){public void run() {System.out.println(fooProxy.poo(5));}};
		new Thread(r).start();
		r = new Runnable(){public void run() {fooProxy.pooj();}};
		new Thread(r).start();
		
	}
}

interface Foo {
	void bar(int i);
	int poo(int i);
	void pooj();
}
