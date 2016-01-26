package com.nya.sms.entities;

import java.io.Serializable;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.OnSave;

//@Subclass(index = true)
@Entity
public class HealthRule extends BaseEntityVersionable implements Serializable {

	private static final long serialVersionUID = 1;

	@Index
	String rulename;
	@Index
	String description;
	@Index
	String detail;
	@Index
	String status;
	String code;


	public HealthRule() {
	}

	public HealthRule(String rulename) {

		this.rulename = rulename;

	}
	
	@OnSave
	void onSave() {


	}

	public String getRulename() {
		return rulename;
	}

	public void setRulename(String rulename) {
		this.rulename = rulename;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}


}
