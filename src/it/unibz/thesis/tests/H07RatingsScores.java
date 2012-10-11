package it.unibz.thesis.tests;

import java.io.IOException;
import java.util.List;

import it.unibz.thesis.beans.RatingScore;
import it.unibz.thesis.data.MAECalculator;
import it.unibz.thesis.elicitation.MyLogger;
import it.unibz.thesis.tests.data.Preference;
import it.unibz.thesis.tests.data.TestingData1;

public class H07RatingsScores {

	public static MyLogger log = new MyLogger();
	
	public static void main(String[] args) throws IOException {
		
		List<Preference> known = TestingData1.knownData();
		List<Preference> unknown = TestingData1.unknownData();
		List<Preference> test = TestingData1.testData();
		
		
		MAECalculator maecalc = new MAECalculator();
		
		H07Tools tools = new H07Tools(log);
		
		// dataset too large, tests too slow so we make unknown smaller and discard the rest of the data		
		unknown = unknown.subList(0, 10000);
		
		log.println("known: " + known.size() + " unknown: " + unknown.size() + " test: " + test.size());
		
		List<Preference> unknown_predicted = tools.predict(known, unknown);
		
		List<RatingScore> unknown_scores = tools.scores(known, unknown_predicted, 10);
		
		List<Preference> group_best = tools.bestScoringPreferences(unknown, unknown_scores, 1000);
						
		double mae = maecalc.calculate(known, test);
		
		double mae_best = tools.mae(known, group_best, test);			
		
		double sum = 0.0;
		int cnt = 10;
		for (int i = 0; i < cnt; i++) {
		
			double mae_random = tools.mae(known, tools.randomGroup(unknown, 1000), test);
			sum += mae_random;
			
		}
		double mae_random_avg = sum / cnt;
		
		
		log.println(mae + " " + mae_best + " " + mae_random_avg);
		
	}
	
}
