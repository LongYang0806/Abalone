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
import java.util.List;

import org.abalone.client.AbaloneState;


/**
 * A heuristic that assigns values for states, and to determine the order in which we visit the next
 * states. <br>
 * A heuristic is used in {@link AlphaBetaPruning} to explore the state tree.
 * 
 * @author yzibin@google.com (Yoav Zibin)
 */
public interface Heuristic {
  /**
   * What is the value of the state for the white player. Higher value means the white has a better
   * position. When the white wins you can return {@link Integer#MAX_VALUE}, and when the white
   * loses you can return {@link Integer#MIN_VALUE}.
   */
  int getStateValue(AbaloneState state);

  /**
   * Returns the order in which we should explore the next states. An optimal heuristic would return
   * the best state (for the current player) first, e.g., if it is the white turn then it should
   * return first a state which is the best for white. For instance, in chess and checkers you
   * should return first states where a piece was captured, because a capturing move has the
   * potential of being better than a simple move without capture. <br>
   * Moreover, the heuristic can decide not to even return some possible moves if they are obviously
   * "bad", or if they are duplicates (e.g., in backgammon for a 5-6 roll, we return both 5->6,
   * 6->5, or directly moving 11 points. If the 3 options result in the same final state, then it is
   * enough to consider just one of the moves). Another example is the doubling-cube move that we
   * should not consider in our simple AIs. <br>
   * The return type is {@code Iterable} and not {@code List} because we might not need all the next
   * states due to pruning. For example, if alpha-beta pruning cut of a branch we can stop iterating
   * over the next states.<br>
   */
  List<ArrayList<ArrayList<Integer>>> getOrderedMoves(AbaloneState state);
}
