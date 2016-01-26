package com.nya.sms.entities;

import java.io.Serializable;
import java.util.Date;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Index;

//@Subclass(index = true)
@Entity
public class StudentHealth extends BaseEntity implements Serializable {

	private static final long serialVersionUID = 1;

	@Index
	Long studentid;
	@Index
	String overallhealth;
	@Index
	Date chickpox;
	@Index
	Date asthma;
	@Index
	Date diabetes;
	@Index
	Date epilepsy;
	@Index
	Date mumps;
	@Index
	Date tenday_measles;
	@Index
	Date rheumaticfever;
	@Index
	Date hayfever;
	@Index
	Date whoopingcough;
	@Index
	Date poliomyelitis;
	@Index
	String otherillness;
	@Index
	String freqcolds;
	@Index
	String freqallergies;
	@Index
	String allergies;
	@Index
	String take_presc_otc_meds;
	@Index
	String has_been_under_doc_care;
	@Index
	String use_spec_devices;
	@Index
	boolean physical_disability;
	String phys_name;
	String phys_medplanandnumber;
	String phys_address;
	String phys_phone;
	String dent_name;
	String dent_medplanandnumber;
	String dent_address;
	String dent_phone;

	public StudentHealth() {
	}

	public StudentHealth(Long studentid) {

		this.studentid = studentid;

	}

	public Long getStudentid() {
		return studentid;
	}

	public void setStudentid(Long studentid) {
		this.studentid = studentid;
	}

	public String getOverallhealth() {
		return overallhealth;
	}

	public void setOverallhealth(String overallhealth) {
		this.overallhealth = overallhealth;
	}

	public Date getChickpox() {
		return chickpox;
	}

	public void setChickpox(Date chickpox) {
		this.chickpox = chickpox;
	}

	public Date getAsthma() {
		return asthma;
	}

	public void setAsthma(Date asthma) {
		this.asthma = asthma;
	}

	public Date getDiabetes() {
		return diabetes;
	}

	public void setDiabetes(Date diabetes) {
		this.diabetes = diabetes;
	}

	public Date getEpilepsy() {
		return epilepsy;
	}

	public void setEpilepsy(Date epilepsy) {
		this.epilepsy = epilepsy;
	}

	public Date getMumps() {
		return mumps;
	}

	public void setMumps(Date mumps) {
		this.mumps = mumps;
	}

	public Date getTenday_measles() {
		return tenday_measles;
	}

	public void setTenday_measles(Date tenday_measles) {
		this.tenday_measles = tenday_measles;
	}

	public Date getRheumaticfever() {
		return rheumaticfever;
	}

	public void setRheumaticfever(Date rheumaticfever) {
		this.rheumaticfever = rheumaticfever;
	}

	public Date getHayfever() {
		return hayfever;
	}

	public void setHayfever(Date hayfever) {
		this.hayfever = hayfever;
	}

	public Date getWhoopingcough() {
		return whoopingcough;
	}

	public void setWhoopingcough(Date whoopingcough) {
		this.whoopingcough = whoopingcough;
	}

	public Date getPoliomyelitis() {
		return poliomyelitis;
	}

	public void setPoliomyelitis(Date poliomyelitis) {
		this.poliomyelitis = poliomyelitis;
	}

	public String getOtherillness() {
		return otherillness;
	}

	public void setOtherillness(String otherillness) {
		this.otherillness = otherillness;
	}

	public String getFreqcolds() {
		return freqcolds;
	}

	public void setFreqcolds(String freqcolds) {
		this.freqcolds = freqcolds;
	}

	public String getFreqallergies() {
		return freqallergies;
	}

	public void setFreqallergies(String freqallergies) {
		this.freqallergies = freqallergies;
	}

	public String getAllergies() {
		return allergies;
	}

	public void setAllergies(String allergies) {
		this.allergies = allergies;
	}

	public String getTake_presc_otc_meds() {
		return take_presc_otc_meds;
	}

	public void setTake_presc_otc_meds(String take_presc_otc_meds) {
		this.take_presc_otc_meds = take_presc_otc_meds;
	}

	public String getHas_been_under_doc_care() {
		return has_been_under_doc_care;
	}

	public void setHas_been_under_doc_care(String has_been_under_doc_care) {
		this.has_been_under_doc_care = has_been_under_doc_care;
	}

	public String getUse_spec_devices() {
		return use_spec_devices;
	}

	public void setUse_spec_devices(String use_spec_devices) {
		this.use_spec_devices = use_spec_devices;
	}

	public boolean isPhysical_disability() {
		return physical_disability;
	}

	public void setPhysical_disability(boolean physical_disability) {
		this.physical_disability = physical_disability;
	}

	public String getPhys_name() {
		return phys_name;
	}

	public void setPhys_name(String phys_name) {
		this.phys_name = phys_name;
	}

	public String getPhys_medplanandnumber() {
		return phys_medplanandnumber;
	}

	public void setPhys_medplanandnumber(String phys_medplanandnumber) {
		this.phys_medplanandnumber = phys_medplanandnumber;
	}

	public String getPhys_address() {
		return phys_address;
	}

	public void setPhys_address(String phys_address) {
		this.phys_address = phys_address;
	}

	public String getPhys_phone() {
		return phys_phone;
	}

	public void setPhys_phone(String phys_phone) {
		this.phys_phone = phys_phone;
	}

	public String getDent_name() {
		return dent_name;
	}

	public void setDent_name(String dent_name) {
		this.dent_name = dent_name;
	}

	public String getDent_medplanandnumber() {
		return dent_medplanandnumber;
	}

	public void setDent_medplanandnumber(String dent_medplanandnumber) {
		this.dent_medplanandnumber = dent_medplanandnumber;
	}

	public String getDent_address() {
		return dent_address;
	}

	public void setDent_address(String dent_address) {
		this.dent_address = dent_address;
	}

	public String getDent_phone() {
		return dent_phone;
	}

	public void setDent_phone(String dent_phone) {
		this.dent_phone = dent_phone;
	}

}
