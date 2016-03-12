package org.usfirst.frc.team2849.robot;

import org.usfirst.frc.team2849.robot.Drive.DriveLock;
import edu.wpi.first.wpilibj.IterativeRobot;

/**
 * Base class which handles all robot operations
 * 
 * @author teamursamajor
 */
public class Robot extends IterativeRobot {

//	// Timing
	@SuppressWarnings("unused")
	private long timer;
//
	private Vision vision;
	public static XboxController xBox = new XboxController(0);

	// Create objects of various parts of the robot
	private StreamSupplier stream;
	
	/**
	 * Run once when the robot is started, initializes various hardware devices
	 */
	public void robotInit() {
		System.load("/usr/local/lib/lib_OpenCV/java/libopencv_java2410.so");
		stream = new StreamSupplier(8500);
		Webcam visionCam = new Webcam(0, 640, 480, 5);
		vision = new Vision(visionCam, stream);
		Shooter.intitalizeShooter(vision);
		stream.addWebcam(new Webcam(1, 320, 240, 5));
		stream.addWebcam(new Webcam(2, 320, 240, 5));
		
	}

	/**
	 * Runs autonomous periodic tasks
	 */
	public void autonomousPeriodic() {
		Autonomous.runAutonomous();
	}

	/**
	 * Initializes the arm for teleop periodic
	 */
	public void teleopInit() {
		Drive.resetEnc();
		Arm.enablePID();
		timer = System.currentTimeMillis();
		Drive.unlock(DriveLock.AUTONOMOUS);
	}

	/**
	 * Run during the teleop period of the match -- 135 seconds Allows driver
	 * control of robot
	 */
	public void teleopPeriodic() {
//		System.out.println("Loop back to teleop took: " + (System.currentTimeMillis() - timer));
		timer = System.currentTimeMillis();

		// Allows climbing
		Climber.runClimber(xBox.getDPad(XboxController.POV_UP), xBox.getDPad(XboxController.POV_DOWN));

		// Drives robot
        Drive.runDrive(xBox.getAxis(XboxController.AXIS_LEFTSTICK_Y), xBox.getAxis(XboxController.AXIS_RIGHTSTICK_Y), DriveLock.UNLOCKED);
		Drive.runShifting(xBox.getButton(XboxController.BUTTON_RIGHTSTICK));

		// Allows arm raising / lowering
		Arm.runArm(xBox.getButton(XboxController.BUTTON_A), xBox.getButton(XboxController.BUTTON_Y));

		// Runs vision
		vision.autoAlign(xBox.getButton(XboxController.BUTTON_B));
		
		vision.runVision(xBox.getButton(XboxController.BUTTON_X));

		// Allows the stream to be changed
		stream.runStreamChanger(xBox.getButton(XboxController.BUTTON_LEFTBUMPER),
				xBox.getButton(XboxController.BUTTON_RIGHTBUMPER));

//		// Runs shooter
		Shooter.runShooter(xBox.getAxisGreaterThan(XboxController.AXIS_LEFTTRIGGER, .25),
				xBox.getAxisGreaterThan(XboxController.AXIS_RIGHTTRIGGER, .25));
//
//		//Sucks in ball / stops the ball being sucked in
		Shooter.runIntake(xBox.getAxisGreaterThan(XboxController.AXIS_LEFTTRIGGER, .25),
				xBox.getAxisGreaterThan(XboxController.AXIS_RIGHTTRIGGER, .25));

//		System.out.println("Teleop took: " + (System.currentTimeMillis() - timer));
		timer = System.currentTimeMillis();
	}

	/**
	 * Runs when the robot is initially disabled
	 */
	public void disabledInit() {
		Arm.disablePID();
	}

}