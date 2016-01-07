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
public class SchoolSchedule extends BaseEntity implements Serializable {

	private static final long serialVersionUID = 1;

	@Index
	Long studentid;
	@Index
	String studentname;
	@Index
	Date scheddate;
	@Index
	String period;
	@Index
	String classname;
	@Index
	String teacher;
	@Index
	String contactinfo;

	public SchoolSchedule() {
	}

	public SchoolSchedule(Long studentid) {

		this.studentid = studentid;

	}
	
	@OnSave
	void onSave() {

		if (studentid != null) {

			Student student = ObjectifyService.ofy().load().type(Student.class)
					.id(studentid).get();

			this.studentname = student.getStudentFullName();

			System.out.println("--> Setting points student name to: "
					+ studentname);

		} else {

			this.studentname = null;

		}

	}
	

	public Long getStudentid() {
		return studentid;
	}

	public void setStudentid(Long studentid) {
		this.studentid = studentid;
	}

	public String getStudentname() {
		return studentname;
	}

	public void setStudentname(String studentname) {
		this.studentname = studentname;
	}

	public Date getScheddate() {
		return scheddate;
	}

	public void setScheddate(Date scheddate) {
		this.scheddate = scheddate;
	}

	public String getPeriod() {
		return period;
	}

	public void setPeriod(String period) {
		this.period = period;
	}

	public String getClassname() {
		return classname;
	}

	public void setClassname(String classname) {
		this.classname = classname;
	}

	public String getTeacher() {
		return teacher;
	}

	public void setTeacher(String teacher) {
		this.teacher = teacher;
	}

	public String getContactinfo() {
		return contactinfo;
	}

	public void setContactinfo(String contactinfo) {
		this.contactinfo = contactinfo;
	}

}
