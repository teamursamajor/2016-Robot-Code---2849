package org.usfirst.frc.team2849.robot;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Class to handle streaming a feed back to the laptop
 * 
 * @author teamursamajor
 */
public class StreamSupplier implements Runnable {

	// Socket stuff
	private ServerSocket server;
	private Socket socket;
	private int port;

	// Output stream from the socket
	private DataOutputStream out;

	// Webcam stuff
	private ArrayList<Webcam> webcams = new ArrayList<Webcam>();
	private int currentWebcam;

	// Whether thread is running
	private boolean running;

	// State machine variables
	private int state;
	private int buttonState;

	// Bytes to write out
	private byte[] bytes;

	/**
	 * Initializer for the stream
	 * 
	 * @param port
	 *            The port the socket should be opened
	 */
	public StreamSupplier(int port) {
		this.port = port;
		state = 0;
		buttonState = 0;
		running = true;
		Thread stream = new Thread(this);
		System.out.println("Starting Streaming!");
		stream.start();
	}

	/**
	 * Adds a webcam to the list of webcams to stream from
	 * 
	 * @param webcam
	 *            Webcam to add to the lsit
	 */
	public void addWebcam(Webcam webcam) {
		webcams.add(webcam);
		currentWebcam = webcams.size() - 1;
	}

	/**
	 * Advances the feed if possible
	 */
	private void nextFeed() {
		if (currentWebcam < (webcams.size() - 1))
			currentWebcam++;
		if (currentWebcam == 2) Drive.inverse = true;
		else Drive.inverse = false;
	}

	/**
	 * Decrements the feed if possible
	 */
	private void previousFeed() {
		if (currentWebcam > 0)
			currentWebcam--;
		if (currentWebcam == 2) Drive.inverse = true;
		else Drive.inverse = false;
	}

	/**
	 * Periodic function to handle the changing of streams
	 * 
	 * @param previous
	 *            Button value for decrementing stream
	 * @param next
	 *            Button value for incrementing stream
	 */
	public void runStreamChanger(boolean previous, boolean next) {
		//		System.out.println(previous + ":" + next + ":" + webcams.size());
		switch (buttonState) {
		case 0:
			if (next && !previous) {
				nextFeed();
				buttonState++;
				System.out.println("Current Webcam is: " + currentWebcam);
			} else if (previous && !next) {
				previousFeed();
				buttonState++;
				System.out.println("Current Webcam is: " + currentWebcam);
			}
			break;

		case 1:
			if (!next && !previous)
				buttonState = 0;
			break;
		}
	}

	/**
	 * Thread function which creates a server socket. Accepts a connection to
	 * the socket and starts streaming.
	 */
	@Override
	public void run() {
		while (running) {
			switch (state) {
			case 0:
				try {
					server = new ServerSocket();
					System.out.println("Trying to assign to: " + new InetSocketAddress("roborio-2849-frc.local", port));
					server.bind(new InetSocketAddress("roborio-2849-frc.local", port));
					System.out.println("Opening socket on: " + server.getInetAddress() + ":" + server.getLocalPort());
				} catch (IOException e) {
					e.printStackTrace();
				}
				state++;
				break;

			case 1:
				try {
					server.setSoTimeout(1000);
					socket = server.accept();
					System.out.println("Accepted socket connection");
					out = new DataOutputStream(socket.getOutputStream());
					state++;
				} catch (IOException e) {
				}
				break;

			case 2:
				if (webcams.size() > 0)
					state++;
				break;

			case 3:
				try {
					bytes = webcams.get(currentWebcam).getRawData(".jpg");
					out.writeInt(bytes.length);
					out.write(bytes);
				} catch (IOException e) {
					try {
						socket.close();
						server.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					state = 0;
					System.out.println("Connection lost");
				}
				break;
			}
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Stop the streaming thread safely
	 */
	public void stop() {
		running = false;
	}

}
