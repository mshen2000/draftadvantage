package com.nya.sms.entities;

import java.util.ArrayList;
import java.util.List;

public class BaseFieldSelect extends BaseFieldAbstract {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	boolean addNewValues;
	List<String> values;

	public BaseFieldSelect(String name) {
		super(name);
		this.addNewValues = false;
		this.values = new ArrayList<String>();
	}

	public boolean isAddNewValues() {
		return addNewValues;
	}

	public void setAddNewValues(boolean addNewValues) {
		this.addNewValues = addNewValues;
	}

	public List<String> getValues() {
		return values;
	}

	public void setValues(List<String> values) {
		this.values = values;
	}

}
