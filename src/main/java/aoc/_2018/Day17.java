package aoc._2018;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import shared.Dir4;
import shared.Grid;
import shared.IntVector2;
import shared.Rect;
import shared.ResourceUtil;
import shared.Test;
import shared.Timer;

/**
 * @author Dan Fielding
 */
public class Day17 {
	/*
--- Day 17: Reservoir Research ---

You arrive in the year 18. If it weren't for the coat you got in 1018, you would
be very cold: the North Pole base hasn't even been constructed.

Rather, it hasn't been constructed yet. The Elves are making a little progress,
but there's not a lot of liquid water in this climate, so they're getting very
dehydrated. Maybe there's more underground?

You scan a two-dimensional vertical slice of the ground nearby and discover that
it is mostly sand with veins of clay. The scan only provides data with a
granularity of square meters, but it should be good enough to determine how much
water is trapped there. In the scan, x represents the distance to the right, and
y represents the distance down. There is also a spring of water near the surface
at x=500, y=0. The scan identifies which square meters are clay (your puzzle
input).

For example, suppose your scan shows the following veins of clay:

x=495, y=2..7
y=7, x=495..501
x=501, y=3..7
x=498, y=2..4
x=506, y=1..2
x=498, y=10..13
x=504, y=10..13
y=13, x=498..504

Rendering clay as #, sand as ., and the water spring as +, and with x increasing
to the right and y increasing downward, this becomes:

   44444455555555
   99999900000000
   45678901234567
 0 ......+.......
 1 ............#.
 2 .#..#.......#.
 3 .#..#..#......
 4 .#..#..#......
 5 .#.....#......
 6 .#.....#......
 7 .#######......
 8 ..............
 9 ..............
10 ....#.....#...
11 ....#.....#...
12 ....#.....#...
13 ....#######...

The spring of water will produce water forever. Water can move through sand, but
is blocked by clay. Water always moves down when possible, and spreads to the
left and right otherwise, filling space that has clay on both sides and falling
out otherwise.

For example, if five squares of water are created, they will flow downward until
they reach the clay and settle there. Water that has come to rest is shown here
as ~, while sand through which water has passed (but which is now dry again) is
shown as |:

......+.......
......|.....#.
.#..#.|.....#.
.#..#.|#......
.#..#.|#......
.#....|#......
.#~~~~~#......
.#######......
..............
..............
....#.....#...
....#.....#...
....#.....#...
....#######...

Two squares of water can't occupy the same location. If another five squares of
water are created, they will settle on the first five, filling the clay
reservoir a little more:

......+.......
......|.....#.
.#..#.|.....#.
.#..#.|#......
.#..#.|#......
.#~~~~~#......
.#~~~~~#......
.#######......
..............
..............
....#.....#...
....#.....#...
....#.....#...
....#######...

Water pressure does not apply in this scenario. If another four squares of water
are created, they will stay on the right side of the barrier, and no water will
reach the left side:

......+.......
......|.....#.
.#..#.|.....#.
.#..#~~#......
.#..#~~#......
.#~~~~~#......
.#~~~~~#......
.#######......
..............
..............
....#.....#...
....#.....#...
....#.....#...
....#######...

At this point, the top reservoir overflows. While water can reach the tiles
above the surface of the water, it cannot settle there, and so the next five
squares of water settle like this:

......+.......
......|.....#.
.#..#||||...#.
.#..#~~#|.....
.#..#~~#|.....
.#~~~~~#|.....
.#~~~~~#|.....
.#######|.....
........|.....
........|.....
....#...|.#...
....#...|.#...
....#~~~~~#...
....#######...

Note especially the leftmost |: the new squares of water can reach this tile,
but cannot stop there. Instead, eventually, they all fall to the right and
settle in the reservoir below.

After 10 more squares of water, the bottom reservoir is also full:

......+.......
......|.....#.
.#..#||||...#.
.#..#~~#|.....
.#..#~~#|.....
.#~~~~~#|.....
.#~~~~~#|.....
.#######|.....
........|.....
........|.....
....#~~~~~#...
....#~~~~~#...
....#~~~~~#...
....#######...

Finally, while there is nowhere left for the water to settle, it can reach a few
more tiles before overflowing beyond the bottom of the scanned data:

......+.......    (line not counted: above minimum y value)
......|.....#.
.#..#||||...#.
.#..#~~#|.....
.#..#~~#|.....
.#~~~~~#|.....
.#~~~~~#|.....
.#######|.....
........|.....
...|||||||||..
...|#~~~~~#|..
...|#~~~~~#|..
...|#~~~~~#|..
...|#######|..
...|.......|..    (line not counted: below maximum y value)
...|.......|..    (line not counted: below maximum y value)
...|.......|..    (line not counted: below maximum y value)

How many tiles can be reached by the water? To prevent counting forever, ignore
tiles with a y coordinate smaller than the smallest y coordinate in your scan
data or larger than the largest one. Any x coordinate is valid. In this example,
the lowest y coordinate given is 1, and the highest is 13, causing the water
spring (in row 0) and the water falling off the bottom of the render (in rows 14
through infinity) to be ignored.

So, in the example above, counting both water at rest (~) and other sand tiles
the water can hypothetically reach (|), the total number of tiles the water can
reach is 57.

How many tiles can the water reach within the range of y values in your scan?

 */

	private static final Pattern LINE_PATTERN = Pattern.compile(
			"(\\w)=(\\d+), \\w=(\\d+)\\.\\.(\\d+)");

	private static final IntVector2 WATER_SOURCE_POS = new IntVector2(500, 0);

	private static final char TILE_EMPTY         = '.';
	private static final char TILE_WALL          = '#';
	private static final char TILE_WATER_SOURCE  = '+';
	private static final char TILE_WATER_SETTLED = '~';
	private static final char TILE_WATER_TOUCHED = '|';


	static class State {
		final Grid<Character> tiles;
		// Where the tile region is
		final Rect            gridBounds;
		// Which tiles are valid for the result
		final Rect            answerBounds;

		State(Rect gridBounds, Rect answerBounds) {
			this.tiles        = new Grid<>(gridBounds.getSize());
			this.gridBounds   = gridBounds;
			this.answerBounds = answerBounds;
		}
	}

	private State state;

	// Positions to process
	private Set<IntVector2> procPositions = new HashSet<>();

	Day17(List<String> lines) {
		init(lines);
	}

	private void init(List<String> lines) {
		// Read input -> rectangles
		List<Rect> rects = new ArrayList<>();
		for (String line : lines) {
			Matcher matcher = LINE_PATTERN.matcher(line);
			if (matcher.matches()) {
				int v1 = Integer.parseInt(matcher.group(2));
				int v2 = Integer.parseInt(matcher.group(3));
				int v3 = Integer.parseInt(matcher.group(4));
				if (matcher.group(1).equals("x")) {
					// x, y-span
					rects.add(new Rect(
							new IntVector2(v1, v2),
							new IntVector2(1, v3 - v2 + 1)));
				} else {
					// y, x-span
					rects.add(new Rect(
							new IntVector2(v2, v1),
							new IntVector2(v3 - v2 + 1, 1)));
				}
			}
		}
		// Work out tiles to consider for answer
		Rect answerBounds = rects.iterator().next();
		for (Rect rect : rects) {
			answerBounds = answerBounds.union(rect);
		}
		// Expand left/right by 1, so we can deal with run off near the border
		answerBounds = answerBounds.resize(
				new IntVector2(-1, 0),
				new IntVector2( 1, 0));
		// Full grid to process should include water source
		Rect gridBounds = answerBounds.union(new Rect(WATER_SOURCE_POS, IntVector2.ONE));
		// Fill the map with empty tiles
		state = new State(gridBounds, answerBounds);
		for (int i = 0; i < state.tiles.getNumCells().x; i++) {
			for (int j = 0; j < state.tiles.getNumCells().y; j++) {
				state.tiles.set(i, j, TILE_EMPTY);
			}
		}
		setTile(WATER_SOURCE_POS, TILE_WATER_SOURCE);
		for (Rect rect : rects) {
			setTileArea(rect, TILE_WALL);
		}
	}

	Character getTile(IntVector2 pos) {
		return state.tiles.get(pos.subtract(state.gridBounds.getStart()));
	}
	void setTile(IntVector2 pos, Character c) {
		state.tiles.set(pos.subtract(state.gridBounds.getStart()), c);
	}
	void setTileArea(Rect rect, Character c) {
		for (int i = rect.getStart().getX(); i < rect.end().getX(); i++) {
			for (int j = rect.getStart().getY(); j < rect.end().getY(); j++) {
				setTile(new IntVector2(i, j), c);
			}
		}
	}
	boolean isInBounds(IntVector2 pos) {
		return state.gridBounds.contains(pos);
	}
	boolean isFlowBlockerAt(IntVector2 pos) {
		Character tile = getTile(pos);
		return tile == TILE_WATER_SETTLED || tile == TILE_WALL;
	}

	void dumpState() {
		System.out.println(state.gridBounds);
		System.out.println(state.answerBounds);
		System.out.println(state.tiles.dumpContents(String::valueOf));
	}
	int count(boolean settled, boolean touched) {
		int result = 0;
		Rect rect = state.answerBounds;
		for (int i = rect.getStart().getX(); i < rect.end().getX(); i++) {
			for (int j = rect.getStart().getY(); j < rect.end().getY(); j++) {
				Character tile = getTile(new IntVector2(i, j));
				if ((settled && tile == TILE_WATER_SETTLED)
						|| (touched && tile == TILE_WATER_TOUCHED)) result++;
			}
		}
		return result;
	}

	void proc(IntVector2 pos) {
		procPositions.add(pos);
	}
	void proc(IntVector2 pos, Character tile) {
		if (getTile(pos) == tile) return;
		setTile(pos, tile);
		proc(pos);
	}
	void simulate() {
		proc(WATER_SOURCE_POS);
		while (!procPositions.isEmpty()) {
			IntVector2 pos = procPositions.stream()
					.max(IntVector2.READING_ORDER)
					.orElseThrow(() -> new IllegalStateException("Nothing to pop"));
			procPositions.remove(pos);
			process(pos);
//			dumpState();
		}
	}
	void process(IntVector2 pos) {
		Character tile = getTile(pos);
		// Settled water spreads left+right
		if (tile == TILE_WATER_SETTLED) {
			// Settle left, right
			spreadX(pos, TILE_WATER_SETTLED);
			// Re-process any tiles above that had water flow through them
			IntVector2 above = pos.add(Dir4.N.step);
			if (getTile(above) == TILE_WATER_TOUCHED) proc(above);
		}
		// Flowing water spreads down, left+right if supported, may settle
		if (tile == TILE_WATER_TOUCHED || tile == TILE_WATER_SOURCE) {
			// Is there a blocker below this flow?
			IntVector2 below = pos.add(Dir4.S.step);
			if (isInBounds(below)) {
				if (isFlowBlockerAt(below)) {
					// Blocked tile below
					if (canSettle(pos, Dir4.E.step) && canSettle(pos, Dir4.W.step)) {
						// Settle
						proc(pos, TILE_WATER_SETTLED);
					} else {
						// Flow left, right
						spreadX(pos, TILE_WATER_TOUCHED);
					}
				} else {
					// Flow down
					proc(below, TILE_WATER_TOUCHED);
				}
			}
		}
	}
	void spreadX(IntVector2 pos, Character tile) {
		IntVector2 left  = pos.add(Dir4.W.step);
		if (!isFlowBlockerAt(left))  proc(left, tile);
		IntVector2 right = pos.add(Dir4.E.step);
		if (!isFlowBlockerAt(right)) proc(right, tile);
	}
	boolean canSettle(IntVector2 pos, IntVector2 step) {
		while (true) {
			// Fail if empty tile below
			if (!isFlowBlockerAt(pos.add(Dir4.S.step))) return false;
			// Move along, if we hit a wall then consider it good
			pos = pos.add(step);
			if (isFlowBlockerAt(pos)) return true;
		}
	}

	public int evalPart1() {
		simulate();
		dumpState();
		return count(true, true);
	}

	public int evalPart2() {
		simulate();
		dumpState();
		return count(true, false);
	}

	public static void main(String[] args) throws Exception {
		List<String> example = ResourceUtil.readAllLines("2018/day17.example");
		List<String> input   = ResourceUtil.readAllLines("2018/day17.input");

		// Part 1
		// Example
		Test.check(new Day17(example).evalPart1(), 57);
		// Vs Input
		Timer.startMessage();
		System.out.println(new Day17(input).evalPart1());
		Timer.endMessage();
		// Failures: 35432 (too low)
		//           35455 (too low)  - time for a rewrite
		//           39164 (too high) - forgot to ignore tiles based on y

		// Part 2
		// Example
		Test.check(new Day17(example).evalPart2(), 29);
		// Vs Input
		Timer.startMessage();
		System.out.println(new Day17(input).evalPart2());
		Timer.endMessage();
	}
}
