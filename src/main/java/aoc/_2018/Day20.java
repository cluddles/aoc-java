package aoc._2018;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Deque;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableMap;

import shared.Dir4;
import shared.IntVector2;
import shared.ResourceUtil;
import shared.Test;

public class Day20 {

	// No doors until otherwise informed
	static class Room {
		final IntVector2 pos;
		Set<Dir4> doors = EnumSet.noneOf(Dir4.class);
		Room(IntVector2 pos) {
			this.pos = pos;
		}
	}

	// Track all the current permutations to expand
	static class Permutation {
		final IntVector2        pos;
		final int               index;
		final Deque<BraceState> braces;
		Permutation(IntVector2 pos, int index, Deque<BraceState> braces) {
			this.pos    = pos;
			this.index  = index;
			this.braces = braces;
		}
		Permutation next(IntVector2 newPos) {
			return new Permutation(newPos, index+1, new LinkedList<>(braces));
		}
		// equals/hashcode ignore braces, but that's fine - two permutations
		// at the same position for the same index shouldn't ever have different
		// brace states.
		@Override public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			Permutation that = (Permutation) o;
			return index == that.index &&
					Objects.equal(pos, that.pos);
		}
		@Override public int hashCode() {
			return Objects.hashCode(pos, index);
		}
		public IntVector2 getPos()   { return pos;   }
		public int        getIndex() { return index; }
	}

	// Track start position and end positions generated within a (...) section
	static class BraceState {
		final IntVector2 start;
		List<IntVector2> ends  = new ArrayList<>();
		BraceState(IntVector2 start) {
			this.start = start;
		}
	}

	private static final Comparator<Permutation> PERMUTATION_COMPARATOR = Comparator
			.comparing(Permutation::getIndex)
			.thenComparing(Permutation::getPos, IntVector2.READING_ORDER);

	private Map<IntVector2, Room> rooms = new HashMap<>();

	Day20(String input) {
		init(input);
	}

	void init(String input) {
		regexPathing(input);
	}

	// Traverse the regex and generate rooms
	private void regexPathing(String input) {
		// Track all current states
		Set<Permutation> permutations = new HashSet<>();
		Set<Permutation> done         = new HashSet<>();
		permutations.add(new Permutation(IntVector2.ZERO, 0, new LinkedList<>()));
		while (!permutations.isEmpty()) {
			// Order shouldn't matter, but let's be consistent
			Permutation permutation = permutations.stream()
					.max(PERMUTATION_COMPARATOR)
					.orElseThrow(() -> new IllegalStateException("No permutation"));
			permutations.remove(permutation);
			done        .add   (permutation);
			IntVector2 pos = permutation.pos;
			int        i   = permutation.index;
			char       c   = input.charAt(i);
			// System.out.println(permutations.size() + ":" + i + ":" + c + ":" + pos + ":" + rooms.size());
			switch (c) {
			case '^':
				// Start
				addPermutation(permutations, done, permutation.next(pos));
				break;

			case '$':
				// Nothing to do
				break;

			case 'E':
			case 'S':
			case 'N':
			case 'W':
				// It's a move
				Dir4 dir = Dir4.valueOf(String.valueOf(c));
				pos = move(pos, dir);
				addPermutation(permutations, done, permutation.next(pos));
				break;

			case '(':
				// Init new brace state
				permutation.braces.push(new BraceState(pos));
				addPermutation(permutations, done, permutation.next(pos));
				break;

			case '|': {
				// Remember this position
				BraceState state = permutation.braces.peek();
				state.ends.add(pos);
				pos = state.start;
				addPermutation(permutations, done, permutation.next(pos));
				break;
			}

			case ')': {
				// Pop all positions as permutations
				BraceState state = permutation.braces.pop();
				state.ends.add(pos);
				for (IntVector2 p : state.ends) {
					addPermutation(permutations, done, permutation.next(p));
				}
				break;
			}

			default:
				break;
			}
		}
	}

	void addPermutation(Set<Permutation> permutations, Set<Permutation> done, Permutation permutation) {
		// Don't add a permutation we've already seen
		if (done.contains(permutation)) return;
		permutations.add(permutation);
	}

	// Move in given direction and create rooms/doors as appropriate.
	IntVector2 move(IntVector2 pos, Dir4 dir) {
		IntVector2 newPos = pos.add(dir.getStep());
		Room from = rooms.computeIfAbsent(pos,    Room::new);
		Room to   = rooms.computeIfAbsent(newPos, Room::new);
		from.doors.add(dir);
		to  .doors.add(dir.opposite());
		return newPos;
	}

	public int evalPart1() {
		// Flood fill will do
		Map<Room, Integer> costs = new HashMap<>();
		return fillPart1(costs, IntVector2.ZERO, 0);
	}
	private int fillPart1(Map<Room, Integer> costs, IntVector2 pos, int cost) {
		// Max cost of this and any recursive calls
		int result = 0;
		Room room = rooms.get(pos);
		if (!costs.containsKey(room)) {
			result = cost;
			costs.put(room, cost);
			for (Dir4 dir : Dir4.values()) {
				if (room.doors.contains(dir)) {
					result = Math.max(result, fillPart1(costs, pos.add(dir.getStep()), cost + 1));
				}
			}
		}
		return result;
	}

	public int evalPart2() {
		Map<Room, Integer> costs = new HashMap<>();
		return fillPart2(costs, IntVector2.ZERO, 0);
	}
	private int fillPart2(Map<Room, Integer> costs, IntVector2 pos, int cost) {
		// Sum of this and recursive calls
		int result = 0;
		Room room = rooms.get(pos);
		if (!costs.containsKey(room)) {
			// Only care about rooms 1000 steps or more away from start
			if (cost >= 1000) result++;
			costs.put(room, cost);
			for (Dir4 dir : Dir4.values()) {
				if (room.doors.contains(dir)) {
					result += fillPart2(costs, pos.add(dir.getStep()), cost + 1);
				}
			}
		}
		return result;
	}

	public static void main(String[] args) throws Exception {
		String               input    = ResourceUtil.readString("2018/day20.input");
		Map<String, Integer> examples = ImmutableMap
				.<String, Integer>builder()
				.put("^WNE$", 3)
				.put("^ENWWW(NEEE|SSE(EE|N))$", 10)
				.put("^ENNWSWW(NEWS|)SSSEEN(WNSE|)EE(SWEN|)NNN$", 18)
				.put("^ESSWWN(E|NNENN(EESS(WNSE|)SSS|WWWSSSSE(SW|NNNE)))$", 23)
				.put("^WSSEESWWWNW(S|NENNEEEENN(ESSSSW(NWSW|SSEN)|WSWWN(E|WWS(E|SS))))$", 31)
				.build();

		// Part 1
		// Examples
		for (Map.Entry<String, Integer> entry : examples.entrySet()) {
			Test.check(new Day20(entry.getKey()).evalPart1(), entry.getValue());
		}
		// Vs Input
		System.out.println(new Day20(input).evalPart1());

		// Part 2
		// Vs Input
		System.out.println(new Day20(input).evalPart2());
	}
}
