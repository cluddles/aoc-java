package shared;

public enum Dir4 implements Dir<Dir4> {
	N( 0, -1),
	E( 1,  0),
	S( 0,  1),
	W(-1,  0),
	;

	public final IntVector2 step;

	Dir4(int x, int y) {
		step = new IntVector2(x, y);
	}

	@Override
	public Dir4[] allValues() {
		return Dir4.values();
	}

	@Override
	public IntVector2 getStep() {
		return step;
	}

}
