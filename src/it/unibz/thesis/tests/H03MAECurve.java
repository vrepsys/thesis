package it.unibz.thesis.tests;

import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import it.unibz.thesis.data.DataSplitter;
import it.unibz.thesis.data.MAECalculator;
import it.unibz.thesis.elicitation.MyLogger;
import it.unibz.thesis.tests.data.Preference;
import it.unibz.thesis.tests.data.PreferenceFile;
import it.unibz.thesis.tests.data.PreferenceReader;


/**
 * H3: MAE Curve declines randomly adding more ratings to the training dataset 
 * 
 * @author Valdemaras
 *
 */

public class H03MAECurve {
	
	private static final int STEP = 10000;

	public static void main(String[] args) throws IOException {
		
		
		MyLogger log = new MyLogger("mae-curve1000K");		
		
		log.println(new Date().toString());
		log.println("mae curve test");
		
		List<Preference> preferences = PreferenceReader.readList(PreferenceFile.MOVIELENS_1000K);
		
		log.println("number of preferences: " + preferences.size());
		
		
		DataSplitter splitter = new DataSplitter(preferences); 
		
		splitter.splitRandomly(70);
		
		List<Preference> trainingData = splitter.getFirstPart();
		
		List<Preference> testData = splitter.getSecondPart();
		
		log.println("training preferences: " + trainingData.size());
		
		log.println("testing preferences: " + testData.size());
		
		log.println("step: " + STEP);
		
		Collections.shuffle(trainingData);
		
		int cursor = 1000;
		
		boolean exit = false;
		
		int numberOfIterations = trainingData.size() / STEP + 1;
		
		int iter = 1;
		
		log.emptyline();
		
		Double previousMae = null;
		
		while (!exit) {
			
			System.out.println(iter + "/" + numberOfIterations);
			
			iter++;
			
			if (cursor >= trainingData.size()) {
				cursor = trainingData.size();
				exit = true;
			}
			
			List<Preference> currentTrainingData = trainingData.subList(0, cursor);
			
			MAECalculator maecalc = new MAECalculator();
			
			double mae = maecalc.calculate(currentTrainingData, testData);
			
			String toLog = cursor + " " + mae;
			
			if (previousMae != null) {
				toLog += " " + (previousMae - mae);
			}
			
			log.println(toLog);
			
			previousMae = mae;
			
			cursor += STEP;
			
		}
		
		log.println(new Date().toString());
		
		log.close();
		
		
	}
	
	
}
