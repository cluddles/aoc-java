package aoc._2015;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * @author Dan Fielding
 */
public class Day1 {

	public void run() throws IOException {
		List<String> lines = Files.readAllLines(Paths.get("/home/danielf/Documents/extra/adventOfCode/2015/day1.input"));
		String line = lines.get(0);
		int floor = 0;
		for (int i = 0; i < line.length(); i++) {
			switch (line.charAt(i)) {
			case '(': floor++; break;
			case ')': floor--; break;
			}
			if (floor == -1) System.out.println(i+1);
		}
		System.out.println(floor);
	}

	public static void main(String[] args) throws Exception {
		Day1 worker = new Day1();
		worker.run();
	}

}
