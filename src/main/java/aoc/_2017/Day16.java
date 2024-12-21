package aoc._2017;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.common.collect.ImmutableList;

import shared.ResourceUtil;
import shared.Test;
import shared.Timer;

public class Day16 {

	public void spin(StringBuilder input, int move) {
		move = move % input.length();
		if (move == 0) return;
		if (move < 0) move = input.length() + move;
		int pivot = input.length() - move;
		input.replace(0, input.length(),
				input.substring(pivot) + input.substring(0, pivot));
	}

	public void swap(StringBuilder input, int p1, int p2) {
		if (p1 == p2) return;
		char c1 = input.charAt(p1);
		input.setCharAt(p1, input.charAt(p2));
		input.setCharAt(p2, c1);
	}

	public void move(StringBuilder input, String move) {
		char op = move.charAt(0);
		if (op == 's') {
			spin(input, Integer.valueOf(move.substring(1)));
		} else {
			String[] split = move.substring(1).split("/");
			if (op == 'x') {
				swap(input, Integer.valueOf(split[0]), Integer.valueOf(split[1]));
			} else {
				swap(input, input.indexOf(split[0]), input.indexOf(split[1]));
			}
		}
	}

	public String dance(String start, List<String> moves) {
		StringBuilder current = new StringBuilder(start);
		for (String move : moves) {
			move(current, move);
		}
		return current.toString();
	}

	public void examples() {
		List<String> input = ImmutableList.of(
				"s1",
				"x3/4",
				"pe/b"
		);
		Test.check(dance("abcde", input), "baedc");
	}

	public static void main(String[] args) throws Exception {
		Day16 day = new Day16();
		day.examples();

		String start = "abcdefghijklmnop";
		List<String> input = Arrays.asList(
				ResourceUtil.readString("2017/day16.input").split(","));

		// Part 1
		Timer.start();
		System.out.println(day.dance(start, input));
		Timer.endMessage();

		// Part 2
		// Running 10000 iterations takes ages, so good luck with a billion -
		// just look for loops, then work out result from the remainder
		Timer.start();
		int numRepetitions = 1_000_000_000;
		List<String> prev = new ArrayList<>();
		int loopSize = -1;
		String current = start;
		for (int i = 0; i < numRepetitions; i++) {
			if (i != 0 && current.equals(start)) {
				loopSize = i;
				break;
			}
			prev.add(current);
			current = day.dance(current, input);
		}

		int offset = numRepetitions % loopSize;
		System.out.println("loopSize " + loopSize + ", offset " + offset);
		System.out.println(prev.get(offset));
		Timer.endMessage();
	}

}
