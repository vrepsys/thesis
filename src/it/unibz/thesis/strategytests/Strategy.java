package it.unibz.thesis.strategytests;

import it.unibz.thesis.tests.H07Tools;
import it.unibz.thesis.tests.data.Preference;

import java.util.List;

public interface Strategy {

	public List<Preference> getItemsToAdd(H07Tools tools, List<Preference> known,
			List<Preference> unknown, List<Preference> unknownPredicted, int size);

	public String getTitle();

	public List<Preference> getItemsToAdd(H07Tools tools,
			List<Preference> knownList, List<Preference> unknownList, int uid, int addOnce);

	public void refreshPredictions();

}