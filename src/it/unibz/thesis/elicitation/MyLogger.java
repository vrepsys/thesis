package it.unibz.thesis.elicitation;

import it.unibz.thesis.strategytests.TestResult;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;


public class MyLogger {

	public FileWriter logfile;

	public File file;
	
	public MyLogger() {
		this("test" +"-"+ System.currentTimeMillis() );
	}
	
	public MyLogger(String desc) {
		try {
			file = new File(desc+ ".log");
			logfile = new FileWriter(file);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void println(String text) {
		try {
			logfile.write(text + "\r\n");
			logfile.flush();
		}
		catch(IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void print(String text) {
		try {
			logfile.write(text);
		}
		catch(IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void emptyline() {
		println("");
	}

	public void close() {
		try {
			logfile.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void println(Throwable t) {
		try {
			t.printStackTrace(new PrintStream(file));
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		
	}

	public void log(List<Future<TestResult>> results) throws InterruptedException, ExecutionException {

		Map<String, List<TestResult>> resultsMap = new HashMap<String, List<TestResult>>();
		
		System.out.println("waiting to print");
		
		for (Future<TestResult> f : results) {
			System.out.println("waiting for the result..");
			TestResult result = f.get();
			System.out.println("got it");
			String title = result.getTitle();
			List<TestResult> rlist = resultsMap.get(title);
			if (rlist == null) {
				rlist = new ArrayList<TestResult>();
				resultsMap.put(title, rlist);
			}
			rlist.add(result);
		}
		
		List<TestResult> avgResults = new ArrayList<TestResult>();
		
		for (List<TestResult> resultsOfCertainType : resultsMap.values()) {	
						
			printTestResults(resultsOfCertainType);
			emptyline();
			
			avgResults.add(TestResult.average(resultsOfCertainType));
		}				
		
		printTestResults(avgResults);
				
	}

	private void printTestResults(List<TestResult> results) {
		
		if (results.size() == 0) {
			println("no results");
			return;
		}
		
		Collections.sort(results);
		
		print("# ");
		for (TestResult res : results) {
			print(res.getTitle() + " ");
		}
		emptyline();
		
		int length = results.get(0).get().size();
		
		for (int i = 0; i < length; i++) {
			
			print(results.get(0).get().get(i).getNrOfItems() + " ");
			
			for (TestResult res : results) {
				print(res.get().get(i).getMae() + " ");
			}
			emptyline();
		}
	}

/*
	public void printGnuPlotData(Map<StrategyType, List<Future<ElicitationTestResult>>> results2)
			throws InterruptedException, ExecutionException, IOException {

		print("Summary ");
		for (StrategyType strategy : results2.keySet()) {
			print(strategy.name() + "-MAE ");
		}
		for (StrategyType strategy : results2.keySet()) {
			print(strategy.name() + "-ITEMS_ELICITED ");
		}
		emptyline();
		for (int i = 0; i < MainTest.NUMBER_OF_TESTS; i++) {
			print("Test-" + i + " ");
			for (StrategyType strategy : results2.keySet()) {
				List<Future<ElicitationTestResult>> futurelist = results2.get(strategy);
				ElicitationTestResult res = futurelist.get(i).get();
				double maeDiff = res.maeDiff;
				print(maeDiff + " ");
			}
			for (StrategyType strategy : results2.keySet()) {
				List<Future<ElicitationTestResult>> futurelist = results2.get(strategy);
				ElicitationTestResult res = futurelist.get(i).get();
				int elicited = res.itemsElicited;
				print(elicited + " ");
			}
						
			emptyline();
		}

		print("Average ");

		for (StrategyType strategy : results2.keySet()) {
			List<Future<ElicitationTestResult>> futurelist = results2.get(strategy);
			List<ElicitationTestResult> results = new ArrayList<ElicitationTestResult>();
			for (Future<ElicitationTestResult> f : futurelist) {
				results.add(f.get());
			}
			ElicitationTestResult res = summarizeTestResults(results);
			double maeDiff = res.maeDiff;
			print(maeDiff + " ");
		}
		
		for (StrategyType strategy : results2.keySet()) {
			List<Future<ElicitationTestResult>> futurelist = results2.get(strategy);
			List<ElicitationTestResult> results = new ArrayList<ElicitationTestResult>();
			for (Future<ElicitationTestResult> f : futurelist) {
				results.add(f.get());
			}
			ElicitationTestResult res = summarizeTestResults(results);
			int elicited = res.itemsElicited;
			print(elicited + " ");
		}

	}

	
	public void printResults(List<Future<ElicitationTestResult>> futures)
			throws InterruptedException, ExecutionException, IOException {
		
		List<ElicitationTestResult> results = new ArrayList<ElicitationTestResult>();
		for (Future<ElicitationTestResult> f : futures) {
			ElicitationTestResult r = f.get();
			println(r.toString());
			emptyline();
			results.add(r);
		}

		ElicitationTestResult summary = summarizeTestResults(results);
		println(summary.toString());
		println("--------");
	}

	public void printResutls(Map<StrategyType, List<Future<ElicitationTestResult>>> results2,
			StrategyType[] typesToPrint) throws InterruptedException, ExecutionException,
			IOException {

		for (StrategyType t : typesToPrint) {
			printResults(results2.get(t));
		}

	}

	private static ElicitationTestResult summarizeTestResults(List<ElicitationTestResult> results) {
		double maePre = 0;
		double maePost = 0;
		double maeDiff = 0;
		long time = 0;
		int itemsElicited = 0;
		for (ElicitationTestResult result : results) {
			maePre += result.maePre;
			maePost += result.maePost;
			maeDiff += result.maeDiff;
			time += result.time;
			itemsElicited += result.itemsElicited;
		}
		
		ElicitationTestResult result = new ElicitationTestResult();
		int size = results.size();
		result.maePre = maePre/size;
		result.maePost = maePost/size;
		result.maeDiff = maeDiff/size;
		result.time = time/size;
		result.itemsElicited = itemsElicited/size;
		result.description = results.get(0).description + " SUMMARY";
		return result;
	}
	
	*/

}
