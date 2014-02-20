package com.longyang.abalone.impl;

import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableList;
import com.longyang.abalone.api.AbaloneConstants;
import com.longyang.abalone.api.GameApi.Operation;
import com.longyang.abalone.api.GameApi.Set;
import com.longyang.abalone.api.GameApi.SetTurn;
import com.longyang.abalone.api.GameApi.VerifyMove;
import com.longyang.abalone.api.GameApi.VerifyMoveDone;
import com.longyang.abalone.api.Square;
import com.longyang.abalone.api.Turn;

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
  	List<Integer> playerIds = verifyMove.getPlayerIds();
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
		List<ImmutableList<ImmutableList<Integer>>> lastJump = (List<ImmutableList<ImmutableList<Integer>>>)
  			((Set)lastMove.get(2)).getValue();
  	AbaloneState resultAbaloneState = abaloneState.applyJumpOnBoard(lastJump);
  	// check whether board is the same.
  	@SuppressWarnings("unchecked")
		List<ImmutableList<Square>> lastMoveBoard = AbaloneUtilities.stringBoardToSquareBoard(
  			(List<ImmutableList<String>>)((Set)lastMove.get(1)).getValue());
  	AbaloneUtilities.check(resultAbaloneState.getBoard().equals(lastMoveBoard));
  	AbaloneUtilities.check(
  			(lastMove.size() == 3 && resultAbaloneState.getIsGameEnd() == null) ||
  			(lastMove.size() == 4 && resultAbaloneState.getIsGameEnd().get() == true));
  }

	public List<Operation> getInitialMove(int wId, int bId) {
		return ImmutableList.<Operation>of(
				new SetTurn(wId),
				new Set(AbaloneConstants.BOARD, AbaloneConstants.initialBoard),
				new Set(AbaloneConstants.JUMP, ImmutableList.<Operation>of())
				);
	}

}
