package aoc._2018;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableList;
import shared.Direction;
import shared.IntVector2;
import shared.ResourceUtil;
import shared.Test;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Dan Fielding
 */
public class Day6 {
/*

--- Day 6: Chronal Coordinates ---

The device on your wrist beeps several times, and once again you feel like
you're falling.

"Situation critical," the device announces. "Destination indeterminate. Chronal
interference detected. Please specify new target coordinates."

The device then produces a list of coordinates (your puzzle input). Are they
places it thinks are safe or dangerous? It recommends you check manual page 729.
The Elves did not give you a manual.

If they're dangerous, maybe you can minimize the danger by finding the
coordinate that gives the largest distance from the other points.

Using only the Manhattan distance, determine the area around each coordinate by
counting the number of integer X,Y locations that are closest to that coordinate
(and aren't tied in distance to any other coordinate).

Your goal is to find the size of the largest area that isn't infinite. For
example, consider the following list of coordinates:

1, 1
1, 6
8, 3
3, 4
5, 5
8, 9

If we name these coordinates A through F, we can draw them on a grid, putting
0,0 at the top left:

..........
.A........
..........
........C.
...D......
.....E....
.B........
..........
..........
........F.

This view is partial - the actual grid extends infinitely in all directions.
Using the Manhattan distance, each location's closest coordinate can be
determined, shown here in lowercase:

aaaaa.cccc
aAaaa.cccc
aaaddecccc
aadddeccCc
..dDdeeccc
bb.deEeecc
bBb.eeee..
bbb.eeefff
bbb.eeffff
bbb.ffffFf

Locations shown as . are equally far from two or more coordinates, and so they
don't count as being closest to any.

In this example, the areas of coordinates A, B, C, and F are infinite - while
not shown here, their areas extend forever outside the visible grid. However,
the areas of coordinates D and E are finite: D is closest to 9 locations, and E
is closest to 17 (both including the coordinate's location itself). Therefore,
in this example, the size of the largest area is 17.

What is the size of the largest area that isn't infinite?

 */

	private static final Pattern INPUT_PATTERN =
			Pattern.compile("(?<x>\\d+), (?<y>\\d+)");

	private static final Node CONTESTED = new Node(-1, IntVector2.ZERO);

	private static final int  INFINITE_GROWTH_TICKS = 100;

	static class Node {
		final int        id;
		final IntVector2 pos;
		int              tilesClaimed;

		int              tilesClaimedThisTick;
		int              tilesClaimedLastTick;
		int              ticksOfGrowth;

		Node(int id, IntVector2 pos) {
			this.id  = id;
			this.pos = pos;
		}

		@Override
		public String toString() {
			return MoreObjects.toStringHelper(this)
					.add("id", id)
					.add("pos", pos)
					.add("tilesClaimed", tilesClaimed)
					.toString();
		}
	}

	static class Tile {
		final IntVector2 pos;
		Set<Node>        pendingClaims = new HashSet<>();
		Node             claimedBy;
		int              lastScore;

		Tile(IntVector2 pos) {
			this.pos = pos;
		}
	}

	public Day6(List<String> lines) {
		nodes = parseInput(lines);
	}

	private Set<Node> parseInput(List<String> lines) {
		Set<Node> result = new HashSet<>();
		int i = 0;
		for (String line : lines) {
			Matcher matcher = INPUT_PATTERN.matcher(line);
			if (!matcher.matches()) {
				throw new IllegalArgumentException("Invalid line: " + line);
			}
			result.add(new Node(
					i,
					new IntVector2(
							Integer.parseInt(matcher.group("x")),
							Integer.parseInt(matcher.group("y")))));
			i++;
		}
		return result;
	}


	private Map<IntVector2, Tile> allTiles = new HashMap<>();
	private Set<Node>             nodes;


	private Tile getTile(IntVector2 pos) {
		return allTiles.computeIfAbsent(pos, k -> new Tile(pos));
	}

	private Tile pendingClaim(IntVector2 pos, Node node) {
		Tile t = getTile(pos);
		if (t.claimedBy == null) {
			t.pendingClaims.add(node);
			return t;
		}
		return null;
	}

	private Tile claim(IntVector2 pos, Node node) {
		Tile t = getTile(pos);
		t.pendingClaims.clear();
		t.claimedBy = node;
		node.tilesClaimed++;
		return t;
	}

	private Collection<Tile> floodFill(Tile tile) {
		List<Tile> modifiedTiles = new ArrayList<>();
		for (Direction d : Direction.values()) {
			Tile t = pendingClaim(tile.pos.add(d.getStep()), tile.claimedBy);
			if (t != null) modifiedTiles.add(t);
		}
		return modifiedTiles;
	}

	public int evalPart1() {
		// Flood fill around each node, 1 tile at a time
		// Any touched tiles - work out which node "claimed" it
		// Infinite detection?

		// Activate tiles that nodes are in, claim them
		Set<Node> activeNodes = new HashSet<>();
		Set<Tile> activeTiles = new HashSet<>();
		for (Node n : nodes) {
			activeNodes.add(n);
			activeTiles.add(getTile(n.pos));
			claim(n.pos, n);
		}

		Set<Node> infiniteNodes = new HashSet<>();
		while (!activeNodes.isEmpty() && !activeTiles.isEmpty()) {
			// Flood fill and remember touched tiles
			Set<Tile> modifiedTiles = new HashSet<>();
			for (Tile tile : activeTiles) {
				modifiedTiles.addAll(floodFill(tile));
			}
			// Clear all actives
			activeNodes.clear();
			activeTiles.clear();
			for (Tile tile : modifiedTiles) {
				// Claim any undisputed tiles
				if (tile.pendingClaims.size() == 1) {
					// Don't process any infinite node nonsense
					Node claimer = tile.pendingClaims.iterator().next();
					if (infiniteNodes.contains(claimer)) continue;
					// Claim
					claim(tile.pos, claimer);
					activeTiles.add(tile);
					// Keep track of growth for infinite detection
					if (!activeNodes.contains(claimer)) {
						activeNodes.add(claimer);
						claimer.tilesClaimedLastTick = claimer.tilesClaimedThisTick;
						claimer.tilesClaimedThisTick = 1;
					} else {
						claimer.tilesClaimedThisTick++;
					}
				} else {
					// Mark contested tiles so we ignore them
					tile.claimedBy = CONTESTED;
				}
			}
			// Detect infinite growth
			for (Node node : activeNodes) {
				if (node.tilesClaimedThisTick >= node.tilesClaimedLastTick) {
					node.ticksOfGrowth++;
					if (node.ticksOfGrowth > INFINITE_GROWTH_TICKS) {
						infiniteNodes.add(node);
					}
				} else {
					node.ticksOfGrowth = 0;
				}
			}
			// Remove infinite growers from the active nodes
			activeNodes.removeAll(infiniteNodes);
		}

		// Now just find the node with the biggest claimed area
		// (that wasn't infinite)
		return nodes.stream()
				.filter     (n -> !infiniteNodes.contains(n))
				.max        (Comparator.comparingInt(n -> n.tilesClaimed))
				.orElseThrow(() -> new IllegalArgumentException("No answer"))
				.tilesClaimed;
	}

	private int p2Score(Tile tile) {
		return nodes.stream()
				.mapToInt(n -> n.pos.manhattanDistance(tile.pos))
				.sum();
	}

	public int evalPart2(int threshold) {
		Set<Tile> potentials = new HashSet<>();
		Set<Tile> answers    = new HashSet<>();
		Set<Tile> rejects    = new HashSet<>();
		// Start at the nodes - why not
		for (Node node : nodes) {
			Tile t = getTile(node.pos);
			potentials.add(t);
			t.lastScore = Integer.MAX_VALUE;
		}
		// Repeat while we've got sensible tiles to look at
		while (!potentials.isEmpty()) {
			Set<Tile> newPotentials = new HashSet<>();
			for (Tile tile : potentials) {
				// Answer/reject
				int score = p2Score(tile);
				if (score < threshold) {
					answers.add(tile);
				} else {
					rejects.add(tile);
				}
				// Expand
				if (score < tile.lastScore || score < threshold) {
					for (Direction d : Direction.values()) {
						Tile other = getTile(tile.pos.add(d.getStep()));
						if (!answers.contains(other) && !rejects.contains(other)) {
							other.lastScore = score;
							newPotentials.add(other);
						}
					}
				}
			}
			// Next load of tiles to consider
			potentials = newPotentials;
		}
		return answers.size();
	}

	public static void main(String[] args) throws Exception {
		List<String> lines = ResourceUtil.readAllLines("2018/day6.input");

		// Part 1
		// Example
		List<String> example = ImmutableList.of(
				"1, 1",
				"1, 6",
				"8, 3",
				"3, 4",
				"5, 5",
				"8, 9");
		Test.check(new Day6(example).evalPart1(), 17);
		// Vs Input
		System.out.println(new Day6(lines).evalPart1());

		// Part 2
		// Example
		Test.check(new Day6(example).evalPart2(32), 16);
		// Vs Input
		// Failures: 43156 (too low)
		System.out.println(new Day6(lines).evalPart2(10000));
	}

}
