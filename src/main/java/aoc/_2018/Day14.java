package aoc._2018;

import java.util.ArrayList;
import java.util.List;

import shared.Test;

public class Day14 {

	private int[] elfPositions;
	private List<Integer> scores;

	Day14() {
		elfPositions = new int[] { 0, 1 };
		scores = new ArrayList<>();
		scores.add(3);
		scores.add(7);
	}

	// Returns the new digits
	private List<Integer> iterate() {
		// Calculate new scores
		int score1 = scores.get(elfPositions[0]);
		int score2 = scores.get(elfPositions[1]);
		int newScore = score1 + score2;
		// 2 digits max?
		List<Integer> digits = new ArrayList<>();
		if (newScore >= 10) {
			digits.add(newScore / 10);
		}
		digits.add(newScore % 10);
		scores.addAll(digits);
		// Move
		elfPositions[0] = (elfPositions[0] + 1 + score1) % scores.size();
		elfPositions[1] = (elfPositions[1] + 1 + score2) % scores.size();
		return digits;
	}

	public String evalPart1(int numDiscard) {
		final int numDigits = 10;
		// Repeat until result is at least discard + digits
		while (scores.size() < numDiscard + numDigits) {
			iterate();
		}
		System.out.println(scores);
		// Build result
		StringBuilder result = new StringBuilder();
		for (int i = 0; i < numDigits; i++) {
			result.append(scores.get(numDiscard + i));
		}
		return result.toString();
	}

	public int evalPart2(String target) {
		int size = scores.size();
		// Track the current answer fragment
		String current = "";
		while (true) {
			List<Integer> digits = iterate();
			for (int digit : digits) {
				current += digit;
				size++;
				// Fragment may contain repetitions, so we need to cope with those
				while (!target.startsWith(current) && !current.isEmpty()) {
					current = current.substring(1);
				}
				if (target.equals(current)) {
					return size - target.length();
				}
			}
		}
	}

	public static void main(String[] args) throws Exception {
		String input = "084601";

		// Part 1
		// Examples
		Test.check(new Day14().evalPart1(9),    "5158916779");
		Test.check(new Day14().evalPart1(5),    "0124515891");
		Test.check(new Day14().evalPart1(18),   "9251071085");
		Test.check(new Day14().evalPart1(2018), "5941429882");
		// Vs Input
		System.out.println(new Day14().evalPart1(Integer.parseInt(input)));

		// Part 2
		// Examples
		Test.check(new Day14().evalPart2("51589"), 9);
		Test.check(new Day14().evalPart2("01245"), 5);
		Test.check(new Day14().evalPart2("92510"), 18);
		Test.check(new Day14().evalPart2("59414"), 2018);
		// Vs Input
		System.out.println(new Day14().evalPart2(input));
		// Failures: 20188251 (too high)
	}

}
