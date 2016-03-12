package application;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class PictureClient implements Runnable {

	private boolean running;
	private int state;
	private Socket client;
	private DataOutputStream out;
	private DataInputStream in;
	private ImageView imageViewer;
	private byte[] byteArray;
	private int port;
	private long lastTime = 0;

	public PictureClient(ImageView image, int port) {
		imageViewer = image;
		this.port = port;
	}

	@Override
	public void run() {
		running = true;
		state = 0;
		while (running) {
			switch (state) {
			case 0:
				try {
					client = new Socket();
					//set socket timeout for client to use for reads here
					client.connect(new InetSocketAddress("roboRIO-2849-FRC.local", port), 5000);
					out = new DataOutputStream(client.getOutputStream());
					in = new DataInputStream(client.getInputStream());
				} catch (IOException e) {
					System.out.println("couldn't connect");
				} finally {
					if (client.isConnected()) {
						System.out.println("connected");
						state++;
						lastTime = System.currentTimeMillis();
					}
				}
				break;

			case 1:
				try {
					//inputstream.available is unreliable for
					//sockets, just do a read in a try/catch
					//to handle the timeout
//					System.out.println("Trying for picture: " + in.available());
					if (in.available() > 0) {
						//check return before setting the image
						imageViewer.setImage(getImageFromBytes(in));
						lastTime = System.currentTimeMillis();
					} else {
//						System.out.println("No image to get: " + (System.currentTimeMillis()-lastTime));
						if (System.currentTimeMillis() - lastTime >= 10000) {
							client.close();
							try {
								Thread.sleep(2000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							state = 0;
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
				break;
			}
		}
	}

	public Image getImageFromBytes(DataInputStream stream) throws IOException {
		//put in a try/catch, return null if it has an exception
		//which it will if it timesout
		byte[] bytes = new byte[stream.readInt()];
		stream.readFully(bytes);
		return new Image(new ByteArrayInputStream(bytes));
	}

	public void stop() {
		System.out.println("stopping");
		running = false;
	}

}
