package com.nya.sms.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Load;
import com.googlecode.objectify.annotation.OnLoad;
import com.googlecode.objectify.annotation.OnSave;

//@Subclass(index = true)
@Entity
public class StudentGroup extends BaseEntity implements Serializable {

	private static final long serialVersionUID = 1;

	@Index
	String name;
	@Index
	String site;
	@Index
	String leadername;
	@Index
	@Load
	Ref<User> leader;
	@Index
	List<Ref<Student>> students = new ArrayList<Ref<Student>>();

	@SuppressWarnings("unused")
	private StudentGroup() {
	}

	public StudentGroup(String name) {

		this.name = name;

	}

	@OnSave
	void onSave() {

		if (leader != null) {

			ObjectifyService.ofy().load().ref(leader);
			User u = leader.get();

			this.leadername = u.firstname + " " + u.lastname;

		} else {

			this.leadername = null;

		}

	}

	// update site field with site that has this student group as child
	@OnLoad
	void onLoad() {

		Ref<StudentGroup> studentgroupref = Ref.create(this);

		// Get site objects that have this student group as a child (there
		// should be only one)
		List<Site> sites = ObjectifyService.ofy().load().type(Site.class)
				.filter("studentgroups", studentgroupref).list();

		if (sites.size() == 0) {

			System.out
					.println("StudentGroup "
							+ name
							+ " is not associated to any sites, site field will be null");

			site = null;

		} else {

			if (sites.size() > 1) {

				System.out
						.println("Found more than one site that contains this student group, there should be only one.  Will use the first one.");

			}

			System.out.println("Filling site field with site name "
					+ sites.get(0).getName());

			site = sites.get(0).getName();

		}

	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSite() {
		return site;
	}

	public void setSite(String site) {
		this.site = site;
	}

	public String getLeadername() {
		return leadername;
	}

	public void setLeadername(String leadername) {
		this.leadername = leadername;
	}

	public Ref<User> getLeader() {
		return leader;
	}

	public void setLeader(Ref<User> leader) {
		this.leader = leader;
	}

	public List<Ref<Student>> getStudents() {
		return students;
	}

	public void setStudents(List<Ref<Student>> students) {
		this.students = students;
	}

	public void addStudent(Student student) {

		Ref<Student> ref = Ref.create(student);

		if (!students.contains(ref))
			students.add(ref);

	}

	public void removeStudent(Student student) {

		Ref<Student> ref = Ref.create(student);

		if (students.contains(ref))
			students.remove(ref);

	}

}
