package com.longyang.abalone.api;

import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.longyang.abalone.impl.AbaloneUtilities;

/**
 * Class used to present a jump for a player, which 
 * contains the original coordinates and destination
 * coordinates.
 *
 * @author Long Yang (ly603@nyu.edu)
 *
 */
public class Jump implements Comparable<Jump>{
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
	 * (x: [0, 10]; y: [0, 18])
	 */
	public Jump(int original_x, int original_y, int destination_x, int destination_y){
		/*
		 * check the input data's validation before assigning.
		 */
		AbaloneUtilities.check(original_x >= 0 && original_x <= 10);
		AbaloneUtilities.check(destination_x >= 0 && destination_x <= 10);
		AbaloneUtilities.check(original_y >= 0 && original_y <= 18);
		AbaloneUtilities.check(destination_y >= 0 && destination_y <= 18);
		this.original_x = original_x;
		this.original_y = original_y;
		this.destination_x = destination_x;
		this.destination_y = destination_y;
	}
	
	/**
	 * Method used to convert {@code List<Integer>} jump to {@link Jump} jump.
	 * @param jump input {@code List<Integer>} jump
	 * @throws RuntimeException if input jump is null or size is not 4.
	 * @return a {@link Jump} object.
	 */
	public static Jump fromIntegerListToJump(List<Integer> jump){
		AbaloneUtilities.check(jump != null && jump.size() == 4, 
				"Input integer-list jump can not be null and must contain 4 coordinates!");
		return new Jump(jump.get(0), jump.get(1), jump.get(2), jump.get(3));
	}
	
	public static List<Jump> fromIntegerListListToJumpList(List<ImmutableList<Integer>> jumps){
		List<Jump> res = Lists.newArrayList();
		for(List<Integer> jump : jumps){
			res.add(fromIntegerListToJump(jump));
		}
		return res;
	}
	
	/**
	 * Method used to convert a {@link Jump} jump to {@code List<Integer>} jump
	 * @param jump input {@link Jump} jump
	 * @throws RuntimeException if input jump is null.
	 * @return an {@code ImmutableList<Integer>} whose size is 4.
	 */
	public static List<Integer> fromJumpToIntegerList(Jump jump){
		AbaloneUtilities.check(jump != null, 
				"Input jump can not be null!");
		return ImmutableList.<Integer>of(jump.getOriginalX(),
				jump.getOriginalY(), jump.getDestinationX(), jump.getDestinationY());
	}
	
	/**
	 * Static method used to convert {@code List<Jump>} jumps to 
	 * {@code List<ImmutableList<Integer>>} jumps
	 * @param jumps input {@code List<Jump>} jumps
	 * @throws RuntimeException if input {@code jumps} is null.
	 * @return output {@code List<ImmutableList<Integer>>} jumps
	 */
	public static List<ImmutableList<Integer>> listJumpToListInteger(List<Jump> jumps){
		AbaloneUtilities.check(jumps != null, 
				"Jumps can not be null!");
		ImmutableList.Builder<ImmutableList<Integer>> jumpList = 
				ImmutableList.<ImmutableList<Integer>>builder();
		for(Jump jump : jumps){
			ImmutableList<Integer> integerJump = ImmutableList.<Integer>of(
					jump.getOriginalX(),
					jump.getOriginalY(),
					jump.getDestinationX(),
					jump.getDestinationY()
			);
			jumpList.add(integerJump);
		}
		return jumpList.build();
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
	
	@Override
	public int compareTo(Jump jump){
		if(jump == null){
			return 1;
		}
		if(original_x > jump.getOriginalX()){
			return 1;
		}else if(original_x < jump.getOriginalX()){
			return -1;
		}else{
			if(original_y > jump.getOriginalY()){
				return 1;
			}else if(original_y == jump.getOriginalY()){
				return 0;
			}else{
				return -1;
			}
		}
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
	
	@Override
	public String toString(){
		return "[(" + original_x + "," + original_y + "), " + 
				"(" + destination_x + "," + destination_y + ")]";
	}
}
