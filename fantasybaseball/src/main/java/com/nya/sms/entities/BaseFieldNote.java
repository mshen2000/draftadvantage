package com.nya.sms.entities;

public class BaseFieldNote extends BaseFieldAbstract {

	int lines;

	public BaseFieldNote(String name) {
		super(name);
		this.lines = 2;
		// TODO Auto-generated constructor stub
	}

	public int getLines() {
		return lines;
	}

	public void setLines(int lines) {
		this.lines = lines;
	}

}
