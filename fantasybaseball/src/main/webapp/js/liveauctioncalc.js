

function calcLiveAuctionValue(){
	
	console.log("Calc Live Auction Values: BEGIN");

	var t1 = new Date().getTime();
	
	// Calculate LIVE auction value
	var num_teams = dm_globalteamlist.length;
	var rostercounts = dm_teamrostercounts;
	var teamlist = dm_globalteamlist;
	
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
	
	console.log("Catcher Players, No Reserve: " + iroster_c + " With Reserve: " + iroster_c_wRes);
	console.log("1B Players, No Reserve: " + iroster_1b + " With Reserve: " + iroster_1b_wRes);
	console.log("2B Players, No Reserve: " + iroster_2b + " With Reserve: " + iroster_2b_wRes);
	console.log("3B Players, No Reserve: " + iroster_3b + " With Reserve: " + iroster_3b_wRes);
	console.log("SS Players, No Reserve: " + iroster_ss + " With Reserve: " + iroster_ss_wRes);
	console.log("OF Players, No Reserve: " + iroster_of + " With Reserve: " + iroster_of_wRes);
	console.log("P Players, No Reserve: " + iroster_p + " With Reserve: " + iroster_p_wRes);
	
	// Sort players by descending Z
	var data_table = $('#playergrid_table').DataTable();
	var data_rows = data_table.rows().data();

	data_rows.sort(function(a, b) {
	    return parseFloat(b.total_z) - parseFloat(a.total_z);
	});
	
	var t2 = new Date().getTime();

	// Get total z values
	var posz_c = getPositionalZ(data_rows, "C", iroster_c);
	var posz_1b = getPositionalZ(data_rows, "1B", iroster_1b);
	var posz_2b = getPositionalZ(data_rows, "2B", iroster_2b);
	var posz_3b = getPositionalZ(data_rows, "3B", iroster_3b);
	var posz_ss = getPositionalZ(data_rows, "SS", iroster_ss);
	var posz_of = getPositionalZ(data_rows, "OF", iroster_of);
	var posz_p = getPositionalZ(data_rows, "P", iroster_p);
	var replval_dh = (posz_1b["avgreplz"] + posz_of["avgreplz"])/2;

	var posz_total = posz_c["totalz"] + posz_1b["totalz"] + posz_2b["totalz"]
			+ posz_3b["totalz"] + posz_ss["totalz"] + posz_of["totalz"] + posz_p["totalz"];
	
	var total_league_salary = 0;
	
	$.each( teamlist, function( index, value ){
		// console.log("Team: " + value.team_name + ", adj_sal: " + value.adj_starting_salary);
		total_league_salary += value.adj_starting_salary;
	});
	
	var total_draftedplayersalary = getTotalDraftedSalary(data_rows);
	
	console.log("Total adj Salary: " + total_league_salary);
	console.log("Total drafted player Salary: " + total_draftedplayersalary);
	
	var coef = (total_league_salary-total_draftedplayersalary)/posz_total;
	
	console.log("Coeff: " + coef);
	
	var t3 = new Date().getTime();
	
	// Update auction value
	$.each( data_rows, function( index, value ){

		if (value.unknownplayer == false){
			
			var auct = 0;
			
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
	
	console.log("Calc Live Auction Values: COMPLETE");
	
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
function getPositionalZ(playertablerows, position, position_num){
	
	var i = 0;
	var totalz = 0;
	var avgz = 0;

	$.each( playertablerows, function( index, value ){
		
		if (value.unknownplayer == false){
			
			if ((value.player_position.toLowerCase().indexOf(position.toLowerCase()) > -1)
					&& (i < position_num)){
				
				// Add Z value only if player is undrafted
				if ((value.leagueteam_name == null)||(value.leagueteam_name == "")) {
					totalz = totalz + value.total_z;
					// if (position == "C")  console.log("Adding Z for: "+ value.full_name + ", Z: " + value.total_z);
				} else {
					// console.log("In getPositionalZ DRAFTED PLAYER: "+ value.full_name + ", Team: " + value.leagueteam_name + ", Z: " + value.total_z);
				}
					
				i++;
					
			} else if ((value.player_position.toLowerCase().indexOf(position.toLowerCase()) > -1)
					&& (i == position_num)){
				
				avgz = avgz + value.total_z;
				i++;
				
				// System.out.println(position + "-AVG1: " + lp.getTotal_z());
				
			} else if ((value.player_position.toLowerCase().indexOf(position.toLowerCase()) > -1)
					&& (i == position_num + 1)){
				
				avgz = avgz + value.total_z;
				i++;
				
				// System.out.println(position + "-AVG2: " + lp.getTotal_z());
				
			} else if (i > position_num + 1) {return false;}
			
		}

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
