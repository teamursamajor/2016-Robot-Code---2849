package org.usfirst.frc.team2849.robot;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;
import org.usfirst.frc.team2849.robot.Drive.DriveLock;

import edu.wpi.first.wpilibj.Solenoid;

/**
 * Super cool vision code by Punchline Thanks to Nate for making it not horrible
 * and Sheldon for making it legible. Thanks to Abhi for commenting
 * "Abhi was here" a few times. I'd like to thank the Academy, my family,
 * friends, and Director Joss Whedon. Vision will be coming back in
 * "Vision II: Electric Boogaloo" in theaters January 2017
 */
public class Vision implements Runnable {

	// LED ring controlled by a solenoid
	private Solenoid ledRing;

	// Webcamera for vision
	private Webcam visionCam;

	// Semaphore to synchronize
	private Semaphore sem;

	// Mats for vision
	private Mat singleChannelLed = new Mat();
	private Mat singleChannelNoLed = new Mat();
	private Mat hierarchy = new Mat();

	// Points for center calculations
	private Point center = new Point(Double.NaN, Double.NaN);
	private Point centerDist = new Point(Double.NaN, Double.NaN);

	// Contours
	private List<MatOfPoint> contours = new ArrayList<MatOfPoint>();

	// Channels
	private static List<Mat> channels = new ArrayList<Mat>();

	// Is thread running?
	private boolean running;

	// Variable if vision should be run 
	private boolean shouldCheck;

	// Is vision done?
	private boolean done;

	// Is the robot aligned?
	private AutoAlignCodes aligned;

	// Number of fails (bad)
	private int failCounter;

	// Max contour size
	private int maxContour;

	// State machine variables
	private int visionState;
	private int alignmentState;

	// Timing of vision 
	private long wholeTime;

	// Area calculations
	private double maxArea;
	private double area;

	// Alignment angle
	private double turnAngle;

	// Shooting threshold stuff
	private final int AREA_THRESH = 800;
	private final double ANGLE_TO_ENCODER = 24 * Math.PI;
	private final double CAM_TO_CENTER = 12.5;
	private final double OPTIMAL_SHOOTING_DIST = 92; // in inches!!!
	//	private final double X_TARGET_MIN = -50;
	//	private final double X_TARGET_MAX = 50;
	//	private final double Y_TARGET_MIN = -50;
	//	private final double Y_TARGET_MAX = 50;
	private final double MAX_DISTANCE = 4;
//	private final double MAX_ANGLE = 10;
	private final double MAX_ANGLE = 10;
	private final double X_OFFSET = 0;

	private int runCounter = 0;

	/**
	 * Initialization for vision and creates a new thread for running vision
	 * 
	 * @param visionCam
	 *            Webcam for vision
	 * @param stream
	 *            Stream for webcams to add the vision cam to
	 */
	public Vision(Webcam visionCam, StreamSupplier stream) {
		this.visionCam = visionCam;
		stream.addWebcam(visionCam);
		ledRing = new Solenoid(0);
		failCounter = 0;
		sem = new Semaphore(1);
		aligned = AutoAlignCodes.STANDBY;
		done = false;
		running = true;
		shouldCheck = false;
		visionState = 0;
		Thread vision = new Thread(this);
		vision.start();
	}

	/**
	 * Started when initialized, while loop keeps it running continuously When
	 * vision is started, it gets a picture and processes it then stores the
	 * result to be gotten from getPoint()
	 */
	@Override
	public void run() {
		while (running) {
			if (shouldCheck) {
				center = new Point(Double.NaN, Double.NaN);
				centerDist = new Point(Double.NaN, Double.NaN);

				//Takes picture w/LED off, starts timing process
				ledRing.set(false);
				wholeTime = System.currentTimeMillis();
				singleChannelNoLed = createSingleChannel(visionCam.getImage(), 2);

				//turns on LED ring, waits a bit (it takes a while) and takes another image
				ledRing.set(true);
				try {
					Thread.sleep(351);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				singleChannelLed = createSingleChannel(visionCam.getImage(), 2);
				sleep(5);

				//Turns the LED ring off, subtracts the two images
				ledRing.set(false);
				Core.subtract(singleChannelLed, singleChannelNoLed, singleChannelLed);
				sleep(5);

				//More image processing -- finds contour of goal
				Imgproc.Canny(singleChannelLed, singleChannelLed, 200, 255);
				sleep(5);
				Imgproc.findContours(singleChannelLed, contours, hierarchy, Imgproc.RETR_TREE,
						Imgproc.CHAIN_APPROX_NONE);
				sleep(5);
				Imgproc.cvtColor(singleChannelLed, singleChannelLed, Imgproc.COLOR_GRAY2BGR);
				sleep(5);

				//If no contours, end processing
				if (contours.size() == 0) {
					System.out.println("No contours found");
					try {
						sem.acquire();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					done = true;
					shouldCheck = false;
					sem.release();
				}

				//Look for contours
				maxArea = -1;
				maxContour = -1;
				area = 0;
				for (int i = 0; i < contours.size(); i++) {
					area = Imgproc.contourArea(contours.get(i));
					if (area > maxArea) {
						maxArea = area;
						maxContour = i;
					}
					sleep(0);
				}

				Imgproc.drawContours(singleChannelLed, contours, maxContour, new Scalar(0, 0, 255), 1);

				//If the area is too small, then end processing
				if (maxArea > AREA_THRESH) {
					Moments moments = Imgproc.moments(contours.get(maxContour));
					sleep(5);
					center = new Point(moments.get_m10() / moments.get_m00(), moments.get_m01() / moments.get_m00());
				} else {
					System.out.println("Large area not found: " + maxArea);
					try {
						sem.acquire();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					done = true;
					shouldCheck = false;
					sem.release();
				}
				Highgui.imwrite("/home/lvuser/out" + runCounter + ".jpg", singleChannelLed);
				runCounter++;
				sleep(5);

				Core.circle(singleChannelLed, center, 10, new Scalar(0, 0, 255));
				sleep(5);

				//Find center coords of the contour
				centerDist.x = center.x - (visionCam.getWidth() / 2);
				centerDist.y = -(center.y - (visionCam.getHeight() / 2));

				//Prints time for the processing
				System.out.println("Vision: Done in " + (System.currentTimeMillis() - wholeTime) + " milliseconds");
				contours.clear();

				//Finishes
				try {
					sem.acquire();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				done = true;
				shouldCheck = false;
				sem.release();
			} else {
				//If not processing, then sleep
				try {
					Thread.sleep(25);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Stops vision from running
	 */
	public void stop() {
		running = false;
	}

	/**
	 * Gets the center of the contour only call if vision is done
	 * 
	 * @return the center point
	 */
	public Point getPoint() {
		return centerDist;
	}

	/**
	 * Tells if vision is done
	 * 
	 * @return whether vision is done
	 */
	public boolean isDone() {
		return done;
	}

	/**
	 * Starts vision running
	 */
	public void startVision() {
		try {
			sem.acquire();
		} catch (InterruptedException e) {

		}
		shouldCheck = true;
		done = false;
		sem.release();
	}

	/**
	 * Periodic function for running vision
	 * 
	 * @param button
	 *            Button value to run vision
	 * @return if done
	 */
	public boolean runVision(boolean button) {
		switch (visionState) {
		case 0:
			if (button) {
				visionState++;
			}
			break;
		case 1:
			startVision();
			visionState++;
			break;
		case 2:
			if (done) {
				System.out.println(centerDist);
				System.out.println("Approx distance is: " + findDistance(centerDist.y));
				System.out.println(findAngle(centerDist.x, centerDist.y));
				visionState++;
			}
			break;
		case 3:
			if (!button) {
				visionState = 0;
			}
			break;
		}
		return done;
	}

	/**
	 * Auto-align code for vision
	 * 
	 * @param button
	 *            Button value to run auto-align
	 * @return whether the robot is aligned
	 */
	public enum AutoAlignCodes { RUNNING, ALIGNED, FAILED, STANDBY };
	
	public AutoAlignCodes autoAlign(boolean button) {
		aligned = AutoAlignCodes.RUNNING;
		switch (alignmentState) {
		case 0:
			if (button) {
				failCounter = 0;
				alignmentState++;
			}
			break;
		case 1:
			Drive.lock(DriveLock.AUTOALIGN);
//			Drive.shiftGear(false);
			startVision();
			alignmentState++;
			break;
		case 2:
			if (done) {
				if (Double.isNaN(centerDist.x) && Double.isNaN(centerDist.y)) { // if vision failed
					// turn clockwise, do vision again, if it fails a second time, rumble and exit
					failCounter++;
					if (failCounter >= 2) {
//						Robot.xBox.rumbleFor(250);
						alignmentState = 0;
						aligned = AutoAlignCodes.FAILED;
						Drive.unlock(DriveLock.AUTOALIGN);
					} else if (Drive.runDriveDist(.5, -.5, 10, -10, DriveLock.AUTOALIGN)) {
						alignmentState = 1;
					}
				} else {
					turnAngle = findAngle(centerDist.x, centerDist.y);
					alignmentState++;
				}
			}
			break;
		case 3:
			// Turning the robot towards the goal
			if (Drive.runDriveDist(-.35 * Math.signum(centerDist.x), .35 * Math.signum(centerDist.x),
					-convertAngleToEncoder(turnAngle), convertAngleToEncoder(turnAngle), DriveLock.AUTOALIGN)) {
				alignmentState++;
			}
			break;
		case 4: // Fix distance
			double distToGo = findDistance(centerDist.y) - OPTIMAL_SHOOTING_DIST;
			if (Drive.runDriveDist(-.9 * Math.signum(distToGo), -.9 * Math.signum(distToGo), distToGo, distToGo,
					DriveLock.AUTOALIGN)) {
				alignmentState++;
				sleep(500);
			}
			break;
		case 5:
			startVision(); // check again for alignment
			alignmentState++;
			break;
		case 6:
			if (done) {
				distToGo = findDistance(centerDist.y) - OPTIMAL_SHOOTING_DIST;
				turnAngle = findAngle(centerDist.x, centerDist.y);

				if (!Double.isNaN(centerDist.x) && !Double.isNaN(centerDist.y)) { // check if not null and in range for shooting
					if (Math.abs(turnAngle) <= MAX_ANGLE && Math.abs(distToGo) <= MAX_DISTANCE) {
						alignmentState++;
					} else { // run again
						alignmentState = 2;
					}
				} else {
					failCounter++;
					if (failCounter >= 2) {
//						Robot.xBox.rumbleFor(250);
						alignmentState = 0;
						aligned = AutoAlignCodes.FAILED;
						Drive.unlock(DriveLock.AUTOALIGN);
					} else {
						alignmentState = 5;
					}
				}
			}
			break;
		case 7:
			alignmentState = 0;
			aligned = AutoAlignCodes.ALIGNED;
			Drive.unlock(DriveLock.AUTOALIGN);
			break;
		}
		return aligned;
	}

	/**
	 * Splits a Mat into a single channel
	 * 
	 * @param m
	 *            Mat to split
	 * @param channel
	 *            Channel to get
	 * @return the single channel
	 */
	public static Mat createSingleChannel(Mat m, int channel) {
		Core.split(m, channels);
		return channels.get(channel - 1);
	}

	/**
	 * Finds the floor distance from the pixel height
	 * 
	 * @param heightDifference
	 *            Robot height from the ground
	 * @return distance in pixel height
	 */
	public double findDistance(double heightDifference) { // super cool function made from benchmark data
		double a = 105.46, b = -0.36378, c = 0.00020747, d = 0.0000072364, e = 1.2298 * Math.pow(10, -8),
				f = -6.0022 * Math.pow(10, -10), g = 5.8114 * Math.pow(10, -13), h = 8.6453 * Math.pow(10, -15);
		//		System.out.println("Height = " + heightDifference + " and dist = "
		//				+ (a * Math.pow(heightDifference, 3) + b * Math.pow(heightDifference, 2) + c * heightDifference + d));
		return a + b * heightDifference + c * Math.pow(heightDifference, 2) + d * Math.pow(heightDifference, 3)
				+ e * Math.pow(heightDifference, 4) + f * Math.pow(heightDifference, 5)
				+ g * Math.pow(heightDifference, 6) + h * Math.pow(heightDifference, 7);
	}

	/**
	 * Angle robot is at relative to target
	 * 
	 * @param xDifference
	 *            Difference in pixel distance
	 * @return Angle the angle
	 */
	public double findAngle(double xDifference, double yDifference) {

		double absXDifference = Math.abs(xDifference);
		double camAngle = Math.toRadians((30.0 / visionCam.getWidth()) * absXDifference) + X_OFFSET;
		double distance = findDistance(yDifference);
		double IT;
//		System.out.println("Cam Angle: " + camAngle + " xdiff: " + xDifference + " sign: " + Math.signum(xDifference));
//		switch ((int) Math.signum(xDifference + X_OFFSET)) {
//		case 1:
//			return 90 - Math
//					.toDegrees(Math.atan(distance / (distance * Math.tan(Math.toRadians(camAngle)) + CAM_TO_CENTER)));
//		case -1:
//			return -Math
//					.toDegrees(Math.atan((distance * Math.tan(Math.toRadians(camAngle)) - CAM_TO_CENTER) / distance));
//		default:
//			return 0;
//		}
		System.out.println(distance*Math.tan(camAngle) + " : " + camAngle);
		if (xDifference > 0) {
			IT = distance*Math.tan(camAngle) + CAM_TO_CENTER;
			System.out.println("Case 1");
		} else { 
 			if (distance*Math.tan(camAngle) <= CAM_TO_CENTER) {
				IT = CAM_TO_CENTER - distance*Math.tan(camAngle);
				System.out.println("Case 2");
			} else {
				IT = distance*Math.tan(camAngle) - CAM_TO_CENTER;
				System.out.println("Case 3");
			}
		}
		return -Math.toDegrees(Math.atan(IT/distance));
	}

	public void sleep(int timeToSleep) {
		try {
			Thread.sleep(timeToSleep);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public double convertAngleToEncoder(double angle) {
		return ANGLE_TO_ENCODER * (angle / 360);
	}

}