package it.unibz.thesis.strategytests;

import java.util.List;
import java.util.concurrent.Callable;

import it.unibz.thesis.data.MAECalculator;
import it.unibz.thesis.elicitation.MyLogger;
import it.unibz.thesis.tests.H07Tools;
import it.unibz.thesis.tests.data.Preference;
import it.unibz.thesis.tests.data.TestingData4;

public class StrategyTest implements Callable<TestResult> {

	private static final int ADD_TOTAL = 10000;

	private static final int ADD_ONCE = 2000;

	private static final int FEATURES = 16;

	private Strategy strategy;
	
	private MyLogger log;

	private MyLogger resultsLog;
	
	private TestResult result;
	
	private int dataset;

	public StrategyTest(Strategy strategy, int testNumber) {
		this.strategy = strategy;
		this.log = new MyLogger(strategy.getTitle() + "-log-" + testNumber);
		this.resultsLog = new MyLogger(strategy.getTitle() + "-res-" + testNumber);
		this.dataset = testNumber;
		this.result = new TestResult(strategy.getTitle());
	}

	public TestResult call() throws Exception {

		try {

			MAECalculator maecalc = new MAECalculator(FEATURES);

			H07Tools tools = new H07Tools(log, FEATURES);

			List<Preference> known = TestingData4.knownData(dataset);
			List<Preference> unknown = TestingData4.unknownData(dataset);
			List<Preference> test = TestingData4.testData(dataset);

			log.println(strategy.getTitle());

			String conditions = "# dataset: " + dataset + " known: " + known.size() + " unknown: "
					+ unknown.size() + " test: " + test.size() + " add total: " + ADD_TOTAL
					+ " add once: " + ADD_ONCE;

			log.println(conditions);
			resultsLog.println(conditions);
			resultsLog.println("# " + strategy.getTitle());

			int iterations = ADD_TOTAL / ADD_ONCE;

			double mae = maecalc.calculate(known, test);

//			results.println(known.size() + " " + mae);
			
			result.add(known.size(), mae);
			
			resultsLog.println("" + mae);

			for (int i = 0; i < iterations; i++) {

				log.println("TEST: " + (i + 1) + "/" + iterations);
				long start = System.currentTimeMillis();

				List<Preference> unknownPredicted = tools.predict(known, unknown);

				List<Preference> groupToAdd = strategy.getItemsToAdd(tools, known, unknown,
						unknownPredicted, ADD_ONCE);

				unknown.removeAll(groupToAdd);

				known.addAll(groupToAdd);

				mae = maecalc.calculate(known, test);

//				results.println(known.size() + " " + mae);
				result.add(known.size(), mae);
				resultsLog.println(""+mae);

				long end = System.currentTimeMillis();

				long timeleft = ((end - start) * (iterations - i - 1));
				timeleft = timeleft / 1000 / 60;
				log.println("TEST: iteration took (s) : " + ((end - start) / 1000)
						+ " time left (m): " + timeleft);

			}

			log.close();
			resultsLog.close();
			
			System.out.println("test finished");

		}
		catch (Throwable e) {
			log.println(e);
		}

		return result;
		
	}

}
