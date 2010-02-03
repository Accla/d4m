package cloudbase.core.util;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.channels.ServerSocketChannel;
import java.util.concurrent.ExecutorService;

import org.apache.log4j.Logger;

import com.facebook.thrift.TProcessor;
import com.facebook.thrift.protocol.TBinaryProtocol;
import com.facebook.thrift.server.TServer;
import com.facebook.thrift.server.TThreadPoolServer;
import com.facebook.thrift.transport.TServerSocket;
import com.facebook.thrift.transport.TServerTransport;
import com.facebook.thrift.transport.TTransportFactory;

public class TServerUtils {
    static Logger log = Logger.getLogger(TServerUtils.class.getName());

    // Use NIO sockets to create a TServer so our shutdown function will work
    public static TServerTransport openPort(int port) throws IOException {
        ServerSocket sock = ServerSocketChannel.open().socket();
        sock.setReuseAddress(true);
        sock.bind(new InetSocketAddress(port));
        return new TServerSocket(sock);
    }
    
    // Boilerplate start-up for a TServer
    public static TServer startTServer(TProcessor processor, 
                                       TServerTransport serverTransport, 
                                       String threadName, 
                                       int numThreads) {
        TThreadPoolServer.Options options = new TThreadPoolServer.Options();
        if (numThreads > 0)
            options.maxWorkerThreads = numThreads;
        final TServer tserver = new TThreadPoolServer(processor, 
                                                      serverTransport,
                                                      new TTransportFactory(), 
                                                      new TTransportFactory(),
                                                      new TBinaryProtocol.Factory(), 
                                                      new TBinaryProtocol.Factory(),
                                                      options);
        Runnable serveTask = new Runnable() { 
            public void run() {
                tserver.serve();
            } 
        };
        serveTask = new LoggingRunnable(log, serveTask);
        Thread thread = new Daemon(serveTask, threadName);
        thread.start();
        return tserver;
    }
    
    // Existing connections will keep our thread running: reach in with reflection and insist that they shutdown.
    public static void stopTServer(TServer s) {
        s.stop();
        try {
            Field f = s.getClass().getDeclaredField("executorService_");
            f.setAccessible(true);
            ExecutorService es = (ExecutorService)f.get(s);
            es.shutdownNow();
        } catch (Exception e) {
            log.error("Unable to call shutdownNow", e);
        }
    }

}
