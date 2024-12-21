package aoc._2018.common;

import java.util.Map;
import java.util.function.BiFunction;

import com.google.common.collect.ImmutableMap;

/**
 * Processor used by Day16, Day19, Day21.
 */
public class Processor {

/*
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

 */

	public interface Op extends BiFunction<Integer, Integer, Integer> {}

	// All supported operations
	private Map<String,Op> ops = ImmutableMap
			.<String, Op>builder()
			.put("addr", this::addr)
			.put("addi", this::addi)
			.put("mulr", this::mulr)
			.put("muli", this::muli)
			.put("banr", this::banr)
			.put("bani", this::bani)
			.put("borr", this::borr)
			.put("bori", this::bori)
			.put("setr", this::setr)
			.put("seti", this::seti)
			.put("gtir", this::gtir)
			.put("gtri", this::gtri)
			.put("gtrr", this::gtrr)
			.put("eqir", this::eqir)
			.put("eqri", this::eqri)
			.put("eqrr", this::eqrr)
			.build();

	int addr(int a, int b) { return reg(a) + reg(b); }
	int addi(int a, int b) { return reg(a) + b; }
	int mulr(int a, int b) { return reg(a) * reg(b); }
	int muli(int a, int b) { return reg(a) * b; }
	int banr(int a, int b) { return reg(a) & reg(b); }
	int bani(int a, int b) { return reg(a) & b; }
	int borr(int a, int b) { return reg(a) | reg(b); }
	int bori(int a, int b) { return reg(a) | b; }
	int setr(int a, int b) { return reg(a); }
	int seti(int a, int b) { return a; }
	int gtir(int a, int b) { return a > reg(b) ? 1 : 0; }
	int gtri(int a, int b) { return reg(a) > b ? 1 : 0; }
	int gtrr(int a, int b) { return reg(a) > reg(b) ? 1 : 0; }
	int eqir(int a, int b) { return a == reg(b) ? 1 : 0; }
	int eqri(int a, int b) { return reg(a) == b ? 1 : 0; }
	int eqrr(int a, int b) { return reg(a) == reg(b) ? 1 : 0; }

	private final ProcessorState state;

	public Processor(ProcessorState state) {
		this.state = state;
	}

	private int  reg    (int index)            { return state.get(index);  }
	private void saveReg(int index, int value) { state.set(index, value); }

	public Map<String, Op> getOps()            { return ops; }

	// Execute the given operation
	public void eval(String opName, int a, int b, int c) {
		saveReg(c, ops.get(opName).apply(a, b));
	}

}
