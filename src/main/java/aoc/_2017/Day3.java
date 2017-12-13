package aoc._2017;

import java.util.HashMap;
import java.util.Map;

import shared.Heading;
import shared.Position;
import shared.Test;

/**
 * @author Dan Fielding
 */
public class Day3 {

	public int evalPart1(int input) {
		System.out.println("Input " + input);
		// Rings end at 1, 9, 25, 49
		int ring = 0;
		int ringMax = 1;
		while (ringMax < input) {
			ring++;
			ringMax = ((ring * 2) + 1);
			ringMax = ringMax * ringMax;
		}
		System.out.println("  ring:   " + ring);
		System.out.println("  max:    " + ringMax);
		// Ring straights: ring 1 - 2, 4, 6, 8
		//                 ring 2 - 11, 15, 19, 23
		int ringEdge = (ring * 2) + 1;
		System.out.println("  edge:   " + ringEdge);
		int straight = ringMax - ringEdge/2;
		int offset = Math.abs(input - straight);
		for (int i = 0; i < 3; i++) {
			straight -= (ringEdge - 1);
			offset = Math.min(offset, Math.abs(input - straight));
		}
		System.out.println("  offset: " + offset);
		return ring + offset;
	}

	public int evalPart2(int input, int stopAfter) {
		Map<Position, Integer> map = new HashMap<>();
		int i = 0;
		Heading dir = Heading.E;
		int len = 0;
		int maxLen = 1;
		Position current = new Position(0, 0);
		int lastVal = 0;
		// right, up, left left, down down, right right right, ...
		while (i < input || (input == 0 && lastVal <= stopAfter)) {
			lastVal = Math.max(sumOfSurrounding(map, current), 1);
			map.put(current, lastVal);

			current = current.add(dir.getPos());
			len++;
			if (len == maxLen) {
				len = 0;
				dir = dir.turnLeft();
				if (dir == Heading.E || dir == Heading.W) maxLen++;
			}

			i++;
		}
		return lastVal;
	}

	public int sumOfSurrounding(Map<Position, Integer> map, Position current) {
		int result = 0;
		for (int i = -1; i <= 1; i++) {
			for (int j = -1; j <= 1; j++) {
				if (i == 0 && j == 0) continue;
				Position pos = current.add(new Position(i, j));
				Integer val = map.get(pos);
				result += (val == null? 0 : val);
			}
		}
		return result;
	}

	public void examples() {
		/*
		Data from square 1 is carried 0 steps, since it's at the access port.
		Data from square 12 is carried 3 steps, such as: down, left, left.
		Data from square 23 is carried only 2 steps: up twice.
		Data from square 1024 must be carried 31 steps.
		 */
		Test.assertEqual(evalPart1(1), 0);
		Test.assertEqual(evalPart1(2), 1);  // extra
		Test.assertEqual(evalPart1(11), 2); // extra
		Test.assertEqual(evalPart1(12), 3);
		Test.assertEqual(evalPart1(23), 2);
		Test.assertEqual(evalPart1(1024), 31);

		/*
		Square 1 starts with the value 1.
		Square 2 has only one adjacent filled square (with value 1), so it also stores 1.
		Square 3 has both of the above squares as neighbors and stores the sum of their values, 2.
		Square 4 has all three of the aforementioned squares as neighbors and stores the sum of their values, 4.
		Square 5 only has the first and fourth squares as neighbors, so it gets the value 5.
		 */
		Test.assertEqual(evalPart2(1, 0), 1);
		Test.assertEqual(evalPart2(2, 0), 1);
		Test.assertEqual(evalPart2(3, 0), 2);
		Test.assertEqual(evalPart2(4, 0), 4);
		Test.assertEqual(evalPart2(5, 0), 5);
		Test.assertEqual(evalPart2(0, 312051), 312453); // solution
	}

	public static void main(String[] args) {
		Day3 day = new Day3();
		day.examples();

		System.out.println(day.evalPart1(312051));
		System.out.println(day.evalPart2(0, 312051));
	}

}
