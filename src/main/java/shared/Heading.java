package shared;

/**
 * @author Dan Fielding
 */
public enum Heading {
	N( 0, -1),
	E( 1,  0),
	S( 0,  1),
	W(-1,  0),
	;

	private final IntVector2 step;

	Heading(int x, int y) {
		step = new IntVector2(x, y);
	}

	public Heading turnLeft() {
		switch(this) {
			case N: return W;
			case E: return N;
			case S: return E;
			case W: return S;
		}
		throw new AssertionError("Unhandled heading: " + this);
	}

	public Heading turnRight() {
		switch(this) {
			case N: return E;
			case E: return S;
			case S: return W;
			case W: return N;
		}
		throw new AssertionError("Unhandled heading: " + this);
	}

	public Heading opposite() {
		switch (this) {
			case N: return S;
			case S: return N;
			case E: return W;
			case W: return E;
		}
		throw new AssertionError("Unhandled heading: " + this);
	}

	public int getX() {
		return step.x;
	}

	public int getY() {
		return step.y;
	}

	public IntVector2 getStep() {
		return step;
	}

}