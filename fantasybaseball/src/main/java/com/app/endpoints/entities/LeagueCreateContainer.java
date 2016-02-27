package com.app.endpoints.entities;

import java.util.List;

import com.nya.sms.entities.League;
import com.nya.sms.entities.LeagueTeam;
import com.nya.sms.entities.ProjectionProfile;

public class LeagueCreateContainer {
	
	League league;
	
	List<LeagueTeam> league_teams;
	
	ProjectionProfile profile;

	public LeagueCreateContainer() {
		// TODO Auto-generated constructor stub
	}

	public League getLeague() {
		return league;
	}

	public void setLeague(League league) {
		this.league = league;
	}

	public List<LeagueTeam> getLeague_teams() {
		return league_teams;
	}

	public void setLeague_teams(List<LeagueTeam> league_teams) {
		this.league_teams = league_teams;
	}

	public ProjectionProfile getProfile() {
		return profile;
	}

	public void setProfile(ProjectionProfile profile) {
		this.profile = profile;
	}
	
	

}
