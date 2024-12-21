package aoc._2016;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import shared.ResourceUtil;

public class Day4 {

	public static final Pattern PATTERN = Pattern.compile("((?:[a-z]|-)*)(\\d*)\\[(\\w*)\\]");

	private void parseFile(String filename) throws IOException {
		List<String> validRooms = new ArrayList<String>();

		List<String> lines = ResourceUtil.readAllLines(filename);
		long sectorIdSum = 0;
		for (String line : lines) {
			if (sectorId(line) != 0L) {
				sectorIdSum += sectorId(line);
				validRooms.add(line);
			}
		}
		System.out.println("Sum of Sector IDs: " + sectorIdSum);

		for (String line : lines) {
			String name = decodeLine(line);
			System.out.println(name);
		}
	}

	private String decodeLine(String line) {
		Matcher matcher = PATTERN.matcher(line);
		if (matcher.matches()) {
			String main = matcher.group(1);
			int id = Integer.parseInt(matcher.group(2));

			StringBuilder decrypted = new StringBuilder();
			for (int i = 0; i < main.length(); i++) {
				char c = main.charAt(i);
				if (c >= 'a' && c <= 'z') {
					int offset = (c - 'a' + id) % 26;
					c = (char) ('a' + offset);
				}
				decrypted.append(c);
			}
			return decrypted.toString() + id;
		}
		return line;
	}

	private int sectorId(String line) {
		/*

		aaaaa-bbb-z-y-x-123[abxyz] is a real room because the most common letters are a (5), b (3), and then a tie between x, y, and z, which are listed alphabetically.
		a-b-c-d-e-f-g-h-987[abcde] is a real room because although the letters are all tied (1 of each), the first five are listed alphabetically.
		not-a-real-room-404[oarel] is a real room.
		totally-real-room-200[decoy] is not.

		 */
		Matcher matcher = PATTERN.matcher(line);
		if (matcher.matches()) {
			// Pull out the data
			String main = matcher.group(1);
			int id = Integer.parseInt(matcher.group(2));
			String checksum = matcher.group(3);

			// Count the characters in the main text
			Map<Character, Integer> letterCount = new HashMap<>();
			for (int i = 0; i < main.length(); i++) {
				char c = main.charAt(i);
				if (c >= 'a' && c <= 'z') {
					if (letterCount.containsKey(c)) {
						letterCount.put(c, letterCount.get(c) + 1);
					} else {
						letterCount.put(c, 1);
					}
				}
			}

			// Sort the map by value, key descending
			Map<Character, Integer> sortedLetterCount = sortByValue(letterCount);
			// Work out the checksum we expect
			int i = 0;
			StringBuilder checksumBuilder = new StringBuilder();
			for (Map.Entry<Character, Integer> entry : sortedLetterCount.entrySet()) {
				if (i >= 5) break;
				checksumBuilder.append(entry.getKey());
				i++;
			}

			// Now compare and return
			String expectedChecksum = checksumBuilder.toString();
			boolean match = Objects.equals(expectedChecksum, checksum);
			System.out.println(line + " -- Checksum " + checksum + " vs " + expectedChecksum + ": " + (match? "GOOD" : "nope!!!"));
			if (match) {
				return id;
			}
		}

		return 0;
	}

	static class EntryComparator implements Comparator<Map.Entry<Character, Integer>> {

		@Override
		public int compare(Map.Entry<Character, Integer> o1, Map.Entry<Character, Integer> o2) {
			if (o1.getValue() != o2.getValue()) {
				return o2.getValue() - o1.getValue();
			}
			return o1.getKey() - o2.getKey();
		}
	}

	public static Map<Character, Integer> sortByValue(Map<Character, Integer> map) {
		return map.entrySet()
				.stream()
				.sorted(new EntryComparator())
				.collect(Collectors.toMap(
						Map.Entry::getKey,
						Map.Entry::getValue,
						(e1, e2) -> e1,
						LinkedHashMap::new
				));
	}

	public static void main(String[] args) throws Exception {
		String filename = "2016/day4.input";

		Day4 worker = new Day4();
		worker.parseFile(filename);
	}

}
