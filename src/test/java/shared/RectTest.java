package shared;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.*;

import org.junit.jupiter.api.Test;

class RectTest {

	@Test
	void fromStartAndEnd() {
		Rect rect = Rect.fromStartAndEnd(new IntVector2(1, 2), new IntVector2(5, 7));
		assertThat(rect.getStart(), is(new IntVector2(1, 2)));
		assertThat(rect.getSize(),  is(new IntVector2(4, 5)));
	}

	@Test
	void fromStartAndEnd_fixSizeAndBounds() {
		// Make sure size is always positive
		Rect rect = Rect.fromStartAndEnd(new IntVector2(5, 6), new IntVector2(8, 4));
		assertThat(rect.getStart(), is(new IntVector2(5, 4)));
		assertThat(rect.getSize(),  is(new IntVector2(3, 2)));
	}

	@Test
	void union() {
		Rect r1 = new Rect(new IntVector2(5, 5), new IntVector2(2, 3));
		Rect r2 = new Rect(new IntVector2(6, 6), new IntVector2(12, 1));
		assertThat(r1.union(r2), is(new Rect(new IntVector2(5, 5), new IntVector2(13, 3))));
	}

}
