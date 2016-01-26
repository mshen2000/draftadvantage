package com.nya.sms.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Index;

//@Subclass(index = true)
@Entity
public class Site extends BaseEntity implements Serializable {

	private static final long serialVersionUID = 1;

	@Index
	String name;
	String description;
	@Index
	List<Ref<Student>> students = new ArrayList<Ref<Student>>();
	@Index
	List<Ref<StudentGroup>> studentgroups = new ArrayList<Ref<StudentGroup>>();
	@Index
	List<Ref<Role>> roles = new ArrayList<Ref<Role>>();

	@SuppressWarnings("unused")
	private Site() {
	}

	public Site(String name) {
		this.name = name;

	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	// Students
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

	// Student Groups
	public List<Ref<StudentGroup>> getStudentgroups() {
		return studentgroups;
	}

	public void setStudentgroups(List<Ref<StudentGroup>> studentgroups) {
		this.studentgroups = studentgroups;
	}

	public void addStudentGroup(StudentGroup studentgroup) {

		Ref<StudentGroup> ref = Ref.create(studentgroup);

		if (!studentgroups.contains(ref))
			studentgroups.add(ref);

	}

	public void removeStudentGroup(StudentGroup studentgroup) {

		Ref<StudentGroup> ref = Ref.create(studentgroup);

		if (studentgroups.contains(ref))
			studentgroups.remove(ref);

	}

	// Roles
	public List<Ref<Role>> getRoles() {
		return roles;
	}

	public void setRoles(List<Ref<Role>> roles) {
		this.roles = roles;
	}

	public void addRole(Role role) {

		Ref<Role> ref = Ref.create(role);

		if (!roles.contains(ref))
			roles.add(ref);

	}

	public void removeRole(Role role) {

		Ref<Role> ref = Ref.create(role);

		if (roles.contains(ref))
			roles.remove(ref);

	}

}
