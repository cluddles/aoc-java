package aoc._2016;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.collect.Multimap;
import com.google.common.collect.Ordering;
import com.google.common.collect.TreeMultimap;

import shared.Dir4;
import shared.IntVector2;
import shared.Md5;

public class Day17 {

	private class State {
		final IntVector2 pos;
		final String moves;

		public State(IntVector2 pos, String moves) {
			this.pos = pos;
			this.moves = moves;
		}

		@Override public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			State state = (State) o;
			return Objects.equal(pos, state.pos) &&
					Objects.equal(moves, state.moves);
		}

		@Override public int hashCode() {
			return Objects.hashCode(pos, moves);
		}

		@Override public String toString() {
			return MoreObjects.toStringHelper(this)
					.add("pos", pos)
					.add("moves", moves)
					.toString();
		}

		public int f(IntVector2 goal) {
			return g() + pos.manhattanDistance(goal);
		}

		public int g() {
			return moves.length();
		}
	}

	private static class Move {
		final Dir4 direction;
		final String name;

		public Move(Dir4 direction, String name) {
			this.direction = direction;
			this.name = name;
		}

		@Override
		public String toString() {
			return MoreObjects.toStringHelper(this)
					.add("direction", direction)
					.add("name", name)
					.toString();
		}
	}

	private static final Move[] MOVES = new Move[] {
			new Move(Dir4.N, "U"),
			new Move(Dir4.S, "D"),
			new Move(Dir4.W, "L"),
			new Move(Dir4.E, "R"),
	};

	public void run(String seed) {
		IntVector2 start = new IntVector2(0, 0);
		IntVector2 goal = new IntVector2(3, 3);
		Multimap<Integer, State> fStates = TreeMultimap.create(
				Comparator.comparingInt(Integer::intValue),
				Ordering.arbitrary()
		);
		State startState = new State(start, "");
		fStates.put(startState.f(goal), startState);

		// Keep all solutions for part 2
		List<State> solutions = new ArrayList<>();

		while (!fStates.isEmpty()) {
			System.out.println("fStates: " + fStates.size() + " ");

			int min = fStates.keySet().stream().findFirst().orElse(-1);
			if (min == -1) {
//				System.out.println("No valid moves remain");
				return;
			}

			Collection<State> states = fStates.removeAll(min);
			System.out.println("For min: " + min + ", got states: " + states.size());

			for (State state : states) {
				String md5 = Md5.hash(seed + state.moves);
				for (int i = 0; i < 4; i++) {
					if (md5.charAt(i) >= 'b') {
						// Open door in dir: u,d,l,r
						Move move = MOVES[i];
//						System.out.println("Open: " + move.name);
						IntVector2 newPos = state.pos.add(move.direction.getStep());
						if (isOutOfBounds(newPos)) {
//							System.out.println("Out of bounds");
							continue;
						}

						State newState = new State(newPos, state.moves + move.name);

						if (newPos.equals(goal)) {
							System.out.println("DONE in " + newState.moves.length() + " (" + newState.moves + ")");
							solutions.add(newState);
							// For part one, just break because we're done
							// break;

						} else {
							fStates.put(newState.f(goal), newState);
						}
					}
				}
			}
		}

		// For part 2, we want the worst solution
		State worstState = solutions.stream()
				.sorted(Comparator.comparing(State::g).reversed())
				.findFirst()
				.get();
		System.out.println(worstState.moves.length() + " : " + worstState);
	}

	private boolean isOutOfBounds(IntVector2 newPos) {
		return (newPos.x < 0 || newPos.y < 0 || newPos.x > 3 || newPos.y > 3);
	}

	public static void main(String[] args) throws Exception {
		Day17 worker = new Day17();
		worker.run("edjrjqaa");
	}

}
