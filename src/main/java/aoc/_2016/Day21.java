package aoc._2016;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.collect.ImmutableList;

import shared.ResourceUtil;

public class Day21 {

	class Action {
		final Pattern pattern;
		final BiFunction<Matcher, String, String> function;
		final BiFunction<Matcher, String, String> inverse;

		Action(String pattern,
				BiFunction<Matcher, String, String> function,
				BiFunction<Matcher, String, String> inverse) {
			this.pattern = Pattern.compile(pattern);
			this.function = function;
			this.inverse = inverse;
		}
	}

	List<Action> actions = ImmutableList.<Action>builder()
			.add(new Action("swap position (\\d*) with position (\\d*)",
					(m, in) -> swapPos(in, mInt(m, 1), mInt(m, 2)),
					(m, in) -> swapPos(in, mInt(m, 1), mInt(m, 2))))
			.add(new Action("swap letter (\\w) with letter (\\w)",
					(m, in) -> swapLetter(in, mStr(m, 1), mStr(m, 2)),
					(m, in) -> swapLetter(in, mStr(m, 1), mStr(m, 2))))
			.add(new Action("rotate (\\w*) (\\d*) step.*",
					(m, in) -> rotate(in, mStr(m, 1), mInt(m, 2)),
					(m, in) -> rotate(in, mStr(m, 1), -mInt(m, 2))))
			.add(new Action("rotate based on position of letter (\\w)",
					(m, in) -> rotateLetter(in, mStr(m, 1)),
					(m, in) -> unrotateLetter(in, mStr(m, 1))))
			.add(new Action("reverse positions (\\d*) through (\\d*)",
					(m, in) -> reverse(in, mInt(m, 1), mInt(m, 2)),
					(m, in) -> reverse(in, mInt(m, 1), mInt(m, 2))))
			.add(new Action("move position (\\d*) to position (\\d*)",
					(m, in) -> move(in, mInt(m, 1), mInt(m, 2)),
					(m, in) -> move(in, mInt(m, 2), mInt(m, 1))))
			.build();

	static int mInt(Matcher m, int group) { return Integer.parseInt(m.group(group)); }
	static String mStr(Matcher m, int group) { return m.group(group); }

	static String swapPos(String in, int x, int y) {
		int min = Math.min(x, y);
		int max = Math.max(x, y);
		return in.substring(0, min)
				+ in.charAt(max)
				+ in.substring(min+1, max)
				+ in.charAt(min)
				+ in.substring(max+1);
	}
	static String swapLetter(String in, String x, String y) {
		return swapPos(in, in.indexOf(x), in.indexOf(y));
	}
	static String rotate(String in, String dir, int steps) {
		boolean isLeft = dir.equals("left");
		if (steps < 0) { isLeft = !isLeft; steps = -steps; }
		for (int i = 0; i < steps; i++) {
			if (isLeft) {
				in = in.substring(1) + in.charAt(0);
			} else {
				in = in.charAt(in.length()-1) + in.substring(0, in.length()-1);
			}
		}
		return in;
	}
	static int calcRotateLetterSteps(String in, String letter) {
		int steps = in.indexOf(letter);
		if (steps >= 4) steps++;
		return steps+1;
	}
	static String rotateLetter(String in, String letter) {
		return rotate(in, "right", calcRotateLetterSteps(in, letter));
	}
	static String reverse(String in, int x, int y) {
		return in.substring(0, x)
				+ new StringBuilder(in.substring(x, y+1)).reverse().toString()
				+ in.substring(y+1);
	}
	static String move(String in, int x, int y) {
		char c = in.charAt(x);
		in = in.substring(0, x) + in.substring(x+1);
		in = in.substring(0, y) + c + in.substring(y);
		return in;
	}
	static String unrotateLetter(String in, String letter) {
		// Just rotate left until it matches...
		int steps = 0;
		do {
			in = rotate(in, "left", 1);
			steps++;
		} while (calcRotateLetterSteps(in, letter) != steps);
		return in;
	}

	private void scrambler(String name, String in, boolean invert) throws IOException {
		List<String> lines = ResourceUtil.readAllLines(name);
		if (invert) Collections.reverse(lines);
		for (String line : lines) {
			for (Action action : actions) {
				Matcher m = action.pattern.matcher(line);
				if (m.matches()) {
					BiFunction<Matcher, String, String> f = invert? action.inverse : action.function;
					in = f.apply(m, in);
					System.out.println(line + " -> " + in);
					break;
				}
			}
		}
	}

	public static void main(String[] args) throws Exception {
		Day21 worker = new Day21();
		// p1
		worker.scrambler("2016/day21.input", "abcdefgh", false);
		// p2
		System.out.println("---");
		worker.scrambler("2016/day21.input", "fbgdceah", true);
	}

}
