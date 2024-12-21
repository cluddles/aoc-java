package aoc._2018.common;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.IntPredicate;

public class Program {

	// Op (by name), and argument triplet
	static class Instruction {
		final String opName;
		final int    a;
		final int    b;
		final int    c;

		Instruction(String opName, int a, int b, int c) {
			this.opName = opName;
			this.a      = a;
			this.b      = b;
			this.c      = c;
		}

		@Override public String toString() {
			return opName + " " + a + " " + b + " " + c;
		}
	}

	/** The register used to store the instruction pointer. */
	private final int               ipIndex;
	/** The instructions to execute. */
	private final List<Instruction> instructions;

	private Program(int ipIndex, List<Instruction> instructions) {
		this.ipIndex      = ipIndex;
		this.instructions = instructions;
	}

	public static Program fromInput(List<String> lines) {
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

	public ProcessorState execute(ProcessorState state) {
		return execute(state, null);
	}

	public ProcessorState execute(
			ProcessorState state,
			IntPredicate breakPredicate) {
		Processor processor = new Processor(state);

		int ip = 0;
		while (ip >= 0 && ip < instructions.size()) {
			state.set(ipIndex, ip);
			Instruction instruction = instructions.get(ip);
			processor.eval(instruction.opName, instruction.a, instruction.b, instruction.c);
			// Allow callers to run custom logic, terminate loop early
			if (breakPredicate != null && breakPredicate.test(ip)) {
				return state;
			}
			// Next line
			ip = state.get(ipIndex);
			ip++;
		}
		return state;
	}

}
