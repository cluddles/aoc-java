package aoc._2018;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import shared.IntVector2;
import shared.Rect;
import shared.ResourceUtil;

public class Day10 {

	private static final Pattern LINE_PATTERN = Pattern.compile(
			"position=<(.+),(.+)> velocity=<(.+),(.+)>");

	class Particle {
		final IntVector2 pos;
		final IntVector2 velocity;

		public Particle(IntVector2 pos, IntVector2 velocity) {
			this.pos = pos;
			this.velocity = velocity;
		}
	}

	int parseInt(String str) {
		return Integer.parseInt(str.trim());
	}

	List<Particle> parse(List<String> input) {
		List<Particle> result = new ArrayList<>();
		for (String line : input) {
			Matcher m = LINE_PATTERN.matcher(line);
			if (!m.matches()) throw new IllegalStateException("Invalid line: " + line);
			result.add(new Particle(
					new IntVector2(
							parseInt(m.group(1)),
							parseInt(m.group(2))),
					new IntVector2(
							parseInt(m.group(3)),
							parseInt(m.group(4)))));
		}
		return result;
	}

	Rect activeArea(List<Particle> particles) {
		int minX = Integer.MAX_VALUE;
		int minY = Integer.MAX_VALUE;
		int maxX = Integer.MIN_VALUE;
		int maxY = Integer.MIN_VALUE;
		for (Particle p : particles) {
			minX = Math.min(p.pos.x, minX);
			minY = Math.min(p.pos.y, minY);
			maxX = Math.max(p.pos.x, maxX);
			maxY = Math.max(p.pos.y, maxY);
		}
		return new Rect(
				new IntVector2(minX, minY),
				new IntVector2(maxX - minX + 1, maxY - minY + 1));
	}

	void result(List<Particle> particles, Rect bounds) {
		boolean[][] grid = new boolean[bounds.size.x][bounds.size.y];
		for (Particle p : particles) {
			grid[p.pos.x - bounds.start.x][p.pos.y - bounds.start.y] = true;
		}
		for (int j = 0; j < bounds.size.y; j++) {
			StringBuilder row = new StringBuilder();
			for (int i = 0; i < bounds.size.x; i++) {
				row.append(grid[i][j]? "#" : ".");
			}
			System.out.println(row.toString());
		}
	}

	public void simulate(List<String> lines) {
		int ticks = 0;
		List<Particle> particles = parse(lines);
		Rect bounds = new Rect(
				IntVector2.ZERO,
				new IntVector2(Integer.MAX_VALUE, Integer.MAX_VALUE));
		while (true) {
			List<Particle> newParticles = new ArrayList<>();
			for (Particle p : particles) {
				newParticles.add(new Particle(
						p.pos.add(p.velocity),
						p.velocity));
			}
			Rect newBounds = activeArea(newParticles);
			if (newBounds.area() > bounds.area()) {
				result(particles, bounds);
				System.out.println(ticks);
				return;
			}
			particles = newParticles;
			bounds    = newBounds;
			ticks++;
		}
	}

	public static void main(String[] args) throws Exception {
		List<String> lines = ResourceUtil.readAllLines("2018/day10.input");
		new Day10().simulate(lines);
	}

}
