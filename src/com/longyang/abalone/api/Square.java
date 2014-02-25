package com.longyang.abalone.api;

public enum Square {
	W, // Square occupied by White piece.
	B, // Square occupied by Black piece.
	S, // Score Square.
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
	
	@Override
	public String toString(){
		switch(this){
			case W:
				return AbaloneConstants.W;
			case B:
				return AbaloneConstants.B;
			case S:
				return AbaloneConstants.S;
			case E:
				return AbaloneConstants.E;
			case I:
				return AbaloneConstants.I;
			default:
				return null;
		}
	}
	
}
