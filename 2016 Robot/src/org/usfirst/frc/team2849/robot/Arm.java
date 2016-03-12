package org.usfirst.frc.team2849.robot;

import edu.wpi.first.wpilibj.AnalogPotentiometer;
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
	private static AnalogPotentiometer pot = new AnalogPotentiometer(3, 360, -300);

	// PID Controller to handle the movement of the arm
	private static PIDController pidControl;

	// PID Controller variables
	private static final double PROPORTIONAL = .01;
	private static final double INTEGRAL = 0;
	private static final double DERIVATIVE = 0;

	// Position arm should be moving to
	private static int armPos = 1;

	// Variable to handle button press state machine
	private static int armState = 0;

	// Initialize the PID Controller
	static {
		pidControl = new PIDController(PROPORTIONAL, INTEGRAL, DERIVATIVE, pot, new PIDOutput() {
			@Override
			public void pidWrite(double output) {
				armLeft.set(output);
				armRight.set(output);
			}
		});
		pidControl.setAbsoluteTolerance(.1);
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
		switch (armState) {
		case 0:
			if (armDown) {
				armPos--;
				if (armPos < 0)
					armPos = 0;
				pidControl.setSetpoint(ArmCodes.POSTITIONS[armPos]);
				armState++;
			} else if (armUp) {
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
	public static void printPot() {
		System.out.println(pot.get());
	}

	/**
	 * Class that provides easy selection of arm positions
	 * 
	 * @author teamursamajor
	 */
	public static class ArmCodes {
		public static final double FLAT = 43;
		public static final double PORTCULLIS = 70;
		public static final double INSIDE_FRAME = -5;
		public static final double[] POSTITIONS = { PORTCULLIS, FLAT, INSIDE_FRAME };
	};
}
