package it.unibz.thesis.tests;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import it.unibz.thesis.data.MAECalculator;
import it.unibz.thesis.elicitation.MyLogger;
import it.unibz.thesis.tests.data.Preference;
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

public class H06ThreeTestSets {
	
	private static int getCommonItemRatingNr(PreferenceList knownRatings, PreferenceList testData) {
		Set<Integer> training_items = new HashSet<Integer>();
		for (Preference pref : knownRatings.getPreferences()) {
			training_items.add(pref.getItemId());
		}
		int commonRatings = 0;
		for (Preference pref : testData.getPreferences()) {				
			if (training_items.contains(pref.getItemId())) commonRatings++;
		}
		return commonRatings;
	}
	
	private static int getCommonItemRatingNrNew(PreferenceList knownRatings, PreferenceList testData) {
	
		Map<Integer, Integer> trainingDistr = new HashMap<Integer, Integer>();		
		for (Preference pref : knownRatings.getPreferences()) {
			int itemId = pref.getItemId();
			Integer distrPoint = trainingDistr.get(itemId);
			if (distrPoint == null) {
				distrPoint = 1;				
			}
			else {
				distrPoint++;
			}
			trainingDistr.put(itemId, distrPoint);
		}
		
		Map<Integer, Integer> testingDistr = new HashMap<Integer, Integer>();		
		for (Preference pref : testData.getPreferences()) {
			int itemId = pref.getItemId();
			Integer distrPoint = testingDistr.get(itemId);
			if (distrPoint == null) {
				distrPoint = 1;				
			}
			else {
				distrPoint++;
			}
			testingDistr.put(itemId, distrPoint);
		}
		
		int cnt = 0;
		
		for (int itemId : trainingDistr.keySet()) {
			int tr = trainingDistr.get(itemId);
			Integer te = testingDistr.get(itemId);
			if (te==null) te=0;
			if (te > tr) cnt+=tr;
		}		
		return cnt;
	}
	
	private static int getCommonUserRatingNrNew(PreferenceList knownRatings, PreferenceList testData) {
		
		Map<Integer, Integer> trainingDistr = new HashMap<Integer, Integer>();		
		for (Preference pref : knownRatings.getPreferences()) {
			int userId = pref.getUserId();
			Integer distrPoint = trainingDistr.get(userId);
			if (distrPoint == null) {
				distrPoint = 1;				
			}
			else {
				distrPoint++;
			}
			trainingDistr.put(userId, distrPoint);
		}
		
		Map<Integer, Integer> testingDistr = new HashMap<Integer, Integer>();		
		for (Preference pref : testData.getPreferences()) {
			int userId = pref.getUserId();
			Integer distrPoint = testingDistr.get(userId);
			if (distrPoint == null) {
				distrPoint = 1;				
			}
			else {
				distrPoint++;
			}
			testingDistr.put(userId, distrPoint);
		}
		
		int cnt = 0;
		
		for (int userId : trainingDistr.keySet()) {
			int tr = trainingDistr.get(userId);
			Integer te = testingDistr.get(userId);
			if (te==null) te=0;
			if (te > tr) cnt+=tr;
		}		
		return cnt;
	}
	
	private static int getCommonUserRatingNr(PreferenceList knownRatings, PreferenceList testData) {
		Set<Integer> users_items = new HashSet<Integer>();
		for (Preference pref : knownRatings.getPreferences()) {
			users_items.add(pref.getUserId());
		}
		int commonRatings = 0;
		for (Preference pref : testData.getPreferences()) {				
			if (users_items.contains(pref.getUserId())) commonRatings++;
		}
		return commonRatings;
	}

	public static void main(String[] args) throws IOException {
		MyLogger maeLog = new MyLogger();
		
		PreferenceList allPrefs = PreferenceReader.read(PreferenceFile.MOVIELENS_1000K);
		
		double mae1sum = 0;
		double mae2sum = 0;
		double mae3sum = 0;
		int cnt = 100;
		
		for (int i = 1; i < cnt; i++) {
			allPrefs.splitByTimePrc(10);
			PreferenceList firstPart = allPrefs.getLeftPart();
			PreferenceList secondPart = allPrefs.getRightPart();
			firstPart.splitRandomlyPrc(20);
			PreferenceList knownRatings = firstPart.getLeftPart();			
//			PreferenceDataList unknownRatings = firstPart.getRightPart();
			secondPart.splitByTimePrc(33);
			PreferenceList testData1 = secondPart.getLeftPart();
			PreferenceList rightTesting = secondPart.getRightPart();
			rightTesting.splitByTimePrc(50);
			PreferenceList testData2 = rightTesting.getLeftPart();
			PreferenceList testData3 = rightTesting.getRightPart();

			if (i == 1) {
				maeLog.println("#known: " + knownRatings.size());
				maeLog.println("#testData1: " + testData1.size());
				maeLog.println("#testData2: " + testData2.size());
				maeLog.println("#testData3: " + testData3.size());
			}

			MAECalculator maecalc = new MAECalculator();
			
			double mae1 = maecalc.calculate(knownRatings.getPreferences(), testData1.getPreferences());
			double mae2 = maecalc.calculate(knownRatings.getPreferences(), testData2.getPreferences());
			double mae3 = maecalc.calculate(knownRatings.getPreferences(), testData3.getPreferences());
			int commonItem1 = getCommonItemRatingNr(knownRatings, testData1);
			int commonItem2 = getCommonItemRatingNr(knownRatings, testData2);
			int commonItem3 = getCommonItemRatingNr(knownRatings, testData3);
			int commonUser1 = getCommonUserRatingNr(knownRatings, testData1);
			int commonUser2 = getCommonUserRatingNr(knownRatings, testData2);
			int commonUser3 = getCommonUserRatingNr(knownRatings, testData3);
			int commonItemNew1 = getCommonItemRatingNrNew(knownRatings, testData1);
			int commonItemNew2 = getCommonItemRatingNrNew(knownRatings, testData2);
			int commonItemNew3 = getCommonItemRatingNrNew(knownRatings, testData3);
			int commonUserNew1 = getCommonUserRatingNrNew(knownRatings, testData1);
			int commonUserNew2 = getCommonUserRatingNrNew(knownRatings, testData2);
			int commonUserNew3 = getCommonUserRatingNrNew(knownRatings, testData3);
						
			maeLog.println(mae1 + " " + mae2 + " " + mae3 + " " + commonItem1 + " " + commonItem2 + " " + commonItem3 + " " + commonUser1 + " " + commonUser2 + " " + commonUser3  + " " + commonItemNew1 + " " + commonItemNew2 + " " + commonItemNew3 + " " + commonUserNew1 + " " + commonUserNew2 + " " + commonUserNew3);
			
			mae1sum += mae1;
			mae2sum += mae2;
			mae3sum += mae3;
		}		
		maeLog.println("#averages: " + mae1sum / cnt + " " + mae2sum / cnt + " " + mae3sum / cnt);

		maeLog.close();
		
	}

}
