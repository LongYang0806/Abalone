package com.longyang.abalone.test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.longyang.abalone.api.AbaloneMessage;
import com.longyang.abalone.api.GameApi;
import com.longyang.abalone.api.GameApi.Container;
import com.longyang.abalone.api.GameApi.Operation;
import com.longyang.abalone.api.GameApi.SetTurn;
import com.longyang.abalone.api.GameApi.UpdateUI;
import com.longyang.abalone.api.Jump;
import com.longyang.abalone.api.Square;
import com.longyang.abalone.api.View;
import com.longyang.abalone.impl.AbaloneLogic;
import com.longyang.abalone.impl.AbalonePresenter;
import com.longyang.abalone.impl.AbaloneUtilities;

@RunWith(JUnit4.class)
public class AbalonePresenterTest {
	private AbalonePresenter abalonePresenter;
	private View mockView;
	private Container mockContainer;
	private final AbaloneLogic abaloneLogic= new AbaloneLogic();
	
	private static final String PLAYER_ID = "playerId";
	private static final String BOARD = "board";
	private static final String JUMP = "jump";
	private static final String W = "White Piece";
	private static final String B = "Black Piece";
	private static final String E = "Empty Square";
	private static final String S = "Score Square";
	private static final String I = "Illegal Square";
	private final int viewerId = GameApi.VIEWER_ID;
	private final int wId = 1;
	private final int bId = 2;
	private final ImmutableMap<String, Object> wInfo = ImmutableMap.<String, Object>of(PLAYER_ID, wId);
	private final ImmutableMap<String, Object> bInfo = ImmutableMap.<String, Object>of(PLAYER_ID, bId);
	private final ImmutableList<Map<String, Object>> playersInfo = 
			ImmutableList.<Map<String, Object>>of(wInfo, bInfo);
	private final ImmutableList<Integer> playerIds = ImmutableList.of(wId, bId);
	private final ImmutableMap<String, Object> emptyState = ImmutableMap.<String, Object>of();
  
  /*
   * The entities used in Abalone game are as following (in both move and state, and fixed order):
   * TURN: WP/BP, 
   * BOARD: a list of list => stand for a 11 X 19 board
   * MOVE: a list of list operation => (a, b, c, d) stands for piece jump 
   * from (a, b) to (c, d)
   */
  private final List<ImmutableList<String>> initialBoard = 
  		ImmutableList.<ImmutableList<String>>of(
  				ImmutableList.<String>of(I, I, I, I, S, I, S, I, S, I, S, I, S, I, S, I, I, I, I),
  				ImmutableList.<String>of(I, I, I, S, S, W, I, W, I, E, I, B, I, B, S, S, I, I, I),
  				ImmutableList.<String>of(I, I, S, S, W, I, W, I, W, I, B, I, B, I, B, S, S, I, I),
  				ImmutableList.<String>of(I, S, S, E, I, W, I, W, I, E, I, B, I, B, E, I, S, S, I),
  				ImmutableList.<String>of(S, S, E, I, E, I, E, I, E, I, E, I, E, I, E, I, E, S, S),
  				ImmutableList.<String>of(S, E, I, E, I, E, I, E, I, E, I, E, I, E, I, E, I, E, S),
  				ImmutableList.<String>of(S, S, E, I, E, I, E, I, E, I, E, I, E, I, E, I, E, S, S),
  				ImmutableList.<String>of(I, S, S, E, I, B, I, B, I, E, I, W, I, W, E, I, S, S, I),
  				ImmutableList.<String>of(I, I, S, S, B, I, B, I, B, I, W, I, W, I, W, S, S, I, I),
  				ImmutableList.<String>of(I, I, I, S, S, B, I, B, I, E, I, W, I, W, S, S, I, I, I),
  				ImmutableList.<String>of(I, I, I, I, S, I, S, I, S, I, S, I, S, I, S, I, I, I, I)
  		);
  private final List<ImmutableList<String>> specialBoard = 
  		ImmutableList.<ImmutableList<String>>of(
  				ImmutableList.<String>of(I, I, I, I, S, I, S, I, S, I, S, I, S, I, S, I, I, I, I),
  				ImmutableList.<String>of(I, I, I, S, S, E, I, B, I, W, I, B, I, E, S, S, I, I, I),
  				ImmutableList.<String>of(I, I, S, S, E, I, W, I, W, I, W, I, W, I, E, S, S, I, I),
  				ImmutableList.<String>of(I, S, S, W, I, W, I, W, I, B, I, B, I, B, I, E, S, S, I),
  				ImmutableList.<String>of(S, S, E, I, E, I, B, I, B, I, B, I, W, I, W, I, W, S, S),
  				ImmutableList.<String>of(S, E, I, E, I, E, I, E, I, E, I, E, I, B, I, E, I, E, S),
  				ImmutableList.<String>of(S, S, E, I, E, I, W, I, E, I, E, I, B, I, E, I, E, S, S),
  				ImmutableList.<String>of(I, S, S, E, I, B, I, E, I, E, I, E, I, E, I, E, S, S, I),
  				ImmutableList.<String>of(I, I, S, S, E, I, E, I, W, I, W, I, B, I, B, S, S, I, I),
  				ImmutableList.<String>of(I, I, I, S, S, B, I, W, I, E, I, E, I, E, S, S, I, I, I),
  				ImmutableList.<String>of(I, I, I, I, S, I, S, I, S, I, S, I, S, I, S, I, I, I, I)
  		);
  private final Map<String, Object> stateAfterInitial = ImmutableMap.<String, Object>builder()
  		.put(BOARD, initialBoard)
  		.put(JUMP, ImmutableList.<ImmutableList<Integer>>of())
  		.build();
	private final Map<String, Object> specialBoardState = ImmutableMap.<String, Object>builder()
  		.put(BOARD, specialBoard)
  		.put(JUMP, ImmutableList.<ImmutableList<Integer>>of())
  		.build();
	
	/**
	 * Helper class used to create a new updateUI object, and this is borrowed from Professor's code.
	 * @param yourPlayerId input {@code yourPlayerId}
	 * @param turnOfPlayerId input current turn's palyerId
	 * @param state current state
	 * @return a {@link UpdateUI} object
	 */
	private UpdateUI createUpdateUI(
      int yourPlayerId, int turnOfPlayerId, Map<String, Object> state) {
    // Our UI only looks at the current state
    // (we ignore: lastState, lastMovePlayerId, playerIdToNumberOfTokensInPot)
    return new UpdateUI(yourPlayerId, playersInfo, state,
        emptyState, // we ignore lastState
        ImmutableList.<Operation>of(new SetTurn(turnOfPlayerId)),
        0,
        ImmutableMap.<Integer, Integer>of());
  }
	
	@Before
	public void runBefore(){
		mockView = Mockito.mock(View.class);
    mockContainer = Mockito.mock(Container.class);
    abalonePresenter = new AbalonePresenter(mockView, mockContainer);
    verify(mockView).setPresenter(abalonePresenter);
	}
	
	@After
	public void runAfter(){
		verifyNoMoreInteractions(mockContainer);
    verifyNoMoreInteractions(mockView);
	}
	
	@Test
	public void testEmptyStateForWP(){
		abalonePresenter.updateUI(createUpdateUI(wId, wId, emptyState));
		verify(mockContainer).sendMakeMove(abaloneLogic.getInitialMove(playerIds));
	}
	
	@Test
	public void testEmptyStateForBP(){
		abalonePresenter.updateUI(createUpdateUI(bId, 0, emptyState));
	}
	
	@Test
	public void testEmptyStateForViewer(){
		abalonePresenter.updateUI(createUpdateUI(viewerId, 0, emptyState));
	}
	
	@Test
	public void testInitialStateForViewer(){
		abalonePresenter.updateUI(createUpdateUI(viewerId, wId, stateAfterInitial));
		verify(mockView).setPlayerState(AbaloneUtilities.stringBoardToSquareBoard(initialBoard), 
				AbaloneMessage.UNDERGOING);
	}
	
	@Test
	public void testInitialStateForWP(){
		abalonePresenter.updateUI(createUpdateUI(wId, wId, emptyState));
		verify(mockContainer).sendMakeMove(abaloneLogic.getInitialMove(playerIds));
		abalonePresenter.updateUI(createUpdateUI(wId, wId, stateAfterInitial));
		verify(mockView).setPlayerState(AbaloneUtilities.stringBoardToSquareBoard(initialBoard),
				AbaloneMessage.UNDERGOING);
		verify(mockView).nextPieceJump(AbaloneUtilities.stringBoardToSquareBoard(initialBoard),
				ImmutableList.<Jump>of(), AbaloneMessage.UNDERGOING);
	}
	
	@Test 
	public void testInitialStateForBP(){
		abalonePresenter.updateUI(createUpdateUI(bId, bId, stateAfterInitial));
		verify(mockView).setPlayerState(AbaloneUtilities.stringBoardToSquareBoard(initialBoard),
				AbaloneMessage.UNDERGOING);
		verify(mockView).nextPieceJump(AbaloneUtilities.stringBoardToSquareBoard(initialBoard),
				ImmutableList.<Jump>of(), AbaloneMessage.UNDERGOING);
	}
	
	@Test
	public void testJumpsByWP(){
		abalonePresenter.updateUI(createUpdateUI(wId, wId, stateAfterInitial));
		verify(mockView).setPlayerState(AbaloneUtilities.stringBoardToSquareBoard(initialBoard),
				AbaloneMessage.UNDERGOING);
		verify(mockView).nextPieceJump(AbaloneUtilities.stringBoardToSquareBoard(initialBoard), 
				ImmutableList.<Jump>of(), AbaloneMessage.UNDERGOING);
		assertEquals(abalonePresenter.getJumps().size(), 0);
		
		abalonePresenter.pieceJumped(7, 11, 6, 12);
		Jump firstJump = new Jump(7, 11, 6, 12);
		assertEquals(abalonePresenter.getJumps(), ImmutableList.<Jump>of(firstJump));
		List<ImmutableList<Square>> boardAfterFirstJump = AbaloneUtilities.boardAppliedJumps(
						AbaloneUtilities.stringBoardToSquareBoard(initialBoard), 
						ImmutableList.<Jump>of(firstJump));
		verify(mockView).nextPieceJump(boardAfterFirstJump, 
				ImmutableList.<Jump>of(firstJump), AbaloneMessage.UNDERGOING);
	}
	
	@Test
	public void testJumpByBP(){
		abalonePresenter.updateUI(createUpdateUI(bId, bId, stateAfterInitial));
		verify(mockView).setPlayerState(AbaloneUtilities.stringBoardToSquareBoard(initialBoard),
				AbaloneMessage.UNDERGOING);
		verify(mockView).nextPieceJump(AbaloneUtilities.stringBoardToSquareBoard(initialBoard), 
				ImmutableList.<Jump>of(), AbaloneMessage.UNDERGOING);
		assertEquals(abalonePresenter.getJumps().size(), 0);
		
		abalonePresenter.pieceJumped(7, 7, 7, 9);
		Jump firstJump = new Jump(7, 7, 7, 9);
		assertEquals(abalonePresenter.getJumps(), ImmutableList.<Jump>of(firstJump));
		List<ImmutableList<Square>> boardAfterFirstJump = AbaloneUtilities.boardAppliedJumps(
						AbaloneUtilities.stringBoardToSquareBoard(initialBoard), 
						ImmutableList.<Jump>of(firstJump));
		verify(mockView, times(2)).nextPieceJump(boardAfterFirstJump, 
				ImmutableList.<Jump>of(firstJump), AbaloneMessage.UNDERGOING);
	}
	
	@Test
	public void testFinished1JumpByWP(){
		abalonePresenter.updateUI(createUpdateUI(wId, wId, stateAfterInitial));
		verify(mockView).setPlayerState(AbaloneUtilities.stringBoardToSquareBoard(initialBoard),
				AbaloneMessage.UNDERGOING);
		verify(mockView).nextPieceJump(AbaloneUtilities.stringBoardToSquareBoard(initialBoard), 
				ImmutableList.<Jump>of(), AbaloneMessage.UNDERGOING);
		assertEquals(abalonePresenter.getJumps().size(), 0);
		
		abalonePresenter.pieceJumped(7, 11, 6, 12);
		Jump firstJump = new Jump(7, 11, 6, 12);
		assertEquals(abalonePresenter.getJumps(), ImmutableList.<Jump>of(firstJump));
		List<ImmutableList<Square>> boardAfterFirstJump = AbaloneUtilities.boardAppliedJumps(
						AbaloneUtilities.stringBoardToSquareBoard(initialBoard), 
						ImmutableList.<Jump>of(firstJump));
		verify(mockView).nextPieceJump(boardAfterFirstJump, 
				ImmutableList.<Jump>of(firstJump), AbaloneMessage.UNDERGOING);
		
		abalonePresenter.finishedJumpingPieces();
		verify(mockView).finishMoves(boardAfterFirstJump, 
				ImmutableList.<Jump>of(firstJump), AbaloneMessage.UNDERGOING);
		verify(mockContainer).sendMakeMove(
				AbaloneUtilities.getMoves(AbaloneUtilities.squareBoardToStringBoard(boardAfterFirstJump), 
						Jump.listJumpToListInteger(ImmutableList.<Jump>of(firstJump)), bId, false));
	}
	
	@Test
	public void testFinished2JumpsByWPNoPush(){
		abalonePresenter.updateUI(createUpdateUI(wId, wId, stateAfterInitial));
		verify(mockView).setPlayerState(AbaloneUtilities.stringBoardToSquareBoard(initialBoard),
				AbaloneMessage.UNDERGOING);
		verify(mockView).nextPieceJump(AbaloneUtilities.stringBoardToSquareBoard(initialBoard), 
				ImmutableList.<Jump>of(), AbaloneMessage.UNDERGOING);
		assertEquals(abalonePresenter.getJumps().size(), 0);
		
		abalonePresenter.pieceJumped(7, 11, 6, 12);
		Jump firstJump = new Jump(7, 11, 6, 12);
		assertEquals(abalonePresenter.getJumps(), ImmutableList.<Jump>of(firstJump));
		List<ImmutableList<Square>> boardAfterFirstJump = AbaloneUtilities.boardAppliedJumps(
						AbaloneUtilities.stringBoardToSquareBoard(initialBoard), 
						ImmutableList.<Jump>of(firstJump));
		verify(mockView).nextPieceJump(boardAfterFirstJump, 
				ImmutableList.<Jump>of(firstJump), AbaloneMessage.UNDERGOING);
		
		abalonePresenter.pieceJumped(8, 10, 7, 11);
		Jump secondJump = new Jump(8, 10, 7, 11);
		assertEquals(abalonePresenter.getJumps(), ImmutableList.<Jump>of(firstJump, secondJump));
		List<ImmutableList<Square>> boardAfterSecondJump = AbaloneUtilities.boardAppliedJumps(
						AbaloneUtilities.stringBoardToSquareBoard(initialBoard), 
						ImmutableList.<Jump>of(firstJump, secondJump));
		verify(mockView).nextPieceJump(boardAfterSecondJump, 
				ImmutableList.<Jump>of(firstJump, secondJump), AbaloneMessage.UNDERGOING);
		
		abalonePresenter.finishedJumpingPieces();
		verify(mockView).finishMoves(boardAfterSecondJump, 
				ImmutableList.<Jump>of(firstJump, secondJump), AbaloneMessage.UNDERGOING);
		verify(mockContainer).sendMakeMove(
				AbaloneUtilities.getMoves(AbaloneUtilities.squareBoardToStringBoard(boardAfterSecondJump), 
						Jump.listJumpToListInteger(ImmutableList.<Jump>of(firstJump, secondJump)), bId, false));
	}
	
	@Test
	public void testFinished2JumpsWithPushes(){
		abalonePresenter.updateUI(createUpdateUI(bId, bId, specialBoardState));
		verify(mockView).setPlayerState(AbaloneUtilities.stringBoardToSquareBoard(specialBoard),
				AbaloneMessage.UNDERGOING);
		verify(mockView).nextPieceJump(AbaloneUtilities.stringBoardToSquareBoard(specialBoard), 
				ImmutableList.<Jump>of(), AbaloneMessage.UNDERGOING);
		assertEquals(abalonePresenter.getJumps().size(), 0);
		
		abalonePresenter.pieceJumped(3, 11, 2, 12);
		Jump firstJump = new Jump(3, 11, 2, 12);
		assertEquals(abalonePresenter.getJumps(), ImmutableList.<Jump>of(firstJump));
		List<ImmutableList<Square>> boardAfterFirstJump = AbaloneUtilities.boardAppliedJumps(
						AbaloneUtilities.stringBoardToSquareBoard(specialBoard), 
						ImmutableList.<Jump>of(firstJump));
		verify(mockView, times(2)).nextPieceJump(boardAfterFirstJump, 
				ImmutableList.<Jump>of(firstJump), AbaloneMessage.UNDERGOING);
		
		abalonePresenter.pieceJumped(4, 10, 3, 11);
		Jump secondJump = new Jump(4, 10, 3, 11);
		assertEquals(abalonePresenter.getJumps(), ImmutableList.<Jump>of(firstJump, secondJump));
		List<ImmutableList<Square>> boardAfterSecondJump = AbaloneUtilities.boardAppliedJumps(
						AbaloneUtilities.stringBoardToSquareBoard(specialBoard), 
						ImmutableList.<Jump>of(firstJump, secondJump));
		verify(mockView, times(3)).nextPieceJump(boardAfterSecondJump, 
				ImmutableList.<Jump>of(firstJump, secondJump), AbaloneMessage.UNDERGOING);
		
		abalonePresenter.finishedJumpingPieces();
		Jump thirdJump = new Jump(2, 12, 1, 13);
		List<ImmutableList<Square>> boardAfterThirdJump = AbaloneUtilities.boardAppliedJumps(
				AbaloneUtilities.stringBoardToSquareBoard(specialBoard), 
				ImmutableList.<Jump>of(firstJump, secondJump, thirdJump));
		verify(mockView).finishMoves(boardAfterThirdJump, 
				ImmutableList.<Jump>of(firstJump, secondJump, thirdJump), AbaloneMessage.UNDERGOING);
		verify(mockContainer).sendMakeMove(
				AbaloneUtilities.getMoves(AbaloneUtilities.squareBoardToStringBoard(boardAfterThirdJump), 
						Jump.listJumpToListInteger(ImmutableList.<Jump>of(firstJump, secondJump, thirdJump)), 
						wId, false));
	}
	
	@Test
	public void testFinished3JumpsWithPushes(){
		abalonePresenter.updateUI(createUpdateUI(wId, wId, specialBoardState));
		verify(mockView).setPlayerState(AbaloneUtilities.stringBoardToSquareBoard(specialBoard),
				AbaloneMessage.UNDERGOING);
		verify(mockView).nextPieceJump(AbaloneUtilities.stringBoardToSquareBoard(specialBoard), 
				ImmutableList.<Jump>of(), AbaloneMessage.UNDERGOING);
		assertEquals(abalonePresenter.getJumps().size(), 0);
		
		abalonePresenter.pieceJumped(3, 7, 4, 6);
		Jump firstJump = new Jump(3, 7, 4, 6);
		assertEquals(abalonePresenter.getJumps(), ImmutableList.<Jump>of(firstJump));
		List<ImmutableList<Square>> boardAfterFirstJump = AbaloneUtilities.boardAppliedJumps(
						AbaloneUtilities.stringBoardToSquareBoard(specialBoard), 
						ImmutableList.<Jump>of(firstJump));
		verify(mockView).nextPieceJump(boardAfterFirstJump, 
				ImmutableList.<Jump>of(firstJump), AbaloneMessage.UNDERGOING);
		
		abalonePresenter.pieceJumped(2, 8, 3, 7);
		Jump secondJump = new Jump(2, 8, 3, 7);
		assertEquals(abalonePresenter.getJumps(), ImmutableList.<Jump>of(firstJump, secondJump));
		List<ImmutableList<Square>> boardAfterSecondJump = AbaloneUtilities.boardAppliedJumps(
						AbaloneUtilities.stringBoardToSquareBoard(specialBoard), 
						ImmutableList.<Jump>of(firstJump, secondJump));
		verify(mockView).nextPieceJump(boardAfterSecondJump, 
				ImmutableList.<Jump>of(firstJump, secondJump), AbaloneMessage.UNDERGOING);

		abalonePresenter.pieceJumped(1, 9, 2, 8);
		Jump thirdJump = new Jump(1, 9, 2, 8);
		assertEquals(abalonePresenter.getJumps(), ImmutableList.<Jump>of(firstJump, secondJump, thirdJump));
		List<ImmutableList<Square>> boardAfterThirdJump = AbaloneUtilities.boardAppliedJumps(
						AbaloneUtilities.stringBoardToSquareBoard(specialBoard), 
						ImmutableList.<Jump>of(firstJump, secondJump, thirdJump));
		verify(mockView).nextPieceJump(boardAfterThirdJump, 
				ImmutableList.<Jump>of(firstJump, secondJump, thirdJump), AbaloneMessage.UNDERGOING);
		
		abalonePresenter.finishedJumpingPieces();
		Jump fourthJump = new Jump(4, 6, 5, 5);
		List<ImmutableList<Square>> boardAfterFourthJump = AbaloneUtilities.boardAppliedJumps(
				AbaloneUtilities.stringBoardToSquareBoard(specialBoard), 
				ImmutableList.<Jump>of(firstJump, secondJump, thirdJump, fourthJump));
		verify(mockView).finishMoves(boardAfterFourthJump, 
				ImmutableList.<Jump>of(thirdJump, secondJump, firstJump, fourthJump), AbaloneMessage.UNDERGOING);
		verify(mockContainer).sendMakeMove(
				AbaloneUtilities.getMoves(AbaloneUtilities.squareBoardToStringBoard(boardAfterFourthJump), 
						Jump.listJumpToListInteger(ImmutableList.<Jump>of(thirdJump, secondJump, 
								firstJump, fourthJump)), bId, false));
	}
	
	@Test
	public void testFinished2JumpsWithPushAndWin(){
		abalonePresenter.updateUI(createUpdateUI(bId, bId, specialBoardState));
		verify(mockView).setPlayerState(AbaloneUtilities.stringBoardToSquareBoard(specialBoard),
				AbaloneMessage.UNDERGOING);
		verify(mockView).nextPieceJump(AbaloneUtilities.stringBoardToSquareBoard(specialBoard), 
				ImmutableList.<Jump>of(), AbaloneMessage.UNDERGOING);
		assertEquals(abalonePresenter.getJumps().size(), 0);
		
		abalonePresenter.pieceJumped(2, 6, 1, 7);
		Jump firstJump = new Jump(2, 6, 1, 7);
		assertEquals(abalonePresenter.getJumps(), ImmutableList.<Jump>of(firstJump));
		List<ImmutableList<Square>> boardAfterFirstJump = AbaloneUtilities.boardAppliedJumps(
						AbaloneUtilities.stringBoardToSquareBoard(specialBoard), 
						ImmutableList.<Jump>of(firstJump));
		verify(mockView).nextPieceJump(boardAfterFirstJump, 
				ImmutableList.<Jump>of(firstJump), AbaloneMessage.UNDERGOING);
		
		abalonePresenter.pieceJumped(3, 5, 2, 6);
		Jump secondJump = new Jump(3, 5, 2, 6);
		assertEquals(abalonePresenter.getJumps(), ImmutableList.<Jump>of(firstJump, secondJump));
		List<ImmutableList<Square>> boardAfterSecondJump = AbaloneUtilities.boardAppliedJumps(
						AbaloneUtilities.stringBoardToSquareBoard(specialBoard), 
						ImmutableList.<Jump>of(firstJump, secondJump));
		verify(mockView).nextPieceJump(boardAfterSecondJump, 
				ImmutableList.<Jump>of(firstJump, secondJump), AbaloneMessage.UNDERGOING);
		
		abalonePresenter.finishedJumpingPieces();
		Jump thirdJump = new Jump(1, 7, 0, 8);
		List<ImmutableList<Square>> boardAfterThirdJump = AbaloneUtilities.boardAppliedJumps(
				AbaloneUtilities.stringBoardToSquareBoard(specialBoard), 
				ImmutableList.<Jump>of(thirdJump, firstJump, secondJump));
		verify(mockView).finishMoves(boardAfterThirdJump, 
				ImmutableList.<Jump>of(firstJump, secondJump, thirdJump), AbaloneMessage.GAMEOVER);
		verify(mockContainer).sendMakeMove(
				AbaloneUtilities.getMoves(AbaloneUtilities.squareBoardToStringBoard(boardAfterThirdJump), 
						Jump.listJumpToListInteger(ImmutableList.<Jump>of(firstJump, secondJump, thirdJump)),
						wId, true));
	}
	
}
