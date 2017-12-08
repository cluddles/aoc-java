package aoc._2017;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;

import shared.ResourceUtil;
import shared.Test;

/**
 * @author Dan Fielding
 */
public class Day8 {

	/*

		--- Day 8: I Heard You Like Registers ---

		You receive a signal directly from the CPU. Because of your recent
		assistance with jump instructions, it would like you to compute the
		result of a series of unusual register instructions.

		Each instruction consists of several parts: the register to modify,
		whether to increase or decrease that register's value, the amount by
		which to increase or decrease it, and a condition. If the condition
		fails, skip the instruction without modifying the register. The
		registers all start at 0. The instructions look like this:

		b inc 5 if a > 1
		a inc 1 if b < 5
		c dec -10 if a >= 1
		c inc -20 if c == 10

		These instructions would be processed as follows:

		- Because a starts at 0, it is not greater than 1, and so b is not
		  modified.
		- a is increased by 1 (to 1) because b is less than 5 (it is 0).
		- c is decreased by -10 (to 10) because a is now greater than or equal
		  to 1 (it is 1).
		- c is increased by -20 (to -10) because c is equal to 10.

		After this process, the largest value in any register is 1.

		You might also encounter <= (less than or equal to) or != (not equal
		to). However, the CPU doesn't have the bandwidth to tell you what all
		the registers are named, and leaves that to you to determine.

		What is the largest value in any register after completing the
		instructions in your puzzle input?

		--- Part Two ---

		To be safe, the CPU also needs to know the highest value held in any
		register during this process so that it can decide how much memory to
		allocate to these operations. For example, in the above instructions,
		the highest value ever held was 10 (in register c after the third
		instruction was evaluated).

    */

	class Instruction {
		private final String register;
		private final int modifier;
		private final String conditionRegister;
		private final String conditionOp;
		private final int conditionValue;
		Instruction(String line) {
			String[] parts = line.split(" ");
			this.register = parts[0];
			int sign = Objects.equal(parts[1], "inc")? 1 : -1;
			this.modifier = sign * Integer.valueOf(parts[2]);
			// parts[3] is "if"
			this.conditionRegister = parts[4];
			this.conditionOp = parts[5];
			this.conditionValue = Integer.valueOf(parts[6]);
		}
	}

	class Result {
		private final int maxAtEnd;
		private final int maxDuring;
		Result(int maxAtEnd, int maxDuring) {
			this.maxAtEnd = maxAtEnd;
			this.maxDuring = maxDuring;
		}
	}

	public Result eval(List<String> lines) {
		Map<String, Integer> registers = new HashMap<>();
		int maxDuring = 0;
		for (String line : lines) {
			Instruction in = new Instruction(line);
			int condRegVal = registers.computeIfAbsent(in.conditionRegister, (key) -> 0);
			boolean pass = false;
			switch (in.conditionOp) {
			case "<":  pass = (condRegVal <  in.conditionValue); break;
			case ">":  pass = (condRegVal >  in.conditionValue); break;
			case "<=": pass = (condRegVal <= in.conditionValue); break;
			case ">=": pass = (condRegVal >= in.conditionValue); break;
			case "==": pass = (condRegVal == in.conditionValue); break;
			case "!=": pass = (condRegVal != in.conditionValue); break;
			}
			//System.out.println(in.conditionRegister + "(" + condRegVal + ")" + in.conditionOp + in.conditionValue + ":" + pass);
			if (pass) {
				int oldVal = registers.computeIfAbsent(in.register, (key) -> 0);
				int newVal = oldVal + in.modifier;
				registers.put(in.register, newVal);
				maxDuring = Math.max(maxDuring, newVal);
				//System.out.println(in.register + "->" + newVal);
			}
		}
		return new Result(Collections.max(registers.values()), maxDuring);
	}

	public void examples() {
		Result result = eval(ImmutableList.of(
				"b inc 5 if a > 1",
				"a inc 1 if b < 5",
				"c dec -10 if a >= 1",
				"c inc -20 if c == 10"
		));
		Test.assertEqual(result.maxAtEnd, 1);
		Test.assertEqual(result.maxDuring, 10);
	}

	public static void main(String[] args) throws Exception {
		Day8 day = new Day8();
		day.examples();

		List<String> lines = ResourceUtil.readAllLines("2017/day8.input");
		Result result = day.eval(lines);
		System.out.println(result.maxAtEnd);
		System.out.println(result.maxDuring);
	}

}
