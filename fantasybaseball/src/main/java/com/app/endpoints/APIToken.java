package com.app.endpoints;

public class APIToken {
	
  public String token;

  public APIToken() {};

  public APIToken(String token) {
    this.token = token;
    }

	public String getToken() {
		return token;
	}
	
	public void setToken(String token) {
		this.token = token;
	}


}
