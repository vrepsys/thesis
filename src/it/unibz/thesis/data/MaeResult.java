package it.unibz.thesis.data;

public class MaeResult {

	private double mae;
	
	private double coverage;
	
	public MaeResult(double mae) {
		this.mae = mae;
	}
	
	public MaeResult(double mae, double coverage) {
		this.mae = mae;
		this.coverage = coverage;
	}
	
	public double getMae() {
		return mae;
	}
	
	public double getCoverage() {
		return coverage;
	}
	
	@Override
	public String toString() {
		return "mae: " + mae + " coverage: " + coverage;
	}
	
}
