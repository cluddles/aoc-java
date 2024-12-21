package aoc._2017;

import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

import com.google.common.collect.ImmutableList;

import shared.ResourceUtil;
import shared.Test;
import shared.Timer;

public class Day18 {

	class PartOne {
		int line;

		Long snd;
		Long rcv;

		Map<String, Long> registers = new HashMap<>();
		List<String> lines;

		// I'd use ImmutableMap.builder for this, but it doesn't work
		Map<String, BiConsumer<String, String>> ops;
		PartOne(List<String> lines) {
			this.lines = lines;
			ops = new HashMap<>();
			ops.put("snd", this::opSound);
			ops.put("set", this::opSet);
			ops.put("add", this::opAdd);
			ops.put("mul", this::opMultiply);
			ops.put("mod", this::opMod);
			ops.put("rcv", this::opRecover);
			ops.put("jgz", this::opJump);
		}

		long reg(String value) {
			return registers.getOrDefault(value, 0L);
		}
		long regOrLiteral(String value) {
			char c = value.charAt(0);
			if (c >= 'a' && c <= 'z') return reg(value);
			return Long.valueOf(value);
		}

		// set X Y sets register X to the value of Y.
		void opSet(String x, String y) {
			registers.put(x, regOrLiteral(y));
		}

		// add X Y increases register X by the value of Y.
		void opAdd(String x, String y) {
			registers.put(x, reg(x) + regOrLiteral(y));
		}

		// mul X Y sets register X to the result of multiplying the value contained
		// in register X by the value of Y.
		void opMultiply(String x, String y) {
			registers.put(x, reg(x) * regOrLiteral(y));
		}

		// mod X Y sets register X to the remainder of dividing the value contained
		// in register X by the value of Y (that is, it sets X to the result of X
		// modulo Y).
		void opMod(String x, String y) {
			registers.put(x, reg(x) % regOrLiteral(y));
		}

		// jgz X Y jumps with an offset of the value of Y, but only if the value of
		// X is greater than zero. (An offset of 2 skips the next instruction, an
		// offset of -1 jumps to the previous instruction, and so on.)
		void opJump(String x, String y) {
			// Hack by -1 since the line increments automatically each tick
			if (regOrLiteral(x) > 0) line += (regOrLiteral(y) - 1);
		}

		// snd X plays a sound with a frequency equal to the value of X.
		void opSound(String x, String non) {
			snd = regOrLiteral(x);
		}

		// rcv X recovers the frequency of the last sound played, but only when the
		// value of X is not zero. (If it is zero, the command does nothing.)
		void opRecover(String x, String non) {
			if (regOrLiteral(x) != 0) rcv = snd;
		}

		void execute(String line) {
			String[] parts = line.split(" ");
			BiConsumer<String, String> op = ops.get(parts[0]);
			if (op == null) throw new RuntimeException("Unrecognised op: " + line);
			op.accept(parts[1], parts.length >= 3? parts[2] : null);
		}

		void executeAll() {
			while (line >= 0 && line < lines.size()) {
				execute(lines.get(line));
				line++;
				if (rcv != null) break;
			}
		}
	}

	class PartTwo extends PartOne {
		Deque<Long> msgQueue = new LinkedList<>();
		boolean blocking;
		PartTwo other;
		int sendCount;

		PartTwo(List<String> lines, long p) {
			super(lines);
			ops.put("snd", this::opSend);
			ops.put("rcv", this::opReceive);
			registers.put("p", p);
		}

		void opSend(String x, String non) {
			other.msgQueue.add(regOrLiteral(x));
			sendCount++;
		}

		void opReceive(String x, String non) {
			if (msgQueue.isEmpty()) {
				blocking = true;
				// Just repeat over and over...
				line--;
				return;
			}
			blocking = false;
			registers.put(x, msgQueue.pop());
		}

		void executeCurrent() {
			execute(lines.get(line));
			line++;
		}
	}

	public void examples() {
		List<String> lines = ImmutableList.of(
			"set a 1",
			"add a 2",
			"mul a a",
			"mod a 5",
			"snd a",
			"set a 0",
			"rcv a",
			"jgz a -1",
			"set a 1",
			"jgz a -2"
		);

		PartOne partOne = new PartOne(lines);
		partOne.executeAll();
		Test.check(partOne.rcv, 4L);
		System.out.println("Tests fine");
	}

	public static void main(String[] args) throws Exception {
		Day18 day = new Day18();
		day.examples();

		Timer.start();
		List<String> lines = ResourceUtil.readAllLines("2017/day18.input");
		PartOne partOne = day.new PartOne(lines);
		partOne.executeAll();
		// 862 is too low (caused by using ints instead of longs...)
		System.out.println(partOne.rcv);
		Timer.endMessage();

		Timer.start();
		PartTwo p0 = day.new PartTwo(lines, 0);
		PartTwo p1 = day.new PartTwo(lines, 1);
		p0.other = p1;
		p1.other = p0;
		while (!p0.blocking || !p1.blocking) {
			p0.executeCurrent();
			p1.executeCurrent();
		}
		System.out.println(p1.sendCount);
		Timer.endMessage();
	}

}
