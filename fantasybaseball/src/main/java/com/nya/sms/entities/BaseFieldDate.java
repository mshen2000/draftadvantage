package com.nya.sms.entities;

public class BaseFieldDate extends BaseFieldAbstract {

	private static final long serialVersionUID = 1L;
	
	String date_type; // simple, detail

	public BaseFieldDate(String name, String date_type) {
		super(name);
		this.date_type = date_type;
		if (date_type.equals("simple")) this.format = "MM-dd-yyyy";
		else if (date_type.equals("detail")) this.format = "MM-dd-yyyy hh:mm";
	}

	public String getDate_type() {
		return date_type;
	}

	public void setDate_type(String date_type) {
		this.date_type = date_type;
	}

}
