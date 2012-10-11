package it.unibz.thesis.tests;

import java.io.IOException;
import java.util.Date;
import it.unibz.thesis.data.MAECalculator;
import it.unibz.thesis.elicitation.MyLogger;
import it.unibz.thesis.tests.data.PreferenceFile;
import it.unibz.thesis.tests.data.PreferenceList;
import it.unibz.thesis.tests.data.PreferenceReader;

/**
 * 
 * H4: Here we divide training data into training1 and testing1. Then we find
 * groups of ratings in training1 that calculate MAE for [training1, testing1]
 * and [training
 * 
 * @author Valdemaras
 * 
 */

public class H05TwoTestSets {

	public static void main(String[] args) throws IOException {
		MyLogger log = new MyLogger();

		PreferenceList allPrefs = PreferenceReader.read(PreferenceFile.MOVIELENS_1000K);
		allPrefs.splitByTimePrc(40);
		PreferenceList firstPart = allPrefs.getLeftPart();
		PreferenceList secondPart = allPrefs.getRightPart();
		firstPart.splitRandomlyPrc(5);
		PreferenceList knownRatings = firstPart.getLeftPart();			
		PreferenceList unknownRatings = firstPart.getRightPart();
		secondPart.splitRandomlyPrc(50);
		PreferenceList testData1 = secondPart.getLeftPart();
		PreferenceList testData2 = secondPart.getRightPart();		
		
		log.println(new Date().toString());
		log.println("testing1 preferences: " + testData1.size());
		log.println("testing2 preferences: " + testData2.size());
		log.println("known preferences: " + knownRatings.size());
		log.println("unknown preferences: " + unknownRatings.size());
		
		MAECalculator maecalc = new MAECalculator();
		
		double mae1 = maecalc.calculate(knownRatings.getPreferences(), testData1.getPreferences());
		double mae2 = maecalc.calculate(knownRatings.getPreferences(), testData2.getPreferences());

		for (int i = 0; i < 100; i++) {
			PreferenceList groupToAdd = unknownRatings.getRandomGroup(1000);
			PreferenceList knownRatings_new = new PreferenceList(knownRatings, groupToAdd);
			double mae1_new = maecalc.calculate(knownRatings_new.getPreferences(), testData1.getPreferences());
			double mae2_new = maecalc.calculate(knownRatings_new.getPreferences(), testData2.getPreferences());
			double diff1 = mae1 - mae1_new;
			double diff2 = mae2 - mae2_new;
			log.println(i + " " + diff1 + " " + diff2);
		}
		
		log.close();
		
	}

}
