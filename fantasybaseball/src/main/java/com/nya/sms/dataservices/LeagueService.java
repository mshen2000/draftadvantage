package com.nya.sms.dataservices;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
