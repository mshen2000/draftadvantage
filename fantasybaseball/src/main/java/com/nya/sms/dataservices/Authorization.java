package com.nya.sms.dataservices;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.googlecode.objectify.Ref;
import com.nya.sms.entities.Role;
import com.nya.sms.entities.User;

public class Authorization extends Role implements Serializable {

	private static final long serialVersionUID = 1;
	
	private User authuser;
	
	private List<String> levels;
	
	private List<String> scopes;
	
	public static final String AUTH_WARNING = "You do not have the proper Authorization to perform this action";
	
	public Authorization(User user) {
		super(user.getUsername(), "session");
		
		levels = getIdentityService().getAccessLevels();
		
		scopes = getIdentityService().getStudentDataScopes();
		
		this.authuser = user;
		
		initiate();
		
		List<Role> userroles = getIdentityService().getUserRoles(user.getUsername());
		
		define(userroles);
		
	}
	
	public Authorization(User user, Role role) {
		super(user.getUsername(), "session");
		
		levels = getIdentityService().getAccessLevels();
		
		scopes = getIdentityService().getStudentDataScopes();
		
		this.authuser = user;
		
		initiate();
		
		List<Role> userroles = new ArrayList<Role>();
		
		userroles.add(role);
		
		define(userroles);
		
	}
	
	public User getAuthuser() {
		return authuser;
	}

	public void setAuthuser(User user) {
		this.authuser = user;
	}

	private String levelcompare(String currentlevel, String newlevel){
		
		String delim = "-";
		int newnum = 0;
		
		String[] currentlevelsplit = currentlevel.split(delim);
		int currentnum = Integer.parseInt(currentlevelsplit[0]);
		
		if (newlevel == null){
			newnum = 1;
		}
		else {
			String[] newlevelsplit = newlevel.split(delim);
			newnum = Integer.parseInt(newlevelsplit[0]);
		}
		
		if (newnum > currentnum) return newlevel;
		else return currentlevel;
		
	}
	
	private void initiate() {
		
		// List<String> levels = getIdentityService().getAccessLevels();
		
	    this.setAccess_userrole(levels.get(0));
	    this.setAccess_sg(levels.get(0));
	    this.setAccess_students(levels.get(0));
	    this.setAccess_program(levels.get(0));
	    this.setAccess_test(levels.get(0));
	    this.setAccess_points(levels.get(0));
	    this.setAccess_grades(levels.get(0));
	    this.setAccess_schedule(levels.get(0));
	    this.setAccess_notes(levels.get(0));
	    this.setStudentdata_access_scope(scopes.get(0));
	    this.setAccess_med(false);
	    this.setAccess_app_admin(false);
	    this.setAccess_app_student(false);
	    this.setAccess_app_program(false);
	    this.setAccess_app_test(false);
	    this.setAccess_app_points(false);
	    this.setAccess_app_grades(false);
	    this.setAccess_app_schedule(false);
	    this.setAccess_app_notes(false);
		
	}
	
	
	private void define(List<Role> userroles){
		
		for (Role r : userroles){
			
			this.setSite(r.getSite());
			
			this.setAccess_userrole(levelcompare(this.getAccess_userrole(),r.getAccess_userrole()));
		    this.setAccess_sg(levelcompare(this.getAccess_sg(),r.getAccess_sg()));
		    this.setAccess_students(levelcompare(this.getAccess_students(),r.getAccess_students()));
		    this.setAccess_program(levelcompare(this.getAccess_program(),r.getAccess_program()));
		    this.setAccess_test(levelcompare(this.getAccess_test(),r.getAccess_test()));
		    this.setAccess_points(levelcompare(this.getAccess_points(),r.getAccess_points()));
		    this.setAccess_grades(levelcompare(this.getAccess_grades(),r.getAccess_grades()));
		    this.setAccess_schedule(levelcompare(this.getAccess_schedule(),r.getAccess_schedule()));
		    this.setAccess_notes(levelcompare(this.getAccess_notes(),r.getAccess_notes()));
		    
		    this.setStudentdata_access_scope(levelcompare(this.getStudentdata_access_scope(),r.getStudentdata_access_scope()));
		    
		    if (r.getAccess_med() != null){
		    	if (r.getAccess_med().equals(true)) this.setAccess_med(true);
		    }
		    if (r.getAccess_app_admin() != null){
		    	if (r.getAccess_app_admin().equals(true)) this.setAccess_app_admin(true);
		    }
		    
		    if (r.getAccess_app_student() != null){
		    	if (r.getAccess_app_student().equals(true)) this.setAccess_app_student(true);
		    }
		    
		    if (r.getAccess_app_program() != null){
		    	if (r.getAccess_app_program().equals(true)) this.setAccess_app_program(true);
		    }
		    
		    if (r.getAccess_app_test() != null){
		    	if (r.getAccess_app_test().equals(true)) this.setAccess_app_test(true);
		    }
		    
		    if (r.getAccess_app_points() != null){
		    	if (r.getAccess_app_points().equals(true)) this.setAccess_app_points(true);
		    }
		    
		    if (r.getAccess_app_grades() != null){
		    	if (r.getAccess_app_grades().equals(true)) this.setAccess_app_grades(true);
		    }
		    
		    if (r.getAccess_app_schedule() != null){
		    	if (r.getAccess_app_schedule().equals(true)) this.setAccess_app_schedule(true);
		    }
		    
		    if (r.getAccess_app_notes() != null){
		    	if (r.getAccess_app_notes().equals(true)) this.setAccess_app_notes(true);
		    }
			
			
		}
		
	}
	
	
	public boolean getSGLeaderAuth(){
		
		// List<String> levels = getIdentityService().getAccessLevels();
		
//		System.out.println("Auth user = " + this.getUser().getUsername());
//		System.out.println("Auth program: " + this.getAccess_program());
//		System.out.println("Auth test: " + this.getAccess_test());
//		System.out.println("Auth points: " + this.getAccess_points());
//		System.out.println("Auth grades: " + this.getAccess_grades());
//		System.out.println("Auth schedule: " + this.getAccess_schedule());
		
		
		if (	this.getAccess_program().equals(levels.get(2)) &&
				this.getAccess_test().equals(levels.get(2)) &&
				this.getAccess_points().equals(levels.get(2)) &&
				this.getAccess_grades().equals(levels.get(2)) &&
				this.getAccess_schedule().equals(levels.get(2)) &&
				this.getAccess_notes().equals(levels.get(2))
				)
			return true;
		
		return false;
		
	}
	
	
	private IdentityService getIdentityService() {
		 
		return new IdentityService();
	 
	}


}
