package com.longyang.abalone.impl;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.longyang.abalone.api.AbaloneConstants;
import com.longyang.abalone.api.Square;
import com.longyang.abalone.api.Turn;

/**
 * Class used to represent the state of the Abalone game state: TURN, BOARD, JUMP,
 * which should be mapped to field variables in this class: turn, board, jump.
 * In addition, adding one field isGameEnd to record whether a winner has be selected.
 * 
 * All field variables are immutable, so this class is thread safe.
 * @author Long Yang (ly603@nyu.edu)
 *
 */
public class AbaloneState {
	
	private final ImmutableList<Integer> playerIds;
	private final Turn turn;
	private final List<ImmutableList<Square>> board;
	
	/**
	 * The composition of jump: {{original_x, original_y, dest_x, dest_y, piece_color}, ...}
	 */
	private final List<ImmutableList<ImmutableList<Integer>>> jump;
	private Optional<Boolean> isGameEnd;
	
	/**
	 * Constructor to create a AbaloneState object, and the "isGameEnd" parameter is optional.
	 * @param turn Turn of play, WP or BP
	 * @param playerIds Player IDs of all players
	 * @param board The game board
	 * @param jump The last jump made
	 * @param isGameEnd This is a optional parameter, means whether one has won.
	 */
	public AbaloneState(Turn turn, ImmutableList<Integer> playerIds,
			List<ImmutableList<Square>> board, List<ImmutableList<ImmutableList<Integer>>> jump,
			Optional<Boolean> isGameEnd){
		this.turn = checkNotNull(turn);
		this.playerIds = checkNotNull(playerIds);
		this.board = checkNotNull(board);
		this.jump = checkNotNull(jump);
		this.isGameEnd = isGameEnd;
	}
	
	/**
	 * Method used to get the result board by applying current jump on last board.
	 * 
	 * In addition, this method also throw exceptions in the following situations:
	 * 1. The result moves lands in illegal squares.
	 * 2. It moves more than 4 pieces.
	 * 3. It moves less pieces against with more pieces: 2 against 3, or 2 against 2.
	 * 4. It moves 2 or 3 pieces which are not on the same horizontal or diagonal line.
	 * 5. It can not be moved because of no space.
	 * 
	 * @param lastJump input current jump
	 * @throws RuntimeException if the above errors happen.
	 * @return new board by applying current jump on last board
	 */
	public AbaloneState applyJumpOnBoard(List<ImmutableList<ImmutableList<Integer>>> lastJump){
		if(lastJump == null || lastJump.isEmpty()){
			return this;
		}
		
		List<ImmutableList<Integer>> whiteHandJump = new ArrayList<ImmutableList<Integer>>(lastJump.get(0));
		List<ImmutableList<Integer>> blackHandJump = new ArrayList<ImmutableList<Integer>>(lastJump.get(1));
		
		// 1. check whether white-hand jump or black-hand jump lands in illegal square.
		if(landInIllegalSquares(whiteHandJump) || landInIllegalSquares(blackHandJump)){
			throw new RuntimeException("Hacker found: pieces land in illegal squares "
					+ lastJump.toString());
		}
		
		// 2. move more than 4 pieces error.
		if(whiteHandJump.size() >= 4 || blackHandJump.size() >= 4){
			throw new RuntimeException("Hacker found: move more than 4 pieces, " + lastJump.toString());
		}
		// 3. It moves less pieces against with more pieces.
		if(turn == Turn.WP){
			if(whiteHandJump.size() <= blackHandJump.size()){
				throw new RuntimeException("Hacker found: " + whiteHandJump.size() + " white pieces against"
						+ blackHandJump.size() + " black pieces " + lastJump.toString());
			}
		}else if(turn == Turn.BP){
			if(blackHandJump.size() <= whiteHandJump.size()){
				throw new RuntimeException("Hacker found: " + blackHandJump.size() + " black pieces against"
						+ whiteHandJump.size() + " white pieces " + lastJump.toString());
			}
		}
		// 4. move 2 or 3 pieces which are not on the same horizontal or diagonal line.
		// 4.0 first, check when moving 1 pieces.
		if(turn == Turn.WP && whiteHandJump.size() == 1){
			AbaloneUtilities.check(
					(whiteHandJump.get(0).get(0) == whiteHandJump.get(0).get(2) && 
							Math.abs(whiteHandJump.get(0).get(1) - whiteHandJump.get(0).get(3)) == 2) ||
					(Math.abs(whiteHandJump.get(0).get(0) - whiteHandJump.get(0).get(2)) == 1 && 
							Math.abs(whiteHandJump.get(0).get(1) - whiteHandJump.get(3).get(2)) == 1)
			);
		}else if(turn == Turn.BP && blackHandJump.size() == 1){
			AbaloneUtilities.check(
					(blackHandJump.get(0).get(0) == blackHandJump.get(0).get(2) && 
							Math.abs(blackHandJump.get(0).get(1) - blackHandJump.get(0).get(3)) == 2) ||
					(Math.abs(blackHandJump.get(0).get(0) - blackHandJump.get(0).get(2)) == 1 && 
							Math.abs(blackHandJump.get(0).get(1) - blackHandJump.get(3).get(2)) == 1)
			);
		}
		// 4.1 sort both list based on the first two elements of the jump list.
		Collections.sort(whiteHandJump, AbaloneConstants.listComparator);
		Collections.sort(blackHandJump, AbaloneConstants.listComparator);
		// 4.2 determine directions: horizontal/diagonal
		check2And3Pieces(whiteHandJump);
		check2And3Pieces(blackHandJump);
		
		// 5. get the result new abaloneState and check "can not move".
		// because of the immutable, we have to create a new mutable list.
		List<ArrayList<Square>> newBoard = new ArrayList<ArrayList<Square>>();
		for(List<Square> row : board){
			ArrayList<Square> newRow = new ArrayList<Square>();
			for(Square square : row){
				newRow.add(square);
			}
			newBoard.add(newRow);
		}
		
		// 5.1. change the new board according to the jump.
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
		
		// 6. check whether anyone gets a win.
		boolean anyoneWin = getWin(whiteHandJump) || getWin(blackHandJump);
		if(anyoneWin){
			return new AbaloneState(turn.getOppositeTurn(), playerIds, newImmutableBoard, lastJump, 
					Optional.fromNullable(anyoneWin));
		}else{
			return new AbaloneState(turn.getOppositeTurn(), playerIds, newImmutableBoard, lastJump, null);
		}
		
	}
	
	/**
	 * Method used to check 2 or 3 pieces moved together, and check the validity of 
	 * the jump.
	 * In addition, it will check whether the move can be made, because sometimes your move can be
	 * stopped by other pieces.
	 * 
	 * @param jumps input jump
	 * @throws RuntimeException if input jump is invalid.
	 */
	public void check2And3Pieces(List<ImmutableList<Integer>> jumps){
		if(jumps == null || jumps.isEmpty()){
			// error input, ignore.
			return; 
		}
		
		if(jumps.size() == 2){
			// 1. Two single jumps.
			if(jumps.get(0).get(0) == jumps.get(1).get(0)){
				
				// 1.1. horizontal
				AbaloneUtilities.check(
						jumps.get(0).get(0) == jumps.get(0).get(2) && 
						jumps.get(1).get(0) == jumps.get(1).get(2));
				AbaloneUtilities.check(
						(jumps.get(0).get(2) == jumps.get(1).get(0) && jumps.get(0).get(3) == jumps.get(1).get(1)) ||
						(jumps.get(0).get(0) == jumps.get(1).get(2) && jumps.get(0).get(1) == jumps.get(1).get(3)));
			}else{
				// 1.2. diagonal
				AbaloneUtilities.check(
						Math.abs(jumps.get(0).get(0) - jumps.get(0).get(2)) == 1 &&
						Math.abs(jumps.get(0).get(1) - jumps.get(0).get(3)) == 1 &&
						Math.abs(jumps.get(1).get(1) - jumps.get(1).get(3)) == 1 &&
						Math.abs(jumps.get(1).get(1) - jumps.get(1).get(3)) == 1 &&
						Math.abs(jumps.get(0).get(0) - jumps.get(1).get(0)) == 1 &&
						Math.abs(jumps.get(0).get(1) - jumps.get(1).get(1)) == 1 &&
						Math.abs(jumps.get(0).get(2) - jumps.get(1).get(2)) == 1 &&
						Math.abs(jumps.get(0).get(3) - jumps.get(1).get(3)) == 1);
				AbaloneUtilities.check(
						(jumps.get(0).get(2) == jumps.get(1).get(0) && jumps.get(0).get(3) == jumps.get(1).get(1)) ||
						(jumps.get(0).get(0) == jumps.get(1).get(2) && jumps.get(0).get(1) == jumps.get(1).get(3)));
			}
		}else if(jumps.size() == 3){
			// 2. Three single jumps
			if(jumps.get(0).get(0) == jumps.get(1).get(0) && jumps.get(1).get(0) == jumps.get(2).get(0)){
				// 2.1. horizontal
				AbaloneUtilities.check(
						jumps.get(0).get(0) == jumps.get(0).get(2) &&
						jumps.get(0).get(2) == jumps.get(1).get(0) &&
						jumps.get(1).get(0) == jumps.get(1).get(2) &&
						jumps.get(1).get(2) == jumps.get(2).get(0) &&
						jumps.get(2).get(0) == jumps.get(2).get(2));
			}else{
				// 2.2. diagonal
				AbaloneUtilities.check(
						Math.abs(jumps.get(0).get(0) - jumps.get(0).get(2)) == 1 &&
						Math.abs(jumps.get(0).get(1) - jumps.get(0).get(3)) == 1 &&
						Math.abs(jumps.get(1).get(0) - jumps.get(1).get(2)) == 1 &&
						Math.abs(jumps.get(1).get(0) - jumps.get(1).get(2)) == 1 &&
						Math.abs(jumps.get(2).get(0) - jumps.get(2).get(2)) == 1 &&
						Math.abs(jumps.get(2).get(0) - jumps.get(2).get(2)) == 1);
				AbaloneUtilities.check(
						Math.abs(jumps.get(0).get(0) - jumps.get(1).get(0)) == 1 &&
						Math.abs(jumps.get(1).get(0) - jumps.get(2).get(0)) == 1 &&
						Math.abs(jumps.get(0).get(1) - jumps.get(1).get(1)) == 1 &&
						Math.abs(jumps.get(1).get(1) - jumps.get(2).get(1)) == 1 &&
						Math.abs(jumps.get(0).get(2) - jumps.get(1).get(2)) == 1 &&
						Math.abs(jumps.get(1).get(2) - jumps.get(2).get(2)) == 1 &&
						Math.abs(jumps.get(0).get(3) - jumps.get(1).get(3)) == 1 &&
						Math.abs(jumps.get(1).get(3) - jumps.get(2).get(3)) == 1);
			}
		}
	}
	
	/**
	 * Method used to determine whether jump starts from or jumps to an illegal square.
	 * @param jump input single jump
	 * @return true if jump starts from or jumps to an illegal square, false otherwise.
	 */
	public boolean landInIllegalSquares(List<ImmutableList<Integer>> jumps){
		if(jumps == null || jumps.isEmpty()){
			return false;
		}
		
		for(List<Integer> singleMove : jumps){
			// Create two points to represent original point and destination point.
			List<Integer> original = ImmutableList.<Integer>of(singleMove.get(0), singleMove.get(1));
			List<Integer> destination = ImmutableList.<Integer>of(singleMove.get(2), singleMove.get(3));
			if(AbaloneConstants.illegalSquares.contains(original) ||
				 AbaloneConstants.illegalSquares.contains(destination)){
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Method used to determine whether anyone gets a win.
	 * @param jump input single jump
	 * @return true if any points land into the score square, false otherwise.
	 */
	public boolean getWin(List<ImmutableList<Integer>> jump){
		if(jump == null || jump.isEmpty()){
			return false;
		}
		
		for(List<Integer> singleMove : jump){
			// Create a destination point.
			List<Integer> destination = ImmutableList.<Integer>of(singleMove.get(2), singleMove.get(3));
			if(AbaloneConstants.scoreSquares.contains(destination)){
				return true;
			}
		}
		
		return false;
	}
	
	@Override
	public boolean equals(Object other){
		if(other == null){
			return false;
		}
		if(other == this){
			return true;
		}
		if(!(other instanceof AbaloneState)){
			return false;
		}
		AbaloneState otherState = (AbaloneState) other;
		
		return 
				Objects.equals(turn, otherState.getTurn()) &&
				Objects.equals(playerIds, otherState.getPlayerIds()) &&
				Objects.equals(isGameEnd, otherState.getIsGameEnd()) &&
				Objects.equals(board, otherState.getBoard()) &&
				Objects.equals(jump, otherState.getJump());
	}
	
	public Turn getTurn(){
		return turn;
	}
	
	public List<Integer> getPlayerIds(){
		return playerIds;
	}
	
	public List<ImmutableList<Square>> getBoard(){
		return board;
	}
	
	public List<ImmutableList<ImmutableList<Integer>>> getJump(){
		return jump;
	}
	
	public Optional<Boolean> getIsGameEnd(){
		return isGameEnd;
	}
}
