package it.unibz.thesis.tests;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import it.unibz.thesis.data.MAECalculator;
import it.unibz.thesis.elicitation.MyLogger;
import it.unibz.thesis.tests.data.Preference;
import it.unibz.thesis.tests.data.TestingData1;


/**
 * Method1: choose a set of ratings  randomly  and add it to the known ratings dataset
 * Method2: choose N sets of ratings randomly, check which of them improves the mae more 
 * and add it to the known ratings dataset
 * 
 * H4: MAE Curve using method1 goes above MAE curve using method 2.
 * 
 * @author Valdemaras
 *
 */

public class H04RandomChooseBetter {
	
	private static final int STEP = 1000;
	
	private static final int START = 20000;
	
	private static final int FINISH = 100000;

	private static int choices;

	public static void main(String[] args) throws IOException {
		
		if (args.length == 0) {
			choices = 2;
		}
		else {
			choices = Integer.parseInt(args[0]);
		}
		
		MyLogger log = new MyLogger();				
		log.println(new Date().toString());						
		log.println("TEST WHERE 1 SET OF RATINGS FROM "+choices+" IS CHOSEN ACCORDING TO MAE.");
		
		List<Preference> testData = TestingData1.testData();						
		List<Preference> unknownRatings = TestingData1.unknownData();		
		List<Preference> knownRatings =  TestingData1.knownData();		
		
		log.println("testing preferences: " + testData.size());
		log.println("known preferences: " + knownRatings.size());
		log.println("unknown preferences: " + unknownRatings.size());
		log.println("start: " + START);		
		log.println("finish: " + FINISH);		
		log.println("step: " + STEP);
		
		int cursor = START;
		
		boolean exit = false;
		
		int numberOfIterations = (FINISH-START) / STEP;
		
		int iter = 0;
		
		log.emptyline();
			
		List<Preference> currentTrainingData = new ArrayList<Preference>(knownRatings);
		
		MAECalculator maecalc = new MAECalculator();
		
		while (!exit) {
			
			iter++;
			System.out.println(iter + "/" + numberOfIterations);						
			
			if (cursor >= FINISH) {
				cursor = FINISH;
				exit = true;
			}
			
			//-------------------------					
			currentTrainingData.addAll(unknownRatings.subList(cursor-STEP, cursor));
			cursor += STEP;
			double mae1 = maecalc.calculate(currentTrainingData, testData);
			
			//-------------------------
			
			//-------------------------
			double mae2;
			if (iter == 1) {
				mae2 = mae1;
			}
			else {
								
				List<Preference> knownRatingsMin = null;
				List<Preference> unknownRatingsMin = null;
				double minMae = Double.MAX_VALUE;
				
				for (int i = 0; i < choices; i++) {
					Collections.shuffle(unknownRatings);
					List<Preference> toAdd1 = unknownRatings.subList(0, STEP);				
					List<Preference> knownRatings1 = new ArrayList<Preference>(knownRatings);
					knownRatings1.addAll(toAdd1); 
					double res1 = maecalc.calculate(knownRatings1, testData);
					
					if (res1 < minMae) {
						knownRatingsMin = knownRatings1;
						unknownRatingsMin = new ArrayList<Preference>((unknownRatings.subList(STEP, unknownRatings.size())));
						minMae = res1;
					}
					
				}
				
				knownRatings = knownRatingsMin;
				unknownRatings = unknownRatingsMin;
				
				mae2 = minMae;
				
			}
			
			String toLog = cursor + " " + mae1 + " " + mae2 + " " + (mae1 - mae2);
			
			log.println(toLog);
			

			
		}
		
		log.println(new Date().toString());
		
		log.close();
		
		
	}
	
	
}
