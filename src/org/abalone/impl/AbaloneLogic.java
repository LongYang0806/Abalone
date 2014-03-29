package org.abalone.impl;

import java.util.List;
import java.util.Map;

import org.abalone.api.AbaloneConstants;
import org.abalone.api.Jump;
import org.abalone.api.Square;
import org.abalone.api.Turn;
import org.game_api.GameApi.Operation;
import org.game_api.GameApi.Set;
import org.game_api.GameApi.SetTurn;
import org.game_api.GameApi.VerifyMove;
import org.game_api.GameApi.VerifyMoveDone;

import com.google.common.collect.ImmutableList;

public class AbaloneLogic {
	
	public VerifyMoveDone verify(VerifyMove verifyMove) {
    try {
      checkMoveIsLegal(verifyMove);
      return new VerifyMoveDone();
    } catch (Exception e) {
      return new VerifyMoveDone(verifyMove.getLastMovePlayerId(), e.getMessage());
    }
  }

  void checkMoveIsLegal(VerifyMove verifyMove) {
  	// We use SetTurn, so we don't need to check that the correct player did the move.
    // However, we do need to check the first move is done by the white player (and then in the
    // first MakeMove we'll send SetTurn which will guarantee the correct player send MakeMove).
    if (verifyMove.getLastState().isEmpty()) {
      AbaloneUtilities.check(verifyMove.getLastMovePlayerId() == verifyMove.getPlayerIds().get(0));
      AbaloneUtilities.check(verifyMove.getLastMove().size() == 3);
      return;
    }
    
  	Map<String, Object> lastState = verifyMove.getLastState();
  	List<Operation> lastMove = verifyMove.getLastMove();
  	List<String> playerIds = verifyMove.getPlayerIds();
  	Turn turn = Turn.values()[playerIds.indexOf(verifyMove.getLastMovePlayerId())];
  	AbaloneState abaloneState = 
  			AbaloneUtilities.gameApiStateToAbaloneState(lastState, turn, playerIds);
  	@SuppressWarnings("unchecked")
  	/*
  	 * Structure of lastMove:
  	 * 	SetTurn
  	 * 	Board
  	 * 	Jump
  	 * 	(EndGame)
  	 */
		List<ImmutableList<Integer>> lastJump = (List<ImmutableList<Integer>>)
  			((Set)lastMove.get(2)).getValue();
  	List<ImmutableList<Square>> newBoard = AbaloneUtilities.boardAppliedJumps(
  			abaloneState.getBoard(), Jump.fromIntegerListListToJumpList(lastJump));
  	// check whether board is the same.
  	@SuppressWarnings("unchecked")
		List<ImmutableList<Square>> lastMoveBoard = AbaloneUtilities.stringBoardToSquareBoard(
  			(List<ImmutableList<String>>)((Set)lastMove.get(1)).getValue());
  	System.out.println(newBoard.equals(lastMoveBoard));
  	AbaloneUtilities.check(newBoard.equals(lastMoveBoard), 
  			"board and appliedBoard is not equal");
//  	AbaloneUtilities.check(
//  			(lastMove.size() == 3 && resultAbaloneState.getIsGameEnd() == null) ||
//  			(lastMove.size() == 4 && resultAbaloneState.getIsGameEnd().get() == true));
  }

	public List<Operation> getInitialMove(List<String> playerIds) {
		String whitePlayerId = playerIds.get(0);
		return ImmutableList.<Operation>of(
				new SetTurn(whitePlayerId),
				new Set(AbaloneConstants.BOARD, AbaloneConstants.initialBoard),
				new Set(AbaloneConstants.JUMP, ImmutableList.<Operation>of())
				);
	}

}
