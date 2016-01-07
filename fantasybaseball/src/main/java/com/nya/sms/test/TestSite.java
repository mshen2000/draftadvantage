package com.nya.sms.test;

import static org.junit.Assert.*;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.googlecode.objectify.ObjectifyService;
import com.nya.sms.dataservices.IdentityService;
import com.nya.sms.dataservices.SiteService;
import com.nya.sms.dataservices.StudentService;
import com.nya.sms.entities.Note;
import com.nya.sms.entities.Role;
import com.nya.sms.entities.Site;
import com.nya.sms.entities.Student;
import com.nya.sms.entities.StudentGroup;
import com.nya.sms.entities.StudentHealth;
import com.nya.sms.entities.User;

public class TestSite {
	
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

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		
		helper.setUp();
		
		ObjectifyService.register(Student.class);
		ObjectifyService.register(StudentGroup.class);
		ObjectifyService.register(StudentHealth.class);
		ObjectifyService.register(User.class);
		ObjectifyService.register(Site.class);
		ObjectifyService.register(Role.class);

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
		
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		
		helper.tearDown();
		
	}

	@Test
	public void testSite() {
		
		// test savescore, getallscores, deletescore, getscore
		User usr1 = new User("test1","test1");
		usr1.setFirstname("Test");
		usr1.setLastname("One");
		
		getIdentityService().saveUser(usr1,usr1.getUsername());
		
		User usrreturn = getIdentityService().getUser(usr1.getUsername());
		
		Role roleinit = new Role("testrole","Test Role");
		getIdentityService().saveRole(roleinit, usr1.getUsername());
		
		Role rolereturn = getIdentityService().getRole(roleinit.getName());

		Student student1 = new Student("jeff","smith");
		student1.setMiddlename("mid");
		student1.setDob(tenyearsago);
		
		getStudentService().saveStudent(student1,usr1.getUsername());
		
		Student studentreturn = getStudentService().getStudent("jeff","mid","smith",tenyearsago);
		
		StudentGroup sginit = new StudentGroup("testgroup");
		getStudentService().saveGroup(sginit, usr1.getUsername());
		
		StudentGroup groupreturn = getStudentService().getGroup(sginit.getName());
		
		Site site = new Site("testsite");
		site.setDescription("site description");
		site.addRole(rolereturn);
		site.addStudent(studentreturn);
		site.addStudentGroup(groupreturn);
		
		Long saveditemid = getSiteService().save(site, usr1.getUsername());
		
		Assert.assertEquals(1, getSiteService().getAll().size());
		
		Site ritem = getSiteService().get(saveditemid);
		
		Assert.assertEquals("testsite", ritem.getName());
		Assert.assertEquals("site description", ritem.getDescription());
		Assert.assertEquals(1, ritem.getRoles().size());
		Assert.assertEquals(1, ritem.getStudentgroups().size());
		Assert.assertEquals(1, ritem.getStudents().size());
		Assert.assertEquals(1, getSiteService().getSiteRoles(ritem.getName()).size());
		Assert.assertEquals(1, getSiteService().getSiteStudents(ritem.getName()).size());
		Assert.assertEquals(1, getSiteService().getSiteStudentGroups(ritem.getName()).size());
		
		ritem.removeRole(rolereturn);
		ritem.removeStudent(studentreturn);
		ritem.removeStudentGroup(groupreturn);
		
		saveditemid = getSiteService().save(ritem, usr1.getUsername());
		
		Site ritem2 = getSiteService().get(saveditemid);
		
		Assert.assertEquals(0, ritem2.getRoles().size());
		Assert.assertEquals(0, ritem2.getStudentgroups().size());
		Assert.assertEquals(0, ritem2.getStudents().size());
		
		getSiteService().delete(ritem.getId());
		
		Assert.assertEquals(0, getSiteService().getAll().size());
		
	}
	
	
	@Test
	public void testAssociateRoletoSite() {
		
		// test savescore, getallscores, deletescore, getscore
		User usr1 = new User("test1","test1");
		usr1.setFirstname("Test");
		usr1.setLastname("One");
		
		getIdentityService().saveUser(usr1,usr1.getUsername());
		
		User usrreturn = getIdentityService().getUser(usr1.getUsername());
		
		Role roleinit = new Role("testrole","Test Role");
		getIdentityService().saveRole(roleinit, usr1.getUsername());
		
		Role rolereturn = getIdentityService().getRole(roleinit.getName());

		Student student1 = new Student("jeff","smith");
		student1.setMiddlename("mid");
		student1.setDob(tenyearsago);
		
		getStudentService().saveStudent(student1,usr1.getUsername());
		
		Student studentreturn = getStudentService().getStudent("jeff","mid","smith",tenyearsago);
		
		StudentGroup sginit = new StudentGroup("testgroup");
		getStudentService().saveGroup(sginit, usr1.getUsername());
		
		StudentGroup groupreturn = getStudentService().getGroup(sginit.getName());
		
		Site site1 = new Site("testsite");
		site1.setDescription("site description");
		Long saveditemid1 = getSiteService().save(site1, usr1.getUsername());
		
		Site site2 = new Site("testsite2");
		site2.setDescription("site description 2");
		Long saveditemid2 = getSiteService().save(site2, usr1.getUsername());
		
		getSiteService().associateRoletoSite(site1.getName(), rolereturn.getName());
		
		Site site1return1 = getSiteService().get(saveditemid1);
		Site site2return1 = getSiteService().get(saveditemid2);
		
		Assert.assertEquals(1, site1return1.getRoles().size());
		Assert.assertEquals(0, site2return1.getRoles().size());

		getSiteService().associateRoletoSite(site2.getName(), rolereturn.getName());
		
		Site site1return2 = getSiteService().get(saveditemid1);
		Site site2return2 = getSiteService().get(saveditemid2);
		
		Assert.assertEquals(0, site1return2.getRoles().size());
		Assert.assertEquals(1, site2return2.getRoles().size());
		
		getSiteService().removeRoleFromSite(site2.getName(), rolereturn.getName());
		
		Site site1return3 = getSiteService().get(saveditemid1);
		Site site2return3 = getSiteService().get(saveditemid2);
		
		Assert.assertEquals(0, site1return3.getRoles().size());
		Assert.assertEquals(0, site2return3.getRoles().size());
		
	}
	
	@Test
	public void testAssociateStudenttoSite() {
		
		// test savescore, getallscores, deletescore, getscore
		User usr1 = new User("test1","test1");
		usr1.setFirstname("Test");
		usr1.setLastname("One");
		
		getIdentityService().saveUser(usr1,usr1.getUsername());
		
		User usrreturn = getIdentityService().getUser(usr1.getUsername());
		
		Role roleinit = new Role("testrole","Test Role");
		getIdentityService().saveRole(roleinit, usr1.getUsername());
		
		Role rolereturn = getIdentityService().getRole(roleinit.getName());

		Student student1 = new Student("jeff","smith");
		student1.setMiddlename("mid");
		student1.setDob(tenyearsago);
		
		getStudentService().saveStudent(student1,usr1.getUsername());
		
		Student studentreturn = getStudentService().getStudent("jeff","mid","smith",tenyearsago);
		
		StudentGroup sginit = new StudentGroup("testgroup");
		getStudentService().saveGroup(sginit, usr1.getUsername());
		
		StudentGroup groupreturn = getStudentService().getGroup(sginit.getName());
		
		Site site1 = new Site("testsite");
		site1.setDescription("site description");
		Long saveditemid1 = getSiteService().save(site1, usr1.getUsername());
		
		Site site2 = new Site("testsite2");
		site2.setDescription("site description 2");
		Long saveditemid2 = getSiteService().save(site2, usr1.getUsername());
		
		getSiteService().associateStudenttoSite(site1.getName(), studentreturn.getId());
		
		Site site1return1 = getSiteService().get(saveditemid1);
		Site site2return1 = getSiteService().get(saveditemid2);
		
		Assert.assertEquals(1, site1return1.getStudents().size());
		Assert.assertEquals(0, site2return1.getStudents().size());

		getSiteService().associateStudenttoSite(site2.getName(), studentreturn.getId());
		
		Site site1return2 = getSiteService().get(saveditemid1);
		Site site2return2 = getSiteService().get(saveditemid2);
		
		Assert.assertEquals(0, site1return2.getStudents().size());
		Assert.assertEquals(1, site2return2.getStudents().size());
		
		getSiteService().removeStudentFromSite(site2.getName(), studentreturn.getId());
		
		Site site1return3 = getSiteService().get(saveditemid1);
		Site site2return3 = getSiteService().get(saveditemid2);
		
		Assert.assertEquals(0, site1return3.getStudents().size());
		Assert.assertEquals(0, site2return3.getStudents().size());
		
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
