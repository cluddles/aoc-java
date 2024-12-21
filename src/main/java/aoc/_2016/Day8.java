package aoc._2016;

import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import shared.ResourceUtil;

public class Day8 {

	private static final Pattern PATTERN_RECT
			= Pattern.compile("rect (\\d*)x(\\d*)");
	private static final Pattern PATTERN_ROTATE_X
			= Pattern.compile("rotate row y=(\\d*) by (\\d*)");
	private static final Pattern PATTERN_ROTATE_Y
			= Pattern.compile("rotate column x=(\\d*) by (\\d*)");

	class Display {
		final int width, height;
		final boolean[][] pixels;

		Display(int width, int height) {
			this.width = width;
			this.height = height;
			pixels = new boolean[width][height];
		}

		private void rect(int x, int y) {
			for (int i = 0; i < x; i++) {
				for (int j = 0; j < y; j++) {
					pixels[i][j] = true;
				}
			}
		}

		private void shiftRow(int row, int dist) {
			for (int z = 0; z < dist; z++) {
				boolean temp = pixels[width-1][row];
				for (int i = width - 1; i > 0; i--) {
					pixels[i][row] = pixels[i-1][row];
				}
				pixels[0][row] = temp;
			}
		}

		private void shiftCol(int col, int dist) {
			for (int z = 0; z < dist; z++) {
				boolean temp = pixels[col][height-1];
				for (int j = height - 1; j > 0; j--) {
					pixels[col][j] = pixels[col][j-1];
				}
				pixels[col][0] = temp;
			}
		}

		private int countPixels(Display display) {
			int count = 0;
			for (int i = 0; i < width; i++) {
				for (int j = 0; j < height; j++) {
					if (pixels[i][j]) count++;
				}
			}
			return count;
		}

		@Override
		public String toString() {
			StringBuilder result = new StringBuilder();
			for (int j = 0; j < height; j++) {
				for (int i = 0; i < width; i++) {
					result.append(pixels[i][j]? "#" : ".");
				}
				result.append("\n");
			}
			return result.toString();
		}

	}

	private void parseFile(String filename) throws IOException {
		List<String> lines = ResourceUtil.readAllLines(filename);

		Display display = new Display(50, 6);
		for (String line : lines) {
			System.out.println(line);
			processLine(display, line);
			System.out.println(display);
		}

		System.out.println(display.countPixels(display));
	}

	private void processLine(Display display, String line) {
		Matcher m = PATTERN_RECT.matcher(line);
		if (m.matches()) {
			display.rect(
					Integer.parseInt(m.group(1)),
					Integer.parseInt(m.group(2)));
			return;
		}

		m = PATTERN_ROTATE_X.matcher(line);
		if (m.matches()) {
			display.shiftRow(
					Integer.parseInt(m.group(1)),
					Integer.parseInt(m.group(2)));
		}

		m = PATTERN_ROTATE_Y.matcher(line);
		if (m.matches()) {
			display.shiftCol(
					Integer.parseInt(m.group(1)),
					Integer.parseInt(m.group(2)));
		}
	}

	public static void main(String[] args) throws Exception {
		String filename = "2016/day8.input";

		Day8 worker = new Day8();
		worker.parseFile(filename);
	}

}
