package aoc._2015;

import java.io.IOException;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import shared.ResourceUtil;

public class Day6 {

	private static final Pattern RANGE_PATTERN = Pattern.compile("([\\w\\s]*) (\\d*),(\\d*) through (\\d*),(\\d*)");

	private static final int SIZE = 1000;

	private void from(String path) throws IOException {
		List<String> lines = ResourceUtil.readAllLines(path);
		int[][] grid = new int[SIZE][SIZE];
		for (String line : lines) {
			Matcher matcher = RANGE_PATTERN.matcher(line);
			if (matcher.matches()) {
				int fromX = Integer.parseInt(matcher.group(2));
				int fromY = Integer.parseInt(matcher.group(3));
				int toX = Integer.parseInt(matcher.group(4));
				int toY = Integer.parseInt(matcher.group(5));
				Function<Integer, Integer> f = null;
				switch (matcher.group(1)) {
				case "turn on":
					f = (x -> x + 1); break;

				case "turn off":
					f = (x -> Math.max(x-1, 0)); break;

				case "toggle":
					f = (x -> x + 2); break;
				}

				for (int i = fromX; i <= toX; i++) {
					for (int j = fromY; j <= toY; j++) {
						grid[i][j] = f.apply(grid[i][j]);
					}
				}
			}
		}

		int totalBrightness = 0;
		for (int i = 0; i < SIZE; i++) {
			for (int j = 0; j < SIZE; j++) {
				totalBrightness += grid[i][j];
			}
		}

		System.out.println(totalBrightness);
	}

	public static void main(String[] args) throws Exception {
		Day6 worker = new Day6();
		worker.from("2015/day6.input");
	}

}
