package it.unibz.thesis.strategytests;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import it.unibz.thesis.elicitation.MyLogger;
import it.unibz.thesis.tests.data.TestingData4;

public class Main {
	
	private static final int NR_OF_TESTS = 10;

	public static void main(String[] args) throws IOException, InterruptedException, ExecutionException {
		
		TestingData4.generate(NR_OF_TESTS);
		
		ExecutorService exec = Executors.newFixedThreadPool(10);
		
		Future<TestResult> result;
		
		List<Future<TestResult>> results = new ArrayList<Future<TestResult>>();
		
		for (int i = 0; i < NR_OF_TESTS; i++) {
		
//			result = exec.submit(new StrategyTest(new HighestPredictedStrategy(), i));
//			results.add(result);
			result = exec.submit(new StrategyTestForUsers(new LowestPredictedStrategy(), i));
			results.add(result);
//			result = exec.submit(new StrategyTest(new LowestHighestPredictedStrategy(), i));
//			results.add(result);
			result = exec.submit(new StrategyTestForUsers(new RandomStrategy(), i));
			results.add(result);
//			result = exec.submit(new StrategyTest(new VarianceStrategy(), i));
//			results.add(result);			
//			result = exec.submit(new StrategyTest(new PopularityStrategy(), i));
//			results.add(result);
			
		}
		
		MyLogger logger = new MyLogger("gnuplot");
		
		logger.log(results);
		
		logger.close();
		
		exec.shutdown();
		
	}
	
}
