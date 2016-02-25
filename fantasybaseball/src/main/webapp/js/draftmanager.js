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
		  console.log("In league-select on change");
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
		
		// If it's the preview tab then load projection selections
		if($current == 4) {
			var teamtable = $('#team_table').DataTable();
	        var data = teamtable.rows().data();
			loadTeamPreviewTable(data, false);
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
		$('#draftmanager-modal').modal('hide');
		progressmodal.showPleaseWait("Updating Projections...");
		parseprojections();

	});
	
	$('#btn-addteam').click(function() 
	{
		var teamtable = $('#team_table').DataTable();
		
		teamtable.row.add( {
	        "team_name":       $("#input-teamname").val(),
	        "owner_name":   $("#input-teamowner").val(),
	        "isuserowner":     $("#check-myteam").is(":checked"),
	        "delete": "0"
	    } ).draw();
		    	
	});
	
    $('#team_table').on( 'click', 'button', function () {
    	var teamtable = $('#team_table').DataTable();
        // var data = teamtable.row( $(this).parents('tr') ).data();
        
        teamtable
        .row( $(this).parents('tr') )
        .remove()
        .draw();

    } );
	
    loadTeamPreviewTable(null, true);
	loadTeamTable(null, true);


});

function loadTeamTable(data, isInitialLoad)
{
	var data_table;
	var table_element = $('#team_table');
	var config = {
        "data": data,
        responsive: true,
        "bSort" : false,
        "searching": false,
        "paging": false,
        "columns": [
            { "title": "Team Name", "mData": "team_name" },
            { "title": "Team Owner", "mData": "owner_name"},
            { "title": "My Team?", "mData": "isuserowner"},
            { "title": "<i class='fa fa-trash-o'></i>"},
        ],
        "columnDefs": [ {
            // The `data` parameter refers to the data for the cell (defined by the
            // `data` option, which defaults to the column being worked with, in
            // this case `data: 0`.
            "render": function ( data, type, row ) {
                // return data +' ('+ row[3]+')';
                
                if (data == true){
                	return "<i class='fa fa-check' style='color: #008000;'></i>";
                } else {
                	return "";
                }
                
            },
            "targets": 2
        },
        {
            "targets": -1,
            "data": null,
            "defaultContent": "<button class='btn btn-danger btn-xs'>&nbsp;&nbsp;<i class='fa fa-trash-o'></i>&nbsp;&nbsp;</button>"
        } ]
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
        responsive: true,
        "bSort" : false,
        "searching": false,
        "paging": false,
        "columns": [
            { "title": "Team Name", "mData": "team_name" },
            { "title": "Team Owner", "mData": "owner_name"},
            { "title": "My Team?", "mData": "isuserowner"},
        ],
        "columnDefs": [ {
            // The `data` parameter refers to the data for the cell (defined by the
            // `data` option, which defaults to the column being worked with, in
            // this case `data: 0`.
            "render": function ( data, type, row ) {
                if (data == true){
                	return "<i class='fa fa-check' style='color: #008000;'></i>";
                } else {
                	return "";
                }
                
            },
            "targets": 2
        }]
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
	$.each(data, function() {
		options.append($("<option value='"+ this.id +"'/>").text(this.league_name));
	});
	options.append($("<option value='newleague'/>").text("Add New League..."));
}


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
			}
		}

		apisToLoad = 1; // must match number of calls to gapi.client.load()
		gapi.client.load('draftapp', 'v1', callback, apiRoot);

	}
	else {
		mssolutions.fbapp.draftmanager.loadLeagues();
	}
};