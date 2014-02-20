package com.longyang.abalone.api;

import java.util.List;

import com.google.common.collect.ImmutableList;

/**
 * Class used to store all the constants in the Abalone game.
 * 
 * @author Long Yang (ly603@nyu.edu)
 * 
 */
public class AbaloneConstants {
	
	public static final String PLAYER_ID = "player_id";
	public static final String AI_PLAYER = "artificial intelligence player";
	public static final String GAME_VIEWER = "game viewer";
	public static final String BOARD = "board";
	public static final String JUMP = "jump";
	public static final String W = "White Piece";
	public static final String B = "Black Piece";
	public static final String E = "Empty Square";
	public static final String S = "Score Square";
	public static final String I = "Illegal Square";
	
	@SuppressWarnings("unchecked")
	public static final List<ImmutableList<Integer>> illegalSquares = 
			ImmutableList.<ImmutableList<Integer>>of(
					// Row 0
					ImmutableList.<Integer>of(0, 0), ImmutableList.<Integer>of(0, 1), 
					ImmutableList.<Integer>of(0, 2), ImmutableList.<Integer>of(0, 3),
					ImmutableList.<Integer>of(0, 5), ImmutableList.<Integer>of(0, 7), 
					ImmutableList.<Integer>of(0, 9), ImmutableList.<Integer>of(0, 11), 
					ImmutableList.<Integer>of(0, 13), ImmutableList.<Integer>of(0, 15), 
					ImmutableList.<Integer>of(0, 16), ImmutableList.<Integer>of(0, 17), 
					ImmutableList.<Integer>of(0, 18),
					// Row 1
					ImmutableList.<Integer>of(1, 0), ImmutableList.<Integer>of(1, 1),
					ImmutableList.<Integer>of(1, 2), ImmutableList.<Integer>of(1, 6),
					ImmutableList.<Integer>of(1, 8), ImmutableList.<Integer>of(1, 10),
					ImmutableList.<Integer>of(1, 12), ImmutableList.<Integer>of(1, 16),
					ImmutableList.<Integer>of(1, 17), ImmutableList.<Integer>of(1, 18),
					// Row 2
					ImmutableList.<Integer>of(2, 0), ImmutableList.<Integer>of(2, 1),
					ImmutableList.<Integer>of(2, 5), ImmutableList.<Integer>of(2, 7),
					ImmutableList.<Integer>of(2, 9), ImmutableList.<Integer>of(2, 11),
					ImmutableList.<Integer>of(2, 13), ImmutableList.<Integer>of(2, 17),
					ImmutableList.<Integer>of(2, 18), 
					// Row 3
					ImmutableList.<Integer>of(3, 0), ImmutableList.<Integer>of(3, 4),
					ImmutableList.<Integer>of(3, 6), ImmutableList.<Integer>of(3, 8),
					ImmutableList.<Integer>of(3, 10), ImmutableList.<Integer>of(3, 12),
					ImmutableList.<Integer>of(3, 14), ImmutableList.<Integer>of(3, 18),
					// Row 4
					ImmutableList.<Integer>of(4, 3), ImmutableList.<Integer>of(4, 5),
					ImmutableList.<Integer>of(4, 7), ImmutableList.<Integer>of(4, 9),
					ImmutableList.<Integer>of(4, 11), ImmutableList.<Integer>of(4, 13),
					ImmutableList.<Integer>of(4, 15),
					// Row 5
					ImmutableList.<Integer>of(5, 2), ImmutableList.<Integer>of(5, 4),
					ImmutableList.<Integer>of(5, 6), ImmutableList.<Integer>of(5, 8),
					ImmutableList.<Integer>of(5, 10), ImmutableList.<Integer>of(5, 12),
					ImmutableList.<Integer>of(5, 14), ImmutableList.<Integer>of(5, 16),
					// Row 6
					ImmutableList.<Integer>of(6, 3), ImmutableList.<Integer>of(6, 5),
					ImmutableList.<Integer>of(6, 7), ImmutableList.<Integer>of(6, 9),
					ImmutableList.<Integer>of(6, 11), ImmutableList.<Integer>of(6, 13),
					ImmutableList.<Integer>of(6, 15),
					// Row 7
					ImmutableList.<Integer>of(7, 0), ImmutableList.<Integer>of(7, 4),
					ImmutableList.<Integer>of(7, 6), ImmutableList.<Integer>of(7, 8),
					ImmutableList.<Integer>of(7, 10), ImmutableList.<Integer>of(7, 12),
					ImmutableList.<Integer>of(7, 14), ImmutableList.<Integer>of(7, 18),
					// Row 8
					ImmutableList.<Integer>of(8, 0), ImmutableList.<Integer>of(8, 1),
					ImmutableList.<Integer>of(8, 5), ImmutableList.<Integer>of(8, 7),
					ImmutableList.<Integer>of(8, 9), ImmutableList.<Integer>of(8, 11),
					ImmutableList.<Integer>of(8, 13), ImmutableList.<Integer>of(8, 17),
					ImmutableList.<Integer>of(8, 18), 
					// Row 9
					ImmutableList.<Integer>of(9, 0), ImmutableList.<Integer>of(9, 1),
					ImmutableList.<Integer>of(9, 2), ImmutableList.<Integer>of(9, 6),
					ImmutableList.<Integer>of(9, 8), ImmutableList.<Integer>of(9, 10),
					ImmutableList.<Integer>of(9, 12), ImmutableList.<Integer>of(9, 16),
					ImmutableList.<Integer>of(9, 17), ImmutableList.<Integer>of(9, 18),
					// Row 10
					ImmutableList.<Integer>of(10, 0), ImmutableList.<Integer>of(10, 1), 
					ImmutableList.<Integer>of(10, 2), ImmutableList.<Integer>of(10, 3),
					ImmutableList.<Integer>of(10, 5), ImmutableList.<Integer>of(10, 7), 
					ImmutableList.<Integer>of(10, 9), ImmutableList.<Integer>of(10, 11), 
					ImmutableList.<Integer>of(10, 13), ImmutableList.<Integer>of(10, 15), 
					ImmutableList.<Integer>of(10, 16), ImmutableList.<Integer>of(10, 17), 
					ImmutableList.<Integer>of(10, 18)
			);
}
