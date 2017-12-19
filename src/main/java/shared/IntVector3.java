package shared;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

/**
 * @author Dan Fielding
 */
public class IntVector3 {

	public static final IntVector3 ZERO = new IntVector3(0, 0, 0);

	public final int x, y, z;

	public IntVector3(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public IntVector3 add(IntVector3 other) {
		return new IntVector3(x + other.x, y + other.y, z + other.z);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		IntVector3 position = (IntVector3) o;
		return x == position.x && y == position.y && z == position.z;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(x, y, z);
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
				.add("x", x)
				.add("y", y)
				.add("z", z)
				.toString();
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getZ() {
		return z;
	}

}
