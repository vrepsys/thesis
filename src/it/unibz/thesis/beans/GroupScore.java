package it.unibz.thesis.beans;

import it.unibz.thesis.tests.data.Preference;

import java.util.List;

public class GroupScore implements Comparable<GroupScore> {

	private List<Preference> prefs;

	private double diff;

	public GroupScore(List<Preference> prefs, double diff) {
		this.prefs = prefs;
		this.diff = diff;
	}

	@Override
	public int compareTo(GroupScore o) {
		return (int) Math.signum(o.diff - diff);
	}

	public List<Preference> getPrefs() {
		return prefs;
	}

	public double getDiff() {
		return diff;
	}

	@Override
	public String toString() {
		return String.valueOf(diff);
	}
}