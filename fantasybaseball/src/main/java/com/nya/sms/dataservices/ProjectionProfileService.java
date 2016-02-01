package com.nya.sms.dataservices;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.app.endpoints.entities.ProjectionPeriod;
import com.app.endpoints.entities.ProjectionService;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.VoidWork;
import com.googlecode.objectify.Work;
import com.googlecode.objectify.cmd.Query;
import com.nya.sms.entities.PlayerProjected;
import com.nya.sms.entities.ProjectionProfile;

/**
 * @author Michael
 *
 */
public class ProjectionProfileService extends AbstractDataServiceImpl<ProjectionProfile>{
	
	private static final long serialVersionUID = 1L;
	
	public static final String PROJECTION_SERVICE_STEAMER = "Steamer";
	
	public static final String PROJECTION_SERVICE_ZIPS = "Zips";
	
	public static final String PROJECTION_PERIOD_PRESEASON = "Pre-Season";
	
	public static final String PROJECTION_PERIOD_ROS = "ROS";
	

	public ProjectionProfileService(Class<ProjectionProfile> clazz) {
		super(clazz);
		// TODO Auto-generated constructor stub
	}
	
	public boolean isProjectionProfilePresent(String proj_service, String proj_period, Integer year) {
		Query<ProjectionProfile> q = ofy().load().type(ProjectionProfile.class);
		q = q.filter("projection_service", proj_service);
		q = q.filter("projection_period", proj_period);
		q = q.filter("projected_year", year);
		// List<ProjectionProfile> slist = q.list();

		if (q.count() > 0) return true;

		return false;
	}
	
	public ProjectionProfile get(String proj_service, String proj_period, Integer year){
		Query<ProjectionProfile> q = ofy().load().type(ProjectionProfile.class);
		q = q.filter("projection_service", proj_service);
		q = q.filter("projection_period", proj_period);
		q = q.filter("projected_year", year);
		
		return q.first().now();
		
	}
	
	
	/* (non-Javadoc)
	 * @see com.nya.sms.dataservices.AbstractDataServiceImpl#delete(java.lang.Long)
	 * If the profile has any associated player projections, then it delete those too.
	 */
	@Override
	public void delete (Long id){
		
		ProjectionProfile p = this.get(id);
		
		if (getPlayerProjectedService().countPlayerProjections(p) > 0) {

			getPlayerProjectedService().deletePlayerProjections(p);

		} 
		
		super.delete(id);

	}
	
	@Override
	public Long save(ProjectionProfile profile, String uname){
		String service = profile.getProjection_service();
		String period = profile.getProjection_period();
		Integer year = profile.getProjected_year();
		
		if (!isProjectionProfilePresent(service, period, year)) {

			profile.setCreatedby(uname);

		}else{
			profile.setId(get(service, period, year).getId());
		}
		
		profile.setModifiedby(uname);
		 
		Key<ProjectionProfile> key = ObjectifyService.ofy().save().entity(profile).now(); 
		
		int i = 0;
		
		while ((!isProjectionProfilePresent(service, period, year))&&(i < 10)){
			
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			i++;
			
		}
		
		return key.getId();
		
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
	
	private PlayerProjectedService getPlayerProjectedService() {

		return new PlayerProjectedService();

	}


}
