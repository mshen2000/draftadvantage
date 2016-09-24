package com.app.endpoints.entities;

public class PositionalZContainer {

	double replacementvalue;  // Average of the two highest replacement players
	
	double totalvalue;  // Total z value above replacement average
	
	double avgplayervalue;   // Average z score of players above replacement

	public PositionalZContainer() {

	}

	public double getReplacementvalue() {
		return replacementvalue;
	}

	public void setReplacementvalue(double replacementvalue) {
		this.replacementvalue = replacementvalue;
	}

	public double getTotalvalue() {
		return totalvalue;
	}

	public void setTotalvalue(double totalvalue) {
		this.totalvalue = totalvalue;
	}

	public double getAvgplayervalue() {
		return avgplayervalue;
	}

	public void setAvgplayervalue(double avgplayervalue) {
		this.avgplayervalue = avgplayervalue;
	}


}
