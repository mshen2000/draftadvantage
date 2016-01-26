package com.nya.sms.dataservices;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.VoidWork;
import com.googlecode.objectify.cmd.Query;
import com.nya.sms.entities.Site;
import com.nya.sms.entities.Student;
import com.nya.sms.entities.StudentGroup;
import com.nya.sms.entities.StudentHealth;
import com.nya.sms.entities.User;

import static com.googlecode.objectify.ObjectifyService.ofy;


public class StudentService implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public List<Student> getAllStudents(){
		
		return ObjectifyService.ofy().load().type(Student.class).list();

	}

	
	// Get all students based on site ("All Sites" is applicable)
	public List<Student> getAllStudents(String site){
		
		List<Student> students = new ArrayList<Student>();
		
		getSiteService();
		if (site.equals(SiteService.MASTER_SITE))
			students = ObjectifyService.ofy().load().type(Student.class).list();
		else {
			// Query for a list of students with the site
			Query<Student> q = ofy().load().type(Student.class);
			q = q.filter("site", site);
			students = q.list(); 
		}
		
		return students;

	}
	
	
	// Get a list of students for a student group
	public List<Student> getStudentsInSG(String sgname){
		
		List<Student> students = new ArrayList<Student>();
		
		// Query for a list of student Refs for a student group
		Query<StudentGroup> q = ofy().load().type(StudentGroup.class);
		q = q.filter("name", sgname);
		List<StudentGroup> sglist = q.list();
		
		List<Ref<Student>> reflist = sglist.get(0).getStudents();
		
		// For each student Ref, get the Student object and add to the List
		for (Ref<Student> rs : reflist){
			students.add(ObjectifyService.ofy().load().ref(rs).now());
		}
		
		return students;
		
	}
	
	// Return list of students based on authorization
	//   - i.e. student access and student scope
	public List<Student> getAuthorizedStudentList (Authorization auth){
		
		List<Student> students = new ArrayList<Student>();
		
		getIdentityService();
		// Check if authorized to query student data
		if (!auth.getAccess_students().equals(IdentityService.NO_ACCESS)){

			// If student access scope is for all students, then get all the students
			if (auth.getStudentdata_access_scope().equals(IdentityService.ALL_STUDENTS))
				students = getAllStudents(auth.getSite());

			// If student access scope is my group only, then get only students from that group
			if (auth.getStudentdata_access_scope().equals(IdentityService.MY_STUDENT_GROUP)){
				
				// Get the student group that the current user is a leader of
				List<StudentGroup> groups = getLeaderGroups(auth.getAuthuser().getUsername());
				
				// Get the list of students in the group
				if (groups.size() > 0)
					students = getStudentsInSG(groups.get(0).getName());

			}
				
		}
		
		return students;
		
	}
	
	// Check if authorization can access all student data
	public boolean isStudentQueryAccessAllData(Authorization auth){
		
		getIdentityService();
		getSiteService();
		// Check if authorized to query student data AND
		if ((!auth.getAccess_students().equals(IdentityService.NO_ACCESS)) &&

			// If student access scope is for all students AND
			(auth.getStudentdata_access_scope().equals(IdentityService.ALL_STUDENTS)) &&
			
			//  If site access is master site
			(auth.getSite().equals(SiteService.MASTER_SITE)))
			
			return true;
		
		return false;
		
	}
	
	// Return list of students IDs based on authorization
	//   - i.e. student access and student scope
	public List<Long> getAuthorizedStudentIDList (Authorization auth){
		
		List<Student> students = getAuthorizedStudentList(auth);
		
		// Create list of student IDs
		List<Long> sids = new ArrayList<Long>();
		
		for (Student s : students){
			
			sids.add(s.getId());
			
		}

		return sids;
		
	}
	
	
	public boolean isStudentPresent(String firstname, String middlename, String lastname, Date dob) {

		Query<Student> q = ofy().load().type(Student.class);
		q = q.filter("firstname", firstname);
		q = q.filter("middlename", middlename);
		q = q.filter("lastname", lastname);
		q = q.filter("dob", dob);
		List<Student> slist = q.list();

		if (slist.isEmpty()) return false;

		return true;
	}
	
	public boolean isStudentPresent(Long studentid) {

//		Query<Student> q = ofy().load().type(Student.class);
//		q = q.filter("id", studentid);
//		List<Student> slist = q.list();
		
		Student s =  ofy().load().type(Student.class).id(studentid).now();

		if (s == null) return false;

		return true;
	}

	
	public Long saveStudent(Student student, String uname){
		
		if (!isStudentPresent(student.getFirstname(), student.getMiddlename(), student.getLastname(), student.getDob())) {
			
			student.setCreatedby(uname);
			
		} 
		
		student.setModifiedby(uname);
		
		Key<Student> key = ObjectifyService.ofy().save().entity(student).now(); 

		int i = 0;
		
		// Wait for data store to verify student exists
		while ((!isStudentPresent(key.getId()))&&(i < 10)){
			
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			i++;
			
		}
		
		
		// Use site field and create relationship to site object
		getSiteService().associateStudenttoSite(student.getSite(), key.getId());
		
		return key.getId();

	}
	
	public void deleteStudent(Long studentid){
		
		final Long sid = studentid;
		
		final Student student = getStudent(sid);
		
		final StudentHealth sh = getStudentHealth(sid);
		
		// ObjectifyService.ofy().delete().entity(student).now(); 
		
		// remove site relationship
		if (student.getSite() != null){
			
			getSiteService().removeStudentFromSite(student.getSite(), studentid);
			
		}
		
		ObjectifyService.ofy().transact(new VoidWork() {
	        @SuppressWarnings("unused")
			public void vrun() {
	            
	        	if (student != null){
		        	// System.out.println("Deleting student: " + sid);
		        	ObjectifyService.ofy().delete().entity(student).now(); 
	        	} else {
	        		// System.out.println("Delete cancelled - Could not find student: " + sid);
	        	}
	        	if (sh != null){
		        	// System.out.println("Deleting studenthealth: " + sid);
		        	ObjectifyService.ofy().delete().entity(sh).now(); 
	        	} else {
	        		// System.out.println("Delete cancelled - Could not find studenthealth for student: " + sid);
	        	}
	        	
	        }
	    });
		
	}
	
	public Student getStudent(Long studentid){
		
		return ObjectifyService.ofy().load().type(Student.class).id(studentid).now();
		
	}
	
	public Student getStudent(String firstname, String middlename, String lastname, Date dob){
		
		Query<Student> q = ofy().load().type(Student.class);
		q = q.filter("firstname", firstname);
		q = q.filter("middlename", middlename);
		q = q.filter("lastname", lastname);
		q = q.filter("dob", dob);
		List<Student> slist = q.list();
		
		return slist.get(0);
		
	}
	
	public User getCreatedByUser(Student student){
		
		User u = ObjectifyService.ofy().load().type(User.class).filter("username", student.getCreatedby()).first().now();
		
		return u;
		
	}
	
	
	public List<StudentGroup> getAllGroups(){
		
		return ObjectifyService.ofy().load().type(StudentGroup.class).list();

	}

	
	public long saveGroup(StudentGroup sg, String uname){
		
		// System.out.println("Saving group...");
		
		if (!isGroupPresent(sg.getName())) {
			
			// System.out.println("Group " + sg.getName() + " not found, setting created by to: " + uname);
			
			sg.setCreatedby(uname);
			
		} 
		
		// System.out.println("Setting modified by to: " + uname);
		
		sg.setModifiedby(uname);
		
		Key<StudentGroup> key = ObjectifyService.ofy().save().entity(sg).now(); 
		
		int i = 0;
		
		// Wait for data store to verify student exists
		while ((!isGroupPresent(sg.getName()))&&(i < 10)){
			
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			i++;
			
		}
		
		
		// Use site field and create relationship to site object
		getSiteService().associateStudentGrouptoSite(sg.getSite(), sg.getName());
		
		return key.getId();
		
	}
	
	public void deleteGroup(String name){
		
		System.out.println("Deleting group " + name);
		
		StudentGroup sg = ObjectifyService.ofy().load().type(StudentGroup.class).filter("name", name).list().get(0);
		
		// remove site relationship
		if (sg.getSite() != null){
			
			System.out.println("Removing site relationship " + sg.getSite());
			
			getSiteService().removeStudentGroupFromSite(sg.getSite(), name);
			
		}
		
		ObjectifyService.ofy().delete().entity(sg).now(); 
		
	}
	
	public boolean isGroupPresent(String name) {

		if (ObjectifyService.ofy().load().type(StudentGroup.class).filter("name", name).list().isEmpty()) return false;

		return true;
	}
	
	public StudentGroup getGroup(String groupname){
		
		return ObjectifyService.ofy().load().type(StudentGroup.class).filter("name", groupname).first().now();
		
	}
	
	public List<StudentGroup> getStudentGroup(Long studentid){
		
		Student st = getStudent(studentid);
		
		return ObjectifyService.ofy().load().type(StudentGroup.class).filter("students", st).list();
		
	}
	
	// Given a username, return a list of Student Groups of which the user is a leader of.
	// There should only be either 0 or 1 items in the list.
	public List<StudentGroup> getLeaderGroups(String username){
		
		User u = getIdentityService().getUser(username);
		
		return ObjectifyService.ofy().load().type(StudentGroup.class).filter("leader", u).list();
		
	}
	
	public void createMembership(Long studentid, String groupname){
		
		StudentGroup sg = getGroup(groupname);
		
		Student st = getStudent(studentid);
		
		sg.addStudent(st);
		
		ObjectifyService.ofy().save().entity(sg).now(); 
		
	}
	
	public void deleteMembership(Long studentid, String groupname){
		
		StudentGroup sg = getGroup(groupname);
		
		Student st = getStudent(studentid);
		
		sg.removeStudent(st);
		
		ObjectifyService.ofy().save().entity(sg).now(); 
		
	}
	
	
//********* Student Health ********************************
	
	public List<StudentHealth> getAllStudentHealths(){
		
		return ObjectifyService.ofy().load().type(StudentHealth.class).list();

	}
	
	public boolean isStudentHealthPresent(Long studentid) {

		Query<StudentHealth> q = ofy().load().type(StudentHealth.class);
		q = q.filter("studentid", studentid);
		List<StudentHealth> slist = q.list();

		if (slist.isEmpty()) return false;

		return true;
	}
	
	public void saveStudentHealth(StudentHealth studenthealth){
		
		ObjectifyService.ofy().save().entity(studenthealth).now(); 

	}
	
	public void deleteStudentHealth(Long studentid){
		
		StudentHealth studenthealth = getStudentHealth(studentid);
		
		ObjectifyService.ofy().delete().entity(studenthealth).now(); 
		
	}
	
	public StudentHealth getStudentHealth(Long studentid){
		
		Query<StudentHealth> q = ofy().load().type(StudentHealth.class);
		q = q.filter("studentid", studentid);
		List<StudentHealth> slist = q.list();
		
		if (slist.size() < 1) return null;
		
		return slist.get(0);
		
	}
	
	 private IdentityService getIdentityService() {
		 
		 return new IdentityService();
	 
	 }
	 
	 private SiteService getSiteService() {
		 
		 return new SiteService(Site.class);
	 
	 }
	
}
