package aoc._2016;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.MoreObjects;

public class Day15 {

	private static final Pattern PATTERN = Pattern.compile(
			"Disc \\#(\\d*) has (\\d*) positions; at time=0, it is at position (\\d*).");

	class Disc {
		final int positions;
		final int offset;

		Disc(int positions, int offset) {
			this.positions = positions;
			this.offset = offset;
		}

		int position(int tick) {
			return (offset + tick) % positions;
		}

		@Override
		public String toString() {
			return MoreObjects.toStringHelper(this)
					.add("positions", positions)
					.add("offset", offset)
					.toString();
		}
	}

	public void run() throws IOException {
		List<String> lines = Files.readAllLines(Paths.get("/home/danielf/Documents/extra/day15.input"));
		List<Disc> discs = new ArrayList<>();
		for (String line : lines) {
			Matcher matcher = PATTERN.matcher(line);
			if (matcher.find()) {
				discs.add(new Disc(
						Integer.parseInt(matcher.group(2)),
						Integer.parseInt(matcher.group(3))));
			}
		}

		discs.add(new Disc(11, 0));

		for (int i = 0; i < Integer.MAX_VALUE; i++) {
			int tick = i;
			boolean good = true;
			for (Disc disc : discs) {
				tick++;
				if (disc.position(tick) != 0) {
					good = false;
					break;
				}
			}
			if (good) {
				System.out.println("Tick " + i);
				break;
			}
		}
		System.out.println(discs);
	}

	public static void main(String[] args) throws Exception {
		Day15 worker = new Day15();
		worker.run();
	}

}
