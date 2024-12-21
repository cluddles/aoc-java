package aoc._2018;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.IntPredicate;

import aoc._2018.common.ProcessorState;
import aoc._2018.common.Program;
import shared.ResourceUtil;

public class Day21 {

	Collection<Integer> eval(List<String> lines) {
		Program        program        = Program.fromInput(lines);
		ProcessorState state          = new ProcessorState(6);
		Set<Integer>   answers        = new LinkedHashSet<>();
		IntPredicate   breakPredicate = ip -> {
			// Only check line 29
			if (ip != 29) return false;
			// reg4 == reg0 EXIT
			// So the first value of reg4 here is the answer for part 1
			// And the last UNIQUE value of reg4 is the answer for part 2
			if (answers.size() % 100 == 0) System.out.println("..." + answers.size());
			int answer = state.get(4);
			if (answers.contains(answer)) return true;
			answers.add(answer);
			return false;
		};
		program.execute(state, breakPredicate);

		// Part 1 is the first
		// Part 2 is the last (since we don't add the repetition now)
		return answers;
	}

	public static void main(String[] args) throws Exception {
		List<String> input = ResourceUtil.readAllLines("2018/day21.input");

		// Part 1 + Part 2 all-in-one
		Collection<Integer> answer      = new Day21().eval(input);
		Integer[]           answerArray = answer.toArray(new Integer[0]);
		// Part 1 answer
		System.out.println("Part1: " + answerArray[0]);
		// Part 2 answer
		System.out.println("Part2: " + answerArray[answerArray.length - 1]);
		// Failures: 1211266 (too low) - picked first repetition rather than the last unique, whoops
	}

}
