package aoc._2017;

import com.google.common.base.Strings;

import shared.Test;

/**
 * @author Dan Fielding
 */
public class Day14 {

	/*

	--- Day 14: Disk Defragmentation ---

	Suddenly, a scheduled job activates the system's disk defragmenter. Were the
	situation different, you might sit and watch it for a while, but today, you
	just don't have that kind of time. It's soaking up valuable system resources
	that are needed elsewhere, and so the only option is to help it finish its
	task as soon as possible.

	The disk in question consists of a 128x128 grid; each square of the grid is
	either free or used. On this disk, the state of the grid is tracked by the
	bits in a sequence of knot hashes.

	A total of 128 knot hashes are calculated, each corresponding to a single
	row in the grid; each hash contains 128 bits which correspond to individual
	grid squares. Each bit of a hash indicates whether that square is free (0)
	or used (1).

	The hash inputs are a key string (your puzzle input), a dash, and a number
	from 0 to 127 corresponding to the row. For example, if your key string were
	flqrgnkx, then the first row would be given by the bits of the knot hash of
	flqrgnkx-0, the second row from the bits of the knot hash of flqrgnkx-1, and
	so on until the last row, flqrgnkx-127.

	The output of a knot hash is traditionally represented by 32 hexadecimal
	digits; each of these digits correspond to 4 bits, for a total of
	4 * 32 = 128 bits. To convert to bits, turn each hexadecimal digit to its
	equivalent binary value, high-bit first: 0 becomes 0000, 1 becomes 0001, e
	becomes 1110, f becomes 1111, and so on; a hash that begins with a0c2017...
	in hexadecimal would begin with 10100000110000100000000101110000... in
	binary.

	Continuing this process, the first 8 rows and columns for key flqrgnkx
	appear as follows, using # to denote used squares, and . to denote free
	ones:

	##.#.#..-->
	.#.#.#.#
	....#.#.
	#.#.##.#
	.##.#...
	##..#..#
	.#...#..
	##.#.##.-->
	|      |
	V      V

	In this example, 8108 squares are used across the entire 128x128 grid.

	Given your actual key string, how many squares are used?


	--- Part Two ---

	Now, all the defragmenter needs to know is the number of regions. A region
	is a group of used squares that are all adjacent, not including diagonals.
	Every used square is in exactly one region: lone used squares form their own
	isolated regions, while several adjacent squares all count as a single
	region.

	In the example above, the following nine regions are visible, each marked
	with a distinct digit:

	11.2.3..-->
	.1.2.3.4
	....5.6.
	7.8.55.9
	.88.5...
	88..5..8
	.8...8..
	88.8.88.-->
	|      |
	V      V

	Of particular interest is the region marked 8; while it does not appear
	contiguous in this small view, all of the squares marked 8 are connected
	when considering the whole 128x128 grid. In total, in this example, 1242
	regions are present.

	How many regions are present given your key string?

	*/

	private Day10 hasher = new Day10();
	private final int GRID_W = 128;
	private final int GRID_H = 128;

	public String toBinaryString(String hashed) {
		StringBuilder result = new StringBuilder();
		for (int i = 0; i < hashed.length(); i += 8) {
			long val = Long.valueOf(String.valueOf(hashed.substring(i, i + 8)), 16);
			result.append(Strings.padStart(Long.toBinaryString(val), 32, '0'));
		}
		return result.toString();
	}

	public int[][] toGrid(String input) {
		int[][] grid = new int[GRID_W][GRID_H];
		for (int j = 0; j < GRID_H; j++) {
			char[] bits = toBinaryString(hasher.hash(input + "-" + j)).toCharArray();
			for (int i = 0; i < GRID_W; i++) {
				if (bits[i] == '1') grid[i][j] = -1;
			}
		}
		return grid;
	}

	public int countUsedBitsInGrid(String input) {
		int[][] grid = toGrid(input);
		int used = 0;
		for (int i = 0; i < GRID_W; i++) {
			for (int j = 0; j < GRID_H; j++) {
				if (grid[i][j] != 0) used++;
			}
		}
		return used;
	}

	private void fill(int[][] grid, int x, int y, int regionNum) {
		if (x < 0 || y < 0 || x >= GRID_W || y >= GRID_H) return;
		if (grid[x][y] != -1) return;
		grid[x][y] = regionNum;
		fill(grid, x - 1, y, regionNum);
		fill(grid, x + 1, y, regionNum);
		fill(grid, x, y - 1, regionNum);
		fill(grid, x, y + 1, regionNum);
	}

	public int countRegions(String input) {
		int[][] grid = toGrid(input);
		int regionNum = 1;
		for (int i = 0; i < GRID_W; i++) {
			for (int j = 0; j < GRID_H; j++) {
				if (grid[i][j] == -1) {
					fill(grid, i, j, regionNum);
					regionNum++;
				}
			}
		}
		return regionNum-1;
	}

	public void examples() {
		Test.assertEqual(countUsedBitsInGrid("flqrgnkx"), 8108);
		Test.assertEqual(countRegions("flqrgnkx"), 1242);
	}

	public static void main(String[] args) {
		Day14 day = new Day14();
		day.examples();

		String input = "wenycdww";
		System.out.println(day.countUsedBitsInGrid(input));
		System.out.println(day.countRegions(input));
	}

}
