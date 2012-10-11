package it.unibz.thesis.tests;

import it.unibz.thesis.data.MAECalculator;
import it.unibz.thesis.elicitation.MyLogger;
import it.unibz.thesis.tests.data.Preference;
import it.unibz.thesis.tests.data.PreferenceFile;
import it.unibz.thesis.tests.data.PreferenceList;
import it.unibz.thesis.tests.data.PreferenceReader;

import java.util.ArrayList;
import java.util.List;

public class H17ScoreWorstComparisonReal {	
	
	private static PreferenceList all;
	
	private static synchronized PreferenceList getSplitPrefs() {
		if (all == null) {
			all = PreferenceReader.read(PreferenceFile.MOVIELENS_100K);
			all.splitRandomly(30000, 40000, 30000);
		}		
		return all;
	}

	private static class FirstTest implements Runnable {

		private int features;
		
		public FirstTest(int features) {
			this.features = features;
		}
	
		public void run() {
			MyLogger log = new MyLogger("score-worst-comparison-real"+features);
			try {									
				H07Tools tools = new H07Tools(log, features);
				
				MAECalculator mae = new MAECalculator(features);
		
				PreferenceList all = getSplitPrefs();
					
				List<Preference> known = new ArrayList<Preference>(all.getLeftPartList());
				List<Preference> unknown = new ArrayList<Preference>(all.getMiddlePartList());
				List<Preference> test = new ArrayList<Preference>(all.getRightPartList());
		
				List<Preference> unknownPredicted = tools.predict(known, unknown);
		
				List<Preference> groupMaxScoreReal = tools.bestScoringPreferences(known, unknown,
						unknownPredicted, 1000);
		
				List<Preference> groupWorstReal = tools.lowestPreferences(unknown, unknownPredicted, 1000);
		
				double mae_real = mae.calculate(known, test);
		
				List<Preference> t = new ArrayList<Preference>(known);
				t.addAll(groupMaxScoreReal);
				double mae_score_real = mae.calculate(t, test);
		
				t = new ArrayList<Preference>(known);
				t.addAll(groupWorstReal);
				double mae_worst_real = mae.calculate(t, test);
				log.println("Features: " + features);
				log.println("Before:" + mae_real);
				log.println("Score: " + mae_score_real);
				log.println("Worst: " + mae_worst_real);
				log.println("Difference: " + (mae_worst_real - mae_score_real));		
			}			
			catch (Throwable t) {
				log.println(t);
			}
		}
	
	}

	public static void main(String[] args) {
//		new Thread(new FirstTest(8)).start();
//		new Thread(new FirstTest(16)).start();
		new Thread(new FirstTest(32)).start();
//		new Thread(new FirstTest(64)).start();
//		new Thread(new FirstTest(128)).start();
		
	}

}
