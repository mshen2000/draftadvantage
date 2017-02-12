package com.app.endpoints.entities;

public class LeaguePlayerInputInfoContainer {
	
	long league_id;
	
	long league_team_id;
	
	long player_projected_id;
	
	String team_player_note;
	
	String custom_position;
	
	boolean custom_position_flag;

	public LeaguePlayerInputInfoContainer() {
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

	public String getTeam_player_note() {
		return team_player_note;
	}

	public void setTeam_player_note(String team_player_note) {
		this.team_player_note = team_player_note;
	}

	public String getCustom_position() {
		return custom_position;
	}

	public void setCustom_position(String custom_position) {
		this.custom_position = custom_position;
	}

	public boolean isCustom_position_flag() {
		return custom_position_flag;
	}

	public void setCustom_position_flag(boolean custom_position_flag) {
		this.custom_position_flag = custom_position_flag;
	}
	
	
}
