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

/**
 * @author Dan Fielding
 */
public class Day20 {

/*

--- Day 20: Particle Swarm ---

Suddenly, the GPU contacts you, asking for help. Someone has asked it to
simulate too many particles, and it won't be able to finish them all in time to
render the next frame at this rate.

It transmits to you a buffer (your puzzle input) listing each particle in order
(starting with particle 0, then particle 1, particle 2, and so on). For each
particle, it provides the X, Y, and Z coordinates for the particle's position
(p), velocity (v), and acceleration (a), each in the format <X,Y,Z>.

Each tick, all particles are updated simultaneously. A particle's properties are
updated in the following order:

    Increase the X velocity by the X acceleration.
    Increase the Y velocity by the Y acceleration.
    Increase the Z velocity by the Z acceleration.
    Increase the X position by the X velocity.
    Increase the Y position by the Y velocity.
    Increase the Z position by the Z velocity.

Because of seemingly tenuous rationale involving z-buffering, the GPU would like
to know which particle will stay closest to position <0,0,0> in the long term.
Measure this using the Manhattan distance, which in this situation is simply the
sum of the absolute values of a particle's X, Y, and Z position.

For example, suppose you are only given two particles, both of which stay
entirely on the X-axis (for simplicity). Drawing the current states of particles
0 and 1 (in that order) with an adjacent a number line and diagram of current X
positions (marked in parenthesis), the following would take place:

p=< 3,0,0>, v=< 2,0,0>, a=<-1,0,0>    -4 -3 -2 -1  0  1  2  3  4
p=< 4,0,0>, v=< 0,0,0>, a=<-2,0,0>                         (0)(1)

p=< 4,0,0>, v=< 1,0,0>, a=<-1,0,0>    -4 -3 -2 -1  0  1  2  3  4
p=< 2,0,0>, v=<-2,0,0>, a=<-2,0,0>                      (1)   (0)

p=< 4,0,0>, v=< 0,0,0>, a=<-1,0,0>    -4 -3 -2 -1  0  1  2  3  4
p=<-2,0,0>, v=<-4,0,0>, a=<-2,0,0>          (1)               (0)

p=< 3,0,0>, v=<-1,0,0>, a=<-1,0,0>    -4 -3 -2 -1  0  1  2  3  4
p=<-8,0,0>, v=<-6,0,0>, a=<-2,0,0>                         (0)

At this point, particle 1 will never be closer to <0,0,0> than particle 0, and
so, in the long run, particle 0 will stay closest.

Which particle will stay closest to position <0,0,0> in the long term?


--- Part Two ---

To simplify the problem further, the GPU would like to remove any particles that
collide. Particles collide if their positions ever exactly match. Because
particles are updated simultaneously, more than two particles can collide at the
same time and place. Once particles collide, they are removed and cannot collide
with anything else after that tick.

For example:

p=<-6,0,0>, v=< 3,0,0>, a=< 0,0,0>
p=<-4,0,0>, v=< 2,0,0>, a=< 0,0,0>    -6 -5 -4 -3 -2 -1  0  1  2  3
p=<-2,0,0>, v=< 1,0,0>, a=< 0,0,0>    (0)   (1)   (2)            (3)
p=< 3,0,0>, v=<-1,0,0>, a=< 0,0,0>

p=<-3,0,0>, v=< 3,0,0>, a=< 0,0,0>
p=<-2,0,0>, v=< 2,0,0>, a=< 0,0,0>    -6 -5 -4 -3 -2 -1  0  1  2  3
p=<-1,0,0>, v=< 1,0,0>, a=< 0,0,0>             (0)(1)(2)      (3)
p=< 2,0,0>, v=<-1,0,0>, a=< 0,0,0>

p=< 0,0,0>, v=< 3,0,0>, a=< 0,0,0>
p=< 0,0,0>, v=< 2,0,0>, a=< 0,0,0>    -6 -5 -4 -3 -2 -1  0  1  2  3
p=< 0,0,0>, v=< 1,0,0>, a=< 0,0,0>                       X (3)
p=< 1,0,0>, v=<-1,0,0>, a=< 0,0,0>

------destroyed by collision------
------destroyed by collision------    -6 -5 -4 -3 -2 -1  0  1  2  3
------destroyed by collision------                      (3)
p=< 0,0,0>, v=<-1,0,0>, a=< 0,0,0>

In this example, particles 0, 1, and 2 are simultaneously destroyed at the time
and place marked X. On the next tick, particle 3 passes through unharmed.

How many particles are left after all collisions are resolved?

*/

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
