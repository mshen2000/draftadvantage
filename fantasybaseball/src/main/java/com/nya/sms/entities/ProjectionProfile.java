package com.nya.sms.entities;

import java.io.Serializable;
import java.util.Date;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Index;

//@Subclass(index = true)
@Entity
public class ProjectionProfile extends BaseEntity implements Serializable {
	
	private static final long serialVersionUID = 1;
	
	@Index
	public Integer projected_year;
	
	@Index
	public String projection_service;
	
	@Index
	public String projection_period;  // Pre-Season, ROS
	
	@Index
	Date projection_date;
	
	long pitchers;
	
	long hitters;

	public ProjectionProfile() {
	}

	public ProjectionProfile(Integer projected_year, String projection_service, String projection_period, Date projection_date) {
		this.projected_year = projected_year;
		this.projection_service = projection_service;
		this.projection_period = projection_period;
		this.projection_date = projection_date;
	}

	public Integer getProjected_year() {
		return projected_year;
	}

	public void setProjected_year(Integer projected_year) {
		this.projected_year = projected_year;
	}

	public String getProjection_service() {
		return projection_service;
	}

	public void setProjection_service(String projection_service) {
		this.projection_service = projection_service;
	}

	public String getProjection_period() {
		return projection_period;
	}

	public void setProjection_period(String projection_period) {
		this.projection_period = projection_period;
	}

	public Date getProjection_date() {
		return projection_date;
	}

	public void setProjection_date(Date projection_date) {
		this.projection_date = projection_date;
	}

	public long getPitchers() {
		return pitchers;
	}

	public void setPitchers(long pitchers) {
		this.pitchers = pitchers;
	}

	public long getHitters() {
		return hitters;
	}

	public void setHitters(long hitters) {
		this.hitters = hitters;
	}



}
