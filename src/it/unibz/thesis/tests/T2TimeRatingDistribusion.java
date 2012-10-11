package it.unibz.thesis.tests;

import it.unibz.thesis.elicitation.MyLogger;
import it.unibz.thesis.tests.data.Preference;
import it.unibz.thesis.tests.data.PreferenceFile;
import it.unibz.thesis.tests.data.PreferenceList;
import it.unibz.thesis.tests.data.PreferenceReader;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class T2TimeRatingDistribusion {

	private static Comparator<Preference> comparatorByTime = new Comparator<Preference>(){
		@Override
		public int compare(Preference mpd1, Preference mpd2) {			
			return (int) (mpd1.getTime() - mpd2.getTime());
		}
		
	};
	
	public static void main(String[] args) throws IOException {
		PreferenceList prefList = PreferenceReader.read(PreferenceFile.MOVIELENS_100K);
		List<Preference> prefs = prefList.getPreferences();
		Collections.sort(prefs, comparatorByTime);
		
		long startTime = prefs.get(0).getTime();
		long endTime = prefs.get(prefs.size()-1).getTime();
		
		
		long seconds = endTime-startTime;
		long mins = seconds / 60;
		long hours = mins / 60;
		long days = hours / 24;
		long months = days / 30;
		
		System.out.println(months);
		System.out.println(new Date(startTime*1000));
		System.out.println(new Date(endTime*1000));		
		
		Map<Integer, Map<Integer, Integer>> distributions = new HashMap<Integer, Map<Integer,Integer>>(); 

		Map<Integer, Integer> all = new HashMap<Integer,Integer>();
		
		int interval = 0;		
		
		for (Preference pref : prefs) {
			
			int itemId = pref.getItemId();
			long time = pref.getTime();
			interval = (int) (((((time - startTime) / 60) / 60) / 24) / 30);
			
			Map<Integer, Integer> distribution = distributions.get(itemId);
			if (distribution == null) {
				distribution = new HashMap<Integer, Integer>();
				distributions.put(itemId, distribution);
			}
			
			Integer distrPoint = distribution.get(interval);
			if (distrPoint == null) {
				distrPoint = 1;				
			}
			else {
				distrPoint++;
			}
			distribution.put(interval, distrPoint);
			
			distrPoint = all.get(interval);
			if (distrPoint == null) {
				distrPoint = 1;				
			}
			else {
				distrPoint++;
			}
			all.put(interval, distrPoint);
			
			
		}
		
		System.out.println("inverval="+interval);
		
		MyLogger log = new MyLogger();
		
		for (Integer itemId : distributions.keySet()) {
			Map<Integer, Integer> distribution = distributions.get(itemId);
			log.println("#itemId: " + itemId );
			log.println("#map: " + distribution );
			for (int i = 0; i < 35; i++) {
				Integer val = distribution.get(i);
				if (val == null) val = 0;
				if (i / 10 < 1) 
					log.println(i + "  " + val);
				else
					log.println(i + " " + val);				
			}
			log.emptyline();
		}
		
		log.println("#ALL");
		log.println("#map: " + all );
		for (int i = 0; i < 35; i++) {
			Integer val = all.get(i);
			if (val == null) val = 0;
			if (i / 10 < 1) 
				log.println(i + "  " + val);
			else
				log.println(i + " " + val);				
		}
		log.emptyline();
		
		log.close();
	}
	
}
