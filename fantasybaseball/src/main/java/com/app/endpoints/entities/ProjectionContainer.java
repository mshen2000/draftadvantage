package com.app.endpoints.entities;

import java.util.Date;

public class ProjectionContainer {

	public String ProjectionsJSONString;
	
	public String proj_service;
	
	public String proj_period;
	
	public Date proj_date;
	
	public int proj_year;

	public String getProjectionsJSONString() {
		return ProjectionsJSONString;
	}

	public void setProjectionsJSONString(String projectionsJSONString) {
		ProjectionsJSONString = projectionsJSONString;
	}

	public String getProj_service() {
		return proj_service;
	}

	public void setProj_service(String proj_service) {
		this.proj_service = proj_service;
	}

	public String getProj_period() {
		return proj_period;
	}

	public void setProj_period(String proj_period) {
		this.proj_period = proj_period;
	}

	public Date getProj_date() {
		return proj_date;
	}

	public void setProj_date(Date proj_date) {
		this.proj_date = proj_date;
	}

	public int getProj_year() {
		return proj_year;
	}

	public void setProj_year(int proj_year) {
		this.proj_year = proj_year;
	}


}
