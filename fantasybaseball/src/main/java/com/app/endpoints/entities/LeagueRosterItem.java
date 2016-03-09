package com.app.endpoints.entities;

public class LeagueRosterItem {
	
	int index;
	String position;
	String name;
	long salary;
	long playerid;

	public LeagueRosterItem() {
		// TODO Auto-generated constructor stub
	}
	
	public LeagueRosterItem(int index, String position) {
		this.index = index;
		this.position = position;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getSalary() {
		return salary;
	}

	public void setSalary(long salary) {
		this.salary = salary;
	}

	public long getPlayerid() {
		return playerid;
	}

	public void setPlayerid(long playerid) {
		this.playerid = playerid;
	}
	
	

}
