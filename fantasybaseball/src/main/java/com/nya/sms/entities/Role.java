package com.nya.sms.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.EntitySubclass;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Load;
import com.googlecode.objectify.annotation.OnLoad;
import com.googlecode.objectify.annotation.OnSave;
import com.googlecode.objectify.annotation.Serialize;
import com.nya.sms.dataservices.SiteService;

@EntitySubclass(index = true)
public class Role extends BaseEntity implements Serializable {

	private static final long serialVersionUID = 1;

	@Index
	String name;
	String alias;
	@Index
	String site;
	@Index
	List<Ref<User>> users = new ArrayList<Ref<User>>();

	String access_userrole;
	String access_sg;
	String access_students;
	Boolean access_med;
	String access_program;
	String access_test;
	String access_points;
	String access_grades;
	String access_schedule;
	String access_notes;
	String studentdata_access_scope;
	Boolean access_app_admin;
	Boolean access_app_student;
	Boolean access_app_program;
	Boolean access_app_test;
	Boolean access_app_points;
	Boolean access_app_grades;
	Boolean access_app_schedule;
	Boolean access_app_notes;

	private Role() {
	}

	public Role(String name, String alias) {
		this.name = name;
		this.alias = alias;
	}

	// update site field with site that has this role as child
	@OnLoad
	void onLoad() {

		System.out.println("Loading role: " + this.getName());

		Ref<Role> roleref = Ref.create(this);

		// Get site objects that have this role as a child (there should be only
		// one)
		List<Site> sites = ObjectifyService.ofy().load().type(Site.class)
				.filter("roles", roleref).list();

		if (site != null) {
			if (site.equals(getSiteService().getMasterSite())) {

				System.out
						.println("Site for this role is the Master Site, exiting onLoad for Role");
				return;

			}
		}

		if (sites.size() == 0) {

			System.out
					.println("Role "
							+ name
							+ " is not associated to any sites, site field will be null");

			site = null;

		} else {

			if (sites.size() > 1) {

				System.out
						.println("Found more than one site that contains this role, there should be only one.  Will use the first one.");

			}

			System.out.println("Filling site field with site name "
					+ sites.get(0).getName());

			site = sites.get(0).getName();

		}

	}

	public List<Ref<User>> getUsers() {

		return users;
	}

	public void setGroupusers(List<Ref<User>> users) {
		this.users = users;
	}

	public void addUser(User user) {

		Ref<User> ref = Ref.create(user);

		if (!users.contains(ref))
			users.add(ref);

	}

	public void removeUser(User user) {

		Ref<User> ref = Ref.create(user);

		if (users.contains(ref))
			users.remove(ref);

	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getSite() {
		return site;
	}

	public void setSite(String site) {
		this.site = site;
	}

	public String getAccess_userrole() {
		return access_userrole;
	}

	public void setAccess_userrole(String access_userrole) {
		this.access_userrole = access_userrole;
	}

	public String getAccess_sg() {
		return access_sg;
	}

	public void setAccess_sg(String access_sg) {
		this.access_sg = access_sg;
	}

	public String getAccess_students() {
		return access_students;
	}

	public void setAccess_students(String access_students) {
		this.access_students = access_students;
	}

	public Boolean getAccess_med() {
		return access_med;
	}

	public void setAccess_med(Boolean access_med) {
		this.access_med = access_med;
	}

	public String getAccess_program() {
		return access_program;
	}

	public void setAccess_program(String access_program) {
		this.access_program = access_program;
	}

	public String getAccess_test() {
		return access_test;
	}

	public void setAccess_test(String access_test) {
		this.access_test = access_test;
	}

	public String getAccess_points() {
		return access_points;
	}

	public void setAccess_points(String access_points) {
		this.access_points = access_points;
	}

	public String getAccess_grades() {
		return access_grades;
	}

	public void setAccess_grades(String access_grades) {
		this.access_grades = access_grades;
	}

	public String getAccess_schedule() {
		return access_schedule;
	}

	public void setAccess_schedule(String access_schedule) {
		this.access_schedule = access_schedule;
	}

	public String getAccess_notes() {
		return access_notes;
	}

	public void setAccess_notes(String access_notes) {
		this.access_notes = access_notes;
	}

	public String getStudentdata_access_scope() {
		return studentdata_access_scope;
	}

	public void setStudentdata_access_scope(String studentdata_access_scope) {
		this.studentdata_access_scope = studentdata_access_scope;
	}

	public Boolean getAccess_app_admin() {
		return access_app_admin;
	}

	public void setAccess_app_admin(Boolean access_app_admin) {
		this.access_app_admin = access_app_admin;
	}

	public Boolean getAccess_app_student() {
		return access_app_student;
	}

	public void setAccess_app_student(Boolean access_app_student) {
		this.access_app_student = access_app_student;
	}

	public Boolean getAccess_app_program() {
		return access_app_program;
	}

	public void setAccess_app_program(Boolean access_app_program) {
		this.access_app_program = access_app_program;
	}

	public Boolean getAccess_app_test() {
		return access_app_test;
	}

	public void setAccess_app_test(Boolean access_app_test) {
		this.access_app_test = access_app_test;
	}

	public Boolean getAccess_app_points() {
		return access_app_points;
	}

	public void setAccess_app_points(Boolean access_app_points) {
		this.access_app_points = access_app_points;
	}

	public Boolean getAccess_app_grades() {
		return access_app_grades;
	}

	public void setAccess_app_grades(Boolean access_app_grades) {
		this.access_app_grades = access_app_grades;
	}

	public Boolean getAccess_app_schedule() {
		return access_app_schedule;
	}

	public void setAccess_app_schedule(Boolean access_app_schedule) {
		this.access_app_schedule = access_app_schedule;
	}

	public Boolean getAccess_app_notes() {
		return access_app_notes;
	}

	public void setAccess_app_notes(Boolean access_app_notes) {
		this.access_app_notes = access_app_notes;
	}

	private SiteService getSiteService() {

		return new SiteService(Site.class);

	}

}
