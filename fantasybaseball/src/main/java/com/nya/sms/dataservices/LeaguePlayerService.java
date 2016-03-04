package com.nya.sms.dataservices;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.util.List;

import com.app.endpoints.entities.LeaguePlayerInputContainer;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Ref;
import com.nya.sms.entities.League;
import com.nya.sms.entities.LeaguePlayer;
import com.nya.sms.entities.LeagueTeam;
import com.nya.sms.entities.PlayerProjected;

/**
 * @author Michael
 *
 */
public class LeaguePlayerService extends AbstractDataServiceImpl<LeaguePlayer>{
	
	private static final long serialVersionUID = 1L;
	
	public static final String TEAM_ROSTER_POSITION_C = "C";
	public static final String TEAM_ROSTER_POSITION_1B = "1B";
	public static final String TEAM_ROSTER_POSITION_2B = "2B";
	public static final String TEAM_ROSTER_POSITION_SS = "SS";
	public static final String TEAM_ROSTER_POSITION_3B = "3B";
	public static final String TEAM_ROSTER_POSITION_MI = "MI";
	public static final String TEAM_ROSTER_POSITION_CI = "CI";
	public static final String TEAM_ROSTER_POSITION_OF = "OF";
	public static final String TEAM_ROSTER_POSITION_P = "P";
	public static final String TEAM_ROSTER_POSITION_UT = "UT";
	public static final String TEAM_ROSTER_POSITION_RES = "RES";

	public LeaguePlayerService(Class<LeaguePlayer> clazz) {
		super(clazz);
		// TODO Auto-generated constructor stub
	}
	
	
	/**
	 * Description:	Get a list of LeaguePlayers based on a given league
	 * @param league_id
	 * @param username
	 * @return List<LeaguePlayer>
	 */
	public List<LeaguePlayer> getLeaguePlayersByLeague(long league_id, String username){
		
		Key<League> leaguekey = Key.create(League.class, league_id);
		
		List<LeaguePlayer> leagueplayers = ofy().load().type(LeaguePlayer.class).filter("league", leaguekey).list();
		
		return leagueplayers;
		
	}
	
	/**
	 * Description:	Delete a list of LeaguePlayers based on a given league
	 * @param league_id
	 * @param username
	 * @return 
	 */
	public void deleteLeaguePlayersByLeague(long league_id, String username){
		
		Key<League> leaguekey = Key.create(League.class, league_id);
		
		List<Key<LeaguePlayer>> leagueplayerskeys = ofy().load().type(LeaguePlayer.class).filter("league", leaguekey).keys().list();
		
		if (leagueplayerskeys.size() > 0)
			ofy().delete().keys(leagueplayerskeys).now();

	}
	
	/**
	 * Description:	Get a list of LeaguePlayers based on a given league team
	 * @param team_id
	 * @param username
	 * @return List<LeaguePlayer>
	 */
	public List<LeaguePlayer> getLeaguePlayersByTeam(long team_id, String username){
		
		Key<LeagueTeam> teamkey = Key.create(LeagueTeam.class, team_id);
		
		List<LeaguePlayer> leagueplayers = ofy().load().type(LeaguePlayer.class).filter("league_team", teamkey).list();
		
		return leagueplayers;
		
	}
	
//	public boolean isLeaguePlayerDrafted (Long leagueplayerid){
//		
//		LeaguePlayer lp = this.get(leagueplayerid);
//		
//		if (lp.getLeague_teamRef() == null) return false;
//		
//		return true;
//		
//	}
//	
//	public void draftLeaguePlayer (Long leagueteamid, Long leagueplayerid, String team_roster_position, String uname){
//		
//		LeaguePlayer lp = this.get(leagueplayerid);
//		
//		lp.setLeague_team(Ref.create(Key.create(LeagueTeam.class, leagueteamid)));
//		lp.setTeam_roster_position(team_roster_position);
//		
//		this.save(lp, uname);
//		
//	}
	
	public long draftLeaguePlayer (LeaguePlayerInputContainer container, String uname){
		
		Key<LeagueTeam> teamkey = Key.create(LeagueTeam.class, container.getLeague_team_id());
		Key<League> leaguekey = Key.create(League.class, container.getLeague_id());
		Key<PlayerProjected> playerprojectedkey = Key.create(PlayerProjected.class, container.getPlayer_projected_id());
		
		// Check to see if LeaguePlayer already exists
		List<LeaguePlayer> leagueplayers = ofy().load().type(LeaguePlayer.class).filter("league", leaguekey)
				.filter("player_projected", playerprojectedkey).list();

		LeaguePlayer lp = new LeaguePlayer();
		
		// If exists, update existing LeaguePlayer
		// Otherwise, update new LeaguePlayer with draft information
		if (leagueplayers.size() > 0) {
			lp = leagueplayers.get(0);
		} else {
			lp.setLeagueRef(Ref.create(leaguekey));
			lp.setPlayer_projected(Ref.create(playerprojectedkey));
		}

		lp.setLeague_team(Ref.create(teamkey));
		lp.setTeam_player_salary(container.getTeam_player_salary());
		lp.setTeam_roster_position(container.getTeam_roster_position());
		
		return this.save(lp, uname);
		
	}
	
	public void undraftLeaguePlayer (Long leagueplayerid, String uname){
		
		LeaguePlayer lp = ofy().load().type(LeaguePlayer.class).id(leagueplayerid).now();
		
		Ref<LeagueTeam> r = null;
		
		lp.setLeague_team(r);
		lp.setTeam_roster_position(null);
		lp.setTeam_player_salary(0);
		
		this.save(lp, uname);
		
	}
	
	public void undraftAllLeaguePlayersInLeague (Long leagueid, String uname){
		
		List<LeaguePlayer> leagueplayers = this.getLeaguePlayersByLeague(leagueid, uname);
		
		Ref<LeagueTeam> r = null;
		
		for (LeaguePlayer lp : leagueplayers){
			lp.setLeague_team(r);
		}
		
		this.save(leagueplayers, uname);
		
	}
	
	public void undraftAllLeaguePlayersInTeam (Long teamid, String uname){
		
		List<LeaguePlayer> leagueplayers = this.getLeaguePlayersByTeam(teamid, uname);
		
		Ref<LeagueTeam> r = null;
		
		for (LeaguePlayer lp : leagueplayers){
			lp.setLeague_team(r);
		}
		
		this.save(leagueplayers, uname);
		
	}


}
