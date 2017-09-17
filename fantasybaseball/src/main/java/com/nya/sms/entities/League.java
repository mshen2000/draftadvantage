package com.nya.sms.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.api.server.spi.config.AnnotationBoolean;
import com.google.api.server.spi.config.ApiResourceProperty;
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
	int league_year;
	
	@Index
	Ref<User> user;

	int num_of_teams;
	
	@Index
	String mlb_leagues;
	
	@Index
	List<Ref<LeagueTeam>> league_teams = new ArrayList<Ref<LeagueTeam>>();
	
	@Index
	Ref<ProjectionProfile> projection_profile;
	
	List<String> position_priority_list = new ArrayList<String>();
	
	double team_salary;
	double avg_hitter_ba;
	double avg_hitter_ab;
	double avg_hitter_hits;
	double avg_hitter_obp;
	double avg_hitter_pa;
	double avg_hitter_bb;
	double avg_hitter_hbp;
	double avg_pitcher_era;
	double avg_pitcher_ip;
	double avg_pitcher_er;
	double avg_pitcher_whip;
	double avg_pitcher_bbplushits;
	
	boolean cat_hitter_hr;
	boolean cat_hitter_rbi;
	boolean cat_hitter_r;
	boolean cat_hitter_sb;
	boolean cat_hitter_avg;
	boolean cat_pitcher_wins;
	boolean cat_pitcher_saves;
	boolean cat_pitcher_so;
	boolean cat_pitcher_era;
	boolean cat_pitcher_whip;
	
	// New Categories: Holds, OBP
	boolean cat_pitcher_holds;
	boolean cat_hitter_obp;

	int num_1b;
	int num_2b;
	int num_3b;
	int num_ss;
	int num_c;
	int num_of;
	int num_p;
	int num_util;
	int num_res;
	int num_mi;
	int num_ci;
	
	public League() {
		this.cat_hitter_hr = false;
		this.cat_hitter_rbi = false;
		this.cat_hitter_r = false;
		this.cat_hitter_sb = false;
		this.cat_hitter_avg = false;
		this.cat_hitter_obp = false;
		this.cat_pitcher_wins = false;
		this.cat_pitcher_saves = false;
		this.cat_pitcher_so = false;
		this.cat_pitcher_era = false;
		this.cat_pitcher_whip = false;
		this.cat_pitcher_holds = false;
		
		if (this.position_priority_list.isEmpty()){
			List<String> plist = new ArrayList<String>();
			plist.add("DEFAULT");
			this.position_priority_list = plist;
		}

	}

	public League(String league_name, User user) {
		this.league_name = league_name;
		this.user = Ref.create(user);
		this.cat_hitter_hr = false;
		this.cat_hitter_rbi = false;
		this.cat_hitter_r = false;
		this.cat_hitter_sb = false;
		this.cat_hitter_avg = false;
		this.cat_hitter_obp = false;
		this.cat_pitcher_wins = false;
		this.cat_pitcher_saves = false;
		this.cat_pitcher_so = false;
		this.cat_pitcher_era = false;
		this.cat_pitcher_whip = false;
		this.cat_pitcher_holds = false;
		
		// New Categories
		this.cat_pitcher_holds = false;
		this.cat_hitter_obp = false;
		
		if (this.position_priority_list.isEmpty()){
			List<String> plist = new ArrayList<String>();
			plist.add("DEFAULT");
			this.position_priority_list = plist;
		}

	}
	
	// Create a copy of a league "l" for export
	// (removes user and projection profile)
	public League (League l){
		this.league_name = l.league_name ;
		this.league_site = l.league_site ;
		this.league_year = l.league_year ;
		this.num_of_teams = l.num_of_teams ;
		this.mlb_leagues = l.mlb_leagues ;
		this.league_teams = l.league_teams ;
		this.team_salary = l.team_salary ;
		this.avg_hitter_ba = l.avg_hitter_ba ;
		this.avg_hitter_ab = l.avg_hitter_ab ;
		this.avg_hitter_hits = l.avg_hitter_hits ;
		this.avg_hitter_obp = l.avg_hitter_obp;
		this.avg_hitter_pa = l.avg_hitter_pa;
		this.avg_hitter_bb = l.avg_hitter_bb;
		this.avg_hitter_hbp = l.avg_hitter_hbp;
		this.avg_pitcher_era = l.avg_pitcher_era ;
		this.avg_pitcher_ip = l.avg_pitcher_ip ;
		this.avg_pitcher_er = l.avg_pitcher_er ;
		this.avg_pitcher_whip = l.avg_pitcher_whip ;
		this.avg_pitcher_bbplushits = l.avg_pitcher_bbplushits ;
		this.cat_hitter_hr = l.cat_hitter_hr ;
		this.cat_hitter_rbi = l.cat_hitter_rbi ;
		this.cat_hitter_r = l.cat_hitter_r ;
		this.cat_hitter_sb = l.cat_hitter_sb ;
		this.cat_hitter_avg = l.cat_hitter_avg ;
		this.cat_pitcher_wins = l.cat_pitcher_wins ;
		this.cat_pitcher_saves = l.cat_pitcher_saves ;
		this.cat_pitcher_so = l.cat_pitcher_so ;
		this.cat_pitcher_era = l.cat_pitcher_era ;
		this.cat_pitcher_whip = l.cat_pitcher_whip ;
		this.num_1b = l.num_1b ;
		this.num_2b = l.num_2b ;
		this.num_3b = l.num_3b ;
		this.num_ss = l.num_ss ;
		this.num_c = l.num_c ;
		this.num_of = l.num_of ;
		this.num_p = l.num_p ;
		this.num_util = l.num_util ;
		this.num_res = l.num_res ;
		this.num_mi = l.num_mi ;
		this.num_ci = l.num_ci ;
		
		// New Categories
		this.cat_pitcher_holds = l.cat_pitcher_holds;
		this.cat_hitter_obp = l.cat_hitter_obp;
		
		this.position_priority_list = l.position_priority_list;
		
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
		if (this.user == null) return new User("no-user","no-user");
		else return user.get();
	}

	public void setUser(User user) {
		this.user = Ref.create(user);
	}

	public int getNum_of_teams() {
		return num_of_teams;
	}

	public void setNum_of_teams(int num_of_teams) {
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
	
	 @ApiResourceProperty(ignored = AnnotationBoolean.TRUE) 
	public List<Ref<LeagueTeam>> getLeague_teamRefs() {
		return this.league_teams;
	}

	public void setLeague_teams(List<LeagueTeam> league_teams) {
		List<Ref<LeagueTeam>> t = new ArrayList<Ref<LeagueTeam>>();
		for(LeagueTeam l : league_teams){
			t.add(Ref.create(l));
		}
		this.league_teams = t;
	}

	public ProjectionProfile getProjection_profile() {
		if (this.projection_profile == null) return new ProjectionProfile();
		return projection_profile.get();
	}

	public void setProjection_profile(ProjectionProfile projection_profile) {
		this.projection_profile = Ref.create(projection_profile);
	}

	public List<String> getPosition_priority_list() {
		return position_priority_list;
	}

	public void setPosition_priority_list(List<String> position_priority_list) {
		this.position_priority_list = position_priority_list;
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
	
	public double getAvg_hitter_obp() {
		return avg_hitter_obp;
	}

	public void setAvg_hitter_obp(double avg_hitter_obp) {
		this.avg_hitter_obp = avg_hitter_obp;
	}

	public double getAvg_hitter_pa() {
		return avg_hitter_pa;
	}

	public void setAvg_hitter_pa(double avg_hitter_pa) {
		this.avg_hitter_pa = avg_hitter_pa;
	}

	public double getAvg_hitter_bb() {
		return avg_hitter_bb;
	}

	public void setAvg_hitter_bb(double avg_hitter_bb) {
		this.avg_hitter_bb = avg_hitter_bb;
	}

	public double getAvg_hitter_hbp() {
		return avg_hitter_hbp;
	}

	public void setAvg_hitter_hbp(double avg_hitter_hbp) {
		this.avg_hitter_hbp = avg_hitter_hbp;
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

	public boolean isCat_pitcher_holds() {
		return cat_pitcher_holds;
	}

	public void setCat_pitcher_holds(boolean cat_pitcher_holds) {
		this.cat_pitcher_holds = cat_pitcher_holds;
	}

	public boolean isCat_hitter_obp() {
		return cat_hitter_obp;
	}

	public void setCat_hitter_obp(boolean cat_hitter_obp) {
		this.cat_hitter_obp = cat_hitter_obp;
	}

	public int getNum_1b() {
		return num_1b;
	}

	public void setNum_1b(int num_1b) {
		this.num_1b = num_1b;
	}

	public int getNum_2b() {
		return num_2b;
	}

	public void setNum_2b(int num_2b) {
		this.num_2b = num_2b;
	}

	public int getNum_3b() {
		return num_3b;
	}

	public void setNum_3b(int num_3b) {
		this.num_3b = num_3b;
	}

	public int getNum_ss() {
		return num_ss;
	}

	public void setNum_ss(int num_ss) {
		this.num_ss = num_ss;
	}

	public int getNum_c() {
		return num_c;
	}

	public void setNum_c(int num_c) {
		this.num_c = num_c;
	}

	public int getNum_of() {
		return num_of;
	}

	public void setNum_of(int num_of) {
		this.num_of = num_of;
	}

	public int getNum_p() {
		return num_p;
	}

	public void setNum_p(int num_p) {
		this.num_p = num_p;
	}

	public int getNum_util() {
		return num_util;
	}

	public void setNum_util(int num_util) {
		this.num_util = num_util;
	}

	public int getNum_res() {
		return num_res;
	}

	public void setNum_res(int num_res) {
		this.num_res = num_res;
	}

	public int getNum_mi() {
		return num_mi;
	}

	public void setNum_mi(int num_mi) {
		this.num_mi = num_mi;
	}

	public int getNum_ci() {
		return num_ci;
	}

	public void setNum_ci(int num_ci) {
		this.num_ci = num_ci;
	}

	public int getLeague_year() {
		return league_year;
	}

	public void setLeague_year(int league_year) {
		this.league_year = league_year;
	}

	

}
