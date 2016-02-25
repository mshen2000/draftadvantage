package com.app.endpoints.entities;

import java.util.List;

public class LeagueModalFields {
	
	List<String> mlb_leagues;
	
	List<String> league_sites;

	public LeagueModalFields(List<String> mlb_leagues, List<String> league_sites) {
		this.mlb_leagues = mlb_leagues;
		this.league_sites = league_sites;
	}

	public List<String> getMlb_leagues() {
		return mlb_leagues;
	}

	public void setMlb_leagues(List<String> mlb_leagues) {
		this.mlb_leagues = mlb_leagues;
	}

	public List<String> getLeague_sites() {
		return league_sites;
	}

	public void setLeague_sites(List<String> league_sites) {
		this.league_sites = league_sites;
	}

}
