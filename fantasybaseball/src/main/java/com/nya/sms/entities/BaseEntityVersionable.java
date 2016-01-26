package com.nya.sms.entities;

import java.io.Serializable;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Index;

// @Subclass(index = true)
@Entity
public class BaseEntityVersionable extends BaseEntity implements Serializable {

	private static final long serialVersionUID = 1;

	@Index
	Integer versionnum;


	public Integer getVersionnum() {
		return versionnum;
	}

	public void setVersionnum(Integer versionnum) {
		this.versionnum = versionnum;
	}

	

}
