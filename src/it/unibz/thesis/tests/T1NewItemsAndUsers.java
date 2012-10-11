package it.unibz.thesis.tests;

import it.unibz.thesis.data.DataSplitter;
import it.unibz.thesis.elicitation.MyLogger;
import it.unibz.thesis.tests.data.Preference;
import it.unibz.thesis.tests.data.PreferenceFile;
import it.unibz.thesis.tests.data.PreferenceReader;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class T1NewItemsAndUsers {

	public static void main(String[] args) throws IOException {

		List<Preference> preferences = PreferenceReader.readList(PreferenceFile.MOVIELENS_100K);
		
		Set<Integer> items = new HashSet<Integer>();
		Set<Integer> users = new HashSet<Integer>();
		
		Map<Integer, Integer> itemsDistr = new HashMap<Integer, Integer>();
		Map<Integer, Integer> usersDistr = new HashMap<Integer, Integer>();
		
		Collections.sort(preferences, DataSplitter.COMPARATOR_BY_TIME);
		
		long startTime = preferences.get(0).getTime(); 
		
		for (Preference pref : preferences) {
			int itemId = pref.getItemId();
			int userId = pref.getUserId();
			long time = pref.getTime();
			int interval = (int) (((((time - startTime) / 60) / 60) / 24) / 30);
			if (!items.contains(itemId)) {
				items.add(itemId);
				Integer intervalValue = itemsDistr.get(interval);
				if (intervalValue == null) {
					intervalValue = 0;
				}
				intervalValue++;
				itemsDistr.put(interval, intervalValue);				
			}
			if (!users.contains(userId)) {
				users.add(userId);
				Integer intervalValue = usersDistr.get(interval);
				if (intervalValue == null) {
					intervalValue = 0;
				}
				intervalValue++;
				usersDistr.put(interval, intervalValue);				
			}
		}
		
		MyLogger log = new MyLogger();
		
		log.println("#new items");		
		for (int i = 0; i < 35; i++) {
			Integer val = itemsDistr.get(i);
			if (val == null) val = 0;
			if (i / 10 < 1) 
				log.println(i + "  " + val);
			else
				log.println(i + " " + val);				
		}
		
		log.emptyline();
		log.println("#new users");		
		for (int i = 0; i < 35; i++) {
			Integer val = usersDistr.get(i);
			if (val == null) val = 0;
			if (i / 10 < 1) 
				log.println(i + "  " + val);
			else
				log.println(i + " " + val);				
		}
		
		log.close();
		
	}
	
}
