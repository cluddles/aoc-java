package aoc._2018;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import shared.Dir4;
import shared.Grid;
import shared.IntVector2;
import shared.ResourceUtil;
import shared.Test;
import shared.Timer;

public class Day13 {

	static class Cart {
		IntVector2 pos;
		Dir4 dir;
		int        junction;

		Cart(IntVector2 pos, Dir4 dir) {
			this.pos = pos;
			this.dir = dir;
		}
		IntVector2 getPos() {
			return pos;
		}
	}

	private Grid<Character> map;
	private Set<Cart>       carts = new HashSet<>();

	private static final Comparator<Cart> CART_ORDER = Comparator
			.comparing(Cart::getPos, IntVector2.READING_ORDER);

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
				Dir4 dir = null;
				switch (c) {
				case '<': c = '-'; dir = Dir4.W; break;
				case '>': c = '-'; dir = Dir4.E; break;
				case '^': c = '|'; dir = Dir4.N; break;
				case 'v': c = '|'; dir = Dir4.S; break;
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
				.sorted(CART_ORDER)
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

	private Dir4 cartDir(Cart cart) {
		Character c   = map.get(cart.pos);
		Dir4 dir = cart.dir;
		switch (c) {
		case '/':
			if (cart.dir == Dir4.N || cart.dir == Dir4.S) return dir.rotateClockwise();
			return dir.rotateAntiClockwise();
		case '\\':
			if (cart.dir == Dir4.N || cart.dir == Dir4.S) return dir.rotateAntiClockwise();
			return dir.rotateClockwise();
		case '+':
			int i = cart.junction;
			cart.junction = (i + 1) % 3;
			switch (i) {
			case 0:  return dir.rotateAntiClockwise();
			case 2:  return dir.rotateClockwise();
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
