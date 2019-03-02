
/**
 * @param data
 * @param isInitialLoad
 * @returns
 */
function loadLeagueStandingsTable(data, isInitialLoad)
{
	var data_table;
	var table_element = $('#league_standings_table');
	var config = {
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
           
            { "title": "Hitting", "mData": "hitting_score", "sDefaultContent": ""},
            { "title": "Pitching", "mData": "pitching_score", "sDefaultContent": ""},
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


/**
 * @param data
 * @param isInitialLoad
 * @returns
 */
function loadLeagueStandingsCatTable(data, isInitialLoad, element, cat_title, cat_name)
{
	var data_table;
	var table_element = $(element);
	var config = {
        // "bSort" : true,
        "searching": false,
        "paging": false,
        "info": false,
        "order": [[ 3, "desc" ]],
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
            { "title": cat_title, "mData": cat_name, "sDefaultContent": ""},

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