package com.longyang.abalone.test;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.longyang.abalone.api.GameApi.EndGame;
import com.longyang.abalone.api.GameApi.Operation;
import com.longyang.abalone.api.GameApi.Set;
import com.longyang.abalone.api.GameApi.SetTurn;
import com.longyang.abalone.api.GameApi.VerifyMove;
import com.longyang.abalone.api.GameApi.VerifyMoveDone;
import com.longyang.abalone.impl.AbaloneLogic;

/**
 * Unit test class used to test Abalone Game.
 * 
 * @author Long Yang (ly603@nyu.edu)
 *
 */
@RunWith(JUnit4.class)
public class AbaloneLogicTest {
	// Abalone logic object used for test.
	private AbaloneLogic abaloneLogic = new AbaloneLogic();
	
	private static final String PLAYER_ID = "playerId";
	private static final String BOARD = "board";
	private static final String JUMP = "jump";
	private static final String W = "White Piece";
	private static final String B = "Black Piece";
	private static final String E = "Empty Square";
	private static final String S = "Score Square";
	private static final String I = "Illegal Square";
	private final int wId = 1;
	private final int bId = 2;
	private final ImmutableMap<String, Object> wInfo = ImmutableMap.<String, Object>of(PLAYER_ID, wId);
	private final ImmutableMap<String, Object> bInfo = ImmutableMap.<String, Object>of(PLAYER_ID, bId);
	private final ImmutableList<Map<String, Object>> playersInfo = 
			ImmutableList.<Map<String, Object>>of(wInfo, bInfo);
	private final ImmutableMap<String, Object> emptyState = ImmutableMap.<String, Object>of();
  private final ImmutableMap<String, Object> nonEmptyState = ImmutableMap.<String, Object>of("k", "v");
  
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
  
  private final List<ImmutableList<String>> boardAfterFirstMoveByWP = 
  		ImmutableList.<ImmutableList<String>>of(
  				ImmutableList.<String>of(I, I, I, I, S, I, S, I, S, I, S, I, S, I, S, I, I, I, I),
  				ImmutableList.<String>of(I, I, I, S, S, W, I, W, I, E, I, B, I, B, S, S, I, I, I),
  				ImmutableList.<String>of(I, I, S, S, W, I, W, I, W, I, B, I, B, I, B, S, S, I, I),
  				ImmutableList.<String>of(I, S, S, E, I, W, I, W, I, E, I, B, I, B, E, I, S, S, I),
  				ImmutableList.<String>of(S, S, E, I, E, I, E, I, E, I, E, I, E, I, E, I, E, S, S),
  				ImmutableList.<String>of(S, E, I, E, I, E, I, E, I, E, I, E, I, E, I, E, I, E, S),
  				ImmutableList.<String>of(S, S, E, I, E, I, E, I, E, I, W, I, E, I, E, I, E, S, S),
  				ImmutableList.<String>of(I, S, S, E, I, B, I, B, I, E, I, W, I, W, E, I, S, S, I),
  				ImmutableList.<String>of(I, I, S, S, B, I, B, I, B, I, W, I, W, I, W, S, S, I, I),
  				ImmutableList.<String>of(I, I, I, S, S, B, I, B, I, E, I, W, I, E, S, S, I, I, I),
  				ImmutableList.<String>of(I, I, I, I, S, I, S, I, S, I, S, I, S, I, S, I, I, I, I)
  		);
  
  private final List<ImmutableList<String>> boardAfterFirstMoveByBP = 
  		ImmutableList.<ImmutableList<String>>of(
  				ImmutableList.<String>of(I, I, I, I, S, I, S, I, S, I, S, I, S, I, S, I, I, I, I),
  				ImmutableList.<String>of(I, I, I, S, S, W, I, W, I, E, I, B, I, B, S, S, I, I, I),
  				ImmutableList.<String>of(I, I, S, S, W, I, W, I, W, I, B, I, B, I, B, S, S, I, I),
  				ImmutableList.<String>of(I, S, S, E, I, W, I, W, I, E, I, B, I, B, E, I, S, S, I),
  				ImmutableList.<String>of(S, S, E, I, E, I, E, I, E, I, E, I, E, I, E, I, E, S, S),
  				ImmutableList.<String>of(S, E, I, E, I, E, I, E, I, E, I, E, I, E, I, E, I, E, S),
  				ImmutableList.<String>of(S, S, E, I, E, I, B, I, E, I, W, I, E, I, E, I, E, S, S),
  				ImmutableList.<String>of(I, S, S, E, I, B, I, B, I, E, I, W, I, W, E, I, S, S, I),
  				ImmutableList.<String>of(I, I, S, S, E, I, B, I, B, I, W, I, W, I, W, S, S, I, I),
  				ImmutableList.<String>of(I, I, I, S, S, B, I, B, I, E, I, W, I, E, S, S, I, I, I),
  				ImmutableList.<String>of(I, I, I, I, S, I, S, I, S, I, S, I, S, I, S, I, I, I, I)
  		);
  
  private final List<ImmutableList<ImmutableList<Integer>>> firstJumpByWP = 
			ImmutableList.<ImmutableList<ImmutableList<Integer>>>of(
					// List for WP's pieces' moves
					ImmutableList.<ImmutableList<Integer>>of(
							ImmutableList.<Integer>of(7, 11, 6, 10),
							ImmutableList.<Integer>of(8, 12, 7, 11),
							ImmutableList.<Integer>of(9, 13, 8, 12)
					),
					// List for BP pieces' moves
					ImmutableList.<ImmutableList<Integer>>of(
					)
			);
  
  private final List<ImmutableList<ImmutableList<Integer>>> firstJumpByBP = 
			ImmutableList.<ImmutableList<ImmutableList<Integer>>>of(
					// List for WP's pieces' moves
					ImmutableList.<ImmutableList<Integer>>of(
					),
					// List for BP pieces' moves
					ImmutableList.<ImmutableList<Integer>>of(
							ImmutableList.<Integer>of(7, 5, 6, 6),
							ImmutableList.<Integer>of(8, 4, 7, 5)
					)
			);
  
  private final Map<String, Object> stateAfterInitialization = ImmutableMap.<String, Object>builder()
  		.put(BOARD, initialBoard)
  		.put(JUMP, ImmutableList.<Integer>of())
  		.build();
  
  private final Map<String, Object> stateAfterFirstWPMove = ImmutableMap.<String, Object>builder()
  		.put(BOARD, boardAfterFirstMoveByWP)
  		.put(JUMP, firstJumpByWP)
  		.build();
  
  private final Map<String, Object> stateAfterFirstBPMove = ImmutableMap.<String, Object>builder()
  		.put(BOARD, boardAfterFirstMoveByBP)
  		.put(JUMP, firstJumpByWP)
  		.build();
  
	private final List<Operation> firstMoveByWP = ImmutableList.<Operation>of(
			new SetTurn(bId),
			new Set(BOARD, boardAfterFirstMoveByWP),
			new Set(JUMP, firstJumpByWP));
	
	private final List<Operation> firstMoveByBP = ImmutableList.<Operation>of(
			new SetTurn(wId),
			new Set(BOARD, boardAfterFirstMoveByBP),
			new Set(JUMP, firstJumpByBP));
	
	private final List<ImmutableList<String>> commonPreviousBoard = 
  		ImmutableList.<ImmutableList<String>>of(
  				ImmutableList.<String>of(I, I, I, I, S, I, S, I, S, I, S, I, S, I, S, I, I, I, I),
  				ImmutableList.<String>of(I, I, I, S, S, E, I, B, I, W, I, B, I, E, S, S, I, I, I),
  				ImmutableList.<String>of(I, I, S, S, E, I, W, I, W, I, W, I, W, I, E, S, S, I, I),
  				ImmutableList.<String>of(I, S, S, W, I, W, I, W, I, B, I, B, I, B, I, E, S, S, I),
  				ImmutableList.<String>of(S, S, E, I, E, I, E, I, B, I, B, I, B, I, W, I, W, S, S),
  				ImmutableList.<String>of(S, E, I, E, I, E, I, E, I, E, I, E, I, B, I, E, I, E, S),
  				ImmutableList.<String>of(S, S, E, I, E, I, W, I, E, I, E, I, B, I, E, I, E, S, S),
  				ImmutableList.<String>of(I, S, S, E, I, B, I, E, I, E, I, E, I, E, I, E, S, S, I),
  				ImmutableList.<String>of(I, I, S, S, E, I, E, I, W, I, W, I, B, I, B, S, S, I, I),
  				ImmutableList.<String>of(I, I, I, S, S, B, I, W, I, E, I, E, I, E, S, S, I, I, I),
  				ImmutableList.<String>of(I, I, I, I, S, I, S, I, S, I, S, I, S, I, S, I, I, I, I)
  		);
	
	private final Map<String, Object> commonPreviousStateBP = ImmutableMap.<String, Object>builder()
  		.put(BOARD, commonPreviousBoard)
  		.put(JUMP, firstJumpByWP)
  		.build();
	
	private final Map<String, Object> commonPreviousStateWP = ImmutableMap.<String, Object>builder()
  		.put(BOARD, commonPreviousBoard)
  		.put(JUMP, firstJumpByWP)
  		.build();
	
	/*
	 * Helper classes, brought from Professor's cold: from class "CheatLogicTest",
	 * link: https://github.com/yoav-zibin
	 */
	private void assertMoveOk(VerifyMove verifyMove){
		VerifyMoveDone verifyMoveDone = abaloneLogic.verify(verifyMove);
		assertEquals(0, verifyMoveDone.getHackerPlayerId());
	}
	
	private void assertHacker(VerifyMove verifyMove){
		VerifyMoveDone verifyMoveDone = abaloneLogic.verify(verifyMove);
		assertEquals(verifyMove.getLastMovePlayerId(), verifyMoveDone.getHackerPlayerId());
	}
	
	private VerifyMove move(
      int lastMovePlayerId, Map<String, Object> lastState, List<Operation> lastMove) {
    return new VerifyMove(playersInfo,
        // in cheat we never need to check the resulting state (the server makes it, and the game
        // doesn't have any hidden decisions such in Battleships)
        emptyState,
        lastState, lastMove, lastMovePlayerId, ImmutableMap.<Integer, Integer>of());
  }
	
	private List<Operation> getInitialOperations() {
    return abaloneLogic.getInitialMove(ImmutableList.<Integer>of(wId, bId));
  }
	
	@Test
	public void testInitalizeByRightPlayer(){
		assertMoveOk(move(wId, emptyState, getInitialOperations()));
	}
	
	@Test
	public void testInitalizeByWrongPlayer(){
		// Initial operation should always be done by W.
		assertHacker(move(bId, emptyState, getInitialOperations()));
	}
	
	@Test
	public void testInitalizeWthNonEmptyState(){
		assertHacker(move(wId, nonEmptyState, getInitialOperations()));
	}
	
	@Test
  public void testInitialMoveWithExtraOperation() {
    List<Operation> initialOperations = getInitialOperations();
    List<Operation> operations = new ArrayList<Operation>(initialOperations);
    operations.add(new Set(BOARD, ImmutableList.of()));
    assertHacker(move(wId, emptyState, ImmutableList.copyOf(operations)));
  }
	
	@Test
	public void testMoveTurnByRightPlayerWP(){
		assertMoveOk(move(wId, stateAfterInitialization, firstMoveByWP));
	}
	
	@Test
	public void testMoveTurnByRightPlayerBP(){
		assertMoveOk(move(bId, stateAfterFirstWPMove, firstMoveByBP));
	}
	
	@Test
	public void testMoveTurnByWrongPlayer(){
		assertHacker(move(bId, stateAfterInitialization, firstMoveByWP));
	}
	
	@Test
	public void testRightDiagonalMoveByWP(){
		// First, create jump
		List<ImmutableList<ImmutableList<Integer>>> diagonalJump = 
				ImmutableList.<ImmutableList<ImmutableList<Integer>>>of(
						// List for WP's pieces' moves
						ImmutableList.<ImmutableList<Integer>>of(
								ImmutableList.<Integer>of(7, 13, 6, 14),
			  				ImmutableList.<Integer>of(8, 12, 7, 13)
						),
						// List for BP pieces' moves
						ImmutableList.<ImmutableList<Integer>>of(
						)
				);
		
		// Second, create the board after the move
		List<ImmutableList<String>> boardAfterDiagonalMoveByWP = 
	  		ImmutableList.<ImmutableList<String>>of(
	  				ImmutableList.<String>of(I, I, I, I, S, I, S, I, S, I, S, I, S, I, S, I, I, I, I),
	  				ImmutableList.<String>of(I, I, I, S, S, W, I, W, I, E, I, B, I, B, S, S, I, I, I),
	  				ImmutableList.<String>of(I, I, S, S, W, I, W, I, W, I, B, I, B, I, B, S, S, I, I),
	  				ImmutableList.<String>of(I, S, S, E, I, W, I, W, I, E, I, B, I, B, E, I, S, S, I),
	  				ImmutableList.<String>of(S, S, E, I, E, I, E, I, E, I, E, I, E, I, E, I, E, S, S),
	  				ImmutableList.<String>of(S, E, I, E, I, E, I, E, I, E, I, E, I, E, I, E, I, E, S),
	  				ImmutableList.<String>of(S, S, E, I, E, I, B, I, E, I, W, I, E, I, W, I, E, S, S),
	  				ImmutableList.<String>of(I, S, S, E, I, B, I, B, I, E, I, W, I, W, E, I, S, S, I),
	  				ImmutableList.<String>of(I, I, S, S, E, I, B, I, B, I, W, I, E, I, W, S, S, I, I),
	  				ImmutableList.<String>of(I, I, I, S, S, B, I, B, I, E, I, W, I, E, S, S, I, I, I),
	  				ImmutableList.<String>of(I, I, I, I, S, I, S, I, S, I, S, I, S, I, S, I, I, I, I)
	  		);
		
		// Third, create the move
		List<Operation> diagonalMoveByWP = ImmutableList.<Operation>of(
				new SetTurn(bId),
				new Set(BOARD, boardAfterDiagonalMoveByWP),
				new Set(JUMP, diagonalJump));
		
		assertMoveOk(move(wId, stateAfterFirstBPMove, diagonalMoveByWP));
	}
	
	@Test 
	public void testWrongDiagonalMoveWith4Pieces(){
		// Previous
		List<ImmutableList<String>> previousBoard = 
	  		ImmutableList.<ImmutableList<String>>of(
	  				ImmutableList.<String>of(I, I, I, I, S, I, S, I, S, I, S, I, S, I, S, I, I, I, I),
	  				ImmutableList.<String>of(I, I, I, S, S, W, I, W, I, E, I, B, I, B, S, S, I, I, I),
	  				ImmutableList.<String>of(I, I, S, S, W, I, W, I, W, I, B, I, B, I, B, S, S, I, I),
	  				ImmutableList.<String>of(I, S, S, E, I, W, I, W, I, E, I, E, I, B, E, I, S, S, I),
	  				ImmutableList.<String>of(S, S, E, I, E, I, E, I, E, I, E, I, E, I, E, I, E, S, S),
	  				ImmutableList.<String>of(S, E, I, E, I, E, I, E, I, E, I, E, I, E, I, E, I, E, S),
	  				ImmutableList.<String>of(S, S, E, I, E, I, B, I, B, I, W, I, E, I, W, I, E, S, S),
	  				ImmutableList.<String>of(I, S, S, E, I, B, I, B, I, E, I, W, I, W, E, I, S, S, I),
	  				ImmutableList.<String>of(I, I, S, S, E, I, B, I, B, I, W, I, E, I, W, S, S, I, I),
	  				ImmutableList.<String>of(I, I, I, S, S, B, I, B, I, E, I, W, I, E, S, S, I, I, I),
	  				ImmutableList.<String>of(I, I, I, I, S, I, S, I, S, I, S, I, S, I, S, I, I, I, I)
	  		);
		
		Map<String, Object> previousState = 
				ImmutableMap.<String, Object>builder()
	  			.put(BOARD, previousBoard)
	  			.put(JUMP, firstJumpByWP)
	  			.build();
		
		// Afterward
		List<ImmutableList<ImmutableList<Integer>>> currentJump = 
				ImmutableList.<ImmutableList<ImmutableList<Integer>>>of(
						// List for WP's pieces' moves
						ImmutableList.<ImmutableList<Integer>>of(
						),
						// List for BP pieces' moves
						ImmutableList.<ImmutableList<Integer>>of(
								ImmutableList.<Integer>of(6, 8, 5, 9),
			  				ImmutableList.<Integer>of(7, 7, 6, 8),
			  				ImmutableList.<Integer>of(8, 6, 7, 7),
			  				ImmutableList.<Integer>of(9, 5, 8, 6)
						)
				);

		List<ImmutableList<String>> afterwardBoard = 
	  		ImmutableList.<ImmutableList<String>>of(
	  				ImmutableList.<String>of(I, I, I, I, S, I, S, I, S, I, S, I, S, I, S, I, I, I, I),
	  				ImmutableList.<String>of(I, I, I, S, S, W, I, W, I, E, I, B, I, B, S, S, I, I, I),
	  				ImmutableList.<String>of(I, I, S, S, W, I, W, I, W, I, B, I, B, I, B, S, S, I, I),
	  				ImmutableList.<String>of(I, S, S, E, I, W, I, W, I, E, I, E, I, B, E, I, S, S, I),
	  				ImmutableList.<String>of(S, S, E, I, E, I, E, I, E, I, E, I, E, I, E, I, E, S, S),
	  				ImmutableList.<String>of(S, E, I, E, I, E, I, E, I, B, I, E, I, E, I, E, I, E, S),
	  				ImmutableList.<String>of(S, S, E, I, E, I, B, I, B, I, W, I, E, I, W, I, E, S, S),
	  				ImmutableList.<String>of(I, S, S, E, I, B, I, B, I, E, I, W, I, W, E, I, S, S, I),
	  				ImmutableList.<String>of(I, I, S, S, E, I, B, I, B, I, W, I, E, I, W, S, S, I, I),
	  				ImmutableList.<String>of(I, I, I, S, S, E, I, B, I, E, I, W, I, E, S, S, I, I, I),
	  				ImmutableList.<String>of(I, I, I, I, S, I, S, I, S, I, S, I, S, I, S, I, I, I, I)
	  		);
		List<Operation> currentMove = ImmutableList.<Operation>of(
				new SetTurn(wId),
				new Set(BOARD, afterwardBoard),
				new Set(JUMP, currentJump));
		
		assertHacker(move(bId, previousState, currentMove));
	}
	
	@Test
	public void testRightHorizontalMove(){
		//Use initialState as previous, create Afterward
		List<ImmutableList<ImmutableList<Integer>>> currentJump = 
				ImmutableList.<ImmutableList<ImmutableList<Integer>>>of(
						// List for WP's pieces' moves
						ImmutableList.<ImmutableList<Integer>>of(
								ImmutableList.<Integer>of(9, 11, 9, 9),
			  				ImmutableList.<Integer>of(9, 13, 9, 11)
						),
						// List for BP pieces' moves
						ImmutableList.<ImmutableList<Integer>>of(
						)
				);
		
		List<ImmutableList<String>> afterwardBoard = 
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
	  				ImmutableList.<String>of(I, I, I, S, S, B, I, B, I, W, I, W, I, E, S, S, I, I, I),
	  				ImmutableList.<String>of(I, I, I, I, S, I, S, I, S, I, S, I, S, I, S, I, I, I, I)
	  		);
		List<Operation> currentMove = ImmutableList.<Operation>of(
				new SetTurn(bId),
				new Set(BOARD, afterwardBoard),
				new Set(JUMP, currentJump));
		
		assertMoveOk(move(wId, stateAfterInitialization, currentMove));
	}
	
	@Test
	public void testWrongHorizontalMoveOnEmptySqure(){
		List<ImmutableList<ImmutableList<Integer>>> currentJump = 
				ImmutableList.<ImmutableList<ImmutableList<Integer>>>of(
						// List for WP's pieces' moves
						ImmutableList.<ImmutableList<Integer>>of(
								ImmutableList.<Integer>of(9, 11, 9, 10),
			  				ImmutableList.<Integer>of(9, 13, 9, 11)
						),
						// List for BP pieces' moves
						ImmutableList.<ImmutableList<Integer>>of(
						)
				);

		List<ImmutableList<String>> afterwardBoard = 
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
	  				ImmutableList.<String>of(I, I, I, S, S, B, I, B, I, E, W, W, I, E, S, S, I, I, I),
	  				ImmutableList.<String>of(I, I, I, I, S, I, S, I, S, I, S, I, S, I, S, I, I, I, I)
	  		);
		List<Operation> currentMove = ImmutableList.<Operation>of(
				new SetTurn(bId),
				new Set(BOARD, afterwardBoard),
				new Set(JUMP, currentJump));
		
		assertHacker(move(wId, stateAfterInitialization, currentMove));
	}
	
	@Test
	public void testWrongHorizontalMoveWith4Pieces(){
		List<ImmutableList<ImmutableList<Integer>>> currentJump = 
				ImmutableList.<ImmutableList<ImmutableList<Integer>>>of(
						// List for WP's pieces' moves
						ImmutableList.<ImmutableList<Integer>>of(
								ImmutableList.<Integer>of(2, 6, 2, 4),
			  				ImmutableList.<Integer>of(2, 8, 2, 6),
			  				ImmutableList.<Integer>of(2, 10, 2, 8),
			  				ImmutableList.<Integer>of(2, 12, 2, 10)
						),
						// List for BP pieces' moves
						ImmutableList.<ImmutableList<Integer>>of(
						)
				);

		List<ImmutableList<String>> afterwardBoard = 
	  		ImmutableList.<ImmutableList<String>>of(
	  				ImmutableList.<String>of(I, I, I, I, S, I, S, I, S, I, S, I, S, I, S, I, I, I, I),
	  				ImmutableList.<String>of(I, I, I, S, S, E, I, B, I, W, I, B, I, E, S, S, I, I, I),
	  				ImmutableList.<String>of(I, I, S, S, W, I, W, I, W, I, W, I, E, I, E, S, S, I, I),
	  				ImmutableList.<String>of(I, S, S, W, I, W, I, W, I, B, I, B, I, B, I, E, S, S, I),
	  				ImmutableList.<String>of(S, S, E, I, E, I, E, I, B, I, B, I, B, I, W, I, W, S, S),
	  				ImmutableList.<String>of(S, E, I, E, I, E, I, E, I, E, I, E, I, B, I, E, I, E, S),
	  				ImmutableList.<String>of(S, S, E, I, E, I, W, I, E, I, E, I, B, I, E, I, E, S, S),
	  				ImmutableList.<String>of(I, S, S, E, I, B, I, E, I, E, I, E, I, E, I, E, S, S, I),
	  				ImmutableList.<String>of(I, I, S, S, E, I, E, I, W, I, W, I, B, I, B, S, S, I, I),
	  				ImmutableList.<String>of(I, I, I, S, S, B, I, W, I, E, I, E, I, E, S, S, I, I, I),
	  				ImmutableList.<String>of(I, I, I, I, S, I, S, I, S, I, S, I, S, I, S, I, I, I, I)
	  		);
		List<Operation> currentMove = ImmutableList.<Operation>of(
				new SetTurn(wId),
				new Set(BOARD, afterwardBoard),
				new Set(JUMP, currentJump));
		
		assertHacker(move(bId, commonPreviousStateBP, currentMove));
	}
	
	@Test
	public void testRightPush2on1(){
		List<ImmutableList<ImmutableList<Integer>>> currentJump = 
				ImmutableList.<ImmutableList<ImmutableList<Integer>>>of(
						// List for WP's pieces' moves
						ImmutableList.<ImmutableList<Integer>>of(
								ImmutableList.<Integer>of(4, 14, 3, 15)
						),
						// List for BP pieces' moves
						ImmutableList.<ImmutableList<Integer>>of(
								ImmutableList.<Integer>of(5, 13, 4, 14),
			  				ImmutableList.<Integer>of(6, 12, 5, 13)
						)
				);
		
		List<ImmutableList<String>> afterwardBoard = 
	  		ImmutableList.<ImmutableList<String>>of(
	  				ImmutableList.<String>of(I, I, I, I, S, I, S, I, S, I, S, I, S, I, S, I, I, I, I),
	  				ImmutableList.<String>of(I, I, I, S, S, E, I, B, I, W, I, B, I, E, S, S, I, I, I),
	  				ImmutableList.<String>of(I, I, S, S, E, I, W, I, W, I, W, I, W, I, E, S, S, I, I),
	  				ImmutableList.<String>of(I, S, S, W, I, W, I, W, I, B, I, B, I, B, I, W, S, S, I),
	  				ImmutableList.<String>of(S, S, E, I, E, I, E, I, B, I, B, I, B, I, B, I, W, S, S),
	  				ImmutableList.<String>of(S, E, I, E, I, E, I, E, I, E, I, E, I, B, I, E, I, E, S),
	  				ImmutableList.<String>of(S, S, E, I, E, I, W, I, E, I, E, I, E, I, E, I, E, S, S),
	  				ImmutableList.<String>of(I, S, S, E, I, B, I, E, I, E, I, E, I, E, I, E, S, S, I),
	  				ImmutableList.<String>of(I, I, S, S, E, I, E, I, W, I, W, I, B, I, B, S, S, I, I),
	  				ImmutableList.<String>of(I, I, I, S, S, B, I, W, I, E, I, E, I, E, S, S, I, I, I),
	  				ImmutableList.<String>of(I, I, I, I, S, I, S, I, S, I, S, I, S, I, S, I, I, I, I)
	  		);
		List<Operation> currentMove = ImmutableList.<Operation>of(
				new SetTurn(wId),
				new Set(BOARD, afterwardBoard),
				new Set(JUMP, currentJump));
		
		assertMoveOk(move(bId, commonPreviousStateBP, currentMove));
	}
	
	@Test
	public void testWrongPush1on1(){
		List<ImmutableList<ImmutableList<Integer>>> currentJump = 
				ImmutableList.<ImmutableList<ImmutableList<Integer>>>of(
						// List for WP's pieces' moves
						ImmutableList.<ImmutableList<Integer>>of(
								ImmutableList.<Integer>of(6, 6, 5, 7)
						),
						// List for BP pieces' moves
						ImmutableList.<ImmutableList<Integer>>of(
								ImmutableList.<Integer>of(7, 5, 6, 6)
						)
				);
		
		List<ImmutableList<String>> afterwardBoard = 
	  		ImmutableList.<ImmutableList<String>>of(
	  				ImmutableList.<String>of(I, I, I, I, S, I, S, I, S, I, S, I, S, I, S, I, I, I, I),
	  				ImmutableList.<String>of(I, I, I, S, S, E, I, B, I, W, I, B, I, E, S, S, I, I, I),
	  				ImmutableList.<String>of(I, I, S, S, E, I, W, I, W, I, W, I, W, I, E, S, S, I, I),
	  				ImmutableList.<String>of(I, S, S, W, I, W, I, W, I, B, I, B, I, B, I, E, S, S, I),
	  				ImmutableList.<String>of(S, S, E, I, E, I, E, I, B, I, B, I, B, I, W, I, W, S, S),
	  				ImmutableList.<String>of(S, E, I, E, I, E, I, W, I, E, I, E, I, B, I, E, I, E, S),
	  				ImmutableList.<String>of(S, S, E, I, E, I, B, I, E, I, E, I, B, I, E, I, E, S, S),
	  				ImmutableList.<String>of(I, S, S, E, I, E, I, E, I, E, I, E, I, E, I, E, S, S, I),
	  				ImmutableList.<String>of(I, I, S, S, E, I, E, I, W, I, W, I, B, I, B, S, S, I, I),
	  				ImmutableList.<String>of(I, I, I, S, S, B, I, W, I, E, I, E, I, E, S, S, I, I, I),
	  				ImmutableList.<String>of(I, I, I, I, S, I, S, I, S, I, S, I, S, I, S, I, I, I, I)
	  		);
		List<Operation> currentMove = ImmutableList.<Operation>of(
				new SetTurn(wId),
				new Set(BOARD, afterwardBoard),
				new Set(JUMP, currentJump));
		
		assertHacker(move(bId, commonPreviousStateBP, currentMove));
	}
	
	@Test
	public void testWrongPush1on2(){
		List<ImmutableList<ImmutableList<Integer>>> currentJump = 
				ImmutableList.<ImmutableList<ImmutableList<Integer>>>of(
						// List for WP's pieces' moves
						ImmutableList.<ImmutableList<Integer>>of(
								ImmutableList.<Integer>of(4, 14, 5, 13)
						),
						// List for BP pieces' moves
						ImmutableList.<ImmutableList<Integer>>of(
								ImmutableList.<Integer>of(5, 13, 6, 12),
								ImmutableList.<Integer>of(6, 12, 7, 11)
						)
				);
		
		List<ImmutableList<String>> afterwardBoard = 
	  		ImmutableList.<ImmutableList<String>>of(
	  				ImmutableList.<String>of(I, I, I, I, S, I, S, I, S, I, S, I, S, I, S, I, I, I, I),
	  				ImmutableList.<String>of(I, I, I, S, S, E, I, B, I, W, I, B, I, E, S, S, I, I, I),
	  				ImmutableList.<String>of(I, I, S, S, E, I, W, I, W, I, W, I, W, I, E, S, S, I, I),
	  				ImmutableList.<String>of(I, S, S, W, I, W, I, W, I, B, I, B, I, B, I, E, S, S, I),
	  				ImmutableList.<String>of(S, S, E, I, E, I, E, I, B, I, B, I, B, I, E, I, W, S, S),
	  				ImmutableList.<String>of(S, E, I, E, I, E, I, E, I, E, I, E, I, W, I, E, I, E, S),
	  				ImmutableList.<String>of(S, S, E, I, E, I, W, I, E, I, E, I, B, I, E, I, E, S, S),
	  				ImmutableList.<String>of(I, S, S, E, I, B, I, E, I, E, I, B, I, E, I, E, S, S, I),
	  				ImmutableList.<String>of(I, I, S, S, E, I, E, I, W, I, W, I, B, I, B, S, S, I, I),
	  				ImmutableList.<String>of(I, I, I, S, S, B, I, W, I, E, I, E, I, E, S, S, I, I, I),
	  				ImmutableList.<String>of(I, I, I, I, S, I, S, I, S, I, S, I, S, I, S, I, I, I, I)
	  		);
		List<Operation> currentMove = ImmutableList.<Operation>of(
				new SetTurn(bId),
				new Set(BOARD, afterwardBoard),
				new Set(JUMP, currentJump));
		
		assertHacker(move(wId, commonPreviousStateWP, currentMove));
	}
	
	@Test
	public void testWrongPush2on3(){
		List<ImmutableList<ImmutableList<Integer>>> currentJump = 
				ImmutableList.<ImmutableList<ImmutableList<Integer>>>of(
						// List for WP's pieces' moves
						ImmutableList.<ImmutableList<Integer>>of(
								ImmutableList.<Integer>of(4, 16, 4, 14),
								ImmutableList.<Integer>of(4, 14, 4, 12)
						),
						// List for BP pieces' moves
						ImmutableList.<ImmutableList<Integer>>of(
								ImmutableList.<Integer>of(4, 12, 4, 10),
								ImmutableList.<Integer>of(4, 10, 4, 8),
								ImmutableList.<Integer>of(4, 8, 4, 6)
						)
				);
		
		List<ImmutableList<String>> afterwardBoard = 
	  		ImmutableList.<ImmutableList<String>>of(
	  				ImmutableList.<String>of(I, I, I, I, S, I, S, I, S, I, S, I, S, I, S, I, I, I, I),
	  				ImmutableList.<String>of(I, I, I, S, S, E, I, B, I, W, I, B, I, E, S, S, I, I, I),
	  				ImmutableList.<String>of(I, I, S, S, E, I, W, I, W, I, W, I, W, I, E, S, S, I, I),
	  				ImmutableList.<String>of(I, S, S, W, I, W, I, W, I, B, I, B, I, B, I, E, S, S, I),
	  				ImmutableList.<String>of(S, S, E, I, E, I, B, I, B, I, B, I, W, I, W, I, E, S, S),
	  				ImmutableList.<String>of(S, E, I, E, I, E, I, E, I, E, I, E, I, B, I, E, I, E, S),
	  				ImmutableList.<String>of(S, S, E, I, E, I, W, I, E, I, E, I, B, I, E, I, E, S, S),
	  				ImmutableList.<String>of(I, S, S, E, I, B, I, E, I, E, I, E, I, E, I, E, S, S, I),
	  				ImmutableList.<String>of(I, I, S, S, E, I, E, I, W, I, W, I, B, I, B, S, S, I, I),
	  				ImmutableList.<String>of(I, I, I, S, S, B, I, W, I, E, I, E, I, E, S, S, I, I, I),
	  				ImmutableList.<String>of(I, I, I, I, S, I, S, I, S, I, S, I, S, I, S, I, I, I, I)
	  		);
		List<Operation> currentMove = ImmutableList.<Operation>of(
				new SetTurn(bId),
				new Set(BOARD, afterwardBoard),
				new Set(JUMP, currentJump));
		
		assertHacker(move(wId, commonPreviousStateWP, currentMove));
	}
	
	@Test
	public void testCannotPush(){
		List<ImmutableList<ImmutableList<Integer>>> currentJump = 
				ImmutableList.<ImmutableList<ImmutableList<Integer>>>of(
						// List for WP's pieces' moves
						ImmutableList.<ImmutableList<Integer>>of(
								ImmutableList.<Integer>of(1, 9, 1, 7)
						),
						// List for BP pieces' moves
						ImmutableList.<ImmutableList<Integer>>of(
								ImmutableList.<Integer>of(1, 11, 1, 9),
								ImmutableList.<Integer>of(1, 7, 1, 5)
						)
				);
		
		List<ImmutableList<String>> afterwardBoard = 
	  		ImmutableList.<ImmutableList<String>>of(
	  				ImmutableList.<String>of(I, I, I, I, S, I, S, I, S, I, S, I, S, I, S, I, I, I, I),
	  				ImmutableList.<String>of(I, I, I, S, S, B, I, W, I, B, I, E, I, E, S, S, I, I, I),
	  				ImmutableList.<String>of(I, I, S, S, E, I, W, I, W, I, W, I, W, I, E, S, S, I, I),
	  				ImmutableList.<String>of(I, S, S, W, I, W, I, W, I, B, I, B, I, B, I, E, S, S, I),
	  				ImmutableList.<String>of(S, S, E, I, E, I, E, I, B, I, B, I, B, I, W, I, W, S, S),
	  				ImmutableList.<String>of(S, E, I, E, I, E, I, E, I, E, I, E, I, B, I, E, I, E, S),
	  				ImmutableList.<String>of(S, S, E, I, E, I, W, I, E, I, E, I, B, I, E, I, E, S, S),
	  				ImmutableList.<String>of(I, S, S, E, I, B, I, E, I, E, I, E, I, E, I, E, S, S, I),
	  				ImmutableList.<String>of(I, I, S, S, E, I, E, I, W, I, W, I, B, I, B, S, S, I, I),
	  				ImmutableList.<String>of(I, I, I, S, S, B, I, W, I, E, I, E, I, E, S, S, I, I, I),
	  				ImmutableList.<String>of(I, I, I, I, S, I, S, I, S, I, S, I, S, I, S, I, I, I, I)
	  		);
		List<Operation> currentMove = ImmutableList.<Operation>of(
				new SetTurn(wId),
				new Set(BOARD, afterwardBoard),
				new Set(JUMP, currentJump));
		
		assertHacker(move(bId, commonPreviousStateBP, currentMove));
	}
	
	@Test
	public void testNormalEndGame(){
		List<ImmutableList<ImmutableList<Integer>>> currentJump = 
				ImmutableList.<ImmutableList<ImmutableList<Integer>>>of(
						// List for WP's pieces' moves
						ImmutableList.<ImmutableList<Integer>>of(
								ImmutableList.<Integer>of(4, 16, 4, 17),
								ImmutableList.<Integer>of(4, 14, 4, 16)
						),
						// List for BP pieces' moves
						ImmutableList.<ImmutableList<Integer>>of(
								ImmutableList.<Integer>of(4, 12, 4, 14),
								ImmutableList.<Integer>of(4, 10, 4, 12),
								ImmutableList.<Integer>of(4, 8, 4, 10)
						)
				);

		List<ImmutableList<String>> afterwardBoard = 
	  		ImmutableList.<ImmutableList<String>>of(
	  				ImmutableList.<String>of(I, I, I, I, S, I, S, I, S, I, S, I, S, I, S, I, I, I, I),
	  				ImmutableList.<String>of(I, I, I, S, S, E, I, B, I, W, I, B, I, E, S, S, I, I, I),
	  				ImmutableList.<String>of(I, I, S, S, E, I, W, I, W, I, W, I, W, I, E, S, S, I, I),
	  				ImmutableList.<String>of(I, S, S, W, I, W, I, W, I, B, I, B, I, B, I, E, S, S, I),
	  				ImmutableList.<String>of(S, S, E, I, E, I, E, I, E, I, B, I, B, I, B, I, W, W, S),
	  				ImmutableList.<String>of(S, E, I, E, I, E, I, E, I, E, I, E, I, B, I, E, I, E, S),
	  				ImmutableList.<String>of(S, S, E, I, E, I, W, I, E, I, E, I, B, I, E, I, E, S, S),
	  				ImmutableList.<String>of(I, S, S, E, I, B, I, E, I, E, I, E, I, E, I, E, S, S, I),
	  				ImmutableList.<String>of(I, I, S, S, E, I, E, I, W, I, W, I, B, I, B, S, S, I, I),
	  				ImmutableList.<String>of(I, I, I, S, S, B, I, W, I, E, I, E, I, E, S, S, I, I, I),
	  				ImmutableList.<String>of(I, I, I, I, S, I, S, I, S, I, S, I, S, I, S, I, I, I, I)
	  		);
		List<Operation> currentMove = ImmutableList.<Operation>of(
				new SetTurn(wId),
				new Set(BOARD, afterwardBoard),
				new Set(JUMP, currentJump),
				new EndGame(bId));
		
		assertMoveOk(move(bId, commonPreviousStateBP, currentMove));
	}
	
	@Test
	public void testWrongEndGameClaimWithNoEnding(){
		List<ImmutableList<ImmutableList<Integer>>> currentJump = 
				ImmutableList.<ImmutableList<ImmutableList<Integer>>>of(
						// List for WP's pieces' moves
						ImmutableList.<ImmutableList<Integer>>of(
								ImmutableList.<Integer>of(1, 9, 1, 7)
						),
						// List for BP pieces' moves
						ImmutableList.<ImmutableList<Integer>>of(
								ImmutableList.<Integer>of(1, 11, 1, 7),
								ImmutableList.<Integer>of(1, 7, 1, 5)
						)
				);

		List<ImmutableList<String>> afterwardBoard = 
	  		ImmutableList.<ImmutableList<String>>of(
	  				ImmutableList.<String>of(I, I, I, I, S, I, S, I, S, I, S, I, S, I, S, I, I, I, I),
	  				ImmutableList.<String>of(I, I, I, S, S, B, I, W, I, B, I, E, I, E, S, S, I, I, I),
	  				ImmutableList.<String>of(I, I, S, S, E, I, W, I, W, I, W, I, W, I, E, S, S, I, I),
	  				ImmutableList.<String>of(I, S, S, W, I, W, I, W, I, B, I, B, I, B, I, E, S, S, I),
	  				ImmutableList.<String>of(S, S, E, I, E, I, E, I, B, I, B, I, B, I, W, I, W, S, S),
	  				ImmutableList.<String>of(S, E, I, E, I, E, I, E, I, E, I, E, I, B, I, E, I, E, S),
	  				ImmutableList.<String>of(S, S, E, I, E, I, W, I, E, I, E, I, B, I, E, I, E, S, S),
	  				ImmutableList.<String>of(I, S, S, E, I, B, I, E, I, E, I, E, I, E, I, E, S, S, I),
	  				ImmutableList.<String>of(I, I, S, S, E, I, E, I, W, I, W, I, B, I, B, S, S, I, I),
	  				ImmutableList.<String>of(I, I, I, S, S, B, I, W, I, E, I, E, I, E, S, S, I, I, I),
	  				ImmutableList.<String>of(I, I, I, I, S, I, S, I, S, I, S, I, S, I, S, I, I, I, I)
	  		);
		List<Operation> currentMove = ImmutableList.<Operation>of(
				new SetTurn(wId),
				new Set(BOARD, afterwardBoard),
				new Set(JUMP, currentJump),
				new EndGame(bId));
		
		assertHacker(move(bId, commonPreviousStateBP, currentMove));
	}
	
	@Test
	public void testNoEndGameWhenGameIsEnd(){
		List<ImmutableList<ImmutableList<Integer>>> currentJump = 
				ImmutableList.<ImmutableList<ImmutableList<Integer>>>of(
						// List for WP's pieces' moves
						ImmutableList.<ImmutableList<Integer>>of(
								ImmutableList.<Integer>of(4, 16, 4, 17),
								ImmutableList.<Integer>of(4, 14, 4, 16)
						),
						// List for BP pieces' moves
						ImmutableList.<ImmutableList<Integer>>of(
								ImmutableList.<Integer>of(4, 12, 4, 14),
								ImmutableList.<Integer>of(4, 10, 4, 12),
								ImmutableList.<Integer>of(4, 8, 4, 10)
						)
				);
		
		List<ImmutableList<String>> afterwardBoard = 
	  		ImmutableList.<ImmutableList<String>>of(
	  				ImmutableList.<String>of(I, I, I, I, S, I, S, I, S, I, S, I, S, I, S, I, I, I, I),
	  				ImmutableList.<String>of(I, I, I, S, S, E, I, B, I, W, I, B, I, E, S, S, I, I, I),
	  				ImmutableList.<String>of(I, I, S, S, E, I, W, I, W, I, W, I, W, I, E, S, S, I, I),
	  				ImmutableList.<String>of(I, S, S, W, I, W, I, W, I, B, I, B, I, B, I, E, S, S, I),
	  				ImmutableList.<String>of(S, S, E, I, E, I, E, I, B, I, B, I, B, I, W, I, W, S, S),
	  				ImmutableList.<String>of(S, E, I, E, I, E, I, E, I, E, I, E, I, B, I, E, I, E, S),
	  				ImmutableList.<String>of(S, S, E, I, E, I, W, I, E, I, E, I, B, I, E, I, E, S, S),
	  				ImmutableList.<String>of(I, S, S, E, I, B, I, E, I, E, I, E, I, E, I, E, S, S, I),
	  				ImmutableList.<String>of(I, I, S, S, E, I, E, I, W, I, W, I, B, I, B, S, S, I, I),
	  				ImmutableList.<String>of(I, I, I, S, S, B, I, W, I, E, I, E, I, E, S, S, I, I, I),
	  				ImmutableList.<String>of(I, I, I, I, S, I, S, I, S, I, S, I, S, I, S, I, I, I, I)
	  		);
		List<Operation> currentMove = ImmutableList.<Operation>of(
				new SetTurn(wId),
				new Set(BOARD, afterwardBoard),
				new Set(JUMP, currentJump));
		
		assertHacker(move(bId, commonPreviousStateBP, currentMove));
	}
	
}
