package com.nya.sms.test;

import static com.googlecode.objectify.ObjectifyService.ofy;
import static org.junit.Assert.fail;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.app.endpoints.LeaguePlayerOutput;
import com.app.endpoints.entities.LeagueCreateContainer;
import com.app.endpoints.entities.LeaguePlayerInputCustPosContainer;
import com.app.endpoints.entities.LeaguePlayerInputDraftContainer;
import com.app.endpoints.entities.LeaguePlayerInputInfoContainer;
import com.app.endpoints.entities.LeagueRosterItem;
import com.app.endpoints.entities.PositionZPriorityContainer;
import com.app.endpoints.entities.ProjectionPeriod;
import com.app.endpoints.entities.ProjectionService;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.util.Closeable;
import com.nya.sms.dataservices.IdentityService;
import com.nya.sms.dataservices.LeaguePlayerService;
import com.nya.sms.dataservices.LeagueService;
import com.nya.sms.dataservices.LeagueTeamService;
import com.nya.sms.dataservices.PlayerProjectedService;
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
	
	private String testfile1 = "D:/AllData/Dropbox/Fantasy Sports/2016 - MLB/AppTest/Source Data - 20160210-TESTa.csv";
	private String testfile2 = "D:/AllData/Dropbox/Fantasy Sports/2016 - MLB/AppTest/Source Data - 20160210-TESTb.csv";

	private Closeable closeable;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {

		helper.setUp();
		
		ObjectifyService.init();

		ObjectifyService.register(User.class);
		ObjectifyService.register(League.class);
		ObjectifyService.register(LeagueTeam.class);
		ObjectifyService.register(LeaguePlayer.class);
		ObjectifyService.register(ProjectionProfile.class);
		ObjectifyService.register(PlayerProjected.class);

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
		
		this.deleteTestItems();
		
		// Create user
		User usr1 = new User("test1", "test1");
		usr1.setFirstname("Test");
		usr1.setLastname("One");
		
		User usr2 = new User("test2", "test2");
		usr2.setFirstname("Test");
		usr2.setLastname("Two");

		getIdentityService().saveUser(usr1, usr1.getUsername());
		getIdentityService().saveUser(usr2, usr2.getUsername());
		
		// Create projection profile
		ProjectionProfile p1 = new ProjectionProfile();
		p1.setProjected_year(2016);
		p1.setProjection_date(yesterday);
		p1.setProjection_period(ProjectionProfileService.PROJECTION_PERIOD_PRESEASON);
		p1.setProjection_service(ProjectionProfileService.PROJECTION_SERVICE_STEAMER);

		Long p1_id = getProjectionProfileService().save(p1, usr1.getUsername());
		ProjectionProfile p1_r = getProjectionProfileService().get(p1_id);
		
		// Parser
		Reader in = new FileReader(testfile2);

		getPlayerProjectedService().updatePlayerProjections(parseProjections(in), p1_r, usr1.getUsername());
	
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		this.deleteTestItems();
		closeable.close();
		helper.tearDown();

	}


	@Test
	public void testLeagues() {
		
		System.out.println("In testLeagues...");

		User usr1 = getIdentityService().getUser("test1");
		User usr2 = getIdentityService().getUser("test2");
		
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
		System.out.println("--Number of League Teams: " + getLeagueTeamService().getAll().size());
		Assert.assertTrue(getLeagueTeamService().getAll().size() == 4);
		
		getLeagueTeamService().delete(lt3_id);
		
		// Test League Team count after delete
		Assert.assertTrue(getLeagueTeamService().getAll().size() == 3);
		
		lt2_r.setOwner_name("Owner2_changed");
		getLeagueTeamService().save(lt2_r, usr1.getUsername());
		
		// Test League Team after update
		Assert.assertTrue(getLeagueTeamService().getAll().size() == 3);
		Assert.assertTrue(getLeagueTeamService().get(lt2_r.id).getOwner_name() == "Owner2_changed");
		
		ProjectionProfile p1 = getProjectionProfileService().get(ProjectionProfileService.PROJECTION_SERVICE_STEAMER,
				ProjectionProfileService.PROJECTION_PERIOD_PRESEASON, 2016);

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
		l1.setProjection_profile(getProjectionProfileService().get(p1.getId()));
		
		long l1_id = getLeagueService().save(l1, usr1.getUsername());
		League l1_r = getLeagueService().get(l1_id);
		
		getLeagueService().addLeagueTeam(l1_id, lt1_id, usr1.getUsername());
		getLeagueService().addLeagueTeam(l1_id, lt2_id, usr1.getUsername());
		
		l1_r = getLeagueService().get(l1_id);
		
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

		// Test get Leagues by user
		Assert.assertTrue(getLeagueService().getUserLeagues(usr1.getUsername()).size() == 1);
		Assert.assertTrue(getLeagueService().getUserLeagues(usr2.getUsername()).size() == 0);
		
		// Test league team max check method (true)
		Assert.assertTrue(getLeagueService().isLeagueTeamsMaxed(l1_r.getId()));
		
		// Test limit of teams in league
		try {
			getLeagueService().addLeagueTeam(l1_id, lt4_id, usr1.getUsername());
		    fail( "Adding 3rd LeagueTeam did not cause exception" );
		} catch (IndexOutOfBoundsException e) {
		}
		
		l1_r = getLeagueService().get(l1_id);

		getLeagueService().deleteLeagueTeam(l1_id, lt2_id, usr1.getUsername());
		
		// Test league team max check method (true)
		Assert.assertTrue(getLeagueService().isLeagueTeamsMaxed(l1_r.getId()));
		
		// Test count of league teams and total teams after delete Team from League
		Assert.assertTrue(l1_r.getLeague_teams().size() == 1);
		Assert.assertTrue(getLeagueTeamService().getAll().size() == 2);
		
		getLeagueService().deleteLeagueFull(l1_id,usr1.getUsername());
		
		// Test count of Leagues and Teams after deleting the League
		Assert.assertTrue(getLeagueTeamService().getAll().size() == 1);
		Assert.assertTrue(getLeagueService().getAll().size() == 0);
		
		// Delete last unlinked league team
		getLeagueTeamService().delete(lt4_id);
		Assert.assertTrue(getLeagueTeamService().getAll().size() == 0);

	}
	
	
	@Test
	public void testLeagueContainer() {
		
		User usr1 = getIdentityService().getUser("test1");
		ProjectionProfile p1 = new ProjectionProfile();
		p1.setProjection_service(ProjectionProfileService.PROJECTION_SERVICE_STEAMER);
		p1.setProjection_period(ProjectionProfileService.PROJECTION_PERIOD_PRESEASON);
		p1.setProjected_year(2016);
		
		String uname = usr1.getUsername();
		
		// Create a league
		League l1 = new League();
		l1.setMlb_leagues(LeagueService.MLB_LEAGUES_AL);
		l1.setNum_of_teams(11);
		
		l1.setCat_hitter_avg(true);
		l1.setCat_hitter_hr(true);
		l1.setCat_hitter_r(true);
		l1.setCat_hitter_rbi(true);
		l1.setCat_hitter_sb(true);
		l1.setCat_pitcher_era(true);
		l1.setCat_pitcher_saves(true);
		l1.setCat_pitcher_so(true);
		l1.setCat_pitcher_whip(true);
		l1.setCat_pitcher_wins(true);
		
		l1.setLeague_name("League 1");
		l1.setLeague_site("CBS");
		l1.setLeague_year(2016);
		l1.setNum_1b(1);
		l1.setNum_2b(1);
		l1.setNum_3b(1);
		l1.setNum_c(2);
		l1.setNum_ci(1);
		l1.setNum_mi(1);
		l1.setNum_of(5);
		l1.setNum_res(3);
		l1.setNum_ss(1);
		l1.setNum_util(1);
		l1.setNum_p(9);
		l1.setTeam_salary(260);
		
		// Create list of teams 
		LeagueTeam lt1 = new LeagueTeam();
		lt1.setTeam_name("Team1");
		lt1.setOwner_name("Owner1");
		lt1.setIsuserowner(true);
		lt1.setTeam_num(1);
		
		LeagueTeam lt2 = new LeagueTeam();
		lt2.setTeam_name("Team2");
		lt2.setOwner_name("Owner2");
		lt2.setTeam_num(2);
		
		LeagueTeam lt3 = new LeagueTeam();
		lt3.setTeam_name("Team3");
		lt3.setOwner_name("Owner3");
		lt3.setTeam_num(3);
		lt3.setSalary_adjustment(30);
		
		List<LeagueTeam> teamlist = new ArrayList<LeagueTeam>();
		teamlist.add(lt1);
		teamlist.add(lt2);
		teamlist.add(lt3);
		
		LeagueCreateContainer container = new LeagueCreateContainer();
		container.setLeague(l1);
		container.setLeague_teams(teamlist);
		container.setProfile(p1);
		
		long league_id = getLeagueService().saveNewLeague(container, uname);
		
		List<League> leaguelist = getLeagueService().getUserLeague("League 1", 2016, uname);
		
		// Test saveNewLeague saved one league
		Assert.assertTrue(leaguelist.size() == 1);
		
		List<LeagueTeam> teamlist2 = leaguelist.get(0).getLeague_teams();
		
		// Test saveNewLeague saved league has 3 teams
		Assert.assertTrue(teamlist2.size() == 3);
		
		// Test that team salaries are correct
		for (LeagueTeam team : teamlist2){
			if (team.getTeam_name().equals("Team1")){
				Assert.assertTrue(team.getAdj_starting_salary() == 260);
			}
			if (team.getTeam_name().equals("Team3")){
				Assert.assertTrue(team.getAdj_starting_salary() == 290);
			}
		}
		
		ProjectionProfile p2 = leaguelist.get(0).getProjection_profile();
		
		// Test saveNewLeague saved profile
		Assert.assertTrue(p2.getProjection_service() == ProjectionProfileService.PROJECTION_SERVICE_STEAMER);
		
		// Test saving existing league causes exception
		try {
			getLeagueService().saveNewLeague(container, uname);
		    fail( "Saving league did not cause exception for duplicate league." );
		} catch (IllegalArgumentException e) {
		}
		
		// Test Non-existent profile causes exception
		ProjectionProfile p3 = new ProjectionProfile();
		p3.setProjection_service("Fake Profile");
		p3.setProjection_period(ProjectionProfileService.PROJECTION_PERIOD_PRESEASON);
		p3.setProjected_year(2016);
		
		LeagueCreateContainer container2 = new LeagueCreateContainer();
		container2.setLeague(l1);
		container2.setLeague_teams(teamlist);
		container2.setProfile(p3);

		try {
			getLeagueService().saveNewLeague(container2, uname);
		    fail( "Saving league did not cause exception for non-existent profile." );
		} catch (IllegalArgumentException e) {
		}
		
		// Test getLeagueRosterItems
		List<LeagueRosterItem> items = getLeagueService().getLeagueRoster(league_id, uname);
		Assert.assertTrue(items.size() == 26);

	}
	
	@Test
	public void testPositionZPriorityContainer() {
		
		// KEY
		//
		// Position		Z-score		Priority
		// c			44.56		1
		// 1b			105.2		5
		// 2b			88.5		3
		// ss			80.666		2
		// 3b			95.12		4
		// of			120			7
		// p			110.1		6
		
		PositionZPriorityContainer c = new PositionZPriorityContainer(44.56, 105.2,88.5,80.666,95.12,120.0,110.1,1000.0);
//		System.out.println(c.getPos_priority().get(0));
//		System.out.println(c.getPos_priority().get(1));
//		System.out.println(c.getPos_priority().get(2));
//		System.out.println(c.getPos_priority().get(3));
//		System.out.println(c.getPos_priority().get(4));
//		System.out.println(c.getPos_priority().get(5));
//		System.out.println(c.getPos_priority().get(6));
		Assert.assertTrue(c.getPos_priority().get(0) == "c");
		Assert.assertTrue(c.getPos_priority().get(4) == "1b");
		Assert.assertTrue(c.getPos_priority().get(2) == "2b");
		Assert.assertTrue(c.getPos_priority().get(1) == "ss");
		Assert.assertTrue(c.getPos_priority().get(3) == "3b");
		Assert.assertTrue(c.getPos_priority().get(6) == "of");
		Assert.assertTrue(c.getPos_priority().get(5) == "p");
		
	}
	
	/**
	 * Description:	 Test league player auction calculation - 10 cat league
	 */
	@Test
	public void testLeaguePlayerOutput() {
		// League test 
		System.out.println("***************************************************************");
		System.out.println("****************** STARTING PLAYER OUTPUT TEST 1 **************");
		System.out.println("***************************************************************");
		
		User usr1 = getIdentityService().getUser("test1");
		ProjectionProfile p1 = getProjectionProfileService().get(ProjectionProfileService.PROJECTION_SERVICE_STEAMER,
				ProjectionProfileService.PROJECTION_PERIOD_PRESEASON, 2016);
		
		String uname = usr1.getUsername();
		
		// Test getUserLeague = 0
		Assert.assertTrue(getLeagueService().getUserLeague("League 1", 2016, usr1.getUsername()).size() == 0);
		
		// Create a league
		League l1 = new League();
		l1.setMlb_leagues(LeagueService.MLB_LEAGUES_AL);
		l1.setNum_of_teams(11);
		l1.setAvg_hitter_ab(6500);
		l1.setAvg_hitter_ba(0.258);
		l1.setAvg_hitter_hits(l1.getAvg_hitter_ab()*l1.getAvg_hitter_ba());
		
		l1.setAvg_pitcher_era(3.96);
		l1.setAvg_pitcher_ip(1500);
		l1.setAvg_pitcher_whip(1.27);
		l1.setAvg_pitcher_er((l1.getAvg_pitcher_era()/9)*l1.getAvg_pitcher_ip());
		l1.setAvg_pitcher_bbplushits(l1.getAvg_pitcher_ip()*l1.getAvg_pitcher_whip());
		
		l1.setCat_hitter_avg(true);
		l1.setCat_hitter_hr(true);
		l1.setCat_hitter_r(true);
		l1.setCat_hitter_rbi(true);
		l1.setCat_hitter_sb(true);
		l1.setCat_pitcher_era(true);
		l1.setCat_pitcher_saves(true);
		l1.setCat_pitcher_so(true);
		l1.setCat_pitcher_whip(true);
		l1.setCat_pitcher_wins(true);
		
		l1.setLeague_name("League 1");
		l1.setLeague_site("CBS");
		l1.setLeague_year(2016);
		l1.setNum_1b(1);
		l1.setNum_2b(1);
		l1.setNum_3b(1);
		l1.setNum_c(2);
		l1.setNum_ci(1);
		l1.setNum_mi(1);
		l1.setNum_of(5);
		l1.setNum_res(3);
		l1.setNum_ss(1);
		l1.setNum_util(1);
		l1.setNum_p(9);
		l1.setTeam_salary(260);
		l1.setUser(usr1);
		l1.setProjection_profile(getProjectionProfileService().get(p1.getId()));
		
		long l1_id = getLeagueService().save(l1, usr1.getUsername());
		League l1_r = getLeagueService().get(l1_id);

		List<PlayerProjected> playeroutput = getLeagueService().getLeaguePlayerData(l1_id, usr1.getUsername());
		
		// Test count of AL and FA league players
		Assert.assertTrue(playeroutput.size() == 850);
		
		// Parser
		Reader in = null;
		try {
			in = new FileReader(testfile2);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		getPlayerProjectedService().updatePlayerProjections(parseProjections(in), p1, usr1.getUsername());
		
		playeroutput = getLeagueService().getLeaguePlayerData(l1_id, usr1.getUsername());
		
		// Test updated count of AL and FA league players
		Assert.assertTrue(playeroutput.size() == 850);
		
		// Check if deleted player projections are deleted from League Players
		Assert.assertTrue(this.findPlayerByName(playeroutput, "Francisco Lindor").isEmpty());
		Assert.assertTrue(this.findPlayerByName(playeroutput, "Adam Eaton").isEmpty());
		Assert.assertTrue(this.findPlayerByName(playeroutput, "Jose Altuve").size() == 1); // player exists, verifies test
		
		// Check for new projection player in League Players
		Assert.assertTrue(this.findPlayerByName(playeroutput, "Kole Calhoun2").size() == 1);
		
		// Check for updated projection player in League Players
		Assert.assertTrue(this.findPlayerByName(playeroutput, "Ian Kinsler").size() == 1); //  Checks if player exists
		Assert.assertTrue(this.findPlayerByName(playeroutput, "Ian Kinsler").get(0).getDc_status() == null);   //  Checks if update worked
		
		// Test Auction Values
		// Original commented out tests were before 2 pass system was set up
//		Assert.assertTrue(this.findPlayerByName(playeroutput, "Chris Sale").get(0).getInit_auction_value() == 54);
//		Assert.assertTrue(this.findPlayerByName(playeroutput, "Chris Davis").get(0).getInit_auction_value() == 18);
//		Assert.assertTrue(this.findPlayerByName(playeroutput, "Starlin Castro").get(0).getInit_auction_value() == 17);
//		Assert.assertTrue(this.findPlayerByName(playeroutput, "Mike Trout").get(0).getInit_auction_value() == 43);
//		Assert.assertTrue(this.findPlayerByName(playeroutput, "Aroldis Chapman").get(0).getInit_auction_value() == 28);
//		Assert.assertTrue(this.findPlayerByName(playeroutput, "Salvador Perez").get(0).getInit_auction_value() == 18);
		
		System.out.println("Starlin Castro Auction Value: " + this.findPlayerByName(playeroutput, "Starlin Castro").get(0).getInit_auction_value());
		System.out.println("Starlin Castro Z Value: " + this.findPlayerByName(playeroutput, "Starlin Castro").get(0).getTotal_z());
		
		System.out.println("Mike Trout Auction Value: " + this.findPlayerByName(playeroutput, "Mike Trout").get(0).getInit_auction_value());
		System.out.println("Mike Trout Z Value: " + this.findPlayerByName(playeroutput, "Mike Trout").get(0).getTotal_z());
		
		System.out.println("Aroldis Chapman Auction Value: " + this.findPlayerByName(playeroutput, "Aroldis Chapman").get(0).getInit_auction_value());
		System.out.println("Aroldis Chapman Z Value: " + this.findPlayerByName(playeroutput, "Aroldis Chapman").get(0).getTotal_z());
		
		System.out.println("Danny Valencia Auction Value: " + this.findPlayerByName(playeroutput, "Danny Valencia").get(0).getInit_auction_value());
		System.out.println("Danny Valencia Z Value: " + this.findPlayerByName(playeroutput, "Danny Valencia").get(0).getTotal_z());
		
		System.out.println("Chris Sale Auction Value: " + this.findPlayerByName(playeroutput, "Chris Sale").get(0).getInit_auction_value());
		System.out.println("Chris Sale Z Value: " + this.findPlayerByName(playeroutput, "Chris Sale").get(0).getTotal_z());
		
		Assert.assertTrue(this.findPlayerByName(playeroutput, "Chris Sale").get(0).getInit_auction_value() == 46);
		Assert.assertTrue(this.findPlayerByName(playeroutput, "Mike Trout").get(0).getInit_auction_value() == 43);
		Assert.assertTrue(this.findPlayerByName(playeroutput, "Aroldis Chapman").get(0).getInit_auction_value() == 21);
		Assert.assertTrue(this.findPlayerByName(playeroutput, "Chris Davis").get(0).getInit_auction_value() == 20);
		Assert.assertTrue(this.findPlayerByName(playeroutput, "Salvador Perez").get(0).getInit_auction_value() == 18);
		Assert.assertTrue(this.findPlayerByName(playeroutput, "Starlin Castro").get(0).getInit_auction_value() == 17); // value= 17
		
		Assert.assertTrue(this.findPlayerByName(playeroutput, "Logan Forsythe").get(0).getInit_auction_value() == 11);
		Assert.assertTrue(this.findPlayerByName(playeroutput, "Stephen Vogt").get(0).getInit_auction_value() == 11);
		Assert.assertTrue(this.findPlayerByName(playeroutput, "Brock Holt").get(0).getInit_auction_value() == 6);
		Assert.assertTrue(this.findPlayerByName(playeroutput, "Danny Valencia").get(0).getInit_auction_value() == 11);
		
		// Create teams in league
		LeagueTeam lt1 = new LeagueTeam();
		lt1.setTeam_name("Team1");
		lt1.setOwner_name("Owner1");
		lt1.setIsuserowner(true);
		
		LeagueTeam lt2 = new LeagueTeam();
		lt2.setTeam_name("Team2");
		lt2.setOwner_name("Owner2");
		
		LeagueTeam lt3 = new LeagueTeam();
		lt3.setTeam_name("Team3");
		lt3.setOwner_name("Owner3");

		long lt1_id = getLeagueTeamService().save(lt1, usr1.getUsername());
		long lt2_id = getLeagueTeamService().save(lt2, usr1.getUsername());
		long lt3_id = getLeagueTeamService().save(lt3, usr1.getUsername());
		
		LeagueTeam lt1_r = getLeagueTeamService().get(lt1_id);
		LeagueTeam lt2_r = getLeagueTeamService().get(lt2_id);
		LeagueTeam lt3_r = getLeagueTeamService().get(lt3_id);
		
		getLeagueService().addLeagueTeam(l1_id, lt1_id, usr1.getUsername());
		getLeagueService().addLeagueTeam(l1_id, lt2_id, usr1.getUsername());
		getLeagueService().addLeagueTeam(l1_id, lt3_id, usr1.getUsername());
		
		// Test getUserLeague = 1
		Assert.assertTrue(getLeagueService().getUserLeague("League 1", 2016, usr1.getUsername()).size() == 1);
		
		PlayerProjected chris_sale = ofy().load().type(PlayerProjected.class).filter("full_name", "Chris Sale").list().get(0);
		PlayerProjected chris_davis = ofy().load().type(PlayerProjected.class).filter("full_name", "Chris Davis").list().get(0);
		PlayerProjected salvador_perez = ofy().load().type(PlayerProjected.class).filter("full_name", "Salvador Perez").list().get(0);
		PlayerProjected starlin_castro = ofy().load().type(PlayerProjected.class).filter("full_name", "Starlin Castro").list().get(0);
		PlayerProjected aroldis_chapman = ofy().load().type(PlayerProjected.class).filter("full_name", "Aroldis Chapman").list().get(0);
		// Mike Trout used for player note test
		PlayerProjected mike_trout = ofy().load().type(PlayerProjected.class).filter("full_name", "Mike Trout").list().get(0);
		
		LeaguePlayerInputDraftContainer cont_sale = new LeaguePlayerInputDraftContainer();
		cont_sale.setLeague_id(l1_id);
		cont_sale.setLeague_team_id(lt1_id);
		cont_sale.setPlayer_projected_id(chris_sale.getId());
		cont_sale.setTeam_roster_position(LeaguePlayerService.TEAM_ROSTER_POSITION_P);
		cont_sale.setTeam_player_salary(30);
		
		LeaguePlayerInputDraftContainer cont_davis = new LeaguePlayerInputDraftContainer();
		cont_davis.setLeague_id(l1_id);
		cont_davis.setLeague_team_id(lt1_id);
		cont_davis.setPlayer_projected_id(chris_davis.getId());
		cont_davis.setTeam_roster_position(LeaguePlayerService.TEAM_ROSTER_POSITION_1B);
		cont_davis.setTeam_player_salary(20);
		
		LeaguePlayerInputDraftContainer cont_perez = new LeaguePlayerInputDraftContainer();
		cont_perez.setLeague_id(l1_id);
		cont_perez.setLeague_team_id(lt1_id);
		cont_perez.setPlayer_projected_id(salvador_perez.getId());
		cont_perez.setTeam_roster_position(LeaguePlayerService.TEAM_ROSTER_POSITION_C);
		cont_perez.setTeam_player_salary(10);
		
		LeaguePlayerInputDraftContainer cont_castro = new LeaguePlayerInputDraftContainer();
		cont_castro.setLeague_id(l1_id);
		cont_castro.setLeague_team_id(lt1_id);
		cont_castro.setPlayer_projected_id(starlin_castro.getId());
		cont_castro.setTeam_roster_position(LeaguePlayerService.TEAM_ROSTER_POSITION_2B);
		cont_castro.setTeam_player_salary(10);
		
		// Container for testing drafting unknown player (hitter)
		LeaguePlayerInputDraftContainer cont_unknown_h = new LeaguePlayerInputDraftContainer();
		cont_unknown_h.setLeague_id(l1_id);
		cont_unknown_h.setLeague_team_id(lt1_id);
		cont_unknown_h.setTeam_roster_position(LeaguePlayerService.TEAM_ROSTER_POSITION_2B);
		cont_unknown_h.setTeam_player_salary(12);
		cont_unknown_h.setUnknownplayer(true);
		cont_unknown_h.setUnknown_player_pitcher_hitter(getPlayerProjectedService().PITCHER_HITTER_HITTER);
		cont_unknown_h.setUnknown_player_name("Unknown PlayerH");
		
		// Container for testing drafting unknown player (pitcher)
		LeaguePlayerInputDraftContainer cont_unknown_p1 = new LeaguePlayerInputDraftContainer();
		cont_unknown_p1.setLeague_id(l1_id);
		cont_unknown_p1.setLeague_team_id(lt1_id);
		cont_unknown_p1.setTeam_roster_position(LeaguePlayerService.TEAM_ROSTER_POSITION_P);
		cont_unknown_p1.setTeam_player_salary(9);
		cont_unknown_p1.setUnknownplayer(true);
		cont_unknown_p1.setUnknown_player_pitcher_hitter(getPlayerProjectedService().PITCHER_HITTER_PITCHER);
		cont_unknown_p1.setUnknown_player_name("Unknown PlayerP1");
		
		// Container for testing drafting unknown player (pitcher)
		LeaguePlayerInputDraftContainer cont_unknown_p2 = new LeaguePlayerInputDraftContainer();
		cont_unknown_p2.setLeague_id(l1_id);
		cont_unknown_p2.setLeague_team_id(lt1_id);
		cont_unknown_p2.setTeam_roster_position(LeaguePlayerService.TEAM_ROSTER_POSITION_P);
		cont_unknown_p2.setTeam_player_salary(19);
		cont_unknown_p2.setUnknownplayer(true);
		cont_unknown_p2.setUnknown_player_pitcher_hitter(getPlayerProjectedService().PITCHER_HITTER_PITCHER);
		cont_unknown_p2.setUnknown_player_name("Unknown PlayerP2");
		
		String player_note = "This is a test player note. This will test the player note capability";
		LeaguePlayerInputDraftContainer cont_chapman = new LeaguePlayerInputDraftContainer();
		cont_chapman.setLeague_id(l1_id);
		cont_chapman.setLeague_team_id(lt2_id);
		cont_chapman.setPlayer_projected_id(aroldis_chapman.getId());
		cont_chapman.setTeam_roster_position(LeaguePlayerService.TEAM_ROSTER_POSITION_P);
		cont_chapman.setTeam_player_salary(5);
		
		// Chapman to test player note capability
		LeaguePlayerInputInfoContainer ncont_chapman = new LeaguePlayerInputInfoContainer();
		ncont_chapman.setLeague_id(l1_id);
		ncont_chapman.setPlayer_projected_id(aroldis_chapman.getId());
		ncont_chapman.setTeam_player_note(player_note);
		ncont_chapman.setCustom_position_flag(false);
		
		// Mike Trout to test player note capability
		LeaguePlayerInputInfoContainer ncont_trout = new LeaguePlayerInputInfoContainer();
		ncont_trout.setLeague_id(l1_id);
		ncont_trout.setPlayer_projected_id(mike_trout.getId());
		ncont_trout.setTeam_player_note(player_note);
		ncont_trout.setCustom_position_flag(false);
		
		LeaguePlayerInputInfoContainer pcont_chapman = new LeaguePlayerInputInfoContainer();
		pcont_chapman.setLeague_id(l1_id);
		pcont_chapman.setPlayer_projected_id(aroldis_chapman.getId());
		pcont_chapman.setTeam_player_note(player_note);
		
		LeaguePlayerInputInfoContainer pcont_trout = new LeaguePlayerInputInfoContainer();
		pcont_trout.setLeague_id(l1_id);
		pcont_trout.setPlayer_projected_id(mike_trout.getId());
		pcont_trout.setCustom_position("OF,2B");
		pcont_trout.setCustom_position_flag(true);
		pcont_trout.setTeam_player_note(player_note);
		pcont_trout.setFavorite_flag(true);

		long sale_id = getLeaguePlayerService().draftLeaguePlayer(cont_sale, uname);
		long davis_id = getLeaguePlayerService().draftLeaguePlayer(cont_davis, uname);
		long perez_id = getLeaguePlayerService().draftLeaguePlayer(cont_perez, uname);
		long castro_id = getLeaguePlayerService().draftLeaguePlayer(cont_castro, uname);
		long chapman_id = getLeaguePlayerService().updateLeaguePlayerInfo(pcont_chapman, uname);
		// chapman_id = getLeaguePlayerService().updateLeaguePlayerInfo(ncont_chapman, uname);
		chapman_id = getLeaguePlayerService().draftLeaguePlayer(cont_chapman, uname);
		long trout_id = getLeaguePlayerService().updateLeaguePlayerInfo(ncont_trout, uname);
		long unknown_h_id = getLeaguePlayerService().draftLeaguePlayer(cont_unknown_h, uname);
		long unknown_p1_id = getLeaguePlayerService().draftLeaguePlayer(cont_unknown_p1, uname);
		long unknown_p2_id = getLeaguePlayerService().draftLeaguePlayer(cont_unknown_p2, uname);
		trout_id = getLeaguePlayerService().updateLeaguePlayerInfo(pcont_trout, uname);
		
		
		// Test updated auction values due to custom position change
		List<PlayerProjected> playeroutput2 = getLeagueService().getLeaguePlayerData(l1_id, usr1.getUsername());
		System.out.println("Mike Trout Updated Auction Value: " + this.findPlayerByName(playeroutput2, "Mike Trout").get(0).getInit_auction_value());
		System.out.println("Mike Trout Cust Pos Flag: " + this.findPlayerByName(playeroutput2, "Mike Trout").get(0).isCustom_position_flag());
		System.out.println("Mike Trout Cust Position: " + this.findPlayerByName(playeroutput2, "Mike Trout").get(0).getCustom_position());
		Assert.assertTrue(this.findPlayerByName(playeroutput2, "Chris Sale").get(0).getInit_auction_value() == 46);
		Assert.assertTrue(this.findPlayerByName(playeroutput2, "Chris Davis").get(0).getInit_auction_value() == 20);
		Assert.assertTrue(this.findPlayerByName(playeroutput2, "Mike Trout").get(0).getInit_auction_value() == 47);
		
		// Test player favorite capability
		Assert.assertTrue(this.findPlayerByName(playeroutput2, "Mike Trout").get(0).isFavorite_flag());
		
		// Test getLeaguePlayersbyLeague
		Assert.assertTrue(getLeaguePlayerService().getLeaguePlayersByLeague(l1_id, uname).size() == 9);
		
		// Test getLeaguePlayersbyTeam
		Assert.assertTrue(getLeaguePlayerService().getLeaguePlayersByTeam(lt1_id, uname).size() == 7);
		Assert.assertTrue(getLeaguePlayerService().getLeaguePlayersByTeam(lt2_id, uname).size() == 1);
		Assert.assertTrue(getLeaguePlayerService().getLeaguePlayersByTeam(lt3_id, uname).size() == 0);
		
		// Test league player note and custom eligibility
		LeaguePlayer trout = getLeaguePlayerService().get(trout_id);
		Assert.assertTrue(trout.getTeam_player_note().equals(player_note));
		Assert.assertTrue(trout.isCustom_position_flag());
		Assert.assertTrue(trout.getCustom_position().equals("OF,2B"));
		
		// Test drafted player attributes and note
		LeaguePlayer chapman = getLeaguePlayerService().getLeaguePlayersByTeam(lt2_id, uname).get(0);
		Assert.assertTrue(chapman.getTeam_roster_position().equals(LeaguePlayerService.TEAM_ROSTER_POSITION_P));
		Assert.assertTrue(chapman.getTeam_player_salary() == 5);
		Assert.assertTrue(chapman.getLeague_team().getId() == lt2_id);
		Assert.assertTrue(chapman.getTeam_player_note().equals(player_note));
		Assert.assertTrue(!chapman.isCustom_position_flag());
		
		// Test unknown player attributes
		LeaguePlayer unknown_h = getLeaguePlayerService().get(unknown_h_id);
		Assert.assertTrue(unknown_h.getTeam_roster_position().equals(LeaguePlayerService.TEAM_ROSTER_POSITION_2B));
		Assert.assertTrue(unknown_h.getTeam_player_salary() == 12);
		Assert.assertTrue(unknown_h.getLeague_team().getId() == lt1_id);
		
		// Test update existing drafted unknown player
		cont_unknown_h.setTeam_player_salary(8);
		cont_unknown_h.setLeague_team_id(lt2_id);
		cont_unknown_h.setTeam_roster_position(LeaguePlayerService.TEAM_ROSTER_POSITION_3B);
		unknown_h_id = getLeaguePlayerService().draftLeaguePlayer(cont_unknown_h, uname);
		Assert.assertTrue(getLeaguePlayerService().getLeaguePlayersByTeam(lt2_id, uname).size() == 2);

		
		unknown_h  = getLeaguePlayerService().get(unknown_h_id);
		Assert.assertTrue(unknown_h.getTeam_roster_position().equals(LeaguePlayerService.TEAM_ROSTER_POSITION_3B));
		Assert.assertTrue(unknown_h.getTeam_player_salary() == 8);
		Assert.assertTrue(unknown_h.getLeague_team().getId() == lt2_id);
		
		// Test update existing drafted player
		cont_chapman.setTeam_player_salary(7);
		chapman_id = getLeaguePlayerService().draftLeaguePlayer(cont_chapman, uname);
		Assert.assertTrue(getLeaguePlayerService().getLeaguePlayersByTeam(lt2_id, uname).size() == 2);
		chapman = getLeaguePlayerService().get(chapman_id);
		Assert.assertTrue(chapman.getTeam_roster_position().equals(LeaguePlayerService.TEAM_ROSTER_POSITION_P));
		Assert.assertTrue(chapman.getTeam_player_salary() == 7);
		Assert.assertTrue(chapman.getLeague_team().getId() == lt2_id);
		
		// Test undraft unknown player
		System.out.println("Undrafting unknown playerp1");
		LeaguePlayerInputDraftContainer ud_unknown_container = new LeaguePlayerInputDraftContainer();
		ud_unknown_container.setUnknown_player_name("Unknown PlayerP1");
		ud_unknown_container.setUnknownplayer(true);
		ud_unknown_container.setLeague_id(l1_id);
		getLeaguePlayerService().undraftLeaguePlayer(ud_unknown_container, uname);
		
		// Test undraft player
		LeaguePlayerInputDraftContainer ud_container = new LeaguePlayerInputDraftContainer();
		ud_container.setPlayer_projected_id(aroldis_chapman.getId());
		ud_container.setLeague_id(l1_id);
		getLeaguePlayerService().undraftLeaguePlayer(ud_container, uname);
		
		// Test league player note and custom elig after undraft
		chapman = getLeaguePlayerService().get(chapman_id);
		Assert.assertTrue(chapman.getTeam_player_note().equals(player_note));
		Assert.assertTrue(!chapman.isCustom_position_flag());
		
		// Test remove custom eligibility
		pcont_chapman = new LeaguePlayerInputInfoContainer();
		pcont_chapman.setLeague_id(l1_id);
		pcont_chapman.setPlayer_projected_id(aroldis_chapman.getId());
		pcont_chapman.setTeam_player_note(player_note);
		pcont_chapman.setCustom_position("");
		pcont_chapman.setCustom_position_flag(false);
		chapman_id = getLeaguePlayerService().updateLeaguePlayerInfo(pcont_chapman, uname);
		chapman = getLeaguePlayerService().get(chapman_id);
		Assert.assertTrue(!chapman.isCustom_position_flag());
		
		// Test getLeaguePlayersbyLeague
		Assert.assertTrue(getLeaguePlayerService().getLeaguePlayersByLeague(l1_id, uname).size() == 8);
		
		// Test getLeaguePlayersbyTeam
		Assert.assertTrue(getLeaguePlayerService().getLeaguePlayersByTeam(lt2_id, uname).size() == 1);
		Assert.assertTrue(getLeaguePlayerService().getLeaguePlayersByTeam(lt1_id, uname).size() == 5);
		
		// Get updated playeroutput after draft
		playeroutput = getLeagueService().getLeaguePlayerData(l1_id, usr1.getUsername());
		
		List<PlayerProjected> po_drafted = new ArrayList<PlayerProjected>();
		
		for (PlayerProjected po : playeroutput){
			
			if (po.getLeagueteam_id() > 0) po_drafted.add(po);
			else if (po.getTeam_player_note() != null)
				po_drafted.add(po);
		}
		
		// Test there are 6 players that were drafted or have notes
		Assert.assertTrue(po_drafted.size() == 8);
		
		PlayerProjected po_sale = new PlayerProjected();
		PlayerProjected po_davis = new PlayerProjected();
		PlayerProjected po_perez = new PlayerProjected();
		PlayerProjected po_castro = new PlayerProjected();
		PlayerProjected po_chapman = new PlayerProjected();
		PlayerProjected po_unknown_h = new PlayerProjected();
		PlayerProjected po_unknown_p2 = new PlayerProjected();
		
		for (PlayerProjected po : po_drafted){
			
			if (po.getFull_name().equals("Chris Sale")) po_sale = po;
			if (po.getFull_name().equals("Chris Davis")) po_davis = po;
			if (po.getFull_name().equals("Salvador Perez")) po_perez = po;
			if (po.getFull_name().equals("Starlin Castro")) po_castro = po;
			if (po.getFull_name().equals("Aroldis Chapman")) po_chapman = po;
			
			if (po.getFull_name().equals("Unknown PlayerH")) po_unknown_h = po;
			if (po.getFull_name().equals("Unknown PlayerP2")) po_unknown_p2 = po;
			
		}
		
		// Test attributes of drafted players from playeroutput
		Assert.assertTrue(po_sale.getLeagueteam_id() == lt1_id);
		Assert.assertTrue(po_sale.getLeagueteam_name().equals("Team1"));
		Assert.assertTrue(po_davis.getTeam_roster_position().equals(LeaguePlayerService.TEAM_ROSTER_POSITION_1B));
		Assert.assertTrue(po_perez.getTeam_player_salary() == 10);
		Assert.assertTrue(po_castro.getTeam_player_salary() == 10);
		Assert.assertTrue(po_chapman.getTeam_player_note().equals(player_note));
		Assert.assertTrue(po_unknown_h.getTeam_player_salary() == 8);
		Assert.assertTrue(po_unknown_p2.getTeam_roster_position().equals(LeaguePlayerService.TEAM_ROSTER_POSITION_P));
		
		// Test Delete a team
		System.out.println("In delete team test, PRE - # of teams: " + getLeagueService().getLeagueTeams(l1_id, uname).size());
		System.out.println("In delete team test, PRE - # of players on team: " + getLeaguePlayerService().getLeaguePlayersByTeam(lt2_id, uname).size());
		getLeagueService().deleteLeagueTeam(l1_id, lt2_id, uname);
		System.out.println("In delete team test, POST - # of teams: " + getLeagueService().getLeagueTeams(l1_id, uname).size());
		System.out.println("In delete team test, POST - # of players on team: " + getLeaguePlayerService().getLeaguePlayersByTeam(lt2_id, uname).size());
		System.out.println("In delete team test, POST - getting league team: " + getLeagueTeamService().get(lt2_id));
		Assert.assertTrue(getLeagueService().getLeagueTeams(l1_id, uname).size() == 2);
		Assert.assertTrue(getLeaguePlayerService().getLeaguePlayersByTeam(lt2_id, uname).size() == 0);
		Assert.assertNull(getLeagueTeamService().get(lt2_id)); 
		
		// Test Delete league
		getLeagueService().deleteLeagueFull(l1_id, uname);
		
		// Verify league has no players
		Assert.assertTrue(getLeaguePlayerService().getAll().size() == 0);
		
		// Verify league has no teams
		Assert.assertTrue(getLeagueTeamService().getAll().size() == 0);
		
		// Verify league was deleted
		Assert.assertTrue(getLeagueService().getAll().size() == 0);
		
	}	
	
	/**
	 * Description:	 Test league player auction calculation - 12 cat league 
	 */
	@Test
	public void testLeaguePlayerOutput2() {
		// League test adding holds and obp
		System.out.println("***************************************************************");
		System.out.println("****************** STARTING PLAYER OUTPUT TEST 2 **************");
		System.out.println("***************************************************************");
		
		User usr1 = getIdentityService().getUser("test1");
		ProjectionProfile p1 = getProjectionProfileService().get(ProjectionProfileService.PROJECTION_SERVICE_STEAMER,
				ProjectionProfileService.PROJECTION_PERIOD_PRESEASON, 2016);
		
		String uname = usr1.getUsername();
		
		// Test getUserLeague = 0
		Assert.assertTrue(getLeagueService().getUserLeague("League 1", 2016, usr1.getUsername()).size() == 0);
		
		// Create a league
		League l1 = new League();
		l1.setMlb_leagues(LeagueService.MLB_LEAGUES_AL);
		l1.setNum_of_teams(11);
		l1.setAvg_hitter_ab(6500);
		l1.setAvg_hitter_ba(0.258);
		l1.setAvg_hitter_hits(l1.getAvg_hitter_ab()*l1.getAvg_hitter_ba());
		l1.setAvg_hitter_obp(0.321);
		l1.setAvg_hitter_bb(583);
		l1.setAvg_hitter_hbp(30);
		l1.setAvg_hitter_pa(7143);
		
		l1.setAvg_pitcher_era(3.96);
		l1.setAvg_pitcher_ip(1500);
		l1.setAvg_pitcher_whip(1.27);
		l1.setAvg_pitcher_er((l1.getAvg_pitcher_era()/9)*l1.getAvg_pitcher_ip());
		l1.setAvg_pitcher_bbplushits(l1.getAvg_pitcher_ip()*l1.getAvg_pitcher_whip());
		
		l1.setCat_hitter_avg(true);
		l1.setCat_hitter_hr(true);
		l1.setCat_hitter_r(true);
		l1.setCat_hitter_rbi(true);
		l1.setCat_hitter_sb(true);
		l1.setCat_hitter_obp(true);
		l1.setCat_pitcher_era(true);
		l1.setCat_pitcher_saves(true);
		l1.setCat_pitcher_so(true);
		l1.setCat_pitcher_whip(true);
		l1.setCat_pitcher_wins(true);
		l1.setCat_pitcher_holds(true);
		
		l1.setLeague_name("League 1");
		l1.setLeague_site("CBS");
		l1.setLeague_year(2016);
		l1.setNum_1b(1);
		l1.setNum_2b(1);
		l1.setNum_3b(1);
		l1.setNum_c(2);
		l1.setNum_ci(1);
		l1.setNum_mi(1);
		l1.setNum_of(5);
		l1.setNum_res(3);
		l1.setNum_ss(1);
		l1.setNum_util(1);
		l1.setNum_p(9);
		l1.setTeam_salary(260);
		l1.setUser(usr1);
		l1.setProjection_profile(getProjectionProfileService().get(p1.getId()));
		
		long l1_id = getLeagueService().save(l1, usr1.getUsername());
		League l1_r = getLeagueService().get(l1_id);

		List<PlayerProjected> playeroutput = getLeagueService().getLeaguePlayerData(l1_id, usr1.getUsername());
		
		// Test count of AL and FA league players
		Assert.assertTrue(playeroutput.size() == 850);
		
		// Parser
		Reader in = null;
		try {
			in = new FileReader(testfile2);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		getPlayerProjectedService().updatePlayerProjections(parseProjections(in), p1, usr1.getUsername());
		
		playeroutput = getLeagueService().getLeaguePlayerData(l1_id, usr1.getUsername());
		
		System.out.println("Mike Trout Projected BB: " + this.findPlayerByName(playeroutput, "Mike Trout").get(0).getHitter_bb());
		System.out.println("Mike Trout Projected PA: " + this.findPlayerByName(playeroutput, "Mike Trout").get(0).getHitter_pa());
		System.out.println("Mike Trout Projected HITS: " + this.findPlayerByName(playeroutput, "Mike Trout").get(0).getHitter_hits());
		System.out.println("Mike Trout Projected HBP: " + this.findPlayerByName(playeroutput, "Mike Trout").get(0).getHitter_hbp());
		
		// Test updated count of AL and FA league players
		Assert.assertTrue(playeroutput.size() == 850);
		
		// Check if deleted player projections are deleted from League Players
		Assert.assertTrue(this.findPlayerByName(playeroutput, "Francisco Lindor").isEmpty());
		Assert.assertTrue(this.findPlayerByName(playeroutput, "Adam Eaton").isEmpty());
		Assert.assertTrue(this.findPlayerByName(playeroutput, "Jose Altuve").size() == 1); // player exists, verifies test
		
		// Check for new projection player in League Players
		Assert.assertTrue(this.findPlayerByName(playeroutput, "Kole Calhoun2").size() == 1);
		
		// Check for updated projection player in League Players
		Assert.assertTrue(this.findPlayerByName(playeroutput, "Ian Kinsler").size() == 1); //  Checks if player exists
		Assert.assertTrue(this.findPlayerByName(playeroutput, "Ian Kinsler").get(0).getDc_status() == null);   //  Checks if update worked
		
		// Test Auction Values
		System.out.println("Starlin Castro Auction Value: " + this.findPlayerByName(playeroutput, "Starlin Castro").get(0).getInit_auction_value());
		System.out.println("Starlin Castro Z Value: " + this.findPlayerByName(playeroutput, "Starlin Castro").get(0).getTotal_z());
		
		System.out.println("Mike Trout Auction Value: " + this.findPlayerByName(playeroutput, "Mike Trout").get(0).getInit_auction_value());
		System.out.println("Mike Trout Z Value: " + this.findPlayerByName(playeroutput, "Mike Trout").get(0).getTotal_z());
		
		System.out.println("Aroldis Chapman Auction Value: " + this.findPlayerByName(playeroutput, "Aroldis Chapman").get(0).getInit_auction_value());
		System.out.println("Aroldis Chapman Z Value: " + this.findPlayerByName(playeroutput, "Aroldis Chapman").get(0).getTotal_z());
		
		System.out.println("Danny Valencia Auction Value: " + this.findPlayerByName(playeroutput, "Danny Valencia").get(0).getInit_auction_value());
		System.out.println("Danny Valencia Z Value: " + this.findPlayerByName(playeroutput, "Danny Valencia").get(0).getTotal_z());
		
		System.out.println("Chris Sale Auction Value: " + this.findPlayerByName(playeroutput, "Chris Sale").get(0).getInit_auction_value());
		System.out.println("Chris Sale Z Value: " + this.findPlayerByName(playeroutput, "Chris Sale").get(0).getTotal_z());
		
		Assert.assertTrue(this.findPlayerByName(playeroutput, "Chris Sale").get(0).getInit_auction_value() == 41);
		Assert.assertTrue(this.findPlayerByName(playeroutput, "Mike Trout").get(0).getInit_auction_value() == 60);
		Assert.assertTrue(this.findPlayerByName(playeroutput, "Aroldis Chapman").get(0).getInit_auction_value() == 17);
		Assert.assertTrue(this.findPlayerByName(playeroutput, "Chris Davis").get(0).getInit_auction_value() == 23);
		Assert.assertTrue(this.findPlayerByName(playeroutput, "Salvador Perez").get(0).getInit_auction_value() == 16);
		Assert.assertTrue(this.findPlayerByName(playeroutput, "Starlin Castro").get(0).getInit_auction_value() == 16); 
		
		Assert.assertTrue(this.findPlayerByName(playeroutput, "Logan Forsythe").get(0).getInit_auction_value() == 13);
		Assert.assertTrue(this.findPlayerByName(playeroutput, "Stephen Vogt").get(0).getInit_auction_value() == 13);
		Assert.assertTrue(this.findPlayerByName(playeroutput, "Brock Holt").get(0).getInit_auction_value() == 10);
		Assert.assertTrue(this.findPlayerByName(playeroutput, "Danny Valencia").get(0).getInit_auction_value() == 7);
		
		// Create teams in league
		LeagueTeam lt1 = new LeagueTeam();
		lt1.setTeam_name("Team1");
		lt1.setOwner_name("Owner1");
		lt1.setIsuserowner(true);
		
		LeagueTeam lt2 = new LeagueTeam();
		lt2.setTeam_name("Team2");
		lt2.setOwner_name("Owner2");
		
		LeagueTeam lt3 = new LeagueTeam();
		lt3.setTeam_name("Team3");
		lt3.setOwner_name("Owner3");

		long lt1_id = getLeagueTeamService().save(lt1, usr1.getUsername());
		long lt2_id = getLeagueTeamService().save(lt2, usr1.getUsername());
		long lt3_id = getLeagueTeamService().save(lt3, usr1.getUsername());
		
		LeagueTeam lt1_r = getLeagueTeamService().get(lt1_id);
		LeagueTeam lt2_r = getLeagueTeamService().get(lt2_id);
		LeagueTeam lt3_r = getLeagueTeamService().get(lt3_id);
		
		getLeagueService().addLeagueTeam(l1_id, lt1_id, usr1.getUsername());
		getLeagueService().addLeagueTeam(l1_id, lt2_id, usr1.getUsername());
		getLeagueService().addLeagueTeam(l1_id, lt3_id, usr1.getUsername());
		
		// Test getUserLeague = 1
		Assert.assertTrue(getLeagueService().getUserLeague("League 1", 2016, usr1.getUsername()).size() == 1);
		
		PlayerProjected chris_sale = ofy().load().type(PlayerProjected.class).filter("full_name", "Chris Sale").list().get(0);
		PlayerProjected chris_davis = ofy().load().type(PlayerProjected.class).filter("full_name", "Chris Davis").list().get(0);
		PlayerProjected salvador_perez = ofy().load().type(PlayerProjected.class).filter("full_name", "Salvador Perez").list().get(0);
		PlayerProjected starlin_castro = ofy().load().type(PlayerProjected.class).filter("full_name", "Starlin Castro").list().get(0);
		PlayerProjected aroldis_chapman = ofy().load().type(PlayerProjected.class).filter("full_name", "Aroldis Chapman").list().get(0);
		// Mike Trout used for player note test
		PlayerProjected mike_trout = ofy().load().type(PlayerProjected.class).filter("full_name", "Mike Trout").list().get(0);
		
		LeaguePlayerInputDraftContainer cont_sale = new LeaguePlayerInputDraftContainer();
		cont_sale.setLeague_id(l1_id);
		cont_sale.setLeague_team_id(lt1_id);
		cont_sale.setPlayer_projected_id(chris_sale.getId());
		cont_sale.setTeam_roster_position(LeaguePlayerService.TEAM_ROSTER_POSITION_P);
		cont_sale.setTeam_player_salary(30);
		
		LeaguePlayerInputDraftContainer cont_davis = new LeaguePlayerInputDraftContainer();
		cont_davis.setLeague_id(l1_id);
		cont_davis.setLeague_team_id(lt1_id);
		cont_davis.setPlayer_projected_id(chris_davis.getId());
		cont_davis.setTeam_roster_position(LeaguePlayerService.TEAM_ROSTER_POSITION_1B);
		cont_davis.setTeam_player_salary(20);
		
		LeaguePlayerInputDraftContainer cont_perez = new LeaguePlayerInputDraftContainer();
		cont_perez.setLeague_id(l1_id);
		cont_perez.setLeague_team_id(lt1_id);
		cont_perez.setPlayer_projected_id(salvador_perez.getId());
		cont_perez.setTeam_roster_position(LeaguePlayerService.TEAM_ROSTER_POSITION_C);
		cont_perez.setTeam_player_salary(10);
		
		LeaguePlayerInputDraftContainer cont_castro = new LeaguePlayerInputDraftContainer();
		cont_castro.setLeague_id(l1_id);
		cont_castro.setLeague_team_id(lt1_id);
		cont_castro.setPlayer_projected_id(starlin_castro.getId());
		cont_castro.setTeam_roster_position(LeaguePlayerService.TEAM_ROSTER_POSITION_2B);
		cont_castro.setTeam_player_salary(10);
		
		// Container for testing drafting unknown player (hitter)
		LeaguePlayerInputDraftContainer cont_unknown_h = new LeaguePlayerInputDraftContainer();
		cont_unknown_h.setLeague_id(l1_id);
		cont_unknown_h.setLeague_team_id(lt1_id);
		cont_unknown_h.setTeam_roster_position(LeaguePlayerService.TEAM_ROSTER_POSITION_2B);
		cont_unknown_h.setTeam_player_salary(12);
		cont_unknown_h.setUnknownplayer(true);
		cont_unknown_h.setUnknown_player_pitcher_hitter(getPlayerProjectedService().PITCHER_HITTER_HITTER);
		cont_unknown_h.setUnknown_player_name("Unknown PlayerH");
		
		// Container for testing drafting unknown player (pitcher)
		LeaguePlayerInputDraftContainer cont_unknown_p1 = new LeaguePlayerInputDraftContainer();
		cont_unknown_p1.setLeague_id(l1_id);
		cont_unknown_p1.setLeague_team_id(lt1_id);
		cont_unknown_p1.setTeam_roster_position(LeaguePlayerService.TEAM_ROSTER_POSITION_P);
		cont_unknown_p1.setTeam_player_salary(9);
		cont_unknown_p1.setUnknownplayer(true);
		cont_unknown_p1.setUnknown_player_pitcher_hitter(getPlayerProjectedService().PITCHER_HITTER_PITCHER);
		cont_unknown_p1.setUnknown_player_name("Unknown PlayerP1");
		
		// Container for testing drafting unknown player (pitcher)
		LeaguePlayerInputDraftContainer cont_unknown_p2 = new LeaguePlayerInputDraftContainer();
		cont_unknown_p2.setLeague_id(l1_id);
		cont_unknown_p2.setLeague_team_id(lt1_id);
		cont_unknown_p2.setTeam_roster_position(LeaguePlayerService.TEAM_ROSTER_POSITION_P);
		cont_unknown_p2.setTeam_player_salary(19);
		cont_unknown_p2.setUnknownplayer(true);
		cont_unknown_p2.setUnknown_player_pitcher_hitter(getPlayerProjectedService().PITCHER_HITTER_PITCHER);
		cont_unknown_p2.setUnknown_player_name("Unknown PlayerP2");
		
		String player_note = "This is a test player note. This will test the player note capability";
		LeaguePlayerInputDraftContainer cont_chapman = new LeaguePlayerInputDraftContainer();
		cont_chapman.setLeague_id(l1_id);
		cont_chapman.setLeague_team_id(lt2_id);
		cont_chapman.setPlayer_projected_id(aroldis_chapman.getId());
		cont_chapman.setTeam_roster_position(LeaguePlayerService.TEAM_ROSTER_POSITION_P);
		cont_chapman.setTeam_player_salary(5);
		
		// Chapman to test player note capability
		LeaguePlayerInputInfoContainer ncont_chapman = new LeaguePlayerInputInfoContainer();
		ncont_chapman.setLeague_id(l1_id);
		ncont_chapman.setPlayer_projected_id(aroldis_chapman.getId());
		ncont_chapman.setTeam_player_note(player_note);
		ncont_chapman.setCustom_position_flag(false);
		
		// Mike Trout to test player note capability
		LeaguePlayerInputInfoContainer ncont_trout = new LeaguePlayerInputInfoContainer();
		ncont_trout.setLeague_id(l1_id);
		ncont_trout.setPlayer_projected_id(mike_trout.getId());
		ncont_trout.setTeam_player_note(player_note);
		ncont_trout.setCustom_position_flag(false);
		
		LeaguePlayerInputInfoContainer pcont_chapman = new LeaguePlayerInputInfoContainer();
		pcont_chapman.setLeague_id(l1_id);
		pcont_chapman.setPlayer_projected_id(aroldis_chapman.getId());
		pcont_chapman.setTeam_player_note(player_note);
		
		LeaguePlayerInputInfoContainer pcont_trout = new LeaguePlayerInputInfoContainer();
		pcont_trout.setLeague_id(l1_id);
		pcont_trout.setPlayer_projected_id(mike_trout.getId());
		pcont_trout.setCustom_position("OF,2B");
		pcont_trout.setCustom_position_flag(true);
		pcont_trout.setTeam_player_note(player_note);
		pcont_trout.setFavorite_flag(true);

		long sale_id = getLeaguePlayerService().draftLeaguePlayer(cont_sale, uname);
		long davis_id = getLeaguePlayerService().draftLeaguePlayer(cont_davis, uname);
		long perez_id = getLeaguePlayerService().draftLeaguePlayer(cont_perez, uname);
		long castro_id = getLeaguePlayerService().draftLeaguePlayer(cont_castro, uname);
		long chapman_id = getLeaguePlayerService().updateLeaguePlayerInfo(pcont_chapman, uname);
		// chapman_id = getLeaguePlayerService().updateLeaguePlayerInfo(ncont_chapman, uname);
		chapman_id = getLeaguePlayerService().draftLeaguePlayer(cont_chapman, uname);
		long trout_id = getLeaguePlayerService().updateLeaguePlayerInfo(ncont_trout, uname);
		long unknown_h_id = getLeaguePlayerService().draftLeaguePlayer(cont_unknown_h, uname);
		long unknown_p1_id = getLeaguePlayerService().draftLeaguePlayer(cont_unknown_p1, uname);
		long unknown_p2_id = getLeaguePlayerService().draftLeaguePlayer(cont_unknown_p2, uname);
		trout_id = getLeaguePlayerService().updateLeaguePlayerInfo(pcont_trout, uname);
		
		
		// Test updated auction values due to custom position change
		List<PlayerProjected> playeroutput2 = getLeagueService().getLeaguePlayerData(l1_id, usr1.getUsername());
		System.out.println("Mike Trout Updated Auction Value: " + this.findPlayerByName(playeroutput2, "Mike Trout").get(0).getInit_auction_value());
		System.out.println("Mike Trout Cust Pos Flag: " + this.findPlayerByName(playeroutput2, "Mike Trout").get(0).isCustom_position_flag());
		System.out.println("Mike Trout Cust Position: " + this.findPlayerByName(playeroutput2, "Mike Trout").get(0).getCustom_position());
		Assert.assertTrue(this.findPlayerByName(playeroutput2, "Chris Sale").get(0).getInit_auction_value() == 41);
		Assert.assertTrue(this.findPlayerByName(playeroutput2, "Chris Davis").get(0).getInit_auction_value() == 23);
		Assert.assertTrue(this.findPlayerByName(playeroutput2, "Mike Trout").get(0).getInit_auction_value() == 65);
		
		// Test player favorite capability
		Assert.assertTrue(this.findPlayerByName(playeroutput2, "Mike Trout").get(0).isFavorite_flag());
		
		// Test getLeaguePlayersbyLeague
		Assert.assertTrue(getLeaguePlayerService().getLeaguePlayersByLeague(l1_id, uname).size() == 9);
		
		// Test getLeaguePlayersbyTeam
		Assert.assertTrue(getLeaguePlayerService().getLeaguePlayersByTeam(lt1_id, uname).size() == 7);
		Assert.assertTrue(getLeaguePlayerService().getLeaguePlayersByTeam(lt2_id, uname).size() == 1);
		Assert.assertTrue(getLeaguePlayerService().getLeaguePlayersByTeam(lt3_id, uname).size() == 0);
		
		// Test league player note and custom eligibility
		LeaguePlayer trout = getLeaguePlayerService().get(trout_id);
		Assert.assertTrue(trout.getTeam_player_note().equals(player_note));
		Assert.assertTrue(trout.isCustom_position_flag());
		Assert.assertTrue(trout.getCustom_position().equals("OF,2B"));
		
		// Test drafted player attributes and note
		LeaguePlayer chapman = getLeaguePlayerService().getLeaguePlayersByTeam(lt2_id, uname).get(0);
		Assert.assertTrue(chapman.getTeam_roster_position().equals(LeaguePlayerService.TEAM_ROSTER_POSITION_P));
		Assert.assertTrue(chapman.getTeam_player_salary() == 5);
		Assert.assertTrue(chapman.getLeague_team().getId() == lt2_id);
		Assert.assertTrue(chapman.getTeam_player_note().equals(player_note));
		Assert.assertTrue(!chapman.isCustom_position_flag());
		
		// Test unknown player attributes
		LeaguePlayer unknown_h = getLeaguePlayerService().get(unknown_h_id);
		Assert.assertTrue(unknown_h.getTeam_roster_position().equals(LeaguePlayerService.TEAM_ROSTER_POSITION_2B));
		Assert.assertTrue(unknown_h.getTeam_player_salary() == 12);
		Assert.assertTrue(unknown_h.getLeague_team().getId() == lt1_id);
		
		// Test update existing drafted unknown player
		cont_unknown_h.setTeam_player_salary(8);
		cont_unknown_h.setLeague_team_id(lt2_id);
		cont_unknown_h.setTeam_roster_position(LeaguePlayerService.TEAM_ROSTER_POSITION_3B);
		unknown_h_id = getLeaguePlayerService().draftLeaguePlayer(cont_unknown_h, uname);
		System.out.println("In Test, size of lt2: " + getLeaguePlayerService().getLeaguePlayersByTeam(lt2_id, uname).size());
		Assert.assertTrue(getLeaguePlayerService().getLeaguePlayersByTeam(lt2_id, uname).size() == 2);
		unknown_h  = getLeaguePlayerService().get(unknown_h_id);
		Assert.assertTrue(unknown_h.getTeam_roster_position().equals(LeaguePlayerService.TEAM_ROSTER_POSITION_3B));
		Assert.assertTrue(unknown_h.getTeam_player_salary() == 8);
		Assert.assertTrue(unknown_h.getLeague_team().getId() == lt2_id);
		
		// Test update existing drafted player
		cont_chapman.setTeam_player_salary(7);
		chapman_id = getLeaguePlayerService().draftLeaguePlayer(cont_chapman, uname);
		Assert.assertTrue(getLeaguePlayerService().getLeaguePlayersByTeam(lt2_id, uname).size() == 2);
		chapman = getLeaguePlayerService().get(chapman_id);
		Assert.assertTrue(chapman.getTeam_roster_position().equals(LeaguePlayerService.TEAM_ROSTER_POSITION_P));
		Assert.assertTrue(chapman.getTeam_player_salary() == 7);
		Assert.assertTrue(chapman.getLeague_team().getId() == lt2_id);
		
		// Test undraft unknown player
		System.out.println("Undrafting unknown playerp1");
		LeaguePlayerInputDraftContainer ud_unknown_container = new LeaguePlayerInputDraftContainer();
		ud_unknown_container.setUnknown_player_name("Unknown PlayerP1");
		ud_unknown_container.setUnknownplayer(true);
		ud_unknown_container.setLeague_id(l1_id);
		getLeaguePlayerService().undraftLeaguePlayer(ud_unknown_container, uname);
		
		// Test undraft player
		LeaguePlayerInputDraftContainer ud_container = new LeaguePlayerInputDraftContainer();
		ud_container.setPlayer_projected_id(aroldis_chapman.getId());
		ud_container.setLeague_id(l1_id);
		getLeaguePlayerService().undraftLeaguePlayer(ud_container, uname);
		
		// Test league player note and custom elig after undraft
		chapman = getLeaguePlayerService().get(chapman_id);
		Assert.assertTrue(chapman.getTeam_player_note().equals(player_note));
		Assert.assertTrue(!chapman.isCustom_position_flag());
		
		// Test remove custom eligibility
		pcont_chapman = new LeaguePlayerInputInfoContainer();
		pcont_chapman.setLeague_id(l1_id);
		pcont_chapman.setPlayer_projected_id(aroldis_chapman.getId());
		pcont_chapman.setTeam_player_note(player_note);
		pcont_chapman.setCustom_position("");
		pcont_chapman.setCustom_position_flag(false);
		chapman_id = getLeaguePlayerService().updateLeaguePlayerInfo(pcont_chapman, uname);
		chapman = getLeaguePlayerService().get(chapman_id);
		Assert.assertTrue(!chapman.isCustom_position_flag());
		
		// Test getLeaguePlayersbyLeague
		Assert.assertTrue(getLeaguePlayerService().getLeaguePlayersByLeague(l1_id, uname).size() == 8);
		
		// Test getLeaguePlayersbyTeam
		Assert.assertTrue(getLeaguePlayerService().getLeaguePlayersByTeam(lt2_id, uname).size() == 1);
		Assert.assertTrue(getLeaguePlayerService().getLeaguePlayersByTeam(lt1_id, uname).size() == 5);
		
		// Get updated playeroutput after draft
		playeroutput = getLeagueService().getLeaguePlayerData(l1_id, usr1.getUsername());
		
		List<PlayerProjected> po_drafted = new ArrayList<PlayerProjected>();
		
		for (PlayerProjected po : playeroutput){
			
			if (po.getLeagueteam_id() > 0) po_drafted.add(po);
			else if (po.getTeam_player_note() != null)
				po_drafted.add(po);
		}
		
		// Test there are 6 players that were drafted or have notes
		Assert.assertTrue(po_drafted.size() == 8);
		
		PlayerProjected po_sale = new PlayerProjected();
		PlayerProjected po_davis = new PlayerProjected();
		PlayerProjected po_perez = new PlayerProjected();
		PlayerProjected po_castro = new PlayerProjected();
		PlayerProjected po_chapman = new PlayerProjected();
		PlayerProjected po_unknown_h = new PlayerProjected();
		PlayerProjected po_unknown_p2 = new PlayerProjected();
		
		for (PlayerProjected po : po_drafted){
			
			if (po.getFull_name().equals("Chris Sale")) po_sale = po;
			if (po.getFull_name().equals("Chris Davis")) po_davis = po;
			if (po.getFull_name().equals("Salvador Perez")) po_perez = po;
			if (po.getFull_name().equals("Starlin Castro")) po_castro = po;
			if (po.getFull_name().equals("Aroldis Chapman")) po_chapman = po;
			
			if (po.getFull_name().equals("Unknown PlayerH")) po_unknown_h = po;
			if (po.getFull_name().equals("Unknown PlayerP2")) po_unknown_p2 = po;
			
		}
		
		// Test attributes of drafted players from playeroutput
		Assert.assertTrue(po_sale.getLeagueteam_id() == lt1_id);
		Assert.assertTrue(po_sale.getLeagueteam_name().equals("Team1"));
		Assert.assertTrue(po_davis.getTeam_roster_position().equals(LeaguePlayerService.TEAM_ROSTER_POSITION_1B));
		Assert.assertTrue(po_perez.getTeam_player_salary() == 10);
		Assert.assertTrue(po_castro.getTeam_player_salary() == 10);
		Assert.assertTrue(po_chapman.getTeam_player_note().equals(player_note));
		Assert.assertTrue(po_unknown_h.getTeam_player_salary() == 8);
		Assert.assertTrue(po_unknown_p2.getTeam_roster_position().equals(LeaguePlayerService.TEAM_ROSTER_POSITION_P));
		
		// Test Delete league
		getLeagueService().deleteLeagueFull(l1_id, uname);
		
		// Verify league has no players
		Assert.assertTrue(getLeaguePlayerService().getAll().size() == 0);
		
		// Verify league has no teams
		Assert.assertTrue(getLeagueTeamService().getAll().size() == 0);
		
		// Verify league was deleted
		Assert.assertTrue(getLeagueService().getAll().size() == 0);

		System.out.println("*************************************************************");
		System.out.println("****************** ENDING PLAYER OUTPUT TEST 2 **************");
		System.out.println("*************************************************************");
	}

	
	private List<PlayerProjected> findPlayerByName(List<PlayerProjected> outputlist, String fullname){
		
		List<PlayerProjected> result = new ArrayList<PlayerProjected>();
		
		for (PlayerProjected o : outputlist){
			if (o.getFull_name().equals(fullname)) {
				
				result.add(o);
				return result;
				
			}
		}
		
		return result;
		
	}
	
	private List<PlayerProjected> parseProjections(Reader in) {

		Iterable<CSVRecord> records = null;
		
		try {
			records = CSVFormat.EXCEL.withHeader().withNullString("").parse(in);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		List<PlayerProjected> pplist = new ArrayList<PlayerProjected>();
		for (CSVRecord record : records) {
			
			PlayerProjected pp = new PlayerProjected();

		    pp.setOther_id(record.get("id"));
		    pp.setOther_id_name("Steamer");
		    pp.setAge(Integer.parseInt(record.get("age")));
		    pp.setPitcher_hitter(record.get("P-H"));
		    pp.setDc_status(record.get("dc-status"));
		    pp.setAl_nl(record.get("League"));
		    pp.setFull_name(record.get("Name"));
		    pp.setTeam(record.get("Team"));
		    
		    if (record.get("P-H").equals("H")){
		    	
			    pp.setHitter_bats(record.get("Bats"));
			    pp.setHitter_pos_elig_espn(record.get("H_ESPN"));
			    pp.setHitter_pos_elig_yahoo(record.get("H_YAHOO"));
			    pp.setHitter_games(Float.valueOf(record.get("H_G")));
			    pp.setHitter_pa(Float.valueOf(record.get("H_PA")));
			    pp.setHitter_ab(Float.valueOf(record.get("H_AB")));
			    pp.setHitter_hits(Float.valueOf(record.get("H_H")));
			    pp.setHitter_singles(Float.valueOf(record.get("H_1B")));
			    pp.setHitter_doubles(Float.valueOf(record.get("H_2B")));
			    pp.setHitter_triples(Float.valueOf(record.get("H_3B")));
			    pp.setHitter_tb(Float.valueOf(record.get("H_TB")));
			    pp.setHitter_hr(Float.valueOf(record.get("H_HR")));
			    pp.setHitter_rbi(Float.valueOf(record.get("H_RBI")));
			    pp.setHitter_runs(Float.valueOf(record.get("H_R")));
			    pp.setHitter_so(Float.valueOf(record.get("H_SO")));
			    pp.setHitter_bb(Float.valueOf(record.get("H_BB")));
			    pp.setHitter_hbp(Float.valueOf(record.get("H_HBP")));
			    pp.setHitter_sb(Float.valueOf(record.get("H_SB")));
			    pp.setHitter_cs(Float.valueOf(record.get("H_CS")));
			    pp.setHitter_avg(Float.valueOf(record.get("H_AVG")));
			    pp.setHitter_obp(Float.valueOf(record.get("H_OBP")));
			    pp.setHitter_slg(Float.valueOf(record.get("H_SLG")));
			    pp.setHitter_ops(Float.valueOf(record.get("H_OPS")));
		    	
		    } else if (record.get("P-H").equals("P")){
		    	
			    pp.setPitcher_pos(record.get("P_POS"));
			    pp.setPitcher_babip(Float.valueOf(record.get("P_BABIP")));
			    pp.setPitcher_bb(Float.valueOf(record.get("P_BB")));
			    pp.setPitcher_er(Float.valueOf(record.get("P_ER")));
			    pp.setPitcher_era(Float.valueOf(record.get("P_ERA")));
			    pp.setPitcher_fb_pct(Float.valueOf(record.get("P_FB%")));
			    pp.setPitcher_games(Float.valueOf(record.get("P_G")));
			    pp.setPitcher_gb_pct(Float.valueOf(record.get("P_GB%")));
			    pp.setPitcher_gs(Float.valueOf(record.get("P_GS")));
			    pp.setPitcher_hbp(Float.valueOf(record.get("P_HBP")));
			    pp.setPitcher_hits(Float.valueOf(record.get("P_H")));
			    pp.setPitcher_hld(Float.valueOf(record.get("P_HLD")));
			    pp.setPitcher_hr(Float.valueOf(record.get("P_HR")));
			    pp.setPitcher_ip(Float.valueOf(record.get("P_IP")));
			    pp.setPitcher_k(Float.valueOf(record.get("P_K")));
			    pp.setPitcher_l(Float.valueOf(record.get("P_L")));
			    pp.setPitcher_ld_pct(Float.valueOf(record.get("P_LD%")));
			    pp.setPitcher_qs(Float.valueOf(record.get("P_QS")));
			    pp.setPitcher_r(Float.valueOf(record.get("P_R")));
			    pp.setPitcher_siera(Float.valueOf(record.get("P_SIERA")));
			    pp.setPitcher_sv(Float.valueOf(record.get("P_SV")));
			    pp.setPitcher_w(Float.valueOf(record.get("P_W")));
		    	
		    }

		    pplist.add(pp);

		}
		
		return pplist;
		
	}
	
	private void deleteTestItems(){
		// Delete projection profiles if they exist
		
		getIdentityService().deleteUser("test1");
		getIdentityService().deleteUser("test2");
		
		if (getProjectionProfileService().isProjectionProfilePresent(
				ProjectionProfileService.PROJECTION_SERVICE_STEAMER,
				ProjectionProfileService.PROJECTION_PERIOD_PRESEASON, 2016)) {
			getProjectionProfileService().delete(getProjectionProfileService().get(
					ProjectionProfileService.PROJECTION_SERVICE_STEAMER,
					ProjectionProfileService.PROJECTION_PERIOD_PRESEASON, 2016).getId());			
		}
		
		// Delete leagues
		List<League> leagues = getLeagueService().getAll();
		for (League l : leagues) {
			if (l.getLeague_name().equals("League 1") ) {
				getLeagueService().delete(l.getId());
			}
		}
		
		// Delete league teams
		List<LeagueTeam> leagueteams = getLeagueTeamService().getAll();
		for (LeagueTeam l : leagueteams) {
			if (l.getTeam_name().equals("Team1") || l.getTeam_name().equals("Team2")
					|| l.getTeam_name().equals("Team3") || l.getTeam_name().equals("Team4")) {
				getLeagueTeamService().delete(l.getId());
			}
		}
		

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
	
	private PlayerProjectedService getPlayerProjectedService() {

		return new PlayerProjectedService();

	}

}
