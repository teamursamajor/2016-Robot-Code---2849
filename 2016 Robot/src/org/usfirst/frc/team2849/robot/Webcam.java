package org.usfirst.frc.team2849.robot;

import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfInt;
import org.opencv.highgui.Highgui;
import org.opencv.highgui.VideoCapture;

/**
 * Webcam class
 * 
 * @author teamursamajor
 */
public class Webcam implements Runnable{

	private VideoCapture webcam;
	
	private Mat imageMat = new Mat();
	private MatOfByte rawDataMat = new MatOfByte();
	private MatOfInt jpegQuality = new MatOfInt(Highgui.CV_IMWRITE_JPEG_QUALITY, 50);

	private boolean running;
	private int width;
	private int height;
	private double fps;
	
	public Webcam(int deviceNumber, int width, int height, double fps) {
		this.width = width;
		this.height = height;
		this.fps = fps;
		running = true;
		webcam = new VideoCapture();
		webcam.open(deviceNumber, width, height, fps);
		Thread webcamStream = new Thread(this);
		webcamStream.start();
	}
	
	@Override
	public void run() {
		while (running) {
			webcam.grab();
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void stop() {
		running = false;
	}
	
	public synchronized Mat getImage() {
		webcam.retrieve(imageMat);
		return imageMat;
	}
	
	public byte[] getRawData(String extension) {
		Highgui.imencode(extension, getImage(), rawDataMat, jpegQuality);
		return rawDataMat.toArray();
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public double getFps() {
		return fps;
	}

}
