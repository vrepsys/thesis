package it.unibz.thesis.playground;

import it.unibz.thesis.tests.data.Preference;
import it.unibz.thesis.tests.data.PreferenceFile;
import it.unibz.thesis.tests.data.PreferenceReader;

import java.io.IOException;
import java.util.List;

public class RatingsDistributionTest {

	/*
	overall
	1-6110 6.11%
	2-11370 11.370000000000001%
	3-27145 27.145%
	4-34174 34.174%
	5-21201 21.201%
	*/
	
	public static final String DATA_FILE = "u.data";
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		List<Preference> allRatings = PreferenceReader.readList(PreferenceFile.MOVIELENS_100K);
		int[] counts = new int[] {0, 0, 0, 0, 0};
		for (Preference d : allRatings) {
			int rating = ((int) d.getValue()) - 1;
			counts[rating]++;
		}
		int sum = 0;
		for (int i=0; i<5; i++) {
			sum+=counts[i];
		}
		for (int i=0; i<5; i++) {
			System.out.println((i+1) + "-" + (counts[i]) + " " + ((100/(double)sum)*counts[i]) + "%");
		}
	}

}
