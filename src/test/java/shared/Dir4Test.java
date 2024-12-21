package shared;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.*;

import org.junit.jupiter.api.Test;

class Dir4Test {

	@Test
	void rotateAntiClockwise() {
		assertThat(Dir4.N.rotateAntiClockwise(), is(Dir4.W));
		assertThat(Dir4.E.rotateAntiClockwise(), is(Dir4.N));
		assertThat(Dir4.S.rotateAntiClockwise(), is(Dir4.E));
		assertThat(Dir4.W.rotateAntiClockwise(), is(Dir4.S));
	}

	@Test
	void rotateClockwise() {
		assertThat(Dir4.N.rotateClockwise(), is(Dir4.E));
		assertThat(Dir4.E.rotateClockwise(), is(Dir4.S));
		assertThat(Dir4.S.rotateClockwise(), is(Dir4.W));
		assertThat(Dir4.W.rotateClockwise(), is(Dir4.N));
	}

	@Test
	void opposite() {
		assertThat(Dir4.N.opposite(), is(Dir4.S));
		assertThat(Dir4.E.opposite(), is(Dir4.W));
		assertThat(Dir4.S.opposite(), is(Dir4.N));
		assertThat(Dir4.W.opposite(), is(Dir4.E));
	}

}
