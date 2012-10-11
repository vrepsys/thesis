package it.unibz.thesis.tests.data;

public enum PreferenceFile {

	MOVIELENS_100K("data\\movielens100K.data", "	"),
	
	MOVIELENS_1000K("data\\movielens1000K.data", "::");
	
	
	private String path;
	
	private String separator;
	
	private PreferenceFile(String filePath, String separator) {
		this.path = filePath;
		this.separator = separator;
	}
	
	public String getPath() {
		return path;
	}
	
	public String getSeparator() {
		return separator;
	}
	
}
