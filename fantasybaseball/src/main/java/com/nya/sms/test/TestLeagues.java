package com.nya.sms.test;

import static org.junit.Assert.fail;

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
import com.nya.sms.dataservices.LeaguePlayerService;
import com.nya.sms.dataservices.LeagueService;
import com.nya.sms.dataservices.LeagueTeamService;
import com.nya.sms.dataservices.ProjectionProfileService;
import com.nya.sms.entities.League;
import com.nya.sms.entities.LeaguePlayer;
import com.nya.sms.entities.LeagueTeam;
import com.nya.sms.entities.PlayerProjected;
import com.nya.sms.entities.ProjectionProfile;
import com.nya.sms.entities.User;

public class TestLeagues {

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
		ObjectifyService.register(League.class);
		ObjectifyService.register(LeagueTeam.class);
		ObjectifyService.register(LeaguePlayer.class);
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
	public void testProjectionProfiles() {

		User usr1 = new User("test1", "test1");
		usr1.setFirstname("Test");
		usr1.setLastname("One");

		getIdentityService().saveUser(usr1, usr1.getUsername());
		
		LeagueTeam lt1 = new LeagueTeam();
		lt1.setTeam_name("Team1");
		lt1.setOwner_name("Owner1");
		lt1.setSalary_adjustment(100.00);
		lt1.setIsuserowner(true);
		
		LeagueTeam lt2 = new LeagueTeam();
		lt2.setTeam_name("Team2");
		lt2.setOwner_name("Owner2");
		lt2.setSalary_adjustment(10.50);
		
		LeagueTeam lt3 = new LeagueTeam();
		lt3.setTeam_name("Team3");
		lt3.setOwner_name("Owner3");
		lt3.setSalary_adjustment(103.00);
		
		LeagueTeam lt4 = new LeagueTeam();
		lt4.setTeam_name("Team4");
		lt4.setOwner_name("Owner4");
		lt4.setSalary_adjustment(103.00);
		
		long lt1_id = getLeagueTeamService().save(lt1, usr1.getUsername());
		long lt2_id = getLeagueTeamService().save(lt2, usr1.getUsername());
		long lt3_id = getLeagueTeamService().save(lt3, usr1.getUsername());
		long lt4_id = getLeagueTeamService().save(lt4, usr1.getUsername());
		
		LeagueTeam lt1_r = getLeagueTeamService().get(lt1_id);
		LeagueTeam lt2_r = getLeagueTeamService().get(lt2_id);
		LeagueTeam lt3_r = getLeagueTeamService().get(lt3_id);
		LeagueTeam lt4_r = getLeagueTeamService().get(lt4_id);
		
		// Test save of League Teams
		Assert.assertTrue(lt1_r.getTeam_name() == lt1.getTeam_name());
		Assert.assertTrue(lt1_r.getOwner_name() == lt1.getOwner_name());
		Assert.assertTrue(lt1_r.getSalary_adjustment() == lt1.getSalary_adjustment());
		Assert.assertTrue(lt1_r.isIsuserowner() == lt1.isIsuserowner());
		
		Assert.assertTrue(lt2_r.getTeam_name() == lt2.getTeam_name());
		Assert.assertTrue(lt2_r.getOwner_name() == lt2.getOwner_name());
		Assert.assertTrue(lt2_r.getSalary_adjustment() == lt2.getSalary_adjustment());
		Assert.assertTrue(lt2_r.isIsuserowner() == lt2.isIsuserowner());
		
		// Test League Team count
		Assert.assertTrue(getLeagueTeamService().getAll().size() == 4);
		
		getLeagueTeamService().delete(lt3_id);
		
		// Test League Team count after delete
		Assert.assertTrue(getLeagueTeamService().getAll().size() == 3);
		
		lt2_r.setOwner_name("Owner2_changed");
		getLeagueTeamService().save(lt2_r, usr1.getUsername());
		
		// Test League Team after update
		Assert.assertTrue(getLeagueTeamService().getAll().size() == 3);
		Assert.assertTrue(getLeagueTeamService().get(lt2_r.id).getOwner_name() == "Owner2_changed");
		
		
		ProjectionProfile p1 = new ProjectionProfile();
		p1.setProjected_year(2016);
		p1.setProjection_date(yesterday);
		p1.setProjection_period(ProjectionProfileService.PROJECTION_PERIOD_PRESEASON);
		p1.setProjection_service(ProjectionProfileService.PROJECTION_SERVICE_STEAMER);

		Long p1_id = getProjectionProfileService().save(p1, usr1.getUsername());
		
		League l1 = new League();
		l1.setNum_of_teams(2);
		l1.setAvg_hitter_ab(1000);
		l1.setAvg_pitcher_er(500);
		l1.setCat_hitter_avg(true);
		l1.setCat_pitcher_era(true);
		l1.setLeague_name("League 1");
		l1.setLeague_site("CBS");
		l1.setNum_1b(1);
		l1.setNum_p(5);
		l1.setTeam_salary(260);
		l1.setUser(usr1);
		l1.setProjection_profile(getProjectionProfileService().get(p1_id));
		
		long l1_id = getLeagueService().save(l1, usr1.getUsername());
		League l1_r = getLeagueService().get(l1_id);
		
		getLeagueService().addLeagueTeam(l1_id, lt1_id, usr1.getUsername());
		getLeagueService().addLeagueTeam(l1_id, lt2_id, usr1.getUsername());
		
		// Test Team League attributes
		Assert.assertTrue(l1_r.getAvg_hitter_ab() == 1000);
		Assert.assertTrue(l1_r.getAvg_pitcher_er() == 500);
		Assert.assertTrue(l1_r.isCat_hitter_avg() == true);
		Assert.assertTrue(l1_r.isCat_pitcher_era() == true);
		Assert.assertTrue(l1_r.getLeague_name() == "League 1");
		Assert.assertTrue(l1_r.getLeague_site() == "CBS");
		Assert.assertTrue(l1_r.getNum_1b() == 1);
		Assert.assertTrue(l1_r.getNum_p() == 5);
		Assert.assertTrue(l1_r.getTeam_salary() == 260);
		Assert.assertTrue(l1_r.getUser().getUsername() == "test1");
		Assert.assertTrue(l1_r.getProjection_profile().getProjection_service() == ProjectionProfileService.PROJECTION_SERVICE_STEAMER);
		
		// Test count of league teams in league
		Assert.assertTrue(l1_r.getLeague_teams().size() == 2);
		
		// Test limit of teams in league
		try {
			getLeagueService().addLeagueTeam(l1_id, lt4_id, usr1.getUsername());
		    fail( "Adding 3rd LeagueTeam did not cause exception" );
		} catch (IndexOutOfBoundsException e) {
		}
		
		getLeagueService().deleteLeagueTeam(l1_id, lt2_id, usr1.getUsername());
		
		l1_r = getLeagueService().get(l1_id);
		
		// Test count of league teams and total teams after delete Team from League
		Assert.assertTrue(l1_r.getLeague_teams().size() == 1);
		Assert.assertTrue(getLeagueTeamService().getAll().size() == 2);
		
		getLeagueService().delete(l1_id);
		
		// Test count of Leagues and Teams after deleting the League
		Assert.assertTrue(getLeagueTeamService().getAll().size() == 1);
		Assert.assertTrue(getLeagueService().getAll().size() == 0);

	}

	private IdentityService getIdentityService() {

		return new IdentityService();

	}

	private LeaguePlayerService getLeaguePlayerService() {

		return new LeaguePlayerService(LeaguePlayer.class);

	}
	
	private LeagueService getLeagueService() {

		return new LeagueService(League.class);

	}
	
	private LeagueTeamService getLeagueTeamService() {

		return new LeagueTeamService(LeagueTeam.class);

	}
	
	private ProjectionProfileService getProjectionProfileService() {

		return new ProjectionProfileService(ProjectionProfile.class);

	}

}
