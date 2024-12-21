package aoc._2016;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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
import com.google.common.base.Objects;

import shared.ResourceUtil;

public class Day11 {

	private static final Pattern CONTENT_PATTERN = Pattern.compile("a (\\w*-compatible microchip|\\w* ógenerator)");

	class State {
		int elevatorFloor = 1;
		final List<Pair> pairs = new ArrayList<>();

		@Override public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			State state = (State) o;
			return elevatorFloor == state.elevatorFloor &&
					Objects.equal(pairs, state.pairs);
		}

		@Override public int hashCode() {
			return Objects.hashCode(elevatorFloor, pairs);
		}

		@Override public String toString() {
			return MoreObjects.toStringHelper(this)
					.add("elevatorFloor", elevatorFloor)
					.add("pairs", pairs)
					.toString();
		}

		public State copy() {
			State result = new State();
			result.elevatorFloor = elevatorFloor;
			for (Pair pair : pairs) {
				result.pairs.add(pair.copy());
			}
			return result;
		}

		public void optimise() {
			// This is a fudge to sort the list of pairs according to hashcode.
			// We don't care about chip/gen types, only which combinations of
			// thingies are on each floor.
			// Doing this makes the search space WAAAAAAY smaller.
			pairs.sort(Comparator.comparing(o -> o.hashCode()));
		}

		public boolean isVictoryState() {
			for (Pair pair : pairs) {
				if (pair.genFloor != 4 || pair.chipFloor != 4) return false;
			}
			return true;
		}

		public boolean isInvalid() {
			for (int i = 0; i < pairs.size(); i++) {
				Pair p1 = pairs.get(i);
				for (int j = i+1; j < pairs.size(); j++) {
					Pair p2 = pairs.get(j);
					if (p1.chipFloor != p1.genFloor && p1.chipFloor == p2.genFloor) {
						return true;
					}
				}
			}
			return false;
		}
	}

	class Pair {
		int chipFloor;
		int genFloor;

		public Pair() { }

		public Pair(int chipFloor, int genFloor) {
			this.chipFloor = chipFloor;
			this.genFloor = genFloor;
		}

		@Override public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			Pair pair = (Pair) o;
			return chipFloor == pair.chipFloor &&
					genFloor == pair.genFloor;
		}

		@Override public int hashCode() {
			return Objects.hashCode(chipFloor, genFloor);
		}

		@Override public String toString() {
			return MoreObjects.toStringHelper(this)
					.add("chipFloor", chipFloor)
					.add("genFloor", genFloor)
					.toString();
		}

		public Pair copy() {
			Pair result = new Pair();
			result.chipFloor = chipFloor;
			result.genFloor = genFloor;
			return result;
		}
	}

	class Tree<T> {
		final T data;
		final Tree<T> parent;
		final List<Tree<T>> children = new ArrayList<>();

		Tree(Tree<T> parent, T data) {
			this.parent = parent;
			this.data = data;
		}

		boolean contains(T obj) {
			if (Objects.equal(obj, data)) return true;
			for (Tree<T> child : children) {
				if (child.contains(obj)) return true;
			}
			return false;
		}

		void add(T obj) {
			children.add(new Tree<T>(this, obj));
		}

		public T getData() {
			return data;
		}

		public List<Tree<T>> getChildren() {
			return children;
		}
	}

	class FloorItem {
		final int index;
		final boolean isChip;

		FloorItem(int index, boolean isChip) {
			this.isChip = isChip;
			this.index = index;
		}
	}

	class Move {
		final List<FloorItem> items;

		Move(FloorItem... items) {
			this.items = Arrays.asList(items);
		}
	}

	private final Map<String, Integer> nameLookup = new HashMap<>();

	private void parseFile(String filename) throws IOException {
		List<String> lines = ResourceUtil.readAllLines(filename);

		State startState = new State();
		for (int i = 0; i < lines.size(); i++) {
			parseLine(startState, i+1, lines.get(i));
		}

		// Manually add some more shit for part 2
		startState.pairs.add(new Pair(1, 1));
		startState.pairs.add(new Pair(1, 1));

		System.out.println(startState);

		run(startState);
	}

	private void parseLine(State state, int floor, String line) {
		Matcher matcher = CONTENT_PATTERN.matcher(line);
		while (matcher.find()) {
			String text = matcher.group(1);
			boolean isChip = text.contains("-compatible microchip");
			String name = text.replaceAll("(-compatible microchip| generator)", "");
			if (!nameLookup.containsKey(name)) {
				nameLookup.put(name, state.pairs.size());
				state.pairs.add(new Pair());
			}

			Pair pair = state.pairs.get(nameLookup.get(name));
			if (isChip) {
				pair.chipFloor = floor;
			} else {
				pair.genFloor = floor;
			}
		}
	}

	private void run(State state) {
		Tree<State> allStates = new Tree<>(null, state);
		Set<State> history = new HashSet<>();
		Collection<Tree<State>> thisLevel, nextLevel;

		int move = 0;
		nextLevel = new ArrayList<Tree<State>>();
		nextLevel.add(allStates);

		while (true) {
			// Next set of nodes
			thisLevel = nextLevel;
			nextLevel = new ArrayList<>();
			System.out.println("Checking for victory");
			for (Tree<State> leaf : thisLevel) {
				if (leaf.getData().isVictoryState()) {
					System.out.println("SOLVED on move " + move);
					Tree<State> current = leaf;
					do {
						System.out.println("-------");
						System.out.println(current.getData());
						current = current.parent;
					} while (current != null);
					return;
				}
			}

			// Show moves
			move++;
			System.out.println("Processing move " + move + ", states: " + thisLevel.size());
			System.out.println("History size: " + history.size());
			// Work out new states
			for (Tree<State> subtree : thisLevel) {
				nextLevel.addAll(expand(subtree, history));
			}
		}
	}

	public Collection<Tree<State>> expand(Tree<State> tree, Set<State> history) {
		// System.out.println("Expanding " + tree);
		State state = tree.getData();
		// Calculate all possible moves
		Collection<Move> moves = generateAllMoves(state);
		// Create new states using these moves
		for (Move move : moves) {
			if (state.elevatorFloor > 1) addIfValid(tree, history, applyMove(state, move, -1));
			if (state.elevatorFloor < 4) addIfValid(tree, history, applyMove(state, move, 1));
		}
		return tree.getChildren();
	}

	private void addIfValid(Tree<State> tree, Set<State> history, State state) {
		state.optimise();
		// Discard invalid states
		if (state.isInvalid()) return;
		// Discard duplicates that already exist in "all"
		if (history.contains(state)) return;
		// Keep it
		history.add(state);
		tree.add(state);
	}

	private Collection<Move> generateAllMoves(State state) {
		int floor = state.elevatorFloor;
		List<FloorItem> items = new ArrayList<>();
		for (int i = 0; i < state.pairs.size(); i++) {
			Pair pair = state.pairs.get(i);
			if (pair.chipFloor == floor) items.add(new FloorItem(i, true));
			if (pair.genFloor == floor)  items.add(new FloorItem(i, false));
		}
		// Every valid combination (single item and item pair)
		Collection<Move> result = new ArrayList<>();
		for (int i = 0; i < items.size(); i++) {
			result.add(new Move(items.get(i)));
			for (int j = i+1; j < items.size(); j++) {
				result.add(new Move(items.get(i), items.get(j)));
			}
		}
		return result;
	}

	private State applyMove(State state, Move move, int dir) {
		State result = state.copy();
		result.elevatorFloor += dir;
		for (FloorItem item : move.items) {
			Pair pair = result.pairs.get(item.index);
			if (item.isChip) {
				pair.chipFloor += dir;
			} else {
				pair.genFloor += dir;
			}
		}
		return result;
	}

	public static void main(String[] args) throws Exception {
		String filename = "2016/day11.input";

		Day11 worker = new Day11();
		worker.parseFile(filename);
	}

}
