package com.nya.sms.test;

import static org.junit.Assert.*;

import java.io.Serializable;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.googlecode.objectify.ObjectifyService;
import com.nya.sms.dataservices.CustomTestService;
import com.nya.sms.dataservices.IdentityService;
import com.nya.sms.dataservices.SiteService;
import com.nya.sms.dataservices.StudentService;
import com.nya.sms.entities.BaseFieldAbstract;
import com.nya.sms.entities.BaseFieldString;
import com.nya.sms.entities.CustomObjectTest;
import com.nya.sms.entities.Note;
import com.nya.sms.entities.ProgramScoreCaughtYa;
import com.nya.sms.entities.Role;
import com.nya.sms.entities.Site;
import com.nya.sms.entities.Student;
import com.nya.sms.entities.StudentGroup;
import com.nya.sms.entities.StudentHealth;
import com.nya.sms.entities.User;

public class TestProgram implements Serializable {

	private static final long serialVersionUID = 1L;

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
		ObjectifyService.register(CustomObjectTest.class);

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
	public void testProgram() {
		
		// test savescore, getallscores, deletescore, getscore
		User usr1 = new User("test1","test1");
		usr1.setFirstname("Test");
		usr1.setLastname("One");
		
		getIdentityService().saveUser(usr1,usr1.getUsername());
		
		User usrreturn = getIdentityService().getUser(usr1.getUsername());
		
		Role roleinit = new Role("testrole","Test Role");
		getIdentityService().saveRole(roleinit, usr1.getUsername());
		
		Role rolereturn = getIdentityService().getRole(roleinit.getName());

		
		BaseFieldString field1 = new BaseFieldString("teststring1");
		field1.setPriority(2);
		field1.setQueryVisible(true);
		field1.setFormAccess(true);
		BaseFieldString field2 = new BaseFieldString("teststring2");
		field2.setPriority(1);
		field2.setQueryVisible(true);
		field2.setFormAccess(true);
		BaseFieldString field3 = new BaseFieldString("teststring3");
		field3.setPriority(3);
		field3.setQueryVisible(true);
		field3.setFormAccess(false);
		Map<String, BaseFieldAbstract> fields = new HashMap<String, BaseFieldAbstract>();
		fields.put("teststring1", field1);
		fields.put("teststring2", field2);
		fields.put("teststring3", field3);
		
		CustomObjectTest program = new CustomObjectTest("caughtya", "Caught Ya");
		program.setProgramScoreClass(ProgramScoreCaughtYa.class);
		program.setFields(fields);

		Long saveditemid = getProgramService().save(program, usr1.getUsername());
		
		Assert.assertEquals(1, getProgramService().getAll().size());
		
		CustomObjectTest ritem = getProgramService().get(saveditemid);
		
		Assert.assertEquals("caughtya", ritem.getName());
		
		Assert.assertEquals(field2.getName(), getProgramService().getSortedFieldsForQuery(ritem).get(0).getName());
		Assert.assertEquals(field1.getName(), getProgramService().getSortedFieldsForQuery(ritem).get(1).getName());
		Assert.assertEquals(2, getProgramService().getSortedFieldsForForm(ritem).size());
		
		getProgramService().delete(ritem.getId());
		
		Assert.assertEquals(0, getProgramService().getAll().size());
		
	}

	
	
	 private CustomTestService getProgramService() {
		 
		 return new CustomTestService(CustomObjectTest.class);
	 
	 }
	 
	 private IdentityService getIdentityService() {
		 
		 return new IdentityService();
	 
	 }
	 
	 private SiteService getSiteService() {
		 
		 return new SiteService(Site.class);
	 
	 }

}
