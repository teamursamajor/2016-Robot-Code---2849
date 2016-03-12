
package org.usfirst.frc.team2849.robot;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.IterativeRobot;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends IterativeRobot {
	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */

//	Joystick xbox = new Joystick(0);
//	boolean runningVision = false;
//	Point targetDist;
//	Vision vision;
//	Diagnostics diagnostic;
//	Talon talon;
	private DigitalInput breakBeam;
	private int counter;

	public void robotInit() {
//		System.load("/usr/local/lib/lib_OpenCV/java/libopencv_java2410.so");
//		vision = new Vision(0);
		// JFrame frame = new JFrame("Hello!");
		// frame.setVisible(true);
		breakBeam = new DigitalInput(0);
		counter = 0;
	}

	/**
	 * This function is called periodically during autonomous
	 */
	public void autonomousPeriodic() {

	}

	public void teleopInit() {

	}

	/**
	 * This function is called periodically during operator control
	 */
	public void teleopPeriodic() {
		System.out.println(breakBeam.get());
	}

	/**
	 * This function is called periodically during test mode
	 */
	public void testPeriodic() {

	}

}
