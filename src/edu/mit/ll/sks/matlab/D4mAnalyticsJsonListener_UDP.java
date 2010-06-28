package edu.mit.ll.sks.matlab;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.concurrent.LinkedBlockingQueue;

import matlabcontrol.*;

public class D4mAnalyticsJsonListener_UDP {

	private String results = "";
	private static DatagramSocket tx = null;
	private static UdpReceiver rx = null;

	public static void main(String args[]) throws Exception {
		D4mAnalyticsJsonListener_UDP d4m = new D4mAnalyticsJsonListener_UDP("localhost");
		Thread.sleep(Long.MAX_VALUE);
	}
	
	public D4mAnalyticsJsonListener_UDP(String responseReciptient) {
		while (true) {
			// setup rx socket
			if (rx == null) {
				try {
					rx = new UdpReceiver(new DatagramSocket(6333));
				}
				catch (SocketException e) {
					e.printStackTrace();
				}
				rx.init();
			}
			System.out.println("Ready...");
			
			// wait for a message
			DatagramPacket request = (DatagramPacket) rx.fetch();
			String json = new String(request.getData());
			System.out.println("just received: " + json);

			// malab call goes here
			try {
				results = (String) LocalMatlabProxy.returningFeval("D4MwebQueryResponse", new Object[] { json });
//				results = (String) LocalMatlabProxy.returningEval("pwd", 1);
				System.out.println(results);
			}
			catch (Exception e) {
				e.printStackTrace();
				results = "";
			}

			byte[] message = results.getBytes();
			DatagramPacket response = new DatagramPacket(message, message.length, resolveHost(responseReciptient), 6444);

			// setup tx socket
			if (tx == null) {
				try {
					tx = new DatagramSocket();
				}
				catch (SocketException e) {
					e.printStackTrace();
				}
			}

			// send response
			try {
				System.out.println("about to send: " + results);
				tx.send(response);
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private InetAddress resolveHost(String host) {
		try {
			return InetAddress.getByName(host);
		}
		catch (UnknownHostException e) {
			e.printStackTrace();
			return null;
		}
	}

	private class UdpReceiver implements Runnable {

		byte[] buffer = new byte[10240];
		private LinkedBlockingQueue<DatagramPacket> Q;
		private Thread listener;
		private boolean isRunning = false;
		private DatagramSocket dsocket;

		public UdpReceiver(DatagramSocket dsocket) {
			this.dsocket = dsocket;
		}

		public Object fetch() {
			DatagramPacket packet = null;
			try {
				packet = (DatagramPacket) Q.take();
			}
			catch (InterruptedException e) {
				e.printStackTrace();
			}
			return packet;
		}

		public void init() {
			Q = new LinkedBlockingQueue<DatagramPacket>(100);
			isRunning = true;
			start();
		}

		public void run() {
			while (isRunning == true) {
				try {
					// Create a packet to receive data into the buffer
					buffer = new byte[10240];
					DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

					// Wait to receive a packet
					dsocket.receive(packet);

					// Add to packet queue
					Q.put(packet);
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		public void start() {
			isRunning = true;
			listener = new Thread(this);
			listener.start();
		}
	}
}
