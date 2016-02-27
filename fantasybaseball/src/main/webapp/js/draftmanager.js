/**
 * @fileoverview
 * Provides methods for the top navbar UI and interaction with the APIs
 */

/** global namespace for MS Solutions projects. */
var mssolutions = mssolutions || {};

/** namespace for fantasy baseball applications. */
mssolutions.fbapp = mssolutions.fbapp || {};

/** projections namespace. */
mssolutions.fbapp.draftmanager = mssolutions.fbapp.draftmanager || {};
/**
 * Client ID of the application (from the APIs Console).
 * @type {string}
 */
mssolutions.fbapp.draftmanager.CLIENT_ID =
    '689526189606-inp22gjuvbvcel90gmetviseks27bkc7.apps.googleusercontent.com';

/**
 * Scopes used by the application.
 * @type {string}
 */
mssolutions.fbapp.draftmanager.SCOPES =
    'https://www.googleapis.com/auth/userinfo.email';


// League selector listener
$(function() {

	  $('#league-select').on('change', function(){
		  // console.log("In league-select on change");
	    var selected = $(this).find("option:selected").val();
		if(selected == "newleague") {

			
			$("#createleague-modal").modal("show");
		} else {
			// $('#btn-load-proj').prop("disabled",false);
		}
	  });
	  
});



$(document).ready(function()
{
	
	
  	$('#rootwizard').bootstrapWizard({onTabShow: function(tab, navigation, index) {
		var $total = navigation.find('li').length;
		var $current = index+1;
		var $percent = ($current/$total) * 100;
		$('#rootwizard').find('.progress-bar').css({width:$percent + '%'});
		
		// If it's the league team tab then pre-load teams
		if($current == 3){
			var teamtable = $('#team_table').DataTable();
	        var data = teamtable.rows().data();
	        var numteams = $('#select-numberofteams').find("option:selected").val();
	        var teams = [];

	        for (i = 1; i <= numteams; i++) { 
	        	var team = {};
	        	if (i == 1){
	        		team["team_num"] = i;
		        	team["team_name"] = "My Team";
		        	team["owner_name"] = "Me";
		        	team["isuserowner"] = true;
	        	} else {
	        		team["team_num"] = i;
		        	team["team_name"] = "Team " + i;
		        	team["owner_name"] = "Owner " + i;
		        	team["isuserowner"] = false;
	        	}
	        	
	        	teams.push(team);
	        }
	        
	        // console.log('Teams: ' + JSON.stringify(teams));
	        loadTeamTable(teams, false);
			
		}
		
		// If it's the preview tab then load projection selections
		if($current == 4) {
			
			// update league info
			$("#prevleaguename").text($("#input-leaguename").val()
				+ " ("+ $("#select-leagueyear").find("option:selected").val() + ")");
			// $("#prevleagueyear").text($("#select-leagueyear").find("option:selected").val());
			$("#prevleaguesite").text($("#select-leaguesite").find("option:selected").val());
			$("#prevnumteams").text($("#select-numberofteams").find("option:selected").val());
			$("#prevmlbleagues").text($("#select-mlbleagues").find("option:selected").val());
			$("#prevteamsalary").text($("#select-teamsalary").find("option:selected").val());
			
			// Stat categories
			var hitting = "";
			var pitching = "";
			
			if ($("#cat-hitter-hr").is(':checked')) hitting = hitting + "HR, ";
			if ($("#cat-hitter-r").is(':checked')) hitting = hitting + "R, ";
			if ($("#cat-hitter-rbi").is(':checked')) hitting = hitting + "RBI, ";
			if ($("#cat-hitter-sb").is(':checked')) hitting = hitting + "SB, ";
			if ($("#cat-hitter-avg").is(':checked')) hitting = hitting + "AVG, ";
			hitting = hitting.slice(0, -2);
			
			if ($("#cat-pitcher-w").is(':checked')) pitching = pitching + "W, ";
			if ($("#cat-pitcher-sv").is(':checked')) pitching = pitching + "SV, ";
			if ($("#cat-pitcher-k").is(':checked')) pitching = pitching + "K, ";
			if ($("#cat-pitcher-era").is(':checked')) pitching = pitching + "ERA, ";
			if ($("#cat-pitcher-whip").is(':checked')) pitching = pitching + "WHIP, ";
			pitching = pitching.slice(0, -2);
			
			$("#prevhittingcats").text(hitting);
			$("#prevpitchingcats").text(pitching);
			
			// Roster positions
			$("#prev1b").text($("#1b-num-select").find("option:selected").val());
			$("#prev2b").text($("#2b-num-select").find("option:selected").val());
			$("#prevss").text($("#ss-num-select").find("option:selected").val());
			$("#prev3b").text($("#3b-num-select").find("option:selected").val());
			$("#prevmi").text($("#mi-num-select").find("option:selected").val());
			$("#prevci").text($("#ci-num-select").find("option:selected").val());
			$("#prevc").text($("#c-num-select").find("option:selected").val());
			$("#prevof").text($("#of-num-select").find("option:selected").val());
			$("#prevp").text($("#p-num-select").find("option:selected").val());
			$("#prevut").text($("#util-num-select").find("option:selected").val());
			$("#prevrs").text($("#res-num-select").find("option:selected").val());
			
			// Set the teams list
			var teamtable = $('#team_table').DataTable();
	        var data = teamtable.rows().data();
	        var dataout = [];

			for (index = 0; index < data.length; ++index) {
				var idnum = index + 1;
				var nameid = "#inputteamname" + idnum;
				var ownerid = "#inputteamowner" + idnum;
				var team = data[index];
				team["team_name"] = $(nameid).val();
				team["owner_name"] = $(ownerid).val();
				dataout.push(team);
			}
			
			loadTeamPreviewTable(dataout, false);
			
//			$("#proj-date-label").text($( "#projection-date-selector2" ).val());
//			var hitterfile = $( "#hitter-proj-file" ).val().toString().split("\\");
//			$("#proj-hitterfile-label").text(hitterfile[hitterfile.length - 1]);
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
		$('#createleague-modal').modal('hide');
		progressmodal.showPleaseWait("Updating Projections...");
		
		var leaguecontainer = {};
		var league = {};
		var teamlist = [];
		var teams = {};
		var team = {};
		var profile = {};
		
		league["league_name"] = $("#input-leaguename").val();
		league["league_site"] = $("#select-leaguesite").find("option:selected").val();
		league["league_year"] = $("#select-leagueyear").find("option:selected").val();
		league["num_of_teams"] = $("#select-numberofteams").find("option:selected").val();
		league["mlb_leagues"] = $("#select-mlbleagues").find("option:selected").val();
		league["team_salary"] = $("#select-teamsalary").find("option:selected").val();
		
		league["cat_hitter_hr"] = $("#cat-hitter-hr").is(':checked');
		league["cat_hitter_rbi"] = $("#cat-hitter-hr").is(':checked');
		league["cat_hitter_r"] = $("#cat-hitter-hr").is(':checked');
		league["cat_hitter_sb"] = $("#cat-hitter-hr").is(':checked');
		league["cat_hitter_avg"] = $("#cat-hitter-hr").is(':checked');
		league["cat_pitcher_wins"] = $("#cat-hitter-hr").is(':checked');
		league["cat_pitcher_saves"] = $("#cat-hitter-hr").is(':checked');
		league["cat_pitcher_so"] = $("#cat-hitter-hr").is(':checked');
		league["cat_pitcher_era"] = $("#cat-hitter-hr").is(':checked');
		league["cat_pitcher_whip"] = $("#cat-hitter-hr").is(':checked');
		league["num_1b"] = $("#1b-num-select").val();
		league["num_2b"] = $("#2b-num-select").val();
		league["num_ss"] = $("#ss-num-select").val();
		league["num_3b"] = $("#3b-num-select").val();
		league["num_mi"] = $("#mi-num-select").val();
		league["num_ci"] = $("#ci-num-select").val();
		league["num_of"] = $("#of-num-select").val();
		league["num_p"] = $("#p-num-select").val();
		league["num_util"] = $("#util-num-select").val();
		league["num_res"] = $("#res-num-select").val();
		league["num_c"] = $("#c-num-select").val();

		var teamtable = $('#teamprev_table').DataTable();

		teamlist = teamtable.rows().data().toArray();
		
		profile["proj_service"] = "Steamer";
		profile["proj_period"] = "Pre-Season";
		profile["proj_year"] = (new Date).getFullYear();

		leaguecontainer["league"] = league;
		leaguecontainer["league_teams"] = teamlist;
		leaguecontainer["profile"] = profile;

		console.log("leaguecontainer: " + JSON.stringify(leaguecontainer));
		
		mssolutions.fbapp.draftmanager.createandupdateLeague(leaguecontainer);
		
	});

    loadTeamPreviewTable(null, true);
	loadTeamTable(null, true);

});

function loadTeamTable(data, isInitialLoad)
{
	var data_table;
	var table_element = $('#team_table');
	var config = {
        "data": data,
        // responsive: true,
        "bSort" : false,
        "searching": false,
        "paging": false,
        "info": false,
        "columns": [
            { "title": "#", "mData": "team_num", className: "dt-center",
                "render": function ( data, type, row ) {
                	if (data == "1"){
                		return "<b>" + data + "</b>";
                	} else {
                		return data;
                	}
                	
                },
                "targets": 0 },
            { "title": "Team Name", "mData": "team_name" },
            { "title": "Team Owner", "mData": "owner_name"},
            // { "title": "My Team?", "mData": "isuserowner"},
            // { "title": "<i class='fa fa-trash-o'></i>"},
        ],
        "columnDefs": [ 
        {
            "render": function ( data, type, row ) {
            	if (row.team_num == "1"){
            		// console.log("In row.team_num = 1 check: " + row.team_num);
            		return "<input class='form-control input-sm' style='font-weight: bold;' id='inputteamname"+ row.team_num +"'  value='" + data + "'>";
            	} else {
            		// console.log("In row.team_num else check: " + row.team_num);
            		return "<input class='form-control input-sm' id='inputteamname"+ row.team_num +"' value='" + data + "'>";
            	}
            },
            "targets": 1
        },
        {
            "render": function ( data, type, row ) {
            	if (row.team_num == "1"){
            		// console.log("In row.team_num = 1 check: " + row.team_num);
            		return "<input class='form-control input-sm' style='font-weight: bold;'  id='inputteamowner"+ row.team_num +"' value='" + data + "'>";
            	} else {
            		// console.log("In row.team_num else check: " + row.team_num);
            		return "<input class='form-control input-sm'  id='inputteamowner"+ row.team_num +"' value='" + data + "'>";
            	}
            },
            "targets": 2
        },
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


function loadTeamPreviewTable(data, isInitialLoad)
{
	var data_table;
	var table_element = $('#teamprev_table');
	var config = {
        "data": data,
        "bSort" : false,
        "searching": false,
        "paging": false,
        "info": false,
        "columns": [
            { "title": "#", "mData": "team_num" },
            // { "title": "<i class='fa fa-user'></i>", "mData": "isuserowner" },
            { "title": "Team Name", "mData": "team_name" },
            { "title": "Team Owner", "mData": "owner_name"},
        ],
        "columnDefs": [ 
//        {
//            "render": function ( data, type, row ) {
//                if (data == true){
//                	return "<i class='fa fa-check' style='color: #008000;'></i>";
//                } else {
//                	return "";
//                }
//                
//            },
//            "targets": 2
//        },
        {
            "render": function ( data, type, row ) {
            	if (row.team_num == "1"){
            		return "<b>" + data + "</b>";
            	} else {
            		return data;
            	}
            },
            "targets": 0
        },
//        {
//            "render": function ( data, type, row ) {
//            	if (row.team_num == "1"){
//            		return "<i class='fa fa-user'></i>";
//            	} else {
//            		return "";
//            	}
//            },
//            "targets": 1
//        },
        {
            "render": function ( data, type, row ) {
            	if (row.team_num == "1"){
            		return "<b>" + data + "</b>";
            	} else {
            		return data;
            	}
            },
            "targets": 1
        },
        {
            "render": function ( data, type, row ) {
            	if (row.team_num == "1"){
            		return "<b>" + data + "</b>";
            	} else {
            		return data;
            	}
            },
            "targets": 2
        },
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


function loadLeagueSelector(data){
	var options = $("#league-select");
	options.find('option').remove().end();
	options.append($("<option value='0'/>").text("--- Select League ---"));
	
	if (undefined !== data){
		$.each(data, function() {
			options.append($("<option value='"+ this.id +"'/>").text(this.league_name));
		});
	} else {
		// console.log("League data is null");
	}

	options.append($("<option value='newleague'/>").text("Add New League..."));
}

/**
 * Create a new league and update it via the API.
 */
mssolutions.fbapp.draftmanager.createandupdateLeague = function(leaguecontainer) {

	gapi.client.draftapp.league.savenewleague(leaguecontainer).execute(
      function(resp) {
        if (!resp.code) { 
        	console.log("League save complete.");
        	mssolutions.fbapp.draftmanager.loadLeagues();
        	progressmodal.hidePleaseWait();
        }
        else {
        	console.log("Failed to create league: ", resp.code + " : " + resp.message);
        }
      });
};

/**
 * Update a league via the API.
 */
mssolutions.fbapp.draftmanager.updateLeague = function() {

	gapi.client.draftapp.league.updateleague().execute(
      function(resp) {
        if (!resp.code) { 
        	// loadLeagueSelector(resp.items);
        }
        else {
        	console.log("Failed to load leagues: ", resp.code + " : " + resp.message);
        }
      });
};


/**
 * load projection profiles via the API.
 */
mssolutions.fbapp.draftmanager.loadLeagues = function() {

	gapi.client.draftapp.league.getuserleagues().execute(
      function(resp) {
        if (!resp.code) { 
        	loadLeagueSelector(resp.items);
        }
        else {
        	console.log("Failed to load leagues: ", resp.code + " : " + resp.message);
        }
      });
};

/**
 * load league definition lists for the league create modal via the API.
 */
mssolutions.fbapp.draftmanager.loadLeagueModal = function() {

	gapi.client.draftapp.league.getleaguemodalfields().execute(
      function(resp) {
        if (!resp.code) { 
        	var yearselect = $("#select-leagueyear");
        	var year = (new Date).getFullYear();
        	var yearnext = (new Date).getFullYear()+1;
        	yearselect.find('option').remove().end();
        	yearselect.append($('<option>', { value : year }).text(year)); 
        	yearselect.append($('<option>', { value : yearnext }).text(yearnext)); 
        	
        	var siteselect = $("#select-leaguesite");
        	siteselect.find('option').remove().end();
        	$.each(resp.league_sites, function() {
        		siteselect.append($('<option>', { value : this }).text(this)); 
        	});
        	
        	var mlbselect = $("#select-mlbleagues");
        	mlbselect.find('option').remove().end();
        	$.each(resp.mlb_leagues, function() {
        		mlbselect.append($('<option>', { value : this }).text(this)); 
        	});
        	
        	var salaryselect = $("#select-teamsalary");
        	salaryselect.find('option').remove().end();
        	for (i = 100; i <= 300; i=i+10) { 
        		salaryselect.append($('<option>', { value : i }).text("$"+i)); 
        	}

        }
        else {
        	console.log("Failed to load league modal fields: ", resp.code + " : " + resp.message);
        }
      });
};


/**
 * Initializes the application.
 * @param {string} apiRoot Root of the API's path.
 */
mssolutions.fbapp.draftmanager.init_nav = function(apiRoot) {
	
	//  check if endpoints have been intialized
	if (typeof gapi.client.draftapp !== 'object') {
		
		console.log("Loading gapi.client.draftapp");
		
		// Set the token for this app initialization
		gapi.auth.setToken({
		    access_token: localStorage.getItem("ClientToken")
		});;
		
		// Loads the draftapp API asynchronously, and triggers
		// login when they have completed.
		var apisToLoad;
		var callback = function() {
			if (--apisToLoad == 0) {
				mssolutions.fbapp.draftmanager.loadLeagues();
				mssolutions.fbapp.draftmanager.loadLeagueModal();
			}
		}

		apisToLoad = 1; // must match number of calls to gapi.client.load()
		gapi.client.load('draftapp', 'v1', callback, apiRoot);

	}
	else {
		mssolutions.fbapp.draftmanager.loadLeagues();
		mssolutions.fbapp.draftmanager.loadLeagueModal();
	}
};