

function calcLiveAuctionValue(){
	
	console.log("Get Player Output Data: BEGIN");

	console.log("Get Player Output Data: Convert player projections to output...");

	console.log("Get Player Output Data: Calculating league means and std deviations...");

	console.log("Get Player Output Data: Calculating LeaguePlayer Z scores...");

	console.log("Get Player Output Data: Calculating LIVE auction values...");
	
	// Calculate LIVE auction value
	var num_teams = globalteamlist.length;
	var rostercounts = dm_teamrostercounts;
	
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
	
	console.log(JSON.stringify(data_rows));
	
	// Live auction calc requires that "playeroutput" takes the full list of players but only calculates
	// total z value based on undrafted players.
	
	/*
	// Get total z values
	var posz_c = getPositionalZ(data_rows, "C", iroster_c);
	var posz_1b = getPositionalZ(data_rows, "1B", iroster_1b);
	var posz_2b = getPositionalZ(data_rows, "2B", iroster_2b);
	var posz_3b = getPositionalZ(data_rows, "3B", iroster_3b);
	var posz_ss = getPositionalZ(data_rows, "SS", iroster_ss);
	var posz_of = getPositionalZ(data_rows, "OF", iroster_of);
	var posz_p = getPositionalZ(data_rows, "P", iroster_p);
	double replval_dh = (posz_1b.getReplacementvalue() + posz_of.getReplacementvalue())/2;

	double posz_total = posz_c.getTotalvalue() + posz_1b.getTotalvalue() + posz_2b.getTotalvalue()
			+ posz_3b.getTotalvalue() + posz_ss.getTotalvalue() + posz_of.getTotalvalue() + posz_p.getTotalvalue();
	
	double coef = (league.getTeam_salary()*league.getNum_of_teams())/posz_total;
	
	// Update auction value
	for (LeaguePlayerOutput po : playeroutput){
		
		double auct = 0;
		
		if (po.getPlayer_position().toLowerCase().contains("c")) 
			auct = Math.max(auct,(po.getTotal_z()-posz_c.getReplacementvalue())*coef);
		if (po.getPlayer_position().toLowerCase().contains("1b")) 
			auct = Math.max(auct,(po.getTotal_z()-posz_1b.getReplacementvalue())*coef);
		if (po.getPlayer_position().toLowerCase().contains("2b")) 
			auct = Math.max(auct,(po.getTotal_z()-posz_2b.getReplacementvalue())*coef);
		if (po.getPlayer_position().toLowerCase().contains("3b")) 
			auct = Math.max(auct,(po.getTotal_z()-posz_3b.getReplacementvalue())*coef);
		if (po.getPlayer_position().toLowerCase().contains("ss")) 
			auct = Math.max(auct,(po.getTotal_z()-posz_ss.getReplacementvalue())*coef);
		if (po.getPlayer_position().toLowerCase().contains("of")) 
			auct = Math.max(auct,(po.getTotal_z()-posz_of.getReplacementvalue())*coef);
		if (po.getPlayer_position().toLowerCase().contains("p")) 
			auct = Math.max(auct,(po.getTotal_z()-posz_p.getReplacementvalue())*coef);
		if (po.getPlayer_position().toLowerCase().contains("dh")) 
			auct = Math.max(auct,(po.getTotal_z()-replval_dh)*coef);
		
		if (auct < 0) auct = 0;
		
		po.setInit_auction_value((int)Math.round(auct));

	}
	
	console.log("Get Player Output Data: Updating with League Player data...");
	
	List<LeaguePlayer> lplist = getLeaguePlayerService().getLeaguePlayersByLeague(league_id, username);
		

	console.log("Get Player Output Data: " + lplist.size() + " LeaguePlayers found and updated.");
	
//	for (int out = 0; out < 150; out++) {
//		console.log("--Player Test: " + playeroutput.get(out).getFull_name() + ", "
//				+ playeroutput.get(out).getPlayer_position() + ", " + playeroutput.get(out).getInit_auction_value()
//				+ ", " + playeroutput.get(out).getPitcher_whip_eff() + ", " + playeroutput.get(out).getPitcher_z_whip());
//	+ ", " + playeroutput.get(out).getTeam_player_note());
//	}
	
	console.log("Get Player Output Data: COMPLETE");
	
	return playeroutput;
	*/
}




/**
 * Description:	Calculate the total Z value for the given position up to the replacement level (for live calc, sans drafted players).
 * 				Also determines the avg replacement value. 
 * @param leagueplayers
 * @param position
 * @param repl_level
 * @return 
 */
//function getPositionalZ(playertable, position, repl_level_num){
//	
//	var i = 0;
//	var totalz = 0;
//	var avgz = 0;
//	
//	PositionalZContainer p = new PositionalZContainer();
//	
//	for (LeaguePlayerOutput po : leagueplayers){
//		
//		if ((po.getPlayer_position().toLowerCase().contains(position.toLowerCase())) 
//			&& (i < repl_level)){
//			
//			totalz = totalz + po.getTotal_z();
//			i++;
//			
//			// System.out.println(position + ": " + lp.getTotal_z());
//			
//		} else if ((po.getPlayer_position().toLowerCase().contains(position.toLowerCase())) 
//				&& (i == repl_level)){
//			
//			avgz = avgz + po.getTotal_z();
//			i++;
//			
//			// System.out.println(position + "-AVG1: " + lp.getTotal_z());
//			
//		} else if ((po.getPlayer_position().toLowerCase().contains(position.toLowerCase())) 
//				&& (i == repl_level + 1)){
//			
//			avgz = avgz + po.getTotal_z();
//			i++;
//			
//			// System.out.println(position + "-AVG2: " + lp.getTotal_z());
//			
//		} else if (i > repl_level + 1) {break;}
//
//	}
//	
//	avgz = avgz/2;
//	totalz = totalz - repl_level*avgz;
//	
//	// System.out.println(position + "-TOTAL: " + totalz);
//	
//	p.setTotalvalue(totalz);
//	p.setReplacementvalue(avgz);
//	
//	return p;
//	
//}



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
