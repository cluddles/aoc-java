package shared;

public enum Dir8 implements Dir<Dir8> {
	N ( 0, -1),
	NE( 1, -1),
	E ( 1,  0),
	SE( 1,  1),
	S ( 0,  1),
	SW(-1,  1),
	W (-1,  0),
	NW(-1, -1),
	;

	public final IntVector2 step;

	Dir8(int x, int y) {
		step = new IntVector2(x, y);
	}

	@Override
	public Dir8[] allValues() {
		return Dir8.values();
	}

	@Override
	public IntVector2 getStep() {
		return step;
	}

}
