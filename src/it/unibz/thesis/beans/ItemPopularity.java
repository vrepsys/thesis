package it.unibz.thesis.beans;

public class ItemPopularity implements Comparable<ItemPopularity> {

	private int itemId;
	
	private int popularity;
	
	public ItemPopularity(int itemId, int popularity) {
		this.itemId = itemId;
		this.popularity = popularity;
	}

	public int getItemId() {
		return itemId;
	}

	public double getVariance() {
		return popularity;
	}
	
	@Override
	public int compareTo(ItemPopularity o) {
		return Double.compare(o.popularity, popularity);
	}
	
	@Override
	public String toString() {	
		return itemId + "-" + popularity;
	}
	

	
}
