package org.usfirst.frc.team2849.robot;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Talon;

/**
 * Runs the drive train Everything is static -- this class should never be
 * instantiated!!!
 * 
 * --FRC Team 2849 URSA MAJOR 2016 Season
 */
public class Drive {

	// Drive motors
	private static Talon left1 = new Talon(0);
	private static Talon left2 = new Talon(1);
	private static Talon right1 = new Talon(8);
	private static Talon right2 = new Talon(9);

	// Max speed of the drive motors
	private static final double MAX_SPEED = 1.0;

	// Lock on the drive motors
	private static DriveLock lock = DriveLock.UNLOCKED;

	// Solenoid that controls shifting
	private static Solenoid shifter = new Solenoid(7);

	// Current state of shifting
	private static boolean shiftState = false; // high is false, low is true
	private static int numShifts = 0;
	private static final int MAX_SHIFTS = 25;

	// return state of runDriveDist
	private static boolean toReturn = false;

	// Variable to control driving distance state machine
	private static int driveDistState = 0;

	// Variable to control shifting state machine
	private static int shiftingState = 0;

	// Encoders to measure drive motor distance
	public static Encoder lEnc = new Encoder(2, 3);
	public static Encoder rEnc = new Encoder(4, 5);

	// Ratio to enable correct distance whether in high or low gear
	private static double LOW_TO_HIGH_RATIO = 2.56;

	// Inverse driving for backwards driving
	public static boolean inverse;

	// Initialization
	static {
		lEnc.reset();
		rEnc.reset();
		lEnc.setDistancePerPulse(1);
		rEnc.setDistancePerPulse(1);
	}

	//ding dong the nate is dead
	/**
	 * Gets the distance in inches of the left encoder
	 * 
	 * @return Number of inches moved since reset
	 */
	public static double getLEnc() {
		return 0.14183089502 * lEnc.getDistance();
	}

	/**
	 * Gets the distance in inches of the right encoder
	 * 
	 * @return Number of inches moved since reset
	 */
	public static double getREnc() {
		return -0.06924656277 * rEnc.getDistance();
	}

	/**
	 * Resets both encoders
	 */
	public static void resetEnc() {
		lEnc.reset();
		rEnc.reset();
	}

	/**
	 * Returns the current lock on the drive train
	 * 
	 * @return lock on the drive train
	 */
	public static DriveLock getLockout() {
		return lock;
	}

	/**
	 * Handles locking control of the drive train
	 * 
	 * @param reqLock
	 *            the lock to potential set as the current lock
	 * @return whether the lock was successfully set
	 */
	public static boolean lock(DriveLock reqLock) {
		if (lock == DriveLock.UNLOCKED) {
			System.out.println("locking");
			lock = reqLock;
			return true;
		}
		return false;
	}

	/**
	 * Unlocks control of the drive train
	 * 
	 * @param reqLock
	 *            The current lock to make sure request to unlock is valid
	 * @return whether the drive train was unlocked
	 */
	public static boolean unlock(DriveLock reqLock) {
		if (lock == reqLock) {
			System.out.println("unlocking");
			lock = DriveLock.UNLOCKED;
			return true;
		}
		return false;
	}

	/**
	 * Shifts the gear from high to low or vice versa Returns the current
	 * shiftState -- high is true, low is false
	 *
	 * @return the current state of the supershifter
	 */
	public static boolean shiftGear() {
		if (numShifts >= MAX_SHIFTS)
			return shiftState;
		shifter.set(!shifter.get());
		shiftState = shifter.get();
		System.out.println("Shifts left: " + (MAX_SHIFTS - ++numShifts));
		return shiftState;
	}

	/**
	 * Shifts the gear to the input value
	 * 
	 * @param shiftToHigh
	 *            False - shifts to high gear <br>
	 *            True - shifts to low gear
	 */
	public static void shiftGear(boolean shiftToLow) {
		if (numShifts >= MAX_SHIFTS)
			return;
		if (MAX_SHIFTS - numShifts == 0)
			shifter.set(false);
		else {
			System.out.println("Shifts left: " + (MAX_SHIFTS - ++numShifts));
			shifter.set(shiftToLow);
			shiftState = shiftToLow;
		}
	}

	/**
	 * Returns the current shift state of the drive train
	 * 
	 * @return the current shift state
	 */
	public static boolean getShiftState() {
		return shiftState;
	}

	/**
	 * Periodic function to handle shifting of the robot <br>
	 * This should be called in a loop
	 * 
	 * @param shift
	 *            Button value to determine if shifting should occur
	 */
	public static void runShifting(boolean shift) {
		switch (shiftingState) {
		case 0:
			if (shift) {
				shiftGear();
				shiftingState++;
			}
			break;
		case 1:
			if (!shift)
				shiftingState = 0;
			break;
		}
	}

	/**
	 * Periodic function to handle driving -- scales the speed to fall from
	 * -1*MAXSPEED to MAXSPEED
	 * 
	 * @param leftSpeed
	 *            speed left drive should be run <br>
	 *            Precondition: between -1 and 1 inclusive
	 * @param rightSpeed
	 *            speed right drive should be run <br>
	 *            Precondition: between -1 and 1 inclusive
	 */
	public static void runDrive(double leftSpeed, double rightSpeed, DriveLock lockIn) {
		
		// Precondition check
		if (Math.abs(leftSpeed) > 1 || Math.abs(rightSpeed) > 1) {
			try {
				throw new Exception("IncorrectSpeedException");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		if (inverse) {
			double temp = leftSpeed;
			leftSpeed = -rightSpeed;
			rightSpeed = -temp;
		}

		int drivePow = 1;

		// Locking check
		if (lock == DriveLock.UNLOCKED || lock == lockIn) {
			leftSpeed *= -MAX_SPEED;
			rightSpeed *= -MAX_SPEED;

			// Deadzone
			if (leftSpeed > .1) {
				left1.set(-1 * Math.abs(Math.pow(leftSpeed, drivePow)));
				left2.set(-1 * Math.abs(Math.pow(leftSpeed, drivePow)));
			} else if (leftSpeed < -.1) {
				left1.set(Math.abs(Math.pow(leftSpeed, drivePow)));
				left2.set(Math.abs(Math.pow(leftSpeed, drivePow)));
			} else {
				left1.set(0);
				left2.set(0);
			}
			if (rightSpeed > .1) {
				right1.set(Math.abs(Math.pow(rightSpeed, drivePow)));
				right2.set(Math.abs(Math.pow(rightSpeed, drivePow)));
			} else if (rightSpeed < -.1) {
				right1.set(-1 * Math.abs(Math.pow(rightSpeed, drivePow)));
				right2.set(-1 * Math.abs(Math.pow(rightSpeed, drivePow)));
			} else {
				right1.set(0);
				right2.set(0);
			}
		}
	}

	/**
	 * Periodic function to handle distance driving <br>
	 * Should be run in a loop
	 * 
	 * @param leftSpeed
	 *            speed left motor should be run at Precondition: between -1 and
	 *            1 inclusive
	 * @param rightSpeed
	 *            speed right motor should be run at Precondition: between -1
	 *            and 1 inclusive
	 * @param leftDistance
	 *            distance left tread should travel in inches
	 * @param rightDistance
	 *            distance right tread should travel in inches
	 * @return whether the robot has gone the correct distance
	 */
	public static boolean runDriveDist(double leftSpeed, double rightSpeed, double leftDistance, double rightDistance,
			DriveLock lockIn) {

		leftSpeed *= .90;
		
		// Precondition check
		if (Math.abs(leftSpeed) > 1 || Math.abs(rightSpeed) > 1) {
			try {
				throw new Exception("IncorrectSpeedException");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		switch (driveDistState) {
		case 0:
			toReturn = false;
			resetEnc();
			driveDistState++;
			break;
		case 1:
			//			System.out.println(getLEnc() + " : " + leftDistance);
			//			System.out.println(getREnc() + " : " + rightDistance);
			if (Math.abs(getLEnc()) >= Math.abs(leftDistance) || Math.abs(getREnc()) >= Math.abs(rightDistance)) {
				rightSpeed = 0;
				leftSpeed = 0;
				driveDistState = 0;
				toReturn = true;
			}

			//			System.out.println("Left speed: " + leftSpeed + " : Right speed: " + rightSpeed);
			runDrive(leftSpeed, rightSpeed, lockIn);
			break;
		}

		return toReturn;
	}

	/**
	 * Enums for drive train locking
	 * 
	 * @author teamursamajor
	 */
	public enum DriveLock {
		SHOOTER, AUTOALIGN, AUTONOMOUS, UNLOCKED
	};

}