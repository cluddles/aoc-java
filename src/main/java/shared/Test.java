package shared;

/**
 * @author Dan Fielding
 */
public class Test {

	public static void assertEqual(Object val1, Object val2) {
		if (val1 == val2) return;
		if (val1.equals(val2)) return;
		throw new RuntimeException(val1 + " does not equal " + val2);
	}

}
