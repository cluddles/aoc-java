package aoc._2018.common;

import java.util.ArrayList;
import java.util.List;

public class ProcessorState {

	private final int numRegisters;
	private int[]     registers;

	public ProcessorState(int numRegisters) {
		this.numRegisters = numRegisters;
		this.registers    = new int[numRegisters];
	}

	public int get(int regIndex) {
		return registers[regIndex];
	}

	public void set(int regIndex, int value) {
		registers[regIndex] = value;
	}

	public void clear() {
		for (int i = 0; i < registers.length; i++) {
			registers[i] = 0;
		}
	}

	public void setFromList(List<Integer> inputValues) {
		int i = 0;
		for (int value : inputValues) {
			set(i, value);
			i++;
		}
	}

	public List<Integer> asList() {
		List<Integer> result = new ArrayList<>();
		for (int i = 0; i < numRegisters; i++) {
			result.add(get(i));
		}
		return result;
	}

}
