package shared;

import java.util.Comparator;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

public class IntVector2 {

	public static final Comparator<IntVector2> READING_ORDER = Comparator
			.comparing(IntVector2::getY)
			.thenComparing(IntVector2::getX);

	public static final IntVector2 ZERO = new IntVector2(0, 0);
	public static final IntVector2 ONE  = new IntVector2(1, 1);

	public final int x, y;

	public IntVector2(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public IntVector2 add(IntVector2 other) {
		return new IntVector2(x + other.x, y + other.y);
	}
	public IntVector2 subtract(IntVector2 other) {
		return new IntVector2(x - other.x, y - other.y);
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
