package com.app.endpoints;


/**
 * @author Michael
 *	Container bean object for return message with token.
 */
public class APIGeneralMessage {
	
	private String message;
	
	private String token;
	
	public APIGeneralMessage(){}

	public APIGeneralMessage(String message, String token) {
		this.message = message;
		this.token = token;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

}
