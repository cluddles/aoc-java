package aoc._2017;

import java.util.List;

import com.google.common.collect.ImmutableList;

import shared.Dir4;
import shared.Grid;
import shared.IntVector2;
import shared.ResourceUtil;
import shared.Test;
import shared.Timer;

public class Day19 {

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
