package aoc._2017;

import shared.Test;
import shared.Timer;

public class Day17 {

	class Pos {
		final int val;
		Pos next;

		Pos(int val, Pos next) {
			this.val = val;
			this.next = next;
		}
	}

	public Pos spin(int val, int steps, Pos current) {
		for (int i = 0; i < steps; i++) {
			current = current.next;
		}
		// Insert
		Pos newPos = new Pos(val+1, current.next);
		current.next = newPos;
		return newPos;
	}

	public int partOne(int input) {
		Pos first = new Pos(0, null);
		first.next = first;
		Pos current = first;
		for (int i = 0; i < 2017; i++) {
			current = spin(i, input, current);
		}
		return current.next.val;
	}

	public int partTwo(int input) {
		// Brute force!
		Pos first = new Pos(0, null);
		first.next = first;
		Pos current = first;
		for (int i = 0; i < 50_000_000; i++) {
			current = spin(i, input, current);
			if (i % 10000 == 0) {
				System.out.println("Working... " + i);
			}
		}
		return first.next.val;
	}

	public void examples() {
		Test.check(partOne(3), 638);
	}

	public static void main(String[] args) {
		Day17 day = new Day17();
		day.examples();

		Timer.start();
		System.out.println(day.partOne(355));
		Timer.endMessage();

		Timer.start();
		System.out.println(day.partTwo(355));
		Timer.endMessage();
	}

}
