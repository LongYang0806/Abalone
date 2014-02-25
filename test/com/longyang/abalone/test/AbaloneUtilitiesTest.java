package com.longyang.abalone.test;

import static com.longyang.abalone.api.Square.B;
import static com.longyang.abalone.api.Square.E;
import static com.longyang.abalone.api.Square.I;
import static com.longyang.abalone.api.Square.S;
import static com.longyang.abalone.api.Square.W;
import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.google.common.collect.ImmutableList;
import com.longyang.abalone.api.AbaloneConstants;
import com.longyang.abalone.api.Jump;
import com.longyang.abalone.api.Square;
import com.longyang.abalone.impl.AbaloneUtilities;

@RunWith(JUnit4.class)
public class AbaloneUtilitiesTest {
	
	@Test
	public void testBoardAppliedJumps(){
		List<ImmutableList<Square>> newBoard = ImmutableList.<ImmutableList<Square>>of(
				ImmutableList.<Square>of(I, I, I, I, S, I, S, I, S, I, S, I, S, I, S, I, I, I, I),
				ImmutableList.<Square>of(I, I, I, S, S, W, I, W, I, E, I, B, I, B, S, S, I, I, I),
				ImmutableList.<Square>of(I, I, S, S, W, I, W, I, W, I, B, I, B, I, B, S, S, I, I),
				ImmutableList.<Square>of(I, S, S, E, I, W, I, W, I, E, I, B, I, B, E, I, S, S, I),
				ImmutableList.<Square>of(S, S, E, I, E, I, E, I, E, I, E, I, E, I, E, I, E, S, S),
				ImmutableList.<Square>of(S, E, I, E, I, E, I, E, I, E, I, E, I, E, I, E, I, E, S),
				ImmutableList.<Square>of(S, S, E, I, E, I, E, I, E, I, E, I, E, I, E, I, E, S, S),
				ImmutableList.<Square>of(I, S, S, E, I, B, I, B, I, E, I, W, I, W, E, I, S, S, I),
				ImmutableList.<Square>of(I, I, S, S, B, I, B, I, B, I, W, I, W, I, W, S, S, I, I),
				ImmutableList.<Square>of(I, I, I, S, S, B, I, B, I, E, I, W, I, W, S, S, I, I, I),
				ImmutableList.<Square>of(I, I, I, I, S, I, S, I, S, I, S, I, S, I, S, I, I, I, I)
		);
		List<Jump> jumps = ImmutableList.<Jump>of(
				new Jump(7, 7, 6, 8),
				new Jump(8, 6, 7, 7),
				new Jump(9, 5, 8, 6)
		);
		List<ImmutableList<Square>> appliedJumpsBoard = AbaloneUtilities.boardAppliedJumps(
				AbaloneUtilities.stringBoardToSquareBoard(AbaloneConstants.initialBoard),
				jumps);
		assertEquals(newBoard, appliedJumpsBoard);
	}
	
	@Test
	public void testSquareToStringBoard(){
		assertEquals(AbaloneConstants.initialBoard,
				AbaloneUtilities.squareBoardToStringBoard(
						AbaloneUtilities.stringBoardToSquareBoard(AbaloneConstants.initialBoard)));
	}
}
