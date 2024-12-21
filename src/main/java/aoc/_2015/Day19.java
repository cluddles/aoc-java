package aoc._2015;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.base.Strings;

import shared.ResourceUtil;

public class Day19 {

	private static final Pattern RULE_PATTERN = Pattern.compile("(\\w*) \\=\\> (\\w*)");

	private static class Rule {
		final String from;
		final String to;

		private Rule(String rule) {
			Matcher matcher = RULE_PATTERN.matcher(rule);
			if (matcher.matches()) {
				this.from = matcher.group(1);
				this.to = matcher.group(2);
			} else {
				throw new AssertionError("Invalid rule: " + rule);
			}
		}
		private Rule(String from, String to) {
			this.from = from;
			this.to = to;
		}

		private Rule invert() {
			return new Rule(to, from);
		}
	}

	private static class State {
		final String molecule;
		final int g;
		final int h;

		private State(String molecule, int g, String target) {
			this.molecule = molecule;
			this.g = g;
			this.h = heuristic(molecule, target);
		}

		int f() {
			return g+h;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			State state = (State) o;
			return Objects.equal(molecule, state.molecule);
		}

		@Override
		public int hashCode() {
			return Objects.hashCode(molecule);
		}

		@Override
		public String toString() {
			return MoreObjects.toStringHelper(this)
					.add("molecule", molecule)
					.add("g", g)
					.add("h", h)
					.toString();
		}
	}

	public void run() throws IOException {
		List<String> lines = ResourceUtil.readAllLines("2015/day19.input");

		List<Rule> rules = new ArrayList<>();
		String molecule;

		for (int i = 0; i < lines.size() - 1; i++) {
			String ruleText = lines.get(i);
			if (Strings.isNullOrEmpty(ruleText)) continue;

			rules.add(new Rule(ruleText));
		}

		molecule = lines.get(lines.size() - 1);

//		System.out.println(part1(molecule, rules).size());
		part2("e", rules, molecule);
	}

	public Set<String> part1(String input, List<Rule> rules) {
		Set<String> output = new HashSet<>();
		for (Rule rule : rules) {
			int startIndex = input.indexOf(rule.from);
			while (startIndex != -1) {
				output.add(replace(input, rule.from, rule.to, startIndex));
				startIndex = input.indexOf(rule.from, startIndex + 1);
			}
		}
//		System.out.println(output);
		return output;
	}

	public static int heuristic(String state, String target) {
		if (state.equals(target)) return 0;
		// Count the number of molecules in existence that we don't want
		// Only count uppercase shizzles (although I guess this isn't correct
		// since the target is "e" - it works though)
		return countUppers(state) - countUppers(target);
	}

	public static int countUppers(String text) {
		int result = 0;
		for (int i = 0; i < text.length(); i++) {
			char c = text.charAt(i);
			if (c >= 'A' && c <= 'Z') result++;
		}
		return result;
	}

	public static String replace(String s, String from, String to, int position) {
		return s.substring(0, position) + to + s.substring(position + from.length());
	}

	public void part2(String input, List<Rule> uninvertedRules, String output) {
		String target = input;
		String initial = output;

		List<Rule> rules = new ArrayList<>();
		for (Rule rule : uninvertedRules) {
			rules.add(rule.invert());
		}

		Map<String, State> open = new HashMap<>();
		State initialState = new State(initial, 0, target);
		open.put(initialState.molecule, initialState);

		Set<String> closed = new HashSet<>();
		while (!open.isEmpty()) {
			State next = open.values().stream().min(Comparator.comparing(State::f)).get();
			System.out.println("Possible moves: " + open.size() + ", best: " + next.molecule);
			open.remove(next.molecule);
			closed.add(next.molecule);

//			// Let's get greedy
//			Set<String> moves = new HashSet<>();
//			for (Rule rule : rules) {
//				String result = next.molecule.replaceAll(rule.from, rule.to);
//				if (!result.equals(next.molecule)) {
//					moves.add(result);
//				}
//			}

			Set<String> moves = part1(next.molecule, rules);

			for (String move : moves) {
//				System.out.println(next.molecule + " => " + move);
				if (closed.contains(move)) continue;

				State neighbour = new State(move, next.g+1, target);
				if (move.equals(target)) {
					// SOLVED!
					System.out.println("Done in " + neighbour.g + " moves.");
					return;
				}

				State existing = open.get(neighbour.molecule);
				if (existing == null) {
					open.put(neighbour.molecule, neighbour);
				} else {
					// Replace with better
					if (neighbour.g < existing.g) {
						open.put(neighbour.molecule, neighbour);
					}
				}
			}
		}
	}

	// Apparently this works for some people's input
	// Not me
	public void part2Again(String input, List<Rule> uninvertedRules, String output) {
		String target = input;
		String initial = output;

		List<Rule> rules = new ArrayList<>();
		for (Rule rule : uninvertedRules) {
			rules.add(rule.invert());
		}

		String current = initial;
		int moves = 0;
		while (!current.equals(target)) {
			for (Rule rule : rules) {
				int last = current.lastIndexOf(rule.from);
				if (last != -1) {
					current = replace(current, rule.from, rule.to, last);
					System.out.println(current);
					moves++;
				}
			}
		}
		System.out.println(moves);
	}

	public static void main(String[] args) throws Exception {
		Day19 worker = new Day19();

//		List<Rule> rules = new ArrayList<>();
//		rules.add(new Rule("e => H"));
//		rules.add(new Rule("e => O"));
//		rules.add(new Rule("H => HO"));
//		rules.add(new Rule("H => OH"));
//		rules.add(new Rule("O => HH"));
//		worker.part1("HOHOHO", rules);
//		worker.part2("e", rules, "HOHOHO");

//		System.out.println(Test.heuristic("HOO", "HOOHOHO"));
		worker.run();
	}

}
