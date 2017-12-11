package shared;

/**
 * 	Assuming staggered columns - so hexes have horizontal edges, not vertical;
 * 	the top-edge of the map is wibbly-wobbly, and the left-edge is straight.
 *
 * @author Dan Fielding
 */
public class HexGridUtil {

	public static int distanceBetween(Position first, Position second) {
		int u1 = first.x;
		int u2 = second.x;
		int v1 = first.y;
		int v2 = second.y;
		// Unstagger columns
		// (Uglified a bit to cope with negative co-ords)
		v1 -= (u1 >= 0)? u1/2 : (u1-1)/2;
		v2 -= (u2 >= 0)? u2/2 : (u2-1)/2;
		int du = u2 - u1;
		int dv = v2 - v1;
		// This is correct assuming that (1,0) and (0,1) are adjacent
		// For a system where (0,0) and (1,1) are adjacent, use (du-dv)
		return (Math.abs(du) + Math.abs(dv) + Math.abs(du + dv))/2;
	}

}
