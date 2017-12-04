package shared;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

/**
 * @author Dan Fielding
 */
public class Position {

	public final int x, y;

	public Position(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public Position add(Position other) {
		return new Position(x + other.x, y + other.y);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Position position = (Position) o;
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

	public int manhattanDistance(Position other) {
		return Math.abs(x - other.x) + Math.abs(y - other.y);
	}

}
