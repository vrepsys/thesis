package it.unibz.thesis.tests.data;

import it.unibz.thesis.data.DataSplitter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class PreferenceList {

	private Map<String, Preference> preferencesMap = null;

	private Map<Integer, List<Preference>> preferencesByItem = null;

	private List<Preference> preferences;

	private List<Preference> leftPart = null;

	private List<Preference> middlePart = null;

	private List<Preference> rightPart = null;

	private void initializePreferencesMap() {
		if (preferencesMap == null) {
			preferencesMap = new HashMap<String, Preference>();
			for (Preference mpd : preferences) {
				preferencesMap.put(getKey(mpd), mpd);
			}
		}
	}

	public PreferenceList(List<Preference> preferences) {
		this.preferences = new ArrayList<Preference>(preferences);
	}

	public PreferenceList(PreferenceList prefs1, PreferenceList prefs2) {
		this.preferences = new ArrayList<Preference>();
		this.preferences.addAll(prefs1.preferences);
		this.preferences.addAll(prefs2.preferences);
		// TODO do the same for the map
	}

	public PreferenceList(List<Preference> prefs1, List<Preference> prefs2) {
		this.preferences = new ArrayList<Preference>();
		this.preferences.addAll(prefs1);
		this.preferences.addAll(prefs2);
		// TODO do the same for the map
	}

	private String getKey(Preference mpd) {
		return getKey(mpd.getUserId(), mpd.getItemId());
	}

	private String getKey(int userId, int itemId) {
		return userId + "_" + itemId;
	}

	public Preference getPreference(int userId, int itemId) {
		initializePreferencesMap();
		return preferencesMap.get(getKey(userId, itemId));
	}

	public List<Preference> getPreferences() {
		return preferences;
	}

	public void splitByTimePrc(int i) {
		DataSplitter splitter = new DataSplitter(preferences);
		splitter.splitByTime(i);
		this.leftPart = splitter.getFirstPart();
		this.middlePart = null;
		this.rightPart = splitter.getSecondPart();
	}

	public void splitRandomlyPrc(int i) {
		DataSplitter splitter = new DataSplitter(preferences);
		splitter.splitRandomly(i);
		this.leftPart = splitter.getFirstPart();
		this.middlePart = null;
		this.rightPart = splitter.getSecondPart();
	}

	public PreferenceList getLeftPart() {
		return new PreferenceList(leftPart);
	}

	public PreferenceList getRightPart() {
		return new PreferenceList(rightPart);
	}

	public PreferenceList getMiddlePart() {
		return new PreferenceList(middlePart);
	}

	public List<Preference> getLeftPartList() {
		return leftPart;
	}

	public List<Preference> getRightPartList() {
		return rightPart;
	}

	public List<Preference> getMiddlePartList() {
		return middlePart;
	}

	public int size() {
		return preferences.size();
	}

	public PreferenceList getRandomGroup(int size) {
		List<Preference> lst = new ArrayList<Preference>(preferences);
		Collections.shuffle(lst);
		return new PreferenceList(lst.subList(0, size));
	}

	public Preference getRandomPreference() {
		Random random = new Random();
		int i = random.nextInt(preferences.size());
		return preferences.get(i);
	}

	public PreferenceList removePreference(Preference pref) {
		List<Preference> newPrefs = new ArrayList<Preference>(preferences);
		newPrefs.remove(pref);
		return new PreferenceList(newPrefs);
	}

	public PreferenceList addPreferences(List<Preference> prefsToAdd) {
		List<Preference> newPrefs = new ArrayList<Preference>(preferences);
		newPrefs.addAll(prefsToAdd);
		return new PreferenceList(newPrefs);
	}

	public PreferenceList removePreferences(PreferenceList group) {
		return removePreferences(group.getPreferences());
	}

	public PreferenceList removePreferences(List<Preference> prefsToRemove) {
		List<Preference> newPrefs = new ArrayList<Preference>(preferences);
		newPrefs.removeAll(prefsToRemove);
		return new PreferenceList(newPrefs);
	}

	public void splitRandomly(int i, int j, int k) {
		Collections.shuffle(preferences);
		this.leftPart = new ArrayList<Preference>(preferences.subList(0, i));
		this.middlePart = new ArrayList<Preference>(preferences.subList(i, i
				+ j));
		this.rightPart = new ArrayList<Preference>(preferences.subList(i + j, i
				+ j + k));
	}

	public List<Integer> getItemIds() {
		Set<Integer> ids = new HashSet<Integer>();
		for (Preference p : preferences) {
			ids.add(p.getItemId());
		}
		return new ArrayList<Integer>(ids);
	}

	public List<Preference> getPreferencesByItem(Integer itemId) {
		initializePrefsByItem();
		return preferencesByItem.get(itemId);
	}

	private void initializePrefsByItem() {
		if (preferencesByItem == null) {
			preferencesByItem = new HashMap<Integer, List<Preference>>();
			for (Preference p : preferences) {
				int itemId = p.getItemId();
				List<Preference> itemPrefs = preferencesByItem.get(itemId);
				if (itemPrefs == null) {
					itemPrefs = new ArrayList<Preference>();
					preferencesByItem.put(itemId, itemPrefs);
				}
				itemPrefs.add(p);
			}
		}
	}

	public PreferenceList addPreference(Preference preference) {
		List<Preference> newPrefs = new ArrayList<Preference>(preferences);
		newPrefs.add(preference);
		return new PreferenceList(newPrefs);
	}

	public List<Integer> getUserIds() {
		Set<Integer> ids = new HashSet<Integer>();
		for (Preference p : preferences) {
			ids.add(p.getItemId());
		}
		return new ArrayList<Integer>(ids);
	}

}
