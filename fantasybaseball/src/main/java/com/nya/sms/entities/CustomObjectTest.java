package com.nya.sms.entities;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.googlecode.objectify.annotation.Embed;
import com.googlecode.objectify.annotation.EntitySubclass;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Serialize;

@EntitySubclass(index = true)
public class CustomObjectTest extends BaseEntity implements Serializable {

	private static final long serialVersionUID = 1;

	@Index	String name;
	@Index	String alias;
	@Index  String type;  // use to differentiate for UI (ie, Programs, Tests, etc)
	// @Index	String programScoreClass;
	@Serialize	Class<? extends BaseEntity> programScoreClass;
	@Serialize	Map<String, BaseFieldAbstract> fields;

	protected CustomObjectTest() {
	}

	public CustomObjectTest(String name, String alias) {
		super();
		this.name = name;
		this.alias = alias;
		this.fields = new HashMap<String, BaseFieldAbstract>();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}


	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Class<? extends BaseEntity> getProgramScoreClass() {
		return programScoreClass;
	}

	public void setProgramScoreClass(Class<? extends BaseEntity> programScoreClass) {
		this.programScoreClass = programScoreClass;
	}

	public Map<String, BaseFieldAbstract> getFields() {
		return fields;
	}

	public void setFields(Map<String, BaseFieldAbstract> fields) {
		this.fields = fields;
	}

}
