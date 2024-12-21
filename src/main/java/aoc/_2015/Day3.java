package aoc._2015;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.common.base.Objects;

import shared.ResourceUtil;

public class Day3 {

	class Position {
		final int x, y;
		Position(int x, int y) {
			this.x = x;
			this.y = y;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			Position position = (Position) o;
			return x == position.x && y == position.y;
		}

		@Override
		public int hashCode() {
			return Objects.hashCode(x, y);
		}
	}

	class Santa {
		int x = 0, y = 0;
	}

	public void run() throws IOException {
		List<String> lines = ResourceUtil.readAllLines("/2015/day3.input");
		String line = lines.get(0);

		Set<Position> deliveries = new HashSet<>(line.length());

		Santa[] santas = new Santa[2];
		deliveries.add(new Position(0, 0));
		deliveries.add(new Position(0, 0));

		int santaIndex = 0;
		for (int i = 0; i < line.length(); i++) {
			Santa santa;
			santa = santas[santaIndex];
			if (santas[santaIndex] == null) {
				santa = new Santa();
				santas[santaIndex] = santa;
			}

			switch (line.charAt(i)) {
			case '^': santa.y--; break;
			case '<': santa.x--; break;
			case '>': santa.x++; break;
			case 'v': santa.y++; break;
			}
			Position pos = new Position(santa.x, santa.y);
			if (!deliveries.contains(pos)) {
				deliveries.add(pos);
			}
			santaIndex = 1 - santaIndex;
		}

		System.out.println(deliveries.size());
	}

	public static void main(String[] args) throws Exception {
		Day3 worker = new Day3();
		worker.run();
	}

}
