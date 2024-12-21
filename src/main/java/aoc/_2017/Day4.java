package aoc._2017;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import shared.ResourceUtil;

/**
 *
 */
public class Day4 {

	private String normalise(String word) {
		char[] chars = word.toCharArray();
		Arrays.sort(chars);
		return new String(chars);
	}

	private boolean isValidPassphrase(String s) {
		Set<String> foundWords = new HashSet<>();
		String[] splitWords = s.split(" ");
		for (String word : splitWords) {
			// Part 2
			word = normalise(word);

			if (foundWords.contains(word)) return false;
			foundWords.add(word);
		}
		return true;
	}

	private long countPassphrases(List<String> input) {
		return input.stream()
				.filter(this::isValidPassphrase)
				.count();
	}

	public static void main(String[] args) throws IOException {
		List<String> input = ResourceUtil.readAllLines("2017/day4.input");
		Day4 day = new Day4();
		System.out.println(day.countPassphrases(input));
	}

}
