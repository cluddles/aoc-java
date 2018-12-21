package aoc._2016;

import java.util.List;

import aoc._2016.common.Assembunny;
import shared.ResourceUtil;

public class Day25 {
/*
--- Day 25: Clock Signal ---

You open the door and find yourself on the roof. The city sprawls away from you
for miles and miles.

There's not much time now - it's already Christmas, but you're nowhere near the
North Pole, much too far to deliver these stars to the sleigh in time.

However, maybe the huge antenna up here can offer a solution. After all, the
sleigh doesn't need the stars, exactly; it needs the timing data they provide,
and you happen to have a massive signal generator right here.

You connect the stars you have to your prototype computer, connect that to the
antenna, and begin the transmission.

Nothing happens.

You call the service number printed on the side of the antenna and quickly
explain the situation. "I'm not sure what kind of equipment you have connected
over there," he says, "but you need a clock signal." You try to explain that this
is a signal for a clock.

"No, no, a clock signal - timing information so the antenna computer knows how
to read the data you're sending it. An endless, alternating pattern of
0, 1, 0, 1, 0, 1, 0, 1, 0, 1...." He trails off.

You ask if the antenna can handle a clock signal at the frequency you would need
to use for the data from the stars. "There's no way it can! The only antenna
we've installed capable of that is on top of a top-secret Easter Bunny
installation, and you're definitely not-" You hang up the phone.

You've extracted the antenna's clock signal generation assembunny code (your
puzzle input); it looks mostly compatible with code you worked on just recently.

This antenna code, being a signal generator, uses one extra instruction:

    out x transmits x (either an integer or the value of a register) as the next
        value for the clock signal.

The code takes a value (via register a) that describes the signal to generate,
but you're not sure how it's used. You'll have to find the input to produce the
right signal through experimentation.

What is the lowest positive integer that can be used to initialize register a
and cause the code to output a clock signal of 0, 1, 0, 1... repeating forever?

 */

/*
Analysis with some help from the subreddit:

d = a
c = 7
do {
  b = 362
  do {
    d++
    b--
  } while (b != 0)
  c--
} while (c != 0)


// Looks like 362 * 7 (=2534) loops of the top block
d = a + 2534

mainLoop:
while (true) {
  a = d
  while (a != 0) {
    blockB;
    blockC;
  }
}

  blockB:
    b = a
    a = 0
    c = 2
    do {
      if (b == 0) jump blockC;
      b--
      c--
    } while (c != 0);
    a++

// This is a = a/2, c = a%2 (or maybe 2-a%2)

  blockC:
    b = 2
    while (true) {
      if (c == 0) break;
      b--
      c--
    }
    out b

// 2-2-a%2, so a%2

// So something like
d = a + 2534;
while (true) {
  a = d;
  while (a != 0) {
    a = a/2;
    b = a%2;
    out b
  }
}

// Just running the damn program...
// For a = 0, d = 2534 and you end up with output 011001111001011001111001...
// Which is 2534, least significant bit first, repeated: 011001111001 011001111001 011001111001...
// So we want something where a + 2534 -> 0101010...
// 2645 is 1010 0101 0101, oops - nearly
// 2730 is 1010 1010 1010 -- that'll do, pig
// Try a = 2730 - 2534 = 196
 */


	public void evalPart1() throws InterruptedException {
		int a = 196;
		int b = 0;
		int d = a + 2534;
		while (true) {
			a = d;
			while (a != 0) {
				b = a % 2;
				a = a / 2;
				Thread.sleep(50);
				System.out.println(b);
			}
		}
	}

	public static void main(String[] args) throws Exception {
		new Day25().evalPart1();

//		List<String> lines = ResourceUtil.readAllLines("2016/day25.input");
//		Assembunny worker = new Assembunny(lines);
//
//		// Part 1
//		worker.execute();
	}

}
