package aoc._2016;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class Day7 {

	private void parseFile(String filename) throws IOException {
		// Read file into stream, try-with-resources
		try (Stream<String> stream = Files.lines(Paths.get(filename))) {
			long abbaCount = stream.filter(this::checkLineTls).count();
			System.out.println("TLS count: " + abbaCount);
		}
		try (Stream<String> stream = Files.lines(Paths.get(filename))) {
			long sslCount = stream.filter(this::checkLineSsl).count();
			System.out.println("SSL count: " + sslCount);
		}
	}

	private boolean checkLineSsl(String line) {
		System.out.println("Checking line (SSL): " + line);

		// Track bracketed sections
		int braceCount = 0;
		// We need to build up a queue of 3 characters
		LinkedList<Character> chars = new LinkedList<>();
		for (int i = 0; i < line.length(); i++) {
			char c = line.charAt(i);

			if (c == '[') {
				braceCount++;
				chars.clear();

			} else if (c == ']') {
				braceCount--;
				if (braceCount < 0) braceCount = 0;
				chars.clear();

			} else {
				if (braceCount == 0) {
					chars.add(c);
					while (chars.size() > 3) {
						chars.removeFirst();
					}
					if (chars.size() == 3) {
						if (chars.get(0) == chars.get(2) && chars.get(0) != chars.get(1)) {
							// Got an ABA
							System.out.println("Found ABA: " + chars);
							// Look for BAB
							Pattern pattern = Pattern.compile(
									".*\\[\\w*"
									+ chars.get(1)
									+ chars.get(0)
									+ chars.get(1)
									+ "\\w*\\].*");
							Matcher matcher = pattern.matcher(line);
							if (matcher.matches()) {
								System.out.println("Found BAB");
								return true;
							}
						}
					}
				}
			}
		}

		return false;
	}

	private boolean checkLineTls(String line) {
		System.out.println("Checking line (TLS): " + line);
		// Track bracketed sections
		int braceCount = 0;
		// We need to build up a queue of 4 characters
		LinkedList<Character> chars = new LinkedList<>();

		boolean foundGood = false;
		for (int i = 0; i < line.length(); i++) {
			char c = line.charAt(i);
			if (c == '[') {
				braceCount++;
				chars.clear();

			} else if (c == ']') {
				braceCount--;
				if (braceCount < 0) braceCount = 0;
				chars.clear();

			} else {
				chars.add(c);
				while (chars.size() > 4) {
					chars.removeFirst();
				}
				if (chars.size() == 4) {
					if (chars.get(0) == chars.get(3)
							&& chars.get(0) != chars.get(1)
							&& chars.get(1) == chars.get(2)) {
						if (braceCount == 0) {
							System.out.println("Found GOOD: " + chars);
							foundGood = true;

						} else {
							System.out.println("Found BAD: " + chars);
							return false;
						}
					}
				}
			}
		}

		System.out.println("No match");
		return foundGood;
	}

	public static void main(String[] args) throws Exception {
		String filename = "2016/day7.input";

		Day7 worker = new Day7();
		worker.parseFile(filename);
	}

}
