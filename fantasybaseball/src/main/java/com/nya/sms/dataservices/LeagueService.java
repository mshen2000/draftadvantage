package com.nya.sms.dataservices;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.stat.StatUtils;
import org.apache.commons.math3.util.FastMath;

import com.app.endpoints.entities.LeagueModalFields;
import com.app.endpoints.entities.PositionalZContainer;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.Ref;
import com.nya.sms.entities.League;
import com.nya.sms.entities.LeaguePlayer;
import com.nya.sms.entities.LeagueTeam;
import com.nya.sms.entities.PlayerProjected;
import com.nya.sms.entities.ProjectionProfile;
import com.nya.sms.entities.User;

/**
 * @author Michael
 *
 */
public class LeagueService extends AbstractDataServiceImpl<League>{
	
	private static final long serialVersionUID = 1L;
	
	public static final String LEAGUE_SITE_CBS = "CBS";
	public static final String LEAGUE_SITE_ESPN = "ESPN";
	public static final String MLB_LEAGUES_AL = "AL";
	public static final String MLB_LEAGUES_NL = "NL";
	public static final String MLB_LEAGUES_BOTH = "BOTH";

	public LeagueService(Class<League> clazz) {
		super(clazz);
		// TODO Auto-generated constructor stub
	}
	
	public LeagueModalFields getLeagueModalFields(){
		List<String> mlb_leagues = new ArrayList<String>();
		List<String> league_sites = new ArrayList<String>();
		league_sites.add(LEAGUE_SITE_CBS);
		league_sites.add(LEAGUE_SITE_ESPN);
		mlb_leagues.add(MLB_LEAGUES_AL);
		mlb_leagues.add(MLB_LEAGUES_NL);
		mlb_leagues.add(MLB_LEAGUES_BOTH);
		
		return new LeagueModalFields(mlb_leagues, league_sites);
		
	}
	
	
	/* (non-Javadoc)
	 * @see com.nya.sms.dataservices.AbstractDataServiceImpl#delete(java.lang.Long)
	 * When deleting League, also delete associated teams
	 */
	@Override
	public void delete(Long id){
		
		League league = this.get(id);
		List<LeagueTeam> lt = league.getLeague_teams();
		
		for (LeagueTeam t : lt){
			getLeagueTeamService().delete(t.id);
		}
		
		super.delete(id);
		
	}
	
	public List<League> getUserLeagues(String username){
		
		getIdentityService().getUser(username);
		
		Key<User> userkey = Key.create(User.class, getIdentityService().getUser(username).getId());
		
		List<League> leagues = ofy().load().type(League.class).filter("user", userkey).list();
		
		return leagues;
	}
	
	public boolean isLeagueTeamsMaxed(Long league_id){
		
		League league = this.get(league_id);
		
		if (league.getLeague_teams().isEmpty()){

			return false;
		} else if (league.getLeague_teams().size() >= league.getNum_of_teams()) {
			return true;
		}
		
		return false;
		
	}
	
	public void addLeagueTeam(Long league_id, Long team_id, String uname) throws IndexOutOfBoundsException {

		League league = this.get(league_id);
		
		if (league.getLeague_teams().isEmpty()){
			List<LeagueTeam> lg = league.getLeague_teams();
			lg.add(getLeagueTeamService().get(team_id));
			league.setLeague_teams(lg);
			this.save(league, uname);
			return;
		} else {
			if (league.getLeague_teams().size() >= league.getNum_of_teams()) {
				throw new IndexOutOfBoundsException("League has maximum number of teams: " + league.getNum_of_teams());
			} else {
				List<LeagueTeam> lg = league.getLeague_teams();
				lg.add(getLeagueTeamService().get(team_id));
				league.setLeague_teams(lg);
				this.save(league, uname);
			}
		}

	}
	
	
	/**
	 * Description:	Removes Team from League, then deletes the Team
	 * @param league_id
	 * @param team_id
	 * @param uname 
	 */
	public void deleteLeagueTeam(Long league_id, Long team_id, String uname) throws IndexOutOfBoundsException{

		League league = this.get(league_id);

		List<LeagueTeam> lg = league.getLeague_teams();
		
		boolean isTeamFound = false;
		long delete_team_id = 0;
		
		// System.out.println("In deleteLeagueTeam: Number of league teams = " + lg.size());
		
		Iterator<LeagueTeam> iter;
		List<LeagueTeam> teams = new ArrayList<LeagueTeam>();

		for (iter = lg.listIterator(); iter.hasNext(); ) {
			LeagueTeam a = iter.next();
			// System.out.println("Delete check: " + a.getId() + ", " + team_id);
		    if (a.getId().equals(team_id)) {
		    	// System.out.println("In deleteLeagueTeam: Team to remove found = " + a.getTeam_name() + ", " + a.getId());
		    	isTeamFound = true;
		    	delete_team_id = a.getId();
		        iter.remove();
		    } else {
		    	teams.add(a);
		    }
		}
		
		if (!isTeamFound){
			throw new IndexOutOfBoundsException("Team does not exist in league");
		}
		
        // List<LeagueTeam> teams = Lists.newArrayList(iter);
        league.setLeague_teams(teams);
        // System.out.println("In deleteLeagueTeam: Number of league teams after update = " + teams.size());
        this.save(league, uname);
        getLeagueTeamService().delete(delete_team_id);
	}
	

	
	
	/**
	 * Description:	Create/update league player data from projection data
	 * @param league_id
	 * @param username 
	 */
	public void updateLeaguePlayerData(long league_id, String username) {
		
		System.out.println("Update League Player Data: BEGIN");
		
		System.out.println("Update League Player Data: Deleting LeaguePlayers w/out projections...");

		League league = this.get(league_id);
		
		List<LeaguePlayer> leagueplayers = getLeaguePlayerService().getLeaguePlayersByLeague(league_id, username);
		
		int deleted = 0;
		
		// Delete league players that don't have projections
		for (LeaguePlayer lp : leagueplayers){
			
			if (lp.getPlayer_projected() == null){
				getLeaguePlayerService().delete(lp.getId());
				deleted++;
				// System.out.println("Deleting LeaguePlayer with null projection:  " + lp.getFull_name());
			}
			
		}
		
		System.out.println("Update League Player Data: " + deleted + " LeaguePlayers deleted.");
		
		System.out.println("Update League Player Data: Update existing LeaguePlayers...");
		
		leagueplayers = getLeaguePlayerService().getLeaguePlayersByLeague(league_id, username);
		
		// Update existing league players
		if (!leagueplayers.isEmpty()){
			
			Map<Key<LeaguePlayer>, LeaguePlayer> keylist = null;
			
			// Save activates onsave and onload methods in LeaguePlayers
			// keylist = ObjectifyService.ofy().save().entities(leagueplayers).now();
			keylist = getLeaguePlayerService().save(leagueplayers, username);
			
		}
		
		System.out.println("Update League Player Data: " + leagueplayers.size() + " LeaguePlayers updated.");
		
		System.out.println("Update League Player Data: Adding new LeaguePlayers...");
		
		List<PlayerProjected> projections = getPlayerProjectedService().getPlayerProjections(
				league.getProjection_profile(), league.getMlb_leagues());
		
		Key<PlayerProjected> ppkey;
		Ref<PlayerProjected> ppref;
		List<LeaguePlayer> leagueplayers2;
		List<LeaguePlayer> leagueplayerstoadd = new ArrayList<LeaguePlayer>();
		
		int i = 0;
		
		// Add new projection players
		for (PlayerProjected p : projections){
			boolean inlp = false;
			
			for (LeaguePlayer lp : leagueplayers){
				if (p.getId() == lp.getPlayer_projectedRef().getKey().getId())
					inlp = true;
			}
			
			// If player projected does not match an existing league player, then add new league player
			if (!inlp){
				
				LeaguePlayer lp = new LeaguePlayer();
				lp.setLeague(league);
				lp.setPlayer_projected(p);
				leagueplayerstoadd.add(lp);
				// getLeaguePlayerService().save(lp, username);
				i++;
				
			}
			
//			ppkey = Key.create(PlayerProjected.class, p.getId());
//			ppref = Ref.create(ppkey);
			
//			leagueplayers2 = ofy().load().type(LeaguePlayer.class).filter("player_projected", ppkey).list();

			// If player projected does not match an existing league player, then add new league player
//			if (leagueplayers2.isEmpty()){
//				
//				LeaguePlayer lp = new LeaguePlayer();
//				lp.setLeague(league);
//				lp.setPlayer_projected(p);
//				leagueplayerstoadd.add(lp);
//				// getLeaguePlayerService().save(lp, username);
//				i++;
//				
//			}
			
		}
		
		getLeaguePlayerService().save(leagueplayerstoadd, username);
		
		System.out.println("Update League Player Data: " + i + " LeaguePlayers added.");
		
		System.out.println("Update League Player Data: Calculating league means and std deviations...");
		
		// Calculate league means and std deviations
		List<Double> hitter_hrs = new ArrayList<Double>();
		List<Double> hitter_rbis = new ArrayList<Double>();
		List<Double> hitter_runs = new ArrayList<Double>();
		List<Double> hitter_sbs = new ArrayList<Double>();
		List<Double> hitter_avgeff = new ArrayList<Double>();
		
		List<Double> pitcher_wins = new ArrayList<Double>();
		List<Double> pitcher_saves = new ArrayList<Double>();
		List<Double> pitcher_sos = new ArrayList<Double>();
		List<Double> pitcher_whipeff = new ArrayList<Double>();
		List<Double> pitcher_eraeff = new ArrayList<Double>();
		
		double hitter_hr_mean = 0;
		double hitter_hr_sd = 0;
		double hitter_rbi_mean = 0;
		double hitter_rbi_sd = 0;
		double hitter_r_mean = 0;
		double hitter_r_sd = 0;
		double hitter_sb_mean = 0;
		double hitter_sb_sd = 0;
		double hitter_avgeff_mean = 0;
		double hitter_avgeff_sd = 0;
		
		double pitcher_w_mean = 0;
		double pitcher_w_sd = 0;
		double pitcher_sv_mean = 0;
		double pitcher_sv_sd = 0;
		double pitcher_k_mean = 0;
		double pitcher_k_sd = 0;
		double pitcher_whipeff_mean = 0;
		double pitcher_whipeff_sd = 0;
		double pitcher_eraeff_mean = 0;
		double pitcher_eraeff_sd = 0;

		leagueplayers = getLeaguePlayerService().getLeaguePlayersByLeague(league_id, username);
		
		for (LeaguePlayer lp : leagueplayers) {

			PlayerProjected pp = lp.getPlayer_projected();

			if (lp.getPitcher_hitter().equals(PlayerProjectedService.PITCHER_HITTER_HITTER)){
				if (league.isCat_hitter_hr())
					hitter_hrs.add(pp.getHitter_hr());
				if (league.isCat_hitter_rbi())
					hitter_rbis.add(pp.getHitter_rbi());
				if (league.isCat_hitter_r())
					hitter_runs.add(pp.getHitter_runs());
				if (league.isCat_hitter_sb())
					hitter_sbs.add(pp.getHitter_sb());
	
				if (league.isCat_hitter_avg()) {
					double h_avgeff = ((league.getAvg_hitter_hits() + pp.getHitter_hits()) / (league.getAvg_hitter_ab() + pp
							.getHitter_ab())) - league.getAvg_hitter_ba();
	
					hitter_avgeff.add(h_avgeff);
					lp.setHitter_avg_eff(h_avgeff);
				}
			}
			else if (lp.getPitcher_hitter().equals(PlayerProjectedService.PITCHER_HITTER_PITCHER)){
				if (league.isCat_pitcher_wins())
					pitcher_wins.add(pp.getPitcher_w());
				if (league.isCat_pitcher_saves())
					pitcher_saves.add(pp.getPitcher_sv());
				if (league.isCat_pitcher_so())
					pitcher_sos.add(pp.getPitcher_k());
	
				if (league.isCat_pitcher_whip()) {
					double p_whipeff = ((league.getAvg_pitcher_bbplushits() + pp.getPitcher_bb() + pp.getPitcher_hits()) / (league
							.getAvg_pitcher_ip() + pp.getPitcher_ip())) - league.getAvg_pitcher_whip();
	
					pitcher_whipeff.add(p_whipeff);
					lp.setPitcher_whip_eff(p_whipeff);
				}
	
				if (league.isCat_pitcher_era()) {
					double p_eraeff = (((league.getAvg_pitcher_er() + pp.getPitcher_er()) / (league.getAvg_pitcher_ip() + pp
							.getPitcher_ip())) * 9) - league.getAvg_pitcher_era();
	
					pitcher_eraeff.add(p_eraeff);
					lp.setPitcher_era_eff(p_eraeff);
				}
			}

		}

		if (league.isCat_hitter_hr()){
			hitter_hr_mean = StatUtils.mean(toPrimitive(hitter_hrs));
			hitter_hr_sd = FastMath.sqrt(StatUtils.variance(toPrimitive(hitter_hrs)));
			// System.out.println("Hitter HR Mean: " + hitter_hr_mean);
			// System.out.println("Hitter HR Std Dev: " + hitter_hr_sd);
		}
		if (league.isCat_hitter_rbi()){
			hitter_rbi_mean = StatUtils.mean(toPrimitive(hitter_rbis));
			hitter_rbi_sd = FastMath.sqrt(StatUtils.variance(toPrimitive(hitter_rbis)));
		}
		if (league.isCat_hitter_r()){
			hitter_r_mean = StatUtils.mean(toPrimitive(hitter_runs));
			hitter_r_sd = FastMath.sqrt(StatUtils.variance(toPrimitive(hitter_runs)));
		}
		if (league.isCat_hitter_sb()){
			hitter_sb_mean = StatUtils.mean(toPrimitive(hitter_sbs));
			hitter_sb_sd = FastMath.sqrt(StatUtils.variance(toPrimitive(hitter_sbs)));
		}
		if (league.isCat_hitter_avg()) {
			hitter_avgeff_mean = StatUtils.mean(toPrimitive(hitter_avgeff));
			hitter_avgeff_sd = FastMath.sqrt(StatUtils.variance(toPrimitive(hitter_avgeff)));
			// System.out.println("Hitter Avg Eff Mean: " + hitter_avgeff_mean);
			// System.out.println("Hitter Avg Eff Std Dev: " + hitter_avgeff_sd);
		}
		
		if (league.isCat_pitcher_wins()){
			pitcher_w_mean = StatUtils.mean(toPrimitive(pitcher_wins));
			pitcher_w_sd = FastMath.sqrt(StatUtils.variance(toPrimitive(pitcher_wins)));
			// System.out.println("Pitcher Wins Mean: " + pitcher_w_mean);
			// System.out.println("Pitcher Wins Std Dev: " + pitcher_w_sd);
		}
		if (league.isCat_pitcher_saves()){
			pitcher_sv_mean = StatUtils.mean(toPrimitive(pitcher_saves));
			pitcher_sv_sd = FastMath.sqrt(StatUtils.variance(toPrimitive(pitcher_saves)));
		}
		if (league.isCat_pitcher_so()){
			pitcher_k_mean = StatUtils.mean(toPrimitive(pitcher_sos));
			pitcher_k_sd = FastMath.sqrt(StatUtils.variance(toPrimitive(pitcher_sos)));
		}
		if (league.isCat_pitcher_whip()){
			pitcher_whipeff_mean = StatUtils.mean(toPrimitive(pitcher_whipeff));
			pitcher_whipeff_sd = FastMath.sqrt(StatUtils.variance(toPrimitive(pitcher_whipeff)));
		}
		if (league.isCat_pitcher_era()) {
			pitcher_eraeff_mean = StatUtils.mean(toPrimitive(pitcher_eraeff));
			pitcher_eraeff_sd = FastMath.sqrt(StatUtils.variance(toPrimitive(pitcher_eraeff)));
			// System.out.println("Pitcher ERA Eff Mean: " + pitcher_eraeff_mean);
			// System.out.println("Pitcher ERA Eff Std Dev: " + pitcher_eraeff_sd);
		}
		
		System.out.println("Update League Player Data: Calculating LeaguePlayer Z scores...");
		// Calculate Z scores
		for (LeaguePlayer lp : leagueplayers) {
			
			double z = 0;
			double tz = 0;
			
			PlayerProjected pp = lp.getPlayer_projected();

			if (lp.getPitcher_hitter().equals(PlayerProjectedService.PITCHER_HITTER_HITTER)){

				if (league.isCat_hitter_hr()){
					z = calcZ(pp.getHitter_hr(),hitter_hr_mean,hitter_hr_sd);
					lp.setHitter_z_hr(z);
					tz = tz + z;
				}
				if (league.isCat_hitter_rbi()){
					z = calcZ(pp.getHitter_rbi(),hitter_rbi_mean,hitter_rbi_sd);
					lp.setHitter_z_rbi(z);
					tz = tz + z;
				}
				if (league.isCat_hitter_r()){
					z = calcZ(pp.getHitter_runs(),hitter_r_mean,hitter_r_sd);
					lp.setHitter_z_runs(z);
					tz = tz + z;
				}
				if (league.isCat_hitter_sb()){
					z = calcZ(pp.getHitter_sb(),hitter_sb_mean,hitter_sb_sd);
					lp.setHitter_z_sb(z);
					tz = tz + z;
				}
				if (league.isCat_hitter_avg()){ 
					z = calcZ(lp.getHitter_avg_eff(),hitter_avgeff_mean,hitter_avgeff_sd);
					lp.setHitter_z_avg(z);
					tz = tz + z;
				}
			}
			else if (lp.getPitcher_hitter().equals(PlayerProjectedService.PITCHER_HITTER_PITCHER)){
				if (league.isCat_pitcher_wins()){
					z = calcZ(pp.getPitcher_w(),pitcher_w_mean,pitcher_w_sd);
					lp.setPitcher_z_wins(z);
					tz = tz + z;
				}
				if (league.isCat_pitcher_saves()){
					z = calcZ(pp.getPitcher_sv(),pitcher_sv_mean,pitcher_sv_sd);
					lp.setPitcher_z_saves(z);
					tz = tz + z;
				}
				if (league.isCat_pitcher_so()){
					z = calcZ(pp.getPitcher_k(),pitcher_k_mean,pitcher_k_sd);
					lp.setPitcher_z_so(z);
					tz = tz + z;
				}
				if (league.isCat_pitcher_whip()){ 
					z = -calcZ(lp.getPitcher_whip_eff(),pitcher_whipeff_mean,pitcher_whipeff_sd);
					lp.setPitcher_z_whip(z);
					tz = tz + z;
				}
				if (league.isCat_pitcher_era()){
					z = -calcZ(lp.getPitcher_era_eff(),pitcher_eraeff_mean,pitcher_eraeff_sd);
					lp.setPitcher_z_era(z);
					tz = tz + z;
				}
			}
			
			lp.setTotal_z(tz);

		}
		
		System.out.println("Update League Player Data: Calculating LeaguePlayer static auction values...");
		
		// Calculate static auction value
		double num_teams = league.getNum_of_teams();
		
		double roster_c = num_teams * league.getNum_c();
		double roster_1b = num_teams * (league.getNum_1b() + league.getNum_ci()/2.0 + league.getNum_util()/5.0);
		double roster_2b = num_teams * (league.getNum_2b() + league.getNum_mi()/2.0 + league.getNum_util()/5.0);
		double roster_3b = num_teams * (league.getNum_3b() + league.getNum_ci()/2.0 + league.getNum_util()/5.0);
		double roster_ss = num_teams * (league.getNum_ss() + league.getNum_mi()/2.0 + league.getNum_util()/5.0);
		double roster_of = num_teams * (league.getNum_of() + league.getNum_util()/5.0);
		double roster_p = num_teams * league.getNum_p();
		
		double roster_c_wRes = roster_c;
		double roster_1b_wRes = roster_1b + (num_teams * league.getNum_res()/12.0);
		double roster_2b_wRes = roster_2b + (num_teams * league.getNum_res()/12.0);
		double roster_3b_wRes = roster_3b + (num_teams * league.getNum_res()/12.0);
		double roster_ss_wRes = roster_ss + (num_teams * league.getNum_res()/12.0);
		double roster_of_wRes = roster_of + (num_teams * league.getNum_res()/3.0);
		double roster_p_wRes = roster_p + (num_teams * league.getNum_res()/3.0);
		
		int iroster_c = (int) Math.round(roster_c);
		int iroster_1b = (int) Math.round(roster_1b);
		int iroster_2b = (int) Math.round(roster_2b);
		int iroster_3b = (int) Math.round(roster_3b);
		int iroster_ss = (int) Math.round(roster_ss);
		int iroster_of = (int) Math.round(roster_of);
		int iroster_p = (int) Math.round(roster_p);
		
		int iroster_c_wRes = (int) Math.round(roster_c_wRes);
		int iroster_1b_wRes = (int) Math.round(roster_1b_wRes);
		int iroster_2b_wRes = (int) Math.round(roster_2b_wRes);
		int iroster_3b_wRes = (int) Math.round(roster_3b_wRes);
		int iroster_ss_wRes = (int) Math.round(roster_ss_wRes);
		int iroster_of_wRes = (int) Math.round(roster_of_wRes);
		int iroster_p_wRes = (int) Math.round(roster_p_wRes);
		
		System.out.println("Catcher Players, No Reserve: " + iroster_c + " With Reserve: " + iroster_c_wRes);
		System.out.println("1B Players, No Reserve: " + iroster_1b + " With Reserve: " + iroster_1b_wRes);
		System.out.println("2B Players, No Reserve: " + iroster_2b + " With Reserve: " + iroster_2b_wRes);
		System.out.println("3B Players, No Reserve: " + iroster_3b + " With Reserve: " + iroster_3b_wRes);
		System.out.println("SS Players, No Reserve: " + iroster_ss + " With Reserve: " + iroster_ss_wRes);
		System.out.println("OF Players, No Reserve: " + iroster_of + " With Reserve: " + iroster_of_wRes);
		System.out.println("P Players, No Reserve: " + iroster_p + " With Reserve: " + iroster_p_wRes);
		
		// Sort players by descending Z
		Collections.sort(leagueplayers, new Comparator<LeaguePlayer>() {
		    @Override
		    public int compare(LeaguePlayer z1, LeaguePlayer z2) {
		        if (z1.getTotal_z() < z2.getTotal_z())
		            return 1;
		        if (z1.getTotal_z() > z2.getTotal_z())
		            return -1;
		        return 0;
		    }
		});

		PositionalZContainer posz_c = getPositionalZ(leagueplayers, "C", iroster_c);
		PositionalZContainer posz_1b = getPositionalZ(leagueplayers, "1B", iroster_1b);
		PositionalZContainer posz_2b = getPositionalZ(leagueplayers, "2B", iroster_2b);
		PositionalZContainer posz_3b = getPositionalZ(leagueplayers, "3B", iroster_3b);
		PositionalZContainer posz_ss = getPositionalZ(leagueplayers, "SS", iroster_ss);
		PositionalZContainer posz_of = getPositionalZ(leagueplayers, "OF", iroster_of);
		PositionalZContainer posz_p = getPositionalZ(leagueplayers, "P", iroster_p);
		double replval_dh = (posz_1b.getReplacementvalue() + posz_of.getReplacementvalue())/2;

		double posz_total = posz_c.getTotalvalue() + posz_1b.getTotalvalue() + posz_2b.getTotalvalue()
				+ posz_3b.getTotalvalue() + posz_ss.getTotalvalue() + posz_of.getTotalvalue() + posz_p.getTotalvalue();
		
		double coef = (league.getTeam_salary()*league.getNum_of_teams())/posz_total;
		
		// Update auction value
		for (LeaguePlayer lp : leagueplayers){
			
			double auct = 0;
			
			if (lp.getPlayer_position().toLowerCase().contains("c")) 
				auct = Math.max(auct,(lp.getTotal_z()-posz_c.getReplacementvalue())*coef);
			if (lp.getPlayer_position().toLowerCase().contains("1b")) 
				auct = Math.max(auct,(lp.getTotal_z()-posz_1b.getReplacementvalue())*coef);
			if (lp.getPlayer_position().toLowerCase().contains("2b")) 
				auct = Math.max(auct,(lp.getTotal_z()-posz_2b.getReplacementvalue())*coef);
			if (lp.getPlayer_position().toLowerCase().contains("3b")) 
				auct = Math.max(auct,(lp.getTotal_z()-posz_3b.getReplacementvalue())*coef);
			if (lp.getPlayer_position().toLowerCase().contains("ss")) 
				auct = Math.max(auct,(lp.getTotal_z()-posz_ss.getReplacementvalue())*coef);
			if (lp.getPlayer_position().toLowerCase().contains("of")) 
				auct = Math.max(auct,(lp.getTotal_z()-posz_of.getReplacementvalue())*coef);
			if (lp.getPlayer_position().toLowerCase().contains("p")) 
				auct = Math.max(auct,(lp.getTotal_z()-posz_p.getReplacementvalue())*coef);
			if (lp.getPlayer_position().toLowerCase().contains("dh")) 
				auct = Math.max(auct,(lp.getTotal_z()-replval_dh)*coef);
			
			if (auct < 0) auct = 0;
			
			lp.setInit_auction_value((int)Math.round(auct));

		}
		
		for (int out = 0; out < 150; out++) {
			System.out.println("--Player Test: " + leagueplayers.get(out).getFull_name() + ", "
					+ leagueplayers.get(out).getPlayer_position() + ", " + leagueplayers.get(out).getInit_auction_value());
		}

		System.out.println("Update League Player Data: Saving LeaguePlayers...");
		
		Map<Key<LeaguePlayer>, LeaguePlayer> keylist = null;
		keylist = ObjectifyService.ofy().save().entities(leagueplayers).now();
		
		System.out.println("Update League Player Data: COMPLETE");
		
	}
	
	
	/**
	 * Description:	Calculate the total Z value for the given position up the the replacement level.
	 * 				Also determines the avg replacement value. 
	 * @param leagueplayers
	 * @param position
	 * @param repl_level
	 * @return 
	 */
	private PositionalZContainer getPositionalZ(List<LeaguePlayer> leagueplayers, String position, int repl_level){
		
		int i = 0;
		double totalz = 0;
		double avgz = 0;
		
		PositionalZContainer p = new PositionalZContainer();
		
		for (LeaguePlayer lp : leagueplayers){
			
			if ((lp.getPlayer_position().toLowerCase().contains(position.toLowerCase())) 
				&& (i < repl_level)){
				
				totalz = totalz + lp.getTotal_z();
				i++;
				
				// System.out.println(position + ": " + lp.getTotal_z());
				
			} else if ((lp.getPlayer_position().toLowerCase().contains(position.toLowerCase())) 
					&& (i == repl_level)){
				
				avgz = avgz + lp.getTotal_z();
				i++;
				
				// System.out.println(position + "-AVG1: " + lp.getTotal_z());
				
			} else if ((lp.getPlayer_position().toLowerCase().contains(position.toLowerCase())) 
					&& (i == repl_level + 1)){
				
				avgz = avgz + lp.getTotal_z();
				i++;
				
				// System.out.println(position + "-AVG2: " + lp.getTotal_z());
				
			} else if (i > repl_level + 1) {break;}

		}
		
		avgz = avgz/2;
		totalz = totalz - repl_level*avgz;
		
		// System.out.println(position + "-TOTAL: " + totalz);
		
		p.setTotalvalue(totalz);
		p.setReplacementvalue(avgz);
		
		return p;
		
	}
	
	
	/**
	 * Description:	Calculates z score
	 * @param player_value
	 * @param mean
	 * @param std_dev
	 * @return calculated z value
	 */
	private double calcZ (double player_value, double mean, double std_dev){
		double z = (player_value-mean)/std_dev;
		return z;
	}
	
	
	/**
	 * Description:	Convert List<Double> to array double[]
	 * @param doublelist
	 * @return double[]
	 */
	private static double[] toPrimitive (List<Double> doublelist) {
	
		 double[] target = new double[doublelist.size()];
		 for (int i = 0; i < target.length; i++) {

		    target[i] = doublelist.get(i);         
		 }
		 
		 return target;
		
	}
	
	
	private LeagueTeamService getLeagueTeamService(){

		return new LeagueTeamService(LeagueTeam.class);

	}
	
	private LeaguePlayerService getLeaguePlayerService(){

		return new LeaguePlayerService(LeaguePlayer.class);

	}
	
	private IdentityService getIdentityService(){

		return new IdentityService();

	}
	
	private PlayerProjectedService getPlayerProjectedService(){

		return new PlayerProjectedService();

	}


}