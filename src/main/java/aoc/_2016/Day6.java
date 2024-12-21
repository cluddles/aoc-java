package aoc._2016;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import shared.MapSort;
import shared.ResourceUtil;

public class Day6 {

	private void parseFile(String filename) throws IOException {
		List<String> lines = ResourceUtil.readAllLines(filename);
		// Map of frequencies per column
		List<Map<Character, Integer>> freqs = new ArrayList<>();
		for (int i = 0; i < 8; i++){
			freqs.add(new HashMap<>());
		}
		// Work out for each column for each line
		for (String line : lines) {
			for (int i = 0; i < line.length(); i++) {
				char c = line.charAt(i);
				Map<Character, Integer> map = freqs.get(i);
				if (map.containsKey(c)) {
					map.put(c, map.get(c) + 1);
				} else {
					map.put(c, 1);
				}
			}
		}
		// Poop pants
		StringBuilder result = new StringBuilder();
		for (int i = 0; i < freqs.size(); i++) {
			Map<Character, Integer> sorted = MapSort.sortByValueAsc(freqs.get(i));
			for (Map.Entry<Character, Integer> entry : sorted.entrySet()) {
				result.append(entry.getKey());
				break;
			}
		}
		System.out.println(result.toString());
	}

	public static void main(String[] args) throws Exception {
		String filename = "2016/day6.input";

		Day6 worker = new Day6();
		worker.parseFile(filename);
	}

}
