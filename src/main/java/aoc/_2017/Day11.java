package aoc._2017;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import shared.HexUtil;
import shared.HexDirection;
import shared.IntVector3;
import shared.ResourceUtil;
import shared.Test;

/**
 * @author Dan Fielding
 */
public class Day11 {

	/*

	--- Day 11: Hex Ed ---

	Crossing the bridge, you've barely reached the other side of the stream when
	a program comes up to you, clearly in distress. "It's my child process," she
	says, "he's gotten lost in an infinite grid!"

	Fortunately for her, you have plenty of experience with infinite grids.

	Unfortunately for you, it's a hex grid.

	The hexagons ("hexes") in this grid are aligned such that adjacent hexes can
	be found to the north, northeast, southeast, south, southwest, and
	northwest:

	  \ n  /
	nw +--+ ne
	  /    \
	-+      +-
	  \    /
	sw +--+ se
	  / s  \

	You have the path the child process took. Starting where he started, you
	need to determine the fewest number of steps required to reach him. (A
	"step" means to move from the hex you are in to any adjacent hex.)

	For example:

	- ne,ne,ne is 3 steps away.
	- ne,ne,sw,sw is 0 steps away (back where you started).
	- ne,ne,s,s is 2 steps away (se,se).
	- se,sw,se,sw,sw is 3 steps away (s,s,sw).

	--- Part Two ---

	How many steps away is the furthest he ever got from his starting position?

	*/

	class Result {
		final int finalDist;
		final int maxDist;
		Result(int finalDist, int maxDist) {
			this.finalDist = finalDist;
			this.maxDist = maxDist;
		}
	}

	public Result distance(String input) {
		List<HexDirection> moves = Arrays.stream(input.split(","))
				.map(dir -> HexDirection.valueOf(dir.toUpperCase()))
				.collect(Collectors.toList());

		IntVector3 origin  = IntVector3.ZERO;
		IntVector3 pos     = origin;
		int maxDist = 0;
		for (HexDirection move : moves) {
			pos = pos.add(move.getStep());
			maxDist = Math.max(maxDist, HexUtil.distance(origin, pos));
		}
		return new Result(HexUtil.distance(origin, pos), maxDist);
	}

	public void examples() {
		Test.check(distance("ne,ne,ne").finalDist, 3);
		Test.check(distance("ne,ne,sw,sw").finalDist, 0);
		Test.check(distance("ne,ne,s,s").finalDist, 2);
		Test.check(distance("se,sw,se,sw,sw").finalDist, 3);

		Test.check(distance("sw,se,sw,se,se").finalDist, 3);
	}

	public static void main(String[] args) throws Exception {
		Day11 day = new Day11();
		day.examples();

		String input = ResourceUtil.readString("2017/day11.input");
		Result result = day.distance(input);
		System.out.println(result.finalDist);
		System.out.println(result.maxDist);
	}

}
