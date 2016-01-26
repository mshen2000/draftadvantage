package com.nya.sms.entities;

import java.io.Serializable;
import java.util.List;

import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.annotation.Subclass;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.OnSave;

@Subclass(index = true)
public class ProgramScoreNYAOnlineMath extends ProgramScore implements
		Serializable {

	private static final long serialVersionUID = 1;

	@Index
	String level;
	@Index
	String block;
	String score;
	// int possiblescore;
	double scorepercent;

	public ProgramScoreNYAOnlineMath() {

		this.programname = "NYA Online Math";

	}

	public ProgramScoreNYAOnlineMath(Long studentid) {

		this.studentid = studentid;
		this.programname = "NYA Online Math";

	}

	@OnSave
	void onSave() {

		List<Integer> scores = parseProgramScore(score);

		if (scores.get(0) > 0) {

			this.scorepercent = (double) scores.get(0) / scores.get(1);

		}

		if (studentid != null) {

			Student student = ObjectifyService.ofy().load().type(Student.class)
					.id(studentid).now();

			this.studentname = student.getStudentFullName();

		} else {

			this.studentname = null;

		}

	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public String getBlock() {
		return block;
	}

	public void setBlock(String block) {
		this.block = block;
	}

	public String getScore() {
		return score;
	}

	public void setScore(String score) {
		this.score = score;
	}

	// public int getPossiblescore() {
	// return possiblescore;
	// }
	//
	// public void setPossiblescore(int possiblescore) {
	// this.possiblescore = possiblescore;
	// }

	public double getScorepercent() {
		return scorepercent;
	}

	public void setScorepercent(double scorepercent) {
		this.scorepercent = scorepercent;
	}

}
