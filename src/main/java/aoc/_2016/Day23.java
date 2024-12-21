package aoc._2016;

import java.util.List;

import aoc._2016.common.Assembunny;
import shared.ResourceUtil;

public class Day23 {

	public static void main(String[] args) throws Exception {
		List<String> lines = ResourceUtil.readAllLines("2016/day23.input");
		Assembunny worker = new Assembunny(lines);

		// Part 1
//		worker.setRegister("a", 7);

		// Part 2
		worker.setDay23Part2HackEnabled(true);
		worker.setRegister("a", 12);

		worker.execute();
	}

}
