package com.nya.sms.dataservices;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.util.ArrayList;
import java.util.List;

import com.app.endpoints.entities.LeaguePlayerInputCustPosContainer;
import com.app.endpoints.entities.LeaguePlayerInputDraftContainer;
import com.app.endpoints.entities.LeaguePlayerInputInfoContainer;
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
	
	public long draftLeaguePlayer (LeaguePlayerInputDraftContainer container, String uname){
		
		System.out.println("draftLeaguePlayer: BEGIN");
		
		Key<LeagueTeam> teamkey = Key.create(LeagueTeam.class, container.getLeague_team_id());
		Key<League> leaguekey = Key.create(League.class, container.getLeague_id());
		
		List<LeaguePlayer> leagueplayers = new ArrayList<LeaguePlayer>();
		
		LeaguePlayer lp = new LeaguePlayer();
		
		// System.out.println("draftLeaguePlayer: unknown_player_name: " + container.getUnknown_player_name());
		// System.out.println("draftLeaguePlayer: unknownplayer: " + container.isUnknownplayer());
		
		if (container.isUnknownplayer()){
			
			// Check to see if LeaguePlayer already exists
			leagueplayers = ofy().load().type(LeaguePlayer.class)
					.filter("league", leaguekey)
					.filter("unknown_player_name", container.getUnknown_player_name()).list();
			
			// If exists, update existing LeaguePlayer
			// Otherwise, update new LeaguePlayer with draft information
			if (leagueplayers.size() > 0) {
				System.out.println("draftLeaguePlayer: found existing unknown LeaguePlayer: " + container.getUnknown_player_name());
				lp = leagueplayers.get(0);
			} else {
				System.out.println("draftLeaguePlayer: Did NOT find existing unknown LeaguePlayer: " + container.getUnknown_player_name());
				lp.setLeagueRef(Ref.create(leaguekey));
			}
			
			lp.setUnknownplayer(true);
			lp.setUnknown_player_name(container.getUnknown_player_name());
			lp.setUnknown_player_pitcher_hitter(container.getUnknown_player_pitcher_hitter());
			System.out.println("draftLeaguePlayer: Set unknown player name: " + lp.getUnknown_player_name());
			
			lp.setLeague_team(Ref.create(teamkey));
			lp.setTeam_player_salary(container.getTeam_player_salary());
			lp.setTeam_roster_position(container.getTeam_roster_position());
			
			System.out.println("draftLeaguePlayer: Right before save 1: " + lp.getUnknown_player_name());
			System.out.println("draftLeaguePlayer: Right before save 1 (team): " + lp.getLeague_team().getTeam_name());
			
			long tempid = this.save(lp, uname);
			
			System.out.println("draftLeaguePlayer: Right after save 1: " + this.get(tempid).getUnknown_player_name());
			System.out.println("draftLeaguePlayer: Right after save 1 (team): " + this.get(tempid).getLeague_team().getTeam_name());
			
			return tempid;
			
			// return this.save(lp, uname);
			
		} else {
			// System.out.println("draftLeaguePlayer: IN ELSE STATEMENT");
			Key<PlayerProjected> playerprojectedkey = Key.create(PlayerProjected.class, container.getPlayer_projected_id());
			
			// Check to see if LeaguePlayer already exists
			leagueplayers = ofy().load().type(LeaguePlayer.class).filter("league", leaguekey)
					.filter("player_projected", playerprojectedkey).list();
			
			// If exists, update existing LeaguePlayer
			// Otherwise, update new LeaguePlayer with draft information
			if (leagueplayers.size() > 0) {
				lp = leagueplayers.get(0);
			} else {
				lp.setLeagueRef(Ref.create(leaguekey));
				lp.setPlayer_projected(Ref.create(playerprojectedkey));
			}
		}

		lp.setLeague_team(Ref.create(teamkey));
		lp.setTeam_player_salary(container.getTeam_player_salary());
		lp.setTeam_roster_position(container.getTeam_roster_position());
		lp.setFavorite_flag(container.isPlayer_favorite_flag());
		
		System.out.println("draftLeaguePlayer: Right before save 2 - unknown player name: " + lp.getUnknown_player_name());
		System.out.println("draftLeaguePlayer: Right before save 2 - proj player name: " + lp.getPlayer_projected().getFull_name());
		System.out.println("draftLeaguePlayer: Right before save 2 - league team: " + lp.getLeague_team().getTeam_name());
		
		long tempid = this.save(lp, uname);
		
		System.out.println("draftLeaguePlayer: Right after save 2 - unknown player name: " + this.get(tempid).getUnknown_player_name());
		System.out.println("draftLeaguePlayer: Right after save 2 - proj player name: " + this.get(tempid).getPlayer_projected().getFull_name());
		System.out.println("draftLeaguePlayer: Right after save 2 - league team: " + this.get(tempid).getLeague_team().getTeam_name());
		
		return tempid;
		
		// return this.save(lp, uname);
		
	}
	
	
	/**
	 * Description: Undrafts league player, only requires league_id and
	 * player_projected_id from container.  Unknown player requires unknown_player_name.
	 * 
	 * @param container
	 * @param uname
	 */
	public void undraftLeaguePlayer (LeaguePlayerInputDraftContainer container, String uname){
		
		Key<League> leaguekey = Key.create(League.class, container.getLeague_id());
		List<LeaguePlayer> leagueplayers = new ArrayList<LeaguePlayer>();
		
		if (container.isUnknownplayer()){
			// Key<LeagueTeam> teamkey = Key.create(LeagueTeam.class, container.getLeague_team_id());
			
			// Check to see if LeaguePlayer already exists
			leagueplayers = ofy().load().type(LeaguePlayer.class).filter("league", leaguekey)
					.filter("unknown_player_name", container.getUnknown_player_name()).list();
			
			if (leagueplayers.size() > 0){
				System.out.println("undraftLeaguePlayer: Found LeaguePlayer, deleting...");
				this.delete(leagueplayers.get(0).getId());
			}
			else System.out.println("undraftLeaguePlayer: Could not find league player to delete.");
			
		} else {
			
			Key<PlayerProjected> playerprojectedkey = Key.create(PlayerProjected.class, container.getPlayer_projected_id());
			
			// Check to see if LeaguePlayer already exists
			leagueplayers = ofy().load().type(LeaguePlayer.class).filter("league", leaguekey)
					.filter("player_projected", playerprojectedkey).list();
			
			LeaguePlayer lp = leagueplayers.get(0);
			
			Ref<LeagueTeam> r = null;
			
			lp.setLeague_team(r);
			lp.setTeam_roster_position(null);
			lp.setTeam_player_salary(0);
			
			this.save(lp, uname);
		}

	}
	
	/**
	 * Description: Updates information for league player, only requires league_id,
	 * player_projected_id, and fields to update from container.
	 * Currently only updating player note, custom position and custom position flag.
	 * 
	 * @param container
	 * @param uname
	 * @return Long, id of saved LeaguePlayer
	 */
	public Long updateLeaguePlayerInfo (LeaguePlayerInputInfoContainer container, String uname){
		
		System.out.println("In updateLeaguePlayerInfo, player ID: " + container.getPlayer_projected_id());
		
		Key<League> leaguekey = Key.create(League.class, container.getLeague_id());
		Key<PlayerProjected> playerprojectedkey = Key.create(PlayerProjected.class, container.getPlayer_projected_id());
		
		// Check to see if LeaguePlayer already exists
		List<LeaguePlayer> leagueplayers = ofy().load().type(LeaguePlayer.class).filter("league", leaguekey)
				.filter("player_projected", playerprojectedkey).list();

		LeaguePlayer lp = new LeaguePlayer();
		
		// If exists, update existing LeaguePlayer info
		// Otherwise, update new LeaguePlayer with info
		if (leagueplayers.size() > 0) {
			lp = leagueplayers.get(0);
		} else {
			lp.setLeagueRef(Ref.create(leaguekey));
			lp.setPlayer_projected(Ref.create(playerprojectedkey));
		}

		lp.setTeam_player_note(container.getTeam_player_note());
		lp.setCustom_position_flag(container.isCustom_position_flag());
		lp.setCustom_position(container.getCustom_position());
		if (container.isFavorite_flag()) lp.setFavorite_flag(true);
		else if (!container.isFavorite_flag()) lp.setFavorite_flag(false);
			
		System.out.println("Saving player info for: " + lp.getPlayer_projected().getFull_name());
		System.out.println("Player note: " + lp.getTeam_player_note());
		System.out.println("Player custom pos flag: " + lp.isCustom_position_flag());
		System.out.println("Player custom position: " + lp.getCustom_position());
		System.out.println("Player isFavorite: " + lp.isFavorite_flag());
		System.out.println("Player isFavorite cont: " + container.isFavorite_flag());
		
		return this.save(lp, uname);
		
	}
	
	/**
	 * Description: Updates custom position eligibility for league player, only requires league_id,
	 * player_projected_id and custom_position_eligibility from container.
	 * 
	 * @param container
	 * @param uname
	 * @return Long, id of saved LeaguePlayer
	 */
//	public Long updateLeaguePlayerCustomPosition (LeaguePlayerInputCustPosContainer container, String uname){
//		
//		Key<League> leaguekey = Key.create(League.class, container.getLeague_id());
//		Key<PlayerProjected> playerprojectedkey = Key.create(PlayerProjected.class, container.getPlayer_projected_id());
//		
//		// Check to see if LeaguePlayer already exists
//		List<LeaguePlayer> leagueplayers = ofy().load().type(LeaguePlayer.class).filter("league", leaguekey)
//				.filter("player_projected", playerprojectedkey).list();
//
//		LeaguePlayer lp = new LeaguePlayer();
//		
//		// If exists, update existing LeaguePlayer custom position
//		// Otherwise, update new LeaguePlayer with custom position
//		if (leagueplayers.size() > 0) {
//			lp = leagueplayers.get(0);
//		} else {
//			lp.setLeagueRef(Ref.create(leaguekey));
//			lp.setPlayer_projected(Ref.create(playerprojectedkey));
//		}
//
//		lp.setCustom_position(container.getCustom_position_eligibility());
//		lp.setCustom_position_flag(true);
//		
//		System.out.println("Saving player custom position for: " + lp.getPlayer_projected().getFull_name());
//		System.out.println("Custom position: " + lp.getCustom_position());
//		
//		return this.save(lp, uname);
//		
//	}
	
	/**
	 * Description: Removes custom position eligibility for league player, only requires league_id,
	 * player_projected_id and custom_position_eligibility from container.  Returns -1 if player not found.
	 * 
	 * @param container
	 * @param uname
	 * @return Long, id of saved LeaguePlayer
	 */
//	public Long removeLeaguePlayerCustomPosition (LeaguePlayerInputCustPosContainer container, String uname){
//		
//		Key<League> leaguekey = Key.create(League.class, container.getLeague_id());
//		Key<PlayerProjected> playerprojectedkey = Key.create(PlayerProjected.class, container.getPlayer_projected_id());
//		
//		// Check to see if LeaguePlayer already exists
//		List<LeaguePlayer> leagueplayers = ofy().load().type(LeaguePlayer.class).filter("league", leaguekey)
//				.filter("player_projected", playerprojectedkey).list();
//
//		LeaguePlayer lp = new LeaguePlayer();
//		
//		// If exists, remove existing LeaguePlayer custom position
//		// Otherwise, return -1
//		if (leagueplayers.size() > 0) {
//			lp = leagueplayers.get(0);
//			lp.setCustom_position("");
//			lp.setCustom_position_flag(false);
//			
//			System.out.println("Removing player custom position for: " + lp.getPlayer_projected().getFull_name());
//			System.out.println("Cusotm position flag: " + lp.isCustom_position_flag());
//		} else {
//			System.out.println("Could NOT remove custom position for: " + lp.getPlayer_projected().getFull_name());
//			System.out.println("Could not find player.");
//			return Long.valueOf(-1);
//		}
//
//		return this.save(lp, uname);
//		
//	}
	
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

	private PlayerProjectedService getPlayerProjectedService(){

		return new PlayerProjectedService();

	}

}
