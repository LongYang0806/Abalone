package com.longyang.abalone.test;

import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.List;

import org.abalone.api.Jump;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.google.common.collect.Lists;

@RunWith(JUnit4.class)
public class JumpTest {
	private List<Jump> toBeSortedJumps;
	private List<Jump> sortedJumps;
	
	@Before
	public void setUp(){
		toBeSortedJumps = Lists.newLinkedList();
		toBeSortedJumps.add(new Jump(3, 5, 2, 6));
		toBeSortedJumps.add(new Jump(4, 4, 3, 5));
		toBeSortedJumps.add(new Jump(2, 6, 1, 7));
		
		sortedJumps = Lists.newLinkedList();
		sortedJumps.add(new Jump(2, 6, 1, 7));
		sortedJumps.add(new Jump(3, 5, 2, 6));
		sortedJumps.add(new Jump(4, 4, 3, 5));
		
	}
	
	@Test
	public void testCompareToAndEquals() {
		Collections.sort(toBeSortedJumps);
		assertTrue(toBeSortedJumps.equals(sortedJumps));
	}

}
