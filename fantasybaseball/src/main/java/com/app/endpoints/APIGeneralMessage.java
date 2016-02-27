package com.app.endpoints;


/**
 * @author Michael
 *	Container bean object for return message with token.
 */
public class APIGeneralMessage {
	
	public String msg;
	
	public String token;
	
	long longmsg;
	
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

	public long getLongmsg() {
		return longmsg;
	}

	public void setLongmsg(long longmsg) {
		this.longmsg = longmsg;
	}
	
	

}
