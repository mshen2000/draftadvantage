package com.app.endpoints.entities;

public class PositionalZContainer {

	double replacementvalue;  // Average of the two highest replacement players
	
	double totalvalue;  // Total z value above replacement average

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


}
