package shared;

import java.util.ArrayList;

/**
 * Simple two-dimensional grid structure.
 *
 * @author Dan Fielding
 */
public class Grid<T> {

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

}
