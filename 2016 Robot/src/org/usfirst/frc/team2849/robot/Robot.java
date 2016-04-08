package org.usfirst.frc.team2849.robot;

import org.usfirst.frc.team2849.robot.Drive.DriveLock;
import edu.wpi.first.wpilibj.IterativeRobot;

/**
 * Base class which handles all robot operations
 * 
 * @author teamursamajor
 */
public class Robot extends IterativeRobot {

	boolean shootToggle = true;
	
//	// Timing
	@SuppressWarnings("unused")
	private long timer;
//
	private Vision vision;
	public static XboxController xBox = new XboxController(0);

	// Create objects of various parts of the robot
	private StreamSupplier stream;
	private Diagnostics diagnostics;
	
	/**
	 * Run once when the robot is started, initializes various hardware devices
	 */
	public void robotInit() {
		System.load("/usr/local/lib/lib_OpenCV/java/libopencv_java2410.so");
		stream = new StreamSupplier(8500);
		Webcam visionCam = new Webcam(0, 640, 480, 5);
		vision = new Vision(visionCam, stream);
		stream.addWebcam(new Webcam(1, 320, 240, 5));
		diagnostics = new Diagnostics();
		Shooter.intitalizeShooter(vision, diagnostics);
	}

	public void autonomousInit(){
		Autonomous.autonomousInit();
	}
	
	/**
	 * Runs autonomous periodic tasks
	 */
	public void autonomousPeriodic() {
		Autonomous.runAutonomous();
		Arm.enablePID();
		diagnostics.logAutonomous();
	}

	/**
	 * Initializes the arm for teleop periodic
	 */
	public void teleopInit() {
		Drive.resetEnc();
		Arm.enablePID();
		timer = System.currentTimeMillis();
		Drive.unlock(DriveLock.AUTONOMOUS);
		diagnostics.logTeleop();
	}

	/**
	 * Run during the teleop period of the match -- 135 seconds Allows driver
	 * control of robot
	 */
	public void teleopPeriodic() {
//		System.out.println("Loop back to teleop took: " + (System.currentTimeMillis() - timer));
		timer = System.currentTimeMillis();

		// write data from the PD Board to a text file in /home/lvuser
//		diagnostics.writePDBoardData();
		
		// Allows climbing
		Climber.runClimber(xBox.getDPad(XboxController.POV_UP), xBox.getDPad(XboxController.POV_DOWN));

		// Drives robot
        Drive.runDrive(xBox.getAxis(XboxController.AXIS_LEFTSTICK_Y), xBox.getAxis(XboxController.AXIS_RIGHTSTICK_Y), DriveLock.UNLOCKED);
		Drive.runShifting(xBox.getButton(XboxController.BUTTON_RIGHTSTICK));

		// Allows arm raising / lowering
		Arm.runArm(xBox.getButton(XboxController.BUTTON_A), xBox.getButton(XboxController.BUTTON_Y));

		// Runs vision
		vision.autoAlign(xBox.getButton(XboxController.BUTTON_B));
		
		vision.autoDistance(xBox.getButton(XboxController.BUTTON_X));
		
		if (xBox.getButton(XboxController.BUTTON_START)) {
			vision.alignmentState = 7;
			vision.distanceState = 4;
			Drive.unlock(DriveLock.AUTOALIGN);
			Drive.unlock(DriveLock.AUTONOMOUS);
			Drive.unlock(DriveLock.SHOOTER);
			Drive.resetEnc();
			Arm.printEncoder();
		}
		
		if(xBox.getPOV() == xBox.POV_LEFT && shootToggle){
			Shooter.SHOOT_POWER -= .02;
			shootToggle = false;
			if(Shooter.SHOOT_POWER < 0) Shooter.SHOOT_POWER = 0;
			System.out.println("SHOOTER POWER = " + Shooter.SHOOT_POWER);
		} else if(xBox.getPOV() == xBox.POV_RIGHT && shootToggle){
			Shooter.SHOOT_POWER += .02;
			if(Shooter.SHOOT_POWER > 1) Shooter.SHOOT_POWER = 1;
			shootToggle = false;
			System.out.println("SHOOTER POWER = " + Shooter.SHOOT_POWER);
		} else if(xBox.getPOV() == xBox.POV_NONE){
			shootToggle = true;
		}

		// Allows the stream to be changed
		stream.runStreamChanger(xBox.getButton(XboxController.BUTTON_LEFTBUMPER),
				xBox.getButton(XboxController.BUTTON_RIGHTBUMPER));

//		// Runs shooter
		Shooter.runShooter(xBox.getAxisGreaterThan(XboxController.AXIS_LEFTTRIGGER, .25),
				xBox.getAxisGreaterThan(XboxController.AXIS_RIGHTTRIGGER, .25));
//
//		//Sucks in ball / stops the ball being sucked in
		Shooter.runIntake(xBox.getAxisGreaterThan(XboxController.AXIS_LEFTTRIGGER, .25));
		
		// low goal, runs while pressed
		Shooter.runLowGoal(xBox.getButton(XboxController.BUTTON_BACK));

//		System.out.println("Teleop took: " + (System.currentTimeMillis() - timer));
		timer = System.currentTimeMillis();
	}

	/**
	 * Runs when the robot is initially disabled
	 */
	public void disabledInit() {
		Arm.disablePID();
		diagnostics.logDisabled();
	}
	

}