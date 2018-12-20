package aoc._2018;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;

import shared.pathing.AStarPathfinder;
import shared.pathing.AStarApplication;
import shared.Dir4;
import shared.Grid;
import shared.IntVector2;
import shared.pathing.Path;
import shared.ResourceUtil;
import shared.Test;

/**
 * @author Dan Fielding
 */
public class Day15 {
/*
--- Day 15: Beverage Bandits ---

Having perfected their hot chocolate, the Elves have a new problem: the Goblins
that live in these caves will do anything to steal it. Looks like they're here
for a fight.

You scan the area, generating a map of the walls (#), open cavern (.), and
starting position of every Goblin (G) and Elf (E) (your puzzle input).

Combat proceeds in rounds; in each round, each unit that is still alive takes a
turn, resolving all of its actions before the next unit's turn begins. On each
unit's turn, it tries to move into range of an enemy (if it isn't already) and
then attack (if it is in range).

All units are very disciplined and always follow very strict combat rules. Units
never move or attack diagonally, as doing so would be dishonorable. When
multiple choices are equally valid, ties are broken in reading order:
top-to-bottom, then left-to-right. For instance, the order in which units take
their turns within a round is the reading order of their starting positions in
that round, regardless of the type of unit or whether other units have moved
after the round started. For example:

                 would take their
These units:   turns in this order:
  #######           #######
  #.G.E.#           #.1.2.#
  #E.G.E#           #3.4.5#
  #.G.E.#           #.6.7.#
  #######           #######

Each unit begins its turn by identifying all possible targets (enemy units). If
no targets remain, combat ends.

Then, the unit identifies all of the open squares (.) that are in range of each
target; these are the squares which are adjacent (immediately up, down, left, or
right) to any target and which aren't already occupied by a wall or another
unit. Alternatively, the unit might already be in range of a target. If the unit
is not already in range of a target, and there are no open squares which are in
range of a target, the unit ends its turn.

If the unit is already in range of a target, it does not move, but continues its
turn with an attack. Otherwise, since it is not in range of a target, it moves.

To move, the unit first considers the squares that are in range and determines
which of those squares it could reach in the fewest steps. A step is a single
movement to any adjacent (immediately up, down, left, or right) open (.) square.
Units cannot move into walls or other units. The unit does this while
considering the current positions of units and does not do any prediction about
where units will be later. If the unit cannot reach (find an open path to) any
of the squares that are in range, it ends its turn. If multiple squares are in
range and tied for being reachable in the fewest steps, the square which is
first in reading order is chosen. For example:

Targets:      In range:     Reachable:    Nearest:      Chosen:
#######       #######       #######       #######       #######
#E..G.#       #E.?G?#       #E.@G.#       #E.!G.#       #E.+G.#
#...#.#  -->  #.?.#?#  -->  #.@.#.#  -->  #.!.#.#  -->  #...#.#
#.G.#G#       #?G?#G#       #@G@#G#       #!G.#G#       #.G.#G#
#######       #######       #######       #######       #######

In the above scenario, the Elf has three targets (the three Goblins):

    Each of the Goblins has open, adjacent squares which are in range (marked
        with a ? on the map).
    Of those squares, four are reachable (marked @); the other two (on the
        right) would require moving through a wall or unit to reach.
    Three of these reachable squares are nearest, requiring the fewest steps
        (only 2) to reach (marked !).
    Of those, the square which is first in reading order is chosen (+).

The unit then takes a single step toward the chosen square along the shortest
path to that square. If multiple steps would put the unit equally closer to its
destination, the unit chooses the step which is first in reading order.

(This requires knowing when there is more than one shortest path so that you can
consider the first step of each such path.)

For example:

In range:     Nearest:      Chosen:       Distance:     Step:
#######       #######       #######       #######       #######
#.E...#       #.E...#       #.E...#       #4E212#       #..E..#
#...?.#  -->  #...!.#  -->  #...+.#  -->  #32101#  -->  #.....#
#..?G?#       #..!G.#       #...G.#       #432G2#       #...G.#
#######       #######       #######       #######       #######

The Elf sees three squares in range of a target (?), two of which are nearest
(!), and so the first in reading order is chosen (+). Under "Distance", each
open square is marked with its distance from the destination square; the two
squares to which the Elf could move on this turn (down and to the right) are
both equally good moves and would leave the Elf 2 steps from being in range of
the Goblin. Because the step which is first in reading order is chosen, the Elf
moves right one square.

Here's a larger example of movement:

Initially:
#########
#G..G..G#
#.......#
#.......#
#G..E..G#
#.......#
#.......#
#G..G..G#
#########

After 1 round:
#########
#.G...G.#
#...G...#
#...E..G#
#.G.....#
#.......#
#G..G..G#
#.......#
#########

After 2 rounds:
#########
#..G.G..#
#...G...#
#.G.E.G.#
#.......#
#G..G..G#
#.......#
#.......#
#########

After 3 rounds:
#########
#.......#
#..GGG..#
#..GEG..#
#G..G...#
#......G#
#.......#
#.......#
#########

Once the Goblins and Elf reach the positions above, they all are either in range
of a target or cannot find any square in range of a target, and so none of the
units can move until a unit dies.

After moving (or if the unit began its turn in range of a target), the unit
attacks.

To attack, the unit first determines all of the targets that are in range of it
by being immediately adjacent to it. If there are no such targets, the unit ends
its turn. Otherwise, the adjacent target with the fewest hit points is selected;
in a tie, the adjacent target with the fewest hit points which is first in
reading order is selected.

The unit deals damage equal to its attack power to the selected target, reducing
its hit points by that amount. If this reduces its hit points to 0 or fewer, the
selected target dies: its square becomes . and it takes no further turns.

Each unit, either Goblin or Elf, has 3 attack power and starts with 200 hit
points.

For example, suppose the only Elf is about to attack:

       HP:            HP:
G....  9       G....  9
..G..  4       ..G..  4
..EG.  2  -->  ..E..
..G..  2       ..G..  2
...G.  1       ...G.  1

The "HP" column shows the hit points of the Goblin to the left in the
corresponding row. The Elf is in range of three targets: the Goblin above it
(with 4 hit points), the Goblin to its right (with 2 hit points), and the Goblin
below it (also with 2 hit points). Because three targets are in range, the ones
with the lowest hit points are selected: the two Goblins with 2 hit points each
(one to the right of the Elf and one below the Elf). Of those, the Goblin first
in reading order (the one to the right of the Elf) is selected. The selected
Goblin's hit points (2) are reduced by the Elf's attack power (3), reducing its
hit points to -1, killing it.

After attacking, the unit's turn ends. Regardless of how the unit's turn ends,
the next unit in the round takes its turn. If all units have taken turns in this
round, the round ends, and a new round begins.

The Elves look quite outnumbered. You need to determine the outcome of the
battle: the number of full rounds that were completed (not counting the round in
which combat ends) multiplied by the sum of the hit points of all remaining
units at the moment combat ends. (Combat only ends when a unit finds no targets
during its turn.)

Below is an entire sample combat. Next to each map, each row's units' hit points
are listed from left to right.

Initially:
#######
#.G...#   G(200)
#...EG#   E(200), G(200)
#.#.#G#   G(200)
#..G#E#   G(200), E(200)
#.....#
#######

After 1 round:
#######
#..G..#   G(200)
#...EG#   E(197), G(197)
#.#G#G#   G(200), G(197)
#...#E#   E(197)
#.....#
#######

After 2 rounds:
#######
#...G.#   G(200)
#..GEG#   G(200), E(188), G(194)
#.#.#G#   G(194)
#...#E#   E(194)
#.....#
#######

Combat ensues; eventually, the top Elf dies:

After 23 rounds:
#######
#...G.#   G(200)
#..G.G#   G(200), G(131)
#.#.#G#   G(131)
#...#E#   E(131)
#.....#
#######

After 24 rounds:
#######
#..G..#   G(200)
#...G.#   G(131)
#.#G#G#   G(200), G(128)
#...#E#   E(128)
#.....#
#######

After 25 rounds:
#######
#.G...#   G(200)
#..G..#   G(131)
#.#.#G#   G(125)
#..G#E#   G(200), E(125)
#.....#
#######

After 26 rounds:
#######
#G....#   G(200)
#.G...#   G(131)
#.#.#G#   G(122)
#...#E#   E(122)
#..G..#   G(200)
#######

After 27 rounds:
#######
#G....#   G(200)
#.G...#   G(131)
#.#.#G#   G(119)
#...#E#   E(119)
#...G.#   G(200)
#######

After 28 rounds:
#######
#G....#   G(200)
#.G...#   G(131)
#.#.#G#   G(116)
#...#E#   E(113)
#....G#   G(200)
#######

More combat ensues; eventually, the bottom Elf dies:

After 47 rounds:
#######
#G....#   G(200)
#.G...#   G(131)
#.#.#G#   G(59)
#...#.#
#....G#   G(200)
#######

Before the 48th round can finish, the top-left Goblin finds that there are no
targets remaining, and so combat ends. So, the number of full rounds that were
completed is 47, and the sum of the hit points of all remaining units is
200+131+59+200 = 590. From these, the outcome of the battle is 47 * 590 = 27730.

Here are a few example summarized combats:

#######       #######
#G..#E#       #...#E#   E(200)
#E#E.E#       #E#...#   E(197)
#G.##.#  -->  #.E##.#   E(185)
#...#E#       #E..#E#   E(200), E(200)
#...E.#       #.....#
#######       #######

Combat ends after 37 full rounds
Elves win with 982 total hit points left
Outcome: 37 * 982 = 36334

#######       #######
#E..EG#       #.E.E.#   E(164), E(197)
#.#G.E#       #.#E..#   E(200)
#E.##E#  -->  #E.##.#   E(98)
#G..#.#       #.E.#.#   E(200)
#..E#.#       #...#.#
#######       #######

Combat ends after 46 full rounds
Elves win with 859 total hit points left
Outcome: 46 * 859 = 39514

#######       #######
#E.G#.#       #G.G#.#   G(200), G(98)
#.#G..#       #.#G..#   G(200)
#G.#.G#  -->  #..#..#
#G..#.#       #...#G#   G(95)
#...E.#       #...G.#   G(200)
#######       #######

Combat ends after 35 full rounds
Goblins win with 793 total hit points left
Outcome: 35 * 793 = 27755

#######       #######
#.E...#       #.....#
#.#..G#       #.#G..#   G(200)
#.###.#  -->  #.###.#
#E#G#G#       #.#.#.#
#...#G#       #G.G#G#   G(98), G(38), G(200)
#######       #######

Combat ends after 54 full rounds
Goblins win with 536 total hit points left
Outcome: 54 * 536 = 28944

#########       #########
#G......#       #.G.....#   G(137)
#.E.#...#       #G.G#...#   G(200), G(200)
#..##..G#       #.G##...#   G(200)
#...##..#  -->  #...##..#
#...#...#       #.G.#...#   G(200)
#.G...G.#       #.......#
#.....G.#       #.......#
#########       #########

Combat ends after 20 full rounds
Goblins win with 937 total hit points left
Outcome: 20 * 937 = 18740

What is the outcome of the combat described in your puzzle input?

--- Part Two ---

According to your calculations, the Elves are going to lose badly. Surely, you
won't mess up the timeline too much if you give them just a little advanced
technology, right?

You need to make sure the Elves not only win, but also suffer no losses: even
the death of a single Elf is unacceptable.

However, you can't go too far: larger changes will be more likely to permanently
alter spacetime.

So, you need to find the outcome of the battle in which the Elves have the
lowest integer attack power (at least 4) that allows them to win without a
single death. The Goblins always have an attack power of 3.

In the first summarized example above, the lowest attack power the Elves need to
win without losses is 15:

#######       #######
#.G...#       #..E..#   E(158)
#...EG#       #...E.#   E(14)
#.#.#G#  -->  #.#.#.#
#..G#E#       #...#.#
#.....#       #.....#
#######       #######

Combat ends after 29 full rounds
Elves win with 172 total hit points left
Outcome: 29 * 172 = 4988

In the second example above, the Elves need only 4 attack power:

#######       #######
#E..EG#       #.E.E.#   E(200), E(23)
#.#G.E#       #.#E..#   E(200)
#E.##E#  -->  #E.##E#   E(125), E(200)
#G..#.#       #.E.#.#   E(200)
#..E#.#       #...#.#
#######       #######

Combat ends after 33 full rounds
Elves win with 948 total hit points left
Outcome: 33 * 948 = 31284

In the third example above, the Elves need 15 attack power:

#######       #######
#E.G#.#       #.E.#.#   E(8)
#.#G..#       #.#E..#   E(86)
#G.#.G#  -->  #..#..#
#G..#.#       #...#.#
#...E.#       #.....#
#######       #######

Combat ends after 37 full rounds
Elves win with 94 total hit points left
Outcome: 37 * 94 = 3478

In the fourth example above, the Elves need 12 attack power:

#######       #######
#.E...#       #...E.#   E(14)
#.#..G#       #.#..E#   E(152)
#.###.#  -->  #.###.#
#E#G#G#       #.#.#.#
#...#G#       #...#.#
#######       #######

Combat ends after 39 full rounds
Elves win with 166 total hit points left
Outcome: 39 * 166 = 6474

In the last example above, the lone Elf needs 34 attack power:

#########       #########
#G......#       #.......#
#.E.#...#       #.E.#...#   E(38)
#..##..G#       #..##...#
#...##..#  -->  #...##..#
#...#...#       #...#...#
#.G...G.#       #.......#
#.....G.#       #.......#
#########       #########

Combat ends after 30 full rounds
Elves win with 38 total hit points left
Outcome: 30 * 38 = 1140

After increasing the Elves' attack power until it is just barely enough for them
to win without any Elves dying, what is the outcome of the combat described in
your puzzle input?

 */

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
		Set<IntVector2> result = new HashSet<>();
		for (Dir4 d : Dir4.values()) {
			result.add(pos.add(d.getStep()));
		}
		return result;
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
