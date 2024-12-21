package aoc._2018;

import java.util.Arrays;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableMap;

import shared.ResourceUtil;
import shared.Test;

public class Day9 {

	private static final Pattern INPUT_PATTERN = Pattern.compile(
			"(\\d+) players; last marble is worth (\\d+) points");

	// DIY linked list...
	class Marble {
		final int value;
		Marble    prev;
		Marble    next;

		Marble(int value) {
			this.value = value;
			this.prev  = this;
			this.next  = this;
		}

		@Override
		public String toString() {
			return MoreObjects.toStringHelper(this)
					.add("value", value)
					.add("prev", prev.value)
					.add("next", next.value)
					.toString();
		}

		String debug() {
			StringBuilder result = new StringBuilder();
			result.append(value);
			Marble next = this.next;
			while (next != this) {
				result.append(" ");
				result.append(next.value);
				next = next.next;
			}
			return result.toString();
		}
	}

	// Step back or forward X jumps
	private Marble step(Marble current, int steps) {
		while (steps > 0) {
			current = current.next;
			steps--;
		}
		while (steps < 0) {
			current = current.prev;
			steps++;
		}
		return current;
	}

	// Insert after current, returns inserted marble
	private Marble insert(Marble current, Marble toInsert) {
		Marble next   = current.next;
		current.next  = toInsert;
		next.prev     = toInsert;

		toInsert.next = next;
		toInsert.prev = current;
		return toInsert;
	}

	// Removes current, returns the marble after the one removed
	private Marble remove(Marble current) {
		current.next.prev = current.prev;
		current.prev.next = current.next;
		// Shift to next
		current = current.next;
		return current;
	}

	public long eval(int numPlayers, int lastMarble) {
		long[] scores = new long[numPlayers];

		Marble zero    = new Marble(0);
		Marble current = zero;
		int    currentPlayer = 0;
		int    nextNewMarble = 1;
		while (nextNewMarble <= lastMarble) {
			current = step(current, 1);

			Marble marble = new Marble(nextNewMarble);
			// Multiples of 23 are scorers
			boolean scoringMarble = (marble.value % 23 == 0);
			if (!scoringMarble) {
				current = insert(current, marble);
			} else {
				current = step(current, -8);
				Marble removed = current;
				current = remove(removed);
				scores[currentPlayer] += marble.value + removed.value;
			}

			// Next turn
			currentPlayer = (currentPlayer + 1) % numPlayers;
			nextNewMarble++;
		}
		return Arrays.stream(scores)
				.max()
				.orElseThrow(() -> new IllegalStateException("No max"));
	}

	public long evalInput(String input, int lastMarbleFactor) {
		Matcher matcher = INPUT_PATTERN.matcher(input);
		if (!matcher.matches()) throw new IllegalStateException("Bum input: " + input);
		int numPlayers = Integer.parseInt(matcher.group(1));
		int lastMarble = Integer.parseInt(matcher.group(2)) * lastMarbleFactor;
		return eval(numPlayers, lastMarble);
	}

	public long evalPart1(String input) {
		return evalInput(input, 1);
	}

	public long evalPart2(String input) {
		return evalInput(input, 100);
	}

	public static void main(String[] args) throws Exception {
		Day9   solver = new Day9();
		String input  = ResourceUtil.readString("2018/day9.input");
		Map<String, Long> examples = ImmutableMap.<String, Long>builder()
				.put( "9 players; last marble is worth 32 points",   32L)
				.put("10 players; last marble is worth 1618 points", 8317L)
				.put("13 players; last marble is worth 7999 points", 146373L)
				.put("17 players; last marble is worth 1104 points", 2764L)
				.put("21 players; last marble is worth 6111 points", 54718L)
				.put("30 players; last marble is worth 5807 points", 37305L)
				.build();

		// Part 1
		// Examples
		for (String key : examples.keySet()) {
			Test.check(solver.evalPart1(key), examples.get(key));
		}
		// Vs Input
		System.out.println(solver.evalPart1(input));

		// Part 2
		System.out.println(solver.evalPart2(input));
	}
}
