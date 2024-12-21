package shared.pathing;

import java.util.Iterator;

public interface AStarApplication<T> {

	int heuristic(T pos, T target);

	int distance(T pos, T neighbour);

	Iterator<T> accessibleNeighbours(T pos);

}
