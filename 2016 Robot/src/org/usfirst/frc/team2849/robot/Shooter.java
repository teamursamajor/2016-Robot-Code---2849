package org.usfirst.frc.team2849.robot;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

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

	public static double SHOOT_POWER = 0.905;
	private static double highestCurrent = 0.0;
	private static double current;
	private static double highestIndex = 0.0;

	// Motors for shooter
	private static Talon intakeWheel = new Talon(4);
	private static Talon shooterWheel = new Talon(5);

	// Variable used for timing
	private static long startTime;

	// Variable to handle shooter state machine
	private static int shooterStage = 0;
	private static int autoShooterStage = 0;
	private static int stage = 0;
	private static int readyStage = 0;
	private static int toIgnore = 20;

	// Variable to determine if shooting is done
	private static boolean shootDone = true;
	private static boolean readyBallDone = true;
	private static boolean toPrint = false;

	// Break beam sensors
	private static DigitalInput intakeBeam = new DigitalInput(0);
	private static DigitalInput shootBeam = new DigitalInput(1);

	// Benchmarks for shooting
	//private static final double BENCHMARK_DISTANCE = Double.NaN; //in inches
	//private static final double BENCHMARK_HEIGHT = Double.NaN; // in pixels

	// Vision object
	private static Vision vision;

	private static List<Double> currents = new ArrayList<Double>();

	private static Diagnostics diag;

	/**
	 * Initialize shooter object
	 * 
	 * @param vision
	 *            Vision object
	 */
	public static void intitalizeShooter(Vision visionIn, Diagnostics d) {
		vision = visionIn;
		diag = d;
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
			if (diag.getVoltage() < 12) {
				shooterWheel.set(SHOOT_POWER + .04); // speed up shooter
			} else if (diag.getVoltage() < 12.3) {
				shooterWheel.set(SHOOT_POWER + .02);
			} else {
				shooterWheel.set(SHOOT_POWER);
			}
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
	 * Runs the intake backwards to reach the low goals
	 * 
	 * @param button
	 *            boolean value of button
	 */
	public static void runLowGoal(boolean button) {
		if (button) {
			//			if (toIgnore != 0) {
			//				toIgnore--;
			//			} else {
			current = diag.getCurrent(12);
			currents.add(current);
			if (current > highestCurrent) {
				highestCurrent = current;
				highestIndex = (double) currents.size();
				//				}
			}
			intakeWheel.set(-1);
			shooterWheel.set(-.75);
			toPrint = true;
		} else if (shootDone) {
//			intakeWheel.set(0);
			shooterWheel.set(0);
			if (toPrint) {
				currents.forEach(d -> SHOOT_POWER += d);
				SHOOT_POWER /= currents.size();
				System.out.println("Average current is " + SHOOT_POWER);
				System.out.println("Peak current is " + highestCurrent);
				System.out.println("Peak occurred at " + ((highestIndex / currents.size()) * 100) + "% through run");
				diag.writeTimeStamp();
				diag.writeDiagnostic(currents.toString());
				highestIndex = 0;
				highestCurrent = 0.0;
				toIgnore = 20;
				toPrint = false;
				currents.clear();
			}
		}
	}

	/**
	 * Periodic function to handle advanced shooting
	 * 
	 * @return whether shooting is done
	 */

	public static boolean assistedShoot(boolean button) { // NEEDS TESTING TO TIME CORRECTLY
		switch (autoShooterStage) {
		case 0:
			if (button) {
				shootDone = false;
				autoShooterStage++;
			}
			break;
		case 1:
			AutoAlignCodes algn = vision.autoAlign(true);
			if (algn == AutoAlignCodes.ALIGNED) {
				Drive.lock(DriveLock.SHOOTER);
				autoShooterStage++;
			} else if (algn == AutoAlignCodes.FAILED) {
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
	static double hnum = -Double.MIN_VALUE;
	public static void runIntake(boolean leftTrigger) {
		if (leftTrigger) {
			if (!intakeBeam.get()) {
				Robot.xBox.rumbleFor(10);
				intakeWheel.set(0);
			} else {
				intakeWheel.set(1);
				if(diag.getCurrent(12) > hnum) hnum = diag.getCurrent(12);
				System.out.println(hnum);
			}
		} else if (shootDone) {
			intakeWheel.set(0);
			hnum = -Double.MIN_VALUE;
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

	/**
	 * Retreats the ball and winds up the shooter motor
	 * 
	 * @return whether the method is complete
	 */

	public static boolean runReadyBall() { // because the old name was too long
		switch (readyStage) {
		case 0:
			intakeWheel.set(-1);
			readyBallDone = false;
			readyStage++;
			break;
		case 1:
			if (shootBeam.get()) {
				intakeWheel.set(0);
				readyStage++;
			} else {
//				currents.add(diag.getCurrent(15));
			}
			break;
		case 2:
//			currents.forEach(d -> SHOOT_POWER += d);
//			SHOOT_POWER /= currents.size();
//			currents.clear();
//			shooterWheel.set(SHOOT_POWER); // begin charging spirit bomb
			shooterWheel.set(1);
			startTime = System.currentTimeMillis();
			readyStage++;
			break;
		case 3:
			if (System.currentTimeMillis() - startTime > SPINUP_TIME)
				readyStage++;
		case 4:
			readyBallDone = true;
			readyStage = 0;
			break;
		}
		return readyBallDone;
	}
}