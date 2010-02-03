package cloudbase.core.client.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import cloudbase.core.util.AddressUtil;
import cloudbase.core.util.Daemon;

import com.facebook.thrift.transport.TTransport;
import com.facebook.thrift.transport.TTransportException;

public class ThriftTansportPool {
	// Replaced 1000*30 with 1000*3, cdubs
	private long killTime = 1000 * 3; 
	
	private Map<ArrayList<Object>, List<CachedConnection>> cache;
	
	private static Logger log = Logger.getLogger(ThriftTansportPool.class.getName());
	
	private static class CachedConnection {
		
		public CachedConnection(CachedTTransport t) {
			this.transport = t;
		}
		
		CachedTTransport transport;
		
		boolean reserved;
		long reserveTime;
		long lastReturnTime;
	}
	
	private class Closer implements Runnable {
		public void run() {
			while(true){

				ArrayList<CachedConnection> connectionsToClose = new ArrayList<CachedConnection>();

				synchronized (ThriftTansportPool.this){
					for(List<CachedConnection> ccl : cache.values()){
						Iterator<CachedConnection> iter = ccl.iterator();
						while(iter.hasNext()){
							CachedConnection cachedConnection = iter.next();

							if(!cachedConnection.reserved &&
								System.currentTimeMillis() - cachedConnection.lastReturnTime > killTime)
							{
								connectionsToClose.add(cachedConnection);
								iter.remove();
							}
						}
					}
				}

				//close connections outside of sync block
				for (CachedConnection cachedConnection : connectionsToClose) {
					//System.out.println("DEBUG : closing idle transport "+cachedConnection.transport.cacheKey);
					cachedConnection.transport.close(); 
				}
				
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	class CachedTTransport extends TTransport {
		

		private ArrayList<Object> cacheKey;
		private TTransport wrappedTransport;
		private boolean sawError = false;
		
		public CachedTTransport(TTransport transport, ArrayList<Object> cacheKey2) {
			this.wrappedTransport = transport;
			this.cacheKey = cacheKey2;
		}

		public boolean isOpen() {
			return wrappedTransport.isOpen();
		}

		public void open() throws TTransportException {
			try{
				wrappedTransport.open();
			}catch(TTransportException tte){
				sawError = true;
				throw tte;
			}
		}

		public int read(byte[] arg0, int arg1, int arg2) throws TTransportException {
			try{
				return wrappedTransport.read(arg0, arg1, arg2);
			}catch(TTransportException tte){
				sawError = true;
				throw tte;
			}
		}

		public int readAll(byte[] arg0, int arg1, int arg2) throws TTransportException {
			try{
				return wrappedTransport.readAll(arg0, arg1, arg2);
			}catch(TTransportException tte){
				sawError = true;
				throw tte;
			}
		}
		
		public void write(byte[] arg0, int arg1, int arg2) throws TTransportException {
			try{
				wrappedTransport.write(arg0, arg1, arg2);
			}catch(TTransportException tte){
				sawError = true;
				throw tte;
			}
		}

		public void write(byte[] arg0) throws TTransportException {
			try{
				wrappedTransport.write(arg0);
			}catch(TTransportException tte){
				sawError = true;
				throw tte;
			}
		}
		
		public void close() {
			wrappedTransport.close();
		}
		
		public void flush() throws TTransportException{
			try{
				wrappedTransport.flush();
			}catch(TTransportException tte){
				sawError = true;
				throw tte;
			}
		}
		
		public boolean peek(){
			return wrappedTransport.peek();
		}

		public ArrayList<Object> getCacheKey() {
			return cacheKey;
		}
		
	}
	
	private ArrayList<Object> createKey(String location, int port){
		ArrayList<Object> l = new ArrayList<Object>(2);
		l.add(location);
		l.add(new Integer(port));
		
		return l;
	}
	
	private ThriftTansportPool(){
		cache = new HashMap<ArrayList<Object>, List<CachedConnection>>();
		
		Thread thread = new Daemon(new Closer(), "Cached TS Connection Closer");
		thread.start();
	}
	
	public TTransport getTransport(String location, int port) throws TTransportException {
		CachedConnection cc = null; 
		
		ArrayList<Object> cacheKey = createKey(location, port);
		cacheKey.equals(null);
		
		synchronized (this) {
			//atomically reserve location if it exist in cache
			List<CachedConnection> ccl = cache.get(cacheKey);
			
			if(ccl == null){
				ccl = new LinkedList<CachedConnection>();
				cache.put(cacheKey, ccl);
			}
			
			for (CachedConnection cachedConnection : ccl) {
				if(!cachedConnection.reserved){
					cachedConnection.reserved = true;
					cachedConnection.reserveTime = System.currentTimeMillis();
					cc = cachedConnection;
					
					//System.out.println("DEBUG : Using existing connection "+cacheKey);
					
					break;
				}
			}
		}
		
		if(cc == null){
			//nothing in cache, so create a new connection... do not want to synchronize this... multiple threads could
			//end up waiting
			
			TTransport transport = AddressUtil.createTSocket(location, port);
			transport.open();
			
			//System.out.println("DEBUG : Creating new connection "+cacheKey);
			
			CachedTTransport tsc = new CachedTTransport(transport, cacheKey);
			
			cc = new CachedConnection(tsc);
			cc.reserved = true;
			cc.reserveTime = System.currentTimeMillis();
			
			synchronized (this) {
				List<CachedConnection> ccl = cache.get(cacheKey);
				
				if(ccl == null){
					ccl = new LinkedList<CachedConnection>();
					cache.put(cacheKey, ccl);
				}
				
				ccl.add(cc);
			}
		}
		
		return cc.transport;
	}
	
	public void returnTransport(TTransport tsc){
		
		if(tsc == null){
			return;
		}
		
		boolean existInCache = false;
		CachedTTransport ctsc = (CachedTTransport)tsc;
		
		synchronized (this) {
			List<CachedConnection> ccl = cache.get(ctsc.getCacheKey());
			for (Iterator<CachedConnection> iterator = ccl.iterator(); iterator.hasNext();) {
				CachedConnection cachedConnection = iterator.next();
				if(cachedConnection.transport == tsc){
					if(ctsc.sawError){
						tsc.close();
						iterator.remove();
						
						//System.out.println("DEBUG : Returned connection had error "+ctsc.getCacheKey());
					}else{
						cachedConnection.lastReturnTime = System.currentTimeMillis();
						cachedConnection.reserved = false;
			
						//System.out.println("DEBUG : Returned connection "+ctsc.getCacheKey());
					}
					existInCache = true;
					break;
				}
			}
		}
		
		
		if(!existInCache){
			log.warn("Returned tablet server connection to cache that did not come from cache");
			tsc.close();
		}
	}
	
	private static ThriftTansportPool instance = null;
	
	public static ThriftTansportPool getInstance(){
		if(instance == null){
			synchronized(ThriftTansportPool.class){
				if(instance == null){
					instance = new ThriftTansportPool();
				}
			}
		}
		
		return instance;
	}
}
