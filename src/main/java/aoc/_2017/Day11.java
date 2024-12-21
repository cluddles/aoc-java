package aoc._2017;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import shared.HexDirection;
import shared.HexUtil;
import shared.IntVector3;
import shared.ResourceUtil;
import shared.Test;

public class Day11 {

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
