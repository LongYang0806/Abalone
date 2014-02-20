package com.longyang.abalone.impl;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
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
	private final List<ImmutableList<Integer>> jump;
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
			List<ImmutableList<Square>> board, List<ImmutableList<Integer>> jump,
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
	 * 3. It moves 2 or 3 pieces which do not on the same horizontal or diagonal line.
	 * 4. It moves less pieces to against with more pieces: 2 against 3, or 2 against 2.
	 * 
	 * @param currentJump input current jump
	 * @return new board by applying current jump on last board
	 */
	public AbaloneState applyJumpOnBoard(List<ImmutableList<Integer>> currentJump){
		
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
	
	public List<ImmutableList<Integer>> getJump(){
		return jump;
	}
	
	public Optional<Boolean> getIsGameEnd(){
		return isGameEnd;
	}
}
