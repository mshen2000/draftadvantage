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
	
//	private static final String[] FILE_HEADER_MAPPING = { "id", "dup-id", "age", "P-H", "dc-status", "League", "#",
//			"Name", "Team", "Bats", "H_ESPN", "H_YAHOO", "H_G", "H_PA", "H_AB", "H_R", "H_HR", "H_RBI", "H_SB", "H_H",
//			"H_1B", "H_2B", "H_3B", "H_TB", "H_SO", "H_BB", "H_HBP", "H_SF", "H_SH", "H_CS", "H_AVG", "H_OBP", "H_SLG",
//			"H_OPS", "P_POS", "P_R/L", "P_G", "P_GS", "P_QS", "P_TBF", "P_IP", "P_W", "P_L", "P_SV", "P_HLD", "P_ERA",
//			"P_SIERA", "P_WHIP", "P_K", "P_BB", "P_H", "P_HBP", "P_ER", "P_R", "P_HR", "P_GB%", "P_FB%", "P_LD%",
//			"P_BABIP" };

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
		
		// Create user
		User usr1 = new User("test1", "test1");
		usr1.setFirstname("Test");
		usr1.setLastname("One");

		getIdentityService().saveUser(usr1, usr1.getUsername());
		
		// Create projection profile
		ProjectionProfile p1 = new ProjectionProfile();
		p1.setProjected_year(2016);
		p1.setProjection_date(yesterday);
		p1.setProjection_period(ProjectionProfileService.PROJECTION_PERIOD_PRESEASON);
		p1.setProjection_service(ProjectionProfileService.PROJECTION_SERVICE_STEAMER);

		Long p1_id = getProjectionProfileService().save(p1, usr1.getUsername());
		ProjectionProfile p1_r = getProjectionProfileService().get(p1_id);
		
		// Parser
		Reader in = new FileReader("D:/AllData/Dropbox/Fantasy Sports/2016 - MLB/Source Data - 20160210-test200.csv");

		getPlayerProjectedService().updatePlayerProjections(parseProjections(in), p1_r, usr1.getUsername());
	
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
	public void testLeagues() {
		
		User usr1 = getIdentityService().getUser("test1");
		
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
		
		// Test limit of teams in league
		try {
			getLeagueService().addLeagueTeam(l1_id, lt4_id, usr1.getUsername());
		    fail( "Adding 3rd LeagueTeam did not cause exception" );
		} catch (IndexOutOfBoundsException e) {
		}
		
		l1_r = getLeagueService().get(l1_id);

		getLeagueService().deleteLeagueTeam(l1_id, lt2_id, usr1.getUsername());
		
		// Test count of league teams and total teams after delete Team from League
		Assert.assertTrue(l1_r.getLeague_teams().size() == 1);
		Assert.assertTrue(getLeagueTeamService().getAll().size() == 2);
		
		getLeagueService().delete(l1_id);
		
		// Test count of Leagues and Teams after deleting the League
		Assert.assertTrue(getLeagueTeamService().getAll().size() == 1);
		Assert.assertTrue(getLeagueService().getAll().size() == 0);

	}
	
	@Test
	public void testLeaguePlayerUpdate() {
		
		User usr1 = getIdentityService().getUser("test1");
		ProjectionProfile p1 = getProjectionProfileService().get(ProjectionProfileService.PROJECTION_SERVICE_STEAMER,
				ProjectionProfileService.PROJECTION_PERIOD_PRESEASON, 2016);
		
		// Create a league
		League l1 = new League();
		l1.setMlb_leagues(LeagueService.MLB_LEAGUES_AL);
		l1.setNum_of_teams(2);
		l1.setAvg_hitter_ab(6500);
		l1.setAvg_hitter_ba(0.258);
		l1.setAvg_hitter_hits(l1.getAvg_hitter_ab()*l1.getAvg_hitter_ba());
		
		l1.setAvg_pitcher_era(3.96);
		l1.setAvg_pitcher_ip(1000);
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

		getLeagueService().updateLeaguePlayerData(l1_id, usr1.getUsername());
		
		List<LeaguePlayer> leagueplayers = getLeagueService().getLeaguePlayers(l1_id, usr1.getUsername());
		
		// Test count of AL and FA league players
		Assert.assertTrue(leagueplayers.size() == 116);
		
		// Parser
		Reader in = null;
		try {
			in = new FileReader("D:/AllData/Dropbox/Fantasy Sports/2016 - MLB/Source Data - 20160210-test200b.csv");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		getPlayerProjectedService().updatePlayerProjections(parseProjections(in), p1, usr1.getUsername());
		
		getLeagueService().updateLeaguePlayerData(l1_id, usr1.getUsername());
		
		leagueplayers = getLeagueService().getLeaguePlayers(l1_id, usr1.getUsername());
		
		// Test updated count of AL and FA league players
		Assert.assertTrue(leagueplayers.size() == 115);
		
		Key<League> leaguekey = Key.create(League.class, l1_id);
		
		// Check if deleted player projections are deleted from League Players
		Assert.assertTrue(ofy().load().type(LeaguePlayer.class).filter("full_name", "Francisco Lindor")
				.filter("league",leaguekey).list().isEmpty());
		Assert.assertTrue(ofy().load().type(LeaguePlayer.class).filter("full_name", "Adam Eaton")
				.filter("league",leaguekey).list().isEmpty());
		Assert.assertTrue(ofy().load().type(LeaguePlayer.class).filter("full_name", "Jose Altuve")
				.filter("league",leaguekey).list().size() == 1);  // player exists, verifies test
		
		// Check for new projection player in League Players
		Assert.assertTrue(ofy().load().type(LeaguePlayer.class).filter("full_name", "Kole Calhoun2")
				.filter("league",leaguekey).list().size() == 1);
		
		// Check for updated projection player in League Players
		Assert.assertTrue(ofy().load().type(LeaguePlayer.class).filter("full_name", "Ian Kinsler")
				.filter("league",leaguekey).list().size() == 1);  //  Checks if player exists
		Assert.assertTrue(ofy().load().type(LeaguePlayer.class).filter("full_name", "Ian Kinsler")
				.filter("dc-status", "S").filter("league",leaguekey).list().isEmpty());  //  Checks if update worked
		
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
