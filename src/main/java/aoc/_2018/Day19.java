package aoc._2018;

import java.util.List;

import aoc._2018.common.ProcessorState;
import aoc._2018.common.Program;
import shared.ResourceUtil;

public class Day19 {

	private static final int NUM_REGISTERS = 6;

	public ProcessorState eval(List<String> lines) {
		return eval(lines, new ProcessorState(NUM_REGISTERS));
	}
	public ProcessorState eval(List<String> lines, ProcessorState state) {
		return Program.fromInput(lines).execute(state);
	}

	public int evalPart1(List<String> lines) {
		return eval(lines).get(0);
	}
	public int evalPart2(List<String> lines) {
		ProcessorState state = new ProcessorState(NUM_REGISTERS);
		state.set(0, 1);
		return eval(lines, state).get(0);
	}

/*
Musing on part 2, since it takes forever.

Remember that jumps + 1 immediately after - thanks IP

Looks like reg3 is some big number (10551374) that we're trying to find factors of
reg0 ends up being the sum of these factors
reg5 ticks up to reg3
reg4 ticks up once reg5 > reg3 (and loops back round to 0)
the whole loop exits when reg4 > reg3


0	addi 2 16 2   ---   jump 16
1	seti 1 0 4    --- 1 -> reg4                                                      --- reg4 = 1

2	seti 1 5 5    --- 1 -> reg5                                                      --- { reg5 = 1

3	mulr 4 5 1    --- reg4 * reg5   -> reg1                                          ---   { reg1 = reg4 * reg5
4	eqrr 1 3 1    --- reg1 == reg3? -> reg1                                          ---     .
5	addr 1 2 2    --- reg1 + reg2   -> reg2 (skips next line when reg1 == reg3)      ---     .
6	addi 2 1 2    --- reg2 + 1      -> reg2 (skips next line)                        ---     .
7	addr 4 0 0    --- reg4 + reg0   -> reg0 (when reg1 == reg3, reg0 += reg4)        ---     if (reg1 == reg3) reg0 += reg4
8	addi 5 1 5    --- reg5 + 1      -> reg5                                          ---     reg5 += 1
9	gtrr 5 3 1    --- reg5 > reg3?  -> reg1                                          ---     .
10	addr 2 1 2    --- reg2 + reg1   -> reg2 (skips next line when reg5 > reg3)       ---     .
11	seti 2 6 2    ---   jump 2                                                       ---   } if (reg5 <= reg3) goto 3

12	addi 4 1 4    --- reg4 + 1      -> reg4                                          ---   reg4 += 1
13	gtrr 4 3 1    --- reg4 > reg3?  -> reg1                                          ---   .
14	addr 1 2 2    --- reg1 + reg2   -> reg2 (skips next line when reg4 > reg3)       ---   .
15	seti 1 7 2    ---   jump 1                                                       --- } if (reg4 <= reg3) goto 2

16	mulr 2 2 2    ---   jump 256 (reg2 * reg2 -> reg2)                               --- EXIT

17	addi 3 2 3    --- reg3 + 2      -> reg3                                          ---   reg3 += 2
18	mulr 3 3 3    --- reg3 * reg3   -> reg3                                          ---   .
19	mulr 2 3 3    --- reg2 * reg3   -> reg3 (this is reg3 * 19 -> reg3)              ---   .
20	muli 3 11 3   --- reg3 * 11     -> reg3                                          ---   reg3 = reg3 * reg3 * 19 * 11
21	addi 1 6 1    --- reg1 + 16     -> reg1                                          ---   .
22	mulr 1 2 1    --- reg1 * reg2   -> reg1 (this is reg1 * 22 -> reg1)              ---   .
23	addi 1 6 1    --- reg1 + 6      -> reg1                                          ---   reg1 = (reg1 + 16) * 22 + 6
24	addr 3 1 3    --- reg3 + reg1   -> reg3                                          ---   reg3 += reg1
25	addr 2 0 2    --- reg2 + reg0   -> reg2                                          ---
26	seti 0 3 2    ---   jump 0
27	setr 2 3 1    --- reg2 -> reg1          (this is 27 -> reg1)                     ---   .
28	mulr 1 2 1    --- reg1 * reg2 -> reg1   (this is reg1 * 28 -> reg1)              ---   .
29	addr 2 1 1    --- reg2 + reg1 -> reg1   (this is reg1 + 29 -> reg1)              ---   .
30	mulr 2 1 1    --- reg2 * reg1 -> reg1   (this is reg1 * 30 -> reg1)              ---   .
31	muli 1 14 1   --- reg1 * 14 -> reg1                                              ---   .
32	mulr 1 2 1    --- reg1 * reg2 -> reg1   (this is reg1 * 32 -> reg1)              ---   reg1 = ((27 * 28) + 29) * 30 * 14 * 32 = 10550400
33	addr 3 1 3    --- reg3 + reg1 -> reg3                                            ---   reg3 += reg1
34	seti 0 9 0    --- 0 -> reg0                                                      ---   reg0 = 0
35	seti 0 5 2    ---   jump 0                                                       --- goto 1

*/

	public long findFactors(int num) {
		long result = 0L;
		for (int i = 1; i <= num; i++) {
			int v = (num / i);
			if (v * i == num) {
				System.out.println(i + " * " + v + " = " + num);
				result += v;
			}
		}
		return result;
	}

	public static void main(String[] args) throws Exception {
//		List<String> example = ResourceUtil.readAllLines("2018/day19.example");
//		List<String> input   = ResourceUtil.readAllLines("2018/day19.input");

		// Part 1
		// Example
//		Test.check(new Day19().eval(example).asList(), ImmutableList.of(6, 5, 6, 0, 0, 9));
		// Vs Input
//		System.out.println(new Day19().evalPart1(input));

		// Part 2
		// Vs Input
		//System.out.println(new Day19().evalPart2(input));
		System.out.println(new Day19().findFactors(10551374));
		// Failures: 15864119 (too low) -- I did <num instead of <=num (1 off!)
	}
}
