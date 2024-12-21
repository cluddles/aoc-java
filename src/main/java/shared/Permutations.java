package shared;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Permutations {

	// https://www.programcreek.com/2013/02/leetcode-permutations-java/
	@SuppressWarnings("unchecked")
	public static <T> List<List<T>> of(Collection<T> items) {
		// This is not the most efficient, but it shouldn't matter...
		T[] array            = (T[]) items.toArray();
		List<List<T>> result = new ArrayList<>();

		// Start from an empty list
		result.add(new ArrayList<>());

		for (int i = 0; i < array.length; i++) {
			//list of list in current iteration of the array num
			List<List<T>> current = new ArrayList<>();

			for (List<T> l : result) {
				// # of locations to insert is largest index + 1
				for (int j = 0; j < l.size()+1; j++) {
					// + add num[i] to different locations
					l.add(j, array[i]);

					List<T> temp = new ArrayList<>(l);
					current.add(temp);

					// - remove num[i] add
					l.remove(j);
				}
			}

			result = new ArrayList<>(current);
		}
		return result;
	}

}
