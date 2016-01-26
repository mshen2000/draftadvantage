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
import com.nya.sms.dataservices.AbstractDataServiceImpl;
import com.nya.sms.dataservices.Authorization;
import com.nya.sms.dataservices.IdentityService;
import com.nya.sms.dataservices.PointsService;
import com.nya.sms.dataservices.ProgramScoreService;
import com.nya.sms.dataservices.SiteService;
import com.nya.sms.dataservices.StudentService;
import com.nya.sms.entities.BaseEntity;
import com.nya.sms.entities.Points;
import com.nya.sms.entities.ProgramScore;
import com.nya.sms.entities.ProgramScoreCaughtYa;
import com.nya.sms.entities.ProgramScoreEnglish;
import com.nya.sms.entities.ProgramScoreMath;
import com.nya.sms.entities.ProgramScoreNYAOnlineAssess;
import com.nya.sms.entities.ProgramScoreNYAOnlineMath;
import com.nya.sms.entities.ProgramScoreSixTraits;
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
public class TestProgramScores {
	
    private final LocalServiceTestHelper helper =
            new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());
    
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

		ObjectifyService.register(Student.class);
		ObjectifyService.register(StudentGroup.class);
		ObjectifyService.register(StudentHealth.class);
		ObjectifyService.register(ProgramScore.class);
		ObjectifyService.register(ProgramScoreCaughtYa.class);
		ObjectifyService.register(ProgramScoreEnglish.class);
		ObjectifyService.register(ProgramScoreMath.class);
		ObjectifyService.register(ProgramScoreNYAOnlineAssess.class);
		ObjectifyService.register(ProgramScoreNYAOnlineMath.class);
		ObjectifyService.register(ProgramScoreSixTraits.class);
		
		ObjectifyService.register(Points.class);

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
	public void testProgramScoreCaughtYa() {
		
		// test savescore, getallscores, deletescore, getscore
		User usr1 = new User("test1","test1");
		usr1.setFirstname("Test");
		usr1.setLastname("One");
		
		getIdentityService().saveUser(usr1,usr1.getUsername());
		
		Student student1 = new Student("jeff","smith");
		student1.setMiddlename("mid");
		student1.setDob(tenyearsago);
		
		getStudentService().saveStudent(student1,usr1.getUsername());
		
		Student s1r = getStudentService().getStudent("jeff","mid","smith",tenyearsago);
		
		ProgramScoreCaughtYa score = new ProgramScoreCaughtYa(s1r.getId());
		score.setScoredate(new Date(java.lang.System.currentTimeMillis()));
		score.setNumber("test1");
		score.setScore("8/10");
		// score.setPossiblescore(10);
		score.setPoints(10);
		
		Long saveditemid = getProgramScoreCaughtYaService().save(score, usr1.getUsername());
		
		Assert.assertEquals(1, getProgramScoreCaughtYaService().getAll().size());
		
		ProgramScoreCaughtYa ritem = getProgramScoreCaughtYaService().get(saveditemid);
		
		Assert.assertEquals("test1", ritem.getNumber());
		// Assert.assertEquals(10, ritem.getPossiblescore());
		Assert.assertEquals(0.80, ritem.getScorepercent(), 0.01);
		
		getProgramScoreCaughtYaService().delete(ritem.getId());
		
		Assert.assertEquals(0, getProgramScoreCaughtYaService().getAll().size());

	}
	
	
	@Test
	public void testProgramScoreEnglish() {
		
		// test savescore, getallscores, deletescore, getscore
		User usr1 = new User("test1","test1");
		usr1.setFirstname("Test");
		usr1.setLastname("One");
		
		getIdentityService().saveUser(usr1,usr1.getUsername());
		
		Student student1 = new Student("jeff","smith");
		student1.setMiddlename("mid");
		student1.setDob(tenyearsago);
		
		getStudentService().saveStudent(student1,usr1.getUsername());
		
		Student s1r = getStudentService().getStudent("jeff","mid","smith",tenyearsago);
		
		ProgramScoreEnglish score = new ProgramScoreEnglish(s1r.getId());
		score.setScoredate(new Date(java.lang.System.currentTimeMillis()));
		score.setNumber("test1");
		score.setScore("8/10");
		// score.setPossiblescore(10);
		score.setPoints(10);
		
		Long saveditemid = getProgramScoreEnglishService().save(score, usr1.getUsername());
		
		Assert.assertEquals(1, getProgramScoreEnglishService().getAll().size());
		
		ProgramScoreEnglish ritem = getProgramScoreEnglishService().get(saveditemid);
		
		Assert.assertEquals("test1", ritem.getNumber());
		// Assert.assertEquals(10, ritem.getPossiblescore());
		Assert.assertEquals(0.80, ritem.getScorepercent(), 0.01);
		
		getProgramScoreEnglishService().delete(ritem.getId());
		
		Assert.assertEquals(0, getProgramScoreEnglishService().getAll().size());

	}
	
	
	@Test
	public void testProgramScoreMath() {
		
		// test savescore, getallscores, deletescore, getscore
		User usr1 = new User("test1","test1");
		usr1.setFirstname("Test");
		usr1.setLastname("One");
		
		getIdentityService().saveUser(usr1,usr1.getUsername());
		
		Student student1 = new Student("jeff","smith");
		student1.setMiddlename("mid");
		student1.setDob(tenyearsago);
		
		getStudentService().saveStudent(student1,usr1.getUsername());
		
		Student s1r = getStudentService().getStudent("jeff","mid","smith",tenyearsago);
		
		ProgramScoreMath score = new ProgramScoreMath(s1r.getId());
		score.setScoredate(new Date(java.lang.System.currentTimeMillis()));
		score.setNumber("test1");
		score.setScore("8/10");
		// score.setPossiblescore(10);
		score.setPoints(10);
		
		Long saveditemid = getProgramScoreMathService().save(score, usr1.getUsername());
		
		Assert.assertEquals(1, getProgramScoreMathService().getAll().size());
		
		ProgramScoreMath ritem = getProgramScoreMathService().get(saveditemid);
		
		Assert.assertEquals("test1", ritem.getNumber());
		// Assert.assertEquals(10, ritem.getPossiblescore());
		Assert.assertEquals(0.80, ritem.getScorepercent(), 0.01);
		
		getProgramScoreMathService().delete(ritem.getId());
		
		Assert.assertEquals(0, getProgramScoreMathService().getAll().size());

	}
	
	
	@Test
	public void testProgramScoreNYAOnlineAssess() {
		
		// test savescore, getallscores, deletescore, getscore
		User usr1 = new User("test1","test1");
		usr1.setFirstname("Test");
		usr1.setLastname("One");
		
		getIdentityService().saveUser(usr1,usr1.getUsername());
		
		Student student1 = new Student("jeff","smith");
		student1.setMiddlename("mid");
		student1.setDob(tenyearsago);
		
		getStudentService().saveStudent(student1,usr1.getUsername());
		
		Student s1r = getStudentService().getStudent("jeff","mid","smith",tenyearsago);
		
		ProgramScoreNYAOnlineAssess score = new ProgramScoreNYAOnlineAssess(s1r.getId());
		score.setScoredate(new Date(java.lang.System.currentTimeMillis()));
		score.setNotes("test1");
		score.setAddition_score("8/10");
		score.setSubtraction_score("8/10");
		score.setMultiplication_score("8/10");
		score.setDivision_score("8/10");
		// score.setAddition_scorepossible(10);
		score.setPoints(10);
		
		Long saveditemid = getProgramScoreNYAOnlineAssessService().save(score, usr1.getUsername());
		
		Assert.assertEquals(1, getProgramScoreNYAOnlineAssessService().getAll().size());
		
		ProgramScoreNYAOnlineAssess ritem = getProgramScoreNYAOnlineAssessService().get(saveditemid);
		
		Assert.assertEquals("test1", ritem.getNotes());
		// Assert.assertEquals(10, ritem.getAddition_scorepossible());
		Assert.assertEquals(0.80, ritem.getAddition_scorepercent(), 0.01);
		
		getProgramScoreNYAOnlineAssessService().delete(ritem.getId());
		
		Assert.assertEquals(0, getProgramScoreNYAOnlineAssessService().getAll().size());

	}
	
	
	@Test
	public void testProgramScoreNYAOnlineMath() {
		
		// test savescore, getallscores, deletescore, getscore
		User usr1 = new User("test1","test1");
		usr1.setFirstname("Test");
		usr1.setLastname("One");
		
		getIdentityService().saveUser(usr1,usr1.getUsername());
		
		Student student1 = new Student("jeff","smith");
		student1.setMiddlename("mid");
		student1.setDob(tenyearsago);
		
		getStudentService().saveStudent(student1,usr1.getUsername());
		
		Student s1r = getStudentService().getStudent("jeff","mid","smith",tenyearsago);
		
		ProgramScoreNYAOnlineMath score = new ProgramScoreNYAOnlineMath(s1r.getId());
		score.setScoredate(new Date(java.lang.System.currentTimeMillis()));
		score.setLevel("test1");
		score.setScore("8/10");
		// score.setPossiblescore(10);
		score.setPoints(10);
		
		Long saveditemid = getProgramScoreNYAOnlineMathService().save(score, usr1.getUsername());
		
		Assert.assertEquals(1, getProgramScoreNYAOnlineMathService().getAll().size());
		
		ProgramScoreNYAOnlineMath ritem = getProgramScoreNYAOnlineMathService().get(saveditemid);
		
		Assert.assertEquals("test1", ritem.getLevel());
		// Assert.assertEquals(10, ritem.getPossiblescore());
		Assert.assertEquals(0.80, ritem.getScorepercent(), 0.01);
		
		getProgramScoreNYAOnlineMathService().delete(ritem.getId());
		
		Assert.assertEquals(0, getProgramScoreNYAOnlineMathService().getAll().size());

	}
	
	
	@Test
	public void testProgramScoreSixTraits() {
		
		// test savescore, getallscores, deletescore, getscore
		User usr1 = new User("test1","test1");
		usr1.setFirstname("Test");
		usr1.setLastname("One");
		
		getIdentityService().saveUser(usr1,usr1.getUsername());
		
		Student student1 = new Student("jeff","smith");
		student1.setMiddlename("mid");
		student1.setDob(tenyearsago);
		
		getStudentService().saveStudent(student1,usr1.getUsername());
		
		Student s1r = getStudentService().getStudent("jeff","mid","smith",tenyearsago);
		
		ProgramScoreSixTraits score = new ProgramScoreSixTraits(s1r.getId());
		score.setScoredate(new Date(java.lang.System.currentTimeMillis()));
		score.setTitle("test1");
		score.setScore_i("8/10");
		score.setScore_org("8/10");
		score.setScore_v("8/10");
		score.setScore_wc("8/10");
		score.setScore_sf("8/10");
		score.setScore_c("8/10");
		
		// score.setPossiblescore_i(10);
		score.setPoints(10);
		
		Long saveditemid = getProgramScoreSixTraitsService().save(score, usr1.getUsername());
		
		Assert.assertEquals(1, getProgramScoreSixTraitsService().getAll().size());
		
		ProgramScoreSixTraits ritem = getProgramScoreSixTraitsService().get(saveditemid);
		
		Assert.assertEquals("test1", ritem.getTitle());
		// Assert.assertEquals(10, ritem.getPossiblescore_i());
		Assert.assertEquals(0.80, ritem.getPercentscore_i(), 0.01);
		
		getProgramScoreSixTraitsService().delete(ritem.getId());
		
		Assert.assertEquals(0, getProgramScoreSixTraitsService().getAll().size());

	}
	
	
	// Test AuthorizedProgramScoreList using ProgramScoreCaughtYa
	@Test
	public void testAuthorizedProgramScoreList() {
		
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
		role_allsites.setAccess_program(levels.get(2));
		role_allsites.setStudentdata_access_scope(getIdentityService().ALL_STUDENTS);
		getIdentityService().saveRole(role_allsites,usr1.getUsername());
		
		Role role_allstudents = new Role("role_allstudents","role_allstudents");
		role_allstudents.setAccess_app_admin(true);
		role_allstudents.setAccess_grades(levels.get(2));
		role_allstudents.setSite(site1.getName());
		role_allstudents.setAccess_students(levels.get(2));
		role_allstudents.setAccess_program(levels.get(2));
		role_allstudents.setStudentdata_access_scope(getIdentityService().ALL_STUDENTS);
		getIdentityService().saveRole(role_allstudents,usr1.getUsername());
		
		Role role_mysg = new Role("role_mysg","role_mysg");
		role_mysg.setAccess_app_admin(true);
		role_mysg.setAccess_grades(levels.get(2));
		role_mysg.setSite(site1.getName());
		role_mysg.setAccess_students(levels.get(2));
		role_mysg.setAccess_program(levels.get(2));
		role_mysg.setStudentdata_access_scope(getIdentityService().MY_STUDENT_GROUP);
		getIdentityService().saveRole(role_mysg,usr1.getUsername());
		
		Role role_nostudents = new Role("role_nostudents","role_nostudents");
		role_nostudents.setAccess_app_admin(true);
		role_nostudents.setAccess_grades(levels.get(2));
		role_nostudents.setSite(site1.getName());
		role_nostudents.setAccess_students(levels.get(0));
		role_nostudents.setAccess_program(levels.get(0));
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
		
		// Create program scores
		ProgramScoreCaughtYa score1 = new ProgramScoreCaughtYa(student1_id);
		score1.setScoredate(new Date(java.lang.System.currentTimeMillis()));
		score1.setNumber("test1");
		score1.setScore("8/10");
		// score1.setPossiblescore(10);
		score1.setPoints(8);
		
		ProgramScoreCaughtYa score2 = new ProgramScoreCaughtYa(student2_id);
		score2.setScoredate(new Date(java.lang.System.currentTimeMillis()));
		score2.setNumber("test1");
		score2.setScore("8/10");
		// score2.setPossiblescore(10);
		score2.setPoints(9);
		
		ProgramScoreCaughtYa score3 = new ProgramScoreCaughtYa(student3_id);
		score3.setScoredate(new Date(java.lang.System.currentTimeMillis()));
		score3.setNumber("test1");
		score3.setScore("8/10");
		// score3.setPossiblescore(10);
		score3.setPoints(10);
		
		Long saveditemidA = getProgramScoreCaughtYaService().save(score1, usr1.getUsername());
		Long saveditemidB = getProgramScoreCaughtYaService().save(score2, usr1.getUsername());
		Long saveditemidC = getProgramScoreCaughtYaService().save(score3, usr1.getUsername());
		
		// Test that getAuthorizedProgramScoreList returns the right number of scores
		Assert.assertTrue(getProgramScoreCaughtYaService().getAuthorizedProgramScoreList(auth_allsites).size() == 3);
		Assert.assertTrue(getProgramScoreCaughtYaService().getAuthorizedProgramScoreList(auth_allstudents).size() == 2);
		Assert.assertTrue(getProgramScoreCaughtYaService().getAuthorizedProgramScoreList(auth_mysg).size() == 1);
		Assert.assertTrue(getProgramScoreCaughtYaService().getAuthorizedProgramScoreList(auth_nostudents).size() == 0);
		
		// Test that all points objects were automatically created 
		Assert.assertTrue(getPointsService().getAll().size() == 3);
		
		// Get score3
		ProgramScoreCaughtYa score3_ret = getProgramScoreCaughtYaService().get(saveditemidC);
		
		// Verify score3 has a linked points object
		Assert.assertTrue(score3_ret.getPointsobject() != null);
		
		// Verify score3 points object has the right points
		Assert.assertTrue(score3_ret.getPointsobject().get().getPoints() == 10);
		
		// Delete score3
		getProgramScoreCaughtYaService().delete(saveditemidC);
		
		// Verify there are only 2 points objects now
		Assert.assertTrue(getPointsService().getAll().size() == 2);
		
		// Update score2 points to be 0
		ProgramScoreCaughtYa score2_ret = getProgramScoreCaughtYaService().get(saveditemidB);
		score2_ret.setPoints(0);
		Long saveditemidD = getProgramScoreCaughtYaService().save(score2_ret, usr1.getUsername());
		
		// Verify there is only 1 points object now
		Assert.assertTrue(getPointsService().getAll().size() == 1);

		// Verify there is no linked points object for score2
		ProgramScoreCaughtYa score2_retB = getProgramScoreCaughtYaService().get(saveditemidD);
		Assert.assertTrue(score2_retB.getPointsobject() == null);
		Assert.assertFalse(getProgramScoreCaughtYaService().isPointsObjectLinked(score2_retB));
	
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
	 
	 private PointsService getPointsService() {
		 
		 return new PointsService(Points.class);
	 
	 }

	 
	 
	 private ProgramScoreService<ProgramScoreCaughtYa> getProgramScoreCaughtYaService() {
		 
		 return new ProgramScoreService<ProgramScoreCaughtYa>(ProgramScoreCaughtYa.class);
	 }
	 
	 private ProgramScoreService<ProgramScoreEnglish> getProgramScoreEnglishService() {
		 
		 return new ProgramScoreService<ProgramScoreEnglish>(ProgramScoreEnglish.class);
	 }
	 
	 private ProgramScoreService<ProgramScoreMath> getProgramScoreMathService() {
		 
		 return new ProgramScoreService<ProgramScoreMath>(ProgramScoreMath.class);
	 }
	 
	 private ProgramScoreService<ProgramScoreNYAOnlineAssess> getProgramScoreNYAOnlineAssessService() {
		 
		 return new ProgramScoreService<ProgramScoreNYAOnlineAssess>(ProgramScoreNYAOnlineAssess.class);
	 }
	 
	 private ProgramScoreService<ProgramScoreNYAOnlineMath> getProgramScoreNYAOnlineMathService() {
		 
		 return new ProgramScoreService<ProgramScoreNYAOnlineMath>(ProgramScoreNYAOnlineMath.class);
	 }
	 
	 private ProgramScoreService<ProgramScoreSixTraits> getProgramScoreSixTraitsService() {
		 
		 return new ProgramScoreService<ProgramScoreSixTraits>(ProgramScoreSixTraits.class);
	 }


}
