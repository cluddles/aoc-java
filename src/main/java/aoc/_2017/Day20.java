package aoc._2017;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;

import shared.IntVector3;
import shared.ResourceUtil;
import shared.Test;
import shared.Timer;

public class Day20 {

	class Particle {
		int i;
		IntVector3 pos, vel, accel;
		boolean destroyed;

		Particle(int i, IntVector3 pos, IntVector3 vel, IntVector3 accel) {
			this.i = i;
			this.pos = pos;
			this.vel = vel;
			this.accel = accel;
		}

		int getIndex() { return i; }
		boolean isActive() { return !destroyed; }
	}

	public IntVector3 velocityAtTime(Particle p, int time) {
		// v=u+at
		return p.vel.add(p.accel.multiply(time));
	}

	public long collide(List<Particle> particles) {
		Multimap<IntVector3, Particle> particlesAtPos = HashMultimap.create();
		for (int i = 0; i < 1000; i++) {
			particlesAtPos.clear();
			for (Particle p : particles) {
				if (p.destroyed) continue;
				p.vel = p.vel.add(p.accel);
				p.pos = p.pos.add(p.vel);

				particlesAtPos.put(p.pos, p);

				Collection<Particle> existing = particlesAtPos.get(p.pos);
				if (existing.size() > 1) {
					for (Particle ps : existing) ps.destroyed = true;
				}
			}
		}
		return particles.stream().filter(Particle::isActive).count();
	}

	public int closestToOriginLongTerm(List<Particle> particles) {
		// For some "big" t, find the particle with the smallest velocity
		// (this isn't actually correct - position should be taken into account)
		final int t = 1000;
		return particles.stream()
				.sorted(Comparator.comparingInt(p -> velocityAtTime(p, t).manhattanDistance(IntVector3.ZERO)))
				.mapToInt(Particle::getIndex)
				.findFirst()
				.orElse(0);
	}

	public void examples() {
		List<Particle> particles = ImmutableList.of(
				new Particle(0, new IntVector3(3,0,0), new IntVector3(2,0,0), new IntVector3(-1,0,0)),
				new Particle(1, new IntVector3(4,0,0), new IntVector3(0,0,0), new IntVector3(-2,0,0))
		);
		int closest = closestToOriginLongTerm(particles);
		Test.check(closest, 0);

		particles = ImmutableList.of(
				new Particle(0, new IntVector3(-6,0,0), new IntVector3(3,0,0), new IntVector3(0,0,0)),
				new Particle(1, new IntVector3(-4,0,0), new IntVector3(2,0,0), new IntVector3(0,0,0)),
				new Particle(1, new IntVector3(-2,0,0), new IntVector3(1,0,0), new IntVector3(0,0,0)),
				new Particle(1, new IntVector3(-3,0,0), new IntVector3(-1,0,0), new IntVector3(0,0,0))
		);
		long remaining = collide(particles);
		Test.check(remaining, 1L);
	}


	public List<Particle> parse(List<String> lines) {
		List<Particle> result = new ArrayList<>(lines.size());
		Pattern p = Pattern.compile("p=<(.*)>, v=<(.*)>, a=<(.*)>");
		int i = 0;
		for (String line : lines) {
			Matcher m = p.matcher(line);
			m.matches();
			result.add(new Particle(
					i,
					IntVector3.fromString(m.group(1)),
					IntVector3.fromString(m.group(2)),
					IntVector3.fromString(m.group(3))));
			i++;
		}
		return result;
	}

	public static void main(String[] args) throws Exception {
		Day20 day = new Day20();
		day.examples();

		Timer.start();
		List<Particle> particles = day.parse(ResourceUtil.readAllLines("2017/day20.input"));
		System.out.println(day.closestToOriginLongTerm(particles));
		Timer.endMessage();

		Timer.start();
		System.out.println(day.collide(particles));
		Timer.endMessage();
	}

}
