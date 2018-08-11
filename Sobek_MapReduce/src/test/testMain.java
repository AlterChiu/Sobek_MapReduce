package test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import Analysis.DelicateTotal.FloodInitail.InitailFloodTime;

import GlobalProperty.GlobalProperty;
import SOBEK.Runtimes;
import SOBEK.SobekDem;
import asciiFunction.AsciiBasicControl;
import asciiFunction.AsciiMerge;
import asciiFunction.AsciiSplit;
import main.InitializeFolder;
import usualTool.AtFileWriter;
import usualTool.FileFunction;

public class testMain {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		FileFunction ff = new FileFunction();
		
//		SobekDem sobekDem = new SobekDem();
//		sobekDem.addNewDem(GlobalProperty.originalRough, GlobalProperty.originalRoughKn);
//		sobekDem.start();
//		System.out.print("test runtimes ");
//		getTimes();
//		System.out.println();
		
		InitializeFolder initial = new InitializeFolder();
		initial.createBeforeTotalRun();
		
	
	}

	private static void getTimes() throws IOException {
		long start = System.currentTimeMillis();
		new Runtimes();
		long end = System.currentTimeMillis();
		System.out.print(end - start + "\t");
	}

}
