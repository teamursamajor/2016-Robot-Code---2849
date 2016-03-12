//package org.usfirst.frc.team2849.robot.vision;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import org.opencv.core.Core;
//import org.opencv.core.Mat;
//import org.opencv.core.MatOfPoint;
//import org.opencv.core.Point;
////import org.opencv.core.Scalar;
////import org.opencv.highgui.Highgui;
//import org.opencv.imgproc.Imgproc;
//import org.opencv.imgproc.Moments;
//
//import edu.wpi.first.wpilibj.Solenoid;
//
//public class Vision implements Runnable {
//	/**
//	 * Super cool vision code by Punchline Thanks to Nate for making it not
//	 * horrible Thanks to Abhi for commenting "Abhi was here" a few times I'd
//	 * like to thank the Academy, my family, friends, and Director Joss Whedon
//	 * Vision will be coming back in "Vision II: Electric Boogaloo" in theaters
//	 * January 2017
//	 */
//	private Solenoid ledRing;
//	private int runCounter;
//	private long startTime;
//	private int stage;
//	private Mat singleChannelLed = new Mat();
//	private Mat singleChannelNoLed = new Mat();
//	private Point center;
//	private Point centerDist = new Point();
//	private List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
//	private Mat hierarchy = new Mat();
//	private double maxArea;
//	private int maxContour;
//	private double area;
//	private int index;
//	private boolean done;
//	private long runTime;
//	private long wholeTime;
//	private Webcam visionCam;
//	private StreamSupplier stream;
//	private boolean running;
//	private boolean shouldCheck;
//
//	public Vision(int deviceNumber) {
//		visionCam = new Webcam(deviceNumber, 640, 480, 30);
//		stream = new StreamSupplier(visionCam, 5800);
//		ledRing = new Solenoid(0);
//		runCounter = 0;
//		stage = 0;
//		done = false;
//		running = true;
//		shouldCheck = false;
//		Thread vision = new Thread(this);
//		vision.start();
//	}
//
//	@Override
//	public void run() {
//		while (running) {
//			if (shouldCheck) {
//				done = false;
//				switch (stage) {
//				case 0:
//					runCounter++;
//					ledRing.set(false);
//					stage++;
//					wholeTime = System.currentTimeMillis();
//					break;
//
//				case 1:
//					singleChannelNoLed = createSingleChannel(visionCam.getImage(), 2);
//					// Highgui.imwrite("/home/lvuser/noled" + runCounter +
//					// ".jpg",
//					// singleChannelNoLed);
//					ledRing.set(true);
//					startTime = System.currentTimeMillis();
//					stage++;
//					break;
//
//				case 2:
//					if (System.currentTimeMillis() - startTime >= 300) {
//						stage++;
//					}
//					break;
//
//				case 3:
//					singleChannelLed = createSingleChannel(visionCam.getImage(), 2);
//					// Highgui.imwrite("/home/lvuser/led" + runCounter + ".jpg",
//					// singleChannelLed);
//					ledRing.set(false);
//					stage++;
//					break;
//
//				case 4:
//					Core.subtract(singleChannelLed, singleChannelNoLed, singleChannelLed);
//					Imgproc.Canny(singleChannelLed, singleChannelLed, 200, 255);
//					stage++;
//					break;
//
//				case 5:
//					Imgproc.findContours(singleChannelLed, contours, hierarchy, Imgproc.RETR_TREE,
//							Imgproc.CHAIN_APPROX_NONE);
//					Imgproc.cvtColor(singleChannelLed, singleChannelLed, Imgproc.COLOR_GRAY2BGR);
//					stage++;
//					break;
//
//				case 6:
//					maxArea = -1;
//					maxContour = -1;
//					area = 0;
//					index = 0;
//					stage++;
//					break;
//
//				case 7:
//					if (contours.size() != 0) {
//						area = Imgproc.contourArea(contours.get(index));
//						if (area > maxArea) {
//							maxArea = area;
//							maxContour = index;
//						}
//						index++;
//						if (index >= contours.size())
//							stage++;
//					} else {
//						System.out.println("No contours found");
//						stage = 0;
//						done = false;
//					}
//					break;
//
//				case 8:
//					// Imgproc.drawContours(singleChannelLed, contours,
//					// maxContour, new
//					// Scalar(0, 0, 255), 1);
//					if (maxArea > 2000) {
//						Moments moments = Imgproc.moments(contours.get(maxContour));
//						center = new Point(moments.get_m10() / moments.get_m00(),
//								moments.get_m01() / moments.get_m00());
//						// Core.circle(singleChannelLed, center, 5, new
//						// Scalar(0, 0,
//						// 255), 1);
//					} else {
//						System.out.println("Large area not found: " + maxArea);
//						stage = 0;
//						done = false;
//					}
//					// Highgui.imwrite("/home/lvuser/out" + runCounter + ".jpg",
//					// singleChannelLed);
//					stage++;
//					break;
//
//				case 9:
//					centerDist.x = center.x - (visionCam.getWidth() / 2);
//					centerDist.y = -(center.y - (visionCam.getHeight() / 2));
//					stage++;
//					break;
//
//				case 10:
//					System.out.println("Vision: Done in " + (System.currentTimeMillis() - wholeTime) + " milliseconds");
//					contours.clear();
//					stage = 0;
//					done = true;
//					shouldCheck = false;
//					break;
//				}
//			} else {
//				try {
//					Thread.sleep(100);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//			}
//		}
//	}
//
//	public Point getPoint() {
//		shouldCheck = true;
//		while (!done) {
//		}
//		return centerDist;
//	}
//
//	public static Mat createSingleChannel(Mat m, int channel) {
//		List<Mat> channels = new ArrayList<Mat>();
//		Core.split(m, channels);
//		return channels.get(channel - 1);
//	}
//
//}