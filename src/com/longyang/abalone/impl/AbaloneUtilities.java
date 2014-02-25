package com.longyang.abalone.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableList;
import com.longyang.abalone.api.AbaloneConstants;
import com.longyang.abalone.api.GameApi.Operation;
import com.longyang.abalone.api.GameApi.Set;
import com.longyang.abalone.api.GameApi.SetTurn;
import com.longyang.abalone.api.Jump;
import com.longyang.abalone.api.Square;
import com.longyang.abalone.api.Turn;

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
	
	public static List<ImmutableList<String>> squareBoardToStringBoard(
			List<ImmutableList<Square>> squareBoard){
		if(squareBoard == null || squareBoard.isEmpty()){
			throw new IllegalArgumentException("Input board should not be null or empty");
		}
		
		ImmutableList.Builder<ImmutableList<String>> stringBoard = 
				ImmutableList.<ImmutableList<String>>builder();
		
		for(List<Square> rowOfSquareBoard : squareBoard){
			ImmutableList.Builder<String> rowOfStringBoard = new ImmutableList.Builder<String>();
			for(Square square : rowOfSquareBoard){
				rowOfStringBoard.add(square.toString());
			}
			stringBoard.add(rowOfStringBoard.build());
		}
		return stringBoard.build();
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
		}else if(AbaloneConstants.S.equals(str)){
			return Square.S;
		}else{
			throw new IllegalArgumentException("Input str is invalid.");
		}
	}
	
	@SuppressWarnings("unchecked")
	public static AbaloneState gameApiStateToAbaloneState(Map<String, Object> gameApiState,
			Turn turn, List<Integer> playerIds){
		if(gameApiState == null || gameApiState.isEmpty()){
			// return empty state.
			return new AbaloneState(Turn.WP, ImmutableList.<Integer>of(), 
					ImmutableList.<ImmutableList<Square>>of(), 
					ImmutableList.<ImmutableList<ImmutableList<Integer>>>of(), null);
		}
		
		// can not be isGameEnd state at first.
		return new AbaloneState(turn, ImmutableList.<Integer>copyOf(playerIds), 
				AbaloneUtilities.stringBoardToSquareBoard((List<ImmutableList<String>>)gameApiState.get(AbaloneConstants.BOARD)), 
				(List<ImmutableList<ImmutableList<Integer>>>)gameApiState.get(AbaloneConstants.JUMP), null);
	}
	
	/**
	 * Method used to apply {@link Jump} jumps on {@code List<ImmutableList<Integer>>} board.
	 * @param board input {@code List<ImmutableList<Integer>>} board for abalone game
	 * @param jumps input {@link Jump} jumps
	 * @return new applied jumps board.
	 */
	public static List<ImmutableList<Square>> boardAppliedJumps(List<ImmutableList<Square>> board, 
			List<Jump> jumps){
		List<ArrayList<Square>> newBoard = new ArrayList<ArrayList<Square>>();
		for(List<Square> row : board){
			ArrayList<Square> newRow = new ArrayList<Square>();
			for(Square square : row){
				newRow.add(square);
			}
			newBoard.add(newRow);
		}
		List<ImmutableList<Integer>> whiteHandJump = new ArrayList<ImmutableList<Integer>>();
		List<ImmutableList<Integer>> blackHandJump = new ArrayList<ImmutableList<Integer>>();
		for(Jump jump : jumps){
			if(board.get(jump.getOriginalX()).get(jump.getOriginalY()) == Square.W){
				whiteHandJump.add((ImmutableList<Integer>)Jump.fromJumpToIntegerList(jump));
			}else if(board.get(jump.getOriginalX()).get(jump.getOriginalY()) == Square.E){
				blackHandJump.add((ImmutableList<Integer>)Jump.fromJumpToIntegerList(jump));
			}
		}
		
		for(List<Integer> coords : whiteHandJump){
			newBoard.get(coords.get(0)).set(coords.get(1), Square.E);
		}
		for(List<Integer> coords : blackHandJump){
			newBoard.get(coords.get(0)).set(coords.get(1), Square.E);
		}
		for(List<Integer> coords : whiteHandJump){
			newBoard.get(coords.get(2)).set(coords.get(3), Square.W);
		}
		for(List<Integer> coords : blackHandJump){
			newBoard.get(coords.get(2)).set(coords.get(3), Square.B);
		}
		
		ImmutableList.Builder<ImmutableList<Square>> newImmutableBoardBuilder = 
				ImmutableList.<ImmutableList<Square>>builder();
		
		for(List<Square> row : newBoard){
			ImmutableList.Builder<Square> rowBuilder = ImmutableList.<Square>builder();
			for(Square entity : row){
				rowBuilder.add(entity);
			}
			newImmutableBoardBuilder.add(rowBuilder.build());
		}
		ImmutableList<ImmutableList<Square>> newImmutableBoard = newImmutableBoardBuilder.build();
		return newImmutableBoard;
	}
	
	/**
	 * Method used to return {@code List<Operation>} operations
	 * @param board input {@code List<ImmutableList<String>>} board
	 * @param jumps input {@code List<ImmutableList<Integer>>} jumps
	 * @param playerId input playerId which is used to identify the turn.
	 * @return {@code List<Operation>} operations.
	 */
	public static List<Operation> getMoves(List<ImmutableList<String>> board, 
			List<ImmutableList<Integer>> jumps, int playerId){
		check(board != null && jumps != null, "Board and jumps can not be null!");
		return ImmutableList.<Operation>of(
				new SetTurn(playerId),
				new Set(AbaloneConstants.BOARD, board),
				new Set(AbaloneConstants.JUMP, jumps)
		);
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
