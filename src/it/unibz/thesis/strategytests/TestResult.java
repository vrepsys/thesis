package it.unibz.thesis.strategytests;

import it.unibz.thesis.beans.NumberOfItemsMae;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TestResult implements Comparable<TestResult> {

	private String title;

	List<NumberOfItemsMae> results = new ArrayList<NumberOfItemsMae>();

	public TestResult(String title) {
		this.title = title;
	}
	
	public TestResult(String title, List<NumberOfItemsMae> results) {
		this.title = title;
		this.results = results;
	}
	
	

	public String getTitle() {
		return title;
	}

	public void add(int nr, double mae) {
		results.add(new NumberOfItemsMae(nr, mae));
	}

	public List<NumberOfItemsMae> get() {
		return results;
	}

	public static TestResult average(List<TestResult> results) {

		NumberOfItemsMae[] avg = results.get(0).results
				.toArray(new NumberOfItemsMae[results.get(0).results.size()]);

		results = results.subList(1, results.size());
		
		for (TestResult r : results) {
			List<NumberOfItemsMae> nrMaePairList = r.get();
			for (int i = 0; i < nrMaePairList.size(); i++) {
				NumberOfItemsMae nrMaePair = nrMaePairList.get(i);
				avg[i].addMae(nrMaePair.getMae());
			}			
		}
		
		for (NumberOfItemsMae nrMaePair : avg) {
			nrMaePair.divide(results.size()+1);
		}
		
		return new TestResult(results.get(0).title, Arrays.asList(avg));
		
	}
	
	public static void main(String[] args) {
		
		
		
		TestResult tr1 = new TestResult("vienas");
		tr1.add(0, 1.0);
		tr1.add(1, 2.0);
		
		TestResult tr2 = new TestResult("vienas");
		tr2.add(0, 3.0);
		tr2.add(1, 4.0);
		
		TestResult r = average(Arrays.asList(new TestResult[] {tr1, tr2}));
		
		System.out.println(r.get());
		
		
	}

	@Override
	public int compareTo(TestResult o) {
		return String.CASE_INSENSITIVE_ORDER.compare(o.title, title);
	}


}
