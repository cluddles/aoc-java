package shared;

/**
 * See https://www.redblobgames.com/grids/hexagons/
 */
public class HexUtil {

	// Axial constructor
	public static IntVector3 fromAxial(int x, int y) {
		return new IntVector3(x, y, -x - y);
	}

	public static int distance(IntVector3 first, IntVector3 second) {
		return  ( Math.abs(first.x - second.x)
				+ Math.abs(first.y - second.y)
				+ Math.abs(first.z - second.z) ) / 2;
	}

}
