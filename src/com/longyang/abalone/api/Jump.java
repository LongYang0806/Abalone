package com.longyang.abalone.api;

import com.longyang.abalone.impl.AbaloneUtilities;

/**
 * Class used to present a jump for a player, which 
 * contains the original coordinates and destination
 * coordinates.
 *
 * @author Long Yang (ly603@nyu.edu)
 *
 */
public class Jump {
	private final int original_x;
	private final int original_y;
	private final int destination_x;
	private final int destination_y;
	
	/**
	 * Constructor used to create a new jump object.
	 * @param original_x input original x coordinate.
	 * @param original_y input original y coordinate.
	 * @param destination_x input destination x coordinate.
	 * @param destination_y input destination y coordinate.
	 * @throws IllegalArgumentException if input data break the data validation 
	 * (x: [0, 18]; y: [0, 10])
	 */
	public Jump(int original_x, int original_y, int destination_x, int destination_y){
		/*
		 * check the input data's validation before assigning.
		 */
		AbaloneUtilities.check(original_x >= 0 && original_x <= 18);
		AbaloneUtilities.check(destination_x >= 0 && destination_x <= 18);
		AbaloneUtilities.check(original_y >= 0 && original_y <= 10);
		AbaloneUtilities.check(destination_y >= 0 && destination_y <= 10);
		this.original_x = original_x;
		this.original_y = original_y;
		this.destination_x = destination_x;
		this.destination_y = destination_y;
	}
	
	@Override
	public boolean equals(Object other){
		if(other == null){
			return false;
		}
		if(other == this){
			return true;
		}
		if(other instanceof Jump){
			Jump otherJump = (Jump) other;
			if( original_x == otherJump.getOriginalX() &&
					original_y == otherJump.getOriginalY() &&
					destination_x == otherJump.getDestinationX() &&
					destination_y == otherJump.getDestinationY()){
				return true;
			}else{
				return false;
			}
		}
		return false;
	}
	
	public int getOriginalX(){
		return original_x;
	}
	
	public int getOriginalY(){
		return original_y;
	}
	
	public int getDestinationX(){
		return destination_x;
	}
	
	public int getDestinationY(){
		return destination_y;
	}
}
