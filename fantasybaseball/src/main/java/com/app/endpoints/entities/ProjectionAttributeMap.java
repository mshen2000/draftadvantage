package com.app.endpoints.entities;

public class ProjectionAttributeMap {
	
  public String attributes;
  
  public String note;

  public ProjectionAttributeMap() {}
  
  	public ProjectionAttributeMap(String attributes) {
  		this.attributes = attributes;
    }
  	
  	public ProjectionAttributeMap(String attributes, String note) {
  		this.attributes = attributes;
  		this.note = note;
    }

	public String getAttributes() {
		return attributes;
	}
	
	public void setAttributes(String attributes) {
		this.attributes = attributes;
	}
	
	public String getNote() {
		return note;
	}
	
	public void setNote(String note) {
		this.note = note;
	}

}
