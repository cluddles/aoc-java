package aoc._2018;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableList;

import shared.Dir4;
import shared.IntVector2;
import shared.ResourceUtil;
import shared.Test;

public class Day6 {

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
		for (Dir4 d : Dir4.values()) {
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

		// It turns out that a better check for infinity is any node claiming a
		// tile along the min/max boundaries.

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
					for (Dir4 d : Dir4.values()) {
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
