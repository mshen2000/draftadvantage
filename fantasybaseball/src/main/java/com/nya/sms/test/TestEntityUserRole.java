/**
 * 
 */
package com.nya.sms.test;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
import com.googlecode.objectify.util.Closeable;
import com.nya.sms.dataservices.IdentityService;
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
		
		ObjectifyService.init();

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
		System.out.println("In testCreateUser...");
		
		// delete users and roles before test start
		getIdentityService().deleteUser("test1");
		getIdentityService().deleteUser("test2");
		
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

		// delete users and roles before test start
		getIdentityService().deleteUser("test1");
		getIdentityService().deleteUser("test2");
		
		System.out.println("End testCreateUser...");
	}
	
	@Test
	public void testUserModifiedBy() {
		System.out.println("In testUserModifiedBy...");
		
		// delete users and roles before test start
		getIdentityService().deleteUser("test1");
		getIdentityService().deleteUser("test2");
		
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

		// delete users and roles before test start
		getIdentityService().deleteUser("test1");
		getIdentityService().deleteUser("test2");
		
		System.out.println("End testUserModifiedBy...");
	}

	
	@Test
	public void testCheckPassword() {
		System.out.println("In testCheckPassword...");
		
		// delete users and roles before test start
		getIdentityService().deleteUser("test1");
		
		User usr1 = new User("test1","test1");
		usr1.setFirstname("Test");
		usr1.setLastname("One");
		usr1.setEmail("test1@test.com");

		getIdentityService().saveUser(usr1,usr1.getUsername());

		Assert.assertTrue(getIdentityService().checkPassword("test1@test.com", "test1"));
		Assert.assertFalse(getIdentityService().checkPassword("test1@test.com", "aaaa"));

		// delete users and roles before test start
		getIdentityService().deleteUser("test1");

		System.out.println("End testCheckPassword...");
	}
	
	@Test 
	public void testJWT(){
		System.out.println("In testJWT...");
		
		// delete users and roles before test start
		getIdentityService().deleteUser("test1");
		getIdentityService().deleteRole("user");
		getIdentityService().deleteRole("admin");
		
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

		// delete users and roles after test
		getIdentityService().deleteUser("test1");
		getIdentityService().deleteRole("user");
		getIdentityService().deleteRole("admin");
		
		System.out.println("End testJWT...");
	}
	
	@Test
	public void testIsUserPresent() {
		
		// also test deleteUser
		System.out.println("In testIsUserPresent..");
		
		//  make sure there are now test1 users to start
		getIdentityService().deleteUser("test1");
		
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
		System.out.println("In testCreateRole...");
		
		// delete users and roles before test start
		getIdentityService().deleteUser("test1");
		getIdentityService().deleteRole("test1");
		getIdentityService().deleteRole("test2");
		
		// also test getallroles, getrole, saverole
		
		User usr1 = new User("test1","test1");
		usr1.setFirstname("Test");
		usr1.setLastname("One");

		getIdentityService().saveUser(usr1,usr1.getUsername());

		// Site site1 = new Site("testsite");
		// site1.setDescription("site description");
		// Long saveditemid1 = getSiteService().save(site1, usr1.getUsername());

		Role r1 = new Role("test1","test1");
		Role r2 = new Role("test2","test2");
		
		// r1.setSite(site1.getName());
		
		getIdentityService().saveRole(r1,usr1.getUsername());
		getIdentityService().saveRole(r2,usr1.getUsername());

		Assert.assertEquals(getIdentityService().getRole("test1").getName(), r1.getName());
		Assert.assertEquals(getIdentityService().getRole("test2").getName(), r2.getName());
		
		getIdentityService().deleteRole(r1.getName());
		
		Assert.assertFalse(getIdentityService().isRolePresent(r1.getName()));

		// delete users and roles after test 
		getIdentityService().deleteUser("test1");
		getIdentityService().deleteRole("test1");
		getIdentityService().deleteRole("test2");
		
		System.out.println("End testCreateRole...");
	}
	
	@Test
	public void testIsRolePresent() {
		System.out.println("In testIsRolePresent...");
		
		// delete users and roles before test start
		getIdentityService().deleteUser("test1");
		getIdentityService().deleteRole("test1");
		
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
		
		// delete users and roles before test start
		getIdentityService().deleteUser("test1");
		getIdentityService().deleteRole("test1");
		
		System.out.println("In testIsRolePresent...");
	}
	

	@Test
	public void testGetAccessLevels() {

		Assert.assertTrue(getIdentityService().getAccessLevels().get(0).equals("1-No Access"));
		Assert.assertTrue(getIdentityService().getAccessLevels().get(1).equals("2-Query"));
		Assert.assertTrue(getIdentityService().getAccessLevels().get(2).equals("3-Create/Update"));
		

	}
	
	
	@Test
	public void testTestMembership() {
		System.out.println("In testTestMembership...");
		
		// delete users and roles before test start
		getIdentityService().deleteUser("test1");
		getIdentityService().deleteUser("test2");
		getIdentityService().deleteRole("role1");
		
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
		
		// delete users and roles before test start
		getIdentityService().deleteUser("test1");
		getIdentityService().deleteUser("test2");
		getIdentityService().deleteRole("role1");
		
		System.out.println("End testTestMembership...");
	}
	
	private void deleteAllUsersRoles() throws InterruptedException {
		
		// System.out.println("Deleting Users and Roles");
		// System.out.println("-- Users: " + ObjectifyService.ofy().load().type(User.class).count());
		// System.out.println("-- Roles: " + ObjectifyService.ofy().load().type(Role.class).count());
		// System.out.println("Deleting...");
		List<Key<Role>> rolekeys = ofy().load().type(Role.class).keys().list();
		ofy().delete().keys(rolekeys).now();
		
		List<Key<User>> userkeys = ofy().load().type(User.class).keys().list();
		ofy().delete().keys(userkeys).now();
		Thread.sleep(400);
		// System.out.println("-- Users: " + ObjectifyService.ofy().load().type(User.class).count());
		// System.out.println("-- Roles: " + ObjectifyService.ofy().load().type(Role.class).count());
		
	}
	
	
	 private IdentityService getIdentityService() {
		 
		 return new IdentityService();
	 
	 }
	 


}
