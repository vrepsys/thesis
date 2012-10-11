package it.unibz.thesis.tests;

import it.unibz.thesis.data.MAECalculator;
import it.unibz.thesis.elicitation.MyLogger;
import it.unibz.thesis.tests.data.Preference;
import it.unibz.thesis.tests.data.PreferenceFile;
import it.unibz.thesis.tests.data.PreferenceList;
import it.unibz.thesis.tests.data.PreferenceReader;

import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.List;


/**
 * 
 * H1: adding one rating does not improve the prediction accuracy statistically
 * BUT IT SHOULD IMPROVE IT!!!
 * 
 */

public class H01OneRating {

	private static final int NR_OF_TESTS = 1000;
	
	private static MyLogger log = new MyLogger();
	
	public static void main(String[] args) throws IOException {		

		log.println((new Date()).toString());
		log.println("testing effects of removal one rating to mae");
		PreferenceList all = PreferenceReader.read(PreferenceFile.MOVIELENS_100K);

		all.splitRandomly(30000, 40000, 30000);

		PreferenceList training = all.getLeftPart();
		
		PreferenceList unknown = all.getMiddlePart();
		
		PreferenceList test = all.getRightPart();	
		
		MAECalculator maecalc = new MAECalculator();
		
		double maePre = maecalc.calculate(training, test);
						
		double signumSum = 0.0;
		
		double sum = 0.0;		
		
		int cnt = 0;
		
		List<Preference> unknownList = unknown.getPreferences();
		
		Collections.shuffle(unknownList);
		
		for (int i=0; i<NR_OF_TESTS; i++) {
			
			long s = System.currentTimeMillis();
			
						
			PreferenceList trainingWithAddPref = training.addPreference(unknownList.get(i));
			
			double maePost = maecalc.calculate(trainingWithAddPref, test);
			double improvement = maePre - maePost;
			
			if (!Double.isNaN(improvement)) {
				signumSum += Math.signum(improvement);
				sum += improvement;
			}
			
			cnt++;
			
			long e = System.currentTimeMillis();
			
			log.println(cnt+"/"+NR_OF_TESTS+" sgnsum=" + signumSum + " sum=" + sum + " improvement="+improvement+" time=" + (e-s));
						
		}
		
		log.println((new Date()).toString());				

	}

}
