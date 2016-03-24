package com.nya.sms.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Load;
import com.googlecode.objectify.annotation.OnLoad;
import com.googlecode.objectify.annotation.OnSave;

@Entity
public class LeaguePlayer extends BaseEntity implements Serializable {
	
	private static final long serialVersionUID = 1;
	
	@Index
	Ref<League> league;
	
	@Index
	@Load
	Ref<LeagueTeam> league_team;
	
	@Index
	String team_roster_position;
	
	long team_roster_position_num;
	
	double team_player_salary;
	
	String team_player_note;
	
	@Index
	Ref<PlayerProjected> player_projected;
	
	Date league_update_date;
	
	@Index
	boolean unknownplayer;
	
	@Index
	String unknown_player_name;
	
	@Index
	String unknown_player_pitcher_hitter;

	public LeaguePlayer() {
	}

	public LeaguePlayer(League league, PlayerProjected player_projected) {
		this.league = Ref.create(league);
		this.player_projected = Ref.create(player_projected);
	}
	

	public League getLeague() {
		return league.get();
	}

	public void setLeague(League league) {
		this.league = Ref.create(league);
	}
	
	public void setLeagueRef(Ref<League> leagueref) {
		this.league = leagueref;
	}

	public LeagueTeam getLeague_team() {
		
		if (league_team == null) return null;
		
		return league_team.get();
	}
	
	public Ref<LeagueTeam> getLeague_teamRef() {
		return league_team;
	}

	public void setLeague_team(LeagueTeam league_team) {
		this.league_team = Ref.create(league_team);
	}
	
	public String getTeam_roster_position() {
		return team_roster_position;
	}

	public void setTeam_roster_position(String team_roster_position) {
		this.team_roster_position = team_roster_position;
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

	public PlayerProjected getPlayer_projected() {
		return player_projected.get();
	}
	
	// Added to get only ref
	public Ref<PlayerProjected> getPlayer_projectedRef() {
		return player_projected;
	}

	public void setPlayer_projected(PlayerProjected player_projected) {
		this.player_projected = Ref.create(player_projected);
	}

	public Date getLeague_update_date() {
		return league_update_date;
	}

	public void setLeague_update_date(Date league_update_date) {
		this.league_update_date = league_update_date;
	}

	public void setLeague(Ref<League> league) {
		this.league = league;
	}

	public void setLeague_team(Ref<LeagueTeam> league_team) {
		this.league_team = league_team;
	}

	public void setPlayer_projected(Ref<PlayerProjected> player_projected) {
		this.player_projected = player_projected;
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
