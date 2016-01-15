package com.app.endpoints;

import com.app.endpoints.entities.ProjectionAttributeMap;
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
   


  @ApiMethod(name = "main.updateplayerprojections", httpMethod = "post")
  public APIGeneralResult updatePlayerProjections(APIGeneralMessage container) 
		  throws InternalServerErrorException {
	  
	System.out.println("In UpdateProjections endpoint.");
	
	APIToken token = new APIToken(container.getToken());
	  
    if(!getIdentityService().validateAdminJWT(token)) //Validate credentials
    	return new APIGeneralResult("KO", "Token is invalid");
    
    com.nya.sms.entities.User user = getIdentityService().getUserfromToken(token);
	    
	Integer count = 0;
	Date date = new Date();

	/*
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
    */

	return new APIGeneralResult("KO", "No Player projections were created.");
  }
  
  
  @ApiMethod(name = "map.getattributemap", httpMethod = "post")
  public ProjectionAttributeMap getAttributeMap(HttpServletRequest req){

	  APIToken token = new APIToken(req.getHeader("Authorization").split(" ")[1]);
	  
	String attributes = "";
	
    if(!getIdentityService().validateAdminJWT(token)) 
    	return new ProjectionAttributeMap("none", "Token is invalid");

    try
    {
    	attributes = getPlayerProjectedService().getPlayerProjectionAttributes();
    	if (attributes.length() > 0)
    		return new ProjectionAttributeMap(attributes, "Attributes available");
    	else
    		return new ProjectionAttributeMap("none", "No Player projection attributes returned.");

    }catch(Exception e)
    {
    	e.printStackTrace();
    	return new ProjectionAttributeMap("none", "No Player projection attributes returned.");
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
