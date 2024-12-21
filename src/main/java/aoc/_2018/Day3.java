package aoc._2018;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.collect.ImmutableList;

import shared.IntVector2;
import shared.Rect;
import shared.ResourceUtil;
import shared.Test;

public class Day3 {

	private static final Pattern INPUT_LINE_PATTERN =
			Pattern.compile("#(?<id>\\d+) " +
					"@ (?<x>\\d+),(?<y>\\d+)" +
					": (?<w>\\d+)x(?<h>\\d+)");

	class Claim {
		final int id;
		final Rect area;

		Claim(int id, Rect area) {
			this.id = id;
			this.area = area;
		}
	}

	public Claim parseLine(String line) {
		Matcher matcher = INPUT_LINE_PATTERN.matcher(line);
		if (matcher.matches()) {
			return new Claim(
					Integer.valueOf(matcher.group("id")),
					new Rect(
							new IntVector2(
									Integer.valueOf(matcher.group("x")),
									Integer.valueOf(matcher.group("y"))),
							new IntVector2(
									Integer.valueOf(matcher.group("w")),
									Integer.valueOf(matcher.group("h")))));
		}
		throw new IllegalArgumentException("Could not parse line: " + line);
	}

	public int eval(
			List<String>           lines,
			Map<IntVector2, Claim> claims,
			Set<IntVector2>        conflicts) {
		for (String line : lines) {
			Claim   claim = parseLine(line);
			Rect    rect  = claim.area;
			// Is this claim conflicting only with itself?
			boolean clean = true;
			for (int i = 0; i < rect.getSize().getX(); i++) {
				for (int j = 0; j < rect.getSize().getY(); j++) {
					IntVector2 point = new IntVector2(
							rect.getStart().getX() + i,
							rect.getStart().getY() + j);
					Claim other = claims.get(point);
					if (other == null) {
						clean = false;
					} else if (other.id != claim.id) {
						conflicts.add(point);
						clean = false;
					}
					claims.put(point, claim);
				}
			}
			// For part 2
			if (clean) return claim.id;
		}
		return -1;
	}

	public int evalPart1(List<String> lines) {
		Map<IntVector2, Claim> claims    = new HashMap<>();
		Set<IntVector2>        conflicts = new HashSet<>();
		// 1 pass, check conflicts
		eval(lines, claims, conflicts);
		return conflicts.size();
	}

	public int evalPart2(List<String> lines) {
		Map<IntVector2, Claim> claims    = new HashMap<>();
		Set<IntVector2>        conflicts = new HashSet<>();
		// 2 passes, result of the second
		eval(lines, claims, conflicts);
		return eval(lines, claims, conflicts);
	}

	public static void main(String[] args) throws Exception {
		Day3 solver = new Day3();
		List<String> lines = ResourceUtil.readAllLines("2018/day3.input");

		// Part 1
		// Examples
		Test.check(
				solver.evalPart1(ImmutableList.of(
						"#1 @ 1,3: 4x4",
						"#2 @ 3,1: 4x4",
						"#3 @ 5,5: 2x2")),
				4);
		// Vs Input
		System.out.println(solver.evalPart1(lines));

		// Part 2
		// Examples
		Test.check(
				solver.evalPart2(ImmutableList.of(
						"#1 @ 1,3: 4x4",
						"#2 @ 3,1: 4x4",
						"#3 @ 5,5: 2x2")),
				3);
		// Vs Input
		System.out.println(solver.evalPart2(lines));
	}

}
