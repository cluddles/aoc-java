package aoc._2018;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.collect.Comparators;

import shared.Direction;
import shared.Grid;
import shared.IntVector2;
import shared.ResourceUtil;
import shared.Test;
import shared.Timer;

/**
 * @author Dan Fielding
 */
public class Day13 {
/*
--- Day 13: Mine Cart Madness ---

A crop of this size requires significant logistics to transport produce, soil,
fertilizer, and so on. The Elves are very busy pushing things around in carts on
some kind of rudimentary system of tracks they've come up with.

Seeing as how cart-and-track systems don't appear in recorded history for
another 1000 years, the Elves seem to be making this up as they go along. They
haven't even figured out how to avoid collisions yet.

You map out the tracks (your puzzle input) and see where you can help.

Tracks consist of straight paths (| and -), curves (/ and \), and intersections
(+). Curves connect exactly two perpendicular pieces of track; for example, this
is a closed loop:

/----\
|    |
|    |
\----/

Intersections occur when two perpendicular paths cross. At an intersection, a
cart is capable of turning left, turning right, or continuing straight. Here are
two loops connected by two intersections:

/-----\
|     |
|  /--+--\
|  |  |  |
\--+--/  |
   |     |
   \-----/

Several carts are also on the tracks. Carts always face either up (^), down (v),
left (<), or right (>). (On your initial map, the track under each cart is a
straight path matching the direction the cart is facing.)

Each time a cart has the option to turn (by arriving at any intersection), it
turns left the first time, goes straight the second time, turns right the third
time, and then repeats those directions starting again with left the fourth
time, straight the fifth time, and so on. This process is independent of the
particular intersection at which the cart has arrived - that is, the cart has no
per-intersection memory.

Carts all move at the same speed; they take turns moving a single step at a
time. They do this based on their current location: carts on the top row move
first (acting from left to right), then carts on the second row move
(again from left to right), then carts on the third row, and so on. Once each
cart has moved one step, the process repeats; each of these loops is called a
tick.

For example, suppose there are two carts on a straight track:

|  |  |  |  |
v  |  |  |  |
|  v  v  |  |
|  |  |  v  X
|  |  ^  ^  |
^  ^  |  |  |
|  |  |  |  |

First, the top cart moves. It is facing down (v), so it moves down one square.
Second, the bottom cart moves. It is facing up (^), so it moves up one square.
Because all carts have moved, the first tick ends.
Then, the process repeats, starting with the first cart.
The first cart moves down, then the second cart moves up - right into the first
cart, colliding with it! (The location of the crash is marked with an X.)
This ends the second and last tick.

Here is a longer example:

/->-\
|   |  /----\
| /-+--+-\  |
| | |  | v  |
\-+-/  \-+--/
  \------/

/-->\
|   |  /----\
| /-+--+-\  |
| | |  | |  |
\-+-/  \->--/
  \------/

/---v
|   |  /----\
| /-+--+-\  |
| | |  | |  |
\-+-/  \-+>-/
  \------/

/---\
|   v  /----\
| /-+--+-\  |
| | |  | |  |
\-+-/  \-+->/
  \------/

/---\
|   |  /----\
| /->--+-\  |
| | |  | |  |
\-+-/  \-+--^
  \------/

/---\
|   |  /----\
| /-+>-+-\  |
| | |  | |  ^
\-+-/  \-+--/
  \------/

/---\
|   |  /----\
| /-+->+-\  ^
| | |  | |  |
\-+-/  \-+--/
  \------/

/---\
|   |  /----<
| /-+-->-\  |
| | |  | |  |
\-+-/  \-+--/
  \------/

/---\
|   |  /---<\
| /-+--+>\  |
| | |  | |  |
\-+-/  \-+--/
  \------/

/---\
|   |  /--<-\
| /-+--+-v  |
| | |  | |  |
\-+-/  \-+--/
  \------/

/---\
|   |  /-<--\
| /-+--+-\  |
| | |  | v  |
\-+-/  \-+--/
  \------/

/---\
|   |  /<---\
| /-+--+-\  |
| | |  | |  |
\-+-/  \-<--/
  \------/

/---\
|   |  v----\
| /-+--+-\  |
| | |  | |  |
\-+-/  \<+--/
  \------/

/---\
|   |  /----\
| /-+--v-\  |
| | |  | |  |
\-+-/  ^-+--/
  \------/

/---\
|   |  /----\
| /-+--+-\  |
| | |  X |  |
\-+-/  \-+--/
  \------/

After following their respective paths for a while, the carts eventually crash.
To help prevent crashes, you'd like to know the location of the first crash.
Locations are given in X,Y coordinates, where the furthest left column is X=0
and the furthest top row is Y=0:

           111
 0123456789012
0/---\
1|   |  /----\
2| /-+--+-\  |
3| | |  X |  |
4\-+-/  \-+--/
5  \------/

In this example, the location of the first crash is 7,3.

--- Part Two ---

There isn't much you can do to prevent crashes in this ridiculous system.
However, by predicting the crashes, the Elves know where to be in advance and
instantly remove the two crashing carts the moment any crash occurs.

They can proceed like this for a while, but eventually, they're going to run out
of carts. It could be useful to figure out where the last cart that hasn't
crashed will end up.

For example:

/>-<\
|   |
| /<+-\
| | | v
\>+</ |
  |   ^
  \<->/

/---\
|   |
| v-+-\
| | | |
\-+-/ |
  |   |
  ^---^

/---\
|   |
| /-+-\
| v | |
\-+-/ |
  ^   ^
  \---/

/---\
|   |
| /-+-\
| | | |
\-+-/ ^
  |   |
  \---/

After four very expensive crashes, a tick ends with only one cart remaining;
its final location is 6,4.

What is the location of the last cart at the end of the first tick where it is
the only cart left?

*/

	static class Cart {
		IntVector2 pos;
		Direction  dir;
		int        junction;

		Cart(IntVector2 pos, Direction dir) {
			this.pos = pos;
			this.dir = dir;
		}
	}

	private Grid<Character> map;
	private Set<Cart>       carts = new HashSet<>();

	Day13(List<String> lines) {
		init(lines);
	}

	private void init(List<String> lines) {
		// IntelliJ has stripped the trailing spaces, so figure out real width
		// rather than trusting the first line
		int width = lines.stream().mapToInt(String::length).max().orElse(0);
		map = new Grid<>(width, lines.size());
		for (int y = 0; y < lines.size(); y++) {
			char[] line = lines.get(y).toCharArray();
			for (int x = 0; x < line.length; x++) {
				char c = line[x];
				// Look for minecart, place track under it
				Direction dir = null;
				switch (c) {
				case '<': c = '-'; dir = Direction.W; break;
				case '>': c = '-'; dir = Direction.E; break;
				case '^': c = '|'; dir = Direction.N; break;
				case 'v': c = '|'; dir = Direction.S; break;
				}
				if (dir != null) {
					// New cart
					carts.add(new Cart(new IntVector2(x, y), dir));
				}
				// Map tile
				if (c != ' ') map.set(x, y, c);
			}
		}
	}

	private IntVector2 tick() {
		// Move carts, top-left first
		List<Cart> moveOrder = carts.stream()
				.sorted((c1, c2) -> {
						if (c1.pos.y != c2.pos.y) return c1.pos.y - c2.pos.y;
						return c1.pos.x - c2.pos.x;
				})
				.collect(Collectors.toList());
		IntVector2 collisionPos = null;
		for (Cart cart : moveOrder) {
			// Skip carts that have been removed already
			if (!carts.contains(cart)) continue;
			// Move cart
			cart.pos = cart.pos.add(cart.dir.step);
			// Check for collision
			for (Cart other : carts) {
				if (cart != other && cart.pos.equals(other.pos)) {
					// Remember collision position
					if (collisionPos == null) collisionPos = cart.pos;
					// Update stuff
					carts.remove(cart);
					carts.remove(other);
					break;
				}
			}
			// Check for dir change
			cart.dir = cartDir(cart);
		}
		return collisionPos;
	}

	private Direction cartDir(Cart cart) {
		Character c   = map.get(cart.pos);
		Direction dir = cart.dir;
		switch (c) {
		case '/':
			if (cart.dir == Direction.N || cart.dir == Direction.S) return dir.turnRight();
			return dir.turnLeft();
		case '\\':
			if (cart.dir == Direction.N || cart.dir == Direction.S) return dir.turnLeft();
			return dir.turnRight();
		case '+':
			int i = cart.junction;
			cart.junction = (i + 1) % 3;
			switch (i) {
			case 0:  return dir.turnLeft();
			case 2:  return dir.turnRight();
			default: return dir;
			}
		default:
			return cart.dir;
		}
	}

	public IntVector2 part1() {
		IntVector2 result = null;
		while (result == null) { result = tick(); }
		return result;
	}

	public IntVector2 part2() {
		while (carts.size() > 1) { tick(); }
		return carts.iterator().next().pos;
	}

	public static void main(String[] args) throws Exception {
		List<String> example  = ResourceUtil.readAllLines("2018/day13.example");
		List<String> example2 = ResourceUtil.readAllLines("2018/day13.example2");
		List<String> input    = ResourceUtil.readAllLines("2018/day13.input");

		// Part 1
		// Example
		Test.check(new Day13(example).part1(), new IntVector2(7, 3));
		// Vs Input
		Timer.startMessage();
		System.out.println(new Day13(input).part1());
		Timer.endMessage();

		// Part 2
		// Example
		Test.check(new Day13(example2).part2(), new IntVector2(6, 4));
		// Vs Input
		Timer.startMessage();
		System.out.println(new Day13(input).part2());
		Timer.endMessage();
	}

}
