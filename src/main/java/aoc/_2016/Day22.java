package aoc._2016;

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
import java.util.stream.Collectors;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

import shared.ResourceUtil;

public class Day22 {

	// p2 solution is quicker by hand - this one gets there, but is sloooow

	// group 1 = x
	// group 2 = y
	// group 3 = used
	// group 4 = avail
	Pattern nodePattern = Pattern.compile(
			"/dev/grid/node-x(\\d*)-y(\\d*)\\s*\\d*T\\s*(\\d*)T\\s*(\\d*)T\\s*.*");

	class Node {
		final int used;
		final int avail;

		Node(int used, int avail) {
			this.used = used;
			this.avail = avail;
		}
	}

	class State {
		final int currentX, currentY;
		final int blankX, blankY;
		final int g;
		double h;

		State(int currentX, int currentY, int blankX, int blankY, int g) {
			this.currentX = currentX;
			this.currentY = currentY;
			this.blankX = blankX;
			this.blankY = blankY;
			this.g = g;
		}

		private State apply(Move move) {
			int cx = currentX; int cy = currentY;
			if (move.fromX == cx && move.fromY == cy) { cx = move.toX; cy = move.toY; }
			else if (move.toX == cx && move.toY == cy) { cx = move.fromX; cy = move.fromY; }
			int bx = blankX; int by = blankY;
			if (move.fromX == bx && move.fromY == by) { bx = move.toX; by = move.toY; }
			else if (move.toX == bx && move.toY == by) { bx = move.fromX; by = move.fromY; }
			return new State(cx, cy, bx, by, g+1);
		}

		double f() { return g + h; }

		@Override public String toString() {
			return MoreObjects.toStringHelper(this)
					.add("currentX", currentX)
					.add("currentY", currentY)
					.add("blankX", blankX)
					.add("blankY", blankY)
					.add("g", g)
					.toString();
		}

		@Override public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			State state = (State) o;
			return currentX == state.currentX &&
					currentY == state.currentY &&
					blankX == state.blankX &&
					blankY == state.blankY;
		}

		@Override public int hashCode() {
			return Objects.hashCode(currentX, currentY, blankX, blankY);
		}

//		public String toFullString() {
//			StringBuilder sb = new StringBuilder();
//			for (int j = 0; j < grid[0].length; j++) {
//				for (int i = 0; i < grid.length; i++) {
//					Node node = grid[i][j];
//					sb.append(node.used).append("/").append(node.avail).append(" ");
//				}
//				sb.append("\n");
//			}
//			return sb.toString();
//		}
	}

	class Move {
		final int fromX, fromY, toX, toY;

		Move(int fromX, int fromY, int toX, int toY) {
			this.fromX = fromX;
			this.fromY = fromY;
			this.toX = toX;
			this.toY = toY;
		}
	}

	static int mInt(Matcher m, int group) { return Integer.parseInt(m.group(group)); }

	// p1
	private int countViablePairs(String path) throws IOException {
		List<String> lines = ResourceUtil.readAllLines(path);
		Node[][] grid = makeGrid(lines);
		int viablePairs = 0;
		for (int i = 0; i < grid.length; i++) {
			for (int j = 0; j < grid[i].length; j++) {
				for (int a = 0; a < grid.length; a++) {
					for (int b = 0; b < grid[a].length; b++) {
						if (isViablePair(grid[i][j], grid[a][b])) {
							// System.out.println(i + "," + j + " -> " + a + "," + b);
							viablePairs++;
						}
					}
				}
			}
		}
		return viablePairs;
	}

	private boolean isViablePair(Node a, Node b) {
		if (a == null || b == null) return false;
		return a != b && a.used != 0 && a.used <= b.avail;
	}

	// p2
	private int shortestPath(String path) throws IOException {
		List<String> lines = ResourceUtil.readAllLines(path);
		Node[][] grid = makeGrid(lines);
//		System.out.println(initialState.toFullString());

		// p2
		State initialState = new State(38, 0, 13, 23, 0);
		Map<State, Integer> open = new HashMap<>();
		open.put(initialState, initialState.g);
		Set<State> closed = new HashSet<>();
		while (!open.isEmpty()) {
			//System.out.println("open: " + open.size() + ", closed: " + closed.size());
			State current = findBestState(open);
			open.remove(current);
			closed.add(current);

			//System.out.println("best: " + current);

			List<Move> moves = allPossibleMoves(grid, current);
			//System.out.println("moves: " + moves.size());
			List<State> states = moves.stream()
					.map(m -> current.apply(m))
					.collect(Collectors.toList());
			for (State state : states) {
				state.h = heuristic(state);
				if (state.currentX == 0 && state.currentY == 0) {
					return state.g;
				}

				if (!closed.contains(state)) {
					Integer g = open.get(state);
					if (g == null || state.g < g) {
						open.put(state, state.g);
					}
				}
			}
		}
		return -1;
	}

	private State findBestState(Map<State, Integer> open) {
		return open.keySet().stream().min(Comparator.comparingDouble(State::f)).get();
	}

	private int manhattan(int x, int y, int x1, int y1) {
		return Math.abs(x - x1) + Math.abs(y - y1);
	}

	private double heuristic(State state) {
		int moves
				= manhattan(0, 0, state.currentX, state.currentY)
				+ manhattan(state.currentX, state.currentY, state.blankX, state.blankY);
		return moves;
	}

	private List<Move> allPossibleMoves(Node[][] grid, State state) {
		List<Move> moves = new ArrayList<>();
		int i = state.blankX;
		int j = state.blankY;
		if (i<WIDTH-1 && grid[i+1][j].used < WALL_THRESHOLD) moves.add(new Move(i, j, i+1, j));
		if (j<HEIGHT-1 && grid[i][j+1].used < WALL_THRESHOLD) moves.add(new Move(i, j, i, j+1));
		if (i>0 && grid[i-1][j].used < WALL_THRESHOLD) moves.add(new Move(i, j, i-1, j));
		if (j>0 && grid[i][j-1].used < WALL_THRESHOLD) moves.add(new Move(i, j, i, j-1));
		return moves;
	}

	final int WIDTH = 39;
	final int HEIGHT = 25;
	final int WALL_THRESHOLD = 400;

	private Node[][] makeGrid(List<String> lines) {
		// Create grid
		Node[][] grid = new Node[WIDTH][HEIGHT];
		for (String line : lines) {
			Matcher m = nodePattern.matcher(line);
			if (m.matches()) {
				grid[mInt(m, 1)][mInt(m, 2)] =
						new Node(mInt(m, 3), mInt(m, 4));
			}
		}
		return grid;
	}

	public static void main(String[] args) throws Exception {
		Day22 worker = new Day22();
		String filename = "2016/day22.input";
		// p1
		System.out.println(worker.countViablePairs(filename));
		// p2
		System.out.println(worker.shortestPath(filename));
	}

}
