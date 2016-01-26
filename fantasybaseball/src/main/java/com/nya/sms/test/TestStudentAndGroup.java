/**
 * 
 */
package com.nya.sms.test;

import static com.googlecode.objectify.ObjectifyService.ofy;
import static org.junit.Assert.*;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.cmd.Query;
import com.googlecode.objectify.util.Closeable;
import com.nya.sms.dataservices.Authorization;
import com.nya.sms.dataservices.IdentityService;
import com.nya.sms.dataservices.SiteService;
import com.nya.sms.dataservices.StudentService;
import com.nya.sms.entities.BaseEntity;
import com.nya.sms.entities.Role;
import com.nya.sms.entities.Site;
import com.nya.sms.entities.Student;
import com.nya.sms.entities.StudentGroup;
import com.nya.sms.entities.StudentHealth;
import com.nya.sms.entities.User;


/**
 * @author Michael
 *
 */
public class TestStudentAndGroup {
    
    private final LocalServiceTestHelper helper =
            new LocalServiceTestHelper(
                            new LocalDatastoreServiceTestConfig()
                              .setDefaultHighRepJobPolicyUnappliedJobPercentage(0.1f)
                            );

	private Calendar c;
	private Date yesterday;
	private Date tomorrow;
	private Date tenyearsago;
	private DateFormat dateFormatter;
	
	private Closeable closeable;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		
		helper.setUp();

		ObjectifyService.register(Role.class);
		ObjectifyService.register(User.class);
		ObjectifyService.register(Site.class);
		ObjectifyService.register(Student.class);
		ObjectifyService.register(StudentGroup.class);
		ObjectifyService.register(StudentHealth.class);

		c= Calendar.getInstance();
		c.setTime(new Date(java.lang.System.currentTimeMillis()));
		c.add(Calendar.DAY_OF_MONTH, -1);
		yesterday=c.getTime();
		c.add(Calendar.DAY_OF_MONTH, 2);
		tomorrow=c.getTime();
		c.add(Calendar.YEAR, -10);
		tenyearsago = c.getTime();
		
		Locale currentlocal = new Locale("en_US");
		dateFormatter = DateFormat.getDateInstance(DateFormat.DEFAULT, currentlocal);
		
		closeable = ObjectifyService.begin();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		closeable.close();
		helper.tearDown();
		
	}
	

	@Test
	public void testStudent1() {
		
		// also test save student, isstudentpresent, getallstudents, deleteStudent
		User usr1 = new User("test1","test1");
		usr1.setFirstname("Test");
		usr1.setLastname("One");
		
		getIdentityService().saveUser(usr1,usr1.getUsername());
		
		Site site1 = new Site("testsite");
		site1.setDescription("site description");
		Long saveditemid1 = getSiteService().save(site1, usr1.getUsername());
		
		Student student1 = new Student("joe","blow");
		student1.setDob(tenyearsago);
		
		Student student2 = new Student("jeff","smith");
		student2.setMiddlename("mid");
		student2.setDob(tenyearsago);
		
		student1.setSite(site1.getName());

		Long s1id = getStudentService().saveStudent(student1,usr1.getUsername());
		Long s2id = getStudentService().saveStudent(student2,usr1.getUsername());
		
		Student teststudent = getStudentService().getStudent("jeff","mid","smith",tenyearsago);
		
		StudentHealth sh = new StudentHealth(teststudent.getId());
		sh.setOtherillness("other illness test");
		getStudentService().saveStudentHealth(sh);
		
		// Test that there is exactly 1 StudentHealth object
		Assert.assertEquals(1, getStudentService().getAllStudentHealths().size());
		
		// Test that there is exactly 2 Student objects
		Assert.assertEquals(2, getStudentService().getAllStudents().size());
		
		// Test that the student is associated to the site
		Assert.assertEquals(s1id, getSiteService().getSiteStudents(site1.getName()).get(0).getId());
		
		List<Student> teststudents = getStudentService().getAllStudents();
		
		// Test that the 2 students created exist
		Assert.assertTrue(getStudentService().isStudentPresent("joe",null,"blow",tenyearsago));
		Assert.assertTrue(getStudentService().isStudentPresent("jeff","mid","smith",tenyearsago));
		
		getStudentService().deleteStudent(getStudentService().getStudent("joe",null,"blow",tenyearsago).getId());
		getStudentService().deleteStudent(getStudentService().getStudent("jeff","mid","smith",tenyearsago).getId());
		
		// Test that there are no students left after delete
		Assert.assertEquals(0, getStudentService().getAllStudents().size());
		
		// Test that there is no StudentHealths left after delete
		Assert.assertEquals(0, getStudentService().getAllStudentHealths().size());
		
		// Test that there are no students associated to sites
		Assert.assertEquals(0, getSiteService().getSiteStudents(site1.getName()).size());

	}
	
	@Test
	public void testStudentModifiedBy() {
		
		User usr1 = new User("test1","test1");
		usr1.setFirstname("Test");
		usr1.setLastname("One");
		
		getIdentityService().saveUser(usr1,usr1.getUsername());
		
		User user1_ret = getIdentityService().getUser("test1");
		
		Student student1 = new Student("joe","blow");
		student1.setDob(tenyearsago);
		student1.setCreatedby(usr1.getUsername());

		getStudentService().saveStudent(student1,usr1.getUsername());

		Assert.assertTrue(getStudentService().getStudent("joe",null,"blow",tenyearsago).getCreatedon().after(yesterday));
		Assert.assertTrue(getStudentService().getStudent("joe",null,"blow",tenyearsago).getCreatedon().before(tomorrow));
		Assert.assertTrue(getStudentService().getCreatedByUser(getStudentService()
				.getStudent("joe",null,"blow",tenyearsago)).getUsername().equals("test1"));

	}


	@Test
	public void testSaveStudentGroup() {
		
		// also test getallStudentGroups, getStudentGroup, saveStudentGroup
		User usr1 = new User("test1","test1");
		usr1.setFirstname("Test");
		usr1.setLastname("One");
		
		getIdentityService().saveUser(usr1,usr1.getUsername());
		
		Site site1 = new Site("testsite");
		site1.setDescription("site description");
		Long saveditemid1 = getSiteService().save(site1, usr1.getUsername());

		StudentGroup r1 = new StudentGroup("test1");
		StudentGroup r2 = new StudentGroup("test2");
		
		r1.setSite(site1.getName());
		r2.setSite(site1.getName());
		
		Long sg1id = getStudentService().saveGroup(r1,usr1.getUsername());
		Long sg2id = getStudentService().saveGroup(r2,usr1.getUsername());
		
		// test that there exists a group named test1
		Assert.assertEquals(getStudentService().getGroup("test1").getName(), r1.getName());
		
		List<StudentGroup> StudentGroups = getStudentService().getAllGroups();
		
		// test that there are exactly 2 groups
		Assert.assertEquals(2, getStudentService().getAllGroups().size());
		
		// test that group test1 is present
		Assert.assertTrue(getStudentService().isGroupPresent("test1"));
		
		// Test that the student group test1 is associated to the site
		Assert.assertEquals(sg1id, getSiteService().getSiteStudentGroups(site1.getName()).get(0).getId());
		
		getStudentService().deleteGroup("test1");
		getStudentService().deleteGroup("test2");
		
		// test that group test1 is not present (deleted)
		Assert.assertFalse(getStudentService().isGroupPresent("test1"));
		
		// Test that there are no student groups associated to sites
		System.out.println("Starting test - no student groups associated to site");
		Assert.assertEquals(0, getSiteService().getSiteStudentGroups(site1.getName()).size());
		
	}

	
	@Test
	public void testTestMembership() {
		
		System.out.println("Starting testTestMembership");
		
		User usr1 = new User("test1","test1");
		usr1.setFirstname("Test");
		usr1.setLastname("One");
		
		Long userid = getIdentityService().saveUser(usr1,usr1.getUsername());
		
		User usr1_r = getIdentityService().getUser(userid);
		
		Student student1 = new Student("joe","blow");
		student1.setDob(tenyearsago);
		
		Student student2 = new Student("jeff","smith");
		student2.setMiddlename("mid");
		student2.setDob(tenyearsago);

		getStudentService().saveStudent(student1,usr1.getUsername());
		getStudentService().saveStudent(student2,usr1.getUsername());
		
		StudentGroup r1 = new StudentGroup("StudentGroup1");
		
		r1.setLeader(Ref.create(usr1_r));

		getStudentService().saveGroup(r1,usr1.getUsername());
		
		List<StudentGroup> sglist = getStudentService().getLeaderGroups(usr1_r.getUsername());
		
		// test that user is leader of student group
		Assert.assertTrue(sglist.size() == 1);
		
		Student student1r = getStudentService().getStudent("joe",null,"blow",tenyearsago);

		getStudentService().createMembership(student1r.getId(), "StudentGroup1");
		
		// Test that the student group has 1 member
		Assert.assertTrue(getStudentService().getStudentGroup(student1r.getId()).size() == 1);
		
		// Test membership using getStudentsInSG
		Assert.assertTrue(getStudentService().getStudentsInSG("StudentGroup1").size() == 1);
		
		getStudentService().deleteMembership(student1r.getId(), "StudentGroup1");
		
		// Test that the student group has 0 members
		Assert.assertTrue(getStudentService().getStudentGroup(student1r.getId()).size() == 0);

	}
	
	@Test
	public void testAuthorizedStudentList() {
		
		List<String> levels = getIdentityService().getAccessLevels();
		
		// Create Users
		User usr1 = new User("test1","test1");
		usr1.setFirstname("Test");
		usr1.setLastname("One");
		
		User usr2 = new User("test2","test2");
		usr2.setFirstname("Test");
		usr2.setLastname("Two");
		
		getIdentityService().saveUser(usr1,usr1.getUsername());
		getIdentityService().saveUser(usr2,usr1.getUsername());
		
		User usr1_r = getIdentityService().getUser(usr1.getUsername());
		
		// Create Sites
		Site site1 = new Site("testsite1");
		site1.setDescription("site description");
		Long savedsite1 = getSiteService().save(site1, usr1.getUsername());
		
		Site site2 = new Site("testsite2");
		site1.setDescription("site description");
		Long savedsite2 = getSiteService().save(site2, usr1.getUsername());
		
		// Create roles (with site info)
		Role role_allsites = new Role("role_allsites","role_allsites");
		role_allsites.setAccess_app_admin(true);
		role_allsites.setAccess_grades(levels.get(2));
		role_allsites.setSite(getSiteService().MASTER_SITE);
		role_allsites.setAccess_students(levels.get(2));
		role_allsites.setStudentdata_access_scope(getIdentityService().ALL_STUDENTS);
		getIdentityService().saveRole(role_allsites,usr1.getUsername());
		
		Role role_allstudents = new Role("role_allstudents","role_allstudents");
		role_allstudents.setAccess_app_admin(true);
		role_allstudents.setAccess_grades(levels.get(2));
		role_allstudents.setSite(site1.getName());
		role_allstudents.setAccess_students(levels.get(2));
		role_allstudents.setStudentdata_access_scope(getIdentityService().ALL_STUDENTS);
		getIdentityService().saveRole(role_allstudents,usr1.getUsername());
		
		Role role_mysg = new Role("role_mysg","role_mysg");
		role_mysg.setAccess_app_admin(true);
		role_mysg.setAccess_grades(levels.get(2));
		role_mysg.setSite(site1.getName());
		role_mysg.setAccess_students(levels.get(2));
		role_mysg.setStudentdata_access_scope(getIdentityService().MY_STUDENT_GROUP);
		getIdentityService().saveRole(role_mysg,usr1.getUsername());
		
		Role role_nostudents = new Role("role_nostudents","role_nostudents");
		role_nostudents.setAccess_app_admin(true);
		role_nostudents.setAccess_grades(levels.get(2));
		role_nostudents.setSite(site1.getName());
		role_nostudents.setAccess_students(levels.get(0));
		role_nostudents.setStudentdata_access_scope(getIdentityService().ALL_STUDENTS);
		getIdentityService().saveRole(role_nostudents,usr1.getUsername());
		
		// Add user to roles
		getIdentityService().createMembership("test1", "role_allstudents");
		getIdentityService().createMembership("test1", "role_mysg");
		getIdentityService().createMembership("test1", "role_nostudents");
		getIdentityService().createMembership("test1", "role_allsites");
		
		// Retrieve rolls
		Role role_allstudents_r = getIdentityService().getRole("role_allstudents");
		Role role_mysg_r = getIdentityService().getRole("role_mysg");
		Role role_nostudents_r = getIdentityService().getRole("role_nostudents");
		Role role_allsites_r = getIdentityService().getRole("role_allsites");
		
		// Create Authorizations from roles
		Authorization auth_allstudents = new Authorization(usr1_r, role_allstudents_r);
		Authorization auth_mysg = new Authorization(usr1_r, role_mysg_r);
		Authorization auth_nostudents = new Authorization(usr1_r, role_nostudents_r);
		Authorization auth_allsites = new Authorization(usr1_r, role_allsites_r);
		
		// Create students
		Student student1 = new Student("joe","blow");
		student1.setDob(tenyearsago);
		student1.setSite(site1.getName());
		
		Student student2 = new Student("jeff","smith");
		student2.setMiddlename("mid");
		student2.setDob(tenyearsago);
		student2.setSite(site1.getName());
		
		Student student3 = new Student("Jane","Doe");
		student3.setDob(tenyearsago);
		student3.setSite(site2.getName());

		Long student1_id = getStudentService().saveStudent(student1,usr1.getUsername());
		Long student2_id = getStudentService().saveStudent(student2,usr1.getUsername());
		Long student3_id = getStudentService().saveStudent(student3,usr1.getUsername());
		
		// Create student group set leader as user1
		StudentGroup sg1 = new StudentGroup("StudentGroup1");
		
		sg1.setLeader(Ref.create(usr1_r));

		getStudentService().saveGroup(sg1,usr1.getUsername());
		
		List<StudentGroup> sglist = getStudentService().getLeaderGroups(usr1_r.getUsername());
		
		// Add one student to group
		getStudentService().createMembership(student1_id, sg1.getName());
		
		// Test that getAuthorizedStudentList returns the right number of students
		Assert.assertTrue(getStudentService().getAuthorizedStudentList(auth_allsites).size() == 3);
		Assert.assertTrue(getStudentService().getAuthorizedStudentList(auth_allstudents).size() == 2);
		Assert.assertTrue(getStudentService().getAuthorizedStudentList(auth_mysg).size() == 1);
		Assert.assertTrue(getStudentService().getAuthorizedStudentList(auth_nostudents).size() == 0);
	
	}
	
	
	
	@Test
	public void testStudentHealth() {
		
		// also test savestudenthealth, isstudenthealthpresent, getallstudenthealths, getstudenthealth, deleteStudentHealth
		User usr1 = new User("test1","test1");
		usr1.setFirstname("Test");
		usr1.setLastname("One");
		
		getIdentityService().saveUser(usr1,usr1.getUsername());
		
		Student student2 = new Student("jeff","smith");
		student2.setMiddlename("mid");
		student2.setDob(tenyearsago);

		getStudentService().saveStudent(student2,usr1.getUsername());
		
		Student teststudent = getStudentService().getStudent("jeff","mid","smith",tenyearsago);
		
		StudentHealth sh = new StudentHealth(teststudent.getId());
		sh.setOtherillness("other illness test");
		
		getStudentService().saveStudentHealth(sh);

		Assert.assertTrue(getStudentService().isStudentHealthPresent(teststudent.getId()));
		
		Assert.assertTrue(!getStudentService().getAllStudentHealths().isEmpty());
		
		Assert.assertNotNull(getStudentService().getStudentHealth(teststudent.getId()));
		
		getStudentService().deleteStudentHealth(teststudent.getId());
		
		Assert.assertEquals(0, getStudentService().getAllStudentHealths().size());

	}
	
	
	 private StudentService getStudentService() {
		 
		 return new StudentService();
	 
	 }
	 
	 private IdentityService getIdentityService() {
		 
		 return new IdentityService();
	 
	 }
	 
	 private SiteService getSiteService() {
		 
		 return new SiteService(Site.class);
	 
	 }


}
