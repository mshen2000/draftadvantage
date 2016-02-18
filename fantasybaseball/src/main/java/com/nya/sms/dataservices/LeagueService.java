package com.nya.sms.dataservices;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.stat.StatUtils;
import org.apache.commons.math3.util.FastMath;

import com.google.common.collect.Lists;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyService;
import com.nya.sms.entities.League;
import com.nya.sms.entities.LeaguePlayer;
import com.nya.sms.entities.LeagueTeam;
import com.nya.sms.entities.PlayerProjected;
import com.nya.sms.entities.ProjectionProfile;

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
		
		System.out.println("In deleteLeagueTeam: Number of league teams = " + lg.size());
		
		Iterator<LeagueTeam> iter;
		List<LeagueTeam> teams = new ArrayList<LeagueTeam>();

		for (iter = lg.listIterator(); iter.hasNext(); ) {
			LeagueTeam a = iter.next();
			System.out.println("Delete check: " + a.getId() + ", " + team_id);
		    if (a.getId().equals(team_id)) {
		    	System.out.println("In deleteLeagueTeam: Team to remove found = " + a.getTeam_name() + ", " + a.getId());
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
        System.out.println("In deleteLeagueTeam: Number of league teams after update = " + teams.size());
        this.save(league, uname);
        getLeagueTeamService().delete(delete_team_id);
	}
	
	
	/**
	 * Description:	Get a list of LeaguePlayers based on a given league
	 * @param league_id
	 * @param username
	 * @return List<LeaguePlayer>
	 */
	public List<LeaguePlayer> getLeaguePlayers(long league_id, String username){
		
		Key<League> leaguekey = Key.create(League.class, league_id);
		
		List<LeaguePlayer> leagueplayers = ofy().load().type(LeaguePlayer.class).filter("league", leaguekey).list();
		
		return leagueplayers;
		
	}
	
	
	/**
	 * Description:	Create/update league player data from projection data
	 * @param league_id
	 * @param username 
	 */
	public void updateLeaguePlayerData(long league_id, String username) {

		League league = this.get(league_id);

		List<PlayerProjected> projections = getPlayerProjectedService().getPlayerProjections(
				league.getProjection_profile(), league.getMlb_leagues());
		
		List<LeaguePlayer> leagueplayers = this.getLeaguePlayers(league_id, username);
		
		// Delete league players that don't have projections
		for (LeaguePlayer lp : leagueplayers){
			
			if (lp.getPlayer_projected() == null){
				getLeaguePlayerService().delete(lp.getId());
				System.out.println("Deleting LeaguePlayer with null projection:  " + lp.getFull_name());
			}
			
		}
		
		leagueplayers = this.getLeaguePlayers(league_id, username);
		
		// Update existing league players
		if (!leagueplayers.isEmpty()){
			
			Map<Key<LeaguePlayer>, LeaguePlayer> keylist = null;
			
			// Save activates onsave and onload methods in LeaguePlayers
			keylist = ObjectifyService.ofy().save().entities(leagueplayers).now();
			System.out.println("Updated " + leagueplayers.size() + " existing LeaguePlayers.");
			
		}
		
		Key<PlayerProjected> ppkey;
		List<LeaguePlayer> leagueplayers2;
		
		int i = 0;
		
		// Add new projection players
		for (PlayerProjected p : projections){
			
			ppkey = Key.create(PlayerProjected.class, p.getId());
			
			leagueplayers2 = ofy().load().type(LeaguePlayer.class).filter("player_projected", ppkey).list();
			
			// If player projected does not match an existing league player, then add new league player
			if (leagueplayers2.isEmpty()){
				
				LeaguePlayer lp = new LeaguePlayer();
				lp.setLeague(league);
				lp.setPlayer_projected(p);
				getLeaguePlayerService().save(lp, username);
				i++;
				
			}
			
		}
		
		System.out.println("Added " + i + " new LeaguePlayers from Projections.");
		
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
		
		double hitter_hr_mean;
		double hitter_hr_sd;
		double hitter_rbi_mean;
		double hitter_rbi_sd;
		double hitter_r_mean;
		double hitter_r_sd;
		double hitter_sb_mean;
		double hitter_sb_sd;
		double hitter_avgeff_mean;
		double hitter_avgeff_sd;
		
		double pitcher_w_mean;
		double pitcher_w_sd;
		double pitcher_sv_mean;
		double pitcher_sv_sd;
		double pitcher_k_mean;
		double pitcher_k_sd;
		double pitcher_whipeff_mean;
		double pitcher_whipeff_sd;
		double pitcher_eraeff_mean;
		double pitcher_eraeff_sd;

		leagueplayers = this.getLeaguePlayers(league_id, username);
		
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
			System.out.println("Hitter HR Mean: " + hitter_hr_mean);
			System.out.println("Hitter HR Std Dev: " + hitter_hr_sd);
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
			System.out.println("Hitter Avg Eff Mean: " + hitter_avgeff_mean);
			System.out.println("Hitter Avg Eff Std Dev: " + hitter_avgeff_sd);
		}
		
		if (league.isCat_pitcher_wins()){
			pitcher_w_mean = StatUtils.mean(toPrimitive(pitcher_wins));
			pitcher_w_sd = FastMath.sqrt(StatUtils.variance(toPrimitive(pitcher_wins)));
			System.out.println("Pitcher Wins Mean: " + pitcher_w_mean);
			System.out.println("Pitcher Wins Std Dev: " + pitcher_w_sd);
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
			System.out.println("Pitcher ERA Eff Mean: " + pitcher_eraeff_mean);
			System.out.println("Pitcher ERA Eff Std Dev: " + pitcher_eraeff_sd);
		}
		
		

	}
	
	
	/**
	 * Description:	Convert List<Double> to array double[]
	 * @param doublelist
	 * @return double[]
	 */
	private static double[] toPrimitive (List<Double> doublelist) {
	
		 double[] target = new double[doublelist.size()];
		 for (int i = 0; i < target.length; i++) {

		    target[i] = doublelist.get(i);                // java 1.5+ style (outboxing)
		 }
		 
		 return target;
		
	}
	
	
	private LeagueTeamService getLeagueTeamService(){

		return new LeagueTeamService(LeagueTeam.class);

	}
	
	private LeaguePlayerService getLeaguePlayerService(){

		return new LeaguePlayerService(LeaguePlayer.class);

	}
	
	private ProjectionProfileService getProjectionProfileService(){

		return new ProjectionProfileService(ProjectionProfile.class);

	}
	
	private PlayerProjectedService getPlayerProjectedService(){

		return new PlayerProjectedService();

	}


}
