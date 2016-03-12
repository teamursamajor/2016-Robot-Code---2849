//package org.usfirst.frc.team2849.robot.vision;
//
//import java.io.DataOutputStream;
//import java.io.IOException;
//import java.net.InetSocketAddress;
//import java.net.ServerSocket;
//import java.net.Socket;
//
//public class StreamSupplier implements Runnable {
//
//	private ServerSocket server;
//	private Socket socket;
//	private int port;
//
////	private DataInputStream in;
//	private DataOutputStream out;
//
//	private Webcam webcam;
//
//	private boolean running;
//	private int state;
//	
//	private byte[] bytes;
//
//	public StreamSupplier(Webcam webcam, int port) {
//		this.webcam = webcam;
//		this.port = port;
//		Thread stream = new Thread(this);
//		stream.start();
//	}
//
//	@Override
//	public void run() {
//		while (running) {
//			switch (state) {
//			case 0:
//				try {
//					server = new ServerSocket();
//					server.bind(new InetSocketAddress("10.28.49.21", port));
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//				state++;
//				break;
//
//			case 1:
//				try {
//					socket = server.accept();
////					in = new DataInputStream(socket.getInputStream());
//					out = new DataOutputStream(socket.getOutputStream());
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//				state++;
//				break;
//				
//			case 2:
//				try {
//					bytes = webcam.getRawData(".jpg");
//					out.writeInt(bytes.length);
//					out.write(bytes);
//				} catch (IOException e) {
//					try {
//						socket.close();
//					} catch (IOException e1) {
//						e1.printStackTrace();
//					}
//					state = 0;
//					e.printStackTrace();
//				}
//				break;
//			}
//		}
//	}
//
//	public void stop() {
//		running = false;
//	}
//
//}
