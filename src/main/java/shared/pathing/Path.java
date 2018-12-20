package shared.pathing;

import java.util.List;

/**
 * @author Dan Fielding
 */
public class Path<T> {

	private final T destination;
	private final List<T> steps;

	Path(T destination, List<T> steps) {
		this.destination = destination;
		this.steps       = steps;
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

}
