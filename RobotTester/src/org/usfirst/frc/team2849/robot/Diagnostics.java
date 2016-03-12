//package org.usfirst.frc.team2849.robot;
//
//import java.io.File;
//import java.io.FileWriter;
//import java.io.IOException;
//import java.io.PrintWriter;
//import java.util.Date;
//
//import edu.wpi.first.wpilibj.PowerDistributionPanel;
//
//public class Diagnostics {
//	
//	private FileWriter fileWrite;
//	private PrintWriter printLine;
//	private PowerDistributionPanel pdBoard;
//	private long timeStamp;
//	private Date date;
//	private File diagnosticFile;
//	private String logHeader = "=============================\n=============================\n=============================";
//	private String header = "-----------------------------";
//	private int counter = 0;
//	
//	public void init() {
//		date = new Date();
//		nextFile();
//		pdBoard = new PowerDistributionPanel();
//		timeStamp = System.currentTimeMillis();
//		writeDiagnostic(logHeader);
//		writeDiagnostic(date.toString() + " @ " + (System.currentTimeMillis() - timeStamp));
//	}
//	
//	public void nextFile() {
//		counter++;
//		diagnosticFile = new File("/home/lvuser/" + date.toString() + "-" + counter + ".txt");
//		try {
//			fileWrite = new FileWriter(diagnosticFile, true);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		printLine = new PrintWriter(fileWrite);
//	}
//	
//	public void writeDiagnostic(String line) {
//		printLine.println(line);
//	}
//	
//	public void writeDiagnostic(long line) {
//		printLine.print(line);
//	}
//	
//	public void close() {
//		try {
//			fileWrite.close();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		printLine.close();
//	}
//	
//	public boolean fileOverSize(int sizeInBytes) {
//		if (diagnosticFile.length() > sizeInBytes) return true;
//		else return false;
//	}
//	
//	public void writePDBoardData() {
//		writeDiagnostic(header);
//		writeDiagnostic("PDBoard stats @ " + (System.currentTimeMillis() - timeStamp) + " milliseconds");
//		writeDiagnostic("Total power is: " + pdBoard.getTotalPower());
//		writeDiagnostic("Total current is: " + pdBoard.getTotalCurrent());
//		writeDiagnostic("Total energy is: " + pdBoard.getTotalEnergy());
//		writeDiagnostic("Total voltage is: " + pdBoard.getVoltage());
//		writeDiagnostic("Current Tempature is: " + pdBoard.getTemperature());
//		writeDiagnostic("Current at channel " + 0 +" is "+ pdBoard.getCurrent(0));
//		writeDiagnostic("Current at channel " + 1 +" is "+ pdBoard.getCurrent(1));
//		writeDiagnostic("Current at channel " + 2 +" is "+ pdBoard.getCurrent(2));
//		writeDiagnostic("Current at channel " + 3 +" is "+ pdBoard.getCurrent(3));
//		writeDiagnostic("Current at channel " + 4 +" is "+ pdBoard.getCurrent(4));
//		writeDiagnostic("Current at channel " + 5 +" is "+ pdBoard.getCurrent(5));
//		writeDiagnostic("Current at channel " + 6 +" is "+ pdBoard.getCurrent(6));
//		writeDiagnostic("Current at channel " + 7 +" is "+ pdBoard.getCurrent(7));
//		writeDiagnostic("Current at channel " + 8 +" is "+ pdBoard.getCurrent(8));
//		writeDiagnostic("Current at channel " + 9 +" is "+ pdBoard.getCurrent(9));
//		writeDiagnostic("Current at channel " + 10 +" is "+ pdBoard.getCurrent(10));
//		writeDiagnostic("Current at channel " + 11 +" is "+ pdBoard.getCurrent(11));
//		writeDiagnostic("Current at channel " + 12 +" is "+ pdBoard.getCurrent(12));
//		writeDiagnostic("Current at channel " + 13 +" is "+ pdBoard.getCurrent(13));
//		writeDiagnostic("Current at channel " + 14 +" is "+ pdBoard.getCurrent(14));
//		writeDiagnostic("Current at channel " + 15 +" is "+ pdBoard.getCurrent(15));
//		if (fileOverSize(1000000)) nextFile();
//	}
//}