package com.app.endpoints.entities;

public class ProjectionService {
	
	public String projection_service_name;
	
	public ProjectionService() {}

	public ProjectionService(String projection_service_name) {
		this.projection_service_name = projection_service_name;
	}

	public String getProjection_service_name() {
		return projection_service_name;
	}

	public void setProjection_service_name(String projection_service_name) {
		this.projection_service_name = projection_service_name;
	}

	
}
