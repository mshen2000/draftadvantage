package com.nya.sms.entities;

import java.io.Serializable;
import java.util.Date;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Index;

//@Subclass(index = true)
@Entity
public class StudentGroupLogItem extends BaseEntity implements Serializable {

	private static final long serialVersionUID = 1;

	@Index
	Long studentid;
	@Index
	String event;
	@Index
	String group;
	@Index
	Date createdon;

	protected StudentGroupLogItem() {
	}

	public StudentGroupLogItem(Long studentid) {

		this.studentid = studentid;

	}

	public Long getStudentid() {
		return studentid;
	}

	public void setStudentid(Long studentid) {
		this.studentid = studentid;
	}

	public String getEvent() {
		return event;
	}

	public void setEvent(String event) {
		this.event = event;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public Date getCreatedon() {
		return createdon;
	}

	public void setCreatedon(Date createdon) {
		this.createdon = createdon;
	}

}
