package com.nya.sms.dataservices;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.app.endpoints.entities.ProjectionPeriod;
import com.app.endpoints.entities.ProjectionService;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.Result;
import com.googlecode.objectify.VoidWork;
import com.googlecode.objectify.Work;
import com.googlecode.objectify.cmd.Query;
import com.nya.sms.entities.PlayerProjected;
import com.nya.sms.entities.Student;

/**
 * @author Michael
 *
 */
public class PlayerProjectedService implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public static final String PROJECTION_SERVICE_STEAMER = "Steamer";
	
	public static final String PROJECTION_SERVICE_ZIPS = "Zips";
	
	public static final String PROJECTION_PERIOD_PRESEASON = "Pre-Season";
	
	public static final String PROJECTION_PERIOD_ROS = "ROS";
	
	public static final String PITCHER_HITTER_PITCHER = "P";
	
	public static final String PITCHER_HITTER_HITTER = "H";
	
	
	
	/**
	 * Description:	Returns all PlayerProjected
	 * @return Returns all PlayerProjected
	 */
	public List<PlayerProjected> getAllPlayerProjected(){
		
		return ObjectifyService.ofy().load().type(PlayerProjected.class).list();

	}
	

	/**
	 * Description:	Retrieves player projections for a given service, period (pre-season or ROS) and year
	 * @param proj_service
	 * @param proj_period
	 * @param year 
	 * @return Returns a list of PlayerProjected for the given parameters
	 */
	public List<PlayerProjected> getPlayerProjections(String proj_service, String proj_period, Integer year){
		
		Query<PlayerProjected> q = ofy().load().type(PlayerProjected.class);
		q = q.filter("projection_service", proj_service);
		q = q.filter("projection_period", proj_period);
		q = q.filter("projected_year", year);
		List<PlayerProjected> slist = q.list();
		
		return slist;
	}
	
	
	/**
	 * Description:	Retrieves a single player projection for a given mlb_id, service, period (pre-season or ROS) and year
	 * @param mlb_id
	 * @param proj_service
	 * @param proj_period
	 * @param year 
	 * @return Returns a list of PlayerProjected for the given parameters
	 */
	public PlayerProjected getPlayerProjection(String proj_service, String proj_period, Integer year, String other_id_name, String other_id){
		
		Query<PlayerProjected> q = ofy().load().type(PlayerProjected.class);
		q = q.filter("projection_service", proj_service);
		q = q.filter("projection_period", proj_period);
		q = q.filter("projected_year", year);
		q = q.filter("other_id_name", other_id_name);
		q = q.filter("other_id", other_id);
		List<PlayerProjected> slist = q.list();
		
		return slist.get(0);
	}
	
	/**
	 * Description:	Determines if a single player projection exists for a given mlb_id, service, period (pre-season or ROS) and year
	 * @param mlb_id
	 * @param proj_service
	 * @param proj_period
	 * @param year 
	 * @return Returns a list of PlayerProjected for the given parameters
	 */
	public boolean isPlayerProjectionPresent(String proj_service, String proj_period, Integer year, String other_id_name, String other_id){
		
		Query<PlayerProjected> q = ofy().load().type(PlayerProjected.class);
		q = q.filter("projection_service", proj_service);
		q = q.filter("projection_period", proj_period);
		q = q.filter("projected_year", year);
		q = q.filter("other_id_name", other_id_name);
		q = q.filter("other_id", other_id);
		List<PlayerProjected> slist = q.list();
		
		if (slist.size() > 0) return true;
		else return false;
	}
	
	
	/**
	 * Description:	Deletes player projections for a given service, period (pre-season or ROS) and year
	 * @param proj_service
	 * @param proj_period
	 * @param year 
	 */
	public void deletePlayerProjections(String proj_service, String proj_period, Integer year){
		
		Query<PlayerProjected> q = ofy().load().type(PlayerProjected.class);
		q = q.filter("projection_service", proj_service);
		q = q.filter("projection_period", proj_period);
		q = q.filter("projected_year", year);
		List<PlayerProjected> slist = q.list();
		
		if (slist.size() > 0)
			ObjectifyService.ofy().delete().entities(slist).now();
	}
	
	/**
	 * Description:	Deletes all player projections 
	 */
	public void deleteAllPlayerProjections(){
		
		Query<PlayerProjected> q = ofy().load().type(PlayerProjected.class);
		List<PlayerProjected> slist = q.list();
		
		if (slist.size() > 0)
			ObjectifyService.ofy().delete().entities(slist).now();
	}
	

	/**
	 * Description:	Takes a list of player projections for a given service, period (pre-season or ROS), and year, 
	 * 				deletes the previous set of projections and updates the data with the new set of projections.
	 * @param playerlist
	 * @param proj_service
	 * @param proj_period
	 * @param proj_date
	 * @param year
	 * @param uname
	 * @return Integer representing the number of saved elements
	 */
	public Integer updatePlayerProjections(List<PlayerProjected> playerlist, String proj_service, String proj_period, Date proj_date, Integer year, String uname){
		
		final String iproj_service = proj_service;
		final String iproj_period = proj_period;
		final Date iproj_date = proj_date;
		final Integer iyear = year;
		
		final String iuname = uname;
		
		// Update playerlist with proj_service, proj_period, proj_date, year, and created/modified by uname
		for (PlayerProjected element : playerlist) {
			element.setProjection_service(iproj_service);
			element.setProjection_period(iproj_period);
			element.setProjection_date(iproj_date);
			element.setProjected_year(iyear);
			element.setCreatedby(iuname);
			element.setModifiedby(iuname);
		}

		final List<PlayerProjected> iplayerlist = playerlist;
		
		final List<PlayerProjected> playerlistdelete = getPlayerProjections(iproj_service, iproj_period, iyear);
		
		Integer size = ObjectifyService.ofy().transact(new Work<Integer>() {
	        public Integer run() {
	        	
	        	Map<Key<PlayerProjected>, PlayerProjected> keylist = null;
	        	
	        	// deletePlayerProjections(iproj_service, iproj_period, iyear);
	        	ObjectifyService.ofy().delete().entities(playerlistdelete).now();
	            
	        	if (iplayerlist.size() > 0){
		        	// System.out.println("Updating player projections: " + iproj_service);
	        		keylist = ObjectifyService.ofy().save().entities(iplayerlist).now();
	        	} else {
	        		// System.out.println("Delete cancelled - player list was empty);
	        	}
	        	return keylist.size();
	        }
	    });
		
//		List<PlayerProjected> playerlisttest = this.getAllPlayerProjected();
//		System.out.println("*********** Update Cycle *********************");
//		for (PlayerProjected element : playerlisttest) {
//			System.out.println("MLB_ID: " + element.getMlb_id());
//			System.out.println("-- ID: " + element.getId());
//			System.out.println("-- Service: " + element.getProjection_service());
//			System.out.println("-- Period: " + element.getProjection_period());
//			System.out.println("-- Year: " + element.getProjected_year());
//		}
		
		return size;
	}
	
	/**
	 * Description:	Returns all player projection attributes
	 */
	public String getPlayerProjectionAttributes() throws Exception{
		
		PlayerProjected pp = new PlayerProjected();
		String result = "";
		
	    Class<?> objClass = pp.getClass();

	    Field[] fields = objClass.getFields();
	    for(Field field : fields) {
	        String name = field.getName();
	        result = result + name + ",";
	        
	    }

	    return result.substring(0, result.length()-1);
	}
	
	
	/**
	 * Description:	Returns a list of all projection services
	 * @return List of strings of all projections services.
	 */
	public List<ProjectionService> getProjectionServices() {
		
		List<ProjectionService> services = new ArrayList<ProjectionService>();
		
		services.add(new ProjectionService(PROJECTION_SERVICE_STEAMER));
		services.add(new ProjectionService(PROJECTION_SERVICE_ZIPS));
		
		return services;
	}
	
	/**
	 * Description:	Returns a list of all projection periods
	 * @return List of strings of all projections periods.
	 */
	public List<ProjectionPeriod> getProjectionPeriods() {
		
		List<ProjectionPeriod> periods = new ArrayList<ProjectionPeriod>();
		
		periods.add(new ProjectionPeriod(PROJECTION_PERIOD_PRESEASON));
		periods.add(new ProjectionPeriod(PROJECTION_PERIOD_ROS));
		
		return periods;
	}
	
	
	private IdentityService getIdentityService() {
		 
		return new IdentityService();
	 
	}
	
}
