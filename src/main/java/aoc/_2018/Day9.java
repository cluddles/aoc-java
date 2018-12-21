package aoc._2018;

import java.util.Arrays;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableMap;

import shared.ResourceUtil;
import shared.Test;

/**
 * @author Dan Fielding
 */
public class Day9 {
/*
--- Day 9: Marble Mania ---

You talk to the Elves while you wait for your navigation system to initialize.
To pass the time, they introduce you to their favorite marble game.

The Elves play this game by taking turns arranging the marbles in a circle
according to very particular rules. The marbles are numbered starting with 0 and
increasing by 1 until every marble has a number.

First, the marble numbered 0 is placed in the circle. At this point, while it
contains only a single marble, it is still a circle: the marble is both
clockwise from itself and counter-clockwise from itself. This marble is
designated the current marble.

Then, each Elf takes a turn placing the lowest-numbered remaining marble into
the circle between the marbles that are 1 and 2 marbles clockwise of the current
marble. (When the circle is large enough, this means that there is one marble
between the marble that was just placed and the current marble.) The marble that
was just placed then becomes the current marble.

However, if the marble that is about to be placed has a number which is a
multiple of 23, something entirely different happens. First, the current player
keeps the marble they would have placed, adding it to their score. In addition,
the marble 7 marbles counter-clockwise from the current marble is removed from
the circle and also added to the current player's score. The marble located
immediately clockwise of the marble that was removed becomes the new current
marble.

For example, suppose there are 9 players. After the marble with value 0 is
placed in the middle, each player (shown in square brackets) takes a turn. The
result of each of those turns would produce circles of marbles like this, where
clockwise is to the right and the resulting current marble is in parentheses:

[-] (0)
[1]  0 (1)
[2]  0 (2) 1
[3]  0  2  1 (3)
[4]  0 (4) 2  1  3
[5]  0  4  2 (5) 1  3
[6]  0  4  2  5  1 (6) 3
[7]  0  4  2  5  1  6  3 (7)
[8]  0 (8) 4  2  5  1  6  3  7
[9]  0  8  4 (9) 2  5  1  6  3  7
[1]  0  8  4  9  2(10) 5  1  6  3  7
[2]  0  8  4  9  2 10  5(11) 1  6  3  7
[3]  0  8  4  9  2 10  5 11  1(12) 6  3  7
[4]  0  8  4  9  2 10  5 11  1 12  6(13) 3  7
[5]  0  8  4  9  2 10  5 11  1 12  6 13  3(14) 7
[6]  0  8  4  9  2 10  5 11  1 12  6 13  3 14  7(15)
[7]  0(16) 8  4  9  2 10  5 11  1 12  6 13  3 14  7 15
[8]  0 16  8(17) 4  9  2 10  5 11  1 12  6 13  3 14  7 15
[9]  0 16  8 17  4(18) 9  2 10  5 11  1 12  6 13  3 14  7 15
[1]  0 16  8 17  4 18  9(19) 2 10  5 11  1 12  6 13  3 14  7 15
[2]  0 16  8 17  4 18  9 19  2(20)10  5 11  1 12  6 13  3 14  7 15
[3]  0 16  8 17  4 18  9 19  2 20 10(21) 5 11  1 12  6 13  3 14  7 15
[4]  0 16  8 17  4 18  9 19  2 20 10 21  5(22)11  1 12  6 13  3 14  7 15
[5]  0 16  8 17  4 18(19) 2 20 10 21  5 22 11  1 12  6 13  3 14  7 15
[6]  0 16  8 17  4 18 19  2(24)20 10 21  5 22 11  1 12  6 13  3 14  7 15
[7]  0 16  8 17  4 18 19  2 24 20(25)10 21  5 22 11  1 12  6 13  3 14  7 15

The goal is to be the player with the highest score after the last marble is
used up. Assuming the example above ends after the marble numbered 25, the
winning score is 23+9=32 (because player 5 kept marble 23 and removed marble 9,
while no other player got any points in this very short example game).

Here are a few more examples:

    10 players; last marble is worth 1618 points: high score is 8317
    13 players; last marble is worth 7999 points: high score is 146373
    17 players; last marble is worth 1104 points: high score is 2764
    21 players; last marble is worth 6111 points: high score is 54718
    30 players; last marble is worth 5807 points: high score is 37305

What is the winning Elf's score?

--- Part Two ---

Amused by the speed of your answer, the Elves are curious:

What would the new winning Elf's score be if the number of the last marble were
100 times larger?

 */

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
