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
public class ProgramScoreSixTraits extends ProgramScore implements Serializable {

	private static final long serialVersionUID = 1;

	@Index
	String title;
	String score_i;
	String score_org;
	String score_v;
	String score_wc;
	String score_sf;
	String score_c;

	// int possiblescore_i;
	// int possiblescore_org;
	// int possiblescore_v;
	// int possiblescore_wc;
	// int possiblescore_sf;
	// int possiblescore_c;

	double percentscore_i;
	double percentscore_org;
	double percentscore_v;
	double percentscore_wc;
	double percentscore_sf;
	double percentscore_c;

	public ProgramScoreSixTraits() {
	}

	public ProgramScoreSixTraits(Long studentid) {

		this.studentid = studentid;

	}

	@OnSave
	void onSave() {

		List<Integer> i_scores = parseProgramScore(score_i);
		List<Integer> org_scores = parseProgramScore(score_org);
		List<Integer> v_scores = parseProgramScore(score_v);
		List<Integer> wc_scores = parseProgramScore(score_wc);
		List<Integer> sf_scores = parseProgramScore(score_sf);
		List<Integer> c_scores = parseProgramScore(score_c);

		if (i_scores.get(0) > 0)
			this.percentscore_i = (double) i_scores.get(0) / i_scores.get(1);
		if (org_scores.get(0) > 0)
			this.percentscore_org = (double) org_scores.get(0)
					/ org_scores.get(1);
		if (v_scores.get(0) > 0)
			this.percentscore_v = (double) v_scores.get(0) / v_scores.get(1);
		if (wc_scores.get(0) > 0)
			this.percentscore_wc = (double) wc_scores.get(0) / wc_scores.get(1);
		if (sf_scores.get(0) > 0)
			this.percentscore_sf = (double) sf_scores.get(0) / sf_scores.get(1);
		if (c_scores.get(0) > 0)
			this.percentscore_c = (double) c_scores.get(0) / c_scores.get(1);

		if (studentid != null) {

			Student student = ObjectifyService.ofy().load().type(Student.class)
					.id(studentid).get();

			this.studentname = student.getStudentFullName();

		} else {

			this.studentname = null;

		}

	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getScore_i() {
		return score_i;
	}

	public void setScore_i(String score_i) {
		this.score_i = score_i;
	}

	public String getScore_org() {
		return score_org;
	}

	public void setScore_org(String score_org) {
		this.score_org = score_org;
	}

	public String getScore_v() {
		return score_v;
	}

	public void setScore_v(String score_v) {
		this.score_v = score_v;
	}

	public String getScore_wc() {
		return score_wc;
	}

	public void setScore_wc(String score_wc) {
		this.score_wc = score_wc;
	}

	public String getScore_sf() {
		return score_sf;
	}

	public void setScore_sf(String score_sf) {
		this.score_sf = score_sf;
	}

	public String getScore_c() {
		return score_c;
	}

	public void setScore_c(String score_c) {
		this.score_c = score_c;
	}

	// public int getPossiblescore_i() {
	// return possiblescore_i;
	// }
	//
	// public void setPossiblescore_i(int possiblescore_i) {
	// this.possiblescore_i = possiblescore_i;
	// }
	//
	// public int getPossiblescore_org() {
	// return possiblescore_org;
	// }
	//
	// public void setPossiblescore_org(int possiblescore_org) {
	// this.possiblescore_org = possiblescore_org;
	// }
	//
	// public int getPossiblescore_v() {
	// return possiblescore_v;
	// }
	//
	// public void setPossiblescore_v(int possiblescore_v) {
	// this.possiblescore_v = possiblescore_v;
	// }
	//
	// public int getPossiblescore_wc() {
	// return possiblescore_wc;
	// }
	//
	// public void setPossiblescore_wc(int possiblescore_wc) {
	// this.possiblescore_wc = possiblescore_wc;
	// }
	//
	// public int getPossiblescore_sf() {
	// return possiblescore_sf;
	// }
	//
	// public void setPossiblescore_sf(int possiblescore_sf) {
	// this.possiblescore_sf = possiblescore_sf;
	// }
	//
	// public int getPossiblescore_c() {
	// return possiblescore_c;
	// }
	//
	// public void setPossiblescore_c(int possiblescore_c) {
	// this.possiblescore_c = possiblescore_c;
	// }

	public double getPercentscore_i() {
		return percentscore_i;
	}

	public void setPercentscore_i(double percentscore_i) {
		this.percentscore_i = percentscore_i;
	}

	public double getPercentscore_org() {
		return percentscore_org;
	}

	public void setPercentscore_org(double percentscore_org) {
		this.percentscore_org = percentscore_org;
	}

	public double getPercentscore_v() {
		return percentscore_v;
	}

	public void setPercentscore_v(double percentscore_v) {
		this.percentscore_v = percentscore_v;
	}

	public double getPercentscore_wc() {
		return percentscore_wc;
	}

	public void setPercentscore_wc(double percentscore_wc) {
		this.percentscore_wc = percentscore_wc;
	}

	public double getPercentscore_sf() {
		return percentscore_sf;
	}

	public void setPercentscore_sf(double percentscore_sf) {
		this.percentscore_sf = percentscore_sf;
	}

	public double getPercentscore_c() {
		return percentscore_c;
	}

	public void setPercentscore_c(double percentscore_c) {
		this.percentscore_c = percentscore_c;
	}

}
