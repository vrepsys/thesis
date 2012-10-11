package it.unibz.thesis.strategytests;

import it.unibz.thesis.tests.H07Tools;
import it.unibz.thesis.tests.data.Preference;

import java.util.List;

public class PopularityStrategy implements Strategy {

	@Override
	public List<Preference> getItemsToAdd(H07Tools tools, List<Preference> known,
			List<Preference> unknown, List<Preference> unknownPredicted, int size) {
		return tools.mostPopularItemPrefs(known, unknown, size);
	}

	@Override
	public String getTitle() {
		return "popularity";
	}

}
