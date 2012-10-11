package it.unibz.thesis.strategytests;

import it.unibz.thesis.tests.H07Tools;
import it.unibz.thesis.tests.data.Preference;

import java.util.List;

public class ScoreStrategy implements Strategy {

	public List<Preference> getItemsToAdd(H07Tools tools, List<Preference> known, List<Preference> unknown, List<Preference> unknownPredicted, int size) {
		return tools.bestScoringPreferences(known, unknown, unknownPredicted, size);
	}
	
	public String getTitle() {
		return "score";
	}
	
}
