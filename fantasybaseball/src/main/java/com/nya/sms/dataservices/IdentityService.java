package com.nya.sms.dataservices;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.jose4j.jwk.*;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.jose4j.lang.JoseException;

import com.app.endpoints.APIToken;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.Ref;
import com.nya.sms.entities.JKey;
import com.nya.sms.entities.Role;
import com.nya.sms.entities.Site;
import com.nya.sms.entities.StudentGroup;
import com.nya.sms.entities.User;

public class IdentityService implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public static final String NO_ACCESS = "1-No Access";
	
	public static final String READ_ONLY = "2-Query";
	
	public static final String CREATE_UPDATE = "3-Create/Update";
	
	public static final String MY_STUDENT_GROUP = "1-My Student Group Only";
	
	public static final String ALL_STUDENTS = "2-All Students";

	
	public void createWebKey(){

		// Delete any existing JKeys
		List<Key<JKey>> keys = ofy().load().type(JKey.class).keys().list();
		ofy().delete().keys(keys).now();
		
		int i = 0;
		
		while ((ofy().load().type(JKey.class).keys().list().size()>0)&&(i < 10)){
			
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			i++;
			
		}
		
		// Create a new JKey
	    RsaJsonWebKey rsaJsonWebKey;
		try {
			rsaJsonWebKey = RsaJwkGenerator.generateJwk(2048);

		    // Give the JWK a Key ID (kid), which is just the polite thing to do
		    rsaJsonWebKey.setKeyId("k1");
		    
		    // Keep the following 2 lines, demo code for breaking down RsaJsonWebKey and re-forming
		    // Map<String,Object> n =  rsaJsonWebKey.toParams(JsonWebKey.OutputControlLevel.INCLUDE_PRIVATE);
		    // RsaJsonWebKey rsaJsonWebKey2 = new RsaJsonWebKey(n);
		
		    JKey jkey = new JKey(rsaJsonWebKey);
		    
			@SuppressWarnings("unused")
			Key<JKey> key = ObjectifyService.ofy().save().entity(jkey).now(); 
			
			int j = 0;
			
			while ((ofy().load().type(JKey.class).keys().list().size()<1)&&(j < 10)){
				
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				j++;
				
			}
		} catch (JoseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public RsaJsonWebKey getStoredWebKey(){
		
		return ofy().load().type(JKey.class).list().get(0).getWebkey();
		
	}
	
	
	public boolean checkPassword(String email, String password){
		
		if (isUserEmailPresent(email)){
			
			if (getUserByEmail(email).getPassword().equals(password)) return true;
			
		}
		
		return false;
		
	}
	
	public APIToken generateJWT(String email) {
		
		List<Role> roles = getUserRoles(getUserByEmail(email).getUsername());
		List<String> rolenames = new ArrayList<String>();
		
		for (Role r : roles){
		
			rolenames.add(r.getName());
			
		}
		
	    //
	    // JSON Web Token is a compact URL-safe means of representing claims/attributes to be transferred between two parties.
	    // This example demonstrates producing and consuming a signed JWT
	    //

	    // Generate an RSA key pair, which will be used for signing and verification of the JWT, wrapped in a JWK
	    RsaJsonWebKey rsaJsonWebKey;
		try {
			rsaJsonWebKey = getStoredWebKey();

		    // Create the Claims, which will be the content of the JWT
		    JwtClaims claims = new JwtClaims();
		    claims.setIssuer("DraftAdvantage Server");  // who creates the token and signs it
		    claims.setAudience("DraftAdvantage Client"); // to whom the token is intended to be sent
		    claims.setExpirationTimeMinutesInTheFuture(1440); // time when the token will expire (24 hours from now)
		    claims.setGeneratedJwtId(); // a unique identifier for the token
		    claims.setIssuedAtToNow();  // when the token was issued/created (now)
		    claims.setNotBeforeMinutesInThePast(2); // time before which the token is not yet valid (2 minutes ago)
		    claims.setSubject("user"); // the subject/principal is whom the token is about
		    claims.setClaim("email",email); // additional claims/attributes about the subject can be added
		    claims.setStringListClaim("roles", rolenames); // multi-valued claims work too and will end up as a JSON array
	
		    // A JWT is a JWS and/or a JWE with JSON claims as the payload.
		    // In this example it is a JWS so we create a JsonWebSignature object.
		    JsonWebSignature jws = new JsonWebSignature();

		    // The payload of the JWS is JSON content of the JWT Claims
		    jws.setPayload(claims.toJson());
	
		    // The JWT is signed using the private key
		    jws.setKey(rsaJsonWebKey.getPrivateKey());
	
		    // Set the Key ID (kid) header because it's just the polite thing to do.
		    // We only have one key in this example but a using a Key ID helps
		    // facilitate a smooth key rollover process
		    jws.setKeyIdHeaderValue(rsaJsonWebKey.getKeyId());
	
		    // Set the signature algorithm on the JWT/JWS that will integrity protect the claims
		    jws.setAlgorithmHeaderValue(AlgorithmIdentifiers.RSA_USING_SHA256);
	
		    // Sign the JWS and produce the compact serialization or the complete JWT/JWS
		    // representation, which is a string consisting of three dot ('.') separated
		    // base64url-encoded parts in the form Header.Payload.Signature
		    // If you wanted to encrypt it, you can simply set this jwt as the payload
		    // of a JsonWebEncryption object and set the cty (Content Type) header to "jwt".
		    String jwt = jws.getCompactSerialization();
		    
		    return new APIToken(jwt);
	    
		} catch (JoseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

	}
	
	public boolean validateUserJWT(APIToken jwt){
		System.out.println("In validateUserJWT");
		return validateJWT("user",jwt);
	}

	public boolean validateAdminJWT(APIToken jwt){
		System.out.println("In validateAdminJWT");
		return validateJWT("admin",jwt);
	}
	
	private boolean validateJWT(String rolename, APIToken jwt){

	    JwtConsumer jwtConsumer = getJWTConsumer();

	    try
	    {
	        //  Validate the JWT and process it to the Claims
	        JwtClaims jwtClaims = jwtConsumer.processToClaims(jwt.getToken());
	        @SuppressWarnings("unchecked")
			List<String> roles = (List<String>) jwtClaims.getClaimValue("roles");
	        for (String role : roles){
	        	if (role.equals(rolename)){
	        		System.out.println("JWT validation succeeded for " + rolename + " role.");
	        		return true;
	        	}
	        }
	        return false;
	    }
	    catch (InvalidJwtException e)
	    {
	        // InvalidJwtException will be thrown, if the JWT failed processing or validation in anyway.
	        // Hopefully with meaningful explanations(s) about what went wrong.
	        System.out.println("JWT it invalid.");
	        // e.printStackTrace();
	        return false;
	    }	
	}
	
	private JwtConsumer getJWTConsumer(){
		RsaJsonWebKey rsaJsonWebKey = getStoredWebKey();
		
	    // Use JwtConsumerBuilder to construct an appropriate JwtConsumer, which will
	    // be used to validate and process the JWT.
	    // The specific validation requirements for a JWT are context dependent, however,
	    // it typically advisable to require a expiration time, a trusted issuer, and
	    // and audience that identifies your system as the intended recipient.
	    // If the JWT is encrypted too, you need only provide a decryption key or
	    // decryption key resolver to the builder.
	    JwtConsumer jwtConsumer = new JwtConsumerBuilder()
	            .setRequireExpirationTime() // the JWT must have an expiration time
	            .setAllowedClockSkewInSeconds(30) // allow some leeway in validating time based claims to account for clock skew
	            .setRequireSubject() // the JWT must have a subject claim
	            .setExpectedIssuer("DraftAdvantage Server") // whom the JWT needs to have been issued by
	            .setExpectedAudience("DraftAdvantage Client") // to whom the JWT is intended for
	            .setVerificationKey(rsaJsonWebKey.getKey()) // verify the signature with the public key
	            .build(); // create the JwtConsumer instance
	    
	    return jwtConsumer;
	}
	
	public User getUserfromToken(APIToken jwt){
		
		JwtConsumer jwtConsumer = getJWTConsumer();
		JwtClaims jwtClaims;
		User user = null;
		try {
			jwtClaims = jwtConsumer.processToClaims(jwt.getToken());
		} catch (InvalidJwtException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return user;
		}
		return getUserByEmail((String)jwtClaims.getClaimValue("email"));
	}

	
	public boolean isUserPresent(String username) {

		if (ObjectifyService.ofy().load().type(User.class).filter("username", username).count() > 0) return true;

		return false;
	}
	
	public boolean isUserEmailPresent(String email) {

		if (ObjectifyService.ofy().load().type(User.class).filter("email", email).count() > 0) return true;

		return false;
	}
	
	public boolean isUserExtIDPresent(String ext_id) {

		if (ObjectifyService.ofy().load().type(User.class).filter("ext_id", ext_id).count() > 0) return true;

		return false;
	}
	
	public User getUserByExtID(String ext_id){
		
		return ObjectifyService.ofy().load().type(User.class).filter("ext_id", ext_id).first().now();
		
	}
	
	public User getUser(String username){
		
		return ObjectifyService.ofy().load().type(User.class).filter("username", username).first().now();
		
	}
	
	public User getUserByEmail(String email){
		
		return ObjectifyService.ofy().load().type(User.class).filter("email", email).first().now();
		
	}
	
	public User getUser(long id){
		
		return ObjectifyService.ofy().load().type(User.class).id(id).now();
		
	}
	
	public Long saveUser(User user, String uname){
		
		if (!isUserPresent(user.getUsername())) {
			
			user.setCreatedby(uname);
			
		} 
		
		user.setModifiedby(uname);
		 
		Key<User> key = ObjectifyService.ofy().save().entity(user).now(); 
		
		int i = 0;
		
		while ((!isUserPresent(user.getUsername()))&&(i < 10)){
			
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			i++;
			
		}
		
		return key.getId();
		
	}
	
	public boolean isRolePresent(String rolename) {
		
		if (ObjectifyService.ofy().load().type(Role.class).filter("name", rolename).count() > 0) return true;

		return false;
	}
	
	public Role getRole(String rolename){
		
		return ObjectifyService.ofy().load().type(Role.class).filter("name", rolename).first().now();
		
	}
	
	public void deleteRole(String rolename){
		
		Role r = getRole(rolename);
		
		// remove site relationship
		if (r.getSite() != null){
			
			getSiteService().removeRoleFromSite(r.getSite(), rolename);
			
		}
		
		// delete the role
		ObjectifyService.ofy().delete().entity(r).now(); 
		
	}
	
	public Long saveRole(Role r, String uname){
		
		if (!isRolePresent(r.getName())) {
			
			r.setCreatedby(uname);
			
		} 
		
		r.setModifiedby(uname);
		
		Key<Role> key = ObjectifyService.ofy().save().entity(r).now(); 

		int i = 0;
		
		// Wait for data store to verify role exists
		while ((!isRolePresent(r.getName()))&&(i < 10)){
			
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			i++;
			
		}

		// Use site field and create relationship to site object
		getSiteService().associateRoletoSite(r.getSite(), r.getName());

		return key.getId();
		
	}
	
	public void deleteUser(String username){
		
		User usr = getUser(username);
		
		ObjectifyService.ofy().delete().entity(usr).now(); 
		
		while (isUserPresent(username)){
			
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
	}
	
	
	public List<User> getAllUsers(){
		
		ObjectifyService.ofy().clear();

		return ObjectifyService.ofy().load().type(User.class).list();

	}
	
	public List<User> getSGLeaderAvailableUsers(){

		List<User> allusers = getAllUsers();

		List<User> usersNoSGLeaders = removeSGLeadersfromUserlist(allusers);
		
		// System.out.println("Users that are not SG Leaders: " + usersNoSGLeaders.size());
		
		List <User> usersWithSGLeaderAuth = new ArrayList<User>();
		
		for (User u : usersNoSGLeaders){
			
			Authorization auth = new Authorization(u);
			
			if (auth.getSGLeaderAuth()) {
				usersWithSGLeaderAuth.add(u);
				// System.out.println("User available to be SG Leader: " + u.getUsername());
			}
			
		}
		
		return usersWithSGLeaderAuth;

	}
	
	private List<User> removeSGLeadersfromUserlist (List<User> userlist){
		
		List<StudentGroup> sgleaderslist = ObjectifyService.ofy().load().type(StudentGroup.class).filter("leader !=", null).list();
		List<User> leaders = new ArrayList<User>();
		
		// System.out.println("SGs with leaders: " + sgleaderslist.size());
		
		if (sgleaderslist.size() > 0){
			for (StudentGroup sg : sgleaderslist){
				
				User u = sg.getLeader().get();
				
				leaders.add(u);
	
			}
			
			for (User u : leaders){
				
				if (userlist.contains(u)) userlist.remove(u);
				
			}
		}
		
		return userlist;

	}
	
	public List<Role> getAllRoles(){
		
		return ObjectifyService.ofy().load().type(Role.class).list();

	}
	
	public List<Role> getUserRoles(String username){
		
		User usr = getUser(username);
		
		return ObjectifyService.ofy().load().type(Role.class).filter("users", usr).list();
		
	}
	
	public List<User> getRoleUsers(String rolename){
		
		List<User> users = new ArrayList<User>();
		
		Role r = getRole(rolename);
		
		List<Ref<User>> rusers = r.getUsers();
		
		for (Ref<User> ruser : rusers){
			
			ObjectifyService.ofy().load().ref(ruser);
			users.add(ruser.get());
			
		}
		
		return users;
		
	}
	
    public List<String> getAccessLevels(){
    	
    	List<String> levels = new ArrayList<String>();
    	// levels.add("1-No Access");
    	// levels.add("2-Query");
    	// levels.add("3-Create/Update");
    	
    	levels.add(NO_ACCESS);
    	levels.add(READ_ONLY);
    	levels.add(CREATE_UPDATE);
    	
    	return levels;
    	
    }
    
    public List<String> getStudentDataScopes(){
    	
    	List<String> levels = new ArrayList<String>();
    	levels.add(MY_STUDENT_GROUP);
    	levels.add(ALL_STUDENTS);
    	
    	return levels;
    	
    }
	
	public void createMembership(String username, String rolename){
		
		Role role = getRole(rolename);
		
		User usr = getUser(username);
		
		role.addUser(usr);
		
		ObjectifyService.ofy().save().entity(role).now(); 
		
	}
	
	public void deleteMembership(String username, String rolename){
		
		Role group = getRole(rolename);
		
		User usr = getUser(username);
		
		group.removeUser(usr);
		
		ObjectifyService.ofy().save().entity(group).now(); 
		
	}
	 
	 private SiteService getSiteService() {
		 
		 return new SiteService(Site.class);
	 
	 }
}
