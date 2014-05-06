package org.abalone.ai;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.abalone.client.AbaloneConstants;
import org.abalone.client.AbalonePresenter;
import org.abalone.client.AbalonePresenter.Direction;
import org.abalone.client.AbaloneState;

import com.google.gwt.thirdparty.guava.common.collect.Lists;

public class HeuristicImpl implements Heuristic {
	// constants stands for move scores.
	private final int oneToZero = 1000;
	private final int twoToZero = 2000;
	private final int threeToZero = 5000;
	private final int twoToOne = 10000;
	private final int threeToOne = 20000;
	private final int threeToTwo = 50000;
	
	private List<ArrayList<ArrayList<Integer>>> oneToZeroJumps = Lists.newArrayList();
	private List<ArrayList<ArrayList<Integer>>> twoToZeroJumps = Lists.newArrayList();
	private List<ArrayList<ArrayList<Integer>>> threeToZeroJumps = Lists.newArrayList();
	private List<ArrayList<ArrayList<Integer>>> twoToOneJumps = Lists.newArrayList();
	private List<ArrayList<ArrayList<Integer>>> threeToOneJumps = Lists.newArrayList();
	private List<ArrayList<ArrayList<Integer>>> threeToTwoJumps = Lists.newArrayList();
	private List<ArrayList<ArrayList<Integer>>> winningJumps = Lists.newArrayList();
	
	
	@Override
	public int getStateValue(AbaloneState state) {
		if (state.getIsGameEnd().isPresent()) {
			if (state.getTurn().equals(AbaloneConstants.WTurn)) {
				return Integer.MAX_VALUE;
			} else {
				return Integer.MIN_VALUE;
			}
		}
		
		String turn = state.getTurn();
		getAllPossibleMoves(state);
		int totalScore = 0;
		if(!winningJumps.isEmpty()) {
			totalScore = Integer.MAX_VALUE;
		} else {
			totalScore += 
					oneToZero * oneToZeroJumps.size() +
					twoToZero * twoToZeroJumps.size() +
					threeToZero * threeToZeroJumps.size() +
					twoToOne * twoToOneJumps.size() +
					threeToOne * threeToOneJumps.size() +
					threeToTwo * threeToTwoJumps.size();
		}
		if (turn.equals(AbaloneConstants.WTurn)) {
			return totalScore;
		} else {
			if (totalScore == Integer.MAX_VALUE) {
				return Integer.MIN_VALUE;
			} else {
				return -totalScore;
			}
		}
		
	}

	@Override
	public List<ArrayList<ArrayList<Integer>>> getOrderedMoves(AbaloneState state) {
		getAllPossibleMoves(state);
		List<ArrayList<ArrayList<Integer>>> orderedMoves = Lists.newArrayList();
		orderedMoves.addAll(winningJumps);
		orderedMoves.addAll(threeToTwoJumps);
		orderedMoves.addAll(threeToOneJumps);
		orderedMoves.addAll(twoToOneJumps);
		orderedMoves.addAll(threeToZeroJumps);
		orderedMoves.addAll(twoToZeroJumps);
		orderedMoves.addAll(oneToZeroJumps);
		return orderedMoves;
	}
	
	@SuppressWarnings("unchecked")
	private void getAllPossibleMoves(AbaloneState state) {
		String turn = state.getTurn();
		List<ArrayList<String>> board = state.getBoard();
		String myColor = 
				turn.equals(AbaloneConstants.WTurn) ? AbaloneConstants.W : AbaloneConstants.B;
//		String opponentColor = 
//				myColor.equals(AbaloneConstants.W) ? AbaloneConstants.B : AbaloneConstants.W;
		AbalonePresenter presenter = new AbalonePresenter();
		boolean[][] holdableMatrix = presenter.getEnableSquares(board, turn);
		for (int i = 0; i < AbaloneConstants.BoardRowNum; i++) {
			for (int j = 0; j < AbaloneConstants.BoardColNum; j++) {
				boolean isHoldable = holdableMatrix[i][j];
				if (isHoldable) {
					boolean[][] placableMatrix = presenter.getPlacableMatrix(i, j);
					for (int k = 0; k < AbaloneConstants.BoardRowNum; k++) {
						for (int t = 0; t < AbaloneConstants.BoardColNum; t++) {
							boolean isPlacable = placableMatrix[k][t];
							if (isPlacable) {
								List<ArrayList<Integer>> jumps = Lists.<ArrayList<Integer>>newArrayList(
										Lists.newArrayList(i, j, k, t, myColor.equals(AbaloneConstants.W) ? 0 : 1));
								Direction direction = AbalonePresenter.getJumpDirection(jumps);
								getMoveOnDirection(i, j, myColor, direction, board);
							}
						}
					}
				}
			}
		}
	}
	
	private void getMoveOnDirection (int x, int y, String color, Direction direction, 
			List<ArrayList<String>> board) {
		if (x < 0 || y < 0 || color == null || color.isEmpty()) {
			throw new IllegalArgumentException("Illegal argument provided!");
		}
		int piece = color.equals(AbaloneConstants.W) ? 0: 1;
		int myCount = 0;
		int opponentCount = 0;
		List<String> row = Lists.newArrayList();
		int i = 0;
		int j = 0;
		ArrayList<ArrayList<Integer>> jumps = Lists.<ArrayList<Integer>>newArrayList(); 
		switch(direction) {
			case LEFT_HORIZONTAL:
				row = board.get(x);
				i = y + 2;
				myCount++;
				while(i < AbaloneConstants.BoardColNum && 
							!row.get(i + 1).equals(AbaloneConstants.S) &&
							!row.get(i).equals(AbaloneConstants.E)) {
					if(row.get(i).equals(color)) {
						myCount++;
					} else {
						opponentCount++;
					}
					jumps.add(Lists.newArrayList(x, i - 2, x, i, piece));
					i += 2;
				}
				if(row.get(i).equals(AbaloneConstants.E)) {
					addToCertainJumpGroup(jumps, myCount, opponentCount);
				} else if (row.get(i + 1).equals(AbaloneConstants.S)) {
					jumps.add(Lists.newArrayList(x, i - 2, x, i, piece));
					Collections.sort(jumps, AbalonePresenter.jumpComparator);
					winningJumps.add(jumps);
				}
				break;
			case RIGHT_HORIZONTAL:
				row = board.get(x);
				i = y - 2;
				myCount++;
				while(i >= 0 && !row.get(i - 1).equals(AbaloneConstants.S) &&
							!row.get(i).equals(AbaloneConstants.E)) {
					if (row.get(i).equals(color)) {
						myCount++;
					} else {
						opponentCount++;
					}
					jumps.add(Lists.newArrayList(x, i + 2, x, i, piece));
					i -= 2;
 				}
				if(row.get(i).equals(AbaloneConstants.E)) {
					addToCertainJumpGroup(jumps, myCount, opponentCount);
				} else if (row.get(i - 1).equals(AbaloneConstants.S)) {
					jumps.add(Lists.newArrayList(x, i + 2, x, i, piece));
					Collections.sort(jumps, AbalonePresenter.jumpComparator);
					winningJumps.add(jumps);				
				}
				break;
			case UPPER_LEFT_DIAGONAL:
				i = x - 1;
				j = y - 1;
				myCount++;
				while(i >= 0 && j >= 0 && 
							!board.get(i).get(j).equals(AbaloneConstants.E) &&
							!board.get(i).get(j).equals(AbaloneConstants.S)) {
					if(board.get(i).get(j).equals(color)) {
						myCount++;
					} else {
						opponentCount++;
					}
					jumps.add(Lists.newArrayList(i + 1, j + 1, i, j, piece));
					i--;
					j--;
				}
				if (board.get(i).get(j).equals(AbaloneConstants.E)) {
					addToCertainJumpGroup(jumps, myCount, opponentCount); 
				} else if (board.get(i).get(j).equals(AbaloneConstants.S)) {
					Collections.sort(jumps, AbalonePresenter.jumpComparator);
					winningJumps.add(jumps);
				}
				break;
			case UPPER_RIGHT_DIAGONAL:
				i = x - 1;
				j = y + 1;
				myCount++;
				while(i >= 0 && j < AbaloneConstants.BoardColNum && 
							!board.get(i).get(j).equals(AbaloneConstants.E) &&
							!board.get(i).get(j).equals(AbaloneConstants.S)) {
					if(board.get(i).get(j).equals(color)) {
						myCount++;
					} else {
						opponentCount++;
					}
					jumps.add(Lists.newArrayList(i + 1, j - 1, i, j, piece));
					i--;
					j++;
				}
				if (board.get(i).get(j).equals(AbaloneConstants.E)) {
					addToCertainJumpGroup(jumps, myCount, opponentCount); 
				} else if (board.get(i).get(j).equals(AbaloneConstants.S)) {
					Collections.sort(jumps, AbalonePresenter.jumpComparator);
					winningJumps.add(jumps);
				}
				break;
			case LOWER_LEFT_DIAGONAL:
				i = x + 1;
				j = y - 1;
				myCount++;
				while(i >= 0 && j >= 0 && 
							!board.get(i).get(j).equals(AbaloneConstants.E) &&
							!board.get(i).get(j).equals(AbaloneConstants.S)) {
					if(board.get(i).get(j).equals(color)) {
						myCount++;
					} else {
						opponentCount++;
					}
					jumps.add(Lists.newArrayList(i - 1, j + 1, i, j, piece));
					i++;
					j--;
				}
				if (board.get(i).get(j).equals(AbaloneConstants.E)) {
					addToCertainJumpGroup(jumps, myCount, opponentCount); 
				} else if (board.get(i).get(j).equals(AbaloneConstants.S)) {
					Collections.sort(jumps, AbalonePresenter.jumpComparator);
					winningJumps.add(jumps);
				}
				break;
			case LOWER_RIGHT_DIAGONAL:
				i = x + 1;
				j = y + 1;
				myCount++;
				while(i >= 0 && j >= 0 && 
							!board.get(i).get(j).equals(AbaloneConstants.E) &&
							!board.get(i).get(j).equals(AbaloneConstants.S)) {
					if(board.get(i).get(j).equals(color)) {
						myCount++;
					} else {
						opponentCount++;
					}
					jumps.add(Lists.newArrayList(i - 1, j - 1, i, j, piece));
					i++;
					j++;
				}
				if (board.get(i).get(j).equals(AbaloneConstants.E)) {
					addToCertainJumpGroup(jumps, myCount, opponentCount); 
				} else if (board.get(i).get(j).equals(AbaloneConstants.S)) {
					Collections.sort(jumps, AbalonePresenter.jumpComparator);
					winningJumps.add(jumps);
				}
				break;
			default:
				break;
		}
	}
	
	private void addToCertainJumpGroup(ArrayList<ArrayList<Integer>> jumps,
			int myCount, int opponentCount) {
		Collections.sort(jumps, AbalonePresenter.jumpComparator);
		if(myCount == 1 && opponentCount == 0) {
			oneToZeroJumps.add(jumps);
		} else if (myCount == 2 && opponentCount == 0) {
			twoToZeroJumps.add(jumps);
		} else if (myCount == 3 && opponentCount == 0) {
			threeToZeroJumps.add(jumps);
		} else if (myCount == 2 && opponentCount == 1) {
			twoToOneJumps.add(jumps);
		} else if (myCount == 3 && opponentCount == 1) {
			threeToOneJumps.add(jumps);
		} else if (myCount == 3 && opponentCount == 2) {
			threeToTwoJumps.add(jumps);
		}
	}
}
