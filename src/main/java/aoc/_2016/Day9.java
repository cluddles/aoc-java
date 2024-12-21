package aoc._2016;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import shared.ResourceUtil;

public class Day9 {

	private static final Pattern REPEAT_PATTERN
			= Pattern.compile("\\((\\d*)x(\\d*)\\)(.*)");

	private void parseFile(String filename) throws IOException {
		List<String> lines = ResourceUtil.readAllLines(filename);
		// There should only be one hugenormous line anyway
		String toParse = lines.get(0);
		//simpleDecompress(toParse);
		uglyDecompress(toParse);
	}

	class Repetition {
		int charsRemaining;
		int repeats;

		Repetition(int charsRemaining, int repeats) {
			this.charsRemaining = charsRemaining;
			this.repeats = repeats;
		}
	}

	private void uglyDecompress(String toParse) {
		long length = 0;
		List<Repetition> reps = new ArrayList<>();
		while (toParse.length() > 0) {
			String toParseBefore = toParse;
			Repetition repToAdd = null;

			char c = toParse.charAt(0);
			Matcher matcher = REPEAT_PATTERN.matcher(toParse);
			if (matcher.matches()) {
				// Work out chars to read
				repToAdd = new Repetition(
						Integer.parseInt(matcher.group(1)),
						Integer.parseInt(matcher.group(2)));
				// Skip to end of pattern
				toParse = matcher.group(3);

			} else {
				toParse = toParse.substring(1);
				// Work out how many characters this will decompress to
				int chars = 1;
				for (Repetition rep : reps) {
					chars *= rep.repeats;
				}
				length += chars;
			}

			// Count down for reps, remove any expired ones
			int diff = toParseBefore.length() - toParse.length();
			Iterator<Repetition> itRep = reps.iterator();
			while (itRep.hasNext()) {
				Repetition rep = itRep.next();
				rep.charsRemaining -= diff;
				if (rep.charsRemaining <= 0) itRep.remove();
			}
			if (repToAdd != null) {
				reps.add(repToAdd);
			}
		}
		System.out.println(length);
	}

	private void simpleDecompress(String toParse) {
		StringBuilder out = new StringBuilder();
		int repetitions = 0;
		int repeatCharsRemaining = 0;
		StringBuilder repeat = new StringBuilder();
		while (toParse.length() > 0) {
			char c = toParse.charAt(0);
			Matcher matcher = REPEAT_PATTERN.matcher(toParse);
			if (repeatCharsRemaining == 0) {
				if (matcher.matches()) {
					// Work out chars to read
					repeatCharsRemaining = Integer.parseInt(matcher.group(1));
					repetitions = Integer.parseInt(matcher.group(2));
					// Skip to end of pattern
					toParse = matcher.group(3);

				} else {
					// Just copy to output
					out.append(c);
					toParse = toParse.substring(1);
				}

			} else {
				// We're reading the buffer to repeat
				repeat.append(c);
				toParse = toParse.substring(1);

				repeatCharsRemaining--;
				if (repeatCharsRemaining == 0) {
					// Insert the repeat buffer now
					for (int i = 0; i < repetitions; i++) {
						out.append(repeat.toString());
					}
					repeat = new StringBuilder();
				}
			}
		}
		System.out.println(out.toString());
		System.out.println(out.length());
	}

	public static void main(String[] args) throws Exception {
		String filename = "2016/day9.input";

		Day9 worker = new Day9();
		worker.parseFile(filename);
	}

}
