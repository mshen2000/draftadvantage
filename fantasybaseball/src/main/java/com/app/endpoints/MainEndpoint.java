package com.app.endpoints;

import com.app.endpoints.entities.ProjectionContainer;
import com.app.endpoints.entities.ProjectionAttributeMap;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.Nullable;
import com.google.api.server.spi.response.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nya.sms.dataservices.IdentityService;
import com.nya.sms.dataservices.PlayerProjectedService;
import com.nya.sms.entities.PlayerProjected;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

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

	@ApiMethod(name = "main.getprojections")
	public List<PlayerProjected> GetProjections(HttpServletRequest req)
			throws UnauthorizedException {

		APIToken token = new APIToken(
				req.getHeader("Authorization").split(" ")[1]);

		if (!getIdentityService().validateAdminJWT(token))
			throw new UnauthorizedException("Token is invalid");

		return getPlayerProjectedService().getAllPlayerProjected();

	}
   

  @ApiMethod(name = "main.updateplayerprojections", httpMethod = "post")
  public APIGeneralResult updatePlayerProjections(@Nullable ProjectionContainer container, HttpServletRequest req) 
		  throws InternalServerErrorException, UnauthorizedException {
	
	APIToken token = new APIToken(
			req.getHeader("Authorization").split(" ")[1]);

	if (!getIdentityService().validateAdminJWT(token))
		throw new UnauthorizedException("Token is invalid");
    
    com.nya.sms.entities.User user = getIdentityService().getUserfromToken(token);

    System.out.println("Container String: " + container.getProjectionsJSONString());
    
    Gson gson = new Gson();
	    
    List<PlayerProjected> p_array = gson.fromJson(container.getProjectionsJSONString(), new TypeToken<List<PlayerProjected>>(){}.getType()); 
    
    System.out.println("Player 0: " + p_array.get(0).getFull_name());

    int count = 0;
	
    try
    {
    	count = getPlayerProjectedService().updatePlayerProjections(p_array, container.getProj_service(), 
    			container.getProj_period(), container.getProj_date(), container.getProj_year(), user.getUsername());
    	
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
  
  
	@ApiMethod(name = "map.getattributemap", httpMethod = "post")
	public ProjectionAttributeMap getAttributeMap(HttpServletRequest req)
			throws UnauthorizedException {

		String attributes = "";
		
		APIToken token = new APIToken(
				req.getHeader("Authorization").split(" ")[1]);

		if (!getIdentityService().validateAdminJWT(token))
			throw new UnauthorizedException("Token is invalid");

		try {
			attributes = getPlayerProjectedService()
					.getPlayerProjectionAttributes();
			if (attributes.length() > 0)
				return new ProjectionAttributeMap(attributes,
						"Attributes available");
			else
				return new ProjectionAttributeMap("none",
						"No Player projection attributes returned.");

		} catch (Exception e) {
			e.printStackTrace();
			return new ProjectionAttributeMap("none",
					"No Player projection attributes returned.");
		}

	}

	
}
