package aoc._2016;

import java.io.IOException;
import java.util.List;

import shared.IntVector2;
import shared.ResourceUtil;

public class Day2 {

	private void parseFile(String filename) throws IOException {
		// Read file into stream, try-with-resources
		IntVector2 pos = new IntVector2(1, 1);
		List<String> lines = ResourceUtil.readAllLines(filename);
		for (String line : lines) {
//			pos = parseThreeByThree(line, pos);
//			System.out.println(pos + " -> " + threeByThreeButtonAt(pos));
			pos = parseDiamond(line, pos);
			System.out.println(pos + " -> " + diamondButtonAt(pos));
		}
	}

	private IntVector2 parseThreeByThree(String line, IntVector2 startPos) {
		int x = startPos.getX();
		int y = startPos.getY();
		for (int i = 0; i < line.length(); i++) {
			char c = line.charAt(i);
			switch (c) {
				case 'U': y--; break;
				case 'D': y++; break;
				case 'L': x--; break;
				case 'R': x++; break;
			}
			x = Math.max(0, Math.min(2, x));
			y = Math.max(0, Math.min(2, y));
		}
		return new IntVector2(x, y);
	}

	private int threeByThreeButtonAt(IntVector2 pos) {
		return (pos.getY() * 3) + pos.getX() + 1;
	}

	private IntVector2 parseDiamond(String line, IntVector2 startPos) {
		int x = startPos.getX();
		int y = startPos.getY();
		for (int i = 0; i < line.length(); i++) {
			int newX = x;
			int newY = y;
			char c = line.charAt(i);
			switch (c) {
				case 'U': newY--; break;
				case 'D': newY++; break;
				case 'L': newX--; break;
				case 'R': newX++; break;
			}
			if (Math.abs(2 - newX) + Math.abs(2 - newY) > 2) {
				newX = x;
				newY = y;
			}
			// Finally apply
			x = newX;
			y = newY;
		}
		return new IntVector2(x, y);
	}

	private char diamondButtonAt(IntVector2 pos) {
		String chars
				= "  1  "
				+ " 234 "
				+ "56789"
				+ " ABC "
				+ "  D  ";
		return chars.charAt(pos.getY() * 5 + pos.getX());
	}

	public static void main(String[] args) throws Exception {
		String filename = "2016/day2.input";

		Day2 worker = new Day2();
		worker.parseFile(filename);
	}

}
