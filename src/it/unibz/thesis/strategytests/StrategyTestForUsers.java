package it.unibz.thesis.strategytests;

import java.util.List;
import java.util.concurrent.Callable;

import it.unibz.thesis.data.MAECalculator;
import it.unibz.thesis.elicitation.MyLogger;
import it.unibz.thesis.tests.H07Tools;
import it.unibz.thesis.tests.data.Preference;
import it.unibz.thesis.tests.data.PreferenceList;
import it.unibz.thesis.tests.data.TestingData4;

public class StrategyTestForUsers implements Callable<TestResult> {

	private static final int ADD_TOTAL = 10000;

	private static final int ADD_ONCE = 2;

	private static final int FEATURES = 16;

	private static final int ITERATIONS = 5;

	private Strategy strategy;

	private MyLogger log;

	private MyLogger resultsLog;

	private TestResult result;

	private int dataset;

	public StrategyTestForUsers(Strategy strategy, int testNumber) {
		this.strategy = strategy;
		this.log = new MyLogger(strategy.getTitle() + "-log-" + testNumber);
		this.resultsLog = new MyLogger(strategy.getTitle() + "-res-"
				+ testNumber);
		this.dataset = testNumber;
		this.result = new TestResult(strategy.getTitle());
	}

	public TestResult call() throws Exception {

		try {

			MAECalculator maecalc = new MAECalculator(FEATURES);

			H07Tools tools = new H07Tools(log, FEATURES);

			List<Preference> knownList = TestingData4.knownData(dataset);
			List<Preference> unknownList = TestingData4.unknownData(dataset);
			List<Preference> testList = TestingData4.testData(dataset);

			PreferenceList known = new PreferenceList(knownList);

			log.println(strategy.getTitle());

			String conditions = "# FOR EVERY USER TEST dataset: " + dataset
					+ " known: " + knownList.size() + " unknown: "
					+ unknownList.size() + " test: " + testList.size()
					+ " add total: " + ADD_TOTAL;

			log.println(conditions);
			resultsLog.println(conditions);
			resultsLog.println("# " + strategy.getTitle());

			double mae = maecalc.calculate(knownList, testList);

			resultsLog.println("" + mae);
			result.add(known.size(), mae);

			List<Integer> userIds = known.getUserIds();

			for (int i = 0; i < ITERATIONS; i++) {

				long start = System.currentTimeMillis();
				log.println("TEST: " + i + "/" + ITERATIONS);

					
				strategy.refreshPredictions();
				
				for (int uid : userIds) {												
					
					List<Preference> groupToAdd = strategy.getItemsToAdd(tools,
							knownList, unknownList, uid, ADD_ONCE);

					unknownList.removeAll(groupToAdd);

					knownList.addAll(groupToAdd);

				}

				mae = maecalc.calculate(knownList, testList);
				result.add(knownList.size(), mae);
				resultsLog.println("" + mae);

				long end = System.currentTimeMillis();
				long timeleft = ((end - start) * (ITERATIONS - i - 1));
				timeleft = timeleft / 1000 / 60;
				log.println("TEST: iteration took (s) : "
						+ ((end - start) / 1000) + " time left (m): "
						+ timeleft);
			}

			log.close();
			resultsLog.close();

			System.out.println("test finished");

		} catch (Throwable e) {
			log.println(e);
			e.printStackTrace();
		}

		return result;

	}

}
