package org.usfirst.frc.team2849.robot;

import edu.wpi.first.wpilibj.AnalogPotentiometer;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PIDOutput;
import edu.wpi.first.wpilibj.Talon;

/***
 * This class is responsible for handling control of the obstacle arm on the
 * robot
 * 
 * @author teamursamajor
 */

public class Arm {

	// Drive motors for the arm
	private static Talon armLeft = new Talon(6);
	private static Talon armRight = new Talon(3);

	// Potentiometer to determine the position of the arm
//	private static AnalogPotentiometer pot = new AnalogPotentiometer(3, 360, -300);
	private static Encoder armEncoder = new Encoder(6,7);

	// PID Controller to handle the movement of the arm
	private static PIDController pidControl;

	// PID Controller variables
	private static final double PROPORTIONAL = .001;
	private static final double INTEGRAL = 0;
	private static final double DERIVATIVE = 0;

	// Position arm should be moving to
	private static int armPos = 3;

	// Variable to handle button press state machine
	private static int armState = 0;

	// Initialize the PID Controller
	static {
		pidControl = new PIDController(PROPORTIONAL, INTEGRAL, DERIVATIVE, armEncoder, new PIDOutput() {
			@Override
			public void pidWrite(double output) {
//				System.out.println("PID is outputting: " + output);
				armLeft.set(output);
				armRight.set(output);
			}
		});
		pidControl.setAbsoluteTolerance(.1);
		armEncoder.reset();
	}

	/**
	 * Disable the PID Controller
	 */
	public static void disablePID() {
		pidControl.disable();
	}

	/**
	 * Enable the PID Controller
	 */
	public static void enablePID() {
		pidControl.enable();
	}

	/**
	 * Periodic funciton to handle arm movement
	 * 
	 * Should be run in a loop
	 * 
	 * @param armDown
	 *            Boolean for a button to signal arm should move down a position
	 * @param armU
	 *            Boolean for a button to signal arm should move up a position
	 */
	public static void runArm(boolean armDown, boolean armUp) {
//		System.out.println("Trying to get to: " + ArmCodes.POSTITIONS[armPos]);
//		System.out.println("Arm is at : " + pot.get());
		switch (armState) {
		case 0:
			if (armDown) {
				pidControl.setPID(.005, 0, 0);
				armPos--;
				if (armPos <= 0) {
					armPos = 0;
				}
				pidControl.setSetpoint(ArmCodes.POSTITIONS[armPos]);
				armState++;
			} else if (armUp) {
				pidControl.setPID(.008, 0, 0);
				armPos++;
				if (armPos > ArmCodes.POSTITIONS.length - 1)
					armPos = ArmCodes.POSTITIONS.length - 1;
				pidControl.setSetpoint(ArmCodes.POSTITIONS[armPos]);
				armState++;
			}
			break;
		case 1:
			if (!armDown && !armUp)
				armState = 0;
			break;
		}
	}

	public static void runBasicArm(boolean up, boolean down){
		if(up){
			armLeft.set(.75);
			armRight.set(.75);
		} else if(down){
			armLeft.set(-.75);
			armRight.set(-.75);
			
		} else {
			armLeft.set(0);
			armRight.set(0);			
		}
	}
	
	/**
	 * Tells the arm to move to a certain position
	 * 
	 * @param position
	 *            Position should be gotten using ArmCodes
	 * @return Returns whether the arm is within tolerance of the selected
	 *         position
	 */
	public static boolean toPosition(double position) {
		pidControl.setSetpoint(position);
		return pidControl.onTarget();
	}

	/**
	 * Prints debug information about the potentiometer
	 */
//	public static void printPot() {
//		System.out.println("Pot is at: " + pot.get());
//	}
	
	public static void printEncoder() {
		System.out.println("Encoder is at: " + armEncoder.get());
	}

	/**
	 * Class that provides easy selection of arm positions
	 * 
	 * @author teamursamajor
	 */
	public static class ArmCodes {
//		public static final double FLAT = 5;
//		public static final double PORTCULLIS = 0;
//		public static final double PORTCULLIS_UP = 30;
//		public static final double INSIDE_FRAME = 50;
		public static final double FLAT = 130;
		public static final double PORTCULLIS = 160;
		public static final double PORTCULLIS_UP = 54;
		public static final double INSIDE_FRAME = -15;
		public static final double[] POSTITIONS = { PORTCULLIS, FLAT, PORTCULLIS_UP, INSIDE_FRAME };
	};
}
