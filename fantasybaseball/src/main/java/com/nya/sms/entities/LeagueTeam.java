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
	
	@Index
	int team_num;
	
	double salary_adjustment;
	
	double starting_league_salary;
	
	double adj_starting_salary;

	public LeagueTeam() {
		this.isuserowner = false;
	}

	public LeagueTeam(String team_name, String owner_name, boolean isuserowner) {
		this.team_name = team_name;
		this.owner_name = owner_name;
		this.isuserowner = isuserowner;
	}
	
	public LeagueTeam(int team_num, String team_name, String owner_name, boolean isuserowner) {
		this.team_name = team_name;
		this.owner_name = owner_name;
		this.isuserowner = isuserowner;
		this.team_num = team_num;
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

	public int getTeam_num() {
		return team_num;
	}

	public void setTeam_num(int team_num) {
		this.team_num = team_num;
	}

	public double getStarting_league_salary() {
		return starting_league_salary;
	}

	public void setStarting_league_salary(double starting_league_salary) {
		this.starting_league_salary = starting_league_salary;
	}

	public double getAdj_starting_salary() {
		return adj_starting_salary;
	}

	public void setAdj_starting_salary(double adj_starting_salary) {
		this.adj_starting_salary = adj_starting_salary;
	}



}
