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

var stepped = 0, chunks = 0, rows = 0;
var start, end;
var parser;

$(function()
{
	$('#submit-parse').click(function()
	{
		stepped = 0;
		chunks = 0;
		rows = 0;

		var files = $('#input-1a')[0].files;
		var config = buildConfig();

		if (files.length > 0)
		{
			for (var i = 0; i < files.length; i++)
			{
				if (files[i].size > 1024 * 1024 * 10)
				{
					alert("A file you've selected is larger than 10 MB; please choose to stream or chunk the input to prevent the browser from crashing.");
					return;
				}
			}

			start = performance.now();
			
			$('#input-1a').parse({
				config: config,
				before: function(file, inputElem)
				{
					console.log("Parsing file:", file);
				},
				complete: function(results)
				{
					console.log("Done with all files.");
				}
			});
		}
		else
		{
			alert("You have not selected a file to load. Please select a file.");
			return;
		}
	});

});

function buildConfig()
{
	return {
		complete: completeFn,
		error: errorFn,
		header: true,
	};

}

function errorFn(error, file)
{
	console.log("ERROR:", error, file);
}

function completeFn(results)
{
	end = performance.now();
	if (!$('#stream').prop('checked')
			&& !$('#chunk').prop('checked')
			&& arguments[0]
			&& arguments[0].data)
		rows = arguments[0].data.length;
	
	console.log("Finished input (async). Time:", end-start, arguments);
	console.log("Rows:", rows, "Stepped:", stepped, "Chunks:", chunks);
	console.log("Results:", results);
	console.log("Results Data:", results.data);
	$("#output").val(JSON.stringify(results.data));
	
	// Get player attribute map from server
	mssolutions.fbapp.loadprojections.loadattributemap();
	
	// Temp property map
	var hitterpropertymap = {
			other_id: "playerid",
			full_name: "Name",
			team: "Team",
			hitter_pa: "PA",
			hitter_ab: "AB",
			hitter_hits: "H",
			hitter_doubles: "2B",
			hitter_triples: "3B",
			hitter_hr: "HR",
			hitter_rbi: "RBI",
			hitter_runs: "R",
			hitter_so: "SO",
			hitter_bb: "BB",
			hitter_hbp: "HBP",
			hitter_sb: "SB",
			hitter_cs: "CS",
			hitter_avg: "AVG",
			hitter_obp: "OBP",
			hitter_slg: "SLG",
			hitter_ops: "OPS"
	}
	
	var csvresults = results.data;
	var uploadplayerprojections = [];
	
	// For each uploaded csv line in the csv file...
	$.each( csvresults, function( csvresults_key, csvresults_value ) {
		var csvplayer = csvresults_value;
		var uploadplayer = {};
		
		// For each attribute in the csv player...
		$.each(csvplayer,function(csvplayer_key,csvplayer_value){
			var uploadplayerattribute = {};
			
			// For each attribute in the property map, check if the attribute is
			//    in the csvplayer attributes, if so, then add it to a new
			//    player object.
			$.each(hitterpropertymap,function(map_key,map_value){
				if (map_value == csvplayer_key){
					// console.log("map_key: ", map_key);
					// console.log("csvplayer_value: ", csvplayer_value);
					// uploadplayerattribute.id = map_key;
					// uploadplayerattribute.value = csvplayer_value;
					uploadplayerattribute[map_key] = csvplayer_value;
					// uploadplayer.push(uploadplayerattribute);
					uploadplayer[map_key] = csvplayer_value;
				}

			});
			
			// console.log("Updated Player Attribute: ", JSON.stringify(uploadplayerattribute));

		});
		uploadplayerprojections.push(uploadplayer);

	});
	
	console.log("Updated PlayerList: ", JSON.stringify(uploadplayerprojections));
	
	var date = new Date("January 12, 2016 12:01:00");
	
	mssolutions.fbapp.loadprojections.updateprojections(uploadplayerprojections);
}



/**
 * load player projections via the API.
 */
mssolutions.fbapp.loadprojections.loadProjections = function(id) {
	gapi.client.draftapp.main.getprojections().execute(
      function(resp) {
        if (!resp.code) { 
        	
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
        else {
        	console.log("Failed to load projections: ", resp.code + " : " + resp.message);
        }
      });
};

/**
 * load player attribute map via the API.
 */
mssolutions.fbapp.loadprojections.updateprojections = function(container) {
	gapi.client.draftapp.main.updateplayerprojections({'ObjectJSONString' : JSON.stringify(container)}).execute(
      function(resp) {
        if (!resp.code) { 
        	
        	console.log("Load Success: ", resp.description);

        }
        else {
        	console.log("Failed to update projections: ", resp.code + " : " + resp.message);
        }
      });
};


/**
 * load player attribute map via the API.
 */
mssolutions.fbapp.loadprojections.loadattributemap = function() {
	console.log("Pre-Attribute Map Token: ", gapi.auth.getToken());

	gapi.client.draftapp.map.getattributemap().execute(
      function(resp) {
        if (!resp.code) { 
        	console.log("Attribute Map: ", resp.attributes, " : " , resp.note);
        }
        else {
        	console.log("Failed to load Attribute Map: ", resp.code + " : " + resp.message);
        }
      });
};

/**
 * Initializes the application.
 * @param {string} apiRoot Root of the API's path.
 */
mssolutions.fbapp.loadprojections.init_nav = function(apiRoot) {
	
	//  check if endpoints have been intialized
	if (typeof gapi.client.draftapp !== 'object') {
		
		console.log("Loading gapi.client.draftapp");
		
		// Set the token for this app initialization
		gapi.auth.setToken({
		    access_token: localStorage.getItem("ClientToken")
		});;
		
		// Loads the OAuth and helloworld APIs asynchronously, and triggers
		// login when they have completed.
		var apisToLoad;
		var callback = function() {
			if (--apisToLoad == 0) {
				mssolutions.fbapp.loadprojections.loadProjections();
			}
		}

		apisToLoad = 1; // must match number of calls to gapi.client.load()
		gapi.client.load('draftapp', 'v1', callback, apiRoot);
		// gapi.client.load('oauth2', 'v2', callback);
	}
	else {
		mssolutions.fbapp.loadprojections.loadProjections();
	}
};