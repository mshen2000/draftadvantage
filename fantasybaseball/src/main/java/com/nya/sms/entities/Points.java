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
public class Points extends BaseEntity implements Serializable {

	private static final long serialVersionUID = 1;

	@Index
	Long studentid;
	@Index
	String studentname;
	@Index
	Date pointdate;
	@Index
	String type;
	@Index
	int points;
	@Index
	String note;

	public Points() {
	}

	public Points(Long studentid) {

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

			// if (student.middlename != null && !student.middlename.isEmpty())
			// this.studentname = student.firstname + " " + student.middlename +
			// " " + student.lastname;
			// else
			// this.studentname = student.firstname + " " + student.lastname;

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

	public Date getPointdate() {
		return pointdate;
	}

	public void setPointdate(Date pointdate) {
		this.pointdate = pointdate;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getPoints() {
		return points;
	}

	public void setPoints(int points) {
		this.points = points;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

}
