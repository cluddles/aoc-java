package shared;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.Test;

class Dir8Test {

	@Test
	void rotateAntiClockwise() {
		assertThat(Dir8. N.rotateAntiClockwise(), is(Dir8.NW));
		assertThat(Dir8.NE.rotateAntiClockwise(), is(Dir8.N));
		assertThat(Dir8. E.rotateAntiClockwise(), is(Dir8.NE));
		assertThat(Dir8.SE.rotateAntiClockwise(), is(Dir8.E));
		assertThat(Dir8. S.rotateAntiClockwise(), is(Dir8.SE));
		assertThat(Dir8.SW.rotateAntiClockwise(), is(Dir8.S));
		assertThat(Dir8. W.rotateAntiClockwise(), is(Dir8.SW));
		assertThat(Dir8.NW.rotateAntiClockwise(), is(Dir8.W));
	}

	@Test
	void rotateClockwise() {
		assertThat(Dir8. N.rotateClockwise(), is(Dir8.NE));
		assertThat(Dir8.NE.rotateClockwise(), is(Dir8.E));
		assertThat(Dir8. E.rotateClockwise(), is(Dir8.SE));
		assertThat(Dir8.SE.rotateClockwise(), is(Dir8.S));
		assertThat(Dir8. S.rotateClockwise(), is(Dir8.SW));
		assertThat(Dir8.SW.rotateClockwise(), is(Dir8.W));
		assertThat(Dir8. W.rotateClockwise(), is(Dir8.NW));
		assertThat(Dir8.NW.rotateClockwise(), is(Dir8.N));
	}

	@Test
	void opposite() {
		assertThat(Dir8. N.opposite(), is(Dir8.S));
		assertThat(Dir8.NE.opposite(), is(Dir8.SW));
		assertThat(Dir8. E.opposite(), is(Dir8.W));
		assertThat(Dir8.SE.opposite(), is(Dir8.NW));
		assertThat(Dir8. S.opposite(), is(Dir8.N));
		assertThat(Dir8.SW.opposite(), is(Dir8.NE));
		assertThat(Dir8. W.opposite(), is(Dir8.E));
		assertThat(Dir8.NW.opposite(), is(Dir8.SE));
	}

}
