package com.longyang.abalone.impl;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.longyang.abalone.api.AbaloneMessage;
import com.longyang.abalone.api.GameApi.Container;
import com.longyang.abalone.api.GameApi.Operation;
import com.longyang.abalone.api.GameApi.SetTurn;
import com.longyang.abalone.api.GameApi.UpdateUI;
import com.longyang.abalone.api.Jump;
import com.longyang.abalone.api.Square;
import com.longyang.abalone.api.Turn;
import com.longyang.abalone.api.View;

/**
 * We use MVP Pattern:
 * You can find Model at {@link AbaloneState},
 * You can find View at {@link View},
 * You can find Presenter here, and it's used to control view and model.
 *
 * @author Long Yang (ly603@nyu.edu)
 *
 */
public class AbalonePresenter {
	
	enum Direction{
		UPPER_LEFT_DIAGONAL, UPPER_RIGHT_DIAGONAL, 
		LOWER_LEFT_DIAGONAL, LOWER_RIGHT_DIAGONAL,
		LEFT_HORIZONTAL, RIGHT_HORIZONTAL
	}
	
	private final AbaloneLogic abaloneLogic = new AbaloneLogic();
	private final View view;
	private final Container container;
	/* A viewer does not have turn */
	private Optional<Turn> myTurn;
	private AbaloneState abaloneState;
	private List<ImmutableList<Square>> board;
	private List<ImmutableList<Square>> newBoard;
	private List<Operation> moves;
	private AbaloneMessage abaloneMessage;
	private List<Jump> jumps;
	private int x;
	private int y;
	
	/**
	 * Constructor used to create an AbalonePresenter object with the input 
	 * {@code view} and {@code container} to form the MVP pattern.
	 * @param view input viewer
	 * @param container input container.
	 */
	public AbalonePresenter(View view, Container container){
		this.view = view;
		this.container = container;
		view.setPresenter(this);
	}
	
	/**
	 * Method used to update the presenter and view with the information from {@code updateUI}
	 * @param updateUI input updateUI to pass necessary information.
	 */
	public void updateUI(UpdateUI updateUI){
		List<Integer> playerIds = updateUI.getPlayerIds();
		int yourPlayerIndex = updateUI.getYourPlayerIndex();
		myTurn = 
				yourPlayerIndex == 0 ? Optional.<Turn>of(Turn.WP) : 
				yourPlayerIndex == 1 ? Optional.<Turn>of(Turn.BP) : 
				Optional.<Turn>absent(); // for the viewer.
		/*
		 * The reason for choose {@link LinkedList} other than {@link ArrayList}:
		 * player can cancel the previous jumps, so you need a data structure which 
		 * supports the random delete in random position of the list.
		 */
		jumps = new LinkedList<Jump>();
		if(updateUI.getState().isEmpty()){
			// WP should finish the initialization.
			if(myTurn.isPresent() && myTurn.get().isWhite()){
				container.sendMakeMove(abaloneLogic.getInitialMove(playerIds));
			}
			return;
		}
		Turn currentTurn = null;
		for(Operation operation : updateUI.getLastMove()){
			if(operation instanceof SetTurn){
				currentTurn = Turn.values()[playerIds.indexOf(((SetTurn)operation).getPlayerId())];
			}
		}
		abaloneState = AbaloneUtilities.gameApiStateToAbaloneState(updateUI.getState(), 
				currentTurn, playerIds);
		if(updateUI.isViewer()){
			view.setPlayerState(abaloneState.getBoard(), AbaloneMessage.UNDERGOING);
			return;
		}
		if(updateUI.isAiPlayer()){
			//TODO: to be finished in HW4
			//container.sentMakeMove();
			return;
		}
		// So now, it must be a player.
		Turn myturn = myTurn.get();
		view.setPlayerState(abaloneState.getBoard(), AbaloneMessage.UNDERGOING);
		// this is my turn.
		if(myturn == currentTurn){
			if(jumps.size() == 0){
				// if the player do not make any jump, he has to make jumps
				view.nextPieceJump(abaloneState.getBoard(), jumps, AbaloneMessage.UNDERGOING);
			}else{
				/*
				 * otherwise, we give him two alternatives:
				 * (1). Make another jump, even though the size of jumps is already 3, 
				 * he can still cancel previous jumps.
				 * (2). Finish this round, and this method will highlight the "finish this round" button.
				 */
				view.nextPieceJump(abaloneState.getBoard(), jumps, AbaloneMessage.UNDERGOING);
				view.finishMoves(abaloneState.getBoard(), jumps, AbaloneMessage.UNDERGOING);
			}
		}
		// if not my round, I just watch, nothing needed to be done!
	}
	
	/**
	 * Method to be called by {@link View} for make another piece jump. 
	 * And it should also call {@link View#nextPieceJump()} to make the next move.
	 * 
	 * @param orig_x input original x coordinate.
	 * @param orig_y input original y coordinate.
	 * @param dest_x input destination x coordinate.
	 * @param dest_y input destination y coordinate.
	 * @throws IllegalArgumentException if input coordinates break the data validation
	 * @throws RuntimeException if the player want to make another jump when he has already 
	 * make three jumps.
	 */
	public void pieceJumped(int orig_x, int orig_y, int dest_x, int dest_y){
		Jump jump = new Jump(orig_x, orig_y, dest_x, dest_y);
		if(jumps.contains(jump)){
			jumps.remove(jump);
		}else{
			if(jumps.size() >= 3){
				throw new RuntimeException("You can move at most three of your pieces");
			}else{
				jumps.add(jump);
			}
		}
	}
	
	/**
	 * Method called by {@link View}, which is used for player to tell back end 
	 * that I have finished my jumps, and you should decide which oppoent's 
	 * pieces should be pushed.
	 * 
	 * In addition, if this player has won the game, this method should also notify {@link View}
	 * 
	 * And it should also call {@code container} to send the move this time, containing:
	 * {@link Turn}, {@link AbaloneState}, and {@link Jump} but all in GameApi format.
	 * 
	 * And it should also call {@link View#finishMoves()} to finish
	 * the whole circle.
	 * 
	 * @throws RuntimeException if the size of {@code jumps} is 0 when he want to finish his round.
	 * @throws RuntimeException if the size of {@code jumps} is larger than 3.
	 */
	public void finishedJumpingPieces(){
		if(jumps.size() == 0){
			throw new RuntimeException("You have to make the jumps before finishing this round.");
		}
		Collections.sort(jumps);
		if(jumps.size() == 1){
			// only one piece's jump can not change the game message ({@link AbaloneMessage})
		}else if(jumps.size() <= 3){
			board = abaloneState.getBoard();
			/*
			 * Because whether the jumps are legal has been checked in the view or front end code, 
			 * we just need to figure out the "pushed pieces", and whether we have enter into a 
			 * {@link AbaloneM essage#GAMEOVER} state.
			 */
			switch(getJumpDirection(jumps)){
				case LEFT_HORIZONTAL:
					x = jumps.get(0).getDestinationX();
					y = jumps.get(0).getDestinationY();
					while(board.get(x).get(y - 1) != Square.S && board.get(x).get(y - 2) != Square.E){
						jumps.add(new Jump(x, y, x, y - 2));
						y = y - 2;
					}
					if(board.get(x).get(y - 1) == Square.S){
						abaloneMessage = AbaloneMessage.GAMEOVER; 
						jumps.add(new Jump(x, y, x, y - 1));
					}else{
						abaloneMessage = AbaloneMessage.UNDERGOING;
						jumps.add(new Jump(x, y, x, y - 2));
					}
					break;
				case RIGHT_HORIZONTAL:
					x = jumps.get(jumps.size() - 1).getDestinationX();
					y = jumps.get(jumps.size() - 1).getDestinationY();
					while(board.get(x).get(y + 1) != Square.S && board.get(x).get(y + 2) != Square.E){
						jumps.add(new Jump(x, y, x, y + 2));
						y = y + 2;
					}
					if(board.get(x).get(y + 1) == Square.S){
						abaloneMessage = AbaloneMessage.GAMEOVER;
						jumps.add(new Jump(x, y, x, y + 1));
					}
					if(board.get(x).get(y + 2) == Square.E){
						abaloneMessage = AbaloneMessage.UNDERGOING;
						jumps.add(new Jump(x, y, x, y + 2));
					}
					break;
				case UPPER_LEFT_DIAGONAL:
					x = jumps.get(0).getDestinationX();
					y = jumps.get(0).getDestinationY();
					while(board.get(x - 1).get(y - 1) != Square.S && board.get(x - 1).get(y - 1) != Square.E){
						jumps.add(new Jump(x, y, x - 1, y - 1));
						x = x - 1;
						y = y - 1;
					}
					if(board.get(x - 1).get(y - 1) == Square.S){
						abaloneMessage = AbaloneMessage.GAMEOVER;
					}
					if(board.get(x - 1).get(y - 1) == Square.E){
						abaloneMessage = AbaloneMessage.UNDERGOING;
					}
					jumps.add(new Jump(x, y, x - 1, y - 1));
					break;
				case UPPER_RIGHT_DIAGONAL:
					x = jumps.get(0).getDestinationX();
					y = jumps.get(0).getDestinationY();
					while(board.get(x - 1).get(y + 1) != Square.S && board.get(x - 1).get(y + 1) != Square.E){
						jumps.add(new Jump(x, y, x - 1, y + 1));
						x = x - 1;
						y = y + 1;
					}
					if(board.get(x - 1).get(y + 1) == Square.S){
						abaloneMessage = AbaloneMessage.GAMEOVER;
					}
					if(board.get(x - 1).get(y + 1) == Square.E){
						abaloneMessage = AbaloneMessage.UNDERGOING;
					}
					jumps.add(new Jump(x, y, x - 1, y + 1));
					break;
				case LOWER_LEFT_DIAGONAL:
					x = jumps.get(jumps.size() - 1).getDestinationX();
					y = jumps.get(jumps.size() - 1).getDestinationY();
					while(board.get(x + 1).get(y - 1) != Square.S && board.get(x + 1).get(y - 1) != Square.E){
						jumps.add(new Jump(x, y, x + 1, y - 1));
						x = x + 1;
						y = y - 1;
					}
					if(board.get(x + 1).get(y - 1) == Square.S){
						abaloneMessage = AbaloneMessage.GAMEOVER;
					}
					if(board.get(x + 1).get(y - 1) == Square.E){
						abaloneMessage = AbaloneMessage.UNDERGOING;
					}
					jumps.add(new Jump(x, y, x + 1, y - 1));
					break;
				case LOWER_RIGHT_DIAGONAL:
					x = jumps.get(jumps.size() - 1).getDestinationX();
					y = jumps.get(jumps.size() - 1).getDestinationY();
					while(board.get(x + 1).get(y + 1) != Square.S && board.get(x + 1).get(y + 1) != Square.E){
						jumps.add(new Jump(x, y, x + 1, y + 1));
						x = x + 1;
						y = y + 1;
					}
					if(board.get(x + 1).get(y + 1) == Square.S){
						abaloneMessage = AbaloneMessage.GAMEOVER;
					}
					if(board.get(x + 1).get(y + 1) == Square.E){
						abaloneMessage = AbaloneMessage.UNDERGOING;
					}
					jumps.add(new Jump(x, y, x + 1, y + 1));
					break;
			}
		}else{
			throw new RuntimeException("You can only place three piece jumps!");
		}
		newBoard = AbaloneUtilities.boardAppliedJumps(board, jumps);
		view.finishMoves(board, jumps, abaloneMessage);
		moves = AbaloneUtilities.getMoves(
				AbaloneUtilities.squareBoardToStringBoard(board),
				Jump.listJumpToListInteger(jumps), 
				abaloneState.getPlayerIds().get(abaloneState.getTurn().getOppositeTurn().ordinal()));
		container.sendMakeMove(moves);
	}
	
	/**
	 * Helper method used to return the direction the input {@code jump}
	 * @param jump input jump, and the size of jump should be inside [2, 3] inclusively.
	 * @return one of the six directions.
	 */
	private Direction getJumpDirection(List<Jump> jump){
		AbaloneUtilities.check(jump.size() >= 2 && jump.size() <= 3, 
				"Size of the input jump list should be [2, 3] inclusively");
		if(jump.get(0).getOriginalX() == jump.get(0).getDestinationX()){
			if(jump.get(0).getOriginalY() < jump.get(0).getDestinationY()){
				return Direction.RIGHT_HORIZONTAL;
			}else{
				return Direction.LEFT_HORIZONTAL;
			}
		}else if(jump.get(0).getOriginalX() < jump.get(0).getDestinationX()){
			if(jump.get(0).getOriginalY() < jump.get(0).getDestinationY()){
				return Direction.UPPER_RIGHT_DIAGONAL;
			}else{
				return Direction.UPPER_LEFT_DIAGONAL;
			}
		}else{
			if(jump.get(0).getOriginalY() < jump.get(0).getDestinationY()){
				return Direction.LOWER_RIGHT_DIAGONAL;
			}else{
				return Direction.LOWER_LEFT_DIAGONAL;
			}
		}
	}

}
