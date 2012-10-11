package it.unibz.thesis.data;

import it.unibz.thesis.tests.data.Preference;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class DataSplitter {

	private List<Preference> firstPart = null;
	
	private List<Preference> secondPart = null;
	
	private Collection<Preference> preferences = null;
	
	public DataSplitter(Collection<Preference> prefs) {
		this.preferences = new ArrayList<Preference>(prefs);			
	}

	public List<Preference> getFirstPart() {
		return firstPart;
	}

	public List<Preference> getSecondPart() {
		return secondPart;
	}

	public void splitByTime(int percent) {	
		List<Preference> prefs = new ArrayList<Preference>(this.preferences); 
		Collections.sort(prefs, COMPARATOR_BY_TIME);	
		int size = preferences.size();
		int cut = (int) ((size / 100.0) * percent);
		this.firstPart = new ArrayList<Preference>(prefs.subList(0, cut));
		this.secondPart = new ArrayList<Preference>(prefs.subList(cut, size));
	}
	
	public static final Comparator<Preference> COMPARATOR_BY_TIME = new Comparator<Preference>(){
		@Override
		public int compare(Preference mpd1, Preference mpd2) {			
			return (int) (mpd1.getTime() - mpd2.getTime());
		}
		
	};
	
	
	public void splitRandomly(int firstPartPercent) {
		int size = preferences.size();
		int firstPartSize =  (int) ((size / 100.0) * firstPartPercent);
		List<Preference> prefs = new ArrayList<Preference>(this.preferences); 
		Collections.shuffle(prefs);		
		this.firstPart = new ArrayList<Preference>(prefs.subList(0, firstPartSize));
		this.secondPart = new ArrayList<Preference>(prefs.subList(firstPartSize, size));
	}
	
	
	
}
