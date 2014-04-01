package org.abalone.client;

import static org.abalone.client.AbaloneConstants.BTurn;
import static org.abalone.client.AbaloneConstants.illegalSquares;
import static org.abalone.client.AbaloneConstants.scoreSquares;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;

public class AbaloneLogicTest {
	private AbaloneLogic abaloneLogic;
	
	@Before
	public void setUp() {
		abaloneLogic = new AbaloneLogic();
	}
	
	@Test
	public void testGetTurn() {
		List<String> playerIds = Lists.newArrayList("1", "2");
		String lastMovePlayerId = "2";
		String targetTurn = BTurn;
		String turn = abaloneLogic.getTurn(playerIds, lastMovePlayerId);
		assertEquals(turn, targetTurn);
	}

	@Test
	public void testIllegalScoreSquaresContains() {
		assertTrue(illegalSquares.contains(Lists.<Integer>newArrayList(0, 2)));
		assertTrue(scoreSquares.contains(Lists.<Integer>newArrayList(5, 0)));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testJumpValidation() {
		List<ArrayList<Integer>> jumpHorizontal = Lists.<ArrayList<Integer>>newArrayList(
				Lists.<Integer>newArrayList(8, 8, 8, 6, 0),
				Lists.<Integer>newArrayList(8, 10, 8, 8, 0));
		List<ArrayList<Integer>> jumpDiagonal = Lists.<ArrayList<Integer>>newArrayList(
				Lists.<Integer>newArrayList(3, 13, 2, 14, 1),
				Lists.<Integer>newArrayList(4, 12, 3, 13, 1));
		abaloneLogic.checkJump(jumpHorizontal);
		abaloneLogic.checkJump(jumpDiagonal);
	}
	
	@SuppressWarnings("unchecked")
	@Test(expected = RuntimeException.class)
	public void testInvalidJumpValidation() {
		List<ArrayList<Integer>> jumpIllegal = Lists.<ArrayList<Integer>>newArrayList(
				Lists.<Integer>newArrayList(7, 8, 8, 6, 0),
				Lists.<Integer>newArrayList(8, 10, 8, 8, 0));
		List<ArrayList<Integer>> jumpScore = Lists.<ArrayList<Integer>>newArrayList(
				Lists.<Integer>newArrayList(3, 13, 2, 14, 1),
				Lists.<Integer>newArrayList(4, 1, 3, 13, 1));
		abaloneLogic.checkJump(jumpIllegal);
		abaloneLogic.checkJump(jumpScore);
	}
}
