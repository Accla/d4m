package cloudbase.core.util;

import com.facebook.thrift.TException;
import com.facebook.thrift.TProcessor;
import com.facebook.thrift.TProcessorFactory;
import com.facebook.thrift.protocol.TProtocol;
import com.facebook.thrift.server.TServer;
import com.facebook.thrift.transport.TServerTransport;
import com.facebook.thrift.transport.TTransport;
import com.facebook.thrift.transport.TTransportException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/* A simple version of Thrift's TThreadPoolServer, 
 * with a bounded number of threads, and an unbounded work queue
 */

public class ThreadPoolServer extends TServer {

    // Executor service for handling client connections
   private ExecutorService executorService_;

   // Flag for stopping the server
   private volatile boolean stopped_;

   public ThreadPoolServer(TProcessor processor,
           TServerTransport serverTransport,
           int threads) {
       super(new TProcessorFactory(processor), serverTransport);
       executorService_ = Executors.newFixedThreadPool(threads);
   }


   public ThreadPoolServer(TProcessor processor,
           TServerTransport serverTransport) {
       super(new TProcessorFactory(processor), serverTransport);
       executorService_ = Executors.newFixedThreadPool(5);
   }


   public void serve() {
     try {
       serverTransport_.listen();
     } catch (TTransportException ttx) {
       ttx.printStackTrace();
       return;
     }

     stopped_ = false;
     while (!stopped_) {
       int failureCount = 0;
       try {
         TTransport client = serverTransport_.accept();
         WorkerProcess wp = new WorkerProcess(client);
         executorService_.execute(wp);
       } catch (TTransportException ttx) {
         if (!stopped_) {
           ++failureCount;
           ttx.printStackTrace();
         }
       }
     }

     executorService_.shutdown();
     try {
         
       executorService_.awaitTermination(60, TimeUnit.SECONDS);
     } catch (InterruptedException ix) {
       // Ignore and more on
     }
   }

   public void stop() {
     stopped_ = true;
     serverTransport_.interrupt();
   }

   private class WorkerProcess implements Runnable {

     /**
      * Client that this services.
      */
     private TTransport client_;

     /**
      * Default constructor.
      *
      * @param client Transport to process
      */
     private WorkerProcess(TTransport client) {
       client_ = client;
     }

     /**
      * Loops on processing a client forever
      */
     public void run() {
       TProcessor processor = null;
       TTransport inputTransport = null;
       TTransport outputTransport = null;
       TProtocol inputProtocol = null;
       TProtocol outputProtocol = null;
       try {
         processor = processorFactory_.getProcessor(client_);
         inputTransport = inputTransportFactory_.getTransport(client_);
         outputTransport = outputTransportFactory_.getTransport(client_);
         inputProtocol = inputProtocolFactory_.getProtocol(inputTransport);
         outputProtocol = outputProtocolFactory_.getProtocol(outputTransport);
         while (processor.process(inputProtocol, outputProtocol)) {}
       } catch (TTransportException ttx) {
         // Assume the client died and continue silently
       } catch (TException tx) {
         tx.printStackTrace();
       } catch (Exception x) {
         x.printStackTrace();
       }

       if (inputTransport != null) {
         inputTransport.close();
       }

       if (outputTransport != null) {
         outputTransport.close();
       }
     }
   }
 }


