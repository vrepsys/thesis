package it.unibz.thesis.tests;

import it.unibz.thesis.data.MAECalculator;
import it.unibz.thesis.elicitation.MyLogger;
import it.unibz.thesis.tests.data.PreferenceFile;
import it.unibz.thesis.tests.data.PreferenceList;
import it.unibz.thesis.tests.data.PreferenceReader;

import java.io.IOException;
import java.util.Date;


/**
 * 
 * H1: adding one rating does not improve the prediction accuracy statistically
 * 
 */

public class H02GroupOfRatings {

	private static final int NR_OF_TESTS = 100;
	
	private static int GROUP_SIZE = 500;
	
	private static MyLogger log = new MyLogger();
	
	public static void main(String[] args) throws IOException {		

		if (args.length > 0) {
			GROUP_SIZE = Integer.parseInt(args[0]);
		}
		
		log.println((new Date()).toString());
		log.println("testing effects of removal of group of ratings to mae, group size: " + GROUP_SIZE);
		
		MAECalculator maecalc = new MAECalculator();
		
		PreferenceList all = PreferenceReader.read(PreferenceFile.MOVIELENS_1000K);

		all.splitRandomlyPrc(70);

		PreferenceList training = all.getLeftPart();
		
		PreferenceList test = all.getRightPart();				

		double mae1 = maecalc.calculate(training, test);
						
		double signumSum = 0.0;
		
		double sum = 0.0;		
		
		int cnt = 0;
		
		for (int i=0; i<NR_OF_TESTS; i++) {
			
			long s = System.currentTimeMillis();
			
			PreferenceList group = training.getRandomGroup(GROUP_SIZE);
			
			PreferenceList trainingWithoutPref = training.removePreferences(group);
			
			double mae2 = maecalc.calculate(trainingWithoutPref, test);
			double improvement = mae2 - mae1;			
			
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
