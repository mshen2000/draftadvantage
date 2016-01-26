package com.nya.sms.entities;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.OnLoad;

//@Subclass(index = true)
@Entity
public class Student extends BaseEntity implements Serializable {

	private static final long serialVersionUID = 1;

	@Index
	String firstname;
	@Index
	String middlename;
	@Index
	String lastname;
	@Index
	String status;
	@Index
	String site;
	@Index
	Date dob;
	@Index
	String gender;
	String address;
	String phone1;
	String phone2;
	String phone1_type;
	String phone2_type;
	String email;
	@Index
	String ethnicity;
	@Index
	boolean lasdoradas;
	@Index
	String schoolname;
	@Index
	String grade;
	String learningdis;
	String behaviorproblems;
	@Index
	boolean gradesbelowavg;
	@Index
	boolean speaksenglish;
	String othersiblingsnya;
	@Index
	Date origprogstartdate;
	@Index
	boolean rec_counseling_nya;
	@Index
	boolean fam_rec_counseling;
	@Index
	boolean firstgencollege;
	@Index
	boolean hasIEP;
	@Index
	boolean imm_fam_has_drugalc_prob;
	String g1_name;
	String g1_type;
	String g1_address;
	String g1_phone1;
	String g1_phone1_type;
	String g1_phone2;
	String g1_phone2_type;
	String g1_workaddress;
	String g1_workphone;
	String g1_email;
	String g1_highestgrade;
	String g1_dialect;
	String g2_name;
	String g2_type;
	String g2_address;
	String g2_phone1;
	String g2_phone1_type;
	String g2_phone2;
	String g2_phone2_type;
	String g2_workaddress;
	String g2_workphone;
	String g2_email;
	String g2_highestgrade;
	String g2_dialect;
	@Index
	String num_fam_mems;
	@Index
	String fam_income;
	@Index
	String qual_freelunch_prog;
	@Index
	boolean singleparent;
	@Index
	boolean parent_speaks_english;
	@Index
	String parent_hs_grad;
	@Index
	String ref_source;
	String fammem1_name;
	String fammem1_age;
	String fammem1_school;
	String fammem1_grade;
	String fammem2_name;
	String fammem2_age;
	String fammem2_school;
	String fammem2_grade;
	String fammem3_name;
	String fammem3_age;
	String fammem3_school;
	String fammem3_grade;
	String fammem4_name;
	String fammem4_age;
	String fammem4_school;
	String fammem4_grade;
	String fammem5_name;
	String fammem5_age;
	String fammem5_school;
	String fammem5_grade;
	String fammem6_name;
	String fammem6_age;
	String fammem6_school;
	String fammem6_grade;
	String emerg1_name;
	String emerg1_rel_to_student;
	String emerg1_phone1_type;
	String emerg1_phone1;
	String emerg1_phone2_type;
	String emerg1_phone2;
	String emerg2_name;
	String emerg2_rel_to_student;
	String emerg2_phone1_type;
	String emerg2_phone1;
	String emerg2_phone2_type;
	String emerg2_phone2;
	String emerg3_name;
	String emerg3_rel_to_student;
	String emerg3_phone1_type;
	String emerg3_phone1;
	String emerg3_phone2_type;
	String emerg3_phone2;
	String emerg4_name;
	String emerg4_rel_to_student;
	String emerg4_phone1_type;
	String emerg4_phone1;
	String emerg4_phone2_type;
	String emerg4_phone2;
	String emerg5_name;
	String emerg5_rel_to_student;
	String emerg5_phone1_type;
	String emerg5_phone1;
	String emerg5_phone2_type;
	String emerg5_phone2;

	@SuppressWarnings("unused")
	private Student() {
	}

	public Student(String firstname, String lastname) {

		this.firstname = firstname;
		this.lastname = lastname;

	}

	// update site field with site that has this student as child
	@OnLoad
	void onLoad() {

		Ref<Student> studentref = Ref.create(this);

		// Get site objects that have this student as a child (there should be
		// only one)
		List<Site> sites = ObjectifyService.ofy().load().type(Site.class)
				.filter("students", studentref).list();

		if (sites.size() == 0) {

			System.out
					.println("Student "
							+ firstname
							+ " is not associated to any sites, site field will be null");

			site = null;

		} else {

			if (sites.size() > 1) {

				System.out
						.println("Found more than one site that contains this student, there should be only one.  Will use the first one.");

			}

			System.out.println("Filling site field with site name "
					+ sites.get(0).getName());

			site = sites.get(0).getName();

		}

	}

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getMiddlename() {
		return middlename;
	}

	public void setMiddlename(String middlename) {
		this.middlename = middlename;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getSite() {
		return site;
	}

	public void setSite(String site) {
		this.site = site;
	}

	public Date getDob() {
		return dob;
	}

	public void setDob(Date dob) {
		this.dob = dob;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getPhone1() {
		return phone1;
	}

	public void setPhone1(String phone1) {
		this.phone1 = phone1;
	}

	public String getPhone2() {
		return phone2;
	}

	public void setPhone2(String phone2) {
		this.phone2 = phone2;
	}

	public String getPhone1_type() {
		return phone1_type;
	}

	public void setPhone1_type(String phone1_type) {
		this.phone1_type = phone1_type;
	}

	public String getPhone2_type() {
		return phone2_type;
	}

	public void setPhone2_type(String phone2_type) {
		this.phone2_type = phone2_type;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getEthnicity() {
		return ethnicity;
	}

	public void setEthnicity(String ethnicity) {
		this.ethnicity = ethnicity;
	}

	public boolean isLasdoradas() {
		return lasdoradas;
	}

	public void setLasdoradas(boolean lasdoradas) {
		this.lasdoradas = lasdoradas;
	}

	public String getSchoolname() {
		return schoolname;
	}

	public void setSchoolname(String schoolname) {
		this.schoolname = schoolname;
	}

	public String getGrade() {
		return grade;
	}

	public void setGrade(String grade) {
		this.grade = grade;
	}

	public String getLearningdis() {
		return learningdis;
	}

	public void setLearningdis(String learningdis) {
		this.learningdis = learningdis;
	}

	public String getBehaviorproblems() {
		return behaviorproblems;
	}

	public void setBehaviorproblems(String behaviorproblems) {
		this.behaviorproblems = behaviorproblems;
	}

	public boolean isGradesbelowavg() {
		return gradesbelowavg;
	}

	public void setGradesbelowavg(boolean gradesbelowavg) {
		this.gradesbelowavg = gradesbelowavg;
	}

	public boolean isSpeaksenglish() {
		return speaksenglish;
	}

	public void setSpeaksenglish(boolean speaksenglish) {
		this.speaksenglish = speaksenglish;
	}

	public String getOthersiblingsnya() {
		return othersiblingsnya;
	}

	public void setOthersiblingsnya(String othersiblingsnya) {
		this.othersiblingsnya = othersiblingsnya;
	}

	public Date getOrigprogstartdate() {
		return origprogstartdate;
	}

	public void setOrigprogstartdate(Date origprogstartdate) {
		this.origprogstartdate = origprogstartdate;
	}

	public boolean isRec_counseling_nya() {
		return rec_counseling_nya;
	}

	public void setRec_counseling_nya(boolean rec_counseling_nya) {
		this.rec_counseling_nya = rec_counseling_nya;
	}

	public boolean isFam_rec_counseling() {
		return fam_rec_counseling;
	}

	public void setFam_rec_counseling(boolean fam_rec_counseling) {
		this.fam_rec_counseling = fam_rec_counseling;
	}

	public boolean isFirstgencollege() {
		return firstgencollege;
	}

	public void setFirstgencollege(boolean firstgencollege) {
		this.firstgencollege = firstgencollege;
	}

	public boolean isHasIEP() {
		return hasIEP;
	}

	public void setHasIEP(boolean hasIEP) {
		this.hasIEP = hasIEP;
	}

	public boolean isImm_fam_has_drugalc_prob() {
		return imm_fam_has_drugalc_prob;
	}

	public void setImm_fam_has_drugalc_prob(boolean imm_fam_has_drugalc_prob) {
		this.imm_fam_has_drugalc_prob = imm_fam_has_drugalc_prob;
	}

	public String getG1_name() {
		return g1_name;
	}

	public void setG1_name(String g1_name) {
		this.g1_name = g1_name;
	}

	public String getG1_type() {
		return g1_type;
	}

	public void setG1_type(String g1_type) {
		this.g1_type = g1_type;
	}

	public String getG2_type() {
		return g2_type;
	}

	public void setG2_type(String g2_type) {
		this.g2_type = g2_type;
	}

	public String getG1_address() {
		return g1_address;
	}

	public void setG1_address(String g1_address) {
		this.g1_address = g1_address;
	}

	public String getG1_phone1() {
		return g1_phone1;
	}

	public void setG1_phone1(String g1_phone1) {
		this.g1_phone1 = g1_phone1;
	}

	public String getG1_phone1_type() {
		return g1_phone1_type;
	}

	public void setG1_phone1_type(String g1_phone1_type) {
		this.g1_phone1_type = g1_phone1_type;
	}

	public String getG1_phone2_type() {
		return g1_phone2_type;
	}

	public void setG1_phone2_type(String g1_phone2_type) {
		this.g1_phone2_type = g1_phone2_type;
	}

	public String getG2_phone1_type() {
		return g2_phone1_type;
	}

	public void setG2_phone1_type(String g2_phone1_type) {
		this.g2_phone1_type = g2_phone1_type;
	}

	public String getG2_phone2_type() {
		return g2_phone2_type;
	}

	public void setG2_phone2_type(String g2_phone2_type) {
		this.g2_phone2_type = g2_phone2_type;
	}

	public String getG1_phone2() {
		return g1_phone2;
	}

	public void setG1_phone2(String g1_phone2) {
		this.g1_phone2 = g1_phone2;
	}

	public String getG1_workaddress() {
		return g1_workaddress;
	}

	public void setG1_workaddress(String g1_workaddress) {
		this.g1_workaddress = g1_workaddress;
	}

	public String getG1_workphone() {
		return g1_workphone;
	}

	public void setG1_workphone(String g1_workphone) {
		this.g1_workphone = g1_workphone;
	}

	public String getG1_email() {
		return g1_email;
	}

	public void setG1_email(String g1_email) {
		this.g1_email = g1_email;
	}

	public String getG1_highestgrade() {
		return g1_highestgrade;
	}

	public void setG1_highestgrade(String g1_highestgrade) {
		this.g1_highestgrade = g1_highestgrade;
	}

	public String getG1_dialect() {
		return g1_dialect;
	}

	public void setG1_dialect(String g1_dialect) {
		this.g1_dialect = g1_dialect;
	}

	public String getG2_dialect() {
		return g2_dialect;
	}

	public void setG2_dialect(String g2_dialect) {
		this.g2_dialect = g2_dialect;
	}

	public String getG2_highestgrade() {
		return g2_highestgrade;
	}

	public void setG2_highestgrade(String g2_highestgrade) {
		this.g2_highestgrade = g2_highestgrade;
	}

	public String getG2_name() {
		return g2_name;
	}

	public void setG2_name(String g2_name) {
		this.g2_name = g2_name;
	}

	public String getG2_address() {
		return g2_address;
	}

	public void setG2_address(String g2_address) {
		this.g2_address = g2_address;
	}

	public String getG2_phone1() {
		return g2_phone1;
	}

	public void setG2_phone1(String g2_phone1) {
		this.g2_phone1 = g2_phone1;
	}

	public String getG2_phone2() {
		return g2_phone2;
	}

	public void setG2_phone2(String g2_phone2) {
		this.g2_phone2 = g2_phone2;
	}

	public String getG2_workaddress() {
		return g2_workaddress;
	}

	public void setG2_workaddress(String g2_workaddress) {
		this.g2_workaddress = g2_workaddress;
	}

	public String getG2_workphone() {
		return g2_workphone;
	}

	public void setG2_workphone(String g2_workphone) {
		this.g2_workphone = g2_workphone;
	}

	public String getG2_email() {
		return g2_email;
	}

	public void setG2_email(String g2_email) {
		this.g2_email = g2_email;
	}

	public String getNum_fam_mems() {
		return num_fam_mems;
	}

	public void setNum_fam_mems(String num_fam_mems) {
		this.num_fam_mems = num_fam_mems;
	}

	public String getFam_income() {
		return fam_income;
	}

	public void setFam_income(String fam_income) {
		this.fam_income = fam_income;
	}

	public String getQual_freelunch_prog() {
		return qual_freelunch_prog;
	}

	public void setQual_freelunch_prog(String qual_freelunch_prog) {
		this.qual_freelunch_prog = qual_freelunch_prog;
	}

	public boolean isSingleparent() {
		return singleparent;
	}

	public void setSingleparent(boolean singleparent) {
		this.singleparent = singleparent;
	}

	public boolean isParent_speaks_english() {
		return parent_speaks_english;
	}

	public void setParent_speaks_english(boolean parent_speaks_english) {
		this.parent_speaks_english = parent_speaks_english;
	}

	public String getParent_hs_grad() {
		return parent_hs_grad;
	}

	public void setParent_hs_grad(String parent_hs_grad) {
		this.parent_hs_grad = parent_hs_grad;
	}

	public String getRef_source() {
		return ref_source;
	}

	public void setRef_source(String ref_source) {
		this.ref_source = ref_source;
	}

	public String getFammem1_name() {
		return fammem1_name;
	}

	public void setFammem1_name(String fammem1_name) {
		this.fammem1_name = fammem1_name;
	}

	public String getFammem1_age() {
		return fammem1_age;
	}

	public void setFammem1_age(String fammem1_age) {
		this.fammem1_age = fammem1_age;
	}

	public String getFammem1_school() {
		return fammem1_school;
	}

	public void setFammem1_school(String fammem1_school) {
		this.fammem1_school = fammem1_school;
	}

	public String getFammem1_grade() {
		return fammem1_grade;
	}

	public void setFammem1_grade(String fammem1_grade) {
		this.fammem1_grade = fammem1_grade;
	}

	public String getFammem2_name() {
		return fammem2_name;
	}

	public void setFammem2_name(String fammem2_name) {
		this.fammem2_name = fammem2_name;
	}

	public String getFammem2_age() {
		return fammem2_age;
	}

	public void setFammem2_age(String fammem2_age) {
		this.fammem2_age = fammem2_age;
	}

	public String getFammem2_school() {
		return fammem2_school;
	}

	public void setFammem2_school(String fammem2_school) {
		this.fammem2_school = fammem2_school;
	}

	public String getFammem2_grade() {
		return fammem2_grade;
	}

	public void setFammem2_grade(String fammem2_grade) {
		this.fammem2_grade = fammem2_grade;
	}

	public String getFammem3_name() {
		return fammem3_name;
	}

	public void setFammem3_name(String fammem3_name) {
		this.fammem3_name = fammem3_name;
	}

	public String getFammem3_age() {
		return fammem3_age;
	}

	public void setFammem3_age(String fammem3_age) {
		this.fammem3_age = fammem3_age;
	}

	public String getFammem3_school() {
		return fammem3_school;
	}

	public void setFammem3_school(String fammem3_school) {
		this.fammem3_school = fammem3_school;
	}

	public String getFammem3_grade() {
		return fammem3_grade;
	}

	public void setFammem3_grade(String fammem3_grade) {
		this.fammem3_grade = fammem3_grade;
	}

	public String getFammem4_name() {
		return fammem4_name;
	}

	public void setFammem4_name(String fammem4_name) {
		this.fammem4_name = fammem4_name;
	}

	public String getFammem4_age() {
		return fammem4_age;
	}

	public void setFammem4_age(String fammem4_age) {
		this.fammem4_age = fammem4_age;
	}

	public String getFammem4_school() {
		return fammem4_school;
	}

	public void setFammem4_school(String fammem4_school) {
		this.fammem4_school = fammem4_school;
	}

	public String getFammem4_grade() {
		return fammem4_grade;
	}

	public void setFammem4_grade(String fammem4_grade) {
		this.fammem4_grade = fammem4_grade;
	}

	public String getFammem5_name() {
		return fammem5_name;
	}

	public void setFammem5_name(String fammem5_name) {
		this.fammem5_name = fammem5_name;
	}

	public String getFammem5_age() {
		return fammem5_age;
	}

	public void setFammem5_age(String fammem5_age) {
		this.fammem5_age = fammem5_age;
	}

	public String getFammem5_school() {
		return fammem5_school;
	}

	public void setFammem5_school(String fammem5_school) {
		this.fammem5_school = fammem5_school;
	}

	public String getFammem5_grade() {
		return fammem5_grade;
	}

	public void setFammem5_grade(String fammem5_grade) {
		this.fammem5_grade = fammem5_grade;
	}

	public String getFammem6_name() {
		return fammem6_name;
	}

	public void setFammem6_name(String fammem6_name) {
		this.fammem6_name = fammem6_name;
	}

	public String getFammem6_age() {
		return fammem6_age;
	}

	public void setFammem6_age(String fammem6_age) {
		this.fammem6_age = fammem6_age;
	}

	public String getFammem6_school() {
		return fammem6_school;
	}

	public void setFammem6_school(String fammem6_school) {
		this.fammem6_school = fammem6_school;
	}

	public String getFammem6_grade() {
		return fammem6_grade;
	}

	public void setFammem6_grade(String fammem6_grade) {
		this.fammem6_grade = fammem6_grade;
	}

	public String getEmerg1_name() {
		return emerg1_name;
	}

	public void setEmerg1_name(String emerg1_name) {
		this.emerg1_name = emerg1_name;
	}

	public String getEmerg1_rel_to_student() {
		return emerg1_rel_to_student;
	}

	public void setEmerg1_rel_to_student(String emerg1_rel_to_student) {
		this.emerg1_rel_to_student = emerg1_rel_to_student;
	}

	public String getEmerg1_phone1_type() {
		return emerg1_phone1_type;
	}

	public void setEmerg1_phone1_type(String emerg1_phone1_type) {
		this.emerg1_phone1_type = emerg1_phone1_type;
	}

	public String getEmerg1_phone1() {
		return emerg1_phone1;
	}

	public void setEmerg1_phone1(String emerg1_phone1) {
		this.emerg1_phone1 = emerg1_phone1;
	}

	public String getEmerg1_phone2_type() {
		return emerg1_phone2_type;
	}

	public void setEmerg1_phone2_type(String emerg1_phone2_type) {
		this.emerg1_phone2_type = emerg1_phone2_type;
	}

	public String getEmerg1_phone2() {
		return emerg1_phone2;
	}

	public void setEmerg1_phone2(String emerg1_phone2) {
		this.emerg1_phone2 = emerg1_phone2;
	}

	public String getEmerg2_name() {
		return emerg2_name;
	}

	public void setEmerg2_name(String emerg2_name) {
		this.emerg2_name = emerg2_name;
	}

	public String getEmerg2_rel_to_student() {
		return emerg2_rel_to_student;
	}

	public void setEmerg2_rel_to_student(String emerg2_rel_to_student) {
		this.emerg2_rel_to_student = emerg2_rel_to_student;
	}

	public String getEmerg2_phone1_type() {
		return emerg2_phone1_type;
	}

	public void setEmerg2_phone1_type(String emerg2_phone1_type) {
		this.emerg2_phone1_type = emerg2_phone1_type;
	}

	public String getEmerg2_phone1() {
		return emerg2_phone1;
	}

	public void setEmerg2_phone1(String emerg2_phone1) {
		this.emerg2_phone1 = emerg2_phone1;
	}

	public String getEmerg2_phone2_type() {
		return emerg2_phone2_type;
	}

	public void setEmerg2_phone2_type(String emerg2_phone2_type) {
		this.emerg2_phone2_type = emerg2_phone2_type;
	}

	public String getEmerg2_phone2() {
		return emerg2_phone2;
	}

	public void setEmerg2_phone2(String emerg2_phone2) {
		this.emerg2_phone2 = emerg2_phone2;
	}

	public String getEmerg3_name() {
		return emerg3_name;
	}

	public void setEmerg3_name(String emerg3_name) {
		this.emerg3_name = emerg3_name;
	}

	public String getEmerg3_rel_to_student() {
		return emerg3_rel_to_student;
	}

	public void setEmerg3_rel_to_student(String emerg3_rel_to_student) {
		this.emerg3_rel_to_student = emerg3_rel_to_student;
	}

	public String getEmerg3_phone1_type() {
		return emerg3_phone1_type;
	}

	public void setEmerg3_phone1_type(String emerg3_phone1_type) {
		this.emerg3_phone1_type = emerg3_phone1_type;
	}

	public String getEmerg3_phone1() {
		return emerg3_phone1;
	}

	public void setEmerg3_phone1(String emerg3_phone1) {
		this.emerg3_phone1 = emerg3_phone1;
	}

	public String getEmerg3_phone2_type() {
		return emerg3_phone2_type;
	}

	public void setEmerg3_phone2_type(String emerg3_phone2_type) {
		this.emerg3_phone2_type = emerg3_phone2_type;
	}

	public String getEmerg3_phone2() {
		return emerg3_phone2;
	}

	public void setEmerg3_phone2(String emerg3_phone2) {
		this.emerg3_phone2 = emerg3_phone2;
	}

	public String getEmerg4_name() {
		return emerg4_name;
	}

	public void setEmerg4_name(String emerg4_name) {
		this.emerg4_name = emerg4_name;
	}

	public String getEmerg4_rel_to_student() {
		return emerg4_rel_to_student;
	}

	public void setEmerg4_rel_to_student(String emerg4_rel_to_student) {
		this.emerg4_rel_to_student = emerg4_rel_to_student;
	}

	public String getEmerg4_phone1_type() {
		return emerg4_phone1_type;
	}

	public void setEmerg4_phone1_type(String emerg4_phone1_type) {
		this.emerg4_phone1_type = emerg4_phone1_type;
	}

	public String getEmerg4_phone1() {
		return emerg4_phone1;
	}

	public void setEmerg4_phone1(String emerg4_phone1) {
		this.emerg4_phone1 = emerg4_phone1;
	}

	public String getEmerg4_phone2_type() {
		return emerg4_phone2_type;
	}

	public void setEmerg4_phone2_type(String emerg4_phone2_type) {
		this.emerg4_phone2_type = emerg4_phone2_type;
	}

	public String getEmerg4_phone2() {
		return emerg4_phone2;
	}

	public void setEmerg4_phone2(String emerg4_phone2) {
		this.emerg4_phone2 = emerg4_phone2;
	}

	public String getEmerg5_name() {
		return emerg5_name;
	}

	public void setEmerg5_name(String emerg5_name) {
		this.emerg5_name = emerg5_name;
	}

	public String getEmerg5_rel_to_student() {
		return emerg5_rel_to_student;
	}

	public void setEmerg5_rel_to_student(String emerg5_rel_to_student) {
		this.emerg5_rel_to_student = emerg5_rel_to_student;
	}

	public String getEmerg5_phone1_type() {
		return emerg5_phone1_type;
	}

	public void setEmerg5_phone1_type(String emerg5_phone1_type) {
		this.emerg5_phone1_type = emerg5_phone1_type;
	}

	public String getEmerg5_phone1() {
		return emerg5_phone1;
	}

	public void setEmerg5_phone1(String emerg5_phone1) {
		this.emerg5_phone1 = emerg5_phone1;
	}

	public String getEmerg5_phone2_type() {
		return emerg5_phone2_type;
	}

	public void setEmerg5_phone2_type(String emerg5_phone2_type) {
		this.emerg5_phone2_type = emerg5_phone2_type;
	}

	public String getEmerg5_phone2() {
		return emerg5_phone2;
	}

	public void setEmerg5_phone2(String emerg5_phone2) {
		this.emerg5_phone2 = emerg5_phone2;
	}

	public String getStudentFullName() {

		String studentname = "";

		if (this.middlename != null && !this.middlename.isEmpty())
			studentname = this.firstname + " " + this.middlename + " "
					+ this.lastname;
		else
			studentname = this.firstname + " " + this.lastname;

		return studentname;

	}

}
