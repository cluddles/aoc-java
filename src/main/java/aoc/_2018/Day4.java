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

/**
 * @author Dan Fielding
 */
public class Day4 {

/*

--- Day 4: Repose Record ---

You've sneaked into another supply closet - this time, it's across from the
prototype suit manufacturing lab. You need to sneak inside and fix the issues
with the suit, but there's a guard stationed outside the lab, so this is as
close as you can safely get.

As you search the closet for anything that might help, you discover that you're
not the first person to want to sneak in. Covering the walls, someone has spent
an hour starting every midnight for the past few months secretly observing this
guard post! They've been writing down the ID of the one guard on duty that
night - the Elves seem to have decided that one guard was enough for the
overnight shift - as well as when they fall asleep or wake up while at their
post (your puzzle input).

For example, consider the following records, which have already been organized
into chronological order:

[1518-11-01 00:00] Guard #10 begins shift
[1518-11-01 00:05] falls asleep
[1518-11-01 00:25] wakes up
[1518-11-01 00:30] falls asleep
[1518-11-01 00:55] wakes up
[1518-11-01 23:58] Guard #99 begins shift
[1518-11-02 00:40] falls asleep
[1518-11-02 00:50] wakes up
[1518-11-03 00:05] Guard #10 begins shift
[1518-11-03 00:24] falls asleep
[1518-11-03 00:29] wakes up
[1518-11-04 00:02] Guard #99 begins shift
[1518-11-04 00:36] falls asleep
[1518-11-04 00:46] wakes up
[1518-11-05 00:03] Guard #99 begins shift
[1518-11-05 00:45] falls asleep
[1518-11-05 00:55] wakes up

Timestamps are written using year-month-day hour:minute format. The guard
falling asleep or waking up is always the one whose shift most recently started.
Because all asleep/awake times are during the midnight hour (00:00 - 00:59),
only the minute portion (00 - 59) is relevant for those events.

Visually, these records show that the guards are asleep at these times:

Date   ID   Minute
            000000000011111111112222222222333333333344444444445555555555
            012345678901234567890123456789012345678901234567890123456789
11-01  #10  .....####################.....#########################.....
11-02  #99  ........................................##########..........
11-03  #10  ........................#####...............................
11-04  #99  ....................................##########..............
11-05  #99  .............................................##########.....

The columns are Date, which shows the month-day portion of the relevant day; ID,
which shows the guard on duty that day; and Minute, which shows the minutes
during which the guard was asleep within the midnight hour. (The Minute column's
header shows the minute's ten's digit in the first row and the one's digit in
the second row.) Awake is shown as ., and asleep is shown as #.

Note that guards count as asleep on the minute they fall asleep, and they count
as awake on the minute they wake up. For example, because Guard #10 wakes up at
00:25 on 1518-11-01, minute 25 is marked as awake.

If you can figure out the guard most likely to be asleep at a specific time, you
might be able to trick that guard into working tonight so you can have the best
chance of sneaking in. You have two strategies for choosing the best
guard/minute combination.

Strategy 1: Find the guard that has the most minutes asleep. What minute does
that guard spend asleep the most?

In the example above, Guard #10 spent the most minutes asleep, a total of 50
minutes (20+25+5), while Guard #99 only slept for a total of 30 minutes
(10+10+10). Guard #10 was asleep most during minute 24 (on two days, whereas any
other minute the guard was asleep was only seen on one day).

While this example listed the entries in chronological order, your entries are
in the order you found them. You'll need to organize them before they can be
analyzed.

What is the ID of the guard you chose multiplied by the minute you chose? (In
the above example, the answer would be 10 * 24 = 240.)

--- Part Two ---

Strategy 2: Of all guards, which guard is most frequently asleep on the same
minute?

In the example above, Guard #99 spent minute 45 asleep more than any other guard
or minute - three times in total. (In all other cases, any guard spent any
minute asleep at most twice.)

What is the ID of the guard you chose multiplied by the minute you chose? (In
the above example, the answer would be 99 * 45 = 4455.)

*/

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
