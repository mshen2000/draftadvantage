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

$(document).ready(function()
{
	$('.input-group.date').datepicker({
	    autoclose: true,
	    todayHighlight: true
	});
	
  	$('#rootwizard').bootstrapWizard({onTabShow: function(tab, navigation, index) {
		var $total = navigation.find('li').length;
		var $current = index+1;
		var $percent = ($current/$total) * 100;
		$('#rootwizard').find('.progress-bar').css({width:$percent + '%'});
		
		// If it's the preview tab then load projection selections
		if($current == 4) {
			$("#proj-date-label").text($( "#projection-date-selector2" ).val());
			var hitterfile = $( "#hitter-proj-file" ).val().toString().split("\\");
			$("#proj-hitterfile-label").text(hitterfile[hitterfile.length - 1]);
		} 
		
		// If it's the last tab then hide the last button and show the finish instead
		if($current >= $total) {
			$('#rootwizard').find('.pager .next').hide();
			$('#rootwizard').find('.pager .finish').show();
			$('#rootwizard').find('.pager .finish').removeClass('disabled');
		} else {
			$('#rootwizard').find('.pager .next').show();
			$('#rootwizard').find('.pager .finish').hide();
		}
		
	}});

	$('#rootwizard .finish').click(function()
	{
		$('#loadprojections-modal').modal('hide');
		progressmodal.showPleaseWait("Updating Projections...");
		parseprojections();

	});
	
	$('#btn-save-profile').click(function() 
	{
		$(this).find($(".fa")).removeClass('fa-floppy-o').addClass('fa-refresh fa-spin');
		
		var service = $("#projection-service-selector").find("option:selected").text();
		var period = $("#projection-period-selector").find("option:selected").text();
		var year = $("#projection-year-selector").find("option:selected").text();
		
    	mssolutions.fbapp.loadprojections.saveProfile(service, period, year);
    	
	});
	
	// Creates initial load of projection and profile table
	loadProjectionTable(null, true);
    loadProfileTable(null, true);
    
    // Define select events for profile table
    var profile_table = $('#profile_table').DataTable();
	
    profile_table
    .on( 'select', function ( e, dt, type, indexes ) {
    	var profile_table_b = $('#profile_table').DataTable();
        var rows = profile_table_b.rows( indexes ).data();
        console.log("Table Select :", rows[0]);
        
        var selectedRows = profile_table.rows( { selected: true } ).count();
        
        profile_table_b.button( 1 ).enable();
        profile_table_b.button( 2 ).enable();
        profile_table_b.button( 3 ).enable();

        $("#proj-profile-label").text(rows[0].projection_service + " - " + rows[0].projection_period + " - " + rows[0].projected_year);
        $("#proj-profile-label2").text(rows[0].projection_service + " - " + rows[0].projection_period + " - " + rows[0].projected_year);
    } )
    .on( 'deselect', function ( e, dt, type, indexes ) {
    	var profile_table_b = $('#profile_table').DataTable();
    	var selectedRows = profile_table.rows( { selected: true } ).count();
    	
    	profile_table_b.button( 1 ).disable();
    	profile_table_b.button( 2 ).disable();
    	profile_table_b.button( 3 ).disable();

    } );

});

function parseprojections()
{
	stepped = 0;
	chunks = 0;
	rows = 0;

	var files = $('#hitter-proj-file')[0].files;
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
		
		$('#hitter-proj-file').parse({
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
}

function uploadprojections(parse_results)
{
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
	
	var csvresults = parse_results;
	var uploadplayerprojections = [];
	
	// console.log("parse_results: ", JSON.stringify(csvresults));
	
	// Get the selected projection profile service name
	var profile_table_b = $('#profile_table').DataTable();
    var rows = profile_table_b.rows( { selected: true } ).data();

	var service = rows[0].projection_service;
	var period = rows[0].projection_period;
	var year = rows[0].projected_year;
	
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
					uploadplayerattribute[map_key] = csvplayer_value;
					uploadplayer[map_key] = csvplayer_value;
				}

			});
			
			// Set the other_id_name for the player to the projection profile service name.
			// The other_id and other_id_name will be used to uniquely identify a player 
			// during the projection update.
			uploadplayer["other_id_name"] = service;

		});
		uploadplayerprojections.push(uploadplayer);

	});
	
	// console.log("Updated PlayerList: ", JSON.stringify(uploadplayerprojections));

	var date = $("#projection-date-selector1").datepicker('getDate');
	
	console.log("Profile service: ", service);
	console.log("Profile period: ", period);
	console.log("Profile year: ", year);
	console.log("Profile date: ", date);
	
	mssolutions.fbapp.loadprojections.updateprojections(uploadplayerprojections, service, period, date, year);

}


function buildConfig()
{
	return {
		complete: completeFn,
		error: errorFn,
		header: true,
		skipEmptyLines: true
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
	// console.log("Results:", results);
	// console.log("Results Data:", results.data);

	uploadprojections(results.data);
}

function loadProjectionTable(data, isInitialLoad)
{
	var data_table;
	var table_element = $('#example1');
	var config = {
			responsive: true,
        	"processing": true,
            data: data,
            "columns": [
                { "title": "Name", "mData": "full_name" },
                { "title": "Team", "mData": "team"},
                { "title": "PA", "mData": "hitter_pa"},
                { "title": "BA", "mData": "hitter_avg"},
                { "title": "HR", "mData": "hitter_hr"}
            ]
        };
	
	if (isInitialLoad) 	{
		data_table = table_element.dataTable(config);
	} else {
		data_table = table_element.DataTable();
		data_table.destroy();
		table_element.empty();
		data_table = table_element.dataTable(config);
	}
	
	if (! data){
		$('#proj-profile-header-label').text("No Profile");
	} else {
    	var profile_table_b = $('#profile_table').DataTable();
        var rows = profile_table_b.rows( { selected: true } ).data();
        $("#proj-profile-header-label").text(rows[0].projection_service + " - " + rows[0].projection_period + " - " + rows[0].projected_year);
	}

}

function loadProfileTable(data, isInitialLoad)
{
	var data_table;
	var table_element = $('#profile_table');
	var config = {
        "data": data,
        select: 'single',
        responsive: true,
        "searching": false,
        "paging": false,
        dom: 'Bfrtip',
        buttons: [
          {
              text: '<i class="fa fa-plus"></i> Add',
              className: 'btn-success',
              action: function ( e, dt, node, config ) {
          		mssolutions.fbapp.loadprojections.load_projection_services();
        		mssolutions.fbapp.loadprojections.load_projection_periods();
            	var options = $("#projection-year-selector");
            	options.find('option').remove().end();
            	options.append($("<option />").text((new Date).getFullYear()));
            	options.append($("<option />").text((new Date).getFullYear() + 1));
            	$("#addprofile-modal").modal("show");
              }
          },
          {
              text: '<i class="fa fa-trash"></i> Delete',
              className: 'btn-danger',
              enabled: false,
              action: function ( e, dt, node, config ) {
                  BootstrapDialog.show({
                  	type: 'type-default',
                      title: 'Confirm Delete Projection Profile',
                      message: 'Are you sure you want to delete this Profile?  Deleting a profile will delete all associated player projections.',
                      spinicon: 'fa fa-refresh',
                      buttons: [{
                          id: 'btn-confirm-delete-profile',   
                          icon: 'fa fa-trash',       
                          cssClass: 'btn-danger', 
                          autospin: true,
                          label: 'Delete',
                          action: function(dialog) {
                          	var profile_table = $('#profile_table').DataTable();
                          	var d = profile_table.rows('.selected').data();
                              console.log("Delete row 0: ", d[0]);
                              console.log("Delete ID 0: ", d[0].id);
                          	mssolutions.fbapp.loadprojections.deleteProfile(d[0].id);
                          }
                      }, {
                          label: 'Cancel',
                          action: function(dialog) {
                          	dialog.close();
                          }
                      }]
                  });
              }
          },
          {
              text: '<i class="fa fa-list"></i> Load',
              className: 'btn-primary',
              enabled: false,
              action: function ( e, dt, node, config ) {
                	var profile_table = $('#profile_table').DataTable();
                  	var d = profile_table.rows('.selected').data();
                      console.log("Delete row 0: ", d[0]);
                      console.log("Delete ID 0: ", d[0].id);
            	  mssolutions.fbapp.loadprojections.loadProjections(d[0].id);
              }
          },
          {
              text: '<i class="fa fa-cloud-upload"></i> Update',
              className: 'btn-primary',
              enabled: false,
              action: function ( e, dt, node, config ) {
            	  $("#loadprojections-modal").modal("show");
              }
          }
          ],
        "columns": [
            { "title": "Service", "mData": "projection_service" },
            { "title": "Period", "mData": "projection_period"},
            { "title": "Year", "mData": "projected_year"},
            { "title": "Date of Update", "mData": "projection_date", "type": "date", "sDefaultContent": "<i>None</i>",
                "render": function (data) {
                	// console.log(data);
                	if (! data){return data};
                    var date = new Date(data);
                    var month = date.getMonth() + 1;
                    return (month.length > 1 ? month : "0" + month) + "/" + date.getDate() + "/" + date.getFullYear();
                }},
            { "title": "Pitchers", "mData": "pitchers", "sDefaultContent": "0"},
            { "title": "Hitters", "mData": "hitters", "sDefaultContent": "0"},
        ]
    };
	
	if (isInitialLoad) 	{
		data_table = table_element.dataTable(config);
	} else {
		data_table = table_element.DataTable();
		data_table.destroy();
		table_element.empty();
		data_table = table_element.dataTable(config);
	}

}


/**
 * Save a new projection profile via the API.
 */
mssolutions.fbapp.loadprojections.saveProfile = function(service, period, year) {
	gapi.client.draftapp.projectionprofile.save({
		'projection_service' : service,
		'projection_period' : period,
		'projected_year' : year}).execute(
      function(resp) {
        if (!resp.code) { 
        	mssolutions.fbapp.loadprojections.loadProfiles();
        	$('#addprofile-modal').modal('hide');
        	$('#btn-save-profile').find($(".fa")).removeClass('fa-refresh fa-spin').addClass('fa-floppy-o');

        }
        else {
        	console.log("Failed to load profiles: ", resp.code + " : " + resp.message);
        }
      });
};

/**
 * Delete a new projection profile via the API.
 */
mssolutions.fbapp.loadprojections.deleteProfile = function(id) {
	gapi.client.draftapp.projectionprofile.remove({
		'msg' : id}).execute(
      function(resp) {
        if (!resp.code) { 
        	mssolutions.fbapp.loadprojections.loadProfiles();
        	loadProjectionTable(null, false);
        	BootstrapDialog.closeAll()
        }
        else {
        	console.log("Failed to delete profile: ", resp.code + " : " + resp.message);
        }
      });
};


/**
 * load projection profiles via the API.
 */
mssolutions.fbapp.loadprojections.loadProfiles = function(id) {
	// loadspinner.showLoader('#projections-table-div');
	gapi.client.draftapp.projectionprofile.getall().execute(
      function(resp) {
        if (!resp.code) { 
        	
        	loadProfileTable(resp.items, false);
        	// loadspinner.hideLoader('#projections-table-div');

        }
        else {
        	console.log("Failed to load profiles: ", resp.code + " : " + resp.message);
        }
      });
};

/**
 * load player projections via the API.
 */
mssolutions.fbapp.loadprojections.loadProjections = function(id) {
	var profile_table_b = $('#profile_table').DataTable();
    profile_table_b.button( 2 ).disable();
	loadspinner.showLoader('#projections-table-div');
	gapi.client.draftapp.playerprojections.get({
		'msg' : id}).execute(
      function(resp) {
        if (!resp.code) { 
        	
        	loadProjectionTable(resp.items, false);
        	
    		loadspinner.hideLoader('#projections-table-div');
    		profile_table_b.button( 2 ).enable();
        }
        else {
        	console.log("Failed to load projections: ", resp.code + " : " + resp.message);
        }
      });
};

/**
 * load player attribute map via the API.
 */
mssolutions.fbapp.loadprojections.updateprojections = function(container, proj_service, 
		proj_period, proj_date, proj_year) {
	gapi.client.draftapp.main.updateplayerprojections({
		'ProjectionsJSONString' : JSON.stringify(container),
		'proj_service' : proj_service,
		'proj_period' : proj_period,
		'proj_date' : proj_date,
		'proj_year' : proj_year}).execute(
      function(resp) {
        if (!resp.code) { 
        	console.log("Load Success: ", resp.description);
        	progressmodal.hidePleaseWait();
        	mssolutions.fbapp.loadprojections.loadProfiles();
        	loadProjectionTable(null, false);
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
        	// console.log("Attribute Map: ", resp.attributes, " : " , resp.note);
        }
        else {
        	console.log("Failed to load Attribute Map: ", resp.code + " : " + resp.message);
        }
      });
};


/**
 * load projection services via the API.
 */
mssolutions.fbapp.loadprojections.load_projection_services = function() {
	gapi.client.draftapp.map.getprojectionservices().execute(
      function(resp) {
        if (!resp.code) { 
        	var options = $("#projection-service-selector");
        	options.find('option').remove().end();
        	$.each(resp.items, function() {
        	    options.append($("<option />").text(this.projection_service_name));
        	});
        }
        else {
        	console.log("Failed to load Projection Services: ", resp.code + " : " + resp.message);
        }
      });
};

/**
 * load projection periods via the API.
 */
mssolutions.fbapp.loadprojections.load_projection_periods = function() {
	gapi.client.draftapp.map.getprojectionperiods().execute(
      function(resp) {
        if (!resp.code) { 
        	var options = $("#projection-period-selector");
        	options.find('option').remove().end();
        	$.each(resp.items, function() {
        	    options.append($("<option />").text(this.projection_period_name));
        	});
        }
        else {
        	console.log("Failed to load Projection Periods: ", resp.code + " : " + resp.message);
        }
      });
};

/**
 * delete all projections via the API.
 */
mssolutions.fbapp.loadprojections.delete_all_projections = function() {
	gapi.client.draftapp.main.deleteallprojections().execute(
      function(resp) {
        if (!resp.code) { 
        	progressmodal.hidePleaseWait();
        	// mssolutions.fbapp.loadprojections.reloadProjections();
        }
        else {
        	console.log("Failed to delete Projections: ", resp.code + " : " + resp.message);
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
				mssolutions.fbapp.loadprojections.loadProfiles();
			}
		}

		apisToLoad = 1; // must match number of calls to gapi.client.load()
		gapi.client.load('draftapp', 'v1', callback, apiRoot);
		// gapi.client.load('oauth2', 'v2', callback);
	}
	else {
		mssolutions.fbapp.loadprojections.loadProfiles();
	}
};