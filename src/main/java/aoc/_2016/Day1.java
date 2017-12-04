package aoc._2016;

import shared.Heading;
import shared.Position;
import shared.ResourceUtil;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * http://adventofcode.com/2016
 *
 * @author Dan Fielding
 */
public class Day1 {

	private void parseFile(String filename) throws IOException {
		List<String> lines = ResourceUtil.readAllLines(filename);
		int blocks = parseLine(lines.get(0));
		System.out.println("Blocks: " + blocks);
	}

	private int manhattanDistance(Position pos) {
		return Math.abs(pos.getX()) + Math.abs(pos.getY());
	}

	private int parseLine(String line) {
		String[] directions = line.split(", ");
		Heading heading = Heading.N;
		Position pos = new Position(0, 0);
		Set<Position> previousPos = new HashSet<Position>();
		for (String direction : directions) {
			char dir = direction.charAt(0);
			int dist = Integer.parseInt(direction.substring(1));
			switch (dir) {
				case 'R':
					heading = heading.turnRight();
					break;
				case 'L':
					heading = heading.turnLeft();
					break;
			}
			for (int i = 0; i < dist; i++) {
				if (previousPos.contains(pos)) {
					System.out.println("Been here before: " + pos + ", " + manhattanDistance(pos));
				}
				previousPos.add(pos);
				pos = pos.add(new Position(heading.getX(), heading.getY()));
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
