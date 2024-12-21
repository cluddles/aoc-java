package aoc._2017;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;

import shared.ResourceUtil;
import shared.Test;

public class Day6 {

	class Result {
		int steps;
		List<Integer> state;

		public Result(int steps, List<Integer> state) {
			this.steps = steps;
			this.state = state;
		}
	}

	public List<Integer> redistribute(List<Integer> input) {
		List<Integer> result = new ArrayList<>(input);
		// Find biggest
		int max = Integer.MIN_VALUE;
		int maxIndex = -1;
		for (int i = 0; i < result.size(); i++) {
			if (result.get(i) > max) {
				max = result.get(i);
				maxIndex = i;
			}
		}
		// Share the load
		result.set(maxIndex, 0);
		int current = maxIndex;
		while (max > 0) {
			max--;
			current = (current + 1) % result.size();
			result.set(current, result.get(current) + 1);
		}
		return result;
	}

	public Result eval(List<Integer> input) {
		Set<List<Integer>> history = new HashSet<>();
		List<Integer> current = input;
		while (!history.contains(current)) {
			history.add(current);
			current = redistribute(current);
		}
		return new Result(history.size(), current);
	}

	public void examples() {
		Test.check(eval(ImmutableList.of(0, 2, 7, 0)).steps, 5);
		Test.check(eval(ImmutableList.of(2, 4, 1, 2)).steps, 4);
	}

	public static void main(String[] args) throws Exception {
		Day6 day = new Day6();
		day.examples();

		List<String> lines = ResourceUtil.readAllLines("2017/day6.input");
		List<Integer> input = Arrays.stream(lines.get(0).split("\\t"))
				.map(Integer::valueOf)
				.collect(Collectors.toList());

		Result result1 = day.eval(input);
		System.out.println(result1.steps);

		Result result2 = day.eval(result1.state);
		System.out.println(result2.steps);
	}

}
