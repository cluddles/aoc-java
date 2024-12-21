package aoc._2017;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableList;

import shared.ResourceUtil;
import shared.Test;
import shared.Timer;

public class Day13 {

	public int scannerPos(int depth, int tick) {
		// 1 -> 0
		if (depth <= 1) return 0;
		// 2 -> 0, 1
		// 3 -> 0, 1, 2, 1
		// 4 -> 0, 1, 2, 3, 2, 1
		int result = tick % ((depth - 1) * 2);
		if (result >= depth) {
			// 3 -> 0, 1, 2, 3
			//  - > 0, 1, 2, 1
			// 4 -> 0, 1, 2, 3, 4, 5
			//   -> 0, 1, 2, 3, 2, 1
			result = depth * 2 - result - 2;
		}
		return result;
	}

	public Map<Integer, Integer> parseLayers(List<String> input) {
		// Scanner position, depth
		Map<Integer, Integer> result = new HashMap<>();
		for (String line : input) {
			String[] split = line.split(":");
			result.put(Integer.valueOf(split[0]), Integer.valueOf(split[1].trim()));
		}
		return result;
	}

	private int cost(
			Map<Integer, Integer> layers,
			int startTick,
			boolean failImmediately) {
		int cost = 0;
		for (Map.Entry<Integer, Integer> entry : layers.entrySet()) {
			int pos = entry.getKey();
			int depth = entry.getValue();
			if (depth >= 0 && scannerPos(depth, pos + startTick) == 0) {
				if (failImmediately) return 1;
				cost += pos * depth;
			}
		}
		return cost;
	}

	public int cost(List<String> input) {
		Map<Integer, Integer> layers = parseLayers(input);
		return cost(layers, 0, false);
	}

	public int delay(List<String> input) {
		Map<Integer, Integer> layers = parseLayers(input);
		int delay = 0;
		while (cost(layers, delay, true) > 0) {
			delay++;
		}
		return delay;
	}

	public void examples() {
		Test.check(scannerPos(2, 0), 0);
		Test.check(scannerPos(2, 1), 1);
		Test.check(scannerPos(2, 2), 0);

		Test.check(scannerPos(3, 0), 0);
		Test.check(scannerPos(3, 1), 1);
		Test.check(scannerPos(3, 2), 2);
		Test.check(scannerPos(3, 3), 1);
		Test.check(scannerPos(3, 4), 0);

		Test.check(scannerPos(4, 0), 0);
		Test.check(scannerPos(4, 1), 1);
		Test.check(scannerPos(4, 2), 2);
		Test.check(scannerPos(4, 3), 3);
		Test.check(scannerPos(4, 4), 2);
		Test.check(scannerPos(4, 5), 1);
		Test.check(scannerPos(4, 6), 0);

		List<String> input = ImmutableList.of(
				"0: 3",
				"1: 2",
				"4: 4",
				"6: 4");
		Test.check(cost (input), 24);
		Test.check(delay(input), 10);
	}

	public static void main(String[] args) throws Exception {
		Day13 day = new Day13();
		day.examples();

		Timer.start();
		List<String> input = ResourceUtil.readAllLines("2017/day13.input");
		System.out.println(day.cost (input));
		System.out.println(day.delay(input));
		System.out.println(Timer.end());
	}

}
