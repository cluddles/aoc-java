package shared;

import java.util.HashSet;
import java.util.Set;

public interface Dir<T extends Dir> {

	// Get "for free" from enum
	int ordinal();

	// Since values() is static, expose it here so defaults below can use it
	T[] allValues();

	default T rotate(int steps) {
		// Use Math.floorMod - Java % operator is remainder rather than modulus
		return allValues()[Math.floorMod((ordinal() + steps), allValues().length)];
	}
	default T rotateAntiClockwise() {
		return rotate(-1);
	}
	default T rotateClockwise() {
		return rotate(1);
	}
	default T opposite() {
		return rotate(allValues().length/2);
	}

	IntVector2 getStep();

	default int getX() {
		return getStep().getX();
	}
	default int getY() {
		return getStep().getY();
	}

	static <T extends Dir> Set<IntVector2> adjacentPositions(IntVector2 pos, T[] values) {
		Set<IntVector2> result = new HashSet<>();
		for (T d : values) { result.add(pos.add(d.getStep())); }
		return result;
	}

}
