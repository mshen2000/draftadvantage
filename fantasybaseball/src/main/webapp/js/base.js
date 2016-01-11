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
 * Custom logon not using Google auth
 * @param {string} email userid/email 
 * @param {string} password 
 */
mssolutions.fbapp.login.logon = function(email, password) {
  gapi.client.authenticate.auth.customlogon({
      'email': email,
      'password': password
    }).execute(function(resp) {
      if (resp.status == "OK") {
    	  localStorage.setItem("ClientToken", resp.description);
    	  // alert(localStorage.getItem("ClientToken"));
    	  window.location.href = "pages/admin/playerload.html";
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
mssolutions.fbapp.login.authenticate_admin = function(token) {
  gapi.client.authenticate.auth.authenticateadmin({'token': token}).execute(function(resp) {
      if (resp.status != "OK") {
    	  window.location.href = "../../index.html";
      } else {
    	  $("body").removeAttr("hidden");
      }
    });
};



/**
 * Enables the button callbacks in the UI.
 */
mssolutions.fbapp.login.enableButtons = function() {
	

};


/**
 * Initializes the application.
 * @param {string} apiRoot Root of the API's path.
 */
mssolutions.fbapp.login.init_login = function(apiRoot) {
  // Loads the OAuth and helloworld APIs asynchronously, and triggers login
  // when they have completed.
  var apisToLoad;
  var callback = function() {
    if (--apisToLoad == 0) {
    	mssolutions.fbapp.login.enableButtons();

    }
  }

  apisToLoad = 2; // must match number of calls to gapi.client.load()
  gapi.client.load('authenticate', 'v1', callback, apiRoot);
  gapi.client.load('oauth2', 'v2', callback);
};


/**
 * Initializes the application.
 * @param {string} apiRoot Root of the API's path.
 */
mssolutions.fbapp.login.auth_admin = function(apiRoot) {
  // Loads the OAuth and helloworld APIs asynchronously, and triggers login
  // when they have completed.
  var apisToLoad;
  var callback = function() {
    if (--apisToLoad == 0) {
 	    var token = localStorage.getItem("ClientToken");
 	    // var token = "aasdfasdfaf";
	    mssolutions.fbapp.login.authenticate_admin(token);

    }
  }

  apisToLoad = 1; // must match number of calls to gapi.client.load()
  gapi.client.load('authenticate', 'v1', callback, apiRoot);
};

