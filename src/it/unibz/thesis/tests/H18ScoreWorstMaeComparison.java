package it.unibz.thesis.tests;

import it.unibz.thesis.beans.RatingScore;
import it.unibz.thesis.data.MAECalculator;
import it.unibz.thesis.elicitation.MyLogger;
import it.unibz.thesis.tests.data.Preference;
import it.unibz.thesis.tests.data.PreferenceFile;
import it.unibz.thesis.tests.data.PreferenceList;
import it.unibz.thesis.tests.data.PreferenceReader;

import java.util.ArrayList;
import java.util.List;

public class H18ScoreWorstMaeComparison {

	public static void main(String[] args) {
		MyLogger log = new MyLogger("score-worst-mae-comparison");
		try {
			H07Tools tools = new H07Tools(log, 8);

			PreferenceList all = PreferenceReader.read(PreferenceFile.MOVIELENS_100K);
			all.splitRandomly(30000, 40000, 30000);

			List<Preference> known = new ArrayList<Preference>(all.getLeftPartList());
			List<Preference> unknown = new ArrayList<Preference>(all.getMiddlePartList());
			List<Preference> test = new ArrayList<Preference>(all.getRightPartList());

			List<Preference> unknownPredicted = tools.predict(known, unknown);

			
			List<RatingScore> scores = tools.scores(known, unknownPredicted, 6);			
			List<Preference> groupMaxScorePred = tools.bestScoringPreferences(scores, 1000);

			List<Preference> groupWorstPred = tools.lowestPreferences(unknownPredicted,
					1000);
			
			List<Preference> groupBestPred = tools.highestPreferences(unknownPredicted,
					1000);

			MAECalculator mae = new MAECalculator(8);
			
			double mae_average = mae.calculate(known, test);

			double mae_score = mae.calculatePredictedReal(groupMaxScorePred, unknown);
			
			double mae_worst = mae.calculatePredictedReal(groupWorstPred, unknown);
			
			double mae_best = mae.calculatePredictedReal(groupBestPred, unknown);
			
			
			
			log.println("avg: " + mae_average);
			log.println("best: " + mae_best);
			log.println("score: " + mae_score);
			log.println("worst: " + mae_worst);
			
		}
		catch (Throwable t) {
			log.println(t);
		}
	}

}
