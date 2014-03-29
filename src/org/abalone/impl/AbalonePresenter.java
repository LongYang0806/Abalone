package org.abalone.impl;

import java.util.LinkedList;
import java.util.List;

import org.abalone.api.AbaloneConstants;
import org.abalone.api.AbaloneMessage;
import org.abalone.api.Jump;
import org.abalone.api.Position;
import org.abalone.api.Square;
import org.abalone.api.Turn;
import org.abalone.api.View;
import org.game_api.GameApi.Container;
import org.game_api.GameApi.Operation;
import org.game_api.GameApi.SetTurn;
import org.game_api.GameApi.UpdateUI;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

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
	private Jump lastJump;
	private int heldX;
	private int heldY;
	
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
		List<String> playerIds = updateUI.getPlayerIds();
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
			view.setPlayerState(abaloneState.getBoard(), 
					boardToBooleanMatrix(abaloneState.getBoard(), abaloneState.getTurn()), 
					AbaloneMessage.UNDERGOING);
			return;
		}
		if(updateUI.isAiPlayer()){
			//TODO: to be finished in HW4
			//container.sentMakeMove();
			return;
		}
		// So now, it must be a player.
		Turn myturn = myTurn.get();
		view.setPlayerState(abaloneState.getBoard(),
				boardToBooleanMatrix(abaloneState.getBoard(), abaloneState.getTurn()), 
				AbaloneMessage.UNDERGOING);
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
		Jump reversedJump = new Jump(dest_x, dest_y, orig_x, orig_y);
		if(jumps.contains(reversedJump)){
			jumps.remove(reversedJump);
		}else{
			if(jumps.size() >= 3){
				throw new RuntimeException("You can move at most three of your pieces");
			}else{
				jumps.add(jump);
			}
		}
		List<ImmutableList<Square>> appliedBoard = 
				AbaloneUtilities.boardAppliedJumps(abaloneState.getBoard(), Lists.<Jump>newArrayList(jumps));
		view.nextPieceJump(appliedBoard, jumps, AbaloneMessage.UNDERGOING);
	}
	
	public void pieceHeld(int x, int y){
		AbaloneUtilities.check(x >= 0 && x < AbaloneConstants.boardRowNum &&
				y >= 0 && y < AbaloneConstants.boardColumnNum, "Data invalid!");
		heldX = x;
		heldY = y;
		List<Position> possibleSquaresToPlace = getPossibleSquaresToPlacePiece(x, y);
		List<ImmutableList<Square>> appliedBoard = 
				AbaloneUtilities.boardAppliedJumps(board, Lists.newArrayList(jumps));
		view.toPlacePiece(appliedBoard, positionListToBooleanArray(possibleSquaresToPlace), 
				!jumps.isEmpty(), abaloneState.getTurn(), AbaloneMessage.UNDERGOING);
	}
	
	public void piecePlaced(int x, int y){
		AbaloneUtilities.check(x >= 0 && x < AbaloneConstants.boardRowNum &&
				y >= 0 && y < AbaloneConstants.boardColumnNum, "Data invalid!");
		
		List<ImmutableList<Square>> appliedBoard;
		boolean[][] enableSquare;
		board = AbaloneUtilities.boardAppliedJumps(abaloneState.getBoard(), jumps);
		if(heldX == x && heldY == y){
			// cancel the previous piece holding
			enableSquare = getEnableSquare(abaloneState.getBoard(), abaloneState.getTurn());
			appliedBoard = AbaloneUtilities.boardAppliedJumps(abaloneState.getBoard(), Lists.newArrayList(jumps));
			view.toHoldPiece(appliedBoard, enableSquare, !jumps.isEmpty(), 
					abaloneState.getTurn(), AbaloneMessage.UNDERGOING);
		}else{
			Jump reverseJump = new Jump(x, y, heldX, heldY);
			if(jumps.contains(reverseJump)){
				// cancel all the jumps after that.
				jumps = jumps.subList(0, jumps.indexOf(reverseJump));
				if(jumps.isEmpty()){
					lastJump = null;
				}else{
					lastJump = jumps.get(jumps.size() - 1);
				}
				enableSquare = getEnableSquare(abaloneState.getBoard(), abaloneState.getTurn());
				appliedBoard = AbaloneUtilities.boardAppliedJumps(abaloneState.getBoard(), Lists.newArrayList(jumps));
				view.toHoldPiece(appliedBoard, enableSquare, !jumps.isEmpty(), 
						abaloneState.getTurn(), AbaloneMessage.UNDERGOING);
			}else{
				Jump jump = new Jump(heldX, heldY, x, y);
				lastJump = new Jump(heldX, heldY, x, y);
				if(board.get(x).get(y) == Square.E){
					jumps.add(jump);
					enableSquare = getEnableSquare(abaloneState.getBoard(), abaloneState.getTurn());
					appliedBoard = AbaloneUtilities.boardAppliedJumps(abaloneState.getBoard(), 
							Lists.newArrayList(jumps));
					view.toHoldPiece(appliedBoard, enableSquare, !jumps.isEmpty(), 
							abaloneState.getTurn(), AbaloneMessage.UNDERGOING);
				}else{
					// this can make, we can make sure that this is right, because it has been varified by 
					// place square selecting phase.
					Direction direction = getJumpDirection(Lists.newArrayList(jump));
					switch(direction){
						case LEFT_HORIZONTAL:
							while(y >= 0 && 
										(board.get(x).get(y) == Square.W || board.get(x).get(y) == Square.B)){
								jumps.add(new Jump(x, y + 2, x, y));
								y = y - 2;
							}
							if(y >= 0 && board.get(x).get(y) == Square.E){
								jumps.add(new Jump(x, y + 2, x, y));
								abaloneMessage = AbaloneMessage.UNDERGOING;
							}
							if(y < 0 || board.get(x).get(y) == Square.S){
								jumps.add(new Jump(x, y + 2, x, y + 1));
								abaloneMessage = AbaloneMessage.GAMEOVER;
							}
							break;
						case RIGHT_HORIZONTAL:
							while(y < AbaloneConstants.boardColumnNum && 
									(board.get(x).get(y) == Square.W || board.get(x).get(y) == Square.B)){
								jumps.add(new Jump(x, y - 2, x, y));
								y = y + 2;
							}
							if(y < AbaloneConstants.boardColumnNum &&  board.get(x).get(y) == Square.E){
								jumps.add(new Jump(x, y - 2, x, y));
								abaloneMessage = AbaloneMessage.UNDERGOING;
							}
							if(y >= AbaloneConstants.boardColumnNum || board.get(x).get(y) == Square.S){
								jumps.add(new Jump(x, y - 2, x, y - 1));
								abaloneMessage = AbaloneMessage.GAMEOVER;
							}
							break;
						case UPPER_LEFT_DIAGONAL:
							while(x >= 0 && y >= 0 && 
							(board.get(x).get(y) == Square.W || board.get(x).get(y) == Square.B)){
								jumps.add(new Jump(x + 1, y + 1, x, y));
								x = x - 1;
								y = y - 1;
							}
							if(x >= 0 && y >= 0 && board.get(x).get(y) == Square.E){
								jumps.add(new Jump(x + 1, y + 1, x, y));
								abaloneMessage = AbaloneMessage.UNDERGOING;
							}
							if(board.get(x).get(y) == Square.S){
								jumps.add(new Jump(x + 1, y + 1, x, y));
								abaloneMessage = AbaloneMessage.GAMEOVER;
							}
							break;
						case UPPER_RIGHT_DIAGONAL:
							while(x >= 0 && y < AbaloneConstants.boardColumnNum && 
							(board.get(x).get(y) == Square.W || board.get(x).get(y) == Square.B)){
								jumps.add(new Jump(x + 1, y - 1, x, y));
								x = x - 1;
								y = y + 1;
							}
							if(x >= 0 && y < AbaloneConstants.boardColumnNum && board.get(x).get(y) == Square.E){
								jumps.add(new Jump(x + 1, y - 1, x, y));
								abaloneMessage = AbaloneMessage.UNDERGOING;
							}
							if(board.get(x).get(y) == Square.S){
								jumps.add(new Jump(x + 1, y - 1, x, y));
								abaloneMessage = AbaloneMessage.GAMEOVER;
							}
							break;
						case LOWER_LEFT_DIAGONAL:
							while(x < AbaloneConstants.boardRowNum && y >= 0 && 
									(board.get(x).get(y) == Square.W || board.get(x).get(y) == Square.B)){
								jumps.add(new Jump(x - 1, y + 1, x, y));
								x = x + 1;
								y = y - 1;
							}
							if(x < AbaloneConstants.boardRowNum && y >= 0 && board.get(x).get(y) == Square.E){
								jumps.add(new Jump(x - 1, y + 1, x, y));
								abaloneMessage = AbaloneMessage.UNDERGOING;
							}
							if(board.get(x).get(y) == Square.S){
								jumps.add(new Jump(x - 1, y + 1, x, y));
								abaloneMessage = AbaloneMessage.GAMEOVER;
							}
							break;
						case LOWER_RIGHT_DIAGONAL:
							while(x < AbaloneConstants.boardRowNum && y < AbaloneConstants.boardColumnNum && 
									(board.get(x).get(y) == Square.W || board.get(x).get(y) == Square.B)){
								jumps.add(new Jump(x - 1, y - 1, x, y));
								x = x + 1;
								y = y + 1;
							}
							if(x < AbaloneConstants.boardRowNum && y < AbaloneConstants.boardColumnNum && 
									board.get(x).get(y) == Square.E){
								jumps.add(new Jump(x - 1, y - 1, x, y));
								abaloneMessage = AbaloneMessage.UNDERGOING;
							}
							if(board.get(x).get(y) == Square.S){
								jumps.add(new Jump(x - 1, y - 1, x, y));
								abaloneMessage = AbaloneMessage.GAMEOVER;
							}
							break;
						default:
							break;
					}
					appliedBoard = AbaloneUtilities.boardAppliedJumps(abaloneState.getBoard(), Lists.newArrayList(jumps));
					enableSquare = getEnableSquare(abaloneState.getBoard(), abaloneState.getTurn());
					view.toHoldPiece(appliedBoard, enableSquare, !jumps.isEmpty(),
							abaloneState.getTurn(), abaloneMessage);
				}
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
		newBoard = AbaloneUtilities.boardAppliedJumps(abaloneState.getBoard(), Lists.newArrayList(jumps));
		boolean isGameOver = abaloneMessage == AbaloneMessage.GAMEOVER ? true : false;
		moves = AbaloneUtilities.getMoves(
				AbaloneUtilities.squareBoardToStringBoard(newBoard),
				Jump.listJumpToListInteger(jumps), 
				abaloneState.getPlayerIds().get(abaloneState.getTurn().getOppositeTurn().ordinal()),
				isGameOver, abaloneState.getPlayerIds().get(abaloneState.getTurn().ordinal()));
		container.sendMakeMove(moves);
		jumps.clear();
		lastJump = null;
	}
	
	/**
	 * Helper method used to return the direction the input {@code jump}
	 * @param jump input jump.
	 * @return one of the six directions.
	 */
	private Direction getJumpDirection(List<Jump> jumpList) {
		if(jumpList.get(0).getOriginalX() == jumpList.get(0).getDestinationX()){
			if(jumpList.get(0).getOriginalY() < jumpList.get(0).getDestinationY()){
				return Direction.RIGHT_HORIZONTAL;
			}else{
				return Direction.LEFT_HORIZONTAL;
			}
		}else if(jumpList.get(0).getOriginalX() > jumpList.get(0).getDestinationX()){
			if(jumpList.get(0).getOriginalY() < jumpList.get(0).getDestinationY()){
				return Direction.UPPER_RIGHT_DIAGONAL;
			}else{
				return Direction.UPPER_LEFT_DIAGONAL;
			}
		}else{
			if(jumpList.get(0).getOriginalY() < jumpList.get(0).getDestinationY()){
				return Direction.LOWER_RIGHT_DIAGONAL;
			}else{
				return Direction.LOWER_LEFT_DIAGONAL;
			}
		}
	}
	
	public List<Jump> getJumps(){
		return jumps;
	}

	/**
	 * Method used to get the possible placed positions for a being-held piece
	 * @param pieceHeldPosition the start position for the being-held piece.
	 * @return a {@code List<Position>} list of possible placed positions for this being-held piece.
	 */
	public List<Position> getPossibleSquaresToPlacePiece(int startX, int startY){
		board = AbaloneUtilities.boardAppliedJumps(abaloneState.getBoard(), Lists.newArrayList(jumps));
		int x, y, numOfB = 0, numOfW = 0;
		Square pieceColor = board.get(startX).get(startY);
		List<Position> possibleSquaresToPlacePiece = Lists.newArrayList();
		// add itself into the possibilities.
		possibleSquaresToPlacePiece.add(new Position(startX, startY));
		
		if(jumps == null || jumps.isEmpty()) {
			// no previous jumps.
			// Six possible directions, left, right, upper-left, upper-right, lower-left, lower-right.
			// left:
			x = startX;
			y = startY;
			numOfB = 0;
			numOfW = 0;
			if(pieceColor == Square.B) {
				while(y >= 0 && board.get(x).get(y) == Square.B){
					numOfB++;
					y = y - 2;
				}
				if(y >= 0 && board.get(x).get(y) == Square.E){
					possibleSquaresToPlacePiece.add(new Position(startX, startY - 2));
				} else if(y >= 0 && board.get(x).get(y) == Square.W){
					while(y >= 0 && board.get(x).get(y) == Square.W) {
						numOfW++;
						y = y - 2;
					}
					if(numOfB > numOfW){
						possibleSquaresToPlacePiece.add(new Position(startX, startY - 2));
					}
				}
			} else {
				while(y >= 0 && board.get(x).get(y) == Square.W){
					numOfW++;
					y = y - 2;
				}
				if(y >= 0 && board.get(x).get(y) == Square.E){
					possibleSquaresToPlacePiece.add(new Position(startX, startY - 2));
				} else if(y >= 0 && board.get(x).get(y) == Square.B){
					while(y >= 0 && board.get(x).get(y) == Square.B) {
						numOfB++;
						y = y - 2;
					}
					if(numOfW > numOfB){
						possibleSquaresToPlacePiece.add(new Position(startX, startY - 2));
					}
				}
			}
			// right:
			x = startX;
			y = startY;
			numOfB = 0;
			numOfW = 0;
			if(pieceColor == Square.B) {
				while(y >= 0 && board.get(x).get(y) == Square.B){
					numOfB++;
					y = y + 2;
				}
				if(y < AbaloneConstants.boardColumnNum && board.get(x).get(y) == Square.E){
					possibleSquaresToPlacePiece.add(new Position(startX, startY + 2));
				} else if(y < AbaloneConstants.boardColumnNum && board.get(x).get(y) == Square.W){
					while(y < AbaloneConstants.boardColumnNum && board.get(x).get(y) == Square.W) {
						numOfW++;
						y = y + 2;
					}
					if(numOfB > numOfW){
						possibleSquaresToPlacePiece.add(new Position(startX, startY + 2));
					}
				}
			} else {
				while(y < AbaloneConstants.boardColumnNum && board.get(x).get(y) == Square.W){
					numOfW++;
					y = y + 2;
				}
				if(y < AbaloneConstants.boardColumnNum && board.get(x).get(y) == Square.E){
					possibleSquaresToPlacePiece.add(new Position(startX, startY + 2));
				} else if(y < AbaloneConstants.boardColumnNum && board.get(x).get(y) == Square.B){
					while(y < AbaloneConstants.boardColumnNum && board.get(x).get(y) == Square.B) {
						numOfB++;
						y = y + 2;
					}
					if(numOfW > numOfB){
						possibleSquaresToPlacePiece.add(new Position(startX, startY + 2));
					}
				}
			}
			// upper-left
			x = startX;
			y = startY;
			numOfB = 0;
			numOfW = 0;
			if(pieceColor == Square.B) {
				while(x >= 0 && y >= 0 && board.get(x).get(y) == Square.B){
					numOfB++;
					x = x - 1;
					y = y - 1;
				}
				if(x >= 0 && y >= 0 && board.get(x).get(y) == Square.E){
					possibleSquaresToPlacePiece.add(new Position(startX - 1, startY - 1));
				} else if(x >= 0 && y >= 0 && board.get(x).get(y) == Square.W){
					while(x >= 0 && y >= 0 && board.get(x).get(y) == Square.W) {
						numOfW++;
						x = x - 1;
						y = y - 1;
					}
					if(x >= 0 && y >= 0 && numOfB > numOfW){
						possibleSquaresToPlacePiece.add(new Position(startX - 1, startY - 1));
					}
				}
			} else {
				while(x >= 0 && y >= 0 && board.get(x).get(y) == Square.W){
					numOfW++;
					x = x - 1;
					y = y - 1;
				}
				if(x >= 0 && y >= 0 && board.get(x).get(y) == Square.E){
					possibleSquaresToPlacePiece.add(new Position(startX - 1, startY - 1));
				} else if(x >= 0 && y >= 0 && board.get(x).get(y) == Square.B){
					while(x >= 0 && y >= 0 && board.get(x).get(y) == Square.B) {
						numOfB++;
						x = x - 1;
						y = y - 1;
					}
					if(x >= 0 && y >= 0 && numOfW > numOfB){
						possibleSquaresToPlacePiece.add(new Position(startX - 1, startY - 1));
					}
				}
			}
			// upper-right
			x = startX;
			y = startY;
			numOfB = 0;
			numOfW = 0;
			if(pieceColor == Square.B) {
				while(x >= 0 && y < AbaloneConstants.boardColumnNum && board.get(x).get(y) == Square.B){
					numOfB++;
					x = x - 1;
					y = y + 1;
				}
				if(x >= 0 && y < AbaloneConstants.boardColumnNum && board.get(x).get(y) == Square.E){
					possibleSquaresToPlacePiece.add(new Position(startX - 1, startY + 1));
				} else if(x >= 0 && y < AbaloneConstants.boardColumnNum && board.get(x).get(y) == Square.W){
					while(x >= 0 && y < AbaloneConstants.boardColumnNum && board.get(x).get(y) == Square.W) {
						numOfW++;
						x = x - 1;
						y = y + 1;
					}
					if(x >= 0 && y < AbaloneConstants.boardColumnNum && numOfB > numOfW){
						possibleSquaresToPlacePiece.add(new Position(startX - 1, startY + 1));
					}
				}
			} else {
				while(x >= 0 && y < AbaloneConstants.boardColumnNum && board.get(x).get(y) == Square.W){
					numOfW++;
					x = x - 1;
					y = y + 1;
				}
				if(x >= 0 && y < AbaloneConstants.boardColumnNum && board.get(x).get(y) == Square.E){
					possibleSquaresToPlacePiece.add(new Position(startX - 1, startY + 1));
				} else if(x >= 0 && y < AbaloneConstants.boardColumnNum && board.get(x).get(y) == Square.B){
					while(x >= 0 && y < AbaloneConstants.boardColumnNum && board.get(x).get(y) == Square.B) {
						numOfB++;
						x = x - 1;
						y = y + 1;
					}
					if(x >= 0 && y < AbaloneConstants.boardColumnNum && numOfW > numOfB){
						possibleSquaresToPlacePiece.add(new Position(startX - 1, startY + 1));
					}
				}
			}
			// lower-left:
			x = startX;
			y = startY;
			numOfB = 0;
			numOfW = 0;
			if(pieceColor == Square.B) {
				while(x < AbaloneConstants.boardRowNum && y >= 0 && board.get(x).get(y) == Square.B){
					numOfB++;
					x = x + 1;
					y = y - 1;
				}
				if(x < AbaloneConstants.boardRowNum && y >= 0 && board.get(x).get(y) == Square.E){
					possibleSquaresToPlacePiece.add(new Position(startX + 1, startY - 1));
				} else if(x < AbaloneConstants.boardRowNum && y >= 0 && board.get(x).get(y) == Square.W){
					while(x < AbaloneConstants.boardRowNum && y >= 0 && board.get(x).get(y) == Square.W) {
						numOfW++;
						x = x + 1;
						y = y - 1;
					}
					if(x < AbaloneConstants.boardRowNum && y >= 0 && numOfB > numOfW){
						possibleSquaresToPlacePiece.add(new Position(startX + 1, startY - 1));
					}
				}
			} else {
				while(x < AbaloneConstants.boardRowNum && y >= 0 && board.get(x).get(y) == Square.W){
					numOfW++;
					x = x + 1;
					y = y - 1;
				}
				if(x < AbaloneConstants.boardRowNum && y >= 0 && board.get(x).get(y) == Square.E){
					possibleSquaresToPlacePiece.add(new Position(startX + 1, startY - 1));
				} else if(x < AbaloneConstants.boardRowNum && y >= 0 && board.get(x).get(y) == Square.B){
					while(x < AbaloneConstants.boardRowNum && y >= 0 && board.get(x).get(y) == Square.B) {
						numOfB++;
						x = x + 1;
						y = y - 1;
					}
					if(x < AbaloneConstants.boardRowNum && y >= 0 && numOfW > numOfB){
						possibleSquaresToPlacePiece.add(new Position(startX + 1, startY - 1));
					}
				}
			}
			// lower-right:
			x = startX;
			y = startY;
			numOfB = 0;
			numOfW = 0;
			if(pieceColor == Square.B) {
				while(x < AbaloneConstants.boardRowNum && y < AbaloneConstants.boardRowNum 
						&& board.get(x).get(y) == Square.B){
					numOfB++;
					x = x + 1;
					y = y + 1;
				}
				if(x < AbaloneConstants.boardRowNum && y < AbaloneConstants.boardRowNum && 
						board.get(x).get(y) == Square.E){
					possibleSquaresToPlacePiece.add(new Position(startX + 1, startY + 1));
				} else if(x < AbaloneConstants.boardRowNum && y < AbaloneConstants.boardRowNum && 
						board.get(x).get(y) == Square.W){
					while(x < AbaloneConstants.boardRowNum && y < AbaloneConstants.boardRowNum && 
							board.get(x).get(y) == Square.W) {
						numOfW++;
						x = x + 1;
						y = y + 1;
					}
					if(x < AbaloneConstants.boardRowNum && y < AbaloneConstants.boardRowNum && numOfB > numOfW){
						possibleSquaresToPlacePiece.add(new Position(startX + 1, startY + 1));
					}
				}
			} else {
				while(x < AbaloneConstants.boardRowNum && y < AbaloneConstants.boardRowNum && 
						board.get(x).get(y) == Square.W){
					numOfW++;
					x = x + 1;
					y = y + 1;
				}
				if(x < AbaloneConstants.boardRowNum && y < AbaloneConstants.boardRowNum &&
						board.get(x).get(y) == Square.E){
					possibleSquaresToPlacePiece.add(new Position(startX + 1, startY + 1));
				} else if(x < AbaloneConstants.boardRowNum && y < AbaloneConstants.boardRowNum && 
						board.get(x).get(y) == Square.B){
					while(x < AbaloneConstants.boardRowNum && y < AbaloneConstants.boardRowNum && 
							board.get(x).get(y) == Square.B) {
						numOfB++;
						x = x + 1;
						y = y + 1;
					}
					if(x < AbaloneConstants.boardRowNum && y < AbaloneConstants.boardRowNum && numOfW > numOfB){
						possibleSquaresToPlacePiece.add(new Position(startX + 1, startY + 1));
					}
				}
			}
		} else {
			// previous jumps exist.
			if(lastJump.getDestinationX() == startX && lastJump.getDestinationY() == startY){
				// want to cancel last jump
				possibleSquaresToPlacePiece.add(new Position(lastJump.getOriginalX(), 
						lastJump.getOriginalY()));
//				jumps.remove(lastJump);
				return possibleSquaresToPlacePiece;
			}
			
			Direction direction = getJumpDirection(jumps);
			switch(direction){
				case LEFT_HORIZONTAL:
					if(board.get(startX).get(startY - 2) == Square.E){
						possibleSquaresToPlacePiece.add(new Position(startX, startY - 2));
					}
					break;
				case RIGHT_HORIZONTAL:
					if(board.get(startX).get(startY + 2) == Square.E){
						possibleSquaresToPlacePiece.add(new Position(startX, startY + 2));
					}
					break;
				case UPPER_LEFT_DIAGONAL:
					if(board.get(startX - 1).get(startY - 1) == Square.E){
						possibleSquaresToPlacePiece.add(new Position(startX - 1, startY - 1));
					}
					break;
				case UPPER_RIGHT_DIAGONAL:
					if(board.get(startX - 1).get(startY + 1) == Square.E){
						possibleSquaresToPlacePiece.add(new Position(startX - 1, startY + 1));
					}
					break;
				case LOWER_LEFT_DIAGONAL:
					if(board.get(startX + 1).get(startY - 1) == Square.E){
						possibleSquaresToPlacePiece.add(new Position(startX + 1, startY - 1));
					}
					break;
				case LOWER_RIGHT_DIAGONAL:
					if(board.get(startX + 1).get(startY + 1) == Square.E){
						possibleSquaresToPlacePiece.add(new Position(startX + 1, startY + 1));
					}
					break;
				default:
					break;
			}
		}
		return possibleSquaresToPlacePiece;
	}
	
	/**
	 * Method used to get the following possible pieces to make following moves, to be called
	 * in {@code #piecePlace()} then, inside it, it will call {@code View#toHoldPiece()}
	 * @param board input board
	 * @param turn input turn
	 * @return 2D boolean array, which indicate where should be highlighted.
	 */
	public boolean[][] getEnableSquare(List<ImmutableList<Square>> board, Turn turn){
		if(board == null || board.isEmpty()){
			throw new IllegalArgumentException("Input board should not be null or empty!");
		}
		boolean[][] enableSquare = 
				new boolean[AbaloneConstants.boardRowNum][AbaloneConstants.boardColumnNum];
		Square turnSquare = turn.getSquare();
		
		if(jumps == null || jumps.isEmpty()){
			for(int i = 0; i < AbaloneConstants.boardRowNum; i++){
				for(int j = 0; j < AbaloneConstants.boardColumnNum; j++){
					if(board.get(i).get(j) == turnSquare){
						enableSquare[i][j] = true;
					}
				}
			}
		}else{
			// {@code jumps} is not empty.
			if(jumps.size() >= 3){
				enableSquare[lastJump.getDestinationX()][lastJump.getDestinationY()] = true;
				return enableSquare;
			}
			Direction direction = getJumpDirection(jumps);
			switch(direction){
			case LEFT_HORIZONTAL:
				if(board.get(lastJump.getOriginalX()).get(lastJump.getOriginalY()) == turn.getSquare()){
					enableSquare[lastJump.getOriginalX()][lastJump.getOriginalY() + 2] = true;
				}
				enableSquare[lastJump.getDestinationX()][lastJump.getDestinationY()] = true;
				break;
			case RIGHT_HORIZONTAL:
				if(board.get(lastJump.getOriginalX()).get(lastJump.getOriginalY() - 2) == turn.getSquare()){
					enableSquare[lastJump.getOriginalX()][lastJump.getOriginalY() - 2] = true;
				}
				enableSquare[lastJump.getDestinationX()][lastJump.getDestinationY()] = true;
				break;
			case UPPER_LEFT_DIAGONAL:
				if(board.get(lastJump.getOriginalX() + 1).get(lastJump.getOriginalY() + 1) == turn.getSquare()){
					enableSquare[lastJump.getOriginalX() + 1][lastJump.getOriginalY() + 1] = true;
				}
				enableSquare[lastJump.getDestinationX()][lastJump.getDestinationY()] = true;
				break;
			case UPPER_RIGHT_DIAGONAL:
				if(board.get(lastJump.getOriginalX() + 1).get(lastJump.getOriginalY() - 1) == turn.getSquare()){
					enableSquare[lastJump.getOriginalX() + 1][lastJump.getOriginalY() - 1] = true;
				}
				enableSquare[lastJump.getDestinationX()][lastJump.getDestinationY()] = true;
				break;
			case LOWER_LEFT_DIAGONAL:
				if(board.get(lastJump.getOriginalX() - 1).get(lastJump.getOriginalY() + 1) == turn.getSquare()){
					enableSquare[lastJump.getOriginalX() - 1][lastJump.getOriginalY() + 1] = true;
				}
				enableSquare[lastJump.getDestinationX()][lastJump.getDestinationY()] = true;
				break;
			case LOWER_RIGHT_DIAGONAL:
				if(board.get(lastJump.getOriginalX() - 1).get(lastJump.getOriginalY() - 1) == turn.getSquare()){
					enableSquare[lastJump.getOriginalX() - 1][lastJump.getOriginalY() - 1] = true;
				}
				enableSquare[lastJump.getDestinationX()][lastJump.getDestinationY()] = true;
				break;
			default:
				break;
		}
		}
		return enableSquare;
	}
	
	public void setBoard(List<ImmutableList<Square>> board){
		this.board = board;
	}
	
	public void setJumps(List<Jump> jumps){
		this.jumps = jumps;
	}
	
	private boolean[][] positionListToBooleanArray(List<Position> positions){
		if(positions == null || positions.isEmpty()){
			return null;
		}
		boolean[][] res = new boolean[AbaloneConstants.boardRowNum][AbaloneConstants.boardColumnNum];
		for(Position position : positions){
			res[position.getX()][position.getY()] = true;
		}
		return res;
	}
	
	private boolean[][] boardToBooleanMatrix(List<ImmutableList<Square>> board, Turn turn){
		if(board == null || board.isEmpty()){
			return null;
		}
		Square turnSquare = turn.getSquare();
		boolean[][] res = new boolean[AbaloneConstants.boardRowNum][AbaloneConstants.boardColumnNum];
		for(int i = 0; i < AbaloneConstants.boardRowNum; i++){
			for(int j = 0; j < AbaloneConstants.boardColumnNum; j++){
				if(board.get(i).get(j) == turnSquare){
					res[i][j] = true;
				}
			}
		}
		return res;
	}
}
