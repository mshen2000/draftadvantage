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

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.util.Closeable;
import com.nya.sms.dataservices.IdentityService;
import com.nya.sms.dataservices.LeagueService;
import com.nya.sms.dataservices.PlayerProjectedService;
import com.nya.sms.dataservices.ProjectionProfileService;
import com.nya.sms.entities.PlayerProjected;
import com.nya.sms.entities.ProjectionProfile;
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
	
	private Closeable closeable;
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		
		helper.setUp();

		ObjectifyService.register(User.class);
		ObjectifyService.register(PlayerProjected.class);
		ObjectifyService.register(ProjectionProfile.class);

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
	public void testPlayerProjections() {
		
		User usr1 = new User("test1","test1");
		usr1.setFirstname("Test");
		usr1.setLastname("One");
		
		getIdentityService().saveUser(usr1,usr1.getUsername());
		
		ProjectionProfile p1 = new ProjectionProfile();
		p1.setProjected_year(2016);
		p1.setProjection_date(yesterday);
		p1.setProjection_period(ProjectionProfileService.PROJECTION_PERIOD_PRESEASON);
		p1.setProjection_service(ProjectionProfileService.PROJECTION_SERVICE_STEAMER);

		Long p1_id = getProjectionProfileService().save(p1, usr1.getUsername());
		ProjectionProfile p1_out = getProjectionProfileService().get(p1_id);
		
		ProjectionProfile p2 = new ProjectionProfile();
		p2.setProjected_year(2015);
		p2.setProjection_date(tomorrow);
		p2.setProjection_period(ProjectionProfileService.PROJECTION_PERIOD_ROS);
		p2.setProjection_service(ProjectionProfileService.PROJECTION_SERVICE_STEAMER);

		Long p2_id = getProjectionProfileService().save(p2, usr1.getUsername());
		ProjectionProfile p2_out = getProjectionProfileService().get(p2_id);
		
		ProjectionProfile p3 = new ProjectionProfile();
		p3.setProjected_year(2015);
		p3.setProjection_date(tomorrow);
		p3.setProjection_period(ProjectionProfileService.PROJECTION_PERIOD_PRESEASON);
		p3.setProjection_service(ProjectionProfileService.PROJECTION_SERVICE_STEAMER);

		Long p3_id = getProjectionProfileService().save(p3, usr1.getUsername());
		ProjectionProfile p3_out = getProjectionProfileService().get(p3_id);
		
		PlayerProjected ppHitter1 = new PlayerProjected(ProjectionProfileService.PROJECTION_SERVICE_STEAMER, 
				"1111","Joe Smith",PlayerProjectedService.PITCHER_HITTER_HITTER);
		PlayerProjected ppPitcher1 = new PlayerProjected(ProjectionProfileService.PROJECTION_SERVICE_STEAMER, 
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
		
		PlayerProjected ppHitter2 = new PlayerProjected(ProjectionProfileService.PROJECTION_SERVICE_STEAMER, 
				"1111","Joe Smith",PlayerProjectedService.PITCHER_HITTER_HITTER);
		PlayerProjected ppPitcher2 = new PlayerProjected(ProjectionProfileService.PROJECTION_SERVICE_STEAMER, 
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
		
		PlayerProjected ppHitter3 = new PlayerProjected(ProjectionProfileService.PROJECTION_SERVICE_STEAMER, 
				"1111","Joe Smith",PlayerProjectedService.PITCHER_HITTER_HITTER);
		PlayerProjected ppPitcher3 = new PlayerProjected(ProjectionProfileService.PROJECTION_SERVICE_STEAMER, 
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
		
		Integer i1 = getPlayerProjectedService().updatePlayerProjections(list1, p1, "test1");
		
		// Save with different period
		Integer i2 = getPlayerProjectedService().updatePlayerProjections(list2, p2, "test1");
		
		// Save with different year
		Integer i3 = getPlayerProjectedService().updatePlayerProjections(list3, p3, "test1");
		
		// Test return statements for updatePlayerProjections
		Assert.assertTrue(i1 == 2);
		Assert.assertTrue(i2 == 2);
		Assert.assertTrue(i3 == 2);
		
		// Test there are a total of 6 projections
		// Tests method getAllPlayerProjected
		// System.out.println("Count of all player projections: " + getPlayerProjectedService().getAllPlayerProjected().size());
		Assert.assertTrue(getPlayerProjectedService().getAllPlayerProjected().size() == 6);
		
		// Count pitchers and hitters
		Assert.assertTrue(getPlayerProjectedService().countHitterProjections(p1_out) == 1);
		Assert.assertTrue(getPlayerProjectedService().countPitcherProjections(p2_out) == 1);
		
		// Load list 3 again
		i3 = getPlayerProjectedService().updatePlayerProjections(list3, p3, "test1");
		
		// Test there are still a total of 6 projections
		// Verify that updatePlayerProjections deletes previous set
		Assert.assertTrue(getPlayerProjectedService().getAllPlayerProjected().size() == 6);
		
		// Load list 3 again with one list member
		list3.remove(0);
		i3 = getPlayerProjectedService().updatePlayerProjections(list3, p3, "test1");
		
		// Test there are now a total of 5 projections
		// Verify that updatePlayerProjections deletes projection not in new list3
		Assert.assertTrue(getPlayerProjectedService().getAllPlayerProjected().size() == 5);
		
		
		// Test there are of 2 projections for a particular projection set
		// Tests method getPlayerProjections
		Assert.assertTrue(getPlayerProjectedService().getPlayerProjections(p1,LeagueService.MLB_LEAGUES_BOTH).size() == 2);
		
		// Test Count all projections
		Assert.assertTrue(getPlayerProjectedService().countAllPlayerProjections() == 5);
		
		// Test Count projections for a projection set
		// Tests method getPlayerProjections
		Assert.assertTrue(getPlayerProjectedService().countPlayerProjections(p2) == 2);
		
		// Test if a single projection exists
		// Tests method isPlayerProjectionPresent
		Assert.assertTrue(getPlayerProjectedService().isPlayerProjectionPresent(p1, ProjectionProfileService.PROJECTION_SERVICE_STEAMER, "1111"));
		
		PlayerProjected pp_r1 = getPlayerProjectedService().getPlayerProjection(p1, ProjectionProfileService.PROJECTION_SERVICE_STEAMER, "1111");
		
		// Test getting a single projection and validating attributes
		// Test method getPlayerProjection
		Assert.assertTrue(pp_r1.getProjection_profile().getProjection_period() == ProjectionProfileService.PROJECTION_PERIOD_PRESEASON);
		Assert.assertTrue(pp_r1.getProjection_profile().getProjected_year() == 2016);
		Assert.assertTrue(pp_r1.getFull_name() == "Joe Smith");
		Assert.assertTrue(pp_r1.getHitter_bats() == "L");
		Assert.assertTrue(pp_r1.getHitter_pos_elig_espn() == "1b/3b");
		Assert.assertTrue(pp_r1.getHitter_games() == (float) 100);
		Assert.assertTrue(pp_r1.getHitter_avg() == (float) 0.310);
		
		// Test deleting player projections
		getPlayerProjectedService().deletePlayerProjections(p1);
		Assert.assertTrue(getPlayerProjectedService().getPlayerProjections(p1,LeagueService.MLB_LEAGUES_BOTH).size() == 0);
		Assert.assertTrue(getPlayerProjectedService().getAllPlayerProjected().size() == 3);
		
		// Test deleting a profile (and all it's projections)
		getProjectionProfileService().delete(p2_id);
		Assert.assertNull(getProjectionProfileService().get(p2_id));
		Assert.assertTrue(getPlayerProjectedService().getPlayerProjections(p2,LeagueService.MLB_LEAGUES_BOTH).size() == 0);
		Assert.assertTrue(getPlayerProjectedService().getAllPlayerProjected().size() == 1);
		
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

	private ProjectionProfileService getProjectionProfileService() {

		return new ProjectionProfileService(ProjectionProfile.class);

	}
	
}
