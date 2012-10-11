package it.unibz.thesis.tests;

import it.unibz.thesis.beans.GroupScore;
import it.unibz.thesis.beans.ItemPopularity;
import it.unibz.thesis.beans.ItemVariance;
import it.unibz.thesis.beans.PreferenceValuePair;
import it.unibz.thesis.beans.RatingScore;
import it.unibz.thesis.data.MAECalculator;
import it.unibz.thesis.elicitation.MyLogger;
import it.unibz.thesis.factorization.Factorization;
import it.unibz.thesis.tests.data.Preference;
import it.unibz.thesis.tests.data.PreferenceList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class H07Tools {

	private MyLogger log;
	private int features;

	public H07Tools(MyLogger log, int features) {
		this.log = log;
		this.features = features;
	}

	public H07Tools(MyLogger log) {
		this.log = log;
		this.features = 8;
	}

	public List<Preference> predict(List<Preference> training,
			List<Preference> toPredict) {
		Factorization fact = new Factorization(training, features);
		fact.initialize();

		List<Preference> result = new ArrayList<Preference>();
		for (Preference p : toPredict) {
			Double rating = fact.predictRating(p.getItemId(), p.getUserId());
			if (rating != null) {
				result.add(new Preference(p.getUserId(), p.getItemId(), p
						.getTime(), rating));
			}
		}
		return result;
	}
	
	public List<Preference> predict(List<Preference> training,
			List<Preference> toPredict, int uid) {
		toPredict = getUsersPreferences(toPredict, uid);
		return predict(training, toPredict);
	}

	public List<RatingScore> scores(List<Preference> known,
			List<Preference> unknown, int iterations) {

		PreferenceList all = new PreferenceList(known, unknown);

		Map<Preference, RatingScore> scores = new HashMap<Preference, RatingScore>();

		MAECalculator maecalc = new MAECalculator(features);

		for (int j = 0; j < iterations; j++) {
			log.println("scores: " + (j + 1) + "/" + iterations);
			long start = System.currentTimeMillis();

			all.splitRandomlyPrc(75);
			List<Preference> training = all.getLeftPart().getPreferences();
			List<Preference> test = all.getRightPart().getPreferences();
			double mae1 = maecalc.calculate(training, test);
			double sum = 0.0;

			int parts = training.size() / 1000;

			List<GroupScore> results = new ArrayList<GroupScore>();

			for (int i = 0; i < parts; i++) {
				if (i % 10 == 0 || i == parts - 1)
					log.println((i + 1) + "/" + parts);
				List<Preference> newTraining = new ArrayList<Preference>(
						training.subList(0, i * 1000));
				newTraining.addAll(new ArrayList<Preference>(training.subList(
						(i + 1) * 1000, training.size())));
				List<Preference> exclude = new ArrayList<Preference>(training
						.subList(i * 1000, (i + 1) * 1000));
				double mae2 = maecalc.calculate(newTraining, test);
				double diff = mae2 - mae1;
				results.add(new GroupScore(exclude, diff));
				sum += diff;
			}

			double avg = sum / parts;

			for (GroupScore gr : results) {

				double diff = gr.getDiff();
				double diff_norm = diff - avg;

				for (Preference p : gr.getPrefs()) {

					if (known.contains(p))
						continue;

					RatingScore score = scores.get(p);
					if (score == null) {
						score = new RatingScore(p);
						scores.put(p, score);
					}
					score.addScore(diff_norm);
				}
			}
			long end = System.currentTimeMillis();
			long timeleft = ((end - start) * (iterations - j - 1));
			timeleft = timeleft / 1000 / 60;
			log.println("scores: iteration took (s) : "
					+ ((end - start) / 1000) + " time left (m): " + timeleft);
		}

		List<RatingScore> scorelist = new ArrayList<RatingScore>(scores
				.values());

		return scorelist;
	}

	public List<Preference> bestScoringPreferences(List<Preference> known,
			List<Preference> unknown, List<Preference> unknownPredicted,
			int size) {
		List<RatingScore> unknown_scores = scores(known, unknownPredicted, 10);
		List<Preference> bestScoringPreferences = bestScoringPreferences(
				unknown, unknown_scores, size);
		return bestScoringPreferences;
	}

	public List<Preference> bestScoringPreferences(List<Preference> unknown,
			List<RatingScore> scores, int size) {
		Collections.sort(scores);
		List<Preference> prefs = new ArrayList<Preference>();
		PreferenceList unknownlist = new PreferenceList(unknown);
		for (int i = 0; i < size; i++) {
			Preference pref_pred = scores.get(i).getPref();
			Preference pref = unknownlist.getPreference(pref_pred.getUserId(),
					pref_pred.getItemId());
			if (pref == null) {
				log.println("SOMETHING WRONG, PREF IS NULL");
			}
			prefs.add(pref);
		}
		return prefs;
	}

	public List<Preference> bestScoringPreferences(List<RatingScore> scores,
			int size) {
		Collections.sort(scores);
		List<Preference> prefs = new ArrayList<Preference>();
		for (int i = 0; i < size; i++) {
			Preference pref_pred = scores.get(i).getPref();
			prefs.add(pref_pred);
		}
		return prefs;
	}

	/**
	 * Returns a list of preferences from prefs1 that have the highest ratings
	 * in prefs2
	 * 
	 * @param prefs1
	 * @param prefs2
	 * @param size
	 * @return
	 */
	public List<Preference> highestPreferences(List<Preference> prefs1,
			List<Preference> prefs2, int size) {
		Collections.sort(prefs2);
		List<Preference> prefs = new ArrayList<Preference>();
		PreferenceList unknownlist = new PreferenceList(prefs1);
		for (int i = 0; i < size; i++) {
			Preference pref_pred = prefs2.get(i);
			Preference pref = unknownlist.getPreference(pref_pred.getUserId(),
					pref_pred.getItemId());
			if (pref == null) {
				log.println("SOMETHING WRONG, PREF IS NULL");
			}
			prefs.add(pref);
		}
		return prefs;
	}

	public List<Preference> randomGroup(List<Preference> unknown, int size) {
		Collections.shuffle(unknown);
		return new ArrayList<Preference>(unknown.subList(0, size));
	}
	
	public List<Preference> randomGroup(List<Preference> unknown, int uid, int size) {
		unknown = getUsersPreferences(unknown, uid);
		Collections.shuffle(unknown);
		if (unknown.size() < size) {
			size = unknown.size();
			// TODO: maybe I should split the dataset in a way that
			// each user has enough items in all the parts of the split
//			System.out.println("warning: returning less items than wanted");
//			log.print("warning: returning less items than wanted");
			
		}
		return new ArrayList<Preference>(unknown.subList(0, size));
	}
	
	public List<Preference> getUsersPreferences(List<Preference> all, Integer uid) {
		List<Preference> result = new ArrayList<Preference>();
		for (Preference p : all) {
			if (p.getUserId() == uid) {
				result.add(p);
			}
		}
		return result;
	}

	public double mae(List<Preference> known,
			List<Preference> additionalratings, List<Preference> test) {

		known = new ArrayList<Preference>(known);
		known.addAll(additionalratings);

		MAECalculator maecalc = new MAECalculator(features);

		double mae = maecalc.calculate(known, test);

		return mae;
	}


	/**
	 * Returns a list of preferences from prefs1 that have the lowest ratings in
	 * prefs2
	 * 
	 * @param prefs1
	 * @param prefs2
	 * @param size
	 * @return
	 */
	public List<Preference> lowestPreferences(List<Preference> prefs1,
			List<Preference> prefs2, int size) {
		Collections.sort(prefs2);
		Collections.reverse(prefs2);
		List<Preference> prefs = new ArrayList<Preference>();
		PreferenceList unknownlist = new PreferenceList(prefs1);

		for (int i = 0; i < size; i++) {
			Preference pref_pred = prefs2.get(i);
			Preference pref = unknownlist.getPreference(pref_pred.getUserId(),
					pref_pred.getItemId());
			prefs.add(pref);
		}
		return prefs;
	}

	/**
	 * Returns a list of preferences with the lowest ratings
	 * 
	 * @param prefs
	 * @param size
	 * @return
	 */
	public List<Preference> lowestPreferences(List<Preference> prefs, int size) {
		Collections.sort(prefs);
		Collections.reverse(prefs);
		List<Preference> result = new ArrayList<Preference>();
		for (int i = 0; i < size; i++) {
			Preference pref_pred = prefs.get(i);
			result.add(pref_pred);
		}
		return result;
	}

	/**
	 * Returns a list of preferences with the highest ratings
	 * 
	 * @param prefs
	 * @param size
	 * @return
	 */
	public List<Preference> highestPreferences(List<Preference> prefs, int size) {
		Collections.sort(prefs);
		List<Preference> result = new ArrayList<Preference>();
		for (int i = 0; i < size; i++) {
			Preference pref_pred = prefs.get(i);
			result.add(pref_pred);
		}
		return result;
	}

	public List<Preference> highestAndLowestPreferences(
			List<Preference> unknown, List<Preference> predicted_unknown,
			int size) {
		int first_half = size / 2;
		int second_half = size - first_half;
		Collections.sort(predicted_unknown);
		PreferenceList unknownlist = new PreferenceList(unknown);
		List<Preference> prefs = new ArrayList<Preference>();
		for (int i = 0; i < first_half; i++) {
			Preference pref_pred = predicted_unknown.get(i);
			Preference pref = unknownlist.getPreference(pref_pred.getUserId(),
					pref_pred.getItemId());
			if (pref == null) {
				log.println("SOMETHING WRONG, PREF IS NULL");
			}
			prefs.add(pref);
		}
		Collections.reverse(predicted_unknown);
		for (int i = 0; i < second_half; i++) {
			Preference pref_pred = predicted_unknown.get(i);
			Preference pref = unknownlist.getPreference(pref_pred.getUserId(),
					pref_pred.getItemId());
			if (pref == null) {
				log.println("SOMETHING WRONG, PREF IS NULL");
			}
			prefs.add(pref);
		}
		return prefs;
	}

	public List<Preference> worstPredictedPreferences(List<Preference> unknown,
			List<Preference> unknownPredicted, int size) {

		PreferenceList real = new PreferenceList(unknown);

		List<PreferenceValuePair> errors = new ArrayList<PreferenceValuePair>();

		for (Preference pref : unknownPredicted) {
			Preference realPref = real.getPreference(pref.getUserId(), pref
					.getItemId());
			double predictedValue = pref.getValue();
			double realValue = realPref.getValue();
			double error = Math.abs(predictedValue - realValue);
			errors.add(new PreferenceValuePair(realPref, error));
		}

		Collections.sort(errors);

		List<Preference> result = new ArrayList<Preference>();

		System.out.println(errors);

		for (int i = 0; i < size; i++) {
			result.add(errors.get(i).getPref());

		}

		return result;
	}

	public List<Preference> worstPredictedPreferences_predictedValue(
			List<Preference> unknown, List<Preference> unknownPredicted,
			int size) {

		PreferenceList real = new PreferenceList(unknown);

		List<PreferenceValuePair> errors = new ArrayList<PreferenceValuePair>();

		for (Preference pref : unknownPredicted) {
			Preference realPref = real.getPreference(pref.getUserId(), pref
					.getItemId());
			double predictedValue = pref.getValue();
			double realValue = realPref.getValue();
			double error = Math.abs(predictedValue - realValue);
			errors.add(new PreferenceValuePair(pref, error));
		}

		Collections.sort(errors);

		List<Preference> result = new ArrayList<Preference>();

		for (int i = 0; i < size; i++) {
			result.add(errors.get(i).getPref());
		}

		return result;
	}

	public List<Preference> mostDiverseItemPrefs(List<Preference> knownList,
			List<Preference> unknownList, int size) {

		PreferenceList known = new PreferenceList(knownList);

		PreferenceList unknown = new PreferenceList(unknownList);

		List<Integer> itemIds = known.getItemIds();

		List<ItemVariance> variances = new ArrayList<ItemVariance>();

		for (Integer itemId : itemIds) {

			List<Preference> itemPrefs = known.getPreferencesByItem(itemId);

			Double variance = variance(itemPrefs);
			
			variances.add(new ItemVariance(itemId, variance));
			
		}
		
		Collections.sort(variances);
		
		List<Preference> result = new ArrayList<Preference>();
		
		int i = 0;
		
		while (result.size() < size) {			
			int itemId = variances.get(i).getItemId();			
			List<Preference> prefs = unknown.getPreferencesByItem(itemId);
			if (prefs != null) {
				result.addAll(prefs);
			}			
			i++;			
		}

		if (result.size() > size) result = result.subList(0, size);
		
		return result;
	}


	private double variance(List<Preference> preferences) {

		long n = 0;
		double mean = 0;
		double s = 0.0;

		for (Preference p : preferences) {
			n++;
			double x = p.getValue();
			double delta = x - mean;
			mean += delta / n;
			s += delta * (x - mean);
		}

		// if you want to calculate std deviation
		// of a sample change this to (s/(n-1))
		return (s / n);

	}

	public List<Preference> mostPopularItemPrefs(List<Preference> knownList,
			List<Preference> unknownList, int size) {

		PreferenceList known = new PreferenceList(knownList);

		PreferenceList unknown = new PreferenceList(unknownList);

		List<Integer> itemIds = known.getItemIds();

		List<ItemPopularity> popularities = new ArrayList<ItemPopularity>();

		for (Integer itemId : itemIds) {

			List<Preference> itemPrefs = known.getPreferencesByItem(itemId);

			int numberOfItems = itemPrefs.size();
			
			popularities.add(new ItemPopularity(itemId, numberOfItems));
			
		}
		
		Collections.sort(popularities);
		
		List<Preference> result = new ArrayList<Preference>();
		
		int i = 0;
		
		while (result.size() < size) {			
			int itemId = popularities.get(i).getItemId();			
			List<Preference> prefs = unknown.getPreferencesByItem(itemId);
			if (prefs != null) {
				result.addAll(prefs);
			}			
			i++;			
		}

		if (result.size() > size) result = result.subList(0, size);
		
		return result;
	}

	public List<Preference> lowestPreferences(List<Preference> predicted, List<Preference> unknown,
			int uid, int size) {
				
		predicted = getUsersPreferences(predicted, uid);
		
		Collections.sort(predicted);
		Collections.reverse(predicted);
		
		List<Preference> prefs = new ArrayList<Preference>();
		PreferenceList unknownlist = new PreferenceList(getUsersPreferences(unknown, uid));
		
		if (size > predicted.size()) size = predicted.size();
		
		for (int i = 0; i < size; i++) {
			Preference pref_pred = predicted.get(i);
			Preference pref = unknownlist.getPreference(pref_pred.getUserId(),
					pref_pred.getItemId());
			prefs.add(pref);
		}
		
		return prefs;
	}
	

	

}
