package aoc._2018;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.ImmutableList;

import shared.ResourceUtil;
import shared.Test;

public class Day2 {

	// Map of characters to number of times they exist
	public Map<Character, Integer> countOccurrences(String line) {
		Map<Character, Integer> result = new HashMap<>();
		for (char c : line.toCharArray()) {
			result.put(c, result.getOrDefault(c, 0) + 1);
		}
		return result;
	}

	public int checksum(List<String> lines) {
		int twos   = 0;
		int threes = 0;
		for (String line : lines) {
			Map<Character, Integer> count = countOccurrences(line);
			if (count.containsValue(2)) twos++;
			if (count.containsValue(3)) threes++;
		}
		return twos * threes;
	}

	public String findCommon(List<String> lines) {
		// We can brute force this - just try removing each character in turn
		int numChars = lines.get(0).length();
		// 1st->last character
		for (int i = 0; i < numChars; i++) {
			Set<String> seen = new HashSet<>();
			for (String line : lines) {
				// Snip out the character
				String current = line.substring(0, i) + line.substring(i + 1);
				// Have we seen it?
				if (seen.contains(current)) return current;
				seen.add(current);
			}
		}
		throw new IllegalArgumentException("No answer for input");
	}

	public static void main(String[] args) throws Exception {
		Day2 solver = new Day2();
		List<String> lines = ResourceUtil.readAllLines("2018/day2.input");

		// Part 1
		// Examples
		Test.check(solver.checksum(ImmutableList.of(
				"abcdef", "bababc", "abbcde", "abcccd", "aabcdd", "abcdee", "ababab")),
				12);
		// Vs Input
		System.out.println(solver.checksum(lines));

		// Part 2
		// Examples
		Test.check(solver.findCommon(ImmutableList.of(
				"abcde", "fghij", "klmno", "pqrst", "fguij", "axcye", "wvxyz")),
				"fgij");
		// Vs Input
		System.out.println(solver.findCommon(lines));
	}

}
