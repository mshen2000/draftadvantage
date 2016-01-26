package com.nya.sms.entities;

import java.io.Serializable;
import java.util.List;

import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.annotation.Subclass;
import com.googlecode.objectify.annotation.OnSave;

@Subclass(index = true)
public class ProgramScoreNYAOnlineAssess extends ProgramScore implements
		Serializable {

	private static final long serialVersionUID = 1;

	String addition_score;
	String subtraction_score;
	String multiplication_score;
	String division_score;

	// int addition_scorepossible;
	// int subtraction_scorepossible;
	// int multiplication_scorepossible;
	// int division_scorepossible;

	double addition_scorepercent;
	double subtraction_scorepercent;
	double multiplication_scorepercent;
	double division_scorepercent;

	public ProgramScoreNYAOnlineAssess() {

		this.programname = "NYA Online Assessment";

	}

	public ProgramScoreNYAOnlineAssess(Long studentid) {

		this.studentid = studentid;
		this.programname = "NYA Online Assessment";

	}

	@OnSave
	void onSave() {

		List<Integer> addition_scores = parseProgramScore(addition_score);
		List<Integer> subtraction_scores = parseProgramScore(subtraction_score);
		List<Integer> multiplication_scores = parseProgramScore(multiplication_score);
		List<Integer> division_scores = parseProgramScore(division_score);

		if (addition_scores.get(0) > 0)
			this.addition_scorepercent = (double) addition_scores.get(0)
					/ addition_scores.get(1);
		if (subtraction_scores.get(0) > 0)
			this.subtraction_scorepercent = (double) subtraction_scores.get(0)
					/ subtraction_scores.get(1);
		if (multiplication_scores.get(0) > 0)
			this.multiplication_scorepercent = (double) multiplication_scores
					.get(0) / multiplication_scores.get(1);
		if (division_scores.get(0) > 0)
			this.division_scorepercent = (double) division_scores.get(0)
					/ division_scores.get(1);

		if (studentid != null) {

			Student student = ObjectifyService.ofy().load().type(Student.class)
					.id(studentid).now();

			this.studentname = student.getStudentFullName();

		} else {

			this.studentname = null;

		}

	}

	public String getAddition_score() {
		return addition_score;
	}

	public void setAddition_score(String addition_score) {
		this.addition_score = addition_score;
	}

	public String getSubtraction_score() {
		return subtraction_score;
	}

	public void setSubtraction_score(String subtraction_score) {
		this.subtraction_score = subtraction_score;
	}

	public String getMultiplication_score() {
		return multiplication_score;
	}

	public void setMultiplication_score(String multiplication_score) {
		this.multiplication_score = multiplication_score;
	}

	public String getDivision_score() {
		return division_score;
	}

	public void setDivision_score(String division_score) {
		this.division_score = division_score;
	}

	// public int getAddition_scorepossible() {
	// return addition_scorepossible;
	// }
	//
	// public void setAddition_scorepossible(int addition_scorepossible) {
	// this.addition_scorepossible = addition_scorepossible;
	// }
	//
	// public int getSubtraction_scorepossible() {
	// return subtraction_scorepossible;
	// }
	//
	// public void setSubtraction_scorepossible(int subtraction_scorepossible) {
	// this.subtraction_scorepossible = subtraction_scorepossible;
	// }
	//
	// public int getMultiplication_scorepossible() {
	// return multiplication_scorepossible;
	// }
	//
	// public void setMultiplication_scorepossible(int
	// multiplication_scorepossible) {
	// this.multiplication_scorepossible = multiplication_scorepossible;
	// }
	//
	// public int getDivision_scorepossible() {
	// return division_scorepossible;
	// }
	//
	// public void setDivision_scorepossible(int division_scorepossible) {
	// this.division_scorepossible = division_scorepossible;
	// }

	public double getAddition_scorepercent() {
		return addition_scorepercent;
	}

	public void setAddition_scorepercent(double addition_scorepercent) {
		this.addition_scorepercent = addition_scorepercent;
	}

	public double getSubtraction_scorepercent() {
		return subtraction_scorepercent;
	}

	public void setSubtraction_scorepercent(double subtraction_scorepercent) {
		this.subtraction_scorepercent = subtraction_scorepercent;
	}

	public double getMultiplication_scorepercent() {
		return multiplication_scorepercent;
	}

	public void setMultiplication_scorepercent(
			double multiplication_scorepercent) {
		this.multiplication_scorepercent = multiplication_scorepercent;
	}

	public double getDivision_scorepercent() {
		return division_scorepercent;
	}

	public void setDivision_scorepercent(double division_scorepercent) {
		this.division_scorepercent = division_scorepercent;
	}

}
