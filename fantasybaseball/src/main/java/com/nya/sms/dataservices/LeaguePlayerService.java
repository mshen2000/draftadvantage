package com.nya.sms.dataservices;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.util.List;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.Ref;
import com.nya.sms.entities.League;
import com.nya.sms.entities.LeaguePlayer;
import com.nya.sms.entities.LeagueTeam;

/**
 * @author Michael
 *
 */
public class LeaguePlayerService extends AbstractDataServiceImpl<LeaguePlayer>{
	
	private static final long serialVersionUID = 1L;

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
	
	public boolean isLeaguePlayerDrafted (Long leagueplayerid){
		
		LeaguePlayer lp = this.get(leagueplayerid);
		
		if (lp.getLeague_teamRef() == null) return false;
		
		return true;
		
	}
	
	public void draftLeaguePlayer (Long leagueteamid, Long leagueplayerid, String uname){
		
		LeaguePlayer lp = this.get(leagueplayerid);
		
		lp.setLeague_team(Ref.create(Key.create(LeagueTeam.class, leagueteamid)));
		
		this.save(lp, uname);
		
	}
	
	public void undraftLeaguePlayer (Long leagueplayerid, String uname){
		
		LeaguePlayer lp = ofy().load().type(LeaguePlayer.class).id(leagueplayerid).now();
		
		Ref<LeagueTeam> r = null;
		
		lp.setLeague_team(r);
		
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
