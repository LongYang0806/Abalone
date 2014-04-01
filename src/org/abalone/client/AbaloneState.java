package org.abalone.client;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.abalone.client.AbaloneConstants.BOARD;
import static org.abalone.client.AbaloneConstants.JUMP;
import static org.abalone.client.AbaloneConstants.WTurn;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

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
	
	private final List<String> playerIds;
	//Turn is used to indicate who should player next time, do not consider Viewer.
	private final String turn; 
	private final List<ArrayList<String>> board;
	private final List<ArrayList<Integer>> jump;
	private final Optional<Boolean> isGameEnd;
	
	public AbaloneState(String turn, List<String> playerIds, List<ArrayList<String>> board, 
			List<ArrayList<Integer>> jump, Optional<Boolean> isGameEnd) {
		this.turn = checkNotNull(turn);
		this.playerIds = checkNotNull(playerIds);
		this.board = checkNotNull(board);
		this.jump = checkNotNull(jump);
		this.isGameEnd = isGameEnd;
	}
	
	/**
	 * Assume that the input {@code jumps} are sorted based on their move direction
	 * @param jumps the format is as following:
	 * {
	 * 	{startX, startY, endX, endY, 0/1 (0 for white, 1 for black)}, 
	 * 	{startX, startY, endX, endY, 0/1 (0 for white, 1 for black)}
	 * }
	 * @return a new result {@link AbaloneState}
	 */
	@SuppressWarnings("unchecked")
	public AbaloneState applyJumpOnBoard(List<ArrayList<Integer>> jumps) {
		if(jumps == null || jumps.isEmpty()){
			return this;
		}
		
		System.out.println("State Jumps");
		for(int i = 0; i < jumps.size(); i++){
			for(int j = 0; j < jumps.get(i).size(); j++) {
				System.out.print(jumps.get(i).get(j) + " ");
			}
			System.out.println();
		}
		
		List<ArrayList<String>> newBoard = new ArrayList<ArrayList<String>>();
		
		for(int i = 0; i < board.size(); i++) {
			newBoard.add((ArrayList<String>)board.get(i).clone());
		}
		// variable used to stand for whether the current player wins the game.
		boolean youWin = false;
		
		for(List<Integer> jump : jumps) {
			int startX = jump.get(0);
			int startY = jump.get(1);
			int endX = jump.get(2);
			int endY = jump.get(3);
			int pieceColor = jump.get(4);
			
			if(AbaloneConstants.scoreSquares.contains(ImmutableList.<Integer>of(endX, endY))){
				youWin = true;
			}
			
			if( AbaloneConstants.illegalSquares.contains(ImmutableList.<Integer>of(startX, startY)) || 
					AbaloneConstants.illegalSquares.contains(ImmutableList.<Integer>of(endX, endY)) ) {
				throw new RuntimeException("Jump should not start or end in illegal scores");
			}
			
			if(pieceColor == 0) {
				newBoard.get(endX).set(endY, AbaloneConstants.W);
				newBoard.get(startX).set(startY, AbaloneConstants.E);
			} else {
				newBoard.get(endX).set(endY, AbaloneConstants.B);
				newBoard.get(startX).set(startY, AbaloneConstants.E);
			}
		}
		return new AbaloneState(turn, playerIds, newBoard, jumps, isGameEnd);
	}
	
	/**
	 * Method used to get opponent's turn: 
	 * {@link AbaloneConstants#bTurn} => {@link AbaloneConstants#wTurn}
	 * {@link AbaloneConstants#wTurn} => {@link AbaloneConstants#bTurn}
	 * 
	 * @param turn
	 * @return
	 */
	String getOpponentTurn(String turn) {
		return turn == AbaloneConstants.WTurn ? AbaloneConstants.BTurn : AbaloneConstants.WTurn;
	}
	
	/**
	 * Method used to convert gameApiState, which is transmitted from JSON format messages, to
	 * the real {@link AbaloneState} object.
	 * 
	 * @param gameApiState {"Board" : {@code List<ArrayList<String>>}, 
	 * 											"Jump" : {@code List<ArrayList<Integer>>}}
	 * @param turn
	 * @param playerIds
	 * @return
	 */
	public static AbaloneState gameApiState2AbaloneState(Map<String, Object> gameApiState, 
			String turn, List<String> playerIds) {
		if(gameApiState == null || gameApiState.isEmpty()) {
			return getEmptyAbaloneState();
		}
		
		@SuppressWarnings("unchecked")
		List<ArrayList<String>> board = (List<ArrayList<String>>)gameApiState.get(BOARD);
		@SuppressWarnings("unchecked")
		List<ArrayList<Integer>> jump = (List<ArrayList<Integer>>)gameApiState.get(JUMP);
		return new AbaloneState(turn, playerIds, board, jump, null);
	}
	
	public static AbaloneState getEmptyAbaloneState() {
		return new AbaloneState(WTurn, Lists.<String>newArrayList(), Lists.<ArrayList<String>>newArrayList(), 
				Lists.<ArrayList<Integer>>newArrayList(), null);
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
	
	public String getTurn() {
		return turn;
	}
	
	public List<String> getPlayerIds() {
		return playerIds;
	}
	
	public Optional<Boolean> getIsGameEnd(){
		return isGameEnd;
	}
	
	public List<ArrayList<String>> getBoard() {
		return board;
	}
	
	public List<ArrayList<Integer>> getJump(){
		return jump;
	}
}
