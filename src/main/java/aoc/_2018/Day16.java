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

public class Day16 {

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
