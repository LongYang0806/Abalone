package org.abalone.graphics;

import static org.abalone.client.AbaloneConstants.B;
import static org.abalone.client.AbaloneConstants.BoardColNum;
import static org.abalone.client.AbaloneConstants.BoardRowNum;
import static org.abalone.client.AbaloneConstants.E;
import static org.abalone.client.AbaloneConstants.GAMEOVER;
import static org.abalone.client.AbaloneConstants.W;
import static org.abalone.client.AbaloneConstants.picHight;
import static org.abalone.client.AbaloneConstants.picWidth;

import java.util.ArrayList;
import java.util.List;

import org.abalone.client.AbaloneConstants;
import org.abalone.client.AbalonePresenter;
import org.abalone.client.AbalonePresenter.View;
import org.abalone.i18n.AbaloneMessages;
import org.abalone.sounds.GameSounds;

import com.google.common.collect.Lists;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.AudioElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.media.client.Audio;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

/**
 * Class used to implement {@link View}
 *
 * @author Long Yang (ly603@nyu.edu)
 *
 */
public class AbaloneGraphics extends Composite implements View {
  public interface AbaloneGraphicsUiBinder extends UiBinder<Widget, AbaloneGraphics> {
  }
  
  @UiField
  HorizontalPanel outerBoard;
  @UiField
  Button finishRoundBtn;
  
  private AbaloneMessages abaloneMessages = GWT.create(AbaloneMessages.class);
  private GameSounds gameSounds;
  private AbaloneImages abaloneImages;
  private AbalonePresenter abalonePresenter;
  private AbaloneAbsolutePanel innerBoard;
  private String innerBoardWidth = "525px";
  private String innerBoardHeight = "275px";
  private boolean isGameOver = false;
  private boolean isPieceTurn = true;
  private Image[][] squareImages = new Image[BoardRowNum][BoardColNum];
  private Image[][] pieceImages = new Image[BoardRowNum][BoardColNum];
  private Audio pieceDown;
  private PieceMovingAnimation animation;
  private AbaloneDragController abaloneDragController;
  private AbaloneDropController abaloneDropController;
  
  /**
   * Constructor used to create an AbaloneGraphics object,
   * and this also create {@code abaloneImages}
   * and {#link AbaloneGraphicsUiBinder} and initialize it.
   */
  public AbaloneGraphics(){
  	gameSounds = GWT.create(GameSounds.class);
  	abaloneImages = GWT.create(AbaloneImages.class);
  	AbaloneGraphicsUiBinder uiBinder = GWT.create(AbaloneGraphicsUiBinder.class);
  	innerBoard = new AbaloneAbsolutePanel();
  	innerBoard.setSize(innerBoardWidth, innerBoardHeight);
    initWidget(uiBinder.createAndBindUi(this));
    
    if (Audio.isSupported()) {
      pieceDown = Audio.createIfSupported();
      pieceDown.addSource(gameSounds.pieceDownMp3().getSafeUri()
                      .asString(), AudioElement.TYPE_MP3);
      pieceDown.addSource(gameSounds.pieceDownWav().getSafeUri()
                      .asString(), AudioElement.TYPE_WAV);
    }
    finishRoundBtn.setText(abaloneMessages.finishThisRound());
  }
  
  @UiHandler("finishRoundBtn")
  void onClickClaimBtn(ClickEvent e) {
    finishRoundBtn.setEnabled(false);
    abalonePresenter.finishAllPlacing(isGameOver);
  }
  
	@Override
	public void setPresenter(AbalonePresenter abalonePresenter) {
		this.abalonePresenter = abalonePresenter;
		
		abaloneDragController = new AbaloneDragController(innerBoard, false, abalonePresenter);
    abaloneDragController.setBehaviorConstrainedToBoundaryPanel(true);
    abaloneDragController.setBehaviorMultipleSelection(false);
    abaloneDragController.setBehaviorDragStartSensitivity(1);
	}

	@Override
	public void setPlayerState(List<ArrayList<String>> board, boolean[][] enableMatrix, 
			String message){
		placeBoardWithSquare(board, new boolean[BoardRowNum][BoardColNum]);
		placeBoardWithPieces(board, enableMatrix);
		finishRoundBtn.setEnabled(false);
		if(message.equals(GAMEOVER)){
			//TODO should throw out box to say whose is the winner.
		}
	}
	
	@Override
	public void toHoldOnePiece(List<ArrayList<String>> board, boolean[][] holdableMatrix, 
			boolean enableFinishButton, String turn, String message, List<ArrayList<Integer>> jumps) {
		final List<ArrayList<String>> finalBoard = board;
		final boolean[][] finalHoldableMatrix = holdableMatrix;
		List<ArrayList<String>> boardWithoutJumps = Lists.<ArrayList<String>>newArrayList();
		copyBoardWithJumps(board, boardWithoutJumps, jumps);
		Timer animationTimer = new Timer() {
			@Override
			public void run() {
				placeBoardWithPieces(finalBoard, finalHoldableMatrix);
			}
		};
		placeBoardWithSquare(board, new boolean[BoardRowNum][BoardColNum]);
		animationTimer.schedule(1000);
		placeBoardWithPieces(boardWithoutJumps, holdableMatrix);
		finishRoundBtn.setEnabled(enableFinishButton);
		String winnerMessage = abaloneMessages.gameOver(
				turn.equalsIgnoreCase(AbaloneConstants.WTurn) ? abaloneMessages.whitePlayer() :
					abaloneMessages.redPlayer());
		if(message.equals(GAMEOVER)) {
			isGameOver = true;
			List<String> options = Lists.newArrayList(abaloneMessages.ok());
			abalonePresenter.finishAllPlacing(isGameOver);
			new PopupChoices(winnerMessage, options,
					new PopupChoices.OptionChosen() {
				@Override
				public void optionChosen(String option) {
					if (option.equals(abaloneMessages.ok())) {
					}
				}
			}).center();
		}
	}

	@Override
	public void toPlaceOnePiece(List<ArrayList<String>> board, boolean[][] placableMatrix, 
			boolean enableFinishButton, String turn, String message) {
		placeBoardWithSquare(board, placableMatrix);
		placeBoardWithPieces(board, placableMatrix);
		finishRoundBtn.setEnabled(enableFinishButton);
		String winnerMessage = abaloneMessages.gameOver(
				turn.equalsIgnoreCase(AbaloneConstants.WTurn) ? abaloneMessages.whitePlayer() :
					abaloneMessages.redPlayer());
		if(message.equals(GAMEOVER)){ 
			isGameOver = true;
			List<String> options = Lists.newArrayList(abaloneMessages.ok());
			abalonePresenter.finishAllPlacing(isGameOver);
			new PopupChoices(winnerMessage, options,
					new PopupChoices.OptionChosen() {
				@Override
				public void optionChosen(String option) {
					if (option.equals(abaloneMessages.ok())) {
					}
				}
			}).center();
		}
	}
	
	/**
	 * Method used to place board with square, it required dropable squares to be highlighted.
	 */
	public void placeBoardWithSquare(List<ArrayList<String>> board, boolean[][] enableMatrix) {
		if(board == null || board.isEmpty()) {
			throw new IllegalArgumentException("Input board should not null or empty!");
		}
	
		for(int i = 0; i < BoardRowNum; i++) {
			for(int j = 0; j < BoardColNum; j++) {
				final int row = i;
				final int col = j;
				Image image = getImageBySquare(board.get(i).get(j), enableMatrix[i][j], i, j);
				image.setAltText(row + "," + col);
				if(enableMatrix[row][col]) {
					abaloneDropController = new AbaloneDropController(image, abalonePresenter);
					abaloneDragController.registerDropController(abaloneDropController);
					image.addClickHandler(new ClickHandler() {
						@Override
						public void onClick(ClickEvent event) {
							isPieceTurn = !isPieceTurn;
							abalonePresenter.placedOnePiece(row, col);
	          } 
					});
				}
				image.setStyleName("imgSquare");
				innerBoard.add(image, j * picHight, i * picWidth);
				squareImages[i][j] = image;
			}
		}
		outerBoard.add(innerBoard);
	}
	
	/**
	 * Method used to add pieces to the board, and make the piece dragable or highlighted.
	 */
	public void placeBoardWithPieces(List<ArrayList<String>> board, boolean[][] enableMatrix) {
		if(board == null || board.isEmpty()) {
			throw new IllegalArgumentException("Input board should not null or empty!");
		}
		
		for(int i = 0; i < BoardRowNum; i++) {
			for(int j = 0; j < BoardColNum; j++) {
				boolean isAPiece = false;
				final int row = i;
				final int col = j;
				Image image = new Image();
				ImageResource imageRes = abaloneImages.illegal_board();
				
				if(board.get(i).get(j).equals(W)) {
					isAPiece = true;
					if(enableMatrix[i][j]) {
						imageRes = abaloneImages.white_piece_highlight();
						image = new Image(imageRes);
					} else {
						imageRes = abaloneImages.white_piece();
						image = new Image(imageRes);
					}
				} else if(board.get(i).get(j).equals(B)) {
					isAPiece = true;
					if(enableMatrix[i][j]) {
						imageRes = abaloneImages.red_piece_highlight();
						image = new Image(imageRes);
					} else {
						imageRes = abaloneImages.red_piece();
						image = new Image(imageRes);
					}
				}
				image.setAltText(row + "," + col);
				if(isAPiece) {
					// only when you have the turn, the piece is enabled onClick();
					if(enableMatrix[i][j]) {
						abaloneDragController.makeDraggable(image);
						image.addClickHandler(new ClickHandler() {
							@Override
							public void onClick(ClickEvent event) {
								if(isPieceTurn){
									abalonePresenter.heldOnePiece(row, col);
								} else {
									abalonePresenter.placedOnePiece(row, col);
								}
								isPieceTurn = !isPieceTurn;
		          } 
						});
					}
					image.setStyleName("imgSquare");
					innerBoard.add(image, j * picHight, i * picWidth);
					pieceImages[i][j] = image;
				}
			}
		}
		outerBoard.add(innerBoard);
	}
	
	private Image getImageBySquare(String square, boolean dropable, int x, int y) {
		ImageResource imageRes;
		if (dropable) {
			imageRes = abaloneImages.board_highlight();
		}else if(square.equals(W) || square.equals(B) || square.equals(E)) {
			imageRes = abaloneImages.empty_board();
		} else {
			imageRes = abaloneImages.illegal_board();
		}
		return new Image(imageRes);
	}
	
	@Override
	public void animateMove(int startX, int startY, int endX, int endY, int color) {
		Image startImage = pieceImages[startX][startY];
		Image endImage = squareImages[endX][endY];
		ImageResource startRes;
		ImageResource endRes;
		ImageResource blankRes = abaloneImages.red_piece();
		if(color == 0) {
			startRes = abaloneImages.white_piece();
		} else {
			startRes = abaloneImages.red_piece();
		}
		endRes = abaloneImages.empty_board();
		animation = new PieceMovingAnimation(startImage, endImage, startRes, 
				endRes, blankRes, pieceDown);
		animation.run(1000);
	}
	
	private void copyBoardWithJumps(List<ArrayList<String>> fromList, 
			List<ArrayList<String>> toList, List<ArrayList<Integer>> jumps) {
		// 1. Copy the list from "fromList" to "toList"
		for (List<String> list: fromList) {
			ArrayList<String> copiedList = Lists.<String>newArrayList(list);
			toList.add(copiedList);
		}
		// 2. Set the jumps' destination with "Empty" sign
		if(jumps != null && !jumps.isEmpty()) {
			for (List<Integer> list : jumps) {
				int x = list.get(2);
				int y = list.get(3);
				toList.get(x).set(y, AbaloneConstants.E);
			}
		}
	}
	
}
