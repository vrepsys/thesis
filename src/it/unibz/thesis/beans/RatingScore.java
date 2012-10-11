package it.unibz.thesis.beans;

import it.unibz.thesis.tests.data.Preference;

public class RatingScore implements Comparable<RatingScore> {

	private double score = 0.0;

	private int count = 0;

	private Preference pref;

	public RatingScore(Preference pref) {
		this.pref = pref;
	}

	public void addScore(double score) {
		this.score += score;
		count++;
	}

	public double getScore() {
		return score / count;
	}

	public Preference getPref() {
		return pref;
	}

	public int getCount() {
		return count;
	}

	@Override
	public int compareTo(RatingScore rs) {
		return Double.compare(rs.score, score);
	}

}