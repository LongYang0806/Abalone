package com.longyang.abalone.impl;

import java.util.List;

import com.google.common.base.Optional;
import com.longyang.abalone.api.GameApi.Container;
import com.longyang.abalone.api.GameApi.UpdateUI;
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
	
	private final AbaloneLogic abaloneLogic = new AbaloneLogic();
	private final View view;
	private final Container container;
	/* A viewer does not have turn */
	private Optional<Turn> turn;
	private AbaloneState abaloneState;
	/* List used to store all the moves for this player's move, and it
	 * should have the following format (list.size <= 3):
	 * {
	 * 		{orig_x, orig_y, dest_x, dest_y},
	 * 		...
	 * 		{orig_x, orig_y, dest_x, dest_y}
	 * } 
	 */
	private List<Integer> jumps;
	
	public AbalonePresenter(View view, Container container){
		this.view = view;
		this.container = container;
		view.setPresenter(this);
	}
	
	public void updateUI(UpdateUI updateUI){
		
	}
	
	public void pieceJumped(int orig_x, int orig_y, int dest_x, int dest_y){
		
	}
	
	/**
	 * Method called by {@link View}, which is used for player to tell backend 
	 * that I have finished my jumps, and you should decide which oppoent's 
	 * pieces should be pushed.
	 * 
	 * And it should also call {@link View#finishMoves(AbaloneState)} to finish
	 * the whole circle.
	 */
	public void finishedJumpingPieces(){
		
	}

}
