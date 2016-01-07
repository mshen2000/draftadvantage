/**
 * 
 */
package com.nya.sms.test;

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
import com.nya.sms.dataservices.Authorization;
import com.nya.sms.dataservices.IdentityService;
import com.nya.sms.dataservices.SiteService;
import com.nya.sms.dataservices.StudentService;
import com.nya.sms.entities.Role;
import com.nya.sms.entities.Site;
import com.nya.sms.entities.StudentGroup;
import com.nya.sms.entities.User;

/**
 * @author Michael
 *
 */
public class TestAuthorization {
	
    private final LocalServiceTestHelper helper =
            new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());
    
	private Calendar c;
	private Date yesterday;
	private Date tomorrow;
	private DateFormat dateFormatter;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		
		helper.setUp();
		
		ObjectifyService.register(User.class);
		ObjectifyService.register(Role.class);
		ObjectifyService.register(Site.class);
		ObjectifyService.register(StudentGroup.class);

		c= Calendar.getInstance();
		c.setTime(new Date(java.lang.System.currentTimeMillis()));
		c.add(Calendar.DAY_OF_MONTH, -1);
		yesterday=c.getTime();
		c.add(Calendar.DAY_OF_MONTH, 2);
		tomorrow=c.getTime();
		
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
	public void testAuthorization() {
		
		List<String> levels = getIdentityService().getAccessLevels();
		
		User usr1 = new User("test1","test1");
		usr1.setFirstname("Test");
		usr1.setLastname("One");
		
		User usr2 = new User("test2","test2");
		usr2.setFirstname("Test");
		usr2.setLastname("Two");
		
		getIdentityService().saveUser(usr1,usr1.getUsername());
		getIdentityService().saveUser(usr2,usr1.getUsername());
		
		User usr1_r = getIdentityService().getUser(usr1.getUsername());
		
		Site site1 = new Site("testsite");
		site1.setDescription("site description");
		Long saveditemid1 = getSiteService().save(site1, usr1.getUsername());
		
		Role r1 = new Role("role1","role1");
		
		r1.setAccess_app_admin(true);
		r1.setAccess_grades(levels.get(2));
		r1.setSite(site1.getName());

		getIdentityService().saveRole(r1,usr1.getUsername());

		getIdentityService().createMembership("test1", "role1");
		
		Authorization auth = new Authorization(usr1_r);
		
		Assert.assertTrue(auth.getAccess_app_admin().equals(true));
		Assert.assertTrue(auth.getAccess_app_points().equals(false));
		Assert.assertTrue(auth.getAccess_app_student().equals(false));
		
		Assert.assertTrue(auth.getAccess_grades().equals(levels.get(2)));
		Assert.assertTrue(auth.getAccess_points().equals(levels.get(0)));
		Assert.assertTrue(auth.getAccess_test().equals(levels.get(0)));
		
		Assert.assertFalse(auth.getSGLeaderAuth());
		
		Assert.assertTrue(auth.getSite().equals(site1.getName()));
		
		r1.setAccess_points(levels.get(2));
		r1.setAccess_program(levels.get(2));
		r1.setAccess_schedule(levels.get(2));
		r1.setAccess_test(levels.get(2));
		r1.setAccess_notes(levels.get(2));
		
		getIdentityService().saveRole(r1,usr1.getUsername());
		
		Authorization auth2 = new Authorization(usr1_r);
		
		Assert.assertTrue(auth2.getSGLeaderAuth());

	}

	
	
	@Test
	public void testGetAccessLevels() {

		Assert.assertTrue(getIdentityService().getAccessLevels().get(0).equals("1-No Access"));
		Assert.assertTrue(getIdentityService().getAccessLevels().get(1).equals("2-Query"));
		Assert.assertTrue(getIdentityService().getAccessLevels().get(2).equals("3-Create/Update"));
		

	}

	
	
	 private IdentityService getIdentityService() {
		 
		 return new IdentityService();
	 
	 }
	 
	 private StudentService getStudentService() {
		 
		 return new StudentService();
	 
	 }
	 
	 private SiteService getSiteService() {
		 
		 return new SiteService(Site.class);
	 
	 }


}
