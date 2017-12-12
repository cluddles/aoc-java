package aoc._2017;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;

import shared.ResourceUtil;
import shared.Test;

/**
 * @author Dan Fielding
 */
public class Day12 {

	/*

	--- Day 12: Digital Plumber ---

	Walking along the memory banks of the stream, you find a small village that
	is experiencing a little confusion: some programs can't communicate with
	each other.

	Programs in this village communicate using a fixed system of pipes. Messages
	are passed between programs using these pipes, but most programs aren't
	connected to each other directly. Instead, programs pass messages between
	each other until the message reaches the intended recipient.

	For some reason, though, some of these messages aren't ever reaching their
	intended recipient, and the programs suspect that some pipes are missing.
	They would like you to investigate.

	You walk through the village and record the ID of each program and the IDs
	with which it can communicate directly (your puzzle input). Each program has
	one or more programs with which it can communicate, and these pipes are
	bidirectional; if 8 says it can communicate with 11, then 11 will say it
	can communicate with 8.

	You need to figure out how many programs are in the group that contains
	program ID 0.

	For example, suppose you go door-to-door like a travelling salesman and
	record the following list:

	0 <-> 2
	1 <-> 1
	2 <-> 0, 3, 4
	3 <-> 2, 4
	4 <-> 2, 3, 6
	5 <-> 6
	6 <-> 4, 5

	In this example, the following programs are in the group that contains
	program ID 0:

	    Program 0 by definition.
	    Program 2, directly connected to program 0.
	    Program 3 via program 2.
	    Program 4 via program 2.
	    Program 5 via programs 6, then 4, then 2.
	    Program 6 via programs 4, then 2.

	Therefore, a total of 6 programs are in this group; all but program 1, which
	has a pipe that connects it to itself.

	How many programs are in the group that contains program ID 0?

	 */

	public void visit(Multimap<Integer, Integer> map, int current, Set<Integer> visited) {
		visited.add(current);
		for (int target : map.get(current)) {
			if (!visited.contains(target)) visit(map, target, visited);
		}
	}

	public Multimap<Integer, Integer> map(List<String> input) {
		Multimap<Integer, Integer> map = HashMultimap.create();
		for (String line : input) {
			String[] parts = line.split(" ", 3);
			int node = Integer.valueOf(parts[0]);
			List<Integer> targets = Arrays.stream(parts[2].split(","))
					.map(s -> Integer.valueOf(s.trim()))
					.collect(Collectors.toList());
			map.putAll(node, targets);
		}
		return map;
	}

	public int part1(List<String> input) {
		Set<Integer> visited = new HashSet<>();
		visit(map(input), 0, visited);
		return visited.size();
	}

	public int part2(List<String> input) {
		Multimap<Integer, Integer> map = map(input);
		Set<Integer> visited = new HashSet<>();
		Set<Integer> unvisited = new HashSet<>(map.keySet());
		int groups = 0;
		while (!unvisited.isEmpty()) {
			visit(map, unvisited.iterator().next(), visited);
			unvisited.removeAll(visited);
			groups++;
		}
		return groups;
	}

	public void examples() {
		List<String> input = ImmutableList.of(
				"0 <-> 2",
				"1 <-> 1",
				"2 <-> 0, 3, 4",
				"3 <-> 2, 4",
				"4 <-> 2, 3, 6",
				"5 <-> 6",
				"6 <-> 4, 5"
		);
		Test.assertEqual(part1(input), 6);
		Test.assertEqual(part2(input), 2);
	}

	public static void main(String[] args) throws Exception {
		Day12 day = new Day12();
		day.examples();

		List<String> input = ResourceUtil.readAllLines("2017/day12.input");
		System.out.println(day.part1(input));
		System.out.println(day.part2(input));
	}

}
