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
public class TestScoreInternal extends TestScore implements Serializable {

	private static final long serialVersionUID = 1;

	@Index
	String type;
	@Index
	String subject;
	@Index
	String title;
	String score;
	double scorepercent;

	public TestScoreInternal() {
	}

	public TestScoreInternal(Long studentid) {

		this.studentid = studentid;

	}

	@OnSave
	void onSave() {

		List<Integer> scores = parseProgramScore(score);

		if (scores.get(0) > 0) {

			this.scorepercent = (double) scores.get(0) / scores.get(1);

		}

		if (studentid != null) {

			Student student = ObjectifyService.ofy().load().type(Student.class)
					.id(studentid).get();

			this.studentname = student.getStudentFullName();

		} else {

			this.studentname = null;

		}

	}
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}


	public String getScore() {
		return score;
	}

	public void setScore(String score) {
		this.score = score;
	}

	public double getScorepercent() {
		return scorepercent;
	}

	public void setScorepercent(double scorepercent) {
		this.scorepercent = scorepercent;
	}

}
