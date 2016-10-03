function calcStandings(){
	
	var num_teams = dm_leagueinfo.league_teams.length;
	var teamlist = dm_leagueinfo.league_teams;

	var data_table = $('#playergrid_table').DataTable();
	var data_rows = data_table.rows().data();
	
	var teamstandingslist = [];
	
	// Filtered for only drafted players
	var filtered_data = $.grep(data_rows, function(v) {
		var isdrafted;
		if (v.leagueteam_name == null) isdrafted = false;
		else if (v.leagueteam_name.trim().length < 1) isdrafted = false;
		else isdrafted = true;
	    return v.unknownplayer == false && isdrafted == true;
	});
	
	console.log("filtered_data length: " + filtered_data.length);

	// For each team, filter the player grid to get stat totals
	$.each( teamlist, function( index, value ){
		var team = {};
		team['team_name'] = value.team_name;
		team['team_id'] = value.id;
		
		//  Set "My Team"
		if (index == 0) team['isMyTeam'] = true;
		else team['isMyTeam'] = false;

		// Filter for only players drafted by given team
		var filtered_data_team = $.grep(filtered_data, function(v) {
		    return v.leagueteam_id == value.id;});
		
		console.log("filtered_data_team length: " + filtered_data_team.length);
		
		if (filtered_data_team.length > 0){
			if (dm_leagueinfo.cat_hitter_hr){
				team['team_hitter_hr'] = filtered_data_team.map(function(b) { return b.hitter_hr; })
		        	.reduce(function(p, c) { return p + c; });
				if (team['team_hitter_hr'] == null) team['team_hitter_hr'] = 0;
			}
			if (dm_leagueinfo.cat_hitter_sb){
				team['team_hitter_sb'] = filtered_data_team.map(function(b) { return b.hitter_sb; })
	        		.reduce(function(p, c) { return p + c; });
				if (team['team_hitter_sb'] == null) team['team_hitter_sb'] = 0;
			}
			if (dm_leagueinfo.cat_hitter_rbi){
				team['team_hitter_rbi'] = filtered_data_team.map(function(b) { return b.hitter_rbi; })
	        		.reduce(function(p, c) { return p + c; });
				if (team['team_hitter_rbi'] == null) team['team_hitter_rbi'] = 0;
			}
			if (dm_leagueinfo.cat_hitter_r){
				team['team_hitter_runs'] = filtered_data_team.map(function(b) { return b.hitter_runs; })
	        		.reduce(function(p, c) { return p + c; });
				if (team['team_hitter_runs'] == null) team['team_hitter_runs'] = 0;
			}
			if (dm_leagueinfo.cat_hitter_avg){
				team['team_hitter_hits'] = filtered_data_team.map(function(b) { return b.hitter_hits; })
	        		.reduce(function(p, c) { return p + c; });
				team['team_hitter_ab'] = filtered_data_team.map(function(b) { return b.hitter_ab; })
	        		.reduce(function(p, c) { return p + c; });
				team['team_hitter_avg'] = team['team_hitter_hits'] / team['team_hitter_ab'];
				if (team['team_hitter_avg'] == null) team['team_hitter_avg'] = 0;
			}

			if (dm_leagueinfo.cat_pitcher_wins){
				team['team_pitcher_w'] = filtered_data_team.map(function(b) { return b.pitcher_w; })
        			.reduce(function(p, c) { return p + c; });
				if (team['team_pitcher_w'] == null) team['team_pitcher_w'] = 0;
			}
			if (dm_leagueinfo.cat_pitcher_saves){
				team['team_pitcher_sv'] = filtered_data_team.map(function(b) { return b.pitcher_sv; })
    				.reduce(function(p, c) { return p + c; });
				if (team['team_pitcher_sv'] == null) team['team_pitcher_sv'] = 0;
			}
			if (dm_leagueinfo.cat_pitcher_so){
				team['team_pitcher_k'] = filtered_data_team.map(function(b) { return b.pitcher_k; })
    				.reduce(function(p, c) { return p + c; });
				if (team['team_pitcher_k'] == null) team['team_pitcher_k'] = 0;
			}
			if (dm_leagueinfo.cat_pitcher_era){
				team['team_pitcher_er'] = filtered_data_team.map(function(b) { return b.pitcher_er; })
    				.reduce(function(p, c) { return p + c; });
				team['team_pitcher_ip'] = filtered_data_team.map(function(b) { return b.pitcher_ip; })
    				.reduce(function(p, c) { return p + c; });
				if ((team['team_pitcher_ip']==null)||(team['team_pitcher_ip'] == 0)){
					team['team_pitcher_era'] = 0;
				} else {
					team['team_pitcher_era'] = team['team_pitcher_er'] / (team['team_pitcher_ip'] / 9);
				}
			}
			if (dm_leagueinfo.cat_pitcher_whip){
				team['team_pitcher_hits'] = filtered_data_team.map(function(b) { return b.pitcher_hits; })
    				.reduce(function(p, c) { return p + c; });
				team['team_pitcher_bb'] = filtered_data_team.map(function(b) { return b.pitcher_bb; })
	    			.reduce(function(p, c) { return p + c; });
				team['team_pitcher_ip'] = filtered_data_team.map(function(b) { return b.pitcher_ip; })
					.reduce(function(p, c) { return p + c; });
				if ((team['team_pitcher_ip']==null)||(team['team_pitcher_ip'] == 0)){
					team['team_pitcher_whip'] = 0;
				} else {
					team['team_pitcher_whip'] = (team['team_pitcher_bb'] + team['team_pitcher_hits']) / team['team_pitcher_ip'];
				}
			}

			
		} else {
			if (dm_leagueinfo.cat_hitter_hr) team['team_hitter_hr'] = 0;
			if (dm_leagueinfo.cat_hitter_sb) team['team_hitter_sb'] = 0;
			if (dm_leagueinfo.cat_hitter_rbi) team['team_hitter_rbi'] = 0;
			if (dm_leagueinfo.cat_hitter_r) team['team_hitter_runs'] = 0;
			if (dm_leagueinfo.cat_hitter_avg) {
				team['team_hitter_hits'] = 0;
				team['team_hitter_ab'] = 0;
				team['team_hitter_avg'] = 0;
			}
			
			if (dm_leagueinfo.cat_pitcher_wins) team['team_pitcher_w'] = 0;
			if (dm_leagueinfo.cat_pitcher_saves) team['team_pitcher_sv'] = 0;
			if (dm_leagueinfo.cat_pitcher_so) team['team_pitcher_k'] = 0;
			if (dm_leagueinfo.cat_pitcher_era) {
				team['team_pitcher_er'] = 0;
				team['team_pitcher_ip'] = 0;
				team['team_pitcher_era'] = 0;
			}
			if (dm_leagueinfo.cat_pitcher_whip) {
				team['team_pitcher_ip'] = 0;
				team['team_pitcher_hits'] = 0;
				team['team_pitcher_bb'] = 0;
				team['team_pitcher_whip'] = 0;
			}
		}

		teamstandingslist.push(team);
	});
	
	if (dm_leagueinfo.cat_hitter_hr) addRankScore(teamstandingslist, "team_hitter_hr", true);
	if (dm_leagueinfo.cat_hitter_sb) addRankScore(teamstandingslist, "team_hitter_sb", true);
	if (dm_leagueinfo.cat_hitter_rbi) addRankScore(teamstandingslist, "team_hitter_rbi", true);
	if (dm_leagueinfo.cat_hitter_r) addRankScore(teamstandingslist, "team_hitter_runs", true);
	if (dm_leagueinfo.cat_hitter_avg) addRankScore(teamstandingslist, "team_hitter_avg", true);
		
	if (dm_leagueinfo.cat_pitcher_wins) addRankScore(teamstandingslist, "team_pitcher_w", true);
	if (dm_leagueinfo.cat_pitcher_saves) addRankScore(teamstandingslist, "team_pitcher_sv", true);
	if (dm_leagueinfo.cat_pitcher_so) addRankScore(teamstandingslist, "team_pitcher_k", true);
	if (dm_leagueinfo.cat_pitcher_era) addRankScore(teamstandingslist, "team_pitcher_era", false);
	if (dm_leagueinfo.cat_pitcher_whip) addRankScore(teamstandingslist, "team_pitcher_whip", false);

	// For each team, calculate total scoring
	$.each( teamstandingslist, function( index, value ){
		var total_score = 0;
		
		if (dm_leagueinfo.cat_hitter_hr) total_score = total_score + value.team_hitter_hr_score;
		if (dm_leagueinfo.cat_hitter_sb) total_score = total_score + value.team_hitter_sb_score;
		if (dm_leagueinfo.cat_hitter_rbi) total_score = total_score + value.team_hitter_rbi_score;
		if (dm_leagueinfo.cat_hitter_r) total_score = total_score + value.team_hitter_runs_score;
		if (dm_leagueinfo.cat_hitter_avg) total_score = total_score + value.team_hitter_avg_score;
			
		if (dm_leagueinfo.cat_pitcher_wins) total_score = total_score + value.team_pitcher_w_score;
		if (dm_leagueinfo.cat_pitcher_saves) total_score = total_score + value.team_pitcher_sv_score;
		if (dm_leagueinfo.cat_pitcher_so) total_score = total_score + value.team_pitcher_k_score;
		if (dm_leagueinfo.cat_pitcher_era) total_score = total_score + value.team_pitcher_era_score;
		if (dm_leagueinfo.cat_pitcher_whip) total_score = total_score + value.team_pitcher_whip_score;
		
		value['total_score'] = total_score;
	});
	
	console.log("calcStandings teamlist: " + JSON.stringify(teamstandingslist));
	
	dm_teamstandings = teamstandingslist;
	
	loadLeagueStandingsTable(dm_teamstandings, false);
	
}

function addRankScore(teamstandingslist, statcategory, isAscending){
	
	console.log("addRankScore for " + statcategory);
	
	var stat_score = statcategory + '_score';
	
	// Sort on stat
	if (isAscending) {
		teamstandingslist.sort(function(a, b) {
			return parseFloat(a[statcategory]) - parseFloat(b[statcategory]);
		});
	} else {
		teamstandingslist.sort(function(a, b) {
			return parseFloat(b[statcategory]) - parseFloat(a[statcategory]);
		});
	}

	
	// For each team, calculate scoring
	$.each( teamstandingslist, function( index, value ){
		console.log("--value.team_name: " + value.team_name);
		console.log("--stat category: " + value[statcategory]);
		console.log("--index: " + index);
		value[stat_score] = index + 1;
	});
}


function calcLiveAuctionValue(){
	
	console.log("Calc Live Auction Values: BEGIN");

	var t1 = new Date().getTime();
	
	// Calculate LIVE auction value
	var num_teams = dm_globalteamlist.length;
	var rostercounts = dm_teamrostercounts;
	var teamlist = dm_globalteamlist;
	var position_priority_list = dm_leagueinfo.position_priority_list;
	
	console.log("-- Position priority list: " + position_priority_list);
	
	// VERIFIED that priority list comes over correctly
	// TODO: Use priority list to calc live auction values.
	
	var roster_c = num_teams * rostercounts["C"];
	var roster_1b = num_teams * (rostercounts["1B"] + rostercounts["CI"]/2.0 + rostercounts["UT"]/5.0);
	var roster_2b = num_teams * (rostercounts["2B"] + rostercounts["MI"]/2.0 + rostercounts["UT"]/5.0);
	var roster_3b = num_teams * (rostercounts["3B"] + rostercounts["CI"]/2.0 + rostercounts["UT"]/5.0);
	var roster_ss = num_teams * (rostercounts["SS"] + rostercounts["MI"]/2.0 + rostercounts["UT"]/5.0);
	var roster_of = num_teams * (rostercounts["OF"] + rostercounts["UT"]/5.0);
	var roster_p = num_teams * rostercounts["P"];
	
	var roster_c_wRes = roster_c;
	var roster_1b_wRes = roster_1b + (num_teams * rostercounts["RES"]/12.0);
	var roster_2b_wRes = roster_2b + (num_teams * rostercounts["RES"]/12.0);
	var roster_3b_wRes = roster_3b + (num_teams * rostercounts["RES"]/12.0);
	var roster_ss_wRes = roster_ss + (num_teams * rostercounts["RES"]/12.0);
	var roster_of_wRes = roster_of + (num_teams * rostercounts["RES"]/3.0);
	var roster_p_wRes = roster_p + (num_teams * rostercounts["RES"]/3.0);
	
	var iroster_c =  Math.round(roster_c);
	var iroster_1b =  Math.round(roster_1b);
	var iroster_2b =  Math.round(roster_2b);
	var iroster_3b =  Math.round(roster_3b);
	var iroster_ss =  Math.round(roster_ss);
	var iroster_of =  Math.round(roster_of);
	var iroster_p =  Math.round(roster_p);
	
	var iroster_c_wRes =  Math.round(roster_c_wRes);
	var iroster_1b_wRes =  Math.round(roster_1b_wRes);
	var iroster_2b_wRes =  Math.round(roster_2b_wRes);
	var iroster_3b_wRes =  Math.round(roster_3b_wRes);
	var iroster_ss_wRes =  Math.round(roster_ss_wRes);
	var iroster_of_wRes =  Math.round(roster_of_wRes);
	var iroster_p_wRes =  Math.round(roster_p_wRes);
	
	// console.log("Catcher Players, No Reserve: " + iroster_c + " With Reserve: " + iroster_c_wRes);
	// console.log("1B Players, No Reserve: " + iroster_1b + " With Reserve: " + iroster_1b_wRes);
	// console.log("2B Players, No Reserve: " + iroster_2b + " With Reserve: " + iroster_2b_wRes);
	// console.log("3B Players, No Reserve: " + iroster_3b + " With Reserve: " + iroster_3b_wRes);
	// console.log("SS Players, No Reserve: " + iroster_ss + " With Reserve: " + iroster_ss_wRes);
	// console.log("OF Players, No Reserve: " + iroster_of + " With Reserve: " + iroster_of_wRes);
	// console.log("P Players, No Reserve: " + iroster_p + " With Reserve: " + iroster_p_wRes);
	
	// Sort players by descending Z
	var data_table = $('#playergrid_table').DataTable();
	var data_rows = data_table.rows().data();

	data_rows.sort(function(a, b) {
	    return parseFloat(b.total_z) - parseFloat(a.total_z);
	});
	
	var t2 = new Date().getTime();

	// Get total z values
	var posz_c = getPositionalZ(data_rows, "C", iroster_c, position_priority_list);
	var posz_1b = getPositionalZ(data_rows, "1B", iroster_1b, position_priority_list);
	var posz_2b = getPositionalZ(data_rows, "2B", iroster_2b, position_priority_list);
	var posz_3b = getPositionalZ(data_rows, "3B", iroster_3b, position_priority_list);
	var posz_ss = getPositionalZ(data_rows, "SS", iroster_ss, position_priority_list);
	var posz_of = getPositionalZ(data_rows, "OF", iroster_of, position_priority_list);
	var posz_p = getPositionalZ(data_rows, "P", iroster_p, position_priority_list);
	var replval_dh = (posz_1b["avgreplz"] + posz_of["avgreplz"])/2;

	var posz_total = posz_c["totalz"] + posz_1b["totalz"] + posz_2b["totalz"]
			+ posz_3b["totalz"] + posz_ss["totalz"] + posz_of["totalz"] + posz_p["totalz"];
	
	var total_league_salary = 0;
	
	$.each( teamlist, function( index, value ){
		// console.log("Team: " + value.team_name + ", adj_sal: " + value.adj_starting_salary);
		total_league_salary += value.adj_starting_salary;
	});
	
	var total_draftedplayersalary = getTotalDraftedSalary(data_rows);
	
	// console.log("Total adj Salary: " + total_league_salary);
	// console.log("Total drafted player Salary: " + total_draftedplayersalary);
	
	var coef = (total_league_salary-total_draftedplayersalary)/posz_total;
	
	// console.log("Coeff: " + coef);
	
	var t3 = new Date().getTime();
	
	// Update auction value
	$.each( data_rows, function( index, value ){

		if (value.unknownplayer == false){
			
			var auct = 0;
			
			// If a player is multi-position, these if statements ensure the 
			// position with the highest auction value is used.
			if ((value.player_position.toLowerCase().indexOf("c") > -1))
				auct = Math.max(auct,(value.total_z - posz_c["avgreplz"])*coef);
			if ((value.player_position.toLowerCase().indexOf("1b") > -1))
				auct = Math.max(auct,(value.total_z - posz_1b["avgreplz"])*coef);
			if ((value.player_position.toLowerCase().indexOf("2b") > -1))
				auct = Math.max(auct,(value.total_z - posz_2b["avgreplz"])*coef);
			if ((value.player_position.toLowerCase().indexOf("ss") > -1))
				auct = Math.max(auct,(value.total_z - posz_ss["avgreplz"])*coef);
			if ((value.player_position.toLowerCase().indexOf("3b") > -1))
				auct = Math.max(auct,(value.total_z - posz_3b["avgreplz"])*coef);
			if ((value.player_position.toLowerCase().indexOf("of") > -1))
				auct = Math.max(auct,(value.total_z - posz_of["avgreplz"])*coef);
			if ((value.player_position.toLowerCase().indexOf("p") > -1))
				auct = Math.max(auct,(value.total_z - posz_p["avgreplz"])*coef);
			if ((value.player_position.toLowerCase().indexOf("dh") > -1))
				auct = Math.max(auct,(value.total_z - replval_dh)*coef);
			
			if (auct < 0) auct = 0;
			
			value.live_auction_value = Math.round(auct);
			
			data_table = $('#playergrid_table').DataTable();
			data_table.row('#' + this.id + '').data(this);
			
		}

	});
	
	var t4 = new Date().getTime();

//	var j=0;
//	$.each( data_rows, function( index, value ){
//		console.log("Player: " + value.full_name + ", live $: " + value.live_auction_value);
//		if (j == 50) return false;
//		j++;
//	});
	
	data_table.draw( 'page' );
	
	// console.log("Calc Live Auction Values: COMPLETE");
	
	var t5 = new Date().getTime();
	
}


/**
 * Description:	Calculate the sum of the drafted salaries of all drafted players 
 * @param leagueplayers
 * @return 
 */
function getTotalDraftedSalary(playertablerows){
	
	var draftedplayersalary = 0;
	
	$.each( playertablerows, function( index, value ){
		
		// Sum salaries of all drafted players
		if ((value.leagueteam_name != null)&&(value.leagueteam_name != "")) {
			draftedplayersalary =  parseInt(draftedplayersalary) + parseInt(value.team_player_salary);
			// console.log("Adding drafted salary for: "+ value.full_name + ", Z: " + value.team_player_salary + ", running: " + draftedplayersalary);
		}

	});
	
	return draftedplayersalary;
	
}


/**
 * Description:	Calculate the total Z value for the given position up to the replacement level (for live calc, sans drafted players).
 * 				Also determines the avg replacement value. 
 * @param leagueplayers
 * @param position
 * @param repl_level
 * @return 
 */
function getPositionalZ(playertablerows, position, position_num, priority){
	
	var i = 0;
	var totalz = 0;
	var avgz = 0;

	$.each( playertablerows, function( index, value ){
		
		if (value.unknownplayer == false){
			
			// Check if player is eligible for position, and if player has multi-position
			// then also check if position is the highest priority position that the player holds.
			// AND check if the count of players at that position is still less than the replacement level
			if ((isPlayerPositionPriority(position, value.player_position, priority))
					&& (i < position_num)){
				
				// Add Z value only if player is undrafted
				if ((value.leagueteam_name == null)||(value.leagueteam_name == "")) {
					totalz = totalz + value.total_z;
					if (position == "C")  console.log("Adding Z for: "+ value.full_name + ", Z: " + value.total_z);
				} else {
					console.log("In getPositionalZ DRAFTED PLAYER: "+ value.full_name + ", Team: " + value.leagueteam_name + ", Z: " + value.total_z);
				}
					
				i++;
					
			} else if ((isPlayerPositionPriority(position, value.player_position, priority))
					&& (i == position_num)){
				
				avgz = avgz + value.total_z;
				i++;
				
				//  System.out.println(position + "-AVG1: " + lp.getTotal_z());
				
			} else if ((isPlayerPositionPriority(position, value.player_position, priority))
					&& (i == position_num + 1)){
				
				avgz = avgz + value.total_z;
				i++;
				
				// System.out.println(position + "-AVG2: " + lp.getTotal_z());
				
			} else if (i > position_num + 1) {return false;}
			
		}
		
		// Original code, before utilizing position priority
		// to count multi-position players only once.
//		if (value.unknownplayer == false){
//			
//			if ((value.player_position.toLowerCase().indexOf(position.toLowerCase()) > -1)
//					&& (i < position_num)){
//				
//				// Add Z value only if player is undrafted
//				if ((value.leagueteam_name == null)||(value.leagueteam_name == "")) {
//					totalz = totalz + value.total_z;
//					// if (position == "C")  // console.log("Adding Z for: "+ value.full_name + ", Z: " + value.total_z);
//				} else {
//					// console.log("In getPositionalZ DRAFTED PLAYER: "+ value.full_name + ", Team: " + value.leagueteam_name + ", Z: " + value.total_z);
//				}
//					
//				i++;
//					
//			} else if ((value.player_position.toLowerCase().indexOf(position.toLowerCase()) > -1)
//					&& (i == position_num)){
//				
//				avgz = avgz + value.total_z;
//				i++;
//				
//				// System.out.println(position + "-AVG1: " + lp.getTotal_z());
//				
//			} else if ((value.player_position.toLowerCase().indexOf(position.toLowerCase()) > -1)
//					&& (i == position_num + 1)){
//				
//				avgz = avgz + value.total_z;
//				i++;
//				
//				// System.out.println(position + "-AVG2: " + lp.getTotal_z());
//				
//			} else if (i > position_num + 1) {return false;}
//			
//		}

	});
	
	avgz = avgz/2;
	totalz = totalz - position_num*avgz;
	
	var PositionalZOutput = {};
	PositionalZOutput["totalz"] = totalz;
	PositionalZOutput["avgreplz"] = avgz
	
	 console.log(position + "-TOTAL Z: " + totalz);
	 console.log(position + "-AVG REPL Z: " + avgz);
	
	return PositionalZOutput;
	
}

/**
 * Description:	Determine if the given position (1) matches to the player position string and
 * 				(2) is the highest priority position in the player position string
 * @param position
 * @param playerposition
 * @param priority
 * @return boolean
 */
function isPlayerPositionPriority(position, playerposition, priority){

	// Does the player position string contain the position being looked for?
	if (playerposition.toLowerCase().indexOf(position.toLowerCase()) > -1){

		if (playerposition.indexOf(",") > -1){
			console.log("Checking isPlayerPositionPriority");
			console.log("-- Player with position elig '" + playerposition + "' has position '" + position + "'");
			
			// For each position in the position priority list (starting from highest priority)
			var arrayLength = priority.length;
			for (var i = 0; i < arrayLength; i++){
				console.log("-- Checking position priority '" + priority[i] + "'");
				if (position.toLowerCase().indexOf(priority[i].toLowerCase()) > -1){
					console.log("-- Position '" + position + "' is the highest priority for player eligibility '" + playerposition + "'");
					return true;
				}
				else if (playerposition.toLowerCase().indexOf(priority[i].toLowerCase()) > -1){
					console.log("-- Position '" + position + "' is the NOT THE HIGHEST priority for player eligibility '" + playerposition + "'");
					return false;
				}
				
			}
			
			return false;
			
		} else return true;

		
		
	} 
	else {
		// System.out.println("-- Player with position elig '" + playerposition + "' DOES NOT HAVE position '" + position + "'");
		return false;
	}
	
}

function getReplPitcher(){
	
	var LeaguePlayerOutput = {};
	
	LeaguePlayerOutput["pitcher_era"] = 0;
	LeaguePlayerOutput["pitcher_era_eff"] = 0;
	LeaguePlayerOutput["pitcher_w"] = 0;
	LeaguePlayerOutput["pitcher_sv"] = 0;
	LeaguePlayerOutput["pitcher_whip"] = 0;
	LeaguePlayerOutput["pitcher_whip_eff"] = 0;
	LeaguePlayerOutput["pitcher_k"] = 0;
	LeaguePlayerOutput["pitcher_hitter"] = "P";
	
	LeaguePlayerOutput["pitcher_z_wins"] = 0;
	LeaguePlayerOutput["pitcher_z_saves"] = 0;
	LeaguePlayerOutput["pitcher_z_so"] = 0;
	LeaguePlayerOutput["pitcher_z_era"] = 0;
	LeaguePlayerOutput["pitcher_z_whip"] = 0;
	
	LeaguePlayerOutput["init_auction_value"] = 0;
	LeaguePlayerOutput["live_auction_value"] = 0;

	return LeaguePlayerOutput;
	
}

function getReplHitter(){
	
	var LeaguePlayerOutput = {};
	
	LeaguePlayerOutput["hitter_avg"] = 0;
	LeaguePlayerOutput["hitter_avg_eff"] = 0;
	LeaguePlayerOutput["hitter_rbi"] = 0;
	LeaguePlayerOutput["hitter_runs"] = 0;
	LeaguePlayerOutput["hitter_sb"] = 0;
	LeaguePlayerOutput["hitter_hr"] = 0;
	LeaguePlayerOutput["pitcher_hitter"] = "H";
	
	LeaguePlayerOutput["hitter_z_avg"] = 0;
	LeaguePlayerOutput["hitter_z_hr"] = 0;
	LeaguePlayerOutput["hitter_z_sb"] = 0;
	LeaguePlayerOutput["hitter_z_runs"] = 0;
	LeaguePlayerOutput["hitter_z_rbi"] = 0;

	LeaguePlayerOutput["init_auction_value"] = 0;
	LeaguePlayerOutput["live_auction_value"] = 0;
	
	return LeaguePlayerOutput;
	
}



/**
 * Description:	Calculates z score
 * @param player_value
 * @param mean
 * @param std_dev
 * @return calculated z value
 */
//private double calcZ (double player_value, double mean, double std_dev){
//	double z = (player_value-mean)/std_dev;
//	return z;
//}
