package it.unibz.thesis.tests.data;

public class Preference implements Comparable<Preference>{

	private int userId;
	
	private int itemId;
	
	private double value;
	
	private long time;
	
	public int getUserId() {
		return userId;
	}

	public int getItemId() {
		return itemId;
	}

	public double getValue() {
		return value;
	}

	public long getTime() {
		return time;
	}

	public Preference(int userId, int itemId, long time, double value) {
		this.userId = userId;
		this.itemId = itemId;
		this.value = value;
		this.time = time;
	}

	public Preference copy() {		
		return new Preference(userId, itemId, time, value);
	}
	
	public Preference binaryCopy() {
		return new Preference(userId, itemId, time, 1);
	}
	
	@Override
	public boolean equals(Object obj) {		
		if (!(obj instanceof Preference)) return false;
		Preference p = (Preference) obj;
		return (userId == p.userId && itemId == p.itemId && value == p.value && time == p.time);
	}
	
	@Override
	public int hashCode() {		
		return userId + itemId;
	}

	@Override
	public int compareTo(Preference o) {
		return Double.compare(o.value, value);
	}
	
	@Override
	public String toString() {	
		return String.valueOf(value);
	}
	
}
