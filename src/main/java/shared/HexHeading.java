package shared;

/**
 * @author Dan Fielding
 */
public enum HexHeading {
	// Delta for even- and odd-columns.
	N   (new Position( 0, -1), new Position( 0, -1)),
	NE  (new Position( 1, -1), new Position( 1,  0)),
	SE  (new Position( 1,  0), new Position( 1,  1)),
	S   (new Position( 0,  1), new Position( 0,  1)),
	SW  (new Position(-1,  0), new Position(-1,  1)),
	NW  (new Position(-1, -1), new Position(-1,  0)),
	;

	private final Position deltaEven;
	private final Position deltaOdd;

	HexHeading(Position deltaEven, Position deltaOdd) {
		this.deltaEven = deltaEven;
		this.deltaOdd  = deltaOdd;
	}

	public static HexHeading fromString(String str) {
		for (HexHeading heading : values()) {
			if (heading.name().equalsIgnoreCase(str)) return heading;
		}
		return null;
	}

	public Position getDelta(Position pos) {
		return ((pos.x & 1) == 0)? deltaEven : deltaOdd;
	}

}
