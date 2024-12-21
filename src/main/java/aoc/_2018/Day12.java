package aoc._2018;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import shared.ResourceUtil;
import shared.Test;

public class Day12 {

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
