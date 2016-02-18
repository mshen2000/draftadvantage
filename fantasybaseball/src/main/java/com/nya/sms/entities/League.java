package com.nya.sms.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Index;

@Entity
public class League extends BaseEntity implements Serializable {
	
	private static final long serialVersionUID = 1;
	
	@Index
	String league_name;
	
	@Index
	String league_site;
	
	@Index
	Ref<User> user;
	
	@Index
	Integer num_of_teams;
	
	@Index
	String mlb_leagues;
	
	@Index
	List<Ref<LeagueTeam>> league_teams = new ArrayList<Ref<LeagueTeam>>();
	
	@Index
	Ref<ProjectionProfile> projection_profile;
	
	double team_salary;
	
	double avg_hitter_ba;
	
	double avg_hitter_ab;
	
	double avg_hitter_hits;
	
	double avg_pitcher_era;
	
	double avg_pitcher_ip;
	
	double avg_pitcher_er;
	
	double avg_pitcher_whip;
	
	double avg_pitcher_bbplushits;
	
	@Index
	boolean cat_hitter_hr;
	
	@Index
	boolean cat_hitter_rbi;
	
	@Index
	boolean cat_hitter_r;
	
	@Index
	boolean cat_hitter_sb;
	
	@Index
	boolean cat_hitter_avg;
	
	@Index
	boolean cat_pitcher_wins;
	
	@Index
	boolean cat_pitcher_saves;

	@Index
	boolean cat_pitcher_so;
	
	@Index
	boolean cat_pitcher_era;
	
	@Index
	boolean cat_pitcher_whip;
	
	@Index
	Integer num_1b;
	
	@Index
	Integer num_2b;
	
	@Index
	Integer num_3b;
	
	@Index
	Integer num_ss;
	
	@Index
	Integer num_c;
	
	@Index
	Integer num_of;
	
	@Index
	Integer num_p;
	
	@Index
	Integer num_util;
	
	@Index
	Integer num_res;
	
	@Index
	Integer num_mi;
	
	@Index
	Integer num_ci;
	

	public League() {
		this.cat_hitter_hr = false;
		this.cat_hitter_rbi = false;
		this.cat_hitter_r = false;
		this.cat_hitter_sb = false;
		this.cat_hitter_avg = false;
		this.cat_pitcher_wins = false;
		this.cat_pitcher_saves = false;
		this.cat_pitcher_so = false;
		this.cat_pitcher_era = false;
		this.cat_pitcher_whip = false;
	}

	public League(String league_name, User user) {
		this.league_name = league_name;
		this.user = Ref.create(user);
		this.cat_hitter_hr = false;
		this.cat_hitter_rbi = false;
		this.cat_hitter_r = false;
		this.cat_hitter_sb = false;
		this.cat_hitter_avg = false;
		this.cat_pitcher_wins = false;
		this.cat_pitcher_saves = false;
		this.cat_pitcher_so = false;
		this.cat_pitcher_era = false;
		this.cat_pitcher_whip = false;
	}

	public String getLeague_name() {
		return league_name;
	}

	public void setLeague_name(String league_name) {
		this.league_name = league_name;
	}

	public String getLeague_site() {
		return league_site;
	}

	public void setLeague_site(String league_site) {
		this.league_site = league_site;
	}

	public User getUser() {
		return user.get();
	}

	public void setUser(User user) {
		this.user = Ref.create(user);
	}

	public Integer getNum_of_teams() {
		return num_of_teams;
	}

	public void setNum_of_teams(Integer num_of_teams) {
		this.num_of_teams = num_of_teams;
	}

	public String getMlb_leagues() {
		return mlb_leagues;
	}

	public void setMlb_leagues(String mlb_leagues) {
		this.mlb_leagues = mlb_leagues;
	}

	public List<LeagueTeam> getLeague_teams() {
		List<LeagueTeam> t = new ArrayList<LeagueTeam>();
		for(Ref<LeagueTeam> l : league_teams){
			t.add(l.get());
		}
		return t;
	}

	public void setLeague_teams(List<LeagueTeam> league_teams) {
		List<Ref<LeagueTeam>> t = new ArrayList<Ref<LeagueTeam>>();
		for(LeagueTeam l : league_teams){
			t.add(Ref.create(l));
		}
		this.league_teams = t;
	}

	public ProjectionProfile getProjection_profile() {
		return projection_profile.get();
	}

	public void setProjection_profile(ProjectionProfile projection_profile) {
		this.projection_profile = Ref.create(projection_profile);
	}

	public double getTeam_salary() {
		return team_salary;
	}

	public void setTeam_salary(double team_salary) {
		this.team_salary = team_salary;
	}

	public double getAvg_hitter_ba() {
		return avg_hitter_ba;
	}

	public void setAvg_hitter_ba(double avg_hitter_ba) {
		this.avg_hitter_ba = avg_hitter_ba;
	}

	public double getAvg_hitter_ab() {
		return avg_hitter_ab;
	}

	public void setAvg_hitter_ab(double avg_hitter_ab) {
		this.avg_hitter_ab = avg_hitter_ab;
	}

	public double getAvg_hitter_hits() {
		return avg_hitter_hits;
	}

	public void setAvg_hitter_hits(double avg_hitter_hits) {
		this.avg_hitter_hits = avg_hitter_hits;
	}

	public double getAvg_pitcher_era() {
		return avg_pitcher_era;
	}

	public void setAvg_pitcher_era(double avg_pitcher_era) {
		this.avg_pitcher_era = avg_pitcher_era;
	}

	public double getAvg_pitcher_ip() {
		return avg_pitcher_ip;
	}

	public void setAvg_pitcher_ip(double avg_pitcher_ip) {
		this.avg_pitcher_ip = avg_pitcher_ip;
	}

	public double getAvg_pitcher_er() {
		return avg_pitcher_er;
	}

	public void setAvg_pitcher_er(double avg_pitcher_er) {
		this.avg_pitcher_er = avg_pitcher_er;
	}

	public double getAvg_pitcher_whip() {
		return avg_pitcher_whip;
	}

	public void setAvg_pitcher_whip(double avg_pitcher_whip) {
		this.avg_pitcher_whip = avg_pitcher_whip;
	}

	public double getAvg_pitcher_bbplushits() {
		return avg_pitcher_bbplushits;
	}

	public void setAvg_pitcher_bbplushits(double avg_pitcher_bbplushits) {
		this.avg_pitcher_bbplushits = avg_pitcher_bbplushits;
	}

	public boolean isCat_hitter_hr() {
		return cat_hitter_hr;
	}

	public void setCat_hitter_hr(boolean cat_hitter_hr) {
		this.cat_hitter_hr = cat_hitter_hr;
	}

	public boolean isCat_hitter_rbi() {
		return cat_hitter_rbi;
	}

	public void setCat_hitter_rbi(boolean cat_hitter_rbi) {
		this.cat_hitter_rbi = cat_hitter_rbi;
	}

	public boolean isCat_hitter_r() {
		return cat_hitter_r;
	}

	public void setCat_hitter_r(boolean cat_hitter_r) {
		this.cat_hitter_r = cat_hitter_r;
	}

	public boolean isCat_hitter_sb() {
		return cat_hitter_sb;
	}

	public void setCat_hitter_sb(boolean cat_hitter_sb) {
		this.cat_hitter_sb = cat_hitter_sb;
	}

	public boolean isCat_hitter_avg() {
		return cat_hitter_avg;
	}

	public void setCat_hitter_avg(boolean cat_hitter_avg) {
		this.cat_hitter_avg = cat_hitter_avg;
	}

	public boolean isCat_pitcher_wins() {
		return cat_pitcher_wins;
	}

	public void setCat_pitcher_wins(boolean cat_pitcher_wins) {
		this.cat_pitcher_wins = cat_pitcher_wins;
	}

	public boolean isCat_pitcher_saves() {
		return cat_pitcher_saves;
	}

	public void setCat_pitcher_saves(boolean cat_pitcher_saves) {
		this.cat_pitcher_saves = cat_pitcher_saves;
	}

	public boolean isCat_pitcher_so() {
		return cat_pitcher_so;
	}

	public void setCat_pitcher_so(boolean cat_pitcher_so) {
		this.cat_pitcher_so = cat_pitcher_so;
	}

	public boolean isCat_pitcher_era() {
		return cat_pitcher_era;
	}

	public void setCat_pitcher_era(boolean cat_pitcher_era) {
		this.cat_pitcher_era = cat_pitcher_era;
	}

	public boolean isCat_pitcher_whip() {
		return cat_pitcher_whip;
	}

	public void setCat_pitcher_whip(boolean cat_pitcher_whip) {
		this.cat_pitcher_whip = cat_pitcher_whip;
	}

	public Integer getNum_1b() {
		return num_1b;
	}

	public void setNum_1b(Integer num_1b) {
		this.num_1b = num_1b;
	}

	public Integer getNum_2b() {
		return num_2b;
	}

	public void setNum_2b(Integer num_2b) {
		this.num_2b = num_2b;
	}

	public Integer getNum_3b() {
		return num_3b;
	}

	public void setNum_3b(Integer num_3b) {
		this.num_3b = num_3b;
	}

	public Integer getNum_ss() {
		return num_ss;
	}

	public void setNum_ss(Integer num_ss) {
		this.num_ss = num_ss;
	}

	public Integer getNum_c() {
		return num_c;
	}

	public void setNum_c(Integer num_c) {
		this.num_c = num_c;
	}

	public Integer getNum_of() {
		return num_of;
	}

	public void setNum_of(Integer num_of) {
		this.num_of = num_of;
	}

	public Integer getNum_p() {
		return num_p;
	}

	public void setNum_p(Integer num_p) {
		this.num_p = num_p;
	}

	public Integer getNum_util() {
		return num_util;
	}

	public void setNum_util(Integer num_util) {
		this.num_util = num_util;
	}

	public Integer getNum_res() {
		return num_res;
	}

	public void setNum_res(Integer num_res) {
		this.num_res = num_res;
	}

	public Integer getNum_mi() {
		return num_mi;
	}

	public void setNum_mi(Integer num_mi) {
		this.num_mi = num_mi;
	}

	public Integer getNum_ci() {
		return num_ci;
	}

	public void setNum_ci(Integer num_ci) {
		this.num_ci = num_ci;
	}

	

}
