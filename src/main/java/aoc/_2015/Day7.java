package aoc._2015;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.base.MoreObjects;

import shared.ResourceUtil;

public class Day7 {

	private static final int NONE = -1;

	private void from(String path) throws IOException {
		List<String> lines = ResourceUtil.readAllLines(path);
		part1(lines);
	}

	class Gate {
		final String type;
		final Wire a;
		final Wire b;
		final Wire out;
		Gate(String type, Wire a, Wire b, Wire out) {
			this.type = type;
			this.a = a;
			this.b = b;
			this.out = out;
		}

		boolean eval() {
			switch (type) {
			case "AND":
				if (!a.ready() || !b.ready()) return false;
				out.set(a.value & b.value);
				return true;
			case "OR":
				if (!a.ready() || !b.ready()) return false;
				out.set(a.value | b.value);
				return true;
			case "LSHIFT":
				if (!a.ready() || !b.ready()) return false;
				out.set(a.value << b.value);
				return true;
			case "RSHIFT":
				if (!a.ready() || !b.ready()) return false;
				out.set(a.value >> b.value);
				return true;
			case "NOT":
				if (!a.ready()) return false;
				out.set(~a.value);
				return true;
			case "SET":
				if (!a.ready()) return false;
				out.set(a.value);
				return true;
			}
			return false;
		}

		@Override
		public String toString() {
			return MoreObjects.toStringHelper(this)
					.add("type", type)
					.add("a", a)
					.add("b", b)
					.add("out", out)
					.toString();
		}
	}

	class Wire {
		final String name;
		int value = NONE;
		boolean locked;

		public Wire(String name) {
			this.name = name;
		}
		public Wire(String name, int i) {
			this.name = name;
			set(i);
		}
		boolean ready() {
			return value != NONE;
		}

		void set(int value) {
			if (locked) return;
			this.value = value & 0xffff;
		}

		@Override
		public String toString() {
			return MoreObjects.toStringHelper(this)
					.add("name", name)
					.add("value", value)
					.toString();
		}
	}

	Wire wire(Map<String, Wire> wires, String name) {
		try {
			return new Wire("none", Integer.valueOf(name));
		} catch (NumberFormatException e) {}

		Wire wire = wires.get(name);
		if (wire == null) {
			wire = new Wire(name);
			wires.put(name, wire);
		}
		return wire;
	}

	private void part1(List<String> lines) {
		List<Gate> gates = new ArrayList<>();
		Map<String, Wire> wires = new HashMap<>();
		for (String line : lines) {
			System.out.println(line);
			String[] words = line.split(" ");
			if (words.length == 3) {
				gates.add(new Gate("SET", wire(wires, words[0]), null, wire(wires, words[2])));

			} else if (words.length == 5) {
				gates.add(new Gate(words[1], wire(wires, words[0]), wire(wires, words[2]), wire(wires, words[4])));

			} else {
				gates.add(new Gate(words[0], wire(wires, words[1]), null, wire(wires, words[3])));
			}
		}

		// Part 2
		wires.get("b").set(3176);
		wires.get("b").locked = true;

		while (!gates.isEmpty()) {
			gates.removeIf(Gate::eval);
		}

		System.out.println(wires.get("a"));
	}

	public static void main(String[] args) throws Exception {
		Day7 worker = new Day7();

//		List<String> lines = new ArrayList<>();
//		lines.add("123 -> x");
//		lines.add("456 -> y");
//		lines.add("x AND y -> d");
//		lines.add("x OR y -> e");
//		lines.add("x LSHIFT 2 -> f");
//		lines.add("y RSHIFT 2 -> g");
//		lines.add("NOT x -> h");
//		lines.add("NOT y -> i");
//		worker.part1(lines);

		worker.from("2015/day7.input");
	}

}
