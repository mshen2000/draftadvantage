package com.app.endpoints;

import java.util.List;

import com.nya.sms.entities.PlayerProjected;

/**
 * @author Michael
 *	Container bean object for list of player projections.
 *	REST service cannot pass List as parameter, container object
 *	is required.
 */
public class APIPlayerProjectionContainer {
	
	private List<PlayerProjected> playerlist;

	public List<PlayerProjected> getPlayerlist() {
		return playerlist;
	}

	public void setPlayerlist(List<PlayerProjected> playerlist) {
		this.playerlist = playerlist;
	}
	

}
