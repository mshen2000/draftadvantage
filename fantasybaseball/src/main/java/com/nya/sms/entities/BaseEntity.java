package com.nya.sms.entities;

import java.io.Serializable;
import java.util.Date;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

@Entity
public class BaseEntity implements Serializable {

	private static final long serialVersionUID = 1;

	@Id
	Long id;

	@Index
	String createdby;
	Date createdon;
	@Index
	String modifiedby;
	Date modifiedon;

	// private BaseEntity() {}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCreatedby() {
		return createdby;
	}

	public void setCreatedby(String createdby) {
		this.createdon = new Date(java.lang.System.currentTimeMillis());
		this.createdby = createdby;
	}

	public Date getCreatedon() {
		return createdon;
	}

	public void setCreatedon(Date createdon) {
		this.createdon = createdon;
	}

	public String getModifiedby() {
		return modifiedby;
	}

	public void setModifiedby(String modifiedby) {
		this.modifiedon = new Date(java.lang.System.currentTimeMillis());
		this.modifiedby = modifiedby;
	}

	public Date getModifiedon() {
		return modifiedon;
	}

	public void setModifiedon(Date modifiedon) {
		this.modifiedon = modifiedon;
	}

}
