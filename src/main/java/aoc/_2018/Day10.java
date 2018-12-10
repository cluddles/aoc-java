package aoc._2018;

import shared.IntVector2;
import shared.Rect;
import shared.ResourceUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Dan Fielding
 */
public class Day10 {
/*
--- Day 10: The Stars Align ---

It's no use; your navigation system simply isn't capable of providing walking
directions in the arctic circle, and certainly not in 1018.

The Elves suggest an alternative. In times like these, North Pole rescue
operations will arrange points of light in the sky to guide missing Elves back
to base. Unfortunately, the message is easy to miss: the points move slowly
enough that it takes hours to align them, but have so much momentum that they
only stay aligned for a second. If you blink at the wrong time, it might be
hours before another message appears.

You can see these points of light floating in the distance, and record their
position in the sky and their velocity, the relative change in position per
second (your puzzle input). The coordinates are all given from your perspective;
given enough time, those positions and velocities will move the points into a
cohesive message!

Rather than wait, you decide to fast-forward the process and calculate what the
points will eventually spell.

For example, suppose you note the following points:

position=< 9,  1> velocity=< 0,  2>
position=< 7,  0> velocity=<-1,  0>
position=< 3, -2> velocity=<-1,  1>
position=< 6, 10> velocity=<-2, -1>
position=< 2, -4> velocity=< 2,  2>
position=<-6, 10> velocity=< 2, -2>
position=< 1,  8> velocity=< 1, -1>
position=< 1,  7> velocity=< 1,  0>
position=<-3, 11> velocity=< 1, -2>
position=< 7,  6> velocity=<-1, -1>
position=<-2,  3> velocity=< 1,  0>
position=<-4,  3> velocity=< 2,  0>
position=<10, -3> velocity=<-1,  1>
position=< 5, 11> velocity=< 1, -2>
position=< 4,  7> velocity=< 0, -1>
position=< 8, -2> velocity=< 0,  1>
position=<15,  0> velocity=<-2,  0>
position=< 1,  6> velocity=< 1,  0>
position=< 8,  9> velocity=< 0, -1>
position=< 3,  3> velocity=<-1,  1>
position=< 0,  5> velocity=< 0, -1>
position=<-2,  2> velocity=< 2,  0>
position=< 5, -2> velocity=< 1,  2>
position=< 1,  4> velocity=< 2,  1>
position=<-2,  7> velocity=< 2, -2>
position=< 3,  6> velocity=<-1, -1>
position=< 5,  0> velocity=< 1,  0>
position=<-6,  0> velocity=< 2,  0>
position=< 5,  9> velocity=< 1, -2>
position=<14,  7> velocity=<-2,  0>
position=<-3,  6> velocity=< 2, -1>

Each line represents one point. Positions are given as <X, Y> pairs: X
represents how far left (negative) or right (positive) the point appears, while
Y represents how far up (negative) or down (positive) the point appears.

At 0 seconds, each point has the position given. Each second, each point's
velocity is added to its position. So, a point with velocity <1, -2> is moving
to the right, but is moving upward twice as quickly. If this point's initial
position were <3, 9>, after 3 seconds, its position would become <6, 3>.

Over time, the points listed above would move like this:

Initially:
........#.............
................#.....
.........#.#..#.......
......................
#..........#.#.......#
...............#......
....#.................
..#.#....#............
.......#..............
......#...............
...#...#.#...#........
....#..#..#.........#.
.......#..............
...........#..#.......
#...........#.........
...#.......#..........

After 1 second:
......................
......................
..........#....#......
........#.....#.......
..#.........#......#..
......................
......#...............
....##.........#......
......#.#.............
.....##.##..#.........
........#.#...........
........#...#.....#...
..#...........#.......
....#.....#.#.........
......................
......................

After 2 seconds:
......................
......................
......................
..............#.......
....#..#...####..#....
......................
........#....#........
......#.#.............
.......#...#..........
.......#..#..#.#......
....#....#.#..........
.....#...#...##.#.....
........#.............
......................
......................
......................

After 3 seconds:
......................
......................
......................
......................
......#...#..###......
......#...#...#.......
......#...#...#.......
......#####...#.......
......#...#...#.......
......#...#...#.......
......#...#...#.......
......#...#..###......
......................
......................
......................
......................

After 4 seconds:
......................
......................
......................
............#.........
........##...#.#......
......#.....#..#......
.....#..##.##.#.......
.......##.#....#......
...........#....#.....
..............#.......
....#......#...#......
.....#.....##.........
...............#......
...............#......
......................
......................

After 3 seconds, the message appeared briefly: HI. Of course, your message will
be much longer and will take many more seconds to appear.

What message will eventually appear in the sky?

--- Part Two ---

Good thing you didn't have to wait, because that would have taken a long time -
much longer than the 3 seconds in the example above.

Impressed by your sub-hour communication capabilities, the Elves are curious:
exactly how many seconds would they have needed to wait for that message to
appear?

 */

	private static final Pattern LINE_PATTERN = Pattern.compile(
			"position=<(.+),(.+)> velocity=<(.+),(.+)>");

	class Particle {
		final IntVector2 pos;
		final IntVector2 velocity;

		public Particle(IntVector2 pos, IntVector2 velocity) {
			this.pos = pos;
			this.velocity = velocity;
		}
	}

	int parseInt(String str) {
		return Integer.parseInt(str.trim());
	}

	List<Particle> parse(List<String> input) {
		List<Particle> result = new ArrayList<>();
		for (String line : input) {
			Matcher m = LINE_PATTERN.matcher(line);
			if (!m.matches()) throw new IllegalStateException("Invalid line: " + line);
			result.add(new Particle(
					new IntVector2(
							parseInt(m.group(1)),
							parseInt(m.group(2))),
					new IntVector2(
							parseInt(m.group(3)),
							parseInt(m.group(4)))));
		}
		return result;
	}

	Rect activeArea(List<Particle> particles) {
		int minX = Integer.MAX_VALUE;
		int minY = Integer.MAX_VALUE;
		int maxX = Integer.MIN_VALUE;
		int maxY = Integer.MIN_VALUE;
		for (Particle p : particles) {
			minX = Math.min(p.pos.x, minX);
			minY = Math.min(p.pos.y, minY);
			maxX = Math.max(p.pos.x, maxX);
			maxY = Math.max(p.pos.y, maxY);
		}
		return new Rect(
				new IntVector2(minX, minY),
				new IntVector2(maxX - minX + 1, maxY - minY + 1));
	}

	void result(List<Particle> particles, Rect bounds) {
		boolean[][] grid = new boolean[bounds.size.x][bounds.size.y];
		for (Particle p : particles) {
			grid[p.pos.x - bounds.start.x][p.pos.y - bounds.start.y] = true;
		}
		for (int j = 0; j < bounds.size.y; j++) {
			StringBuilder row = new StringBuilder();
			for (int i = 0; i < bounds.size.x; i++) {
				row.append(grid[i][j]? "#" : ".");
			}
			System.out.println(row.toString());
		}
	}

	public void simulate(List<String> lines) {
		int ticks = 0;
		List<Particle> particles = parse(lines);
		Rect bounds = new Rect(
				IntVector2.ZERO,
				new IntVector2(Integer.MAX_VALUE, Integer.MAX_VALUE));
		while (true) {
			List<Particle> newParticles = new ArrayList<>();
			for (Particle p : particles) {
				newParticles.add(new Particle(
						p.pos.add(p.velocity),
						p.velocity));
			}
			Rect newBounds = activeArea(newParticles);
			if (newBounds.area() > bounds.area()) {
				result(particles, bounds);
				System.out.println(ticks);
				return;
			}
			particles = newParticles;
			bounds    = newBounds;
			ticks++;
		}
	}

	public static void main(String[] args) throws Exception {
		List<String> lines = ResourceUtil.readAllLines("2018/day10.input");
		new Day10().simulate(lines);
	}

}
