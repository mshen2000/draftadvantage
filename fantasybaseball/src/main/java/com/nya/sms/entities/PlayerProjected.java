package com.nya.sms.entities;

import java.io.Serializable;

import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Load;

//@Subclass(index = true)
@Entity
public class PlayerProjected extends BaseEntity implements Serializable {
	
	private static final long serialVersionUID = 1;
	
	@Index
	@Load
	Ref<ProjectionProfile> projection_profile;
	
	@Index
	public String mlb_id;
	
	@Index 
	public String other_id_name;
	
	@Index
	public String other_id;
	
	@Index
	public String full_name;
	
	@Index
	public String first_name;
	
	@Index
	public String last_name;
	
	@Index
	String dc_status;
	
	@Index
	Integer age;
	
	@Index
	public String team;
	
	@Index
	public String al_nl;
	
	@Index
	public String pitcher_hitter;
	
	@Index 
	public String hitter_bats;
	
	@Index 
	public String pitcher_throws;
	
	@Index
	public String hitter_pos_elig_espn;
	
	@Index
	public String hitter_pos_elig_yahoo;
	
	@Index
	public String pitcher_pos;
	
	@Index
	public double hitter_games;
	
	@Index
	public double hitter_pa;
	
	@Index
	public double hitter_ab;
	
	@Index
	public double hitter_runs;
	
	@Index
	public double hitter_hr;
	
	@Index
	public double hitter_rbi;
	
	@Index
	public double hitter_sb;
	
	@Index
	public double hitter_hits;
	
	@Index
	public double hitter_singles;
	
	@Index
	public double hitter_doubles;
	
	@Index
	public double hitter_triples;
	
	@Index
	public double hitter_tb;
	
	@Index
	public double hitter_so;
	
	@Index
	public double hitter_bb;
	
	@Index
	public double hitter_hbp;
	
	@Index
	public double hitter_sf;
	
	@Index
	public double hitter_cs;
	
	@Index
	public double hitter_avg;
	
	@Index
	public double hitter_obp;
	
	@Index
	public double hitter_slg;
	
	@Index
	public double hitter_ops;
	
	@Index
	public double pitcher_games;
	
	@Index
	public double pitcher_gs;
	
	@Index
	public double pitcher_qs;
	
	@Index
	public double pitcher_ip;
	
	@Index
	public double pitcher_w;
	
	@Index
	public double pitcher_l;
	
	@Index
	public double pitcher_sv;
	
	@Index
	public double pitcher_hld;
	
	@Index
	public double pitcher_era;
	
	@Index
	public double pitcher_siera;
	
	@Index
	public double pitcher_k;
	
	@Index
	public double pitcher_bb;
	
	@Index
	public double pitcher_hits;
	
	@Index
	public double pitcher_hbp;
	
	@Index
	public double pitcher_er;
	
	@Index
	public double pitcher_r;
	
	@Index
	public double pitcher_hr;
	
	@Index
	public double pitcher_gb_pct;
	
	@Index
	public double pitcher_fb_pct;
	
	@Index
	public double pitcher_ld_pct;
	
	@Index
	public double pitcher_babip;

	public PlayerProjected() {
	}

	public PlayerProjected(String other_id_name, String other_id, String full_name, String pitcher_hitter) {
		this.other_id_name = other_id_name;
		this.other_id = other_id;
		this.full_name = full_name;
		this.pitcher_hitter = pitcher_hitter;
	}
	
	// This getter-setter is custom updated to convert Ref to a ProjectionProfile
	public ProjectionProfile getProjection_profile() {
		return projection_profile.get();
	}

	public void setProjection_profile(ProjectionProfile projection_profile) {
		this.projection_profile = Ref.create(projection_profile);
	}

	public String getMlb_id() {
		return mlb_id;
	}

	public void setMlb_id(String mlb_id) {
		this.mlb_id = mlb_id;
	}

	public String getOther_id() {
		return other_id;
	}

	public void setOther_id(String other_id) {
		this.other_id = other_id;
	}

	public String getFull_name() {
		return full_name;
	}

	public void setFull_name(String full_name) {
		this.full_name = full_name;
	}

	public String getFirst_name() {
		return first_name;
	}

	public void setFirst_name(String first_name) {
		this.first_name = first_name;
	}

	public String getLast_name() {
		return last_name;
	}

	public void setLast_name(String last_name) {
		this.last_name = last_name;
	}

	public String getDc_status() {
		return dc_status;
	}

	public void setDc_status(String dc_status) {
		this.dc_status = dc_status;
	}

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

	public String getTeam() {
		return team;
	}

	public void setTeam(String team) {
		this.team = team;
	}

	public String getAl_nl() {
		return al_nl;
	}

	public void setAl_nl(String al_nl) {
		this.al_nl = al_nl;
	}

	public String getPitcher_hitter() {
		return pitcher_hitter;
	}

	public void setPitcher_hitter(String pitcher_hitter) {
		this.pitcher_hitter = pitcher_hitter;
	}

	public String getHitter_bats() {
		return hitter_bats;
	}

	public void setHitter_bats(String hitter_bats) {
		this.hitter_bats = hitter_bats;
	}

	public String getPitcher_throws() {
		return pitcher_throws;
	}

	public void setPitcher_throws(String pitcher_throws) {
		this.pitcher_throws = pitcher_throws;
	}

	public String getHitter_pos_elig_espn() {
		return hitter_pos_elig_espn;
	}

	public void setHitter_pos_elig_espn(String hitter_pos_elig_espn) {
		this.hitter_pos_elig_espn = hitter_pos_elig_espn;
	}

	public String getHitter_pos_elig_yahoo() {
		return hitter_pos_elig_yahoo;
	}

	public void setHitter_pos_elig_yahoo(String hitter_pos_elig_yahoo) {
		this.hitter_pos_elig_yahoo = hitter_pos_elig_yahoo;
	}

	public String getPitcher_pos() {
		return pitcher_pos;
	}

	public void setPitcher_pos(String pitcher_pos) {
		this.pitcher_pos = pitcher_pos;
	}

	public double getHitter_games() {
		return hitter_games;
	}

	public void setHitter_games(double hitter_games) {
		this.hitter_games = hitter_games;
	}

	public double getHitter_pa() {
		return hitter_pa;
	}

	public void setHitter_pa(double hitter_pa) {
		this.hitter_pa = hitter_pa;
	}

	public double getHitter_ab() {
		return hitter_ab;
	}

	public void setHitter_ab(double hitter_ab) {
		this.hitter_ab = hitter_ab;
	}

	public double getHitter_runs() {
		return hitter_runs;
	}

	public void setHitter_runs(double hitter_runs) {
		this.hitter_runs = hitter_runs;
	}

	public double getHitter_hr() {
		return hitter_hr;
	}

	public void setHitter_hr(double hitter_hr) {
		this.hitter_hr = hitter_hr;
	}

	public double getHitter_rbi() {
		return hitter_rbi;
	}

	public void setHitter_rbi(double hitter_rbi) {
		this.hitter_rbi = hitter_rbi;
	}

	public double getHitter_sb() {
		return hitter_sb;
	}

	public void setHitter_sb(double hitter_sb) {
		this.hitter_sb = hitter_sb;
	}

	public double getHitter_hits() {
		return hitter_hits;
	}

	public void setHitter_hits(double hitter_hits) {
		this.hitter_hits = hitter_hits;
	}

	public double getHitter_singles() {
		return hitter_singles;
	}

	public void setHitter_singles(double hitter_singles) {
		this.hitter_singles = hitter_singles;
	}

	public double getHitter_doubles() {
		return hitter_doubles;
	}

	public void setHitter_doubles(double hitter_doubles) {
		this.hitter_doubles = hitter_doubles;
	}

	public double getHitter_triples() {
		return hitter_triples;
	}

	public void setHitter_triples(double hitter_triples) {
		this.hitter_triples = hitter_triples;
	}

	public double getHitter_tb() {
		return hitter_tb;
	}

	public void setHitter_tb(double hitter_tb) {
		this.hitter_tb = hitter_tb;
	}

	public double getHitter_so() {
		return hitter_so;
	}

	public void setHitter_so(double hitter_so) {
		this.hitter_so = hitter_so;
	}

	public double getHitter_bb() {
		return hitter_bb;
	}

	public void setHitter_bb(double hitter_bb) {
		this.hitter_bb = hitter_bb;
	}

	public double getHitter_hbp() {
		return hitter_hbp;
	}

	public void setHitter_hbp(double hitter_hbp) {
		this.hitter_hbp = hitter_hbp;
	}

	public double getHitter_sf() {
		return hitter_sf;
	}

	public void setHitter_sf(double hitter_sf) {
		this.hitter_sf = hitter_sf;
	}

	public double getHitter_cs() {
		return hitter_cs;
	}

	public void setHitter_cs(double hitter_cs) {
		this.hitter_cs = hitter_cs;
	}

	public double getHitter_avg() {
		return hitter_avg;
	}

	public void setHitter_avg(double hitter_avg) {
		this.hitter_avg = hitter_avg;
	}

	public double getHitter_obp() {
		return hitter_obp;
	}

	public void setHitter_obp(double hitter_obp) {
		this.hitter_obp = hitter_obp;
	}

	public double getHitter_slg() {
		return hitter_slg;
	}

	public void setHitter_slg(double hitter_slg) {
		this.hitter_slg = hitter_slg;
	}

	public double getHitter_ops() {
		return hitter_ops;
	}

	public void setHitter_ops(double hitter_ops) {
		this.hitter_ops = hitter_ops;
	}

	public double getPitcher_games() {
		return pitcher_games;
	}

	public void setPitcher_games(double pitcher_games) {
		this.pitcher_games = pitcher_games;
	}

	public double getPitcher_gs() {
		return pitcher_gs;
	}

	public void setPitcher_gs(double pitcher_gs) {
		this.pitcher_gs = pitcher_gs;
	}

	public double getPitcher_qs() {
		return pitcher_qs;
	}

	public void setPitcher_qs(double pitcher_qs) {
		this.pitcher_qs = pitcher_qs;
	}

	public double getPitcher_ip() {
		return pitcher_ip;
	}

	public void setPitcher_ip(double pitcher_ip) {
		this.pitcher_ip = pitcher_ip;
	}

	public double getPitcher_w() {
		return pitcher_w;
	}

	public void setPitcher_w(double pitcher_w) {
		this.pitcher_w = pitcher_w;
	}

	public double getPitcher_l() {
		return pitcher_l;
	}

	public void setPitcher_l(double pitcher_l) {
		this.pitcher_l = pitcher_l;
	}

	public double getPitcher_sv() {
		return pitcher_sv;
	}

	public void setPitcher_sv(double pitcher_sv) {
		this.pitcher_sv = pitcher_sv;
	}

	public double getPitcher_hld() {
		return pitcher_hld;
	}

	public void setPitcher_hld(double pitcher_hld) {
		this.pitcher_hld = pitcher_hld;
	}

	public double getPitcher_era() {
		return pitcher_era;
	}

	public void setPitcher_era(double pitcher_era) {
		this.pitcher_era = pitcher_era;
	}

	public double getPitcher_siera() {
		return pitcher_siera;
	}

	public void setPitcher_siera(double pitcher_siera) {
		this.pitcher_siera = pitcher_siera;
	}

	public double getPitcher_k() {
		return pitcher_k;
	}

	public void setPitcher_k(double pitcher_k) {
		this.pitcher_k = pitcher_k;
	}

	public double getPitcher_bb() {
		return pitcher_bb;
	}

	public void setPitcher_bb(double pitcher_bb) {
		this.pitcher_bb = pitcher_bb;
	}

	public double getPitcher_hits() {
		return pitcher_hits;
	}

	public void setPitcher_hits(double pitcher_hits) {
		this.pitcher_hits = pitcher_hits;
	}

	public double getPitcher_hbp() {
		return pitcher_hbp;
	}

	public void setPitcher_hbp(double pitcher_hbp) {
		this.pitcher_hbp = pitcher_hbp;
	}

	public double getPitcher_er() {
		return pitcher_er;
	}

	public void setPitcher_er(double pitcher_er) {
		this.pitcher_er = pitcher_er;
	}

	public double getPitcher_r() {
		return pitcher_r;
	}

	public void setPitcher_r(double pitcher_r) {
		this.pitcher_r = pitcher_r;
	}

	public double getPitcher_hr() {
		return pitcher_hr;
	}

	public void setPitcher_hr(double pitcher_hr) {
		this.pitcher_hr = pitcher_hr;
	}

	public double getPitcher_gb_pct() {
		return pitcher_gb_pct;
	}

	public void setPitcher_gb_pct(double pitcher_gb_pct) {
		this.pitcher_gb_pct = pitcher_gb_pct;
	}

	public double getPitcher_fb_pct() {
		return pitcher_fb_pct;
	}

	public void setPitcher_fb_pct(double pitcher_fb_pct) {
		this.pitcher_fb_pct = pitcher_fb_pct;
	}

	public double getPitcher_ld_pct() {
		return pitcher_ld_pct;
	}

	public void setPitcher_ld_pct(double pitcher_ld_pct) {
		this.pitcher_ld_pct = pitcher_ld_pct;
	}

	public double getPitcher_babip() {
		return pitcher_babip;
	}

	public void setPitcher_babip(double pitcher_babip) {
		this.pitcher_babip = pitcher_babip;
	}


	public String getOther_id_name() {
		return other_id_name;
	}

	public void setOther_id_name(String other_id_name) {
		this.other_id_name = other_id_name;
	}
	
	

}
