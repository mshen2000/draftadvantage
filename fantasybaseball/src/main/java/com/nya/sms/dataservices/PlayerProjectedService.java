package com.nya.sms.dataservices;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.cmd.Query;
import com.nya.sms.entities.PlayerProjected;
import com.nya.sms.entities.ProjectionProfile;

/**
 * @author Michael
 *
 */
public class PlayerProjectedService implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final String PITCHER_HITTER_PITCHER = "P";

	public static final String PITCHER_HITTER_HITTER = "H";

	/**
	 * Description: Returns all PlayerProjected
	 * 
	 * @return Returns all PlayerProjected
	 */
	public List<PlayerProjected> getAllPlayerProjected() {

		return ObjectifyService.ofy().load().type(PlayerProjected.class).list();

	}

	/**
	 * Description: Retrieves player projections for a projection profile
	 * 
	 * @param profile
	 * @return Returns a list of PlayerProjected for the given parameters
	 */
	public List<PlayerProjected> getPlayerProjections(ProjectionProfile profile, String mlb_leagues) {

		// Query<PlayerProjected> q = ofy().load().type(PlayerProjected.class);
		// q = q.filter("projection_service", proj_service);
		// q = q.filter("projection_period", proj_period);
		// q = q.filter("projected_year", year);
		// List<PlayerProjected> slist = q.list();

		if (profile.getId() == null) {
			profile = getProjectionProfileService().get(profile.getProjection_service(),
					profile.getProjection_period(), profile.getProjected_year());
		}
		
		double startTime = System.currentTimeMillis();
		
		NumberFormat formatter = new DecimalFormat("#0.00");     
		
		Key<ProjectionProfile> profileKey = Key.create(ProjectionProfile.class, profile.getId());
		List<PlayerProjected> players = new ArrayList<PlayerProjected>();
		
		if (mlb_leagues.equals(LeagueService.MLB_LEAGUES_BOTH)) {
			players = ofy().load().type(PlayerProjected.class).filter("projection_profile", profileKey).list();
		} else {
			List<String> league_list = new ArrayList<String>();
			league_list.add("FA");
			league_list.add(mlb_leagues);

			players = ofy().load().type(PlayerProjected.class).filter("projection_profile", profileKey)
					.filter("al_nl in", league_list).list();
		}

		double estimatedTime1 = System.currentTimeMillis() - startTime;
		
		// System.out.println("Do something with list: " + players.get(500).getFull_name());
		System.out.println("InService: Time to Query in Objectify: " + formatter.format(estimatedTime1/1000) + " seconds");

		return players;
	}

	/**
	 * Description: Retrieves a single player projection for a projection
	 * profile and id
	 * 
	 * @param profile
	 * @param other_id_name
	 * @param other_id
	 * @return Returns a list of PlayerProjected for the given parameters
	 */
	public PlayerProjected getPlayerProjection(ProjectionProfile profile, String other_id_name, String other_id) {

		// Query<PlayerProjected> q = ofy().load().type(PlayerProjected.class);
		// q = q.filter("projection_service", proj_service);
		// q = q.filter("projection_period", proj_period);
		// q = q.filter("projected_year", year);
		// q = q.filter("other_id_name", other_id_name);
		// q = q.filter("other_id", other_id);
		// List<PlayerProjected> slist = q.list();

		if (profile.getId() == null) {
			profile = getProjectionProfileService().get(profile.getProjection_service(),
					profile.getProjection_period(), profile.getProjected_year());
		}

		Query<PlayerProjected> q = ofy().load().type(PlayerProjected.class).filter("projection_profile", profile);
		q = q.filter("other_id_name", other_id_name);
		q = q.filter("other_id", other_id);
		List<PlayerProjected> slist = q.list();

		return slist.get(0);
	}

	/**
	 * Description: Determines if a single player projection exists for a given
	 * projection profile and id
	 * 
	 * @param profile
	 * @param other_id_name
	 * @param other_id
	 * @return Returns a list of PlayerProjected for the given parameters
	 */
	public boolean isPlayerProjectionPresent(ProjectionProfile profile, String other_id_name, String other_id) {

		// Query<PlayerProjected> q = ofy().load().type(PlayerProjected.class);
		// q = q.filter("projection_service", proj_service);
		// q = q.filter("projection_period", proj_period);
		// q = q.filter("projected_year", year);
		// q = q.filter("other_id_name", other_id_name);
		// q = q.filter("other_id", other_id);
		// List<PlayerProjected> slist = q.list();

		if (profile.getId() == null) {
			profile = getProjectionProfileService().get(profile.getProjection_service(),
					profile.getProjection_period(), profile.getProjected_year());
		}

		Query<PlayerProjected> q = ofy().load().type(PlayerProjected.class).filter("projection_profile", profile);
		q = q.filter("other_id_name", other_id_name);
		q = q.filter("other_id", other_id);
		List<PlayerProjected> slist = q.list();

		if (slist.size() > 0)
			return true;
		else
			return false;
	}

	/**
	 * Description: Deletes all player projections for a given projection
	 * profile
	 * 
	 * @param profile
	 */
	public void deletePlayerProjections(ProjectionProfile profile) {

		// Query<PlayerProjected> q = ofy().load().type(PlayerProjected.class);
		// q = q.filter("projection_service", proj_service);
		// q = q.filter("projection_period", proj_period);
		// q = q.filter("projected_year", year);
		// List<PlayerProjected> slist = q.list();

		if (profile.getId() == null) {
			profile = getProjectionProfileService().get(profile.getProjection_service(),
					profile.getProjection_period(), profile.getProjected_year());
		}

		Query<PlayerProjected> q = ofy().load().type(PlayerProjected.class).filter("projection_profile", profile);
		List<PlayerProjected> slist = q.list();

		if (slist.size() > 0)
			ObjectifyService.ofy().delete().entities(slist).now();

		int i = 0;

		while ((countPlayerProjections(profile) != 0) && (i < 10)) {
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			i++;
		}
	}

	/**
	 * Description: Deletes all player projections
	 */
	public void deleteAllPlayerProjections() {

		Query<PlayerProjected> q = ofy().load().type(PlayerProjected.class);
		List<PlayerProjected> slist = q.list();

		if (slist.size() > 0)
			ObjectifyService.ofy().delete().entities(slist).now();

		int i = 0;

		while ((countAllPlayerProjections() != 0) && (i < 10)) {
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			i++;
		}
	}

	public int countAllPlayerProjections() {
		Query<PlayerProjected> list = ofy().load().type(PlayerProjected.class);
		return list.list().size();
	}

	public int countPlayerProjections(ProjectionProfile profile) {
		// Query<PlayerProjected> q = ofy().load().type(PlayerProjected.class);
		// q = q.filter("projection_service", proj_service);
		// q = q.filter("projection_period", proj_period);
		// q = q.filter("projected_year", year);

		if (profile.getId() == null) {
			profile = getProjectionProfileService().get(profile.getProjection_service(),
					profile.getProjection_period(), profile.getProjected_year());
		}

		// Query<PlayerProjected> q = ofy().load().type(PlayerProjected.class).filter("projection_profile", profile);
		
		Iterable<Key<PlayerProjected>> q = ofy().load().type(PlayerProjected.class).filter("projection_profile", profile).keys();
		
		int size = 0;
		for(Key<PlayerProjected> value : q) {
		   size++;
		}

		// return q.list().size();
		return size;
	}
	
	public int countPitcherProjections(ProjectionProfile profile) {

		if (profile.getId() == null) {
			profile = getProjectionProfileService().get(profile.getProjection_service(),
					profile.getProjection_period(), profile.getProjected_year());
		}

		Iterable<Key<PlayerProjected>> q = ofy().load().type(PlayerProjected.class)
				.filter("projection_profile", profile).filter("pitcher_hitter", "P").keys();

		int size = 0;
		for (Key<PlayerProjected> value : q) {
			size++;
		}

		return size;
	}
	
	public int countHitterProjections(ProjectionProfile profile) {

		if (profile.getId() == null) {
			profile = getProjectionProfileService().get(profile.getProjection_service(),
					profile.getProjection_period(), profile.getProjected_year());
		}

		Iterable<Key<PlayerProjected>> q = ofy().load().type(PlayerProjected.class)
				.filter("projection_profile", profile).filter("pitcher_hitter", "H").keys();

		int size = 0;
		for (Key<PlayerProjected> value : q) {
			size++;
		}

		return size;
	}

	/**
	 * Description: Takes a list of player projections for a given profile, and
	 * updates the profile set with this new set. If the projection exists, it
	 * will overwrite it, if it does not exist, it will create a new one. If the
	 * projection exists in the source but not in the update set, it will delete
	 * the projection from the source. NOTE: Uniqueness of projection sets
	 * defined by: Service, Period, and Year
	 * 
	 * @param playerlist
	 * @param profile
	 * @param uname
	 * @return Integer representing the number of saved elements
	 */
	public Integer updatePlayerProjections(List<PlayerProjected> playerlist, ProjectionProfile profile, String uname) {

		// final String iproj_service = proj_service;
		// final String iproj_period = proj_period;
		// final Date iproj_date = proj_date;
		// final Integer iyear = year;

		final String iuname = uname;

		// Update playerlist with proj_service, proj_period, proj_date, year,
		// and created/modified by uname
		for (PlayerProjected element : playerlist) {
			// element.setProjection_service(iproj_service);
			// element.setProjection_period(iproj_period);
			// element.setProjection_date(iproj_date);
			// element.setProjected_year(iyear);
			element.setProjection_profile(profile);
			element.setCreatedby(iuname);
			element.setModifiedby(iuname);
		}

		final List<PlayerProjected> updateplayerlist = playerlist;

		final List<PlayerProjected> sourceplayerlist = getPlayerProjections(profile, LeagueService.MLB_LEAGUES_BOTH);

		List<PlayerProjected> playerlistdelete = new ArrayList<PlayerProjected>();

		// Get list of players on the source list but not on update list
		for (PlayerProjected p : sourceplayerlist) {

			// System.out.println("Source list other_id_name, other_id: " +
			// p.getOther_id_name() + ", " + p.getOther_id());

			boolean inupdatelist = false;

			for (PlayerProjected p2 : updateplayerlist) {

				if (p2.getOther_id_name().equals(p.getOther_id_name()) && p2.getOther_id().equals(p.getOther_id())) {
					inupdatelist = true;
				}

			}

			if (!inupdatelist)
				playerlistdelete.add(p);

		}

		// For every player on update list, update with source id if they exist
		// in source
		int j = 0;
		for (PlayerProjected p : updateplayerlist) {

			// System.out.println("Update list other_id_name, other_id: " +
			// p.getOther_id_name() + ", " + p.getOther_id());

			for (PlayerProjected p2 : sourceplayerlist) {
				if (p2.getOther_id_name().equals(p.getOther_id_name()) && p2.getOther_id().equals(p.getOther_id())) {
					p.setId(p2.getId());
					j++;
				}
			}

		}

		System.out.println("Number of players to DELETE: " + playerlistdelete.size());
		System.out.println("Number of players in Update list: " + updateplayerlist.size());
		System.out.println("Number of update players matching source: " + j);

		// Previously used transaction to group delete and add
		// Integer size = ObjectifyService.ofy().transact(new Work<Integer>() {
		// public Integer run() {
		// Map<Key<PlayerProjected>, PlayerProjected> keylist = null;
		// ObjectifyService.ofy().delete().entities(playerlistdelete).now();
		//
		// if (iplayerlist.size() > 0){
		// // System.out.println("Updating player projections: " +
		// iproj_service);
		// keylist = ObjectifyService.ofy().save().entities(iplayerlist).now();
		// } else {
		// // System.out.println("Delete cancelled - player list was empty);
		// }
		// return keylist.size();
		// }
		// });

		// Without transaction, delete happens independently
		Map<Key<PlayerProjected>, PlayerProjected> keylist = null;
		ObjectifyService.ofy().delete().entities(playerlistdelete).now();

		if (updateplayerlist.size() > 0) {
			// System.out.println("Updating player projections: " +
			// iproj_service);
			keylist = ObjectifyService.ofy().save().entities(updateplayerlist).now();
		} else {
			// System.out.println("Delete cancelled - player list was empty);
		}

		int i = 0;

		// Wait while objectify is still processing the update
		while ((countPlayerProjections(profile) < updateplayerlist.size()) && (i < 10)) {
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			i++;
		}

		System.out.println("Number of players saved: " + keylist.size());

		// Set pitcher and hitter count
		profile.setHitters(countHitterProjections(profile));
		profile.setPitchers(countPitcherProjections(profile));
		getProjectionProfileService().save(profile, uname);
		
		System.out.println("--Hitters: " + profile.getHitters());
		System.out.println("--Pitchers: " + profile.getPitchers());
		
		return keylist.size();

	}

	/**
	 * Description: Returns all player projection attributes
	 */
	public String getPlayerProjectionAttributes() throws Exception {

		PlayerProjected pp = new PlayerProjected();
		String result = "";

		Class<?> objClass = pp.getClass();

		Field[] fields = objClass.getFields();
		for (Field field : fields) {
			String name = field.getName();
			result = result + name + ",";

		}

		return result.substring(0, result.length() - 1);
	}

	private ProjectionProfileService getProjectionProfileService() {

		return new ProjectionProfileService(ProjectionProfile.class);

	}
}
