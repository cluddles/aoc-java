package shared.pathing;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AStarPathfinder<T> {

	private final AStarApplication<T> app;

	public AStarPathfinder(AStarApplication<T> app) {
		this.app = app;
	}

	public Path<T> findPath(T start, T end) {
		Map<T, Integer> fScores  = new HashMap<>();
		Map<T, Integer> gScores  = new HashMap<>();
		Map<T, T>       cameFrom = new HashMap<>();

		Set<T> closedSet = new HashSet<>();
		Set<T> openSet   = new HashSet<>();

		openSet.add(start);
		fScores.put(start, app.heuristic(start, end));
		gScores.put(start, 0);

		while (!openSet.isEmpty()) {
			T current = openSet.stream()
					.min(Comparator.comparingInt(n -> fScores.getOrDefault(n, Integer.MAX_VALUE)))
					.orElseThrow(() -> new IllegalStateException("No nodes"));
			if (current.equals(end)) {
				return reconstructPath(cameFrom, current, gScores.get(end));
			}

			openSet.remove(current);
			closedSet.add(current);

			Iterator<T> it = app.accessibleNeighbours(current);
			while (it.hasNext()) {
				T neighbour = it.next();

				// Ignore already evaluated neighbour
				if (closedSet.contains(neighbour)) continue;

				int tentativeG = gScores.get(current) + app.distance(current, neighbour);

				if (!openSet.contains(neighbour)) {
					openSet.add(neighbour);
				} else if (tentativeG > gScores.getOrDefault(neighbour, Integer.MAX_VALUE)) {
					continue;
				}

				cameFrom.put(neighbour, current);
				gScores.put(neighbour, tentativeG);
				fScores.put(neighbour, tentativeG + app.heuristic(neighbour, end));
			}
		}
		// No answer
		return null;
	}

	private Path<T> reconstructPath(Map<T, T> cameFrom, T current, int cost) {
		T destination = current;
		List<T> steps = new LinkedList<>();
		while (current != null) {
			steps.add(0, current);
			current = cameFrom.get(current);
		}
		return new Path<>(destination, steps, cost);
	}

}
