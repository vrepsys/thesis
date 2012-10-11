package it.unibz.thesis.beans;

import it.unibz.thesis.tests.data.Preference;

public class PreferenceValuePair implements Comparable<PreferenceValuePair> {

	private double value;

	private Preference pref;

	public PreferenceValuePair(Preference pref, double value) {
		this.pref = pref;
		this.value = value;
	}

	public Preference getPref() {
		return pref;
	}

	public double getValue() {
		return value;
	}

	@Override
	public int compareTo(PreferenceValuePair rs) {
		return Double.compare(rs.value, value);
	}

	@Override
	public String toString() {
		return value + "";
	}

}