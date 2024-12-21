package aoc._2018;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.collect.ImmutableList;

import shared.ResourceUtil;
import shared.Test;

public class Day4 {

	private static final Pattern TIME_PATTERN =
			Pattern.compile("\\d\\d:(?<min>\\d\\d)]");
	private static final Pattern SHIFT_PATTERN =
			Pattern.compile("Guard #(?<id>\\d+) begins shift");


	class Guard {
		final int id;
		int[] sleepMinutes = new int[60];
		int   totalSleepMinutes;

		Guard(int id) {
			this.id = id;
		}

		public int getTotalSleepMinutes() {
			return totalSleepMinutes;
		}
		public int sleepiestMinute() {
			int maxVal   = -1;
			int maxIndex =  0;
			for (int i = 0; i < 60; i++) {
				if (sleepMinutes[i] > maxVal) {
					maxVal   = sleepMinutes[i];
					maxIndex = i;
				}
			}
			return maxIndex;
		}
	}

	public void applySleep(Guard guard, int from, int to) {
		for (int i = from; i < to; i++) {
			guard.sleepMinutes[i]++;
			guard.totalSleepMinutes++;
		}
	}

	public void endShift(Guard guard, int lastMin, boolean lastAwake) {
		// If guard was asleep, populate sleep minutes for the rest of the hour
		if (!lastAwake) applySleep(guard, lastMin, 60);
	}

	public Map<Integer, Guard> parseShifts(List<String> lines) {
		// Guard state by id
		Map<Integer, Guard> result = new HashMap<>();

		// Simple state machine
		boolean lastAwake = true;
		int     lastMin   = 0;
		Guard   guard     = null;

		for (String line : lines) {
			Matcher shiftMatcher = SHIFT_PATTERN.matcher(line);
			if (shiftMatcher.find()) {
				// Shift starts
				// End the previous shift
				endShift(guard, lastMin, lastAwake);
				// Reset state
				int id    = Integer.parseInt(shiftMatcher.group("id"));
				guard     = result.computeIfAbsent(id, Guard::new);
				lastMin   = 0;
				lastAwake = true;

			} else {
				// Sleep state change
				Matcher timeMatcher = TIME_PATTERN.matcher(line);
				if (timeMatcher.find()) {
					int     min   = Integer.parseInt(timeMatcher.group("min"));
					boolean awake = line.contains("wakes up");
					// Check if anything actually changed
					if (awake != lastAwake) {
						// Apply sleep (if appropriate)
						if (!lastAwake) applySleep(guard, lastMin, min);
						lastMin   = min;
						lastAwake = awake;
					}
				} else {
					throw new IllegalStateException("Couldn't parse line: " + line);
				}
			}
		}
		// End the final shift
		endShift(guard, lastMin, lastAwake);
		return result;
	}

	public int evalPart1(List<String> lines) {
		// Turn input into state
		Map<Integer, Guard> guardStates = parseShifts(lines);
		// Who is the sleepiest guard?
		Guard sleepiest = guardStates.values().stream()
				.max(Comparator.comparingInt(Guard::getTotalSleepMinutes))
				.orElseThrow(() -> new IllegalStateException("Couldn't find sleepiest guard"));
		// Determine answer
		return sleepiest.id * sleepiest.sleepiestMinute();
	}

	public int evalPart2(List<String> lines) {
		// Turn input into state
		Map<Integer, Guard> guardStates = parseShifts(lines);
		// Find minute spent asleep most by any one guard
		int maxVal   = -1;
		int maxIndex = 0;
		int maxId    = 0;
		for (Guard guard : guardStates.values()) {
			int sleepiestMinute = guard.sleepiestMinute();
			if (guard.sleepMinutes[sleepiestMinute] > maxVal) {
				maxVal   = guard.sleepMinutes[sleepiestMinute];
				maxIndex = sleepiestMinute;
				maxId    = guard.id;
			}
		}
		return maxId * maxIndex;
	}

	public static void main(String[] args) throws Exception {
		Day4 solver = new Day4();
		List<String> lines = ResourceUtil.readAllLines("2018/day4.input");
		// Since it's [yyyy-mm-dd] this will conveniently sort chronologically
		lines.sort(String::compareTo);

		// Part 1
		// Examples
		List<String> exampleLines = ImmutableList.of(
				"[1518-11-01 00:00] Guard #10 begins shift",
				"[1518-11-01 00:05] falls asleep",
				"[1518-11-01 00:25] wakes up",
				"[1518-11-01 00:30] falls asleep",
				"[1518-11-01 00:55] wakes up",
				"[1518-11-01 23:58] Guard #99 begins shift",
				"[1518-11-02 00:40] falls asleep",
				"[1518-11-02 00:50] wakes up",
				"[1518-11-03 00:05] Guard #10 begins shift",
				"[1518-11-03 00:24] falls asleep",
				"[1518-11-03 00:29] wakes up",
				"[1518-11-04 00:02] Guard #99 begins shift",
				"[1518-11-04 00:36] falls asleep",
				"[1518-11-04 00:46] wakes up",
				"[1518-11-05 00:03] Guard #99 begins shift",
				"[1518-11-05 00:45] falls asleep",
				"[1518-11-05 00:55] wakes up");
		Test.check(solver.evalPart1(exampleLines), 240);
		// Vs Input
		System.out.println(solver.evalPart1(lines));

		// Part 2
		// Examples
		Test.check(solver.evalPart2(exampleLines), 4455);
		// Vs Input
		System.out.println(solver.evalPart2(lines));
	}

}
