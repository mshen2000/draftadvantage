/**
 * @fileoverview
 * Provides methods for the Hello Endpoints sample UI and interaction with the
 * Hello Endpoints API.
 */

/** google global namespace for Google projects. */
var mssolutions = mssolutions || {};

/** appengine namespace for Google Developer Relations projects. */
mssolutions.fbapp = mssolutions.fbapp || {};

/** hello namespace for this sample. */
mssolutions.fbapp.login = mssolutions.fbapp.login || {};
/**
 * Client ID of the application (from the APIs Console).
 * @type {string}
 */
mssolutions.fbapp.login.CLIENT_ID =
    '689526189606-inp22gjuvbvcel90gmetviseks27bkc7.apps.googleusercontent.com';

/**
 * Scopes used by the application.
 * @type {string}
 */
mssolutions.fbapp.login.SCOPES =
    'https://www.googleapis.com/auth/userinfo.email';
/**
 * Whether or not the user is signed in.
 * @type {boolean}
 */
mssolutions.fbapp.login.signedIn = false;


/**
 * Authentication for initial login page
 * Returns a valid client token
 * @param {string} email userid/email 
 * @param {string} password 
 */
mssolutions.fbapp.login.logon = function(email, password) {
  gapi.client.authenticate.auth.customlogon({
      'email': email,
      'password': password
    }).execute(function(resp) {
      if (resp.status == "OK") {
    	  localStorage.setItem("email", email);
    	  localStorage.setItem("firstname", resp.description2);
    	  localStorage.setItem("ClientToken", resp.description);
    	  window.location.href = "pages/admin/draftmanager.html";
      }
      else {
    	  $('#loginalert').show();
      }
    });
};

/**
 * Authenticate for admin access
 * @param {string} token
 */
mssolutions.fbapp.login.authenticate_admin = function() {

	gapi.client.authenticate.auth.authenticateadmin().execute(function(resp) {
		if (resp.status != "OK") {
			window.location.href = "../../index.html";
		} else {
			// console.log("Auth Admin: ", resp.description);
			$("body").removeAttr("hidden");
			$('#login-name').html("<i class='fa fa-user fa-lg'></i> Signed in as " + localStorage.getItem("firstname"));
			//$('#login-name').text("UPDATE");
		}
	});

};

/**
 * Authenticate for user access
 * @param {string} token
 */
mssolutions.fbapp.login.authenticate_user = function() {

	gapi.client.authenticate.auth.authenticateuser().execute(function(resp) {
		if (resp.status != "OK") {
			window.location.href = "../../index.html";
		} else {
			// console.log("Auth User: ", resp.description);
			$("body").removeAttr("hidden");
			$('#login-name').html("<i class='fa fa-user fa-lg'></i> Signed in as " + localStorage.getItem("firstname"));
			//$('#login-name').text("UPDATE");
		}
	});

};



/**
 * Enables the button callbacks in the UI.
 */
mssolutions.fbapp.login.enableButtons = function() {

	$('#submit_button').prop('disabled', false);
	$('#submit-icon').hide();
	$('#submit_button').text('Sign In');
	
};


/**
 * Initializes the application.
 * @param {string} apiRoot Root of the API's path.
 */
mssolutions.fbapp.login.init_login = function(apiRoot) {
	
	//  check if endpoints have been intialized
	if (typeof gapi.client.authenticate !== 'object') {
		
		console.log("Loading gapi.client.authenticate (from init_logon)");
		
		// Loads the OAuth and authenticate APIs asynchronously, and
		// triggers login when they have completed.
		var apisToLoad;
		var callback = function() {
			if (--apisToLoad == 0) {
				console.log("Loading gapi.client.authenticate - COMPLETE.");
				mssolutions.fbapp.login.enableButtons();
			}
		}

		apisToLoad = 1; // must match number of calls to gapi.client.load()
		gapi.client.load('authenticate', 'v1', callback, apiRoot);
		// gapi.client.load('oauth2', 'v2', callback);
	}
	else {
		mssolutions.fbapp.login.enableButtons();
		console.log("Loading gapi.client.authenticate - COMPLETE.");
	}
};


/**
 * Initializes the authentication for admin
 * @param {string} apiRoot Root of the API's path.
 */
mssolutions.fbapp.login.auth_admin = function(apiRoot) {
	
	//  check if endpoints have been intialized
	if (typeof gapi.client.authenticate !== 'object') {
	
		console.log("Loading gapi.client.authenticate (from auth_admin)");
		
		// Set the token for this app initialization
		gapi.auth.setToken({
		    access_token: localStorage.getItem("ClientToken")
		});;
		
		// Loads the authenticate API asynchronously, and triggers
		// token authentication when completed.
		var apisToLoad;
		var callback = function() {
			if (--apisToLoad == 0) {
				console.log("Loading gapi.client.authenticate - COMPLETE.");
				mssolutions.fbapp.login.authenticate_admin();
			}
		}
	
		apisToLoad = 1; // must match number of calls to gapi.client.load()
		gapi.client.load('authenticate', 'v1', callback, apiRoot);
		
	}
	else {
		// If app is already initialized, run the callback
		console.log("Loading gapi.client.authenticate - COMPLETE.");
		mssolutions.fbapp.login.authenticate_admin();
	}
};


/**
 * Initializes the authentication for user
 * @param {string} apiRoot Root of the API's path.
 */
mssolutions.fbapp.login.auth_user = function(apiRoot) {
	
	//  check if endpoints have been intialized
	if (typeof gapi.client.authenticate !== 'object') {
	
		console.log("Loading gapi.client.authenticate (from auth_user)");
		
		// Set the token for this app initialization
		gapi.auth.setToken({
		    access_token: localStorage.getItem("ClientToken")
		});;
		
		// Loads the authenticate API asynchronously, and triggers
		// token authentication when completed.
		var apisToLoad;
		var callback = function() {
			if (--apisToLoad == 0) {
				console.log("Loading gapi.client.authenticate - COMPLETE.");
				mssolutions.fbapp.login.authenticate_user();
			}
		}
	
		apisToLoad = 1; // must match number of calls to gapi.client.load()
		gapi.client.load('authenticate', 'v1', callback, apiRoot);
		
	}
	else {
		// If app is already initialized, run the callback
		console.log("Loading gapi.client.authenticate - COMPLETE.");
		mssolutions.fbapp.login.authenticate_user();
	}
};





