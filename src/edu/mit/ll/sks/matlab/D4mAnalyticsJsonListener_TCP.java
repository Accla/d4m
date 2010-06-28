package edu.mit.ll.sks.matlab;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import matlabcontrol.LocalMatlabProxy;

public class D4mAnalyticsJsonListener_TCP {

	private static ServerSocket serverSocket;
	private static final int port = 6222;

	public static void main(String args[]) throws Exception {
		D4mAnalyticsJsonListener_TCP d4m = new D4mAnalyticsJsonListener_TCP("localhost");
		Thread.sleep(Long.MAX_VALUE);
	}

	public D4mAnalyticsJsonListener_TCP(String responseReciptient) {
		try {
			serverSocket = new ServerSocket(port);
			System.out.println("Listening on 0.0.0.0:" + port);
		}
		catch (IOException e) {
			System.out.println("Could not listen on port: "+ port);
			System.exit(-1);
		}
		System.out.println("Ready...");

		while (true) {
			try {
				new ServerThread(serverSocket.accept()).start();
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	class ServerThread extends Thread {
		private Socket socket = null;

		ServerThread(Socket socket) {
			super("ServerThread");
			this.socket = socket;
		}

		public void run() {
			try {
				PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
				BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

				while (!socket.isConnected()) {
					Thread.sleep(100);
				}
				System.out.println("Connected... Waiting for input...");
				String line = in.readLine();

				StringBuffer request = new StringBuffer();
				request.append(line + "\n");
				while (true) {
					line = in.readLine();					
					if (line == null)
						break;
					request.append(line + "\n");
					if(line.contains("}"))
						break;
				}				
				System.out.println("Got Request: " + request.toString());

				// malab call goes here
				String results = (String) LocalMatlabProxy.returningFeval("D4MwebQueryResponse", new Object[] { request.toString() });
//				String results = "foo";
				System.out.println("Sending Results:  " + results);

				// send response
				out.print(results);
				if(!results.endsWith("\n"))
					out.println();
				out.flush();

				System.out.println("Closing Connection to Client");
				// cleanup
				out.close();
				in.close();
				socket.close();
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}