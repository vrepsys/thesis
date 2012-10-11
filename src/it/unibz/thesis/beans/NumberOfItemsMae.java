package it.unibz.thesis.beans;

public class NumberOfItemsMae {

	private int nrOfItems;
	
	private double mae;
	
	public NumberOfItemsMae(int nrOfItems, double mae) {
		this.nrOfItems = nrOfItems;
		this.mae = mae;
	}

	public int getNrOfItems() {
		return nrOfItems;
	}

	public double getMae() {
		return mae;
	}
	
	public void addMae(double mae) {
		this.mae += mae; 
	}

	public void divide(int size) {
		this.mae = this.mae / size;		
	}
	
	@Override
	public String toString() {	
		return nrOfItems + "-" + mae;
	}
	
	
}
