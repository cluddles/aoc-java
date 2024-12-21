package shared;

public enum HexDirection {
	N   (new IntVector3( 0,  1, -1)),
	NE  (new IntVector3( 1,  0, -1)),
	SE  (new IntVector3( 1, -1,  0)),
	S   (new IntVector3( 0, -1,  1)),
	SW  (new IntVector3(-1,  0,  1)),
	NW  (new IntVector3(-1,  1,  0)),
	;

	public final IntVector3 step;

	HexDirection(IntVector3 step) {
		this.step = step;
	}

	public IntVector3 getStep() {
		return step;
	}

}
