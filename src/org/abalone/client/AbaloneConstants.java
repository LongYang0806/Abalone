package org.abalone.client;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Lists;

public class AbaloneConstants {
	/*
	 * Constants which stands for "Turn".
	 */
	public static final String WTurn = "WP";
	public static final String BTurn = "BP";
	
	
	/*
	 * Board row number and column number
	 */
	public static final int BoardRowNum = 11;
	public static final int BoardColNum = 19;
	
	/*
	 * Game State Message
	 */
	public static final String UNDERGOING = "Under Going";
	public static final String GAMEOVER = "Game Over";
	
	/*
	 * Constants which stands for the "".
	 */
	public static final String W = "W"; // White
	public static final String B = "B"; // Black
	public static final String E = "E"; // Empty
	public static final String I = "I"; // Illegal
	public static final String S = "S"; // Score
	
	/*
	 * Keys for the gameApiState {@code Map<String, Object>}
	 */
	public static final String BOARD = "Board";
	public static final String JUMP = "Jump";
	
	/* 
	 * Initial board for initial operations.
	 */
	@SuppressWarnings("unchecked")
	public static final List<ArrayList<String>> initialBoard = 
  		Lists.<ArrayList<String>>newArrayList(
  				Lists.newArrayList(I, I, I, I, S, I, S, I, S, I, S, I, S, I, S, I, I, I, I),
  				Lists.newArrayList(I, I, I, S, S, W, I, W, I, E, I, B, I, B, S, S, I, I, I),
  				Lists.newArrayList(I, I, S, S, W, I, W, I, W, I, B, I, B, I, B, S, S, I, I),
  				Lists.newArrayList(I, S, S, E, I, W, I, W, I, E, I, B, I, B, I, E, S, S, I),
  				Lists.newArrayList(S, S, E, I, E, I, E, I, E, I, E, I, E, I, E, I, E, S, S),
  				Lists.newArrayList(S, E, I, E, I, E, I, E, I, E, I, E, I, E, I, E, I, E, S),
  				Lists.newArrayList(S, S, E, I, E, I, E, I, E, I, E, I, E, I, E, I, E, S, S),
  				Lists.newArrayList(I, S, S, E, I, B, I, B, I, E, I, W, I, W, I, E, S, S, I),
  				Lists.newArrayList(I, I, S, S, B, I, B, I, B, I, W, I, W, I, W, S, S, I, I),
  				Lists.newArrayList(I, I, I, S, S, B, I, B, I, E, I, W, I, W, S, S, I, I, I),
  				Lists.newArrayList(I, I, I, I, S, I, S, I, S, I, S, I, S, I, S, I, I, I, I)
  		);
	
	@SuppressWarnings("unchecked")
	public static final List<ArrayList<Integer>> illegalSquares = 
			Lists.<ArrayList<Integer>>newArrayList(
					// Row 0
					Lists.<Integer>newArrayList(0, 0), Lists.<Integer>newArrayList(0, 1), 
					Lists.<Integer>newArrayList(0, 2), Lists.<Integer>newArrayList(0, 3),
					Lists.<Integer>newArrayList(0, 5), Lists.<Integer>newArrayList(0, 7), 
					Lists.<Integer>newArrayList(0, 9), Lists.<Integer>newArrayList(0, 11), 
					Lists.<Integer>newArrayList(0, 13), Lists.<Integer>newArrayList(0, 15), 
					Lists.<Integer>newArrayList(0, 16), Lists.<Integer>newArrayList(0, 17), 
					Lists.<Integer>newArrayList(0, 18),
					// Row 1
					Lists.<Integer>newArrayList(1, 0), Lists.<Integer>newArrayList(1, 1),
					Lists.<Integer>newArrayList(1, 2), Lists.<Integer>newArrayList(1, 6),
					Lists.<Integer>newArrayList(1, 8), Lists.<Integer>newArrayList(1, 10),
					Lists.<Integer>newArrayList(1, 12), Lists.<Integer>newArrayList(1, 16),
					Lists.<Integer>newArrayList(1, 17), Lists.<Integer>newArrayList(1, 18),
					// Row 2
					Lists.<Integer>newArrayList(2, 0), Lists.<Integer>newArrayList(2, 1),
					Lists.<Integer>newArrayList(2, 5), Lists.<Integer>newArrayList(2, 7),
					Lists.<Integer>newArrayList(2, 9), Lists.<Integer>newArrayList(2, 11),
					Lists.<Integer>newArrayList(2, 13), Lists.<Integer>newArrayList(2, 17),
					Lists.<Integer>newArrayList(2, 18), 
					// Row 3
					Lists.<Integer>newArrayList(3, 0), Lists.<Integer>newArrayList(3, 4),
					Lists.<Integer>newArrayList(3, 6), Lists.<Integer>newArrayList(3, 8),
					Lists.<Integer>newArrayList(3, 10), Lists.<Integer>newArrayList(3, 12),
					Lists.<Integer>newArrayList(3, 14), Lists.<Integer>newArrayList(3, 18),
					// Row 4
					Lists.<Integer>newArrayList(4, 3), Lists.<Integer>newArrayList(4, 5),
					Lists.<Integer>newArrayList(4, 7), Lists.<Integer>newArrayList(4, 9),
					Lists.<Integer>newArrayList(4, 11), Lists.<Integer>newArrayList(4, 13),
					Lists.<Integer>newArrayList(4, 15),
					// Row 5
					Lists.<Integer>newArrayList(5, 2), Lists.<Integer>newArrayList(5, 4),
					Lists.<Integer>newArrayList(5, 6), Lists.<Integer>newArrayList(5, 8),
					Lists.<Integer>newArrayList(5, 10), Lists.<Integer>newArrayList(5, 12),
					Lists.<Integer>newArrayList(5, 14), Lists.<Integer>newArrayList(5, 16),
					// Row 6
					Lists.<Integer>newArrayList(6, 3), Lists.<Integer>newArrayList(6, 5),
					Lists.<Integer>newArrayList(6, 7), Lists.<Integer>newArrayList(6, 9),
					Lists.<Integer>newArrayList(6, 11), Lists.<Integer>newArrayList(6, 13),
					Lists.<Integer>newArrayList(6, 15),
					// Row 7
					Lists.<Integer>newArrayList(7, 0), Lists.<Integer>newArrayList(7, 4),
					Lists.<Integer>newArrayList(7, 6), Lists.<Integer>newArrayList(7, 8),
					Lists.<Integer>newArrayList(7, 10), Lists.<Integer>newArrayList(7, 12),
					Lists.<Integer>newArrayList(7, 14), Lists.<Integer>newArrayList(7, 18),
					// Row 8
					Lists.<Integer>newArrayList(8, 0), Lists.<Integer>newArrayList(8, 1),
					Lists.<Integer>newArrayList(8, 5), Lists.<Integer>newArrayList(8, 7),
					Lists.<Integer>newArrayList(8, 9), Lists.<Integer>newArrayList(8, 11),
					Lists.<Integer>newArrayList(8, 13), Lists.<Integer>newArrayList(8, 17),
					Lists.<Integer>newArrayList(8, 18), 
					// Row 9
					Lists.<Integer>newArrayList(9, 0), Lists.<Integer>newArrayList(9, 1),
					Lists.<Integer>newArrayList(9, 2), Lists.<Integer>newArrayList(9, 6),
					Lists.<Integer>newArrayList(9, 8), Lists.<Integer>newArrayList(9, 10),
					Lists.<Integer>newArrayList(9, 12), Lists.<Integer>newArrayList(9, 16),
					Lists.<Integer>newArrayList(9, 17), Lists.<Integer>newArrayList(9, 18),
					// Row 10
					Lists.<Integer>newArrayList(10, 0), Lists.<Integer>newArrayList(10, 1), 
					Lists.<Integer>newArrayList(10, 2), Lists.<Integer>newArrayList(10, 3),
					Lists.<Integer>newArrayList(10, 5), Lists.<Integer>newArrayList(10, 7), 
					Lists.<Integer>newArrayList(10, 9), Lists.<Integer>newArrayList(10, 11), 
					Lists.<Integer>newArrayList(10, 13), Lists.<Integer>newArrayList(10, 15), 
					Lists.<Integer>newArrayList(10, 16), Lists.<Integer>newArrayList(10, 17), 
					Lists.<Integer>newArrayList(10, 18)
			);
	
	/* 
	 * This is the matrix indexes which stand for the  when any side's 
	 * piece locates at it, that side will lose the game and the other side 
	 * will win.
	 */
	@SuppressWarnings("unchecked")
	public static final List<ArrayList<Integer>> scoreSquares = 
			Lists.<ArrayList<Integer>>newArrayList(
					// Row 0
					Lists.<Integer>newArrayList(0, 4), Lists.<Integer>newArrayList(0, 6), 
					Lists.<Integer>newArrayList(0, 8), Lists.<Integer>newArrayList(0, 10),
					Lists.<Integer>newArrayList(0, 12), Lists.<Integer>newArrayList(0, 14), 
					// Row 1
					Lists.<Integer>newArrayList(1, 3), Lists.<Integer>newArrayList(1, 4),
					Lists.<Integer>newArrayList(1, 14), Lists.<Integer>newArrayList(1, 15),
					// Row 2
					Lists.<Integer>newArrayList(2, 2), Lists.<Integer>newArrayList(2, 3),
					Lists.<Integer>newArrayList(2, 15), Lists.<Integer>newArrayList(2, 16), 
					// Row 3
					Lists.<Integer>newArrayList(3, 1), Lists.<Integer>newArrayList(3, 2),
					Lists.<Integer>newArrayList(3, 16), Lists.<Integer>newArrayList(3, 17),
					// Row 4
					Lists.<Integer>newArrayList(4, 0), Lists.<Integer>newArrayList(4, 1),
					Lists.<Integer>newArrayList(4, 17), Lists.<Integer>newArrayList(4, 18),
					// Row 5
					Lists.<Integer>newArrayList(5, 0), Lists.<Integer>newArrayList(5, 18),
					// Row 6
					Lists.<Integer>newArrayList(6, 0), Lists.<Integer>newArrayList(6, 1),
					Lists.<Integer>newArrayList(6, 17), Lists.<Integer>newArrayList(6, 18),
					// Row 7
					Lists.<Integer>newArrayList(7, 1), Lists.<Integer>newArrayList(7, 2),
					Lists.<Integer>newArrayList(7, 16), Lists.<Integer>newArrayList(7, 17),
					// Row 8
					Lists.<Integer>newArrayList(8, 2), Lists.<Integer>newArrayList(8, 3),
					Lists.<Integer>newArrayList(8, 15), Lists.<Integer>newArrayList(8, 16),
					// Row 9
					Lists.<Integer>newArrayList(9, 3), Lists.<Integer>newArrayList(9, 4),
					Lists.<Integer>newArrayList(9, 14), Lists.<Integer>newArrayList(9, 15),
					// Row 10
					Lists.<Integer>newArrayList(10, 4), Lists.<Integer>newArrayList(10, 6), 
					Lists.<Integer>newArrayList(10, 8), Lists.<Integer>newArrayList(10, 10),
					Lists.<Integer>newArrayList(10, 12), Lists.<Integer>newArrayList(10, 14)
			);
	
}
