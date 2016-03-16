package org.usfirst.frc.team2849.robot;

import org.usfirst.frc.team2849.robot.Drive.DriveLock;

public class Autonomous {
	
	private static int stage = 0;
	private static boolean done;
	
	private static String autoMode = "lowbar";
	private static int autoLoc = 0;
	private static boolean shoot;
	private static long startTime;

	public static void autonomousInit() {
		Drive.shiftGear(false);
		autoMode = "rockwall";
		shoot = false;
		autoLoc = 0;
		Drive.lock(DriveLock.AUTONOMOUS);
	}

	public static boolean runAutonomous() {
		switch (autoMode) {
		case "ramparts":
			runRamparts();
			break;
		case "cheval":
		case "chevaldefrise": 
			runChevalDeFrise();
			break;
		case "moat": 
			runMoat();
			break;
		case "portcullis":
		case "porticullis":
			runPortcullis();
			break;
		case "highwall":
		case "brickwall":
		case "rockwall":
			runRockWall();
			break;
		case "rockyterrain":
		case "roughterrain":
			runRoughTerrain();
			break;
		default: 
		case "lowbar":
			runLowBar();
			break;
		}
		return done;
	}

	public static double getSpeedProfileSpeed(long timeMillis){
		timeMillis = System.currentTimeMillis() - timeMillis;
		return (1)/(1+Math.exp(-.01*timeMillis+5));
	}
	
	/**
	 * Run periodically when crossing the ramparts
	 */
	private static final int rampartDist = 500;
	public static void runRamparts() {
		switch (stage) {
		case 0:
			done = false;
			Arm.toPosition(Arm.ArmCodes.INSIDE_FRAME);
			stage++;
			startTime = System.currentTimeMillis();
			break;
		case 1:
			if(Drive.runDriveDist(-getSpeedProfileSpeed(startTime),	-getSpeedProfileSpeed(startTime),
					rampartDist, rampartDist, DriveLock.AUTONOMOUS)){
				stage++;
			}
			break;
		case 2:
			if(shoot && Drive.runDriveDist(.75 * Math.signum(autoLoc - 3), -.75 * Math.signum(autoLoc - 3), 
					5 * (autoLoc - 3) * Math.cos(15), 5 * (autoLoc - 3) * Math.cos(15), DriveLock.AUTONOMOUS)){
				stage++;
			}
			break;
		case 3:
			if (shoot) {
				if (Shooter.assistedShoot(true)) {
					done = true;
					stage = 0;
				}
			} else {
				done = true;
				stage = 0;
			}
			break;
		}
	}
	
	/**
	 * Run periodically when crossing the moat
	 */
	private static final int moatDist = 500;
	public static void runMoat() {
		switch (stage) {
		case 0:
			done = false;
			Arm.toPosition(Arm.ArmCodes.INSIDE_FRAME);
			stage++;
			break;
		case 1:
			if(Drive.runDriveDist(-getSpeedProfileSpeed(startTime), -getSpeedProfileSpeed(startTime),
					moatDist, moatDist, DriveLock.AUTONOMOUS)){
				stage++;
			}
			break;
		case 2:
			if(shoot && Drive.runDriveDist(.75 * Math.signum(autoLoc - 3), -.75 * Math.signum(autoLoc - 3), 
					5 * (autoLoc - 3) * Math.cos(15), 5 * (autoLoc - 3) * Math.cos(15), DriveLock.AUTONOMOUS)){
				stage++;
			}
			break;
		case 3:
			if (shoot) {
				if (Shooter.assistedShoot(true)) {
					done = true;
					stage = 0;
				}
			} else {
				done = true;
				stage = 0;
			}
			break;
		}
	}
	
	/**
	 * Run periodically when crossing the rough terrain
	 */
	private static final int roughTerrainDist = 500;
	public static void runRoughTerrain() {
		switch (stage) {
		case 0:
			done = false;
			Arm.toPosition(Arm.ArmCodes.INSIDE_FRAME);
			stage++;
			break;
		case 1:
			if(Drive.runDriveDist(-getSpeedProfileSpeed(startTime), -getSpeedProfileSpeed(startTime),
					roughTerrainDist, roughTerrainDist, DriveLock.AUTONOMOUS)){
				stage++;
			}
			break;
		case 2:
			if(shoot && Drive.runDriveDist(.75 * Math.signum(autoLoc - 3), -.75 * Math.signum(autoLoc - 3), 
					5 * (autoLoc - 3) * Math.cos(15), 5 * (autoLoc - 3) * Math.cos(15), DriveLock.AUTONOMOUS)){
				stage++;
			}
			break;
		case 3:
			if (shoot) {
				if (Shooter.assistedShoot(true)) {
					done = true;
					stage = 0;
				}
			} else {
				done = true;
				stage = 0;
			}
			break;
		}
	}
	
	/**
	 * Run periodically when crossing the rock wall
	 */
	private static final int rockWallDist = 500;
	public static void runRockWall() {
		switch (stage) {
		case 0:
			done = false;
			Arm.toPosition(Arm.ArmCodes.INSIDE_FRAME);
			stage++;
			break;
		case 1:
			if(Drive.runDriveDist(-getSpeedProfileSpeed(startTime), -getSpeedProfileSpeed(startTime),
					rockWallDist, rockWallDist, DriveLock.AUTONOMOUS)){
				stage++;
			}
			break;
		case 2:
			if(shoot && Drive.runDriveDist(.75 * Math.signum(autoLoc - 3), -.75 * Math.signum(autoLoc - 3), 
					5 * (autoLoc - 3) * Math.cos(15), 5 * (autoLoc - 3) * Math.cos(15), DriveLock.AUTONOMOUS)){
				stage++;
			}
			break;
		case 3:
			if (shoot) {
				if (Shooter.assistedShoot(true)) {
					done = true;
					stage = 0;
				}
			} else {
				done = true;
				stage = 0;
			}
			break;
		}
	}

	/**
	 * Run periodically when crossing the low bar
	 */
	private static final int lowBarDist = 500;
	public static void runLowBar() {
		switch (stage) {
		case 0:
			done = false;
			startTime = System.currentTimeMillis();
			Arm.toPosition(Arm.ArmCodes.PORTCULLIS);
			stage++;
			break;
		case 1:
			if(System.currentTimeMillis() - startTime > 2000 && Drive.runDriveDist(-getSpeedProfileSpeed(startTime), -getSpeedProfileSpeed(startTime),
					lowBarDist, lowBarDist, DriveLock.AUTONOMOUS)){
				stage++;
			}
			break;
		case 2:
			if(shoot && Drive.runDriveDist(.75 * Math.signum(autoLoc - 3), -.75 * Math.signum(autoLoc - 3), 
					5 * (autoLoc - 3) * Math.cos(15), 5 * (autoLoc - 3) * Math.cos(15), DriveLock.AUTONOMOUS)){
				stage++;
			}
			break;
		case 3:
			if (shoot) {
				if (Shooter.assistedShoot(true)) {
					done = true;
					stage = 0;
				}
			} else {
				done = true;
				stage++;
			}
			break;
		}
	}

	/**
	 * Run periodically when crossing the cheval de frise
	 * Currently needs tuning -- might need to add in slight backup step
	 */
	private static final int chevalApproachDist = Integer.MIN_VALUE;
	private static final int chevalCrossDist = Integer.MIN_VALUE;
	public static void runChevalDeFrise() {
		switch (stage) {
		case 0:
			done = false;
			Arm.toPosition(Arm.ArmCodes.INSIDE_FRAME);
			if(Drive.runDriveDist(.75 * getSpeedProfileSpeed(startTime), .75 * getSpeedProfileSpeed(startTime),
					chevalApproachDist, chevalApproachDist, DriveLock.AUTONOMOUS)){
				stage++;
			}
			break;
		case 1:
			Arm.toPosition(Arm.ArmCodes.FLAT);
			stage++;
			break;
		case 2:
			if(Drive.runDriveDist(.75 * getSpeedProfileSpeed(startTime), .75 * getSpeedProfileSpeed(startTime),
					6, 6, DriveLock.AUTONOMOUS)){
				stage++;
			}
			break;
		case 3:
			Arm.toPosition(Arm.ArmCodes.INSIDE_FRAME);
			break;
		case 4:
			if(Drive.runDriveDist(getSpeedProfileSpeed(startTime), getSpeedProfileSpeed(startTime),
					chevalCrossDist, chevalCrossDist, DriveLock.AUTONOMOUS)){
				stage++;
			}
			break;
		case 5:
			if(shoot && Drive.runDriveDist(.75 * Math.signum(autoLoc - 3), -.75 * Math.signum(autoLoc - 3), 
					5 * (autoLoc - 3) * Math.cos(15), 5 * (autoLoc - 3) * Math.cos(15), DriveLock.AUTONOMOUS)){
				stage++;
			}
			break;
		case 6:
			if (shoot) {
				if (Shooter.assistedShoot(true)) {
					done = true;
					stage = 0;
				}
			} else {
				done = true;
				stage = 0;
			}

			break;
		}
	}

	/**
	 * Run periodically when crossing the portcullis
	 * Needs tuning -- might need a better method of arm control
	 */
	private static final int portcullisApproachDist = Integer.MIN_VALUE;
	private static final int portcullisLiftDist = Integer.MIN_VALUE;
	private static final int portcullisCrossDist = Integer.MIN_VALUE;
	public static void runPortcullis() {
		switch (stage) {
		case 0:
			done = false;
			Arm.toPosition(Arm.ArmCodes.INSIDE_FRAME);
			if(Drive.runDriveDist(.75 * getSpeedProfileSpeed(startTime), .75 * getSpeedProfileSpeed(startTime),
					portcullisApproachDist, portcullisApproachDist, DriveLock.AUTONOMOUS)){
				stage++;
			}
			break;
		case 1:
			Arm.toPosition(Arm.ArmCodes.PORTCULLIS);
			stage++;
			break;
		case 2:
			if(Drive.runDriveDist(.75 * getSpeedProfileSpeed(startTime), .75 * getSpeedProfileSpeed(startTime),
					portcullisLiftDist, portcullisLiftDist, DriveLock.AUTONOMOUS)){
				Arm.toPosition(Arm.ArmCodes.INSIDE_FRAME);
				stage++;
			}
			break;
		case 3:
			if(Drive.runDriveDist(getSpeedProfileSpeed(startTime), getSpeedProfileSpeed(startTime),
					portcullisCrossDist, portcullisCrossDist, DriveLock.AUTONOMOUS)){
				stage++;
			}
			break;
		case 4:
			if(shoot && Drive.runDriveDist(.75 * Math.signum(autoLoc - 3), -.75 * Math.signum(autoLoc - 3), 
					5 * (autoLoc - 3) * Math.cos(15), 5 * (autoLoc - 3) * Math.cos(15), DriveLock.AUTONOMOUS)){
				stage++;
			}
			break;
		case 5:
			if (shoot) {
				if (Shooter.assistedShoot(true)) {
					done = true;
					stage = 0;
				}
			} else {
				done = true;
				stage = 0;
			}
			break;
		}
	}

}