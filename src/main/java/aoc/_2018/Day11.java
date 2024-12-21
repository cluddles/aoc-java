package aoc._2018;

import shared.IntVector2;
import shared.IntVector3;
import shared.Test;
import shared.Timer;

public class Day11 {

	private static final int GRID_SIZE = 300;

	private int cellPowerLevel(IntVector2 cellPos, int serial) {
		// Find the fuel cell's rack ID, which is its X coordinate plus 10.
		long rackId = cellPos.getX() + 10L;
		// Begin with a power level of the rack ID times the Y coordinate.
		long powerLevel = rackId * cellPos.getY();
		// Increase the power level by the value of the grid serial number
		powerLevel += serial;
		// Set the power level to itself multiplied by the rack ID.
		powerLevel *= rackId;
		// Keep only the hundreds digit of the power level (so 12345 becomes 3
		if (powerLevel < 100) {
			powerLevel = 0;
		} else {
			// Guys, this is dumb
			String str = String.valueOf(powerLevel);
			powerLevel = Long.valueOf(str.substring(str.length()-3, str.length()-2));
		}
		// Subtract 5 from the power level.
		powerLevel -= 5;
		return (int) powerLevel;
	}

	private int calculateArea(int[][] powerLevels, int x, int y, int size) {
		if (x >= GRID_SIZE-size || y >= GRID_SIZE-size) return Integer.MIN_VALUE;
		int result = 0;
		for (int i = x; i < x+size; i++) {
			for (int j = y; j < y+size; j++) {
				result += powerLevels[i][j];
			}
		}
		return result;
	}

	public IntVector2 evalPart1(int serial) {
		int[][]    powerLevels = new int[GRID_SIZE][GRID_SIZE];
		int        max         = Integer.MIN_VALUE;
		IntVector2 result      = IntVector2.ZERO;
		// Bottom right to top left so we can calculate cells and areas all-in-one
		for (int i = GRID_SIZE-1; i >= 0; i--) {
			for (int j = GRID_SIZE-1; j >= 0; j--) {
				IntVector2 cellPos = new IntVector2(i + 1, j + 1);
				powerLevels[i][j] = cellPowerLevel(cellPos, serial);
				int sum = calculateArea(powerLevels, i, j, 3);
				if (sum > max) {
					max = sum;
					result = cellPos;
				}
			}
		}
		return result;
	}

	// Returns best k (which is best size - 1)
	private int populateAreas(int[][][] powerLevels,
			IntVector2 cellPos,
			int serial,
			int x,
			int y) {
		// Since we're storing all the areas now, we can re-use ones we worked
		// out earlier - we just need to add the new (top, left) edges to them.
		int max  = Integer.MIN_VALUE;
		int maxK = 0;
		int lastEdge = 0;
		for (int k = 0; k < Math.min(GRID_SIZE - x, GRID_SIZE - y); k++) {
			int val;
			if (k == 0) {
				// Size 0 (well, 1, but 0-index and all)
				val = cellPowerLevel(cellPos, serial);
				lastEdge = val;
			} else {
				// Otherwise: size k = cell down-right's size k-1 + edges
				lastEdge = lastEdge +
						powerLevels[x]  [y+k][0] +
						powerLevels[x+k][y]  [0];
				val = lastEdge +
						powerLevels[x+1][y+1][k-1];
			}
			// Store it (quite important)
			powerLevels[x][y][k] = val;
			// Remember the max
			if (val > max) {
				max  = val;
				maxK = k;
			}
		}
		return maxK;
	}

	// Brute forcing part 2 works, but is slow (~30s)
	// Re-using previously calculated areas cuts it down by 100x or so
	public IntVector3 evalPart2(int serial) {
		// x, y, size
		int[][][]  powerLevels = new int[GRID_SIZE][GRID_SIZE][GRID_SIZE];
		int        max         = Integer.MIN_VALUE;
		IntVector3 result      = IntVector3.ZERO;
		for (int i = GRID_SIZE-1; i >= 0; i--) {
			for (int j = GRID_SIZE-1; j >= 0; j--) {
				IntVector2 cellPos = new IntVector2(i + 1, j + 1);
				int bestK = populateAreas(powerLevels, cellPos, serial, i, j);
				int best  = powerLevels[i][j][bestK];
				if (best > max) {
					max    = best;
					result = new IntVector3(i + 1, j + 1, bestK + 1);
				}
			}
		}
		return result;
	}

	public static void main(String[] args) {
		Day11 solver = new Day11();
		int input = 1788;

		// Part 1
		// Test power level calculations
		Test.check(solver.cellPowerLevel(new IntVector2(  3,   5),  8),  4);
		Test.check(solver.cellPowerLevel(new IntVector2(122,  79), 57), -5);
		Test.check(solver.cellPowerLevel(new IntVector2(217, 196), 39),  0);
		Test.check(solver.cellPowerLevel(new IntVector2(101, 153), 71),  4);
		// Examples
		Test.check(solver.evalPart1(18), new IntVector2(33, 45));
		Test.check(solver.evalPart1(42), new IntVector2(21, 61));
		// Vs Input
		Timer.startMessage();
		System.out.println(solver.evalPart1(input));
		Timer.endMessage();

		// Part 2
		// Examples
		Test.check(solver.evalPart2(18), new IntVector3( 90, 269, 16));
		Test.check(solver.evalPart2(42), new IntVector3(232, 251, 12));
		// Vs Input
		Timer.startMessage();
		System.out.println(solver.evalPart2(input));
		Timer.endMessage();
	}
}
