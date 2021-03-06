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

// timer start for getting player grid
var timerLoadPlayerGridStart;

// Saves the selected player from the player grid for draft update
var playerdraftrow;

//Saves the selected player from the player grid for update
var playerselectedrow;

// Saves the selected league team from the league management section
var leagueteamselectedrow

//Saves the selected player from the player custom position grid
var playercustomposrow;

// A blank template of a team roster (includes positions)
var teamrostertemplate;
//A blank template of a team roster (split)
var dm_teamrostertemplate_1;
var dm_teamrostertemplate_2;

// The teamrostertemplate converted into just a list of roster positions and counts.
var dm_teamrostercounts;

// Current team max bid
var dm_teammaxbid = 0;

// List of team roster positions and counts of open slots:
var dm_teamrostercounts_live = [];

// Count of RES and ML spots in teamrostercounts
var dm_rescount;
var dm_mlcount

// List of teams from getleagueteams
var dm_globalteamlist;

// Regex definition for filtering undrafted vs all players
var regex_drafted = '';

// Current player grid filter
var current_player_filter;

// League definition from server
var dm_leagueinfo;
//var dm_leagueinfo = {
//		cat_hitter_avg: false,
//		cat_hitter_hr: false,
//		cat_hitter_sb: false,
//		cat_hitter_r: false,
//		cat_hitter_rbi: false,
//		cat_hitter_obp: false
//	};
//dm_leagueinfo.cat_hitter_avg = false;
//dm_leagueinfo.cat_hitter_hr = false;
//dm_leagueinfo.cat_hitter_sb = false;
//dm_leagueinfo.cat_hitter_r = false;
//dm_leagueinfo.cat_hitter_rbi = false;
//dm_leagueinfo.cat_hitter_obp = false;

// Array of estimated team standings data
var dm_teamstandings;

// Filtered player data by position
var dm_filtered_data_c;
var dm_filtered_data_1b;
var dm_filtered_data_2b;
var dm_filtered_data_ss;
var dm_filtered_data_3b;
var dm_filtered_data_of;
var dm_filtered_data_sp;
var dm_filtered_data_rp;
// var dm_filtered_data_all;
var dm_filtered_data_mi;
var dm_filtered_data_ci;

// Indicator whether the tab should be updated on select
var update_positional_tab = true;
var update_standings_tab = true;

// Alert function
bootstrap_alert = function () {}

bootstrap_alert.lostconnection = function () {
	bootstrap_alert.warning('<strong>Connection Lost!</strong>  Please refresh page.', 'danger', 4000);
}

bootstrap_alert.warning = function (message, alert, timeout) {
	
	 if( !$('#floating_alert').length )         // if element does not exist
	 {
		// available: success, info, warning, danger
	    $('<div id="floating_alert" class="alert alert-' + alert 
	    		+ ' fade in"><button type="button" class="close" data-dismiss="alert" aria-hidden="true">×</button>' 
	    		+ message + '&nbsp;&nbsp;</div>').prependTo('#title-container');
	 }

    // In case an alert timeout is needed
    /*
    setTimeout(function () {
        $(".alert").alert('close');
    }, timeout);
	*/
}

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
			$("#league-top-container").hide();
		} else {

			loadLeagueContent(selected);
		}
	  });
	  
	  $('#team-select').on('change', function(){
	    var selected = $(this).find("option:selected").text();
	  });
	  
	    $( "#sortable" )
	    	.sortable({ handle: ".handle" })
	    	.selectable({ 
	    		filter: "li"
	    		,cancel: ".handle" 
	    		,selected: function(event, ui) { 
	    	        $(ui.selected).addClass("ui-selected").siblings().removeClass("ui-selected");    
	    	        console.log("playerid: " + ui.selected.id);
	    	        selectPlayerInPlayerGridTable(ui.selected.id);
	    	    }  
	    	});
	    $( "#sortable" ).disableSelection();

});


$(document).ready(function()
{

	// Tab change event
	$("a[href='#maintab2']").on('hidden.bs.tab', function (e) {

		$(".dm_poschart").empty();

	});
	
	// Tab change event for standings tab
	$("a[href='#maintab3']").on('shown.bs.tab', function (e) {
		if (update_standings_tab){
			console.log('Updating standings tab...');
			calcStandings(); 
			update_standings_tab = false;
		} else{
			console.log('No update to standings tab needed.');
		}

	});
	
	// Tab change event for team overview
	$("a[href='#maintab4']").on('shown.bs.tab', function (e) {	
		
		// Calc and load team overview list
		// Also calculates team open roster slots
		calcTeamOvwList();
		
	});

	
	// Tab change event for positional analysis
	$("a[href='#maintab2']").on('shown.bs.tab', function (e) {
		
		if (update_positional_tab){
			
			console.log('Updating Positional Tab...');
		
			var player_table = $('#playergrid_table').DataTable();
			var data = player_table.data();
			var filtered_data = $.grep(data, function(v) {
				var isdrafted;
				if (v.leagueteam_name == null) isdrafted = false;
				else if (v.leagueteam_name.trim().length < 1) isdrafted = false;
				else isdrafted = true;
			    return v.unknownplayer == false && isdrafted == false;
			});
			
			// Sort by descending Z
			filtered_data.sort(function(a, b) {
			    return parseFloat(b.total_z) - parseFloat(a.total_z);
			});
			
			// console.log("filtered_data: " + JSON.stringify(filtered_data));
			var slice_size = 10
			var ovw_size = 10
			
			dm_filtered_data_c = $.grep(filtered_data, function(v) {
			    return v.custom_position.indexOf("C") > -1;}).slice(0,slice_size);
			dm_filtered_data_1b = $.grep(filtered_data, function(v) {
			    return v.custom_position.indexOf("1B") > -1;}).slice(0,slice_size);
			dm_filtered_data_2b = $.grep(filtered_data, function(v) {
			    return v.custom_position.indexOf("2B") > -1;}).slice(0,slice_size);
			dm_filtered_data_ss = $.grep(filtered_data, function(v) {
			    return v.custom_position.indexOf("SS") > -1;}).slice(0,slice_size);
			dm_filtered_data_3b = $.grep(filtered_data, function(v) {
			    return v.custom_position.indexOf("3B") > -1;}).slice(0,slice_size);
			dm_filtered_data_of = $.grep(filtered_data, function(v) {
			    return v.custom_position.indexOf("OF") > -1;}).slice(0,slice_size);
			dm_filtered_data_sp = $.grep(filtered_data, function(v) {
			    return v.custom_position.indexOf("SP") > -1;}).slice(0,slice_size);
			dm_filtered_data_rp = $.grep(filtered_data, function(v) {
			    return v.custom_position.indexOf("RP") > -1;}).slice(0,slice_size);
			// dm_filtered_data_all = filtered_data.slice(0,slice_size);
	
			dm_filtered_data_mi = $.grep(filtered_data, function(v) {
			    return v.custom_position.indexOf("2B") > -1 || v.custom_position.indexOf("SS") > -1;
			}).slice(0,slice_size);
			dm_filtered_data_ci = $.grep(filtered_data, function(v) {
			    return v.custom_position.indexOf("3B") > -1 || v.custom_position.indexOf("1B") > -1;
			}).slice(0,slice_size);
			
			var ovw_data = [];
			
			var element_C = [];
			var element_1B = [];
			var element_2B = [];
			var element_SS = [];
			var element_3B = [];
			var element_OF = [];
			var element_RP = [];
			var element_SP = [];
			var element_MI = [];
			var element_CI = [];
			element_C["position"] = "C";
			element_1B["position"] = "1B";
			element_2B["position"] = "2B";
			element_SS["position"] = "SS";
			element_3B["position"] = "3B";
			element_OF["position"] = "OF";
			element_RP["position"] = "RP";
			element_SP["position"] = "SP";
			element_MI["position"] = "MI";
			element_CI["position"] = "CI";
			
			jQuery.each(dm_filtered_data_c, function(index, item) {
				var e = "total_z_" + (index + 1).toString();
				element_C[e] = item.total_z;
				if (index >= 10) return false;	 
			});
			jQuery.each(dm_filtered_data_1b, function(index, item) {
				var e = "total_z_" + (index + 1).toString();
				element_1B[e] = item.total_z;
				if (index >= 10) return false;	 
			});
			jQuery.each(dm_filtered_data_2b, function(index, item) {
				var e = "total_z_" + (index + 1).toString();
				element_2B[e] = item.total_z;
				if (index >= 10) return false;	 
			});
			jQuery.each(dm_filtered_data_ss, function(index, item) {
				var e = "total_z_" + (index + 1).toString();
				element_SS[e] = item.total_z;
				if (index >= 10) return false;	 
			});
			jQuery.each(dm_filtered_data_3b, function(index, item) {
				var e = "total_z_" + (index + 1).toString();
				element_3B[e] = item.total_z;
				if (index >= 10) return false;	 
			});
			jQuery.each(dm_filtered_data_of, function(index, item) {
				var e = "total_z_" + (index + 1).toString();
				element_OF[e] = item.total_z;
				if (index >= 10) return false;	 
			});
			jQuery.each(dm_filtered_data_rp, function(index, item) {
				var e = "total_z_" + (index + 1).toString();
				element_RP[e] = item.total_z;
				if (index >= 10) return false;	 
			});
			jQuery.each(dm_filtered_data_sp, function(index, item) {
				var e = "total_z_" + (index + 1).toString();
				element_SP[e] = item.total_z;
				if (index >= 10) return false;	 
			});
			jQuery.each(dm_filtered_data_mi, function(index, item) {
				var e = "total_z_" + (index + 1).toString();
				element_MI[e] = item.total_z;
				if (index >= 10) return false;	 
			});
			jQuery.each(dm_filtered_data_ci, function(index, item) {
				var e = "total_z_" + (index + 1).toString();
				element_CI[e] = item.total_z;
				if (index >= 10) return false;	 
			});
			
			// Calc and load team overview list
			// Also calculates team open roster slots
			calcTeamOvwList();
			
			element_C["open_slots"] = dm_teamrostercounts_live.open_slots_c;
			element_1B["open_slots"] = dm_teamrostercounts_live.open_slots_1b;
			element_2B["open_slots"] = dm_teamrostercounts_live.open_slots_2b;
			element_SS["open_slots"] = dm_teamrostercounts_live.open_slots_ss;
			element_3B["open_slots"] = dm_teamrostercounts_live.open_slots_3b;
			element_OF["open_slots"] = dm_teamrostercounts_live.open_slots_of;
			element_RP["open_slots"] = dm_teamrostercounts_live.open_slots_p + " (P)";
			element_SP["open_slots"] = dm_teamrostercounts_live.open_slots_p + " (P)";
			element_MI["open_slots"] = dm_teamrostercounts_live.open_slots_mi;
			element_CI["open_slots"] = dm_teamrostercounts_live.open_slots_ci;
			
			ovw_data.push(element_C);
			ovw_data.push(element_1B);
			ovw_data.push(element_3B);
			ovw_data.push(element_CI);
			ovw_data.push(element_2B);
			ovw_data.push(element_SS);
			ovw_data.push(element_MI); 
			ovw_data.push(element_OF);
			ovw_data.push(element_SP);
			ovw_data.push(element_RP);
	
			loadPositionalAnlaysisTable(ovw_data, false);
	
			loadPositionalTable(dm_filtered_data_c, $("#pos_c_table"), false, true, "#chart-c");
			
			/*
			loadPositionalTable(filtered_data_1b, $("#pos_1b_table"), false, true, "#chart-1b");
			loadPositionalTable(filtered_data_2b, $("#pos_2b_table"), false, true, "#chart-2b");
			loadPositionalTable(filtered_data_ss, $("#pos_ss_table"), false, true, "#chart-ss");
			loadPositionalTable(filtered_data_3b, $("#pos_3b_table"), false, true, "#chart-3b");
			loadPositionalTable(filtered_data_of, $("#pos_of_table"), false, true, "#chart-of");
			loadPositionalTable(filtered_data_sp, $("#pos_sp_table"), false, false, "#chart-sp");
			loadPositionalTable(filtered_data_rp, $("#pos_rp_table"), false, false, "#chart-rp");
			*/
			
			// dm_filtered_data_all = null;
			
			player_table = null;
			data = null;
			filtered_data = null;
			
			update_positional_tab = false;
			
		} else {
			console.log('No update to positional tab needed.');
		}

	});
	
	// Tab change event
	$("a[href='#tab-teaminfo']").on('shown.bs.tab', function (e) { $('#section-teaminfo').show(); });
	
	$("a[href='#tab-playerinfo']").on('shown.bs.tab', function (e) { $('#section-teaminfo').hide(); });
	
	$("#btn-detailqueue").click(function(ev){  updatePlayerQueue(); })
	
	$("#btn-detaildraftplayer").click(function(ev){ sendPlayerToBlock(); })
	
	//  Part of original draft player modal
    $("#select-draftteam").change(function(e){

    	if ($(this).val() != null){
    		var text = $(this).find("option:selected").text();
    		var pos = text.lastIndexOf("(");
        	$('#lbl-draftprevteam').text(text.substring(0,pos-2));
        	$('#lbl-draftprevpos').text("[none]");
        	// $("#btn-draftposmoveup").hide();
        	// $("#btn-draftposmovedown").hide();
        	// load pos selector with "NONE", because it's a draft edit, but no specific position
        	loadDraftPlayerPosSelector("NONE");
        	resetDraftPanel();
        	// updateSelectPosTable();
        	// $("#table-selectdraftposition").show();
    	} else {
        	$('#lbl-draftprevteam').text("[none]");
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
    
    $("#select-ontheblock-draftteam").change(function(e){
    	var posselector = $("#select-ontheblock-draftposition");
    	var teamselected = false;
    	var posselected = false
    	
    	if (($(this).val() != null) && ($(this).val() != 0)) teamselected = true;
    	
		var text = $(this).find("option:selected").text();
		var pos1 = text.lastIndexOf("(");
		var pos2 = text.lastIndexOf(")");
		dm_teammaxbid = parseInt(text.substring(pos1 + 2,pos2));
    	
    	if (teamselected){
        	loadDraftPlayerPosSelector();
    	} else {
    		posselector.find('option').remove().end();
    		posselector.attr("disabled","disabled");
    	}
    	
    	posselector = $("#select-ontheblock-draftposition");
    	
    	if ((posselector.val() != null) && 
    			(posselector.val() != 0)) posselected = true;
    	
    	// console.log("posselected = " + posselected);
    	// console.log("pos value = " + posselector.val());
    	// console.log("amt value = " + $("#select-ontheblock-draftamt").val());
        
        if ((!teamselected) || (!posselected) ||
        		!($("#select-ontheblock-draftamt").val() >= 0)){
        	// console.log("disabling draft button");
        	$("#btn-ontheblock-draftplayer").attr("disabled","disabled");
        } else {
        	// console.log("enabling draft button");
        	$("#btn-ontheblock-draftplayer").removeAttr("disabled");
        }
        
        // ADD: update team select for the new team
        $('select[id=team-select]').val($(this).val());
        // $('#team-select').selectpicker('refresh');
        updateTeamInfoTab();
        checkAmtSelector();
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
    
    //  Part of original draft player modal
    $("#select-draftamt").change(function(e){
    	// var selectedcells = $('#table-selectdraftposition').DataTable().cells( { selected: true } ).data();
    	// var selectedname;
    	// var pos_selected;
    	
    	/*
    	if (selectedcells.length > 0){
    		pos_selected = false;
    		if (selectedcells[0].startsWith("[")) pos_selected = true;
    	} else pos_selected = false;
    	*/

    	if ($(this).val() != null){
	        $('#lbl-draftprevamt').text($(this).find("option:selected").text());
    	} else {
        	$('#lbl-draftprevamt').text("[none]");
        }
        
        if (($("#select-draftteam").val() == null) ||
        		($("#select-draftposition").val() == null) ){
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

    $("#select-ontheblock-draftposition").change(function(e){
    	
    	var teamselector = $("#select-ontheblock-draftteam");
    	var amtselector = $("#select-ontheblock-draftamt");
    	var teamselected = false;
    	var posselected = false;
    	var amtselected = false;
    	
    	if (($(this).val() != null) && ($(this).val() != 0)) posselected = true;
    	if (amtselector.val().length > 0) {	amtselected = true;}
    	if ((teamselector.val() != null) && 
    			(teamselector.val() != 0)) teamselected = true;
    	
//    	console.log("teamselected = " + teamselected);
//    	console.log("team value = " + teamselector.val())
//    	console.log("posselected = " + posselected);
//    	console.log("pos value = " + $(this).val());
//    	console.log("amtselected = " + amtselected);
//    	console.log("amt value = " + amtselector.val());
    	
    	checkAmtSelector();
        
    	// If position selector is set to "RES" then disable the amount selector,
    	// set it to 0, and enable the draft button.
    	/*
    	if ($(this).val().toLowerCase() == "res"){
    		updateOTBforResPosSelect();
    	} else if (!teamselected || !posselected || !amtselected){
        	$("#btn-ontheblock-draftplayer").attr("disabled","disabled");
        	$("#select-ontheblock-draftamt").removeAttr("disabled");
        	$('#btn-draftamt-plus').removeAttr("disabled");
        	$('#btn-draftamt-minus').removeAttr("disabled");
        	$("#select-ontheblock-draftamt").css({'background-color' : '#ffffff'});
        	checkAmtSelector();
        } else {
        	$("#btn-ontheblock-draftplayer").removeAttr("disabled");
        	$("#select-ontheblock-draftamt").removeAttr("disabled");
        	$('#btn-draftamt-plus').removeAttr("disabled");
        	$('#btn-draftamt-minus').removeAttr("disabled");
        	$("#select-ontheblock-draftamt").css({'background-color' : '#ffffff'});
        	checkAmtSelector();
        }
		*/
    });

    $("#select-ontheblock-draftamt").keyup(function(e){
    	var amtval = $(this).val();
    	var amtval2 = parseInt($(this).val());
    	var isNum = false;
    	
    	if (Number.isInteger(Number(amtval))) {
    		if (amtval > 0) isNum = true;
    	}
    	
    	if (!isNum) $(this).val("");
    	
    	// console.log("amt value = " + amtval);
    	// console.log("dm_teammaxbid = " + dm_teammaxbid);
    	
//    	if (amtval2 > dm_teammaxbid) {
//    		$(this).css({'background-color' : '#ffe6e6'});
//    		$("#text-draft-amt-warning").show();
//    	}
//    	else {
//    		$(this).css({'background-color' : '#ffffff'});
//    		$("#text-draft-amt-warning").hide();
//    	}
    	
    	checkAmtSelector();
    });
    
	$('#btn-draftamt-plus').click(function() 
	{
		var amtselector = $("#select-ontheblock-draftamt");
		var amtvalue = amtselector.val();
		
		if (amtvalue == "") amtselector.val("1");
		else {
			amtselector.val(Number(amtvalue) + 1);
		}
		
		checkAmtSelector();
	});
	
	$('#btn-draftamt-minus').click(function() 
	{
		var amtselector = $("#select-ontheblock-draftamt");
		var amtvalue = amtselector.val();
		
		if (amtvalue == "") amtselector.val("1");
		else if (amtvalue <= 1) amtselector.val("1");
		else {
			amtselector.val(Number(amtvalue) - 1);
		}
		
		checkAmtSelector();
	});
    
    $("#select-draftposition").change(function(e){
    	if ($(this).val() != null){
	        $('#lbl-draftprevpos').text($(this).find("option:selected").text().split(" ")[0]);
    	} else {
        	$('#lbl-draftprevpos').text("[none]");
        }
        
        if (($("#select-draftteam").val() == "0") ||
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
		
		// console.log("player note textarea: " + playernote);

		playerselectedrow.team_player_note = playernote;
		
		// console.log("Note Player: " + playerselectedrow.full_name);
		// console.log("Note Player ID: " + playerselectedrow.id);
		// console.log("Note Player team_player_note: " + playerselectedrow.team_player_note);
		
		playertable.row('#' + playerselectedrow.id + '').data(playerselectedrow).draw();
		
		// mssolutions.fbapp.draftmanager.updatePlayerNote(league_id, playerid, playernote);
		mssolutions.fbapp.draftmanager.updatePlayerInfo(playerselectedrow);
	});

	// Button to save custom position for a player
	$('#btn-save-custpos').click(function() 
		{
		var customposstring = []; 
		var league_id = $("#league-select").find("option:selected").val();
		var playerid = $("#lbl-custompositionplayername").val();
		var playertable = $('#playergrid_table').DataTable();
		var playercustpostable = $('#customplayerposition_table').DataTable();
		var flag = false;
		
		$('input', $('#form-custpos')).each(function () {
			if ($(this).prop('checked')){
				// console.log("Positions: " +  $(this).val()); 
				if (!$(this).prop('disabled')) flag = true;
				customposstring.push($(this).val().toUpperCase());
			}
				
		});
		
		// console.log("Position String: " +  customposstring); 
		playercustomposrow.custom_position_flag = flag;
		if (flag){
			playercustomposrow.custom_position = customposstring.toString();
		} else {
			playercustomposrow.custom_position = playercustomposrow.player_position;
		}

		playertable.row('#' + playercustomposrow.id + '').data(playercustomposrow).draw();
		playercustpostable.row('#' + playercustomposrow.id + '').data(playercustomposrow).draw();
		mssolutions.fbapp.draftmanager.updatePlayerInfo(playercustomposrow);
		
		calcLiveAuctionValue();
		
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
	
	$('#btn-deleteTeamFromLeague').click(function() 
			{
			var selectedtext = leagueteamselectedrow.team_name;
			
	        BootstrapDialog.show({
	          	type: 'type-default',
	              title: 'Confirm Delete Team From League',
	              message: 'Are you sure you want to delete team ' + selectedtext + '?',
	              spinicon: 'fa fa-refresh',
	              buttons: [{
	                  id: 'btn-confirm-delete-team',   
	                  icon: 'fa fa-trash',       
	                  cssClass: 'btn-danger', 
	                  autospin: true,
	                  label: 'Delete',
	                  action: function(dialog) {
	                	  $("#btn-confirm-delete-team").prop("disabled",true);
	                	var teamid = leagueteamselectedrow.id;
	                	var leagueid = $("#league-select").find("option:selected").val();
	                	console.log("Selected team id: " + teamid);
	                	mssolutions.fbapp.draftmanager.deleteTeamFromLeague(teamid, leagueid);
	                  }
	              }, {
	                  label: 'Cancel',
	                  action: function(dialog) {
	                  	dialog.close();
	                  }
	              }]
	          });
		    	
			});
	
	$('#btn-addTeamToLeague').click(function() 
			{
			// var selectedtext = leagueteamselectedrow.team_name;
			
	        BootstrapDialog.show({
	          	type: 'type-default',
	              title: 'Add a Team to League',
	              message: $('<label>Team Name: </label><input class="form-control" id="input-teamname" placeholder="Team Name">' +
	            		  '<label>Team Owner: </label><input class="form-control" id="input-teamowner" placeholder="Team Owner">'),
	              spinicon: 'fa fa-refresh',
	              buttons: [{
	                  id: 'btn-confirm-add-team',   
	                  icon: 'fa fa-trash',       
	                  cssClass: 'btn-danger', 
	                  autospin: true,
	                  label: 'Add Team',
	                  action: function(dialog) {
	                	  $("#btn-confirm-add-team").prop("disabled",true);
	                	var teamname = $("#input-teamname").val();
	                	var teamowner = $("#input-teamowner").val();
	                	var leagueid = $("#league-select").find("option:selected").val();
	                	console.log("Entered team name: " + teamname);
	                	console.log("Entered team owner: " + teamowner);
	                	mssolutions.fbapp.draftmanager.addTeamToLeague(teamname, teamowner, leagueid);
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
	    
		// Show category columns  used by league
	    // var columns_in = table.columns('.dm_league_cat_true .dm_stat');
	    var columns_out = table.columns('.dm_league_cat_false');
	    // columns_in.visible(true);
	    columns_out.visible(false);
	});
    $('#btn-gridviewz').click(function() {
	    $(this).addClass('active');
	    $(this).siblings().removeClass('active');
	    
	    var table = $('#playergrid_table').DataTable();
	    var columns_stat = table.columns('.dm_stat');
	    var columns_z = table.columns('.dm_zscore');
	    columns_stat.visible(false);
	    columns_z.visible(true);
	    
		// Show category columns  used by league
	    // var columns_in = table.columns('.dm_league_cat_true .dm_stat');
	    var columns_out = table.columns('.dm_league_cat_false');
	    // columns_in.visible(true);
	    columns_out.visible(false);
	
	});
    
	
	$('#btn-allplayers').click(function() 
	{
	    if ($('#select-draftedplayerfilter').val() == '0') regex_drafted = '(^$)|(\s+$)'; 
	    else if ($('#select-draftedplayerfilter').val() == '-1') regex_drafted = ''; 
	    else regex_drafted = $('#select-draftedplayerfilter').find("option:selected").text();
		$('#playergrid_table').DataTable().search( '' ).columns().search( '' );
		// console.log('regex_drafted = ' + regex_drafted);
		$('#playergrid_table').DataTable().columns( 39 ).search( false );			// Filter unknown player
		$('#playergrid_table').DataTable().columns( 34 ).search( regex_drafted , true ).draw();			// Filter league team name
	});
	$('#btn-pitchers').click(function() { filterPlayerType('P', true); });
	$('#btn-hitters').click(function() { filterPlayerType('H', true); });
	$('#btn-c').click(function() { filterPlayerPosition('C', true); });
	$('#btn-1b').click(function() { filterPlayerPosition('1B', true); });
	$('#btn-2b').click(function() { filterPlayerPosition('2B', true); });
	$('#btn-ss').click(function() { filterPlayerPosition('SS', true); });
	$('#btn-3b').click(function() { filterPlayerPosition('3B', true); });
	$('#btn-mi').click(function() { filterPlayerPosition('2B|SS', true); });
	$('#btn-ci').click(function() { filterPlayerPosition('1B|3B', true); });
	$('#btn-of').click(function() { filterPlayerPosition('OF', true); });
	$('#btn-rp').click(function() { filterPlayerPosition('RP', true);	});
	$('#btn-sp').click(function() { filterPlayerPosition('SP', true); });
    $('#select-draftedplayerfilter').change(function() {
        if ($(this).val() == '0') regex_drafted = '(^$)|(\s+$)'; 
        else if ($(this).val() == '-1') regex_drafted = ''; 
        else regex_drafted = $(this).find("option:selected").text();
        // console.log('regex_drafted = ' + regex_drafted);
        $('#playergrid_table').DataTable().columns( 39 ).search( false );				// Filter unknown player 
        $('#playergrid_table').DataTable().columns( 34 ).search( regex_drafted , true ).draw();		// Filter league team name
      });
    $('#btn-fav').click(function() { 
    	if ($('#btn-fav').hasClass('active')){
    		$('#playergrid_table').DataTable().search( '' ).columns().search( '' );
    		getCurrentFilters();
    		$('#playergrid_table').DataTable().draw();
    		$('#btn-fav').removeClass('active');
    	} else {
    		$('#btn-fav').addClass('active');
    		$('#playergrid_table').DataTable().columns( 50 ).search( true ).draw();		// Filter favorite player
    	}
    });
	
    $('#btn-draftunknownplayermodal').click(function() 
	{
    	resetDraftUnknownPlayerModal();
        $("#draftplayerunk-modal").modal("show");
	});  
      
	$('#btn-ontheblock-draftplayer').click(function() 
	{
		
    	var t0 = performance.now();
    	
    	// $("#btn-otb-text-spinner").show();
    	// $("#btn-otb-text").hide();
		
		var league_id = $("#league-select").find("option:selected").val();
		var playertable = $('#playergrid_table').DataTable();
		var playerid = $("#header-ontheblock-draftplayer").val();
		var teamid = $("#select-ontheblock-draftteam").find("option:selected").val();
		// var teamname = $("#select-ontheblock-draftteam").find("option:selected").text();
		// console.log("Check getLeagueTeamName: " + getLeagueTeamName(teamid));
		var teamname = getLeagueTeamName(teamid);
		// console.log("Check teamname: " + teamname);
		var position = $("#select-ontheblock-draftposition").find("option:selected").val();
		// var position = selectedposrows[0].position
		var amount = $("#select-ontheblock-draftamt").val();
		
		playerdraftrow = playertable.row('#' + playerid).data();
		
//		var row = playertable.row('#' + playerid);
		// playerdraftrow.full_name = $("#header-ontheblock-draftplayer").text();
		// playerdraftrow.id = playerid;
		playerdraftrow.leagueteam_id = teamid;
		playerdraftrow.leagueteam_name = teamname;
		// console.log("Check playerdraftrow.leagueteam_name: " + playerdraftrow.leagueteam_name);
		playerdraftrow.team_roster_position = position;
		playerdraftrow.team_player_salary = amount;
		
//		 console.log("Draft Player: " + playerdraftrow.full_name);
//		 console.log("Draft Player ID: " + playerdraftrow.id);
//		 console.log("Draft Player leagueteam_id: " + playerdraftrow.leagueteam_id);
//		 console.log("Draft Player roster_position: " + playerdraftrow.team_roster_position);
//		 console.log("Draft Player salary: " + playerdraftrow.team_player_salary);
//		 console.log("Draft Player league team: " + playerdraftrow.leagueteam_name);
		
		if (playerdraftrow.favorite_flag == true) {
			playerdraftrow.favorite_flag = false;
			$("#player_queue_panel_body ul #" + playerdraftrow.id).remove();
			// mssolutions.fbapp.draftmanager.updatePlayerInfo(playerdraftrow);
		}
		
		playertable.row('#' + playerdraftrow.id + '').data(playerdraftrow).draw();
		
		var t1 = performance.now();

		// Draft player
		mssolutions.fbapp.draftmanager.draftPlayer(league_id, teamid, playerdraftrow.id, 
				playerdraftrow.team_roster_position, playerdraftrow.team_player_salary, playerdraftrow.favorite_flag);
		
		var t2 = performance.now();
		 
		resetDraftPanel();
       
		// Set the team select to the drafting team
		$("#team-select").val(teamid);
		updateTeamInfoTab();

		// calcLiveAuctionValue();
		
		// If the drafted player is still in the player info panel,
		// update the panel with latest information (ie drafted status)
		updatePlayerInfoPanel(playerselectedrow);
		
		// Update team information in team overview list
		// calcTeamOvwList();
		
//    	console.log("Total Draft Time: " + (t6 - t0)/1000 + " seconds.");
//    	console.log("	1. Pre Draft: " + (t1 - t0)/1000 + " seconds.");
//    	console.log("	2. Draft API Call: " + (t2 - t1)/1000 + " seconds.");
//    	console.log("	3. resetDraftPanel & updateTeamInfoTab: " + (t3 - t2)/1000 + " seconds.");
//    	console.log("	4. calcLiveAuctionValue: " + (t4 - t3)/1000 + " seconds.");
//    	console.log("	5. updatePlayerInfoPanel: " + (t5 - t4)/1000 + " seconds.");
//    	console.log("	6. calcTeamOvwList: " + (t6 - t5)/1000 + " seconds.");
		
    	// $("#btn-otb-text-spinner").hide();
    	// $("#btn-otb-text").show();

	});
	
	$('#btn-draftplayer').click(function() 
	{
		var league_id = $("#league-select").find("option:selected").val();
		var playertable = $('#playergrid_table').DataTable();
		var playerid = $("#header-draftplayer").val();
		var teamid = $("#select-draftteam").find("option:selected").val();
		// var teamname = $("#select-draftteam").find("option:selected").text();
		var teamname = getLeagueTeamName(teamid);
		var position = $("#select-draftposition").find("option:selected").val();
		// var position = selectedposrows[0].position
		var amount = $("#select-draftamt").find("option:selected").val();
		
//				var row = playertable.row('#' + playerid);
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
				playerdraftrow.team_roster_position, playerdraftrow.team_player_salary, playerdraftrow.favorite_flag);
		
		// resetDraftPanel();
		
		$('#draftplayer-modal').modal('hide');
	
		// Show the team info tab
		$('#info-tabs a[href="#tab-teaminfo"]').tab('show');
		// Set the team select to the drafting team
		$("#team-select").val(teamid);
		updateTeamInfoTab();
		
		var teamselected = false;
    	if (($("#select-ontheblock-draftteam").val() != null) && ($("#select-ontheblock-draftteam").val() != 0)) teamselected = true;
		if (teamselected)  {loadDraftPlayerPosSelector()}
		
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
		// var teamname = $("#select-draftteamunk").find("option:selected").text();
		var teamname = getLeagueTeamName(teamid);
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

	// Undraft button in player detail panel
	$('#btn-detailownedplayer').click(function () {
        showUndraftPlayerDialog(playerselectedrow);
       
    } );
	
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
		
		var teamid = $('#team-select').find("option:selected").val();
		var teamname = $('#team-select').find("option:selected").text();
		
		// loadTeamSelect(dm_globalteamlist);
		loadEditPlayerTeamSelect(dm_globalteamlist);
		
		$("#team-select").val(teamid);
		var roster_table = $('#teamroster_table').DataTable();
		var roster_row = roster_table.rows('.selected').data()[0];

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
            $("#header-draftplayer").text("Update Player: " + data.full_name + " (" + data.team + ")");
            $("#header-draftplayer").val(data.id);
            $("#lbl-draftprevplayer").text(data.full_name + " (" + data.team + ")");
            $("#select-draftteam").val(teamid);
            
            loadDraftPlayerAmtSelector();
            loadDraftPlayerPosSelector(roster_row.position);
            // resetDraftPanel();
        	
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
			if ($("#cat-hitter-obp").is(':checked')) hitting = hitting + "OBP, ";
			hitting = hitting.slice(0, -2);
			
			if ($("#cat-pitcher-w").is(':checked')) pitching = pitching + "W, ";
			if ($("#cat-pitcher-sv").is(':checked')) pitching = pitching + "SV, ";
			if ($("#cat-pitcher-k").is(':checked')) pitching = pitching + "K, ";
			if ($("#cat-pitcher-era").is(':checked')) pitching = pitching + "ERA, ";
			if ($("#cat-pitcher-whip").is(':checked')) pitching = pitching + "WHIP, ";
			if ($("#cat-pitcher-holds").is(':checked')) pitching = pitching + "Holds, ";
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
		league["cat_hitter_rbi"] = $("#cat-hitter-rbi").is(':checked');
		league["cat_hitter_r"] = $("#cat-hitter-r").is(':checked');
		league["cat_hitter_sb"] = $("#cat-hitter-sb").is(':checked');
		league["cat_hitter_avg"] = $("#cat-hitter-avg").is(':checked');
		league["cat_pitcher_wins"] = $("#cat-pitcher-w").is(':checked');
		league["cat_pitcher_saves"] = $("#cat-pitcher-sv").is(':checked');
		league["cat_pitcher_so"] = $("#cat-pitcher-k").is(':checked');
		league["cat_pitcher_era"] = $("#cat-pitcher-era").is(':checked');
		league["cat_pitcher_whip"] = $("#cat-pitcher-whip").is(':checked');
		
		// New Categories: OBP and Holds
		league["cat_hitter_obp"] = $("#cat-hitter-obp").is(':checked');
		league["cat_pitcher_holds"] = $("#cat-pitcher-holds").is(':checked');
		
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
		// profile["projected_year"] = (new Date).getFullYear();
		profile["projected_year"] = $("#select-leagueyear").find("option:selected").val();

		leaguecontainer["league"] = league;
		leaguecontainer["league_teams"] = teamlist;
		leaguecontainer["profile"] = profile;

		// console.log("leaguecontainer: " + JSON.stringify(leaguecontainer));
		
		mssolutions.fbapp.draftmanager.createandupdateLeague(leaguecontainer);
		
	});

	loadPositionalAnlaysisTable(null, true);
    loadTeamPreviewTable(null, true);
	loadTeamTable(null, true);
	// loadPlayerGridTable(null, true);
	loadTeamRosterTable(null, true);
	// loadSelectPositionTable(null, true);
	loadTeamOvwRosterTable(true);
	loadDraftPlayerAmtSelector();
	
	loadTeamOvwTable(null, true);
	loadLeagueTeamMgmtTable(null, true);
	
	// loadLeagueStandingsTable(null, true);

	/*
	loadLeagueStandingsCatTable(null, true, "#league_standings_table_avg","AVG","team_hitter_avg");
	loadLeagueStandingsCatTable(null, true, "#league_standings_table_hr","HR","team_hitter_hr");
	loadLeagueStandingsCatTable(null, true, "#league_standings_table_sb","SB","team_hitter_sb");
	loadLeagueStandingsCatTable(null, true, "#league_standings_table_r","Runs","team_hitter_runs");
	loadLeagueStandingsCatTable(null, true, "#league_standings_table_rbi","RBI","team_hitter_rbi");
	loadLeagueStandingsCatTable(null, true, "#league_standings_table_obp","OBP","team_hitter_obp");
	
	loadLeagueStandingsCatTable(null, true, "#league_standings_table_w","Wins","team_pitcher_w");
	loadLeagueStandingsCatTable(null, true, "#league_standings_table_sv","Saves","team_pitcher_sv");
	loadLeagueStandingsCatTable(null, true, "#league_standings_table_holds","Holds","team_pitcher_holds");
	loadLeagueStandingsCatTable(null, true, "#league_standings_table_so","SO","team_pitcher_k");
	loadLeagueStandingsCatTable(null, true, "#league_standings_table_era","ERA","team_pitcher_era");
	loadLeagueStandingsCatTable(null, true, "#league_standings_table_whip","WHIP","team_pitcher_whip");
	*/
	
	// loadPositionalTable(null, $("#pos_c_table"), true, true);
	
	/*
	loadPositionalTable(null, $("#pos_1b_table"), true, true);
	loadPositionalTable(null, $("#pos_2b_table"), true, true);
	loadPositionalTable(null, $("#pos_ss_table"), true, true);
	loadPositionalTable(null, $("#pos_3b_table"), true, true);
	loadPositionalTable(null, $("#pos_of_table"), true, true);
	loadPositionalTable(null, $("#pos_sp_table"), true, false);
	loadPositionalTable(null, $("#pos_rp_table"), true, false);
	 */
	
	// loadLeagueStandingsTable(null, true);
	loadCustomPlayerPositionTable(null, true);
	
});

function getCurrentFilters(){
	
	if ($('#btn-pitchers').hasClass("active")) filterPlayerType('P', false);
	else if ($('#btn-hitters').hasClass("active")) filterPlayerType('H', false); 
	else if ($('#btn-c').hasClass("active")) filterPlayerPosition('C', false);
	else if ($('#btn-1b').hasClass("active")) filterPlayerPosition('1B', false);
	else if ($('#btn-2b').hasClass("active")) filterPlayerPosition('2B', false); 
	else if ($('#btn-ss').hasClass("active")) filterPlayerPosition('SS', false); 
	else if ($('#btn-3b').hasClass("active")) filterPlayerPosition('3B', false); 
	else if ($('#btn-mi').hasClass("active")) filterPlayerPosition('2B|SS', false); 
	else if ($('#btn-ci').hasClass("active")) filterPlayerPosition('1B|3B', false); 
	else if ($('#btn-of').hasClass("active")) filterPlayerPosition('OF', false); 
	else if ($('#btn-rp').hasClass("active")) filterPlayerPosition('RP', false);	
	else if ($('#btn-sp').hasClass("active")) filterPlayerPosition('SP', false); 
	else {
	    if ($('#select-draftedplayerfilter').val() == '0') regex_drafted = '(^$)|(\s+$)'; 
	    else if ($('#select-draftedplayerfilter').val() == '-1') regex_drafted = ''; 
	    else regex_drafted = $('#select-draftedplayerfilter').find("option:selected").text();
	    $('#playergrid_table').DataTable().columns( 34 ).search( regex_drafted , true );	// Filter league team name
	}
	
}

function filterPlayerPosition(position, isdraw){
    if ($('#select-draftedplayerfilter').val() == '0') regex_drafted = '(^$)|(\s+$)'; 
    else if ($('#select-draftedplayerfilter').val() == '-1') regex_drafted = ''; 
    else regex_drafted = $('#select-draftedplayerfilter').find("option:selected").text();
    // console.log('regex_drafted = ' + regex_drafted);
	$('#playergrid_table').DataTable().search( '' ).columns().search( '' );
	$('#playergrid_table').DataTable().columns( 39 ).search( false );
	$('#playergrid_table').DataTable().columns( 34 ).search( regex_drafted , true );	
	if (isdraw) $('#playergrid_table').DataTable().columns( 4 ).search( position , true ).draw();	
	else  $('#playergrid_table').DataTable().columns( 4 ).search( position , true );	
}

function filterPlayerType(pitcherhitter, isdraw){
    if ($('#select-draftedplayerfilter').val() == '0') regex_drafted = '(^$)|(\s+$)'; 
    else if ($('#select-draftedplayerfilter').val() == '-1') regex_drafted = ''; 
    else regex_drafted = $('#select-draftedplayerfilter').find("option:selected").text();
    // console.log('regex_drafted = ' + regex_drafted);
	$('#playergrid_table').DataTable().search( '' ).columns().search( '' );
	$('#playergrid_table').DataTable().columns( 39 ).search( false );		// Filter unknown player
	$('#playergrid_table').DataTable().columns( 34 ).search( regex_drafted , true );	// Filter league team name
	if (isdraw) $('#playergrid_table').DataTable().columns( 0 ).search( pitcherhitter , true ).draw();	
	else $('#playergrid_table').DataTable().columns( 0 ).search( pitcherhitter , true );	
}

function updateTeamOvwRosterTable(teamrow){
	// create a deep copy of teamrostertemplate
	var liveteamrostertemplate_1;
	var liveteamrostertemplate_2;
	if (dm_teamrostertemplate_1 != null){
		liveteamrostertemplate_1 = JSON.parse(JSON.stringify(dm_teamrostertemplate_1));
		liveteamrostertemplate_2 = JSON.parse(JSON.stringify(dm_teamrostertemplate_2));

		// update teamrostertabel with the blank template
		loadTeamOvwRosterTable(false);
		
		var teamid = teamrow.id;
		// console.log("TeamID: " + teamid);
		var playertable = $('#playergrid_table').DataTable();
		var teamovwrostertable_1 = $('#team_ovw_roster_table_1').DataTable();
		var teamovwrostertable_2 = $('#team_ovw_roster_table_2').DataTable();
		
		// Get players from table that have been drafted by selected team
		var teamplayers = playertable.rows( function ( idx, data, node ) {
	        return data.leagueteam_id == teamid ?
	            true : false;
	    } )
	    .data();

		// For each drafted player on a team, fill them into the team roster grid
		$.each( teamplayers, function( key, value ) {
			// console.log("Each teamplayer: " + value.full_name);
			$.each( liveteamrostertemplate_1, function( rkey, rvalue ) {
				// console.log("Each teamrostertemplate: " + rvalue.position);
				if ((value.team_roster_position.toLowerCase() == rvalue.position.toLowerCase())&&
						((rvalue.name == null)||(rvalue.name == ""))){
					rvalue.name = value.full_name;
					rvalue.salary = value.team_player_salary;
					rvalue.playerid = value.id;
					// teamovwrostertable_1.row('#' + rvalue.index + '').data(rvalue).draw();
					teamovwrostertable_1.row('#' + rvalue.index + '').data(rvalue);
					return false;
				}
			});	
			$.each( liveteamrostertemplate_2, function( rkey, rvalue ) {
				// console.log("Each teamrostertemplate: " + rvalue.position);
				if ((value.team_roster_position.toLowerCase() == rvalue.position.toLowerCase())&&
						((rvalue.name == null)||(rvalue.name == ""))){
					rvalue.name = value.full_name;
					rvalue.salary = value.team_player_salary;
					rvalue.playerid = value.id;
					// teamovwrostertable_2.row('#' + rvalue.index + '').data(rvalue).draw();
					teamovwrostertable_2.row('#' + rvalue.index + '').data(rvalue);
					return false;
				}
			});	
		});

		teamovwrostertable_1.columns.adjust().draw();
		teamovwrostertable_2.columns.adjust().draw();
		
		teamplayers = null;
		teamovwrostertable_1 = null;
		teamovwrostertable_2 = null;
		playertable = null;
	}
}

/*  Part of old draft player modal
function updateSelectPosTable(){
	// create a deep copy of teamrostertemplate
	var liveteamrostertemplate;
	if (teamrostertemplate != null){
		liveteamrostertemplate = JSON.parse(JSON.stringify(teamrostertemplate));

		// update SelectPosTable with the blank template
		// loadSelectPositionTable(liveteamrostertemplate, false);
		
		var teamid = $("#select-draftteam").find("option:selected").val();
		// console.log("TeamID: " + teamid);
		var playertable = $('#playergrid_table').DataTable();
		var teamrostertable = $('#table-selectdraftposition').DataTable();
		
		// Get players from table that have been drafted by selected team
		var teamplayers = playertable.rows( function ( idx, data, node ) {
	        return data.leagueteam_id == teamid ?
	            true : false;
	    } )
	    .data();
		
		$.each( liveteamrostertemplate, function( rkey, rvalue ) {
				rvalue.name = "[slot open]";
				teamrostertable.row('#' + rvalue.index + '').data(rvalue).draw();
		});

		// For each drafted player on a team, fill them into the team roster grid
		$.each( teamplayers, function( key, value ) {
			// console.log("Each teamplayer: " + value.full_name);
			$.each( liveteamrostertemplate, function( rkey, rvalue ) {
				// console.log("Each teamrostertemplate: " + rvalue.position);
				if ((value.team_roster_position == rvalue.position)&&
						((rvalue.name == null)||(rvalue.name == "")||(rvalue.name == "[slot open]"))){
					rvalue.name = value.full_name;
					rvalue.salary = value.team_player_salary;
					rvalue.playerid = value.id;
					// console.log("Updating teamrostertemplate: " + rvalue.name + ", " + rvalue.salary + ", " + rvalue.position  + ", " + rvalue.index);
					teamrostertable.row('#' + rvalue.index + '').data(rvalue).draw();
					return false;
				} 

			});
			
		});

        // $("#btn-editdraftplayer").attr("disabled", "disabled");
        // $("#btn-undraftplayer").attr("disabled", "disabled");
		$('#table-selectdraftposition').DataTable().search( '' ).columns().search( '' );
		if (playerdraftrow.pitcher_hitter == "H"){
			$('#table-selectdraftposition').DataTable().columns( 0 ).search( 'C|1B|2B|3B|SS|MI|CI|OF|UT|RES', true ).draw();
		} else {
			$('#table-selectdraftposition').DataTable().columns( 0 ).search( 'P|RES', true ).draw();
		}
	}
	playertable = null;
}
*/

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
		
		var drafted_res = 0;
		var drafted_ml = 0;

		// For each drafted player on a team, fill them into the team roster grid
		$.each( teamplayers, function( key, value ) {
			// console.log("Each teamplayer: " + value.full_name);
			$.each( liveteamrostertemplate, function( rkey, rvalue ) {
				// console.log("Each teamrostertemplate: " + rvalue.position);
				if ((value.team_roster_position.toLowerCase() == rvalue.position.toLowerCase())&&
						((rvalue.name == null)||(rvalue.name == ""))){
					rvalue.name = value.full_name;
					rvalue.salary = value.team_player_salary;
					rvalue.playerid = value.id;
					// console.log("Updating teamrostertemplate: " + rvalue.name + ", " + rvalue.salary + ", " + rvalue.position  + ", " + rvalue.index);
					teamrostertable.row('#' + rvalue.index + '').data(rvalue).draw();
					if (value.team_roster_position.toLowerCase() == "res") drafted_res++;
					return false;
				}
			});
			if (value.team_roster_position.toLowerCase() == "ml") {
				drafted_ml++;
				// add ML row to table
				teamrostertable.row.add( {
					"index": 100,
			        "playerid":       value.id,
			        "name":   value.full_name,
			        "position":     "ML",
			        "salary": "0"
			    } ).draw();
			}
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
		
//		console.log("liveteamrostertemplate.length: " + liveteamrostertemplate.length);
//		console.log("teamplayers.length: " + teamplayers.length);
//		console.log("dm_rescount: " + dm_rescount);
//		console.log("drafted_res: " + drafted_res);
		
		var teamstartingsalary = team.adj_starting_salary;
		var balance = teamstartingsalary - teamrostertable.column( 4 ).data().sum();
		var spots = (liveteamrostertemplate.length - dm_rescount) - (teamplayers.length - (drafted_res + drafted_ml));
		var perplayer = balance / spots;
		var maxbid = balance - (spots - 1);
		
		// console.log("Team salary: " + teamstartingsalary);
		// console.log("Sum of salaries: " + teamrostertable.column( 4 ).data().sum());
		
		$('#lbl-teambalance').text("Balance: $" + balance + "  ");
		// $('#lbl-teamstarting').text("Starting: $" + teamstartingsalary);
		$('#lbl-teamspots').text("Remaining Spots: " + spots);
		$('#lbl-teamperplayer').text("Avg Player Amt: $" + perplayer.toFixed(2));
		$('#lbl-teammaxbid').text("Max Bid: $" + maxbid);
		
        $("#btn-editdraftplayer").attr("disabled", "disabled");
        $("#btn-undraftplayer").attr("disabled", "disabled");
	}
	
	playertable = null;
}

// Load Player Queue
function loadPlayerQueue(){

	$("#player_queue_panel_body ul").empty();
	var playertable = $('#playergrid_table').DataTable();

	// Get players from table that have been drafted by selected team
	var favplayers = playertable.rows( function ( idx, data, node ) {
        return data.favorite_flag == true ?
            true : false;
    } )
    .data();

	// For each favorite player, add to player queue
	$.each( favplayers, function( key, value ) {
		$("#player_queue_panel_body ul").append('<li class="ui-state-default" id=' + value.id 
				+ '><i class="fa fa-bars handle" aria-hidden="true"></i>  ' + value.full_name + ', ' 
				+ value.team + ' ' + value.custom_position + '</li>');
	});
		
	playertable = null;
}

//Update Player Queue
function updatePlayerQueue(){
	var playertable = $('#playergrid_table').DataTable();
	// console.log("Player: " + playerselectedrow.full_name);
	// console.log("Player ID: " + playerselectedrow.id);
	  
	if ($("#fav-icon").hasClass("fa-star-o")){
		playerselectedrow.favorite_flag = true;
		$("#fav-icon").removeClass("fa-star-o");
		$("#fav-icon").addClass("fa-star");
		$("#player_queue_panel_body ul").append('<li class="ui-state-default" id=' + playerselectedrow.id 
				+ '><i class="fa fa-bars handle" aria-hidden="true"></i>  ' + playerselectedrow.full_name + ', ' 
				+ playerselectedrow.team + ' ' + playerselectedrow.custom_position + '</li>');
	}
	else if ($("#fav-icon").hasClass("fa-star")){
		playerselectedrow.favorite_flag = false;
		$("#fav-icon").removeClass("fa-star");
		$("#fav-icon").addClass("fa-star-o");
		$("#player_queue_panel_body ul #" + playerselectedrow.id).remove();
	}
	
	playertable.row('#' + playerselectedrow.id + '').data(playerselectedrow).draw();
	selectPlayerInPlayerGridTable(playerselectedrow.id);
	mssolutions.fbapp.draftmanager.updatePlayerInfo(playerselectedrow);
	
	playertable = null;
}

//Send player to block
function sendPlayerToBlock(){
	
	var playerid = $("#lbl-playerinfoname").val();
	var playertable = $('#playergrid_table').DataTable();
	playerdraftrow = playertable.row('#' + playerid).data();

	// New, may want to remove later
	loadOTBTeamSelect(dm_globalteamlist);
	
	$("#select-ontheblock-draftteam").removeAttr("disabled");
	$("#select-ontheblock-draftamt").removeAttr("disabled");
	$("#select-ontheblock-draftposition").attr("disabled","disabled");

    var data = playerdraftrow;
    
    resetDraftPlayerModal();
    $("#btn-draftplayer").attr("disabled","disabled");
    $("#btn-ontheblock-draftplayer").attr("disabled","disabled");
    
    $("#header-ontheblock-draftplayer").text(data.full_name + ", " + data.team + " - " + data.custom_position + " - $" + data.live_auction_value);
    $("#header-ontheblock-draftplayer").val(data.id);
    
    var proj_string = "";
    if (data.pitcher_hitter == "P"){
    	console.log(dm_leagueinfo.cat_pitcher_w + ", " + dm_leagueinfo.cat_pitcher_sv)
    	if (dm_leagueinfo.cat_pitcher_wins) proj_string = proj_string + round(data.pitcher_w,0) + " W, ";
    	if (dm_leagueinfo.cat_pitcher_saves) proj_string = proj_string + round(data.pitcher_sv,0) + " SV, ";
    	if (dm_leagueinfo.cat_pitcher_holds) proj_string = proj_string + round(data.pitcher_hld,0) + " HOLDS, ";
    	if (dm_leagueinfo.cat_pitcher_so) proj_string = proj_string + round(data.pitcher_k,0) + " K, ";
    	if (dm_leagueinfo.cat_pitcher_era) proj_string = proj_string + round(data.pitcher_era,2) + " ERA, ";
    	if (dm_leagueinfo.cat_pitcher_whip) proj_string = proj_string + round(data.pitcher_whip,2) + " WHIP, ";
    	$("#header2-ontheblock-draftplayer").html(dm_leagueinfo.league_year + " Projection:<br>" + proj_string.slice(0,-2));
    }
    else {
    	if (dm_leagueinfo.cat_hitter_avg) proj_string = proj_string + data.hitter_avg.toString().substr(1) + " AVG, ";
    	if (dm_leagueinfo.cat_hitter_hr) proj_string = proj_string + round(data.hitter_hr,0) + " HR, ";
    	if (dm_leagueinfo.cat_hitter_r) proj_string = proj_string + round(data.hitter_runs,0) + " R, ";
    	if (dm_leagueinfo.cat_hitter_sb) proj_string = proj_string + round(data.hitter_sb,0) + " SB, ";
    	if (dm_leagueinfo.cat_hitter_rbi) proj_string = proj_string + round(data.hitter_rbi,0) + " RBI, ";
    	if (dm_leagueinfo.cat_hitter_obp) proj_string = proj_string + data.hitter_obp.toString().substr(1) + " OBP, ";
    	$("#header2-ontheblock-draftplayer").html(dm_leagueinfo.league_year + " Projection:<br>" + proj_string.slice(0,-2));
    }

    $("#btn-draftamt-plus").removeAttr("disabled");
    $("#btn-draftamt-minus").removeAttr("disabled");
}

function loadDraftPlayerAmtSelector(){
	
	var amtselector = $("#select-draftamt");
	// var amtselector2 = $("#select-ontheblock-draftamt");
	var amtselectorunk = $("#select-draftamtunk");
	amtselector.find('option').remove().end();
	// amtselector2.find('option').remove().end();
	amtselectorunk.find('option').remove().end();

	for (i = 0; i <= 100; i++) { 
		amtselector.append($("<option value='" + i + "'/>").text("$" + i));
		// amtselector2.append($("<option value='" + i + "'/>").text("$" + i));
		amtselectorunk.append($("<option value='" + i + "'/>").text("$" + i));
	}

}


function resetDraftPlayerModal(){

	// $("option", "#select-draftteam").removeAttr("selected");
	// $("option", "#select-draftamt").removeAttr("selected");
	// var posselector = $("#select-draftposition");
	// posselector.find('option').remove().end();
	// $('#lbl-draftprevteam').text("[none]");
	// $('#lbl-draftprevamt').text("[none]");
	// $('#lbl-draftprevpos').text("[none]");
	
	$("option", "#select-ontheblock-draftteam").removeAttr("selected");
	$('#select-ontheblock-draftamt').val('');
	var posselector = $("#select-ontheblock-draftposition");
	posselector.find('option').remove().end();

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


function resetDraftPanel(){
	var teamselector = $("#select-ontheblock-draftteam");
	var posselector = $("#select-ontheblock-draftposition");
	var amtselector = $("#select-ontheblock-draftamt");
	
	posselector.find('option').remove().end();
	posselector.attr("disabled","disabled");
	
	teamselector.find('option').remove().end();
	teamselector.attr("disabled","disabled");
	
	amtselector.val('').trigger('propertychange');
	amtselector.attr("disabled","disabled");
	
	$("#btn-ontheblock-draftplayer").attr("disabled","disabled");
	$("#btn-draftamt-plus").attr("disabled","disabled");
	$("#btn-draftamt-minus").attr("disabled","disabled");
	
	$('#header-ontheblock-draftplayer').text("Select a Player to Draft");
	$('#header2-ontheblock-draftplayer').text("");
}

function checkAmtSelector(){
	var posselector = $("#select-ontheblock-draftposition");
	var teamselector = $("#select-ontheblock-draftteam");
	var amtselector = $("#select-ontheblock-draftamt");
	var teamselected = false;
	var posselected = false;
	var amtselected = false; 
	
	var amtval2 = parseInt(amtselector.val());

	if (amtselector.val() != "") amtselected = true;
	if ((teamselector.val() != null) && (teamselector.val() != 0)) teamselected = true;
	if ((posselector.val() != null) && (posselector.val() != 0)) posselected = true;
	
	if ((amtval2 > dm_teammaxbid) && (posselector.val().toLowerCase() != "res")) {
		amtselector.css({'background-color' : '#ffe6e6'});
		$("#text-draft-amt-warning").show();
		amtselected = false;
	}
	else if (amtselector.val() == ""){
		amtselector.css({'background-color' : '#ffffff'});
		$("#text-draft-amt-warning").hide();
		amtselected = false;
	}
	else {
		amtselector.css({'background-color' : '#ffffff'});
		$("#text-draft-amt-warning").hide();
		amtselected = true;
	}	
	
	// console.log("teamselected = " + teamselected);
	// console.log("team value = " + teamselector.val())
	// console.log("posselected = " + posselected);
	// console.log("pos value = " + posselector.val());
	// console.log("amt value = " + amtselector.val());
    
	/*
    if (!teamselected || !posselected || !amtselected){
    	$("#btn-ontheblock-draftplayer").attr("disabled","disabled");
    } else {
    	$("#btn-ontheblock-draftplayer").removeAttr("disabled");
    }
    */
    
	// If position selector is set to "RES" or "ML" then disable the amount selector,
	// set it to 0, and enable the draft button.
	if (posselector.val().toLowerCase() == "res" || posselector.val().toLowerCase() == "ml"){
		$("#btn-ontheblock-draftplayer").removeAttr("disabled");
		$("#select-ontheblock-draftamt").val("");
		$("#select-ontheblock-draftamt").attr("disabled","disabled");
		$('#btn-draftamt-plus').attr("disabled","disabled");
		$('#btn-draftamt-minus').attr("disabled","disabled");
		$("#select-ontheblock-draftamt").css({'background-color' : '#eaeaea'});
		$("#text-draft-amt-warning").hide();
	} else if (!teamselected || !posselected || !amtselected){
    	$("#btn-ontheblock-draftplayer").attr("disabled","disabled");
    	$("#select-ontheblock-draftamt").removeAttr("disabled");
    	$('#btn-draftamt-plus').removeAttr("disabled");
    	$('#btn-draftamt-minus').removeAttr("disabled");
    	$("#select-ontheblock-draftamt").css({'background-color' : '#ffffff'});
    	// checkAmtSelector();
    } else {
    	$("#btn-ontheblock-draftplayer").removeAttr("disabled");
    	$("#select-ontheblock-draftamt").removeAttr("disabled");
    	$('#btn-draftamt-plus').removeAttr("disabled");
    	$('#btn-draftamt-minus').removeAttr("disabled");
    	$("#select-ontheblock-draftamt").css({'background-color' : '#ffffff'});
    	// checkAmtSelector();
    }
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
	var teamid;
	var teamname;
	var posselector;
	
	// if initial draft
	if (updateplayerposition == null){
		teamid = $("#select-ontheblock-draftteam").val();
		teamname = $("#select-ontheblock-draftteam").find("option:selected").text();
		posselector = $("#select-ontheblock-draftposition");
		posselector.removeAttr("disabled");
	} else {
		teamid = $("#select-draftteam").val();
		teamname = $("#select-draftteam").find("option:selected").text();
		posselector = $("#select-draftposition");
	}
	
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
	var countres = dm_teamrostercounts["Res"];
	
	// Update counts by subtracting current team roster counts
	// Determine if roster position is available based on count
	$.each( liveteamrostercounts, function( lkey, lvalue ) {
		// console.log("lkey: " + lkey + ",  lvalue: " + lvalue);
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
		if (lkey == "Res")  countres = dm_teamrostercounts["Res"] - lvalue;

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
		if ((lkey == "Res")&&(dm_teamrostercounts["Res"] <= lvalue)) selres = false;
			
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
		if ((updateplayerposition == "Res")) selres = true;
	}

	if (playerdraftrow != null){
		// if initial draft, add this to the 1st position of selector dropdown
		if (updateplayerposition == null) $("#select-ontheblock-draftposition").append($("<option value='0'/>").text("-- Position --"));
		
		if (selc && playerdraftrow.pitcher_hitter == "H") posselector.append($("<option value='C'/>").text("C (" + countc + ")"));
		else posselector.append($("<option style='background-color:rgb(240,240,240);color:rgb(150,150,150)' disabled='disabled' value='C'/>").text("C (" + countc + ")"));
		if (sel1b && playerdraftrow.pitcher_hitter == "H") posselector.append($("<option value='1B'/>").text("1B (" + count1b + ")"));
		else posselector.append($("<option style='background-color:rgb(240,240,240);color:rgb(150,150,150)' disabled='disabled' value='1B'/>").text("1B (" + count1b + ")"));
		if (sel2b && playerdraftrow.pitcher_hitter == "H") posselector.append($("<option value='2B'/>").text("2B (" + count2b + ")"));
		else posselector.append($("<option style='background-color:rgb(240,240,240);color:rgb(150,150,150)' disabled='disabled' value='2B'/>").text("2B (" + count2b + ")"));
		if (selss && playerdraftrow.pitcher_hitter == "H") posselector.append($("<option value='SS'/>").text("SS (" + countss + ")"));
		else posselector.append($("<option style='background-color:rgb(240,240,240);color:rgb(150,150,150)' disabled='disabled' value='SS'/>").text("SS (" + countss + ")"));
		if (sel3b && playerdraftrow.pitcher_hitter == "H") posselector.append($("<option value='3B'/>").text("3B (" + count3b + ")"));
		else posselector.append($("<option style='background-color:rgb(240,240,240);color:rgb(150,150,150)' disabled='disabled' value='3B'/>").text("3B (" + count3b + ")"));
		if (selmi && playerdraftrow.pitcher_hitter == "H") posselector.append($("<option value='MI'/>").text("MI (" + countmi + ")"));
		else posselector.append($("<option style='background-color:rgb(240,240,240);color:rgb(150,150,150)' disabled='disabled' value='MI'/>").text("MI (" + countmi + ")"));
		if (selci && playerdraftrow.pitcher_hitter == "H") posselector.append($("<option value='CI'/>").text("CI (" + countci + ")"));
		else posselector.append($("<option style='background-color:rgb(240,240,240);color:rgb(150,150,150)' disabled='disabled' value='CI'/>").text("CI (" + countci + ")"));
		if (selof && playerdraftrow.pitcher_hitter == "H") posselector.append($("<option value='OF'/>").text("OF (" + countof + ")"));
		else posselector.append($("<option style='background-color:rgb(240,240,240);color:rgb(150,150,150)' disabled='disabled' value='OF'/>").text("OF (" + countof + ")"));
		if (selutil && playerdraftrow.pitcher_hitter == "H") posselector.append($("<option value='UT'/>").text("Util (" + countutil + ")"));
		else posselector.append($("<option style='background-color:rgb(240,240,240);color:rgb(150,150,150)' disabled='disabled' value='UT'/>").text("Util (" + countutil + ")"));
		if (selp && playerdraftrow.pitcher_hitter == "P") posselector.append($("<option value='P'/>").text("P (" + countp + ")"));
		else posselector.append($("<option style='background-color:rgb(240,240,240);color:rgb(150,150,150)' disabled='disabled' value='P'/>").text("P (" + countp + ")"));
		if (selres) posselector.append($("<option value='RES'/>").text("Res (" + countres + ")"));
		else posselector.append($("<option style='background-color:rgb(240,240,240);color:rgb(150,150,150)' disabled='disabled' value='Res'/>").text("Res (" + countres + ")"));
		
		posselector.append($("<option value='ML'/>").text("ML"));

		if ((playerdraftrow.pitcher_hitter == "P") && (selp)) $('select[id=select-ontheblock-draftposition]').val("P");
		else if (!selp && !selc && !sel1b && !sel2b && !selss && !sel3b && !selmi && !selci && !selof && !selutil && selres){
			console.log("In sel reserve if statement");
			$('select[id=select-ontheblock-draftposition]').val("RES");
		}
	}
	
}

/**
 * Description: Updates the OTB panel Amt Selector and Draft Button for when
 * the Position selector is set to "RES".  When this happens, no amt is needed
 * in the amt selector (it is disabled) and the draft button is enabled.
 */
/*
function updateOTBforResPosSelect(){
	console.log("In updateOTBforResPosSelect");
	$("#btn-ontheblock-draftplayer").removeAttr("disabled");
	$("#select-ontheblock-draftamt").val("");
	$("#select-ontheblock-draftamt").attr("disabled","disabled");
	$('#btn-draftamt-plus').attr("disabled","disabled");
	$('#btn-draftamt-minus').attr("disabled","disabled");
	$("#select-ontheblock-draftamt").css({'background-color' : '#eaeaea'});
	$("#text-draft-amt-warning").hide();
}
*/

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

function loadTeamOvwRosterTable(isInitialLoad)
{	
	var data_table_1;
	var data_table_2;
	var table_element_1 = $('#team_ovw_roster_table_1');
	var table_element_2 = $('#team_ovw_roster_table_2');
	var config_1 = {
		"destroy": true,
		responsive: true,
    	"processing": true,
        "bSort" : false,
        rowId: 'index',
        "searching": false,
        "info": false,
    	select: 'single',
        data: dm_teamrostertemplate_1,
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
	var config_2 = {
			"destroy": true,
			responsive: true,
	    	"processing": true,
	        "bSort" : false,
	        rowId: 'index',
	        "searching": false,
	        "info": false,
	    	select: 'single',
	        data: dm_teamrostertemplate_2,
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
		data_table_1 = table_element_1.dataTable(config_1);
		data_table_2 = table_element_2.dataTable(config_2);
		data_table_1 = null;
		data_table_2 = null;
		config_1 = null;
		config_2 = null;
	} else {
		data_table_1 = table_element_1.DataTable();
		data_table_1.destroy();
		table_element_1.empty();
		data_table_1 = table_element_1.dataTable(config_1);
		
		data_table_2 = table_element_2.DataTable();
		data_table_2.destroy();
		table_element_2.empty();
		data_table_2 = table_element_2.dataTable(config_2);
		
		data_table_1 = null;
		data_table_2 = null;
		table_element_1 = null;
		table_element_2 = null;
		config_1 = null;
		config_2 = null;
	}
	
	/*
	var data_table_1 = $('#team_ovw_roster_table_1').DataTable();
	data_table_1
    .on( 'select', function ( e, dt, type, indexes ) {
    	var data_table_1_b = $('#team_ovw_roster_table_1').DataTable();
        var rows = data_table_1_b.rows( indexes ).data();
        // console.log("Select: " + rowData[0].name);
        // console.log("Select: " + JSON.stringify(rows[0]));
        if ((rows[0].name == null)||(rows[0].name == "")){
        	data_table_1_b.rows( indexes ).deselect();
        } else {

        }

    } )
    .on( 'deselect', function ( e, dt, type, indexes ) {
        // var rowData = data_table.rows( indexes ).data().toArray();
        // console.log("De-Select: " + rowData[0].name);
    } );
	
	var data_table_2 = $('#team_ovw_roster_table_2').DataTable();
	data_table_2
    .on( 'select', function ( e, dt, type, indexes ) {
    	var data_table_2_b = $('#team_ovw_roster_table_2').DataTable();
        var rows = data_table_2_b.rows( indexes ).data();
        // console.log("Select: " + rowData[0].name);
        // console.log("Select: " + JSON.stringify(rows[0]));
        if ((rows[0].name == null)||(rows[0].name == "")){
        	data_table_2_b.rows( indexes ).deselect();
        } else {

        }

    } )
    .on( 'deselect', function ( e, dt, type, indexes ) {
        // var rowData = data_table.rows( indexes ).data().toArray();
        // console.log("De-Select: " + rowData[0].name);
    } );
    */
}

// Team roster table on right side in Draft Analysis Tab
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
        "scrollY": "450px",
        "scrollCollapse": true,
        "paging": false,
        "order": [[ 0, "asc" ]],
        "columns": [
            { "visible": false, "title": "index", "mData": "index" },
            { "visible": false, "title": "ID", "mData": "playerid", "sDefaultContent": "" },
            { "title": "Pos", "mData": "position","width": 20 },
            { "title": "Player", "mData": "name", "sDefaultContent": ""},
            { "title": "$", "mData": "salary", "sDefaultContent": "", "width": 20, "render": function ( data, type, row ) {
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

/* Player position table in the draft player modal
function loadSelectPositionTable(data, isInitialLoad)
{
    var calcDataTableHeight = function() {
        return $(window).height();
    };
	var data_table;
	var table_element = $('#table-selectdraftposition');
	var config = {
		"dom": 't',
		responsive: true,
    	"processing": true,
        "bSort" : false,
        rowId: 'index',
        "searching": true,
        "info": false,
        select: {
            style: 'single',
            items: 'cell'
        },
        selector: 'td:not(:first-child)',
        data: data,
        // "scrollY":        "440px",
        // "scrollCollapse": false,
        "paging": false,
        "order": [[ 2, "asc" ]],
        "columns": [
            { "title": "Pos", "mData": "position","width": 40 },
            { "title": "Player", "mData": "name", "sDefaultContent": "" },
            { "visible": false, "title": "index", "mData": "index" },
            { "visible": false, "title": "ID", "mData": "playerid", "sDefaultContent": "" },
        ],
        fnDrawCallback: function() {
            $("#table-selectdraftposition thead").remove();
          }
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
	
	var data_table = $('#table-selectdraftposition').DataTable();
	data_table
    .on( 'select', function ( e, dt, type, indexes ) {
    	var data_table_b = $('#table-selectdraftposition').DataTable();
    	var rowIdx = data_table_b.cell( indexes ).index().row;
    	var colIdx = data_table_b.cell( indexes ).index().column;
        var rows = data_table_b.rows( rowIdx ).data();
        // console.log("Select colIdx: " + colIdx);
        if (colIdx == 0) {
        	data_table_b.cell(indexes).deselect();
        	$("#btn-draftposmoveup").hide();
        	$("#btn-draftposmovedown").hide();
        }
        else if ((rows[0].name == null)||(rows[0].name.startsWith("["))){
            $('#lbl-draftprevpos').text(rows[0].position);
            
            if (($("#select-draftteam").val() == null) ||
            		($("#select-draftamt").val() == null)){
            	$("#btn-draftplayer").attr("disabled","disabled");
            } else {
            	$("#btn-draftplayer").removeAttr("disabled");
            }
        	$("#btn-draftposmoveup").hide();
        	$("#btn-draftposmovedown").hide();
        } else {
            // $("#btn-editdraftplayer").removeAttr("disabled");  
            // $("#btn-undraftplayer").removeAttr("disabled");  
        	$('#lbl-draftprevpos').text("[none]");
        	$("#btn-draftposmoveup").show();
        	$("#btn-draftposmovedown").show();
        }

    } )
    .on( 'deselect', function ( e, dt, type, indexes ) {
        // var rowData = data_table.rows( indexes ).data().toArray();
        // console.log("De-Select: " + rowData[0].name);
        // $("#btn-editdraftplayer").attr("disabled", "disabled");
        // $("#btn-undraftplayer").attr("disabled", "disabled");
    	$("#btn-draftplayer").attr("disabled","disabled");
    	$('#lbl-draftprevpos').text("[none]");
    	$("#btn-draftposmoveup").hide();
    	$("#btn-draftposmovedown").hide();
    } );
}
*/

function loadCustomPlayerPositionTable(data, isInitialLoad)
{
	// Filter data for DH only
	filtered_data = [];
	
	if (data != null){
		$.each( data, function( index, value ){
			if (value.player_position == 'DH'){
				filtered_data.push(value);
			}
		});
		
		/*
		$.each( dm_leagueinfo.position_priority_list, function( index, value ){
			$("#lbl-custompositionplayername").after(" <div class='checkbox'> " +
					"<label><input type='checkbox' value=''>" + value + "</label> </div>");
		});
		*/
		
		$("#form-custpos").prepend("<div class='checkbox'> " +
			"<label><input class='checkbox-custpos' id='checkbox-custpos-1B' type='checkbox' disabled='true' value='1B'>1B</label></div>"
		+ "<div class='checkbox'> " +
			"<label><input class='checkbox-custpos' id='checkbox-custpos-2B' type='checkbox' disabled='true' value='2B'>2B</label></div>"
		+ "<div class='checkbox'> " +
			"<label><input class='checkbox-custpos' id='checkbox-custpos-SS' type='checkbox' disabled='true' value='SS'>SS</label></div>"
		+ "<div class='checkbox'> " +
			"<label><input class='checkbox-custpos' id='checkbox-custpos-3B' type='checkbox' disabled='true' value='3B'>3B</label></div>"
		+ "<div class='checkbox'> " +
			"<label><input class='checkbox-custpos' id='checkbox-custpos-OF' type='checkbox' disabled='true' value='OF'>OF</label></div>"
		+ "<div class='checkbox'> " +
			"<label><input class='checkbox-custpos' id='checkbox-custpos-C' type='checkbox' disabled='true' value='C'>C</label></div>"
		+ "<div class='checkbox'> " +
			"<label><input class='checkbox-custpos' id='checkbox-custpos-DH' type='checkbox' disabled='true' value='DH'>DH</label></div>");
		
	}

	var data_table;
	var table_element = $('#customplayerposition_table');
	var config = {
        "bSort" : true,
        "searching": false,
        "paging": true,
        "info": false,
        "order": [[ 1, "asc" ]],
		responsive: true,
    	"processing": true,
        data: filtered_data,
        select: {
            style:    'single'
        },
        rowId: 'id',
        "columns": [
                { "title": "Name", className: "dm_export", "mData": "full_name",  "render": function ( data, type, row ) {
                	if ((row.team_player_note == null)||(row.team_player_note.trim() == ""))
                		return data + " (" + row.team + ")";
                	else return data + " (" + row.team + ")&nbsp;&nbsp;<i class='fa fa-file-text'></i>";
                    }},

                { "visible": true, "title": "Team", "mData": "team", "sDefaultContent": ""},
                { "title": "Pos", className: "dm_export", "mData": "player_position", "sDefaultContent": ""},
                { "title": "Custom Pos Flag", className: "dm_export", "mData": "custom_position_flag", "sDefaultContent": ""},
                { "title": "Updated Pos", className: "dm_export", "mData": "custom_position", "sDefaultContent": ""},

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
	
    // On Select of the Custom Player Position Table
	var select_data_table = $('#customplayerposition_table').DataTable();
	select_data_table
    .on( 'select', function ( e, dt, type, indexes ) {
    	
    	$("#btn-save-custpos").removeClass('disabled');
    	
    	var select_data_table_b = $('#customplayerposition_table').DataTable();
        var row = select_data_table_b.rows( indexes ).data()[0];
        playercustomposrow = row;
		
		$("#lbl-custompositionplayername").val(row.id);
		$("#lbl-custompositionplayername").text(row.full_name);
		// $("#lbl-playerinfoelig").text(row.player_position);

		// console.log("-- Position priority list: " + dm_leagueinfo.position_priority_list);
		// console.log("-- Position priority list size: " + dm_leagueinfo.position_priority_list.length);
		
		jQuery('.checkbox-custpos').each(function() {
		    var currentElement = $(this);
	        var $label = $("label[for='"+currentElement.id+"']")
	        // console.log("-- Current element label text5: " + currentElement.val());
	        // console.log("-- Current row player position: " + row.player_position);
	        if(row.custom_position.indexOf(currentElement.val()) != -1){
	        	currentElement.prop('checked', true);
	        	if(row.player_position.indexOf(currentElement.val()) != -1) 
	        		currentElement.prop('disabled', true);
	        	else currentElement.prop('disabled', false);
	        } else {
	        	currentElement.prop('checked', false);
	        	currentElement.prop('disabled', false);
	        }
	        
		});

    } )
    .on( 'deselect', function ( e, dt, type, indexes ) {
    	
    	$("#btn-save-custpos").addClass('disabled');
    	
		$("#lbl-custompositionplayername").val(0);
		$("#lbl-custompositionplayername").text('Select Player');
		
		jQuery('.checkbox-custpos').each(function() {
		    var currentElement = $(this);
		    currentElement.prop('checked', false);
		    currentElement.prop('disabled', true);
		});
		
    } );

}


function loadTeamOvwTable(data, isInitialLoad)
{
	var data_table;
	var table_element = $('#team_ovw_table');
	var config = {
        "bSort" : true,
        "searching": false,
        "paging": false,
        "info": false,
        "order": [],
		responsive: true,
    	"processing": true,
        data: data,
        select: {
            style:    'single'
        },
        rowId: 'id',
        "createdRow": function ( row, data, index ) {
        	// console.log("data.isMyTeam: " + data.isMyTeam)
            if ( data.isuserowner ) {
//                $('td', row).eq(2).addClass('highlight');
//                $('td', row).eq(3).addClass('highlight');
//                $('td', row).eq(4).addClass('highlight');
//                $('td', row).addClass('highlight');
                $('td', row).css("font-weight", "bold");
            }
        },
        "columns": [
            { "visible": false, "title": "Team ID", "mData": "id", "sDefaultContent": ""},	
            { "visible": false, "title": "isMyTeam", "mData": "isuserowner", "sDefaultContent": ""},	
            { "title": "Team", "mData": "team_name", "sDefaultContent": "", render: $.fn.dataTable.render.ellipsis( 15 )},	
            { "title": "Bal", "mData": "balance", "render": function ( data, type, row ) 
            	{return "$" + data.toFixed(0);},"sDefaultContent": ""},
            // { "title": "Spots", "mData": "remainingspots", "sDefaultContent": ""},
            { "title": "H", "mData": "hitterspots", "sDefaultContent": ""},
            { "title": "P", "mData": "pitcherspots", "sDefaultContent": ""},
            { "title": "Avg $", "mData": "perplayeramt", "render": function ( data, type, row ) 
            	{return "$" + data.toFixed(2);},"sDefaultContent": ""},
            { "title": "Max $", "mData": "maxbid", "render": function ( data, type, row ) {
        		return "$" + data.toFixed(0);
            }, "sDefaultContent": ""},

        ]
        };
	
	if (isInitialLoad) 	{
		data_table = table_element.dataTable(config);
	} else {
		data_table = table_element.DataTable();
		data_table.off('select');
		data_table.off('deselect');
		data_table.destroy();
		table_element.empty();
		data_table = table_element.dataTable(config);
		data_table = table_element.DataTable();
	}
	
	var data_table = $('#team_ovw_table').DataTable();
	data_table
    .on( 'select', function ( e, dt, type, indexes ) {
    	
    	var data_table_b = $('#team_ovw_table').DataTable();
    	var row = data_table_b.rows( indexes ).data()[0];

    	// console.log("team row: " + JSON.stringify(row));
    	updateTeamOvwRosterTable(row);
    	
    	data_table_b = null;
    	row = null;

    } )
    .on( 'deselect', function ( e, dt, type, indexes ) {

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
    	"deferRender": true,
        data: data,
        select: {
            style:    'single',
            // If column with button is selected, it will not register select
            // selector: 'td:not(:nth-last-child(2))'  
        },
        rowId: 'id',
        "paging": true,
        "order": [[ 30, "desc" ]],
        "iDisplayLength": 20,
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
            { "visible": false, "title": "pitcher_hitter", "mData": "pitcher_hitter" },				// Column 0
            { "title": "Name", className: "dm_export", "mData": "full_name",  "render": function ( data, type, row ) {
            	var text = data + " (" + row.team + ")";
            	if (row.favorite_flag) {
            		// console.log("Is Favorite: " + row.full_name);
            		text = text + "&nbsp;&nbsp;<i class='fa fa-star'></i>";
            		}
            	if (row.team_player_note != null){
            		if (row.team_player_note.trim() != ""){
            			text = text + "&nbsp;&nbsp;<i class='fa fa-file-text'></i>";
            		}
            	}	
            	return text;
                }},
            { "title": "Age", className: "dm_export","width": 20, "mData": "age", "sDefaultContent": "", 
                	"createdCell": function (td, cellData, rowData, row, col) {setAgeCellColor(td, cellData)} 
                },
            { "visible": false, "title": "Team", "mData": "team", "sDefaultContent": ""},
            /*
            { "title": "Pos", className: "dm_export", "mData": "player_position", "render": function ( data, type, row ) {
            	if (row.custom_position_flag){  return row.custom_position;} 
            	else {return row.player_position;}
                },
                "sDefaultContent": ""}, */ 
	        { "title": "Pos", className: "dm_export", "mData": "custom_position","width": 78, "sDefaultContent": ""},
            { "title": "St", className: "dm_export", "mData": "dc_status", "sDefaultContent": ""},				// Column 5
            { "title": "Avg", className: "dm_stat text-right dm_export" + " dm_league_cat_" + dm_leagueinfo.cat_hitter_avg,"width": "21px", "mData": "hitter_avg", "render": function ( data, type, row ) {
            	if (row.pitcher_hitter == "H"){ 
            		var obpnum = data.toFixed(3);
                    return obpnum.toString().substr(obpnum.length - 4);
            	} else if (row.pitcher_hitter == "P"){return "";}
                },
                "createdCell": function (td, cellData, rowData, row, col) {
                	if (rowData.pitcher_hitter == "H"){ setStatCellColor(td, rowData.hitter_z_avg, 1)}
                },
                "sDefaultContent": ""},
            { "title": "HR", className: "dm_stat text-right dm_export" + " dm_league_cat_" + dm_leagueinfo.cat_hitter_hr,"width": 16, "mData": "hitter_hr", "render": function ( data, type, row ) {		// Column 7
            	if (row.pitcher_hitter == "H"){ return data.toFixed(0); } else if (row.pitcher_hitter == "P"){return "";}
                }, 
                "createdCell": function (td, cellData, rowData, row, col) {
                	if (rowData.pitcher_hitter == "H"){ setStatCellColor(td, rowData.hitter_z_hr, 1)}
                }, "sDefaultContent": ""},
            { "title": "SB", className: "dm_stat text-right dm_export" + " dm_league_cat_" + dm_leagueinfo.cat_hitter_sb,"width": 16, "mData": "hitter_sb", "render": function ( data, type, row ) {
            	if (row.pitcher_hitter == "H"){ return data.toFixed(0); } else if (row.pitcher_hitter == "P"){return "";}
                }, 
                "createdCell": function (td, cellData, rowData, row, col) {
                	if (rowData.pitcher_hitter == "H"){ setStatCellColor(td, rowData.hitter_z_sb, 1)}
                }, "sDefaultContent": ""},
            { "title": "R", className: "dm_stat text-right dm_export" + " dm_league_cat_" + dm_leagueinfo.cat_hitter_r,"width": 22, "mData": "hitter_runs", "render": function ( data, type, row ) {
            	if (row.pitcher_hitter == "H"){ return data.toFixed(0); } else if (row.pitcher_hitter == "P"){return "";}
                },
                "createdCell": function (td, cellData, rowData, row, col) {
                	if (rowData.pitcher_hitter == "H"){ setStatCellColor(td, rowData.hitter_z_runs, 1)}
                }, "sDefaultContent": ""},
            { "title": "RBI", className: "dm_stat text-right dm_export" + " dm_league_cat_" + dm_leagueinfo.cat_hitter_rbi,"width": 22, "mData": "hitter_rbi", "render": function ( data, type, row ) {		// Column 10
            	if (row.pitcher_hitter == "H"){ return data.toFixed(0) } else if (row.pitcher_hitter == "P"){return "";}
                }, 
                "createdCell": function (td, cellData, rowData, row, col) {
                	if (rowData.pitcher_hitter == "H"){ setStatCellColor(td, rowData.hitter_z_rbi, 1)}
                }, "sDefaultContent": ""},
            { "title": "OBP", className: "dm_stat text-right dm_export" + " dm_league_cat_" + dm_leagueinfo.cat_hitter_obp,"width": 25, "mData": "hitter_obp", "render": function ( data, type, row ) {		// Column 11
            	if (row.pitcher_hitter == "H"){ 
            		var avgnum = data.toFixed(3);
                    return avgnum.toString().substr(avgnum.length - 4);
            	} else if (row.pitcher_hitter == "P"){return "";}
                },
                "createdCell": function (td, cellData, rowData, row, col) {
                	if (rowData.pitcher_hitter == "H"){ setStatCellColor(td, rowData.hitter_z_obp, 1)}
                }, "sDefaultContent": ""},             
            { "title": "W", className: "dm_stat text-right dm_export" + " dm_league_cat_" + dm_leagueinfo.cat_pitcher_wins, "mData": "pitcher_w", "render": function ( data, type, row ) {
            	if (row.pitcher_hitter == "P"){ return data.toFixed(0) } else if (row.pitcher_hitter == "H"){return "";}
                },
                "createdCell": function (td, cellData, rowData, row, col) {
                	if (rowData.pitcher_hitter == "P"){ setStatCellColor(td, rowData.pitcher_z_wins, 1)}
                }, "sDefaultContent": ""},
            { "title": "SV", className: "dm_stat text-right dm_export" + " dm_league_cat_" + dm_leagueinfo.cat_pitcher_saves, "mData": "pitcher_sv", "render": function ( data, type, row ) {
            	if (row.pitcher_hitter == "P"){ return data.toFixed(0) } else if (row.pitcher_hitter == "H"){return "";}
                }, 
                "createdCell": function (td, cellData, rowData, row, col) {
                	if (rowData.pitcher_hitter == "P"){ setStatCellColor(td, rowData.pitcher_z_saves, 1)}
                }, "sDefaultContent": ""},
            { "title": "HLD", className: "dm_stat text-right dm_export" + " dm_league_cat_" + dm_leagueinfo.cat_pitcher_holds, "mData": "pitcher_hld","width": 23, "render": function ( data, type, row ) {
            	if (row.pitcher_hitter == "P"){ return data.toFixed(0) } else if (row.pitcher_hitter == "H"){return "";}
                }, 
                "createdCell": function (td, cellData, rowData, row, col) {
                	if (rowData.pitcher_hitter == "P"){ setStatCellColor(td, rowData.pitcher_z_holds, 1)}
                }, "sDefaultContent": ""},
            { "title": "SO", className: "dm_stat text-right dm_export" + " dm_league_cat_" + dm_leagueinfo.cat_pitcher_so, "mData": "pitcher_k","width": 24, "render": function ( data, type, row ) {
            	if (row.pitcher_hitter == "P"){ return data.toFixed(0) } else if (row.pitcher_hitter == "H"){return "";}
                }, 
                "createdCell": function (td, cellData, rowData, row, col) {
                	if (rowData.pitcher_hitter == "P"){ setStatCellColor(td, rowData.pitcher_z_so, 1)}
                }, "sDefaultContent": ""},
            { "title": "ERA", className: "dm_stat text-right dm_export" + " dm_league_cat_" + dm_leagueinfo.cat_pitcher_era, "mData": "pitcher_era","width": 26, "render": function ( data, type, row ) {
            	if (row.pitcher_hitter == "P"){ return data.toFixed(2) } else if (row.pitcher_hitter == "H"){return "";}
                }, 
                "createdCell": function (td, cellData, rowData, row, col) {
                	if (rowData.pitcher_hitter == "P"){ setStatCellColor(td, rowData.pitcher_z_era, 1)}
                }, "sDefaultContent": ""},
            { "title": "WHIP", className: "dm_stat text-right dm_export" + " dm_league_cat_" + dm_leagueinfo.cat_pitcher_whip, "mData": "pitcher_whip","width": 28 , "render": function ( data, type, row ) {		// Column 17
            	if (row.pitcher_hitter == "P"){ return data.toFixed(2) } else if (row.pitcher_hitter == "H"){return "";}
                }, 
                "createdCell": function (td, cellData, rowData, row, col) {
                	if (rowData.pitcher_hitter == "P"){ setStatCellColor(td, rowData.pitcher_z_whip, 1)}
                }, "sDefaultContent": ""},
                
            { "visible": false, className: "dm_zscore text-right dm_export" + " dm_league_cat_" + dm_leagueinfo.cat_hitter_avg, "title": "zAVG", "mData": "hitter_z_avg", "render": function ( data, type, row ) {
            	if (row.pitcher_hitter == "H"){return data.toFixed(2);} else if (row.pitcher_hitter == "P"){return "";}
                },
                "createdCell": function (td, cellData, rowData, row, col) {
                	if (rowData.pitcher_hitter == "H"){ setStatCellColor(td, rowData.hitter_z_avg, 1)}
                }, "sDefaultContent": "0" },
            { "visible": false, className: "dm_zscore text-right dm_export" + " dm_league_cat_" + dm_leagueinfo.cat_hitter_hr, "title": "zHR", "mData": "hitter_z_hr", "render": function ( data, type, row ) {
            	if (row.pitcher_hitter == "H"){return data.toFixed(2);} else if (row.pitcher_hitter == "P"){return "";}
                }, 
                "createdCell": function (td, cellData, rowData, row, col) {
                	if (rowData.pitcher_hitter == "H"){ setStatCellColor(td, rowData.hitter_z_hr, 1)}
                }, "sDefaultContent": "0" },
            { "visible": false, className: "dm_zscore text-right dm_export" + " dm_league_cat_" + dm_leagueinfo.cat_hitter_sb, "title": "zSB", "mData": "hitter_z_sb", "render": function ( data, type, row ) {
            	if (row.pitcher_hitter == "H"){return data.toFixed(2);} else if (row.pitcher_hitter == "P"){return "";}
                },"createdCell": function (td, cellData, rowData, row, col) {
                	if (rowData.pitcher_hitter == "H"){ setStatCellColor(td, rowData.hitter_z_sb, 1)}
                },  "sDefaultContent": "0" },
            { "visible": false, className: "dm_zscore text-right dm_export" + " dm_league_cat_" + dm_leagueinfo.cat_hitter_r, "title": "zR", "mData": "hitter_z_runs", "render": function ( data, type, row ) {	
            	if (row.pitcher_hitter == "H"){return data.toFixed(2);} else if (row.pitcher_hitter == "P"){return "";}
                },
                "createdCell": function (td, cellData, rowData, row, col) {
                	if (rowData.pitcher_hitter == "H"){ setStatCellColor(td, rowData.hitter_z_runs, 1)}
                }, "sDefaultContent": "0" },
            { "visible": false, className: "dm_zscore text-right dm_export" + " dm_league_cat_" + dm_leagueinfo.cat_hitter_rbi, "title": "zRBI", "mData": "hitter_z_rbi", "render": function ( data, type, row ) {	// Column 22
            	if (row.pitcher_hitter == "H"){return data.toFixed(2);} else if (row.pitcher_hitter == "P"){return "";}
                },
                "createdCell": function (td, cellData, rowData, row, col) {
                	if (rowData.pitcher_hitter == "H"){ setStatCellColor(td, rowData.hitter_z_rbi, 1)}
                }, "sDefaultContent": "0" },
            { "visible": false, className: "dm_zscore text-right dm_export" + " dm_league_cat_" + dm_leagueinfo.cat_hitter_obp, "title": "zOBP", "mData": "hitter_z_obp", "render": function ( data, type, row ) {	// Column 23
            	if (row.pitcher_hitter == "H"){return data.toFixed(2);} else if (row.pitcher_hitter == "P"){return "";}
                },
                "createdCell": function (td, cellData, rowData, row, col) {
                	if (rowData.pitcher_hitter == "H"){ setStatCellColor(td, rowData.hitter_z_obp, 1)}
                }, "sDefaultContent": "0" },
            { "visible": false, className: "dm_zscore text-right dm_export" + " dm_league_cat_" + dm_leagueinfo.cat_pitcher_w, "title": "zW", "mData": "pitcher_z_wins", "render": function ( data, type, row ) {
            	if (row.pitcher_hitter == "P"){return data.toFixed(2);} else if (row.pitcher_hitter == "H"){return "";}
                },
                "createdCell": function (td, cellData, rowData, row, col) {
                	if (rowData.pitcher_hitter == "P"){ setStatCellColor(td, rowData.pitcher_z_wins, 1)}
                }, "sDefaultContent": "0" },
            { "visible": false, className: "dm_zscore text-right dm_export" + " dm_league_cat_" + dm_leagueinfo.cat_pitcher_saves, "title": "zSV", "mData": "pitcher_z_saves", "render": function ( data, type, row ) {
            	if (row.pitcher_hitter == "P"){return data.toFixed(2);} else if (row.pitcher_hitter == "H"){return "";}
                }, 
                "createdCell": function (td, cellData, rowData, row, col) {
                	if (rowData.pitcher_hitter == "P"){ setStatCellColor(td, rowData.pitcher_z_saves, 1)}
                }, "sDefaultContent": "0" },
            { "visible": false, className: "dm_zscore text-right dm_export" + " dm_league_cat_" + dm_leagueinfo.cat_pitcher_holds, "title": "zHLD", "mData": "pitcher_z_holds", "render": function ( data, type, row ) {
            	if (row.pitcher_hitter == "P"){return data.toFixed(2);} else if (row.pitcher_hitter == "H"){return "";}
                }, 
                "createdCell": function (td, cellData, rowData, row, col) {
                	if (rowData.pitcher_hitter == "P"){ setStatCellColor(td, rowData.pitcher_z_holds, 1)}
                }, "sDefaultContent": "0" },
            { "visible": false, className: "dm_zscore text-right dm_export" + " dm_league_cat_" + dm_leagueinfo.cat_pitcher_so, "title": "zSO", "mData": "pitcher_z_so", "render": function ( data, type, row ) {
            	if (row.pitcher_hitter == "P"){return data.toFixed(2);} else if (row.pitcher_hitter == "H"){return "";}
                }, 
                "createdCell": function (td, cellData, rowData, row, col) {
                	if (rowData.pitcher_hitter == "P"){ setStatCellColor(td, rowData.pitcher_z_so, 1)}
                }, "sDefaultContent": "0" },
            { "visible": false, className: "dm_zscore text-right dm_export" + " dm_league_cat_" + dm_leagueinfo.cat_pitcher_whip, "title": "zWHIP", "mData": "pitcher_z_whip", "render": function ( data, type, row ) {		
            	if (row.pitcher_hitter == "P"){return data.toFixed(2);} else if (row.pitcher_hitter == "H"){return "";}
                }, 
                "createdCell": function (td, cellData, rowData, row, col) {
                	if (rowData.pitcher_hitter == "P"){ setStatCellColor(td, rowData.pitcher_z_whip, 1)}
                }, "sDefaultContent": "0" },
            { "visible": false, className: "dm_zscore text-right dm_export" + " dm_league_cat_" + dm_leagueinfo.cat_pitcher_era, "title": "zERA", "mData": "pitcher_z_era", "render": function ( data, type, row ) {	// Column 29
            	if (row.pitcher_hitter == "P"){return data.toFixed(2);} else if (row.pitcher_hitter == "H"){return "";}
                }, 
                "createdCell": function (td, cellData, rowData, row, col) {
                	if (rowData.pitcher_hitter == "P"){ setStatCellColor(td, rowData.pitcher_z_era, 1)}
                }, "sDefaultContent": "0" },
                
            { "title": "NPV", className: "dm_export text-right", "mData": "total_z", "width": 26, "render": function ( data, type, row ) {
            		if (data == null) return 0;
            		else return data.toFixed(1);
            	},
            	"createdCell": function (td, cellData, rowData, row, col) { setStatCellColor(td, cellData, 5)}, "sDefaultContent": "0"},    
            { "title": "$", className: "dm_export text-right", "mData": "init_auction_value", "width": 22, "render": function ( data, type, row ) {
            		return "$" + data.toFixed(0);
                }, "sDefaultContent": "0"},
            { "title": "<i class='fa fa-bolt'></i>-$", className: "dm_export text-right", "mData": "live_auction_value", "width": 22, "render": function ( data, type, row ) {
            	if (data == "") return data;
            	else return "$" + data.toFixed(0);
            }, "sDefaultContent": ""},
            { "visible": false, "title": "Action", "mData": "leagueteam_id","width": 56
            	/*
            	, "render": function ( data, type, row ) {			
	            	var buttons;
	            	if (data == 0)
	            		buttons = "<button type='button' class='btn btn-primary btn-xs btn-draft' data-toggle='tooltip' title='Draft Player'><i class='fa fa-user-plus'></i></button>";
	            	else buttons = "<button type='button' class='btn btn-default btn-xs btn-undraft' data-toggle='tooltip' title='Undraft Player'><i class='fa fa-user-times'></i></button>";
	            	buttons = buttons + "&nbsp;<button type='button' class='btn btn-success btn-xs btn-playerinfo' data-toggle='tooltip' title='Player Info Page'><i class='fa fa-external-link'></i></button>";
	            	return buttons;
            	}*/
            }, 
            { "title": "Team", className: "dm_export", "mData": "leagueteam_name","width": 68, "sDefaultContent": "", render: $.fn.dataTable.render.ellipsis( 9 )}, 	// Column 34
            { "visible": false, "title": "id", "mData": "id", "sDefaultContent": "" },
            { "visible": false, "title": "Roster Position", "mData": "team_roster_position", "sDefaultContent": "" },
            { "visible": false, "title": "Team Salary", "mData": "team_player_salary", "sDefaultContent": "" },
            { "visible": false, "title": "LP ID", "mData": "league_player_id", "sDefaultContent": "" },
            { "visible": false, "title": "unk", "mData": "unknownplayer", "sDefaultContent": "" },									// Column 39
            { "visible": false, className: "dm_export", "title": "note", "mData": "team_player_note", "sDefaultContent": "" },
            { "visible": false, "title": "hitter_hits", "mData": "hitter_hits", "sDefaultContent": "" },
            { "visible": false, "title": "hitter_ab", "mData": "hitter_ab", "sDefaultContent": "" },
            { "visible": false, "title": "hitter_pa", "mData": "hitter_pa", "sDefaultContent": "" },
            { "visible": false, "title": "hitter_bb", "mData": "hitter_bb", "sDefaultContent": "" },
            { "visible": false, "title": "hitter_hbp", "mData": "hitter_hbp", "sDefaultContent": "" },
            { "visible": false, "title": "pitcher_er", "mData": "pitcher_er", "sDefaultContent": "" },
            { "visible": false, "title": "pitcher_ip", "mData": "pitcher_ip", "sDefaultContent": "" },
            { "visible": false, "title": "pitcher_bb", "mData": "pitcher_bb", "sDefaultContent": "" },
            { "visible": false, "title": "pitcher_hits", "mData": "pitcher_hits", "sDefaultContent": "" },
            { "visible": false, "title": "favorite_flag", "mData": "favorite_flag", "sDefaultContent": "" },  		// Column 50
        ]
        };
	
	if (isInitialLoad) 	{
		// console.log("window height: " + calcDataTableHeight());
		data_table = table_element.dataTable(config);
	} else {
		// console.log("window height: " + calcDataTableHeight());
		// data_table = table_element.DataTable();
		// data_table.destroy();
		// table_element.empty();
		
		// Destroy tables
		if ( $.fn.DataTable.isDataTable( '#playergrid_table' ) ) {
			data_table = table_element.DataTable();
			data_table.destroy();
			table_element.empty();
			}

		data_table = table_element.dataTable(config);
		data_table = table_element.DataTable();
		// Filter out unknown players
		data_table.columns( 39 ).search( false ).draw();

		// Show category columns  used by league
	    var table = $('#playergrid_table').DataTable();
	    var columns_in = table.columns('.dm_league_cat_true .dm_stat');
	    var columns_out = table.columns('.dm_league_cat_false');
	    columns_in.visible(true);
	    columns_out.visible(false);
		
		// Update the team info tab
		updateTeamInfoTab();
		
		// Update live auction values
		calcLiveAuctionValue();
	}

	/*
	// On Click of the Info button in the Player Grid Table
    $('#playergrid_table tbody').on( 'click', '.btn-playerinfo', function () {

    	var data_table = $('#playergrid_table').DataTable();
        var data = data_table.row( $(this).parents('tr') ).data();
        var win = window.open("http://www.fangraphs.com/players.aspx?lastname=" + data.full_name, '_blank');
        win.focus();
        
    } );
	
	// On Click of the Draft button in the Player Grid Table
    $('#playergrid_table tbody').on( 'click', '.btn-draft', function () {
    	
		// New, may want to remove later
		loadTeamSelect(dm_globalteamlist);
		
		$("#select-ontheblock-draftteam").removeAttr("disabled");
		$("#select-ontheblock-draftamt").removeAttr("disabled");
		$("#select-ontheblock-draftposition").attr("disabled","disabled");

    	var data_table = $('#playergrid_table').DataTable();
        var data = data_table.row( $(this).parents('tr') ).data();
        playerdraftrow = data;
        
        resetDraftPlayerModal();
        $("#btn-draftplayer").attr("disabled","disabled");
    	// $("#btn-draftplayer").removeAttr("disabled");
     // $("#header-draftplayer").text("Draft Player: " + data.full_name + " (" + data.team + ")");
     // $("#header-draftplayer").val(data.id);
     // $("#lbl-draftprevplayer").text(data.full_name + " (" + data.team + ")");
        
        $("#header-ontheblock-draftplayer").text(data.full_name + ", " + data.team + " - " + data.custom_position + " - $" + data.live_auction_value);
        $("#header-ontheblock-draftplayer").val(data.id);
        
        var proj_string = "";
        if (data.pitcher_hitter == "P"){
        	console.log(dm_leagueinfo.cat_pitcher_w + ", " + dm_leagueinfo.cat_pitcher_sv)
        	if (dm_leagueinfo.cat_pitcher_wins) proj_string = proj_string + round(data.pitcher_w,0) + " W, ";
        	if (dm_leagueinfo.cat_pitcher_saves) proj_string = proj_string + round(data.pitcher_sv,0) + " SV, ";
        	if (dm_leagueinfo.cat_pitcher_holds) proj_string = proj_string + round(data.pitcher_hld,0) + " HOLDS, ";
        	if (dm_leagueinfo.cat_pitcher_so) proj_string = proj_string + round(data.pitcher_k,0) + " K, ";
        	if (dm_leagueinfo.cat_pitcher_era) proj_string = proj_string + round(data.pitcher_era,2) + " ERA, ";
        	if (dm_leagueinfo.cat_pitcher_whip) proj_string = proj_string + round(data.pitcher_whip,2) + " WHIP, ";
        	$("#header2-ontheblock-draftplayer").html(dm_leagueinfo.league_year + " Projection:<br>" + proj_string.slice(0,-2));
        }
        else {
        	if (dm_leagueinfo.cat_hitter_avg) proj_string = proj_string + data.hitter_avg.toString().substr(1) + " AVG, ";
        	if (dm_leagueinfo.cat_hitter_hr) proj_string = proj_string + round(data.hitter_hr,0) + " HR, ";
        	if (dm_leagueinfo.cat_hitter_r) proj_string = proj_string + round(data.hitter_runs,0) + " R, ";
        	if (dm_leagueinfo.cat_hitter_sb) proj_string = proj_string + round(data.hitter_sb,0) + " SB, ";
        	if (dm_leagueinfo.cat_hitter_rbi) proj_string = proj_string + round(data.hitter_rbi,0) + " RBI, ";
        	if (dm_leagueinfo.cat_hitter_obp) proj_string = proj_string + data.hitter_obp.toString().substr(1) + " OBP, ";
        	$("#header2-ontheblock-draftplayer").html(dm_leagueinfo.league_year + " Projection:<br>" + proj_string.slice(0,-2));
        }

        $("#btn-draftamt-plus").removeAttr("disabled");
        $("#btn-draftamt-minus").removeAttr("disabled");
        // $("#table-selectdraftposition").hide();
		// $("#btn-draftposmoveup").hide();
		// $("#btn-draftposmovedown").hide();
        
        // $("#draftplayer-modal").modal("show");
        // console.log("Player id: " + $("#header-draftplayer").val());
    } );
    
    // On Click of the Undraft button in the Player Grid Table
    $('#playergrid_table tbody').on( 'click', '.btn-undraft', function () {

    	var data_table = $('#playergrid_table').DataTable();
        var data = data_table.row( $(this).parents('tr') ).data();
        showUndraftPlayerDialog(data);
        
    } );   */
    
    // On Select of the Player Grid Table
	var select_data_table = $('#playergrid_table').DataTable();
	select_data_table
    .on( 'select', function ( e, dt, type, indexes ) {

    	var select_data_table_b = $('#playergrid_table').DataTable();
        var row = select_data_table_b.rows( indexes ).data()[0];
        
        // Set global variable for player row
        playerselectedrow = row;
        updatePlayerInfoPanel(row);
		
    } )
    .on( 'deselect', function ( e, dt, type, indexes ) {
    	// $('#info-tabs a[href="#tab-teaminfo"]').tab('show');
    	// clearPlayerInfoTab();
    } );

}

// For a given player ID, select the player in the player grid table
// and go to the page that the player is on.
function selectPlayerInPlayerGridTable (playerid) {
    var playertable = $('#playergrid_table').DataTable();
    var playerrow = playertable.row('#' + playerid + '');
    playerrow.select();
    
    var displayIndex = playertable.rows( { order: 'applied', search: 'applied' } )
    	.indexes().indexOf( playerrow.index() ); 
    var pageSize = playertable.page.len();
     
    playertable.page( parseInt( displayIndex / pageSize, 10 ) ).draw( false );
    
	playertable = null;
	playerrow = null;
}

function loadPositionalAnlaysisTable(data, isInitialLoad)
{
	if (!isInitialLoad){}

	var table_element = $('#pos_analysis_table');
	var data_table;
	var config;
	config = {
        "bSort" : false,
        "searching": false,
        "paging": false,
        "info": false,
        select: {
            style:    'single'
        },
		responsive: true,
    	"processing": true,
        data: data,
        // "order": [[ 14, "desc" ]],
        "columns": [
            { "title": "Pos", className: "dm_export", "mData": "position", 
                "createdCell": function (td, cellData, rowData, row, col) {
                	$(td).css({"font-size": "16px"})
                }, "sDefaultContent": ""},
            { "title": "Open", className: "dm_export", "mData": "open_slots", "sDefaultContent": ""},
            { "title": "1", className: "dm_export", "mData": "total_z_1", "render": function ( data, type, row ) {return data.toFixed(1);},
            	"createdCell": function (td, cellData, rowData, row, col) { setStatCellColor(td, cellData, 5)}, "sDefaultContent": "0"}, 
            { "title": "2", className: "dm_export", "mData": "total_z_2", "render": function ( data, type, row ) {return data.toFixed(1);},
            	"createdCell": function (td, cellData, rowData, row, col) { setStatCellColor(td, cellData, 5)}, "sDefaultContent": "0"}, 
            { "title": "3", className: "dm_export", "mData": "total_z_3", "render": function ( data, type, row ) {return data.toFixed(1);},
            	"createdCell": function (td, cellData, rowData, row, col) { setStatCellColor(td, cellData, 5)}, "sDefaultContent": "0"}, 
            { "title": "4", className: "dm_export", "mData": "total_z_4", "render": function ( data, type, row ) {return data.toFixed(1);},
            	"createdCell": function (td, cellData, rowData, row, col) { setStatCellColor(td, cellData, 5)}, "sDefaultContent": "0"}, 
            { "title": "5", className: "dm_export", "mData": "total_z_5", "render": function ( data, type, row ) {return data.toFixed(1);},
            	"createdCell": function (td, cellData, rowData, row, col) { setStatCellColor(td, cellData, 5)}, "sDefaultContent": "0"}, 
            { "title": "6", className: "dm_export", "mData": "total_z_6", "render": function ( data, type, row ) {return data.toFixed(1);},
            	"createdCell": function (td, cellData, rowData, row, col) { setStatCellColor(td, cellData, 5)}, "sDefaultContent": "0"}, 
            { "title": "7", className: "dm_export", "mData": "total_z_7", "render": function ( data, type, row ) {return data.toFixed(1);},
            	"createdCell": function (td, cellData, rowData, row, col) { setStatCellColor(td, cellData, 5)}, "sDefaultContent": "0"}, 
            { "title": "8", className: "dm_export", "mData": "total_z_8", "render": function ( data, type, row ) {return data.toFixed(1);},
            	"createdCell": function (td, cellData, rowData, row, col) { setStatCellColor(td, cellData, 5)}, "sDefaultContent": "0"},
            { "title": "9", className: "dm_export", "mData": "total_z_9", "render": function ( data, type, row ) {return data.toFixed(1);},
            	"createdCell": function (td, cellData, rowData, row, col) { setStatCellColor(td, cellData, 5)}, "sDefaultContent": "0"}, 
            { "title": "10", className: "dm_export", "mData": "total_z_10", "render": function ( data, type, row ) {return data.toFixed(1);},
            	"createdCell": function (td, cellData, rowData, row, col) { setStatCellColor(td, cellData, 5)}, "sDefaultContent": "0"}
        ]
        };

	if (isInitialLoad) 	{
		data_table = table_element.dataTable(config);
	} else {
		data_table = table_element.DataTable();
		data_table.off('select');
		data_table.destroy();
		table_element.empty();
		data_table = table_element.dataTable(config);
		data_table = table_element.DataTable();
	}
	
    // On Select of the Positional Analysis Table
	var select_data_table = $('#pos_analysis_table').DataTable();
	select_data_table
    .on( 'select', function ( e, dt, type, indexes ) {
    	var select_data_table_b = $('#pos_analysis_table').DataTable();
        var row = select_data_table_b.rows( indexes ).data()[0];
        if (row.position == "C") loadPositionalTable(dm_filtered_data_c, $("#pos_c_table"), false, true, "#chart-c");
        else if (row.position == "1B") loadPositionalTable(dm_filtered_data_1b, $("#pos_c_table"), false, true, "#chart-c");
        else if (row.position == "2B") loadPositionalTable(dm_filtered_data_2b, $("#pos_c_table"), false, true, "#chart-c");
        else if (row.position == "SS") loadPositionalTable(dm_filtered_data_ss, $("#pos_c_table"), false, true, "#chart-c");
        else if (row.position == "3B") loadPositionalTable(dm_filtered_data_3b, $("#pos_c_table"), false, true, "#chart-c");
        else if (row.position == "OF") loadPositionalTable(dm_filtered_data_of, $("#pos_c_table"), false, true, "#chart-c");
        else if (row.position == "MI") loadPositionalTable(dm_filtered_data_mi, $("#pos_c_table"), false, true, "#chart-c");
        else if (row.position == "CI") loadPositionalTable(dm_filtered_data_ci, $("#pos_c_table"), false, true, "#chart-c");
        else if (row.position == "SP") loadPositionalTable(dm_filtered_data_sp, $("#pos_c_table"), false, false, "#chart-c");
        else if (row.position == "RP") loadPositionalTable(dm_filtered_data_rp, $("#pos_c_table"), false, false, "#chart-c");
			
    } )
    // .on( 'deselect', function ( e, dt, type, indexes ) { } )
    ;

}

// Mini player table for a specific position
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
			    offset: 100,
			}};
		} else {
			options = {
			  referenceValue: 0,
			  seriesBarDistance: 10,
			  reverseData: true,
			  horizontalBars: true,
			  axisY: {
			    offset: 100,
			}};
		}
		
		// console.log("data name 8: " + data[8].full_name);
		// console.log("data name 9: " + data[9].full_name);
		// console.log("data z 8: " + data[8].total_z);
		// console.log("data z 9: " + data[9].total_z);
		
		var chart = $(chartid + " .ct-chart");
		chart.detach();
		chart.off('draw');

		chart = new Chartist.Bar(chartid, {
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
            { "title": "Player", className: "dm_export", "mData": "full_name",  "render": function ( data, type, row ) {
            	if ((row.team_player_note == null)||(row.team_player_note.trim() == ""))
            		return data + " (" + row.team + ")";
            	else return data + " (" + row.team + ")&nbsp;&nbsp;<i class='fa fa-file-text'></i>"; 	
                }},
            { "title": "Ag", className: "dm_export", "mData": "age", "sDefaultContent": "", 
                	"createdCell": function (td, cellData, rowData, row, col) {setAgeCellColor(td, cellData)} 
                },
            { "visible": false, "title": "Team", "mData": "team", "sDefaultContent": ""},
            { "title": "S", className: "dm_export", "mData": "dc_status", "sDefaultContent": ""},
            { "title": "Avg", className: "dm_stat dm_export" + " dm_league_cat_" + dm_leagueinfo.cat_hitter_avg, "mData": "hitter_avg", "render": function ( data, type, row ) {
            	if (row.pitcher_hitter == "H"){ 
            		var avgnum = data.toFixed(3);
                    return avgnum.toString().substr(avgnum.length - 4);
            	} else if (row.pitcher_hitter == "P"){return "";}
                },
                "createdCell": function (td, cellData, rowData, row, col) {
                	if (rowData.pitcher_hitter == "H"){ setStatCellColor(td, rowData.hitter_z_avg, 1)}
                },
                "sDefaultContent": ""},
            { "title": "HR", className: "dm_stat dm_export" + " dm_league_cat_" + dm_leagueinfo.cat_hitter_hr, "mData": "hitter_hr", "render": function ( data, type, row ) {
            	if (row.pitcher_hitter == "H"){ return data.toFixed(0); } else if (row.pitcher_hitter == "P"){return "";}
                }, 
                "createdCell": function (td, cellData, rowData, row, col) {
                	if (rowData.pitcher_hitter == "H"){ setStatCellColor(td, rowData.hitter_z_hr, 1)}
                }, "sDefaultContent": ""},
            { "title": "SB", className: "dm_stat dm_export" + " dm_league_cat_" + dm_leagueinfo.cat_hitter_sb, "mData": "hitter_sb", "render": function ( data, type, row ) {
            	if (row.pitcher_hitter == "H"){ return data.toFixed(0); } else if (row.pitcher_hitter == "P"){return "";}
                }, 
                "createdCell": function (td, cellData, rowData, row, col) {
                	if (rowData.pitcher_hitter == "H"){ setStatCellColor(td, rowData.hitter_z_sb, 1)}
                }, "sDefaultContent": ""},
            { "title": "R", className: "dm_stat dm_export" + " dm_league_cat_" + dm_leagueinfo.cat_hitter_r, "mData": "hitter_runs", "render": function ( data, type, row ) {
            	if (row.pitcher_hitter == "H"){ return data.toFixed(0); } else if (row.pitcher_hitter == "P"){return "";}
                },
                "createdCell": function (td, cellData, rowData, row, col) {
                	if (rowData.pitcher_hitter == "H"){ setStatCellColor(td, rowData.hitter_z_runs, 1)}
                }, "sDefaultContent": ""},
            { "title": "BI", className: "dm_stat dm_export" + " dm_league_cat_" + dm_leagueinfo.cat_hitter_rbi, "mData": "hitter_rbi", "render": function ( data, type, row ) {
            	if (row.pitcher_hitter == "H"){ return data.toFixed(0) } else if (row.pitcher_hitter == "P"){return "";}
                }, 
                "createdCell": function (td, cellData, rowData, row, col) {
                	if (rowData.pitcher_hitter == "H"){ setStatCellColor(td, rowData.hitter_z_rbi, 1)}
                }, "sDefaultContent": ""},
            { "title": "OBP", className: "dm_stat dm_export" + " dm_league_cat_" + dm_leagueinfo.cat_hitter_obp, "mData": "hitter_obp", "render": function ( data, type, row ) {
            	if (row.pitcher_hitter == "H"){ 
            		var avgnum = data.toFixed(3);
                    return avgnum.toString().substr(avgnum.length - 4);
            	} else if (row.pitcher_hitter == "P"){return "";}
                },
                "createdCell": function (td, cellData, rowData, row, col) {
                	if (rowData.pitcher_hitter == "H"){ setStatCellColor(td, rowData.hitter_z_obp, 1)}
                },
                "sDefaultContent": ""},

            { "visible": false, className: "dm_zscore dm_export" + " dm_league_cat_" + dm_leagueinfo.cat_hitter_avg, "title": "zAVG", "mData": "hitter_z_avg", "render": function ( data, type, row ) {
            	if (row.pitcher_hitter == "H"){return data.toFixed(2);} else if (row.pitcher_hitter == "P"){return "";}
                },
                "createdCell": function (td, cellData, rowData, row, col) {
                	if (rowData.pitcher_hitter == "H"){ setStatCellColor(td, rowData.hitter_z_avg, 1)}
                }, "sDefaultContent": "0" },
            { "visible": false, className: "dm_zscore dm_export" + " dm_league_cat_" + dm_leagueinfo.cat_hitter_hr, "title": "zHR", "mData": "hitter_z_hr", "render": function ( data, type, row ) {
            	if (row.pitcher_hitter == "H"){return data.toFixed(2);} else if (row.pitcher_hitter == "P"){return "";}
                }, 
                "createdCell": function (td, cellData, rowData, row, col) {
                	if (rowData.pitcher_hitter == "H"){ setStatCellColor(td, rowData.hitter_z_hr, 1)}
                }, "sDefaultContent": "0" },
            { "visible": false, className: "dm_zscore dm_export" + " dm_league_cat_" + dm_leagueinfo.cat_hitter_sb, "title": "zSB", "mData": "hitter_z_sb", "render": function ( data, type, row ) {
            	if (row.pitcher_hitter == "H"){return data.toFixed(2);} else if (row.pitcher_hitter == "P"){return "";}
                },"createdCell": function (td, cellData, rowData, row, col) {
                	if (rowData.pitcher_hitter == "H"){ setStatCellColor(td, rowData.hitter_z_sb, 1)}
                },  "sDefaultContent": "0" },
            { "visible": false, className: "dm_zscore dm_export" + " dm_league_cat_" + dm_leagueinfo.cat_hitter_r, "title": "zR", "mData": "hitter_z_runs", "render": function ( data, type, row ) {
            	if (row.pitcher_hitter == "H"){return data.toFixed(2);} else if (row.pitcher_hitter == "P"){return "";}
                },
                "createdCell": function (td, cellData, rowData, row, col) {
                	if (rowData.pitcher_hitter == "H"){ setStatCellColor(td, rowData.hitter_z_runs, 1)}
                }, "sDefaultContent": "0" },
            { "visible": false, className: "dm_zscore dm_export" + " dm_league_cat_" + dm_leagueinfo.cat_hitter_rbi, "title": "zRBI", "mData": "hitter_z_rbi", "render": function ( data, type, row ) {
            	if (row.pitcher_hitter == "H"){return data.toFixed(2);} else if (row.pitcher_hitter == "P"){return "";}
                },
                "createdCell": function (td, cellData, rowData, row, col) {
                	if (rowData.pitcher_hitter == "H"){ setStatCellColor(td, rowData.hitter_z_rbi, 1)}
                }, "sDefaultContent": "0" },
            { "visible": false, className: "dm_zscore dm_export" + " dm_league_cat_" + dm_leagueinfo.cat_hitter_obp, "title": "zOBP", "mData": "hitter_z_obp", "render": function ( data, type, row ) {
            	if (row.pitcher_hitter == "H"){return data.toFixed(2);} else if (row.pitcher_hitter == "P"){return "";}
                },
                "createdCell": function (td, cellData, rowData, row, col) {
                	if (rowData.pitcher_hitter == "H"){ setStatCellColor(td, rowData.hitter_z_obp, 1)}
                }, "sDefaultContent": "0" },
                
            { "title": "Z", className: "dm_export", "mData": "total_z", "render": function ( data, type, row ) {return data.toFixed(1);},
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
	            { "title": "Player", className: "dm_export", "mData": "full_name",  "render": function ( data, type, row ) {
	            	if ((row.team_player_note == null)||(row.team_player_note.trim() == ""))
	            		return data + " (" + row.team + ")";
	            	else return data + " (" + row.team + ")&nbsp;&nbsp;<i class='fa fa-file-text'></i>"; 	
	                }},
	            { "title": "Ag", className: "dm_export", "mData": "age", "sDefaultContent": "",
	                	"createdCell": function (td, cellData, rowData, row, col) {setAgeCellColor(td, cellData)} 
	                },
	            { "visible": false, "title": "Team", "mData": "team", "sDefaultContent": ""},
	            { "title": "St", className: "dm_export", "mData": "dc_status", "sDefaultContent": ""},
	         
	            { "title": "W", className: "dm_stat dm_export" + " dm_league_cat_" + dm_leagueinfo.cat_pitcher_w, "mData": "pitcher_w", "render": function ( data, type, row ) {
	            	if (row.pitcher_hitter == "P"){ return data.toFixed(0) } else if (row.pitcher_hitter == "H"){return "";}
	                },
	                "createdCell": function (td, cellData, rowData, row, col) {
	                	if (rowData.pitcher_hitter == "P"){ setStatCellColor(td, rowData.pitcher_z_wins, 1)}
	                }, "sDefaultContent": ""},
	            { "title": "SV", className: "dm_stat dm_export" + " dm_league_cat_" + dm_leagueinfo.cat_pitcher_saves, "mData": "pitcher_sv", "render": function ( data, type, row ) {
	            	if (row.pitcher_hitter == "P"){ return data.toFixed(0) } else if (row.pitcher_hitter == "H"){return "";}
	                }, 
	                "createdCell": function (td, cellData, rowData, row, col) {
	                	if (rowData.pitcher_hitter == "P"){ setStatCellColor(td, rowData.pitcher_z_saves, 1)}
	                }, "sDefaultContent": ""},
	            { "title": "HLD", className: "dm_stat dm_export" + " dm_league_cat_" + dm_leagueinfo.cat_pitcher_holds, "mData": "pitcher_hld", "render": function ( data, type, row ) {
	            	if (row.pitcher_hitter == "P"){ return data.toFixed(0) } else if (row.pitcher_hitter == "H"){return "";}
	                }, 
	                "createdCell": function (td, cellData, rowData, row, col) {
	                	if (rowData.pitcher_hitter == "P"){ setStatCellColor(td, rowData.pitcher_z_holds, 1)}
	                }, "sDefaultContent": ""},
	            { "title": "SO", className: "dm_stat dm_export" + " dm_league_cat_" + dm_leagueinfo.cat_pitcher_so, "mData": "pitcher_k", "render": function ( data, type, row ) {
	            	if (row.pitcher_hitter == "P"){ return data.toFixed(0) } else if (row.pitcher_hitter == "H"){return "";}
	                }, 
	                "createdCell": function (td, cellData, rowData, row, col) {
	                	if (rowData.pitcher_hitter == "P"){ setStatCellColor(td, rowData.pitcher_z_so, 1)}
	                }, "sDefaultContent": ""},
	            { "title": "ERA", className: "dm_stat dm_export" + " dm_league_cat_" + dm_leagueinfo.cat_pitcher_era, "mData": "pitcher_era", "render": function ( data, type, row ) {
	            	if (row.pitcher_hitter == "P"){ return data.toFixed(2) } else if (row.pitcher_hitter == "H"){return "";}
	                }, 
	                "createdCell": function (td, cellData, rowData, row, col) {
	                	if (rowData.pitcher_hitter == "P"){ setStatCellColor(td, rowData.pitcher_z_era, 1)}
	                }, "sDefaultContent": ""},
	            { "title": "WHIP", className: "dm_stat dm_export" + " dm_league_cat_" + dm_leagueinfo.cat_pitcher_whip, "mData": "pitcher_whip", "render": function ( data, type, row ) {
	            	if (row.pitcher_hitter == "P"){ return data.toFixed(2) } else if (row.pitcher_hitter == "H"){return "";}
	                }, 
	                "createdCell": function (td, cellData, rowData, row, col) {
	                	if (rowData.pitcher_hitter == "P"){ setStatCellColor(td, rowData.pitcher_z_whip, 1)}
	                }, "sDefaultContent": ""},
	                
	            { "visible": false, className: "dm_zscore dm_export" + " dm_league_cat_" + dm_leagueinfo.cat_pitcher_w, "title": "zW", "mData": "pitcher_z_wins", "render": function ( data, type, row ) {
	            	if (row.pitcher_hitter == "P"){return data.toFixed(2);} else if (row.pitcher_hitter == "H"){return "";}
	                },
	                "createdCell": function (td, cellData, rowData, row, col) {
	                	if (rowData.pitcher_hitter == "P"){ setStatCellColor(td, rowData.pitcher_z_wins, 1)}
	                }, "sDefaultContent": "0" },
	            { "visible": false, className: "dm_zscore dm_export" + " dm_league_cat_" + dm_leagueinfo.cat_pitcher_saves, "title": "zSV", "mData": "pitcher_z_saves", "render": function ( data, type, row ) {
	            	if (row.pitcher_hitter == "P"){return data.toFixed(2);} else if (row.pitcher_hitter == "H"){return "";}
	                }, 
	                "createdCell": function (td, cellData, rowData, row, col) {
	                	if (rowData.pitcher_hitter == "P"){ setStatCellColor(td, rowData.pitcher_z_saves, 1)}
	                }, "sDefaultContent": "0" },
	            /*
	            { "visible": false, className: "dm_zscore dm_export" + " dm_league_cat_" + dm_leagueinfo.cat_pitcher_holds, "title": "zHLD", "mData": "pitcher_z_hld", "render": function ( data, type, row ) {
	            	if (row.pitcher_hitter == "P"){return data.toFixed(2);} else if (row.pitcher_hitter == "H"){return "";}
	                }, 
	                "createdCell": function (td, cellData, rowData, row, col) {
	                	if (rowData.pitcher_hitter == "P"){ setStatCellColor(td, rowData.pitcher_z_holds, 1)}
	                }, "sDefaultContent": "0" },
	            */
	            { "visible": false, className: "dm_zscore dm_export" + " dm_league_cat_" + dm_leagueinfo.cat_pitcher_so, "title": "zSO", "mData": "pitcher_z_so", "render": function ( data, type, row ) {
	            	if (row.pitcher_hitter == "P"){return data.toFixed(2);} else if (row.pitcher_hitter == "H"){return "";}
	                }, 
	                "createdCell": function (td, cellData, rowData, row, col) {
	                	if (rowData.pitcher_hitter == "P"){ setStatCellColor(td, rowData.pitcher_z_so, 1)}
	                }, "sDefaultContent": "0" },
	            { "visible": false, className: "dm_zscore dm_export" + " dm_league_cat_" + dm_leagueinfo.cat_pitcher_whip, "title": "zWHIP", "mData": "pitcher_z_whip", "render": function ( data, type, row ) {
	            	if (row.pitcher_hitter == "P"){return data.toFixed(2);} else if (row.pitcher_hitter == "H"){return "";}
	                }, 
	                "createdCell": function (td, cellData, rowData, row, col) {
	                	if (rowData.pitcher_hitter == "P"){ setStatCellColor(td, rowData.pitcher_z_whip, 1)}
	                }, "sDefaultContent": "0" },
	            { "visible": false, className: "dm_zscore dm_export" + " dm_league_cat_" + dm_leagueinfo.cat_pitcher_era, "title": "zERA", "mData": "pitcher_z_era", "render": function ( data, type, row ) {
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
		if ( $.fn.DataTable.isDataTable( table_element ) ) {
			data_table = table_element.DataTable();
			data_table.destroy();
			table_element.empty();
			}
		data_table = table_element.dataTable(config);
		data_table = table_element.DataTable();
		
		// Show category columns  used by league
	    var table = table_element.DataTable();
	    // var columns_in = table.columns('.dm_league_cat_true .dm_stat');
	    var columns_out = table.columns('.dm_league_cat_false');
	    // columns_in.visible(true);
	    columns_out.visible(false);
	}

}

function setFilledSlotCellColor(td, value){
    if ( !value.startsWith("[") ) {
    	$(td).css("background-color", "rgb(230, 230, 230)"); // Grey 90%
    }  else {
    	$(td).removeProp("background-color");
    } 
}

function setAgeCellColor(td, age){
    if ( age <= 27 ) {
    	$(td).css("background-color", "rgb(153, 235, 255)"); // Light Blue 80%
    }  else if ( age < 33 ) {
    	$(td).removeProp("background-color");
    } else {
    	$(td).css("background-color", "rgb(255, 153, 153)"); // Red 80%
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

// Update player info panel with input playerrow data
function updatePlayerInfoPanel(playerrow){
	var row = playerrow;
	var btn_teamname;
	
	$("#lbl-playerinfoname").val(row.id);
	$("#lbl-playerinfoname").html(row.full_name + "&nbsp;<small><a href='http://www.fangraphs.com/players.aspx?lastname=" + row.full_name + "' target='_blank'><i class='fa fa-external-link' aria-hidden='true'></i></a></small>");
	$("#lbl-playerinfoteam").text(row.team);
	$("#lbl-playerinfoage").text(row.age);
	$("#lbl-playerinfoelig").text(row.custom_position);
	
	// if player is not owned
	if ((row.leagueteam_name == null)||(row.leagueteam_name == "")) {
		$("#lbl-playerinfoowner").text("[available]");
		$('#player_detail_row2').show();
		$('#player_detail_row2b').hide();
	}
	// else if player is owned
	else {
		$("#lbl-playerinfoowner").text(row.leagueteam_name);
		$('#player_detail_row2b').show();
		$('#player_detail_row2').hide();
		if (row.leagueteam_name.length >= 20) btn_teamname = row.leagueteam_name.substring(0,17) + "...";
		else btn_teamname = row.leagueteam_name;
		$('#btn-detailownedplayer').text('Owned by ' + btn_teamname);
	}
	
	$("#textarea-playernote").removeAttr("disabled");
	if (row.team_player_note != null){
		if (row.team_player_note.trim() != ""){
			$("#textarea-playernote").val(row.team_player_note);
		} else $("#textarea-playernote").val("");
	}else $("#textarea-playernote").val("");
	
	// $('#section-teaminfo').hide();
	
	if (row.favorite_flag) {
		$("#fav-icon").removeClass("fa-star-o");
		$("#fav-icon").addClass("fa-star");
		$("#btn-detailqueue").text("Remove Player From Queue");
	} else {
		$("#fav-icon").removeClass("fa-star");
		$("#fav-icon").addClass("fa-star-o");
		$("#btn-detailqueue").text("Add Player to Queue");
	}
	
	$('#player_detail_paragraph').hide();
	$('#player_detail_row1').show();
	$('#player_detail_row3').show();
	$('#player_detail_row4').show();
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
            		
            		// If a player is selected in the grid, then update the player info panel
            		if (playerselectedrow) updatePlayerInfoPanel(playerselectedrow);
            		
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
        		$('#section-teaminfo').show();
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
	
	$("#league-top-row").hide();
	$("#league-top-spinner-row").show();
	
	$("#intro-container").hide();
	$("#league-container").show();
	$("#league-top-container").show();
	
	timerLoadPlayerGridStart = performance.now();
	
	mssolutions.fbapp.draftmanager.getLeaguePlayerData(leagueid);
	mssolutions.fbapp.draftmanager.getLeagueTeams(leagueid);
	mssolutions.fbapp.draftmanager.getLeagueRoster(leagueid);
	updateTeamInfoTab();
	
	clearPlayerInfoTab();
	
	mssolutions.fbapp.draftmanager.getLeagueInfo(leagueid);
	
}

function loadLeagueIntro(){
	$("#league-container").hide();
	$("#league-top-container").hide();
	$("#intro-container").show();
}


function loadLeagueSelector(data){
	var options = $("#league-select");
	options.find('option').remove().end();
	options.append($("<option value='0'/>").text("--- Select League ---"));
	
	if (undefined !== data){
		// Sort list by year then league name
		data.sort(function(a, b){
		    var x = (a.league_year + a.league_name).toLowerCase();
		    var y = (b.league_year + b.league_name).toLowerCase();
		    if (x < y) {return -1;}
		    if (x > y) {return 1;}
		    return 0;
		});
		
		$.each(data, function() {
			// console.log("Loading league selector: ID-" + this.id + " VAL-" + this.league_name);
			options.append($("<option value='"+ this.id +"'/>").text("[" + this.league_year + "] " + this.league_name));
		});
	} else {
		// console.log("League data is null");
	}

	options.append($("<option value='newleague'/>").text("Add New League..."));
}

// Get the name of a league team with the team ID
function getLeagueTeamName(teamid){
	var teamlist = dm_globalteamlist
	var teamname = "";
	$.each(teamlist, function(index, value) {
		if (value.id == teamid){ return teamname = value.team_name; }
	});
	return teamname;
}

function updateTeamList(data){
	// sort teams alphabetically after my team as first team
	var myteam = data[0];
	data.shift();
	data = data.sort(function(a, b) {
	    return a.team_name.localeCompare(b.team_name);
	});
	data.unshift(myteam);
	
	var data_team_salaries = $('#team_ovw_table').DataTable();
	var data_team_salaries_rows = data_team_salaries.rows().data();
	
	// console.log("Length of data_team_salaries_rows: " + data_team_salaries_rows.length);
	
	$.each(data, function(index, value) {
		$.each(data_team_salaries_rows, function(index2, value2) {
			// console.log("Each team and maxbid: " + value2.team_name + ", " + value2.maxbid);
			if (value.id == value2.id){
				// console.log("FOUND team and maxbid: " + value2.team_name + ", " + value2.maxbid);
				value["maxbid"] = value2.maxbid;
			}
		});
	});
	return data;
}

function loadEditPlayerTeamSelect(data){
	
	var data = updateTeamList(data);
	
	var draftteamselect = $("#select-draftteam");
	draftteamselect.find('option').remove().end();
	if (undefined !== data){
		$.each(data, function() {
			draftteamselect.append($("<option value='"+ this.id +"'/>").text(this.team_name + "  ($" + this.maxbid + ")"));
		});
	} else {}
	
}

function loadOTBTeamSelect(data){

	var data = updateTeamList(data);
	
	var draftteamselect = $("#select-ontheblock-draftteam");
	draftteamselect.find('option').remove().end();
	
	if (undefined !== data){
		draftteamselect.append($("<option value='0'/>").text("--- Select Team ---"));
		$.each(data, function() {
			draftteamselect.append($("<option value='"+ this.id +"'/>").text(this.team_name + "  ($" + this.maxbid + ")"));
		});
	} else {}
	
	var draftteamselectunk = $("#select-draftteamunk");
	draftteamselectunk.find('option').remove().end();
	draftteamselectunk.append($("<option value='0'/>").text("--- Select Team ---"));
	if (undefined !== data){
		$.each(data, function() {
			draftteamselectunk.append($("<option value='"+ this.id +"'/>").text(this.team_name + " ($" + this.maxbid + ")"));
		});
	} else {}
}

function loadTeamSelect(data){

	var data = updateTeamList(data);
	
	var teamfilterselect = $("#select-draftedplayerfilter");
	teamfilterselect.find('option').remove().end();
	teamfilterselect.append($("<option value='-1' selected/>").text('All Players'));
	teamfilterselect.append($("<option value='0'/>").text('Undrafted Players'));
	
	var teamselect = $("#team-select");
	teamselect.find('option').remove().end();
	if (undefined !== data){
		$.each(data, function() {
			// console.log("Loading team selector: ID-" + this.id + " VAL-" + this.team_name);
			if (this.team_name.length >= 20) {
				teamselect.append($("<option value='"+ this.id +"'/>").text(this.team_name.substring(0, 20) + "..."));
				teamfilterselect.append($("<option value='"+ this.id +"'/>").text(this.team_name.substring(0, 20) + "..."));
			} else {
				teamselect.append($("<option value='"+ this.id +"'/>").text(this.team_name));
				teamfilterselect.append($("<option value='"+ this.id +"'/>").text(this.team_name));
			}

		});
	} else {
		// console.log("League data is null");
	}

	var draftteamselectunk = $("#select-draftteamunk");
	draftteamselectunk.find('option').remove().end();
	draftteamselectunk.append($("<option value='0'/>").text("--- Select Team ---"));
	if (undefined !== data){
		$.each(data, function() {
			draftteamselectunk.append($("<option value='"+ this.id +"'/>").text(this.team_name + " ($" + this.maxbid + ")"));
		});
	} else {}
}


function round(value, decimals) {
	  return Number(Math.round(value+'e'+decimals)+'e-'+decimals);
	}


/**
 * Update player note via the API.
 */
mssolutions.fbapp.draftmanager.updatePlayerInfo = function(playerrowdata) {
	
	// console.log("In updatePlayerNote...");
	// console.log("--League ID: " + playerrowdata.league_id);
	// console.log("--Player ID: " + playerrowdata.id);
	// console.log("--Note: " + playerrowdata.team_player_note);
	// console.log("--Cust Pos: " + playerrowdata.custom_position);
	// console.log("--Favorite: " + playerrowdata.favorite_flag);
	
	gapi.client.draftapp.league.updateplayerinfo({
		'league_id' : playerrowdata.league_id,
		'player_projected_id' : playerrowdata.id,
		'team_player_note' : playerrowdata.team_player_note,
		'custom_position_flag' : playerrowdata.custom_position_flag,
		'custom_position' : playerrowdata.custom_position.toString(),
		'favorite_flag' : playerrowdata.favorite_flag
		}).execute(
      function(resp) {
        if (!resp.code) { 
        	console.log("Update player info complete. League Player ID: " + resp.longdescription);
        	// $("#btn-playerinfosavenote").removeAttr("disabled");
        }
        else {
        	bootstrap_alert.lostconnection();
        	console.log("Failed to update player info: ", resp.code + " : " + resp.message);
        	// $("#btn-playerinfosavenote").removeAttr("disabled");
        }
      });
};

/**
 * Draft player via the API.
 */
mssolutions.fbapp.draftmanager.draftPlayer = function(league_id, league_team_id, 
		player_projected_id, team_roster_position, team_player_salary, favorite_flag) {
	
	// console.log("In draftPlayer...");
	
	gapi.client.draftapp.league.draftplayer({
		'league_id' : league_id,
		'league_team_id' : league_team_id,
		'player_projected_id' : player_projected_id,
		'team_roster_position' : team_roster_position,
		'team_player_salary' : team_player_salary,
		'player_favorite_flag': favorite_flag}).execute(
      function(resp) {
        if (!resp.code) { 
        	console.log("Draft player complete. League Player ID: " + resp.longdescription);
        	
        	// Indicator whether the tab should be updated on select
        	update_positional_tab = true;
        	update_standings_tab = true;
        	
        	calcLiveAuctionValue();
        	
    		// Update team information in team overview list
    		calcTeamOvwList();
        }
        else {
        	bootstrap_alert.lostconnection();
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
        	bootstrap_alert.warning('<strong>Connection Lost!</strong>  Please refresh page.', 'danger', 4000);
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
        	
        	// Indicator whether the tab should be updated on select
        	update_positional_tab = true;
        	update_standings_tab = true;
        	
        	calcLiveAuctionValue();
        	
    		// Update team information in team overview list
    		calcTeamOvwList();
        }
        else {
        	bootstrap_alert.lostconnection();
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
        	bootstrap_alert.lostconnection();
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
        	bootstrap_alert.lostconnection();
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
        	dm_teamrostertemplate_1 = [];
        	dm_teamrostertemplate_2 = [];
        	
        	$.each( resp.items, function( key, value ) {
      		  
        		if (value.position == "P" || value.position == "Res"){
        			dm_teamrostertemplate_2.push(value);
        		} else {
        			dm_teamrostertemplate_1.push(value);
        		}
        		
      		});

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
        	dm_rescount = counts["Res"];
        	// console.log("Roster Counts: " + JSON.stringify(dm_teamrostercounts));
        	// console.log("RES Count: " + dm_rescount);
        	
        	// Load the blank roster template into a datatable
        	loadTeamRosterTable(teamrostertemplate, false);
        	loadTeamOvwRosterTable(false);
        }
        else {
        	bootstrap_alert.lostconnection();
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
        	resetDraftPanel();
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
        	
        	var t0 = performance.now();
        	console.log("Get player data took " + (t0 - timerLoadPlayerGridStart) + " milliseconds.")
        	// Load player data into main player grid
        	loadPlayerGridTable(resp.items, false);
        	
        	$("#league-top-row").show();
        	$("#league-top-spinner-row").hide();
        	
        	var t1 = performance.now();
        	console.log("Call to loadPlayerGridTable took " + (t1 - t0) + " milliseconds.")
        	
        	// Load player data into custom position editor
        	loadCustomPlayerPositionTable(resp.items, false);
        	
			// Calc and load team overview list
			// Also calculates team open roster slots
			calcTeamOvwList();
			
			// Calculate team estimated standings
			calcStandings();
			
			loadPlayerQueue();
			
        }
        else {
        	bootstrap_alert.lostconnection();
        	console.log("Failed to get league player data: ", resp.code + " : " + resp.message);
        }
      });
};

/**
 * Get league info via the API.
 */
mssolutions.fbapp.draftmanager.getLeagueInfo = function(leagueid) {
	// console.log("getLeaguePlayerData, leagueid: " + leagueid);
	gapi.client.draftapp.league.getleagueinfo({
		'id' : leagueid}).execute(
      function(resp) {
        if (!resp.code) { 
        	console.log("League info get complete.");
        	// console.log("League Info: " + JSON.stringify(resp));
        	dm_leagueinfo = resp;
        	// console.log("League Info: " + dm_leagueinfo.position_priority_list);
        }
        else {
        	console.log("Failed to get league info: ", resp.code + " : " + resp.message);
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
 * Delete a team from a league via the API.
 */
mssolutions.fbapp.draftmanager.deleteTeamFromLeague = function(teamid, leagueid) {
	console.log("Deleting Team id..." + teamid);
	gapi.client.draftapp.league.deleteteamfromleague({
		'teamid' : teamid,
		'leagueid' : leagueid }).execute(
      function(resp) {
        if (!resp.code) { 
        	console.log("Team delete complete.");
        	// mssolutions.fbapp.draftmanager.loadLeagueList();
        	// loadLeagueIntro();
        	BootstrapDialog.closeAll();
        	loadLeagueContent(leagueid);
        }
        else {
        	console.log("Failed to delete team: ", resp.code + " : " + resp.message);
        }
      });
};

/**
 * Add a team to a league via the API.
 */
mssolutions.fbapp.draftmanager.addTeamToLeague = function(teamname, teamowner, leagueid) {
	console.log("Adding Team name..." + teamname);
	gapi.client.draftapp.league.addteamtoleague({
		'teamname' : teamname,
		'teamowner' : teamowner,
		'leagueid' : leagueid }).execute(
      function(resp) {
        if (!resp.code) { 
        	console.log("Team add complete.");
        	// mssolutions.fbapp.draftmanager.loadLeagueList();
        	// loadLeagueIntro();
        	BootstrapDialog.closeAll();
        	loadLeagueContent(leagueid);
        }
        else {
        	console.log("Failed to add team: ", resp.code + " : " + resp.message);
        }
      });
};


/**
 * load List of leagues via the API.
 */
mssolutions.fbapp.draftmanager.loadLeagueList = function(leagueid) {
	
	$('#league-select').prop( "disabled", true );

	gapi.client.draftapp.league.getuserleagues().execute(
      function(resp) {
        if (!resp.code) { 
        	loadLeagueSelector(resp.items);
        	if (leagueid != null) $('#league-select').val(leagueid);
        	$('#league-select').prop( "disabled", false );
        }
        else {
        	bootstrap_alert.lostconnection();
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
        	var yearprev = (new Date).getFullYear()-1;
        	var yearnext = (new Date).getFullYear()+1;
        	yearselect.find('option').remove().end();
        	yearselect.append($('<option>', { value : yearprev }).text(yearprev)); 
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
        	for (i = 100; i <= 300; i=i+1) { 
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
		
		// console.log("Loading gapi.client.draftapp");
		
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


