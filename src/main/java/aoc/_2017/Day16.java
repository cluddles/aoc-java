package aoc._2017;

import com.google.common.collect.ImmutableList;
import shared.ResourceUtil;
import shared.Test;
import shared.Timer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Dan Fielding
 */
public class Day16 {

	/*

	--- Day 16: Permutation Promenade ---

	You come upon a very unusual sight; a group of programs here appear to be
	dancing.

	There are sixteen programs in total, named a through p. They start by
	standing in a line: a stands in position 0, b stands in position 1, and so
	on until p, which stands in position 15.

	The programs' dance consists of a sequence of dance moves:

	- Spin, written sX, makes X programs move from the end to the front, but
	  maintain their order otherwise. (For example, s3 on abcde produces cdeab).
	- Exchange, written xA/B, makes the programs at positions A and B swap
	  places.
	- Partner, written pA/B, makes the programs named A and B swap places.

	For example, with only five programs standing in a line (abcde), they could
	do the following dance:

	- s1, a spin of size 1: eabcd.
	- x3/4, swapping the last two programs: eabdc.
	- pe/b, swapping programs e and b: baedc.

	After finishing their dance, the programs end up in order baedc.

	You watch the dance for a while and record their dance moves (your puzzle
	input). In what order are the programs standing after their dance?


	--- Part Two ---

	Now that you're starting to get a feel for the dance moves, you turn your
	attention to the dance as a whole.

	Keeping the positions they ended up in from their previous dance, the
	programs perform it again and again: including the first dance, a total of
	one billion (1000000000) times.

	In the example above, their second dance would begin with the order baedc,
	and use the same dance moves:

	- s1, a spin of size 1: cbaed.
	- x3/4, swapping the last two programs: cbade.
	- pe/b, swapping programs e and b: ceadb.

	In what order are the programs standing after their billion dances?

	 */

	public void spin(StringBuilder input, int move) {
		move = move % input.length();
		if (move == 0) return;
		if (move < 0) move = input.length() + move;
		int pivot = input.length() - move;
		input.replace(0, input.length(),
				input.substring(pivot) + input.substring(0, pivot));
	}

	public void swap(StringBuilder input, int p1, int p2) {
		if (p1 == p2) return;
		char c1 = input.charAt(p1);
		input.setCharAt(p1, input.charAt(p2));
		input.setCharAt(p2, c1);
	}

	public void move(StringBuilder input, String move) {
		char op = move.charAt(0);
		if (op == 's') {
			spin(input, Integer.valueOf(move.substring(1)));
		} else {
			String[] split = move.substring(1).split("/");
			if (op == 'x') {
				swap(input, Integer.valueOf(split[0]), Integer.valueOf(split[1]));
			} else {
				swap(input, input.indexOf(split[0]), input.indexOf(split[1]));
			}
		}
	}

	public String dance(String start, List<String> moves) {
		StringBuilder current = new StringBuilder(start);
		for (String move : moves) {
			move(current, move);
		}
		return current.toString();
	}

	public void examples() {
		List<String> input = ImmutableList.of(
				"s1",
				"x3/4",
				"pe/b"
		);
		Test.assertEqual(dance("abcde", input), "baedc");
	}

	public static void main(String[] args) throws Exception {
		Day16 day = new Day16();
		day.examples();

		String start = "abcdefghijklmnop";
		List<String> input = Arrays.asList(
				ResourceUtil.readString("2017/day16.input").split(","));

		// Part 1
		Timer.start();
		System.out.println(day.dance(start, input));
		Timer.endMessage();

		// Part 2
		// Running 10000 iterations takes ages, so good luck with a billion -
		// just look for loops, then work out result from the remainder
		Timer.start();
		int numRepetitions = 1_000_000_000;
		List<String> prev = new ArrayList<>();
		int loopSize = -1;
		String current = start;
		for (int i = 0; i < numRepetitions; i++) {
			if (i != 0 && current.equals(start)) {
				loopSize = i;
				break;
			}
			prev.add(current);
			current = day.dance(current, input);
		}

		int offset = numRepetitions % loopSize;
		System.out.println("loopSize " + loopSize + ", offset " + offset);
		System.out.println(prev.get(offset));
		Timer.endMessage();
	}

}
