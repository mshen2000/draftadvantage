package com.nya.sms.entities;

import java.io.Serializable;
import java.util.Date;

import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Index;

//@Subclass(index = true)
@Entity
public class PlayerProjected extends BaseEntity implements Serializable {
	
	private static final long serialVersionUID = 1;
	
	@Index
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
	public float hitter_games;
	
	@Index
	public float hitter_pa;
	
	@Index
	public float hitter_ab;
	
	@Index
	public float hitter_runs;
	
	@Index
	public float hitter_hr;
	
	@Index
	public float hitter_rbi;
	
	@Index
	public float hitter_sb;
	
	@Index
	public float hitter_hits;
	
	@Index
	public float hitter_singles;
	
	@Index
	public float hitter_doubles;
	
	@Index
	public float hitter_triples;
	
	@Index
	public float hitter_tb;
	
	@Index
	public float hitter_so;
	
	@Index
	public float hitter_bb;
	
	@Index
	public float hitter_hbp;
	
	@Index
	public float hitter_sf;
	
	@Index
	public float hitter_cs;
	
	@Index
	public float hitter_avg;
	
	@Index
	public float hitter_obp;
	
	@Index
	public float hitter_slg;
	
	@Index
	public float hitter_ops;
	
	@Index
	public float pitcher_games;
	
	@Index
	public float pitcher_gs;
	
	@Index
	public float pitcher_qs;
	
	@Index
	public float pitcher_ip;
	
	@Index
	public float pitcher_w;
	
	@Index
	public float pitcher_l;
	
	@Index
	public float pitcher_sv;
	
	@Index
	public float pitcher_hld;
	
	@Index
	public float pitcher_era;
	
	@Index
	public float pitcher_siera;
	
	@Index
	public float pitcher_k;
	
	@Index
	public float pitcher_bb;
	
	@Index
	public float pitcher_hits;
	
	@Index
	public float pitcher_hbp;
	
	@Index
	public float pitcher_er;
	
	@Index
	public float pitcher_r;
	
	@Index
	public float pitcher_hr;
	
	@Index
	public float pitcher_gb_pct;
	
	@Index
	public float pitcher_fb_pct;
	
	@Index
	public float pitcher_ld_pct;
	
	@Index
	public float pitcher_babip;

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

	public float getHitter_games() {
		return hitter_games;
	}

	public void setHitter_games(float hitter_games) {
		this.hitter_games = hitter_games;
	}

	public float getHitter_pa() {
		return hitter_pa;
	}

	public void setHitter_pa(float hitter_pa) {
		this.hitter_pa = hitter_pa;
	}

	public float getHitter_ab() {
		return hitter_ab;
	}

	public void setHitter_ab(float hitter_ab) {
		this.hitter_ab = hitter_ab;
	}

	public float getHitter_runs() {
		return hitter_runs;
	}

	public void setHitter_runs(float hitter_runs) {
		this.hitter_runs = hitter_runs;
	}

	public float getHitter_hr() {
		return hitter_hr;
	}

	public void setHitter_hr(float hitter_hr) {
		this.hitter_hr = hitter_hr;
	}

	public float getHitter_rbi() {
		return hitter_rbi;
	}

	public void setHitter_rbi(float hitter_rbi) {
		this.hitter_rbi = hitter_rbi;
	}

	public float getHitter_sb() {
		return hitter_sb;
	}

	public void setHitter_sb(float hitter_sb) {
		this.hitter_sb = hitter_sb;
	}

	public float getHitter_hits() {
		return hitter_hits;
	}

	public void setHitter_hits(float hitter_hits) {
		this.hitter_hits = hitter_hits;
	}

	public float getHitter_singles() {
		return hitter_singles;
	}

	public void setHitter_singles(float hitter_singles) {
		this.hitter_singles = hitter_singles;
	}

	public float getHitter_doubles() {
		return hitter_doubles;
	}

	public void setHitter_doubles(float hitter_doubles) {
		this.hitter_doubles = hitter_doubles;
	}

	public float getHitter_triples() {
		return hitter_triples;
	}

	public void setHitter_triples(float hitter_triples) {
		this.hitter_triples = hitter_triples;
	}

	public float getHitter_tb() {
		return hitter_tb;
	}

	public void setHitter_tb(float hitter_tb) {
		this.hitter_tb = hitter_tb;
	}

	public float getHitter_so() {
		return hitter_so;
	}

	public void setHitter_so(float hitter_so) {
		this.hitter_so = hitter_so;
	}

	public float getHitter_bb() {
		return hitter_bb;
	}

	public void setHitter_bb(float hitter_bb) {
		this.hitter_bb = hitter_bb;
	}

	public float getHitter_hbp() {
		return hitter_hbp;
	}

	public void setHitter_hbp(float hitter_hbp) {
		this.hitter_hbp = hitter_hbp;
	}

	public float getHitter_sf() {
		return hitter_sf;
	}

	public void setHitter_sf(float hitter_sf) {
		this.hitter_sf = hitter_sf;
	}

	public float getHitter_cs() {
		return hitter_cs;
	}

	public void setHitter_cs(float hitter_cs) {
		this.hitter_cs = hitter_cs;
	}

	public float getHitter_avg() {
		return hitter_avg;
	}

	public void setHitter_avg(float hitter_avg) {
		this.hitter_avg = hitter_avg;
	}

	public float getHitter_obp() {
		return hitter_obp;
	}

	public void setHitter_obp(float hitter_obp) {
		this.hitter_obp = hitter_obp;
	}

	public float getHitter_slg() {
		return hitter_slg;
	}

	public void setHitter_slg(float hitter_slg) {
		this.hitter_slg = hitter_slg;
	}

	public float getHitter_ops() {
		return hitter_ops;
	}

	public void setHitter_ops(float hitter_ops) {
		this.hitter_ops = hitter_ops;
	}

	public float getPitcher_games() {
		return pitcher_games;
	}

	public void setPitcher_games(float pitcher_games) {
		this.pitcher_games = pitcher_games;
	}

	public float getPitcher_gs() {
		return pitcher_gs;
	}

	public void setPitcher_gs(float pitcher_gs) {
		this.pitcher_gs = pitcher_gs;
	}

	public float getPitcher_qs() {
		return pitcher_qs;
	}

	public void setPitcher_qs(float pitcher_qs) {
		this.pitcher_qs = pitcher_qs;
	}

	public float getPitcher_ip() {
		return pitcher_ip;
	}

	public void setPitcher_ip(float pitcher_ip) {
		this.pitcher_ip = pitcher_ip;
	}

	public float getPitcher_w() {
		return pitcher_w;
	}

	public void setPitcher_w(float pitcher_w) {
		this.pitcher_w = pitcher_w;
	}

	public float getPitcher_l() {
		return pitcher_l;
	}

	public void setPitcher_l(float pitcher_l) {
		this.pitcher_l = pitcher_l;
	}

	public float getPitcher_sv() {
		return pitcher_sv;
	}

	public void setPitcher_sv(float pitcher_sv) {
		this.pitcher_sv = pitcher_sv;
	}

	public float getPitcher_hld() {
		return pitcher_hld;
	}

	public void setPitcher_hld(float pitcher_hld) {
		this.pitcher_hld = pitcher_hld;
	}

	public float getPitcher_era() {
		return pitcher_era;
	}

	public void setPitcher_era(float pitcher_era) {
		this.pitcher_era = pitcher_era;
	}

	public float getPitcher_siera() {
		return pitcher_siera;
	}

	public void setPitcher_siera(float pitcher_siera) {
		this.pitcher_siera = pitcher_siera;
	}

	public float getPitcher_k() {
		return pitcher_k;
	}

	public void setPitcher_k(float pitcher_k) {
		this.pitcher_k = pitcher_k;
	}

	public float getPitcher_bb() {
		return pitcher_bb;
	}

	public void setPitcher_bb(float pitcher_bb) {
		this.pitcher_bb = pitcher_bb;
	}

	public float getPitcher_hits() {
		return pitcher_hits;
	}

	public void setPitcher_hits(float pitcher_hits) {
		this.pitcher_hits = pitcher_hits;
	}

	public float getPitcher_hbp() {
		return pitcher_hbp;
	}

	public void setPitcher_hbp(float pitcher_hbp) {
		this.pitcher_hbp = pitcher_hbp;
	}

	public float getPitcher_er() {
		return pitcher_er;
	}

	public void setPitcher_er(float pitcher_er) {
		this.pitcher_er = pitcher_er;
	}

	public float getPitcher_r() {
		return pitcher_r;
	}

	public void setPitcher_r(float pitcher_r) {
		this.pitcher_r = pitcher_r;
	}

	public float getPitcher_hr() {
		return pitcher_hr;
	}

	public void setPitcher_hr(float pitcher_hr) {
		this.pitcher_hr = pitcher_hr;
	}

	public float getPitcher_gb_pct() {
		return pitcher_gb_pct;
	}

	public void setPitcher_gb_pct(float pitcher_gb_pct) {
		this.pitcher_gb_pct = pitcher_gb_pct;
	}

	public float getPitcher_fb_pct() {
		return pitcher_fb_pct;
	}

	public void setPitcher_fb_pct(float pitcher_fb_pct) {
		this.pitcher_fb_pct = pitcher_fb_pct;
	}

	public float getPitcher_ld_pct() {
		return pitcher_ld_pct;
	}

	public void setPitcher_ld_pct(float pitcher_ld_pct) {
		this.pitcher_ld_pct = pitcher_ld_pct;
	}

	public float getPitcher_babip() {
		return pitcher_babip;
	}

	public void setPitcher_babip(float pitcher_babip) {
		this.pitcher_babip = pitcher_babip;
	}


	public String getOther_id_name() {
		return other_id_name;
	}

	public void setOther_id_name(String other_id_name) {
		this.other_id_name = other_id_name;
	}
	
	

}
