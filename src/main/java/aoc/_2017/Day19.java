package aoc._2017;

import java.util.List;

import com.google.common.collect.ImmutableList;

import shared.Dir4;
import shared.Grid;
import shared.IntVector2;
import shared.ResourceUtil;
import shared.Test;
import shared.Timer;

/**
 * @author Dan Fielding
 */
public class Day19 {

/*

--- Day 19: A Series of Tubes ---

Somehow, a network packet got lost and ended up here. It's trying to follow a
routing diagram (your puzzle input), but it's confused about where to go.

Its starting point is just off the top of the diagram. Lines (drawn with |, -,
and +) show the path it needs to take, starting by going down onto the only line
connected to the top of the diagram. It needs to follow this path until it
reaches the end (located somewhere within the diagram) and stop there.

Sometimes, the lines cross over each other; in these cases, it needs to continue
going the same direction, and only turn left or right when there's no other
option. In addition, someone has left letters on the line; these also don't
change its direction, but it can use them to keep track of where it's been. For
example:

     |
     |  +--+
     A  |  C
 F---|----E|--+
     |  |  |  D
     +B-+  +--+

Given this diagram, the packet needs to take the following path:

    - Starting at the only line touching the top of the diagram, it must go
      down, pass through A, and continue onward to the first +.
    - Travel right, up, and right, passing through B in the process.
    - Continue down (collecting C), right, and up (collecting D).
    - Finally, go all the way left through E and stopping at F.

Following the path to the end, the letters it sees on its path are ABCDEF.

The little packet looks up at you, hoping you can help it find the way. What
letters will it see (in the order it would see them) if it follows the path?
(The routing diagram is very wide; make sure you view it without line wrapping.)


--- Part Two ---

The packet is curious how many steps it needs to go.

For example, using the same routing diagram from the example above...

     |
     |  +--+
     A  |  C
 F---|--|-E---+
     |  |  |  D
     +B-+  +--+

...the packet would go:

- 6 steps down (including the first line at the top of the diagram).
- 3 steps right.
- 4 steps up.
- 3 steps right.
- 4 steps down.
- 3 steps right.
- 2 steps up.
- 13 steps left (including the F it stops on).

This would result in a total of 38 steps.

How many steps does the packet need to go?

*/

	class Node {
		final char c;
		Node(char c) {
			this.c = c;
		}
	}

	class Result {
		final String nodes;
		final int steps;
		Result(String nodes, int steps) {
			this.nodes = nodes;
			this.steps = steps;
		}
	}

	public Grid<Node> createGrid(List<String> input) {
		int w = input.stream().mapToInt(String::length).max().orElse(0);
		int h = input.size();
		Grid<Node> result = new Grid<>(w, h);
		for (int j = 0; j < h; j++) {
			String line = input.get(j);
			for (int i = 0; i < line.length(); i++) {
				result.set(i, j, new Node(line.charAt(i)));
			}
		}
		return result;
	}

	public boolean checkValid(Grid<Node> grid, IntVector2 pos) {
		if (!grid.isInBounds(pos)) return false;
		Node node = grid.get(pos);
		if (node == null || node.c == ' ') return false;
		return true;
	}

	public Result path(List<String> input) {
		Grid<Node> grid = createGrid(input);

		Dir4 dir = Dir4.S;
		// Find start pos
		IntVector2 pos = null;
		for (int i = 0; i < grid.getNumCells().x; i++) {
			if (grid.get(i, 0).c == '|') {
				pos = new IntVector2(i, 0); break;
			}
		}
		if (pos == null) throw new RuntimeException("Couldn't find start");

		StringBuilder result = new StringBuilder();
		int steps = 0;
		while (true) {
			// Move
			pos = pos.add(dir.getStep());
			steps++;

			// End when no more path / out of bounds?
			if (!grid.isInBounds(pos)) break;
			Node node = grid.get(pos);
			if (node == null || node.c == ' ') break;

			// If letter encountered, add to result
			if (node.c >= 'A' && node.c <= 'Z') {
				result.append(node.c);
			}
			// If cross encountered, determine new direction
			if (node.c == '+') {
				// check rotate left, right and use whichever isn't blank/out of bounds
				dir = dir.rotateAntiClockwise();
				if (!checkValid(grid, pos.add(dir.getStep()))) {
					dir = dir.opposite();
				}
			}

		}
		return new Result(result.toString(), steps);
	}

	public void examples() {
		List<String> input = ImmutableList.of(
				"     | ",
				"     |  +--+",
				"     A  |  C",
				" F---|----E|--+",
				"     |  |  |  D",
				"     +B-+  +--+"
		);
		Result result = path(input);
		Test.check(result.nodes, "ABCDEF");
		Test.check(result.steps, 38);
	}

	public static void main(String[] args) throws Exception {
		Day19 day = new Day19();
		day.examples();

		Timer.start();
		List<String> input = ResourceUtil.readAllLines("2017/day19.input");
		Result result = day.path(input);
		System.out.println(result.nodes);
		System.out.println(result.steps);
		Timer.endMessage();
	}
}
