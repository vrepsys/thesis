package it.unibz.thesis.data;

import java.util.List;

import it.unibz.thesis.factorization.Factorization;
import it.unibz.thesis.tests.data.Preference;
import it.unibz.thesis.tests.data.PreferenceList;

public class MAECalculator {
	
	private int features;
	
	public MAECalculator(int features) {
		this.features = features;
	}
	
	public MAECalculator() {
		features = 8;
	}
	
	public double calculate(List<Preference> trainingData, List<Preference> testData) {

		Factorization fact = new Factorization(trainingData, features);
		
		fact.initialize();
		
		double errorSum = 0.0;
		int cntSucc = 0, cntAll = 0;
		int nan = 0;
		for (Preference pref : testData) {
			
			double realPref = pref.getValue();
		
			int movieId = Integer.valueOf(pref.getItemId());
			int custId = Integer.valueOf(pref.getUserId());
			
			Double newPref = fact.predictRating(movieId, custId);
			
			if (newPref == null) continue;
					
			cntAll++;
			
			if (!Double.isNaN(newPref)) {
				errorSum += Math.abs(realPref - newPref);
				cntSucc++;
			}
			else {
				nan++;
			}
				
		}
		
		double mae = errorSum / cntSucc;
		
		return mae;

	}
	
	public double calculatePredictedReal(List<Preference> predicted, List<Preference> real) {
		
		PreferenceList realList = new PreferenceList(real);
		
		double errorSum = 0.0;
		int cntSucc = 0;
		for (Preference pref : predicted) {			
			double predictedValue = pref.getValue();		
			double realValue = realList.getPreference(pref.getUserId(), pref.getItemId()).getValue();
			errorSum += Math.abs(realValue - predictedValue);
			cntSucc++;
			
		}
		
		double mae = errorSum / cntSucc;
		
		return mae;

	}
	
	public double calculate(PreferenceList trainingData, PreferenceList testData) {
		return calculate(trainingData.getPreferences(), testData.getPreferences());
	}
	
}
