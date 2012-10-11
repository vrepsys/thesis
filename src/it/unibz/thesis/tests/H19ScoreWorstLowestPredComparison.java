package it.unibz.thesis.tests;

import it.unibz.thesis.data.MAECalculator;
import it.unibz.thesis.elicitation.MyLogger;
import it.unibz.thesis.tests.data.Preference;
import it.unibz.thesis.tests.data.PreferenceFile;
import it.unibz.thesis.tests.data.PreferenceList;
import it.unibz.thesis.tests.data.PreferenceReader;

import java.util.ArrayList;
import java.util.List;

public class H19ScoreWorstLowestPredComparison {	
		
	
public static void main(String args[] ) {
	
			MyLogger log = new MyLogger("score-worst-lowest");
			try {									
				H07Tools tools = new H07Tools(log);
				
				MAECalculator mae = new MAECalculator();
		
				PreferenceList all = PreferenceReader.read(PreferenceFile.MOVIELENS_100K);
				all.splitRandomly(30000, 40000, 30000);
					
				List<Preference> known = all.getLeftPartList();
				List<Preference> unknown = all.getMiddlePartList();
				List<Preference> test = all.getRightPartList();
		
				List<Preference> unknownPredicted = tools.predict(known, unknown);
				
				
//				List<RatingScore> scores = tools.scores(known, unknownPredicted, 6);
				
				
//				List<Preference> groupMaxScore = tools.bestScoredPreferences(scores, 1000);

				List<Preference> groupLowestPredictions = tools.lowestPreferences(unknownPredicted,
						1000);
				
				List<Preference> groupWorstPredictions = tools.worstPredictedPreferences_predictedValue(unknown, unknownPredicted, 1000);
						
				
				
//				List<Preference> groupMaxScoreReal = tools.bestScoredPreferences(unknown, scores, 1000);
		
				List<Preference> groupLowestPredictionReal = tools.lowestPreferences(unknown, unknownPredicted, 1000);
		
				List<Preference> groupWorstPredictionReal = tools.worstPredictedPreferences(unknown, unknownPredicted, 1000);
				
				
				
				
				double mae_real = mae.calculate(known, test);
		
				List<Preference> t = new ArrayList<Preference>(known);
//				t.addAll(groupMaxScoreReal);
//				double mae_score_real = mae.calculate(t, test);
		
				t = new ArrayList<Preference>(known);
				t.addAll(groupLowestPredictionReal);
				double mae_lowest_prediction_real = mae.calculate(t, test);
				
				t = new ArrayList<Preference>(known);
				t.addAll(groupWorstPredictionReal);
				double mae_worst_prediction_real = mae.calculate(t, test);
				
				log.println("Before:" + mae_real);
//				log.println("Score: " + mae_score_real);
				log.println("Lowest Prediction: " + mae_lowest_prediction_real);
				log.println("Worst Prediction: " + mae_worst_prediction_real);
				log.emptyline();
				
//				double mae_score = mae.calculatePredictedReal(groupMaxScore, unknown);
				
				double mae_lowest = mae.calculatePredictedReal(groupLowestPredictions, unknown);
				
				double mae_worst = mae.calculatePredictedReal(groupWorstPredictions, unknown);
				
//				log.println("mae of the score group: " + mae_score);
				log.println("mae of the lowest group: " + mae_lowest);
				log.println("mae of the worst group: " + mae_worst);
				
			}			
			catch (Throwable t) {
				log.println(t);
			}
		}
	


}
