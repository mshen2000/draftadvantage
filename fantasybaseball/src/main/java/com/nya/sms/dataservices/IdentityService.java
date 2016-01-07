package com.nya.sms.dataservices;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.VoidWork;
import com.googlecode.objectify.Work;
import com.nya.sms.entities.Role;
import com.nya.sms.entities.Site;
import com.nya.sms.entities.StudentGroup;
import com.nya.sms.entities.User;

public class IdentityService implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public static final String NO_ACCESS = "1-No Access";
	
	public static final String READ_ONLY = "2-Query";
	
	public static final String CREATE_UPDATE = "3-Create/Update";
	
	public static final String MY_STUDENT_GROUP = "1-My Student Group Only";
	
	public static final String ALL_STUDENTS = "2-All Students";
	

	public boolean checkPassword(String username, String password){
		
		if (isUserPresent(username)){
			
			if (getUser(username).getPassword().equals(password)) return true;
			
		}
		
		return false;
		
	}
	
	public boolean isUserPresent(String username) {

		if (ObjectifyService.ofy().load().type(User.class).filter("username", username).count() > 0) return true;

		return false;
	}
	
	public boolean isUserExtIDPresent(String ext_id) {

		if (ObjectifyService.ofy().load().type(User.class).filter("ext_id", ext_id).count() > 0) return true;

		return false;
	}
	
	public User getUserByExtID(String ext_id){
		
		return ObjectifyService.ofy().load().type(User.class).filter("ext_id", ext_id).first().get();
		
	}
	
	public User getUser(String username){
		
		return ObjectifyService.ofy().load().type(User.class).filter("username", username).first().get();
		
	}
	
	public User getUser(long id){
		
		return ObjectifyService.ofy().load().type(User.class).id(id).get();
		
	}
	
	public Long saveUser(User user, String uname){
		
		if (!isUserPresent(user.getUsername())) {
			
			user.setCreatedby(uname);
			
		} 
		
		user.setModifiedby(uname);
		 
		Key<User> key = ObjectifyService.ofy().save().entity(user).now(); 
		
		int i = 0;
		
		while ((!isUserPresent(user.getUsername()))&&(i < 10)){
			
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
	
	public boolean isRolePresent(String rolename) {
		
		if (ObjectifyService.ofy().load().type(Role.class).filter("name", rolename).count() > 0) return true;

		return false;
	}
	
	public Role getRole(String rolename){
		
		return ObjectifyService.ofy().load().type(Role.class).filter("name", rolename).first().get();
		
	}
	
	public void deleteRole(String rolename){
		
		Role r = getRole(rolename);
		
		// remove site relationship
		if (r.getSite() != null){
			
			getSiteService().removeRoleFromSite(r.getSite(), rolename);
			
		}
		
		// delete the role
		ObjectifyService.ofy().delete().entity(r).now(); 
		
	}
	
	public Long saveRole(Role r, String uname){
		
		if (!isRolePresent(r.getName())) {
			
			r.setCreatedby(uname);
			
		} 
		
		r.setModifiedby(uname);
		
		Key<Role> key = ObjectifyService.ofy().save().entity(r).now(); 

		int i = 0;
		
		// Wait for data store to verify role exists
		while ((!isRolePresent(r.getName()))&&(i < 10)){
			
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			i++;
			
		}

		// Use site field and create relationship to site object
		getSiteService().associateRoletoSite(r.getSite(), r.getName());

		return key.getId();
		
	}
	
	public void deleteUser(String username){
		
		User usr = getUser(username);
		
		ObjectifyService.ofy().delete().entity(usr).now(); 
		
		while (isUserPresent(username)){
			
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
	}
	
	
	public List<User> getAllUsers(){
		
		ObjectifyService.ofy().clear();

		return ObjectifyService.ofy().load().type(User.class).list();

	}
	
	public List<User> getSGLeaderAvailableUsers(){

		List<User> allusers = getAllUsers();

		List<User> usersNoSGLeaders = removeSGLeadersfromUserlist(allusers);
		
		// System.out.println("Users that are not SG Leaders: " + usersNoSGLeaders.size());
		
		List <User> usersWithSGLeaderAuth = new ArrayList<User>();
		
		for (User u : usersNoSGLeaders){
			
			Authorization auth = new Authorization(u);
			
			if (auth.getSGLeaderAuth()) {
				usersWithSGLeaderAuth.add(u);
				// System.out.println("User available to be SG Leader: " + u.getUsername());
			}
			
		}
		
		return usersWithSGLeaderAuth;

	}
	
	private List<User> removeSGLeadersfromUserlist (List<User> userlist){
		
		List<StudentGroup> sgleaderslist = ObjectifyService.ofy().load().type(StudentGroup.class).filter("leader !=", null).list();
		List<User> leaders = new ArrayList<User>();
		
		// System.out.println("SGs with leaders: " + sgleaderslist.size());
		
		if (sgleaderslist.size() > 0){
			for (StudentGroup sg : sgleaderslist){
				
				User u = sg.getLeader().get();
				
				leaders.add(u);
	
			}
			
			for (User u : leaders){
				
				if (userlist.contains(u)) userlist.remove(u);
				
			}
		}
		
		return userlist;

	}
	
	public List<Role> getAllRoles(){
		
		return ObjectifyService.ofy().load().type(Role.class).list();

	}
	
	public List<Role> getUserRoles(String username){
		
		User usr = getUser(username);
		
		return ObjectifyService.ofy().load().type(Role.class).filter("users", usr).list();
		
	}
	
	public List<User> getRoleUsers(String rolename){
		
		List<User> users = new ArrayList<User>();
		
		Role r = getRole(rolename);
		
		List<Ref<User>> rusers = r.getUsers();
		
		for (Ref<User> ruser : rusers){
			
			ObjectifyService.ofy().load().ref(ruser);
			users.add(ruser.get());
			
		}
		
		return users;
		
	}
	
    public List<String> getAccessLevels(){
    	
    	List<String> levels = new ArrayList<String>();
    	// levels.add("1-No Access");
    	// levels.add("2-Query");
    	// levels.add("3-Create/Update");
    	
    	levels.add(NO_ACCESS);
    	levels.add(READ_ONLY);
    	levels.add(CREATE_UPDATE);
    	
    	return levels;
    	
    }
    
    public List<String> getStudentDataScopes(){
    	
    	List<String> levels = new ArrayList<String>();
    	levels.add(MY_STUDENT_GROUP);
    	levels.add(ALL_STUDENTS);
    	
    	return levels;
    	
    }
	
	public void createMembership(String username, String rolename){
		
		Role role = getRole(rolename);
		
		User usr = getUser(username);
		
		role.addUser(usr);
		
		ObjectifyService.ofy().save().entity(role).now(); 
		
	}
	
	public void deleteMembership(String username, String rolename){
		
		Role group = getRole(rolename);
		
		User usr = getUser(username);
		
		group.removeUser(usr);
		
		ObjectifyService.ofy().save().entity(group).now(); 
		
	}
	
	 private StudentService getStudentService() {
		 
		 return new StudentService();
	 
	 }
	 
	 private SiteService getSiteService() {
		 
		 return new SiteService(Site.class);
	 
	 }
}
