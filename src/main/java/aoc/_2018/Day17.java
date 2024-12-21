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

public class Day17 {

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
