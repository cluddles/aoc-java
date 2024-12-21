package aoc._2016;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;

import shared.ResourceUtil;

public class Day3 {

	private void parseFile(String filename) throws IOException {
		// Read file into stream, try-with-resources
//		try (Stream<String> stream = Files.lines(Paths.get(filename))) {
//			long validTriangles = stream.filter(this::checkTriangleLine).count();
//			System.out.println("Valid triangle count: " + validTriangles);
//		}

		int validTriangles = 0;
		List<String> lines = ResourceUtil.readAllLines(filename);
		for (int i = 0; i < lines.size(); i += 3) {
			List<Integer> line1 = parseLine(lines.get(i));
			List<Integer> line2 = parseLine(lines.get(i+1));
			List<Integer> line3 = parseLine(lines.get(i+2));
			for (int j = 0; j < 3; j++) {
				if (checkTriangleColumn(line1, line2, line3, j)) validTriangles++;
			}
		}
		System.out.println("Valid triangle count: " + validTriangles);
	}

	private List<Integer> parseLine(String line) {
		return Splitter
				.on(" ")
				.omitEmptyStrings()
				.splitToList(line)
				.stream()
				.map(Integer::parseInt)
				.collect(Collectors.toList());
	}

	private boolean checkTriangleLine(String line) {
		List<Integer> values = parseLine(line);
		return checkTriangleValues(values);
	}

	private boolean checkTriangleColumn(List<Integer> line1, List<Integer> line2, List<Integer> line3, int columnIndex) {
		List<Integer> values = ImmutableList
				.<Integer>builder()
				.add(line1.get(columnIndex))
				.add(line2.get(columnIndex))
				.add(line3.get(columnIndex))
				.build();
		return checkTriangleValues(values);
	}

	private boolean checkTriangleValues(List<Integer> values) {
		for (int i = 0; i < 3; i++) {
			int total = 0;
			for (int j = 0; j < 3; j++) {
				if (i == j) continue;
				total += values.get(j);
			}
			if (total <= values.get(i)) return false;
		}
		return true;
	}

	public static void main(String[] args) throws Exception {
		String filename = "2016/day3.input";

		Day3 worker = new Day3();
		worker.parseFile(filename);
	}

}
