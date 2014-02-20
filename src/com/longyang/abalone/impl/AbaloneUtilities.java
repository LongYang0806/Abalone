package com.longyang.abalone.impl;

import java.util.List;

import com.longyang.abalone.api.AbaloneConstants;
import com.longyang.abalone.api.Square;

import com.google.common.collect.ImmutableList;
import com.longyang.abalone.api.Square;

/**
 * Class used to provide all kinds of utilities which can make other parts' work easier.
 * 
 * @author Long Yang (ly603@nyu.edu)
 *
 */
public class AbaloneUtilities {
	
	/**
	 * Method used to convert the String Board to Square board.
	 * 
	 * @param stringBoard input Abalone game board which is represented by String.
	 * @throws IllegalArgumentException if {@code stringBoard} contains invalid input.
	 * @return converted Square Abalone game board.
	 */
	public static List<ImmutableList<Square>> stringBoardToSquareBoard(
			List<ImmutableList<String>> stringBoard){
		if(stringBoard == null || stringBoard.isEmpty()){
			throw new IllegalArgumentException("Input board should not be null or empty");
		}
		
		ImmutableList.Builder<ImmutableList<Square>> squareBoard = 
				ImmutableList.<ImmutableList<Square>>builder();
		
		try{
			for(List<String> rowOfStringBoard : stringBoard){
				ImmutableList.Builder<Square> rowOfSquareBoard = new ImmutableList.Builder<Square>();
				for(String strSquare : rowOfStringBoard){
					rowOfSquareBoard.add(stringToSquare(strSquare));
				}
				squareBoard.add(rowOfSquareBoard.build());
			}
			return squareBoard.build();
		} catch(IllegalArgumentException e){
			throw e;
		}
	}
	
	/**
	 * Method used to convert string to Square
	 * @param str input string
	 * @throws IllegalArgumentException if input string is null, empty or invalid (not W, B, E, I).
	 * @return Regarding Square object.
	 */
	public static Square stringToSquare(String str){
		if(str == null || str.isEmpty()){
			throw new IllegalArgumentException("Input str should not be null or empty!");
		}
		
		if(AbaloneConstants.W.equals(str)){
			return Square.W;
		}else if(AbaloneConstants.B.equals(str)){
			return Square.B;
		}else if(AbaloneConstants.E.equals(str)){
			return Square.E;
		}else if(AbaloneConstants.I.equals(str)){
			return Square.I;
		}else{
			throw new IllegalArgumentException("Input str is invalid.");
		}
	}
	
	/**
	 * Method used to check whether the given condition is true.
	 * @param condition to be tested condition
	 * @param message optional debug messages.
	 * @throws RuntimeException if the give {@code condition} is false.
	 */
	public static void check(boolean condition, String... message){
		if(!condition){
			throw new RuntimeException("Hacker found: " + message);
		}
	}
	
}
