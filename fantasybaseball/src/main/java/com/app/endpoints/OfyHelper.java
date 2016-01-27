package com.app.endpoints;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyFactory;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.VoidWork;
import com.nya.sms.dataservices.CustomTestService;
import com.nya.sms.dataservices.IdentityService;
import com.nya.sms.dataservices.ProjectionProfileService;
import com.nya.sms.dataservices.SiteService;
import com.nya.sms.entities.BaseEntity;
import com.nya.sms.entities.BaseFieldAbstract;
import com.nya.sms.entities.BaseFieldDate;
import com.nya.sms.entities.BaseFieldFloat;
import com.nya.sms.entities.BaseFieldInt;
import com.nya.sms.entities.BaseFieldNote;
import com.nya.sms.entities.BaseFieldProgramScore;
import com.nya.sms.entities.BaseFieldScore;
import com.nya.sms.entities.BaseFieldSelect;
import com.nya.sms.entities.BaseFieldString;
import com.nya.sms.entities.CustomObjectTest;
import com.nya.sms.entities.HealthRule;
import com.nya.sms.entities.JKey;
import com.nya.sms.entities.Note;
import com.nya.sms.entities.PlayerProjected;
import com.nya.sms.entities.Points;
import com.nya.sms.entities.ProgramScore;
import com.nya.sms.entities.ProgramScoreCaughtYa;
import com.nya.sms.entities.ProgramScoreEnglish;
import com.nya.sms.entities.ProgramScoreMath;
import com.nya.sms.entities.ProgramScoreNYAOnlineAssess;
import com.nya.sms.entities.ProgramScoreNYAOnlineMath;
import com.nya.sms.entities.ProgramScoreSixTraits;
import com.nya.sms.entities.ProjectionProfile;
import com.nya.sms.entities.Role;
import com.nya.sms.entities.SchoolGrades;
import com.nya.sms.entities.SchoolSchedule;
import com.nya.sms.entities.Site;
import com.nya.sms.entities.Student;
import com.nya.sms.entities.StudentGroup;
import com.nya.sms.entities.StudentGroupLogItem;
import com.nya.sms.entities.StudentHealth;
import com.nya.sms.entities.TestScore;
import com.nya.sms.entities.TestScoreInternal;
import com.nya.sms.entities.TestScoreStandard;
import com.nya.sms.entities.User;

import javax.servlet.ServletContextListener;
import javax.servlet.ServletContextEvent;

/**
 * OfyHelper, a ServletContextListener, is setup in web.xml to run before a JSP
 * is run. This is required to let JSP's access Ofy.
 **/
public class OfyHelper implements ServletContextListener {

	private static final Logger log = Logger.getLogger(OfyHelper.class
			.getName());

	public void contextInitialized(ServletContextEvent event) {
		// This will be invoked as part of a warmup request, or the first user
		// request if no warmup request was invoked.
		log.info("Registering data objects...");

		// ObjectifyService.register(BaseEntity.class);
		ObjectifyService.register(JKey.class);

		ObjectifyService.register(User.class);
		ObjectifyService.register(Role.class);
		ObjectifyService.register(Site.class);
		ObjectifyService.register(Student.class);
		ObjectifyService.register(StudentGroup.class);
		ObjectifyService.register(StudentHealth.class);

		ObjectifyService.register(ProgramScore.class);
		ObjectifyService.register(ProgramScoreCaughtYa.class);
		ObjectifyService.register(ProgramScoreEnglish.class);
		ObjectifyService.register(ProgramScoreMath.class);
		ObjectifyService.register(ProgramScoreNYAOnlineAssess.class);
		ObjectifyService.register(ProgramScoreNYAOnlineMath.class);
		ObjectifyService.register(ProgramScoreSixTraits.class);

		ObjectifyService.register(StudentGroupLogItem.class);
		ObjectifyService.register(TestScore.class);
		ObjectifyService.register(TestScoreInternal.class);
		ObjectifyService.register(TestScoreStandard.class);
		ObjectifyService.register(SchoolGrades.class);
		ObjectifyService.register(SchoolSchedule.class);
		ObjectifyService.register(Note.class);
		ObjectifyService.register(Points.class);

		ObjectifyService.register(HealthRule.class);

		ObjectifyService.register(CustomObjectTest.class);

		ObjectifyService.register(PlayerProjected.class);
		ObjectifyService.register(ProjectionProfile.class);

		ObjectifyService.run(new VoidWork() {
			public void vrun() {
				log.info("Deleting objects (if necessary)...");
				// deleteAllIfPresent();

				log.info("Creating data objects (if necessary)...");

				createNewKey();
				createGroupsIfNotPresent();
				createAdminUserIfNotPresent();
				createInitialProjectionProfile();

				// delete all CustomObjecTest
				// List<Key<CustomObjectTest>> keys =
				// ObjectifyService.ofy().load().type(CustomObjectTest.class).keys().list();
				//
				// for (Key k : keys){
				// System.out.println("Deleting key: " + k.getId());
				// ObjectifyService.ofy().delete().key(k).now();
				//
				// }

				deletePrograms();
				createPrograms();

				deleteTests();
				createTests();

				log.info("Verifying...");
				checkData();
			}
		});

	}

	protected void createInitialProjectionProfile() {
		
		ProjectionProfile profile = new ProjectionProfile();
		profile.setProjected_year(2016);
		profile.setProjection_date(new Date());
		profile.setProjection_period(ProjectionProfileService.PROJECTION_PERIOD_PRESEASON);
		profile.setProjection_service(ProjectionProfileService.PROJECTION_SERVICE_STEAMER);
		
		getProjectionProfileService().save(profile, "admin");

	}

	public void contextDestroyed(ServletContextEvent event) {
		// App Engine does not currently invoke this method.
	}

	private void createNewKey() {

		getIdentityService().createWebKey();

	}

	private void createAdminUserIfNotPresent() {
		if (!isAdminUserPresent()) {
			createAdminUser();
		} else {
			updateAdminUser();
		}
	}

	private void createGroupsIfNotPresent() {
		// if (!isGroupPresent("staff")) {
		// createStaffRole();
		// }
		if (!isGroupPresent("admin")) {
			createAdminRole();
		}
	}

	private void deleteAllIfPresent() {
		if (isAdminUserPresent()) {
			deleteAdminUser();
		}
		// if (isGroupPresent("staff")) {
		// deleteGroup("staff", "Staff");
		// }
		if (isGroupPresent("admin")) {
			deleteGroup("admin", "Administrator");
		}

	}

	private boolean isAdminUserPresent() {

		if (getIdentityService().isUserPresent("admin"))
			return Boolean.TRUE;

		return Boolean.FALSE;
	}

	private void createAdminUser() {
		log.info("Creating an administration user with the username 'admin' and password 'password'");

		User usr = new User("admin", "admin");
		usr.setFirstname("Michael");
		usr.setLastname("Shen");
		usr.setEmail("mshen2000@gmail.com");
		usr.setExt_id("103506611204742844645");

		getIdentityService().saveUser(usr, usr.getUsername());

		assignAdminUserToGroups(usr);

	}

	private void updateAdminUser() {
		log.info("Updating the administration user with the correct attributes");

		User usr = getIdentityService().getUser("admin");

		usr.setPassword("admin");
		usr.setFirstname("Michael");
		usr.setLastname("Shen");
		usr.setEmail("mshen2000@gmail.com");
		usr.setExt_id("103506611204742844645");

		getIdentityService().saveUser(usr, usr.getUsername());

	}

	private void deleteAdminUser() {
		log.info("Deleting an administration user with the username 'admin' and password 'password'");

		getIdentityService().deleteUser("admin");

	}

	private void assignAdminUserToGroups(User usr) {

		getIdentityService().createMembership(usr.getUsername(), "admin");

	}

	private boolean isGroupPresent(String groupId) {

		if (getIdentityService().isRolePresent(groupId))
			return true;

		return false;

	}

	private void createAdminRole() {
		log.log(Level.INFO,
				"Creating a group with the id '{1}' and name '{2}'",
				new Object[] { "admin", "Administrator" });

		List<String> levels = getIdentityService().getAccessLevels();
		List<String> scopes = getIdentityService().getStudentDataScopes();

		Role role = new Role("admin", "Administrator");

		role.setSite(getSiteService().MASTER_SITE);

		role.setAccess_userrole(levels.get(2));
		role.setAccess_sg(levels.get(2));
		role.setAccess_students(levels.get(2));
		role.setAccess_program(levels.get(2));
		role.setAccess_test(levels.get(2));
		role.setAccess_points(levels.get(2));
		role.setAccess_grades(levels.get(2));
		role.setAccess_schedule(levels.get(2));
		role.setStudentdata_access_scope(scopes.get(1));
		role.setAccess_med(true);
		role.setAccess_app_admin(true);
		role.setAccess_app_student(true);
		role.setAccess_app_program(true);
		role.setAccess_app_test(true);
		role.setAccess_app_points(true);
		role.setAccess_app_grades(true);
		role.setAccess_app_schedule(true);

		getIdentityService().saveRole(role, "admin");

	}

	private void createStaffRole() {
		log.log(Level.INFO,
				"Creating a group with the id '{1}' and name '{2}'",
				new Object[] { "staff", "Staff" });

		List<String> levels = getIdentityService().getAccessLevels();
		List<String> scopes = getIdentityService().getStudentDataScopes();

		Role role = new Role("staff", "Staff");
		role.setAccess_userrole(levels.get(0));
		role.setAccess_sg(levels.get(0));
		role.setAccess_students(levels.get(1));
		role.setAccess_program(levels.get(2));
		role.setAccess_test(levels.get(2));
		role.setAccess_points(levels.get(2));
		role.setAccess_grades(levels.get(2));
		role.setAccess_schedule(levels.get(2));
		role.setStudentdata_access_scope(scopes.get(0));
		role.setAccess_med(true);
		role.setAccess_app_admin(false);
		role.setAccess_app_student(false);
		role.setAccess_app_program(true);
		role.setAccess_app_test(true);
		role.setAccess_app_points(true);
		role.setAccess_app_grades(true);
		role.setAccess_app_schedule(true);

		getIdentityService().saveRole(role, "admin");

	}

	private void deleteGroup(String groupname, String groupalias) {
		log.log(Level.INFO,
				"Deleting a group with the id '{1}' and name '{2}'",
				new Object[] { groupname, groupalias });

		getIdentityService().deleteRole(groupname);

		while (getIdentityService().isRolePresent(groupname)) {
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	private void deleteTests() {

		if (getCustomTestService().isProgramPresent("internal")) {
			System.out.println("Found Internal Test, deleting it...");
			getCustomTestService().delete(
					getCustomTestService().get("internal").getId());
		}
		if (getCustomTestService().isProgramPresent("standard")) {
			System.out.println("Found Standard Test, deleting it...");
			getCustomTestService().delete(
					getCustomTestService().get("standard").getId());
		}

	}

	private void createTests() {

		BaseFieldInt id = new BaseFieldInt("id");
		id.setQuery_alias("id");
		id.setForm_alias("id");
		id.setQueryVisible(false);
		id.setFormAccess(false);

		BaseFieldString studentname = new BaseFieldString("studentname");
		studentname.setQuery_alias("Student Name");
		studentname.setForm_alias("Student Name");
		studentname.setQueryVisible(true);
		studentname.setFormAccess(false);
		studentname.setPriority(1);

		BaseFieldSelect studentid = new BaseFieldSelect("studentid");
		studentid.setQuery_alias("Student ID");
		studentid.setForm_alias("Student ID");
		studentid.setQueryVisible(true);
		studentid.setFormAccess(true);
		studentid.setFormEditable(true);
		studentid.setRequired(true);
		studentid.setLength(150);
		studentid.setPriority(2);

		BaseFieldString group = new BaseFieldString("group");
		group.setQuery_alias("Group");
		group.setForm_alias("Group");
		group.setQueryVisible(true);
		group.setFormAccess(false);
		group.setPriority(3);

		BaseFieldDate scoredate = new BaseFieldDate("scoredate", "simple");
		scoredate.setQuery_alias("Score Date");
		scoredate.setForm_alias("Score Date");
		scoredate.setQueryVisible(true);
		scoredate.setFormAccess(true);
		scoredate.setFormEditable(true);
		scoredate.setRequired(true);
		scoredate.setPriority(4);

		BaseFieldSelect type = new BaseFieldSelect("type");
		type.setQuery_alias("Type");
		type.setForm_alias("Type");
		type.setQueryVisible(true);
		type.setFormAccess(true);
		type.setFormEditable(true);
		type.setRequired(true);
		type.setPriority(10);
		List<String> values = new ArrayList<String>();
		values.add("Type 1");
		values.add("Type 2");
		values.add("Type 3");
		type.setValues(values);

		BaseFieldSelect subject = new BaseFieldSelect("subject");
		subject.setQuery_alias("Subject");
		subject.setForm_alias("Subject");
		subject.setQueryVisible(true);
		subject.setFormAccess(true);
		subject.setFormEditable(true);
		subject.setRequired(true);
		subject.setPriority(11);
		values = new ArrayList<String>();
		values.add("Subject 1");
		values.add("Subject 2");
		values.add("Subject 3");
		subject.setValues(values);

		BaseFieldSelect title = new BaseFieldSelect("title");
		title.setQuery_alias("Title");
		title.setForm_alias("Title");
		title.setQueryVisible(true);
		title.setFormAccess(true);
		title.setFormEditable(true);
		title.setRequired(true);
		title.setPriority(12);
		values = new ArrayList<String>();
		values.add("Title 1");
		values.add("Title 2");
		values.add("Title 3");
		title.setValues(values);

		BaseFieldProgramScore score = new BaseFieldProgramScore("score");
		score.setQuery_alias("Score");
		score.setForm_alias("Score");
		score.setQueryVisible(true);
		score.setFormAccess(true);
		score.setFormEditable(true);
		score.setRequired(true);
		score.setPriority(20);

		BaseFieldFloat scorepercent = new BaseFieldFloat("scorepercent");
		scorepercent.setQuery_alias("Score Percent");
		scorepercent.setForm_alias("Score Percent");
		scorepercent.setQueryVisible(true);
		scorepercent.setFormAccess(false);
		scorepercent.setPriority(21);
		scorepercent.setFormat("#.0%");

		BaseFieldSelect proficiency = new BaseFieldSelect("proficiency");
		proficiency.setQuery_alias("Proficiency");
		proficiency.setForm_alias("Proficiency");
		proficiency.setQueryVisible(true);
		proficiency.setFormAccess(true);
		proficiency.setFormEditable(true);
		proficiency.setRequired(true);
		proficiency.setPriority(22);
		values = new ArrayList<String>();
		values.add("Proficiency 1");
		values.add("Proficiency 2");
		values.add("Proficiency 3");
		proficiency.setValues(values);

		BaseFieldNote notes = new BaseFieldNote("notes");
		notes.setQuery_alias("Notes");
		notes.setForm_alias("Notes");
		notes.setQueryVisible(true);
		notes.setFormAccess(true);
		notes.setFormEditable(true);
		notes.setRequired(false);
		notes.setPriority(55);

		BaseFieldString createdby = new BaseFieldString("createdby");
		createdby.setQuery_alias("Created By");
		createdby.setForm_alias("Created By");
		createdby.setQueryVisible(true);
		createdby.setFormAccess(false);
		createdby.setPriority(60);

		BaseFieldDate createdon = new BaseFieldDate("createdon", "detail");
		createdon.setQuery_alias("Created On");
		createdon.setForm_alias("Created On");
		createdon.setQueryVisible(true);
		createdon.setFormAccess(false);
		createdon.setPriority(61);

		BaseFieldString modifiedby = new BaseFieldString("modifiedby");
		modifiedby.setQuery_alias("Modified By");
		modifiedby.setForm_alias("Modified By");
		modifiedby.setQueryVisible(false);
		modifiedby.setFormAccess(false);
		modifiedby.setPriority(62);

		BaseFieldDate modifiedon = new BaseFieldDate("modifiedon", "detail");
		modifiedon.setQuery_alias("Modified On");
		modifiedon.setForm_alias("Modified On");
		modifiedon.setQueryVisible(false);
		modifiedon.setFormAccess(false);
		modifiedon.setPriority(63);

		Map<String, BaseFieldAbstract> fields = new HashMap<String, BaseFieldAbstract>();
		fields.put("id", id);
		fields.put("studentname", studentname);
		fields.put("studentid", studentid);
		fields.put("group", group);
		fields.put("scoredate", scoredate);
		fields.put("type", type);
		fields.put("subject", subject);
		fields.put("title", title);
		fields.put("score", score);
		fields.put("scorepercent", scorepercent);
		// fields.put("points", points);
		fields.put("notes", notes);
		fields.put("createdby", createdby);
		fields.put("createdon", createdon);
		fields.put("modifiedby", modifiedby);
		fields.put("modifiedon", modifiedon);

		System.out.println("Creating Internal Test...");
		CustomObjectTest test = new CustomObjectTest("internal",
				"Internal Tests");
		test.setProgramScoreClass(TestScoreInternal.class);
		test.setFields(fields);
		test.setType("test");
		getCustomTestService().save(test, "admin");

		fields = new HashMap<String, BaseFieldAbstract>();
		fields.put("id", id);
		fields.put("studentname", studentname);
		fields.put("studentid", studentid);
		fields.put("group", group);
		fields.put("scoredate", scoredate);
		fields.put("type", type);
		fields.put("title", title);
		fields.put("score", score);
		fields.put("scorepercent", scorepercent);
		fields.put("proficiency", proficiency);
		// fields.put("points", points);
		fields.put("notes", notes);
		fields.put("createdby", createdby);
		fields.put("createdon", createdon);
		fields.put("modifiedby", modifiedby);
		fields.put("modifiedon", modifiedon);

		System.out.println("Creating Standard Test...");
		test = new CustomObjectTest("standard", "Standard Tests");
		test.setProgramScoreClass(TestScoreStandard.class);
		test.setFields(fields);
		test.setType("test");
		getCustomTestService().save(test, "admin");

	}

	private void deletePrograms() {

		if (getCustomTestService().isProgramPresent("caughtya")) {
			System.out.println("Found caughtya Program, deleting it...");
			getCustomTestService().delete(
					getCustomTestService().get("caughtya").getId());
		}
		if (getCustomTestService().isProgramPresent("math")) {
			System.out.println("Found math Program, deleting it...");
			getCustomTestService().delete(
					getCustomTestService().get("math").getId());
		}
		if (getCustomTestService().isProgramPresent("english")) {
			System.out.println("Found english Program, deleting it...");
			getCustomTestService().delete(
					getCustomTestService().get("english").getId());
		}

		if (getCustomTestService().isProgramPresent("nyaonlineassess")) {
			System.out.println("Found nyaonlineassess Program, deleting it...");
			getCustomTestService().delete(
					getCustomTestService().get("nyaonlineassess").getId());
		}
		if (getCustomTestService().isProgramPresent("nyaonlinemath")) {
			System.out.println("Found nyaonlinemath Program, deleting it...");
			getCustomTestService().delete(
					getCustomTestService().get("nyaonlinemath").getId());
		}
		if (getCustomTestService().isProgramPresent("sixtraits")) {
			System.out.println("Found sixtraits Program, deleting it...");
			getCustomTestService().delete(
					getCustomTestService().get("sixtraits").getId());
		}
	}

	private void createPrograms() {

		BaseFieldInt id = new BaseFieldInt("id");
		id.setQuery_alias("id");
		id.setForm_alias("id");
		id.setQueryVisible(false);
		id.setFormAccess(false);

		BaseFieldString studentname = new BaseFieldString("studentname");
		studentname.setQuery_alias("Student Name");
		studentname.setForm_alias("Student Name");
		studentname.setQueryVisible(true);
		studentname.setFormAccess(false);
		studentname.setPriority(1);

		BaseFieldSelect studentid = new BaseFieldSelect("studentid");
		studentid.setQuery_alias("Student ID");
		studentid.setForm_alias("Student ID");
		studentid.setQueryVisible(true);
		studentid.setFormAccess(true);
		studentid.setFormEditable(true);
		studentid.setRequired(true);
		studentid.setLength(150);
		studentid.setPriority(2);

		BaseFieldString group = new BaseFieldString("group");
		group.setQuery_alias("Group");
		group.setForm_alias("Group");
		group.setQueryVisible(true);
		group.setFormAccess(false);
		group.setPriority(3);

		BaseFieldDate scoredate = new BaseFieldDate("scoredate", "simple");
		scoredate.setQuery_alias("Score Date");
		scoredate.setForm_alias("Score Date");
		scoredate.setQueryVisible(true);
		scoredate.setFormAccess(true);
		scoredate.setFormEditable(true);
		scoredate.setRequired(true);
		scoredate.setPriority(4);

		BaseFieldSelect number = new BaseFieldSelect("number");
		number.setQuery_alias("Test Number");
		number.setForm_alias("Test Number");
		number.setQueryVisible(true);
		number.setFormAccess(true);
		number.setFormEditable(true);
		number.setRequired(true);
		number.setPriority(10);
		List<String> values = new ArrayList<String>();
		values.add("1");
		values.add("2");
		values.add("3");
		number.setValues(values);

		BaseFieldProgramScore score = new BaseFieldProgramScore("score");
		score.setQuery_alias("Score");
		score.setForm_alias("Score");
		score.setQueryVisible(true);
		score.setFormAccess(true);
		score.setFormEditable(true);
		score.setRequired(true);
		score.setPriority(20);

		BaseFieldFloat scorepercent = new BaseFieldFloat("scorepercent");
		scorepercent.setQuery_alias("Score Percent");
		scorepercent.setForm_alias("Score Percent");
		scorepercent.setQueryVisible(true);
		scorepercent.setFormAccess(false);
		scorepercent.setPriority(21);
		scorepercent.setFormat("#.0%");

		BaseFieldScore points = new BaseFieldScore("points");
		points.setQuery_alias("Points");
		points.setForm_alias("Points");
		points.setQueryVisible(true);
		points.setFormAccess(true);
		points.setFormEditable(true);
		points.setRequired(true);
		points.setPriority(50);

		BaseFieldNote notes = new BaseFieldNote("notes");
		notes.setQuery_alias("Notes");
		notes.setForm_alias("Notes");
		notes.setQueryVisible(true);
		notes.setFormAccess(true);
		notes.setFormEditable(true);
		notes.setRequired(false);
		notes.setPriority(55);

		BaseFieldString createdby = new BaseFieldString("createdby");
		createdby.setQuery_alias("Created By");
		createdby.setForm_alias("Created By");
		createdby.setQueryVisible(true);
		createdby.setFormAccess(false);
		createdby.setPriority(60);

		BaseFieldDate createdon = new BaseFieldDate("createdon", "detail");
		createdon.setQuery_alias("Created On");
		createdon.setForm_alias("Created On");
		createdon.setQueryVisible(true);
		createdon.setFormAccess(false);
		createdon.setPriority(61);

		BaseFieldString modifiedby = new BaseFieldString("modifiedby");
		modifiedby.setQuery_alias("Modified By");
		modifiedby.setForm_alias("Modified By");
		modifiedby.setQueryVisible(false);
		modifiedby.setFormAccess(false);
		modifiedby.setPriority(62);

		BaseFieldDate modifiedon = new BaseFieldDate("modifiedon", "detail");
		modifiedon.setQuery_alias("Modified On");
		modifiedon.setForm_alias("Modified On");
		modifiedon.setQueryVisible(false);
		modifiedon.setFormAccess(false);
		modifiedon.setPriority(63);

		BaseFieldProgramScore addition_score = new BaseFieldProgramScore(
				"addition_score");
		addition_score.setQuery_alias("Addition Score");
		addition_score.setForm_alias("Addition Score");
		addition_score.setQueryVisible(true);
		addition_score.setFormAccess(true);
		addition_score.setFormEditable(true);
		addition_score.setRequired(true);
		addition_score.setPriority(20);

		BaseFieldFloat addition_scorepercent = new BaseFieldFloat(
				"addition_scorepercent");
		addition_scorepercent.setQuery_alias("Addition Score Percent");
		addition_scorepercent.setForm_alias("Addition Score Percent");
		addition_scorepercent.setQueryVisible(false);
		addition_scorepercent.setFormAccess(false);
		addition_scorepercent.setPriority(21);
		addition_scorepercent.setFormat("#.0%");

		BaseFieldProgramScore subtraction_score = new BaseFieldProgramScore(
				"subtraction_score");
		subtraction_score.setQuery_alias("Subtraction Score");
		subtraction_score.setForm_alias("Subtraction Score");
		subtraction_score.setQueryVisible(true);
		subtraction_score.setFormAccess(true);
		subtraction_score.setFormEditable(true);
		subtraction_score.setRequired(true);
		subtraction_score.setPriority(22);

		BaseFieldFloat subtraction_scorepercent = new BaseFieldFloat(
				"subtraction_scorepercent");
		subtraction_scorepercent.setQuery_alias("Subtraction Score Percent");
		subtraction_scorepercent.setForm_alias("Subtraction Score Percent");
		subtraction_scorepercent.setQueryVisible(false);
		subtraction_scorepercent.setFormAccess(false);
		subtraction_scorepercent.setPriority(23);
		subtraction_scorepercent.setFormat("#.0%");

		BaseFieldProgramScore multiplication_score = new BaseFieldProgramScore(
				"multiplication_score");
		multiplication_score.setQuery_alias("Multiplication Score");
		multiplication_score.setForm_alias("Multiplication Score");
		multiplication_score.setQueryVisible(true);
		multiplication_score.setFormAccess(true);
		multiplication_score.setFormEditable(true);
		multiplication_score.setRequired(true);
		multiplication_score.setPriority(24);

		BaseFieldFloat multiplication_scorepercent = new BaseFieldFloat(
				"multiplication_scorepercent");
		multiplication_scorepercent
				.setQuery_alias("Multiplication Score Percent");
		multiplication_scorepercent
				.setForm_alias("Multiplication Score Percent");
		multiplication_scorepercent.setQueryVisible(false);
		multiplication_scorepercent.setFormAccess(false);
		multiplication_scorepercent.setPriority(25);
		multiplication_scorepercent.setFormat("#.0%");

		BaseFieldProgramScore division_score = new BaseFieldProgramScore(
				"division_score");
		division_score.setQuery_alias("Division Score");
		division_score.setForm_alias("Division Score");
		division_score.setQueryVisible(true);
		division_score.setFormAccess(true);
		division_score.setFormEditable(true);
		division_score.setRequired(true);
		division_score.setPriority(26);

		BaseFieldFloat division_scorepercent = new BaseFieldFloat(
				"division_scorepercent");
		division_scorepercent.setQuery_alias("Division Score Percent");
		division_scorepercent.setForm_alias("Division Score Percent");
		division_scorepercent.setQueryVisible(false);
		division_scorepercent.setFormAccess(false);
		division_scorepercent.setPriority(27);
		division_scorepercent.setFormat("#.0%");

		BaseFieldSelect level = new BaseFieldSelect("level");
		level.setQuery_alias("Test Level");
		level.setForm_alias("Test Level");
		level.setQueryVisible(true);
		level.setFormAccess(true);
		level.setFormEditable(true);
		level.setRequired(true);
		level.setPriority(10);
		values = new ArrayList<String>();
		values.add("1");
		values.add("2");
		values.add("3");
		level.setValues(values);

		BaseFieldSelect block = new BaseFieldSelect("block");
		block.setQuery_alias("Test Block");
		block.setForm_alias("Test Block");
		block.setQueryVisible(true);
		block.setFormAccess(true);
		block.setFormEditable(true);
		block.setRequired(true);
		block.setPriority(10);
		values = new ArrayList<String>();
		values.add("A");
		values.add("B");
		values.add("C");
		block.setValues(values);

		BaseFieldSelect title = new BaseFieldSelect("title");
		title.setQuery_alias("Test Title");
		title.setForm_alias("Test Title");
		title.setQueryVisible(true);
		title.setFormAccess(true);
		title.setFormEditable(true);
		title.setRequired(true);
		title.setPriority(10);
		values = new ArrayList<String>();
		values.add("Title 1");
		values.add("Title 2");
		values.add("Title 3");
		title.setValues(values);

		BaseFieldProgramScore score_i = new BaseFieldProgramScore("score_i");
		score_i.setQuery_alias("I Score");
		score_i.setForm_alias("I Score");
		score_i.setQueryVisible(true);
		score_i.setFormAccess(true);
		score_i.setFormEditable(true);
		score_i.setRequired(true);
		score_i.setPriority(20);

		BaseFieldFloat scorepercent_i = new BaseFieldFloat("scorepercent_i");
		scorepercent_i.setQuery_alias("I Score Percent");
		scorepercent_i.setForm_alias("I Score Percent");
		scorepercent_i.setQueryVisible(false);
		scorepercent_i.setFormAccess(false);
		scorepercent_i.setPriority(21);
		scorepercent_i.setFormat("#.0%");

		BaseFieldProgramScore score_org = new BaseFieldProgramScore("score_org");
		score_org.setQuery_alias("ORG Score");
		score_org.setForm_alias("ORG Score");
		score_org.setQueryVisible(true);
		score_org.setFormAccess(true);
		score_org.setFormEditable(true);
		score_org.setRequired(true);
		score_org.setPriority(22);

		BaseFieldFloat scorepercent_org = new BaseFieldFloat("scorepercent_org");
		scorepercent_org.setQuery_alias("ORG Score Percent");
		scorepercent_org.setForm_alias("ORG Score Percent");
		scorepercent_org.setQueryVisible(false);
		scorepercent_org.setFormAccess(false);
		scorepercent_org.setPriority(23);
		scorepercent_org.setFormat("#.0%");

		BaseFieldProgramScore score_v = new BaseFieldProgramScore("score_v");
		score_v.setQuery_alias("V Score");
		score_v.setForm_alias("V Score");
		score_v.setQueryVisible(true);
		score_v.setFormAccess(true);
		score_v.setFormEditable(true);
		score_v.setRequired(true);
		score_v.setPriority(24);

		BaseFieldFloat scorepercent_v = new BaseFieldFloat("scorepercent_v");
		scorepercent_v.setQuery_alias("V Score Percent");
		scorepercent_v.setForm_alias("V Score Percent");
		scorepercent_v.setQueryVisible(false);
		scorepercent_v.setFormAccess(false);
		scorepercent_v.setPriority(25);
		scorepercent_v.setFormat("#.0%");

		BaseFieldProgramScore score_wc = new BaseFieldProgramScore("score_wc");
		score_wc.setQuery_alias("WC Score");
		score_wc.setForm_alias("WC Score");
		score_wc.setQueryVisible(true);
		score_wc.setFormAccess(true);
		score_wc.setFormEditable(true);
		score_wc.setRequired(true);
		score_wc.setPriority(26);

		BaseFieldFloat scorepercent_wc = new BaseFieldFloat("scorepercent_wc");
		scorepercent_wc.setQuery_alias("WC Score Percent");
		scorepercent_wc.setForm_alias("WC Score Percent");
		scorepercent_wc.setQueryVisible(false);
		scorepercent_wc.setFormAccess(false);
		scorepercent_wc.setPriority(27);
		scorepercent_wc.setFormat("#.0%");

		BaseFieldProgramScore score_sf = new BaseFieldProgramScore("score_sf");
		score_sf.setQuery_alias("SF Score");
		score_sf.setForm_alias("SF Score");
		score_sf.setQueryVisible(true);
		score_sf.setFormAccess(true);
		score_sf.setFormEditable(true);
		score_sf.setRequired(true);
		score_sf.setPriority(28);

		BaseFieldFloat scorepercent_sf = new BaseFieldFloat("scorepercent_sf");
		scorepercent_sf.setQuery_alias("SF Score Percent");
		scorepercent_sf.setForm_alias("SF Score Percent");
		scorepercent_sf.setQueryVisible(false);
		scorepercent_sf.setFormAccess(false);
		scorepercent_sf.setPriority(29);
		scorepercent_sf.setFormat("#.0%");

		BaseFieldProgramScore score_c = new BaseFieldProgramScore("score_c");
		score_c.setQuery_alias("C Score");
		score_c.setForm_alias("C Score");
		score_c.setQueryVisible(true);
		score_c.setFormAccess(true);
		score_c.setFormEditable(true);
		score_c.setRequired(true);
		score_c.setPriority(30);

		BaseFieldFloat scorepercent_c = new BaseFieldFloat("scorepercent_c");
		scorepercent_c.setQuery_alias("C Score Percent");
		scorepercent_c.setForm_alias("C Score Percent");
		scorepercent_c.setQueryVisible(false);
		scorepercent_c.setFormAccess(false);
		scorepercent_c.setPriority(31);
		scorepercent_c.setFormat("#.0%");

		Map<String, BaseFieldAbstract> fields = new HashMap<String, BaseFieldAbstract>();
		fields.put("id", id);
		fields.put("studentname", studentname);
		fields.put("studentid", studentid);
		fields.put("group", group);
		fields.put("scoredate", scoredate);
		fields.put("number", number);
		fields.put("score", score);
		fields.put("scorepercent", scorepercent);
		fields.put("points", points);
		fields.put("notes", notes);
		fields.put("createdby", createdby);
		fields.put("createdon", createdon);
		fields.put("modifiedby", modifiedby);
		fields.put("modifiedon", modifiedon);

		System.out.println("Creating caughtya Program...");
		CustomObjectTest program = new CustomObjectTest("caughtya", "Caught Ya");
		program.setProgramScoreClass(ProgramScoreCaughtYa.class);
		program.setFields(fields);
		program.setType("program");
		getCustomTestService().save(program, "admin");

		System.out.println("Creating math Program...");
		program = new CustomObjectTest("math", "Math");
		program.setProgramScoreClass(ProgramScoreMath.class);
		program.setFields(fields);
		program.setType("program");
		getCustomTestService().save(program, "admin");

		System.out.println("Creating english Program...");
		program = new CustomObjectTest("english", "English");
		program.setProgramScoreClass(ProgramScoreEnglish.class);
		program.setFields(fields);
		program.setType("program");
		getCustomTestService().save(program, "admin");

		fields = new HashMap<String, BaseFieldAbstract>();
		fields.put("id", id);
		fields.put("studentname", studentname);
		fields.put("studentid", studentid);
		fields.put("group", group);
		fields.put("scoredate", scoredate);
		fields.put("addition_score", addition_score);
		fields.put("addition_scorepercent", addition_scorepercent);
		fields.put("subtraction_score", subtraction_score);
		fields.put("subtraction_scorepercent", subtraction_scorepercent);
		fields.put("multiplication_score", multiplication_score);
		fields.put("multiplication_scorepercent", multiplication_scorepercent);
		fields.put("division_score", division_score);
		fields.put("division_scorepercent", division_scorepercent);
		fields.put("points", points);
		fields.put("notes", notes);
		fields.put("createdby", createdby);
		fields.put("createdon", createdon);
		fields.put("modifiedby", modifiedby);
		fields.put("modifiedon", modifiedon);

		System.out.println("Creating nyaonlineassess Program...");
		program = new CustomObjectTest("nyaonlineassess", "NYA Online Assess");
		program.setProgramScoreClass(ProgramScoreNYAOnlineAssess.class);
		program.setFields(fields);
		program.setType("program");
		getCustomTestService().save(program, "admin");

		fields = new HashMap<String, BaseFieldAbstract>();
		fields.put("id", id);
		fields.put("studentname", studentname);
		fields.put("studentid", studentid);
		fields.put("group", group);
		fields.put("scoredate", scoredate);
		fields.put("level", level);
		fields.put("block", block);
		fields.put("score", score);
		fields.put("scorepercent", scorepercent);
		fields.put("points", points);
		fields.put("notes", notes);
		fields.put("createdby", createdby);
		fields.put("createdon", createdon);
		fields.put("modifiedby", modifiedby);
		fields.put("modifiedon", modifiedon);

		System.out.println("Creating nyaonlinemath Program...");
		program = new CustomObjectTest("nyaonlinemath", "NYA Online Math");
		program.setProgramScoreClass(ProgramScoreNYAOnlineMath.class);
		program.setFields(fields);
		program.setType("program");
		getCustomTestService().save(program, "admin");

		fields = new HashMap<String, BaseFieldAbstract>();
		fields.put("id", id);
		fields.put("studentname", studentname);
		fields.put("studentid", studentid);
		fields.put("group", group);
		fields.put("scoredate", scoredate);
		fields.put("title", title);
		fields.put("score_i", score_i);
		fields.put("scorepercent_i", scorepercent_i);
		fields.put("score_org", score_org);
		fields.put("scorepercent_org", scorepercent_org);
		fields.put("score_v", score_v);
		fields.put("scorepercent_v", scorepercent_v);
		fields.put("score_wc", score_wc);
		fields.put("scorepercent_wc", scorepercent_wc);
		fields.put("score_sf", score_sf);
		fields.put("scorepercent_sf", scorepercent_sf);
		fields.put("score_c", score_c);
		fields.put("scorepercent_c", scorepercent_c);
		fields.put("points", points);
		fields.put("notes", notes);
		fields.put("createdby", createdby);
		fields.put("createdon", createdon);
		fields.put("modifiedby", modifiedby);
		fields.put("modifiedon", modifiedon);

		System.out.println("Creating sixtraits Program...");
		program = new CustomObjectTest("sixtraits", "6 Traits");
		program.setProgramScoreClass(ProgramScoreSixTraits.class);
		program.setFields(fields);
		program.setType("program");
		getCustomTestService().save(program, "admin");

	}

	private void checkData() {

		List<Role> roles = getIdentityService().getAllRoles();
		List<User> users = getIdentityService().getAllUsers();
		List<CustomObjectTest> programs = getCustomTestService().getAll();

		// System.out.println("number of groups: " + groups.size());
		// System.out.println("group 1 name: " + groups.get(0).getName());
		// System.out.println("group 1 ID: " + groups.get(0).getId());

		System.out.println("Printing out Groups:");
		for (Role role : roles) {

			System.out.println("Role: " + role.getName());

			if (role.getUsers().size() > 0) {

				List<User> roleusers = getIdentityService().getRoleUsers(
						role.getName());

				for (User u : roleusers) {

					// System.out.println("Linked User: " + u.getUsername());
					System.out.println("Linked User");

				}

			}

		}

		System.out.println("Printing out Users:");
		for (User user : users) {

			System.out.println("User: " + user.getUsername());

			System.out.println("# of roles: "
					+ getIdentityService().getUserRoles(user.getUsername())
							.size());

		}

		System.out.println("Printing out Programs:");
		for (CustomObjectTest program : programs) {

			System.out.println("Program:" + program.getName());

		}

	}

	private IdentityService getIdentityService() {

		return new IdentityService();

	}

	private SiteService getSiteService() {

		return new SiteService(Site.class);

	}

	private CustomTestService getCustomTestService() {

		return new CustomTestService(CustomObjectTest.class);

	}
	
	private ProjectionProfileService getProjectionProfileService() {

		return new ProjectionProfileService(ProjectionProfile.class);

	}

}
