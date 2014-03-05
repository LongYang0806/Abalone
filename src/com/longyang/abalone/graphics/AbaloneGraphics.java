package com.longyang.abalone.graphics;

import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;
import com.longyang.abalone.api.AbaloneMessage;
import com.longyang.abalone.api.Jump;
import com.longyang.abalone.api.Square;
import com.longyang.abalone.api.Turn;
import com.longyang.abalone.api.View;
import com.longyang.abalone.impl.AbalonePresenter;

/**
 * Class used to implement {@link View}
 *
 * @author Long Yang (ly603@nyu.edu)
 *
 */
public class AbaloneGraphics extends Composite implements View {
  public interface AbaloneGraphicsUiBinder extends UiBinder<Widget, AbaloneGraphics> {
  }

  enum ActionType{
  	HOLD, PLACE;
  }
  
  @UiField
  HorizontalPanel row0;
  @UiField
  HorizontalPanel row1;
  @UiField
  HorizontalPanel row2;
  @UiField
  HorizontalPanel row3;
  @UiField
  HorizontalPanel row4;
  @UiField
  HorizontalPanel row5;
  @UiField
  HorizontalPanel row6;
  @UiField
  HorizontalPanel row7;
  @UiField
  HorizontalPanel row8;
  @UiField
  HorizontalPanel row9;
  @UiField
  HorizontalPanel row10;
  @UiField
  HorizontalPanel selectedArea;
  @UiField
  Button finishRoundBtn;
  private AbaloneImages abaloneImages;
  private AbalonePresenter abalonePresenter;
  /**
   * Constructor used to create an AbaloneGraphics object,
   * and this also create {@code abaloneImages}
   * and {#link AbaloneGraphicsUiBinder} and initialize it.
   */
  public AbaloneGraphics(){
  	abaloneImages = GWT.create(AbaloneImages.class);
  	AbaloneGraphicsUiBinder uiBinder = GWT.create(AbaloneGraphicsUiBinder.class);
    initWidget(uiBinder.createAndBindUi(this));
  }
  
  /**
   * Method used to fill {@code panel} with {@link Image}s
   * In addition, it set all the images with the {@code imgSquare} css style.
   * @param panel input {@link HorizontalPanel}
   * @param images input {@link List<Image>} images
   */
  private void placeImages(HorizontalPanel panel, List<Image> images) {
    panel.clear();
    for (Image image : images) {
      FlowPanel imageContainer = new FlowPanel();
      imageContainer.setStyleName("imgSquare");
      imageContainer.add(image);
      panel.add(imageContainer);
    }
  }
  
  @UiHandler("finishRoundBtn")
  void onClickClaimBtn(ClickEvent e) {
    finishRoundBtn.setEnabled(false);
    abalonePresenter.finishedJumpingPieces();
  }
  
	@Override
	public void setPresenter(AbalonePresenter abalonePresenter) {
		this.abalonePresenter = abalonePresenter;
	}

	@Override
	public void setPlayerState(List<ImmutableList<Square>> board, boolean[][] enableMatrix,
			AbaloneMessage message) {
		fillBoard(board, enableMatrix, ActionType.HOLD);
		finishRoundBtn.setEnabled(false);
		if(message == AbaloneMessage.GAMEOVER){
			//TODO should throw out box to say whose is the winner.
		}
	}

	@Override
	public void nextPieceJump(List<ImmutableList<Square>> board,
			List<Jump> previousJumps, AbaloneMessage message) {
		// aborted method.
//		fillBoard(board, AbaloneUtilities.getAllEnableMatrix(board.size(), board.get(0).size(), true));
	}
	
	/**
	 * Method used to fill the board, which also means to place images to the 11 panels.
	 * @param board the input board.
	 * @param enableMatrix 2D boolean matrix to indicate which slots can be click-enabled.
	 */
	private void fillBoard(List<ImmutableList<Square>> board, boolean[][] enableMatrix, 
			ActionType actionType) {
		if(board == null || board.isEmpty()){
			throw new IllegalArgumentException("Input board should not null or empty!");
		}
		for(int i = 0; i < board.size(); i++){
			List<Image> images = squaresToImages(board.get(i), enableMatrix[i], i, actionType);
			switch(i){
				case 0:
					placeImages(row0, images);
					break;
				case 1:
					placeImages(row1, images);
					break;
				case 2:
					placeImages(row2, images);
					break;
				case 3:
					placeImages(row3, images);
					break;
				case 4:
					placeImages(row4, images);
					break;
				case 5:
					placeImages(row5, images);
					break;
				case 6:
					placeImages(row6, images);
					break;
				case 7:
					placeImages(row7, images);
					break;
				case 8:
					placeImages(row8, images);
					break;
				case 9:
					placeImages(row9, images);
					break;
				case 10:
					placeImages(row10, images);
					break;
				default:
					break;
			}
		}
	}

	/**
	 * Help method used to convert {@code List<Square>} to {@code List<Image>}
	 * @param squares input {@code List<Squares>}
	 * @param enableRow boolean array which indicates which slot will be click-enabled.
	 * @param rowNum indicates which row this is.
	 * @return {@code List<Image>}
	 */
	private List<Image> squaresToImages(List<Square> squares, boolean[] enableRow, 
			int rowNum, ActionType actionType){
		ImmutableList.Builder<Image> images = ImmutableList.<Image>builder();
		Image image = null;
		for(int i = 0; i < squares.size(); i++){
			switch(squares.get(i)){
				case B:
					if(enableRow[i]){
						image = new Image(abaloneImages.board_red_highlight());
					}else{
						image = new Image(abaloneImages.board_red());
					}
					break;
				case W:
					if(enableRow[i]){
						image = new Image(abaloneImages.board_white_highlight());
					}else{
						image = new Image(abaloneImages.board_white());
					}
					break;
				case E:
					if(enableRow[i]){
						image = new Image(abaloneImages.board_highlight());
					}else{
						image = new Image(abaloneImages.empty_board());
					}
					break;
				case I:
				case S:
					image = new Image(abaloneImages.illegal_board());
				default:
					break;
			}
			final int row = rowNum;
			final int column = i;
			final ActionType type = actionType;
			if(enableRow[column]){
				image.addClickHandler(new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						if(type == ActionType.HOLD){
							abalonePresenter.pieceHeld(row, column);
						}else if(type == ActionType.PLACE){
							abalonePresenter.piecePlaced(row, column);
						}
          } 
				});
			}
			images.add(image);
		}
		return images.build();
	}

	@Override
	public void toPlacePiece(List<ImmutableList<Square>> board,
			boolean[][] enableMatrix, boolean btnEnable, Turn turn, AbaloneMessage message) {
		fillBoard(board, enableMatrix, ActionType.PLACE);
		finishRoundBtn.setEnabled(btnEnable);
		String winnerMessage = "Game Over and the winner is: " + turn.toString();
		if(message == AbaloneMessage.GAMEOVER){
			List<String> options = Lists.newArrayList("OK");
			new PopupChoices(winnerMessage, options,
	        new PopupChoices.OptionChosen() {
	      @Override
	      public void optionChosen(String option) {
	        if (option.equals("OK")) {
	        }
	      }
	    }).center();
		}
	}

	@Override
	public void toHoldPiece(List<ImmutableList<Square>> board,
			boolean[][] enableSuqare, boolean btnEnable, Turn turn, AbaloneMessage message) {
		fillBoard(board, enableSuqare, ActionType.HOLD);
		finishRoundBtn.setEnabled(btnEnable);
		String winnerMessage = "Game Over and the winner is: " + turn.toString();
		if(message == AbaloneMessage.GAMEOVER){
			List<String> options = Lists.newArrayList("OK");
			new PopupChoices(winnerMessage, options,
	        new PopupChoices.OptionChosen() {
	      @Override
	      public void optionChosen(String option) {
	        if (option.equals("OK")) {
	        }
	      }
	    }).center();
		}
	}
}
