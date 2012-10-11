package it.unibz.thesis.tests.data;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class TestingData4 {

	private static final PreferenceFile DATA = PreferenceFile.MOVIELENS_100K;
	
	private static final int KNOWN = 30000;
	
	private static final int UNKNOWN = 40000;
	
	private static final int TEST = 30000;
	
	private static final int NUMBER_OF_TEST_SETS = 10;
	
	private static final String KNOWN_PATH = "data/known4-";
	
	private static final String UNKNOWN_PATH = "data/unknown4-";
	
	private static final String TEST_PATH = "data/test4-";
	
	private static final String EXTENTION = ".data";
	
	private static String path(String path, int dataset) {
		return path + dataset + EXTENTION;
	}
	
	public static synchronized List<Preference> knownData(int dataset) {
		return PreferenceReader.readList(path(KNOWN_PATH, dataset));
	}
	
	public static synchronized List<Preference> unknownData(int dataset) {
		return PreferenceReader.readList(path(UNKNOWN_PATH, dataset));
	}
	
	public static synchronized List<Preference> testData(int dataset) {
		return PreferenceReader.readList(path(TEST_PATH, dataset));
	}

	public static void generate(int numberOfTestSets) throws IOException {
		
		List<Preference> all = PreferenceReader.readList(DATA);
		
		for (int i = 0; i < numberOfTestSets; i++) {
			System.out.println((i+1)+"/"+numberOfTestSets);
			Collections.shuffle(all);
			
			List<Preference> known = all.subList(0, KNOWN);
			List<Preference> unknown = all.subList(KNOWN, UNKNOWN+KNOWN);
			List<Preference> test = all.subList(UNKNOWN+KNOWN, UNKNOWN+KNOWN+TEST);

			PreferenceDataWriter.writePreferenceData(known, path(KNOWN_PATH, i));
			PreferenceDataWriter.writePreferenceData(unknown, path(UNKNOWN_PATH, i));
			PreferenceDataWriter.writePreferenceData(test, path(TEST_PATH, i));

		}
		
	}
	
	public static void generate() throws IOException {
		generate(NUMBER_OF_TEST_SETS);
	}
	
	public static void main(String[] args) throws IOException {
		
		generate();
		
	}
	
	
}
