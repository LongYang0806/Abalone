package com.longyang.abalone.test;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.google.common.collect.ImmutableList;
import com.longyang.abalone.api.AbaloneConstants;
import com.longyang.abalone.api.Square;
import com.longyang.abalone.api.Turn;
import com.longyang.abalone.impl.AbaloneState;
import com.longyang.abalone.impl.AbaloneUtilities;

@RunWith(JUnit4.class)
public class AbaloneStateTest {
	/*
	 * AbaloneState object used for testing.
	 */
	AbaloneState abaloneState = new AbaloneState(Turn.WP, ImmutableList.<Integer>of(), 
			ImmutableList.<ImmutableList<Square>>of(), ImmutableList.<ImmutableList<ImmutableList<Integer>>>of(), null);
	
	@Test
	public void testLandInIllegalSquares(){
		List<ImmutableList<ImmutableList<Integer>>> jump = 
				ImmutableList.<ImmutableList<ImmutableList<Integer>>>of(
						// List for WP's pieces' moves
						ImmutableList.<ImmutableList<Integer>>of(
								ImmutableList.<Integer>of(0, 0, 0, 1),
								ImmutableList.<Integer>of(6, 6, 5, 7)
						),
						// List for BP pieces' moves
						ImmutableList.<ImmutableList<Integer>>of(
								ImmutableList.<Integer>of(7, 5, 6, 6)
						)
				);
		assertTrue(abaloneState.landInIllegalSquares(jump.get(0)));
	}

	/**
	 * To test when there are two jumps whose original positions are the same.
	 * Exception expected.
	 */
	@Test(expected=IllegalArgumentException.class)
	public void testMoveTheSamePieceTwice(){
		List<ImmutableList<ImmutableList<Integer>>> jump = 
				ImmutableList.<ImmutableList<ImmutableList<Integer>>>of(
						// List for WP's pieces' moves
						ImmutableList.<ImmutableList<Integer>>of(
								ImmutableList.<Integer>of(6, 6, 5, 7),
								ImmutableList.<Integer>of(6, 6, 5, 7)
						),
						// List for BP pieces' moves
						ImmutableList.<ImmutableList<Integer>>of(
								ImmutableList.<Integer>of(7, 5, 6, 6)
						)
				);
		
		abaloneState.applyJumpOnBoard(jump);
	}
	
	@Test(expected=RuntimeException.class)
	public void testCheck2And3PiecesHoritonalException(){
		List<ImmutableList<ImmutableList<Integer>>> jump = 
				ImmutableList.<ImmutableList<ImmutableList<Integer>>>of(
						// List for WP's pieces' moves
						ImmutableList.<ImmutableList<Integer>>of(
								ImmutableList.<Integer>of(0, 1, 0, 3),
								ImmutableList.<Integer>of(0, 3, 1, 7)
						),
						// List for BP pieces' moves
						ImmutableList.<ImmutableList<Integer>>of(
								ImmutableList.<Integer>of(7, 5, 6, 6)
						)
				);
		abaloneState.check2And3Pieces(jump.get(0));
	}
	
	@Test
	public void testEquals(){
		List<ImmutableList<ImmutableList<Integer>>> jump1 = 
				ImmutableList.<ImmutableList<ImmutableList<Integer>>>of(
						// List for WP's pieces' moves
						ImmutableList.<ImmutableList<Integer>>of(
								ImmutableList.<Integer>of(6, 6, 5, 7),
								ImmutableList.<Integer>of(6, 6, 5, 7)
						),
						// List for BP pieces' moves
						ImmutableList.<ImmutableList<Integer>>of(
								ImmutableList.<Integer>of(7, 5, 6, 6)
						)
				);
		
		List<ImmutableList<ImmutableList<Integer>>> jump2 = 
				ImmutableList.<ImmutableList<ImmutableList<Integer>>>of(
						// List for WP's pieces' moves
						ImmutableList.<ImmutableList<Integer>>of(
								ImmutableList.<Integer>of(6, 6, 5, 7),
								ImmutableList.<Integer>of(6, 6, 5, 7)
						),
						// List for BP pieces' moves
						ImmutableList.<ImmutableList<Integer>>of(
								ImmutableList.<Integer>of(7, 5, 6, 6)
						)
				);
		
		AbaloneState state1 = new AbaloneState(Turn.WP, ImmutableList.of(1, 2), 
				AbaloneUtilities.stringBoardToSquareBoard(AbaloneConstants.initialBoard), jump1, null);
		AbaloneState state2 = new AbaloneState(Turn.WP, ImmutableList.of(1, 2), 
				AbaloneUtilities.stringBoardToSquareBoard(AbaloneConstants.initialBoard), jump2, null);
		
		assertTrue(state1.equals(state2));
	}
}
