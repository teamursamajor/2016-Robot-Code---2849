package org.usfirst.frc.team2849.robot;

import org.usfirst.frc.team2849.robot.Drive.DriveLock;
import org.usfirst.frc.team2849.robot.Vision.AutoAlignCodes;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Talon;

/**
 * Class which handles all shooter functions
 * 
 * @author teamursamajor
 */
public class Shooter {
	
	private static final int SPINUP_TIME = 1000;
	private static final int SHOOT_TIME = 1000;

	public static double SHOOT_POWER = .9;
	
	// Motors for shooter
	private static Talon intakeWheel = new Talon(4);
	private static Talon shooterWheel = new Talon(5);

	// Variable used for timing
	private static long startTime;

	// Variable to handle shooter state machine
	private static int shooterStage = 0;
	private static int autoShooterStage = 0;
	private static int stage = 0;

	// Variable to determine if shooting is done
	private static boolean shootDone = true;

	// Break beam sensors
	private static DigitalInput intakeBeam = new DigitalInput(0);
	private static DigitalInput shootBeam = new DigitalInput(1);

	// Benchmarks for shooting
	//private static final double BENCHMARK_DISTANCE = Double.NaN; //in inches
	//private static final double BENCHMARK_HEIGHT = Double.NaN; // in pixels

	// Vision object
	private static Vision vision;

	/**
	 * Initialize shooter object
	 * 
	 * @param vision
	 *            Vision object
	 */
	public static void intitalizeShooter(Vision visionIn) {
		vision = visionIn;
	}

	/**
	 * Periodic function for basic shooter operation based on triggers
	 * 
	 * @param leftTrigger
	 *            Trigger value (makes sure inverse shooting is not running)
	 * @param rightTrigger
	 *            Trigger value (actually for shooting)
	 */
	public static void runShooter(boolean leftTrigger, boolean rightTrigger) {
		switch (stage) {
		case 0:
			if (rightTrigger && !leftTrigger) {
				stage++;
			}
			break;
		case 1:
			if (shoot()) {
				stage++;
				Robot.xBox.rumbleFor(250);
			}
			break;
		case 2:
			if (!rightTrigger) {
				stage = 0;
				shooterWheel.set(0);
			}
			break;
		}
	}

	/**
	 * Periodic function to actually shoot
	 * 
	 * @return whether shooting is done
	 */
	public static boolean shoot() {
		shootDone = false;
		switch (shooterStage) {
		case 0:
			Drive.lock(DriveLock.SHOOTER);
			startTime = System.currentTimeMillis();
			Arm.toPosition(Arm.ArmCodes.FLAT);
			intakeWheel.set(-1);
			shooterStage++;
			break;
		case 1:
			if (shootBeam.get()) {
				shooterStage++;
				intakeWheel.set(0);
			}
			if (System.currentTimeMillis() - startTime > 4000) {
				intakeWheel.set(0);
				shooterStage = 6;
			}
			break;
		case 2:
			shooterWheel.set(SHOOT_POWER); // speed up shooter
			startTime = System.currentTimeMillis();
			shooterStage++;
			break;
		case 3:
			if (System.currentTimeMillis() - startTime >= SPINUP_TIME) { // wait for shooter to speed up
				shooterStage++;
			}
			break;
		case 4:
			intakeWheel.set(1); // send ball to shooter
			shooterStage++;
			startTime = System.currentTimeMillis();
			break;
		case 5:
			if (System.currentTimeMillis() - startTime >= SHOOT_TIME)
				shooterStage++;
			break;
		case 6:
			shooterWheel.set(0); // wait until ball is shot, then stop
			shootDone = true;
			Drive.unlock(DriveLock.SHOOTER);
			shooterStage = 0;
			Drive.shiftGear(false);
			break;
		}
		return shootDone;
	}

	/**
	 * Periodic function to handle advanced shooting
	 * 
	 * @return whether shooting is done
	 */
	
	public static boolean assistedShoot(boolean button) { // NEEDS TESTING TO TIME CORRECTLY
		switch (autoShooterStage) {
		case 0:
			if(button){
				shootDone = false;
				autoShooterStage++;
			}
			break;
		case 1:
			AutoAlignCodes algn = vision.autoAlign(true);
			if(algn == AutoAlignCodes.ALIGNED){
				Drive.lock(DriveLock.SHOOTER);
				autoShooterStage++;
			} else if(algn == AutoAlignCodes.FAILED){
				autoShooterStage = 0;
				Drive.unlock(DriveLock.SHOOTER);
				shootDone = true;
				Robot.xBox.rumbleFor(250);
			}
			break;
		case 2:
			if (shoot()) {
				autoShooterStage = 0;
				Drive.unlock(DriveLock.SHOOTER);
				shootDone = true;
				Robot.xBox.rumbleFor(250);
			}
			break;
		}
		return shootDone;
	}

	/**
	 * Periodic function to run the intake system
	 * 
	 * @param leftTrigger
	 *            Trigger value (actually intake)
	 * @param rightTrigger
	 *            Trigger value (runs intake backwards if both pressed)
	 */
	public static void runIntake(boolean leftTrigger, boolean rightTrigger) {
		if (leftTrigger) {
			if (rightTrigger) {
				intakeWheel.set(-1);
			} else if (!intakeBeam.get()) {
				Robot.xBox.rumbleFor(10);
				intakeWheel.set(0);
			} else {
				intakeWheel.set(1);
			}
		} else if (shootDone) {
			intakeWheel.set(0);
		}
	}

	/**
	 * Returns the intake beam
	 * 
	 * @return whether intake beam tripped
	 */
	public static boolean getIntakeBeam() {
		return intakeBeam.get();
	}

	/**
	 * Returns the shooter beam
	 *  
	 * @return whether shooter beam tripped
	 */
	public static boolean getShooterBeam() {
		return shootBeam.get();
	}

}