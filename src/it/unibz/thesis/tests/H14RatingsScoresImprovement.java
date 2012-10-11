package it.unibz.thesis.tests;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import it.unibz.thesis.beans.RatingScore;
import it.unibz.thesis.data.MAECalculator;
import it.unibz.thesis.elicitation.MyLogger;
import it.unibz.thesis.tests.data.Preference;
import it.unibz.thesis.tests.data.TestingData2;

public class H14RatingsScoresImprovement {

	private static final int ADD_TOTAL = 10000;

	private static final int ADD_ONCE = 2000;
	
	private static final int ITERATIONS = 100;

	public static void main(String[] args) throws IOException {

		int dataset = 0;
		
		if (args.length > 0) {
			dataset = Integer.parseInt(args[0]);
		}
		
		MyLogger log = new MyLogger("score-improvement-"+dataset);
				
		List<Preference> known = TestingData2.knownData(dataset);
		List<Preference> unknown = TestingData2.unknownData(dataset);
		List<Preference> test = TestingData2.testData(dataset);

		log.println("SCORES IMPROVEMENT");
		
		String conditions = "dataset: " + dataset + " known: " + known.size() + " unknown: "
				+ unknown.size() + " test: " + test.size() + " add total: " + ADD_TOTAL
				+ " add once: " + ADD_ONCE;
		
		log.println(conditions);		
		
		MAECalculator maecalc = new MAECalculator();
		
		H07Tools tools = new H07Tools(log);
				
		double mae = maecalc.calculate(known, test);
		
		log.println("0 " + mae);
		
		List<Preference> unknown_predicted = tools.predict(known, unknown);
		ScoresCalculator scores = new ScoresCalculator(known, unknown_predicted, log);
		
		for (int i = 0; i < ITERATIONS; i++) {
			
			log.println("TEST: " + (i+1) + "/" + ITERATIONS);
			long start = System.currentTimeMillis();
			
			List<RatingScore> unknown_scores = scores.improve();

			List<Preference> group_best = tools.bestScoringPreferences(unknown, unknown_scores, ADD_ONCE);
			
			List<Preference> knownTmp = new ArrayList<Preference>(known);
			
			knownTmp.addAll(group_best);

			mae = maecalc.calculate(knownTmp, test);
			
			long end = System.currentTimeMillis();
			
			long timeleft = ((end - start) * (ITERATIONS - i - 1));
			timeleft = timeleft / 1000 / 60;
			log.println((i+1) + " " + mae + " " + ((end - start) / 1000) + " " + timeleft);			
			
		}

		log.close();		

	}

}
