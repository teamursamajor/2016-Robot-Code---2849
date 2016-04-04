package org.usfirst.frc.team2849.robot;

import edu.wpi.first.wpilibj.Talon;

/**
 * Climber class -- runs climber from POV
 */
public class Climber {

	// Motors for the winch and the extender (tape measure)
	private static Talon winch = new Talon(7);
	private static Talon tapeMeasure = new Talon(2);

	/**
	 * Run the climber
	 * 
	 * @param up
	 *            Variable from button to determine if the climber should move
	 *            up
	 * @param down
	 *            Variable from button to determine if the climber should move
	 *            down
	 */
	public static void runClimber(boolean up, boolean down) {
		if (up) {
			tapeMeasure.set(.3);
			//winch.set(1); <-- Not running winch when going up
		} else if (down) {
			tapeMeasure.set(-.2);
//			winch.set(-1);
		} else {
			tapeMeasure.set(0);
//			winch.set(0);
		}
	}
}
