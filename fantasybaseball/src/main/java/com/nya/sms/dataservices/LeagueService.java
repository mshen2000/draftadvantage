package com.nya.sms.dataservices;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.stat.StatUtils;
import org.apache.commons.math3.util.FastMath;

import com.app.endpoints.LeaguePlayerOutput;
import com.app.endpoints.entities.LeagueCreateContainer;
import com.app.endpoints.entities.LeagueModalFields;
import com.app.endpoints.entities.LeagueRosterItem;
import com.app.endpoints.entities.PositionZPriorityContainer;
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
	

	
	/**
	 * Description: Create a new league (new league only) based on a
	 * LeagueCreateContainer which contains league, team, and profile
	 * information. This is used to make it easier for REST and GCE.
	 * 
	 * @param container
	 * @param username
	 * @return
	 */
	public long saveNewLeague(LeagueCreateContainer container, String username){
		
		League league = container.getLeague();
		
		List<League> existingleagues = getUserLeague(league.getLeague_name(), league.getLeague_year(), username);
		
		if (existingleagues.size() > 0){
			throw new IllegalArgumentException("League already exists, cannot create a new league.");
		}
		
		// Set league average baseline values (for 11 teams)
		// NEED TO PARAMETERIZE based on number of teams.
		league.setAvg_hitter_ab(6500);
		league.setAvg_hitter_ba(0.258);
		league.setAvg_hitter_hits(league.getAvg_hitter_ab()*league.getAvg_hitter_ba());
		
		league.setAvg_pitcher_era(3.96);
		league.setAvg_pitcher_ip(1500);
		league.setAvg_pitcher_whip(1.27);
		league.setAvg_pitcher_er((league.getAvg_pitcher_era()/9)*league.getAvg_pitcher_ip());
		league.setAvg_pitcher_bbplushits(league.getAvg_pitcher_ip()*league.getAvg_pitcher_whip());
		
		//  For each team in container, set salaries, and save league team
		for (LeagueTeam team : container.getLeague_teams()){
			team.setStarting_league_salary(league.getTeam_salary());
			team.setAdj_starting_salary(league.getTeam_salary() + team.getSalary_adjustment());
		}
		
		Map<Key<LeagueTeam>, LeagueTeam>  map = getLeagueTeamService().save(container.getLeague_teams(), username);
		List<LeagueTeam> teamlist = new ArrayList<LeagueTeam>(map.values());

		System.out.println("saveNewLeague - League Name: " + league.getLeague_name());
		System.out.println("saveNewLeague - Size of Teamlist: " + teamlist.size());
		
		league.setLeague_teams(teamlist);
		
		league.setUser(getIdentityService().getUser(username));
		
		System.out.println("saveNewLeague - Profile Service: " + container.getProfile().getProjection_service());
		System.out.println("saveNewLeague - Profile Period: " + container.getProfile().getProjection_period());
		System.out.println("saveNewLeague - Profile Year: " + container.getProfile().getProjected_year());

		ProjectionProfile p = getProjectionProfileService().get(container.getProfile().getProjection_service(),
				container.getProfile().getProjection_period(), container.getProfile().getProjected_year());
		
		if (p == null) throw new IllegalArgumentException("Projection profile does not exist, cannot create a new league.");

		league.setProjection_profile(p);
		
		return this.save(league, username);
		
	}
	
	
	/* (non-Javadoc)
	 * @see com.nya.sms.dataservices.AbstractDataServiceImpl#delete(java.lang.Long)
	 * When deleting League, also delete associated teams and league players
	 */
	public void deleteLeagueFull(Long id, String username){
		
		// System.out.println("In deleteLeagueFull, id: " + id);
		
		League league = this.get(id);
		
		// Delete League Teams
		System.out.println("Delete League: Delete League Teams...");
		List<Ref<LeagueTeam>> ltr = league.getLeague_teamRefs();
		
		if (ltr.size() > 0){
			List<Key<LeagueTeam>> ltk = new ArrayList<Key<LeagueTeam>>();
			for (Ref<LeagueTeam> r : ltr){
				ltk.add(r.getKey());
			}
			getLeagueTeamService().delete(ltk, username);
		}

		
		// Delete League Players
		System.out.println("Delete League: Delete League Players...");
		getLeaguePlayerService().deleteLeaguePlayersByLeague(id, username);
		
		// Delete League
		System.out.println("Delete League: Delete League...");
		super.delete(id);
		
	}
	
	public List<League> getUserLeague(String leaguename, int year, String username){
		
		getIdentityService().getUser(username);
		
		Key<User> userkey = Key.create(User.class, getIdentityService().getUser(username).getId());
		
		List<League> leagues = ofy().load().type(League.class)
				.filter("user", userkey)
				.filter("league_name", leaguename)
				.filter("league_year", year)
				.list();
		
		return leagues;
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
	
	
	public List<LeagueTeam> getLeagueTeams(Long league_id, String uname) {
		
		League league = this.get(league_id);
		
		return league.getLeague_teams();
		
	}
	
	
	public List<LeagueRosterItem> getLeagueRoster(long league_id, String uname){
	
		League league = this.get(league_id);
		List<LeagueRosterItem> items = new ArrayList<LeagueRosterItem>();
		int j = 0;
		
		for (int i = 1; i <= league.getNum_c(); i++){
			items.add(new LeagueRosterItem(j, "C"));
			j++;
		}
		for (int i = 1; i <= league.getNum_1b(); i++){
			items.add(new LeagueRosterItem(j, "1B"));
			j++;
		}
		for (int i = 1; i <= league.getNum_2b(); i++){
			items.add(new LeagueRosterItem(j, "2B"));
			j++;
		}
		for (int i = 1; i <= league.getNum_ss(); i++){
			items.add(new LeagueRosterItem(j, "SS"));
			j++;
		}
		for (int i = 1; i <= league.getNum_3b(); i++){
			items.add(new LeagueRosterItem(j, "3B"));
			j++;
		}
		for (int i = 1; i <= league.getNum_mi(); i++){
			items.add(new LeagueRosterItem(j, "MI"));
			j++;
		}
		for (int i = 1; i <= league.getNum_ci(); i++){
			items.add(new LeagueRosterItem(j, "CI"));
			j++;
		}
		for (int i = 1; i <= league.getNum_of(); i++){
			items.add(new LeagueRosterItem(j, "OF"));
			j++;
		}
		for (int i = 1; i <= league.getNum_util(); i++){
			items.add(new LeagueRosterItem(j, "UT"));
			j++;
		}
		for (int i = 1; i <= league.getNum_p(); i++){
			items.add(new LeagueRosterItem(j, "P"));
			j++;
		}
		for (int i = 1; i <= league.getNum_res(); i++){
			items.add(new LeagueRosterItem(j, "RES"));
			j++;
		}
		
		return items;
	
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
	public List<LeaguePlayerOutput> getLeaguePlayerData(long league_id, String username) {
		
		System.out.println("Get Player Output Data: BEGIN");

		League league = this.get(league_id);
		
		ProjectionProfile profile = league.getProjection_profile();

		System.out.println("Get Player Output Data: Convert player projections to output...");
		
		List<PlayerProjected> projections = getPlayerProjectedService().getPlayerProjections(
				league.getProjection_profile(), league.getMlb_leagues());
		
		List<LeaguePlayerOutput> playeroutput = new ArrayList<LeaguePlayerOutput>();
		
		int i = 0;
		int j = 0;
		
		// Convert PlayerProjected to LeaguePlayerOutput
		for (PlayerProjected p : projections){
			LeaguePlayerOutput po = new LeaguePlayerOutput(p);
			po.setLeague_id(league_id);
			po.setProjection_date(profile.getProjection_date());
			playeroutput.add(po);
			i++;
		}

		System.out.println("Get Player Output Data: " + i + " PlayerProjected converted.");
		if (j > 0) System.out.println("Get Player Output Data: " + j + " PlayerProjected NOT CONVERTED.");
		
		System.out.println("Get Player Output Data: Calculating league means and std deviations...");
		
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
		
		for (LeaguePlayerOutput po : playeroutput) {

			if (po.getPitcher_hitter().equals(PlayerProjectedService.PITCHER_HITTER_HITTER)){
				if (league.isCat_hitter_hr())
					hitter_hrs.add(po.getHitter_hr());
				if (league.isCat_hitter_rbi())
					hitter_rbis.add(po.getHitter_rbi());
				if (league.isCat_hitter_r())
					hitter_runs.add(po.getHitter_runs());
				if (league.isCat_hitter_sb())
					hitter_sbs.add(po.getHitter_sb());
	
				if (league.isCat_hitter_avg()) {
					double h_avgeff = ((league.getAvg_hitter_hits() + po.getHitter_hits()) / (league.getAvg_hitter_ab() + po
							.getHitter_ab())) - league.getAvg_hitter_ba();
	
					hitter_avgeff.add(h_avgeff);
					po.setHitter_avg_eff(h_avgeff);
				}
			}
			else if (po.getPitcher_hitter().equals(PlayerProjectedService.PITCHER_HITTER_PITCHER)){
				if (league.isCat_pitcher_wins())
					pitcher_wins.add(po.getPitcher_w());
				if (league.isCat_pitcher_saves())
					pitcher_saves.add(po.getPitcher_sv());
				if (league.isCat_pitcher_so())
					pitcher_sos.add(po.getPitcher_k());
	
				if (league.isCat_pitcher_whip()) {
					double p_whipeff = ((league.getAvg_pitcher_bbplushits() + po.getPitcher_bb() + po.getPitcher_hits()) / (league
							.getAvg_pitcher_ip() + po.getPitcher_ip())) - league.getAvg_pitcher_whip();
	
					pitcher_whipeff.add(p_whipeff);
					po.setPitcher_whip_eff(p_whipeff);
				}
	
				if (league.isCat_pitcher_era()) {
					double p_eraeff = (((league.getAvg_pitcher_er() + po.getPitcher_er()) / (league.getAvg_pitcher_ip() + po
							.getPitcher_ip())) * 9) - league.getAvg_pitcher_era();
	
					pitcher_eraeff.add(p_eraeff);
					po.setPitcher_era_eff(p_eraeff);
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
		
		System.out.println("Get Player Output Data: Calculating LeaguePlayer Z scores...");
		// Calculate Z scores
		for (LeaguePlayerOutput po : playeroutput) {
			
			double z = 0;
			double tz = 0;

			if (po.getPitcher_hitter().equals(PlayerProjectedService.PITCHER_HITTER_HITTER)){

				if (league.isCat_hitter_hr()){
					z = calcZ(po.getHitter_hr(),hitter_hr_mean,hitter_hr_sd);
					po.setHitter_z_hr(z);
					tz = tz + z;
				}
				if (league.isCat_hitter_rbi()){
					z = calcZ(po.getHitter_rbi(),hitter_rbi_mean,hitter_rbi_sd);
					po.setHitter_z_rbi(z);
					tz = tz + z;
				}
				if (league.isCat_hitter_r()){
					z = calcZ(po.getHitter_runs(),hitter_r_mean,hitter_r_sd);
					po.setHitter_z_runs(z);
					tz = tz + z;
				}
				if (league.isCat_hitter_sb()){
					z = calcZ(po.getHitter_sb(),hitter_sb_mean,hitter_sb_sd);
					po.setHitter_z_sb(z);
					tz = tz + z;
				}
				if (league.isCat_hitter_avg()){ 
					z = calcZ(po.getHitter_avg_eff(),hitter_avgeff_mean,hitter_avgeff_sd);
					po.setHitter_z_avg(z);
					tz = tz + z;
				}
			}
			else if (po.getPitcher_hitter().equals(PlayerProjectedService.PITCHER_HITTER_PITCHER)){
				if (league.isCat_pitcher_wins()){
					z = calcZ(po.getPitcher_w(),pitcher_w_mean,pitcher_w_sd);
					po.setPitcher_z_wins(z);
					tz = tz + z;
				}
				if (league.isCat_pitcher_saves()){
					z = calcZ(po.getPitcher_sv(),pitcher_sv_mean,pitcher_sv_sd);
					po.setPitcher_z_saves(z);
					tz = tz + z;
				}
				if (league.isCat_pitcher_so()){
					z = calcZ(po.getPitcher_k(),pitcher_k_mean,pitcher_k_sd);
					po.setPitcher_z_so(z);
					tz = tz + z;
				}
				if (league.isCat_pitcher_whip()){ 
					z = -calcZ(po.getPitcher_whip_eff(),pitcher_whipeff_mean,pitcher_whipeff_sd);
					po.setPitcher_z_whip(z);
					tz = tz + z;
				}
				if (league.isCat_pitcher_era()){
					z = -calcZ(po.getPitcher_era_eff(),pitcher_eraeff_mean,pitcher_eraeff_sd);
					po.setPitcher_z_era(z);
					tz = tz + z;
				}
			}
			
			po.setTotal_z(tz);

		}
		
		System.out.println("Get Player Output Data: Calculating static auction values...");
		
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
		Collections.sort(playeroutput, new Comparator<LeaguePlayerOutput>() {
		    @Override
		    public int compare(LeaguePlayerOutput z1, LeaguePlayerOutput z2) {
		        if (z1.getTotal_z() < z2.getTotal_z())
		            return 1;
		        if (z1.getTotal_z() > z2.getTotal_z())
		            return -1;
		        return 0;
		    }
		});
		
		PositionalZContainer posz_c = getPositionalZpass1(playeroutput, "C", iroster_c);
		PositionalZContainer posz_1b = getPositionalZpass1(playeroutput, "1B", iroster_1b);
		PositionalZContainer posz_2b = getPositionalZpass1(playeroutput, "2B", iroster_2b);
		PositionalZContainer posz_3b = getPositionalZpass1(playeroutput, "3B", iroster_3b);
		PositionalZContainer posz_ss = getPositionalZpass1(playeroutput, "SS", iroster_ss);
		PositionalZContainer posz_of = getPositionalZpass1(playeroutput, "OF", iroster_of);
		PositionalZContainer posz_p = getPositionalZpass1(playeroutput, "P", iroster_p);
		
		PositionZPriorityContainer priority = new PositionZPriorityContainer(
				posz_c.getTotalvalue(),
				posz_1b.getTotalvalue(),
				posz_2b.getTotalvalue(),
				posz_ss.getTotalvalue(),
				posz_3b.getTotalvalue(),
				posz_of.getTotalvalue(),
				posz_p.getTotalvalue(), 1000
				);
		
		for (String p: priority.getPos_priority()){
			
			System.out.println(p);
			
		}
		
		// TODO: Z sum to feed the priority calculation should be the average player z based on 
		//       above replacement!!!
		

		posz_c = getPositionalZpass2(playeroutput, "C", iroster_c, priority);
		posz_1b = getPositionalZpass2(playeroutput, "1B", iroster_1b, priority);
		posz_2b = getPositionalZpass2(playeroutput, "2B", iroster_2b, priority);
		posz_3b = getPositionalZpass2(playeroutput, "3B", iroster_3b, priority);
		posz_ss = getPositionalZpass2(playeroutput, "SS", iroster_ss, priority);
		posz_of = getPositionalZpass2(playeroutput, "OF", iroster_of, priority);
		posz_p = getPositionalZpass2(playeroutput, "P", iroster_p, priority);
		
		double replval_dh = (posz_1b.getReplacementvalue() + posz_of.getReplacementvalue())/2;

		double posz_total = posz_c.getTotalvalue() + posz_1b.getTotalvalue() + posz_2b.getTotalvalue()
				+ posz_3b.getTotalvalue() + posz_ss.getTotalvalue() + posz_of.getTotalvalue() + posz_p.getTotalvalue();
		
		double coef = (league.getTeam_salary()*league.getNum_of_teams())/posz_total;
		
		System.out.println("League Salary: " + league.getTeam_salary()*league.getNum_of_teams());
		System.out.println("Coef: " + coef);
		
		// Update auction value
		for (LeaguePlayerOutput po : playeroutput){
			
			double auct = 0;
			
			if (po.getPlayer_position().toLowerCase().contains("c")) 
				auct = Math.max(auct,(po.getTotal_z()-posz_c.getReplacementvalue())*coef);
			if (po.getPlayer_position().toLowerCase().contains("1b")) 
				auct = Math.max(auct,(po.getTotal_z()-posz_1b.getReplacementvalue())*coef);
			if (po.getPlayer_position().toLowerCase().contains("2b")) 
				auct = Math.max(auct,(po.getTotal_z()-posz_2b.getReplacementvalue())*coef);
			if (po.getPlayer_position().toLowerCase().contains("3b")) 
				auct = Math.max(auct,(po.getTotal_z()-posz_3b.getReplacementvalue())*coef);
			if (po.getPlayer_position().toLowerCase().contains("ss")) 
				auct = Math.max(auct,(po.getTotal_z()-posz_ss.getReplacementvalue())*coef);
			if (po.getPlayer_position().toLowerCase().contains("of")) 
				auct = Math.max(auct,(po.getTotal_z()-posz_of.getReplacementvalue())*coef);
			if (po.getPlayer_position().toLowerCase().contains("p")) 
				auct = Math.max(auct,(po.getTotal_z()-posz_p.getReplacementvalue())*coef);
			if (po.getPlayer_position().toLowerCase().contains("dh")) 
				auct = Math.max(auct,(po.getTotal_z()-replval_dh)*coef);
			
			if (auct < 0) auct = 0;
			
			po.setInit_auction_value((int)Math.round(auct));

		}

		System.out.println("Get Player Output Data: Updating with League Player data...");
		
//		LeaguePlayerOutput repl_p_base = getReplPitcher();
//		LeaguePlayerOutput repl_h_base = getReplHitter();
		
		List<LeaguePlayer> lplist = getLeaguePlayerService().getLeaguePlayersByLeague(league_id, username);
			
		for (LeaguePlayer lp : lplist){
			
			// Set PlayerOutput if LeaguePlayer is an unknown player
			if (lp.isUnknownplayer()){
				LeaguePlayerOutput repl = new LeaguePlayerOutput();
				
				if (lp.getUnknown_player_pitcher_hitter().equals(getPlayerProjectedService().PITCHER_HITTER_PITCHER)) 
					repl = getReplPitcher();
				if (lp.getUnknown_player_pitcher_hitter().equals(getPlayerProjectedService().PITCHER_HITTER_HITTER)) 
					repl = getReplHitter();
				
				repl.setLeagueteam_id(lp.getLeague_teamRef().getKey().getId());
				repl.setLeagueteam_name(lp.getLeague_team().getTeam_name());
				repl.setTeam_roster_position(lp.getTeam_roster_position());
				repl.setTeam_player_salary(lp.getTeam_player_salary());
				repl.setUnknown_player_name(lp.getUnknown_player_name());
				repl.setFull_name(lp.getUnknown_player_name());
				repl.setLeague_player_id(lp.getId());
				repl.setUnknownplayer(true);
				
				playeroutput.add(repl);
				
			} else {
				// Find matching PlayerOutput if LeaguePlayer is a known player
				for (LeaguePlayerOutput po : playeroutput){
					
					if (!po.isUnknownplayer()){
						
						if (po.getId() == lp.getPlayer_projectedRef().getKey().getId()){
							
							// System.out.println("Get Player Output Data: Found matching LeaguePlayer in PO, ID= " + po.getId());
							
							if (lp.getLeague_teamRef() != null){
								po.setLeagueteam_id(lp.getLeague_teamRef().getKey().getId());
								po.setLeagueteam_name(lp.getLeague_team().getTeam_name());
								po.setTeam_roster_position(lp.getTeam_roster_position());
								po.setTeam_player_salary(lp.getTeam_player_salary());
//								System.out.println("Get Player Output Data: Updated PO, TeamID= " + po.getLeagueteam_id());
//								System.out.println("Get Player Output Data: Updated PO, TeamRosterPostion= " + po.getTeam_roster_position());
							}
							
							po.setTeam_player_note(lp.getTeam_player_note());
							po.setLeague_player_id(lp.getId());
							po.setUnknownplayer(false);
						}
						
					}

				}
			}
		}

		System.out.println("Get Player Output Data: " + lplist.size() + " LeaguePlayers found and updated.");
		
//		for (int out = 0; out < 150; out++) {
//			System.out.println("--Player Test: " + playeroutput.get(out).getFull_name() + ", "
//					+ playeroutput.get(out).getPlayer_position() + ", " + playeroutput.get(out).getInit_auction_value()
//					+ ", " + playeroutput.get(out).getPitcher_whip_eff() + ", " + playeroutput.get(out).getPitcher_z_whip());
//		+ ", " + playeroutput.get(out).getTeam_player_note());
//		}
		
		System.out.println("Get Player Output Data: COMPLETE");
		
		return playeroutput;
		
	}	
	
	
	private LeaguePlayerOutput getReplPitcher(){
		
		LeaguePlayerOutput p = new LeaguePlayerOutput();
		p.setPitcher_era(0);
		p.setPitcher_era_eff(0);
		p.setPitcher_w(0);
		p.setPitcher_sv(0);
		p.setPitcher_whip(0);
		p.setPitcher_whip_eff(0);
		p.setPitcher_k(0);
		p.setPitcher_hitter(getPlayerProjectedService().PITCHER_HITTER_PITCHER);
		
		return p;
		
	}
	
	private LeaguePlayerOutput getReplHitter(){
		
		LeaguePlayerOutput p = new LeaguePlayerOutput();
		p.setHitter_avg(0);
		p.setHitter_avg_eff(0);
		p.setHitter_rbi(0);
		p.setHitter_runs(0);
		p.setHitter_sb(0);
		p.setHitter_hr(0);
		p.setPitcher_hitter(getPlayerProjectedService().PITCHER_HITTER_HITTER);
		
		return p;
		
	}
	
	
	/**
	 * Description:	(Pass 1) Calculate the total Z value for the given position up to the replacement level, 
	 * 				regardless if player has multiple positions.
	 * 				Also determines the avg replacement value. 
	 * @param leagueplayers
	 * @param position
	 * @param repl_level
	 * @param priority
	 * @return 
	 */
	private PositionalZContainer getPositionalZpass1(List<LeaguePlayerOutput> leagueplayers, String position, int repl_level){
		
		int i = 0;
		double totalz = 0;
		double avgz = 0;
		
		PositionalZContainer p = new PositionalZContainer();
		
		for (LeaguePlayerOutput po : leagueplayers){
			
			if ((po.getPlayer_position().toLowerCase().contains(position.toLowerCase())) 
				&& (i < repl_level)){
				
				totalz = totalz + po.getTotal_z();
				i++;
				
				// System.out.println(position + ": " + lp.getTotal_z());
				
			} else if ((po.getPlayer_position().toLowerCase().contains(position.toLowerCase())) 
					&& (i == repl_level)){
				
				avgz = avgz + po.getTotal_z();
				i++;
				
				// System.out.println(position + "-AVG1: " + lp.getTotal_z());
				
			} else if ((po.getPlayer_position().toLowerCase().contains(position.toLowerCase())) 
					&& (i == repl_level + 1)){
				
				avgz = avgz + po.getTotal_z();
				i++;
				
				// System.out.println(position + "-AVG2: " + lp.getTotal_z());
				
			} else if (i > repl_level + 1) {break;}

		}
		
		avgz = avgz/2;
		totalz = totalz - repl_level*avgz;
		
		System.out.println(position + "-TOTAL: " + totalz);
		System.out.println(position + "-AVG REPL Z: " + avgz);
		
		p.setTotalvalue(totalz);
		p.setReplacementvalue(avgz);
		
		return p;
		
	}
	
	/**
	 * Description:	(Pass 2) Calculate the total Z value for the given position up to the replacement level.
	 * 				A player may be included only once, even if they have multiple positions.  Position is decided based
	 * 				On the PositionZPriorityContainer priority of positions.
	 * 				Also determines the avg replacement value. 
	 * @param leagueplayers
	 * @param position
	 * @param repl_level
	 * @param priority
	 * @return 
	 */
	private PositionalZContainer getPositionalZpass2(List<LeaguePlayerOutput> leagueplayers, String position, int repl_level,
			PositionZPriorityContainer priority){
		
		int i = 0;
		double totalz = 0;
		double avgz = 0;
		
		PositionalZContainer p = new PositionalZContainer();
		
		for (LeaguePlayerOutput po : leagueplayers){
			
			if (isPlayerPositionPriority(position, po.getPlayer_position(), priority)
				&& (i < repl_level)){
				
				totalz = totalz + po.getTotal_z();
				i++;
				
				// System.out.println(position + ": " + lp.getTotal_z());
				
			} else if (isPlayerPositionPriority(position, po.getPlayer_position(), priority)
					&& (i == repl_level)){
				
				avgz = avgz + po.getTotal_z();
				i++;
				
				// System.out.println(position + "-AVG1: " + lp.getTotal_z());
				
			} else if (isPlayerPositionPriority(position, po.getPlayer_position(), priority)
					&& (i == repl_level + 1)){
				
				avgz = avgz + po.getTotal_z();
				i++;
				
				// System.out.println(position + "-AVG2: " + lp.getTotal_z());
				
			} else if (i > repl_level + 1) {break;}

		}
		
		avgz = avgz/2;
		totalz = totalz - repl_level*avgz;
		
		System.out.println(position + "-TOTAL: " + totalz);
		System.out.println(position + "-AVG REPL Z: " + avgz);
		
		p.setTotalvalue(totalz);
		p.setReplacementvalue(avgz);
		
		return p;
		
	}
	
	
	/**
	 * Description:	Determine if the given position (1) matches to the player position string and
	 * 				(2) is the highest priority position in the player position string
	 * @param position
	 * @param playerposition
	 * @param priority
	 * @return boolean
	 */
	private boolean isPlayerPositionPriority(String position, String playerposition, PositionZPriorityContainer priority){

		// Does the player position string contain the position being looked for?
		if (playerposition.toLowerCase().contains(position.toLowerCase())){

			if (playerposition.contains(",")){
				System.out.println("Checking isPlayerPositionPriority");
				System.out.println("-- Player with position elig '" + playerposition + "' has position '" + position + "'");
				
				// For each position in the position priority list (starting from highest priority)
				for (String p : priority.getPos_priority()){
					System.out.println("-- Checking position priority '" + p);
					if (position.toLowerCase().contains(p.toLowerCase())){
						System.out.println("-- Position '" + position + "' is the highest priority for player eligibility '" + playerposition + "'");
						return true;
					}
					else if (playerposition.toLowerCase().contains(p.toLowerCase())){
						System.out.println("-- Position '" + position + "' is the NOT THE HIGHEST priority for player eligibility '" + playerposition + "'");
						return false;
					}
					
				}
				
				return false;
				
			} else return true;

			
			
		} 
		else {
			// System.out.println("-- Player with position elig '" + playerposition + "' DOES NOT HAVE position '" + position + "'");
			return false;
		}
		
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

	private ProjectionProfileService getProjectionProfileService(){

		return new ProjectionProfileService(ProjectionProfile.class);

	}


}
