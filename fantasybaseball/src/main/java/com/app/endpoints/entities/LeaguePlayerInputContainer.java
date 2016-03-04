package com.app.endpoints.entities;

public class LeaguePlayerInputContainer {
	
	long league_id;
	
	long league_team_id;
	
	long player_projected_id;
	
	String team_roster_position;
	
	long team_roster_position_num;
	
	double team_player_salary;
	
	String team_player_note;

	public LeaguePlayerInputContainer() {
		// TODO Auto-generated constructor stub
	}

	public long getLeague_id() {
		return league_id;
	}

	public void setLeague_id(long league_id) {
		this.league_id = league_id;
	}

	public long getLeague_team_id() {
		return league_team_id;
	}

	public void setLeague_team_id(long league_team_id) {
		this.league_team_id = league_team_id;
	}

	public long getPlayer_projected_id() {
		return player_projected_id;
	}

	public void setPlayer_projected_id(long player_projected_id) {
		this.player_projected_id = player_projected_id;
	}

	public long getTeam_roster_position_num() {
		return team_roster_position_num;
	}

	public void setTeam_roster_position_num(long team_roster_position_num) {
		this.team_roster_position_num = team_roster_position_num;
	}

	public double getTeam_player_salary() {
		return team_player_salary;
	}

	public void setTeam_player_salary(double team_player_salary) {
		this.team_player_salary = team_player_salary;
	}

	public String getTeam_player_note() {
		return team_player_note;
	}

	public void setTeam_player_note(String team_player_note) {
		this.team_player_note = team_player_note;
	}

	public String getTeam_roster_position() {
		return team_roster_position;
	}

	public void setTeam_roster_position(String team_roster_position) {
		this.team_roster_position = team_roster_position;
	}
	
	

}
