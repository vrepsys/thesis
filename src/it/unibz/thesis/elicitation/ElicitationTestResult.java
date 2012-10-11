package it.unibz.thesis.elicitation;

public class ElicitationTestResult {

	public double maePre;
	
	public double maePost;
	
	public double maeDiff;
	
	public long time;
	
	public int itemsElicited;
	
	public String description;

	public int totalItemsUnknownByTheSystem;

	public double maeAllTrainingImprovement;
	
	@Override
	public String toString() {
		return "DESCRIPTION: " + description + "\n" +
			   "mae before elicitation: " + maePre + "\n" +
			   "mae after elicitation: " + maePost + "\n" +
			   "mae improvement: " + maeDiff + "\n" +
			   "mae improvement if using all training data: " + maeAllTrainingImprovement + "\n" +
			   "unknown items before elicitation: " + totalItemsUnknownByTheSystem + "\n" +
			   "items elicited: " + itemsElicited + "\n" +			   
			   "time (s): " + time/1000 + "\n";
	}
	
}
