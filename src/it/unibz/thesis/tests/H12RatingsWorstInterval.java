package it.unibz.thesis.tests;

import java.io.IOException;
import java.util.List;

import it.unibz.thesis.data.MAECalculator;
import it.unibz.thesis.elicitation.MyLogger;
import it.unibz.thesis.tests.data.Preference;
import it.unibz.thesis.tests.data.TestingData4;

public class H12RatingsWorstInterval {

	private static final int ADD_TOTAL = 10000;

	private static final int ADD_ONCE = 2000;

	public static void main(String[] args) throws IOException {

		int dataset = 0;
		
		if (args.length > 0) {
			dataset = Integer.parseInt(args[0]);
		}
		
		MyLogger log = new MyLogger("worst-log-"+dataset);
		
		MyLogger results = new MyLogger("worst-res-"+dataset);

		List<Preference> known = TestingData4.knownData(dataset);
		List<Preference> unknown = TestingData4.unknownData(dataset);
		List<Preference> test = TestingData4.testData(dataset);

		log.println("WORST");
		
		String conditions = "dataset: " + dataset + " known: " + known.size() + " unknown: "
				+ unknown.size() + " test: " + test.size() + " add total: " + ADD_TOTAL
				+ " add once: " + ADD_ONCE;
		
		log.println(conditions);
		results.println(conditions);

		int iterations = ADD_TOTAL / ADD_ONCE;
		
		MAECalculator maecalc = new MAECalculator();
		
		double mae = maecalc.calculate(known, test);
		
		results.println(known.size() + " " + mae);
		
		H07Tools tools = new H07Tools(log);
		
		for (int i = 0; i < iterations; i++) {
			
			log.println("TEST: " + (i+1) + "/" + iterations);
			long start = System.currentTimeMillis();
			
			List<Preference> unknown_predicted = tools.predict(known, unknown);
			
			log.println("predicted " + unknown_predicted.size() + " ratings out of " + unknown.size());
			
			List<Preference> group_worst = tools.lowestPreferences(unknown, unknown_predicted, ADD_ONCE);

			unknown.removeAll(group_worst);
			
			known.addAll(group_worst);
			
			mae = maecalc.calculate(known, test);
			
			results.println(known.size() + " " + mae);
			
			long end = System.currentTimeMillis();
			
			long timeleft = ((end - start) * (iterations - i - 1));
			timeleft = timeleft / 1000 / 60;
			log.println("TEST: iteration took (s) : " + ((end - start) / 1000) + " time left (m): " + timeleft);
			
		}

		log.close();
		results.close();

	}

}
