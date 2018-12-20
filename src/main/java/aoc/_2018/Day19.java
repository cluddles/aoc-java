package aoc._2018;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import aoc._2018.common.Processor;
import aoc._2018.common.ProcessorState;
import shared.ResourceUtil;

/**
 * @author Dan Fielding
 */
public class Day19 {
/*
--- Day 19: Go With The Flow ---

(See Day 16)

With the Elves well on their way constructing the North Pole base, you turn your
attention back to understanding the inner workings of programming the device.

You can't help but notice that the device's opcodes don't contain any flow
control like jump instructions. The device's manual goes on to explain:

"In programs where flow control is required, the instruction pointer can be
bound to a register so that it can be manipulated directly. This way, setr/seti
can function as absolute jumps, addr/addi can function as relative jumps, and
other opcodes can cause truly fascinating effects."

This mechanism is achieved through a declaration like #ip 1, which would modify
register 1 so that accesses to it let the program indirectly access the
instruction pointer itself. To compensate for this kind of binding, there are
now six registers (numbered 0 through 5); the five not bound to the instruction
pointer behave as normal. Otherwise, the same rules apply as the last time you
worked with this device.

When the instruction pointer is bound to a register, its value is written to
that register just before each instruction is executed, and the value of that
register is written back to the instruction pointer immediately after each
instruction finishes execution. Afterward, move to the next instruction by
adding one to the instruction pointer, even if the value in the instruction
pointer was just updated by an instruction. (Because of this, instructions must
effectively set the instruction pointer to the instruction before the one they
want executed next.)

The instruction pointer is 0 during the first instruction, 1 during the second,
and so on. If the instruction pointer ever causes the device to attempt to load
an instruction outside the instructions defined in the program, the program
instead immediately halts. The instruction pointer starts at 0.

It turns out that this new information is already proving useful: the CPU in the
device is not very powerful, and a background process is occupying most of its
time. You dump the background process' declarations and instructions to a file
(your puzzle input), making sure to use the names of the opcodes rather than the
numbers.

For example, suppose you have the following program:

#ip 0
seti 5 0 1
seti 6 0 2
addi 0 1 0
addr 1 2 3
setr 1 0 0
seti 8 0 4
seti 9 0 5

When executed, the following instructions are executed.
Each line contains the value of the instruction pointer at the time the
instruction started, the values of the six registers before executing the
instructions (in square brackets), the instruction itself, and the values of the
six registers after executing the instruction (also in square brackets).

ip=0 [0, 0, 0, 0, 0, 0] seti 5 0 1 [0, 5, 0, 0, 0, 0]
ip=1 [1, 5, 0, 0, 0, 0] seti 6 0 2 [1, 5, 6, 0, 0, 0]
ip=2 [2, 5, 6, 0, 0, 0] addi 0 1 0 [3, 5, 6, 0, 0, 0]
ip=4 [4, 5, 6, 0, 0, 0] setr 1 0 0 [5, 5, 6, 0, 0, 0]
ip=6 [6, 5, 6, 0, 0, 0] seti 9 0 5 [6, 5, 6, 0, 0, 9]

In detail, when running this program, the following events occur:

    The first line (#ip 0) indicates that the instruction pointer should be
        bound to register 0 in this program. This is not an instruction, and so
        the value of the instruction pointer does not change during the
        processing of this line.
    The instruction pointer contains 0, and so the first instruction is executed
        (seti 5 0 1). It updates register 0 to the current instruction pointer
        value (0), sets register 1 to 5, sets the instruction pointer to the
        value of register 0 (which has no effect, as the instruction did not
        modify register 0), and then adds one to the instruction pointer.
    The instruction pointer contains 1, and so the second instruction,
        seti 6 0 2, is executed. This is very similar to the instruction before
        it: 6 is stored in register 2, and the instruction pointer is left with
        the value 2.
    The instruction pointer is 2, which points at the instruction addi 0 1 0.
        This is like a relative jump: the value of the instruction pointer, 2,
        is loaded into register 0. Then, addi finds the result of adding the
        value in register 0 and the value 1, storing the result, 3, back in
        register 0. Register 0 is then copied back to the instruction pointer,
        which will cause it to end up 1 larger than it would have otherwise and
        skip the next instruction (addr 1 2 3) entirely. Finally, 1 is added to
        the instruction pointer.
    The instruction pointer is 4, so the instruction setr 1 0 0 is run. This is
        like an absolute jump: it copies the value contained in register 1, 5,
        into register 0, which causes it to end up in the instruction pointer.
        The instruction pointer is then incremented, leaving it at 6.
    The instruction pointer is 6, so the instruction seti 9 0 5 stores 9 into
        register 5. The instruction pointer is incremented, causing it to point
        outside the program, and so the program ends.

What value is left in register 0 when the background process halts?

--- Part Two ---

A new background process immediately spins up in its place. It appears
identical, but on closer inspection, you notice that this time, register 0
started with the value 1.

What value is left in register 0 when this new background process halts?
 */

	private static final int NUM_REGISTERS = 6;

	static class Instruction {
		final String opName;
		final int a;
		final int b;
		final int c;
		Instruction(String opName, int a, int b, int c) {
			this.opName = opName;
			this.a = a;
			this.b = b;
			this.c = c;
		}
		@Override public String toString() {
			return opName + " " + a + " " + b + " " + c;
		}
	}

	static class Program {
		final int ipIndex;
		final List<Instruction> instructions;
		Program(int ipIndex, List<Instruction> instructions) {
			this.ipIndex = ipIndex;
			this.instructions = instructions;
		}
	}

	Program parseProgram(List<String> lines) {
		Iterator<String> it = lines.iterator();
		// Of the form "#ip X"
		int ip = Integer.parseInt(it.next().split(" ")[1]);
		List<Instruction> instructions = new ArrayList<>();
		while (it.hasNext()) {
			String line = it.next();
			String[] split = line.split(" ");
			instructions.add(new Instruction(
					split[0],
					Integer.parseInt(split[1]),
					Integer.parseInt(split[2]),
					Integer.parseInt(split[3])
			));
		}
		return new Program(ip, instructions);
	}

	ProcessorState eval(List<String> lines) {
		return eval(lines, new ProcessorState(NUM_REGISTERS));
	}
	ProcessorState eval(List<String> lines, ProcessorState state) {
		Program   program   = parseProgram(lines);
		Processor processor = new Processor(state);

		int ip = 0;
		while (ip >= 0 && ip < program.instructions.size()) {
			state.set(program.ipIndex, ip);
			Instruction instruction = program.instructions.get(ip);
			processor.eval(instruction.opName, instruction.a, instruction.b, instruction.c);
			ip = state.get(program.ipIndex);
			ip++;
		}
		return state;
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
		List<String> example = ResourceUtil.readAllLines("2018/day19.example");
		List<String> input   = ResourceUtil.readAllLines("2018/day19.input");

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
