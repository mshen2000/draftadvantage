package com.app.endpoints;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.api.server.spi.config.AnnotationBoolean;
import com.google.api.server.spi.config.ApiResourceProperty;
import com.nya.sms.entities.PlayerProjected;
import com.nya.sms.entities.ProjectionProfile;

@JsonIgnoreProperties({"projection_profile"})
public class LeaguePlayerOutput extends PlayerProjected{
	
	private static final long serialVersionUID = 1;

	long league_id;

	long leagueteam_id;
	
	String leagueteam_name;
	
	String player_position;

	String team_roster_position;
	
	long team_roster_position_num;

	double team_player_salary;
	
	String team_player_note;
	
	Date projection_date;
	
	boolean UnknownPlayer = false;
	
	String unknown_player_name;
	
	String unknown_player_pitcher_hitter;

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
	int live_auction_value;

	public LeaguePlayerOutput() {
	}
	
	public LeaguePlayerOutput(PlayerProjected p) {
		
		if (p.getPitcher_hitter().equals("P")){
			this.player_position = p.getPitcher_pos();  
		} else {
			this.player_position = p.getHitter_pos_elig_espn();
		}
		
		this.id = p.getId();
		this.hitter_ab = p.getHitter_ab();
		this.hitter_avg = p.getHitter_avg();
		this.hitter_bb = p.getHitter_bb();
		this.hitter_cs = p.getHitter_cs();
		this.hitter_doubles = p.getHitter_doubles();
		this.hitter_games = p.getHitter_games();
		this.hitter_hbp = p.getHitter_hbp();
		this.hitter_hits = p.getHitter_hits();
		this.hitter_hr = p.getHitter_hr();
		this.hitter_obp = p.getHitter_obp();
		this.hitter_ops = p.getHitter_ops();
		this.hitter_pa = p.getHitter_pa();
		this.hitter_rbi = p.getHitter_rbi();
		this.hitter_runs = p.getHitter_runs();
		this.hitter_sb = p.getHitter_sb();
		this.hitter_sf = p.getHitter_sf();
		this.hitter_singles = p.getHitter_singles();
		this.hitter_slg = p.getHitter_slg();
		this.hitter_so = p.getHitter_so();
		this.hitter_tb = p.getHitter_tb();
		this.hitter_triples = p.getHitter_triples();
		this.pitcher_babip = p.getPitcher_babip();
		this.pitcher_bb = p.getPitcher_bb();
		this.pitcher_er = p.getPitcher_er();
		this.pitcher_era = p.getPitcher_era();
		this.pitcher_fb_pct = p.getPitcher_fb_pct();
		this.pitcher_games = p.getPitcher_games();
		this.pitcher_gb_pct = p.getPitcher_gb_pct();
		this.pitcher_gs = p.getPitcher_gs();
		this.pitcher_hbp = p.getPitcher_hbp();
		this.pitcher_hits = p.getPitcher_hits();
		this.pitcher_hld = p.getPitcher_hld();
		this.pitcher_hr = p.getPitcher_hr();
		this.pitcher_ip = p.getPitcher_ip();
		this.pitcher_k = p.getPitcher_k();
		this.pitcher_l = p.getPitcher_l();
		this.pitcher_ld_pct = p.getPitcher_ld_pct();
		this.pitcher_qs = p.getPitcher_qs();
		this.pitcher_r = p.getPitcher_r();
		this.pitcher_siera = p.getPitcher_siera();
		this.pitcher_sv = p.getPitcher_sv();
		this.pitcher_w = p.getPitcher_w();
		this.age = p.getAge();
		this.al_nl = p.getAl_nl();
		this.dc_status = p.getDc_status();
		this.first_name = p.getFirst_name();
		this.full_name = p.getFull_name();
		this.team = p.getTeam();
		this.hitter_bats = p.getHitter_bats();
		this.hitter_pos_elig_espn = p.getHitter_pos_elig_espn();
		this.hitter_pos_elig_yahoo = p.getHitter_pos_elig_yahoo();
		this.last_name = p.getLast_name();
		this.mlb_id = p.getMlb_id();
		this.other_id = p.getOther_id();
		this.other_id_name = p.getOther_id_name();
		this.pitcher_hitter = p.getPitcher_hitter();
		this.pitcher_pos = p.getPitcher_pos();
		this.pitcher_throws = p.getPitcher_throws();
		
		this.pitcher_whip = (p.getPitcher_bb() + p.getPitcher_hits())/p.pitcher_ip;
	}
	
	@Override
	@ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
	public ProjectionProfile getProjection_profile() {
		return new ProjectionProfile();
	}

	public long getLeague_id() {
		return league_id;
	}

	public void setLeague_id(long league_id) {
		this.league_id = league_id;
	}

	public long getLeagueteam_id() {
		return leagueteam_id;
	}

	public void setLeagueteam_id(long leagueteam_id) {
		this.leagueteam_id = leagueteam_id;
	}

	public String getLeagueteam_name() {
		return leagueteam_name;
	}

	public void setLeagueteam_name(String leagueteam_name) {
		this.leagueteam_name = leagueteam_name;
	}

	public String getPlayer_position() {
		return player_position;
	}

	public void setPlayer_position(String player_position) {
		this.player_position = player_position;
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

	public Date getProjection_date() {
		return projection_date;
	}

	public void setProjection_date(Date projection_date) {
		this.projection_date = projection_date;
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

	public int getLive_auction_value() {
		return live_auction_value;
	}

	public void setLive_auction_value(int live_auction_value) {
		this.live_auction_value = live_auction_value;
	}

	public boolean isUnknownPlayer() {
		return UnknownPlayer;
	}

	public void setUnknownPlayer(boolean unknownPlayer) {
		UnknownPlayer = unknownPlayer;
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
