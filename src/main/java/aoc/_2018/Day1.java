package aoc._2018;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.common.collect.ImmutableList;

import shared.ResourceUtil;
import shared.Test;

public class Day1 {

	public int evalPart1(List<String> lines) {
		// Easier to just paste input into Excel...
		return lines.stream().mapToInt(Integer::valueOf).sum();
	}

	public int evalPart2(List<String> lines) {
		// Remember freqs we've seen (0 is seen by default)
		Set<Integer> seenFreqs = new HashSet<>();
		seenFreqs.add(0);
		int currentFreq = 0;
		// May need to iterate over list multiple times
		while (true) {
			for (String line : lines) {
				currentFreq += Integer.valueOf(line);
				if (seenFreqs.contains(currentFreq)) return currentFreq;
				seenFreqs.add(currentFreq);
			}
		}
	}

	static List<String> toInput(String val) {
		return ImmutableList.copyOf(val.split(", "));
	}

	public static void main(String[] args) throws Exception {
		Day1 solver = new Day1();
		List<String> lines = ResourceUtil.readAllLines("2018/day1.input");

		// Part 1
		// Examples
		Test.check(solver.evalPart1(toInput("+1, -2, +3, +1")), 3);
		Test.check(solver.evalPart1(toInput("+1, +1, +1")), 3);
		Test.check(solver.evalPart1(toInput("+1, +1, -2")), 0);
		Test.check(solver.evalPart1(toInput("-1, -2, -3")), -6);
		// Vs Input
		System.out.println(solver.evalPart1(lines));

		// Part 2
		// Examples
		Test.check(solver.evalPart2(toInput("+1, -2, +3, +1")), 2);
		Test.check(solver.evalPart2(toInput("+1, -1")), 0);
		Test.check(solver.evalPart2(toInput("+3, +3, +4, -2, -4")), 10);
		Test.check(solver.evalPart2(toInput("-6, +3, +8, +5, -6")), 5);
		Test.check(solver.evalPart2(toInput("+7, +7, -2, -7, -4")), 14);
		// Vs Input
		System.out.println(solver.evalPart2(lines));
	}

}
