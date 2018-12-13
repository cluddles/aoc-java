package aoc._2018;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import shared.ResourceUtil;
import shared.Test;

/**
 * @author Dan Fielding
 */
public class Day12 {
/*
--- Day 12: Subterranean Sustainability ---

The year 518 is significantly more underground than your history books implied.
Either that, or you've arrived in a vast cavern network under the North Pole.

After exploring a little, you discover a long tunnel that contains a row of
small pots as far as you can see to your left and right. A few of them contain
plants - someone is trying to grow things in these geothermally-heated caves.

The pots are numbered, with 0 in front of you. To the left, the pots are
numbered -1, -2, -3, and so on; to the right, 1, 2, 3.... Your puzzle input
contains a list of pots from 0 to the right and whether they do (#) or do not
(.) currently contain a plant, the initial state. (No other pots currently
contain plants.) For example, an initial state of #..##.... indicates that pots
0, 3, and 4 currently contain plants.

Your puzzle input also contains some notes you find on a nearby table: someone
has been trying to figure out how these plants spread to nearby pots. Based on
the notes, for each generation of plants, a given pot has or does not have a
plant based on whether that pot (and the two pots on either side of it) had a
plant in the last generation. These are written as LLCRR => N, where L are pots
to the left, C is the current pot being considered, R are the pots to the right,
and N is whether the current pot will have a plant in the next generation.

For example:

    A note like ..#.. => . means that a pot that contains a plant but with no
        plants within two pots of it will not have a plant in it during the next
        generation.
    A note like ##.## => . means that an empty pot with two plants on each side
        of it will remain empty in the next generation.
    A note like .##.# => # means that a pot has a plant in a given generation
        if, in the previous generation, there were plants in that pot, the one
        immediately to the left, and the one two pots to the right, but not in
        the ones immediately to the right and two to the left.

It's not clear what these plants are for, but you're sure it's important, so
you'd like to make sure the current configuration of plants is sustainable by
determining what will happen after 20 generations.

For example, given the following input:

initial state: #..#.#..##......###...###

...## => #
..#.. => #
.#... => #
.#.#. => #
.#.## => #
.##.. => #
.#### => #
#.#.# => #
#.### => #
##.#. => #
##.## => #
###.. => #
###.# => #
####. => #

For brevity, in this example, only the combinations which do produce a plant are
listed. (Your input includes all possible combinations.) Then, the next 20
generations will look like this:

                 1         2         3
       0         0         0         0
 0: ...#..#.#..##......###...###...........
 1: ...#...#....#.....#..#..#..#...........
 2: ...##..##...##....#..#..#..##..........
 3: ..#.#...#..#.#....#..#..#...#..........
 4: ...#.#..#...#.#...#..#..##..##.........
 5: ....#...##...#.#..#..#...#...#.........
 6: ....##.#.#....#...#..##..##..##........
 7: ...#..###.#...##..#...#...#...#........
 8: ...#....##.#.#.#..##..##..##..##.......
 9: ...##..#..#####....#...#...#...#.......
10: ..#.#..#...#.##....##..##..##..##......
11: ...#...##...#.#...#.#...#...#...#......
12: ...##.#.#....#.#...#.#..##..##..##.....
13: ..#..###.#....#.#...#....#...#...#.....
14: ..#....##.#....#.#..##...##..##..##....
15: ..##..#..#.#....#....#..#.#...#...#....
16: .#.#..#...#.#...##...#...#.#..##..##...
17: ..#...##...#.#.#.#...##...#....#...#...
18: ..##.#.#....#####.#.#.#...##...##..##..
19: .#..###.#..#.#.#######.#.#.#..#.#...#..
20: .#....##....#####...#######....#.#..##.

The generation is shown along the left, where 0 is the initial state. The pot
numbers are shown along the top, where 0 labels the center pot,
negative-numbered pots extend to the left, and positive pots extend toward the
right. Remember, the initial state begins at pot 0, which is not the leftmost
pot used in this example.

After one generation, only seven plants remain. The one in pot 0 matched the
rule looking for ..#.., the one in pot 4 matched the rule looking for .#.#.,
pot 9 matched .##.., and so on.

In this example, after 20 generations, the pots shown as # contain plants, the
furthest left of which is pot -2, and the furthest right of which is pot 34.
Adding up all the numbers of plant-containing pots after the 20th generation
produces 325.

After 20 generations, what is the sum of the numbers of all pots which contain a
plant?

--- Part Two ---

You realize that 20 generations aren't enough. After all, these plants will need
to last another 1500 years to even reach your timeline, not to mention your
future.

After fifty billion (50000000000) generations, what is the sum of the numbers of
all pots which contain a plant?

 */

	private static final Character SET   = '#';
	private static final Character UNSET = '.';

	static class State {
		final String pots;
		final long   firstPot;
		State(String pots, long firstPot) {
			this.pots     = pots;
			this.firstPot = firstPot;
		}
	}

	private State parseInitialState(List<String> input) {
		return new State(input.get(0).replaceFirst("initial state: ", ""), 0);
	}

	private Map<String, Character> parseRules(List<String> input) {
		Map<String, Character> result = new HashMap<>();
		for (String line : input) {
			String[] split = line.split(" => ");
			if (split.length != 2) continue;
			result.put(split[0], split[1].charAt(0));
		}
		return result;
	}

	private State trim(State state) {
		int i;
		for (i = 0; i < state.pots.length(); i++) {
			if (state.pots.toCharArray()[i] == SET) break;
		}
		int j;
		for (j = state.pots.length() - 1; j >= 0; j--) {
			if (state.pots.toCharArray()[j] == SET) break;
		}
		return new State(state.pots.substring(i, j+1), state.firstPot + i);
	}

	private long stateScore(State state) {
		long result = 0;
		for (int i = 0; i < state.pots.length(); i++) {
			char c = state.pots.toCharArray()[i];
			if (c == SET) result += i + state.firstPot;
		}
		return result;
	}

	private String subState(State state, int start, int length) {
		StringBuilder result = new StringBuilder();
		for (int i = start; i < start + length; i++) {
			if (i < state.firstPot || i >= state.firstPot + state.pots.length()) {
				result.append(UNSET);
			} else {
				result.append(state.pots.toCharArray()[i - (int) state.firstPot]);
			}
		}
		return result.toString();
	}

	public int evalPart1(List<String> input, int numGenerations) {
		State state = parseInitialState(input);
		Map<String, Character> rules = parseRules(input);
		for (long i = 0; i < numGenerations; i++) {
			StringBuilder pots = new StringBuilder();
			int first = (int) state.firstPot;
			for (int j = first - 4; j < first + state.pots.length() + 4; j++) {
				String subState = subState(state, j, 5);
//				System.out.println(subState);
				Character match = rules.getOrDefault(subState, UNSET);
				pots.append(match);
			}
			state = new State(pots.toString(), state.firstPot - 2);
			state = trim(state);
			System.out.println((i + 1) + ":" + state.pots + ":" + state.firstPot);
		}
		return (int) stateScore(state);
	}

	public static void main(String[] args) throws Exception {
		Day12 solver = new Day12();
		List<String> example = ResourceUtil.readAllLines("2018/day12.example");
		List<String> input   = ResourceUtil.readAllLines("2018/day12.input");

		// Part 1
		// Examples
		Test.check(solver.stateScore(
				new State("...#..#.#..##......###...###...........", -3)),
				145L); // 3 + 5 + 8 + 9 + 16 + 17 + 18 + 22 + 23 + 24
		Test.check(solver.stateScore(
				new State(".#....##....#####...#######....#.#..##.", -3)),
				325L);
		Test.check(solver.evalPart1(example, 20), 325);
		// Vs Input
		System.out.println(solver.evalPart1(input, 20));

		// Part 2
		// Vs Input
		// System.out.println(solver.evalPart1(input, 50000000000L));
		// 62055:##......##.............................................................##.#.#..##............##.#..##:62038
		System.out.println(solver.stateScore(
				new State("##......##.............................................................##.#.#..##............##.#..##", 50000000000L - 17)));
		// Failures: 750000000712 (too high)
		// Failures: 750000000711 (too high)
	}
}
