package com.app.endpoints.entities;

public class LeagueTeamContainer {
	
	long teamid;
	
	long leagueid;
	
	String teamname;
	
	String teamowner;

	public LeagueTeamContainer() {
		// TODO Auto-generated constructor stub
	}

	public long getTeamid() {
		return teamid;
	}

	public void setTeamid(long teamid) {
		this.teamid = teamid;
	}

	public long getLeagueid() {
		return leagueid;
	}

	public void setLeagueid(long leagueid) {
		this.leagueid = leagueid;
	}

	public String getTeamname() {
		return teamname;
	}

	public void setTeamname(String teamname) {
		this.teamname = teamname;
	}

	public String getTeamowner() {
		return teamowner;
	}

	public void setTeamowner(String teamowner) {
		this.teamowner = teamowner;
	}
	

}
