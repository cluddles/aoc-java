package shared;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

/**
 * @author Dan Fielding
 */
public class IntVector2 {

	public final int x, y;

	public IntVector2(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public IntVector2 add(IntVector2 other) {
		return new IntVector2(x + other.x, y + other.y);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		IntVector2 position = (IntVector2) o;
		return x == position.x && y == position.y;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(x, y);
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
				.add("x", x)
				.add("y", y)
				.toString();
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int manhattanDistance(IntVector2 other) {
		return Math.abs(x - other.x) + Math.abs(y - other.y);
	}

}
