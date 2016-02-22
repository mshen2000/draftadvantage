package com.app.endpoints;

import com.app.endpoints.entities.ProjectionContainer;
import com.app.endpoints.entities.ProjectionAttributeMap;
import com.app.endpoints.entities.ProjectionPeriod;
import com.app.endpoints.entities.ProjectionService;
import com.app.endpoints.utilities.FloatTypeAdapter;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.Nullable;
import com.google.api.server.spi.response.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.nya.sms.dataservices.IdentityService;
import com.nya.sms.dataservices.LeagueService;
import com.nya.sms.dataservices.PlayerProjectedService;
import com.nya.sms.dataservices.ProjectionProfileService;
import com.nya.sms.entities.League;
import com.nya.sms.entities.PlayerProjected;
import com.nya.sms.entities.ProjectionProfile;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

/**
 * Defines v1 of a DraftApp API, which provides the application methods.
 */
@Api(name = "draftapp", version = "v1", scopes = { Constants.EMAIL_SCOPE }, clientIds = { Constants.WEB_CLIENT_ID,
		Constants.ANDROID_CLIENT_ID, Constants.IOS_CLIENT_ID, Constants.API_EXPLORER_CLIENT_ID }, audiences = { Constants.ANDROID_AUDIENCE })
public class MainEndpoint {
	
	private static final Logger log =Logger.getLogger(MainEndpoint.class.getName());

	private IdentityService getIdentityService() {

		return new IdentityService();

	}

	private PlayerProjectedService getPlayerProjectedService() {

		return new PlayerProjectedService();

	}

	private ProjectionProfileService getProjectionProfileService() {

		return new ProjectionProfileService(ProjectionProfile.class);

	}

	private LeagueService getLeagueService() {

		return new LeagueService(League.class);

	}

	
	private com.nya.sms.entities.User validateAdminToken(HttpServletRequest req) throws UnauthorizedException {
		APIToken token = new APIToken(req.getHeader("Authorization").split(" ")[1]);

		if (!getIdentityService().validateAdminJWT(token))
			throw new UnauthorizedException("Token is invalid");

		return getIdentityService().getUserfromToken(token);
	}
	
	
	
	@ApiMethod(name = "league.getuserleagues")
	public List<League> getUserLeagues(HttpServletRequest req) throws UnauthorizedException {

		return getLeagueService().getUserLeagues(validateAdminToken(req).getUsername());

	}
	

	@ApiMethod(name = "playerprojections.get", httpMethod = "post")
	public List<PlayerProjected> getProjections(APIGeneralMessage m, HttpServletRequest req)
			throws UnauthorizedException {
		System.out.println("Profile id: " + m.getMsg());
		validateAdminToken(req);

		NumberFormat formatter = new DecimalFormat("#0.00");

		log.setLevel(Level.INFO);
		log.info("Test Log Message");

		double startTime = System.currentTimeMillis();

		Long profile_id = Long.parseLong(m.getMsg().trim());

		List<PlayerProjected> l = getPlayerProjectedService().getPlayerProjections(
				getProjectionProfileService().get(profile_id), LeagueService.MLB_LEAGUES_BOTH);

		double estimatedTime = System.currentTimeMillis() - startTime;

		log.info("Time to get projections from Objectify: " + formatter.format(estimatedTime / 1000) + " seconds");

		return l;

	}

	@ApiMethod(name = "projectionprofile.getall")
	public List<ProjectionProfile> getAllProjectionProfiles(HttpServletRequest req) throws UnauthorizedException {

		validateAdminToken(req);

		return getProjectionProfileService().getAll();

	}


	@ApiMethod(name = "projectionprofile.save")
	public APIGeneralResult saveProjectionProfile(ProjectionProfile profile, HttpServletRequest req)
			throws UnauthorizedException {

//		System.out.println("Endpoint Profile Service: " + profile.getProjection_service());
//		System.out.println("Endpoint Profile Period: " + profile.getProjection_period());
//		System.out.println("Endpoint Profile Year: " + profile.getProjected_year());

		getProjectionProfileService().save(profile, validateAdminToken(req).getUsername());

		return new APIGeneralResult("OK", "Save profile successful.");

	}

	@ApiMethod(name = "projectionprofile.remove", httpMethod = "put")
	public APIGeneralResult removeProjectionProfile(APIGeneralMessage m, HttpServletRequest req)
			throws UnauthorizedException {
		System.out.println("Profile ID to Delete: " + m.getMsg());
		validateAdminToken(req);

		getProjectionProfileService().delete(Long.parseLong(m.getMsg().trim()));

		return new APIGeneralResult("OK", "Delete profile successful.");

	}

	@ApiMethod(name = "main.deleteallprojections")
	public APIGeneralResult deleteAllProjections(HttpServletRequest req) throws UnauthorizedException {

		APIToken token = new APIToken(req.getHeader("Authorization").split(" ")[1]);

		if (!getIdentityService().validateAdminJWT(token))
			throw new UnauthorizedException("Token is invalid");

		getPlayerProjectedService().deleteAllPlayerProjections();

		return new APIGeneralResult("OK", "Delete all projections successful.");

	}

	@ApiMethod(name = "main.updateplayerprojections", httpMethod = "post")
	public APIGeneralResult updatePlayerProjections(@Nullable ProjectionContainer container, HttpServletRequest req)
			throws InternalServerErrorException, UnauthorizedException {

		com.nya.sms.entities.User user = validateAdminToken(req);

		// System.out.println("Container String: " + container.getProjectionsJSONString());

		GsonBuilder builder = new GsonBuilder();
		builder.registerTypeAdapter(float.class, new FloatTypeAdapter());
		// if Adapter didn't check for nulls in its read/write methods, you
		// should instead use
		// builder.registerTypeAdapter(Point.class, new
		// PointAdapter().nullSafe());
		Gson gson = builder.create();

		List<PlayerProjected> p_array = gson.fromJson(container.getProjectionsJSONString(),
				new TypeToken<List<PlayerProjected>>() {
				}.getType());

		// System.out.println("Player 0: " + p_array.get(0).getFull_name());

		int count = 0;
		
		System.out.println("Profile service: " + container.getProj_service());
		System.out.println("Profile period: " + container.getProj_period());
		System.out.println("Profile year: " + container.getProj_year());
		System.out.println("Profile date: " + container.getProj_date());

		// Get the profile from data store
		ProjectionProfile profile = getProjectionProfileService().get(container.getProj_service(),
				container.getProj_period(), container.getProj_year());
		
		// Set the profile date and save it
		profile.setProjection_date(container.getProj_date());
		getProjectionProfileService().save(profile, user.getUsername());

		try {
			count = getPlayerProjectedService().updatePlayerProjections(p_array, profile, user.getUsername());

			// System.out.println("After update service call, count = " +
			// count);

			if (count > 0)
				return new APIGeneralResult("OK", "Number of player projections updated: " + count);
			else
				return new APIGeneralResult("KO", "No Player projections were created.");

		} catch (Exception e) {
			throw new InternalServerErrorException("UA08 - Internal error. " + e);
		}

	}

	@ApiMethod(name = "map.getattributemap", httpMethod = "post")
	public ProjectionAttributeMap getAttributeMap(HttpServletRequest req) throws UnauthorizedException {

		String attributes = "";

		APIToken token = new APIToken(req.getHeader("Authorization").split(" ")[1]);

		if (!getIdentityService().validateAdminJWT(token))
			throw new UnauthorizedException("Token is invalid");

		try {
			attributes = getPlayerProjectedService().getPlayerProjectionAttributes();
			if (attributes.length() > 0)
				return new ProjectionAttributeMap(attributes, "Attributes available");
			else
				return new ProjectionAttributeMap("none", "No Player projection attributes returned.");

		} catch (Exception e) {
			e.printStackTrace();
			return new ProjectionAttributeMap("none", "No Player projection attributes returned.");
		}

	}

	@ApiMethod(name = "map.getprojectionservices")
	public List<ProjectionService> getProjectionServices(HttpServletRequest req) throws UnauthorizedException {

		APIToken token = new APIToken(req.getHeader("Authorization").split(" ")[1]);

		if (!getIdentityService().validateAdminJWT(token))
			throw new UnauthorizedException("Token is invalid");

		List<ProjectionService> services = new ArrayList<ProjectionService>();

		services = getProjectionProfileService().getProjectionServices();
		return services;

	}

	@ApiMethod(name = "map.getprojectionperiods")
	public List<ProjectionPeriod> getProjectionPeriods(HttpServletRequest req) throws UnauthorizedException {

		APIToken token = new APIToken(req.getHeader("Authorization").split(" ")[1]);

		if (!getIdentityService().validateAdminJWT(token))
			throw new UnauthorizedException("Token is invalid");

		List<ProjectionPeriod> periods = new ArrayList<ProjectionPeriod>();

		periods = getProjectionProfileService().getProjectionPeriods();
		return periods;

	}

}
