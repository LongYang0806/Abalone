package com.longyang.abalone.impl;

import java.util.List;

import com.google.common.collect.ImmutableList;
import com.longyang.abalone.api.AbaloneConstants;
import com.longyang.abalone.api.GameApi.Operation;
import com.longyang.abalone.api.GameApi.Set;
import com.longyang.abalone.api.GameApi.SetTurn;
import com.longyang.abalone.api.GameApi.VerifyMove;
import com.longyang.abalone.api.GameApi.VerifyMoveDone;

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
    }
  }

	public List<Operation> getInitialMove(int wId, int bId) {
		return ImmutableList.<Operation>of(
				new SetTurn(wId),
				new Set(AbaloneConstants.BOARD, AbaloneConstants.initialBoard),
				new Set(AbaloneConstants.JUMP, ImmutableList.<Operation>of())
				);
	}

}
