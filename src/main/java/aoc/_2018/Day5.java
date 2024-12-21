package aoc._2018;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Set;

import shared.ResourceUtil;
import shared.Test;

public class Day5 {

	public int evalPart1(String input) {
		LinkedList<Character> buffer = new LinkedList<>();
		for (Character c : input.toCharArray()) buffer.add(c);
		ListIterator<Character> li = buffer.listIterator();
		char left = li.next();
		while (li.hasNext()) {
			char right = li.next();
			if (left != right && Character.toUpperCase(left) == Character.toUpperCase(right)) {
				// Remove right
				li.remove();
				// Step left and remove
				li.previous();
				li.remove();
				// Try to step left again if possible, otherwise step right
				if (li.hasPrevious()) {
					left = li.previous();
				} else if (li.hasNext()) {
					left = li.next();
				}
			} else {
				// Step along, shift right char to left
				left = right;
			}
		}
		return buffer.size();
	}

	public int evalPart2(String input) {
		// Unique characters present in input
		Set<Character> chars = new HashSet<>();
		for (Character c : input.toCharArray()) chars.add(Character.toUpperCase(c));
		// Dumb approach - try removing each in turn
		int shortest = Integer.MAX_VALUE;
		for (Character c : chars) {
			// Regex for lower or upper case
			int length = evalPart1(input.replaceAll("[" + c + Character.toLowerCase(c) + "]", ""));
			if (length < shortest) shortest = length;
		}
		return shortest;
	}

	public static void main(String[] args) throws Exception {
		Day5 solver = new Day5();
		String input = ResourceUtil.readString("2018/day5.input");

		// Part 1
		// Examples
		String example = "dabAcCaCBAcCcaDA";
		Test.check(solver.evalPart1(example), 10);
		// Vs Input
		System.out.println(solver.evalPart1(input));

		// Part 2
		// Examples
		Test.check(solver.evalPart2(example), 4);
		// Vs Input
		System.out.println(solver.evalPart2(input));
	}

}
