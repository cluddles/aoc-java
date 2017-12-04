package aoc._2016;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * @author Dan Fielding
 */
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

	/**
	 * An IP supports SSL if it has an Area-Broadcast Accessor, or ABA, anywhere
	 * in the supernet sequences (outside any square bracketed sections), and a
	 * corresponding Byte Allocation Block, or BAB, anywhere in the hypernet
	 * sequences. An ABA is any three-character sequence which consists of the
	 * same character twice with a different character between them, such as xyx
	 * or aba. A corresponding BAB is the same characters but in reversed
	 * positions: yxy and bab, respectively.
	 * <p>
	 * For example:
	 * <ul>
	 * <li>aba[bab]xyz supports SSL (aba outside square brackets with
	 * corresponding bab within square brackets).</li>
	 * <li>xyx[xyx]xyx does not support SSL (xyx, but no corresponding
	 * yxy).</li>
	 * <li>aaa[kek]eke supports SSL (eke in supernet with corresponding kek in
	 * hypernet; the aaa sequence is not related, because the interior
	 * character must be different).</li>
	 * <li>zazbz[bzb]cdb supports SSL (zaz has no corresponding aza, but zbz
	 * has a corresponding bzb, even though zaz and zbz overlap).</li>
	 * </ul>
	 *
	 * @param line
	 * 		Line to check.
	 * @return True for a match, false otherwise.
	 */
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

	/**
	 * An IP supports TLS if it has an Autonomous Bridge Bypass Annotation, or
	 * ABBA. An ABBA is any four-character sequence which consists of a pair of
	 * two different characters followed by the reverse of that pair, such as
	 * xyyx or abba. However, the IP also must not have an ABBA within any
	 * hypernet sequences, which are contained by square brackets.
	 * <p>
	 * For example:
	 * <ul>
	 * <li>abba[mnop]qrst supports TLS (abba outside square brackets).</li>
	 * <li>abcd[bddb]xyyx does not support TLS (bddb is within square brackets,
	 * even though xyyx is outside square brackets).</li>
	 * <li>aaaa[qwer]tyui does not support TLS (aaaa is invalid; the interior
	 * characters must be different).</li>
	 * <li>ioxxoj[asdfgh]zxcvbn supports TLS (oxxo is outside square brackets,
	 * even though it's within a larger string).</li>
	 * </ul>
	 *
	 * @param line
	 * 		Line to check.
	 * @return True for a match, false otherwise.
	 */
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
		String filename = "resources/day7input.txt";

		Day7 worker = new Day7();
		worker.parseFile(filename);
	}

}
