package aoc._2017;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;

import shared.ResourceUtil;
import shared.Test;

public class Day8 {

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
		Test.check(result.maxAtEnd, 1);
		Test.check(result.maxDuring, 10);
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
