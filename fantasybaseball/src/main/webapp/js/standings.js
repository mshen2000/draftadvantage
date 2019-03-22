




/**
 * @param data
 * @param isInitialLoad
 * @returns
 */
function loadLeagueStandingsTable(data, isInitialLoad, parent_element_id, element_id, cat_title, cat_name)
{
	var max_total_score = 0;
	var order;
	
	if (!isInitialLoad){
		// Get the max scores for total, pitching and hitting
		$.each( data, function( index, value ){	
			max_total_score = Math.max(value[cat_name], max_total_score);
		});	
	}
	
	var data_table;
	var table_element = $('#' + element_id);
	var config_init = {
	        "bSort" : true,
	        "searching": false,
	        "paging": false,
	        "info": false,
	        "order": [[ 5, "desc" ]],
			responsive: true,
	    	"processing": true,
	        data: data,
	        select: {
	            style:    'single'
	        },
	        rowId: 'id',
	        "createdRow": function ( row, data, index ) {
	            if ( data.isMyTeam ) {
	                $('td', row).css("font-weight", "bold");
	            }
	        },
	        "columns": [
	            { "visible": false, "title": "Team ID", "mData": "team_id", "sDefaultContent": ""},	
	            { "visible": false, "title": "isMyTeam", "mData": "isMyTeam", "sDefaultContent": ""},	
	            { "title": "Team", "mData": "team_name", "sDefaultContent": ""},	
	           
	            { "title": "Hitting", "mData": "hitting_score", "sDefaultContent": ""},
	            { "title": "Pitching", "mData": "pitching_score", "sDefaultContent": ""},
	            { "title": "Total", "mData": "total_score", "sDefaultContent": ""}
	        ]
	        };
	var config = {
        "bSort" : true,
        "searching": false,
        "paging": false,
        "info": false,
        "order": [[ 3, "desc" ]],
		responsive: true,
    	"processing": true,
        data: data,     
        columnDefs: [    
            { targets: 3, className: 'text-right' }
	    ],
        fnDrawCallback: function() {
            $("#" + element_id + " thead").remove();
          },
        select: {
            style:    'single'
        },
        rowId: 'id',
        "createdRow": function ( row, data, index ) {
        	// console.log("data.isMyTeam: " + data.isMyTeam)
            if ( data.isMyTeam ) {
                $('td', row).css("font-weight", "bold");
            }
        },
        "columns": [
            { "visible": false, "title": "Team ID", "mData": "team_id", "sDefaultContent": ""},	
            { "visible": false, "title": "isMyTeam", "mData": "isMyTeam", "sDefaultContent": ""},	
            { "title": "Team", "mData": "team_name","sDefaultContent": ""},	    
            // { "title": "Hitting", "mData": "hitting_score", "sDefaultContent": ""},
            // { "title": "Pitching", "mData": "pitching_score", "sDefaultContent": ""},
            { "title": "Total", "mData": cat_name, "sDefaultContent": ""},
            {
                "title": cat_title,
                "sortable":false,
                "width": "120px",
                "render": function(data, type, row, meta){
                    return $("<div></div>", {
                        "class": "bar-chart-bar"
                    }).append(function(){
                        var bars = [];
                        var score = 0;
                        var bar_class = "bar bar1";
                        if ( row.isMyTeam ) {
                        	bar_class = "bar bar2";
                        }
                        if (cat_name == "total_score") { score = row.total_score; }
                        else if (cat_name == "pitching_score") { score = row.pitching_score; }
                        else if (cat_name == "hitting_score") { score = row.hitting_score; }
                        bars.push($("<div></div>",{"class": bar_class}).css({"width": 100*(score/max_total_score) + "%"}));
                        return bars;
                    }).prop("outerHTML")
                }
            }
        ]
        };
	
	var element = document.createElement('table');
	element.id = element_id;
	element.className = 'table dm_cat_standings_table';
	// element.style.marginTop = '0px';
	element.setAttribute('style', 'margin-top:0px !important');
	element.setAttribute("cellspacing", "0");
	element.setAttribute("width", "100%");
	
	var header_element = document.createElement('h3');
	header_element.style.marginBottom = '0px';
	// header_element.style.fontWeight = 'bold';
	header_element.style.textAlign = 'center';
	var t = document.createTextNode(cat_title);      
	header_element.appendChild(t);

	var parent = document.getElementById(parent_element_id);
	parent.appendChild(header_element);
	parent.appendChild(element);
	
	var table_element = $("#" + element_id);
	
	if (isInitialLoad) 	{
		data_table = table_element.dataTable(config_init);
	} else {
		data_table = table_element.dataTable(config);
		data_table = table_element.DataTable();
	}
}


/**
 * @param data
 * @param isInitialLoad
 * @returns
 */
function loadLeagueStandingsCatTable(data, isInitialLoad, parent_element_id, element_id, cat_title, cat_name)
{
	
	if (cat_name == "team_pitcher_era" || cat_name == "team_pitcher_whip") order = "asc";
	else order = "desc";
	
	var data_table;
	var config = {
        "bSort" : true,
        "searching": false,
        "paging": false,
        "info": false,
        "order": [[ 3, order ]],
		responsive: true,
    	"processing": true,
        data: data,
        // select: {style:    'single'},
        rowId: 'id',
        "createdRow": function ( row, data, index ) {
            if ( data.isMyTeam ) {
                $('td', row).css("font-weight", "bold");
                $('td', row).css("background-color", "Orange");
            }
        },
        fnDrawCallback: function() {
            $("#" + element_id + " thead").remove();
          },
        columnDefs: [
            { orderable: false, targets: '_all' }, 
            { targets: -1, className: 'text-right' }
        ],
        "columns": [
            { "visible": false, "title": "Team ID", "mData": "team_id", "sDefaultContent": ""},	
            { "visible": false, "title": "isMyTeam", "mData": "isMyTeam", "sDefaultContent": ""},	
            { "title": "Team", "mData": "team_name", "sDefaultContent": ""},	
            { "title": cat_title, "mData": cat_name,"render": function ( data, type, row ) {
                if(cat_title == 'AVG' || cat_title == 'OBP'){
                	var avgnum = data.toFixed(3);
                	if (data > 0) return avgnum.toString().substr(avgnum.length - 4);
                	else return '.000' ;
                } else if (cat_title == 'ERA' || cat_title == 'WHIP'){
                	return data.toFixed(2);
                } else {
                    return data.toFixed(0);
                }
            }, "sDefaultContent": ""}

        ]
        };

	var element = document.createElement('table');
	element.id = element_id;
	element.className = 'table dm_cat_standings_table';
	element.setAttribute('style', 'margin-top:0px !important');
	element.setAttribute("cellspacing", "0");
	element.setAttribute("width", "100%");
	
	var header_element = document.createElement('h3');
	header_element.style.marginBottom = '0px';
	// header_element.style.fontWeight = 'bold';
	header_element.style.textAlign = 'center';
	var t = document.createTextNode(cat_title);      
	header_element.appendChild(t);

	var parent = document.getElementById(parent_element_id);
	parent.appendChild(header_element);
	parent.appendChild(element);
	
	var table_element = $("#" + element_id);
	
	if (isInitialLoad) 	{
		data_table = table_element.dataTable(config);
	} else {
		// data_table = table_element.DataTable();
		// data_table.destroy();
		// table_element.empty();
		data_table = table_element.dataTable(config);
		data_table = table_element.DataTable();
		
		// Show category columns  used by league
	    var table = table_element.DataTable();
  
	}
}


/**
 * @param data
 * @param isInitialLoad
 * @returns
 * Description:  Old league standings load table, one table, all categories
 */
function loadLeagueStandingsTableOLD(data, isInitialLoad)
{
	var data_table;
	var table_element = $('#league_standings_table');
	var config = {
        "bSort" : true,
        "searching": false,
        "paging": false,
        "info": false,
        "order": [[ 27, "desc" ]],
		responsive: true,
    	"processing": true,
        data: data,
        select: {
            style:    'single'
        },
        rowId: 'id',
        "createdRow": function ( row, data, index ) {
        	// console.log("data.isMyTeam: " + data.isMyTeam)
            if ( data.isMyTeam ) {
//                $('td', row).eq(2).addClass('highlight');
//                $('td', row).eq(3).addClass('highlight');
//                $('td', row).eq(4).addClass('highlight');
//                $('td', row).addClass('highlight');
                $('td', row).css("font-weight", "bold");
            }
        },
        "columns": [
            { "visible": false, "title": "Team ID", "mData": "team_id", "sDefaultContent": ""},	
            { "visible": false, "title": "isMyTeam", "mData": "isMyTeam", "sDefaultContent": ""},	
            { "title": "Team", "mData": "team_name", "sDefaultContent": ""},	
            { "title": "Avg", className: "dm_league_cat_" + dm_leagueinfo.cat_hitter_avg, "mData": "team_hitter_avg", "render": function ( data, type, row ) {
            		var avgnum = data.toFixed(3);
                    return avgnum.toString().substr(avgnum.length - 4) + " (" + row.team_hitter_avg_score +")";
                },
                "sDefaultContent": ""},
            { "visible": false, "title": "Avg S", className: "dm_league_cat_" + dm_leagueinfo.cat_hitter_avg, "mData": "team_hitter_avg_score", "sDefaultContent": ""},
            { "title": "HR", className: "dm_league_cat_" + dm_leagueinfo.cat_hitter_hr, "mData": "team_hitter_hr", "render": function ( data, type, row ) 
            	{return data.toFixed(0) + " (" + row.team_hitter_hr_score + ")";}, "sDefaultContent": ""},
            { "visible": false, "title": "HR S", className: "dm_league_cat_" + dm_leagueinfo.cat_hitter_hr, "mData": "team_hitter_hr_score", "sDefaultContent": ""},
            { "title": "SB", className: "dm_league_cat_" + dm_leagueinfo.cat_hitter_sb, "mData": "team_hitter_sb", "render": function ( data, type, row ) 
            	{return data.toFixed(0) + " (" + row.team_hitter_sb_score + ")";}, "sDefaultContent": ""},
            { "visible": false, "title": "SB S", className: "dm_league_cat_" + dm_leagueinfo.cat_hitter_sb, "mData": "team_hitter_sb_score", "sDefaultContent": ""},
            { "title": "R", className: "dm_league_cat_" + dm_leagueinfo.cat_hitter_r, "mData": "team_hitter_runs", "render": function ( data, type, row ) 
            	{return data.toFixed(0) + " (" + row.team_hitter_runs_score + ")";}, "sDefaultContent": ""},
            { "visible": false, "title": "R S", className: "dm_league_cat_" + dm_leagueinfo.cat_hitter_r, "mData": "team_hitter_runs_score", "sDefaultContent": ""},
            { "title": "RBI", className: "dm_league_cat_" + dm_leagueinfo.cat_hitter_rbi, "mData": "team_hitter_rbi", "render": function ( data, type, row ) 
            	{return data.toFixed(0) + " (" + row.team_hitter_rbi_score + ")";},
                "sDefaultContent": ""},
            { "visible": false, "title": "RBI S", className: "dm_league_cat_" + dm_leagueinfo.cat_hitter_rbi, "mData": "team_hitter_rbi_score", "sDefaultContent": ""},
            { "title": "OBP", className: "dm_league_cat_" + dm_leagueinfo.cat_hitter_obp, "mData": "team_hitter_obp", "render": function ( data, type, row ) {
        		var obpnum = data.toFixed(3);
                return obpnum.toString().substr(obpnum.length - 4) + " (" + row.team_hitter_obp_score +")";
            },
            "sDefaultContent": ""},
            { "visible": false, "title": "OBP S", className: "dm_league_cat_" + dm_leagueinfo.cat_hitter_obp, "mData": "team_hitter_obp_score", "sDefaultContent": ""},
            
            { "title": "W", className: "dm_league_cat_" + dm_leagueinfo.cat_pitcher_w, "mData": "team_pitcher_w", "render": function ( data, type, row ) 
            	{return data.toFixed(0) + " (" + row.team_pitcher_w_score + ")";},
                "sDefaultContent": ""},
            { "visible": false, "title": "W S", className: "dm_league_cat_" + dm_leagueinfo.cat_pitcher_w, "mData": "team_pitcher_w_score", "sDefaultContent": ""},
            { "title": "SV", className: "dm_league_cat_" + dm_leagueinfo.cat_pitcher_sv, "mData": "team_pitcher_sv", "render": function ( data, type, row ) 
            	{return data.toFixed(0) + " (" + row.team_pitcher_sv_score + ")";},
                "sDefaultContent": ""},
            { "visible": false, "title": "SV S", className: "dm_league_cat_" + dm_leagueinfo.cat_pitcher_sv, "mData": "team_pitcher_sv_score", "sDefaultContent": ""},
            { "title": "Holds", className: "dm_league_cat_" + dm_leagueinfo.cat_pitcher_holds, "mData": "team_pitcher_holds", "render": function ( data, type, row ) 
            	{return data.toFixed(0) + " (" + row.team_pitcher_holds_score + ")";},
                "sDefaultContent": ""},
            { "visible": false, "title": "HOLDS S", className: "dm_league_cat_" + dm_leagueinfo.cat_pitcher_holds, "mData": "team_pitcher_holds_score", "sDefaultContent": ""},
            { "title": "SO", className: "dm_league_cat_" + dm_leagueinfo.cat_pitcher_so, "mData": "team_pitcher_k", "render": function ( data, type, row ) 
            	{return data.toFixed(0) + " (" + row.team_pitcher_k_score + ")";},
                "sDefaultContent": ""},
            { "visible": false, "title": "SO S", className: "dm_league_cat_" + dm_leagueinfo.cat_pitcher_so, "mData": "team_pitcher_k_score", "sDefaultContent": ""},
            { "title": "ERA", className: "dm_league_cat_" + dm_leagueinfo.cat_pitcher_era, "mData": "team_pitcher_era", "render": function ( data, type, row ) 
            	{return data.toFixed(2) + " (" + row.team_pitcher_era_score + ")";},
                "sDefaultContent": ""},
            { "visible": false, "title": "ERA S", className: "dm_league_cat_" + dm_leagueinfo.cat_pitcher_era, "mData": "team_pitcher_era_score", "sDefaultContent": ""},
            { "title": "WHIP", className: "dm_league_cat_" + dm_leagueinfo.cat_pitcher_whip, "mData": "team_pitcher_whip", "render": function ( data, type, row ) 
            	{return data.toFixed(2) + " (" + row.team_pitcher_whip_score + ")";},
                "sDefaultContent": ""},
            { "visible": false, "title": "WHIP S", className: "dm_league_cat_" + dm_leagueinfo.cat_pitcher_whip, "mData": "team_pitcher_whip_score", "sDefaultContent": ""},
            { "title": "Total", "mData": "total_score", "sDefaultContent": ""},

        ]
        };
	
	if (isInitialLoad) 	{
		data_table = table_element.dataTable(config);
	} else {
		// data_table = table_element.DataTable();
		// data_table.destroy();
		// table_element.empty();
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