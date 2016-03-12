//package org.usfirst.frc.team2849.robot.vision;
//
//import org.opencv.core.Mat;
//import org.opencv.core.MatOfByte;
//import org.opencv.highgui.Highgui;
//import org.opencv.highgui.VideoCapture;
//
//public class Webcam implements Runnable{
//
//	private boolean running;
//	private VideoCapture webcam;
//	private Mat imageMat;
//	private MatOfByte rawDataMat;
//	private int width;
//	private int height;
//	private double fps;
//	
//	public Webcam(int deviceNumber, int width, int height, double fps) {
//		this.width = width;
//		this.height = height;
//		this.fps = fps;
//		webcam = new VideoCapture();
//		webcam.open(deviceNumber, width, height, fps);
//		Thread webcamStream = new Thread(this);
//		webcamStream.start();
//	}
//	
//	@Override
//	public void run() {
//		while (running) {
//			webcam.grab();
//			try {
//				Thread.sleep(10);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//		}
//	}
//	
//	public void stop() {
//		running = false;
//	}
//	
//	public Mat getImage() {
//		webcam.retrieve(imageMat);
//		return imageMat;
//	}
//	
//	public byte[] getRawData(String extension) {
//		Highgui.imencode(extension, getImage(), rawDataMat);
//		return rawDataMat.toArray();
//	}
//
//	public int getWidth() {
//		return width;
//	}
//
//	public int getHeight() {
//		return height;
//	}
//
//	public double getFps() {
//		return fps;
//	}
//
//}
