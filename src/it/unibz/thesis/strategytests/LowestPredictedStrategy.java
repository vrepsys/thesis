package it.unibz.thesis.strategytests;

import it.unibz.thesis.tests.H07Tools;
import it.unibz.thesis.tests.data.Preference;

import java.util.List;

public class LowestPredictedStrategy implements Strategy {

	private List<Preference> predicted = null;
	
	@Override
	public void refreshPredictions() {	
		predicted = null;
	}
	
	
	private List<Preference> getPredictions(H07Tools tools, List<Preference> training, List<Preference> toPredict) {
		if (predicted == null) {
			predicted = tools.predict(training, toPredict);
		}
		return predicted;
	}
	
	@Override
	public List<Preference> getItemsToAdd(H07Tools tools, List<Preference> known,
			List<Preference> unknown, List<Preference> unknownPredicted, int size) {
		return tools.lowestPreferences(unknown, unknownPredicted, size);
	}
	
	@Override
	public List<Preference> getItemsToAdd(H07Tools tools,
			List<Preference> knownList, List<Preference> unknownList, int uid,
			int size) {
		
		
		List<Preference> pred = getPredictions(tools, knownList, unknownList);
		return tools.lowestPreferences(pred, unknownList, uid, size);
	}
	


	@Override
	public String getTitle() {
		return "lowest-predicted";
	}
	
	

}
