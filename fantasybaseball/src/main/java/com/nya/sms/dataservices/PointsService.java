package com.nya.sms.dataservices;


import java.util.ArrayList;
import java.util.List;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyService;
import com.nya.sms.entities.Points;
import com.nya.sms.entities.ProgramScore;
import com.nya.sms.entities.StudentGroup;
import com.nya.sms.entities.User;

public class PointsService extends AbstractDataServiceImpl<Points>{
	
	private static final long serialVersionUID = 1L;

	public PointsService(Class<Points> clazz) {
		super(clazz);
		// TODO Auto-generated constructor stub
	}
	
	public boolean isChildToProgramScore(Long pointsid){
		
		System.out.println("In isChildToProgramScore");
		
		Points points = get(pointsid);
		
		System.out.println("In isChildToProgramScore 2");
		System.out.println("Points id: " + points.getId());
		
		if (ObjectifyService.ofy().load().type(ProgramScore.class).filter("pointsobject", points).list().size() > 0)
			return true;
		
		System.out.println("In isChildToProgramScore 3");
		
		return false;

		
	}
	
	public ProgramScore getParentProgramScore(Long pointsid){
		
		Points points = get(pointsid);
		
		return ObjectifyService.ofy().load().type(ProgramScore.class).filter("pointsobject", points).list().get(0);

	}
	
	
/*	// Return list of points entries based on authorization
	//   - i.e. points access and student scope
	public List<Points> getAuthorizedPointsList (Authorization auth){
		
		List<Points> points = new ArrayList<Points>();
		
		// If authorization can access all student data and points data, then return all points.
		// 		- This is to protect against the situation when a student is deleted
		//		  and the points studentid field doesn't refer to a student anymore
		if ((getStudentService().isStudentQueryAccessAllData(auth))
				&&(!auth.getAccess_points().equals(getIdentityService().NO_ACCESS)))
			return getAll();
		
		
		List<Long> sids = getStudentService().getAuthorizedStudentIDList(auth);
		
		if (sids.size() > 0)
			points = ObjectifyService.ofy().load().type(Points.class).filter("studentid in", sids).list();
		
		return points;
		
	}*/
	
	
	// Return list of points entries based on authorization
	//   - i.e. points access and student scope
	public List<Points> getAuthorizedPointsList (Authorization auth){
		
		return getAuthorizedAbstractList(auth, auth.getAccess_points());
		 
	}
	
	
	 private StudentService getStudentService() {
		 
		 return new StudentService();
	 
	 }
	 
	 private IdentityService getIdentityService() {
		 
		 return new IdentityService();
	 
	 }


}
