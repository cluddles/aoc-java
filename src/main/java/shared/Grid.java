package shared;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Function;

/**
 * Simple two-dimensional grid structure.
 */
public class Grid<T> implements Iterable<T> {

	public static class GridCell<T> {
		IntVector2 pos;
		T data;

		GridCell(IntVector2 pos, T data) {
			this.pos = pos;
			this.data = data;
		}
		public T setData(T data) {
			T result = this.data;
			this.data = data;
			return result;
		}
		public T getData() {
			return data;
		}
		public IntVector2 getPos() {
			return pos;
		}
	}

	private final IntVector2 numCells;
	private ArrayList<ArrayList<GridCell<T>>> cells;

	public Grid(int numCellsU, int numCellsV) {
		this(new IntVector2(numCellsU, numCellsV));
	}

	/** Init a new grid of the specified size */
	public Grid(IntVector2 numCells) {
		// Remember size
		this.numCells = numCells;
		// Init cells
		cells = new ArrayList<>();
		for (int i = 0; i < numCells.getX(); i++) {
			ArrayList<GridCell<T>> col = new ArrayList<>();
			for (int j = 0; j < numCells.getY(); j++) {
				col.add(new GridCell<T> (new IntVector2(i, j), null));
			}
			cells.add(col);
		}
	}

	/**
	 * Generate grid from multi-line string input. Assumes all lines are the
	 * same length - will explode if they're not.
	 *
	 * @param lines
	 * 		Lines to parse.
	 *
	 * @return Generated grid.
	 */
	public static Grid<Character> fromLines(List<String> lines) {
		int w = lines.iterator().next().length();
		int h = lines.size();
		Grid<Character> result = new Grid<>(w, h);
		int j = 0;
		for (String line : lines) {
			for (int i = 0; i < w; i++) {
				result.set(i, j, line.charAt(i));
			}
			j++;
		}
		return result;
	}

	/** @return Number of cells in each axis within the grid */
	public IntVector2 getNumCells() {
		return numCells;
	}

	/**
	 * Determines whether the requested position lies within this grid.
	 * @param u U-pos to check.
	 * @param v V-pos to check.
	 * @return True if in bounds, false otherwise.
	 */
	public boolean isInBounds(int u, int v) {
		return !(u < 0 || v < 0 || u >= numCells.getX() || v >= numCells.getY());
	}
	public boolean isInBounds(IntVector2 pos) {
		return isInBounds(pos.x, pos.y);
	}

	/**
	 * Get the cell at the given cell coordinate.
	 * @param u U-pos to retrieve from.
	 * @param v V-pos to retrieve from.
	 * @return Grid cell at the given position.
	 */
	public GridCell<T> getCell(int u, int v) {
		return cells.get(u).get(v);
	}

	/**
	 * Get the data in the cell at the given coordinate.
	 * @param u U-pos to retrieve from.
	 * @param v V-pos to retrieve from.
	 * @return Stored object.
	 */
	public T get(int u, int v) {
		return getCell(u, v).getData();
	}
	public T get(IntVector2 pos) {
		return get(pos.x, pos.y);
	}

	/**
	 * Set stored object at the given position.
	 * @param u U-pos to store at.
	 * @param v V-pos to store at.
	 * @param data Object to store.
	 * @return Previously stored object, if any.
	 */
	public T set(int u, int v, T data) {
		return getCell(u, v).setData(data);
	}
	public T set(IntVector2 pos, T data) {
		return getCell(pos.x, pos.y).setData(data);
	}

	// Iterate top-left to bottom-right
	public Iterator<GridCell<T>> cellIterator() {
		return new Iterator<GridCell<T>>() {
			private int x, y;
			@Override public boolean hasNext() {
				return x < numCells.getX() && y < numCells.getY();
			}
			@Override public GridCell<T> next() {
				if (!isInBounds(x, y)) throw new NoSuchElementException(x + "," + y);
				GridCell<T> result = getCell(x, y);
				x++;
				if (x >= numCells.getX()) { x = 0; y++; }
				return result;
			}
		};
	}

	// Iterate top-left to bottom-right
	public Iterator<T> iterator() {
		return new Iterator<T>() {
			private final Iterator<GridCell<T>> it = cellIterator();
			@Override public boolean hasNext() { return it.hasNext(); }
			@Override public T next()          { return it.next().getData();
			}
		};
	}

	public String dumpContents(Function<T, String> converter) {
		StringBuilder sb = new StringBuilder();
		for (int j = 0; j < numCells.getY(); j++) {
			for (int i = 0; i < numCells.getX(); i++) {
				sb.append(converter.apply(get(i, j)));
			}
			sb.append("\n");
		}
		return sb.toString();
	}

}
