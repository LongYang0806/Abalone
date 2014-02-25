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
	public void setPlayerState(List<ImmutableList<Square>> board, AbaloneMessage message);
	
	/**
	 * Ask the player to choose the next piece to jump.
	 * We pass the update {@code board} from presenter, and the {@code previousJumps} which is
	 * used to highlight the previous jumps made by this player and it helps him to cancel 
	 * previous jumps or make the further jumps (by calling {@link AbalonePresenter#pieceJumped()}).
	 * 
	 * @param board input updated board from presenter
	 * @param previousJumps input previous jumps made by this player.
	 * @param message input message used to notify player the status of the game.
	 */
	public void nextPieceJump(List<ImmutableList<Square>> board, List<Jump> previousJumps, 
			AbaloneMessage message);
	
	/**
	 * After player makes all the jumps, he/she will call 
	 * {@link AbalonePresenter#finishedJumpingPieces()} to finish the jumps, and it will call this 
	 * method to update graphics with the updated board.
	 * @param board input updated board.
	 * @param previousJumps input previous jumps made by the player.
	 * @param message input message used to notify player the status of the game.
	 */
	public void finishMoves(List<ImmutableList<Square>> board, List<Jump> previousJumps, 
			AbaloneMessage message);
}
