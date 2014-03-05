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
	
	@Override
	public String toString(){
		if(this == WP){
			return "White Hand";
		}else{
			return "Red Hand";
		}
	}
	
	/**
	 * Method used to convert {@link Turn} to {@link Square}
	 * @return W is this is WP, B otherwise.
	 */
	public Square getSquare(){
		if(this == WP){
			return Square.W;
		}else{
			return Square.B;
		}
	}
}
