package aoc._2016;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import shared.Dir;
import shared.Dir4;
import shared.Grid;
import shared.IntVector2;
import shared.Permutations;
import shared.ResourceUtil;
import shared.Test;
import shared.pathing.AStarApplication;
import shared.pathing.AStarPathfinder;

public class Day24 {

	private static final char TILE_WALL =  '#';

	static class Node {
		final int                   index;
		final IntVector2            pos;
		final Map<Integer, Integer> nodeDistances = new HashMap<>();
		Node(int index, IntVector2 pos) {
			this.index = index;
			this.pos   = pos;
		}
	}

	static class State {
		final Map<Integer, Node> nodes;
		final Grid<Boolean>      walls;
		State(
				Map<Integer, Node> nodes,
				Grid<Boolean>      walls) {
			this.nodes    = nodes;
			this.walls    = walls;
		}
	}

	class PathingRules implements AStarApplication<IntVector2> {
		@Override public int heuristic(IntVector2 pos, IntVector2 target) {
			return pos.manhattanDistance(target);
		}

		@Override public int distance(IntVector2 pos, IntVector2 neighbour) {
			return 1;
		}

		@Override public Iterator<IntVector2> accessibleNeighbours(IntVector2 pos) {
			// Bit ugly, but this is just all adjacents that aren't blocked
			return Dir.adjacentPositions(pos, Dir4.values()).stream()
					.filter(p -> !state.walls.get(p))
					.collect(Collectors.toList())
					.iterator();
		}
	}


	private State state;
	private AStarPathfinder<IntVector2> aStar = new AStarPathfinder<>(new PathingRules());

	Day24(List<String> lines) {
		state = parse(lines);
	}

	State parse(List<String> lines) {
		// Load lines into char grid
		Grid<Character>  chars = Grid.fromLines(lines);
		// Need to convert into what we actually want
		Grid<Boolean>      walls    = new Grid<>(chars.getNumCells());
		Map<Integer, Node> nodes    = new HashMap<>();
		Iterator<Grid.GridCell<Character>> it = chars.cellIterator();
		while (it.hasNext()) {
			Grid.GridCell<Character> cell = it.next();
			IntVector2 pos = cell.getPos();
			char       c   = cell.getData();
			if (c == TILE_WALL) {
				walls.set(pos, true);
			} else {
				walls.set(pos, false);
				if (c >= '0' && c <= '9') {
					int index = c - '0';
					nodes.put(index, new Node(index, pos));
				}
			}
		}
		return new State(nodes, walls);
	}

	int cost(Node from, Node to) {
		// I like to cache it, cache it
		int dist = from.nodeDistances.getOrDefault(to.index, -1);
		if (dist != -1) return dist;
		// Calculate distance
		dist = aStar.findPath(from.pos, to.pos).getCost();
		// Cache (distances are symmetrical)
		from.nodeDistances.put(to.index,   dist);
		to  .nodeDistances.put(from.index, dist);
		return dist;
	}

	int evalPermutation(List<Integer> nodeIndexes) {
		int cost = 0;
		for (int i = 1; i < nodeIndexes.size(); i++) {
			cost += cost(
					state.nodes.get(nodeIndexes.get(i-1)),
					state.nodes.get(nodeIndexes.get(i)  ));
		}
		return cost;
	}

	public int eval(boolean returnToStart) {
		// Note that we have to start from 0, so don't include that in the list
		// of nodes to generate permutations of.
		List<Integer> nodeIndexes = IntStream
				.range(1, state.nodes.size())
				.boxed()
				.collect(Collectors.toList());
		List<List<Integer>> nodeIndexPerms = Permutations.of(nodeIndexes);
		int best = Integer.MAX_VALUE;
		for (List<Integer> perm : nodeIndexPerms) {
			// Add node 0 at the start
			perm.add(0, 0);
			// Add node 0 at the end (for part 2)
			if (returnToStart) perm.add(0);
			best = Math.min(best, evalPermutation(perm));
		}
		return best;
	}

	public int evalPart1() { return eval(false); }
	public int evalPart2() { return eval(true);  }

	public static void main(String[] args) throws Exception {
		List<String> example = ResourceUtil.readAllLines("2016/day24.example");
		List<String> input   = ResourceUtil.readAllLines("2016/day24.input");

		// Part 1
		// Example
		Test.check(new Day24(example).evalPart1(), 14);
		// Vs Input
		Day24 solver = new Day24(input);
		System.out.println(solver.evalPart1());
		// Part 2
		System.out.println(solver.evalPart2());
	}

}

