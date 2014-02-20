package com.longyang.abalone.api;

public enum Turn {
	WP, BP;
	
	/**
	 * Method used to determine whether this is the white player.
	 * @return true if this is the white player, false otherwise.
	 */
	public boolean isWhite(){
		return this == WP;
	}
	
	/**
	 * Method used to determine whether this is the black player.
	 * @return true if this is the black player, false otherwise.
	 */
	public boolean isBlack(){
		return this == BP;
	}
	
	/**
	 * Method used to get the opposite turn of the play.
	 * @return WP if this is the black player's turn, BP otherwise.
	 */
	public Turn getOppositeTurn(){
		if(this == WP){
			return BP;
		}else{
			return WP;
		}
	}
}
