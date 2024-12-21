package aoc._2016;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import shared.ResourceUtil;

public class Day12 {

	// TODO refactor to use Assembunny class

	private void parseFile(String filename) throws IOException {
		List<String> lines = new ArrayList<>();
		lines.addAll(ResourceUtil.readAllLines(filename));

		int lineIndex = 0;
		Map<String, Integer> registers = new HashMap<>();

		registers.put("a", 0);
		registers.put("b", 0);
		registers.put("c", 1); // part 2
		registers.put("d", 0);

		while (true) {
			if (lineIndex >= lines.size()) break;

			String line = lines.get(lineIndex);
//			System.out.println(line);

			String command = line.substring(0, 3);
			String args = line.substring(4);
			switch (command) {
			case "cpy": {
				String[] splitArgs = args.split(" ");
				registers.put(splitArgs[1], getValue(registers, splitArgs[0]));
				lineIndex++;
				break;
			}

			case "inc": {
				registers.put(args, registers.get(args) + 1);
				lineIndex++;
				break;
			}

			case "dec": {
				registers.put(args, registers.get(args) - 1);
				lineIndex++;
				break;
			}

			case "jnz": {
				String[] splitArgs = args.split(" ");
				if (getValue(registers, splitArgs[0]) != 0) {
					lineIndex += Integer.parseInt(splitArgs[1]);
				} else {
					lineIndex++;
				}
				break;
			}
			}
		}

		System.out.println(registers.get("a"));
	}

	private int getValue(Map<String, Integer> registers, String arg) {
		if (arg.matches("\\d*")) {
			return Integer.parseInt(arg);
		} else {
			return registers.get(arg);
		}
	}

	public static void main(String[] args) throws Exception {
		String filename = "2016/day12.input";

		Day12 worker = new Day12();
		worker.parseFile(filename);
	}

}
