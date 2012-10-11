package it.unibz.thesis.beans;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ItemVariance implements Comparable<ItemVariance> {

	private int itemId;
	
	private double variance;
	
	public ItemVariance(int itemId, double variance) {
		this.itemId = itemId;
		this.variance = variance;
	}

	public int getItemId() {
		return itemId;
	}

	public double getVariance() {
		return variance;
	}
	
	@Override
	public int compareTo(ItemVariance o) {
		return Double.compare(o.variance, variance);
	}
	
	@Override
	public String toString() {	
		return itemId + "-" + variance;
	}
	
	public static void main(String[] args) {
		
		List<ItemVariance> vars = new ArrayList<ItemVariance>();
		vars.add(new ItemVariance(0, 1.0));
		vars.add(new ItemVariance(1, 2.0));
		
		Collections.sort(vars);
		
		System.out.println(vars);
		
		
	}
	
}
