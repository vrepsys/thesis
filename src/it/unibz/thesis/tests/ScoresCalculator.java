package it.unibz.thesis.tests;

import it.unibz.thesis.beans.GroupScore;
import it.unibz.thesis.beans.RatingScore;
import it.unibz.thesis.data.MAECalculator;
import it.unibz.thesis.elicitation.MyLogger;
import it.unibz.thesis.tests.data.Preference;
import it.unibz.thesis.tests.data.PreferenceList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScoresCalculator {

	private List<Preference> known;
	
	private List<Preference> unknown;
	
	private MyLogger log;
	
	private Map<Preference, RatingScore> scores = new HashMap<Preference, RatingScore>();
	
	public ScoresCalculator(List<Preference> known, List<Preference> unknown, MyLogger log) {
		this.known = known;
		this.unknown = unknown;
		this.log = log;
	}
	
	public List<RatingScore> improve() throws IOException {
		
		PreferenceList all = new PreferenceList(known, unknown);
															
		all.splitRandomlyPrc(75);
		
		List<Preference> training = all.getLeftPart().getPreferences();
		List<Preference> test = all.getRightPart().getPreferences();
		
		MAECalculator maecalc = new MAECalculator();
		
		double mae1 = maecalc.calculate(training, test);
		
			
		int parts = training.size() / 1000;
									
		List<GroupScore> results = new ArrayList<GroupScore>();
			
		double sum = 0.0;
		
		for (int i = 0; i < parts; i++) {				
			if (i % 10 == 0 || i == parts-1) log.println((i+1)+"/"+parts);
			List<Preference> newTraining = new ArrayList<Preference>(training.subList(0, i*1000));
			newTraining.addAll(new ArrayList<Preference>(training.subList((i+1)*1000, training.size())));
			List<Preference> exclude = new ArrayList<Preference>(training.subList(i*1000, (i+1)*1000));
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
				
				if (known.contains(p)) continue;
				
				RatingScore score = scores.get(p);
				if (score == null) {
					score = new RatingScore(p);
					scores.put(p, score);
				}
				score.addScore(diff_norm);					
			}				
		}
							
		List<RatingScore> scorelist = new ArrayList<RatingScore>(scores.values());
		
		return scorelist;
	}
	
}
