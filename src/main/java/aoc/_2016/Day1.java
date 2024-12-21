package aoc._2016;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import shared.Dir4;
import shared.IntVector2;
import shared.ResourceUtil;

public class Day1 {

	private void parseFile(String filename) throws IOException {
		List<String> lines = ResourceUtil.readAllLines(filename);
		int blocks = parseLine(lines.get(0));
		System.out.println("Blocks: " + blocks);
	}

	private int manhattanDistance(IntVector2 pos) {
		return Math.abs(pos.getX()) + Math.abs(pos.getY());
	}

	private int parseLine(String line) {
		String[] directions = line.split(", ");
		Dir4 heading = Dir4.N;
		IntVector2 pos = new IntVector2(0, 0);
		Set<IntVector2> previousPos = new HashSet<IntVector2>();
		for (String direction : directions) {
			char dir = direction.charAt(0);
			int dist = Integer.parseInt(direction.substring(1));
			switch (dir) {
				case 'R':
					heading = heading.rotateClockwise();
					break;
				case 'L':
					heading = heading.rotateAntiClockwise();
					break;
			}
			for (int i = 0; i < dist; i++) {
				if (previousPos.contains(pos)) {
					System.out.println("Been here before: " + pos + ", " + manhattanDistance(pos));
				}
				previousPos.add(pos);
				pos = pos.add(new IntVector2(heading.getX(), heading.getY()));
			}
		}
		return manhattanDistance(pos);
	}

	public static void main(String[] args) throws Exception {
		String filename = "2016/day1.input";

		Day1 worker = new Day1();
		worker.parseFile(filename);
	}

}
