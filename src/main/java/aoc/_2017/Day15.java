package aoc._2017;

import java.util.Iterator;
import java.util.stream.Stream;

import com.google.common.collect.Streams;

import shared.Test;

public class Day15 {

	private static final int DIVISOR = 2147483647;

	class Generator implements Iterator<Integer> {
		long current;
		final int factor;
		final int multiple;
		public Generator(int current, int factor) {
			this(current, factor, -1);
		}
		public Generator(int current, int factor, int multiple) {
			this.current  = current;
			this.factor   = factor;
			this.multiple = multiple;
		}
		@Override
		public boolean hasNext() {
			return true;
		}
		@Override
		public Integer next() {
			do {
				current = (current * factor) % DIVISOR;
			} while (multiple != -1 && current % multiple != 0);
			return (int) current;
		}
	}

	class IntPair {
		final int a, b;
		IntPair(int a, int b) {
			this.a = a;
			this.b = b;
		}
	}

	public boolean match(int a, int b) {
		return (a & 0xffff) == (b & 0xffff);
	}

	public int totalPairs(Generator a, Generator b, int runs) {
		Stream<IntPair> zipped = Streams.zip(Streams.stream(a), Streams.stream(b), IntPair::new);
		return (int) zipped
				.limit(runs)
				.filter(pair -> match(pair.a, pair.b))
				.count();
	}

	public int partOne(int genA, int genB) {
		return totalPairs(
				new Generator(genA, 16807),
				new Generator(genB, 48271),
				40_000_000);
	}

	public int partTwo(int genA, int genB) {
		return totalPairs(
				new Generator(genA, 16807, 4),
				new Generator(genB, 48271, 8),
				5_000_000);
	}

	public void examples() {
		Test.check(match(   1092455,  430625591), false);
		Test.check(match(1181022009, 1233683848), false);
		Test.check(match( 245556042, 1431495498), true);
		Test.check(match(1744312007,  137874439), false);
		Test.check(match(1352636452,  285222916), false);

//		Test.check(partOne(65, 8921), 588);

		Test.check(partTwo(65, 8921), 309);
	}

	public static void main(String[] args) {
		Day15 day = new Day15();
		day.examples();

//		System.out.println(day.partOne(618, 814));

		System.out.println(day.partTwo(618, 814));
	}

}
