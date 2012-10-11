package it.unibz.thesis.tests;

import java.util.ArrayList;
import java.util.List;

import it.unibz.thesis.data.MAECalculator;
import it.unibz.thesis.elicitation.MyLogger;
import it.unibz.thesis.tests.data.Preference;
import it.unibz.thesis.tests.data.PreferenceFile;
import it.unibz.thesis.tests.data.PreferenceList;
import it.unibz.thesis.tests.data.PreferenceReader;

public class H16WorstInvestigation {

	private static MyLogger log = new MyLogger("worst-investigation");

	public static void main(String[] args) {

		H07Tools tools = new H07Tools(log);
		
		MAECalculator maecalc = new MAECalculator();		

		PreferenceList all = PreferenceReader.read(PreferenceFile.MOVIELENS_100K);

		all.splitRandomly(30000, 40000, 30000);
		List<Preference> known = all.getLeftPartList();
		List<Preference> unknown = all.getMiddlePartList();
		List<Preference> test = all.getRightPartList();

		// we get a group of low ratings from the unknown
		List<Preference> lowestGroup = tools.lowestPreferences(unknown, 1000);

		// we get a group of ratings that are predicted low
		List<Preference> unknownPredicted = tools.predict(known, unknown);
		List<Preference> lowestPredictedGroup = tools.lowestPreferences(unknown, unknownPredicted,
				1000);

		// we get a group of ratings with high scores
		List<Preference> highestScoringGroup = tools.bestScoringPreferences(known, unknown,
				unknownPredicted, 1000);

 
		// iterate increasing both the training and the tests sets
		for (int i = 1; i < 11; i++) {

			List<Preference> train1 = known.subList(0, i * 3000);
			List<Preference> test1 = test.subList(0, i * 3000);

			double maePre = maecalc.calculate(train1, test1);

			List<Preference> t = new ArrayList<Preference>(train1);
			t.addAll(lowestGroup);
			double maeLowestGroup = maecalc.calculate(t, test);

			t = new ArrayList<Preference>(train1);
			t.addAll(lowestPredictedGroup);
			double maeLowestPredictedGroup = maecalc.calculate(t, test);

			t = new ArrayList<Preference>(train1);
			t.addAll(highestScoringGroup);
			double maeHighestScoringGroup = maecalc.calculate(t, test);

			double impr1 = maePre - maeLowestGroup;
			double impr2 = maePre - maeLowestPredictedGroup;
			double impr3 = maePre - maeHighestScoringGroup;

			log.println(train1.size() + " " + maePre + " " + maeLowestGroup + " "
					+ maeLowestPredictedGroup + " " + maeHighestScoringGroup + " " + impr1 + " "
					+ impr2 + " " + impr3);

		}

	}

}
