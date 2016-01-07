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
import com.googlecode.objectify.annotation.Load;
import com.googlecode.objectify.annotation.OnSave;

@EntitySubclass(index = true)
public class ProgramScore extends BaseEntity implements Serializable {

	private static final long serialVersionUID = 1;

	@Index
	Long studentid;
	@Index
	String studentname;
	@Index
	String programname;
	@Index
	String group;
	@Index
	Date scoredate;
	int points;
	String notes;
	@Index
	@Load
	Ref<Points> pointsobject;

	protected ProgramScore() {
	}

	public ProgramScore(Long studentid) {

		this.studentid = studentid;

	}

	@OnSave
	void onSave() {

		if (studentid != null) {

			Student student = ObjectifyService.ofy().load().type(Student.class)
					.id(studentid).get();

			this.studentname = student.getStudentFullName();

			System.out.println("--> Setting program score student name to: "
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

	public String getProgramname() {
		return programname;
	}

	public void setProgramname(String programname) {
		this.programname = programname;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public Date getScoredate() {
		return scoredate;
	}

	public void setScoredate(Date scoredate) {
		this.scoredate = scoredate;
	}

	public int getPoints() {
		return points;
	}

	public void setPoints(int points) {
		this.points = points;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public Ref<Points> getPointsobject() {
		return pointsobject;
	}

	public void setPointsobject(Ref<Points> pointsobject) {
		this.pointsobject = pointsobject;
	}

	public List<Integer> parseProgramScore(String score) {

		System.out.println("Score in parseProgramScore: " + score);
		List<Integer> scores = new ArrayList<Integer>();
		String delim = "/";

		if (score == null)
			return scores;

		String[] tokens = ((String) score).split(delim);
		scores.add(Integer.parseInt(tokens[0]));
		scores.add(Integer.parseInt(tokens[1]));

		return scores;

	}

}
