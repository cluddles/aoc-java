package aoc._2016;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;

public class Day13 {

	class Node {
		final int x, y;
		int cost;
		Node from;

		Node(int x, int y) {
			this.x = x;
			this.y = y;
		}

		int getCost() {
			return cost;
		}

		// Only x, y used for equality and hashing
		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			Node node = (Node) o;
			return x == node.x && y == node.y;
		}
		@Override
		public int hashCode() {
			return Objects.hashCode(x, y);
		}

		@Override
		public String toString() {
			return MoreObjects.toStringHelper(this)
					.add("x", x)
					.add("y", y)
					.add("cost", cost)
					.add("from", from)
					.toString();
		}
	}

	final int seed;
	final int startX = 1, startY = 1;
	final int goalX = 31, goalY = 39;

	Day13(int seed) {
		this.seed = seed;
	}

	boolean isBlocked(int x, int y) {
		if (x < 0 || y < 0) return true;
		return (Integer.bitCount((x*x + 3*x + 2*x*y + y + y*y) + seed) % 2) == 1;
	}

	int manhattanDistance(int x, int y, int x1, int y1) {
		return Math.abs(x - x1) + Math.abs(y - y1);
	}

	private int calculateFScoreToGoal(Node node) {
		return node.cost + manhattanDistance(node.x, node.y, goalX, goalY);
	}

	Node findCandidate(Set<Node> open) {
		// For shortest path
		// return open.stream().min(Comparator.comparing(this::calculateFScoreToGoal)).get();

		// For flood fill
		return open.stream().min(Comparator.comparing(Node::getCost)).get();
	}

	Collection<Node> neighbours(Node node) {
		ImmutableList.Builder<Node> builder = ImmutableList.<Node>builder();
		if (node.x > 0) builder.add(new Node(node.x-1, node.y));
		if (node.y > 0) builder.add(new Node(node.x, node.y-1));
		return builder
				.add(new Node(node.x+1, node.y))
				.add(new Node(node.x, node.y+1))
				.build();
	}

	void run() {
		Node goal = runInner();
		int length = 0;
		while (goal != null) {
			length += 1;
			goal = goal.from;
		}
		System.out.println(length - 1);
	}

	Node runInner() {
		Set<Node> closed = new HashSet<>();
		Set<Node> open   = new HashSet<>();

		open.add(new Node(startX, startY));
		Node goal = new Node(goalX, goalY);

		while (!open.isEmpty()) {
//			System.out.println("Open: " + open.size() + ", closed: " + closed.size());

			Node current = findCandidate(open);
//			System.out.println(current);

			// For shortest path
			if (Objects.equal(current, goal)) {
				return current;
			}

			// For flood fill
			if (current.cost > 50) {
				System.out.println(closed.size());
				return null;
			}

			open.remove(current);
			closed.add(current);

			Collection<Node> neighbours = neighbours(current);
			for (Node neighbour : neighbours) {
				if (!open.contains(neighbour)) {
					open.add(neighbour);
				}
				int cost = isBlocked(neighbour.x, neighbour.y)? 50000 : current.cost + 1;
				if (neighbour.cost == 0 || cost < neighbour.cost) {
					neighbour.cost = cost;
					neighbour.from = current;
				}
			}
		}
		return null;
	}

	public static void main(String[] args) {
		Day13 test = new Day13(1358);
		test.run();
	}
}

