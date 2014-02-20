package com.longyang.abalone.impl;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collections;
import java.util.List;

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
	 * 
	 * 
	 * @param lastJump input current jump
	 * @throws RuntimeException if the above errors happen.
	 * @return new board by applying current jump on last board
	 */
	public AbaloneState applyJumpOnBoard(List<ImmutableList<ImmutableList<Integer>>> lastJump){
		if(lastJump == null || lastJump.isEmpty()){
			throw new RuntimeException("Hacker found " + lastJump.toString());
		}
		
		List<ImmutableList<Integer>> whiteHandJump = lastJump.get(0);
		List<ImmutableList<Integer>> blackHandJump = lastJump.get(1);
		
		// 1. check whether white-hand jump or black-hand jump lands in illegal square.
		if(landInIllegalSquares(whiteHandJump) || landInIllegalSquares(blackHandJump)){
			throw new RuntimeException("Hacker found: pieces land in illegal squares "
					+ lastJump.toString());
		}
		
		// 2. move more than 4 pieces error.
		if(whiteHandJump.size() >= 4 || blackHandJump.size() > 4){
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
		
		// 5. get the result new abaloneState
		// 5.1. get new board
		
		
		return null;
	}
	
	/**
	 * Method used to check 2 or 3 pieces moved together, and check the validity of 
	 * the jump.
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
				AbaloneUtilities.check(
						Math.abs(jumps.get(1).get(3) - jumps.get(0).get(1)) == 4 ||
						Math.abs(jumps.get(0).get(3) - jumps.get(1).get(1)) == 4);
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
				AbaloneUtilities.check(
						Math.abs(jumps.get(0).get(1) - jumps.get(0).get(3)) == 2 &&
						Math.abs(jumps.get(1).get(1) - jumps.get(1).get(3)) == 2 &&
						Math.abs(jumps.get(2).get(1) - jumps.get(2).get(3)) == 2 &&
						Math.abs(jumps.get(0).get(1) - jumps.get(1).get(1)) == 2 &&
						Math.abs(jumps.get(1).get(1) - jumps.get(2).get(1)) == 2 &&
						Math.abs(jumps.get(0).get(3) - jumps.get(1).get(3)) == 2 &&
						Math.abs(jumps.get(1).get(3) - jumps.get(2).get(3)) == 2);
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
	public boolean landInIllegalSquares(List<ImmutableList<Integer>> jump){
		if(jump == null || jump.isEmpty()){
			return false;
		}
		
		for(List<Integer> singleMove : jump){
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
