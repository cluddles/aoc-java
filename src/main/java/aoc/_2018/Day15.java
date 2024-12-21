package aoc._2018;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;

import shared.Dir;
import shared.Dir4;
import shared.Grid;
import shared.IntVector2;
import shared.ResourceUtil;
import shared.Test;
import shared.pathing.AStarApplication;
import shared.pathing.AStarPathfinder;
import shared.pathing.Path;

public class Day15 {

	// Tile state is just a char, as per the input
	// Just use this to calculate blockages (i.e. elf/gobby state not included)
	static class Tile {
		char content;
		Tile(char content) {
			this.content = content;
		}
	}

	// Elf/gobby state
	static class Unit {
		int           hp          = 200;
		int           attackPower = DEFAULT_ATTACK_POWER;
		IntVector2    pos;
		final boolean isGoblin;

		Unit(IntVector2 pos, boolean isGoblin) {
			this.pos      = pos;
			this.isGoblin = isGoblin;
		}

		IntVector2 getPos()   { return pos; }
		int        getHp()    { return hp; }
	}

	class PathingRules implements AStarApplication<IntVector2> {

		@Override public int heuristic(IntVector2 pos, IntVector2 target) {
			return pos.manhattanDistance(target);
		}

		@Override public int distance(IntVector2 pos, IntVector2 neighbour) {
			return 1;
		}

		@Override public Iterator<IntVector2> accessibleNeighbours(IntVector2 pos) {
			// Bit ugly, but this is just all adjacents that aren't blocked
			return allAdjacentPositions(pos).stream()
					.filter(p -> !isBlocked(p))
					.collect(Collectors.toList())
					.iterator();
		}
	}

	private static final char TILE_EMPTY  = '.';
	private static final char TILE_GOBLIN = 'G';
	private static final char TILE_ELF    = 'E';

	// Move order based entirely on reading order
	private static final Comparator<Unit> UNIT_MOVE_ORDER = Comparator
			.comparing(Unit::getPos, IntVector2.READING_ORDER);

	// Combat order based on lowest hp, then reading order
	private static final Comparator<Unit> UNIT_FIGHT_ORDER = Comparator
			.comparing(Unit::getHp)
			.thenComparing(Unit::getPos, IntVector2.READING_ORDER);

	// Prefer up-left
	private static final List<Dir4> DIR_ORDER = ImmutableList.of(
			Dir4.N,
			Dir4.W,
			Dir4.E,
			Dir4.S);

	// Part 1 attack strength
	private static final int DEFAULT_ATTACK_POWER = 3;

	// If we keep the input we can re-init, required for part 2
	private final List<String> input;

	private Grid<Tile> tiles;
	private Set<Unit>  units;
	private int        completedTicks;
	private int        numGoblins;
	private boolean    isPart2;
	private boolean    elfDied;

	private AStarPathfinder<IntVector2> aStar = new AStarPathfinder<>(new PathingRules());

	Day15(List<String> input) {
		this.input = input;
		init(false, DEFAULT_ATTACK_POWER);
	}

	void init(boolean isPart2, int elfAttackPower) {
		// Reset our variables so we can re-run from scratch
		completedTicks = 0;
		numGoblins     = 0;
		this.isPart2   = isPart2;
		elfDied        = false;
		int w = input.get(0).length();
		int h = input.size();
		tiles = new Grid<>(w, h);
		units = new HashSet<>();
		for (int j = 0; j < h; j++) {
			for (int i = 0; i < w; i++) {
				char content = input.get(j).charAt(i);
				Unit unit    = null;
				switch (content) {
				case TILE_GOBLIN:
					unit = new Unit(new IntVector2(i, j), true);
					numGoblins++;
					break;
				case TILE_ELF:
					unit = new Unit(new IntVector2(i, j), false);
					unit.attackPower = elfAttackPower;
					break;
				default:
					break;
				}
				if (unit != null) units.add(unit);
				tiles.set(i, j, new Tile(content));
			}
		}
	}

	boolean isBlocked(IntVector2 pos) {
		return tiles.get(pos).content != TILE_EMPTY;
	}

	boolean isAdjacent(IntVector2 pos1, IntVector2 pos2) {
		return pos1.manhattanDistance(pos2) == 1;
	}

	Set<IntVector2> allAdjacentPositions(IntVector2 pos) {
		return Dir.adjacentPositions(pos, Dir4.values());
	}

	void moveUnit(Unit unit, IntVector2 to) {
		// Remove obstruction from tile grid
		IntVector2 from = unit.pos;
		tiles.get(from).content = TILE_EMPTY;
		// Place obstruction in new pos in tile grid
		tiles.get(to)  .content = unit.isGoblin? TILE_GOBLIN : TILE_ELF;
		unit.pos = to;
	}

	void removeUnit(Unit unit) {
		// Remove from set
		units.remove(unit);
		// Remove obstruction from tile grid
		tiles.get(unit.pos).content = TILE_EMPTY;
		// Update gobby count
		if (unit.isGoblin) numGoblins--; else elfDied = true;
	}

	Unit pickFight(Unit attacker) {
		Set<Unit> inRange = new HashSet<>();
		for (Unit unit : units) {
			if (unit.isGoblin != attacker.isGoblin && isAdjacent(attacker.pos, unit.pos)) {
				inRange.add(unit);
			}
		}
		return inRange.stream()
				.min(UNIT_FIGHT_ORDER)
				.orElse(null);
	}

	void fight(Unit attacker, Unit defender) {
		defender.hp -= attacker.attackPower;
		if (defender.hp <= 0) removeUnit(defender);
	}


	Path<IntVector2> pickPath(Unit actor) {
		// Pathing attempts try closest targets first
		Comparator<IntVector2> comp = Comparator
				.comparing((IntVector2 p) -> p.manhattanDistance(actor.pos));
		// Find all viable target co-ords
		List<IntVector2> dests = units.stream()
				.filter (u -> u.isGoblin != actor.isGoblin)
				.map    (u -> allAdjacentPositions(u.pos))
				.flatMap(Set::stream)
				.filter (p -> !isBlocked(p))
				.sorted (comp)
				.collect(Collectors.toList());
		// Work out the best one
		Path<IntVector2> best = null;
		for (IntVector2 dest : dests) {
			// Don't even bother pathing if the shortest theoretical route is
			// longer than the best path we've found so far
			if (best != null && dest.manhattanDistance(actor.pos) >= best.length()) continue;
			// Path it
			Path<IntVector2> current = aStar.findPath(actor.pos, dest);
			if (current == null) continue;
			if (best == null
					|| current.length() < best.length()
					|| (current.length() == best.length() &&
							IntVector2.READING_ORDER.compare(
									current.getDestination(),
									best.getDestination()) < 0)) {
				best = current;
			}
		}
		// No path?
		if (best == null) return null;
		// Now we need to pick the best path in the case of multiple valid ones
		// We only care about the first step, so just hack it with a new start pos.
		for (Dir4 d : DIR_ORDER) {
			// Take one step in the given direction
			IntVector2 firstStep = actor.pos.add(d.getStep());
			// Don't allow invalid first step, otherwise dudes will walk through
			// walls and each other (what a fun bug this was...)
			if (isBlocked(firstStep)) continue;
			// If first step + length of new path is the same as the best path
			// length, then we're done (since we're checking in priority order)
			Path<IntVector2> current = aStar.findPath(firstStep, best.getDestination());
			if (current != null && current.length() == best.length() - 1) {
				current.getSteps().add(0, firstStep);
				return current;
			}
		}
		throw new IllegalStateException("Could not determine best route");
	}


	boolean tick() {
		// System.out.println(completedTicks + ":" + units.size());
		List<Unit> actors = units.stream()
				.sorted(UNIT_MOVE_ORDER)
				.collect(Collectors.toList());
		for (Unit actor : actors) {
			// All elves/goblins dead?
			if (numGoblins == 0 || numGoblins == units.size()) return false;
			act(actor);
		}
		// We can fail early for part 2 if any elves have died
		if (isPart2 && elfDied) return false;
		// Mark tick as completed
		completedTicks++;
		return true;
	}

	void act(Unit actor) {
		// Make sure the actor didn't die already
		if (actor.hp <= 0) return;
		// If not adjacent to enemy, find shortest path to free enemy-adjacent square
		Unit defender = pickFight(actor);
		if (defender == null) {
			// Available paths - prefer reading order
			Path<IntVector2> path = pickPath(actor);
			if (path != null) {
				// Move
				moveUnit(actor, path.getSteps().get(0));
				// Check for adjacency now
				defender = pickFight(actor);
			}
		}
		// Fight
		if (defender != null) fight(actor, defender);
	}

	void dumpState() {
		System.out.println("--- " + completedTicks + " ---");
		for (int j = 0; j < tiles.getNumCells().getY(); j++) {
			StringBuilder line = new StringBuilder();
			for (int i = 0; i < tiles.getNumCells().getX(); i++) {
				line.append(tiles.get(i, j).content);
			}
			System.out.println(line);
		}
	}

	long score() {
		// Completed ticks * sum of hp
		int hp = units.stream().mapToInt(Unit::getHp).sum();
		long score = (long) completedTicks * hp;
		System.out.println("= " + completedTicks + " * " + hp + " = " + score);
		return score;
	}

	void simulateFully() {
		while (tick()) {
			// dumpState();
		}
		dumpState();
	}

	long evalPart1() {
		simulateFully();
		return score();
	}

	long evalPart2() {
		// Start at 4 power and work up until we find the right one
		int attackPower = 4;
		while (true) {
			System.out.println("Attack power: " + attackPower);
			init(true, attackPower);
			simulateFully();
			if (!elfDied) break;
			attackPower++;
		}
		return score();
	}

	public static void main(String[] args) throws Exception {

		// Part 1
		// Examples
		Test.check(new Day15(ImmutableList.of(
				"#######",
				"#.G...#",
				"#...EG#",
				"#.#.#G#",
				"#..G#E#",
				"#.....#",
				"#######")).evalPart1(), 27730L);
		Test.check(new Day15(ImmutableList.of(
				"#######",
				"#G..#E#",
				"#E#E.E#",
				"#G.##.#",
				"#...#E#",
				"#...E.#",
				"#######")).evalPart1(), 36334L);
		Test.check(new Day15(ImmutableList.of(
				"#######",
				"#E..EG#",
				"#.#G.E#",
				"#E.##E#",
				"#G..#.#",
				"#..E#.#",
				"#######")).evalPart1(), 39514L);
		Test.check(new Day15(ImmutableList.of(
				"#######",
				"#E.G#.#",
				"#.#G..#",
				"#G.#.G#",
				"#G..#.#",
				"#...E.#",
				"#######")).evalPart1(), 27755L);
		Test.check(new Day15(ImmutableList.of(
				"#######",
				"#.E...#",
				"#.#..G#",
				"#.###.#",
				"#E#G#G#",
				"#...#G#",
				"#######")).evalPart1(), 28944L);
		Test.check(new Day15(ImmutableList.of(
				"#########",
				"#G......#",
				"#.E.#...#",
				"#..##..G#",
				"#...##..#",
				"#...#...#",
				"#.G...G.#",
				"#.....G.#",
				"#########")).evalPart1(), 18740L);
		// Vs Input
		List<String> input = ResourceUtil.readAllLines("2018/day15.input");
		System.out.println(new Day15(input).evalPart1());
		// Failures: 214011 (too high)

		// Part 2
		// Examples
		Test.check(new Day15(ImmutableList.of(
				"#######",
				"#.G...#",
				"#...EG#",
				"#.#.#G#",
				"#..G#E#",
				"#.....#",
				"#######")).evalPart2(), 4988L);
		Test.check(new Day15(ImmutableList.of(
				"#######",
				"#E..EG#",
				"#.#G.E#",
				"#E.##E#",
				"#G..#.#",
				"#..E#.#",
				"#######")).evalPart2(), 31284L);
		Test.check(new Day15(ImmutableList.of(
				"#######",
				"#E.G#.#",
				"#.#G..#",
				"#G.#.G#",
				"#G..#.#",
				"#...E.#",
				"#######")).evalPart2(), 3478L);
		Test.check(new Day15(ImmutableList.of(
				"#######",
				"#.E...#",
				"#.#..G#",
				"#.###.#",
				"#E#G#G#",
				"#...#G#",
				"#######")).evalPart2(), 6474L);
		Test.check(new Day15(ImmutableList.of(
				"#########",
				"#G......#",
				"#.E.#...#",
				"#..##..G#",
				"#...##..#",
				"#...#...#",
				"#.G...G.#",
				"#.....G.#",
				"#########")).evalPart2(), 1140L);

		// Vs Input
		System.out.println(new Day15(input).evalPart2());
		// Failures: 1805645 (too high) (attack power 23, 1195 * 1511)
	}

}
