package aoc._2016;

import java.io.IOException;

public class Day19 {

	/*
	// Part 1
	public void run(int numElves) throws IOException {
		Map<Integer, Integer> elves = new HashMap<>(numElves);
		for (int i = 0; i < numElves; i++) {
			elves.put(i, 1);
		}

		while (elves.size() > 1) {
			System.out.println("Elves left: " + elves.size());
			for (int i = 0; i < numElves; i++) {
				if (elves.size() == 1) break;

				if (!elves.containsKey(i)) continue;
				for (int j = 1; j < numElves; j++) {
					int other = (i + j) % numElves;
					if (!elves.containsKey(other)) continue;

					elves.put(i, elves.get(i) + elves.remove(other));
					break;
				}
			}
		}

		for (Map.Entry<Integer, Integer> entry : elves.entrySet()) {
			System.out.println("Elfer: " + (entry.getKey() + 1));
		}
	}
	*/

	class Elf {
		final int number;
		int numPresents = 1;

		Elf before;
		Elf next;

		Elf(int number) {
			this.number = number;
		}
	}

	// Part 2, slow and steady (VERY SLOW, but likely to be right)
	public void runSlow(int numElves) throws IOException {
		Elf first = new Elf(1);
		Elf before = first;
		Elf current = null;
		for (int i = 0; i < numElves-1; i++) {
			current = new Elf(2 + i);
			before.next = current;
			before = current;
		}
		// Close the circle
		current.next = first;

		int elvesLeft = numElves;
		current = first;
		while (elvesLeft > 1) {
			// Find the other elf
			int steps = elvesLeft / 2;
			Elf beforeTakingFrom = current;
			while (steps > 1) {
				beforeTakingFrom = beforeTakingFrom.next;
				steps--;
			}
			// Take presents
			Elf takingFrom = beforeTakingFrom.next;
			current.numPresents += takingFrom.numPresents;
			// Remove from circle
			//System.out.println(current.number + " taking from " + takingFrom.number);
			beforeTakingFrom.next = takingFrom.next;
			elvesLeft--;

			// Step along
			//System.out.println("Elf " + current.number + " has " + current.numPresents);
			current = current.next;
		}
		System.out.println(numElves + " -> " + current.number);
	}

	public void run(int numElves) throws IOException {
		Elf first = new Elf(1);
		Elf opposite = null;
		Elf before = first;
		Elf current = null;
		for (int i = 0; i < numElves-1; i++) {
			current = new Elf(2 + i);
			// Close the circle
			before.next = current;
			current.before = before;
			// Move along
			before = current;

			if (i == numElves/2-1) opposite = current;
		}
		// Close the circle
		current.next = first;
		first.before = current;

		int elvesLeft = numElves;
		int stepsTaken = 0;

		current = first;
		while (elvesLeft > 1) {
			if (current == opposite) throw new AssertionError("Well that's busted");

			if (stepsTaken % 1000 == 0) System.out.println("Working. Elves left: " + elvesLeft);
			stepsTaken++;

			// Find the other elf
//			System.out.println(current.number + " taking from " + opposite.number);
			current.numPresents += opposite.numPresents;
			// Close the circle
			opposite.before.next = opposite.next;
			opposite.next.before = opposite.before;
			elvesLeft--;

			if (elvesLeft % 2 == 0) {
				opposite = opposite.next.next;
			} else {
				opposite = opposite.next;
			}

			// Step along
			//System.out.println("Elf " + current.number + " has " + current.numPresents);
			current = current.next;
		}

		System.out.println("In " + stepsTaken + " steps");
		System.out.println(current.number);
	}

	public static void main(String[] args) throws Exception {
		Day19 worker = new Day19();
		for (int i = 2; i <= 100; i++) {
			worker.runSlow(i);
		}
		// int numElves = 3001330;
		// worker.runSlow(numElves);
		// worker.run(numElves);
	}

}
