package punchline.vision;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.highgui.Highgui;
import org.opencv.highgui.VideoCapture;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;

public class Vision {
	/**
	 *
	 */

	public static long timer;

	static FileWriter fileWrite;
	static PrintWriter printLine;

	public static void main(String[] args) {
		//		System.loadLibrary(Core.NATIVE_LIBRARY_NAME); // load opencv library
		////		System.out.println("!");
		////		Mat imageNoled1 = Highgui.imread("images/sidenoled2.jpg", Highgui.CV_LOAD_IMAGE_COLOR);
		////		Mat imageLed1 = Highgui.imread("images/slightoff.jpg", Highgui.CV_LOAD_IMAGE_COLOR);
		//		timer = System.currentTimeMillis();
		//		Mat singleChannel = Highgui.imread("images/test.jpg");
		//		Imgproc.Canny(singleChannel, singleChannel, 200, 255);
		//		List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
		//		Mat hierarchy = new Mat();
		//		Imgproc.findContours(singleChannel, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_NONE);
		//		Imgproc.cvtColor(singleChannel, singleChannel, Imgproc.COLOR_GRAY2BGR);
		//		double maxArea = -1;
		//		int maxContour = -1;
		//		double area = 0;
		//		for (int i = 0; i < contours.size(); i++) {
		//			area = Imgproc.contourArea(contours.get(i));
		//			if (area > maxArea) {
		//				maxArea = area;
		//				maxContour = i;
		//			}
		//		}
		//		
		//		Imgproc.drawContours(singleChannel, contours, maxContour, new Scalar(0, 0, 255), 1);
		//		Moments moments = Imgproc.moments(contours.get(maxContour));
		//		Core.circle(singleChannel,
		//				new Point(moments.get_m10() / moments.get_m00(), moments.get_m01() / moments.get_m00()), 5,
		//				new Scalar(0, 0, 255), 1);
		//		System.out.println(System.currentTimeMillis() - timer);
		//		new ImageLoader(singleChannel, "images/out.jpg");
		//	}
		//
		//	public static Mat createSingleChannel(Mat m, int channel) { // isolate a
		//																// single
		//																// channel
		//		List<Mat> channels = new ArrayList<Mat>();
		//		Core.split(m, channels);
		//		return channels.get(channel - 1);
		//	}
		//
		//	public static int[] findEdges(Mat m) {
		//		boolean firstPoint = true;
		//		int[] bounds = new int[4]; // minx, miny, maxx, maxy
		//		for (int y = 0; y < m.rows(); y++) {
		//			for (int x = 0; x < m.cols(); x++) {
		//				if (m.get(y, x)[0] == 255 && firstPoint) {
		//					bounds[0] = x;
		//					bounds[1] = y;
		//					bounds[2] = x;
		//					bounds[3] = y;
		//					firstPoint = false;
		//				} else if (m.get(y, x)[0] == 255) {
		//					if (x < bounds[0])
		//						bounds[0] = x;
		//					if (y < bounds[1])
		//						bounds[1] = y;
		//					if (x > bounds[2])
		//						bounds[2] = x;
		//					if (y > bounds[3])
		//						bounds[3] = y;
		//				}
		//			}
		//		}
		//		return bounds;
		//
		//	}
		//
		//	public static Point findCenter(int[] bounds) {
		//		Point center = new Point(((bounds[2] - bounds[0]) / 2) + bounds[0], ((bounds[3] - bounds[1]) / 2) + bounds[1]);
		//		return center;
		//	}
		//
		//	public static Mat averageMat(Mat[] noled, Mat[] led) {
		//		Mat avg = Mat.zeros(noled[0].size(), noled[0].type());
		//		Mat tempSub = new Mat(noled[0].size(), noled[0].type());
		//		if (noled.length == led.length) {
		//			for (int i = 0; i < noled.length; i++) {
		//				Core.subtract(led[i], noled[i], tempSub);
		//				Core.add(tempSub, avg, avg);
		//			}
		//		}
		//		Core.divide(avg, new Scalar(noled.length), avg);
		//		return avg;
		//	}
		//
		//	public static Point findHighestIntensity(Mat m) {
		//		Point maxPoint = new Point();
		//		double maxIntensity = -1;
		//		for (int i = 0; i < m.rows(); i++) {
		//			for (int j = 0; j < m.cols(); j++) {
		//				if (m.get(i, j)[0] > maxIntensity) {
		//					maxIntensity = m.get(i, j)[0];
		//					maxPoint.set(new double[] { j, i });
		//				}
		//			}
		//		}
		//		return maxPoint;
		//	}
		//
		//	// public static Mat createGrayscale(Mat m) { // convert a single channel to
		//	// a grayscale image
		//	// Mat gray = new Mat(m.rows(), m.cols(), CvType.CV_8UC1);
		//	// for (int i = 0; i < m.rows(); i++) {
		//	// for (int j = 0; j < m.cols(); j++) {
		//	// gray.put(i, j, m.get(i, j));
		//	// }
		//	// }
		//	// return gray;
		//	// }
	}
}