package aoc._2018;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.IntPredicate;

import aoc._2018.common.ProcessorState;
import aoc._2018.common.Program;
import shared.ResourceUtil;

/**
 * @author Dan Fielding
 */
public class Day21 {
/*
--- Day 21: Chronal Conversion ---

You should have been watching where you were going, because as you wander the
new North Pole base, you trip and fall into a very deep hole!

Just kidding. You're falling through time again.

If you keep up your current pace, you should have resolved all of the temporal
anomalies by the next time the device activates. Since you have very little
interest in browsing history in 500-year increments for the rest of your life,
you need to find a way to get back to your present time.

After a little research, you discover two important facts about the behavior of
the device:

First, you discover that the device is hard-wired to always send you back in
time in 500-year increments. Changing this is probably not feasible.

Second, you discover the activation system (your puzzle input) for the time
travel module. Currently, it appears to run forever without halting.

If you can cause the activation system to halt at a specific moment, maybe you
can make the device send you so far back in time that you cause an integer
underflow in time itself and wrap around back to your current time!

The device executes the program as specified in manual section one (Day 16) and
manual section two (Day 19).

Your goal is to figure out how the program works and cause it to halt. You can
only control register 0; every other register begins at 0 as usual.

Because time travel is a dangerous activity, the activation system begins with a
few instructions which verify that bitwise AND (via bani) does a numeric
operation and not an operation as if the inputs were interpreted as strings.
If the test fails, it enters an infinite loop re-running the test instead of
allowing the program to execute normally. If the test passes, the program
continues, and assumes that all other bitwise operations (banr, bori, and borr)
also interpret their inputs as numbers. (Clearly, the Elves who wrote this
system were worried that someone might introduce a bug while trying to emulate
this system with a scripting language.)

What is the lowest non-negative integer value for register 0 that causes the
program to halt after executing the fewest instructions? (Executing the same
instruction multiple times counts as multiple instructions executed.)

--- Part Two ---

In order to determine the timing window for your underflow exploit, you also
need an upper bound:

What is the lowest non-negative integer value for register 0 that causes the
program to halt after executing the most instructions? (The program must
actually halt; running forever does not count as halting.)

 */


/*

Annotated input - not really helpful, the only important bit is instruction 29.

#ip 3
00  seti 123 0 4        -- reg4 = 123
01  bani 4 456 4        -- reg4 = reg4 & 456
02  eqri 4 72 4         -- .
03  addr 4 3 3          -- .
04  seti 0 0 3          -- if reg4 != 72 goto 1

05  seti 0 6 4          -- reg4 = 0
06  bori 4 65536 5      -- reg5 = reg4 & 65536
07  seti 1855046 9 4    -- reg4 = 1855046
08  bani 5 255 2        -- reg2 = reg5 & 255
09  addr 4 2 4          -- .
10  bani 4 16777215 4   -- .
11  muli 4 65899 4      -- .
12  bani 4 16777215 4   -- reg4 = (((reg4 + reg2) & 16777215) * 65899) & 16777215
13  gtir 256 5 2        -- .
14  addr 2 3 3          -- if reg5 < 256 goto 28
15  addi 3 1 3          -- .
16  seti 27 0 3         -- .
17  seti 0 9 2          -- reg2 = 0
18  addi 2 1 1          -- .
19  muli 1 256 1        -- reg1 = (reg2 + 1) * 256
20  gtrr 1 5 1          -- .
21  addr 1 3 3          -- if reg1 > reg5   reg5 = 2; goto 8
22  addi 3 1 3          -- .
23  seti 25 5 3         -- .
24  addi 2 1 2          -- reg2 = reg2 + 1
25  seti 17 0 3         -- goto 18
26  setr 2 7 5          -- .
27  seti 7 9 3          -- .
28  eqrr 4 0 2          -- .

29  addr 2 3 3          -- if reg4 == reg0 EXIT
30  seti 5 3 3          -- goto 6

 */

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
