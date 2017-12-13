package aoc._2017;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;

import shared.ResourceUtil;
import shared.Test;

/**
 * @author Dan Fielding
 */
public class Day6 {

	/*
		--- Day 6: Memory Reallocation ---

		A debugger program here is having an issue: it is trying to repair a
		memory reallocation routine, but it keeps getting stuck in an infinite
		loop.

		In this area, there are sixteen memory banks; each memory bank can hold
		any number of blocks. The goal of the reallocation routine is to balance
		the blocks between the memory banks.

		The reallocation routine operates in cycles. In each cycle, it finds the
		memory bank with the most blocks (ties won by the lowest-numbered memory
		bank) and redistributes those blocks among the banks. To do this, it
		removes all of the blocks from the selected bank, then moves to the next
		(by index) memory bank and inserts one of the blocks. It continues doing
		this until it runs out of blocks; if it reaches the last memory bank, it
		wraps around to the first one.

		The debugger would like to know how many redistributions can be done
		before a blocks-in-banks configuration is produced that has been seen
		before.

		For example, imagine a scenario with only four memory banks:

		- The banks start with 0, 2, 7, and 0 blocks. The third bank has the
		    most blocks, so it is chosen for redistribution.
		- Starting with the next bank (the fourth bank) and then continuing to
		    the first bank, the second bank, and so on, the 7 blocks are spread
		    out over the memory banks. The fourth, first, and second banks get
		    two blocks each, and the third bank gets one back.
		    The final result looks like this: 2 4 1 2.
		- Next, the second bank is chosen because it contains the most blocks
		    (four). Because there are four memory banks, each gets one block.
		    The result is: 3 1 2 3.
		- Now, there is a tie between the first and fourth memory banks, both of
		    which have three blocks. The first bank wins the tie, and its three
		    blocks are distributed evenly over the other three banks, leaving it
		    with none: 0 2 3 4.
		- The fourth bank is chosen, and its four blocks are distributed such
		    that each of the four banks receives one: 1 3 4 1.
		- The third bank is chosen, and the same thing happens: 2 4 1 2.

		At this point, we've reached a state we've seen before: 2 4 1 2 was
		already seen. The infinite loop is detected after the fifth block
		redistribution cycle, and so the answer in this example is 5.

		Given the initial block counts in your puzzle input, how many
		redistribution cycles must be completed before a configuration is
		produced that has been seen before?
	*/

	/*
		--- Part Two ---

		Out of curiosity, the debugger would also like to know the size of the
		loop: starting from a state that has already been seen, how many block
		redistribution cycles must be performed before that same state is seen
		again?

		In the example above, 2 4 1 2 is seen again after four cycles, and so
		the answer in that example would be 4.

		How many cycles are in the infinite loop that arises from the
		configuration in your puzzle input?
	*/

	class Result {
		int steps;
		List<Integer> state;

		public Result(int steps, List<Integer> state) {
			this.steps = steps;
			this.state = state;
		}
	}

	public List<Integer> redistribute(List<Integer> input) {
		List<Integer> result = new ArrayList<>(input);
		// Find biggest
		int max = Integer.MIN_VALUE;
		int maxIndex = -1;
		for (int i = 0; i < result.size(); i++) {
			if (result.get(i) > max) {
				max = result.get(i);
				maxIndex = i;
			}
		}
		// Share the load
		result.set(maxIndex, 0);
		int current = maxIndex;
		while (max > 0) {
			max--;
			current = (current + 1) % result.size();
			result.set(current, result.get(current) + 1);
		}
		return result;
	}

	public Result eval(List<Integer> input) {
		Set<List<Integer>> history = new HashSet<>();
		List<Integer> current = input;
		while (!history.contains(current)) {
			history.add(current);
			current = redistribute(current);
		}
		return new Result(history.size(), current);
	}

	public void examples() {
		Test.assertEqual(eval(ImmutableList.of(0, 2, 7, 0)).steps, 5);
		Test.assertEqual(eval(ImmutableList.of(2, 4, 1, 2)).steps, 4);
	}

	public static void main(String[] args) throws Exception {
		Day6 day = new Day6();
		day.examples();

		List<String> lines = ResourceUtil.readAllLines("2017/day6.input");
		List<Integer> input = Arrays.stream(lines.get(0).split("\\t"))
				.map(Integer::valueOf)
				.collect(Collectors.toList());

		Result result1 = day.eval(input);
		System.out.println(result1.steps);

		Result result2 = day.eval(result1.state);
		System.out.println(result2.steps);
	}

}
