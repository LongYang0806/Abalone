package com.longyang.abalone.api;

public enum Square {
	W, // Square occupied by White piece.
	B, // Square occupied by Black piece.
	E, // Empty Square.
	I; // Illegal Square.
	
	/**
	 * Method used to determine whether color of the piece is White.
	 * @return true if color of the piece is white, false otherwise.
	 */
	public boolean isWhite(){
		return this == W;
	}
	
	/**
	 * Method used to determine whether color of the piece is Black.
	 * @return true if color of the piece is black, false otherwise.
	 */
	public boolean isBlack(){
		return this == B;
	}
	
}
