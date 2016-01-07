package com.app.endpoints;

public class APIGeneralResult {
	
  public String status;
  public String description;

  public APIGeneralResult() {};

  public APIGeneralResult(String status, String description) {
    this.status = status;
    this.description = description;
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
  
  

}
