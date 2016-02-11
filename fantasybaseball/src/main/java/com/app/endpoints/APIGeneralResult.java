package com.app.endpoints;

public class APIGeneralResult {

	public String status;
	public String description;
	public String description2;

	public APIGeneralResult() {
	};

	public APIGeneralResult(String status, String description) {
		this.status = status;
		this.description = description;
	}

	public APIGeneralResult(String status, String description, String description2) {
		this.status = status;
		this.description = description;
		this.description2 = description2;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDescription2() {
		return description2;
	}

	public void setDescription2(String description2) {
		this.description2 = description2;
	}

}
