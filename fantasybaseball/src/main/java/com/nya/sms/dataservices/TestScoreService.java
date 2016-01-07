package com.nya.sms.dataservices;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.util.ArrayList;
import java.util.List;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.cmd.Query;
import com.nya.sms.entities.Points;
import com.nya.sms.entities.ProgramScore;
import com.nya.sms.entities.Site;
import com.nya.sms.entities.Student;
import com.nya.sms.entities.StudentGroup;
import com.nya.sms.entities.TestScore;

public class TestScoreService<T extends TestScore> extends AbstractDataServiceImpl<T>{
	
	private static final long serialVersionUID = 1L;
	
	private Class<T> valueType;

	public TestScoreService(Class<T> clazz) {
		super(clazz);
		this.valueType = clazz;
		// TODO Auto-generated constructor stub
	}
	
/*
	// Return list of program scores based on authorization
	//   - i.e. program access and student scope
	public List<T> getAuthorizedTestScoreList (Authorization auth){
		
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
	
	
	// Return list of program scores based on authorization
	//   - i.e. program access and student scope
	public List<T> getAuthorizedTestScoreList (Authorization auth){
		
		return getAuthorizedAbstractList(auth, auth.getAccess_test());
		
	}

	
	 private IdentityService getIdentityService() {
		 
		 return new IdentityService();
	 
	 }
	 
	 private StudentService getStudentService() {
		 
		 return new StudentService();
	 
	 }


}
