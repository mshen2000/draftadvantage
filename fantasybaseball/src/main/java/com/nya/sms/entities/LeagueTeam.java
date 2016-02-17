package com.nya.sms.entities;

import java.io.Serializable;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Index;

@Entity
public class LeagueTeam extends BaseEntity implements Serializable {
	
	private static final long serialVersionUID = 1;
	
	@Index
	String team_name;
	
	@Index
	String owner_name;
	
	@Index
	boolean isuserowner; 
	
	double salary_adjustment;

	public LeagueTeam() {
		this.isuserowner = false;
	}

	public LeagueTeam(String team_name, String owner_name, boolean isuserowner) {
		this.team_name = team_name;
		this.owner_name = owner_name;
		this.isuserowner = isuserowner;
	}

	public String getTeam_name() {
		return team_name;
	}

	public void setTeam_name(String team_name) {
		this.team_name = team_name;
	}

	public String getOwner_name() {
		return owner_name;
	}

	public void setOwner_name(String owner_name) {
		this.owner_name = owner_name;
	}

	public boolean isIsuserowner() {
		return isuserowner;
	}

	public void setIsuserowner(boolean isuserowner) {
		this.isuserowner = isuserowner;
	}

	public double getSalary_adjustment() {
		return salary_adjustment;
	}

	public void setSalary_adjustment(double salary_adjustment) {
		this.salary_adjustment = salary_adjustment;
	}



}
