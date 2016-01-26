package com.nya.sms.entities;

import java.io.Serializable;
import java.util.Date;

import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.OnSave;

//@Subclass(index = true)
@Entity
public class SchoolGrades extends BaseEntity implements Serializable {

	private static final long serialVersionUID = 1;

	@Index
	Long studentid;
	@Index
	String studentname;
	@Index
	Date gradedate;
	@Index
	String period; // first or last only
	@Index
	String classtype;
	@Index
	String coursename;
	@Index
	String gradescale;
	@Index
	String grade;
	@Index
	String school;

	public SchoolGrades() {
	}

	public SchoolGrades(Long studentid) {

		this.studentid = studentid;

	}
	
	@OnSave
	void onSave() {

		if (studentid != null) {

			Student student = ObjectifyService.ofy().load().type(Student.class)
					.id(studentid).now();

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

	public Date getGradedate() {
		return gradedate;
	}

	public void setGradedate(Date gradedate) {
		this.gradedate = gradedate;
	}

	public String getPeriod() {
		return period;
	}

	public void setPeriod(String period) {
		this.period = period;
	}

	public String getClasstype() {
		return classtype;
	}

	public void setClasstype(String classtype) {
		this.classtype = classtype;
	}

	public String getCoursename() {
		return coursename;
	}

	public void setCoursename(String coursename) {
		this.coursename = coursename;
	}


	public String getGradescale() {
		return gradescale;
	}

	public void setGradescale(String gradescale) {
		this.gradescale = gradescale;
	}

	public String getGrade() {
		return grade;
	}

	public void setGrade(String grade) {
		this.grade = grade;
	}

	public String getSchool() {
		return school;
	}

	public void setSchool(String school) {
		this.school = school;
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

}
