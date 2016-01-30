package com.app.endpoints;

import com.app.endpoints.entities.ProjectionContainer;
import com.app.endpoints.entities.ProjectionAttributeMap;
import com.app.endpoints.entities.ProjectionPeriod;
import com.app.endpoints.entities.ProjectionService;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.Nullable;
import com.google.api.server.spi.response.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nya.sms.dataservices.IdentityService;
import com.nya.sms.dataservices.PlayerProjectedService;
import com.nya.sms.dataservices.ProjectionProfileService;
import com.nya.sms.entities.PlayerProjected;
import com.nya.sms.entities.ProjectionProfile;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

/**
 * Defines v1 of a DraftApp API, which provides the application methods.
 */
@Api(name = "draftapp", version = "v1", scopes = { Constants.EMAIL_SCOPE }, clientIds = { Constants.WEB_CLIENT_ID,
		Constants.ANDROID_CLIENT_ID, Constants.IOS_CLIENT_ID, Constants.API_EXPLORER_CLIENT_ID }, audiences = { Constants.ANDROID_AUDIENCE })
public class MainEndpoint {

	private IdentityService getIdentityService() {

		return new IdentityService();

	}

	private PlayerProjectedService getPlayerProjectedService() {

		return new PlayerProjectedService();

	}

	private ProjectionProfileService getProjectionProfileService() {

		return new ProjectionProfileService(ProjectionProfile.class);

	}

	private com.nya.sms.entities.User validateToken(HttpServletRequest req) throws UnauthorizedException {
		APIToken token = new APIToken(req.getHeader("Authorization").split(" ")[1]);

		if (!getIdentityService().validateAdminJWT(token))
			throw new UnauthorizedException("Token is invalid");

		return getIdentityService().getUserfromToken(token);
	}

	@ApiMethod(name = "main.getprojections")
	public List<PlayerProjected> GetProjections(HttpServletRequest req) throws UnauthorizedException {

		APIToken token = new APIToken(req.getHeader("Authorization").split(" ")[1]);

		if (!getIdentityService().validateAdminJWT(token))
			throw new UnauthorizedException("Token is invalid");

		return getPlayerProjectedService().getAllPlayerProjected();

	}

	@ApiMethod(name = "projectionprofile.getall")
	public List<ProjectionProfile> getAllProjectionProfiles(HttpServletRequest req) throws UnauthorizedException {

		validateToken(req);

		return getProjectionProfileService().getAll();

	}

	@ApiMethod(name = "projectionprofile.save")
	public APIGeneralResult saveProjectionProfile(ProjectionProfile profile, HttpServletRequest req)
			throws UnauthorizedException {

		System.out.println("Endpoint Profile Service: " + profile.getProjection_service());
		System.out.println("Endpoint Profile Period: " + profile.getProjection_period());
		System.out.println("Endpoint Profile Year: " + profile.getProjected_year());

		getProjectionProfileService().save(profile, validateToken(req).getUsername());

		return new APIGeneralResult("OK", "Save profile successful.");

	}

	@ApiMethod(name = "projectionprofile.remove")
	public APIGeneralResult deleteProjectionProfile(@Named("profile_id") long id, HttpServletRequest req)
			throws UnauthorizedException {
		System.out.println("Profile ID: " + id);
		validateToken(req);

		getProjectionProfileService().delete(id);

		return new APIGeneralResult("OK", "Save profile successful.");

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

		com.nya.sms.entities.User user = validateToken(req);

		// System.out.println("Container String: " +
		// container.getProjectionsJSONString());

		Gson gson = new Gson();

		List<PlayerProjected> p_array = gson.fromJson(container.getProjectionsJSONString(),
				new TypeToken<List<PlayerProjected>>() {
				}.getType());

		// System.out.println("Player 0: " + p_array.get(0).getFull_name());

		int count = 0;

		// ProjectionProfile profile = new ProjectionProfile();
		// profile.setProjected_year(container.getProj_year());
		// profile.setProjection_period(container.getProj_period());
		// profile.setProjection_service(container.getProj_service());

		// Get the profile from data store
		ProjectionProfile profile = getProjectionProfileService().get(container.getProj_service(),
				container.getProj_period(), container.getProj_year());

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
