package aoc._2016;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class Day10 {

	private static Pattern INPUT_PATTERN
			= Pattern.compile("value (\\d*) goes to bot (\\d*)");
	private static Pattern GIVE_PATTERN
			= Pattern.compile("bot (\\d*) gives low to (\\w*) (\\d*) and high to (\\w*) (\\d*)");

	interface Node {
		void addInput(int value);
	}

	class NodeRef {
		String type;
		int index;
		NodeRef(String type, int index) {
			this.type = type;
			this.index = index;
		}
		public Node get() {
			switch (type) {
			case "bot":
				return getBot(index);
			case "output":
				return getOutput(index);
			}
			throw new AssertionError("Unknown type: " + type);
		}
	}

	class Output implements Node {
		private final int index;
		private final List<Integer> inputs = new ArrayList<>();

		Output(int index) {
			this.index = index;
		}

		@Override public void addInput(int value) {
			inputs.add(value);
		}

		@Override
		public String toString() {
			return "output " + index + " (" + inputs + ")";
		}
	}

	class Bot implements Node {
		private final int index;
		private final List<Integer> inputs = new ArrayList<>();
		private NodeRef targetRefLow;
		private NodeRef targetRefHigh;

		Bot(int index) {
			this.index = index;
		}

		@Override public void addInput(int value) {
			inputs.add(value);
		}

		boolean process() {
			if (inputs.size() < 2) {
				return false;
			}
			Node targetLow  = targetRefLow.get();
			Node targetHigh = targetRefHigh.get();
			// Give it away give it away give it away now
			int low  = Math.min(inputs.get(0), inputs.get(1));
			targetLow.addInput(low);
			int high = Math.max(inputs.get(0), inputs.get(1));
			targetHigh.addInput(high);

			if (low == 17 && high == 61) {
				System.out.println(this + " gives " + low + " to " + targetLow + ", " + high + " to " + targetHigh);
			}
			inputs.clear();

			return true;
		}

		@Override
		public String toString() {
			return "bot " + index;
		}
	}

	private Map<Integer, Bot> bots = new HashMap<>();
	private Map<Integer, Output> outputs = new HashMap<>();

	private Bot getBot(int index) {
		Bot bot = bots.get(index);
		if (bot == null) {
			bot = new Bot(index);
			bots.put(index, bot);
		}
		return bot;
	}

	private Output getOutput(int index) {
		Output output = outputs.get(index);
		if (output == null) {
			output = new Output(index);
			outputs.put(index, output);
		}
		return output;
	}

	private void parseFile(String filename) throws IOException {
		try (Stream<String> stream = Files.lines(Paths.get(filename))) {
			stream.forEach(this::parseRule);
		}
		run();
	}

	private void parseRule(String line) {
		Matcher matcher;
		matcher = INPUT_PATTERN.matcher(line);
		if (matcher.matches()) {
			Bot bot = getBot(Integer.parseInt(matcher.group(2)));
			bot.addInput(Integer.parseInt(matcher.group(1)));
			return;
		}

		matcher = GIVE_PATTERN.matcher(line);
		if (matcher.matches()) {
			Bot bot = getBot(Integer.parseInt(matcher.group(1)));
			bot.targetRefLow  = new NodeRef(matcher.group(2), Integer.parseInt(matcher.group(3)));
			bot.targetRefHigh = new NodeRef(matcher.group(4), Integer.parseInt(matcher.group(5)));
			return;
		}
	}

	private void run() {
		boolean didSomething;
		do {
			didSomething = false;
			for (Bot bot : bots.values()) {
				didSomething = bot.process() || didSomething;
			}
		} while (didSomething == true);

		for (int i = 0; i <= 2; i++) {
			System.out.println(getOutput(i));
		}
	}

	public static void main(String[] args) throws Exception {
		String filename = "2016/day10.input";

		Day10 worker = new Day10();
		worker.parseFile(filename);
	}

}
