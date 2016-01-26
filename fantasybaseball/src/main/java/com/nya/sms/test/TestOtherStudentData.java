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
import com.nya.sms.dataservices.NoteService;
import com.nya.sms.dataservices.PointsService;
import com.nya.sms.dataservices.ProgramScoreService;
import com.nya.sms.dataservices.SchoolGradesService;
import com.nya.sms.dataservices.SchoolScheduleService;
import com.nya.sms.dataservices.SiteService;
import com.nya.sms.dataservices.StudentService;
import com.nya.sms.dataservices.TestScoreInternalService;
import com.nya.sms.dataservices.TestScoreService;
import com.nya.sms.dataservices.TestScoreStandardService;
import com.nya.sms.entities.BaseEntity;
import com.nya.sms.entities.Note;
import com.nya.sms.entities.Points;
import com.nya.sms.entities.ProgramScore;
import com.nya.sms.entities.ProgramScoreCaughtYa;
import com.nya.sms.entities.ProgramScoreEnglish;
import com.nya.sms.entities.ProgramScoreMath;
import com.nya.sms.entities.ProgramScoreNYAOnlineAssess;
import com.nya.sms.entities.ProgramScoreNYAOnlineMath;
import com.nya.sms.entities.ProgramScoreSixTraits;
import com.nya.sms.entities.Role;
import com.nya.sms.entities.SchoolGrades;
import com.nya.sms.entities.SchoolSchedule;
import com.nya.sms.entities.Site;
import com.nya.sms.entities.Student;
import com.nya.sms.entities.StudentGroup;
import com.nya.sms.entities.StudentHealth;
import com.nya.sms.entities.TestScore;
import com.nya.sms.entities.TestScoreInternal;
import com.nya.sms.entities.TestScoreStandard;
import com.nya.sms.entities.User;

/**
 * @author Michael
 *
 */
public class TestOtherStudentData {
	
    private final LocalServiceTestHelper helper =
            new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());
    
	private Calendar c;
	private Date yesterday;
	private Date tomorrow;
	private Date tenyearsago;
	private DateFormat dateFormatter;
	
	private Closeable closeable;
	
	User usr1;
	User usr2;
	User usr1_r;
	Site site1;
	Site site2;
	Role role_allsites;
	Role role_allstudents;
	Role role_mysg;
	Role role_nostudents;
	Role role_allstudents_r;
	Role role_mysg_r;
	Role role_nostudents_r;
	Role role_allsites_r;
	Authorization auth_allstudents;
	Authorization auth_mysg;
	Authorization auth_nostudents;
	Authorization auth_allsites;
	Student student1;
	Student student2;
	Student student3;
	Long student1_id;
	Long student2_id;
	Long student3_id;
	StudentGroup sg1;
	
	

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		
		System.out.println("Running setup");
		
		helper.setUp();

		ObjectifyService.register(Student.class);
		ObjectifyService.register(StudentGroup.class);
		ObjectifyService.register(StudentHealth.class);
		
		ObjectifyService.register(Note.class);
		ObjectifyService.register(Points.class);
		ObjectifyService.register(SchoolGrades.class);
		ObjectifyService.register(SchoolSchedule.class);
		ObjectifyService.register(TestScore.class);
		ObjectifyService.register(TestScoreInternal.class);
		ObjectifyService.register(TestScoreStandard.class);
		ObjectifyService.register(ProgramScoreCaughtYa.class);

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
		
		List<String> levels = getIdentityService().getAccessLevels();
		
		// Create Users
		usr1 = new User("test1","test1");
		usr1.setFirstname("Test");
		usr1.setLastname("One");
		
		usr2 = new User("test2","test2");
		usr2.setFirstname("Test");
		usr2.setLastname("Two");
		
		getIdentityService().saveUser(usr1,usr1.getUsername());
		getIdentityService().saveUser(usr2,usr1.getUsername());
		
		usr1_r = getIdentityService().getUser(usr1.getUsername());
		
		// Create Sites
		site1 = new Site("testsite1");
		site1.setDescription("site description");
		Long savedsite1 = getSiteService().save(site1, usr1.getUsername());
	
		site2 = new Site("testsite2");
		site2.setDescription("site description");
		Long savedsite2 = getSiteService().save(site2, usr1.getUsername());
		
		// Create roles (with site info)
		role_allsites = new Role("role_allsites","role_allsites");
		role_allsites.setAccess_app_admin(true);
		role_allsites.setAccess_grades(levels.get(2));
		role_allsites.setSite(getSiteService().MASTER_SITE);
		role_allsites.setAccess_students(levels.get(2));
		role_allsites.setAccess_program(levels.get(2));
		role_allsites.setStudentdata_access_scope(getIdentityService().ALL_STUDENTS);
		getIdentityService().saveRole(role_allsites,usr1.getUsername());
		
		role_allstudents = new Role("role_allstudents","role_allstudents");
		role_allstudents.setAccess_app_admin(true);
		role_allstudents.setAccess_grades(levels.get(2));
		role_allstudents.setSite(site1.getName());
		role_allstudents.setAccess_students(levels.get(2));
		role_allstudents.setAccess_program(levels.get(2));
		role_allstudents.setStudentdata_access_scope(getIdentityService().ALL_STUDENTS);
		getIdentityService().saveRole(role_allstudents,usr1.getUsername());
		
		role_mysg = new Role("role_mysg","role_mysg");
		role_mysg.setAccess_app_admin(true);
		role_mysg.setAccess_grades(levels.get(2));
		role_mysg.setSite(site1.getName());
		role_mysg.setAccess_students(levels.get(2));
		role_mysg.setAccess_program(levels.get(2));
		role_mysg.setStudentdata_access_scope(getIdentityService().MY_STUDENT_GROUP);
		getIdentityService().saveRole(role_mysg,usr1.getUsername());
		
		role_nostudents = new Role("role_nostudents","role_nostudents");
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
		role_allstudents_r = getIdentityService().getRole("role_allstudents");
		role_mysg_r = getIdentityService().getRole("role_mysg");
		role_nostudents_r = getIdentityService().getRole("role_nostudents");
		role_allsites_r = getIdentityService().getRole("role_allsites");
		
		// Create Authorizations from roles
		auth_allstudents = new Authorization(usr1_r, role_allstudents_r);
		auth_mysg = new Authorization(usr1_r, role_mysg_r);
		auth_nostudents = new Authorization(usr1_r, role_nostudents_r);
		auth_allsites = new Authorization(usr1_r, role_allsites_r);
		
		// Create students
		student1 = new Student("joe","blow");
		student1.setDob(tenyearsago);
		student1.setSite(site1.getName());
		
		student2 = new Student("jeff","smith");
		student2.setMiddlename("mid");
		student2.setDob(tenyearsago);
		student2.setSite(site1.getName());
		
		student3 = new Student("Jane","Doe");
		student3.setDob(tenyearsago);
		student3.setSite(site2.getName());

		student1_id = getStudentService().saveStudent(student1,usr1.getUsername());
		student2_id = getStudentService().saveStudent(student2,usr1.getUsername());
		student3_id = getStudentService().saveStudent(student3,usr1.getUsername());
		
		// Create student group set leader as user1
		sg1 = new StudentGroup("StudentGroup1");
		sg1.setLeader(Ref.create(usr1_r));
		getStudentService().saveGroup(sg1,usr1.getUsername());
		
		// Add one student to group
		getStudentService().createMembership(student1_id, sg1.getName());
		
		
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
	public void testNote() {
		
		Student s2r = getStudentService().getStudent("jeff","mid","smith",tenyearsago);
		
		Note score = new Note(s2r.getId());
		score.setNotedate(new Date(java.lang.System.currentTimeMillis()));
		score.setNote("test1");
		
		Long saveditemid = getNoteService().save(score, usr1.getUsername());
		
		Assert.assertEquals(1, getNoteService().getAll().size());
		
		Note ritem = getNoteService().get(saveditemid);
		
		Assert.assertEquals("test1", ritem.getNote());
		Assert.assertEquals("Test One", ritem.getAuthor());
		
		getNoteService().delete(ritem.getId());
		
		Assert.assertEquals(0, getNoteService().getAll().size());

	}
	
	
	@Test
	public void testPoints() {
		
		Student s1r = getStudentService().getStudent("jeff","mid","smith",tenyearsago);
		
		Points points = new Points(s1r.getId());
		points.setType("test");
		points.setPoints(10);
		
		Long saveditemid = getPointsService().save(points, usr1.getUsername());
		
		Assert.assertEquals(1, getPointsService().getAll().size());
		
		Points ritem = getPointsService().get(saveditemid);
		
		Assert.assertEquals("test", ritem.getType());
		
		getPointsService().delete(ritem.getId());
		
		Assert.assertEquals(0, getPointsService().getAll().size());
		
		
		//******************************************************
		// test isChildToProgramScore and getParentProgramScore
		//******************************************************
		
		// create program score
		ProgramScoreCaughtYa score = new ProgramScoreCaughtYa(s1r.getId());
		score.setScoredate(new Date(java.lang.System.currentTimeMillis()));
		score.setNumber("test1");
		score.setScore("8/10");
		// score.setPossiblescore(10);
		score.setPoints(10);
		Long saveditemidB = getProgramScoreCaughtYaService().save(score, usr1.getUsername());
		
		// get the points object
		Points points2 = getProgramScoreCaughtYaService().get(saveditemidB).getPointsobject().get();
		
		// verify it has a program score parent
		Assert.assertTrue(getPointsService().isChildToProgramScore(points2.getId()));
		
		// get the program score
		ProgramScore score_ret = getPointsService().getParentProgramScore(points2.getId());
		
		Assert.assertTrue(score_ret.getPoints() == 10);
		
		
		//******************************************************
		// test getAuthorizedPointsList
		//******************************************************
		
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
		
		Long saveditemidD = getProgramScoreCaughtYaService().save(score1, usr1.getUsername());
		Long saveditemidE = getProgramScoreCaughtYaService().save(score2, usr1.getUsername());
		Long saveditemidF = getProgramScoreCaughtYaService().save(score3, usr1.getUsername());
		
		// Test that getAuthorizedPointsList returns the right number of points entries
		Assert.assertTrue(getPointsService().getAuthorizedPointsList(auth_allsites).size() == 4);
		Assert.assertTrue(getPointsService().getAuthorizedPointsList(auth_allstudents).size() == 3);
		Assert.assertTrue(getPointsService().getAuthorizedPointsList(auth_mysg).size() == 1);
		Assert.assertTrue(getPointsService().getAuthorizedPointsList(auth_nostudents).size() == 0);

	}
	
	
	@Test
	public void testSchoolGrades() {
		
		System.out.println("Test Grades");
		
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
		
		SchoolGrades score = new SchoolGrades(s1r.getId());
		score.setClasstype("test1");
		
		Long saveditemid = getSchoolGradesService().save(score, usr1.getUsername());
		
		Assert.assertEquals(1, getSchoolGradesService().getAll().size());
		
		SchoolGrades ritem = getSchoolGradesService().get(saveditemid);
		
		Assert.assertEquals("test1", ritem.getClasstype());
		
		getSchoolGradesService().delete(ritem.getId());
		
		Assert.assertEquals(0, getSchoolGradesService().getAll().size());

	}
	
	
	@Test
	public void testSchoolSchedule() {
		
		System.out.println("Test Schedule");
		
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
		
		SchoolSchedule score = new SchoolSchedule(s1r.getId());
		score.setPeriod("test1");
		
		Long saveditemid = getSchoolScheduleService().save(score, usr1.getUsername());
		
		Assert.assertEquals(1, getSchoolScheduleService().getAll().size());
		
		SchoolSchedule ritem = getSchoolScheduleService().get(saveditemid);
		
		Assert.assertEquals("test1", ritem.getPeriod());
		
		getSchoolScheduleService().delete(ritem.getId());
		
		Assert.assertEquals(0, getSchoolScheduleService().getAll().size());

	}
	
	@Test
	public void testTest() {
		
		System.out.println("Test Internal");
		
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
		
		TestScoreInternal score = new TestScoreInternal(s1r.getId());
		score.setScoredate(new Date(java.lang.System.currentTimeMillis()));
		score.setSubject("test1");
		score.setScore("90/100");

		
		Long saveditemid = getTestScoreInternalService().save(score, usr1.getUsername());
		
		Assert.assertEquals(1, getTestScoreInternalService().getAll().size());
		
		TestScoreInternal ritem = getTestScoreInternalService().get(saveditemid);
		
		Assert.assertEquals("test1", ritem.getSubject());
		
		getTestScoreInternalService().delete(ritem.getId());
		
		Assert.assertEquals(0, getTestScoreInternalService().getAll().size());

	}
	
	
/*	@Test
	public void testTestInternal() {
		
		System.out.println("Test Internal");
		
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
		
		TestScoreInternal score = new TestScoreInternal(s1r.getId());
		score.setScoredate(new Date(java.lang.System.currentTimeMillis()));
		score.setSubject("test1");
		score.setScore("90/100");

		
		Long saveditemid = getTestInternalService().save(score, usr1.getUsername());
		
		Assert.assertEquals(1, getTestInternalService().getAll().size());
		
		TestScoreInternal ritem = getTestInternalService().get(saveditemid);
		
		Assert.assertEquals("test1", ritem.getSubject());
		
		getTestInternalService().delete(ritem.getId());
		
		Assert.assertEquals(0, getTestInternalService().getAll().size());

	}
	
	
	@Test
	public void testTestStandard() {
		
		System.out.println("Test Standard");
		
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
		
		TestScoreStandard score = new TestScoreStandard(s1r.getId());
		score.setScoredate(new Date(java.lang.System.currentTimeMillis()));
		score.setTitle("test1");
		score.setScore("200/250");

		
		Long saveditemid = getTestStandardService().save(score, usr1.getUsername());
		
		Assert.assertEquals(1, getTestStandardService().getAll().size());
		
		TestScoreStandard ritem = getTestStandardService().get(saveditemid);
		
		Assert.assertEquals("test1", ritem.getTitle());
		
		getTestStandardService().delete(ritem.getId());
		
		Assert.assertEquals(0, getTestStandardService().getAll().size());

	}
	
	*/
	

	
	
	 private StudentService getStudentService() {
		 
		 return new StudentService();
	 
	 }
	 
	 private IdentityService getIdentityService() {
		 
		 return new IdentityService();
	 
	 }
	 
	 private SiteService getSiteService() {
		 
		 return new SiteService(Site.class);
	 }

	 
	 private NoteService getNoteService() {
		 
		 return new NoteService(Note.class);
	 }
	 
	 private PointsService getPointsService() {
		 
		 return new PointsService(Points.class);
	 }
	 
	 private SchoolGradesService getSchoolGradesService() {
		 
		 return new SchoolGradesService(SchoolGrades.class);
	 }
	 
	 private SchoolScheduleService getSchoolScheduleService() {
		 
		 return new SchoolScheduleService(SchoolSchedule.class);
	 }
	 
//	 private TestScoreInternalService getTestInternalService() {
//		 
//		 return new TestScoreInternalService(TestScoreInternal.class);
//	 }
//	 
//	 private TestScoreStandardService getTestStandardService() {
//		 
//		 return new TestScoreStandardService(TestScoreStandard.class);
//	 }
	 
	 private ProgramScoreService<ProgramScoreCaughtYa> getProgramScoreCaughtYaService() {
		 
		 return new ProgramScoreService<ProgramScoreCaughtYa>(ProgramScoreCaughtYa.class);
	 }
	 
	 private TestScoreService<TestScoreInternal> getTestScoreInternalService() {
		 
		 return new TestScoreService<TestScoreInternal>(TestScoreInternal.class);
	 }


}
