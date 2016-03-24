package com.app.endpoints.entities;

public class LeaguePlayerInputDraftContainer {
	
	long league_id;
	
	long league_team_id;
	
	long player_projected_id;
	
	long league_player_id;
	
	String team_roster_position;
	
	long team_roster_position_num;
	
	double team_player_salary;
	
	boolean unknownplayer;
	
	String unknown_player_name;
	
	String unknown_player_pitcher_hitter;

	public LeaguePlayerInputDraftContainer() {
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

	public long getLeague_player_id() {
		return league_player_id;
	}

	public void setLeague_player_id(long league_player_id) {
		this.league_player_id = league_player_id;
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

	public String getTeam_roster_position() {
		return team_roster_position;
	}

	public void setTeam_roster_position(String team_roster_position) {
		this.team_roster_position = team_roster_position;
	}

	public boolean isUnknownplayer() {
		return unknownplayer;
	}

	public void setUnknownplayer(boolean unknownplayer) {
		this.unknownplayer = unknownplayer;
	}

	public String getUnknown_player_name() {
		return unknown_player_name;
	}

	public void setUnknown_player_name(String unknown_player_name) {
		this.unknown_player_name = unknown_player_name;
	}

	public String getUnknown_player_pitcher_hitter() {
		return unknown_player_pitcher_hitter;
	}

	public void setUnknown_player_pitcher_hitter(String unknown_player_pitcher_hitter) {
		this.unknown_player_pitcher_hitter = unknown_player_pitcher_hitter;
	}

}
