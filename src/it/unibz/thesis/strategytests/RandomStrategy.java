package it.unibz.thesis.strategytests;

import it.unibz.thesis.tests.H07Tools;
import it.unibz.thesis.tests.data.Preference;

import java.util.List;

public class RandomStrategy implements Strategy {

	@Override
	public List<Preference> getItemsToAdd(H07Tools tools, List<Preference> known,
			List<Preference> unknown, List<Preference> unknownPredicted, int size) {
		return tools.randomGroup(unknown, size);
	}

	@Override
	public String getTitle() {
		return "random";
	}

	@Override
	public List<Preference> getItemsToAdd(H07Tools tools,
			List<Preference> knownList, List<Preference> unknownList, int uid, int size) {
		return tools.randomGroup(unknownList, uid, size);
	}
	
	@Override
	public void refreshPredictions() {
	}

}
