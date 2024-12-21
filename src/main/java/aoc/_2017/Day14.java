package aoc._2017;

import com.google.common.base.Strings;

import shared.Test;

public class Day14 {

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
		Test.check(countUsedBitsInGrid("flqrgnkx"), 8108);
		Test.check(countRegions("flqrgnkx"), 1242);
	}

	public static void main(String[] args) {
		Day14 day = new Day14();
		day.examples();

		String input = "wenycdww";
		System.out.println(day.countUsedBitsInGrid(input));
		System.out.println(day.countRegions(input));
	}

}
