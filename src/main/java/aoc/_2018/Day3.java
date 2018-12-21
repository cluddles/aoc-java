package aoc._2018;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.collect.ImmutableList;

import shared.IntVector2;
import shared.Rect;
import shared.ResourceUtil;
import shared.Test;

/**
 * @author Dan Fielding
 */
public class Day3 {

/*

--- Day 3: No Matter How You Slice It ---

The Elves managed to locate the chimney-squeeze prototype fabric for Santa's
suit (thanks to someone who helpfully wrote its box IDs on the wall of the
warehouse in the middle of the night). Unfortunately, anomalies are still
affecting them - nobody can even agree on how to cut the fabric.

The whole piece of fabric they're working on is a very large square - at least
1000 inches on each side.

Each Elf has made a claim about which area of fabric would be ideal for Santa's
suit. All claims have an ID and consist of a single rectangle with edges
parallel to the edges of the fabric. Each claim's rectangle is defined as
follows:

    The number of inches between the left edge of the fabric and the left edge
        of the rectangle.
    The number of inches between the top edge of the fabric and the top edge of
        the rectangle.
    The width of the rectangle in inches.
    The height of the rectangle in inches.

A claim like #123 @ 3,2: 5x4 means that claim ID 123 specifies a rectangle 3
inches from the left edge, 2 inches from the top edge, 5 inches wide, and 4
inches tall. Visually, it claims the square inches of fabric represented by #
(and ignores the square inches of fabric represented by .) in the diagram below:

...........
...........
...#####...
...#####...
...#####...
...#####...
...........
...........
...........

The problem is that many of the claims overlap, causing two or more claims to
cover part of the same areas. For example, consider the following claims:

#1 @ 1,3: 4x4
#2 @ 3,1: 4x4
#3 @ 5,5: 2x2

Visually, these claim the following areas:

........
...2222.
...2222.
.11XX22.
.11XX22.
.111133.
.111133.
........

The four square inches marked with X are claimed by both 1 and 2. (Claim 3,
while adjacent to the others, does not overlap either of them.)

If the Elves all proceed with their own plans, none of them will have enough
fabric. How many square inches of fabric are within two or more claims?

--- Part Two ---

Amidst the chaos, you notice that exactly one claim doesn't overlap by even a
single square inch of fabric with any other claim. If you can somehow draw
attention to it, maybe the Elves will be able to make Santa's suit after all!

For example, in the claims above, only claim 3 is intact after all claims are
made.

What is the ID of the only claim that doesn't overlap?

*/

	private static final Pattern INPUT_LINE_PATTERN =
			Pattern.compile("#(?<id>\\d+) " +
					"@ (?<x>\\d+),(?<y>\\d+)" +
					": (?<w>\\d+)x(?<h>\\d+)");

	class Claim {
		final int id;
		final Rect area;

		Claim(int id, Rect area) {
			this.id = id;
			this.area = area;
		}
	}

	public Claim parseLine(String line) {
		Matcher matcher = INPUT_LINE_PATTERN.matcher(line);
		if (matcher.matches()) {
			return new Claim(
					Integer.valueOf(matcher.group("id")),
					new Rect(
							new IntVector2(
									Integer.valueOf(matcher.group("x")),
									Integer.valueOf(matcher.group("y"))),
							new IntVector2(
									Integer.valueOf(matcher.group("w")),
									Integer.valueOf(matcher.group("h")))));
		}
		throw new IllegalArgumentException("Could not parse line: " + line);
	}

	public int eval(
			List<String>           lines,
			Map<IntVector2, Claim> claims,
			Set<IntVector2>        conflicts) {
		for (String line : lines) {
			Claim   claim = parseLine(line);
			Rect    rect  = claim.area;
			// Is this claim conflicting only with itself?
			boolean clean = true;
			for (int i = 0; i < rect.getSize().getX(); i++) {
				for (int j = 0; j < rect.getSize().getY(); j++) {
					IntVector2 point = new IntVector2(
							rect.getStart().getX() + i,
							rect.getStart().getY() + j);
					Claim other = claims.get(point);
					if (other == null) {
						clean = false;
					} else if (other.id != claim.id) {
						conflicts.add(point);
						clean = false;
					}
					claims.put(point, claim);
				}
			}
			// For part 2
			if (clean) return claim.id;
		}
		return -1;
	}

	public int evalPart1(List<String> lines) {
		Map<IntVector2, Claim> claims    = new HashMap<>();
		Set<IntVector2>        conflicts = new HashSet<>();
		// 1 pass, check conflicts
		eval(lines, claims, conflicts);
		return conflicts.size();
	}

	public int evalPart2(List<String> lines) {
		Map<IntVector2, Claim> claims    = new HashMap<>();
		Set<IntVector2>        conflicts = new HashSet<>();
		// 2 passes, result of the second
		eval(lines, claims, conflicts);
		return eval(lines, claims, conflicts);
	}

	public static void main(String[] args) throws Exception {
		Day3 solver = new Day3();
		List<String> lines = ResourceUtil.readAllLines("2018/day3.input");

		// Part 1
		// Examples
		Test.check(
				solver.evalPart1(ImmutableList.of(
						"#1 @ 1,3: 4x4",
						"#2 @ 3,1: 4x4",
						"#3 @ 5,5: 2x2")),
				4);
		// Vs Input
		System.out.println(solver.evalPart1(lines));

		// Part 2
		// Examples
		Test.check(
				solver.evalPart2(ImmutableList.of(
						"#1 @ 1,3: 4x4",
						"#2 @ 3,1: 4x4",
						"#3 @ 5,5: 2x2")),
				3);
		// Vs Input
		System.out.println(solver.evalPart2(lines));
	}

}
