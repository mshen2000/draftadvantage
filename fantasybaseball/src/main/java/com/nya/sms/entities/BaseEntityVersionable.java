package com.nya.sms.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.EntitySubclass;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.OnSave;

@EntitySubclass(index = true)
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
