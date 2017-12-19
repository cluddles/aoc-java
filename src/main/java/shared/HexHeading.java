package shared;

/**
 * @author Dan Fielding
 */
public enum HexHeading {
	// Delta for even- and odd-columns.
	N   (new IntVector2( 0, -1), new IntVector2( 0, -1)),
	NE  (new IntVector2( 1, -1), new IntVector2( 1,  0)),
	SE  (new IntVector2( 1,  0), new IntVector2( 1,  1)),
	S   (new IntVector2( 0,  1), new IntVector2( 0,  1)),
	SW  (new IntVector2(-1,  0), new IntVector2(-1,  1)),
	NW  (new IntVector2(-1, -1), new IntVector2(-1,  0)),
	;

	private final IntVector2 deltaEven;
	private final IntVector2 deltaOdd;

	HexHeading(IntVector2 deltaEven, IntVector2 deltaOdd) {
		this.deltaEven = deltaEven;
		this.deltaOdd  = deltaOdd;
	}

	public static HexHeading fromString(String str) {
		for (HexHeading heading : values()) {
			if (heading.name().equalsIgnoreCase(str)) return heading;
		}
		return null;
	}

	public IntVector2 getDelta(IntVector2 pos) {
		return ((pos.x & 1) == 0)? deltaEven : deltaOdd;
	}

}
