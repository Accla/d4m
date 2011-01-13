package edu.mit.ll.oct4j;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public class MySocketListener {
	/**
	 * 
	 */
	public MySocketListener(String host, int port) {

		super();
		dbHost = host;
		PORT = port;
		try {

			serverSocket = new ServerSocket(PORT);
			System.out.println("Listening on 0.0.0.0:" + PORT);
		}
		catch (IOException e) {
			System.out.println("Could not listen on port: " + PORT);
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
				System.out.println("Got Request: " + request.toString());

				// matlab call goes here
				String results = "Hello. Got you message!";
				try {
					//if(isOctave) {
					//	OctaveEngine octave = new OctaveEngineFactory().getScriptEngine();
					//octave.put("query",new OctaveString(request.toString()));
					//octave.put("host",new OctaveString(dbHost));
					//octave.eval("resp =D4MwebQueryResponse(query,host); ");
					//OctaveString resp = octave.get(OctaveString.class,"resp");
					//octave.close();
					//results = resp.getString();

					//} else {
					//results = (String) LocalMatlabProxy.returningFeval("D4MwebQueryResponse", new Object[] { request.toString(), dbHost });
					// }
				}
				catch (Throwable e) {
					e.printStackTrace();
				}
				System.out.println("Sending Results:  " + results);

				// send response
				out.print(results);
				if (!results.endsWith("\n"))
					out.println();
				out.flush();

				System.out.println("Closing Connection to Client");
				// cleanup
				out.close();
				in.close();
				socket.close();
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static ServerSocket serverSocket;
	private static  int PORT = 6222;
	protected static LinkedBlockingQueue<Socket> sockets;
	private static String dbHost = "localhost";


}
