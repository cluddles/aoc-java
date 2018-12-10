package shared;

/**
 * @author Dan Fielding
 */
public class Rect {
	public final IntVector2 start;
	public final IntVector2 size;

	public Rect(IntVector2 start, IntVector2 size) {
		this.start = start;
		this.size = size;
	}

	public IntVector2 getStart() {
		return start;
	}
	public IntVector2 getSize() {
		return size;
	}

	public long area() {
		return (long) size.x * size.y;
	}

}

