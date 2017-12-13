package aoc._2016;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import shared.ResourceUtil;

public class Day23 {

	private int lineIndex;
	private String[] lines;
	private Map<String, Integer> registers = new HashMap<>();

	Day23() {
		registers.put("a", 0);
		registers.put("b", 0);
		registers.put("c", 0);
		registers.put("d", 0);
	}

	private void loadFile(String filename) throws IOException {
		lines = ResourceUtil.readAllLines(filename).toArray(new String[0]);
	}

	private void execute() {

		while (lineIndex < lines.length) {
			System.out.println(lineIndex + ": " + lines[lineIndex] + ": " + registers);

			// p2 - the glorious hackotron
			if (lineIndex == 4 && "cpy b c".equals(lines[lineIndex])) {
				System.out.println("HACK");
				setRegister("a", getRegister("a") + getRegister("b") * getRegister("d"));
				setRegister("c", 0);
				setRegister("d", 0);
				lineIndex = 10;
			}

			String[] split = lines[lineIndex].split(" ");
			switch (split[0]) {
			case "cpy":
				setRegister(split[2], getValue(split[1]));
				lineIndex++;
				break;

			case "inc":
				setRegister(split[1], getRegister(split[1]) + 1);
				lineIndex++;
				break;

			case "dec":
				setRegister(split[1], getRegister(split[1]) - 1);
				lineIndex++;
				break;

			case "jnz":
				if (getValue(split[1]) != 0) {
					lineIndex += getValue(split[2]);
				} else {
					lineIndex++;
				}
				break;

			case "tgl":
				int toggleIndex = lineIndex + getValue(split[1]);
				if (toggleIndex >= 0 && toggleIndex < lines.length) {
					String[] toggled = lines[toggleIndex].split(" ");
					if (toggled.length == 2) {
						// inc becomes dec, all others become inc
						if ("inc".equals(toggled[0])) {
							toggled[0] = "dec";
						} else {
							toggled[0] = "inc";
						}
					} else {
						// jnz becomes cpy, all others become jnz
						if ("jnz".equals(toggled[0])) {
							toggled[0] = "cpy";
						} else {
							toggled[0] = "jnz";
						}
					}
					lines[toggleIndex] = String.join(" ", toggled);
				}
				lineIndex++;
				break;
			}
		}

		System.out.println(registers);
	}

	private void setRegister(String key, int value) {
		if (!registers.containsKey(key)) return;
		registers.put(key, value);
	}

	private int getRegister(String key) {
		Integer val = registers.get(key);
		return (val == null? 0 : val);
	}

	private int getValue(String keyOrValue) {
		try {
			return Integer.parseInt(keyOrValue);
		} catch (NumberFormatException e) {
			return getRegister(keyOrValue);
		}
	}

	public static void main(String[] args) throws Exception {
		Day23 worker = new Day23();
		String filename = "2016/day23.input";

		worker.loadFile(filename);
		// worker.setRegister("a", 7); // p1
		worker.setRegister("a", 12); // p2
		worker.execute();
	}

}
