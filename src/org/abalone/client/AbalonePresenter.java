package org.abalone.client;

import static org.abalone.client.AbaloneConstants.B;
import static org.abalone.client.AbaloneConstants.BOARD;
import static org.abalone.client.AbaloneConstants.BTurn;
import static org.abalone.client.AbaloneConstants.BoardColNum;
import static org.abalone.client.AbaloneConstants.BoardRowNum;
import static org.abalone.client.AbaloneConstants.E;
import static org.abalone.client.AbaloneConstants.I;
import static org.abalone.client.AbaloneConstants.GAMEOVER;
import static org.abalone.client.AbaloneConstants.JUMP;
import static org.abalone.client.AbaloneConstants.S;
import static org.abalone.client.AbaloneConstants.UNDERGOING;
import static org.abalone.client.AbaloneConstants.W;
import static org.abalone.client.AbaloneConstants.WTurn;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.game_api.GameApi.Container;
import org.game_api.GameApi.EndGame;
import org.game_api.GameApi.Operation;
import org.game_api.GameApi.Set;
import org.game_api.GameApi.SetTurn;
import org.game_api.GameApi.UpdateUI;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;

public class AbalonePresenter {
	
	/**
	 * We use MVP pattern:
	 * You can find Model at {@link AbaloneState},
	 * You can find Presenter at {@link AbalonePresenter},
	 * You can find View here, and it will have the Abalone Graphics. 
	 *
	 * @author Long Yang (ly603@nyu.edu)
	 *
	 */
	public interface View {
		/**
		 * Set the presenter for viewer, and the viewer can call certain methods on presenter, e.g.
		 * (1). When a jump is placed ({@link AbalonePresenter#pieceJumped()})
		 * (2). When all the jumps are placed, player want to finish his round 
		 * ({@link AbalonePresenter#finishedJumpingPieces()})
		 * 
		 * One round for a player is viewed by viewer as following ways:
		 * (1). The viewer calls ({@link AbalonePresenter#pieceJumped()}) to place pieces 
		 * jump several times (of cause, it can also use this to cancel previous jumps,
		 * so this should be checked in the source of {@link AbalonePresenter#pieceJumped()}).
		 * (2). The viewer finishes all of his/her piece jumps and calls 
		 * ({@link AbalonePresenter#finishedJumpingPieces()}), and this method will compute which
		 * opponent's pieces will be pushed.
		 * 
		 * One round for a player is viewed by presenter as following ways:
		 * (1). The presenter calls ({@link #nextPieceJump()}) to send the Board back to viewer,
		 * who can use this to update the graphics.
		 * (2). The presenter calls ({@link #finishMoves()}) to sent the Board back to viewer,
		 * who can use this to update the graphics.
		 * 
		 * @param abalonePresenter input abalonePresenter.
		 */
		public void setPresenter(AbalonePresenter abalonePresenter);
		
		/**
		 * Because there is no invisible materials, no need to separate players and viewers.
		 * @param board input board used to display the board on screen.
		 * @param message input message used to notify player the status of the game.
		 */
		public void setPlayerState(List<ArrayList<String>> board, boolean[][] enableMatrix, 
				String message);
		
		/**
		 * Method to be implemented for holding the pieces after {@code AbalonePresenter#piecePlace()}
		 * @param board
		 * @param holdableSquare
		 * @param message
		 */
		public void toHoldOnePiece(List<ArrayList<String>> board, boolean[][] holdableMatrix, 
				boolean enableFinishButton, String turn, String message);
		
		/**
		 * Method to be implement for place the pieces after {@code AbalonePresenter#pieceHold()}
		 * @param board
		 * @param placableSqaure
		 * @param message
		 */
		public void toPlaceOnePiece(List<ArrayList<String>> board, boolean[][] placableMatrix,
				boolean enableFinishButton, String turn, String message);
	}
		
	public enum Direction{
		UPPER_LEFT_DIAGONAL, 
		UPPER_RIGHT_DIAGONAL, 
		LOWER_LEFT_DIAGONAL, 
		LOWER_RIGHT_DIAGONAL,
		LEFT_HORIZONTAL, 
		RIGHT_HORIZONTAL
	}
	
	/*
	 * field variables for {@link AbalonePresenter}
	 */
	private final AbaloneLogic abaloneLogic = new AbaloneLogic();
	private final View view;
	private final Container container;
	private int yourPlayerIndex;
	private Optional<String> myTurn;
	private String currentTurn; 							// currentTurn must not be null, explanation down.
	private List<ArrayList<Integer>> jumps;		// used to store all the jumps in current round.
	private AbaloneState abaloneState;
	private int[] lastJump = new int[5]; 			// {startX, startY, endX, endY, pieceColor{0/1}}
	private List<ArrayList<String>> currentBoard;
	private int heldX;
	private int heldY;
	private String abaloneMessage = "";
	private List<String> playerIds;
	
	public AbalonePresenter(View view, Container container) {
		this.view = view;
		this.container = container;
		view.setPresenter(this);
	}
	
	/**
	 * Method used to update the presenter and view with the information from {@code updateUI}
	 * @param updateUI input updateUI to pass necessary information.
	 */
	public void updateUI(UpdateUI updateUI){
		playerIds = updateUI.getPlayerIds();
		yourPlayerIndex = updateUI.getYourPlayerIndex();
		myTurn = 
				yourPlayerIndex == 0 ? Optional.<String>of(WTurn) :
				yourPlayerIndex == 1 ? Optional.<String>of(BTurn) :
				Optional.<String>absent(); // for the viewer
		// If we invoke this method, that means the switch between players happens, so we 
		// must place new jumps.
		jumps = Lists.<ArrayList<Integer>>newArrayList();
		if(updateUI.getState().isEmpty()){
			// WP should finish the initialization.
			if(myTurn.isPresent() && myTurn.get().equals(WTurn)){
				container.sendMakeMove(abaloneLogic.getInitialMove(playerIds));
			}
			return;
		}
		// {@code currentTurn} can not be null, because it is gotten from lastMove, and for the 
		// lastMove, we must have SetTurn operation, even lastMove does not contain SetTurn, it 
		// should be before starting game, then it should let WP pass the inital moves as above.
		for(Operation operation : updateUI.getLastMove()){
			if(operation instanceof SetTurn){
				currentTurn = playerIds.indexOf(((SetTurn)operation).getPlayerId()) == 0 ? WTurn : BTurn;
			}
		}
		abaloneState = 
				AbaloneState.gameApiState2AbaloneState(updateUI.getState(), currentTurn, playerIds);
		if(updateUI.isViewer()){
			// TODO: need to be verified after adding the player change frame in the graphics.
			view.setPlayerState(abaloneState.getBoard(), 
					new boolean[BoardRowNum][BoardColNum], UNDERGOING);
			return;
		}
		if(updateUI.isAiPlayer()){
			//TODO: to be finished in HW4
			//container.sentMakeMove();
			return;
		}
		// So now, it must be a player.
		view.setPlayerState(abaloneState.getBoard(),
				getEnableSquares(abaloneState.getBoard(), abaloneState.getTurn()), UNDERGOING);
		// this is my turn, so I need to make the move.
		if(myTurn.isPresent() && myTurn.get().equals(currentTurn)){
			view.toHoldOnePiece(abaloneState.getBoard(), getEnableSquares(abaloneState.getBoard(), 
					myTurn.get()), !jumps.isEmpty(), myTurn.get(), UNDERGOING);
		}
		// if not my round, I just watch, nothing needed to be done!
	}
	
	public void heldOnePiece(int x, int y) {
		if(x < 0 || y < 0 || x > BoardRowNum || y > BoardColNum) {
			throw new RuntimeException("index for held piece should be valid!");
		}
		
		heldX = x;
		heldY = y;
		boolean[][] placableMatrix = getPlacableMatrix(x, y);
		Collections.sort(jumps, jumpComparator);
		List<ArrayList<String>> boardAfterJumps = abaloneState.applyJumpOnBoard(jumps).getBoard();
		view.toPlaceOnePiece(boardAfterJumps, placableMatrix, !jumps.isEmpty(), 
				abaloneState.getTurn(), UNDERGOING);
	}
	
	public void placedOnePiece(int x, int y) {
		if(x < 0 || y < 0 || x > BoardRowNum || y > BoardColNum) {
			throw new RuntimeException("index for held piece should be valid!");
		}
		// currentBoard is the board which before this jump, apply all the previous jumps.
		Collections.sort(jumps, jumpComparator);
		currentBoard = abaloneState.applyJumpOnBoard(jumps).getBoard();
		int piece = abaloneState.getTurn().equals(WTurn) ? 0 : 1;
		List<ArrayList<String>> appliedJumpsBoard;
		boolean[][] holdableMatrix = new boolean[BoardRowNum][BoardColNum];
		if(heldX == x && heldY == y) {
			// cancel previous holding piece operation
			holdableMatrix = getEnableSquares(abaloneState.getBoard(), abaloneState.getTurn());
			view.toHoldOnePiece(currentBoard, holdableMatrix, 
					!jumps.isEmpty(), abaloneState.getTurn(), UNDERGOING);
		} else {
			List<Integer> reverseCurrentJump = Lists.<Integer>newArrayList(x, y, heldX, heldY, piece);
			if(jumps.contains(reverseCurrentJump)) {
				// cancel previous jump after that.
				jumps = jumps.subList(0, jumps.indexOf(reverseCurrentJump));
				if(jumps.isEmpty()) {
					lastJump = null;
				} else {
					List<Integer> lastJumpList = jumps.get(jumps.size() - 1);
					lastJump = new int[5];
					for(int i = 0; i < lastJump.length; i++){
						lastJump[i] = lastJumpList.get(i);
					}
				}
				holdableMatrix = getEnableSquares(abaloneState.getBoard(), abaloneState.getTurn());
				Collections.sort(jumps, jumpComparator);
				appliedJumpsBoard = abaloneState.applyJumpOnBoard(jumps).getBoard();
				view.toHoldOnePiece(appliedJumpsBoard, holdableMatrix, 
						!jumps.isEmpty(), abaloneState.getTurn(), UNDERGOING);
				
			} else {
				ArrayList<Integer> thisJump = Lists.newArrayList(heldX, heldY, x, y, piece);
				lastJump = new int[5];
				for(int i = 0; i < lastJump.length; i++){
					lastJump[i] = thisJump.get(i);
				}
				if(currentBoard.get(x).get(y).equals(E)) {
					// add the jump into the jumps directly.
					jumps.add(thisJump);
					holdableMatrix = getEnableSquares(abaloneState.getBoard(), abaloneState.getTurn());
					Collections.sort(jumps, jumpComparator);
					appliedJumpsBoard = abaloneState.applyJumpOnBoard(jumps).getBoard();
					view.toHoldOnePiece(appliedJumpsBoard, holdableMatrix, 
							!jumps.isEmpty(), abaloneState.getTurn(), UNDERGOING);
				} else {
					/*
					 *  because we have make sure the place is legal, we can directly get the pushed pieces,
					 *  without checking whether the jump is legal.
					 */
					abaloneMessage = generateAllJumps(x, y, piece);
					holdableMatrix = getEnableSquares(abaloneState.getBoard(), abaloneState.getTurn());
					Collections.sort(jumps, jumpComparator);
					appliedJumpsBoard = abaloneState.applyJumpOnBoard(jumps).getBoard();
					view.toHoldOnePiece(appliedJumpsBoard, holdableMatrix, 
							!jumps.isEmpty(), abaloneState.getTurn(), abaloneMessage);
				}
			}
		}
	}
	
	public void finishAllPlacing(boolean isGameOver) {
		Collections.sort(jumps, jumpComparator);
		currentBoard = abaloneState.applyJumpOnBoard(jumps).getBoard();
		String nextPlayerId = yourPlayerIndex == 0 ? playerIds.get(1) : playerIds.get(0);
		List<Operation> moves = Lists.<Operation>newArrayList(
				new SetTurn(nextPlayerId),
				new Set(BOARD, currentBoard), 
				new Set(JUMP, jumps));
		if(isGameOver) {
			moves.add(new EndGame(playerIds.get(yourPlayerIndex)));
		}
		container.sendMakeMove(moves);
		jumps = Lists.<ArrayList<Integer>>newArrayList();
		lastJump = new int[5];
	}
	
	@SuppressWarnings("unchecked")
	private String generateAllJumps(int x, int y, int piece) {
		String message = "";
		List<ArrayList<Integer>> jumpList = Lists.<ArrayList<Integer>>newArrayList(
				Lists.<Integer>newArrayList(heldX, heldY, x, y, myTurn.get().equals(WTurn) ? 0 : 1));
		Direction direction = getJumpDirection(jumpList);
		switch(direction) {
			case LEFT_HORIZONTAL:
				while(y >= 0 && 
						(currentBoard.get(x).get(y).equals(W) || currentBoard.get(x).get(y).equals(B))){
					jumps.add(Lists.newArrayList(x, y + 2, x, y, 
							squareColor2PieceInt(currentBoard.get(x).get(y + 2))));
					y = y - 2;
				}
				if(y >= 0 && currentBoard.get(x).get(y).equals(E)){
					jumps.add(Lists.newArrayList(x, y + 2, x, y, 
							squareColor2PieceInt(currentBoard.get(x).get(y + 2))));
					message = UNDERGOING;
				}
				if(y < 0 || currentBoard.get(x).get(y).equals(I) || currentBoard.get(x).get(y).equals(S)){
					jumps.add(Lists.newArrayList(x, y + 2, x, y + 1, 
							squareColor2PieceInt(currentBoard.get(x).get(y + 2))));
					message = GAMEOVER;
				}
				break;
			case RIGHT_HORIZONTAL:
				while(y < BoardColNum && 
						(currentBoard.get(x).get(y).equals(W) || currentBoard.get(x).get(y).equals(B))){
					jumps.add(Lists.newArrayList(x, y - 2, x, y, 
							squareColor2PieceInt(currentBoard.get(x).get(y - 2))));
					y = y + 2;
				}
				if(y < BoardColNum && currentBoard.get(x).get(y).equals(E)){
					jumps.add(Lists.newArrayList(x, y - 2, x, y, 
							squareColor2PieceInt(currentBoard.get(x).get(y - 2))));
					message = UNDERGOING;
				}
				if(y >= BoardColNum || currentBoard.get(x).get(y).equals(I) || 
						currentBoard.get(x).get(y).equals(S)){
					jumps.add(Lists.newArrayList(x, y - 2, x, y - 1, 
							squareColor2PieceInt(currentBoard.get(x).get(y - 2))));
					message = GAMEOVER;
				}
				break;
			case UPPER_LEFT_DIAGONAL:
				while(x >= 0 && y >= 0 && 
				(currentBoard.get(x).get(y).equals(W) || currentBoard.get(x).get(y).equals(B))){
					jumps.add(Lists.newArrayList(x + 1, y + 1, x, y, 
							squareColor2PieceInt(currentBoard.get(x + 1).get(y + 1))));
					x = x - 1;
					y = y - 1;
				}
				if(x >= 0 && y >= 0 && currentBoard.get(x).get(y).equals(E)){
					jumps.add(Lists.newArrayList(x + 1, y + 1, x, y, 
							squareColor2PieceInt(currentBoard.get(x + 1).get(y + 1))));
					message = UNDERGOING;
				}
				if(currentBoard.get(x).get(y).equals(S)){
					jumps.add(Lists.newArrayList(x + 1, y + 1, x, y, 
							squareColor2PieceInt(currentBoard.get(x + 1).get(y + 1))));
					message = GAMEOVER;
				}
				break;
			case UPPER_RIGHT_DIAGONAL:
				while(x >= 0 && y < BoardColNum && 
						(currentBoard.get(x).get(y).equals(W) || currentBoard.get(x).get(y).equals(B))){
					jumps.add(Lists.newArrayList(x + 1, y - 1, x, y, 
							squareColor2PieceInt(currentBoard.get(x + 1).get(y - 1))));
					x = x - 1;
					y = y + 1;
				}
				if(x >= 0 && y < BoardColNum && currentBoard.get(x).get(y).equals(E)){
					jumps.add(Lists.newArrayList(x + 1, y - 1, x, y, 
							squareColor2PieceInt(currentBoard.get(x + 1).get(y - 1))));
					message = UNDERGOING;
				}
				if(currentBoard.get(x).get(y).equals(S)){
					jumps.add(Lists.newArrayList(x + 1, y - 1, x, y, 
							squareColor2PieceInt(currentBoard.get(x + 1).get(y - 1))));
					message = GAMEOVER;
				}
				break;
			case LOWER_LEFT_DIAGONAL:
				while(x < BoardRowNum && y >= 0 && 
						(currentBoard.get(x).get(y).equals(W) || currentBoard.get(x).get(y).equals(B))){
					jumps.add(Lists.newArrayList(x - 1, y + 1, x, y, 
							squareColor2PieceInt(currentBoard.get(x - 1).get(y + 1))));
					x = x + 1;
					y = y - 1;
				}
				if(x < BoardRowNum && y >= 0 && currentBoard.get(x).get(y).equals(E)){
					jumps.add(Lists.newArrayList(x - 1, y + 1, x, y, 
							squareColor2PieceInt(currentBoard.get(x - 1).get(y + 1))));
					message = UNDERGOING;
				}
				if(currentBoard.get(x).get(y).equals(S)){
					jumps.add(Lists.newArrayList(x - 1, y + 1, x, y, 
							squareColor2PieceInt(currentBoard.get(x - 1).get(y + 1))));
					message = GAMEOVER;
				}
				break;
			case LOWER_RIGHT_DIAGONAL:
				while(x < BoardRowNum && y < BoardColNum && 
						(currentBoard.get(x).get(y).equals(W) || currentBoard.get(x).get(y).equals(B))){
					jumps.add(Lists.newArrayList(x - 1, y - 1, x, y, 
							squareColor2PieceInt(currentBoard.get(x - 1).get(y - 1))));
					x = x + 1;
					y = y + 1;
				}
				if(x < BoardRowNum && y < BoardColNum && 
						currentBoard.get(x).get(y).equals(E)){
					jumps.add(Lists.newArrayList(x - 1, y - 1, x, y, 
							squareColor2PieceInt(currentBoard.get(x - 1).get(y - 1))));
					message = UNDERGOING;
				}
				if(currentBoard.get(x).get(y).equals(S)){
					jumps.add(Lists.newArrayList(x - 1, y - 1, x, y, 
							squareColor2PieceInt(currentBoard.get(x - 1).get(y - 1))));
					message = GAMEOVER;
				}
				break;
			default:
				break;
		}
		return message;
	}
	
	/**
	 * Method used to get the following possible pieces to make following moves, to be called
	 * in {@code #placedOnePiece()} then, inside it, it will call {@code View#toHoldOnePiece()}
	 * @param board input board
	 * @param turn input turn
	 * @return 2D boolean array, which indicate where should be highlighted.
	 */
	private boolean[][] getEnableSquares(List<ArrayList<String>> board, String turn){
		if(board == null || board.isEmpty()){
			throw new IllegalArgumentException("Input board should not be null or empty! For "
					+ "getEnableSquares method from AbalonePresenter class");
		}
		boolean[][] enableSquare = 
				new boolean[BoardRowNum][BoardColNum];
		if(!myTurn.isPresent() || !myTurn.get().equals(turn)){
			// if not my turn, none should be enabled.
			return new boolean[BoardRowNum][BoardColNum];
		}
		String squareColor = turn.equals(WTurn) ? W : B;
		if(jumps == null || jumps.isEmpty()){
			for(int i = 0; i < BoardRowNum; i++){
				for(int j = 0; j < BoardColNum; j++){
					if(board.get(i).get(j).equals(squareColor)){
						enableSquare[i][j] = true;
					}
				}
			}
			return enableSquare;
		} else {
			// {@code jumps} is not empty.
			if(jumps.size() >= 3){
				// you have already made three steps, and your only jump possibility is to cancel previous one.
				enableSquare[lastJump[2]][lastJump[3]] = true;
				return enableSquare;
			}
			Direction direction = getJumpDirection(jumps);
			// you can cancel your last jump.
			enableSquare[lastJump[2]][lastJump[3]] = true;
			switch(direction){
				case LEFT_HORIZONTAL:
					if(board.get(lastJump[0]).get(lastJump[1] + 2).equals(squareColor)){
						enableSquare[lastJump[0]][lastJump[1] + 2] = true;
					}
					break;
				case RIGHT_HORIZONTAL:
					if(board.get(lastJump[0]).get(lastJump[1] - 2).equals(squareColor)){
						enableSquare[lastJump[0]][lastJump[1] - 2] = true;
					}
					break;
				case UPPER_LEFT_DIAGONAL:
					if(board.get(lastJump[0] + 1).get(lastJump[1] + 1).equals(squareColor)){
						enableSquare[lastJump[0] + 1][lastJump[1] + 1] = true;
					}
					break;
				case UPPER_RIGHT_DIAGONAL:
					if(board.get(lastJump[0] + 1).get(lastJump[1] - 1).equals(squareColor)){
						enableSquare[lastJump[0] + 1][lastJump[1] - 1] = true;
					}
					break;
				case LOWER_LEFT_DIAGONAL:
					if(board.get(lastJump[0] - 1).get(lastJump[1] + 1).equals(squareColor)){
						enableSquare[lastJump[0] - 1][lastJump[1] + 1] = true;
					}
					break;
				case LOWER_RIGHT_DIAGONAL:
					if(board.get(lastJump[0] - 1).get(lastJump[1] - 1).equals(squareColor)){
						enableSquare[lastJump[0] - 1][lastJump[1] - 1] = true;
					}
					break;
				default:
					break;
			}
		}
		return enableSquare;
	}
	/**
	 * Method used to get the possible placed positions for a being-held piece
	 * @param pieceHeldPosition the start position for the being-held piece.
	 * @return a {@code List<ArrayList<Integer>>} list of possible placed positions 
	 * for this being-held piece. Format as following: {{4, 5}, {5, 6}}
	 */
	public boolean[][] getPlacableMatrix (int startX, int startY){
		Collections.sort(jumps, jumpComparator);
		currentBoard = abaloneState.applyJumpOnBoard(jumps).getBoard();
		String pieceColor = currentBoard.get(startX).get(startY);
		boolean[][] placableMatrix = new boolean[BoardRowNum][BoardColNum];
		placableMatrix[startX][startY] = true;
		
		if(jumps == null || jumps.isEmpty()) {
			leftPlacableSquareEmptyJump(currentBoard, startX, startY, pieceColor, placableMatrix);
			rightPlacableSquareEmptyJump(currentBoard, startX, startY, pieceColor, placableMatrix);
			lowerLeftPlacableSquareEmptyJump(currentBoard, startX, startY, pieceColor, placableMatrix);
			lowerRightPlacableSquareEmptyJump(currentBoard, startX, startY, pieceColor, placableMatrix);
			upperLeftPlacableSquareEmptyJump(currentBoard, startX, startY, pieceColor, placableMatrix);
			upperRightPlacableSquareEmptyJump(currentBoard, startX, startY, pieceColor, placableMatrix);
		} else {
			// previous jumps exist.
			if(lastJump[2] == startX && lastJump[3] == startY){
				// want to cancel last jump
				placableMatrix[lastJump[0]][lastJump[1]] = true;
				return placableMatrix;
			}
			
			Direction direction = getJumpDirection(jumps);
			switch(direction){
				case LEFT_HORIZONTAL:
					if(currentBoard.get(startX).get(startY - 2).equals(E)){
						placableMatrix[startX][startY - 2] = true;
					}
					break;
				case RIGHT_HORIZONTAL:
					if(currentBoard.get(startX).get(startY + 2).equals(E)){
						placableMatrix[startX][startY + 2] = true;
					}
					break;
				case UPPER_LEFT_DIAGONAL:
					if(currentBoard.get(startX - 1).get(startY - 1).equals(E)){
						placableMatrix[startX - 1][startY - 1] = true;
					}
					break;
				case UPPER_RIGHT_DIAGONAL:
					if(currentBoard.get(startX - 1).get(startY + 1).equals(E)){
						placableMatrix[startX - 1][startY + 1] = true;
					}
					break;
				case LOWER_LEFT_DIAGONAL:
					if(currentBoard.get(startX + 1).get(startY - 1).equals(E)){
						placableMatrix[startX + 1][startY - 1] = true;
					}
					break;
				case LOWER_RIGHT_DIAGONAL:
					if(currentBoard.get(startX + 1).get(startY + 1).equals(E)){
						placableMatrix[startX + 1][startY + 1] = true;
					}
					break;
				default:
					break;
			}
		}
		return placableMatrix;
	}
	
	private void leftPlacableSquareEmptyJump(List<ArrayList<String>> board, 
			int startX, int startY, String pieceColor, boolean[][] placableMatrix) {
		int numB = 0;
		int numW = 0;
		int x = startX;
		int y = startY;
		
		if(pieceColor.equals(W)) {
			while(y >= 0 && board.get(x).get(y).equals(W)) {
				numW++;
				y = y - 2;
			}
			if(y >= 0 && board.get(x).get(y).equals(E) && numW <= 3) {
				placableMatrix[startX][startY - 2] = true;
			} else if(y >= 0 && board.get(x).get(y).equals(B)) {
				while(y >= 0 && board.get(x).get(y).equals(B)) {
					numB++;
					y = y - 2;
				}
				if(numW <= 3 && numW > numB) {
					placableMatrix[startX][startY - 2] = true;
				}
			}
		} else {
			while(y >= 0 && board.get(x).get(y).equals(B)) {
				numB++;
				y = y - 2;
			}
			if(y >= 0 && board.get(x).get(y).equals(E) && numB <= 3) {
				placableMatrix[startX][startY - 2] = true;
			} else if(y >= 0 && board.get(x).get(y).equals(W)) {
				while(y >= 0 && board.get(x).get(y).equals(W)) {
					numW++;
					y = y - 2;
				}
				if(numB <= 3 && numB > numW) {
					placableMatrix[startX][startY - 2] = true;
				}
			}
		}
	}
	
	private void rightPlacableSquareEmptyJump(List<ArrayList<String>> board, 
			int startX, int startY, String pieceColor, boolean[][] placableMatrix) {
		int numB = 0;
		int numW = 0;
		int x = startX;
		int y = startY;
		
		if(pieceColor.equals(W)) {
			while(y < BoardColNum && board.get(x).get(y).equals(W)) {
				numW++;
				y = y + 2;
			}
			if(y < BoardColNum && board.get(x).get(y).equals(E) && numW <= 3) {
				placableMatrix[startX][startY + 2] = true;
			} else if(y < BoardColNum && board.get(x).get(y).equals(B)) {
				while(y < BoardColNum && board.get(x).get(y).equals(B)) {
					numB++;
					y = y + 2;
				}
				if(numW <= 3 && numW > numB) {
					placableMatrix[startX][startY + 2] = true;
				}
			}
		} else {
			while(y < BoardColNum && board.get(x).get(y).equals(B)) {
				numB++;
				y = y + 2;
			}
			if(y < BoardColNum && board.get(x).get(y).equals(E) && numB <= 3) {
				placableMatrix[startX][startY + 2] = true;
			} else if(y < BoardColNum && board.get(x).get(y).equals(W)) {
				while(y < BoardColNum && board.get(x).get(y).equals(W)) {
					numW++;
					y = y + 2;
				}
				if(numB <= 3 && numB > numW) {
					placableMatrix[startX][startY + 2] = true;
				}
			}
		}
	}
	
	private void upperLeftPlacableSquareEmptyJump(List<ArrayList<String>> board, 
			int startX, int startY, String pieceColor, boolean[][] placableMatrix) {
		int numB = 0;
		int numW = 0;
		int x = startX;
		int y = startY;
		
		if(pieceColor.equals(W)) {
			while(x >= 0 && y >= 0 && board.get(x).get(y).equals(W)) {
				numW++;
				x = x - 1;
				y = y - 1;
			}
			if(x >= 0 && y >= 0 && board.get(x).get(y).equals(E) && numW <= 3) {
				placableMatrix[startX - 1][startY - 1] = true;
			} else if(x >= 0 && y >= 0 && board.get(x).get(y).equals(B)) {
				while(x >= 0 && y >= 0 && board.get(x).get(y).equals(B)) {
					numB++;
					x = x - 1;
					y = y - 1;
				}
				if(numW <= 3 && numW > numB) {
					placableMatrix[startX - 1][startY - 1] = true;
				}
			}
		} else {
			while(x >= 0 && y >= 0 && board.get(x).get(y).equals(B)) {
				numB++;
				x = x - 1;
				y = y - 1;
			}
			if(x >= 0 && y >= 0 && board.get(x).get(y).equals(E) && numB <= 3) {
				placableMatrix[startX - 1][startY - 1] = true;
			} else if(x >= 0 && y >= 0 && board.get(x).get(y).equals(W)) {
				while(x >= 0 && y >= 0 && board.get(x).get(y).equals(W)) {
					numW++;
					x = x - 1;
					y = y - 1;
				}
				if(numB <= 3 && numB > numW) {
					placableMatrix[startX - 1][startY - 1] = true;
				}
			}
		}
	}
	
	private void upperRightPlacableSquareEmptyJump(List<ArrayList<String>> board, 
			int startX, int startY, String pieceColor, boolean[][] placableMatrix) {
		int numB = 0;
		int numW = 0;
		int x = startX;
		int y = startY;
		
		if(pieceColor.equals(W)) {
			while(x >= 0 && y < BoardColNum && board.get(x).get(y).equals(W)) {
				numW++;
				x = x - 1;
				y = y + 1;
			}
			if(x >= 0 && y < BoardColNum && board.get(x).get(y).equals(E) && numW <= 3) {
				placableMatrix[startX - 1][startY + 1] = true;
			} else if(x >= 0 && y < BoardColNum && board.get(x).get(y).equals(B)) {
				while(x >= 0 && y < BoardColNum && board.get(x).get(y).equals(B)) {
					numB++;
					x = x - 1;
					y = y + 1;
				}
				if(numW <= 3 && numW > numB) {
					placableMatrix[startX - 1][startY + 1] = true;
				}
			}
		} else {
			while(x >= 0 && y < BoardColNum && board.get(x).get(y).equals(B)) {
				numB++;
				x = x - 1;
				y = y + 1;
			}
			if(x >= 0 && y < BoardColNum && board.get(x).get(y).equals(E) && numB <= 3) {
				placableMatrix[startX - 1][startY + 1] = true;
			} else if(x >= 0 && y < BoardColNum && board.get(x).get(y).equals(W)) {
				while(x >= 0 && y < BoardColNum && board.get(x).get(y).equals(W)) {
					numW++;
					x = x - 1;
					y = y + 1;
				}
				if(numB <= 3 && numB > numW) {
					placableMatrix[startX - 1][startY + 1] = true;
				}
			}
		}
	}
	
	private void lowerLeftPlacableSquareEmptyJump(List<ArrayList<String>> board, 
			int startX, int startY, String pieceColor, boolean[][] placableMatrix) {
		int numB = 0;
		int numW = 0;
		int x = startX;
		int y = startY;
		
		if(pieceColor.equals(W)) {
			while(x < BoardRowNum && y >= 0 && board.get(x).get(y).equals(W)) {
				numW++;
				x = x + 1;
				y = y - 1;
			}
			if(x < BoardRowNum && y >= 0 && board.get(x).get(y).equals(E) && numW <= 3) {
				placableMatrix[startX + 1][startY - 1] = true;
			} else if(x < BoardRowNum && y >= 0 && board.get(x).get(y).equals(B)) {
				while(x < BoardRowNum && y >= 0 && board.get(x).get(y).equals(B)) {
					numB++;
					x = x + 1;
					y = y - 1;
				}
				if(numW <= 3 && numW > numB) {
					placableMatrix[startX + 1][startY - 1] = true;
				}
			}
		} else {
			while(x < BoardRowNum && y >= 0 && board.get(x).get(y).equals(B)) {
				numB++;
				x = x + 1;
				y = y - 1;
			}
			if(x < BoardRowNum && y >= 0 && board.get(x).get(y).equals(E) && numB <= 3) {
				placableMatrix[startX + 1][startY - 1] = true;
			} else if(x < BoardRowNum && y >= 0 && board.get(x).get(y).equals(W)) {
				while(x < BoardRowNum && y >= 0 && board.get(x).get(y).equals(W)) {
					numW++;
					x = x + 1;
					y = y - 1;
				}
				if(numB <= 3 && numB > numW) {
					placableMatrix[startX + 1][startY - 1] = true;
				}
			}
		}
	}
	
	private void lowerRightPlacableSquareEmptyJump(List<ArrayList<String>> board, 
			int startX, int startY, String pieceColor, boolean[][] placablaMatrix) {
		int numB = 0;
		int numW = 0;
		int x = startX;
		int y = startY;
		
		if(pieceColor.equals(W)) {
			while(x < BoardRowNum && y < BoardColNum && board.get(x).get(y).equals(W)) {
				numW++;
				x = x + 1;
				y = y + 1;
			}
			if(x < BoardRowNum && y < BoardColNum  && board.get(x).get(y).equals(E) && numW <= 3) {
				placablaMatrix[startX + 1][startY + 1] = true;
			} else if(x < BoardRowNum && y < BoardColNum  && board.get(x).get(y).equals(B)) {
				while(x < BoardRowNum && y < BoardColNum  && board.get(x).get(y).equals(B)) {
					numB++;
					x = x + 1;
					y = y + 1;
				}
				if(numW <= 3 && numW > numB) {
					placablaMatrix[startX + 1][startY + 1] = true;
				}
			}
		} else {
			while(x < BoardRowNum && y < BoardColNum && board.get(x).get(y).equals(B)) {
				numB++;
				x = x + 1;
				y = y + 1;
			}
			if(x < BoardRowNum && y < BoardColNum && board.get(x).get(y).equals(E) && numB <= 3) {
				placablaMatrix[startX + 1][startY + 1] = true;
			} else if(x < BoardRowNum && y < BoardColNum && board.get(x).get(y).equals(W)) {
				while(x < BoardRowNum && y < BoardColNum && board.get(x).get(y).equals(W)) {
					numW++;
					x = x + 1;
					y = y + 1;
				}
				if(numB <= 3 && numB > numW) {
					placablaMatrix[startX + 1][startY + 1] = true;
				}
			}
		}
	}
	
	/**
	 * Helper method used to return the direction the input {@code jump}
	 * @param jump input jump.
	 * @return one of the six directions.
	 */
	public static Direction getJumpDirection(List<ArrayList<Integer>> jumpList) {
		if(jumpList.get(0).get(0) == jumpList.get(0).get(2)){
			if(jumpList.get(0).get(1) < jumpList.get(0).get(3)){
				return Direction.RIGHT_HORIZONTAL;
			}else{
				return Direction.LEFT_HORIZONTAL;
			}
		}else if(jumpList.get(0).get(0) > jumpList.get(0).get(2)){
			if(jumpList.get(0).get(1) < jumpList.get(0).get(3)){
				return Direction.UPPER_RIGHT_DIAGONAL;
			}else{
				return Direction.UPPER_LEFT_DIAGONAL;
			}
		}else{
			if(jumpList.get(0).get(1) < jumpList.get(0).get(3)){
				return Direction.LOWER_RIGHT_DIAGONAL;
			}else{
				return Direction.LOWER_LEFT_DIAGONAL;
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public static Comparator<ArrayList<Integer>> jumpComparator = new Comparator<ArrayList<Integer>>(){
		@Override
		public int compare(ArrayList<Integer> jumpA, ArrayList<Integer> jumpB) {
			Direction direction = getJumpDirection(Lists.<ArrayList<Integer>>newArrayList(jumpA, jumpB));
			switch(direction){
				case UPPER_LEFT_DIAGONAL:
					if(jumpA.get(0) < jumpB.get(0) && jumpA.get(1) < jumpB.get(1)) {
						return -1;
					} else if (jumpA.get(0) == jumpB.get(0) && jumpA.get(1) == jumpB.get(1)) {
						return 0;
					} else {
						return 1;
					}
				case UPPER_RIGHT_DIAGONAL: 
					if(jumpA.get(0) < jumpB.get(0) && jumpA.get(1) > jumpB.get(1)) {
						return -1;
					} else if(jumpA.get(0) == jumpB.get(0) && jumpA.get(1) == jumpB.get(1)) {
						return 0;
					} else {
						return 1;
					}
				case LOWER_LEFT_DIAGONAL: 
					if(jumpA.get(0) > jumpB.get(0) && jumpA.get(1) < jumpB.get(1)){
						return -1;
					} else if(jumpA.get(0) == jumpB.get(0) && jumpA.get(1) == jumpB.get(1)) {
						return 0;
					} else {
						return 1;
					}
				case LOWER_RIGHT_DIAGONAL:
					if(jumpA.get(0) > jumpB.get(0) && jumpA.get(1) > jumpB.get(1)) {
						return -1;
					} else if(jumpA.get(0) == jumpB.get(0) && jumpA.get(1) == jumpB.get(1)) {
						return 0;
					} else {
						return 1;
					}
				case LEFT_HORIZONTAL: 
					if(jumpA.get(1) < jumpB.get(1)) {
						return -1;
					} else if(jumpA.get(0) == jumpB.get(0) && jumpA.get(1) == jumpB.get(1)) {
						return 0;
					} else {
						return 1;
					}
				case RIGHT_HORIZONTAL:
					if(jumpA.get(1) > jumpB.get(1)) {
						return -1;
					} else if(jumpA.get(0) == jumpB.get(0) && jumpA.get(1) == jumpB.get(1)) {
						return 0;
					} else {
						return 1;
					}
				default:
					return 0;
			}
		}
	};
	
	private int squareColor2PieceInt(String squareColor) {
		if(squareColor == null || squareColor.length() == 0) {
			return -1;
		} else if (squareColor.equals(W)) {
			return 0;
		} else if (squareColor.equals(B)) {
			return 1;
		} else {
			return -1;
		}
	}
}
