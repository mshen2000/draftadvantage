/**
 * @fileoverview
 * Provides methods for the top navbar UI and interaction with the APIs
 */

/** global namespace for MS Solutions projects. */
var mssolutions = mssolutions || {};

/** namespace for fantasy baseball applications. */
mssolutions.fbapp = mssolutions.fbapp || {};

/** projections namespace. */
mssolutions.fbapp.loadprojections = mssolutions.fbapp.loadprojections || {};
/**
 * Client ID of the application (from the APIs Console).
 * @type {string}
 */
mssolutions.fbapp.loadprojections.CLIENT_ID =
    '689526189606-inp22gjuvbvcel90gmetviseks27bkc7.apps.googleusercontent.com';

/**
 * Scopes used by the application.
 * @type {string}
 */
mssolutions.fbapp.loadprojections.SCOPES =
    'https://www.googleapis.com/auth/userinfo.email';


/**
 * Loads the application UI after the user has completed auth.
 */
mssolutions.fbapp.loadprojections.userAuthed = function() {
  var request = gapi.client.oauth2.userinfo.get().execute(function(resp) {
    if (!resp.code) {
      // mssolutions.fbapp.loadprojections.signedIn = true;
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
mssolutions.fbapp.loadprojections.loadProjections = function(id) {
	gapi.client.draftapp.main.getprojections().execute(
      function(resp) {
        if (!resp.code) { 

        	// console.log(resp.items);
        	
            $('#example1').dataTable( {
            	"bProcessing": true,
                "aaData": resp.items,
                "aoColumns": [
                    { "title": "First Name", "mData": "first_name" },
                    { "title": "Last Name", "mData": "last_name" },
                    { "title": "Age", "mData": "age" },
                    { "title": "Team", "mData": "team"}
                ]
            } ); 
        	
        	
        }
      });
};

/**
 * Handles the auth flow, with the given value for immediate mode.
 * @param {boolean} mode Whether or not to use immediate mode.
 * @param {Function} callback Callback to call on completion.
 */
mssolutions.fbapp.loadprojections.signin = function(mode, callback) {
  gapi.auth.authorize({client_id: mssolutions.fbapp.loadprojections.CLIENT_ID,
      scope: mssolutions.fbapp.loadprojections.SCOPES, immediate: mode},
      callback);
};


/**
 * Initializes the application.
 * @param {string} apiRoot Root of the API's path.
 */
mssolutions.fbapp.loadprojections.init_nav = function(apiRoot) {
  // Loads the OAuth and helloworld APIs asynchronously, and triggers login
  // when they have completed.
  var apisToLoad;
  var callback = function() {
    if (--apisToLoad == 0) {
      // mssolutions.fbapp.loadprojections.enableButtons();
    	mssolutions.fbapp.loadprojections.signin(true,
    			mssolutions.fbapp.loadprojections.userAuthed);
    	mssolutions.fbapp.loadprojections.loadProjections();
    }
  }

  apisToLoad = 2; // must match number of calls to gapi.client.load()
  gapi.client.load('draftapp', 'v1', callback, apiRoot);
  gapi.client.load('oauth2', 'v2', callback);
};