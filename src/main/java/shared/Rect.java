package shared;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

public class Rect {
	public final IntVector2 start;
	public final IntVector2 size;

	public Rect(IntVector2 start, IntVector2 size) {
		this.start = start;
		this.size  = size;
	}

	public static Rect fromStartAndEnd(IntVector2 start, IntVector2 end) {
		// Let's make sure start and end are the right way around
		IntVector2 size = end.subtract(start);
		return new Rect(
				new IntVector2(
						Math.min(start.x, end.x),
						Math.min(start.y, end.y)),
				new IntVector2(
						Math.abs(size.x),
						Math.abs(size.y)));
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Rect rect = (Rect) o;
		return Objects.equal(start, rect.start) &&
				Objects.equal(size, rect.size);
	}
	@Override
	public int hashCode() {
		return Objects.hashCode(start, size);
	}
	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
				.add("start", start)
				.add("size", size)
				.toString();
	}

	public IntVector2 getStart() {
		return start;
	}
	public IntVector2 getSize() {
		return size;
	}

	public IntVector2 end() {
		return start.add(size);
	}

	// Rect containing this rect and other
	public Rect union(Rect other) {
		IntVector2 end      = end();
		IntVector2 otherEnd = other.end();
		return Rect.fromStartAndEnd(
				new IntVector2(
						Math.min(start.x, other.start.x),
						Math.min(start.y, other.start.y)),
				new IntVector2(
						Math.max(end.x, otherEnd.x),
						Math.max(end.y, otherEnd.y)));
	}

	public long area() {
		return (long) size.x * size.y;
	}

	public Rect resize(IntVector2 startAdjust, IntVector2 endAdjust) {
		return Rect.fromStartAndEnd(start.add(startAdjust), end().add(endAdjust));
	}

	public boolean contains(IntVector2 pos) {
		return ! ( pos.x < start.x
				|| pos.y < start.y
				|| pos.x >= start.x + size.x
				|| pos.y >= start.y + size.y);
	}

}

