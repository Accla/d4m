package edu.mit.ll.sks.matlab;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import org.apache.log4j.Logger;

import matlabcontrol.LocalMatlabProxy;

public class D4mAnalyticsJsonListener {
	private static Logger log = Logger.getLogger(D4mAnalyticsJsonListener.class);
	private static ServerSocket serverSocket;
	private static final int port = 6222;
	protected static LinkedBlockingQueue<Socket> sockets;
	private static String dbHost = "localhost";

	//property name for designating the math app (matlab or octave) to run
	//default is 'matlab'
	public static String APP_SYS_PROPERTY="d4m.mathapp";
	public static String D4M_SYS_PROPERTY="d4m.home";
	private String appname="matlab";
	private String script="D4MwebQueryResponse";
	public static void main(String args[]) throws Exception {
		D4mAnalyticsJsonListener d4m = new D4mAnalyticsJsonListener("localhost", "localhost");
		Thread.sleep(Long.MAX_VALUE);
	}

	public D4mAnalyticsJsonListener(String responseReciptient, String dbHost) {
		try {
			this.dbHost = dbHost;
			serverSocket = new ServerSocket(port);
			System.out.println("Listening on 0.0.0.0:" + port);
			String appname = System.getProperty(APP_SYS_PROPERTY);

			if(appname != null)
				this.appname = appname;
			System.out.println("Running  "+this.appname);
			System.out.println("%% D4M_home =="+System.getProperty(D4M_SYS_PROPERTY));
		}
		catch (IOException e) {
			System.out.println("Could not listen on port: " + port);
			System.exit(-1);
		}
		sockets = new LinkedBlockingQueue<Socket>();
		System.out.println("Ready...");
		final ExecutorService executor = Executors.newSingleThreadExecutor();

		Runnable runnable = (Runnable) new ServerThread();
		executor.execute(runnable);

		doit();
	}

	public D4mAnalyticsJsonListener(String responseReciptient, String dbHost, String script) {
		try {
			this.script = script;
			this.dbHost = dbHost;
			serverSocket = new ServerSocket(port);
			System.out.println("Listening on 0.0.0.0:" + port);
			String appname = System.getProperty(APP_SYS_PROPERTY);

			if(appname != null)
				this.appname = appname;
			System.out.println("Running  "+this.appname);
			System.out.println("%% D4M_home =="+System.getProperty(D4M_SYS_PROPERTY));
		}
		catch (IOException e) {
			System.out.println("Could not listen on port: " + port);
			System.exit(-1);
		}
		sockets = new LinkedBlockingQueue<Socket>();
		System.out.println("Ready...");
		final ExecutorService executor = Executors.newSingleThreadExecutor();

		Runnable runnable = (Runnable) new ServerThread();
		executor.execute(runnable);

		doit();
	}

	class ServerThread implements Runnable {

		public void run() {
			while (true) {
				try {
					Socket socket = serverSocket.accept();
					sockets.put(socket);
					System.out.println("New Connection from: " + socket.getInetAddress().toString());
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void doit() {
		try {
			while (true) {
				Socket socket = sockets.take();				
				PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
				BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

				while (!socket.isConnected()) {
					Thread.sleep(100);
				}
				System.out.println("Connected... Waiting for input...");

				StringBuffer request = new StringBuffer();
				String line = in.readLine();
				request.append(line + "\n");
				while (line != null && !line.contains("}")) {
					line = in.readLine();
					if (line == null)
						break;
					request.append(line + "\n");					
				}
				System.out.println("Got Request: " + request.toString().substring(0, 50));

				// malab call goes here
				String results = "";
				MathAppIF mathapp =null;
				long startTime = System.currentTimeMillis();
				try {

					mathapp = MathAppFactory.getInstance(this.appname);
					mathapp.process( this.script,new String [] {request.toString(), dbHost});
					results = mathapp.getResult();
					log.info(">>>> Query results = "+results.substring(0,50));
					//mathapp.stop();
					//double estimatedTimeMillis = ((double)System.nanoTime()-(double)startTime)*1/1e+6;
					double estimatedTimeMillis = (double)(System.currentTimeMillis() - startTime);
					log.info("Estimated Octave Processing Time (ms) = "+Double.toString(estimatedTimeMillis));
					//results = (String) LocalMatlabProxy.returningFeval("D4MwebQueryResponse", new Object[] { request.toString(), dbHost });
				}
				catch (Throwable e) {
					e.printStackTrace();
				}
				System.out.println("Sending Results:  " + results.substring(0,50));

				// send response
				startTime = System.nanoTime();
				out.print(results);
				double estimatedTimeMillis = ((double)System.nanoTime()-(double)startTime)*1/1e+6;
				log.info("Estimated Time to send back results (ms) = "+Double.toString(estimatedTimeMillis));

				if (!results.endsWith("\n"))
					out.println();
				out.flush();

				System.out.println("Closing Connection to Client");
				// cleanup
				out.close();
				in.close();
				socket.close();
				startTime = System.nanoTime();
				mathapp.stop();
				estimatedTimeMillis = ((double)System.nanoTime()-(double)startTime)*1/1e+6;
				log.info("Estimated Time (ms) to STOP "+ this.appname + " = "+Double.toString(estimatedTimeMillis));

				
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
/*
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
 */
