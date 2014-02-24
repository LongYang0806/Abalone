package com.longyang.abalone.api;

import java.util.List;

import com.google.common.collect.ImmutableList;
import com.longyang.abalone.impl.AbalonePresenter;
import com.longyang.abalone.impl.AbaloneState;

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
	
	public void setPresenter(AbalonePresenter abalonePresenter);
	
	/**
	 * Because there is no invisible materials, no need to separate players and viewers.
	 * @param board input board used to display the board on screen.
	 */
	public void setPlayerState(List<ImmutableList<String>> board);
	
	public void nextPieceJump(List<ImmutableList<String>> board);
	
	public void finishMoves(List<ImmutableList<String>> board);
}
