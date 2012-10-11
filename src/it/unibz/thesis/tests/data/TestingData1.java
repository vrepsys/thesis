package it.unibz.thesis.tests.data;


import java.util.List;


import java.io.IOException;
import java.util.Collections;
import it.unibz.thesis.data.DataSplitter;


public class TestingData1 {
	
	private static final int SPLIT = 70;
	
	private static final int KNOWN_RATINGS = 20000;
	
	
	private static final String TEST_DATA_FILE = "data/t1_test.data";
	
	private static final String KNOWN_DATA_FILE = "data/t1_known.data";
	
	private static final String UNKNOWN_DATA_FILE = "data/t1_unknown.data";
	
	public static List<Preference> testData() {
		return PreferenceReader.readList(TEST_DATA_FILE);
	}
	
	public static List<Preference> knownData() {
		return PreferenceReader.readList(KNOWN_DATA_FILE);
	}
	
	public static List<Preference> unknownData() {
		return PreferenceReader.readList(UNKNOWN_DATA_FILE);
	}

	
	public static void generate() throws IOException {
		List<Preference> prefs = PreferenceReader.read(PreferenceFile.MOVIELENS_1000K).getPreferences();
		
		DataSplitter splitter = new DataSplitter(prefs);
		splitter.splitRandomly(SPLIT);
		
		List<Preference> training = splitter.getFirstPart();
		
		List<Preference> test = splitter.getSecondPart();
		
		Collections.shuffle(training);
		
		List<Preference> known = training.subList(0, KNOWN_RATINGS);
		
		List<Preference> unknown = training.subList(KNOWN_RATINGS, training.size());
		
		PreferenceDataWriter.writePreferenceData(test, TEST_DATA_FILE);
		
		PreferenceDataWriter.writePreferenceData(known, KNOWN_DATA_FILE);
		
		PreferenceDataWriter.writePreferenceData(unknown, UNKNOWN_DATA_FILE);
	}
	
//	public static void main(String[] args) throws IOException {
//		generate();
//	}

		
}
