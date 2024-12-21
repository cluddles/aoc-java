package shared.pathing;

import java.util.List;

public class Path<T> {

	private final T       destination;
	private final List<T> steps;
	private final int     cost;

	Path(T destination, List<T> steps, int cost) {
		this.destination = destination;
		this.steps       = steps;
		this.cost        = cost;
	}

	public T getDestination() {
		return destination;
	}

	public int length() {
		return steps.size();
	}

	public List<T> getSteps() {
		return steps;
	}

	// For grids this will typically just be the number of steps, but could be
	// something else in other scenarios
	public int getCost() {
		return cost;
	}

}
