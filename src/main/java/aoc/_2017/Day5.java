package aoc._2017;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;

import shared.ResourceUtil;
import shared.Test;

public class Day5 {

	public int modifierPart1(int in) {
		return in + 1;
	}

	public int modifierPart2(int in) {
		if (in >= 3) return in-1;
		return in+1;
	}

	public int eval(List<String> source, Function<Integer, Integer> modifier) {
		// Convert to arraylist of integers
		List<Integer> input = source.stream()
				.map(Integer::valueOf)
				.collect(Collectors.toCollection(ArrayList::new));

		int pos = 0;
		int steps = 0;
		while (pos < input.size()) {
			int posVal = input.get(pos);
			int newPos = pos + posVal;
			input.set(pos, modifier.apply(posVal));
			pos = newPos;
			steps++;
		}
		return steps;
	}

	public void examples() {
		List<String> example1 = ImmutableList.of("0", "3", "0", "1", "-3");
		Test.check(eval(example1, this::modifierPart1), 5);
		Test.check(eval(example1, this::modifierPart2), 10);
	}

	public static void main(String[] args) throws Exception {
		Day5 day = new Day5();
		day.examples();

		List<String> input = ResourceUtil.readAllLines("2017/day5.input");
		System.out.println(day.eval(input, day::modifierPart1));
		System.out.println(day.eval(input, day::modifierPart2));
	}

}
