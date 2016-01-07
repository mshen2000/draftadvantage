/**
 * @fileoverview
 * Provides methods for the top navbar UI and interaction with the APIs
 */

/** google global namespace for Google projects. */
var mssolutions = mssolutions || {};

/** samples namespace for App Engine sample code. */
mssolutions.fbapp = mssolutions.fbapp || {};

/** hello namespace for this sample. */
mssolutions.fbapp.navbar = mssolutions.fbapp.navbar || {};
/**
 * Client ID of the application (from the APIs Console).
 * @type {string}
 */
mssolutions.fbapp.navbar.CLIENT_ID =
    '689526189606-inp22gjuvbvcel90gmetviseks27bkc7.apps.googleusercontent.com';

/**
 * Scopes used by the application.
 * @type {string}
 */
mssolutions.fbapp.navbar.SCOPES =
    'https://www.googleapis.com/auth/userinfo.email';



/**
 * Loads the application UI after the user has completed auth.
 */
mssolutions.fbapp.navbar.userAuthed = function() {
  var request = gapi.client.oauth2.userinfo.get().execute(function(resp) {
    if (!resp.code) {
      // mssolutions.fbapp.navbar.signedIn = true;
    	gapi.client.draftapp.main.authed().execute(
	      function(resp) {
	    	  $("#nav_username").text(resp.description);
	      });

    } else {
		window.location.href = "login.html";

    }
  });
};


/**
 * Lists greetings via the API.
 */
//mssolutions.fbapp.navbar.loadProjections = function() {
//	gapi.client.draftapp.main.getprojections().execute(
//      function(resp) {
//        if (!resp.code) { 
//        	$(function(){
//        	$("#example1").dataTable( {
//        	    data: resp,
//        	    columns: [
//        	        { data: 'first_name' },
//        	        { data: 'last_name' },
//        	        { data: 'age' },
//        	        { data: 'team' }
//        	    ]
//        	});
//        	});
//        }
//      });
//};

/**
 * Handles the auth flow, with the given value for immediate mode.
 * @param {boolean} mode Whether or not to use immediate mode.
 * @param {Function} callback Callback to call on completion.
 */
mssolutions.fbapp.navbar.signin = function(mode, callback) {
  gapi.auth.authorize({client_id: mssolutions.fbapp.navbar.CLIENT_ID,
      scope: mssolutions.fbapp.navbar.SCOPES, immediate: mode},
      callback);
};


/**
 * Initializes the application.
 * @param {string} apiRoot Root of the API's path.
 */
mssolutions.fbapp.navbar.init_nav = function(apiRoot) {
  // Loads the OAuth and helloworld APIs asynchronously, and triggers login
  // when they have completed.
  var apisToLoad;
  var callback = function() {
    if (--apisToLoad == 0) {
      // mssolutions.fbapp.navbar.enableButtons();
    	mssolutions.fbapp.navbar.signin(true,
    			mssolutions.fbapp.navbar.userAuthed);
    	// mssolutions.fbapp.navbar.loadProjections();
    }
  }

  apisToLoad = 2; // must match number of calls to gapi.client.load()
  gapi.client.load('draftapp', 'v1', callback, apiRoot);
  gapi.client.load('oauth2', 'v2', callback);
};