package aoc._2015;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.collect.Collections2;

import shared.ResourceUtil;

public class Day13 {

	class Person {
		final String name;
		final Map<String, Integer> scores = new HashMap<>();
		Person(String name) {
			this.name = name;
		}
	}

	Pattern linePattern = Pattern.compile(
			"(\\w*) would (\\w*) (\\d*) happiness units by sitting next to (\\w*).");


	private void arrange(String path) throws IOException {
		List<String> lines = ResourceUtil.readAllLines(path);

		Map<String, Person> people = new HashMap<>();
		for (String line : lines) {
			Matcher matcher = linePattern.matcher(line);
			if (matcher.matches()) {
				int score = Integer.parseInt(matcher.group(3));
				if ("lose".equals(matcher.group(2))) score = -score;
				addScore(people, matcher.group(1), matcher.group(4), score);
			}
		}

		// p2
		people.put("me", new Person("me"));

		Collection<List<String>> permutations = Collections2.permutations(people.keySet());
		int best = Integer.MIN_VALUE;
		for (List<String> permutation : permutations) {
			best = Math.max(scorePermutation(people, permutation), best);
		}

		System.out.println(best);
	}

	private int scorePermutation(
			Map<String, Person> people,
			List<String> permutation) {
		int score = 0;
		int numPeeps = permutation.size();
		for (int i = 0; i < numPeeps; i++) {
			Person left = people.get(permutation.get(i));
			Person right = people.get(permutation.get((i+1) % numPeeps));
			score += getScore(left, right);
			score += getScore(right, left);
		}
		return score;
	}

	private int getScore(Person left, Person right) {
		Integer score = left.scores.get(right.name);
		if (score != null) return score;
		return 0;
	}

	private void addScore(
			Map<String, Person> people,
			String name,
			String target,
			int score) {
		Person person = people.get(name);
		if (person == null) {
			person = new Person(name);
			people.put(name, person);
		}
		person.scores.put(target, score);
	}

	public static void main(String[] args) throws Exception {
		Day13 worker = new Day13();
		// p1
		worker.arrange("2015/day13.input");
	}

}
