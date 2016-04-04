package org.usfirst.frc.team2849.robot;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.SensorBase;

public class Diagnostics {

	private FileWriter fileWrite;
	private PrintWriter printLine;
	private PowerDistributionPanel pdBoard;
	private long timeStamp;
	private Date date;
	private File diagnosticFile;
	private String logHeader = "=============================\n=============================\n=============================";
	private String header = "-----------------------------";
	private int counter = 0;

	public Diagnostics() {
		init();
	}
	
	public void init() {
		date = new Date();
		nextFile();
		pdBoard = new PowerDistributionPanel();
		timeStamp = System.currentTimeMillis();
		writeDiagnostic(logHeader);
		writeDiagnostic(date.toString() + " @ " + (System.currentTimeMillis() - timeStamp));
	}

	public void nextFile() {
		counter++;
		diagnosticFile = new File("/home/lvuser/" + date.toString() + "-" + counter + ".txt");
		try {
			fileWrite = new FileWriter(diagnosticFile, true);
			printLine = new PrintWriter(fileWrite);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void writeDiagnostic(String line) {
		printLine.print(line + "\r\n");
	}

	public void writeDiagnostic(long line) {
		printLine.print(line + "\r\n");
	}

	public void close() {
		try {
			fileWrite.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		printLine.close();
	}

	public boolean fileOverSize(int sizeInBytes) {
		if (diagnosticFile.length() > sizeInBytes)
			return true;
		else
			return false;
	}

	public void writePDBoardData() {
		writeDiagnostic(header);
		writeDiagnostic("PDBoard stats @ " + (System.currentTimeMillis() - timeStamp) + " milliseconds");
		writeDiagnostic("Total power is: " + getTotalPower());
		writeDiagnostic("Total current is: " + getTotalCurrent());
		writeDiagnostic("Total energy is: " + getTotalEnergy());
		writeDiagnostic("Total voltage is: " + getVoltage());
		writeDiagnostic("Current Tempature is: " + getTemperature());
//		for (int channel = 0; channel < SensorBase.kPDPChannels; channel++) {
//			writeDiagnostic("Current at channel " + channel + " is " + getCurrent(channel));
//		}
		writeDiagnostic("Current at channel 15 is " + getCurrent(15));
		if (fileOverSize(1000000))
			nextFile();
	}
	
	// wrappers for PDP methods in case you want them singularly, because java
	public double getCurrent(int channel) {
		return pdBoard.getCurrent(channel);
	}
	
	public double getTotalPower() {
		return pdBoard.getTotalPower();
	}
	
	public double getTotalCurrent() {
		return pdBoard.getTotalCurrent();
	}
	
	public double getTotalEnergy() {
		return pdBoard.getTotalEnergy();
	}
	
	public double getVoltage() {
		return pdBoard.getVoltage();
	}
	
	public double getTemperature() {
		return pdBoard.getTemperature();
	}
}