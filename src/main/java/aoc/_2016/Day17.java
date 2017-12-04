package aoc._2016;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.collect.Multimap;
import com.google.common.collect.Ordering;
import com.google.common.collect.TreeMultimap;
import shared.Heading;
import shared.Md5;
import shared.Position;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

/**
 * @author Dan Fielding
 */
public class Day17 {

	private class State {
		final Position pos;
		final String moves;

		public State(Position pos, String moves) {
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

		public int f(Position goal) {
			return g() + pos.manhattanDistance(goal);
		}

		public int g() {
			return moves.length();
		}
	}

	private static class Move {
		final Heading heading;
		final String name;

		public Move(Heading heading, String name) {
			this.heading = heading;
			this.name = name;
		}

		@Override
		public String toString() {
			return MoreObjects.toStringHelper(this)
					.add("heading", heading)
					.add("name", name)
					.toString();
		}
	}

	private static final Move[] MOVES = new Move[] {
			new Move(Heading.N, "U"),
			new Move(Heading.S, "D"),
			new Move(Heading.W, "L"),
			new Move(Heading.E, "R"),
	};

	public void run(String seed) {
		Position start = new Position(0, 0);
		Position goal = new Position(3, 3);
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
						Position newPos = state.pos.add(move.heading.getPos());
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

	private boolean isOutOfBounds(Position newPos) {
		return (newPos.x < 0 || newPos.y < 0 || newPos.x > 3 || newPos.y > 3);
	}

	public static void main(String[] args) throws Exception {
		Day17 worker = new Day17();
		worker.run("edjrjqaa");
	}

}
