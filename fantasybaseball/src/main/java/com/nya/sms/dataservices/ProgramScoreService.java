package com.nya.sms.dataservices;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.util.ArrayList;
import java.util.List;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.cmd.Query;
import com.nya.sms.entities.Note;
import com.nya.sms.entities.Points;
import com.nya.sms.entities.ProgramScore;
import com.nya.sms.entities.Site;
import com.nya.sms.entities.Student;
import com.nya.sms.entities.StudentGroup;

public class ProgramScoreService<T extends ProgramScore> extends AbstractDataServiceImpl<T>{
	
	private static final long serialVersionUID = 1L;
	
	private Class<T> valueType;

	public ProgramScoreService(Class<T> clazz) {
		super(clazz);
		this.valueType = clazz;
		// TODO Auto-generated constructor stub
	}

	
	
	
/*	// Return list of program scores based on authorization
	//   - i.e. program access and student scope
	public List<T> getAuthorizedProgramScoreList (Authorization auth){
		
		List<T> pscores = new ArrayList<T>();
		
		// If authorization can access all student data and points data, then return all points.
		// 		- This is to protect against the situation when a student is deleted
		//		  and the points studentid field doesn't refer to a student anymore
		if ((getStudentService().isStudentQueryAccessAllData(auth))
				&&(!auth.getAccess_program().equals(getIdentityService().NO_ACCESS))){
			
			pscores = ObjectifyService.ofy().load().type(valueType).list();
			
			return pscores;
		}
		
		List<Long> sids = getStudentService().getAuthorizedStudentIDList(auth);
		
		if (sids.size() > 0)
			pscores = ObjectifyService.ofy().load().type(valueType).filter("studentid in", sids).list();
		
		return pscores;
		
	}*/
	
	
	// Return list of notes entries based on authorization
	//   - i.e. notes access and student scope
	public List<T> getAuthorizedProgramScoreList (Authorization auth){
		
		return getAuthorizedAbstractList(auth, auth.getAccess_program());
		 
	}
	
	
	
	public boolean isPointsObjectLinked(ProgramScore ps){
		
		if (ps.getPointsobject() != null)
			return true;
		else
			return false;
		
	}
	
	// create and link points object to program score
	public T createPointsObject(T item, String uname){
		
		Points points = new Points(item.getStudentid());
		
		points.setPointdate(item.getScoredate());
		points.setPoints(item.getPoints());
		points.setStudentid(item.getStudentid());
		points.setType("Program Score");
		
		Long pointsid = getPointsService().save(points, uname);

		item.setPointsobject(Ref.create(getPointsService().get(pointsid)));
		
		// save(item, uname);
		
		return item;

		
	}
	
	
	public Points getPointsObject(T item){
		
		return item.getPointsobject().get();
		
	}
	
	public T removePointsObject(T item){
		
		item.setPointsobject(null);
		
		return item;
		
	}
	

	@Override  // add section to delete the linked points object if exists
	public void delete(Long id) {
		
		T item = get(id);
		
		if (isPointsObjectLinked(item)){
			
			if (getPointsObject(item) != null)
				getPointsService().delete(getPointsObject(item).getId());
			
		}
		
		ObjectifyService.ofy().delete().entity(item).now(); 
		
	}
	
	
	@Override  // add section to create points objects when needed
	public Long save(T item, String uname) {
		
		 // If there is not already a linked points and the current points is > 0, 
		 // then create a new linked points object
		 if ((!isPointsObjectLinked(item)) 
				 && (item.getPoints() > 0)){

			 item = createPointsObject(item, uname);
			 
		 }
		 // Else if there is already a linked score and points is > 0, then delete it and create a new one
		 else if ((isPointsObjectLinked(item)) 
				 && (item.getPoints() > 0)){
			 
			 // Get the points object
			 Points points = getPointsObject(item);
			 
			 // Remove the points object from the program score
			 item = removePointsObject(item);
			 
			 // Delete the points object
			 getPointsService().delete(points.getId());
			 
			 // Create the new points object
			 item = createPointsObject(item, uname);
			 
		 }
		 
		 // Else if there is already a linked score and points is 0, then delete the existing points object
		 else if ((isPointsObjectLinked(item)) 
				 && (item.getPoints() < 1)){
			 
			 // Get the points object
			 Points points = getPointsObject(item);
			 
			 // Remove the points object from the program score
			 item = removePointsObject(item);
			 
			 // Delete the points object
			 getPointsService().delete(points.getId());
			 
		 }
			
		if (item.getId() == null) item.setCreatedby(uname);
		
		item.setModifiedby(uname);
		
		Key<T> key = ObjectifyService.ofy().save().entity(item).now(); 
		
		return key.getId();
		
	}

	
	
	
	
	 private IdentityService getIdentityService() {
		 
		 return new IdentityService();
	 
	 }
	 
	 private StudentService getStudentService() {
		 
		 return new StudentService();
	 
	 }
	 
	 private PointsService getPointsService() {
		 
		 return new PointsService(Points.class);
	 
	 }
	 
	 private SiteService getSiteService() {
		 
		 return new SiteService(Site.class);
	 
	 }

}
