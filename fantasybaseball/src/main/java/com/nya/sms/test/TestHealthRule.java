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
import com.nya.sms.dataservices.AbstractDataServiceImpl;
import com.nya.sms.dataservices.Authorization;
import com.nya.sms.dataservices.HealthRuleService;
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
import com.nya.sms.entities.HealthRule;
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
import com.nya.sms.entities.TestScoreInternal;
import com.nya.sms.entities.TestScoreStandard;
import com.nya.sms.entities.User;

/**
 * @author Michael
 *
 */
public class TestHealthRule {
	
    private final LocalServiceTestHelper helper =
            new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());
    
	private Calendar c;
	private Date yesterday;
	private Date tomorrow;
	private Date tenyearsago;
	private DateFormat dateFormatter;
	
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
		
		ObjectifyService.register(User.class);
		ObjectifyService.register(HealthRule.class);

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

		
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		
		helper.tearDown();
		
	}
	

	@Test
	public void testHealthRule() {
		
		// Student s2r = getStudentService().getStudent("jeff","mid","smith",tenyearsago);
		
		HealthRule score = new HealthRule("test rule 1");
		score.setCode("test code");
		score.setDescription("test description");
		score.setDetail("test detail");
		score.setRulename("test name");
		score.setStatus("test status");
		score.setVersionnum(1);
		
		Long saveditemid = getHealthRuleService().save(score, usr1.getUsername());
		
		Assert.assertEquals(1, getHealthRuleService().getAll().size());
		
		HealthRule ritem = getHealthRuleService().get(saveditemid);
		
		Assert.assertEquals("test code", ritem.getCode());
		Assert.assertEquals(1, ritem.getVersionnum().intValue());
		
		getHealthRuleService().delete(ritem.getId());
		
		Assert.assertEquals(0, getHealthRuleService().getAll().size());

	}
	
	 
	 private IdentityService getIdentityService() {
		 
		 return new IdentityService();
	 
	 }
	 
	 
	 private HealthRuleService getHealthRuleService() {
		 
		 return new HealthRuleService(HealthRule.class);
	 }
	 


}
