package it.unibz.thesis.tests.data;


import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class PreferenceDataWriter {

	public static void writePreferenceData(List<Preference> data, String filePath) throws IOException {		
		FileWriter writer = new FileWriter(filePath, false);			
		StringBuffer line = new StringBuffer();
		for (Preference mpd : data) {
			line.append(mpd.getUserId());
			line.append(" ");
			line.append(mpd.getItemId());
			line.append(" ");
			line.append(mpd.getValue());
			line.append(" ");
			line.append(mpd.getTime());
			line.append("\n");
			writer.write(line.toString());
			line = new StringBuffer();
		}		
		writer.flush();
		writer.close();		
	}
	
}
