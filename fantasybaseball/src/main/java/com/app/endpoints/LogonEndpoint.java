package com.app.endpoints;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.response.*;
import com.google.appengine.api.users.User;
import com.nya.sms.dataservices.IdentityService;

import java.util.ArrayList;

import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * Defines v1 of a helloworld API, which provides simple "greeting" methods.
 */
@Api(
    name = "authenticate",
    version = "v1",
    scopes = {Constants.EMAIL_SCOPE},
    clientIds = {Constants.WEB_CLIENT_ID, Constants.ANDROID_CLIENT_ID, Constants.IOS_CLIENT_ID, Constants.API_EXPLORER_CLIENT_ID},
    audiences = {Constants.ANDROID_AUDIENCE}
)
public class LogonEndpoint {

  public static ArrayList<HelloGreeting> greetings = new ArrayList<HelloGreeting>();

  public com.nya.sms.entities.User usr1_r = getIdentityService().getUser("admin");
  
  
  {
    greetings.add(new HelloGreeting("hello world!"));
    greetings.add(new HelloGreeting("goodbye world!"));
    greetings.add(new HelloGreeting(usr1_r.getFirstname()));
  }
  
  
	 private IdentityService getIdentityService() {
		 
		 return new IdentityService();
	 
	 }

  public HelloGreeting getGreeting(@Named("id") Integer id) throws NotFoundException {
    try {
      return greetings.get(id);
    } catch (IndexOutOfBoundsException e) {
      throw new NotFoundException("Greeting not found with an index: " + id);
    }
  }

  public ArrayList<HelloGreeting> listGreeting() {
    return greetings;
  }

  @ApiMethod(name = "greetings.multiply", httpMethod = "post")
  public HelloGreeting insertGreeting(@Named("times") Integer times, HelloGreeting greeting) {
    HelloGreeting response = new HelloGreeting();
    StringBuilder responseBuilder = new StringBuilder();
    for (int i = 0; i < times; i++) {
      responseBuilder.append(greeting.getMessage());
    }
    response.setMessage(responseBuilder.toString());
    return response;
  }

  @ApiMethod(name = "greetings.authed", path = "hellogreeting/authed")
  public HelloGreeting authedGreeting(User user) {
    HelloGreeting response = new HelloGreeting("hello " + user.getEmail());
    return response;
  }
  
  @ApiMethod(name = "auth.customlogon", httpMethod = "post")
  public APIGeneralResult CustomLogon(LogonPackage logonPackage, HttpServletRequest req) 
		  throws InternalServerErrorException, BadRequestException, UnauthorizedException {
    //Validate input
    String email = logonPackage.getEmail();
    String password = logonPackage.getPassword();
    if (!validateEmailAddress(email) || !validatePassword(password))
      throw new BadRequestException("UA01 - Invalid input.");

    try
    {
      if(validateLogin(email, password)) //Validate credentials
      {
        HttpSession session = req.getSession();
        session.setAttribute("UserEmail", email);
        session.setAttribute("Authenticated", true);
        return new APIGeneralResult("OK", getIdentityService().generateJWT(email));
      }
      else return new APIGeneralResult("KO", "Invalid credentials.");

    }catch(Exception e)
    {
      // Logger.logError("UA08 - Exception while logging user in through Mmarazzu logon. Email: " + email + ". ", e);
      throw new InternalServerErrorException("UA08 - Internal error.");
    }

  }
  
  
  @ApiMethod(name = "auth.authenticateadmin", httpMethod = "post")
  public APIGeneralResult AuthenticateAdmin(@Named("token") String token)  {

      if(getIdentityService().validateAdminJWT(token)) //Validate credentials
      {
        return new APIGeneralResult("OK", "Token is valid");
      }
      else return new APIGeneralResult("KO", "Token is invalid");

  }

	private boolean validateLogin(String email, String password) {
		
		if (getIdentityService().checkPassword(email, password))
			return true;
		
		// TODO Auto-generated method stub
		return false;
	}

	private boolean validatePassword(String password) {
		// TODO Auto-generated method stub
		return true;
	}

	private boolean validateEmailAddress(String email) {
		// TODO Auto-generated method stub
		return true;
	}
  
  
  
}
