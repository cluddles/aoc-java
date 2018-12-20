package aoc._2018;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import aoc._2018.common.Processor;
import aoc._2018.common.ProcessorState;
import shared.ResourceUtil;
import shared.Test;

/**
 * @author Dan Fielding
 */
public class Day16 {
/*
--- Day 16: Chronal Classification ---

As you see the Elves defend their hot chocolate successfully, you go back to
falling through time. This is going to become a problem.

If you're ever going to return to your own time, you need to understand how this
device on your wrist works. You have a little while before you reach your next
destination, and with a bit of trial and error, you manage to pull up a
programming manual on the device's tiny screen.

According to the manual, the device has four registers (numbered 0 through 3)
that can be manipulated by instructions containing one of 16 opcodes. The
registers start with the value 0.

Every instruction consists of four values: an opcode, two inputs (named A and
B), and an output (named C), in that order. The opcode specifies the behavior of
the instruction and how the inputs are interpreted. The output, C, is always
treated as a register.

In the opcode descriptions below, if something says "value A", it means to take
the number given as A literally. (This is also called an "immediate" value.) If
something says "register A", it means to use the number given as A to read from
(or write to) the register with that number. So, if the opcode addi adds
register A and value B, storing the result in register C, and the instruction
addi 0 7 3 is encountered, it would add 7 to the value contained by register 0
and store the sum in register 3, never modifying registers 0, 1, or 2 in the
process.

Many opcodes are similar except for how they interpret their arguments.
The opcodes fall into seven general categories:

Addition:

    addr (add register) stores into register C the result of adding register A
        and register B.
    addi (add immediate) stores into register C the result of adding register A
        and value B.

Multiplication:

    mulr (multiply register) stores into register C the result of multiplying
        register A and register B.
    muli (multiply immediate) stores into register C the result of multiplying
        register A and value B.

Bitwise AND:

    banr (bitwise AND register) stores into register C the result of the bitwise
        AND of register A and register B.
    bani (bitwise AND immediate) stores into register C the result of the
        bitwise AND of register A and value B.

Bitwise OR:

    borr (bitwise OR register) stores into register C the result of the bitwise
        OR of register A and register B.
    bori (bitwise OR immediate) stores into register C the result of the bitwise
        OR of register A and value B.

Assignment:

    setr (set register) copies the contents of register A into register C.
        (Input B is ignored.)
    seti (set immediate) stores value A into register C. (Input B is ignored.)

Greater-than testing:

    gtir (greater-than immediate/register) sets register C to 1 if value A is
        greater than register B. Otherwise, register C is set to 0.
    gtri (greater-than register/immediate) sets register C to 1 if register A is
        greater than value B. Otherwise, register C is set to 0.
    gtrr (greater-than register/register) sets register C to 1 if register A is
        greater than register B. Otherwise, register C is set to 0.

Equality testing:

    eqir (equal immediate/register) sets register C to 1 if value A is equal to
        register B. Otherwise, register C is set to 0.
    eqri (equal register/immediate) sets register C to 1 if register A is equal
        to value B. Otherwise, register C is set to 0.
    eqrr (equal register/register) sets register C to 1 if register A is equal
        to register B. Otherwise, register C is set to 0.

Unfortunately, while the manual gives the name of each opcode, it doesn't seem
to indicate the number. However, you can monitor the CPU to see the contents of
the registers before and after instructions are executed to try to work them
out. Each opcode has a number from 0 through 15, but the manual doesn't say
which is which. For example, suppose you capture the following sample:

Before: [3, 2, 1, 1]
9 2 1 2
After:  [3, 2, 2, 1]

This sample shows the effect of the instruction 9 2 1 2 on the registers.
Before the instruction is executed, register 0 has value 3, register 1 has value
2, and registers 2 and 3 have value 1. After the instruction is executed,
register 2's value becomes 2.

The instruction itself, 9 2 1 2, means that opcode 9 was executed with A=2, B=1,
and C=2. Opcode 9 could be any of the 16 opcodes listed above, but only three of
them behave in a way that would cause the result shown in the sample:

    Opcode 9 could be mulr: register 2 (which has a value of 1) times register 1
        (which has a value of 2) produces 2, which matches the value stored in
        the output register, register 2.
    Opcode 9 could be addi: register 2 (which has a value of 1) plus value 1
        produces 2, which matches the value stored in the output register,
        register 2.
    Opcode 9 could be seti: value 2 matches the value stored in the output
        register, register 2; the number given for B is irrelevant.

None of the other opcodes produce the result captured in the sample. Because of
this, the sample above behaves like three opcodes.

You collect many of these samples (the first section of your puzzle input). The
manual also includes a small test program (the second section of your puzzle
input) - you can ignore it for now.

Ignoring the opcode numbers, how many samples in your puzzle input behave like
three or more opcodes?

--- Part Two ---

Using the samples you collected, work out the number of each opcode and execute
the test program (the second section of your puzzle input).

What value is contained in register 0 after executing the test program?

*/

	private static final int NUM_REGISTERS = 4;

	private static final Pattern PATTERN_BEFORE = Pattern.compile(
			"Before:\\s*\\[(\\d+),\\s*(\\d+),\\s*(\\d+),\\s*(\\d+)\\]");
	private static final Pattern PATTERN_INSTRUCTION = Pattern.compile(
			"(\\d+)\\s+(\\d+)\\s+(\\d+)\\s+(\\d+)");
	private static final Pattern PATTERN_AFTER = Pattern.compile(
			"After:\\s*\\[(\\d+),\\s*(\\d+),\\s*(\\d+),\\s*(\\d+)\\]");

	static class Instruction {
		final int opCode;
		final int a;
		final int b;
		final int c;
		Instruction(int opCode, int a, int b, int c) {
			this.opCode = opCode;
			this.a = a;
			this.b = b;
			this.c = c;
		}
	}

	// Input, Instruction -> Output
	static class Scenario {
		final List<Integer> inputValues;
		final Instruction   instruction;
		final List<Integer> outputValues;
		Scenario(
				List<Integer> inputValues,
				Instruction   instruction,
				List<Integer> outputValues) {
			this.inputValues  = inputValues;
			this.instruction  = instruction;
			this.outputValues = outputValues;
		}
	}

	static class Program {
		final List<Instruction> instructions;
		Program(List<Instruction> instructions) {
			this.instructions = instructions;
		}
	}

	static class Input {
		final List<Scenario> scenarios;
		final Program        program;
		Input(List<Scenario> scenarios, Program program) {
			this.scenarios = scenarios;
			this.program = program;
		}
	}

	private ProcessorState state      = new ProcessorState(NUM_REGISTERS);
	private Processor      processor  = new Processor(state);

	// Assign the input register values
	void init(List<Integer> inputValues) {
		state.setFromList(inputValues);
	}
	// Check registers against expected output values
	boolean match(List<Integer> outputValues) {
		return state.asList().equals(outputValues);
	}

	// Which ops turn the input + instruction into output
	Set<String> potentialMatches(
			List<Integer> inputValues,
			Instruction   instruction,
			List<Integer> outputValues) {
		Set<String> matches = new HashSet<>();
		for (Map.Entry<String, Processor.Op> opEntry : processor.getOps().entrySet()) {
			init(inputValues);
			processor.eval(opEntry.getKey(), instruction.a, instruction.b, instruction.c);
			if (match(outputValues)) matches.add(opEntry.getKey());
		}
		return matches;
	}

	List<Integer> parseQuad(Matcher matcher) {
		if (!matcher.matches()) return null;
		return ImmutableList.of(
					Integer.parseInt(matcher.group(1)),
					Integer.parseInt(matcher.group(2)),
					Integer.parseInt(matcher.group(3)),
					Integer.parseInt(matcher.group(4)));
	}
	Instruction parseInstruction(Matcher matcher) {
		List<Integer> instructionValues = parseQuad(matcher);
		if (instructionValues == null) throw new IllegalStateException("No instruction");
		return new Instruction(
						instructionValues.get(0),
						instructionValues.get(1),
						instructionValues.get(2),
						instructionValues.get(3));
	}
	Scenario parseScenario(String line, Iterator<String> it) {
		// Before
		Matcher matcher = PATTERN_BEFORE.matcher(line);
		List<Integer> inputValues = parseQuad(matcher);
		if (inputValues == null) return null;
		// Instruction
		line = it.next();
		matcher = PATTERN_INSTRUCTION.matcher(line);
		Instruction instruction = parseInstruction(matcher);
		// After
		line = it.next();
		matcher = PATTERN_AFTER.matcher(line);
		List<Integer> outputValues = parseQuad(matcher);
		if (outputValues == null) throw new IllegalStateException("No output: " + line);
		// Blank line
		line = it.next();

		return new Scenario(inputValues, instruction, outputValues);
	}
	Program parseProgram(String line, Iterator<String> it) {
		List<Instruction> instructions = new ArrayList<>();
		while (true) {
			Matcher matcher = PATTERN_INSTRUCTION.matcher(line);
			if (!matcher.matches()) break;
			instructions.add(parseInstruction(matcher));
			if (!it.hasNext()) break;
			line = it.next();
		}
		return new Program(instructions);
	}
	Input parseFile(List<String> lines) {
		// Convert input to scenarios and program
		List<Scenario> scenarios = new ArrayList<>();
		Program        program   = null;
		Iterator<String> it = lines.iterator();
		while (it.hasNext()) {
			String line = it.next();
			Scenario scenario = parseScenario(line, it);
			if (scenario != null) {
				scenarios.add(scenario);
			} else {
				program = parseProgram(line, it);
			}
		}
		return new Input(scenarios, program);
	}

	public int evalPart1(List<String> lines) {
		Input input = parseFile(lines);
		// Potential matches per scenario
		return (int) input.scenarios.stream()
				.filter(i -> potentialMatches(
						i.inputValues,
						i.instruction,
						i.outputValues).size() >= 3)
				.count();
	}

	public int evalPart2(List<String> lines) {
		Input input = parseFile(lines);
		// Figure out which opCode maps to which opName
		Map<Integer, String> opCodesToNames = new HashMap<>();
		while (opCodesToNames.size() < processor.getOps().size()) {
			for (Scenario scenario : input.scenarios) {
				Set<String> opNames = potentialMatches(
						scenario.inputValues,
						scenario.instruction,
						scenario.outputValues);
				// Remove any op names that we've already mapped to a code
				opNames.removeAll(opCodesToNames.values());
				if (opNames.size() == 1) {
					String opName = opNames.iterator().next();
					opCodesToNames.put(scenario.instruction.opCode, opName);
				}
			}
		}
		System.out.println(opCodesToNames);
		// Now we can run the program
		for (Instruction instruction : input.program.instructions) {
			// Convert opCode to opName, get Op
			String opName = opCodesToNames.get(instruction.opCode);
			// Evaluate
			processor.eval(opName, instruction.a, instruction.b, instruction.c);
		}
		return state.get(0);
	}

	public static void main(String[] args) throws Exception {
		Day16 solver = new Day16();
		List<String> input = ResourceUtil.readAllLines("2018/day16.input");

		// Part 1
		// Examples
		Test.check(solver.potentialMatches(
				ImmutableList.of(3, 2, 1, 1),
				new Instruction (9, 2, 1, 2),
				ImmutableList.of(3, 2, 2, 1)
		), ImmutableSet.of("mulr", "addi", "seti"));
		// Vs Input
		System.out.println(solver.evalPart1(input));

		// Part 2
		// Vs Input
		System.out.println(solver.evalPart2(input));
	}

}
