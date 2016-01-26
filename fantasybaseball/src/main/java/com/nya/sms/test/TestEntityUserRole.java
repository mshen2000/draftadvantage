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

import org.jose4j.lang.JoseException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.app.endpoints.APIToken;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.VoidWork;
import com.googlecode.objectify.cmd.Query;
import com.googlecode.objectify.util.Closeable;
import com.nya.sms.dataservices.IdentityService;
import com.nya.sms.dataservices.SiteService;
import com.nya.sms.dataservices.StudentService;
import com.nya.sms.entities.BaseEntity;
import com.nya.sms.entities.JKey;
import com.nya.sms.entities.Role;
import com.nya.sms.entities.Site;
import com.nya.sms.entities.StudentGroup;
import com.nya.sms.entities.User;

/**
 * @author Michael
 *
 */
public class TestEntityUserRole {
	
    private final LocalServiceTestHelper helper =
            new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());
    
	private Calendar c;
	private Date yesterday;
	private Date tomorrow;
	private DateFormat dateFormatter;
	
	private Closeable closeable;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		
		helper.setUp();

		ObjectifyService.register(JKey.class);
		ObjectifyService.register(User.class);
		ObjectifyService.register(Role.class);
		ObjectifyService.register(StudentGroup.class);
		ObjectifyService.register(Site.class);

		c= Calendar.getInstance();
		c.setTime(new Date(java.lang.System.currentTimeMillis()));
		c.add(Calendar.DAY_OF_MONTH, -1);
		yesterday=c.getTime();
		c.add(Calendar.DAY_OF_MONTH, 2);
		tomorrow=c.getTime();
		
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
	public void testCreateUser() {
		
		// also test getallusers, getuser, saveuser

		User usr1 = new User("test1","test1");
		usr1.setFirstname("Test");
		usr1.setLastname("One");
		usr1.setExt_id("aaaa");
		usr1.setEmail("test1@test.com");
		
		User usr2 = new User("test2","test2");
		usr2.setFirstname("Test");
		usr2.setLastname("Two");
		usr2.setExt_id("bbbb");
		usr2.setEmail("test2@test.com");
		
		getIdentityService().saveUser(usr1,usr1.getUsername());
		getIdentityService().saveUser(usr2,usr1.getUsername());
		
		Assert.assertTrue(getIdentityService().isUserEmailPresent("test2@test.com"));
		Assert.assertFalse(getIdentityService().isUserEmailPresent("aaaaa"));
		
		Assert.assertEquals(getIdentityService().getUserByEmail("test1@test.com").getUsername(), usr1.getUsername());
		
		Assert.assertEquals(getIdentityService().getUser("test1").getUsername(), usr1.getUsername());
		Assert.assertEquals(getIdentityService().getUser("test2").getUsername(), usr2.getUsername());
		
		Assert.assertEquals(2, getIdentityService().getAllUsers().size());

	}
	
	@Test
	public void testUserModifiedBy() {
		
		User usr1 = new User("test1","test1");
		usr1.setFirstname("Test");
		usr1.setLastname("One");
		
		User usr2 = new User("test2","test2");
		usr2.setFirstname("Test");
		usr2.setLastname("Two");
		
		getIdentityService().saveUser(usr1,usr1.getUsername());
		getIdentityService().saveUser(usr2,usr1.getUsername());
		
		User user1_ret = getIdentityService().getUser("test1");
		User user2_ret = getIdentityService().getUser("test2");

		user2_ret.setCreatedby(usr1.getUsername());
		getIdentityService().saveUser(user2_ret,usr1.getUsername());

		Assert.assertTrue(getIdentityService().getUser("test2").getCreatedon().after(yesterday));
		Assert.assertTrue(getIdentityService().getUser("test2").getCreatedon().before(tomorrow));

	}
	
	@Test
	public void testSGLeaderAvailableUsers() {
		
		List<String> levels = getIdentityService().getAccessLevels();
		
		User usr1 = new User("test1","test1");
		usr1.setFirstname("Test");
		usr1.setLastname("One");
		
		User usr2 = new User("test2","test2");
		usr2.setFirstname("Test");
		usr2.setLastname("Two");
		
		User usr3 = new User("test3","test3");
		usr3.setFirstname("Test");
		usr3.setLastname("three");
		
		getIdentityService().saveUser(usr1,usr1.getUsername());
		getIdentityService().saveUser(usr2,usr1.getUsername());
		getIdentityService().saveUser(usr3,usr1.getUsername());
		
		Role r1 = new Role("test1","test1");
		Role r2 = new Role("test2","test2");
		
		r1.setAccess_grades(levels.get(2));
		r1.setAccess_points(levels.get(2));
		r1.setAccess_program(levels.get(2));
		r1.setAccess_schedule(levels.get(2));
		r1.setAccess_test(levels.get(2));
		r1.setAccess_notes(levels.get(2));
		
		r2.setAccess_grades(levels.get(1));
		r2.setAccess_points(levels.get(2));
		r2.setAccess_program(levels.get(0));
		r2.setAccess_schedule(levels.get(2));
		r2.setAccess_test(levels.get(1));
		r1.setAccess_notes(levels.get(2));
		
		getIdentityService().saveRole(r1,usr1.getUsername());
		getIdentityService().saveRole(r2,usr1.getUsername());
		
		getIdentityService().createMembership(usr1.getUsername(), r1.getName());
		getIdentityService().createMembership(usr2.getUsername(), r2.getName());
		getIdentityService().createMembership(usr3.getUsername(), r1.getName());
		getIdentityService().createMembership(usr3.getUsername(), r2.getName());
		
		User user1_ret = getIdentityService().getUser("test1");
		User user2_ret = getIdentityService().getUser("test2");
		
		StudentGroup sg = new StudentGroup("testsg");
		sg.setLeader(Ref.create(user1_ret));
		getStudentService().saveGroup(sg, "test1");
		
		List<User> leaderusers = getIdentityService().getSGLeaderAvailableUsers();

		Assert.assertEquals(1,leaderusers.size());
		Assert.assertEquals(leaderusers.get(0).getUsername(),"test3");

	}

	
	@Test
	public void testCheckPassword() {
		
		User usr1 = new User("test1","test1");
		usr1.setFirstname("Test");
		usr1.setLastname("One");
		usr1.setEmail("test1@test.com");

		getIdentityService().saveUser(usr1,usr1.getUsername());

		Assert.assertTrue(getIdentityService().checkPassword("test1@test.com", "test1"));
		Assert.assertFalse(getIdentityService().checkPassword("test1@test.com", "aaaa"));

	}
	
	@Test 
	public void testJWT(){
		
		// Test create web key
		getIdentityService().createWebKey();
		
		Assert.assertTrue(ofy().load().type(JKey.class).keys().list().size()==1);
		
		Assert.assertTrue(getIdentityService().getStoredWebKey() != null);
		
		// Create user for JWT test
		User usr1 = new User("test1","test1");
		usr1.setFirstname("Test");
		usr1.setLastname("One");
		usr1.setEmail("test1@test.com");

		getIdentityService().saveUser(usr1,usr1.getUsername());

		Role r1 = new Role("user","role1");
		Role r2 = new Role("admin","role1");
		
		getIdentityService().saveRole(r1,usr1.getUsername());
		getIdentityService().saveRole(r2,usr1.getUsername());
		
		getIdentityService().createMembership("test1", "user");
		
		// Test JWT Create
		APIToken jwt1 = getIdentityService().generateJWT("test1@test.com");
		Assert.assertTrue(jwt1.getToken().length() > 1);
		
		// Test JWT Consume
		Assert.assertTrue(getIdentityService().validateUserJWT(jwt1));
		// Assert.assertFalse(getIdentityService().validateUserJWT(jwt1 + "aaa"));
		Assert.assertFalse(getIdentityService().validateAdminJWT(jwt1));
		
		getIdentityService().createMembership("test1", "admin");
		
		// Test JWT Create
		APIToken jwt2 = getIdentityService().generateJWT("test1@test.com");
		Assert.assertTrue(jwt2.getToken().length() > 1);
		
		// Test JWT Consume
		Assert.assertTrue(getIdentityService().validateUserJWT(jwt2));
		// Assert.assertFalse(getIdentityService().validateUserJWT(jwt2 + "aaa"));
		Assert.assertTrue(getIdentityService().validateAdminJWT(jwt2));
		
		// Test get user from token
		User usrout = getIdentityService().getUserfromToken(jwt2);
		Assert.assertTrue(usrout.getUsername().equals("test1"));
		Assert.assertTrue(usrout.getEmail().equals("test1@test.com"));
	}
	
	@Test
	public void testIsUserPresent() {
		
		// also test deleteUser
		
		User usr1 = new User("test1","test1");
		usr1.setFirstname("Test");
		usr1.setLastname("One");
		usr1.setExt_id("aaaa");
		usr1.setEmail("test1@test.com");

		getIdentityService().saveUser(usr1,usr1.getUsername());

		Assert.assertTrue(getIdentityService().isUserPresent("test1"));
		Assert.assertTrue(getIdentityService().isUserExtIDPresent("aaaa"));
		
		getIdentityService().deleteUser("test1");
		
		Assert.assertFalse(getIdentityService().isUserPresent("test1"));
		Assert.assertFalse(getIdentityService().isUserExtIDPresent("aaaa"));

	}
	
	@Test
	public void testCreateRole() {
		
		// also test getallroles, getrole, saverole
		
		User usr1 = new User("test1","test1");
		usr1.setFirstname("Test");
		usr1.setLastname("One");

		getIdentityService().saveUser(usr1,usr1.getUsername());

		Site site1 = new Site("testsite");
		site1.setDescription("site description");
		Long saveditemid1 = getSiteService().save(site1, usr1.getUsername());

		Role r1 = new Role("test1","test1");
		Role r2 = new Role("test2","test2");
		
		r1.setSite(site1.getName());
		
		getIdentityService().saveRole(r1,usr1.getUsername());
		getIdentityService().saveRole(r2,usr1.getUsername());

		Assert.assertEquals(getIdentityService().getRole("test1").getName(), r1.getName());
		
		Assert.assertEquals(1, getSiteService().getSiteRoles(site1.getName()).size());
		Assert.assertEquals(r1.getName(), getSiteService().getSiteRoles(site1.getName()).get(0).getName());
		
		List<Role> roles = getIdentityService().getAllRoles();
		
		Assert.assertEquals(2, getIdentityService().getAllRoles().size());
		
		getIdentityService().deleteRole(r1.getName());
		
		Assert.assertEquals(1, getIdentityService().getAllRoles().size());
		Assert.assertEquals(0, getSiteService().getSiteRoles(site1.getName()).size());

	}
	
	@Test
	public void testIsRolePresent() {
		
		// also test deleteRole
		User usr1 = new User("test1","test1");
		usr1.setFirstname("Test");
		usr1.setLastname("One");

		getIdentityService().saveUser(usr1,usr1.getUsername());
		
		Role r1 = new Role("test1","test1");

		getIdentityService().saveRole(r1,usr1.getUsername());

		Assert.assertTrue(getIdentityService().isRolePresent("test1"));
		
		getIdentityService().deleteRole("test1");
		
		Assert.assertFalse(getIdentityService().isRolePresent("test1"));

	}
	
	
	@Test
	public void testGetAccessLevels() {

		Assert.assertTrue(getIdentityService().getAccessLevels().get(0).equals("1-No Access"));
		Assert.assertTrue(getIdentityService().getAccessLevels().get(1).equals("2-Query"));
		Assert.assertTrue(getIdentityService().getAccessLevels().get(2).equals("3-Create/Update"));
		

	}
	
	
	@Test
	public void testTestMembership() {
		
		User usr1 = new User("test1","test1");
		usr1.setFirstname("Test");
		usr1.setLastname("One");
		
		User usr2 = new User("test2","test2");
		usr2.setFirstname("Test");
		usr2.setLastname("Two");
		
		getIdentityService().saveUser(usr1,usr1.getUsername());
		getIdentityService().saveUser(usr2,usr1.getUsername());
		
		Role r1 = new Role("role1","role1");

		getIdentityService().saveRole(r1,usr1.getUsername());

		getIdentityService().createMembership("test1", "role1");
		
		Assert.assertTrue(getIdentityService().getUserRoles("test1").size() == 1);
		
		List<Role> roles = getIdentityService().getAllRoles();
		
		getIdentityService().deleteMembership("test1", "role1");
		
		Assert.assertTrue(getIdentityService().getUserRoles("test1").size() == 0);

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
