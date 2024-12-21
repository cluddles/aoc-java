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

public class Day12 {

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
		Test.check(part1(input), 6);
		Test.check(part2(input), 2);
	}

	public static void main(String[] args) throws Exception {
		Day12 day = new Day12();
		day.examples();

		List<String> input = ResourceUtil.readAllLines("2017/day12.input");
		System.out.println(day.part1(input));
		System.out.println(day.part2(input));
	}

}
