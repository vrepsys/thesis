package it.unibz.thesis.tests;

import it.unibz.thesis.beans.RatingScore;
import it.unibz.thesis.data.DataSplitter;
import it.unibz.thesis.data.MAECalculator;
import it.unibz.thesis.elicitation.MyLogger;
import it.unibz.thesis.tests.data.Preference;
import it.unibz.thesis.tests.data.PreferenceFile;
import it.unibz.thesis.tests.data.PreferenceList;
import it.unibz.thesis.tests.data.PreferenceReader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class H15ScoreWorstComparison {

	public static void main(String[] args) throws IOException {
		
		MyLogger log = new MyLogger("score-worst-comparison");
		
		H07Tools tools = new H07Tools(log);
		
		MAECalculator maecalc = new MAECalculator();
		
		PreferenceList all = PreferenceReader.read(PreferenceFile.MOVIELENS_100K);

		all.splitRandomly(30000, 40000, 30000);
		List<Preference> known = all.getLeftPartList();
		List<Preference> unknown = all.getMiddlePartList();
		List<Preference> test = all.getRightPartList();
		
		log.println("known.size: " + known.size() + " unknown.size:" + unknown.size() + " test.size: " + test.size());
		
		List<Preference> unknownPredicted = tools.predict(known, unknown);
		
		List<RatingScore> unknownScores = tools.scores(known, unknownPredicted, 6);
		
		List<Preference> groupMaxScorePred = tools.bestScoringPreferences(unknownScores, 1000);
		
		List<Preference> groupWorstPred = tools.lowestPreferences(unknownPredicted, 1000);
		
		List<Preference> groupMaxScoreReal = tools.bestScoringPreferences(unknown, unknownScores, 1000);
		
		List<Preference> groupWorstReal = tools.lowestPreferences(unknown, unknownPredicted, 1000);
		
		
		DataSplitter splitter = new DataSplitter(known);
		splitter.splitRandomly(50);
		
		List<Preference> training1 = splitter.getFirstPart();
		List<Preference> test1 = splitter.getSecondPart();						
		double mae = maecalc.calculate(training1, test1);
		
		List<Preference> t = new ArrayList<Preference>(training1);
		t.addAll(groupMaxScorePred);		
		double mae_score = maecalc.calculate(t, test1);
		
		t = new ArrayList<Preference>(training1);
		t.addAll(groupWorstPred);		
		double mae_worst = maecalc.calculate(t, test1);
		
		log.println(mae + " " + mae_score + " " + mae_worst);
				
		double mae_real = maecalc.calculate(known, test);

		t = new ArrayList<Preference>(known);
		t.addAll(groupMaxScoreReal);
		double mae_score_real = maecalc.calculate(t, test);
		
		t = new ArrayList<Preference>(known);
		t.addAll(groupWorstReal);
		double mae_worst_real = maecalc.calculate(t, test);
				
		log.println(mae_real + " " + mae_score_real + " " + mae_worst_real);		
		
	
		
		t = new ArrayList<Preference>(training1);
		t.addAll(groupMaxScoreReal);
		double mae_score_t1 = maecalc.calculate(t, test1);
		
		t = new ArrayList<Preference>(training1);
		t.addAll(groupWorstReal);				
		double mae_worst_t1 = maecalc.calculate(t, test1);
		
		log.println(mae_score_t1 + " " + mae_worst_t1);
				
	}
	
}
