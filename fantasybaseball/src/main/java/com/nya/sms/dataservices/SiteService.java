package com.nya.sms.dataservices;

import java.util.ArrayList;
import java.util.List;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.Ref;
import com.nya.sms.entities.Role;
import com.nya.sms.entities.Site;
import com.nya.sms.entities.Student;
import com.nya.sms.entities.StudentGroup;
import com.nya.sms.entities.User;

public class SiteService extends AbstractDataServiceImpl<Site>{
	
	private static final long serialVersionUID = 1L;
	
	public static final String MASTER_SITE = "All Sites";

	public SiteService(Class<Site> clazz) {
		super(clazz);
		// TODO Auto-generated constructor stub
	}
	
	public boolean isSitePresent(String name) {

		if (ObjectifyService.ofy().load().type(Site.class).filter("name", name).count() > 0) return true;

		return false;
	}
	
	public Site get(String name){
		
		return ObjectifyService.ofy().load().type(Site.class).filter("name", name).first().get();
		
	}
	
	@Override
	public Long save(Site site, String uname){
		
		if (!isSitePresent(site.getName())) {
			
			site.setCreatedby(uname);
			
		} 
		
		site.setModifiedby(uname);
		 
		Key<Site> key = ObjectifyService.ofy().save().entity(site).now(); 
		
		int i = 0;
		
		while ((!isSitePresent(site.getName()))&&(i < 10)){
			
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
	
	public List<Role> getSiteRoles(String sitename){
		
		List<Role> roles = new ArrayList<Role>();
		
		Site site = get(sitename);
		
		List<Ref<Role>> rroles = site.getRoles();
		
		for (Ref<Role> rrole : rroles){
			
			ObjectifyService.ofy().load().ref(rrole);
			roles.add(rrole.get());
			
		}

		return roles;
		
	}
	
	public void associateRoletoSite(String sitename, String rolename){
		
		if (!getIdentityService().isRolePresent(rolename)) {
			
			System.out.println("Could not find role to remove from site, exiting removeRoleFromSite.");
			return;
			
		} 

		Role role = getIdentityService().getRole(rolename);
    	Ref<Role> roleref = Ref.create(role);
    	
    	List<Site> sites = ObjectifyService.ofy().load().type(Site.class).filter("name", sitename).list();
    	
    	// If site is master, then just remove previous role associations from all sites
    	if (sitename != null){
	    	if (sitename.equals(MASTER_SITE)){
	    		
	    		System.out.println("Site is master site, removing role associations to all sites.");
	    		
				// Get sites that already have this role
				List<Site> matchingsites2 = ObjectifyService.ofy().load().type(Site.class).filter("roles", roleref).list();
				
				// Remove role association from those sites
				for (Site s : matchingsites2){
					
					System.out.println("Removing role " + role.getName() + " from site " + s.getName());
					s.removeRole(role);
					Key<Site> skey = ObjectifyService.ofy().save().entity(s).now(); 
					
				}
				
				return;
				
	    	}
    	}
    	else if (!isSitePresent(sitename)) {
			
			System.out.println("Could not find site to remove role from, exiting removeRoleFromSite.");
			return;
			
		}
		
		Site site = get(sitename);
		

		// Check if association already exists
		List<Site> matchingsites1 = ObjectifyService.ofy().load().type(Site.class).filter("roles", roleref).filter("name", sitename).list();
		
		if (matchingsites1.size() == 1){
			
			System.out.println("Relationship between site " + sitename + " and role " + rolename + " already exists.");
			return;
			
		} else if(matchingsites1.size() == 0){
			
			System.out.println("Relationship between site " + sitename + " and role " + rolename + " does not exist.");
			
			// Get sites that already have this role
			List<Site> matchingsites2 = ObjectifyService.ofy().load().type(Site.class).filter("roles", roleref).list();
			
			// Remove role association from those sites
			for (Site s : matchingsites2){
				
				System.out.println("Removing role " + role.getName() + " from site " + s.getName());
				s.removeRole(role);
				Key<Site> skey = ObjectifyService.ofy().save().entity(s).now(); 
				
			}
			
			// Add desired role to desired site
			System.out.println("Creating relationship between site " + site.getName() + " and role " + role.getName());
			site.addRole(role);
			Key<Site> key = ObjectifyService.ofy().save().entity(site).now(); 
			
		}
		
		
	}
	
	
	public void associateStudenttoSite(String sitename, Long studentid){

		Student student = getStudentService().getStudent(studentid);
    	Ref<Student> studentref = Ref.create(student);
    	
    	List<Site> sites = ObjectifyService.ofy().load().type(Site.class).filter("name", sitename).list();
    	
		if (!isSitePresent(sitename)) {
			
			System.out.println("Could not find site to associate student to, exiting associateStudenttoSite.");
			return;
			
		}
		
		Site site = get(sitename);
		

		// Check if association already exists
		List<Site> matchingsites1 = ObjectifyService.ofy().load().type(Site.class).filter("students", studentref).filter("name", sitename).list();
		
		if (matchingsites1.size() == 1){
			
			System.out.println("Relationship between site " + sitename + " and student " + student.getFirstname() + " already exists.");
			return;
			
		} else if(matchingsites1.size() == 0){
			
			System.out.println("Relationship between site " + sitename + " and student " + student.getFirstname() + " does not exist.");
			
			// Get sites that already have this student
			List<Site> matchingsites2 = ObjectifyService.ofy().load().type(Site.class).filter("students", studentref).list();
			
			// Remove student association from those sites
			for (Site s : matchingsites2){
				
				System.out.println("Removing student " + student.getFirstname() + " from site " + s.getName());
				s.removeStudent(student);
				Key<Site> skey = ObjectifyService.ofy().save().entity(s).now(); 
				
			}
			
			// Add desired student to desired site
			System.out.println("Creating relationship between site " + site.getName() + " and student " + student.getFirstname());
			site.addStudent(student);
			Key<Site> key = ObjectifyService.ofy().save().entity(site).now(); 
			
		}
		
		
	}
	
	public void associateStudentGrouptoSite(String sitename, String groupname){

		StudentGroup sg = getStudentService().getGroup(groupname);
    	Ref<StudentGroup> sgref = Ref.create(sg);
    	
    	List<Site> sites = ObjectifyService.ofy().load().type(Site.class).filter("name", sitename).list();
    	
		if (!isSitePresent(sitename)) {
			
			System.out.println("Could not find site to associate student group to, exiting associateStudentGrouptoSite.");
			return;
			
		}
		
		Site site = get(sitename);
		

		// Check if association already exists
		List<Site> matchingsites1 = ObjectifyService.ofy().load().type(Site.class).filter("studentgroups", sgref).filter("name", sitename).list();
		
		if (matchingsites1.size() == 1){
			
			System.out.println("Relationship between site " + sitename + " and student group " + sg.getName() + " already exists.");
			return;
			
		} else if(matchingsites1.size() == 0){
			
			System.out.println("Relationship between site " + sitename + " and student group " + sg.getName() + " does not exist.");
			
			// Get sites that already have this student group
			List<Site> matchingsites2 = ObjectifyService.ofy().load().type(Site.class).filter("studentgroups", sgref).list();
			
			// Remove student association from those sites
			for (Site s : matchingsites2){
				
				System.out.println("Removing student group" + sg.getName() + " from site " + s.getName());
				s.removeStudentGroup(sg);
				Key<Site> skey = ObjectifyService.ofy().save().entity(s).now(); 
				
			}
			
			// Add desired student to desired site
			System.out.println("Creating relationship between site " + site.getName() + " and student group" + sg.getName());
			site.addStudentGroup(sg);
			Key<Site> key = ObjectifyService.ofy().save().entity(site).now(); 
			
		}
		
		
	}
	
	
	public void removeStudentFromSite(String sitename, Long studentid){
		
		if (!getStudentService().isStudentPresent(studentid)) {
			
			System.out.println("Could not find student to remove from site, exiting removeStudentFromSite.");
			return;
			
		} 

		Student student = getStudentService().getStudent(studentid);
    	Ref<Student> studentref = Ref.create(student);
    	
    	List<Site> sites = ObjectifyService.ofy().load().type(Site.class).filter("name", sitename).list();
    	
		if (!isSitePresent(sitename)) {
			
			System.out.println("Could not find site to remove student from, exiting removeStudentFromSite.");
			return;
			
		}
		
		Site site = get(sitename);
		
		site.removeStudent(student);
		
		Key<Site> key = ObjectifyService.ofy().save().entity(site).now(); 
		
	}
	
	
	public void removeRoleFromSite(String sitename, String rolename){
		
		if (!getIdentityService().isRolePresent(rolename)) {
			
			System.out.println("Could not find role to remove from site, exiting removeRoleFromSite.");
			return;
			
		} 

		Role role = getIdentityService().getRole(rolename);
    	Ref<Role> roleref = Ref.create(role);
    	
    	List<Site> sites = ObjectifyService.ofy().load().type(Site.class).filter("name", sitename).list();
    	
		if (!isSitePresent(sitename)) {
			
			System.out.println("Could not find site to remove role from, exiting removeRoleFromSite.");
			return;
			
		}
		
		Site site = get(sitename);
		
		site.removeRole(role);
		
		Key<Site> key = ObjectifyService.ofy().save().entity(site).now(); 
		
	}
	
	
	public void removeStudentGroupFromSite(String sitename, String groupname){
		
		if (!getStudentService().isGroupPresent(groupname)) {
			
			System.out.println("Could not find student group to remove from site, exiting removeStudentGroupFromSite.");
			return;
			
		} 

		StudentGroup sg = getStudentService().getGroup(groupname);
    	Ref<StudentGroup> sgref = Ref.create(sg);
    	
    	List<Site> sites = ObjectifyService.ofy().load().type(Site.class).filter("name", sitename).list();
    	
		if (!isSitePresent(sitename)) {
			
			System.out.println("Could not find site to remove student group from, exiting removeStudentGroupFromSite.");
			return;
			
		}
		
		Site site = get(sitename);
		
		site.removeStudentGroup(sg);
		
		Key<Site> key = ObjectifyService.ofy().save().entity(site).now(); 
		
	}
	
	
	public List<Student> getSiteStudents(String sitename){
		
		List<Student> students = new ArrayList<Student>();
		
		Site site = get(sitename);
		
		List<Ref<Student>> rstudents = site.getStudents();
		
		for (Ref<Student> rstudent : rstudents){
			
			ObjectifyService.ofy().load().ref(rstudent);
			students.add(rstudent.get());
			
		}

		return students;
		
	}
	
	public List<StudentGroup> getSiteStudentGroups(String sitename){
		
		List<StudentGroup> studentgroups = new ArrayList<StudentGroup>();
		
		Site site = get(sitename);
		
		List<Ref<StudentGroup>> rstudentgroups = site.getStudentgroups();
		
		for (Ref<StudentGroup> rstudentgroup : rstudentgroups){
			
			ObjectifyService.ofy().load().ref(rstudentgroup);
			studentgroups.add(rstudentgroup.get());
			
		}

		return studentgroups;
		
	}
	
	public List<String> getAllSiteNamesPlus(){
		
		List<String> sitenames = new ArrayList<String>();
		
		List<Site> sites = getAll();
		
		sitenames.add(MASTER_SITE);
		
		for (Site s : sites){
			
			sitenames.add(s.getName());
			
		}
		
		return sitenames;
		
	}
	
	public List<String> getAllSiteNames(){
		
		List<String> sitenames = new ArrayList<String>();
		
		List<Site> sites = getAll();
		
		for (Site s : sites){
			
			sitenames.add(s.getName());
			
		}
		
		return sitenames;
		
	}
	
	public String getMasterSite(){
		
		return MASTER_SITE;
		
	}
	
	 private IdentityService getIdentityService() {
		 
		 return new IdentityService();
	 
	 }
	 
	 private StudentService getStudentService() {
		 
		 return new StudentService();
	 
	 }



}
