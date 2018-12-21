package aoc._2018;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import shared.Dir8;
import shared.Grid;
import shared.IntVector2;
import shared.ResourceUtil;
import shared.Test;

/**
 * @author Dan Fielding
 */
public class Day18 {
/*
--- Day 18: Settlers of The North Pole ---

On the outskirts of the North Pole base construction project, many Elves are
collecting lumber.

The lumber collection area is 50 acres by 50 acres; each acre can be either open
ground (.), trees (|), or a lumberyard (#). You take a scan of the area (your
puzzle input).

Strange magic is at work here: each minute, the landscape looks entirely
different. In exactly one minute, an open acre can fill with trees, a wooded
acre can be converted to a lumberyard, or a lumberyard can be cleared to open
ground (the lumber having been sent to other projects).

The change to each acre is based entirely on the contents of that acre as well
as the number of open, wooded, or lumberyard acres adjacent to it at the start
of each minute. Here, "adjacent" means any of the eight acres surrounding that
acre. (Acres on the edges of the lumber collection area might have fewer than
eight adjacent acres; the missing acres aren't counted.)

In particular:

    An open acre will become filled with trees if three or more adjacent acres
        contained trees. Otherwise, nothing happens.
    An acre filled with trees will become a lumberyard if three or more adjacent
        acres were lumberyards. Otherwise, nothing happens.
    An acre containing a lumberyard will remain a lumberyard if it was adjacent
        to at least one other lumberyard and at least one acre containing trees.
        Otherwise, it becomes open.

These changes happen across all acres simultaneously, each of them using the
state of all acres at the beginning of the minute and changing to their new form
by the end of that same minute. Changes that happen during the minute don't
affect each other.

For example, suppose the lumber collection area is instead only 10 by 10 acres
with this initial configuration:

Initial state:
.#.#...|#.
.....#|##|
.|..|...#.
..|#.....#
#.#|||#|#|
...#.||...
.|....|...
||...#|.#|
|.||||..|.
...#.|..|.

After 1 minute:
.......##.
......|###
.|..|...#.
..|#||...#
..##||.|#|
...#||||..
||...|||..
|||||.||.|
||||||||||
....||..|.

After 2 minutes:
.......#..
......|#..
.|.|||....
..##|||..#
..###|||#|
...#|||||.
|||||||||.
||||||||||
||||||||||
.|||||||||

After 3 minutes:
.......#..
....|||#..
.|.||||...
..###|||.#
...##|||#|
.||##|||||
||||||||||
||||||||||
||||||||||
||||||||||

After 4 minutes:
.....|.#..
...||||#..
.|.#||||..
..###||||#
...###||#|
|||##|||||
||||||||||
||||||||||
||||||||||
||||||||||

After 5 minutes:
....|||#..
...||||#..
.|.##||||.
..####|||#
.|.###||#|
|||###||||
||||||||||
||||||||||
||||||||||
||||||||||

After 6 minutes:
...||||#..
...||||#..
.|.###|||.
..#.##|||#
|||#.##|#|
|||###||||
||||#|||||
||||||||||
||||||||||
||||||||||

After 7 minutes:
...||||#..
..||#|##..
.|.####||.
||#..##||#
||##.##|#|
|||####|||
|||###||||
||||||||||
||||||||||
||||||||||

After 8 minutes:
..||||##..
..|#####..
|||#####|.
||#...##|#
||##..###|
||##.###||
|||####|||
||||#|||||
||||||||||
||||||||||

After 9 minutes:
..||###...
.||#####..
||##...##.
||#....###
|##....##|
||##..###|
||######||
|||###||||
||||||||||
||||||||||

After 10 minutes:
.||##.....
||###.....
||##......
|##.....##
|##.....##
|##....##|
||##.####|
||#####|||
||||#|||||
||||||||||

After 10 minutes, there are 37 wooded acres and 31 lumberyards. Multiplying the
number of wooded acres by the number of lumberyards gives the total resource
value after ten minutes: 37 * 31 = 1147.

What will the total resource value of the lumber collection area be after 10
minutes?

--- Part Two ---

This important natural resource will need to last for at least thousands of
years. Are the Elves collecting this lumber sustainably?

What will the total resource value of the lumber collection area be after
1000000000 minutes?

 */

	private static final char TILE_EMPTY      = '.';
	private static final char TILE_WOOD       = '|';
	private static final char TILE_LUMBERYARD = '#';

	static class State {
		final Grid<Character> tiles;
		State(Grid<Character> tiles) {
			this.tiles = tiles;
		}
	}

	State parse(List<String> lines) {
		return new State(Grid.fromLines(lines));
	}

	void dumpState(State state) {
		System.out.println(state.tiles.dumpContents(String::valueOf));
	}

	Map<Character, Integer> countAdjacents(State state, IntVector2 pos) {
		Map<Character, Integer> result = new HashMap<>();
		for (Dir8 dir : Dir8.values()) {
			IntVector2 newPos = pos.add(dir.getStep());
			if (!state.tiles.isInBounds(newPos)) continue;
			Character tile    = state.tiles.get(newPos);
			result.put(tile, result.getOrDefault(tile, 0) + 1);
		}
		return result;
	}

	State tick(State state) {
		// New map
		Grid<Character> newTiles = new Grid<>(state.tiles.getNumCells());
		// Iterate over grid
		Iterator<Grid.GridCell<Character>> cellIt = newTiles.cellIterator();
		while (cellIt.hasNext()) {
			Grid.GridCell<Character> cell = cellIt.next();
			// Existing tile
			Character tile = state.tiles.get(cell.getPos());
			Map<Character, Integer> adjacents = countAdjacents(state, cell.getPos());
			switch (tile) {
			case TILE_EMPTY:
				// An open acre will become filled with trees if three or more
				// adjacent acres contained trees. Otherwise, nothing happens.
				if (adjacents.getOrDefault(TILE_WOOD, 0) >= 3) tile = TILE_WOOD;
				break;

			case TILE_WOOD:
				// An acre filled with trees will become a lumberyard if three
				// or more adjacent acres were lumberyards. Otherwise, nothing
				// happens.
				if (adjacents.getOrDefault(TILE_LUMBERYARD, 0) >= 3) tile = TILE_LUMBERYARD;
				break;

			case TILE_LUMBERYARD:
				// An acre containing a lumberyard will remain a lumberyard if
				// it was adjacent to at least one other lumberyard and at least
				// one acre containing trees. Otherwise, it becomes open.
				if    ( adjacents.getOrDefault(TILE_WOOD,       0) < 1 ||
						adjacents.getOrDefault(TILE_LUMBERYARD, 0) < 1) {
					tile = TILE_EMPTY;
				}
				break;

			default:
				break;
			}
			cell.setData(tile);
		}
		return new State(newTiles);
	}

	long score(State state) {
		int numWoods = 0;
		int numYards = 0;
		Iterator<Character> it = state.tiles.iterator();
		while (it.hasNext()) {
			Character tile = it.next();
			switch (tile) {
			case TILE_WOOD:       numWoods++; break;
			case TILE_LUMBERYARD: numYards++; break;
			default:              break;
			}
		}
		return (long) numWoods * numYards;
	}

	State simulate(State state, int ticks) {
		for (int i = 0; i < ticks; i++) {
			state = tick(state);
			System.out.println(i + ":" + score(state));
		}
		dumpState(state);
		return state;
	}

	public long evalPart1(List<String> input) {
		State state = parse(input);
		state = simulate(state, 10);
		return score(state);
	}

	public long evalPart2(List<String> input) {
		State state = parse(input);
		// Obviously this will take forever, but you'll see it's a cycle
		//state = simulate(state, 1000000000);
		// Cycle starts at 566, length 28
		// 566 + (1000000000 - 566) % 28
		state = simulate(state, 566 + (1000000000 - 566) % 28);
		return score(state);
	}

	public static void main(String[] args) throws Exception {
		List<String> example = ResourceUtil.readAllLines("2018/day18.example");
		List<String> input   = ResourceUtil.readAllLines("2018/day18.input");

		// Part One
		// Example
		Test.check(new Day18().evalPart1(example), 1147L);
		// Vs Input
		System.out.println(new Day18().evalPart1(input));

		// Part Two
		// Vs Input
		System.out.println(new Day18().evalPart2(input));
	}

}
