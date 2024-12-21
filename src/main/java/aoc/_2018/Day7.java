package aoc._2018;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.collect.ImmutableList;

import shared.ResourceUtil;
import shared.Test;

public class Day7 {

	private static final Pattern INPUT_PATTERN = Pattern.compile(
			"Step (\\w) must be finished before step (\\w) can begin.");

	private List<Character> get(Map<Character, List<Character>> map, Character key) {
		return map.computeIfAbsent(key, k -> new ArrayList<>());
	}

	private Map<Character, List<Character>> parseInput(List<String> lines) {
		Map<Character, List<Character>> result = new TreeMap<>();
		for (String line : lines) {
			Matcher matcher = INPUT_PATTERN.matcher(line);
			if (!matcher.matches()) throw new IllegalStateException("Bad line: " + line);
			Character req  = matcher.group(1).charAt(0);
			Character from = matcher.group(2).charAt(0);
			// Add the requirement
			get(result, from).add(req);
			// Make sure the required letter is also present in the map, even if
			// it has no requirements itself
			get(result, req);
		}
		return result;
	}

	// Pops the next fulfilled preReq
	private Character popNext(
			Map<Character, List<Character>> preReqs,
			Set<Character> done) {
		Character best = null;
		for (Character key : preReqs.keySet()) {
			List<Character> keyPreReqs = preReqs.get(key);
			if (done.containsAll(keyPreReqs)) {
				best = key;
				break;
			}
		}
		if (best == null) return null;
		preReqs.remove(best);
		return best;
	}

	public String evalPart1(List<String> lines) {
		Map<Character, List<Character>> preReqs = parseInput(lines);
		Set<Character> done   = new HashSet<>();
		StringBuilder  answer = new StringBuilder();

		while (!preReqs.isEmpty()) {
			Character next = popNext(preReqs, done);
			if (next == null) throw new IllegalStateException("Cannot solve");
			answer.append(next);
			done.add(next);
		}
		return answer.toString();
	}

	class Worker {
		final Character job;
		int             endTick;

		Worker(Character job, int endTick) {
			this.job     = job;
			this.endTick = endTick;
		}
	}

	public int evalPart2(List<String> lines, int numWorkers, int extraTime) {
		Map<Character, List<Character>> preReqs = parseInput(lines);
		Set<Character> done = new HashSet<>();

		List<Worker> workers = new ArrayList<>();
		int tick = 0;
		while (!preReqs.isEmpty() || !workers.isEmpty()) {
			// Assign a job to each available worker
			while (workers.size() < numWorkers) {
				Character next = popNext(preReqs, done);
				// Is there a job ready?
				if (next == null) break;
				// Start it
				workers.add(new Worker(
						next,
						tick + extraTime + next - 'A' + 1));
			}
			// Work on jobs
			if (workers.isEmpty()) throw new IllegalStateException("No jobs");
			tick++;
			for (Iterator<Worker> it = workers.iterator(); it.hasNext(); ) {
				Worker worker = it.next();
				// Check for finished job
				if (worker.endTick <= tick) {
					done.add(worker.job);
					it.remove();
				}
			}
		}
		return tick;
	}

	public static void main(String[] args) throws Exception {
		Day7 solver = new Day7();
		List<String> input = ResourceUtil.readAllLines("2018/day7.input");

		// Part 1
		// Example
		List<String> example = ImmutableList.of(
				"Step C must be finished before step A can begin.",
				"Step C must be finished before step F can begin.",
				"Step A must be finished before step B can begin.",
				"Step A must be finished before step D can begin.",
				"Step B must be finished before step E can begin.",
				"Step D must be finished before step E can begin.",
				"Step F must be finished before step E can begin."
		);
		Test.check(solver.evalPart1(example), "CABDFE");
		// Vs Input
		System.out.println(solver.evalPart1(input));

		// Part 2
		// Example
		Test.check(solver.evalPart2(example, 2, 0), 15);
		// Vs Input
		System.out.println(solver.evalPart2(input, 5, 60));
	}

}
