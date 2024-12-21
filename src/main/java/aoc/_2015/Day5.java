package aoc._2015;

import java.util.List;
import java.util.regex.Pattern;

import shared.ResourceUtil;

public class Day5 {

//	static final Pattern NICE_PATTERN1 = Pattern.compile(".*[aeiou].*[aeiou].*[aeiou].*");
//	static final Pattern NICE_PATTERN2 = Pattern.compile(".*(.)\\1.*");
//	static final Pattern NAUGHTY_PATTERN = Pattern.compile(".*(ab|cd|pq|xy).*");

	static final Pattern NICE_PATTERN1 = Pattern.compile(".*(..).*\\1.*");
	static final Pattern NICE_PATTERN2 = Pattern.compile(".*(.).\\1.*");

	public void run() throws Exception {
		List<String> lines = ResourceUtil.readAllLines("2015/day5.input");
		int nice = 0;
		for (String line : lines) {
//			if (NICE_PATTERN1.matcher(line).matches()
//					&& NICE_PATTERN2.matcher(line).matches()
//					&& !NAUGHTY_PATTERN.matcher(line).matches()) {
			if (NICE_PATTERN1.matcher(line).matches()
					&& NICE_PATTERN2.matcher(line).matches()) {
				nice++;
			}
		}
		System.out.println(nice);
	}

	public static void main(String[] args) throws Exception {
		Day5 worker = new Day5();
		worker.run();
	}

}
