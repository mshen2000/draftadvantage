package com.nya.sms.test;

import static com.googlecode.objectify.ObjectifyService.ofy;
import static org.junit.Assert.*;

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
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.cmd.Query;
import com.nya.sms.dataservices.IdentityService;
import com.nya.sms.dataservices.PlayerProjectedService;
import com.nya.sms.entities.PlayerProjected;
import com.nya.sms.entities.User;

public class TestPlayerProjections {

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
		
		ObjectifyService.register(User.class);
		ObjectifyService.register(PlayerProjected.class);

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
	public void testGetPlayerAttributes() {
		
		String result = "";
		
		try {
			result = getPlayerProjectedService().getPlayerProjectionAttributes();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Assert.assertTrue(result.length() > 10);
		
	}
	
	@Test
	public void testProjectionMetadata() {
		
		List<ProjectionService> services = new ArrayList<ProjectionService>();
		List<ProjectionPeriod> periods = new ArrayList<ProjectionPeriod>();
		
		try {
			services = getPlayerProjectedService().getProjectionServices();
			periods = getPlayerProjectedService().getProjectionPeriods();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Assert.assertTrue(services.size() == 2);
		Assert.assertTrue(periods.size() == 2);
		
	}
	
	@Test
	public void testPlayerProjections() {
		
		User usr1 = new User("test1","test1");
		usr1.setFirstname("Test");
		usr1.setLastname("One");
		
		getIdentityService().saveUser(usr1,usr1.getUsername());
		getPlayerProjectedService();
		
		PlayerProjected ppHitter1 = new PlayerProjected(PlayerProjectedService.PROJECTION_SERVICE_STEAMER, 
				"1111","Joe Smith",PlayerProjectedService.PITCHER_HITTER_HITTER);
		PlayerProjected ppPitcher1 = new PlayerProjected(PlayerProjectedService.PROJECTION_SERVICE_STEAMER, 
				"2222","Mike Evans",PlayerProjectedService.PITCHER_HITTER_PITCHER);
		
		ppHitter1.setAge(21);
		ppHitter1.setTeam("LAA");
		ppHitter1.setAl_nl("AL");
		ppHitter1.setHitter_bats("L");
		ppHitter1.setHitter_pos_elig_espn("1b/3b");
		ppHitter1.setHitter_games((float) 100);
		ppHitter1.setHitter_avg((float) 0.310);
		
		ppPitcher1.setAge(31);
		ppPitcher1.setTeam("DET");
		ppPitcher1.setAl_nl("AL");
		ppPitcher1.setPitcher_throws("L");
		ppPitcher1.setPitcher_pos("SP");
		ppPitcher1.setPitcher_games((float) 90);
		ppPitcher1.setPitcher_era((float) 2.70);

		List<PlayerProjected> list1 = new ArrayList<PlayerProjected>();
		list1.add(ppHitter1);
		list1.add(ppPitcher1);
		
		PlayerProjected ppHitter2 = new PlayerProjected(PlayerProjectedService.PROJECTION_SERVICE_STEAMER, 
				"1111","Joe Smith",PlayerProjectedService.PITCHER_HITTER_HITTER);
		PlayerProjected ppPitcher2 = new PlayerProjected(PlayerProjectedService.PROJECTION_SERVICE_STEAMER, 
				"2222","Mike Evans",PlayerProjectedService.PITCHER_HITTER_PITCHER);
		
		ppHitter2.setAge(21);
		ppHitter2.setTeam("LAA");
		ppHitter2.setAl_nl("AL");
		ppHitter2.setHitter_bats("L");
		ppHitter2.setHitter_pos_elig_espn("1b/3b");
		ppHitter2.setHitter_games((float) 100);
		ppHitter2.setHitter_avg((float) 0.310);
		
		ppPitcher2.setAge(31);
		ppPitcher2.setTeam("DET");
		ppPitcher2.setAl_nl("AL");
		ppPitcher2.setPitcher_throws("L");
		ppPitcher2.setPitcher_pos("SP");
		ppPitcher2.setPitcher_games((float) 90);
		ppPitcher2.setPitcher_era((float) 2.70);

		List<PlayerProjected> list2 = new ArrayList<PlayerProjected>();
		list2.add(ppHitter2);
		list2.add(ppPitcher2);
		
		PlayerProjected ppHitter3 = new PlayerProjected(PlayerProjectedService.PROJECTION_SERVICE_STEAMER, 
				"1111","Joe Smith",PlayerProjectedService.PITCHER_HITTER_HITTER);
		PlayerProjected ppPitcher3 = new PlayerProjected(PlayerProjectedService.PROJECTION_SERVICE_STEAMER, 
				"2222","Mike Evans",PlayerProjectedService.PITCHER_HITTER_PITCHER);
		
		ppHitter3.setAge(21);
		ppHitter3.setTeam("LAA");
		ppHitter3.setAl_nl("AL");
		ppHitter3.setHitter_bats("L");
		ppHitter3.setHitter_pos_elig_espn("1b/3b");
		ppHitter3.setHitter_games((float) 100);
		ppHitter3.setHitter_avg((float) 0.310);
		
		ppPitcher3.setAge(31);
		ppPitcher3.setTeam("DET");
		ppPitcher3.setAl_nl("AL");
		ppPitcher3.setPitcher_throws("L");
		ppPitcher3.setPitcher_pos("SP");
		ppPitcher3.setPitcher_games((float) 90);
		ppPitcher3.setPitcher_era((float) 2.70);

		List<PlayerProjected> list3 = new ArrayList<PlayerProjected>();
		list3.add(ppHitter3);
		list3.add(ppPitcher3);
		
		Integer i1 = getPlayerProjectedService().updatePlayerProjections(list1, PlayerProjectedService.PROJECTION_SERVICE_STEAMER, 
				PlayerProjectedService.PROJECTION_PERIOD_PRESEASON, yesterday, 2015, "test1");
		
		// Save with different period
		Integer i2 = getPlayerProjectedService().updatePlayerProjections(list2, PlayerProjectedService.PROJECTION_SERVICE_STEAMER, 
				PlayerProjectedService.PROJECTION_PERIOD_ROS, yesterday, 2015, "test1");
		
		// Save with different year
		Integer i3 = getPlayerProjectedService().updatePlayerProjections(list3, PlayerProjectedService.PROJECTION_SERVICE_STEAMER, 
				PlayerProjectedService.PROJECTION_PERIOD_PRESEASON, yesterday, 2016, "test1");
		
		// Test return statements for updatePlayerProjections
		Assert.assertTrue(i1 == 2);
		Assert.assertTrue(i2 == 2);
		Assert.assertTrue(i3 == 2);
		
		// Test there are a total of 6 projections
		// Tests method getAllPlayerProjected
		// System.out.println("Count of all player projections: " + getPlayerProjectedService().getAllPlayerProjected().size());
		Assert.assertTrue(getPlayerProjectedService().getAllPlayerProjected().size() == 6);
		
		// Load list 3 again
		i3 = getPlayerProjectedService().updatePlayerProjections(list3, PlayerProjectedService.PROJECTION_SERVICE_STEAMER, 
				PlayerProjectedService.PROJECTION_PERIOD_PRESEASON, yesterday, 2016, "test1");
		
		// Test there are still a total of 6 projections
		// Verify that updatePlayerProjections deletes previous set
		Assert.assertTrue(getPlayerProjectedService().getAllPlayerProjected().size() == 6);
		
		// Test there are of 2 projections for a particular projection set
		// Tests method getPlayerProjections
		Assert.assertTrue(getPlayerProjectedService().getPlayerProjections(PlayerProjectedService.PROJECTION_SERVICE_STEAMER, 
				PlayerProjectedService.PROJECTION_PERIOD_ROS, 2015).size() == 2);
		
		// Test Count all projections
		Assert.assertTrue(getPlayerProjectedService().countAllPlayerProjections() == 6);
		
		// Test Count projections for a projection set
		// Tests method getPlayerProjections
		Assert.assertTrue(getPlayerProjectedService().countPlayerProjections(PlayerProjectedService.PROJECTION_SERVICE_STEAMER, 
				PlayerProjectedService.PROJECTION_PERIOD_ROS, 2015) == 2);
		
		// Test if a single projection exists
		// Tests method isPlayerProjectionPresent
		Assert.assertTrue(getPlayerProjectedService().isPlayerProjectionPresent(PlayerProjectedService.PROJECTION_SERVICE_STEAMER, 
				PlayerProjectedService.PROJECTION_PERIOD_PRESEASON, 2016, PlayerProjectedService.PROJECTION_SERVICE_STEAMER, "1111"));
		
		PlayerProjected pp_r1 = getPlayerProjectedService().getPlayerProjection(PlayerProjectedService.PROJECTION_SERVICE_STEAMER, 
				PlayerProjectedService.PROJECTION_PERIOD_PRESEASON, 2016, PlayerProjectedService.PROJECTION_SERVICE_STEAMER, "1111");
		
		// Test getting a single projection and validating attributes
		// Test method getPlayerProjection
		Assert.assertTrue(pp_r1.getProjection_period() == PlayerProjectedService.PROJECTION_PERIOD_PRESEASON);
		Assert.assertTrue(pp_r1.getProjected_year() == 2016);
		Assert.assertTrue(pp_r1.getFull_name() == "Joe Smith");
		Assert.assertTrue(pp_r1.getHitter_bats() == "L");
		Assert.assertTrue(pp_r1.getHitter_pos_elig_espn() == "1b/3b");
		Assert.assertTrue(pp_r1.getHitter_games() == (float) 100);
		Assert.assertTrue(pp_r1.getHitter_avg() == (float) 0.310);
		
		// Test deleting player projections
		getPlayerProjectedService().deletePlayerProjections(PlayerProjectedService.PROJECTION_SERVICE_STEAMER, 
				PlayerProjectedService.PROJECTION_PERIOD_PRESEASON, 2016);
		
		Assert.assertTrue(getPlayerProjectedService().getPlayerProjections(PlayerProjectedService.PROJECTION_SERVICE_STEAMER, 
				PlayerProjectedService.PROJECTION_PERIOD_PRESEASON, 2016).size() == 0);
		
		Assert.assertTrue(getPlayerProjectedService().getAllPlayerProjected().size() == 4);
		
		// Test deleting all player projections
		getPlayerProjectedService().deleteAllPlayerProjections();
		Assert.assertTrue(getPlayerProjectedService().getAllPlayerProjected().size() == 0);
		
	}
	
	
	 private IdentityService getIdentityService() {
		 
		 return new IdentityService();
	 
	 }
	 
	 private PlayerProjectedService getPlayerProjectedService() {
		 
		 return new PlayerProjectedService();
	 
	 }
	
}
