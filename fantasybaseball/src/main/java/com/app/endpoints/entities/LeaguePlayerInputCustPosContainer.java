package com.app.endpoints.entities;

public class LeaguePlayerInputCustPosContainer {
	
	long league_id;
	
	long league_team_id;
	
	long player_projected_id;
	
	String custom_position_eligibility;

	public LeaguePlayerInputCustPosContainer() {
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

	public String getCustom_position_eligibility() {
		return custom_position_eligibility;
	}

	public void setCustom_position_eligibility(String custom_position_eligibility) {
		this.custom_position_eligibility = custom_position_eligibility;
	}

	
}
