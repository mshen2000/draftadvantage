package com.nya.sms.test;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.app.endpoints.entities.ProjectionPeriod;
import com.app.endpoints.entities.ProjectionService;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.util.Closeable;
import com.nya.sms.dataservices.IdentityService;
import com.nya.sms.dataservices.ProjectionProfileService;
import com.nya.sms.entities.ProjectionProfile;
import com.nya.sms.entities.User;

public class TestProjectionProfiles {

	private final LocalServiceTestHelper helper = new LocalServiceTestHelper(
			new LocalDatastoreServiceTestConfig()
					.setDefaultHighRepJobPolicyUnappliedJobPercentage(0.1f));

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

		ObjectifyService.register(User.class);
		ObjectifyService.register(ProjectionProfile.class);

		c = Calendar.getInstance();
		c.setTime(new Date(java.lang.System.currentTimeMillis()));
		c.add(Calendar.DAY_OF_MONTH, -1);
		yesterday = c.getTime();
		c.add(Calendar.DAY_OF_MONTH, 2);
		tomorrow = c.getTime();
		c.add(Calendar.YEAR, -10);
		tenyearsago = c.getTime();

		Locale currentlocal = new Locale("en_US");
		dateFormatter = DateFormat.getDateInstance(DateFormat.DEFAULT,
				currentlocal);

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
	public void testProjectionMetadata() {
		
		List<ProjectionService> services = new ArrayList<ProjectionService>();
		List<ProjectionPeriod> periods = new ArrayList<ProjectionPeriod>();
		
		try {
			services = getProjectionProfileService().getProjectionServices();
			periods = getProjectionProfileService().getProjectionPeriods();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Assert.assertTrue(services.size() == 2);
		Assert.assertTrue(periods.size() == 2);
		
	}

	@Test
	public void testProjectionProfiles() {

		User usr1 = new User("test1", "test1");
		usr1.setFirstname("Test");
		usr1.setLastname("One");

		getIdentityService().saveUser(usr1, usr1.getUsername());
		getProjectionProfileService();

		ProjectionProfile p1 = new ProjectionProfile();
		p1.setProjected_year(2016);
		p1.setProjection_date(yesterday);
		p1.setProjection_period(ProjectionProfileService.PROJECTION_PERIOD_PRESEASON);
		p1.setProjection_service(ProjectionProfileService.PROJECTION_SERVICE_STEAMER);

		ProjectionProfile p2 = new ProjectionProfile();
		p2.setProjected_year(2015);
		p2.setProjection_date(tomorrow);
		p2.setProjection_period(ProjectionProfileService.PROJECTION_PERIOD_ROS);
		p2.setProjection_service(ProjectionProfileService.PROJECTION_SERVICE_STEAMER);

		Long p1_id = getProjectionProfileService().save(p1, usr1.getUsername());
		Long p2_id = getProjectionProfileService().save(p2, usr1.getUsername());

		// Test get all profiles
		Assert.assertTrue(getProjectionProfileService().getAll().size() == 2);

		ProjectionProfile p1_out = new ProjectionProfile();
		ProjectionProfile p2_out = new ProjectionProfile();

		// Test get by ID
		p1_out = getProjectionProfileService().get(p1_id);
		Assert.assertTrue(p1_out.getProjection_period() == ProjectionProfileService.PROJECTION_PERIOD_PRESEASON);
		Assert.assertTrue(p1_out.getProjection_service() == ProjectionProfileService.PROJECTION_SERVICE_STEAMER);
		Assert.assertTrue(p1_out.getProjection_date() == yesterday);
		Assert.assertTrue(p1_out.getProjected_year() == 2016);

		// Test get by attributes
		p2_out = getProjectionProfileService().get(
				ProjectionProfileService.PROJECTION_SERVICE_STEAMER,
				ProjectionProfileService.PROJECTION_PERIOD_ROS, 2015);
		Assert.assertTrue(p2_out.getProjection_period() == ProjectionProfileService.PROJECTION_PERIOD_ROS);
		Assert.assertTrue(p2_out.getProjection_service() == ProjectionProfileService.PROJECTION_SERVICE_STEAMER);
		Assert.assertTrue(p2_out.getProjection_date() == tomorrow);
		Assert.assertTrue(p2_out.getProjected_year() == 2015);

		// Test update
		p2.setProjection_date(yesterday);
		p2_id = getProjectionProfileService().save(p2, usr1.getUsername());

		// Verify there are still only 2 profiles
		Assert.assertTrue(getProjectionProfileService().getAll().size() == 2);

		// Verify profile updated correctly
		p2_out = getProjectionProfileService().get(p2_id);
		Assert.assertTrue(p2_out.getProjection_date() == yesterday);
		Assert.assertTrue(p2_out.getProjection_period() == ProjectionProfileService.PROJECTION_PERIOD_ROS);

		// Test delete
		getProjectionProfileService().delete(p2_id);
		Assert.assertTrue(getProjectionProfileService().getAll().size() == 1);
		Assert.assertFalse(getProjectionProfileService()
				.isProjectionProfilePresent(
						ProjectionProfileService.PROJECTION_SERVICE_STEAMER,
						ProjectionProfileService.PROJECTION_PERIOD_ROS, 2015));

	}

	private IdentityService getIdentityService() {

		return new IdentityService();

	}

	private ProjectionProfileService getProjectionProfileService() {

		return new ProjectionProfileService(ProjectionProfile.class);

	}

}
