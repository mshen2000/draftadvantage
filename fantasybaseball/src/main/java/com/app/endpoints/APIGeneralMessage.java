package com.app.endpoints;


/**
 * @author Michael
 *	Container bean object for return message with token.
 */
public class APIGeneralMessage {
	
	public String msg;
	
	public String token;
	
	public APIGeneralMessage(){}

	public APIGeneralMessage(String msg) {
		this.msg = msg;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

}
