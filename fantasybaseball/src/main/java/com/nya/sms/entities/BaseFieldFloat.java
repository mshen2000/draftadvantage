package com.nya.sms.entities;

public class BaseFieldFloat extends BaseFieldAbstract {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	int sigdig;

	public BaseFieldFloat(String name) {
		super(name);
		this.sigdig = 1;
	}

	public int getSigdig() {
		return sigdig;
	}

	public void setSigdig(int sigdig) {
		this.sigdig = sigdig;
	}

}
