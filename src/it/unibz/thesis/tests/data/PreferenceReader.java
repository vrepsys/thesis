package it.unibz.thesis.tests.data;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PreferenceReader {
	
	public static List<Preference> readList(PreferenceFile file){
		return readList(file.getPath(), file.getSeparator());
	}
	
	public static List<Preference> readList(String filePath){
		return readList(filePath, " ");
	}
	
	public static List<Preference> readList(String filePath, String separator){
		try {
			List<Preference> preferenceData = new ArrayList<Preference>();
			BufferedReader reader = new BufferedReader(new FileReader(filePath));
			String line;
			while ((line = reader.readLine()) != null) {
				String[] data = line.split(separator);
				int userId = Integer.parseInt(data[0]);
				int itemId = Integer.parseInt(data[1]);
				double rating = Double.parseDouble(data[2]);
				long time = Long.parseLong(data[3]);
				Preference mpd = new Preference(userId, itemId, time, rating);
				preferenceData.add(mpd);
			}
			return preferenceData;
		}
		catch(IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static PreferenceList read(PreferenceFile file){
		return read(file.getPath(), file.getSeparator());
	}
	
	public static PreferenceList read(String filePath, String separator){
		try {
			List<Preference> preferenceData = new ArrayList<Preference>();
			BufferedReader reader = new BufferedReader(new FileReader(filePath));
			String line;
			while ((line = reader.readLine()) != null) {
				String[] data = line.split(separator);
				int userId = Integer.parseInt(data[0]);
				int itemId = Integer.parseInt(data[1]);
				double rating = Double.parseDouble(data[2]);
				long time = Long.parseLong(data[3]);
				Preference mpd = new Preference(userId, itemId, time, rating);
				preferenceData.add(mpd);
			}
			return new PreferenceList(preferenceData);
		}
		catch(IOException e) {
			throw new RuntimeException(e);
		}
	}	
	
}
