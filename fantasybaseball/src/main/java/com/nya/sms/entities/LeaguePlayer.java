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
	Ref<LeagueTeam> league_team;
	
	@Index
	String team_roster_position;
	
	long team_roster_position_num;
	
	@Index
	@Load
	Ref<PlayerProjected> player_projected;
	
	Date league_update_date;
	
	@Index
	String full_name;  // updated from player_projected
	
	@Index
	String dc_status;  // updated from player_projected
	

	int age;  // updated from player_projected
	

	String team;  // updated from player_projected
	
	@Index
	String pitcher_hitter;  // updated from player_projected
	

	String player_position;  // updated from player_projected

	double hitter_avg_eff;
	double pitcher_era_eff;
	double pitcher_whip_eff;
	
	double hitter_z_hr;
	double hitter_z_rbi;
	double hitter_z_runs;
	double hitter_z_sb;
	double hitter_z_avg;
	double pitcher_z_wins;
	double pitcher_z_saves;
	double pitcher_z_so;
	double pitcher_z_era;
	double pitcher_z_whip;
	double total_z;
	int rank_z;
	int init_auction_value;

	public LeaguePlayer() {
	}

	public LeaguePlayer(League league, PlayerProjected player_projected) {
		this.league = Ref.create(league);
		this.player_projected = Ref.create(player_projected);
	}
	
	@OnSave private void updatePlayerInfoSave(){
		udpatePlayerInfo();
	}
	
	@OnLoad private void updatePlayerInfoLoad(){
		udpatePlayerInfo();
	}
	
	private void udpatePlayerInfo(){
		if (this.player_projected != null){
			this.full_name = this.getPlayer_projected().getFull_name();  
			this.dc_status = this.getPlayer_projected().getDc_status(); 
			this.age = this.getPlayer_projected().getAge();  
			this.team = this.getPlayer_projected().getTeam();  
			this.pitcher_hitter = this.getPlayer_projected().getPitcher_hitter();  
			if (this.getPlayer_projected().getPitcher_hitter().equals("P")){
				this.player_position = this.getPlayer_projected().getPitcher_pos();  
			} else {
				this.player_position = this.getPlayer_projected().getHitter_pos_elig_espn();
			}
		}
	}

	public League getLeague() {
		return league.get();
	}

	public void setLeague(League league) {
		this.league = Ref.create(league);
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

	public double getHitter_z_hr() {
		return hitter_z_hr;
	}

	public void setHitter_z_hr(double hitter_z_hr) {
		this.hitter_z_hr = hitter_z_hr;
	}

	public double getHitter_z_rbi() {
		return hitter_z_rbi;
	}

	public void setHitter_z_rbi(double hitter_z_rbi) {
		this.hitter_z_rbi = hitter_z_rbi;
	}

	public double getHitter_z_runs() {
		return hitter_z_runs;
	}

	public void setHitter_z_runs(double hitter_z_runs) {
		this.hitter_z_runs = hitter_z_runs;
	}

	public double getHitter_z_sb() {
		return hitter_z_sb;
	}

	public void setHitter_z_sb(double hitter_z_sb) {
		this.hitter_z_sb = hitter_z_sb;
	}

	public double getHitter_z_avg() {
		return hitter_z_avg;
	}

	public void setHitter_z_avg(double hitter_z_avg) {
		this.hitter_z_avg = hitter_z_avg;
	}

	public double getPitcher_z_wins() {
		return pitcher_z_wins;
	}

	public void setPitcher_z_wins(double pitcher_z_wins) {
		this.pitcher_z_wins = pitcher_z_wins;
	}

	public double getPitcher_z_saves() {
		return pitcher_z_saves;
	}

	public void setPitcher_z_saves(double pitcher_z_saves) {
		this.pitcher_z_saves = pitcher_z_saves;
	}

	public double getPitcher_z_so() {
		return pitcher_z_so;
	}

	public void setPitcher_z_so(double pitcher_z_so) {
		this.pitcher_z_so = pitcher_z_so;
	}

	public double getPitcher_z_era() {
		return pitcher_z_era;
	}

	public void setPitcher_z_era(double pitcher_z_era) {
		this.pitcher_z_era = pitcher_z_era;
	}

	public double getPitcher_z_whip() {
		return pitcher_z_whip;
	}

	public void setPitcher_z_whip(double pitcher_z_whip) {
		this.pitcher_z_whip = pitcher_z_whip;
	}

	public double getTotal_z() {
		return total_z;
	}

	public void setTotal_z(double total_z) {
		this.total_z = total_z;
	}

	public int getRank_z() {
		return rank_z;
	}

	public void setRank_z(int rank_z) {
		this.rank_z = rank_z;
	}

	public int getInit_auction_value() {
		return init_auction_value;
	}

	public void setInit_auction_value(int init_auction_value) {
		this.init_auction_value = init_auction_value;
	}

	public String getFull_name() {
		return full_name;
	}

	public void setFull_name(String full_name) {
		this.full_name = full_name;
	}

	public String getDc_status() {
		return dc_status;
	}

	public void setDc_status(String dc_status) {
		this.dc_status = dc_status;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public String getTeam() {
		return team;
	}

	public void setTeam(String team) {
		this.team = team;
	}

	public String getPitcher_hitter() {
		return pitcher_hitter;
	}

	public void setPitcher_hitter(String pitcher_hitter) {
		this.pitcher_hitter = pitcher_hitter;
	}

	public String getPlayer_position() {
		return player_position;
	}

	public void setPlayer_position(String player_position) {
		this.player_position = player_position;
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

	public double getHitter_avg_eff() {
		return hitter_avg_eff;
	}

	public void setHitter_avg_eff(double hitter_avg_eff) {
		this.hitter_avg_eff = hitter_avg_eff;
	}

	public double getPitcher_era_eff() {
		return pitcher_era_eff;
	}

	public void setPitcher_era_eff(double pitcher_era_eff) {
		this.pitcher_era_eff = pitcher_era_eff;
	}

	public double getPitcher_whip_eff() {
		return pitcher_whip_eff;
	}

	public void setPitcher_whip_eff(double pitcher_whip_eff) {
		this.pitcher_whip_eff = pitcher_whip_eff;
	}

	

}
