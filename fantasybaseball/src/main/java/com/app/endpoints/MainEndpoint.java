package com.app.endpoints;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.response.*;
import com.google.appengine.api.users.User;
import com.nya.sms.dataservices.IdentityService;
import com.nya.sms.dataservices.PlayerProjectedService;
import com.nya.sms.entities.PlayerProjected;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * Defines v1 of a DraftApp API, which provides the application methods.
 */
@Api(
    name = "draftapp",
    version = "v1",
    scopes = {Constants.EMAIL_SCOPE},
    clientIds = {Constants.WEB_CLIENT_ID, Constants.ANDROID_CLIENT_ID, Constants.IOS_CLIENT_ID, Constants.API_EXPLORER_CLIENT_ID},
    audiences = {Constants.ANDROID_AUDIENCE}
)
public class MainEndpoint {
	
	 private IdentityService getIdentityService() {
		 
		 return new IdentityService();
	 
	 }
	 
	 private PlayerProjectedService getPlayerProjectedService() {
		 
		 return new PlayerProjectedService();
	 
	 }

  @ApiMethod(name = "main.authed", path = "draftappmain/authed")
  public APIGeneralResult authedUser(User user) {
	  
	  APIGeneralResult response = new APIGeneralResult("KO", "UNKNOWN");
	  
	  if (user != null) {
		  if (getIdentityService().isUserExtIDPresent(user.getUserId()))
			  response = new APIGeneralResult("OK", user.getNickname());
		  else response = new APIGeneralResult("KO", "User Not in DB");
	  }
	  
    return response;
  }
  
  
  
  @ApiMethod(name = "main.getprojections")
  public List<PlayerProjected> GetProjections(User user) throws UnauthorizedException {
    
	// if (validateUser(user))
		return getPlayerProjectedService().getAllPlayerProjected();
    
    // throw new UnauthorizedException("UA09 - Invalid credentials.");
  }
   


  @ApiMethod(name = "main.updateprojections", httpMethod = "post")
  public APIGeneralResult UpdateProjections(APIPlayerProjectionContainer container, @Named("proj_service") String proj_service, 
		  @Named("proj_period") String proj_period, /* @Named("proj_date") Date proj_date,*/ @Named("year") Integer year, @Named("token") String token) 
		  throws InternalServerErrorException {
	  
	System.out.println("In UpdateProjections endpoint.");
	  
    if(!getIdentityService().validateAdminJWT(token)) //Validate credentials
    	return new APIGeneralResult("KO", "Token is invalid");
    
    com.nya.sms.entities.User user = getIdentityService().getUserfromToken(token);
	    
	Integer count = 0;
	Date date = new Date();

    try
    {
    	count = getPlayerProjectedService().updatePlayerProjections(container.getPlayerlist(), proj_service, 
    			proj_period, date, year, user.getUsername());
    	
    	System.out.println("After update service call, count = " + count);
    	
    	if (count > 0)
    		return new APIGeneralResult("OK", "Number of player projections updated: " + count);
    	else
    		return new APIGeneralResult("KO", "No Player projections were created.");

    }catch(Exception e)
    {
      throw new InternalServerErrorException("UA08 - Internal error. " + e);
    }

  }
  
  @ApiMethod(name = "main.getprojectionattributes", httpMethod = "get")
  public APIGeneralResult getProjectionAttributes(@Named("token") String token){
	
	String attributes = "";
	
    if(!getIdentityService().validateAdminJWT(token)) //Validate credentials
    	return new APIGeneralResult("KO", "Token is invalid");

    try
    {
    	attributes = getPlayerProjectedService().getPlayerProjectionAttributes();
    	if (attributes.length() > 0)
    		return new APIGeneralResult("OK", attributes);
    	else
    		return new APIGeneralResult("KO", "No Player projection attributes returned.");

    }catch(Exception e)
    {
    	e.printStackTrace();
    	return new APIGeneralResult("KO", "No Player projection attributes returned.");
    }

  }


	private boolean validateUser(User user) {
		// TODO Auto-generated method stub
		
		  if (user != null) {
			  if (getIdentityService().isUserExtIDPresent(user.getUserId()))
				  return true;
			  else return false;
		  }
		
		return false;
	}
	
}
