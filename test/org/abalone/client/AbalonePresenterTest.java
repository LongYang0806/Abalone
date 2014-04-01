package org.abalone.client;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;

public class AbalonePresenterTest {

	@Test
	public void optionalUsageCaseTest() {
		Optional<String> currentTurn = Optional.<String>absent();
		currentTurn = Optional.fromNullable("WTURN");
		assertTrue(!currentTurn.isPresent() || currentTurn.isPresent() && currentTurn.get().equals("WTURN"));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void getDirectionTest() {
		List<ArrayList<Integer>> jumps = Lists.<ArrayList<Integer>>newArrayList(
				Lists.<Integer>newArrayList(8, 8, 7, 9, 0), 
				Lists.<Integer>newArrayList(9, 7, 8, 8, 0));
		assertEquals(AbalonePresenter.getJumpDirection(jumps), 
				AbalonePresenter.Direction.UPPER_RIGHT_DIAGONAL);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void comparableTest() {
		List<ArrayList<Integer>> jumps = Lists.<ArrayList<Integer>>newArrayList(
				Lists.<Integer>newArrayList(2, 8, 3, 7, 0),
				Lists.<Integer>newArrayList(3, 7, 4, 6, 0),
				Lists.<Integer>newArrayList(1, 9, 2, 8, 0));
		List<ArrayList<Integer>> targetJumps = Lists.<ArrayList<Integer>>newArrayList(
				Lists.<Integer>newArrayList(3, 7, 4, 6, 0),
				Lists.<Integer>newArrayList(2, 8, 3, 7, 0),
				Lists.<Integer>newArrayList(1, 9, 2, 8, 0));
		
		Collections.sort(jumps, AbalonePresenter.jumpComparator);
		
		for(int i = 0; i < jumps.size(); i++) {
			System.out.println(jumps.get(i));
			assertEquals(jumps.get(i), targetJumps.get(i));
		}
	}
}
