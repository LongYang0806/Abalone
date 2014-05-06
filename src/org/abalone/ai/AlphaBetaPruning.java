// Copyright 2012 Google Inc.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
// //////////////////////////////////////////////////////////////////////////////

package org.abalone.ai;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.abalone.client.AbaloneConstants;
import org.abalone.client.AbaloneState;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;

/**
 * http://en.wikipedia.org/wiki/Alpha-beta_pruning<br>
 * This algorithm performs both A* and alpha-beta pruning.<br>
 * The set of possible moves is maintained ordered by the current heuristic value of each move. We
 * first use depth=1, and update the heuristic value of each move, then use depth=2, and so on until
 * we get a timeout or reach maximum depth. <br>
 * If a state has {@link TurnBasedState#whoseTurn} null (which happens in backgammon when we should
 * roll the dice), then I treat all the possible moves with equal probabilities. <br>
 * 
 * @author yzibin@google.com (Yoav Zibin)
 */
public class AlphaBetaPruning {
  static class TimeoutException extends RuntimeException {

    private static final long serialVersionUID = 1L;
  }

  static class MoveScore implements Comparable<MoveScore> {
  	// Five integers list represents a move/jump: {orginal_x, orginal_y, dest_x, dest_y, color}
    ArrayList<ArrayList<Integer>> move;
    int score;

    @Override
    public int compareTo(MoveScore o) {
      return o.score - score; // sort DESC (best score first)
    }
  }

  private Heuristic heuristic;

  public AlphaBetaPruning(Heuristic heuristic, AbaloneState state) {
    this.heuristic = heuristic;
  }

  public ArrayList<ArrayList<Integer>> findBestMove(AbaloneState state, int depth, Timer timer) {
    boolean isWhite = state.getTurn().equals(AbaloneConstants.WTurn);
    // Do iterative deepening (A*), and slow get better heuristic values for the states.
    List<MoveScore> scores = Lists.newArrayList();
    {
      List<ArrayList<ArrayList<Integer>>> possibleMoves = heuristic.getOrderedMoves(state);
      for (ArrayList<ArrayList<Integer>> move : possibleMoves) {
        MoveScore score = new MoveScore();
        score.move = move;
        score.score = Integer.MIN_VALUE;
        scores.add(score);
      }
    }

    try {
      for (int i = 0; i < depth; i++) {
        for (MoveScore moveScore : scores) {
          ArrayList<ArrayList<Integer>> move = moveScore.move;
          int score =
              findMoveScore(state.applyJumpOnBoard(move), i, Integer.MIN_VALUE, 
              		Integer.MAX_VALUE, timer);
          if (!isWhite) {
            // the scores are from the point of view of the white, so for black we need to switch.
            score = -score;
          }
          moveScore.score = score;
        }
        Collections.sort(scores); // This will give better pruning on the next iteration.
      }
    } catch (TimeoutException e) {
      // OK, it should happen
    }

    Collections.sort(scores);
    return scores.get(0).move;
  }

  /**
   * If we get a timeout, then the score is invalid.
   */
  private int findMoveScore(AbaloneState state, int depth, int alpha, int beta, Timer timer)
      throws TimeoutException {
    if (timer.didTimeout()) {
      throw new TimeoutException();
    }
    Optional<Boolean> over = state.getIsGameEnd();
    if (depth == 0 || over.isPresent()) {
      return heuristic.getStateValue(state);
    }
    String color = state.getTurn();
    int scoreSum = 0;
    int count = 0;
    List<ArrayList<ArrayList<Integer>>> possibleMoves = heuristic.getOrderedMoves(state);
    for (ArrayList<ArrayList<Integer>> move : possibleMoves) {
      count++;
      int childScore = findMoveScore(state.applyJumpOnBoard(move), depth - 1, alpha, beta, timer);
      if (color == null) {
        scoreSum += childScore;
      } else if (color.equals(AbaloneConstants.WTurn)) {
        alpha = Math.max(alpha, childScore);
        if (beta <= alpha) {
          break;
        }
      } else {
        beta = Math.min(beta, childScore);
        if (beta <= alpha) {
          break;
        }
      }
    }
    return color == null ? scoreSum / count : color.equals(AbaloneConstants.WTurn) ? alpha : beta;
  }
}
