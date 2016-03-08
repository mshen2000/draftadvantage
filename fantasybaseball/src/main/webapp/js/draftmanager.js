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

// Saves the selected player from the player grid
var playerdraftrow;

// A blank template of a team roster (includes positions)
var teamrostertemplate;

// The teamrostertemplate converted into just a list of roster positions and counts.
var teamrostercounts;

// Count of RES spots in teamrostercounts
var rescounts;

// List of teams from getleagueteams
var globalteamlist;

// League selector listener
$(function() {

	  $('#league-select').on('change', function(){
		  // console.log("In league-select on change");
	    var selected = $(this).find("option:selected").val();
		if(selected == "newleague") {
			$("#createleague-modal").modal("show");
			$('#league-select').val("0");
		} else if (selected == "0") {
			$("#intro-container").show();
			$("#league-container").hide();
		} else {
			loadLeagueContent(selected);
		}
	  });
	  
	  $('#team-select').on('change', function(){
	    var selected = $(this).find("option:selected").text();
	  });
	  
});


$(document).ready(function()
{
    $("#select-draftteam").change(function(e){

    	if ($(this).val() != null){
	        if ($(this).val().length > 1) {
	            $("option", this).removeAttr("selected");
	            $('#lbl-draftprevteam').text("[none]");
	        } else {
	        	$('#lbl-draftprevteam').text($(this).find("option:selected").text());
	        	loadDraftPlayerPosSelector();
	        }
    	} else {
        	$('#lbl-draftprevteam').text("[none]");
        }
        
        if (($("#select-draftteam").val() == null) ||
        		($("#select-draftamt").val() == null) ||
        		($("#select-draftposition").val() == null)){
        	$("#btn-draftplayer").attr("disabled","disabled");
        } else {
        	$("#btn-draftplayer").removeAttr("disabled");
        }

    });
    $("#select-draftamt").change(function(e){

    	if ($(this).val() != null){
	        if ($(this).val().length > 1) {
	            $("option", this).removeAttr("selected");
	            $('#lbl-draftprevamt').text("[none]");
	        } else {
	        	$('#lbl-draftprevamt').text($(this).find("option:selected").text());
	        }
    	} else {
        	$('#lbl-draftprevamt').text("[none]");
        }
        
        if (($("#select-draftteam").val() == null) ||
        		($("#select-draftamt").val() == null) ||
        		($("#select-draftposition").val() == null)){
        	$("#btn-draftplayer").attr("disabled","disabled");
        } else {
        	$("#btn-draftplayer").removeAttr("disabled");
        }

    });
    $("#select-draftposition").change(function(e){

    	if ($(this).val() != null){
	        if ($(this).val().length > 1) {
	            $("option", this).removeAttr("selected");
	            $('#lbl-draftprevpos').text("[none]");
	        } else {
	        	$('#lbl-draftprevpos').text($(this).find("option:selected").text().split(" ")[0]);
	        }
    	} else {
        	$('#lbl-draftprevpos').text("[none]");
        }
        
        if (($("#select-draftteam").val() == null) ||
        		($("#select-draftamt").val() == null) ||
        		($("#select-draftposition").val() == null)){
        	$("#btn-draftplayer").attr("disabled","disabled");
        } else {
        	$("#btn-draftplayer").removeAttr("disabled");
        }

    });
    
    // Team select in Team Info Tab
    $("#team-select").change(function(e){

    	updateTeamInfoTab();

    });


	
	$('#btn-deleteleague').click(function() 
		{
		var selectedtext = $("#league-select").find("option:selected").text();
		
        BootstrapDialog.show({
          	type: 'type-default',
              title: 'Confirm Delete League',
              message: 'Are you sure you want to delete league ' + selectedtext + '?',
              spinicon: 'fa fa-refresh',
              buttons: [{
                  id: 'btn-confirm-delete-league',   
                  icon: 'fa fa-trash',       
                  cssClass: 'btn-danger', 
                  autospin: true,
                  label: 'Delete',
                  action: function(dialog) {
                	  $("#btn-confirm-delete-league").prop("disabled",true);
                	var selected = $("#league-select").find("option:selected").val();
                	console.log("Selected league id: " + selected);
                	mssolutions.fbapp.draftmanager.deleteLeague(selected);
                  }
              }, {
                  label: 'Cancel',
                  action: function(dialog) {
                  	dialog.close();
                  }
              }]
          });
	    	
		});
	
	$('#btn-allplayers').click(function() 
	{
		$('#playergrid_table').DataTable().search( '' ).columns().search( '' ).draw();
	});
	$('#btn-pitchers').click(function() 
	{
		$('#playergrid_table').DataTable().search( '' ).columns().search( '' );
		$('#playergrid_table').DataTable().columns( 0 ).search( 'P' ).draw();	
	});
	$('#btn-hitters').click(function() 
			{
				$('#playergrid_table').DataTable().search( '' ).columns().search( '' );
				$('#playergrid_table').DataTable().columns( 0 ).search( 'H' ).draw();	
			});
	$('#btn-c').click(function() 
			{
				$('#playergrid_table').DataTable().search( '' ).columns().search( '' );
				$('#playergrid_table').DataTable().columns( 4 ).search( 'C' ).draw();	
			});
	$('#btn-1b').click(function() 
			{
				$('#playergrid_table').DataTable().search( '' ).columns().search( '' );
				$('#playergrid_table').DataTable().columns( 4 ).search( '1B' ).draw();	
			});
	$('#btn-2b').click(function() 
			{
		$('#playergrid_table').DataTable().search( '' ).columns().search( '' );
				$('#playergrid_table').DataTable().columns( 4 ).search( '2B' ).draw();	
			});
	$('#btn-ss').click(function() 
			{
		$('#playergrid_table').DataTable().search( '' ).columns().search( '' );
				$('#playergrid_table').DataTable().columns( 4 ).search( 'SS' ).draw();	
			});
	$('#btn-3b').click(function() 
			{
		$('#playergrid_table').DataTable().search( '' ).columns().search( '' );
				$('#playergrid_table').DataTable().columns( 4 ).search( '3B' ).draw();	
			});
	$('#btn-mi').click(function() 
			{
		$('#playergrid_table').DataTable().search( '' ).columns().search( '' );
				$('#playergrid_table').DataTable().columns( 4 ).search( '2B|SS',true ).draw();	
			});
	$('#btn-ci').click(function() 
			{
		$('#playergrid_table').DataTable().search( '' ).columns().search( '' );
				$('#playergrid_table').DataTable().columns( 4 ).search( '1B|3B',true ).draw();	
			});
	$('#btn-of').click(function() 
			{
		$('#playergrid_table').DataTable().search( '' ).columns().search( '' );
				$('#playergrid_table').DataTable().columns( 4 ).search( 'OF' ).draw();	
			});
	$('#btn-rp').click(function() 
			{
		$('#playergrid_table').DataTable().search( '' ).columns().search( '' );
				$('#playergrid_table').DataTable().columns( 4 ).search( 'RP' ).draw();	
			});
	$('#btn-sp').click(function() 
			{
		$('#playergrid_table').DataTable().search( '' ).columns().search( '' );
				$('#playergrid_table').DataTable().columns( 4 ).search( 'SP' ).draw();	
			});
	
	
	$('#btn-draftplayer').click(function() 
	{
		var league_id = $("#league-select").find("option:selected").val();
		var playertable = $('#playergrid_table').DataTable();
		var playerid = $("#header-draftplayer").val();
		var teamid = $("#select-draftteam").find("option:selected").val();
		var position = $("#select-draftposition").find("option:selected").val();
		var amount = $("#select-draftamt").find("option:selected").val();
		
//		var row = playertable.row('#' + playerid);
		playerdraftrow.leagueteam_id = teamid;
		playerdraftrow.team_roster_position = position;
		playerdraftrow.team_player_salary = amount;
		
		console.log("Draft Player: " + playerdraftrow.full_name);
		console.log("Draft Player ID: " + playerdraftrow.id);
		console.log("Draft Player leagueteam_id: " + playerdraftrow.leagueteam_id);
		console.log("Draft Player roster_position: " + playerdraftrow.team_roster_position);
		console.log("Draft Player salary: " + playerdraftrow.team_player_salary);
		
		playertable.row('#' + playerdraftrow.id + '').data(playerdraftrow).draw();
		
		// Draft player
		mssolutions.fbapp.draftmanager.draftPlayer(league_id, teamid, playerdraftrow.id, 
				playerdraftrow.team_roster_position, playerdraftrow.team_player_salary);
		
		$('#draftplayer-modal').modal('hide');
		
		// Show the team info tab
		$('#info-tabs a[href="#tab-teaminfo"]').tab('show');
		// Set the team select to the drafting team
		$("#team-select").val(teamid);


	});
	
	
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
		
		profile["projection_service"] = "Steamer";
		profile["projection_period"] = "Pre-Season";
		profile["projected_year"] = (new Date).getFullYear();

		leaguecontainer["league"] = league;
		leaguecontainer["league_teams"] = teamlist;
		leaguecontainer["profile"] = profile;

		// console.log("leaguecontainer: " + JSON.stringify(leaguecontainer));
		
		mssolutions.fbapp.draftmanager.createandupdateLeague(leaguecontainer);
		
	});

    loadTeamPreviewTable(null, true);
	loadTeamTable(null, true);
	loadPlayerGridTable(null, true);
	loadTeamRosterTable(null, true);
	
	loadDraftPlayerAmtSelector();

});


function updateTeamInfoTab(){
	// create a deep copy of teamrostertemplate
	// var liveteamrostertemplate = $.extend( true, {}, teamrostertemplate );
	var liveteamrostertemplate = JSON.parse(JSON.stringify(teamrostertemplate));
	
	// update teamrostertabel with the blank template
	loadTeamRosterTable(liveteamrostertemplate, false);
	
	var teamid = $("#team-select").find("option:selected").val();
	// console.log("TeamID: " + teamid);
	var playertable = $('#playergrid_table').DataTable();
	var teamrostertable = $('#teamroster_table').DataTable();
	
	
	// Get players from table that have been drafted by selected team
	var teamplayers = playertable.rows( function ( idx, data, node ) {
        return data.leagueteam_id == teamid ?
            true : false;
    } )
    .data();

	// For each drafted player on a team, fill them into the team roster grid
	$.each( teamplayers, function( key, value ) {
		// console.log("Each teamplayer: " + value.full_name);
		$.each( liveteamrostertemplate, function( rkey, rvalue ) {
			// console.log("Each teamrostertemplate: " + rvalue.position);
			if ((value.team_roster_position == rvalue.position)&&
					((rvalue.name == null)||(rvalue.name == ""))){
				rvalue.name = value.full_name;
				rvalue.salary = value.team_player_salary;
				// console.log("Updating teamrostertemplate: " + rvalue.name + ", " + rvalue.salary + ", " + rvalue.position  + ", " + rvalue.index);
				teamrostertable.row('#' + rvalue.index + '').data(rvalue).draw();
				return false;
			}

		});
		
	});
	
	// Find team in global team list
	var team;
	$.each( globalteamlist, function( key, value ) {
		// console.log("Each teamlist: " + value.team_name);
		if (teamid == value.id){
			// console.log("Found team: " + value.team_name);
			team = value;
			return false;
		}
	});
	
	var teamstartingsalary = team.adj_starting_salary;
	var balance = teamstartingsalary - teamrostertable.column( 3 ).data().sum();
	var spots = liveteamrostertemplate.length - teamplayers.length - rescount;
	var perplayer = balance / spots;
	
	console.log("Team salary: " + teamstartingsalary);
	console.log("Sum of salaries: " + teamrostertable.column( 3 ).data().sum());
	
	$('#lbl-teambalance').text("Balance: $" + balance + "  ");
	$('#lbl-teamstarting').text("Starting: $" + teamstartingsalary);
	$('#lbl-teamspots').text("Remaining Spots: " + spots);
	$('#lbl-teamperplayer').text("Per Player Amount: $" + perplayer.toFixed(2));

}


function loadDraftPlayerAmtSelector(){
	
	var amtselector = $("#select-draftamt");
	amtselector.find('option').remove().end();

	for (i = 1; i <= 100; i++) { 
		amtselector.append($("<option value='" + i + "'/>").text("$" + i));
	}

}


function resetDraftPlayerModal(){
	
	$("option", "#select-draftteam").removeAttr("selected");
	$("option", "#select-draftamt").removeAttr("selected");
	var posselector = $("#select-draftposition");
	posselector.find('option').remove().end();
	$('#lbl-draftprevteam').text("[none]");
	$('#lbl-draftprevamt').text("[none]");
	$('#lbl-draftprevpos').text("[none]");
	
}

function loadDraftPlayerPosSelector(){
	
	var teamid = $("#select-draftteam").val();
	var posselector = $("#select-draftposition");
	posselector.find('option').remove().end();

	
	console.log("TeamID: " + teamid);
	
	var playertable = $('#playergrid_table').DataTable();
	
	// Get players from table that have been drafted by selected team
	var teamplayers = playertable.rows( function ( idx, data, node ) {
        return data.leagueteam_id == teamid ?
            true : false;
    } )
    .data();
	
	// Convert team roster data into just a list of roster positions and counts.
	var arr = [];
	var liveteamrostercounts = {};
	
	$.each( teamplayers, function( key, value ) {
		  arr.push(value.team_roster_position);
		});

	for(var i = 0; i< arr.length; i++) {
	    var pos = arr[i];
	    liveteamrostercounts[pos] = liveteamrostercounts[pos] ? liveteamrostercounts[pos]+1 : 1;
	}
	
	// console.log("Team Roster Counts: " + JSON.stringify(teamrostercounts));
	
	var selc = true;
	var sel1b = true;
	var sel2b = true;
	var sel3b = true;
	var selss = true;
	var selmi = true;
	var selci = true;
	var selof = true;
	var selutil = true;
	var selp = true;
	var selres = true;
	
	var countc = teamrostercounts["C"];
	var count1b = teamrostercounts["1B"];
	var count2b = teamrostercounts["2B"];
	var count3b = teamrostercounts["SS"];
	var countss = teamrostercounts["3B"];
	var countmi = teamrostercounts["MI"];
	var countci = teamrostercounts["CI"];
	var countof = teamrostercounts["OF"];
	var countutil = teamrostercounts["UT"];
	var countp = teamrostercounts["P"];
	var countres = teamrostercounts["RES"];
	
	$.each( liveteamrostercounts, function( lkey, lvalue ) {
		
		if (lkey == "C")  countc = teamrostercounts["C"] - lvalue;
		if (lkey == "1B")  count1b = teamrostercounts["1B"] - lvalue;
		if (lkey == "2B")  count2b = teamrostercounts["2B"] - lvalue;
		if (lkey == "SS")  countss = teamrostercounts["SS"] - lvalue;
		if (lkey == "3B")  count3b = teamrostercounts["3B"] - lvalue;
		if (lkey == "MI")  countmi = teamrostercounts["MI"] - lvalue;
		if (lkey == "CI")  countci = teamrostercounts["CI"] - lvalue;
		if (lkey == "OF")  countof = teamrostercounts["OF"] - lvalue;
		if (lkey == "UT")  countutil = teamrostercounts["UT"] - lvalue;
		if (lkey == "P")  countp = teamrostercounts["P"] - lvalue;
		if (lkey == "RES")  countres = teamrostercounts["RES"] - lvalue;

		if ((lkey == "C")&&(teamrostercounts["C"] <= lvalue)) selc = false;
		if ((lkey == "1B")&&(teamrostercounts["1B"] <= lvalue)) sel1b = false;
		if ((lkey == "2B")&&(teamrostercounts["2B"] <= lvalue)) sel2b = false;
		if ((lkey == "SS")&&(teamrostercounts["SS"] <= lvalue)) selss = false;
		if ((lkey == "3B")&&(teamrostercounts["3B"] <= lvalue)) sel3b = false;
		if ((lkey == "MI")&&(teamrostercounts["MI"] <= lvalue)) selmi = false;
		if ((lkey == "CI")&&(teamrostercounts["CI"] <= lvalue)) selci = false;
		if ((lkey == "OF")&&(teamrostercounts["OF"] <= lvalue)) selof = false;
		if ((lkey == "UT")&&(teamrostercounts["UT"] <= lvalue)) selutil = false;
		if ((lkey == "P")&&(teamrostercounts["P"] <= lvalue)) selp = false;
		if ((lkey == "RES")&&(teamrostercounts["RES"] <= lvalue)) selres = false;
			
	});

	if (playerdraftrow.pitcher_hitter == "H"){
		if (selc) posselector.append($("<option value='C'/>").text("C (" + countc + ")"));
		if (sel1b) posselector.append($("<option value='1B'/>").text("1B (" + count1b + ")"));
		if (sel2b) posselector.append($("<option value='2B'/>").text("2B (" + count2b + ")"));
		if (selss) posselector.append($("<option value='SS'/>").text("SS (" + countss + ")"));
		if (sel3b) posselector.append($("<option value='3B'/>").text("3B (" + count3b + ")"));
		if (selmi) posselector.append($("<option value='MI'/>").text("MI (" + countmi + ")"));
		if (selci) posselector.append($("<option value='CI'/>").text("CI (" + countci + ")"));
		if (selof) posselector.append($("<option value='OF'/>").text("OF (" + countof + ")"));
		if (selutil) posselector.append($("<option value='UT'/>").text("Util (" + countutil + ")"));
		if (selres) posselector.append($("<option value='RES'/>").text("Res (" + countres + ")"));
	} else if (playerdraftrow.pitcher_hitter == "P"){
		if (selp) posselector.append($("<option value='P'/>").text("P (" + countp + ")"));
		if (selres) posselector.append($("<option value='RES'/>").text("Res (" + countres + ")"));
	}
	
}


function loadTeamRosterTable(data, isInitialLoad)
{
    var calcDataTableHeight = function() {
        return $(window).height();
    };
	var data_table;
	var table_element = $('#teamroster_table');
	var config = {
		responsive: true,
    	"processing": true,
        "bSort" : false,
        rowId: 'index',
        "searching": false,
        "info": false,
    	select: 'single',
        data: data,
        // "scrollY": calcDataTableHeight(),
        "paging": false,
        "order": [[ 0, "asc" ]],
        "columns": [
            { "visible": false, "title": "index", "mData": "index" },
            { "title": "Pos", "mData": "position" },
            { "title": "Player", "mData": "name", "sDefaultContent": ""},
            { "title": "$", "mData": "salary", "sDefaultContent": "", "render": function ( data, type, row ) {
            	if ((row.name == null)||(row.name == "")) return "";
            	return "$" + data;
            }},
        ]
        };
	
	if (isInitialLoad) 	{
		console.log("window height: " + calcDataTableHeight());
		data_table = table_element.dataTable(config);
		// data_table.fnSettings().oScroll.sY = $('#maintab1').height()-125;
		
	} else {
		console.log("window height: " + calcDataTableHeight());
		data_table = table_element.DataTable();
		data_table.destroy();
		table_element.empty();
		data_table = table_element.dataTable(config);
		// data_table.fnSettings().oScroll.sY = $('#maintab1').height()-125;
		
	}

}


function loadPlayerGridTable(data, isInitialLoad)
{
    var calcDataTableHeight = function() {
        return $(window).height();
    };
	var data_table;
	var table_element = $('#playergrid_table');
	var config = {
		responsive: true,
    	"processing": true,
        data: data,
        // "scrollY": calcDataTableHeight(),
        rowId: 'id',
        "paging": true,
        "order": [[ 16, "desc" ]],
        "iDisplayLength": 15,
        "language": {
            "lengthMenu": "Display <select  style='width:auto;' class='form-control'><option value='10'>10</option>" +
            		"<option value='15'>15</option>" +
            		"<option value='20'>20</option>" +
            		"<option value='25'>25</option>" +
            		"<option value='50'>50</option></select> records per page"
        },
        "columns": [
            { "visible": false, "title": "pitcher_hitter", "mData": "pitcher_hitter" },
            { "title": "Name", "mData": "full_name",  "render": function ( data, type, row ) {
            	return data + " (" + row.team + ")"
                }},
            { "title": "Age", "mData": "age" },
            { "visible": false, "title": "Team", "mData": "team"},
            { "title": "Pos", "mData": "player_position", "sDefaultContent": ""},
            { "title": "St", "mData": "dc_status", "sDefaultContent": ""},
            { "title": "Avg", "mData": "hitter_avg", "render": function ( data, type, row ) {
            	if (row.pitcher_hitter == "H"){
            		var avgnum = data.toFixed(3);
                    return avgnum.toString().substr(avgnum.length - 4);
            	} else if (row.pitcher_hitter == "P"){return "";}
                }, "sDefaultContent": ""},
            { "title": "HR", "mData": "hitter_hr", "render": function ( data, type, row ) {
            	if (row.pitcher_hitter == "H"){
            		return avgnum = data.toFixed(0);
            	} else if (row.pitcher_hitter == "P"){return "";}
                }, "sDefaultContent": ""},
            { "title": "SB", "mData": "hitter_sb", "render": function ( data, type, row ) {
            	if (row.pitcher_hitter == "H"){
            		return avgnum = data.toFixed(0);
            	} else if (row.pitcher_hitter == "P"){return "";}
                }, "sDefaultContent": ""},
            { "title": "R", "mData": "hitter_runs", "render": function ( data, type, row ) {
            	if (row.pitcher_hitter == "H"){
            		return avgnum = data.toFixed(0);
            	} else if (row.pitcher_hitter == "P"){return "";}
                }, "sDefaultContent": ""},
            { "title": "RBI", "mData": "hitter_rbi", "render": function ( data, type, row ) {
            	if (row.pitcher_hitter == "H"){
            		return avgnum = data.toFixed(0);
            	} else if (row.pitcher_hitter == "P"){return "";}
                }, "sDefaultContent": ""},
            { "title": "W", "mData": "pitcher_w", "render": function ( data, type, row ) {
            	if (row.pitcher_hitter == "P"){
            		return avgnum = data.toFixed(0);
            	} else if (row.pitcher_hitter == "H"){return "";}
                }, "sDefaultContent": ""},
            { "title": "SV", "mData": "pitcher_sv", "render": function ( data, type, row ) {
            	if (row.pitcher_hitter == "P"){
            		return avgnum = data.toFixed(0);
            	} else if (row.pitcher_hitter == "H"){return "";}
                }, "sDefaultContent": ""},
            { "title": "SO", "mData": "pitcher_k", "render": function ( data, type, row ) {
            	if (row.pitcher_hitter == "P"){
            		return avgnum = data.toFixed(0);
            	} else if (row.pitcher_hitter == "H"){return "";}
                }, "sDefaultContent": ""},
            { "title": "ERA", "mData": "pitcher_era", "render": function ( data, type, row ) {
            	if (row.pitcher_hitter == "P"){
            		return avgnum = data.toFixed(2);
            	} else if (row.pitcher_hitter == "H"){return "";}
                }, "sDefaultContent": ""},
            { "title": "WHIP", "mData": "pitcher_whip", "render": function ( data, type, row ) {
            	if (row.pitcher_hitter == "P"){
            		return avgnum = data.toFixed(2);
            	} else if (row.pitcher_hitter == "H"){return "";}
                }, "sDefaultContent": ""},
            { "title": "NPV", "mData": "total_z", render: $.fn.dataTable.render.number( ',', '.', 1 ), "sDefaultContent": ""},
            { "title": "i$", "mData": "init_auction_value", "render": function ( data, type, row ) {
            		return "$" + data.toFixed(0);
                }, "sDefaultContent": ""},
            { "title": "l$", "mData": "live_auction_value", "render": function ( data, type, row ) {
        		return "$" + data.toFixed(0);
            }, "sDefaultContent": ""},
            { "title": "Draft", "mData": "leagueteam_id", "render": function ( data, type, row ) {
            	if (data == 0)
            		return "<button type='button' class='btn btn-primary btn-xs btn-draft'><i class='fa fa-chevron-right'></i><i class='fa fa-chevron-right'></i></button>";
            	return "<button type='button' class='btn btn-default btn-xs btn-undraft'><i class='fa fa-chevron-left'></i><i class='fa fa-chevron-left'></i></button>";
            }},
            { "title": "TM ID", "mData": "leagueteam_id", "sDefaultContent": ""},
            { "visible": false, "title": "id", "mData": "id" },
            { "visible": false, "title": "Roster Position", "mData": "team_roster_position", "sDefaultContent": "" },
            { "visible": false, "title": "Team Salary", "mData": "team_player_salary", "sDefaultContent": "" },
        ]
        };
	
	if (isInitialLoad) 	{
		console.log("window height: " + calcDataTableHeight());
		data_table = table_element.dataTable(config);
		// data_table.fnSettings().oScroll.sY = $('#maintab1').height()-125;
		
	} else {
		console.log("window height: " + calcDataTableHeight());
		data_table = table_element.DataTable();
		data_table.destroy();
		table_element.empty();
		data_table = table_element.dataTable(config);
		// data_table.fnSettings().oScroll.sY = $('#maintab1').height()-125;
		
		// Update the team info tab
		updateTeamInfoTab();
		
	}

    $('#playergrid_table tbody').on( 'click', '.btn-draft', function () {
    	var data_table = $('#playergrid_table').DataTable();
        var data = data_table.row( $(this).parents('tr') ).data();
        playerdraftrow = data;
        
        resetDraftPlayerModal();
        $("#header-draftplayer").text("Draft Player: " + data.full_name + " (" + data.team + ")");
        $("#header-draftplayer").val(data.id);
        $("#lbl-draftprevplayer").text(data.full_name + " (" + data.team + ")");
        $("#draftplayer-modal").modal("show");
        
        // console.log("Player id: " + $("#header-draftplayer").val());
        
    } );
    
    $('#playergrid_table tbody').on( 'click', '.btn-undraft', function () {

        
    } );

}

function loadTeamTable(data, isInitialLoad)
{
	var data_table;
	var table_element = $('#team_table');
	var config = {
        "data": data,
        "bSort" : false,
        "searching": false,
        "paging": false,
        "info": false,
        "columns": [
            { "title": "#", "mData": "team_num", className: "dt-center",
                "render": function ( data, type, row ) {
                	if (data == "1"){return "<b>" + data + "</b>";
                	} else {return data;}
                },
                "targets": 0 },
            { "title": "Team Name", "mData": "team_name" },
            { "title": "Team Owner", "mData": "owner_name"},
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
        {
            "render": function ( data, type, row ) {
            	if (row.team_num == "1"){return "<b>" + data + "</b>";
            	} else {return data;}
            },
            "targets": 0
        },
        {
            "render": function ( data, type, row ) {
            	if (row.team_num == "1"){return "<b>" + data + "</b>";
            	} else {return data;}
            },
            "targets": 1
        },
        {
            "render": function ( data, type, row ) {
            	if (row.team_num == "1"){return "<b>" + data + "</b>";
            	} else {return data;}
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

function loadLeagueContent(leagueid){
	
	$("#intro-container").hide();
	$("#league-container").show();
	
	mssolutions.fbapp.draftmanager.getLeaguePlayerData(leagueid);
	mssolutions.fbapp.draftmanager.getLeagueTeams(leagueid);
	mssolutions.fbapp.draftmanager.getLeagueRoster(leagueid);
	updateTeamInfoTab();
}

function loadLeagueIntro(){
	$("#league-container").hide();
	$("#intro-container").show();
}


function loadLeagueSelector(data){
	var options = $("#league-select");
	options.find('option').remove().end();
	options.append($("<option value='0'/>").text("--- Select League ---"));
	
	if (undefined !== data){
		$.each(data, function() {
			console.log("Loading league selector: ID-" + this.id + " VAL-" + this.league_name);
			options.append($("<option value='"+ this.id +"'/>").text(this.league_name + "(" + this.league_year + ")"));
		});
	} else {
		// console.log("League data is null");
	}

	options.append($("<option value='newleague'/>").text("Add New League..."));
}

function loadTeamSelect(data){
	var teamselect = $("#team-select");
	teamselect.find('option').remove().end();
	if (undefined !== data){
		$.each(data, function() {
			// console.log("Loading team selector: ID-" + this.id + " VAL-" + this.team_name);
			teamselect.append($("<option value='"+ this.id +"'/>").text(this.team_name));
		});
	} else {
		// console.log("League data is null");
	}
	
	var draftteamselect = $("#select-draftteam");
	draftteamselect.find('option').remove().end();
	if (undefined !== data){
		$.each(data, function() {
			draftteamselect.append($("<option value='"+ this.id +"'/>").text(this.team_name));
		});
	} else {}

}

/**
 * Draft player via the API.
 */
mssolutions.fbapp.draftmanager.draftPlayer = function(league_id, league_team_id, 
		player_projected_id, team_roster_position, team_player_salary) {
	
	console.log("In draftPlayer...");
	
	gapi.client.draftapp.league.draftplayer({
		'league_id' : league_id,
		'league_team_id' : league_team_id,
		'player_projected_id' : player_projected_id,
		'team_roster_position' : team_roster_position,
		'team_player_salary' : team_player_salary}).execute(
      function(resp) {
        if (!resp.code) { 
        	console.log("Draft player complete. League Player ID: " + resp.longdescription);
        }
        else {
        	console.log("Failed to draft player: ", resp.code + " : " + resp.message);
        }
      });
};

/**
 * Undraft player via the API.
 */
mssolutions.fbapp.draftmanager.undraftPlayer = function(league_id, league_team_id, 
		player_projected_id, team_roster_position, team_player_salary) {
	
	console.log("In draftPlayer...");
	
	gapi.client.draftapp.league.draftplayer({
		'league_id' : league_id,
		'league_team_id' : league_team_id,
		'player_projected_id' : player_projected_id,
		'team_roster_position' : team_roster_position,
		'team_player_salary' : team_player_salary}).execute(
      function(resp) {
        if (!resp.code) { 
        	console.log("Draft player complete. League Player ID: " + resp.longdescription);
        }
        else {
        	console.log("Failed to draft player: ", resp.code + " : " + resp.message);
        }
      });
};


/**
 * Create a new league and update it via the API.
 */
mssolutions.fbapp.draftmanager.createandupdateLeague = function(leaguecontainer) {

	gapi.client.draftapp.league.savenewleague(leaguecontainer).execute(
      function(resp) {
        if (!resp.code) { 
        	console.log("League save complete. League ID: " + resp.longdescription);
        	mssolutions.fbapp.draftmanager.loadLeagueList();
        	$('#league-select').val(resp.longdescription);
        	loadLeagueContent(resp.longdescription);
        	progressmodal.hidePleaseWait();
        	
        	// mssolutions.fbapp.draftmanager.updateLeague(resp.longdescription);
        }
        else {
        	console.log("Failed to create league: ", resp.code + " : " + resp.message);
        }
      });
};


/**
 * Get league roster via the API.
 */
mssolutions.fbapp.draftmanager.getLeagueRoster = function(leagueid) {
	console.log("getLeagueRoster, leagueid: " + leagueid);
	gapi.client.draftapp.league.getleagueroster({
		'id' : leagueid}).execute(
      function(resp) {
        if (!resp.code) { 
        	console.log("League roster get complete.");
        	
        	// Save blank roster to global var
        	teamrostertemplate = resp.items;

        	// Convert roster template into just a list of roster positions and counts.
        	var arr = [];
        	var counts = {};
        	
        	$.each( resp.items, function( key, value ) {
        		  arr.push(value.position);
        		});

        	for(var i = 0; i< arr.length; i++) {
        	    var pos = arr[i];
        	    counts[pos] = counts[pos] ? counts[pos]+1 : 1;
        	}
        	
        	teamrostercounts = counts;
        	rescount = counts["RES"];
        	console.log("Roster Counts: " + JSON.stringify(teamrostercounts));
        	console.log("RES Count: " + rescount);
        	
        	// Load the blank roster template into a datatable
        	loadTeamRosterTable(resp.items, false);
        }
        else {
        	console.log("Failed to get league roster: ", resp.code + " : " + resp.message);
        }
      });
};

/**
 * Get league teams via the API.
 */
mssolutions.fbapp.draftmanager.getLeagueTeams = function(leagueid) {
	console.log("getLeagueTeams, leagueid: " + leagueid);
	gapi.client.draftapp.league.getleagueteams({
		'id' : leagueid}).execute(
      function(resp) {
        if (!resp.code) { 
        	console.log("League teams get complete.");
        	globalteamlist = resp.items;
        	loadTeamSelect(resp.items);
        }
        else {
        	console.log("Failed to get league teams: ", resp.code + " : " + resp.message);
        }
      });
};

/**
 * Get league player data via the API.
 */
mssolutions.fbapp.draftmanager.getLeaguePlayerData = function(leagueid) {
	console.log("getLeaguePlayerData, leagueid: " + leagueid);
	gapi.client.draftapp.league.getleagueplayerdata({
		'id' : leagueid}).execute(
      function(resp) {
        if (!resp.code) { 
        	console.log("League player get complete.");
        	loadPlayerGridTable(resp.items, false);
        }
        else {
        	console.log("Failed to get league player data: ", resp.code + " : " + resp.message);
        }
      });
};


/**
 * Delete a league via the API.
 */
mssolutions.fbapp.draftmanager.deleteLeague = function(leagueid) {
	console.log("Deleting League id..." + leagueid);
	gapi.client.draftapp.league.deleteleague({
		'id' : leagueid}).execute(
      function(resp) {
        if (!resp.code) { 
        	console.log("League delete complete.");
        	mssolutions.fbapp.draftmanager.loadLeagueList();
        	loadLeagueIntro();
        	BootstrapDialog.closeAll();
        }
        else {
        	console.log("Failed to delete league: ", resp.code + " : " + resp.message);
        }
      });
};


/**
 * load List of leagues via the API.
 */
mssolutions.fbapp.draftmanager.loadLeagueList = function() {

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
				mssolutions.fbapp.draftmanager.loadLeagueList();
				mssolutions.fbapp.draftmanager.loadLeagueModal();
			}
		}

		apisToLoad = 1; // must match number of calls to gapi.client.load()
		gapi.client.load('draftapp', 'v1', callback, apiRoot);

	}
	else {
		mssolutions.fbapp.draftmanager.loadLeagueList();
		mssolutions.fbapp.draftmanager.loadLeagueModal();
	}
};