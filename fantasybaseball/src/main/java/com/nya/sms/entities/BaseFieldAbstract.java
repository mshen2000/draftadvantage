package com.nya.sms.entities;

import java.io.Serializable;

public class BaseFieldAbstract implements Serializable {

	private static final long serialVersionUID = 1L;
	
	String name;
	String query_alias;
	String form_alias;
	boolean queryVisible;
	boolean formAccess;
	boolean formEditable;
	boolean required;
	int length; // characters
	String description;
	int priority;
	String format;

	public BaseFieldAbstract(String name) {
		super();
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getQuery_alias() {
		return query_alias;
	}

	public void setQuery_alias(String query_alias) {
		this.query_alias = query_alias;
	}

	public String getForm_alias() {
		return form_alias;
	}

	public void setForm_alias(String form_alias) {
		this.form_alias = form_alias;
	}

	public boolean isQueryVisible() {
		return queryVisible;
	}

	public void setQueryVisible(boolean queryVisible) {
		this.queryVisible = queryVisible;
	}

	public boolean isFormAccess() {
		return formAccess;
	}

	public void setFormAccess(boolean formAccess) {
		this.formAccess = formAccess;
	}

	public boolean isFormEditable() {
		return formEditable;
	}

	public void setFormEditable(boolean formEditable) {
		this.formEditable = formEditable;
	}

	public boolean isRequired() {
		return required;
	}

	public void setRequired(boolean required) {
		this.required = required;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}


	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

}
