package it.unibz.thesis.tests;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import it.unibz.thesis.data.MAECalculator;
import it.unibz.thesis.elicitation.MyLogger;
import it.unibz.thesis.tests.data.Preference;
import it.unibz.thesis.tests.data.TestingData4;

public class H10RatingsBestRandomInterval {

	private static final int ADD_TOTAL = 10000;

	private static final int ADD_ONCE = 2000;

	private static final int CHOICES = 10;

	public static void main(String[] args) throws IOException {

		int dataset = 0;

		if (args.length > 0) {
			dataset = Integer.parseInt(args[0]);
		}

		MyLogger log = new MyLogger("randbest-log-" + dataset);

		MyLogger results = new MyLogger("randbest-res-" + dataset);

		List<Preference> known = TestingData4.knownData(dataset);
		List<Preference> unknown = TestingData4.unknownData(dataset);
		List<Preference> test = TestingData4.testData(dataset);

		log.println("RANDOM-BEST");

		String conditions = "dataset: " + dataset + " known: " + known.size() + " unknown: "
				+ unknown.size() + " test: " + test.size() + " add total: " + ADD_TOTAL
				+ " add once: " + ADD_ONCE + "choices: " + CHOICES;

		log.println(conditions);
		results.println(conditions);

		int iterations = ADD_TOTAL / ADD_ONCE;

		MAECalculator maecalc = new MAECalculator();
		
		double mae = maecalc.calculate(known, test);

		results.println(known.size() + " " + mae);

		for (int i = 0; i < iterations; i++) {

			log.println("TEST: " + (i + 1) + "/" + iterations);
			long start = System.currentTimeMillis();

			List<Preference> knownRatingsMin = null;
			List<Preference> unknownRatingsMin = null;
			double minMae = Double.MAX_VALUE;

			for (int j = 0; j < CHOICES; j++) {				
				Collections.shuffle(unknown);
				List<Preference> toAdd1 = unknown.subList(0, ADD_ONCE);
				List<Preference> knownRatings1 = new ArrayList<Preference>(known);
				knownRatings1.addAll(toAdd1);
				double res1 = maecalc.calculate(knownRatings1, test);
				System.out.println("mae: " + res1);
				if (res1 < minMae) {
					knownRatingsMin = knownRatings1;
					unknownRatingsMin = new ArrayList<Preference>((unknown.subList(ADD_ONCE,
							unknown.size())));
					minMae = res1;
				}
				

			}
			System.out.println("chosen: " + minMae);
			known = knownRatingsMin;
			unknown = unknownRatingsMin;

			mae = minMae;

			results.println(known.size() + " " + mae);

			long end = System.currentTimeMillis();

			long timeleft = ((end - start) * (iterations - i - 1));
			timeleft = timeleft / 1000 / 60;
			log.println("TEST: iteration took (s) : " + ((end - start) / 1000) + " time left (m): "
					+ timeleft);

		}

		log.close();
		results.close();

	}

}
