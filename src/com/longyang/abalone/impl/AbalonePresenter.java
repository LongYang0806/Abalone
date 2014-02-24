package com.longyang.abalone.impl;

import java.util.LinkedList;
import java.util.List;

import com.google.common.base.Optional;
import com.longyang.abalone.api.GameApi.Container;
import com.longyang.abalone.api.GameApi.UpdateUI;
import com.longyang.abalone.api.Jump;
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
	private List<Jump> jumps;
	
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
	
	public void updateUI(UpdateUI updateUI){
		List<Integer> playerIds = updateUI.getPlayerIds();
		int yourPlayerId = updateUI.getYourPlayerId();
		int yourPlayerIndex = updateUI.getYourPlayerIndex();
		turn = 
				yourPlayerIndex == 0 ? Optional.<Turn>of(Turn.WP) : 
				yourPlayerIndex == 1 ? Optional.<Turn>of(Turn.BP) : 
				Optional.<Turn>absent(); // for the viewer.
		/*
		 * The reason for choose {@link LinkedList} other than {@link ArrayList}:
		 * player can cancel the previous jumps, so you need a data structure which 
		 * supports the random delete in random position of the list.
		 */
		jumps = new LinkedList<Jump>();
		
			
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
