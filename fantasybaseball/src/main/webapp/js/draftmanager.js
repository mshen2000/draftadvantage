/**
 * @fileoverview
 * Provides methods for the top navbar UI and interaction with the APIs
 */

/** global namespace for MS Solutions projects. */
var mssolutions = mssolutions || {};

/** namespace for fantasy baseball applications. */
mssolutions.fbapp = mssolutions.fbapp || {};

/** projections namespace. */
mssolutions.fbapp.draftmanager = mssolutions.fbapp.draftmanager || /**
 * @author Michael
 *
 */
{};
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

// Saves the selected player from the player grid for draft update
var playerdraftrow;

//Saves the selected player from the player grid for note update
var playernoterow;

// A blank template of a team roster (includes positions)
var teamrostertemplate;

// The teamrostertemplate converted into just a list of roster positions and counts.
var dm_teamrostercounts;

// Count of RES spots in teamrostercounts
var dm_rescount;

// List of teams from getleagueteams
var dm_globalteamlist;

// Regex definition for filtering undrafted vs all players
var regex_drafted = '';

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
	// Tab change event
	$("a[href='#maintab2']").on('hidden.bs.tab', function (e) {

		$(".dm_poschart").empty();

	});
	
	// Tab change event
	$("a[href='#maintab2']").on('shown.bs.tab', function (e) {
		
		var player_table = $('#playergrid_table').DataTable();
		var data = player_table.data();
		var filtered_data = $.grep(data, function(v) {
			var isdrafted;
			if (v.leagueteam_name == null) isdrafted = false;
			else if (v.leagueteam_name.trim().length < 1) isdrafted = false;
			else isdrafted = true;
		    return v.unknownplayer == false && isdrafted == false;
		});
		
		filtered_data.sort(function(a, b) {
		    return parseFloat(b.total_z) - parseFloat(a.total_z);
		});
		
		// console.log("filtered_data: " + JSON.stringify(filtered_data));
		
		var filtered_data_c = $.grep(filtered_data, function(v) {
		    return v.player_position.indexOf("C") > -1;}).slice(0,10);
		var filtered_data_1b = $.grep(filtered_data, function(v) {
		    return v.player_position.indexOf("1B") > -1;}).slice(0,10);
		var filtered_data_2b = $.grep(filtered_data, function(v) {
		    return v.player_position.indexOf("2B") > -1;}).slice(0,10);
		var filtered_data_ss = $.grep(filtered_data, function(v) {
		    return v.player_position.indexOf("SS") > -1;}).slice(0,10);
		var filtered_data_3b = $.grep(filtered_data, function(v) {
		    return v.player_position.indexOf("3B") > -1;}).slice(0,10);
		var filtered_data_of = $.grep(filtered_data, function(v) {
		    return v.player_position.indexOf("OF") > -1;}).slice(0,10);
		var filtered_data_sp = $.grep(filtered_data, function(v) {
		    return v.player_position.indexOf("SP") > -1;}).slice(0,10);
		var filtered_data_rp = $.grep(filtered_data, function(v) {
		    return v.player_position.indexOf("RP") > -1;}).slice(0,10);
		
		loadPositionalTable(filtered_data_c, $("#pos_c_table"), false, true, "#chart-c");
		loadPositionalTable(filtered_data_1b, $("#pos_1b_table"), false, true, "#chart-1b");
		loadPositionalTable(filtered_data_2b, $("#pos_2b_table"), false, true, "#chart-2b");
		loadPositionalTable(filtered_data_ss, $("#pos_ss_table"), false, true, "#chart-ss");
		loadPositionalTable(filtered_data_3b, $("#pos_3b_table"), false, true, "#chart-3b");
		loadPositionalTable(filtered_data_of, $("#pos_of_table"), false, true, "#chart-of");
		loadPositionalTable(filtered_data_sp, $("#pos_sp_table"), false, false, "#chart-sp");
		loadPositionalTable(filtered_data_rp, $("#pos_rp_table"), false, false, "#chart-rp");
		
	});
	
	
    $("#select-draftteam").change(function(e){

    	if ($(this).val() != null){
        	$('#lbl-draftprevteam').text($(this).find("option:selected").text());
        	loadDraftPlayerPosSelector();
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
    $("#select-draftteamunk").change(function(e){
    	// console.log("in select-draftteamunk change");
    	if ($(this).val() != "0"){
        	$('#lbl-draftprevteamunk').text($(this).find("option:selected").text());
        	if ($("#select-draftpitcherhitterunk").val() != "0"){
        		// console.log("before loaddraftplayerselector");
        		loadDraftUnkPlayerPosSelector();
        	}
    	} else {
        	$('#lbl-draftprevteamunk').text("[none]");
        	$("#select-draftpositionunk").find('option').remove().end();
        }
        
        if (($("#select-draftteamunk").val() == "0") ||
        		($("#select-draftpitcherhitterunk").val() == "0") ||
        		($.trim($("#input-draftplayernameunk").val()) == "") ||
        		($("#select-draftamtunk").val() == null) ||
        		($("#select-draftpositionunk").val() == null)){
        	$("#btn-draftplayerunk").attr("disabled","disabled");
        } else {
        	$("#btn-draftplayerunk").removeAttr("disabled");
        }

    });
    $("#input-draftplayernameunk").keyup(function(e){
        
    	if ($.trim($("#input-draftplayernameunk").val()) != ""){
    		$('#lbl-draftprevplayerunk').text($("#input-draftplayernameunk").val());
    	} else {
    		$('#lbl-draftprevplayerunk').text("[none]");
    	}
    	
        if (($("#select-draftteamunk").val() == "0") ||
        		($("#select-draftpitcherhitterunk").val() == "0") ||
        		($.trim($("#input-draftplayernameunk").val()) == "") ||
        		($("#select-draftamtunk").val() == null) ||
        		($("#select-draftpositionunk").val() == null)){
        	$("#btn-draftplayerunk").attr("disabled","disabled");
        } else {
        	$("#btn-draftplayerunk").removeAttr("disabled");
        }

    });
    $("#select-draftpitcherhitterunk").change(function(e){
    	// console.log("in select-draftpitcherhitterunk change");
    	if (($(this).val() != "0")&&($("#select-draftteamunk").val() != "0")){
    		// console.log("before loaddraftplayerselector");
    		loadDraftUnkPlayerPosSelector();
    	}
    	
    	if ($(this).val() == "0"){
        	$("#select-draftpositionunk").find('option').remove().end();
    	}

        if (($("#select-draftteamunk").val() == "0") ||
        		($("#select-draftpitcherhitterunk").val() == "0") ||
        		($.trim($("#input-draftplayernameunk").val()) == "") ||
        		($("#select-draftamtunk").val() == null) ||
        		($("#select-draftpositionunk").val() == null)){
        	$("#btn-draftplayerunk").attr("disabled","disabled");
        } else {
        	$("#btn-draftplayerunk").removeAttr("disabled");
        }
    });
    $("#select-draftamt").change(function(e){

    	if ($(this).val() != null){
	        $('#lbl-draftprevamt').text($(this).find("option:selected").text());
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
    $("#select-draftamtunk").change(function(e){

    	if ($(this).val() != null){
	        $('#lbl-draftprevamtunk').text($(this).find("option:selected").text());
    	} else {
        	$('#lbl-draftprevamtunk').text("[none]");
        }
        
        if (($("#select-draftteamunk").val() == "0") ||
        		($("#select-draftpitcherhitterunk").val() == "0") ||
        		($.trim($("#input-draftplayernameunk").val()) == "") ||
        		($("#select-draftamtunk").val() == null) ||
        		($("#select-draftpositionunk").val() == null)){
        	$("#btn-draftplayerunk").attr("disabled","disabled");
        } else {
        	$("#btn-draftplayerunk").removeAttr("disabled");
        }

    });
    $("#select-draftposition").change(function(e){
    	
    	if ($(this).val() != null){
	        $('#lbl-draftprevpos').text($(this).find("option:selected").text().split(" ")[0]);
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
    $("#select-draftpositionunk").change(function(e){
    	
    	if ($(this).val() != null){
	        $('#lbl-draftprevposunk').text($(this).find("option:selected").text().split(" ")[0]);
    	} else {
        	$('#lbl-draftprevposunk').text("[none]");
        }
        
        if (($("#select-draftteamunk").val() == "0") ||
        		($("#select-draftpitcherhitterunk").val() == "0") ||
        		($.trim($("#input-draftplayernameunk").val()) == "") ||
        		($("#select-draftamtunk").val() == null) ||
        		($("#select-draftpositionunk").val() == null)){
        	$("#btn-draftplayerunk").attr("disabled","disabled");
        } else {
        	$("#btn-draftplayerunk").removeAttr("disabled");
        }

    });
    
    // Team select in Team Info Tab
    $("#team-select").change(function(e){
    	updateTeamInfoTab();
    });
    
    // Text Area player note in player info tab
    $('#textarea-playernote').keyup (function () {
        $("#btn-playerinfosavenote").removeAttr("disabled");
    });
    
	$('#btn-playerinfosavenote').click(function() 
	{
		$("#btn-playerinfosavenote").attr("disabled","disabled");
		
		var league_id = $("#league-select").find("option:selected").val();
		var playerid = $("#lbl-playerinfoname").val();
		var playernote = $("#textarea-playernote").val();
		var playertable = $('#playergrid_table').DataTable();
		
		console.log("player note textarea: " + playernote);

		playernoterow.team_player_note = playernote;
		
		// console.log("Note Player: " + playernoterow.full_name);
		// console.log("Note Player ID: " + playernoterow.id);
		// console.log("Note Player team_player_note: " + playernoterow.team_player_note);
		
		playertable.row('#' + playernoterow.id + '').data(playernoterow).draw();
		
		mssolutions.fbapp.draftmanager.updatePlayerNote(league_id, 
				playerid, playernote);
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
                	// console.log("Selected league id: " + selected);
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
	
	$('#btn-grp1 button').click(function() {
	    $(this).addClass('active');
	    $(this).siblings().removeClass('active');
	    $('#btn-grp2 button').removeClass('active');
	});
    $('#btn-grp2 button').click(function() {
	    $(this).addClass('active');
	    $(this).siblings().removeClass('active');
	    $('#btn-grp1 button').removeClass('active');
	});
    
    $('#btn-gridviewstats').click(function() {
	    $(this).addClass('active');
	    $(this).siblings().removeClass('active');
	    
	    var table = $('#playergrid_table').DataTable();
	    var columns_stat = table.columns('.dm_stat');
	    var columns_z = table.columns('.dm_zscore');
	    columns_stat.visible(true);
	    columns_z.visible(false);
	});
    $('#btn-gridviewz').click(function() {
	    $(this).addClass('active');
	    $(this).siblings().removeClass('active');
	    
	    var table = $('#playergrid_table').DataTable();
	    var columns_stat = table.columns('.dm_stat');
	    var columns_z = table.columns('.dm_zscore');
	    columns_stat.visible(false);
	    columns_z.visible(true);
	});
	
	$('#btn-allplayers').click(function() 
	{
	    if ($('#select-draftedplayerfilter').val() == '0') regex_drafted = '(^$)|(\s+$)'; 
	    else if ($('#select-draftedplayerfilter').val() == '-1') regex_drafted = ''; 
	    else regex_drafted = $('#select-draftedplayerfilter').find("option:selected").text();
		$('#playergrid_table').DataTable().search( '' ).columns().search( '' );
		// console.log('regex_drafted = ' + regex_drafted);
		$('#playergrid_table').DataTable().columns( 35 ).search( false );
		$('#playergrid_table').DataTable().columns( 30 ).search( regex_drafted , true ).draw();
	});
	$('#btn-pitchers').click(function() { filterPlayerType('P'); });
	$('#btn-hitters').click(function() { filterPlayerType('H'); });
	$('#btn-c').click(function() { filterPlayerPosition('C'); });
	$('#btn-1b').click(function() { filterPlayerPosition('1B'); });
	$('#btn-2b').click(function() { filterPlayerPosition('2B'); });
	$('#btn-ss').click(function() { filterPlayerPosition('SS'); });
	$('#btn-3b').click(function() { filterPlayerPosition('3B'); });
	$('#btn-mi').click(function() { filterPlayerPosition('2B|SS'); });
	$('#btn-ci').click(function() { filterPlayerPosition('1B|3B'); });
	$('#btn-of').click(function() { filterPlayerPosition('OF'); });
	$('#btn-rp').click(function() { filterPlayerPosition('RP');	});
	$('#btn-sp').click(function() { filterPlayerPosition('SP'); });
    $('#select-draftedplayerfilter').change(function() {
        if ($(this).val() == '0') regex_drafted = '(^$)|(\s+$)'; 
        else if ($(this).val() == '-1') regex_drafted = ''; 
        else regex_drafted = $(this).find("option:selected").text();
        // console.log('regex_drafted = ' + regex_drafted);
        $('#playergrid_table').DataTable().columns( 35 ).search( false );
        $('#playergrid_table').DataTable().columns( 30 ).search( regex_drafted , true ).draw();	
      });
	
	
    $('#btn-draftunknownplayermodal').click(function() 
	{
    	resetDraftUnknownPlayerModal();
        $("#draftplayerunk-modal").modal("show");
	});  
      
	$('#btn-draftplayer').click(function() 
	{
		var league_id = $("#league-select").find("option:selected").val();
		var playertable = $('#playergrid_table').DataTable();
		var playerid = $("#header-draftplayer").val();
		var teamid = $("#select-draftteam").find("option:selected").val();
		var teamname = $("#select-draftteam").find("option:selected").text();
		var position = $("#select-draftposition").find("option:selected").val();
		var amount = $("#select-draftamt").find("option:selected").val();
		
//		var row = playertable.row('#' + playerid);
		playerdraftrow.leagueteam_id = teamid;
		playerdraftrow.leagueteam_name = teamname;
		playerdraftrow.team_roster_position = position;
		playerdraftrow.team_player_salary = amount;
		
		// console.log("Draft Player: " + playerdraftrow.full_name);
		// console.log("Draft Player ID: " + playerdraftrow.id);
		// console.log("Draft Player leagueteam_id: " + playerdraftrow.leagueteam_id);
		// console.log("Draft Player roster_position: " + playerdraftrow.team_roster_position);
		// console.log("Draft Player salary: " + playerdraftrow.team_player_salary);
		
		playertable.row('#' + playerdraftrow.id + '').data(playerdraftrow).draw();
		
		// Draft player
		mssolutions.fbapp.draftmanager.draftPlayer(league_id, teamid, playerdraftrow.id, 
				playerdraftrow.team_roster_position, playerdraftrow.team_player_salary);
		
		$('#draftplayer-modal').modal('hide');
		
		// Show the team info tab
		$('#info-tabs a[href="#tab-teaminfo"]').tab('show');
		// Set the team select to the drafting team
		$("#team-select").val(teamid);
		updateTeamInfoTab();
		
		calcLiveAuctionValue();

	});
	
	$('#btn-draftplayerunk').click(function() 
	{
		var unknown_player_name = "[U] " + $("#input-draftplayernameunk").val();
		var pitcher_hitter = $("#select-draftpitcherhitterunk").val();
		var league_id = $("#league-select").find("option:selected").val();
		var playertable = $('#playergrid_table').DataTable();
		// var playerid = $("#header-draftplayer").val();
		var teamid = $("#select-draftteamunk").find("option:selected").val();
		var teamname = $("#select-draftteamunk").find("option:selected").text();
		var position = $("#select-draftpositionunk").find("option:selected").val();
		var amount = $("#select-draftamtunk").find("option:selected").val();
		
		var unknownplayerrow = {};
		
		if (pitcher_hitter == "H") unknownplayerrow = getReplHitter();
		else unknownplayerrow = getReplPitcher();

		unknownplayerrow.full_name = unknown_player_name;
		unknownplayerrow.unknown_player_name = unknown_player_name;
		unknownplayerrow.leagueteam_id = teamid;
		unknownplayerrow.leagueteam_name = teamname;
		unknownplayerrow.team_roster_position = position;
		unknownplayerrow.team_player_salary = amount;
		unknownplayerrow.unknownplayer = true;
		
		// console.log("Draft Unknown Player: " + unknownplayerrow.full_name);
		// console.log("Draft Unknown Player leagueteam_id: " + unknownplayerrow.leagueteam_id);
		// console.log("Draft Unknown Player roster_position: " + unknownplayerrow.team_roster_position);
		// console.log("Draft Unknown Player salary: " + unknownplayerrow.team_player_salary);
		
		// If unknown player doesnt already exist in the playertable, 
		// add it to the playertable
		var playercheck = playertable.rows( function ( idx, data, node ) {
	        return data.full_name == unknown_player_name ?
	            true : false;
	    } );
		if (playercheck.length < 1) {
			playertable.row.add(unknownplayerrow).draw(false);
		} else {	
			playercheck.remove();
			playertable.row.add(unknownplayerrow).draw(false);
		}
		
		// Draft player
		mssolutions.fbapp.draftmanager.draftUnknownPlayer(league_id, teamid, unknown_player_name, 
				pitcher_hitter, position, amount);
		
		$('#draftplayerunk-modal').modal('hide');
		$("#input-draftplayernameunk").prop('disabled', false);
		
		// Show the team info tab
		$('#info-tabs a[href="#tab-teaminfo"]').tab('show');
		// Set the team select to the drafting team
		$("#team-select").val(teamid);
		updateTeamInfoTab();
		
		calcLiveAuctionValue();

	});
	
	
	$('#btn-undraftplayer').click(function () {
		var roster_table = $('#teamroster_table').DataTable();
		var roster_row = roster_table.rows('.selected').data()[0];
    	var player_table = $('#playergrid_table').DataTable();
    	var data1;
    	
    	// If player is unknown find player grid row using the player name
    	// Otherwise, use the playerid
    	if (roster_row.name.trim().substring(0, 2) == "[U") {
    		
    		// console.log("Undrafting unknown player");
    		
    		var row = player_table
    	    .rows( function ( idx, data, node ) {
    	        return data.full_name == roster_row.name ?
    	            true : false;
    	    } )
    	    .data();
    		// console.log(JSON.stringify(row[0]));
    		showUndraftPlayerDialog(row[0]);

    	} else {
    		// console.log("Undrafting known player");
            data1 = player_table.row('#' + roster_row.playerid).data();
            // console.log(JSON.stringify(data1));
            showUndraftPlayerDialog(data1);
    	}
    	

    } );
	
	$('#btn-editdraftplayer').click(function () {
		
		var roster_table = $('#teamroster_table').DataTable();
		var roster_row = roster_table.rows('.selected').data()[0];
		var teamid = $('#team-select').find("option:selected").val();
		var teamname = $('#team-select').find("option:selected").text();
		
		// console.log(roster_row);
		// console.log(roster_row.playerid);

    	var player_table = $('#playergrid_table').DataTable();
    	var data;
    	var amtselect;
    	var amtopt;
    	
    	// If player is unknown find player grid row using the player name
    	// Otherwise, use the playerid
    	if (roster_row.name.trim().substring(0, 2) == "[U") {
    		
    		data = player_table.rows( function ( idx, data, node ) {
    	        return data.full_name == roster_row.name ?
    	            true : false;
    	    } ).data()[0];
    		
            playerdraftrow = data;
            // console.log(JSON.stringify(data));
            
            var playername = roster_row.name.substring(3, roster_row.name.length).trim()

            // $("#header-draftplayer").text("Draft Player: " + data.full_name + " (" + data.team + ")");
            $("#input-draftplayernameunk").val(playername);
            $("#input-draftplayernameunk").prop('disabled', true);
            $("#lbl-draftprevplayerunk").text(playername);
            
        	$("#select-draftpitcherhitterunk").val(data.pitcher_hitter);
        	$("#select-draftteamunk").val(teamid);
            
            loadDraftPlayerAmtSelector();
            loadDraftUnkPlayerPosSelector(roster_row.position);

        	// $("#select-draftamt").val(roster_row.salary);
        	$("#select-draftpositionunk").val(roster_row.position);
        	
        	amtselect = $('#select-draftamtunk')[0];
        	amtopt = $('#select-draftamtunk option[value=' + roster_row.salary + ']')[0];
        	amtopt.selected = true;
        	selectAndScrollToOption(amtselect, amtopt);

        	$('#lbl-draftprevteamunk').text(teamname);
        	$('#lbl-draftprevamtunk').text('$' + roster_row.salary);
        	$('#lbl-draftprevposunk').text(roster_row.position);
        	
        	$("#btn-draftplayerunk").removeAttr("disabled");
        	$("#draftplayerunk-modal").modal("show");

    		
    	} else {
    		
            data = player_table.row('#' + roster_row.playerid).data();
            playerdraftrow = data;
            // console.log(JSON.stringify(data));
            
            // resetDraftPlayerModal(roster_row);
            $("#header-draftplayer").text("Draft Player: " + data.full_name + " (" + data.team + ")");
            $("#header-draftplayer").val(data.id);
            $("#lbl-draftprevplayer").text(data.full_name + " (" + data.team + ")");
            $("#select-draftteam").val(teamid);
            
            loadDraftPlayerAmtSelector();
            loadDraftPlayerPosSelector(roster_row.position);
        	
        	// $("#select-draftamt").val(roster_row.salary);
        	$("#select-draftposition").val(roster_row.position);
        	
        	amtselect = $('#select-draftamt')[0];
        	amtopt = $('#select-draftamt option[value=' + roster_row.salary + ']')[0];
        	amtopt.selected = true;
        	selectAndScrollToOption(amtselect, amtopt);

        	$('#lbl-draftprevteam').text(teamname);
        	$('#lbl-draftprevamt').text('$' + roster_row.salary);
        	$('#lbl-draftprevpos').text(roster_row.position);
        	
        	$("#btn-draftplayer").removeAttr("disabled");
        	$("#draftplayer-modal").modal("show");
    		
    	}

    } );
	
	
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
	
	loadPositionalTable(null, $("#pos_c_table"), true, true);
	loadPositionalTable(null, $("#pos_1b_table"), true, true);
	loadPositionalTable(null, $("#pos_2b_table"), true, true);
	loadPositionalTable(null, $("#pos_ss_table"), true, true);
	loadPositionalTable(null, $("#pos_3b_table"), true, true);
	loadPositionalTable(null, $("#pos_of_table"), true, true);
	loadPositionalTable(null, $("#pos_sp_table"), true, false);
	loadPositionalTable(null, $("#pos_rp_table"), true, false);
	
});

function filterPlayerPosition(position){
    if ($('#select-draftedplayerfilter').val() == '0') regex_drafted = '(^$)|(\s+$)'; 
    else if ($('#select-draftedplayerfilter').val() == '-1') regex_drafted = ''; 
    else regex_drafted = $('#select-draftedplayerfilter').find("option:selected").text();
    // console.log('regex_drafted = ' + regex_drafted);
	$('#playergrid_table').DataTable().search( '' ).columns().search( '' );
	$('#playergrid_table').DataTable().columns( 35 ).search( false );
	$('#playergrid_table').DataTable().columns( 30 ).search( regex_drafted , true );	
	$('#playergrid_table').DataTable().columns( 4 ).search( position , true ).draw();	
}

function filterPlayerType(pitcherhitter){
    if ($('#select-draftedplayerfilter').val() == '0') regex_drafted = '(^$)|(\s+$)'; 
    else if ($('#select-draftedplayerfilter').val() == '-1') regex_drafted = ''; 
    else regex_drafted = $('#select-draftedplayerfilter').find("option:selected").text();
    // console.log('regex_drafted = ' + regex_drafted);
	$('#playergrid_table').DataTable().search( '' ).columns().search( '' );
	$('#playergrid_table').DataTable().columns( 35 ).search( false );
	$('#playergrid_table').DataTable().columns( 30 ).search( regex_drafted , true );	
	$('#playergrid_table').DataTable().columns( 0 ).search( pitcherhitter , true ).draw();	
}


function updateTeamInfoTab(){
	// create a deep copy of teamrostertemplate
	var liveteamrostertemplate;
	if (teamrostertemplate != null){
		liveteamrostertemplate = JSON.parse(JSON.stringify(teamrostertemplate));

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
					rvalue.playerid = value.id;
					// console.log("Updating teamrostertemplate: " + rvalue.name + ", " + rvalue.salary + ", " + rvalue.position  + ", " + rvalue.index);
					teamrostertable.row('#' + rvalue.index + '').data(rvalue).draw();
					return false;
				}

			});
			
		});
		
		// Find team in global team list
		var team;
		$.each( dm_globalteamlist, function( key, value ) {
			// console.log("Each teamlist: " + value.team_name);
			if (teamid == value.id){
				// console.log("Found team: " + value.team_name);
				team = value;
				return false;
			}
		});
		
		var teamstartingsalary = team.adj_starting_salary;
		var balance = teamstartingsalary - teamrostertable.column( 4 ).data().sum();
		var spots = liveteamrostertemplate.length - teamplayers.length - dm_rescount;
		var perplayer = balance / spots;
		
		// console.log("Team salary: " + teamstartingsalary);
		// console.log("Sum of salaries: " + teamrostertable.column( 4 ).data().sum());
		
		$('#lbl-teambalance').text("Balance: $" + balance + "  ");
		$('#lbl-teamstarting').text("Starting: $" + teamstartingsalary);
		$('#lbl-teamspots').text("Remaining Spots: " + spots);
		$('#lbl-teamperplayer').text("Per Player Amount: $" + perplayer.toFixed(2));
		
        $("#btn-editdraftplayer").attr("disabled", "disabled");
        $("#btn-undraftplayer").attr("disabled", "disabled");
	}
}


function loadDraftPlayerAmtSelector(){
	
	var amtselector = $("#select-draftamt");
	var amtselectorunk = $("#select-draftamtunk");
	amtselector.find('option').remove().end();
	amtselectorunk.find('option').remove().end();

	for (i = 1; i <= 100; i++) { 
		amtselector.append($("<option value='" + i + "'/>").text("$" + i));
		amtselectorunk.append($("<option value='" + i + "'/>").text("$" + i));
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

function resetDraftUnknownPlayerModal(){

	$("option", "#select-draftteamunk").removeAttr("selected");
	$("option", "#select-draftamtunk").removeAttr("selected");
	$("option", "#select-draftpitcherhitterunk").removeAttr("selected");
	var posselectorunk = $("#select-draftpositionunk");
	posselectorunk.find('option').remove().end();
	$('#lbl-draftprevplayerunk').text("[none]");
	$('#lbl-draftprevteamunk').text("[none]");
	$('#lbl-draftprevamtunk').text("[none]");
	$('#lbl-draftprevposunk').text("[none]");
	
	$('#input-draftplayernameunk').val("");

}


/**
 * Description: Loads selections for the position selector in the draft player
 * modal. Selections are based on available positions based on the player and
 * team. For an initial draft of player, the updateplayerposition parameter is
 * null. For updating an existing drafted player, the parameter is the position
 * string for that player. This makes sure that the position is included in the
 * list even if the team has that position filled.
 * 
 * @param updateplayerposition
 */
function loadDraftPlayerPosSelector(updateplayerposition){
	
	// console.log("In loadDraftPlayerPosSelector");
	
	var teamid = $("#select-draftteam").val();
	var teamname = $("#select-draftteam").find("option:selected").text();
	var posselector = $("#select-draftposition");
	posselector.find('option').remove().end();

	// console.log("TeamID: " + teamid);
	// console.log("TeamName: " + teamname);
	
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
	
	// Get league roster counts for each position
	var countc = dm_teamrostercounts["C"];
	var count1b = dm_teamrostercounts["1B"];
	var count2b = dm_teamrostercounts["2B"];
	var count3b = dm_teamrostercounts["SS"];
	var countss = dm_teamrostercounts["3B"];
	var countmi = dm_teamrostercounts["MI"];
	var countci = dm_teamrostercounts["CI"];
	var countof = dm_teamrostercounts["OF"];
	var countutil = dm_teamrostercounts["UT"];
	var countp = dm_teamrostercounts["P"];
	var countres = dm_teamrostercounts["RES"];
	
	// Update counts by subtracting current team roster counts
	// Determine if roster position is available based on count
	$.each( liveteamrostercounts, function( lkey, lvalue ) {
		
		if (lkey == "C")  countc = dm_teamrostercounts["C"] - lvalue;
		if (lkey == "1B")  count1b = dm_teamrostercounts["1B"] - lvalue;
		if (lkey == "2B")  count2b = dm_teamrostercounts["2B"] - lvalue;
		if (lkey == "SS")  countss = dm_teamrostercounts["SS"] - lvalue;
		if (lkey == "3B")  count3b = dm_teamrostercounts["3B"] - lvalue;
		if (lkey == "MI")  countmi = dm_teamrostercounts["MI"] - lvalue;
		if (lkey == "CI")  countci = dm_teamrostercounts["CI"] - lvalue;
		if (lkey == "OF")  countof = dm_teamrostercounts["OF"] - lvalue;
		if (lkey == "UT")  countutil = dm_teamrostercounts["UT"] - lvalue;
		if (lkey == "P")  countp = dm_teamrostercounts["P"] - lvalue;
		if (lkey == "RES")  countres = dm_teamrostercounts["RES"] - lvalue;

		if ((lkey == "C")&&(dm_teamrostercounts["C"] <= lvalue)) selc = false;
		if ((lkey == "1B")&&(dm_teamrostercounts["1B"] <= lvalue)) sel1b = false;
		if ((lkey == "2B")&&(dm_teamrostercounts["2B"] <= lvalue)) sel2b = false;
		if ((lkey == "SS")&&(dm_teamrostercounts["SS"] <= lvalue)) selss = false;
		if ((lkey == "3B")&&(dm_teamrostercounts["3B"] <= lvalue)) sel3b = false;
		if ((lkey == "MI")&&(dm_teamrostercounts["MI"] <= lvalue)) selmi = false;
		if ((lkey == "CI")&&(dm_teamrostercounts["CI"] <= lvalue)) selci = false;
		if ((lkey == "OF")&&(dm_teamrostercounts["OF"] <= lvalue)) selof = false;
		if ((lkey == "UT")&&(dm_teamrostercounts["UT"] <= lvalue)) selutil = false;
		if ((lkey == "P")&&(dm_teamrostercounts["P"] <= lvalue)) selp = false;
		if ((lkey == "RES")&&(dm_teamrostercounts["RES"] <= lvalue)) selres = false;
			
	});
	
	// If updateplayerposition is not null, then make sure that the position is included
	if (updateplayerposition != null){
		if ((updateplayerposition == "C")) selc = true;
		if ((updateplayerposition == "1B")) sel1b = true;
		if ((updateplayerposition == "2B")) sel2b = true;
		if ((updateplayerposition == "SS")) selss = true;
		if ((updateplayerposition == "3B")) sel3b = true;
		if ((updateplayerposition == "MI")) selmi = true;
		if ((updateplayerposition == "CI")) selci = true;
		if ((updateplayerposition == "OF")) selof = true;
		if ((updateplayerposition == "UT")) selutil = true;
		if ((updateplayerposition == "P")) selp = true;
		if ((updateplayerposition == "RES")) selres = true;
	}

	if (playerdraftrow != null){
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
	
}

/**
 * Description: Loads selections for the position selector in the draft unknown
 * player modal. Selections are based on available positions based on the player
 * and team. For an initial draft of player, the updateplayerposition parameter
 * is null. For updating an existing drafted player, the parameter is the
 * position string for that player. This makes sure that the position is
 * included in the list even if the team has that position filled.
 * 
 * @param updateplayerposition
 */
function loadDraftUnkPlayerPosSelector(updateplayerposition){
	
	var teamidunk = $("#select-draftteamunk").val();
	var posselectorunk = $("#select-draftpositionunk");
	var selectpitcherhitterunk = $("#select-draftpitcherhitterunk");
	posselectorunk.find('option').remove().end();

	// console.log("TeamID: " + teamid);
	
	var playertable = $('#playergrid_table').DataTable();
	
	// Get players from table that have been drafted by selected team
	var teamplayers = playertable.rows( function ( idx, data, node ) {
        return data.leagueteam_id == teamidunk ?
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
	
	// Get league roster counts for each position
	var countc = dm_teamrostercounts["C"];
	var count1b = dm_teamrostercounts["1B"];
	var count2b = dm_teamrostercounts["2B"];
	var count3b = dm_teamrostercounts["SS"];
	var countss = dm_teamrostercounts["3B"];
	var countmi = dm_teamrostercounts["MI"];
	var countci = dm_teamrostercounts["CI"];
	var countof = dm_teamrostercounts["OF"];
	var countutil = dm_teamrostercounts["UT"];
	var countp = dm_teamrostercounts["P"];
	var countres = dm_teamrostercounts["RES"];
	
	// Update counts by subtracting current team roster counts
	// Determine if roster position is available based on count
	$.each( liveteamrostercounts, function( lkey, lvalue ) {
		
		if (lkey == "C")  countc = dm_teamrostercounts["C"] - lvalue;
		if (lkey == "1B")  count1b = dm_teamrostercounts["1B"] - lvalue;
		if (lkey == "2B")  count2b = dm_teamrostercounts["2B"] - lvalue;
		if (lkey == "SS")  countss = dm_teamrostercounts["SS"] - lvalue;
		if (lkey == "3B")  count3b = dm_teamrostercounts["3B"] - lvalue;
		if (lkey == "MI")  countmi = dm_teamrostercounts["MI"] - lvalue;
		if (lkey == "CI")  countci = dm_teamrostercounts["CI"] - lvalue;
		if (lkey == "OF")  countof = dm_teamrostercounts["OF"] - lvalue;
		if (lkey == "UT")  countutil = dm_teamrostercounts["UT"] - lvalue;
		if (lkey == "P")  countp = dm_teamrostercounts["P"] - lvalue;
		if (lkey == "RES")  countres = dm_teamrostercounts["RES"] - lvalue;

		if ((lkey == "C")&&(dm_teamrostercounts["C"] <= lvalue)) selc = false;
		if ((lkey == "1B")&&(dm_teamrostercounts["1B"] <= lvalue)) sel1b = false;
		if ((lkey == "2B")&&(dm_teamrostercounts["2B"] <= lvalue)) sel2b = false;
		if ((lkey == "SS")&&(dm_teamrostercounts["SS"] <= lvalue)) selss = false;
		if ((lkey == "3B")&&(dm_teamrostercounts["3B"] <= lvalue)) sel3b = false;
		if ((lkey == "MI")&&(dm_teamrostercounts["MI"] <= lvalue)) selmi = false;
		if ((lkey == "CI")&&(dm_teamrostercounts["CI"] <= lvalue)) selci = false;
		if ((lkey == "OF")&&(dm_teamrostercounts["OF"] <= lvalue)) selof = false;
		if ((lkey == "UT")&&(dm_teamrostercounts["UT"] <= lvalue)) selutil = false;
		if ((lkey == "P")&&(dm_teamrostercounts["P"] <= lvalue)) selp = false;
		if ((lkey == "RES")&&(dm_teamrostercounts["RES"] <= lvalue)) selres = false;
			
	});
	
	// If updateplayerposition is not null, then make sure that the position is included
	if (updateplayerposition != null){
		if ((updateplayerposition == "C")) selc = true;
		if ((updateplayerposition == "1B")) sel1b = true;
		if ((updateplayerposition == "2B")) sel2b = true;
		if ((updateplayerposition == "SS")) selss = true;
		if ((updateplayerposition == "3B")) sel3b = true;
		if ((updateplayerposition == "MI")) selmi = true;
		if ((updateplayerposition == "CI")) selci = true;
		if ((updateplayerposition == "OF")) selof = true;
		if ((updateplayerposition == "UT")) selutil = true;
		if ((updateplayerposition == "P")) selp = true;
		if ((updateplayerposition == "RES")) selres = true;
	}
	
	if (selectpitcherhitterunk.val() == "H"){
		if (selc) posselectorunk.append($("<option value='C'/>").text("C (" + countc + ")"));
		if (sel1b) posselectorunk.append($("<option value='1B'/>").text("1B (" + count1b + ")"));
		if (sel2b) posselectorunk.append($("<option value='2B'/>").text("2B (" + count2b + ")"));
		if (selss) posselectorunk.append($("<option value='SS'/>").text("SS (" + countss + ")"));
		if (sel3b) posselectorunk.append($("<option value='3B'/>").text("3B (" + count3b + ")"));
		if (selmi) posselectorunk.append($("<option value='MI'/>").text("MI (" + countmi + ")"));
		if (selci) posselectorunk.append($("<option value='CI'/>").text("CI (" + countci + ")"));
		if (selof) posselectorunk.append($("<option value='OF'/>").text("OF (" + countof + ")"));
		if (selutil) posselectorunk.append($("<option value='UT'/>").text("Util (" + countutil + ")"));
		if (selres) posselectorunk.append($("<option value='RES'/>").text("Res (" + countres + ")"));
	} else if (selectpitcherhitterunk.val() == "P"){
		if (selp) posselectorunk.append($("<option value='P'/>").text("P (" + countp + ")"));
		if (selres) posselectorunk.append($("<option value='RES'/>").text("Res (" + countres + ")"));
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
        "paging": false,
        "order": [[ 0, "asc" ]],
        "columns": [
            { "visible": false, "title": "index", "mData": "index" },
            { "visible": false, "title": "ID", "mData": "playerid", "sDefaultContent": "" },
            { "title": "Pos", "mData": "position" },
            { "title": "Player", "mData": "name", "sDefaultContent": ""},
            { "title": "$", "mData": "salary", "sDefaultContent": "", "render": function ( data, type, row ) {
            	if ((row.name == null)||(row.name == "")) return "";
            	return "$" + data;
            }},
        ]
        };
	
	if (isInitialLoad) 	{
		// console.log("window height: " + calcDataTableHeight());
		data_table = table_element.dataTable(config);
		// data_table.fnSettings().oScroll.sY = $('#maintab1').height()-125;
		
	} else {
		// console.log("window height: " + calcDataTableHeight());
		data_table = table_element.DataTable();
		data_table.destroy();
		table_element.empty();
		data_table = table_element.dataTable(config);
		// data_table.fnSettings().oScroll.sY = $('#maintab1').height()-125;
		
	}
	
	var data_table = $('#teamroster_table').DataTable();
	data_table
    .on( 'select', function ( e, dt, type, indexes ) {
    	var data_table_b = $('#teamroster_table').DataTable();
        var rows = data_table_b.rows( indexes ).data();
        // console.log("Select: " + rowData[0].name);
        // console.log("Select: " + JSON.stringify(rows[0]));
        if ((rows[0].name == null)||(rows[0].name == "")){
        	data_table_b.rows( indexes ).deselect();
        } else {
            $("#btn-editdraftplayer").removeAttr("disabled");  
            $("#btn-undraftplayer").removeAttr("disabled");  
        }

    } )
    .on( 'deselect', function ( e, dt, type, indexes ) {
        // var rowData = data_table.rows( indexes ).data().toArray();
        // console.log("De-Select: " + rowData[0].name);
        $("#btn-editdraftplayer").attr("disabled", "disabled");
        $("#btn-undraftplayer").attr("disabled", "disabled");
    } );
}


function loadPlayerGridTable(data, isInitialLoad)
{
    var calcDataTableHeight = function() {
        return $(window).height();
    };
	var data_table;
	var table_element = $('#playergrid_table');
	var config = {
		dom: "<'row'<'col-sm-6'B><'col-sm-6'f>>" +
			"<'row'<'col-sm-12'tr>>" +
			"<'row'<'col-sm-5'i><'col-sm-7'p>>",
		responsive: true,
    	"processing": true,
        data: data,
        select: {
            style:    'single',
            // If column with button is selected, it will not register select
            selector: 'td:not(:nth-last-child(2))'  
        },
        rowId: 'id',
        "paging": true,
        "order": [[ 26, "desc" ]],
        "iDisplayLength": 15,
        "language": {
            "lengthMenu": "Display <select  style='width:auto;' class='form-control'><option value='10'>10</option>" +
            		"<option value='15'>15</option>" +
            		"<option value='20'>20</option>" +
            		"<option value='25'>25</option>" +
            		"<option value='50'>50</option></select> records per page"
        },
        buttons: [
          {
              extend: 'collection',
              className: 'btn-sm',
              text: '<i class="fa fa-download"></i> Export <i class="fa fa-caret-down"></i>',
              buttons: [
                  {extend: 'csv',               
                	  exportOptions: {
                		  columns: '.dm_export'
                	  }
                  },
                  {extend: 'pdf',               
                	  exportOptions: {
                		  columns: '.dm_export'
                	  }
                  },
                  {extend: 'print',               
                	  exportOptions: {
                		  columns: '.dm_export'
                	  }
                  }
              ]
          },
          {
              text: '<i class="fa fa-user-plus"></i> Draft Unknown',
              className: 'btn-primary btn-sm',
              action: function ( e, dt, node, config ) {
              	resetDraftUnknownPlayerModal();
                $("#draftplayerunk-modal").modal("show");
              }
          }
              ],
        "columns": [
            { "visible": false, "title": "pitcher_hitter", "mData": "pitcher_hitter" },
            { "title": "Name", className: "dm_export", "mData": "full_name",  "render": function ( data, type, row ) {
            	if ((row.team_player_note == null)||(row.team_player_note.trim() == ""))
            		return data + " (" + row.team + ")";
            	else return data + " (" + row.team + ")&nbsp;&nbsp;<i class='fa fa-file-text'></i>";
                }},
            { "title": "Age", className: "dm_export", "mData": "age", "sDefaultContent": "" },
            { "visible": false, "title": "Team", "mData": "team", "sDefaultContent": ""},
            { "title": "Pos", className: "dm_export", "mData": "player_position", "sDefaultContent": ""},
            { "title": "St", className: "dm_export", "mData": "dc_status", "sDefaultContent": ""},
            { "title": "Avg", className: "dm_stat dm_export", "mData": "hitter_avg", "render": function ( data, type, row ) {
            	if (row.pitcher_hitter == "H"){ 
            		var avgnum = data.toFixed(3);
                    return avgnum.toString().substr(avgnum.length - 4);
            	} else if (row.pitcher_hitter == "P"){return "";}
                },
                "createdCell": function (td, cellData, rowData, row, col) {
                	if (rowData.pitcher_hitter == "H"){ setStatCellColor(td, rowData.hitter_z_avg, 1)}
                },
                "sDefaultContent": ""},
            { "title": "HR", className: "dm_stat dm_export", "mData": "hitter_hr", "render": function ( data, type, row ) {
            	if (row.pitcher_hitter == "H"){ return data.toFixed(0); } else if (row.pitcher_hitter == "P"){return "";}
                }, 
                "createdCell": function (td, cellData, rowData, row, col) {
                	if (rowData.pitcher_hitter == "H"){ setStatCellColor(td, rowData.hitter_z_hr, 1)}
                }, "sDefaultContent": ""},
            { "title": "SB", className: "dm_stat dm_export", "mData": "hitter_sb", "render": function ( data, type, row ) {
            	if (row.pitcher_hitter == "H"){ return data.toFixed(0); } else if (row.pitcher_hitter == "P"){return "";}
                }, 
                "createdCell": function (td, cellData, rowData, row, col) {
                	if (rowData.pitcher_hitter == "H"){ setStatCellColor(td, rowData.hitter_z_sb, 1)}
                }, "sDefaultContent": ""},
            { "title": "R", className: "dm_stat dm_export", "mData": "hitter_runs", "render": function ( data, type, row ) {
            	if (row.pitcher_hitter == "H"){ return data.toFixed(0); } else if (row.pitcher_hitter == "P"){return "";}
                },
                "createdCell": function (td, cellData, rowData, row, col) {
                	if (rowData.pitcher_hitter == "H"){ setStatCellColor(td, rowData.hitter_z_runs, 1)}
                }, "sDefaultContent": ""},
            { "title": "RBI", className: "dm_stat dm_export", "mData": "hitter_rbi", "render": function ( data, type, row ) {
            	if (row.pitcher_hitter == "H"){ return data.toFixed(0) } else if (row.pitcher_hitter == "P"){return "";}
                }, 
                "createdCell": function (td, cellData, rowData, row, col) {
                	if (rowData.pitcher_hitter == "H"){ setStatCellColor(td, rowData.hitter_z_rbi, 1)}
                }, "sDefaultContent": ""},
            { "title": "W", className: "dm_stat dm_export", "mData": "pitcher_w", "render": function ( data, type, row ) {
            	if (row.pitcher_hitter == "P"){ return data.toFixed(0) } else if (row.pitcher_hitter == "H"){return "";}
                },
                "createdCell": function (td, cellData, rowData, row, col) {
                	if (rowData.pitcher_hitter == "P"){ setStatCellColor(td, rowData.pitcher_z_wins, 1)}
                }, "sDefaultContent": ""},
            { "title": "SV", className: "dm_stat dm_export", "mData": "pitcher_sv", "render": function ( data, type, row ) {
            	if (row.pitcher_hitter == "P"){ return data.toFixed(0) } else if (row.pitcher_hitter == "H"){return "";}
                }, 
                "createdCell": function (td, cellData, rowData, row, col) {
                	if (rowData.pitcher_hitter == "P"){ setStatCellColor(td, rowData.pitcher_z_saves, 1)}
                }, "sDefaultContent": ""},
            { "title": "SO", className: "dm_stat dm_export", "mData": "pitcher_k", "render": function ( data, type, row ) {
            	if (row.pitcher_hitter == "P"){ return data.toFixed(0) } else if (row.pitcher_hitter == "H"){return "";}
                }, 
                "createdCell": function (td, cellData, rowData, row, col) {
                	if (rowData.pitcher_hitter == "P"){ setStatCellColor(td, rowData.pitcher_z_so, 1)}
                }, "sDefaultContent": ""},
            { "title": "ERA", className: "dm_stat dm_export", "mData": "pitcher_era", "render": function ( data, type, row ) {
            	if (row.pitcher_hitter == "P"){ return data.toFixed(2) } else if (row.pitcher_hitter == "H"){return "";}
                }, 
                "createdCell": function (td, cellData, rowData, row, col) {
                	if (rowData.pitcher_hitter == "P"){ setStatCellColor(td, rowData.pitcher_z_era, 1)}
                }, "sDefaultContent": ""},
            { "title": "WHIP", className: "dm_stat dm_export", "mData": "pitcher_whip", "render": function ( data, type, row ) {
            	if (row.pitcher_hitter == "P"){ return data.toFixed(2) } else if (row.pitcher_hitter == "H"){return "";}
                }, 
                "createdCell": function (td, cellData, rowData, row, col) {
                	if (rowData.pitcher_hitter == "P"){ setStatCellColor(td, rowData.pitcher_z_whip, 1)}
                }, "sDefaultContent": ""},
                
            { "visible": false, className: "dm_zscore dm_export", "title": "zAVG", "mData": "hitter_z_avg", "render": function ( data, type, row ) {
            	if (row.pitcher_hitter == "H"){return data.toFixed(2);} else if (row.pitcher_hitter == "P"){return "";}
                },
                "createdCell": function (td, cellData, rowData, row, col) {
                	if (rowData.pitcher_hitter == "H"){ setStatCellColor(td, rowData.hitter_z_avg, 1)}
                }, "sDefaultContent": "0" },
            { "visible": false, className: "dm_zscore dm_export", "title": "zHR", "mData": "hitter_z_hr", "render": function ( data, type, row ) {
            	if (row.pitcher_hitter == "H"){return data.toFixed(2);} else if (row.pitcher_hitter == "P"){return "";}
                }, 
                "createdCell": function (td, cellData, rowData, row, col) {
                	if (rowData.pitcher_hitter == "H"){ setStatCellColor(td, rowData.hitter_z_hr, 1)}
                }, "sDefaultContent": "0" },
            { "visible": false, className: "dm_zscore dm_export", "title": "zSB", "mData": "hitter_z_sb", "render": function ( data, type, row ) {
            	if (row.pitcher_hitter == "H"){return data.toFixed(2);} else if (row.pitcher_hitter == "P"){return "";}
                },"createdCell": function (td, cellData, rowData, row, col) {
                	if (rowData.pitcher_hitter == "H"){ setStatCellColor(td, rowData.hitter_z_sb, 1)}
                },  "sDefaultContent": "0" },
            { "visible": false, className: "dm_zscore dm_export", "title": "zR", "mData": "hitter_z_runs", "render": function ( data, type, row ) {
            	if (row.pitcher_hitter == "H"){return data.toFixed(2);} else if (row.pitcher_hitter == "P"){return "";}
                },
                "createdCell": function (td, cellData, rowData, row, col) {
                	if (rowData.pitcher_hitter == "H"){ setStatCellColor(td, rowData.hitter_z_runs, 1)}
                }, "sDefaultContent": "0" },
            { "visible": false, className: "dm_zscore dm_export", "title": "zRBI", "mData": "hitter_z_rbi", "render": function ( data, type, row ) {
            	if (row.pitcher_hitter == "H"){return data.toFixed(2);} else if (row.pitcher_hitter == "P"){return "";}
                },
                "createdCell": function (td, cellData, rowData, row, col) {
                	if (rowData.pitcher_hitter == "H"){ setStatCellColor(td, rowData.hitter_z_rbi, 1)}
                }, "sDefaultContent": "0" },
            { "visible": false, className: "dm_zscore dm_export", "title": "zW", "mData": "pitcher_z_wins", "render": function ( data, type, row ) {
            	if (row.pitcher_hitter == "P"){return data.toFixed(2);} else if (row.pitcher_hitter == "H"){return "";}
                },
                "createdCell": function (td, cellData, rowData, row, col) {
                	if (rowData.pitcher_hitter == "P"){ setStatCellColor(td, rowData.pitcher_z_wins, 1)}
                }, "sDefaultContent": "0" },
            { "visible": false, className: "dm_zscore dm_export", "title": "zSV", "mData": "pitcher_z_saves", "render": function ( data, type, row ) {
            	if (row.pitcher_hitter == "P"){return data.toFixed(2);} else if (row.pitcher_hitter == "H"){return "";}
                }, 
                "createdCell": function (td, cellData, rowData, row, col) {
                	if (rowData.pitcher_hitter == "P"){ setStatCellColor(td, rowData.pitcher_z_saves, 1)}
                }, "sDefaultContent": "0" },
            { "visible": false, className: "dm_zscore dm_export", "title": "zSO", "mData": "pitcher_z_so", "render": function ( data, type, row ) {
            	if (row.pitcher_hitter == "P"){return data.toFixed(2);} else if (row.pitcher_hitter == "H"){return "";}
                }, 
                "createdCell": function (td, cellData, rowData, row, col) {
                	if (rowData.pitcher_hitter == "P"){ setStatCellColor(td, rowData.pitcher_z_so, 1)}
                }, "sDefaultContent": "0" },
            { "visible": false, className: "dm_zscore dm_export", "title": "zWHIP", "mData": "pitcher_z_whip", "render": function ( data, type, row ) {
            	if (row.pitcher_hitter == "P"){return data.toFixed(2);} else if (row.pitcher_hitter == "H"){return "";}
                }, 
                "createdCell": function (td, cellData, rowData, row, col) {
                	if (rowData.pitcher_hitter == "P"){ setStatCellColor(td, rowData.pitcher_z_whip, 1)}
                }, "sDefaultContent": "0" },
            { "visible": false, className: "dm_zscore dm_export", "title": "zERA", "mData": "pitcher_z_era", "render": function ( data, type, row ) {
            	if (row.pitcher_hitter == "P"){return data.toFixed(2);} else if (row.pitcher_hitter == "H"){return "";}
                }, 
                "createdCell": function (td, cellData, rowData, row, col) {
                	if (rowData.pitcher_hitter == "P"){ setStatCellColor(td, rowData.pitcher_z_era, 1)}
                }, "sDefaultContent": "0" },
                
            { "title": "NPV", className: "dm_export", "mData": "total_z", "render": function ( data, type, row ) {
            		if (data == null) return 0;
            		else return data.toFixed(1);
            	},
            	"createdCell": function (td, cellData, rowData, row, col) { setStatCellColor(td, cellData, 5)}, "sDefaultContent": "0"},    
//            { "title": "NPV", className: "dm_export", "mData": "total_z", render: $.fn.dataTable.render.number( ',', '.', 1 ),
//            	"createdCell": function (td, cellData, rowData, row, col) { setStatCellColor(td, cellData, 5)}, "sDefaultContent": "0"},
            { "title": "$", className: "dm_export", "mData": "init_auction_value", "render": function ( data, type, row ) {
            		return "$" + data.toFixed(0);
                }, "sDefaultContent": "0"},
            { "title": "<i class='fa fa-bolt'></i>-$", className: "dm_export", "mData": "live_auction_value", "render": function ( data, type, row ) {
        		return "$" + data.toFixed(0);
            }, "sDefaultContent": ""},
            { "title": "Action", "mData": "leagueteam_id", "render": function ( data, type, row ) {
            	var buttons;
            	if (data == 0)
            		buttons = "<button type='button' class='btn btn-primary btn-xs btn-draft' data-toggle='tooltip' title='Draft Player'><i class='fa fa-user-plus'></i></button>";
            	else buttons = "<button type='button' class='btn btn-default btn-xs btn-undraft' data-toggle='tooltip' title='Undraft Player'><i class='fa fa-user-times'></i></button>";
            	buttons = buttons + "&nbsp;<button type='button' class='btn btn-success btn-xs btn-playerinfo' data-toggle='tooltip' title='Player Info Page'><i class='fa fa-external-link'></i></button>";
            	return buttons;
            }}, 
            { "title": "Team", className: "dm_export", "mData": "leagueteam_name", "sDefaultContent": "", render: $.fn.dataTable.render.ellipsis( 7 )},
            { "visible": false, "title": "id", "mData": "id", "sDefaultContent": "" },
            { "visible": false, "title": "Roster Position", "mData": "team_roster_position", "sDefaultContent": "" },
            { "visible": false, "title": "Team Salary", "mData": "team_player_salary", "sDefaultContent": "" },
//            { "visible": false, "title": "zAVG", "mData": "hitter_z_avg", "render": function ( data, type, row ) {
//            	if (row.pitcher_hitter == "H"){return data.toFixed(2);} else if (row.pitcher_hitter == "P"){return "";}
//                }, "sDefaultContent": "" },
            { "visible": false, "title": "LP ID", "mData": "league_player_id", "sDefaultContent": "" },
            { "visible": false, "title": "unk", "mData": "unknownplayer", "sDefaultContent": "" },
            { "visible": false, className: "dm_export", "title": "note", "mData": "team_player_note", "sDefaultContent": "" },
            
        ]
        };
	
	if (isInitialLoad) 	{
		// console.log("window height: " + calcDataTableHeight());
		data_table = table_element.dataTable(config);
	} else {
		// console.log("window height: " + calcDataTableHeight());
		data_table = table_element.DataTable();
		data_table.destroy();
		table_element.empty();
		data_table = table_element.dataTable(config);
		data_table = table_element.DataTable();
		
		data_table.columns( 35 ).search( false ).draw();
		
		// Update the team info tab
		updateTeamInfoTab();
		
		// Update live auction values
		calcLiveAuctionValue();
	}

	// On Click of the Info button in the Player Grid Table
    $('#playergrid_table tbody').on( 'click', '.btn-playerinfo', function () {

    	var data_table = $('#playergrid_table').DataTable();
        var data = data_table.row( $(this).parents('tr') ).data();

        var win = window.open("http://www.fangraphs.com/players.aspx?lastname=" + data.full_name, '_blank');
        win.focus();
        
    } );
	
	// On Click of the Draft button in the Player Grid Table
    $('#playergrid_table tbody').on( 'click', '.btn-draft', function () {

    	var data_table = $('#playergrid_table').DataTable();
        var data = data_table.row( $(this).parents('tr') ).data();
        playerdraftrow = data;
        
        resetDraftPlayerModal();
        $("#btn-draftplayer").attr("disabled","disabled");
    	// $("#btn-draftplayer").removeAttr("disabled");
        $("#header-draftplayer").text("Draft Player: " + data.full_name + " (" + data.team + ")");
        $("#header-draftplayer").val(data.id);
        $("#lbl-draftprevplayer").text(data.full_name + " (" + data.team + ")");
        $("#draftplayer-modal").modal("show");
        
        // console.log("Player id: " + $("#header-draftplayer").val());
        
    } );
    
    // On Click of the Undraft button in the Player Grid Table
    $('#playergrid_table tbody').on( 'click', '.btn-undraft', function () {

    	var data_table = $('#playergrid_table').DataTable();
        var data = data_table.row( $(this).parents('tr') ).data();
        showUndraftPlayerDialog(data);
        
    } );
    
    // On Select of the Player Grid Table
	var select_data_table = $('#playergrid_table').DataTable();
	select_data_table
    .on( 'select', function ( e, dt, type, indexes ) {
    	var select_data_table_b = $('#playergrid_table').DataTable();
        var row = select_data_table_b.rows( indexes ).data()[0];
        playernoterow = row;
        
        // Show the player info tab
		$('#info-tabs a[href="#tab-playerinfo"]').tab('show');
		
		$("#lbl-playerinfoname").val(row.id);
		$("#lbl-playerinfoname").text(row.full_name);
		$("#lbl-playerinfoteam").text(row.team);
		$("#lbl-playerinfoage").text(row.age);
		$("#lbl-playerinfoelig").text(row.player_position);
		
		if ((row.leagueteam_name == null)||(row.leagueteam_name == ""))
			$("#lbl-playerinfoowner").text("[available]");
		else $("#lbl-playerinfoowner").text(row.leagueteam_name);
		
		$("#textarea-playernote").removeAttr("disabled");
		if (row.team_player_note != null){
			if (row.team_player_note.trim() != ""){
				$("#textarea-playernote").val(row.team_player_note);
			} else $("#textarea-playernote").val("");
		}else $("#textarea-playernote").val("");
			
    } )
    .on( 'deselect', function ( e, dt, type, indexes ) {
    	clearPlayerInfoTab();
    } );

}

function loadPositionalTable(data, table_element, isInitialLoad, isHitter, chartid)
{
	if (data != null){
		
		var maxvalue = Math.max(data[0].total_z, data[1].total_z, data[2].total_z, data[3].total_z, data[4].total_z,
		           data[5].total_z, data[6].total_z, data[7].total_z, data[8].total_z, data[9].total_z);
		
		var options;
		
		if (maxvalue < 10){
			options = {
			  high: 10,
			  referenceValue: 0,
			  seriesBarDistance: 10,
			  reverseData: true,
			  horizontalBars: true,
			  axisY: {
			    offset: 70
			}};
		} else {
			options = {
			  referenceValue: 0,
			  seriesBarDistance: 10,
			  reverseData: true,
			  horizontalBars: true,
			  axisY: {
			    offset: 70
			}};
		}
		
		var chart = new Chartist.Bar(chartid, {
			  labels: [data[0].full_name, data[1].full_name, data[2].full_name, data[3].full_name, data[4].full_name,
			           data[5].full_name, data[6].full_name, data[7].full_name, data[8].full_name, data[9].full_name],
			  series: [
			    [data[0].total_z, data[1].total_z, data[2].total_z, data[3].total_z, data[4].total_z,
		           data[5].total_z, data[6].total_z, data[7].total_z, data[8].total_z, data[9].total_z]
			  ]
			}, options);
			
		chart.on('draw', function(context) {
			  if(context.type === 'bar') {
		        context.element.attr({
		        	// style: 'stroke-width: 10px'
		        	// style: getPosChartColor(Chartist.getMultiValue(context.value))
			    	style: getPosChartColor(context.value)
			    });
		        
		        context.element.animate({
		            x2: {
		              dur: 1000,
		              from: context.x1,
		              to: context.x2,
		              easing: Chartist.Svg.Easing.easeOutQuint
		            },
		            opacity: {
		              dur: 1000,
		              from: 0,
		              to: 1,
		              easing: Chartist.Svg.Easing.easeOutQuint
		            }
		          });
		        
			  }
			});
	}
	
	
	var data_table;
	var config;
	if (isHitter) config = {
        "bSort" : false,
        "searching": false,
        "paging": false,
        "info": false,
		responsive: true,
    	"processing": true,
        data: data,
        "order": [[ 14, "desc" ]],
        "columns": [
            { "title": "Name", className: "dm_export", "mData": "full_name",  "render": function ( data, type, row ) {
            	if ((row.team_player_note == null)||(row.team_player_note.trim() == ""))
            		return data + " (" + row.team + ")";
            	else return data + " (" + row.team + ")&nbsp;&nbsp;<i class='fa fa-file-text'></i>";
                }},
            { "title": "Age", className: "dm_export", "mData": "age", "sDefaultContent": "" },
            { "visible": false, "title": "Team", "mData": "team", "sDefaultContent": ""},
            { "title": "St", className: "dm_export", "mData": "dc_status", "sDefaultContent": ""},
            { "title": "Avg", className: "dm_stat dm_export", "mData": "hitter_avg", "render": function ( data, type, row ) {
            	if (row.pitcher_hitter == "H"){ 
            		var avgnum = data.toFixed(3);
                    return avgnum.toString().substr(avgnum.length - 4);
            	} else if (row.pitcher_hitter == "P"){return "";}
                },
                "createdCell": function (td, cellData, rowData, row, col) {
                	if (rowData.pitcher_hitter == "H"){ setStatCellColor(td, rowData.hitter_z_avg, 1)}
                },
                "sDefaultContent": ""},
            { "title": "HR", className: "dm_stat dm_export", "mData": "hitter_hr", "render": function ( data, type, row ) {
            	if (row.pitcher_hitter == "H"){ return data.toFixed(0); } else if (row.pitcher_hitter == "P"){return "";}
                }, 
                "createdCell": function (td, cellData, rowData, row, col) {
                	if (rowData.pitcher_hitter == "H"){ setStatCellColor(td, rowData.hitter_z_hr, 1)}
                }, "sDefaultContent": ""},
            { "title": "SB", className: "dm_stat dm_export", "mData": "hitter_sb", "render": function ( data, type, row ) {
            	if (row.pitcher_hitter == "H"){ return data.toFixed(0); } else if (row.pitcher_hitter == "P"){return "";}
                }, 
                "createdCell": function (td, cellData, rowData, row, col) {
                	if (rowData.pitcher_hitter == "H"){ setStatCellColor(td, rowData.hitter_z_sb, 1)}
                }, "sDefaultContent": ""},
            { "title": "R", className: "dm_stat dm_export", "mData": "hitter_runs", "render": function ( data, type, row ) {
            	if (row.pitcher_hitter == "H"){ return data.toFixed(0); } else if (row.pitcher_hitter == "P"){return "";}
                },
                "createdCell": function (td, cellData, rowData, row, col) {
                	if (rowData.pitcher_hitter == "H"){ setStatCellColor(td, rowData.hitter_z_runs, 1)}
                }, "sDefaultContent": ""},
            { "title": "RBI", className: "dm_stat dm_export", "mData": "hitter_rbi", "render": function ( data, type, row ) {
            	if (row.pitcher_hitter == "H"){ return data.toFixed(0) } else if (row.pitcher_hitter == "P"){return "";}
                }, 
                "createdCell": function (td, cellData, rowData, row, col) {
                	if (rowData.pitcher_hitter == "H"){ setStatCellColor(td, rowData.hitter_z_rbi, 1)}
                }, "sDefaultContent": ""},

            { "visible": false, className: "dm_zscore dm_export", "title": "zAVG", "mData": "hitter_z_avg", "render": function ( data, type, row ) {
            	if (row.pitcher_hitter == "H"){return data.toFixed(2);} else if (row.pitcher_hitter == "P"){return "";}
                },
                "createdCell": function (td, cellData, rowData, row, col) {
                	if (rowData.pitcher_hitter == "H"){ setStatCellColor(td, rowData.hitter_z_avg, 1)}
                }, "sDefaultContent": "0" },
            { "visible": false, className: "dm_zscore dm_export", "title": "zHR", "mData": "hitter_z_hr", "render": function ( data, type, row ) {
            	if (row.pitcher_hitter == "H"){return data.toFixed(2);} else if (row.pitcher_hitter == "P"){return "";}
                }, 
                "createdCell": function (td, cellData, rowData, row, col) {
                	if (rowData.pitcher_hitter == "H"){ setStatCellColor(td, rowData.hitter_z_hr, 1)}
                }, "sDefaultContent": "0" },
            { "visible": false, className: "dm_zscore dm_export", "title": "zSB", "mData": "hitter_z_sb", "render": function ( data, type, row ) {
            	if (row.pitcher_hitter == "H"){return data.toFixed(2);} else if (row.pitcher_hitter == "P"){return "";}
                },"createdCell": function (td, cellData, rowData, row, col) {
                	if (rowData.pitcher_hitter == "H"){ setStatCellColor(td, rowData.hitter_z_sb, 1)}
                },  "sDefaultContent": "0" },
            { "visible": false, className: "dm_zscore dm_export", "title": "zR", "mData": "hitter_z_runs", "render": function ( data, type, row ) {
            	if (row.pitcher_hitter == "H"){return data.toFixed(2);} else if (row.pitcher_hitter == "P"){return "";}
                },
                "createdCell": function (td, cellData, rowData, row, col) {
                	if (rowData.pitcher_hitter == "H"){ setStatCellColor(td, rowData.hitter_z_runs, 1)}
                }, "sDefaultContent": "0" },
            { "visible": false, className: "dm_zscore dm_export", "title": "zRBI", "mData": "hitter_z_rbi", "render": function ( data, type, row ) {
            	if (row.pitcher_hitter == "H"){return data.toFixed(2);} else if (row.pitcher_hitter == "P"){return "";}
                },
                "createdCell": function (td, cellData, rowData, row, col) {
                	if (rowData.pitcher_hitter == "H"){ setStatCellColor(td, rowData.hitter_z_rbi, 1)}
                }, "sDefaultContent": "0" },
                
            { "title": "NPV", className: "dm_export", "mData": "total_z", "render": function ( data, type, row ) {return data.toFixed(1);},
            	"createdCell": function (td, cellData, rowData, row, col) { setStatCellColor(td, cellData, 5)}, "sDefaultContent": "0"},  
            { "title": "<i class='fa fa-bolt'></i>-$", className: "dm_export", "mData": "live_auction_value", "render": function ( data, type, row ) {
        		return "$" + data.toFixed(0);
            }, "sDefaultContent": ""}
        ]
        };
	else config = {
	        "bSort" : false,
	        "searching": false,
	        "paging": false,
	        "info": false,
			responsive: true,
	    	"processing": true,
	        data: data,
	        "order": [[ 14, "desc" ]],
	        "columns": [
	            { "title": "Name", className: "dm_export", "mData": "full_name",  "render": function ( data, type, row ) {
	            	if ((row.team_player_note == null)||(row.team_player_note.trim() == ""))
	            		return data + " (" + row.team + ")";
	            	else return data + " (" + row.team + ")&nbsp;&nbsp;<i class='fa fa-file-text'></i>";
	                }},
	            { "title": "Age", className: "dm_export", "mData": "age", "sDefaultContent": "" },
	            { "visible": false, "title": "Team", "mData": "team", "sDefaultContent": ""},
	            { "title": "St", className: "dm_export", "mData": "dc_status", "sDefaultContent": ""},
	         
	            { "title": "W", className: "dm_stat dm_export", "mData": "pitcher_w", "render": function ( data, type, row ) {
	            	if (row.pitcher_hitter == "P"){ return data.toFixed(0) } else if (row.pitcher_hitter == "H"){return "";}
	                },
	                "createdCell": function (td, cellData, rowData, row, col) {
	                	if (rowData.pitcher_hitter == "P"){ setStatCellColor(td, rowData.pitcher_z_wins, 1)}
	                }, "sDefaultContent": ""},
	            { "title": "SV", className: "dm_stat dm_export", "mData": "pitcher_sv", "render": function ( data, type, row ) {
	            	if (row.pitcher_hitter == "P"){ return data.toFixed(0) } else if (row.pitcher_hitter == "H"){return "";}
	                }, 
	                "createdCell": function (td, cellData, rowData, row, col) {
	                	if (rowData.pitcher_hitter == "P"){ setStatCellColor(td, rowData.pitcher_z_saves, 1)}
	                }, "sDefaultContent": ""},
	            { "title": "SO", className: "dm_stat dm_export", "mData": "pitcher_k", "render": function ( data, type, row ) {
	            	if (row.pitcher_hitter == "P"){ return data.toFixed(0) } else if (row.pitcher_hitter == "H"){return "";}
	                }, 
	                "createdCell": function (td, cellData, rowData, row, col) {
	                	if (rowData.pitcher_hitter == "P"){ setStatCellColor(td, rowData.pitcher_z_so, 1)}
	                }, "sDefaultContent": ""},
	            { "title": "ERA", className: "dm_stat dm_export", "mData": "pitcher_era", "render": function ( data, type, row ) {
	            	if (row.pitcher_hitter == "P"){ return data.toFixed(2) } else if (row.pitcher_hitter == "H"){return "";}
	                }, 
	                "createdCell": function (td, cellData, rowData, row, col) {
	                	if (rowData.pitcher_hitter == "P"){ setStatCellColor(td, rowData.pitcher_z_era, 1)}
	                }, "sDefaultContent": ""},
	            { "title": "WHIP", className: "dm_stat dm_export", "mData": "pitcher_whip", "render": function ( data, type, row ) {
	            	if (row.pitcher_hitter == "P"){ return data.toFixed(2) } else if (row.pitcher_hitter == "H"){return "";}
	                }, 
	                "createdCell": function (td, cellData, rowData, row, col) {
	                	if (rowData.pitcher_hitter == "P"){ setStatCellColor(td, rowData.pitcher_z_whip, 1)}
	                }, "sDefaultContent": ""},
	                
	            { "visible": false, className: "dm_zscore dm_export", "title": "zW", "mData": "pitcher_z_wins", "render": function ( data, type, row ) {
	            	if (row.pitcher_hitter == "P"){return data.toFixed(2);} else if (row.pitcher_hitter == "H"){return "";}
	                },
	                "createdCell": function (td, cellData, rowData, row, col) {
	                	if (rowData.pitcher_hitter == "P"){ setStatCellColor(td, rowData.pitcher_z_wins, 1)}
	                }, "sDefaultContent": "0" },
	            { "visible": false, className: "dm_zscore dm_export", "title": "zSV", "mData": "pitcher_z_saves", "render": function ( data, type, row ) {
	            	if (row.pitcher_hitter == "P"){return data.toFixed(2);} else if (row.pitcher_hitter == "H"){return "";}
	                }, 
	                "createdCell": function (td, cellData, rowData, row, col) {
	                	if (rowData.pitcher_hitter == "P"){ setStatCellColor(td, rowData.pitcher_z_saves, 1)}
	                }, "sDefaultContent": "0" },
	            { "visible": false, className: "dm_zscore dm_export", "title": "zSO", "mData": "pitcher_z_so", "render": function ( data, type, row ) {
	            	if (row.pitcher_hitter == "P"){return data.toFixed(2);} else if (row.pitcher_hitter == "H"){return "";}
	                }, 
	                "createdCell": function (td, cellData, rowData, row, col) {
	                	if (rowData.pitcher_hitter == "P"){ setStatCellColor(td, rowData.pitcher_z_so, 1)}
	                }, "sDefaultContent": "0" },
	            { "visible": false, className: "dm_zscore dm_export", "title": "zWHIP", "mData": "pitcher_z_whip", "render": function ( data, type, row ) {
	            	if (row.pitcher_hitter == "P"){return data.toFixed(2);} else if (row.pitcher_hitter == "H"){return "";}
	                }, 
	                "createdCell": function (td, cellData, rowData, row, col) {
	                	if (rowData.pitcher_hitter == "P"){ setStatCellColor(td, rowData.pitcher_z_whip, 1)}
	                }, "sDefaultContent": "0" },
	            { "visible": false, className: "dm_zscore dm_export", "title": "zERA", "mData": "pitcher_z_era", "render": function ( data, type, row ) {
	            	if (row.pitcher_hitter == "P"){return data.toFixed(2);} else if (row.pitcher_hitter == "H"){return "";}
	                }, 
	                "createdCell": function (td, cellData, rowData, row, col) {
	                	if (rowData.pitcher_hitter == "P"){ setStatCellColor(td, rowData.pitcher_z_era, 1)}
	                }, "sDefaultContent": "0" },
	                
	            { "title": "NPV", className: "dm_export", "mData": "total_z", render: $.fn.dataTable.render.number( ',', '.', 1 ),
	            	"createdCell": function (td, cellData, rowData, row, col) { setStatCellColor(td, cellData, 5)}, "sDefaultContent": "0"},
	            { "title": "<i class='fa fa-bolt'></i>-$", className: "dm_export", "mData": "live_auction_value", "render": function ( data, type, row ) {
	        		return "$" + data.toFixed(0);
	            }, "sDefaultContent": ""}
	        ]
	        };
	
	if (isInitialLoad) 	{
		data_table = table_element.dataTable(config);
	} else {
		data_table = table_element.DataTable();
		data_table.destroy();
		table_element.empty();
		data_table = table_element.dataTable(config);
		data_table = table_element.DataTable();
	}

}

function setStatCellColor(td, zscore, sd){
    if ( zscore <= (-1)*(sd/2) ) {
    	$(td).css({"background-color": "rgb(255, 77, 77)", "font-weight": "bold"}) // Red 65%
    } else if ( zscore < 0 ) {
    	$(td).css({"background-color": "rgb(255, 179, 179)", "font-weight": "bold"}) // Red 85%
    } else if ( zscore < (sd/2) ) {
    	$(td).css("font-weight", "normal");
    	$(td).removeProp("background-color");
    } else if ( zscore < sd ) {
    	$(td).css({"background-color": "rgb(174, 234, 174)", "font-weight": "bold"}) // Green 80%
    } else {
    	$(td).css({"background-color": "rgb(93, 213, 93)", "font-weight": "bold"}) // Green 60%
    }
}

function getPosChartColor(value){
	
	var stylecolor;
	
	// console.log("context.value = " + JSON.stringify(value));
	// console.log("context.value-X = " + value['x']);
	  
    if ( value['x'] <= (-1)*(2.5) ) {
    	stylecolor = 'stroke: rgb(255, 0, 0);'  // Red 50%
    } else if ( value['x'] < 0 ) {
    	stylecolor = 'stroke: rgb(255, 102, 102);'  // Red 70%
    } else if ( value['x'] < 2.5 ) {
    	stylecolor = 'stroke: rgb(217, 217, 217);'  // Grey 85%
    } else if ( value['x'] < 5 ) {
    	stylecolor = 'stroke: rgb(102, 255, 102);'  // Green 70%
    } else {
    	stylecolor = 'stroke: rgb(0, 230, 0);'  // Green 45%
    }
    
    return stylecolor;
	
}

function clearPlayerInfoTab(){
	$("#lbl-playerinfoname").val("");
	$("#lbl-playerinfoname").text("[No Player Selected]");
	$("#lbl-playerinfoteam").text("");
	$("#lbl-playerinfoage").text("");
	$("#lbl-playerinfoelig").text("");
	$("#lbl-playerinfoowner").text("");
	$("#textarea-playernote").val("");
	$("#btn-playerinfosavenote").attr("disabled","disabled");
	$("#textarea-playernote").attr("disabled","disabled"); 
}

function showUndraftPlayerDialog(playergridundraftrow){
	var data_table = $('#playergrid_table').DataTable();
    var league_id = $("#league-select").find("option:selected").val();
    playerdraftrow = playergridundraftrow;
	
    BootstrapDialog.show({
    	title: 'Undraft Player',
        message: 'Are you sure you want to undraft player <b>' + playerdraftrow.full_name + '</b> from team <b>' + playerdraftrow.leagueteam_name + '</b>?',
        type: BootstrapDialog.TYPE_DEFAULT,
        buttons: [{
            label: 'Undraft Player',
            // autospin: true,
            // spinicon: "<i class='fa fa-refresh'></i>",
            cssClass: 'btn-danger',
            action: function(dialogItself){
            	
            	var originalteam_id = playerdraftrow.leagueteam_id;
            	
            	if (playerdraftrow.unknownplayer == false){

            		playerdraftrow.leagueteam_id = 0;
            		playerdraftrow.leagueteam_name = "";
            		playerdraftrow.team_roster_position = "";
            		playerdraftrow.team_player_salary = 0;
            		
            		// console.log("Undraft Player: " + playerdraftrow.full_name);
            		// console.log("Undraft Player ID: " + playerdraftrow.id);
            		// console.log("Undraft Player leagueteam_id: " + playerdraftrow.leagueteam_id);
            		// console.log("Undraft Player roster_position: " + playerdraftrow.team_roster_position);
            		// console.log("Undraft Player salary: " + playerdraftrow.team_player_salary);
            		
            		data_table.row('#' + playerdraftrow.id + '').data(playerdraftrow).draw();
            		
            		mssolutions.fbapp.draftmanager.undraftPlayer(league_id, playerdraftrow.id);
            		
            	} else {

            		mssolutions.fbapp.draftmanager.undraftUnknownPlayer(league_id, playerdraftrow.full_name);
            		
            		data_table.rows( function ( idx, data, node ) {
            	        return data.full_name == playerdraftrow.full_name ?
            	            true : false;
            	    } ).remove();

            	}

        		// Show the team info tab
        		$('#info-tabs a[href="#tab-teaminfo"]').tab('show');
        		// Set the team select to the drafting team
        		$("#team-select").val(originalteam_id);
        		updateTeamInfoTab();
        		dialogItself.close();
        		calcLiveAuctionValue();
            }
        }, {
            label: 'Cancel',
            action: function(dialogItself){
                dialogItself.close();
            }
        }]
    });
	
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
	clearPlayerInfoTab();
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
			// console.log("Loading league selector: ID-" + this.id + " VAL-" + this.league_name);
			options.append($("<option value='"+ this.id +"'/>").text(this.league_name + "(" + this.league_year + ")"));
		});
	} else {
		// console.log("League data is null");
	}

	options.append($("<option value='newleague'/>").text("Add New League..."));
}

function loadTeamSelect(data){
	
	// sort teams alphabetically after my team as first team
	var myteam = data[0];
	data.shift();
	data = data.sort(function(a, b) {
	    return a.team_name.localeCompare(b.team_name);
	});
	data.unshift(myteam);
	
	var teamfilterselect = $("#select-draftedplayerfilter");
	teamfilterselect.find('option').remove().end();
	teamfilterselect.append($("<option value='-1' selected/>").text('All Players'));
	teamfilterselect.append($("<option value='0'/>").text('Undrafted Players'));
	
	var teamselect = $("#team-select");
	teamselect.find('option').remove().end();
	if (undefined !== data){
		$.each(data, function() {
			// console.log("Loading team selector: ID-" + this.id + " VAL-" + this.team_name);
			teamselect.append($("<option value='"+ this.id +"'/>").text(this.team_name));
			teamfilterselect.append($("<option value='"+ this.id +"'/>").text(this.team_name));
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
	
	var draftteamselectunk = $("#select-draftteamunk");
	draftteamselectunk.find('option').remove().end();
	draftteamselectunk.append($("<option value='0'/>").text("--- Select Team ---"));
	if (undefined !== data){
		$.each(data, function() {
			draftteamselectunk.append($("<option value='"+ this.id +"'/>").text(this.team_name));
		});
	} else {}

}


/**
 * Update player note via the API.
 */
mssolutions.fbapp.draftmanager.updatePlayerNote = function(league_id, 
		player_projected_id, team_player_note) {
	
	// console.log("In updatePlayerNote...");
	
	gapi.client.draftapp.league.updateplayernote({
		'league_id' : league_id,
		'player_projected_id' : player_projected_id,
		'team_player_note' : team_player_note}).execute(
      function(resp) {
        if (!resp.code) { 
        	console.log("Update player note complete. League Player ID: " + resp.longdescription);
        	// $("#btn-playerinfosavenote").removeAttr("disabled");
        }
        else {
        	console.log("Failed to update player note: ", resp.code + " : " + resp.message);
        	// $("#btn-playerinfosavenote").removeAttr("disabled");
        }
      });
};

/**
 * Draft player via the API.
 */
mssolutions.fbapp.draftmanager.draftPlayer = function(league_id, league_team_id, 
		player_projected_id, team_roster_position, team_player_salary) {
	
	// console.log("In draftPlayer...");
	
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
 * Draft unknown player via the API.
 */
mssolutions.fbapp.draftmanager.draftUnknownPlayer = function(league_id, league_team_id, 
		unknown_player_name, pitcher_hitter, team_roster_position, team_player_salary) {
	
	// console.log("In draftUnknownPlayer...");
	
	gapi.client.draftapp.league.draftplayer({
		'league_id' : league_id,
		'league_team_id' : league_team_id,
		'unknownplayer' : true,
		'unknown_player_name' : unknown_player_name,
		'unknown_player_pitcher_hitter' : pitcher_hitter,
		'team_roster_position' : team_roster_position,
		'team_player_salary' : team_player_salary}).execute(
      function(resp) {
        if (!resp.code) { 
        	console.log("Draft unknown player complete. League Player ID: " + resp.longdescription);
        }
        else {
        	console.log("Failed to draft unknown player: ", resp.code + " : " + resp.message);
        }
      });
};

/**
 * Undraft player via the API.
 */
mssolutions.fbapp.draftmanager.undraftPlayer = function(league_id, player_projected_id) {
	
	// console.log("In undraftPlayer...");
	
	gapi.client.draftapp.league.undraftplayer({
		'league_id' : league_id,
		'player_projected_id' : player_projected_id}).execute(
      function(resp) {
        if (!resp.code) { 
        	console.log("Undraft player complete.");
        }
        else {
        	console.log("Failed to undraft player: ", resp.code + " : " + resp.message);
        }
      });
};

/**
 * Undraft unknown player via the API.
 */
mssolutions.fbapp.draftmanager.undraftUnknownPlayer = function(league_id, unknown_player_name) {
	
	// console.log("In undraftUnknownPlayer...");
	
	gapi.client.draftapp.league.undraftplayer({
		'league_id' : league_id,
		'unknownplayer' : true,
		'unknown_player_name' : unknown_player_name}).execute(
      function(resp) {
        if (!resp.code) { 
        	console.log("Undraft player complete.");
        }
        else {
        	console.log("Failed to undraft player: ", resp.code + " : " + resp.message);
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
        	mssolutions.fbapp.draftmanager.loadLeagueList(resp.longdescription);
        	// $('#league-select').val(resp.longdescription);
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
	// console.log("getLeagueRoster, leagueid: " + leagueid);
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
        	
        	dm_teamrostercounts = counts;
        	dm_rescount = counts["RES"];
        	// console.log("Roster Counts: " + JSON.stringify(dm_teamrostercounts));
        	// console.log("RES Count: " + dm_rescount);
        	
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
	// console.log("getLeagueTeams, leagueid: " + leagueid);
	gapi.client.draftapp.league.getleagueteams({
		'id' : leagueid}).execute(
      function(resp) {
        if (!resp.code) { 
        	console.log("League teams get complete.");
        	dm_globalteamlist = resp.items;
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
	// console.log("getLeaguePlayerData, leagueid: " + leagueid);
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
	// console.log("Deleting League id..." + leagueid);
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
mssolutions.fbapp.draftmanager.loadLeagueList = function(leagueid) {

	gapi.client.draftapp.league.getuserleagues().execute(
      function(resp) {
        if (!resp.code) { 
        	loadLeagueSelector(resp.items);
        	if (leagueid != null) $('#league-select').val(leagueid);
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